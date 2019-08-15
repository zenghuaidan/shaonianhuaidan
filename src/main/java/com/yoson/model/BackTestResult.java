package com.yoson.model;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.yoson.date.DateUtils;

public class BackTestResult
{
	public int totalDays;
	public double totalPnL;
	public double averagePnL;
	public double totalTrades;
	public double averageTrades;
	public double totalWinningDays;
	public double totalLosingDays;
	public double winningPercentage;
	public double averageNoPositiveTrade;
	public double averageNoNegativeTrade;
	public double averageZeroPnLTrade;
	public double averageProfitOfPositiveTrade;
	public double averageProfitOfNegativeTrade;
	public double averageHoldingTime;	
	public double adjustedPnLAfterFee;	
	public double worstLossDay;
	public double bestProfitDay;
	public double worstLossingStreak;
	public double bestWinningStreak;
	public int lossingStreakfreq;
	public int winningStreakFreq;
	public double sumOfLossingStreak;
	public double sumOfWinningStreak;
	public double averageOfLossingStreak;
	public double averageOfWinningStreak;
	public int maxLossingStreakLength;
	public int maxWinningStreakLength;
	public Map<Integer, Double> yearPnlMap;
	public Map<String, Double> monthPnlMap;
	
	public double sumMorningPnL;
	public double sumLunchPnL;
	public double sumNightPnL;
	public double averageMorningPnL;
	public double averageLunchPnL;
	public double averageNightPnL;

	
	public BackTestResult(TestSet testSet, List<PerDayRecord> dayRecords)
	{		
		this.totalDays = dayRecords.size();	
				
		double totalAveragePositiveTrades = 0;
		double totalAverageNegativeTrades = 0;
		double totalPositiveTrades = 0;
		double totalNegativeTrades = 0;
		double totalZeroTrades = 0;
		double totalHoldingTime = 0;
		yearPnlMap = new TreeMap<Integer, Double>();
		monthPnlMap = new TreeMap<String, Double>();
		
		Double previousPnl = null;
		int winOrLossPeriodCount = 0;
		double winOrLossPeriodSum = 0;
		for(int i = 0; i < dayRecords.size(); i++) {
			PerDayRecord perDayRecord = dayRecords.get(i);
			totalPnL = totalPnL + perDayRecord.totalPnL;
			sumMorningPnL += perDayRecord.morningPnL;
			sumLunchPnL += perDayRecord.afternoonPnL;
			sumNightPnL += perDayRecord.nightPnL;
			totalTrades = totalTrades + perDayRecord.totalTrades;
			if (perDayRecord.totalPnL > 0) totalWinningDays++;
			if (perDayRecord.totalPnL < 0) totalLosingDays++;
			totalAveragePositiveTrades = totalAveragePositiveTrades + perDayRecord.averagePositiveTrades;
			totalAverageNegativeTrades = totalAverageNegativeTrades + perDayRecord.averageNegativeTrades;
			totalPositiveTrades = totalPositiveTrades + perDayRecord.positiveTrades;
			totalNegativeTrades = totalNegativeTrades + perDayRecord.negativeTrades;
			totalZeroTrades = totalZeroTrades + perDayRecord.zeroPnlTrades;
			totalHoldingTime = totalHoldingTime + perDayRecord.averageHoldingTime;
						
			bestProfitDay = Math.max(bestProfitDay, perDayRecord.totalPnL);
			worstLossDay = Math.min(worstLossDay, perDayRecord.totalPnL);
			
			
			int year = perDayRecord.getDate().getYear() + 1900;			
			yearPnlMap.put(year, (yearPnlMap.containsKey(year) ? yearPnlMap.get(year) : 0) + perDayRecord.totalPnL);
			
			
			String month = DateUtils.yyyyMM().format(perDayRecord.getDate());			
			monthPnlMap.put(month, (monthPnlMap.containsKey(month) ? monthPnlMap.get(month) : 0) + perDayRecord.totalPnL);	
			
			
			if(i > 0 && dayRecords.get(i - 1).totalPnL != 0) previousPnl = dayRecords.get(i - 1).totalPnL; //previous total pnl, skip the totalPnl = 0
			
			if((previousPnl == null || previousPnl > 0) && perDayRecord.totalPnL < 0  // begin of loss
				|| (previousPnl == null || previousPnl < 0) && perDayRecord.totalPnL > 0)  // begin of win 
			{
				lossingStreakfreq += perDayRecord.totalPnL < 0 ? 1 : 0;
				winningStreakFreq += perDayRecord.totalPnL > 0 ? 1 : 0;
				
				if(previousPnl != null) {
					if(previousPnl > 0) { // end of win
						bestWinningStreak = Math.max(bestWinningStreak, winOrLossPeriodSum);
						maxWinningStreakLength = Math.max(maxWinningStreakLength, winOrLossPeriodCount);
					} else { // end of loss
						worstLossingStreak = Math.min(worstLossingStreak, winOrLossPeriodSum);
						maxLossingStreakLength = Math.max(maxLossingStreakLength, winOrLossPeriodCount);
					}
					
				}
				
				// reset the indicator at last
				winOrLossPeriodCount = 0;
				winOrLossPeriodSum= 0;								
			}
			
			
			if(perDayRecord.totalPnL != 0) { // win/loss period indicator
				winOrLossPeriodCount++;
				winOrLossPeriodSum += perDayRecord.totalPnL;
			}
			
			sumOfLossingStreak += perDayRecord.totalPnL < 0 ? perDayRecord.totalPnL : 0;
			sumOfWinningStreak += perDayRecord.totalPnL > 0 ? perDayRecord.totalPnL : 0;
						
		}
		
		if(winOrLossPeriodCount > 0) {
			if(winOrLossPeriodSum > 0) {
				bestWinningStreak = Math.max(bestWinningStreak, winOrLossPeriodSum);
				maxWinningStreakLength = Math.max(maxWinningStreakLength, winOrLossPeriodCount);
			} else {
				worstLossingStreak = Math.min(worstLossingStreak, winOrLossPeriodSum);
				maxLossingStreakLength = Math.max(maxLossingStreakLength, winOrLossPeriodCount);
			}
		}
		
		averageMorningPnL = sumMorningPnL / totalDays;
		averageLunchPnL = sumLunchPnL / totalDays;
		averageNightPnL = sumNightPnL / totalDays;
		averagePnL = totalPnL / totalDays;		
		averageTrades = totalTrades / totalDays;
		winningPercentage = totalWinningDays / totalDays * 100;
				
		averageProfitOfPositiveTrade = totalAveragePositiveTrades / totalDays;			
		averageProfitOfNegativeTrade = totalAverageNegativeTrades / totalDays;
		
		
		averageNoPositiveTrade = totalPositiveTrades / totalDays;
		averageNoNegativeTrade = totalNegativeTrades / totalDays;
		
		averageZeroPnLTrade = totalZeroTrades / totalDays;
		
		averageHoldingTime = totalHoldingTime / totalDays;

		adjustedPnLAfterFee = (testSet.getCashPerIndexPoint()*totalPnL) - ((testSet.getTradingFee() + testSet.getOtherCostPerTrade())*totalTrades);
		
		if (lossingStreakfreq > 0) averageOfLossingStreak = sumOfLossingStreak / lossingStreakfreq;		
		if (winningStreakFreq > 0) averageOfWinningStreak = sumOfWinningStreak / winningStreakFreq;		
	
	}
				
}
