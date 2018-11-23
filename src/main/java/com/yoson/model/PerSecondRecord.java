package com.yoson.model;

import java.text.ParseException;
import java.util.List;
import java.util.TreeMap;

public class PerSecondRecord {

	private long time;
	private String timeStr;
	private double bidPriceSum;
	private double askPriceSum;
	private double lastTradeSum;
	private double bidPrice;
	private double askPrice;
	private double lastTrade;
	private double actualAskPrice;
	private double actualBidPrice;
	private double actualLastTrade;
	private int checkMarketTime;
	private int reference;
	private int tCounter;
	private double highShort;
	private double highLong;
	private double lowShort;
	private double lowLong;
	private double mvs1;
	private double mvs2;
	private int trend2;
	private int action;
	private int smoothAction;
	private double position;
	private double pnl;
	private double noTrade;
	private double tradeCount;
	private double totalPnl;
//	private double highLowDiffernece;
	private double pc;
	private double mtm;
	private boolean isEnoughCounter;
	
	public PerSecondRecord() {
	}
	
	public void initAskPriceData(PerSecondRecord lastSecondRecord, TestSet testSet, List<ScheduleData> dailyScheduleData, ScheduleData scheduleDataPerSecond) {
		if(testSet.getAvgStep() == 0 || !scheduleDataPerSecond.getAskDataField().equals("askavg")) {
			this.askPrice = scheduleDataPerSecond.getAskPrice();
		} else {
			if(this.reference <= testSet.getAvgStep()) {
				this.askPriceSum = lastSecondRecord.askPriceSum + scheduleDataPerSecond.getAskPrice();							
			} else {
				this.askPrice = lastSecondRecord.askPriceSum / testSet.getAvgStep();				
				
				int first = this.reference - testSet.getAvgStep() - 1;
				this.askPriceSum = scheduleDataPerSecond.getAskPrice() + lastSecondRecord.askPriceSum - dailyScheduleData.get(first).getAskPrice();				
			}
		}
	}
	
	public void initBidPriceData(PerSecondRecord lastSecondRecord, TestSet testSet, List<ScheduleData> dailyScheduleData, ScheduleData scheduleDataPerSecond) {
		if(testSet.getAvgStep() == 0 || !scheduleDataPerSecond.getBidDataField().equals("bidavg")) {			
			this.bidPrice = scheduleDataPerSecond.getBidPrice();
		} else {
			if(this.reference <= testSet.getAvgStep()) {
				this.bidPriceSum = lastSecondRecord.bidPriceSum + scheduleDataPerSecond.getBidPrice();
			} else {
				this.bidPrice = lastSecondRecord.bidPriceSum / testSet.getAvgStep();
				
				int first = this.reference - testSet.getAvgStep() - 1;
				this.bidPriceSum = scheduleDataPerSecond.getBidPrice() + lastSecondRecord.bidPriceSum - dailyScheduleData.get(first).getBidPrice();
			}
		}
	}
	
	public void initLastTradePriceData(PerSecondRecord lastSecondRecord, TestSet testSet, List<ScheduleData> dailyScheduleData, ScheduleData scheduleDataPerSecond) {
		if(testSet.getAvgStep() == 0 || !scheduleDataPerSecond.getTradeDataField().equals("tradeavg")) {			
			this.lastTrade = scheduleDataPerSecond.getLastTrade();			
		} else {
			if(this.reference <= testSet.getAvgStep()) {				
				this.lastTradeSum = lastSecondRecord.lastTradeSum + scheduleDataPerSecond.getLastTrade();				
			} else {				
				this.lastTrade = lastSecondRecord.lastTradeSum / testSet.getAvgStep();	
				
				int first = this.reference - testSet.getAvgStep() - 1;				
				this.lastTradeSum = scheduleDataPerSecond.getLastTrade() + lastSecondRecord.lastTradeSum - dailyScheduleData.get(first).getLastTrade();
			}
		}
	}
	
