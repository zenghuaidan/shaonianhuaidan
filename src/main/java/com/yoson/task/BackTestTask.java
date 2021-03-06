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

public class BackTestTask implements Runnable {
	public static boolean running = false;
	private MainUIParam mainUIParam;	
	
	public static List<String> allBTSummaryResults;
	public static List<String> allBTPnlResults;
	public static List<String> allBTTradeResults;
	public static List<String> allSummaryResults;
	public static String aTradingDayForCheckResult;
//	public static Map<String, Map<Integer, String>> allProfitAndLossResults;
	public static StringBuilder allProfitAndLossResults;
	public static Map<String, Integer> marketTimeMap;
	public static Map<String, Double> sumOfLastTrade;
	public static Map<String, List<ScheduleData>> rowData;
	public static List<String> sortedDateList;
	public static Map<String, String> allPositivePnlResult;
	private StatusCallBack callBack;
	
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
		BackTestTask.allSummaryResults = new ArrayList<String>();
		BackTestTask.aTradingDayForCheckResult = "";
//		BackTestTask.allProfitAndLossResults = new TreeMap<String, Map<Integer, String>>(new DateComparator());
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
	}

	public void runTestSet() throws IOException, ParseException {	
		long start = System.currentTimeMillis();
		
		callBack.updateStatus("The data path on server: " + mainUIParam.getSourcePath() + System.lineSeparator());
		
		callBack.updateStatus(getStatus("Getting data from database started, this may cost several minutes, pls wait..."));
		
		initRawData(SQLUtils.initScheduleData(mainUIParam), mainUIParam);	
		
		long milliseconds = System.currentTimeMillis() - start;
		callBack.updateStatus(getStatus("Getting data from database ended, total cost: " + DateUtils.dateDiff(milliseconds)));
		
		BackTestTask.sortedDateList = new ArrayList<String>(BackTestTask.rowData.keySet());
		if (BackTestTask.sortedDateList.size() == 0) {
			callBack.updateStatus(getStatus("No data been selected from database"));			
			return;
		}
		Collections.sort(BackTestTask.sortedDateList, new DateComparator());
		mainUIParam.setStartStr(sortedDateList.get(0));
		mainUIParam.setEndStr(sortedDateList.get(sortedDateList.size() - 1));
		
		List<TestSet> testSets = new ArrayList<TestSet>();
		for (int tShort = mainUIParam.gettShort(); tShort<= mainUIParam.gettShortTo() ; tShort = tShort + mainUIParam.gettShortLiteral())
		for (int tLong = mainUIParam.gettLong(); tLong<= mainUIParam.gettLongTo() ; tLong = tLong + mainUIParam.gettLongLiteral())
		for (int tLong2 = mainUIParam.gettLong2(); tLong2<= mainUIParam.gettLong2To() ; tLong2 = tLong2 + mainUIParam.gettLong2Literal())
		for (double stopLoss = mainUIParam.getStopLoss(); stopLoss<= mainUIParam.getStopLossTo(); stopLoss = stopLoss + mainUIParam.getStopLossLiteral())
		for (double tradeStopLoss = mainUIParam.getTradeStopLoss(); tradeStopLoss<= mainUIParam.getTradeStopLossTo() ; tradeStopLoss = tradeStopLoss + mainUIParam.getTradeStopLossLiteral())
		for (double hld = mainUIParam.getHld(); hld<= mainUIParam.getHldTo() ; hld = hld + mainUIParam.getHldLiteral())
		for (double instanTradeStopLoss = mainUIParam.getInstantTradeStoploss(); instanTradeStopLoss<= mainUIParam.getInstantTradeStoplossTo() ; instanTradeStopLoss = instanTradeStopLoss + mainUIParam.getInstantTradeStoplossLiteral())
		for (double itsCounter = mainUIParam.getItsCounter(); itsCounter<= mainUIParam.getItsCounterTo() ; itsCounter = itsCounter + mainUIParam.getItsCounterLiteral())
		for (double stopGainPercent = mainUIParam.getStopGainPercent(); stopGainPercent<= mainUIParam.getStopGainPercentTo() ; stopGainPercent = stopGainPercent + mainUIParam.getStopGainPercentLiteral())
		for (double stopGainTrigger = mainUIParam.getStopGainTrigger(); stopGainTrigger<= mainUIParam.getStopGainTriggerTo() ; stopGainTrigger = stopGainTrigger + mainUIParam.getStopGainTriggerLiteral()) {
			testSets.add(new TestSet(tShort, tLong, tLong2, hld, stopLoss, tradeStopLoss,
					instanTradeStopLoss, itsCounter, stopGainPercent, stopGainTrigger, mainUIParam.getUnit(),
					mainUIParam.getMarketStartTime(), mainUIParam.getLunchStartTimeFrom(), mainUIParam.getLunchStartTimeTo(), 
					mainUIParam.getMarketCloseTime(), mainUIParam.getCashPerIndexPoint(), mainUIParam.getTradingFee(), 
					mainUIParam.getOtherCostPerTrade(), mainUIParam.getLastNumberOfMinutesClearPosition(), mainUIParam.getLunchLastNumberOfMinutesClearPosition()));
		}
		BackTestCSVWriter.writeText(mainUIParam.getParamPath(), new Gson().toJson(mainUIParam), false);
		int startStep = 0;
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
			
			BackTestTask.allSummaryResults.add(BackTestCSVWriter.getSummaryContent(index, backTestResult));
			
			StringBuilder pnlContent = new StringBuilder();
			StringBuilder tradContent = new StringBuilder();
			BackTestCSVWriter.initBTPnLAndTradeAndProfitAndLossContent(index, mainUIParam, backTestResult, pnlContent, tradContent);
			BackTestTask.allBTPnlResults.add(pnlContent.toString());
			BackTestTask.allBTTradeResults.add(tradContent.toString());
			BackTestTask.allBTSummaryResults.add(BackTestCSVWriter.getBTSummaryContent(mainUIParam, backTestResult));
			
			if(index == 1 && backTestResult.dayRecords.size() > 0) {
				BackTestTask.aTradingDayForCheckResult = BackTestCSVWriter.getATradingDayContent(mainUIParam, backTestResult.dayRecords.get(2));
				BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getSourcePath(), BackTestCSVWriter.aTradingDayForCheckFileName), BackTestCSVWriter.getATradingDayHeader() + BackTestTask.aTradingDayForCheckResult, true);
			}	
			
			if (index % Global.savePoint == 0 || index == testSets.size()) {
				BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getSourcePath(), BackTestCSVWriter.summaryFileName), (first ? BackTestCSVWriter.getSummaryHeader() : "") + String.join("", BackTestTask.allSummaryResults), true);
				BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getSourcePath(), BackTestCSVWriter.btPnlFileName), (first ? BackTestCSVWriter.getBTPnlHeader() : "") + String.join("", BackTestTask.allBTPnlResults), true);
				BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getSourcePath(), BackTestCSVWriter.btTradeFileName), (first ? BackTestCSVWriter.getBTTradeHeader() : "") + String.join("", BackTestTask.allBTTradeResults), true);
				BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getSourcePath(), BackTestCSVWriter.btSummaryFileName), (first ? BackTestCSVWriter.getBTSummaryHeader() : "") + String.join("", BackTestTask.allBTSummaryResults), true);
				BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getSourcePath(), BackTestCSVWriter.profitAndLossFileName), BackTestTask.allProfitAndLossResults.toString(), true);
				BackTestCSVWriter.writePositivePnlResult(mainUIParam);
				
				BackTestTask.allSummaryResults.clear();
				BackTestTask.allBTPnlResults.clear();
				BackTestTask.allBTTradeResults.clear();
				BackTestTask.allBTSummaryResults.clear();
				BackTestTask.allPositivePnlResult.clear();
				BackTestCSVWriter.writeText(mainUIParam.getStepPath(), index + "," + testSets.size(), false);
				first = false;
			}
			
			milliseconds = System.currentTimeMillis() - start2;
			String percentage = NumberFormat.getInstance().format((float) index / (float) testSets.size() * 100);
			callBack.updateStatus(getStatus("Testset " + index + " of " + testSets.size() + " (" + percentage + "%)" + " ended in " + DateUtils.dateDiff(milliseconds)) + ", Estimated time left: " + DateUtils.dateDiff(milliseconds * (testSets.size() - index)));
			if (!BackTestTask.running)
				break;
		}
		
		milliseconds = System.currentTimeMillis() - start;
		callBack.updateStatus(getStatus("All task done, total time cost: " + DateUtils.dateDiff(milliseconds)));
		
//		BackTestCSVWriter.writeCSVResult(mainUIParam);
		SQLUtils.saveTestSetResult(FilenameUtils.concat(mainUIParam.getSourcePath(), BackTestCSVWriter.summaryFileName), mainUIParam.getVersion());
		gc();
	}

	private void gc() {
		BackTestTask.allBTSummaryResults = null;
		BackTestTask.allBTPnlResults = null;
		BackTestTask.allBTTradeResults = null;
		BackTestTask.allSummaryResults = null;
		BackTestTask.aTradingDayForCheckResult = null;
		BackTestTask.allProfitAndLossResults = null;
		BackTestTask.marketTimeMap = null;
		BackTestTask.sumOfLastTrade = null;
		BackTestTask.rowData = null;
		BackTestTask.sortedDateList = null;
		BackTestTask.allPositivePnlResult = null;
		System.gc();
		Runtime.getRuntime().freeMemory();
	}

	private String getStatus(String status) {
		return DateUtils.yyyyMMddHHmmss().format(new Date()) + " -> " + status;
	}
}
