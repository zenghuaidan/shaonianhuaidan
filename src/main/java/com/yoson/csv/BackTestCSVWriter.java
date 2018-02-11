package com.yoson.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.yoson.chart.chartWriter;
import com.yoson.date.DateUtils;
import com.yoson.model.BackTestResult;
import com.yoson.model.MainUIParam;
import com.yoson.model.PerDayRecord;
import com.yoson.model.PerSecondRecord;
import com.yoson.task.BackTestTask;

public class BackTestCSVWriter {	
	public static final String profitAndLossFileName = "Back Test ProfitAndLoss.csv";
	public static final String profitAndLossByDateFileName = "Back Test ProfitAndLoss By Date.csv";
	public static final String profitAndLossByDateRangeFileName = "Back Test ProfitAndLoss By Date Range.csv";
	public static final String btSummaryFileName = "BT_Summary.csv";
	public static final String btPnlFileName = "BT_PnL.csv";
	public static final String btTradeFileName = "BT_Trade.csv";
	public static final String aTradingDayForCheckFileName = "A Trading Day FOR CHECK.csv";
	
	public static void writeText(String filePath, String result, boolean append) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(filePath, append);
			IOUtils.write(result, fileOutputStream, Charset.forName("utf-8"));
			fileOutputStream.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
	
