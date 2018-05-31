package com.yoson.model;

import java.text.ParseException;
import java.util.List;

public class PerSecondRecord {

	private long time;
	private String timeStr;
	private double bidPrice;
	private double askPrice;
	private double lastTrade;
	private int checkMarketTime;
	private int reference;
	private int tCounter;
	private double highShort;
	private double highLong;
	private double lowShort;
	private double lowLong;
	private int action;
	private int smoothAction;
	private double position;
	private double pnl;
	private double noTrade;
	private double tradeCount;
	private double totalPnl;
	private double highLowDiffernece;
	private double gMax;
	private double gMin;
	private double gHLD;
	private double averageHLD;
	private double pc;
	private double mtm;
	private boolean isEnoughCounter;
	
//	private int reachPoint;
	
	private static double sumHighLowDiffernece;
	
	public PerSecondRecord() {
//		lastTradeDataList.set(new ArrayList<Double>());
//		PerSecondRecord.isReachPoint.set(false);
//		this.reachPoint.set(0);
	}
	
	public PerSecondRecord(List<ScheduleData> dailyScheduleData, TestSet testSet, List<PerSecondRecord> dailyPerSecondRecordList, ScheduleData scheduleDataPerSecond, int checkMarketTime) throws ParseException {
		PerSecondRecord lastSecondRecord = dailyPerSecondRecordList.size() == 0 ? new PerSecondRecord() : dailyPerSecondRecordList.get(dailyPerSecondRecordList.size() - 1);
		this.time = scheduleDataPerSecond.getId();
		this.timeStr = scheduleDataPerSecond.getDateTimeStr();
		this.askPrice = scheduleDataPerSecond.getAskPrice();
		this.bidPrice = scheduleDataPerSecond.getBidPrice();
		this.lastTrade = scheduleDataPerSecond.getLastTrade();
		this.reference = lastSecondRecord.getReference() + 1;
		
		this.checkMarketTime = checkMarketTime;
		this.tCounter = this.checkMarketTime == 1 ? lastSecondRecord.tCounter + 1 : 0;
		this.isEnoughCounter = this.tCounter > Math.max(testSet.gettShort(), testSet.gettLong());
//		if ("2016-07-08 09:29:26".equals(DateUtils.yyyyMMddHHmmss().format(new Date(time)))) {
//			System.out.println("debug point");
//		}
		initLastTradeDataList(lastSecondRecord);
		if (lastSecondRecord.getTotalPnl() > -testSet.getStopLoss() ) {
			initShort(dailyPerSecondRecordList, lastSecondRecord, testSet);
			initLong(dailyPerSecondRecordList, lastSecondRecord, testSet);
			initHighLowDiffernece();
			initSumHighLowDiffernece(testSet, dailyPerSecondRecordList);
			initAction(lastSecondRecord, testSet);
			initSmoothAction(lastSecondRecord, testSet);
		}
		initAverageHLD(testSet);
		initPosition(lastSecondRecord);
		initPc(lastSecondRecord);
		initMtm();
		initPnl(lastSecondRecord);
		initTradeCount(lastSecondRecord);
		initTotalPnl(lastSecondRecord);
	}
	
