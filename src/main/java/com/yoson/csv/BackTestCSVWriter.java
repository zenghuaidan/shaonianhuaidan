package com.yoson.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.opencsv.CSVReader;
import com.yoson.chart.chartWriter;
import com.yoson.model.BackTestResult;
import com.yoson.model.MainUIParam;
import com.yoson.model.PerDayRecord;
import com.yoson.model.PerSecondRecord;
import com.yoson.task.BackTestTask;

public class BackTestCSVWriter {	
	public static final String profitAndLossFileName = "Back Test ProfitAndLoss.csv";
	public static final String morningProfitAndLossFileName = "Back Test First Half ProfitAndLoss.csv";
	public static final String afternoonProfitAndLossFileName = "Back Test Second Half ProfitAndLoss.csv";
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
		return "Time,Bid Price,Ask Price,Last Trade,Check Market Time,Reference,T Counter,HShort,HLong,LShort,LLong,Gmax,Gmin,GHLD,AVGHLD,Action,Smooth Action,Position,PC,MTM,PnL,No_Trade,Total_PnL,HLDiff\n";
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
		return "Test no.,key,version,Source,T-Short,T-Long,HLD,Stoploss,Trade Stop Loss,Instant Trade Stop Loss,Its Counter,Morning Start Time,Lunch Start Time,Cash per index point,Trading fee,Other cost per trade,No. of days,Total PnL,Average PnL ,Total trades,Average trades,No. of winning days,No. of losing days,Winning %,Average gain per +ve trade,Average gain per -ve trade,Average 0 PnL trades,Average no. of positive trade,Average no. of negative trade,Average holding time,Adjusted Profit after fee,Worst Lossing Day,Best Profit Day,Worst Lossing Streak,Best Winning Streak,Lossing Streak freq,Winning Streak freq,Sum Of Lossing Streak,Sum Of Winning Streak,Avg Of Lossing Streak,Avg Of Winning Streak,Max Lossing Streak Length,Max Winning Streak Length," + yearColumnStr +"Start Time,End Time,Including Morning Data,Ignore Lunch Time\n";
	}
	
	public static String getBTSummaryContent(int testNo, MainUIParam mainUIParam, BackTestResult backTestResult) {
		StringBuilder content = new StringBuilder();
		content.append(testNo + ",")
		.append(backTestResult.testSet.getKey() + ",")
		.append(mainUIParam.getVersion() + ",")
		.append(mainUIParam.getSource() + ",")
		.append(backTestResult.testSet.gettShort() + ",")
		.append(backTestResult.testSet.gettLong() + ",")	
		.append(backTestResult.testSet.getHld() + ",")
		.append(backTestResult.testSet.getStopLoss() + ",")
		.append(backTestResult.testSet.getTradeStopLoss() + ",")
		.append(backTestResult.testSet.getInstantTradeStoploss() + ",")
		.append(backTestResult.testSet.getItsCounter() + ",")		
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
			BackTestTask.allMorningProfitAndLossResults.append(allProfitAndLossResultsHeader);
			BackTestTask.allAfternoonProfitAndLossResults.append(allProfitAndLossResultsHeader);
		}
		BackTestTask.allProfitAndLossResults.append(id + "," + backTestResult.testSet.getKey() + ",");
		BackTestTask.allMorningProfitAndLossResults.append(id + "," + backTestResult.testSet.getKey()  +  ",");
		BackTestTask.allAfternoonProfitAndLossResults.append(id + "," + backTestResult.testSet.getKey()  +  ",");
		for (PerDayRecord perDayRecord : backTestResult.dayRecords) {
			BackTestTask.allProfitAndLossResults.append(perDayRecord.totalPnL + ",");
			BackTestTask.allMorningProfitAndLossResults.append(perDayRecord.morningPnL + ",");
			BackTestTask.allAfternoonProfitAndLossResults.append(perDayRecord.afternoonPnL + ",");
			
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
		BackTestTask.allMorningProfitAndLossResults.append("\n");
		BackTestTask.allAfternoonProfitAndLossResults.append("\n");
	}
	
	public static Map<String, List<Object>> bestMoningProfitAndLossResultMap = new TreeMap<String, List<Object>>();
	public static Map<String, List<List<Object>>> allProfitAndLossResultMap = new TreeMap<String, List<List<Object>>>();
	
	public static void initProfitAndLossResultMap(MainUIParam mainUIParam) {
		try {
			bestMoningProfitAndLossResultMap = new TreeMap<String, List<Object>>();
			allProfitAndLossResultMap = new TreeMap<String, List<List<Object>>>();
			List<String> dates = new ArrayList<String>();
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
			
			dates = new ArrayList<String>();
			file = new File(FilenameUtils.concat(mainUIParam.getSourcePath(), BackTestCSVWriter.morningProfitAndLossFileName));
			csvReader = new CSVReader(new FileReader(file), ',', '\n', 0);	
			first = true;			
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
						if(!bestMoningProfitAndLossResultMap.containsKey(dateStr) || (Double)bestMoningProfitAndLossResultMap.get(dateStr).get(0) < totalPnl) {
							bestMoningProfitAndLossResultMap.put(dateStr, pnlInfo);
						}
												
					}
				}
			}
			csvReader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getBestPnlByDate(MainUIParam mainUIParam) {
		StringBuilder content = new StringBuilder();
		content.append("Date,Test no.,key,Total Pnl").append("\n");
		
		for(String dateStr : bestMoningProfitAndLossResultMap.keySet()) {
			List<Object> pnlInfo = bestMoningProfitAndLossResultMap.get(dateStr);				
			content.append(dateStr + "," + pnlInfo.get(1) + "," + pnlInfo.get(2) + "," + pnlInfo.get(0)).append("\n");
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
						
			int i = 1;
			List<String> dates = new ArrayList<String>();
			for(String dateStr : allProfitAndLossResultMap.keySet()) {
				dates.add(dateStr);
			}
			for(String dateStr : dates) {					
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
		}
		return content.toString();
	}
	
	public static void getAccumulatePnlBySpecifyDates(Set<Integer> specifyDateRanges, MainUIParam mainUIParam) {
		if(specifyDateRanges != null && specifyDateRanges.size() > 0) {	
			List<String> dates = new ArrayList<String>();
			for(String dateStr : allProfitAndLossResultMap.keySet()) {
				dates.add(dateStr);
			}
			for(int specifyDateRange : specifyDateRanges) {
				StringBuilder content = new StringBuilder("Test no.,key,");
				for(String dateStr : dates) {	
					content.append(dateStr + ",");
				}
				content.append("\n");
				
				for(int id = 0; id < allProfitAndLossResultMap.get(dates.get(0)).size(); id++) {					
					content.append((id + 1) + "," + allProfitAndLossResultMap.get(dates.get(0)).get(id).get(2) + ",");
					for(int i = 1; i <= dates.size(); i++) {
						if(specifyDateRange >= i) {
							content.append("0,");
						} else {
							int j = i - specifyDateRange - 1;				
							double sumOfPnl = 0;
							for(; j < i - 1; j++) {
								double totalPnl = (double)allProfitAndLossResultMap.get(dates.get(j)).get(id).get(0);
								sumOfPnl += totalPnl;
							}		
							content.append(sumOfPnl + ",");
						}
					}					
					content.append("\n");
				}				
				writeText(FilenameUtils.concat(mainUIParam.getSourcePath(), "Back Test Accumulate ProfitAndLoss By Date Range_" + specifyDateRange + ".csv"), content.toString(), true);			
			}			
		}
		
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
		content.append("HLD," + mainUIParam.getHld() + "\n");
		content.append("Stop Loss," + mainUIParam.getStopLoss() + "\n");
		content.append("Trade stop loss," + mainUIParam.getTradeStopLoss() + "\n");
		content.append("Instant trade stoploss," + mainUIParam.getInstantTradeStoploss() + "\n");
		content.append("ITS counter," + mainUIParam.getItsCounter() + "\n");		
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
		 content.append(perSecondRecord.getLowShort() + ",");
		 content.append(perSecondRecord.getLowLong() + ",");
		 content.append(perSecondRecord.getgMax() + ",");
		 content.append(perSecondRecord.getgMin() + ",");
		 content.append(perSecondRecord.getgHLD() + ",");
		 content.append(perSecondRecord.getAverageHLD() + ",");
		 content.append(perSecondRecord.getAction() + ",");
		 content.append(perSecondRecord.getSmoothAction() + ",");
		 content.append(perSecondRecord.getPosition() + ",");
		 content.append(perSecondRecord.getPc() + ",");
		 content.append(perSecondRecord.getMtm() + ",");
		 content.append(perSecondRecord.getPnl() + ",");
		 content.append(perSecondRecord.getTradeCount() + ",");
		 content.append(perSecondRecord.getTotalPnl() + ",");
		 content.append(perSecondRecord.getHighLowDiffernece());
		 content.append("\n");
	}
}
