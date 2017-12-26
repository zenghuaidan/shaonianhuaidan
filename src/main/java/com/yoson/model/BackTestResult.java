package com.yoson.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
	
	public List<PerDayRecord> dayRecords;

	public TestSet testSet;
	
	public BackTestResult(TestSet testSet, List<PerDayRecord> dayRecords)
	{
		this.testSet = testSet;
		this.dayRecords = dayRecords;
		this.totalDays = dayRecords.size();
		summarizeTestResult();
	}
	
	
	public void summarizeTestResult()
	{		
		calTotalPnL();
		calAveragePnL();
		calTotalTrade();
		calAverageTrade();
		calTotalWinningDay();
		calTotalLosingDay();
		calWinningPercentage();
		calGainPerPositiveTrade();
		calGainPerNegativeTrade();
		calNoPositiveTrade();
		calNoNegativeTrade();
		calNoZeroPnLTrade();
		calAverageholdingTime();
		calAdjustedPnLAfterFee();
		calPnL();
		calPnLByYear();
	}
	
	public void calPnL()
	{
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		Boolean PreviousPositive = false;
		Boolean isWinningStreak = false;
		Boolean isLossingStreak = false;
		Boolean StreakCounted = false; // used to indicate whether the streak is counted or not
		
		ArrayList<Double> WinningStreakList = new ArrayList<Double>();
		ArrayList<Double> LossingStreakList = new ArrayList<Double>();
		ArrayList<Integer> WinningStreakLengthList = new ArrayList<Integer>();
		ArrayList<Integer> LossingStreakLengthList = new ArrayList<Integer>();
		worstLossDay = 0;
		bestProfitDay = 0;
		worstLossingStreak = 0;
		bestWinningStreak = 0;
		lossingStreakfreq = 0;
		winningStreakFreq = 0;
		sumOfLossingStreak = 0;
		sumOfWinningStreak = 0;
		averageOfLossingStreak = 0;
		averageOfWinningStreak = 0;
		int count = 0;
		Double PreviousPnL = 0.0;
		Double temp_sum = 0.0;
		int streak_day_count = 0;
		
		for (PerDayRecord dayRecord : dayRecords)
		{
//			System.out.println(df.format(Day.date));
//			System.out.println(Day.PnL);
			if (dayRecord.totalPnL > bestProfitDay) bestProfitDay = dayRecord.totalPnL;
			if (dayRecord.totalPnL < worstLossDay) worstLossDay = dayRecord.totalPnL;
			
			if (count == 0){ // The first Day, so skipped
				count++;
				PreviousPnL = dayRecord.totalPnL;
				if (dayRecord.totalPnL > 0) PreviousPositive = true;
				else PreviousPositive = false;
				continue;
			}
			else 
			{
				count++;

				if (dayRecord.totalPnL == 0.0){
					if (count == dayRecords.size())
						if (isWinningStreak == true){
							WinningStreakList.add(temp_sum);
							WinningStreakLengthList.add(streak_day_count);
						}
						else if (isLossingStreak == true){
							LossingStreakList.add(temp_sum);
							LossingStreakLengthList.add(streak_day_count);
						}
					continue; // zero
				}
				
				if (PreviousPositive == true && isWinningStreak == true) // The Previous Day has positive PnL and it is a Winning Streak
				{
					if (dayRecord.totalPnL > 0)
					{ 
						if (count != dayRecords.size()){
							// PnL is still positive , the Streak will continue
							streak_day_count++;
							temp_sum = temp_sum + dayRecord.totalPnL;
							PreviousPnL = dayRecord.totalPnL;
							continue;
						}
						else 
						{
							// last element, The Streak stops here
							streak_day_count++;
							WinningStreakLengthList.add(streak_day_count);
							streak_day_count = 0;
							temp_sum = temp_sum + dayRecord.totalPnL;
							WinningStreakList.add(temp_sum);
							break;
						}
						
					}
					else // PnL is negative , the Streak stops
					{
						WinningStreakList.add(temp_sum);
						WinningStreakLengthList.add(streak_day_count);
						streak_day_count = 0;
						temp_sum = 0.0;
						PreviousPnL = dayRecord.totalPnL;
						isWinningStreak = false;
						PreviousPositive = false;
						continue; // not a streak, skip
					}
				}
				
				else if (PreviousPositive == true && isWinningStreak == false) // The Previous Day has positive PnL and it is not a Winning Streak
				{
					if (dayRecord.totalPnL > 0)
					{ 
						// PnL is positive and previous PnL is also positive, it is a new Winning Streak
						isWinningStreak = true;
						winningStreakFreq++;
						streak_day_count = 2;
						temp_sum = PreviousPnL + dayRecord.totalPnL;
						PreviousPnL = dayRecord.totalPnL;
					}
					else 
					{
						// PnL is negative but the previous PnL is positive, it is neither Winning Streak nor Lossing Streak
						PreviousPnL = dayRecord.totalPnL;
						PreviousPositive = false;
						continue; // not a streak, skip
					}
				}
				
				else if (PreviousPositive == false && isLossingStreak == true) // The Previous Day has negative PnL and it is a Lossing Streak
				{
					if (dayRecord.totalPnL > 0)
					{ 
						// PnL is positive but the previous PnL is negative, The Lossing Streak stops
						LossingStreakList.add(temp_sum);
						LossingStreakLengthList.add(streak_day_count);
						streak_day_count = 0;
						temp_sum = 0.0;
						isLossingStreak = false;
						PreviousPositive = true;
						PreviousPnL = dayRecord.totalPnL;
						continue; // not a streak, skip						
					}
					else  
					{
						if (count != dayRecords.size()){
							// PnL is negative and the previous PnL is also negative, the Streak will continue
							streak_day_count++;
							temp_sum = temp_sum + dayRecord.totalPnL;
							PreviousPnL = dayRecord.totalPnL;
							continue;
						}
						else {
							// last element, Lossing Streak stops here
							streak_day_count++;
							LossingStreakLengthList.add(streak_day_count);
							streak_day_count = 0;
							temp_sum = temp_sum + dayRecord.totalPnL;
							LossingStreakList.add(temp_sum);
							break;
						}
						
					}
				}
				else if (PreviousPositive == false && isLossingStreak == false) // The Previous Day has negative PnL and it is not a Lossing Streak
				{
	
					if (dayRecord.totalPnL > 0)
					{ 
						// PnL is positive but the previous PnL is negative, it is neither Winning Streak nor Lossing Streak
						PreviousPositive = true;
						PreviousPnL = dayRecord.totalPnL;
						continue; // not a streak, skip
						
					}
					else 
					{
						// PnL is negative and the previous PnL is also negative, it is a new Lossing Streak
						isLossingStreak = true;
						lossingStreakfreq++;
						streak_day_count = 2;
						temp_sum = PreviousPnL + dayRecord.totalPnL;
						PreviousPnL = dayRecord.totalPnL;
						continue;
					}
				}
			}
			
		}
		
		try
		{
			if (lossingStreakfreq > 0) {
				worstLossingStreak = LossingStreakList.size() > 0 ? Collections.min(LossingStreakList) : 0;
				sumOfLossingStreak = LossingStreakList.stream().mapToDouble(f -> f.doubleValue()).sum();
				averageOfLossingStreak = sumOfLossingStreak/lossingStreakfreq;
				maxLossingStreakLength = LossingStreakLengthList.size() > 0 ? Collections.max(LossingStreakLengthList) : 0;
				
			}
			if (winningStreakFreq > 0) {
				bestWinningStreak = WinningStreakList.size() > 0 ? Collections.max(WinningStreakList) : 0;
				sumOfWinningStreak = WinningStreakList.stream().mapToDouble(f -> f.doubleValue()).sum();
				averageOfWinningStreak = sumOfWinningStreak/winningStreakFreq;
				maxWinningStreakLength = WinningStreakLengthList.size() > 0 ? Collections.max(WinningStreakLengthList) : 0;
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		
		
		
	}	
	
	public void calTotalPnL()
	{
		totalPnL = 0;
		
		PerDayRecord[] tempArray = dayRecords.toArray(new PerDayRecord[0]);
		for (PerDayRecord Per_Day :  tempArray)
		{
			totalPnL = totalPnL + Per_Day.totalPnL;
		}
	}
	
	public void calAveragePnL()
	{
		averagePnL = totalPnL/totalDays;
	}
	
	public void calTotalTrade()
	{
		totalTrades = 0;
		PerDayRecord[] tempArray = dayRecords.toArray(new PerDayRecord[0]);
		for (PerDayRecord Per_Day :  tempArray)
		{
			totalTrades = totalTrades + Per_Day.negativeTrades + Per_Day.positiveTrades;
		}

	}
	
	public void calAverageTrade()
	{
		averageTrades = totalTrades/totalDays;
	}
	
	public void calTotalWinningDay()
	{
		totalWinningDays = 0;
		PerDayRecord[] tempArray = dayRecords.toArray(new PerDayRecord[0]);
		for (PerDayRecord Per_Day :  tempArray)
		{
			if (Per_Day.totalPnL>0)
			{
				totalWinningDays++;
			}
		}
	}
	
	public void calTotalLosingDay()
	{
		totalLosingDays = 0;
		PerDayRecord[] tempArray = dayRecords.toArray(new PerDayRecord[0]);
		for (PerDayRecord Per_Day :  tempArray)
		{
			if (Per_Day.totalPnL<0)
			{
				totalLosingDays++;
			}
		}
	}
	
	public void calWinningPercentage()
	{
		winningPercentage = totalWinningDays/totalDays * 100;
	}
	
	public void calGainPerPositiveTrade()
	{
		averageProfitOfPositiveTrade = 0;
		double total_profit = 0;
		PerDayRecord[] tempArray = dayRecords.toArray(new PerDayRecord[0]);
		for (PerDayRecord Per_Day :  tempArray)
		{
			total_profit = total_profit + Per_Day.averagePositiveTrades;
		}
		averageProfitOfPositiveTrade = total_profit/totalDays;
	}
	
	public void calGainPerNegativeTrade()
	{
		averageProfitOfNegativeTrade = 0;
		double total_profit = 0;
		PerDayRecord[] tempArray = dayRecords.toArray(new PerDayRecord[0]);
		for (PerDayRecord Per_Day :  tempArray)
		{
			total_profit = total_profit + Per_Day.averageNegativeTrades;
		}
		averageProfitOfNegativeTrade = total_profit/totalDays;
	}
	
	public void calNoPositiveTrade()
	{
		averageNoPositiveTrade = 0;
		double total_positive_trades = 0;
		PerDayRecord[] tempArray = dayRecords.toArray(new PerDayRecord[0]);
		for (PerDayRecord Per_Day :  tempArray)
		{
			total_positive_trades = total_positive_trades + Per_Day.positiveTrades;
		}
		averageNoPositiveTrade = total_positive_trades/totalDays;
	}
	
	public void calNoNegativeTrade()
	{
		averageNoNegativeTrade = 0;
		double total_negative_trades = 0;
		PerDayRecord[] tempArray = dayRecords.toArray(new PerDayRecord[0]);
		for (PerDayRecord Per_Day :  tempArray)
		{
			total_negative_trades = total_negative_trades + Per_Day.negativeTrades;
		}
		averageNoNegativeTrade = total_negative_trades/totalDays;
	}
	
	public void calNoZeroPnLTrade()
	{
		averageZeroPnLTrade = 0;
		double total_zero_trades = 0;
		PerDayRecord[] tempArray = dayRecords.toArray(new PerDayRecord[0]);
		for (PerDayRecord Per_Day :  tempArray)
		{
			total_zero_trades = total_zero_trades + Per_Day.zeroPnlTrades;
		}
		averageZeroPnLTrade = total_zero_trades/totalDays;
		
	}
	
	public void calAverageholdingTime()
	{
		averageHoldingTime = 0;
		double total_holding_time = 0;
		
		PerDayRecord[] tempArray = dayRecords.toArray(new PerDayRecord[0]);
		for (PerDayRecord Per_Day :  tempArray)
		{
			total_holding_time = total_holding_time + Per_Day.averageHoldingTime;
		}
		averageHoldingTime = total_holding_time/totalDays;
	}
	
	
	public void calAdjustedPnLAfterFee()
	{
		adjustedPnLAfterFee = (testSet.getCashPerIndexPoint()*totalPnL) - ((testSet.getTradingFee() + testSet.getOtherCostPerTrade())*totalTrades);
	}
	
	public void calPnLByYear() {
		yearPnlMap = new TreeMap<Integer, Double>();
		for (PerDayRecord dayRecord : dayRecords)
		{
			int year = dayRecord.getDate().getYear() + 1900;
			if (yearPnlMap.containsKey(year)) {
				yearPnlMap.replace(year, yearPnlMap.get(year) + dayRecord.totalPnL);				
			} else {
				yearPnlMap.put(year, dayRecord.totalPnL);
			}
		}
	}
}