	public PerSecondRecord(List<ScheduleData> dailyScheduleData, TestSet testSet, List<PerSecondRecord> dailyPerSecondRecordList, ScheduleData scheduleDataPerSecond, int checkMarketTime,TreeMap<Double, Integer> shortMap, TreeMap<Double, Integer> longMap) throws ParseException {
		PerSecondRecord lastSecondRecord = dailyPerSecondRecordList.size() == 0 ? new PerSecondRecord() : dailyPerSecondRecordList.get(dailyPerSecondRecordList.size() - 1);
		this.time = scheduleDataPerSecond.getId();
		this.timeStr = scheduleDataPerSecond.getDateTimeStr();
		this.actualAskPrice = scheduleDataPerSecond.getActualAskPrice();
		this.actualBidPrice = scheduleDataPerSecond.getActualBidPrice();
		this.actualLastTrade = scheduleDataPerSecond.getActualLastTrade();
		this.reference = lastSecondRecord.getReference() + 1;
		initAskPriceData(lastSecondRecord, testSet, dailyScheduleData, scheduleDataPerSecond);
		initBidPriceData(lastSecondRecord, testSet, dailyScheduleData, scheduleDataPerSecond);
		initLastTradePriceData(lastSecondRecord, testSet, dailyScheduleData, scheduleDataPerSecond);
		
		this.checkMarketTime = scheduleDataPerSecond.isLastMarketDayData() ? 0 : checkMarketTime;
		this.tCounter = checkMarketTime == 1 || testSet.isIncludeMorningData() ? lastSecondRecord.tCounter + 1 : 0;
		this.isEnoughCounter = this.tCounter > Math.max(testSet.gettShort(), testSet.gettLong());
//		if ("2015-01-19 13:00:00".equals(DateUtils.yyyyMMddHHmmss().format(new Date(time)))) {
//			System.out.println("debug point");
//		}

		initShort(shortMap, dailyPerSecondRecordList, lastSecondRecord, testSet);
		initLong(longMap, dailyPerSecondRecordList, lastSecondRecord, testSet);
//			initHighLowDiffernece();
		initmvs1(dailyPerSecondRecordList, testSet, lastSecondRecord);
		initmvs2(dailyPerSecondRecordList, testSet, lastSecondRecord);
		inittrend2(testSet);
		initAction(lastSecondRecord, testSet);		
		initSmoothAction(lastSecondRecord, testSet);
		initPosition(lastSecondRecord);
		initPc(lastSecondRecord);
		initMtm();
		initPnl(lastSecondRecord);
		initTradeCount(lastSecondRecord);
		initTotalPnl(lastSecondRecord);
	}

	private void initTotalPnl(PerSecondRecord lastSecondRecord) {
		this.totalPnl = lastSecondRecord.getTotalPnl() + this.pnl;
	}

	private void initTradeCount(PerSecondRecord lastSecondRecord) {
		if(((lastSecondRecord.getSmoothAction() != 0 ) && (this.smoothAction == 0)) || ((lastSecondRecord.getSmoothAction() == 0 ) && (this.smoothAction != 0)))
		{
			this.tradeCount = 1;
		}
		else
		{
			if((lastSecondRecord.getSmoothAction() != 0 ) && (this.smoothAction != 0) && (lastSecondRecord.getSmoothAction() != this.smoothAction))
			{
				this.tradeCount = 2;
			}
			else
			{
				this.tradeCount = 0;
			}
		}

		this.noTrade = lastSecondRecord.getNoTrade() + this.tradeCount;				
	}

	private void initPnl(PerSecondRecord lastSecondRecord) {
		if (this.smoothAction == lastSecondRecord.getSmoothAction())
		{
			this.pnl = 0;
		}
		else
		{
			if ((lastSecondRecord.getSmoothAction() != 0) && (this.smoothAction != lastSecondRecord.getSmoothAction()))
			{
				if (lastSecondRecord.getSmoothAction() > 0)
				{
					this.pnl = (this.bidPrice - lastSecondRecord.getPosition());
				}
				if (lastSecondRecord.getSmoothAction() < 0 )
				{
					this.pnl = lastSecondRecord.getPosition() - this.askPrice;
				}
			}
			else
			{
				this.pnl = 0 ;
			}
		}		
	}

