package com.yoson.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.yoson.callback.StatusCallBack;
import com.yoson.cms.controller.Global;
import com.yoson.csv.BackTestCSVWriter;
import com.yoson.date.DateComparator;
import com.yoson.date.DateUtils;
import com.yoson.model.BackTestResult;
import com.yoson.model.MainUIParam;
import com.yoson.model.PerDayRecord;
import com.yoson.model.ScheduleData;
import com.yoson.model.TestSet;
import com.yoson.sql.SQLUtils;
import com.yoson.tws.ScheduledDataRecord;
import com.yoson.tws.YosonEWrapper;

public class BackTestTask implements Runnable {
	public static boolean running = false;
	private MainUIParam mainUIParam;	
	
	public static List<String> allBTSummaryResults;
	public static List<String> allBTPnlResults;
	public static List<String> allBTTradeResults;
	public static String aTradingDayForCheckResult;
	public static StringBuilder allProfitAndLossResults;
	public static Map<Long, List<Object>> allProfitAndLossResultMap;
	public static Map<String, Integer> marketTimeMap;
	public static Map<String, Double> sumOfLastTrade;
	public static Map<String, List<ScheduleData>> rowData;
	public static List<String> sortedDateList;
	public static Map<String, String> allPositivePnlResult;
	private StatusCallBack callBack;
	public static boolean isLiveData = false;
	
	@Override
	public void run() {
		try {
			runTestSet();
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			callBack.done();
		}
	}		
	
	public BackTestTask(MainUIParam mainUIParam, StatusCallBack callBack) {
		this.mainUIParam = mainUIParam;
		BackTestTask.allBTSummaryResults = new ArrayList<String>();
		BackTestTask.allBTPnlResults = new ArrayList<String>();
		BackTestTask.allBTTradeResults = new ArrayList<String>();
		BackTestTask.aTradingDayForCheckResult = "";
		BackTestTask.allProfitAndLossResultMap = new TreeMap<Long, List<Object>>();
		BackTestTask.allProfitAndLossResults = new StringBuilder();
		
		BackTestTask.rowData = new HashMap<String, List<ScheduleData>>();		
		BackTestTask.marketTimeMap = new HashMap<String, Integer>();
		BackTestTask.sumOfLastTrade = new HashMap<String, Double>();
		BackTestTask.allPositivePnlResult = new HashMap<String, String>();
		
		this.callBack = callBack;
	}
	
	public static void initRawData(List<String> sdatas, MainUIParam mainUIParam) throws ParseException {
		for (String sdata : sdatas) {
			ScheduleData sData = new ScheduleData(sdata.split(",")[0], sdata.split(",")[1], sdata.split(",")[2], sdata.split(",")[3], sdata.split(",")[4]);
			initRawData(mainUIParam, sData);
		}
	}

	private static void initRawData(MainUIParam mainUIParam, ScheduleData sData) throws ParseException {
		String dateStr = sData.getDateStr();
		BackTestTask.sumOfLastTrade.put(dateStr, BackTestTask.sumOfLastTrade.get(dateStr) == null ? sData.getLastTrade() : BackTestTask.sumOfLastTrade.get(dateStr) + sData.getLastTrade());
		if (BackTestTask.rowData.containsKey(dateStr)) {
			BackTestTask.rowData.get(dateStr).add(sData);
		} else {
			List<ScheduleData> dataList = new ArrayList<ScheduleData>();
			dataList.add(sData);
			BackTestTask.rowData.put(dateStr, dataList);
		}
		if (BackTestTask.marketTimeMap.containsKey(sData.getTimeStr())) {
			return;
		}
		BackTestTask.marketTimeMap.put(sData.getTimeStr(), mainUIParam.isCheckMarketTime(sData.getTimeStr()));
	}
	
	public static void initRawData(MainUIParam mainUIParam) throws ParseException {
		List<ScheduledDataRecord> scheduledDataRecords = YosonEWrapper.extractScheduledDataRecord(mainUIParam.getDataRootPath());
		List<List<ScheduleData>> resultDatas = YosonEWrapper.toScheduleDataList(scheduledDataRecords, mainUIParam, Long.parseLong(scheduledDataRecords.get(scheduledDataRecords.size() - 1).getTime()));
		for (List<ScheduleData> resultData : resultDatas) {
			for (ScheduleData sData : resultData) {
				initRawData(mainUIParam, sData);
			}
		}
	}

