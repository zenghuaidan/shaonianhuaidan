package com.yoson.csv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.yoson.chart.chartWriter;
import com.yoson.model.BackTestResult;
import com.yoson.model.MainUIParam;
import com.yoson.model.PerDayRecord;
import com.yoson.model.PerSecondRecord;
import com.yoson.task.BackTestTask;

public class BackTestCSVWriter {	
	public static final String summaryFileName = "Back Test  Summary.csv";
	public static final String profitAndLossFileName = "Back Test  ProfitAndLoss.csv";
	public static final String btSummaryFileName = "BT_Summary.csv";
	public static final String btPnlFileName = "BT_PnL.csv";
	public static final String btTradeFileName = "BT_Trade.csv";
	public static final String aTradingDayForCheckFileName = "A Trading Day FOR CHECK.csv";
	
	public static void writeCSVResult(String filePath, String result) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(filePath, true);
		IOUtils.write(result, fileOutputStream, Charset.forName("utf-8"));
		fileOutputStream.close();
	} 
	
	public static void writeText(String filePath, String result) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(filePath);
		IOUtils.write(result, fileOutputStream, Charset.forName("utf-8"));
		fileOutputStream.close();
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
			writeCSVResult(FilenameUtils.concat(folder, values[0] + ".csv"), getATradingDayHeader() + BackTestTask.allPositivePnlResult.get(key));
		}
	}
	
	public static String getATradingDayHeader() {
		return "Time,Bid Price,Ask Price,Last Trade,Check Market Time,Reference,T Counter,HShort,HLong,HLong2,LShort,LLong,LLong2,Gmax,Gmin,GHLD,AVGHLD,Action,Smooth Action,Position,PC,MTM,MMTM,PnL,No_Trade,Total_PnL,HDiff,LDiff\n";
	}
	
	public static String getBTTradeHeader() {
		return "";
//		return "combinationKey,version,Source,Date,zeroTrade,postiveTrade,negativeTrade,avgHolding,average_gain,average_loss,\n";
	}
	
	public static String getBTPnlHeader() {
		return "";
//		return "combinationKey,Date,version,Source,PnL,\n";
	}
	
	public static String getBTSummaryHeader() {
		return "";
//		return "key,version,Source,T-Short,T-Long,T-Long2,HLD,Stoploss,Trade Stop Loss,Morning_Start_Time,Lunch_Start_Time,Cash_per_index_point,Trading_fee,Other_cost_per_trade,No_of_days,TotalPnL,AveragePnL,Total_trades,Average_trades,No_of_win_days,No_of_loss_days,winning_percent,Average_gain_per_+ve_trade,Average_gain_per_-ve_trade,Average_0PnL_trades,Average_no_of_positive_trade,Average_no_of_negative_trade,Average _holding_time,Adjusted_Profi_ after_fee,Worst_Lossing_Day,Best_Profit_Day,Worst_Lossing_Streak,Best_Winning_Streak,Lossing_Streak_freq,Winning_Streak_freq,Sum_Of_Lossing_Streak,Sum_Of_Winning_Streak,Avg_Of_Lossing_Streak,Avg_Of_Winning_Streak,Max_Lossing_Streak_Length,Max_Winning_Streak_Length,\n";
	}

	public static String getSummaryHeader() {
		return "Test no.,T-Short,T-Long,T-Long2,HLD,Stoploss,Trade Stop Loss,Morning Start Time,Lunch Start Time,Cash per index point,Trading fee,Other cost per trade,No. of days,Total PnL,Average PnL ,Total trades,Average trades,No. of winning days,No. of losing days,Winning %,Average gain per +ve trade,Average gain per -ve trade,Average 0 PnL trades,Average no. of positive trade,Average no. of negative trade,Average holding time,Adjusted Profit after fee,Worst Lossing Day,Best Profit Day,Worst Lossing Streak,Best Winning Streak,Lossing Streak freq,Winning Streak freq,Sum Of Lossing Streak,Sum Of Winning Streak,Avg Of Lossing Streak,Avg Of Winning Streak,Max Lossing Streak Length,Max Winning Streak Length\n";
	}
	
	public static String getSummaryContent(int id, BackTestResult backTestResult) {
		StringBuilder content = new StringBuilder();
		content.append(id + ",");
		content.append(backTestResult.testSet.gettShort() + ",");
		content.append(backTestResult.testSet.gettLong() + ",");				
		content.append(backTestResult.testSet.gettLong2() + ",");
		content.append(backTestResult.testSet.getHld() + ",");
		content.append((backTestResult.testSet.getStopLoss() * backTestResult.testSet.getUnit()) + ",");				
		content.append((backTestResult.testSet.getTradeStopLoss() * backTestResult.testSet.getUnit()) + ",");				
		content.append(backTestResult.testSet.getMarketStartTime() + ",");
		if ((backTestResult.testSet.getLunchStartTimeFrom().compareTo("23:59:59"))==1)
		{
			content.append("No lunch time,");
		}
		else
		{					
			content.append(backTestResult.testSet.getLunchStartTimeFrom() + ",");
		}
		content.append(backTestResult.testSet.getCashPerIndexPoint() + ",");
		content.append(backTestResult.testSet.getTradingFee() + ",");
		content.append(backTestResult.testSet.getOtherCostPerTrade() + ",");
		content.append(backTestResult.totalDays + ",");
		content.append(backTestResult.totalPnL + ",");
		content.append(backTestResult.averagePnL + ",");
		content.append(backTestResult.totalTrades + ",");
		content.append(backTestResult.averageTrades + ",");
		content.append(backTestResult.totalWinningDays + ",");
		content.append(backTestResult.totalLosingDays + ",");
		content.append(backTestResult.winningPercentage + ",");
		content.append(backTestResult.averageProfitOfPositiveTrade + ",");
		content.append(backTestResult.averageProfitOfNegativeTrade + ",");
		content.append(backTestResult.averageZeroPnLTrade + ",");
		content.append(backTestResult.averageNoPositiveTrade + ",");
		content.append(backTestResult.averageNoNegativeTrade + ",");
		content.append(backTestResult.averageHoldingTime + ",");
		content.append(backTestResult.adjustedPnLAfterFee + ",");
		content.append(backTestResult.worstLossDay + ",");
		content.append(backTestResult.bestProfitDay + ",");
		content.append(backTestResult.worstLossingStreak + ",");
		content.append(backTestResult.bestWinningStreak + ",");
		content.append(backTestResult.lossingStreakfreq + ",");
		content.append(backTestResult.winningStreakFreq + ",");
		content.append(backTestResult.sumOfLossingStreak + ",");
		content.append(backTestResult.sumOfWinningStreak + ",");
		content.append(backTestResult.averageOfLossingStreak + ",");
		content.append(backTestResult.averageOfWinningStreak + ",");
		content.append(backTestResult.maxLossingStreakLength + ",");
		content.append(backTestResult.maxWinningStreakLength);
		content.append("\n");
		return content.toString();
	}
	
	
