package com.yoson.task;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yoson.date.DateUtils;
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
		Map<Double, Integer> lastTradeMap = new HashMap<Double, Integer>();
		PerDayRecord perDayRecord = new PerDayRecord(new Date(dailyScheduleData.get(dailyScheduleData.size() - 1).getId()));
		
		perDayRecord.indexPerformance = Math.abs(dailyScheduleData.get(0).getLastTrade() - dailyScheduleData.get(dailyScheduleData.size() - 1).getLastTrade());
		double sumOfTrade = 0;
		double positiveSum = 0;
		double negativeSum = 0;
		double sumOfTime = 0;
		double numberOfTrades = 0;
		double maxLastTrade = Double.MIN_VALUE;
		double minLastTrade = Double.MAX_VALUE;
		double totalPnl = 0;
		double mean = BackTestTask.sumOfLastTrade.get(dailyScheduleData.get(dailyScheduleData.size() - 1).getDateStr()) / dailyScheduleData.size();
		double dailyIndexVolTemp = 0;
		StringBuilder sb = new StringBuilder();
		long marketStartTime = DateUtils.HHmmss().parse(mainUIParam.getMarketStartTime()).getTime();
		long lunchStartTimeFrom = DateUtils.HHmmss().parse(mainUIParam.getLunchStartTimeFrom()).getTime();
		long lunchStartTimeTo = DateUtils.HHmmss().parse(mainUIParam.getLunchStartTimeTo()).getTime();
		long marketCloseTime = DateUtils.HHmmss().parse(mainUIParam.getMarketCloseTime()).getTime();

		for (ScheduleData scheduleDataPerSecond : dailyScheduleData) {
			long time = DateUtils.HHmmss().parse(scheduleDataPerSecond.getTimeStr()).getTime();
			boolean isMorning = time >= marketStartTime && time <= lunchStartTimeFrom;
			boolean isAfternoon = time >= lunchStartTimeTo && time <= marketCloseTime;
			boolean isValidateTime = time >= marketStartTime && time <= marketCloseTime;
			PerSecondRecord perSecondRecord = null;
			if (isMorning || isAfternoon || mainUIParam.isIgnoreLunchTime() && isValidateTime) {
				perSecondRecord = new PerSecondRecord(dailyScheduleData, testSet, dailyPerSecondRecordList, scheduleDataPerSecond, BackTestTask.marketTimeMap.get(scheduleDataPerSecond.getTimeStr()), lastTradeMap);
			} else {
				continue;
			}

			dailyPerSecondRecordList.add(perSecondRecord);
			
			perDayRecord.positiveTrades += perSecondRecord.getPnl() > 0 ? 1 : 0;
			perDayRecord.negativeTrades += perSecondRecord.getPnl() < 0 ? 1 : 0;
			perDayRecord.totalTrades += perSecondRecord.getTradeCount();
			if(isMorning)
				perDayRecord.morningPnL = perSecondRecord.getTotalPnl();
			if(isAfternoon)
				perDayRecord.afternoonPnL += perSecondRecord.getPnl();
			
			positiveSum += perSecondRecord.getPnl() > 0 ? perSecondRecord.getPnl() : 0;
			negativeSum += perSecondRecord.getPnl() < 0 ? perSecondRecord.getPnl() : 0;

			sumOfTrade = sumOfTrade + perSecondRecord.getTradeCount();
			
			sumOfTime += perSecondRecord.getSmoothAction() != 0 ? 1 : 0;
			numberOfTrades += perSecondRecord.getTradeCount() != 0 ? 1 : 0;
			
			maxLastTrade = maxLastTrade > perSecondRecord.getLastTrade() ? maxLastTrade : perSecondRecord.getLastTrade();
			minLastTrade = minLastTrade < perSecondRecord.getLastTrade() ? minLastTrade : perSecondRecord.getLastTrade();
			
			totalPnl = totalPnl + perSecondRecord.getPnl();
	
			dailyIndexVolTemp += ((perSecondRecord.getLastTrade() - mean) * (perSecondRecord.getLastTrade() - mean));
						
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
//		System.out.println("initPerDayRecord(" + dailyScheduleData.get(0).getDateStr() + "):" + (System.currentTimeMillis() - start));
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