	public void runTestSet() throws IOException, ParseException {	
		long start = System.currentTimeMillis();
		
		callBack.updateStatus("The data path on server: " + mainUIParam.getSourcePath() + System.lineSeparator());
		
		callBack.updateStatus(getStatus("Getting data started, this may cost several minutes, pls wait..."));
		
		if(isLiveData)
			initRawData(mainUIParam);
		else 
			initRawData(SQLUtils.initScheduleData(mainUIParam), mainUIParam);	
		
		long milliseconds = System.currentTimeMillis() - start;
		callBack.updateStatus(getStatus("Getting data ended, total cost: " + DateUtils.dateDiff(milliseconds)));
		
		BackTestTask.sortedDateList = new ArrayList<String>(BackTestTask.rowData.keySet());
		if (BackTestTask.sortedDateList.size() == 0) {
			callBack.updateStatus(getStatus("No data been selected"));			
			return;
		}
		Collections.sort(BackTestTask.sortedDateList, new DateComparator());
		mainUIParam.setStartStr(sortedDateList.get(0));
		mainUIParam.setEndStr(sortedDateList.get(sortedDateList.size() - 1));
		
		List<TestSet> testSets = new ArrayList<TestSet>();
		for (int cpTimer = mainUIParam.getCpTimer(); cpTimer<= mainUIParam.getCpTimerTo() ; cpTimer = cpTimer + mainUIParam.getCpTimerLiteral())
		for (double cpBuffer = mainUIParam.getCpBuffer(); cpBuffer<= mainUIParam.getCpBufferTo() ; cpBuffer = cpBuffer + mainUIParam.getCpBufferLiteral())
		for (int cpHitRate = mainUIParam.getCpHitRate(); cpHitRate<= mainUIParam.getCpHitRateTo() ; cpHitRate = cpHitRate + mainUIParam.getCpHitRateLiteral())
		for (double cpSmooth = mainUIParam.getCpSmooth(); cpSmooth<= mainUIParam.getCpSmoothTo(); cpSmooth = cpSmooth + mainUIParam.getCpSmoothLiteral())
		for (double estimationBuffer = mainUIParam.getEstimationBuffer(); estimationBuffer<= mainUIParam.getEstimationBufferTo() ; estimationBuffer = estimationBuffer + mainUIParam.getEstimationBufferLiteral())
		for (double actionTrigger = mainUIParam.getActionTrigger(); actionTrigger<= mainUIParam.getActionTriggerTo() ; actionTrigger = actionTrigger + mainUIParam.getActionTriggerLiteral())
		for (int actionCounting = mainUIParam.getActionCounting(); actionCounting<= mainUIParam.getActionCountingTo() ; actionCounting = actionCounting + mainUIParam.getActionCountingLiteral())
		for (double tradeStopLossTrigger = mainUIParam.getTradeStopLossTrigger(); tradeStopLossTrigger<= mainUIParam.getTradeStopLossTriggerTo() ; tradeStopLossTrigger = tradeStopLossTrigger + mainUIParam.getTradeStopLossTriggerLiteral())
		for (double tradeStopLossTriggerPercent = mainUIParam.getTradeStopLossTriggerPercent(); tradeStopLossTriggerPercent<= mainUIParam.getTradeStopLossTriggerPercentTo() ; tradeStopLossTriggerPercent = tradeStopLossTriggerPercent + mainUIParam.getTradeStopLossTriggerPercentLiteral())
		for (double absoluteTradeStopLoss = mainUIParam.getAbsoluteTradeStopLoss(); absoluteTradeStopLoss<= mainUIParam.getAbsoluteTradeStopLossTo() ; absoluteTradeStopLoss = absoluteTradeStopLoss + mainUIParam.getAbsoluteTradeStopLossLiteral()) {
			testSets.add(new TestSet(cpTimer, cpBuffer, cpHitRate, cpSmooth, estimationBuffer, actionTrigger,
					actionCounting, tradeStopLossTrigger, tradeStopLossTriggerPercent, absoluteTradeStopLoss, mainUIParam.getUnit(),
					mainUIParam.getMarketStartTime(), mainUIParam.getLunchStartTimeFrom(), mainUIParam.getLunchStartTimeTo(), 
					mainUIParam.getMarketCloseTime(), mainUIParam.getCashPerIndexPoint(), mainUIParam.getTradingFee(), 
					mainUIParam.getOtherCostPerTrade(), mainUIParam.getLastNumberOfMinutesClearPosition(), mainUIParam.getLunchLastNumberOfMinutesClearPosition()));
		
		}
		BackTestCSVWriter.writeText(mainUIParam.getParamPath(), new Gson().toJson(mainUIParam), false);
		int startStep = 0;
		if (!isLiveData) {
			try {
				File stepFile = new File(mainUIParam.getStepPath());
				if(stepFile.exists()) {
					FileInputStream input = new FileInputStream(stepFile);
					String step = IOUtils.toString(input);
					input.close();
					if(StringUtils.isNotEmpty(step)) {
						startStep = Integer.parseInt(step.split(",")[0]);
					}
				}
			} catch (Exception e) {
				try {
					FileUtils.cleanDirectory(new File(mainUIParam.getSourcePath()));			
				} catch (Exception ex) {
				}
			}
		}
		boolean first = (startStep == 0);
		for (int index = 1; index <= testSets.size(); index++) {
			if (index <= startStep)
				continue;
			TestSet testSet = testSets.get(index - 1);
			long start2 = System.currentTimeMillis();
			
			callBack.updateStatus(getStatus("Testset " + index + " started"));
			
			List<PerDayRecord> dayRecords = BackTestSet.initAndRun(index, mainUIParam, testSet);
			
			BackTestResult backTestResult = new BackTestResult(testSet, dayRecords);
			
			StringBuilder pnlContent = new StringBuilder();
			StringBuilder tradContent = new StringBuilder();
			BackTestCSVWriter.initBTPnLAndTradeAndProfitAndLossContent(index, mainUIParam, backTestResult, pnlContent, tradContent);
			BackTestTask.allBTPnlResults.add(pnlContent.toString());
			BackTestTask.allBTTradeResults.add(tradContent.toString());
			BackTestTask.allBTSummaryResults.add(BackTestCSVWriter.getBTSummaryContent(index, mainUIParam, backTestResult));
			
			if(index == 1 && backTestResult.dayRecords.size() > 0 && !isLiveData) {
				BackTestTask.aTradingDayForCheckResult = BackTestCSVWriter.getATradingDayContent(mainUIParam, backTestResult.dayRecords.get(0));
				BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getSourcePath(), BackTestCSVWriter.aTradingDayForCheckFileName), BackTestCSVWriter.getATradingDayHeader() + BackTestTask.aTradingDayForCheckResult, true);
			}	
			