//	public static String getProfitAndLossContent() throws IOException
//	{		
//		StringBuilder header = new StringBuilder("Test no.,");	  
//		StringBuilder content = new StringBuilder();
//		boolean headerReady = false;
//	    for (String dateStr : BackTestTask.allProfitAndLossResults.keySet())
//	    {
//	    	content.append(dateStr + "," + String.join("", BackTestTask.allProfitAndLossResults.get(dateStr).values()) + "\n");
//	    	if (!headerReady) {
//	    		for(int i = 1; i <= BackTestTask.allProfitAndLossResults.get(dateStr).size(); i++) {
//	    			header.append(i + ",");
//	    		}
//	    		header.append("\n");
//	    		headerReady = true;
//	    	}
//	    }	    
//		return header.toString() + content.toString();
//	}
	
	public static String getBTSummaryContent(MainUIParam mainUIParam, BackTestResult backTestResult) {
		StringBuilder content = new StringBuilder();
		content.append(backTestResult.testSet.gettShort()  +  "_")
		.append(backTestResult.testSet.gettLong() + "_")
		.append(backTestResult.testSet.gettLong2() + "_")
		.append(backTestResult.testSet.getHld() + "_")
		.append(backTestResult.testSet.getStopLoss() + "_")
		.append(backTestResult.testSet.getTradeStopLoss() + "_")
		.append(backTestResult.testSet.getInstantTradeStoploss() + "_")
		.append(backTestResult.testSet.getItsCounter() + "_")
		.append(backTestResult.testSet.getStopGainPercent() + "_")
		.append(backTestResult.testSet.getStopGainTrigger()  +  ",")
		.append(mainUIParam.getVersion() + ",")
		.append(mainUIParam.getSource() + ",")
		.append(backTestResult.testSet.gettShort() + ",")
		.append(backTestResult.testSet.gettLong() + ",")
		.append(backTestResult.testSet.gettLong2() + ",")
		.append(backTestResult.testSet.getHld() + ",")
		.append(backTestResult.testSet.getStopLoss() + ",")
		.append(backTestResult.testSet.getTradeStopLoss() + ",")
		.append(backTestResult.testSet.getInstantTradeStoploss() + ",")
		.append(backTestResult.testSet.getItsCounter() + ",")
		.append(backTestResult.testSet.getStopGainPercent() + ",")
		.append(backTestResult.testSet.getStopGainTrigger() + ",")
		.append(mainUIParam.getMarketStartTime() + ",")
		.append(mainUIParam.getLunchStartTimeFrom() + ",")
		.append(mainUIParam.getCashPerIndexPoint() + ",")
		.append(mainUIParam.getTradingFee() + ",")
		.append(mainUIParam.getOtherCostPerTrade() + ",")
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
		.append(backTestResult.maxWinningStreakLength + ",")
		.append(mainUIParam.getStartStr() + ",")
		.append(mainUIParam.getEndStr() + ",")
		.append("\n");
		return content.toString();
	}

	public static void initBTPnLAndTradeAndProfitAndLossContent(int id, MainUIParam mainUIParam, BackTestResult backTestResult, StringBuilder pnlContent, StringBuilder tradContent) {
		if (id == 1) {
			StringBuilder allProfitAndLossResultsHeader = new StringBuilder("Test no.,");
			for (PerDayRecord perDayRecord : backTestResult.dayRecords) {
				String dateStr = perDayRecord.getDateStr();
				allProfitAndLossResultsHeader.append(dateStr + ",");
			}
			allProfitAndLossResultsHeader.append("\n");
			BackTestTask.allProfitAndLossResults.append(allProfitAndLossResultsHeader);
		}
		BackTestTask.allProfitAndLossResults.append(id + ",");
		for (PerDayRecord perDayRecord : backTestResult.dayRecords) {
//			String dateStr = perDayRecord.getDateStr();
//			Map<Integer, String> profitAndLossMap = BackTestTask.allProfitAndLossResults.get(dateStr);
//			if(profitAndLossMap == null) {
//				profitAndLossMap = new TreeMap<Integer, String>();
//				BackTestTask.allProfitAndLossResults.put(dateStr, profitAndLossMap);
//			}
//			profitAndLossMap.put(id, perDayRecord.totalPnL + ",");
			BackTestTask.allProfitAndLossResults.append(perDayRecord.totalPnL + ",");
			
			pnlContent.append(backTestResult.testSet.gettShort()+"_")
			.append(backTestResult.testSet.gettLong()+"_")
			.append(backTestResult.testSet.gettLong2()+"_")
			.append(backTestResult.testSet.getHld()+"_")
			.append(backTestResult.testSet.getStopLoss()+"_")
			.append(backTestResult.testSet.getTradeStopLoss()+"_")
			.append(backTestResult.testSet.getInstantTradeStoploss()+"_")
			.append(backTestResult.testSet.getItsCounter()+"_")
			.append(backTestResult.testSet.getStopGainPercent()+"_")
			.append(backTestResult.testSet.getStopGainTrigger() + ",")
			.append(perDayRecord.getDateStr() + ",")
			.append(mainUIParam.getVersion() + ",")
			.append(mainUIParam.getSource() + ",")
			.append(perDayRecord.totalPnL + ",")
			.append("\n"); 
			
			tradContent.append(backTestResult.testSet.gettShort() + "_")
			.append(backTestResult.testSet.gettLong() + "_")
			.append(backTestResult.testSet.gettLong2() + "_")
			.append(backTestResult.testSet.getHld() + "_")
			.append(backTestResult.testSet.getStopLoss() + "_")
			.append(backTestResult.testSet.getTradeStopLoss() + "_")
			.append(backTestResult.testSet.getInstantTradeStoploss() + "_")
			.append(backTestResult.testSet.getItsCounter() + "_")
			.append(backTestResult.testSet.getStopGainPercent() + "_")
			.append(backTestResult.testSet.getStopGainTrigger() + ",")
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
		content.append("T-short," + mainUIParam.gettShort() + "\n");
		content.append("T-Long," + mainUIParam.gettLong() + "\n");
		content.append("T-Long2," + mainUIParam.gettLong2() + "\n");
		content.append("HLD," + mainUIParam.getHld() + "\n");
		content.append("Stop Loss," + mainUIParam.getStopLoss() + "\n");
		content.append("Trade stop loss," + mainUIParam.getTradeStopLoss() + "\n");
		content.append("Instant trade stoploss," + mainUIParam.getInstantTradeStoploss() + "\n");
		content.append("ITS counter," + mainUIParam.getItsCounter() + "\n");
		content.append("Stop gain trigger," + mainUIParam.getStopGainTrigger() + "\n");
		content.append("Stop gain percent," + mainUIParam.getStopGainPercent() + "\n");	 
		return content.toString();
	}

	public static void constructPerSecondRecord(StringBuilder content, PerSecondRecord perSecondRecord) {
		content.append(perSecondRecord.getTimeStr() + ",");
		 content.append(perSecondRecord.getBidPrice() + ",");
		 content.append(perSecondRecord.getAskPrice() + ",");
		 content.append(perSecondRecord.getLastTrade() + ",");
		 content.append(perSecondRecord.getCheckMarketTime() + ",");
		 content.append(perSecondRecord.getReference() + ",");
		 content.append(perSecondRecord.gettCounter() + ",");
		 content.append(perSecondRecord.getHighShort() + ",");
		 content.append(perSecondRecord.getHighLong() + ",");
		 content.append(perSecondRecord.getHighLong2() + ",");
		 content.append(perSecondRecord.getLowShort() + ",");
		 content.append(perSecondRecord.getLowLong() + ",");
		 content.append(perSecondRecord.getLowLong2() + ",");
		 content.append(perSecondRecord.getgMax() + ",");
		 content.append(perSecondRecord.getgMin() + ",");
		 content.append(perSecondRecord.getgHLD() + ",");
		 content.append(perSecondRecord.getAverageHLD() + ",");
		 content.append(perSecondRecord.getAction() + ",");
		 content.append(perSecondRecord.getSmoothAction() + ",");
		 content.append(perSecondRecord.getPosition() + ",");
		 content.append(perSecondRecord.getPc() + ",");
		 content.append(perSecondRecord.getMtm() + ",");
		 content.append(perSecondRecord.getMmtm() + ",");
		 content.append(perSecondRecord.getPnl() + ",");
		 content.append(perSecondRecord.getTradeCount() + ",");
		 content.append(perSecondRecord.getTotalPnl() + ",");
		 content.append(perSecondRecord.getHighDiffernece() + ",");
		 content.append(perSecondRecord.getLowDiffernece());
		 content.append("\n");
	}
}
