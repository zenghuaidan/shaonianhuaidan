package com.yoson.model;

import java.util.Date;
import java.util.List;

import com.yoson.date.DateUtils;

public class PerDayRecord {
	public Date date;
	public double positiveTrades;
	public double negativeTrades;
	public double totalTrades;
	public double zeroPnlTrades;
	public double averagePositiveTrades;
	public double averageNegativeTrades;
	public double averageHoldingTime;
	public double dailyTradingRange;
	public double dailyIndexVol;
	public double performanceInVol;
	public double indexPerformance;
	public double performanceInPts;
	public double averageHLDiff;
	public double totalPnL;
	public double morningPnL;
	public double afternoonPnL;
	public double nightPnL;
	
	public List<PerSecondRecord> dailyPerSecondRecordList;
	
	public PerDayRecord(Date date)
	{
		this.date = date;
	}
	
	public Date getDate(){
		return date;
	}
	
	public String getDateStr(){
		return DateUtils.yyyyMMdd().format(date);
	}
}

