package com.yoson.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.directwebremoting.util.FakeHttpServletRequest;

import com.opencsv.CSVReader;
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
	
	public static final String TotalPnl = "Total Pnl of ";
	
	public static String getBTSummaryHeader(Set<Integer> years) {
		List<String> yearColums = new ArrayList<String>();
		for (Integer year : years) {
			yearColums.add(TotalPnl + year);
		}
		String yearColumnStr = String.join(",", yearColums);
		yearColumnStr = yearColumnStr.length() > 0 ? (yearColumnStr + ",") : yearColumnStr;
		return "Test no.,key,version,Source,T-Short,T-Long,T-Long2,HLD,Stoploss,Trade Stop Loss,Instant Trade Stop Loss,Its Counter,Stop Gain Percent,Stop Gain Trigger,Morning Start Time,Lunch Start Time,Cash per index point,Trading fee,Other cost per trade,No. of days,Total PnL,Average PnL ,Total trades,Average trades,No. of winning days,No. of losing days,Winning %,Average gain per +ve trade,Average gain per -ve trade,Average 0 PnL trades,Average no. of positive trade,Average no. of negative trade,Average holding time,Adjusted Profit after fee,Worst Lossing Day,Best Profit Day,Worst Lossing Streak,Best Winning Streak,Lossing Streak freq,Winning Streak freq,Sum Of Lossing Streak,Sum Of Winning Streak,Avg Of Lossing Streak,Avg Of Winning Streak,Max Lossing Streak Length,Max Winning Streak Length," + yearColumnStr +"Start Time,End Time,Including Morning Data,Ignore Lunch Time\n";
	}

	public static String getBTSummaryContent(int testNo, MainUIParam mainUIParam, BackTestResult backTestResult) {
		StringBuilder content = new StringBuilder();
		content.append(testNo + ",")
		.append(backTestResult.testSet.getKey() + ",")
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
		BackTestTask.allProfitAndLossResults.append(id + "," + backTestResult.testSet.getKey() + ",");
		for (PerDayRecord perDayRecord : backTestResult.dayRecords) {
			BackTestTask.allProfitAndLossResults.append(perDayRecord.totalPnL + ",");
			
			pnlContent.append(backTestResult.testSet.getKey() + ",")
			.append(perDayRecord.getDateStr() + ",")
			.append(mainUIParam.getVersion() + ",")
			.append(mainUIParam.getSource() + ",")
			.append(perDayRecord.totalPnL + ",")
			.append("\n"); 
			
			tradContent.append(backTestResult.testSet.getKey() + ",")
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
	
	public static String getBestPnlByDate(MainUIParam mainUIParam) {
		StringBuilder content = new StringBuilder();
		content.append("Date,Test no.,key,Total Pnl").append("\n");
		try {
			Map<String, List<Object>> allProfitAndLossResultMap = new TreeMap<String, List<Object>>();
			File file = new File(FilenameUtils.concat(mainUIParam.getSourcePath(), BackTestCSVWriter.profitAndLossFileName));
			CSVReader csvReader = new CSVReader(new FileReader(file), ',', '\n', 0);	
			String [] lines;
			boolean first = true;
			List<String> dates = new ArrayList<String>();
			while ((lines = csvReader.readNext()) != null)  {
				if (first) {
					for(int i = 2; i < lines.length - 1; i++) {
						dates.add(lines[i]);
					}	
					first = false;
				} else {
					String id = lines[0]; 
					String key = lines[1];
					for(int i = 2; i < lines.length - 1; i++) {
						String dateStr = dates.get(i - 2);
						double totalPnl = Double.parseDouble(lines[i]);
						if(!allProfitAndLossResultMap.containsKey(dateStr) || (Double)allProfitAndLossResultMap.get(dateStr).get(0) < totalPnl) {
							List<Object> pnlInfo = new ArrayList<Object>();
							pnlInfo.add(totalPnl);
							pnlInfo.add(id);
							pnlInfo.add(key);
							allProfitAndLossResultMap.put(dateStr, pnlInfo);
						}
					}
				}
			}
			csvReader.close();
						
			for(String dateStr : allProfitAndLossResultMap.keySet()) {
				List<Object> pnlInfo = allProfitAndLossResultMap.get(dateStr);				
				content.append(dateStr + "," + pnlInfo.get(1) + "," + pnlInfo.get(2) + "," + pnlInfo.get(0)).append("\n");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content.toString();
	}
	
	public static String getBestPnlBySpecifyDates(Set<Integer> specifyDateRanges, MainUIParam mainUIParam) {
		StringBuilder content = new StringBuilder();
		if(specifyDateRanges != null && specifyDateRanges.size() > 0) {
			content.append("Date,");
			for(int specifyDateRange : specifyDateRanges) {
				content.append("," + "n=" + specifyDateRange + " Test no.," + "n=" + specifyDateRange + " key," + "n=" + specifyDateRange + " Sum of Total Pnl," + "n=" + specifyDateRange + " Total Pnl,");
			}
			content.append("\n");
			
			List<String> dates = new ArrayList<String>();
			Map<String, List<List<Object>>> allProfitAndLossResultMap = new TreeMap<String, List<List<Object>>>();
			try {
				File file = new File(FilenameUtils.concat(mainUIParam.getSourcePath(), BackTestCSVWriter.profitAndLossFileName));
				CSVReader csvReader = new CSVReader(new FileReader(file), ',', '\n', 0);	
				String [] lines;
				boolean first = true;
				while ((lines = csvReader.readNext()) != null)  {
					if (first) {
						for(int i = 2; i < lines.length - 1; i++) {
							dates.add(lines[i]);
						}
						first = false;
					} else {
						String id = lines[0]; 
						String key = lines[1];
						for(int i = 2; i < lines.length - 1; i++) {
							String dateStr = dates.get(i - 2);
							double totalPnl = Double.parseDouble(lines[i]);
							List<Object> pnlInfo = new ArrayList<Object>();
							pnlInfo.add(totalPnl);
							pnlInfo.add(id);
							pnlInfo.add(key);
							if(allProfitAndLossResultMap.containsKey(dateStr)) {
								allProfitAndLossResultMap.get(dateStr).add(pnlInfo);
							} else {
								List<List<Object>> list = new ArrayList<List<Object>>();
								list.add(pnlInfo);
								allProfitAndLossResultMap.put(dateStr, list);
							}
						}
					}
				}
				csvReader.close();							
												
				int i = 1;
				for(String dateStr : allProfitAndLossResultMap.keySet()) {					
					content.append(dateStr + ",");
					for(int specifyDateRange : specifyDateRanges) {
						if(specifyDateRange >= i) {
							content.append(",0,0,0,0,");							
						} else {
							Double maxSumOfPnl = null;
							int maxId = 0;
							for(int id = 0; id < allProfitAndLossResultMap.get(dateStr).size(); id++) {
								double sumOfPnl = 0;
								int j = i - specifyDateRange - 1;							
								for(; j < i - 1; j++) {
									String _dateStr = dates.get(j);
									double totalPnl = (double)allProfitAndLossResultMap.get(_dateStr).get(id).get(0);
									sumOfPnl += totalPnl;
								}
								if (maxSumOfPnl == null || maxSumOfPnl < sumOfPnl) {
									maxSumOfPnl = sumOfPnl;
									maxId = id;
								}
							}
							
							content.append("," + allProfitAndLossResultMap.get(dateStr).get(maxId).get(1) + "," + allProfitAndLossResultMap.get(dateStr).get(maxId).get(2) + "," + maxSumOfPnl + "," + allProfitAndLossResultMap.get(dateStr).get(maxId).get(0) + ",");
						}						
					}
					content.append("\n");
					i++;
				}
			} catch (Exception e) {
				e.printStackTrace();
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