	private void initMtm() {
		if(this.smoothAction == 0){
			this.mtm = 0;
		} else{
			if(this.smoothAction >= 1){
				this.mtm = this.lastTrade - this.position;
			}else{
				this.mtm = this.position - this.lastTrade;
			}
		}			
	}

	private void initPc(PerSecondRecord lastSecondRecord) {
		if( (this.position != 0 && lastSecondRecord.getPosition() != 0 && this.position != lastSecondRecord.getPosition()) || (lastSecondRecord.getPosition() == 0 && this.position != 0) ){
			this.pc = 1;
		}else{
			if(this.position == lastSecondRecord.getPosition() && this.position !=0){
				this.pc = lastSecondRecord.getPc() + 1;
			}else{
				this.pc = 0;
			}
		}
	}

	private void initPosition(PerSecondRecord lastSecondRecord) {		
		if ((lastSecondRecord.getPosition() != 0) && (this.smoothAction == lastSecondRecord.getSmoothAction()))
		{
			this.position = lastSecondRecord.getPosition();
		}
		else
		{
			if (this.smoothAction == 1)
			{
				this.position = this.askPrice;
			}
			else
			{
				if (this.smoothAction == -1)
				{
					this.position = this.bidPrice;
				}
				else
				{
					this.position = 0;
				}
					
			}
		}
	}



	private void initSmoothAction(PerSecondRecord lastSecondRecord, TestSet testSet) throws ParseException {
		if (lastSecondRecord.getTotalPnl() <= testSet.getNegativeStopLoss()
			|| this.checkMarketTime == 0
			|| !this.isEnoughCounter
			|| (lastSecondRecord.getPc() <= testSet.getItsCounter() && lastSecondRecord.getMtm() <= -testSet.getTradeStopLoss()*testSet.getInstantTradeStoploss()))
		{
			this.smoothAction = 0;
		}
		else
		{
			
			if ((lastSecondRecord.getSmoothAction() == 1) && (this.action == 0) && ((this.lastTrade - this.highShort) >= -testSet.getTradeStopLoss()))
			{
				this.smoothAction = 1;
			}
			else
			{

				if ((lastSecondRecord.getSmoothAction() == -1) && (this.action == 0) && ((this.lastTrade - this.lowShort) <= testSet.getTradeStopLoss()))				
				{
					this.smoothAction =  -1;
				}
				else
				{
					if (((lastSecondRecord.getSmoothAction() == 0) && (this.action != 0)) || (lastSecondRecord.getSmoothAction() == this.action) || ((lastSecondRecord.getSmoothAction() != 0) && (this.action != 0)))
					{
						this.smoothAction = this.action;
					}
					else
					{
						this.smoothAction = 0;
					}
				}
			
			}
		}
	}
	
	private double mvs1Sum = 0;
	private void initmvs1(List<PerSecondRecord> dailyPerSecondRecordList, TestSet testSet, PerSecondRecord lastSecondRecord) {
		if(tCounter > testSet.getMas()) {
		 	if(lastSecondRecord.mvs1Sum == 0) {
				for(int i = reference - testSet.getMas() - 1; i < reference -1; i++) {
					mvs1Sum += dailyPerSecondRecordList.get(i).getLastTrade();
				}
				mvs1Sum += lastTrade;
			} else {
				mvs1Sum = lastSecondRecord.mvs1Sum - dailyPerSecondRecordList.get(reference - testSet.getMas() - 2).getLastTrade() + lastTrade; 
			}	
		}
		if (checkMarketTime == 0 || reference < testSet.getMas() + 1) {
			this.mvs1 = 0;
		} else {
			this.mvs1 = mvs1Sum / (testSet.getMas() + 1);
		}
	}
	
