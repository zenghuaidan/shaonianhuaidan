package com.yoson.task;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.yoson.csv.BackTestCSVWriter;
import com.yoson.model.MainUIParam;
import com.yoson.model.PerDayRecord;
import com.yoson.model.PerSecondRecord;
import com.yoson.model.ScheduleData;
import com.yoson.model.TestSet;

public class BackTestSet {
	
	public static PerDayRecord initPerDayRecord(List<ScheduleData> dailyScheduleData, MainUIParam mainUIParam, TestSet testSet) throws ParseException
	{		
		long start = System.currentTimeMillis();
		List<PerSecondRecord> dailyPerSecondRecordList = new ArrayList<PerSecondRecord>();
		PerDayRecord perDayRecord = new PerDayRecord(new Date(dailyScheduleData.get(0).getId()));
		
		perDayRecord.indexPerformance = Math.abs(dailyScheduleData.get(0).getLastTrade() - dailyScheduleData.get(dailyScheduleData.size() - 1).getLastTrade());
		double sumOfTrade = 0;
		double positiveSum = 0;
		double negativeSum = 0;
		double sumOfTime = 0;
		double numberOfTrades = 0;
		double maxLastTrade = Double.MIN_VALUE;
		double minLastTrade = Double.MAX_VALUE;
		double totalPnl = 0;
		double mean = BackTestTask.sumOfLastTrade.get(dailyScheduleData.get(0).getDateStr()) / dailyScheduleData.size();
		double dailyIndexVolTemp = 0;
		StringBuilder sb = new StringBuilder();
		for (ScheduleData scheduleDataPerSecond : dailyScheduleData) {
			//long start1 = System.currentTimeMillis();
			PerSecondRecord perSecondRecord = new PerSecondRecord(dailyScheduleData, testSet, dailyPerSecondRecordList, scheduleDataPerSecond, BackTestTask.marketTimeMap.get(scheduleDataPerSecond.getTimeStr()));
			//System.out.println("initPerSecondRecord:" + (System.currentTimeMillis() - start1));
			dailyPerSecondRecordList.add(perSecondRecord);
			
			perDayRecord.positiveTrades += perSecondRecord.getPnl() > 0 ? 1 : 0;
			perDayRecord.negativeTrades += perSecondRecord.getPnl() < 0 ? 1 : 0;
			
			positiveSum += perSecondRecord.getPnl() > 0 ? perSecondRecord.getPnl() : 0;
			negativeSum += perSecondRecord.getPnl() < 0 ? perSecondRecord.getPnl() : 0;

			sumOfTrade = sumOfTrade + perSecondRecord.getTradeCount();
			
			sumOfTime += perSecondRecord.getSmoothAction() != 0 ? 1 : 0;
			numberOfTrades += perSecondRecord.getTradeCount() != 0 ? 1 : 0;
			
			maxLastTrade = maxLastTrade > perSecondRecord.getLastTrade() ? maxLastTrade : perSecondRecord.getLastTrade();
			minLastTrade = minLastTrade < perSecondRecord.getLastTrade() ? minLastTrade : perSecondRecord.getLastTrade();
			
			totalPnl = totalPnl + perSecondRecord.getPnl();
	
			dailyIndexVolTemp += ((perSecondRecord.getLastTrade() - mean) * (perSecondRecord.getLastTrade() - mean));
			
			if (mainUIParam.getPnlThreshold() > 0) {
				if(perSecondRecord.getPc() == 1) {
					sb = new StringBuilder();
					BackTestCSVWriter.constructPerSecondRecord(sb, perSecondRecord);
				} else if(sb.length() > 0) {
					BackTestCSVWriter.constructPerSecondRecord(sb, perSecondRecord);
				}
				
				if (perSecondRecord.getPnl() >= mainUIParam.getPnlThreshold()) {
					String key = StringUtils.join(scheduleDataPerSecond.getDateStr() , "-" , scheduleDataPerSecond.getTimeStr().replaceAll(":", "-") , "#"
							, testSet.gettShort() , "_" , testSet.gettLong() , "_" , testSet.gettLong2() , "_" , testSet.getHld() , "_"
							, testSet.getStopLoss() , "_" , testSet.getTradeStopLoss() , "_" , testSet.getInstantTradeStoploss() 
							, "_" , testSet.getItsCounter() , "_" , testSet.getStopGainPercent() , "_" , testSet.getStopGainTrigger());
					BackTestTask.allPositivePnlResult.put(key, sb.toString());
				}				
			}
		}				
		
		perDayRecord.zeroPnlTrades = sumOfTrade - ((perDayRecord.positiveTrades + perDayRecord.negativeTrades ) *2);
		perDayRecord.averagePositiveTrades = perDayRecord.positiveTrades == 0 ? positiveSum : positiveSum / perDayRecord.positiveTrades;
		perDayRecord.averageNegativeTrades = perDayRecord.negativeTrades == 0 ? negativeSum : negativeSum / perDayRecord.negativeTrades;
		perDayRecord.averageHoldingTime = sumOfTime/((numberOfTrades == 0 ? 1 : numberOfTrades) /2);
		perDayRecord.dailyTradingRange = maxLastTrade - minLastTrade;
		perDayRecord.performanceInPts = totalPnl - perDayRecord.indexPerformance;
		perDayRecord.dailyIndexVol = Math.sqrt((dailyIndexVolTemp/(dailyPerSecondRecordList.size() - 1)));
		perDayRecord.performanceInVol = totalPnl / perDayRecord.dailyIndexVol;
		perDayRecord.totalPnL = totalPnl;
		perDayRecord.dailyPerSecondRecordList = dailyPerSecondRecordList;
//		System.out.println("initPerDayRecord:" + (System.currentTimeMillis() - start));
		return perDayRecord;		
	}

	public static List<PerDayRecord> initAndRun(int index, MainUIParam mainUIParam, TestSet testSet) throws ParseException {
		List<PerDayRecord> dayRecords = new ArrayList<PerDayRecord>();
		for (String key : BackTestTask.sortedDateList) {				
			dayRecords.add(initPerDayRecord(BackTestTask.rowData.get(key), mainUIParam, testSet));																		
		}
		return dayRecords;	
//		System.gc();
	}	
}