	private void initLastTradeDataList(PerSecondRecord lastSecondRecord) {
//		if (this.checkMarketTime == 1 && !PerSecondRecord.isReachPoint) {
//			PerSecondRecord.isReachPoint = true;
//			this.reachPoint = this.reference - 1;
//		} else if(this.checkMarketTime == 0) {
//			PerSecondRecord.isReachPoint = false;
//			this.reachPoint = 0;
//		}	
		
//		if(this.checkMarketTime == 0) {
//			this.reachPoint = 0;
//		} else if (this.checkMarketTime == 1 && lastSecondRecord.checkMarketTime == 1) {
//			this.reachPoint = lastSecondRecord.reachPoint;
//		} else if (this.checkMarketTime == 1 && lastSecondRecord.checkMarketTime == 0) {
//			this.reachPoint = this.reference - 1;
//		}
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
		if (this.checkMarketTime == 0
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
	
	private void initSumHighLowDiffernece(TestSet testSet, List<PerSecondRecord> dailyPerSecondRecordList) {
		sumHighLowDiffernece += this.highLowDiffernece;
		if (this.reference > testSet.gettLong() + 1)
		{	
			this.sumHighLowDiffernece -= dailyPerSecondRecordList.get(this.reference - testSet.gettLong() - 2).highLowDiffernece;			
		}
	}
	
	private void initAverageHLD(TestSet testSet) {
		if (this.lowLong != 0) {
			this.averageHLD = sumHighLowDiffernece / (testSet.gettLong() + 1);
		}
	}
	
	private void initAction(PerSecondRecord lastSecondRecord, TestSet testSet) {
		
		if (checkMarketTime == 0 || !this.isEnoughCounter || this.highLowDiffernece < testSet.getHld() * this.averageHLD)
		{
			this.action = 0; 			
		}
		else 
		{
			if (lastTrade >= this.highShort && this.highShort == this.highLong)
			{
				this.action = 1;
			}
			else 
			{
				if (lastTrade <= this.lowShort && this.lowShort == this.lowLong)
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
	
	private void initShort(List<PerSecondRecord> dailyPerSecondRecordList, PerSecondRecord lastSecondRecord, TestSet testSet) {
		if (this.tCounter> testSet.gettShort() && checkMarketTime == 1)
		{
			this.highShort = lastTrade;
			this.lowShort = lastTrade;
			for(int i = reference - testSet.gettShort() - 1; i < reference - 1; i++) {
				this.highShort = Math.max(this.highShort, dailyPerSecondRecordList.get(i).getLastTrade());
				this.lowShort = Math.min(this.lowShort, dailyPerSecondRecordList.get(i).getLastTrade());
			}
		}
	}
	
//	private void initShort(List<ScheduleData> dailyScheduleData, PerSecondRecord lastSecondRecord, TestSet testSet) {
//		if (this.tCounter> testSet.gettShort())
//		{
//			int start = this.tCounter - (testSet.gettShort() + 1);
//			int end = this.tCounter - 1;
//			if (start > 0 
//				&& start <= end
//				&& lastSecondRecord.highShort != dailyScheduleData.get(this.reachPoint + start - 1).getLastTrade()
//				&& lastSecondRecord.lowShort != dailyScheduleData.get(this.reachPoint + start - 1).getLastTrade()) {
//				double lastTrade2 = dailyScheduleData.get(this.reachPoint + end).getLastTrade();
//				this.highShort = Math.max(lastSecondRecord.highShort, lastTrade2);
//				this.lowShort = Math.min(lastSecondRecord.lowShort, lastTrade2);
//			} else {
//				this.highShort = Double.MIN_VALUE;
//				this.lowShort = Double.MAX_VALUE;
//				for (int i = start; i<= end; i++)
//				{
//					double _lastTrade = dailyScheduleData.get(this.reachPoint + i).getLastTrade();
//					this.highShort = Math.max(this.highShort, _lastTrade);
//					this.lowShort = Math.min(this.lowShort, _lastTrade);
//				}				
//			}
//		}
//	}
	
	private void initLong(List<PerSecondRecord> dailyPerSecondRecordList, PerSecondRecord lastSecondRecord, TestSet testSet) {
		if (this.tCounter> testSet.gettLong() && checkMarketTime == 1)
		{
			this.highLong = lastTrade;
			this.lowLong = lastTrade;
			for(int i = reference - testSet.gettLong() - 1; i < reference - 1; i++) {
				this.highLong = Math.max(this.highLong, dailyPerSecondRecordList.get(i).getLastTrade());
				this.lowLong = Math.min(this.lowLong, dailyPerSecondRecordList.get(i).getLastTrade());
			}
		}
	}
	
//	private void initLong(List<ScheduleData> dailyScheduleData, PerSecondRecord lastSecondRecord, TestSet testSet) {
//		if (this.tCounter> testSet.gettLong())
//		{
//			int start = this.tCounter - (testSet.gettLong() + 1);
//			int end = this.tCounter - 1;
//			
//			if (start > 0 
//				&& start <= end
//				&& lastSecondRecord.highLong != dailyScheduleData.get(this.reachPoint + start-1).getLastTrade()
//				&& lastSecondRecord.lowLong != dailyScheduleData.get(this.reachPoint + start-1).getLastTrade()) {
//				double lastTrade2 = dailyScheduleData.get(this.reachPoint + end).getLastTrade();
//				this.highLong = Math.max(lastSecondRecord.highLong, lastTrade2);
//				this.lowLong = Math.min(lastSecondRecord.lowLong, lastTrade2);
//			} else {
//				this.highLong = Double.MIN_VALUE;
//				this.lowLong = Double.MAX_VALUE;
//				for (int i = start; i<= end;  i++)
//				{
//					double _lastTrade = dailyScheduleData.get(this.reachPoint + i).getLastTrade();
//					this.highLong = Math.max(this.highLong, _lastTrade);
//					this.lowLong = Math.min(this.lowLong, _lastTrade);
//				}				
//			}
//		}
//	}
	
	private void initHighLowDiffernece() {
		if (this.isEnoughCounter)
		{	
			this.highLowDiffernece = Math.max(Math.max(Math.max(lowLong, highLong), lowShort), lowLong) - Math.min(Math.min(Math.min(lowLong, highLong), lowShort), lowLong);			
		}
	}

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

	public double getgMax() {
		return gMax;
	}

	public void setgMax(double gMax) {
		this.gMax = gMax;
	}

	public double getgMin() {
		return gMin;
	}

	public void setgMin(double gMin) {
		this.gMin = gMin;
	}

	public double getgHLD() {
		return gHLD;
	}

	public void setgHLD(double gHLD) {
		this.gHLD = gHLD;
	}

	public double getAverageHLD() {
		return averageHLD;
	}

	public void setAverageHLD(double averageHLD) {
		this.averageHLD = averageHLD;
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

	public double getHighLowDiffernece() {
		return highLowDiffernece;
	}

	public void setHighLowDiffernece(double highLowDiffernece) {
		this.highLowDiffernece = highLowDiffernece;
	}

}