	private double mvs2Sum = 0;
	private void initmvs2(List<PerSecondRecord> dailyPerSecondRecordList, TestSet testSet, PerSecondRecord lastSecondRecord) {
		if(tCounter > testSet.getMal()) {
			if(lastSecondRecord.mvs2Sum == 0) {
				for(int i = reference - testSet.getMal() - 1; i < reference - 1; i++) {
					mvs2Sum += dailyPerSecondRecordList.get(i).getLastTrade();
				}
				mvs2Sum += lastTrade;
			} else {
				mvs2Sum = lastSecondRecord.mvs2Sum - dailyPerSecondRecordList.get(reference - testSet.getMal() - 2).getLastTrade() + lastTrade; 
			}
		}
		if (checkMarketTime == 0 || reference < testSet.getMal() + 1) {
			this.mvs2 = 0;
		} else {
			this.mvs2 = mvs2Sum / (testSet.getMal() + 1);
		}		
	}
	
	private void inittrend2(TestSet testSet) {
		if(mvs1 != 0 && mvs2 != 0) {
			if(mvs1 - mvs2 >= testSet.getMat()) {
				this.trend2 = 1;
			} else if(mvs2 - mvs1 >= testSet.getMat()) {
				this.trend2 = -1;
			}
		}
	}
	
	private void initAction(PerSecondRecord lastSecondRecord, TestSet testSet) {
		
		if (checkMarketTime == 0 || highLong == 0 || highShort == 0 || lowLong == 0 || lowShort == 0 || trend2 == 0)
		{
			this.action = 0; 			
		}
		else 
		{
			if (lastTrade >= this.highShort && this.highShort == this.highLong && trend2 == 1)
			{
				this.action = 1;
			}
			else 
			{
				if (lastTrade <= this.lowShort && this.lowShort == this.lowLong && trend2 == -1)
				{
					this.action = -1;
				}
				else
				{
					this.action = 0;
				}
			}
		}	
	}
	
	private void initShort(TreeMap<Double, Integer> shortMap, List<PerSecondRecord> dailyPerSecondRecordList, PerSecondRecord lastSecondRecord, TestSet testSet) {
		renewMap(shortMap, dailyPerSecondRecordList, testSet.gettShort());
		if (this.tCounter > testSet.gettShort() && checkMarketTime == 1){
			this.highShort = shortMap.lastKey();
			this.lowShort = shortMap.firstKey();
		}
	}
	
	private void initLong(TreeMap<Double, Integer> longMap, List<PerSecondRecord> dailyPerSecondRecordList, PerSecondRecord lastSecondRecord, TestSet testSet) {
		renewMap(longMap, dailyPerSecondRecordList, testSet.gettLong());
		if (this.tCounter > testSet.gettLong() && checkMarketTime == 1){
			this.highLong = longMap.lastKey();
			this.lowLong = longMap.firstKey();
		}
	}

	private void renewMap(TreeMap<Double, Integer> map, List<PerSecondRecord> dailyPerSecondRecordList, int base) {
		if(this.reference == 1) map.clear();
		if(map.containsKey(lastTrade)) {
			map.replace(lastTrade, map.get(lastTrade) + 1);
		} else {
			map.put(lastTrade, 1);
		}
		if(this.reference > base + 1) {
			int firstIndex = reference - base - 2;
			double firstTrade = dailyPerSecondRecordList.get(firstIndex).getLastTrade();
			if (map.get(firstTrade) > 1) {
				map.replace(firstTrade, map.get(firstTrade) - 1);
			} else {
				map.remove(firstTrade);
			}
		}
	}
	
//	private void initHighLowDiffernece() {
//		if (this.isEnoughCounter)
//		{	
//			this.highLowDiffernece = Math.max(Math.max(Math.max(lowLong, highLong), lowShort), lowLong) - Math.min(Math.min(Math.min(lowLong, highLong), lowShort), lowLong);			
//		}
//	}

	public long getTime() {
		return time;
	}
	
