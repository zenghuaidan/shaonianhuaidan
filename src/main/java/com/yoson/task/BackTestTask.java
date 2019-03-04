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
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
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
	public static StringBuilder allMorningProfitAndLossResults;
	public static StringBuilder allAfternoonProfitAndLossResults;
	public static Map<String, Integer> marketTimeMap;
	public static Map<String, Double> sumOfLastTrade;
	public static Map<String, List<ScheduleData>> rowData;
	public static List<String> sortedDateList;
	public static Map<String, String> allPositivePnlResult;
	private StatusCallBack callBack;
	public static boolean isLiveData = false;
	private static String[] dataTypes = new String[]{ "Open", "Avg", "Last", "Max", "Min" };
	
	public static List<String> getDataTypeList(MainUIParam mainUIParam) {
		List<String> dataTypeList = new ArrayList<String>();		
		
		Map<String, Boolean> dataTypeMap = new HashMap<String, Boolean>() {
			{
				put(dataTypes[0], mainUIParam == null ? false : mainUIParam.isOutputDataOpen());
				put(dataTypes[1], mainUIParam == null ? false : mainUIParam.isOutputDataAvg());
				put(dataTypes[2], mainUIParam == null ? false : mainUIParam.isOutputDataLast());
				put(dataTypes[3], mainUIParam == null ? false : mainUIParam.isOutputDataMax());
				put(dataTypes[4], mainUIParam == null ? false : mainUIParam.isOutputDataMin());
			}
		};
		for(int i = 0; i < dataTypes.length; i++) {
			if(dataTypeMap.get(dataTypes[i]))
				dataTypeList.add(dataTypes[i]);
		}
		return dataTypeList;
	}		
	
	@Override
	public void run() {		
		try {			
			int current = 1;
			int startStep = 0;
			if (!isLiveData) {
				try {
					File stepFile = new File(mainUIParam.getStepPath());
					if(stepFile.exists()) {
						FileInputStream input = new FileInputStream(stepFile);
						String step = IOUtils.toString(input);
						input.close();
						if(StringUtils.isNotEmpty(step)) {
							String[] steps = step.split(",");
							if(steps.length == 3) {
								startStep = Integer.parseInt(steps[0]);
								current = Integer.parseInt(steps[2]);
							}							
						}
					}
				} catch (Exception e) {
					try {
						FileUtils.cleanDirectory(new File(mainUIParam.getSourcePath()));			
					} catch (Exception ex) {
					}
				}
			}
			List<String> dataTypeList = getDataTypeList(mainUIParam);
			for(int i = 1; i <= dataTypeList.size(); i++) {
				if(i < current) continue;
				String dataType = dataTypeList.get(i-1);
				mainUIParam.setResultPath(FilenameUtils.concat(mainUIParam.getSourcePath(), dataType));
				mainUIParam.setTradeDataField("trade" + dataType.toLowerCase());
				mainUIParam.setAskDataField("ask" + dataType.toLowerCase());
				mainUIParam.setBidDataField("bid" + dataType.toLowerCase());
								
				runTestSet(i, startStep);
				startStep = 0;
				if (!BackTestTask.running)
					break;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			callBack.done();
		}
	}		
	
	public BackTestTask(MainUIParam mainUIParam, StatusCallBack callBack) {
		this.mainUIParam = mainUIParam;		
		this.callBack = callBack;
	}

	private void init() {
		BackTestTask.allBTSummaryResults = new ArrayList<String>();
		BackTestTask.allBTPnlResults = new ArrayList<String>();
		BackTestTask.allBTTradeResults = new ArrayList<String>();
		BackTestTask.aTradingDayForCheckResult = "";
		BackTestTask.allProfitAndLossResults = new StringBuilder();
		BackTestTask.allMorningProfitAndLossResults = new StringBuilder();
		BackTestTask.allAfternoonProfitAndLossResults = new StringBuilder();
		
		BackTestTask.rowData = new HashMap<String, List<ScheduleData>>();		
		BackTestTask.marketTimeMap = new HashMap<String, Integer>();
		BackTestTask.sumOfLastTrade = new HashMap<String, Double>();
		BackTestTask.allPositivePnlResult = new HashMap<String, String>();
	}
	
	public static void initRawData(List<String> sdatas, MainUIParam mainUIParam) throws ParseException {
		for (String sdata : sdatas) {
			ScheduleData sData = new ScheduleData(sdata.split(",")[0], sdata.split(",")[1], sdata.split(",")[2], sdata.split(",")[3], sdata.split(",")[4], sdata.split(",")[5], sdata.split(",")[6], sdata.split(",")[7], mainUIParam.getAskDataField(), mainUIParam.getBidDataField(), mainUIParam.getTradeDataField());
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
		List<ScheduleData> resultDatas = YosonEWrapper.toScheduleDataList(scheduledDataRecords, mainUIParam, Long.parseLong(scheduledDataRecords.get(scheduledDataRecords.size() - 1).getTime()));
		for (ScheduleData sData : resultDatas) {
			initRawData(mainUIParam, sData);
		}		
	}
	
	public static void applyLastMarketDayData(MainUIParam mainUIParam) throws ParseException {
		if(!mainUIParam.isIncludeLastMarketDayData()) return;		
		long lunchStartTimeTo = DateUtils.HHmmss().parse(mainUIParam.getLunchStartTimeTo()).getTime();
		long marketCloseTime = DateUtils.HHmmss().parse(mainUIParam.getMarketCloseTime()).getTime();
		List<String> scheduleDates = SQLUtils.getScheduleDates();
		for (int i = BackTestTask.sortedDateList.size() - 1; i >= 0; i--) {
			String today = BackTestTask.sortedDateList.get(i);
			String lastMarketDay = getLastMarketDay(scheduleDates, today);
			List<ScheduleData> resultDatas = BackTestTask.rowData.containsKey(lastMarketDay) ? BackTestTask.rowData.get(lastMarketDay) : SQLUtils.getLastMarketDayScheduleData(mainUIParam, lastMarketDay, true);
			List<ScheduleData> lastMarketDayData = new ArrayList<ScheduleData>();
			for(ScheduleData resultData : resultDatas) {
				long time = DateUtils.HHmmss().parse(resultData.getTimeStr()).getTime();
				boolean isAfternoon = time >= lunchStartTimeTo && time <= marketCloseTime;
				if(!isAfternoon) continue;
				BackTestTask.sumOfLastTrade.put(today, BackTestTask.sumOfLastTrade.get(today) == null ? resultData.getLastTrade() : BackTestTask.sumOfLastTrade.get(today) + resultData.getLastTrade());
				lastMarketDayData.add(resultData.copyAndSetAsLastMarketDayData());
			}
			BackTestTask.rowData.get(today).addAll(0, lastMarketDayData);
		}	
	}
	
	public static String getLastMarketDay(List<String> scheduleDates, String today) {
		if (scheduleDates != null && scheduleDates.indexOf(today) >= 0 && scheduleDates.indexOf(today) < (scheduleDates.size() - 1)) {
			return scheduleDates.get(scheduleDates.indexOf(today) + 1);
		}
		return "";
	}
	
	public static List<TestSet> getCombinations(MainUIParam mainUIParam) {
		List<TestSet> testSets = new ArrayList<TestSet>();
		return testSets;
	}
	
	public void runTestSet(int current, int startStep) throws IOException, ParseException {	
		init();
		long start = System.currentTimeMillis();
		
		callBack.updateStatus("The data path on server: " + mainUIParam.getResultPath() + System.lineSeparator());
		
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
		applyLastMarketDayData(mainUIParam);
		mainUIParam.setStartStr(sortedDateList.get(0));
		mainUIParam.setEndStr(sortedDateList.get(sortedDateList.size() - 1));
		
		List<TestSet> testSets = new ArrayList<TestSet>();
		for (int timer = mainUIParam.getTimer(); timer<= mainUIParam.getTimerTo() ; timer = timer + mainUIParam.getTimerLiteral())
		for (double smooth = mainUIParam.getSmooth(); smooth<= mainUIParam.getSmoothTo() ; smooth = smooth + mainUIParam.getSmoothLiteral())
		for (double action = mainUIParam.getAction(); action<= mainUIParam.getActionTo() ; action = action + mainUIParam.getActionLiteral())		
		for (double absoluteTradeStopLoss = mainUIParam.getAbsoluteTradeStopLoss(); absoluteTradeStopLoss<= mainUIParam.getAbsoluteTradeStopLossTo() ; absoluteTradeStopLoss = absoluteTradeStopLoss + mainUIParam.getAbsoluteTradeStopLossLiteral()) 
		for (int avgStep = mainUIParam.getAvgStep(); avgStep<= mainUIParam.getAvgStepTo() ; avgStep = avgStep + mainUIParam.getAvgStepLiteral()) {
			testSets.add(new TestSet(timer, smooth, action, absoluteTradeStopLoss, mainUIParam.getUnit(),
					mainUIParam.getMarketStartTime(), mainUIParam.getLunchStartTimeFrom(), mainUIParam.getLunchStartTimeTo(), 
					mainUIParam.getMarketCloseTime(), mainUIParam.getCashPerIndexPoint(), mainUIParam.getTradingFee(), 
					mainUIParam.getOtherCostPerTrade(), mainUIParam.getLastNumberOfMinutesClearPosition(), mainUIParam.getLunchLastNumberOfMinutesClearPosition(), mainUIParam.isIncludeMorningData(), avgStep, mainUIParam.isIncludeLastMarketDayData()));
		
		}
		BackTestCSVWriter.writeText(mainUIParam.getParamPath(), new Gson().toJson(mainUIParam), false);

		File resultDirectory = new File(mainUIParam.getResultPath());
		if(!resultDirectory.exists()) {
			resultDirectory.mkdir();
		}
		
		boolean first = (startStep == 0);
		if(first) {
			FileUtils.cleanDirectory(resultDirectory);
		}
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
				BackTestTask.aTradingDayForCheckResult = BackTestCSVWriter.getATradingDayContent(mainUIParam, backTestResult.dayRecords.get(RandomUtils.nextInt(mainUIParam.isIncludeLastMarketDayData() && backTestResult.dayRecords.size() > 1 ? 1 : 0, backTestResult.dayRecords.size())));
				BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getResultPath(), BackTestCSVWriter.aTradingDayForCheckFileName), BackTestCSVWriter.getATradingDayHeader() + BackTestTask.aTradingDayForCheckResult, true);
			}	
			
			if (index % Global.savePoint == 0 || index == testSets.size()) {
				if (!isLiveData) {
					BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getResultPath(), BackTestCSVWriter.btPnlFileName), (first ? BackTestCSVWriter.getBTPnlHeader() : "") + String.join("", BackTestTask.allBTPnlResults), true);
					BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getResultPath(), BackTestCSVWriter.btTradeFileName), (first ? BackTestCSVWriter.getBTTradeHeader() : "") + String.join("", BackTestTask.allBTTradeResults), true);
					BackTestCSVWriter.writePositivePnlResult(mainUIParam);					
				}
				BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getResultPath(), BackTestCSVWriter.profitAndLossFileName), BackTestTask.allProfitAndLossResults.toString(), true);
				if(mainUIParam.isMatrixFile()) {
					BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getResultPath(), BackTestCSVWriter.morningProfitAndLossFileName), BackTestTask.allMorningProfitAndLossResults.toString(), true);
					BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getResultPath(), BackTestCSVWriter.afternoonProfitAndLossFileName), BackTestTask.allAfternoonProfitAndLossResults.toString(), true);					
				}
				BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getResultPath(), BackTestCSVWriter.btSummaryFileName), (first ? BackTestCSVWriter.getBTSummaryHeader(backTestResult.yearPnlMap.keySet(), backTestResult.monthPnlMap.keySet()) : "") + String.join("", BackTestTask.allBTSummaryResults), true);
				
				BackTestTask.allBTPnlResults.clear();
				BackTestTask.allBTTradeResults.clear();
				BackTestTask.allBTSummaryResults.clear();
				BackTestTask.allPositivePnlResult.clear();
				BackTestTask.allProfitAndLossResults = new StringBuilder();
				BackTestTask.allMorningProfitAndLossResults = new StringBuilder();
				BackTestTask.allAfternoonProfitAndLossResults = new StringBuilder();
				if (!isLiveData) {
					BackTestCSVWriter.writeText(mainUIParam.getStepPath(), index + "," + testSets.size() + "," + current, false);
				}
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
		if(mainUIParam.isMatrixFile()) {
			BackTestCSVWriter.initProfitAndLossResultMap(mainUIParam);
			BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getResultPath(), BackTestCSVWriter.profitAndLossByDateFileName), BackTestCSVWriter.getBestPnlByDate(mainUIParam), true);
			BackTestCSVWriter.writeText(FilenameUtils.concat(mainUIParam.getResultPath(), BackTestCSVWriter.profitAndLossByDateRangeFileName), BackTestCSVWriter.getBestPnlBySpecifyDates(specifyDateRanges, mainUIParam), true);			
			BackTestCSVWriter.getAccumulatePnlBySpecifyDates(specifyDateRanges, mainUIParam);
		}
		milliseconds = System.currentTimeMillis() - start;
		callBack.updateStatus(getStatus("All task done, total time cost: " + DateUtils.dateDiff(milliseconds)));
		
//		BackTestCSVWriter.writeCSVResult(mainUIParam);
		if (!isLiveData) {
			SQLUtils.saveTestSetResult(FilenameUtils.concat(mainUIParam.getResultPath(), BackTestCSVWriter.btSummaryFileName), mainUIParam.getVersion().replaceAll(" ", ""));
		}
		gc();
	}

	private void gc() {
		BackTestTask.allBTSummaryResults = null;
		BackTestTask.allBTPnlResults = null;
		BackTestTask.allBTTradeResults = null;
		BackTestTask.aTradingDayForCheckResult = null;
		BackTestTask.allProfitAndLossResults = null;
		BackTestTask.allMorningProfitAndLossResults = null;
		BackTestTask.allAfternoonProfitAndLossResults = null;
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