	public static String readText(String filePath) {
		String result = "";
		try {
			FileInputStream fileInputStream = new FileInputStream(filePath);
			result = IOUtils.toString(fileInputStream);
			fileInputStream.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	} 
	
	public static void writeCSVResult(MainUIParam mainUIParam) throws IOException {
//		writeCSVResult(FilenameUtils.concat(mainUIParam.getSourcePath(), summaryFileName), getSummaryHeader() + String.join("", BackTestTask.allSummaryResults));
//		writeCSVResult(FilenameUtils.concat(mainUIParam.getSourcePath(), btPnlFileName), getBTPnlHeader() + String.join("", BackTestTask.allBTPnlResults));
//		writeCSVResult(FilenameUtils.concat(mainUIParam.getSourcePath(), btTradeFileName), getBTTradeHeader() + String.join("", BackTestTask.allBTTradeResults));
//		writeCSVResult(FilenameUtils.concat(mainUIParam.getSourcePath(), btSummaryFileName), getBTSummaryHeader() + String.join("", BackTestTask.allBTSummaryResults));
//		writeCSVResult(FilenameUtils.concat(mainUIParam.getSourcePath(), aTradingDayForCheckFileName), getATradingDayHeader() + BackTestTask.aTradingDayForCheckResult);
//		writeCSVResult(FilenameUtils.concat(mainUIParam.getSourcePath(), profitAndLossFileName), getProfitAndLossContent());
//		writePositivePnlResult(mainUIParam);
	}

	public static void writePositivePnlResult(MainUIParam mainUIParam) throws IOException {
		for (String key : BackTestTask.allPositivePnlResult.keySet()) {
			String[] values = key.split("#");
			String folder = FilenameUtils.concat(mainUIParam.getSourcePath(), values[1]);
			if (!new File(folder).exists()) {
				FileUtils.forceMkdir(new File(folder));
			}
			writeText(FilenameUtils.concat(folder, values[0] + ".csv"), getATradingDayHeader() + BackTestTask.allPositivePnlResult.get(key), true);
		}
	}
	
	public static String getATradingDayHeader() {
		return "Time,Bid Price,Ask Price,Last Trade,Check Market Time,Reference,CP counting,CP,CPS,CPS Av. L,CP Acc count,Previous Max CPAC,counting after CP,Est.,ON/ OFF,Pre.Action,Action,smooth Action,Position,Pos. counting,MTM,Max. MTM,PnL,No. Trades,Total PnL\n";
	}
	
	public static String getBTTradeHeader() {
		return "";
//		return "combinationKey,version,Source,Date,zeroTrade,postiveTrade,negativeTrade,avgHolding,average_gain,average_loss,\n";
	}
	
	public static String getBTPnlHeader() {
		return "";
//		return "combinationKey,Date,version,Source,PnL,\n";
	}
	
	public static final String TotalPnl = "Total Pnl of ";
	
	public static String getBTSummaryHeader(Set<Integer> years) {
		List<String> yearColums = new ArrayList<String>();
		for (Integer year : years) {
			yearColums.add(TotalPnl + year);
		}
		String yearColumnStr = String.join(",", yearColums);
		yearColumnStr = yearColumnStr.length() > 0 ? (yearColumnStr + ",") : yearColumnStr;
		return "Test no.,key,version,Source,CP timer,CP Buffer,CP Hit Rate,CP smooth,estimation buffer,action trigger,action counting,% trade stoploss trigger,% trade stoploss,Absolute trade stoploss,Morning Start Time,Lunch Start Time,Cash per index point,Trading fee,Other cost per trade,No. of days,Total PnL,Average PnL ,Total trades,Average trades,No. of winning days,No. of losing days,Winning %,Average gain per +ve trade,Average gain per -ve trade,Average 0 PnL trades,Average no. of positive trade,Average no. of negative trade,Average holding time,Adjusted Profit after fee,Worst Lossing Day,Best Profit Day,Worst Lossing Streak,Best Winning Streak,Lossing Streak freq,Winning Streak freq,Sum Of Lossing Streak,Sum Of Winning Streak,Avg Of Lossing Streak,Avg Of Winning Streak,Max Lossing Streak Length,Max Winning Streak Length," + yearColumnStr +"Start Time,End Time,Including Morning Data,Ignore Lunch Time\n";
	}

	public static String getBTSummaryContent(int testNo, MainUIParam mainUIParam, BackTestResult backTestResult) {
		StringBuilder content = new StringBuilder();
		content.append(testNo + ",")
		.append(backTestResult.testSet.getKey()  +  ",")
		.append(mainUIParam.getVersion() + ",")
		.append(mainUIParam.getSource() + ",")
		.append(backTestResult.testSet.getCpTimer() + ",")
		.append(backTestResult.testSet.getCpBuffer() + ",")
		.append(backTestResult.testSet.getCpHitRate() + ",")
		.append(backTestResult.testSet.getCpSmooth() + ",")
		.append(backTestResult.testSet.getEstimationBuffer() + ",")
		.append(backTestResult.testSet.getActionTrigger() + ",")
		.append(backTestResult.testSet.getActionCounting() + ",")
		.append(backTestResult.testSet.getTradeStopLossTrigger() + ",")
		.append(backTestResult.testSet.getTradeStopLossTriggerPercent() + ",")
		.append(backTestResult.testSet.getAbsoluteTradeStopLoss() + ",")
		.append(backTestResult.testSet.getMarketStartTime() + ",")
		.append(backTestResult.testSet.getLunchStartTimeFrom() + ",")
		.append(backTestResult.testSet.getCashPerIndexPoint() + ",")
		.append(backTestResult.testSet.getTradingFee() + ",")
		.append(backTestResult.testSet.getOtherCostPerTrade() + ",")
		.append(backTestResult.totalDays + ",")
		.append(backTestResult.totalPnL + ",")
		.append(backTestResult.averagePnL + ",")
		.append(backTestResult.totalTrades + ",")
		.append(backTestResult.averageTrades + ",")
		.append(backTestResult.totalWinningDays + ",")
		.append(backTestResult.totalLosingDays + ",")
		.append(backTestResult.winningPercentage + ",")
		.append(backTestResult.averageProfitOfPositiveTrade + ",")
		.append(backTestResult.averageProfitOfNegativeTrade + ",")
		.append(backTestResult.averageZeroPnLTrade + ",")
		.append(backTestResult.averageNoPositiveTrade + ",")
		.append(backTestResult.averageNoNegativeTrade + ",")
		.append(backTestResult.averageHoldingTime + ",")
		.append(backTestResult.adjustedPnLAfterFee + ",")
		.append(backTestResult.worstLossDay + ",")
		.append(backTestResult.bestProfitDay + ",")
		.append(backTestResult.worstLossingStreak + ",")
		.append(backTestResult.bestWinningStreak + ",")
		.append(backTestResult.lossingStreakfreq + ",")
		.append(backTestResult.winningStreakFreq + ",")
		.append(backTestResult.sumOfLossingStreak + ",")
		.append(backTestResult.sumOfWinningStreak + ",")
		.append(backTestResult.averageOfLossingStreak + ",")
		.append(backTestResult.averageOfWinningStreak + ",")
		.append(backTestResult.maxLossingStreakLength + ",")
		.append(backTestResult.maxWinningStreakLength + ",");
		for (int year : backTestResult.yearPnlMap.keySet()) {
			content.append(backTestResult.yearPnlMap.get(year) + ",");		
		}
		content.append(mainUIParam.getStartStr() + ",");
		content.append(mainUIParam.getEndStr() + ",");
		content.append((mainUIParam.isIncludeMorningData() ? "Yes" : "No") + ",");
		content.append((mainUIParam.isIgnoreLunchTime() ? "Yes" : "No") + ",");
		content.append("\n");
		return content.toString();
	}

	public static void initBTPnLAndTradeAndProfitAndLossContent(int id, MainUIParam mainUIParam, BackTestResult backTestResult, StringBuilder pnlContent, StringBuilder tradContent) {
		if (id == 1) {
			StringBuilder allProfitAndLossResultsHeader = new StringBuilder("Test no.,key,");
			for (PerDayRecord perDayRecord : backTestResult.dayRecords) {
				String dateStr = perDayRecord.getDateStr();
				allProfitAndLossResultsHeader.append(dateStr + ",");
			}
			allProfitAndLossResultsHeader.append("\n");
			BackTestTask.allProfitAndLossResults.append(allProfitAndLossResultsHeader);
		}
		BackTestTask.allProfitAndLossResults.append(id + "," + backTestResult.testSet.getKey()  +  ",");
		for (PerDayRecord perDayRecord : backTestResult.dayRecords) {
			Long dateLong = perDayRecord.getDate().getTime();
			if(!BackTestTask.allProfitAndLossResultMap.containsKey(dateLong) || (Double)BackTestTask.allProfitAndLossResultMap.get(dateLong).get(0) < perDayRecord.totalPnL) {
				List<Object> pnlInfo = new ArrayList<Object>();
				pnlInfo.add(perDayRecord.totalPnL);
				pnlInfo.add(id);
				pnlInfo.add(backTestResult.testSet.getKey());
				BackTestTask.allProfitAndLossResultMap.put(dateLong, pnlInfo);
			}
			BackTestTask.allProfitAndLossResults.append(perDayRecord.totalPnL + ",");
			
			pnlContent.append(backTestResult.testSet.getKey()  +  ",")
			.append(perDayRecord.getDateStr() + ",")
			.append(mainUIParam.getVersion() + ",")
			.append(mainUIParam.getSource() + ",")
			.append(perDayRecord.totalPnL + ",")
			.append("\n"); 
			
			tradContent.append(backTestResult.testSet.getKey()  +  ",")
			.append(mainUIParam.getVersion() + ",")
			.append(mainUIParam.getSource() + ",")
			.append(perDayRecord.getDateStr() + ",")
			.append(perDayRecord.zeroPnlTrades + ",")
			.append(perDayRecord.positiveTrades + ",")
			.append(perDayRecord.negativeTrades + ",")
			.append(perDayRecord.averageHoldingTime + ",")
			.append(perDayRecord.averagePositiveTrades + ",")
			.append(perDayRecord.averageNegativeTrades + ",")
			.append("\n");
			
			
			if (mainUIParam.isOutputChart()) {			
				new chartWriter(mainUIParam.getSourcePath(), mainUIParam.getUnit(), perDayRecord);					
			}
		}
		BackTestTask.allProfitAndLossResults.append("\n");
	}
	
	public static String getBestPnlByDate() {
		StringBuilder content = new StringBuilder();
		content.append("Date,Test no.,key,Total Pnl").append("\n");
		if(BackTestTask.allProfitAndLossResultMap != null) {
			for(Long dateLong : BackTestTask.allProfitAndLossResultMap.keySet()) {
				List<Object> pnlInfo = BackTestTask.allProfitAndLossResultMap.get(dateLong);
				Date date = new Date(dateLong);
				String dateStr = DateUtils.yyyyMMdd().format(date);
				content.append(dateStr + "," + pnlInfo.get(1) + "," + pnlInfo.get(2) + "," + pnlInfo.get(0)).append("\n");
			}
		}
		return content.toString();
	}
	
	public static String getBestPnlBySpecifyDates(Set<Integer> specifyDateRanges) {
		StringBuilder content = new StringBuilder();
		if(specifyDateRanges != null && specifyDateRanges.size() > 0) {
			content.append("Date,");
			for(int specifyDateRange : specifyDateRanges) {
				content.append("," + "n=" + specifyDateRange + " Test no.," + "n=" + specifyDateRange + " key," + "n=" + specifyDateRange + " Total Pnl,");
			}
			content.append("\n");
			if(BackTestTask.allProfitAndLossResultMap != null) {
				List<List<Object>> allProfitAndLossResultList = new ArrayList<List<Object>>();
				for(Long dateLong : BackTestTask.allProfitAndLossResultMap.keySet()) {
					allProfitAndLossResultList.add(BackTestTask.allProfitAndLossResultMap.get(dateLong));
				}
				int i = 1;
				for(Long dateLong : BackTestTask.allProfitAndLossResultMap.keySet()) {
					Date date = new Date(dateLong);
					String dateStr = DateUtils.yyyyMMdd().format(date);
					content.append(dateStr + ",");
					for(int specifyDateRange : specifyDateRanges) {
						if(specifyDateRange >= i) {
							content.append(",0,0,0,");							
						} else {
							int j = i - specifyDateRange - 1;
							List<Object> maxPnlInfo = allProfitAndLossResultList.get(j);
							for(; j < i - 1; j++) {
								if((double)maxPnlInfo.get(0) < (double)allProfitAndLossResultList.get(j).get(0)) {
									maxPnlInfo = allProfitAndLossResultList.get(j);
								}
							}
							content.append("," + maxPnlInfo.get(1) + "," + maxPnlInfo.get(2) + "," + maxPnlInfo.get(0) + ",");
						}						
					}
					content.append("\n");
					i++;
				}
			}
		}
		return content.toString();
	}
	
	public static String getATradingDayContent(MainUIParam mainUIParam, PerDayRecord perDayRecord)
	{
		StringBuilder content = new StringBuilder();
		for (PerSecondRecord perSecondRecord : perDayRecord.dailyPerSecondRecordList)
		{
			 constructPerSecondRecord(content, perSecondRecord);
		}
		content.append("positive trades," + perDayRecord.positiveTrades + "\n");
		content.append("negative trades," + perDayRecord.negativeTrades + "\n");
		content.append("0 PnL Trades," + perDayRecord.zeroPnlTrades + "\n");
		content.append("average +ve trades," + perDayRecord.averagePositiveTrades + "\n");
		content.append("average -ve trades," + perDayRecord.averageNegativeTrades + "\n");
		content.append("average holding time," + perDayRecord.averageHoldingTime + "\n");
		content.append("Daily trading range," + perDayRecord.dailyTradingRange + "\n");
		content.append("Daily index vol," + perDayRecord.dailyIndexVol + "\n");
		content.append("Performance in vol," + perDayRecord.performanceInVol + "\n");
		content.append("Index performance," + perDayRecord.indexPerformance + "\n");
		content.append("Performance in pts," + perDayRecord.performanceInPts + "\n");
		content.append("Average HL Diff.," + perDayRecord.averageHLDiff + "\n");
		content.append("CP timer," + mainUIParam.getCpTimer() + "\n");
		content.append("CP Buffer," + mainUIParam.getCpBuffer() + "\n");
		content.append("CP Hit Rate," + mainUIParam.getCpHitRate() + "\n");
		content.append("CP smooth," + mainUIParam.getCpSmooth() + "\n");
		content.append("estimation buffer," + mainUIParam.getEstimationBuffer() + "\n");
		content.append("action trigger," + mainUIParam.getActionTrigger() + "\n");
		content.append("action counting," + mainUIParam.getActionCounting() + "\n");
		content.append("% trade stoploss trigger," + mainUIParam.getTradeStopLossTrigger() + "\n");
		content.append("% trade stoploss," + mainUIParam.getTradeStopLossTriggerPercent() + "\n");
		content.append("Absolute trade stoploss," + mainUIParam.getAbsoluteTradeStopLoss() + "\n");	 
		return content.toString();
	}

	public static void constructPerSecondRecord(StringBuilder content, PerSecondRecord perSecondRecord) {
		content.append(perSecondRecord.getTimeStr() + ",");
		 content.append(perSecondRecord.getBidPrice() + ",");
		 content.append(perSecondRecord.getAskPrice() + ",");
		 content.append(perSecondRecord.getLastTrade() + ",");
		 content.append(perSecondRecord.getCheckMarketTime() + ",");
		 content.append(perSecondRecord.getReference() + ",");
		 content.append(perSecondRecord.getCpCounting() + ",");
		 content.append(perSecondRecord.getCp() + ",");
		 content.append(perSecondRecord.getCps() + ",");
		 content.append(perSecondRecord.getCpsAverage() + ",");
		 content.append(perSecondRecord.getCpAccount() + ",");
		 content.append(perSecondRecord.getPreviousMaxCPAC() + ",");
		 content.append(perSecondRecord.getCountingAfterCP() + ",");
		 content.append(perSecondRecord.getEst() + ",");
		 content.append(perSecondRecord.getOffOn() + ",");
		 content.append(perSecondRecord.getPreAction() + ",");
		 content.append(perSecondRecord.getAction() + ",");
		 content.append(perSecondRecord.getSmoothAction() + ",");
		 content.append(perSecondRecord.getPosition() + ",");
		 content.append(perSecondRecord.getPosCounting() + ",");
		 content.append(perSecondRecord.getMtm() + ",");
		 content.append(perSecondRecord.getMaxMtm() + ",");
		 content.append(perSecondRecord.getPnl() + ",");
		 content.append(perSecondRecord.getTradeCount() + ",");
		 content.append(perSecondRecord.getTotalPnl());		 
		 content.append("\n");
	}
}