	public String getTimeStr() {
		return timeStr;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public double getBidPrice() {
		return bidPrice;
	}

	public void setBidPrice(double bidprice) {
		this.bidPrice = bidprice;
	}

	public double getAskPrice() {
		return askPrice;
	}

	public void setAskPrice(double askprice) {
		this.askPrice = askprice;
	}

	public double getLastTrade() {
		return lastTrade;
	}

	public void setLastTrade(double lasttrade) {
		this.lastTrade = lasttrade;
	}

	public int getCheckMarketTime() {
		return checkMarketTime;
	}

	public void setCheckMarketTime(int checkMarketTime) {
		this.checkMarketTime = checkMarketTime;
	}

	public int getReference() {
		return reference;
	}

	public void setReference(int reference) {
		this.reference = reference;
	}

	public int gettCounter() {
		return tCounter;
	}

	public void settCounter(int tCounter) {
		this.tCounter = tCounter;
	}

	public double getHighShort() {
		return highShort;
	}

	public void setHighShort(double highShort) {
		this.highShort = highShort;
	}

	public double getHighLong() {
		return highLong;
	}

	public void setHighLong(double highLong) {
		this.highLong = highLong;
	}

	public double getLowShort() {
		return lowShort;
	}

	public void setLowShort(double lowShort) {
		this.lowShort = lowShort;
	}

	public double getLowLong() {
		return lowLong;
	}

	public void setLowLong(double lowLong) {
		this.lowLong = lowLong;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public int getSmoothAction() {
		return smoothAction;
	}

	public void setSmoothAction(int smoothAction) {
		this.smoothAction = smoothAction;
	}

	public double getPosition() {
		return position;
	}

	public void setPosition(double position) {
		this.position = position;
	}

	public double getPnl() {
		return pnl;
	}

	public void setPnl(double pnl) {
		this.pnl = pnl;
	}

	public double getNoTrade() {
		return noTrade;
	}

	public void setNoTrade(double noTrade) {
		this.noTrade = noTrade;
	}

	public double getTradeCount() {
		return tradeCount;
	}

	public void setTradeCount(double tradeCount) {
		this.tradeCount = tradeCount;
	}

	public double getTotalPnl() {
		return totalPnl;
	}

	public void setTotalPnl(double totalPnl) {
		this.totalPnl = totalPnl;
	}

	public double getPc() {
		return pc;
	}

	public void setPc(double pc) {
		this.pc = pc;
	}

	public double getMtm() {
		return mtm;
	}

	public void setMtm(double mtm) {
		this.mtm = mtm;
	}

//	public double getHighLowDiffernece() {
//		return highLowDiffernece;
//	}
//
//	public void setHighLowDiffernece(double highLowDiffernece) {
//		this.highLowDiffernece = highLowDiffernece;
//	}

	public double getMvs1() {
		return mvs1;
	}

	public void setMvs1(double mvs1) {
		this.mvs1 = mvs1;
	}

	public double getMvs2() {
		return mvs2;
	}

	public void setMvs2(double mvs2) {
		this.mvs2 = mvs2;
	}

	public int getTrend2() {
		return trend2;
	}

	public void setTrend2(int trend2) {
		this.trend2 = trend2;
	}

	public double getBidPriceSum() {
		return bidPriceSum;
	}

	public void setBidPriceSum(double bidPriceSum) {
		this.bidPriceSum = bidPriceSum;
	}

	public double getAskPriceSum() {
		return askPriceSum;
	}

	public void setAskPriceSum(double askPriceSum) {
		this.askPriceSum = askPriceSum;
	}

	public double getLastTradeSum() {
		return lastTradeSum;
	}

	public void setLastTradeSum(double lastTradeSum) {
		this.lastTradeSum = lastTradeSum;
	}

	public double getActualAskPrice() {
		return actualAskPrice;
	}

	public void setActualAskPrice(double actualAskPrice) {
		this.actualAskPrice = actualAskPrice;
	}

	public double getActualBidPrice() {
		return actualBidPrice;
	}

	public void setActualBidPrice(double actualBidPrice) {
		this.actualBidPrice = actualBidPrice;
	}

	public double getActualLastTrade() {
		return actualLastTrade;
	}

	public void setActualLastTrade(double actualLastTrade) {
		this.actualLastTrade = actualLastTrade;
	}

}