			if (index % Global.savePoint == 0 || index == testSets.size()) {
				if (!isLiveData) {
					BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getSourcePath(), BackTestCSVWriter.btPnlFileName), (first ? BackTestCSVWriter.getBTPnlHeader() : "") + String.join("", BackTestTask.allBTPnlResults), true);
					BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getSourcePath(), BackTestCSVWriter.btTradeFileName), (first ? BackTestCSVWriter.getBTTradeHeader() : "") + String.join("", BackTestTask.allBTTradeResults), true);
					BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getSourcePath(), BackTestCSVWriter.profitAndLossFileName), BackTestTask.allProfitAndLossResults.toString(), true);
					BackTestCSVWriter.writePositivePnlResult(mainUIParam);					
				}
				BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getSourcePath(), BackTestCSVWriter.btSummaryFileName), (first ? BackTestCSVWriter.getBTSummaryHeader(backTestResult.yearPnlMap.keySet()) : "") + String.join("", BackTestTask.allBTSummaryResults), true);
				
				BackTestTask.allBTPnlResults.clear();
				BackTestTask.allBTTradeResults.clear();
				BackTestTask.allBTSummaryResults.clear();
				BackTestTask.allPositivePnlResult.clear();
				BackTestTask.allProfitAndLossResults = new StringBuilder();
				BackTestCSVWriter.writeText(mainUIParam.getStepPath(), index + "," + testSets.size(), false);
				first = false;
			}
			
			milliseconds = System.currentTimeMillis() - start2;
			String percentage = NumberFormat.getInstance().format((float) index / (float) testSets.size() * 100);
			callBack.updateStatus(getStatus("Testset " + index + " of " + testSets.size() + " (" + percentage + "%)" + " ended in " + DateUtils.dateDiff(milliseconds)) + ", Estimated time left: " + DateUtils.dateDiff(milliseconds * (testSets.size() - index)));
			if (!BackTestTask.running)
				break;
		}
		Set<Integer> specifyDateRanges = new TreeSet<Integer>();
		if (!StringUtils.isBlank(mainUIParam.getnForPnl())) {
			for(String nForPnl : mainUIParam.getnForPnl().split(",")) {
				try {
					specifyDateRanges.add(Integer.parseInt(nForPnl));				
				} catch (Exception e) {
				}
			}			
		}
		BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getSourcePath(), BackTestCSVWriter.profitAndLossByDateFileName), BackTestCSVWriter.getBestPnlByDate(), true);
		BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getSourcePath(), BackTestCSVWriter.profitAndLossByDateRangeFileName), BackTestCSVWriter.getBestPnlBySpecifyDates(specifyDateRanges), true);
		milliseconds = System.currentTimeMillis() - start;
		callBack.updateStatus(getStatus("All task done, total time cost: " + DateUtils.dateDiff(milliseconds)));
		
//		BackTestCSVWriter.writeCSVResult(mainUIParam);
		if (!isLiveData) {
			SQLUtils.saveTestSetResult(FilenameUtils.concat(mainUIParam.getSourcePath(), BackTestCSVWriter.btSummaryFileName), mainUIParam.getVersion().replaceAll(" ", ""));
		}
		gc();
	}

	private void gc() {
		BackTestTask.allBTSummaryResults = null;
		BackTestTask.allBTPnlResults = null;
		BackTestTask.allBTTradeResults = null;
		BackTestTask.aTradingDayForCheckResult = null;
		BackTestTask.allProfitAndLossResults = null;
		BackTestTask.allProfitAndLossResultMap = null;
		BackTestTask.marketTimeMap = null;
		BackTestTask.sumOfLastTrade = null;
		BackTestTask.rowData = null;
		BackTestTask.sortedDateList = null;
		BackTestTask.allPositivePnlResult = null;
		BackTestTask.allProfitAndLossResults = null;
		System.gc();
		Runtime.getRuntime().freeMemory();
	}

	private String getStatus(String status) {
		return DateUtils.yyyyMMddHHmmss().format(new Date()) + " -> " + status;
	}
}
