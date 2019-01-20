package com.yoson.model;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yoson.date.DateUtils;

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
	
	private double maxRange;
	private double minRange;
	private double _maxRange;
	private double _minRange;
	private double range;
	private double upper;
	private double lower;
	private int check;
	private int stationaryCheck;
	private double stationarySlope;	
	private int action;
	private int smoothAction;
	private double position;
	private int posCounting;
	private double mtm;
	private double maxMtm;
	private double pnl;
	private int tradeCount;
	private int totalTrade;
	private double totalPnl;
	private int tCounter;
	private int pc;
	
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
	
	public PerSecondRecord(List<ScheduleData> dailyScheduleData, TestSet testSet, List<PerSecondRecord> dailyPerSecondRecordList, ScheduleData scheduleDataPerSecond, int checkMarketTime) throws ParseException {
		this(dailyScheduleData, testSet, dailyPerSecondRecordList, scheduleDataPerSecond, checkMarketTime, null);
	}
	
	public PerSecondRecord(List<ScheduleData> dailyScheduleData, TestSet testSet, List<PerSecondRecord> dailyPerSecondRecordList, ScheduleData scheduleDataPerSecond, int checkMarketTime, Map<Double, Integer> lastTradeCountMap) throws ParseException {
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
		//2015-01-19  9:41:16
		if ("2015-01-19 10:01:50".equals(DateUtils.yyyyMMddHHmmss().format(new Date(time)))) {
			System.out.println("debug point");
		}
		initCheckMarketTime(dailyScheduleData, scheduleDataPerSecond, testSet, checkMarketTime);
		this.tCounter = checkMarketTime == 1 || testSet.isIncludeMorningData() ? lastSecondRecord.tCounter + 1 : 0;
		initMaxRangeAndMinRange(lastSecondRecord, dailyPerSecondRecordList, testSet);
		initRange();
		initUpper();
		initLower();
		initCheck();
		initStationaryCheck(lastSecondRecord, testSet);
		initStationarySlope(dailyPerSecondRecordList);				
		initAction(lastSecondRecord, testSet);		
		initSmoothAction(lastSecondRecord, testSet);
		initPosition(lastSecondRecord);
		initMtm();
		initPosCounting(lastSecondRecord);
		initMaxMtm(dailyPerSecondRecordList, lastSecondRecord);
		initPnl(lastSecondRecord);
		initTradeCount(lastSecondRecord);
		initTotalTrades(lastSecondRecord);
		initTotalPnl(lastSecondRecord);
		initPc(lastSecondRecord);
	}
	
	public void initCheckMarketTime(List<ScheduleData> dailyScheduleData, ScheduleData scheduleDataPerSecond, TestSet testSet, int checkMarketTime) throws ParseException {
		if(scheduleDataPerSecond.isLastMarketDayData()) {
			this.checkMarketTime = 0;
		} else if(testSet.isIncludeLastMarketDayData()) {
			boolean hasLastMarketDayData = dailyScheduleData != null && dailyScheduleData.get(0).isLastMarketDayData();
			long current = DateUtils.HHmmss().parse(scheduleDataPerSecond.getTimeStr()).getTime();
			long morningStartTime = DateUtils.HHmmss().parse(testSet.getMarketStartTime()).getTime();
			long lunchStartTime = DateUtils.HHmmss().parse(testSet.getLunchStartTimeFrom()).getTime();
			boolean isMorningData = current >= morningStartTime && current <= lunchStartTime;
			this.checkMarketTime = !hasLastMarketDayData && isMorningData ? 0 : checkMarketTime;
		} else {
			this.checkMarketTime = checkMarketTime;
		}
	}
	
	public void initMaxRangeAndMinRange(PerSecondRecord lastSecondRecord, List<PerSecondRecord> dailyPerSecondRecordList, TestSet testSet) {
		_maxRange = Math.max(lastSecondRecord._maxRange, lastTrade);
		_minRange = dailyPerSecondRecordList.size() == 0 ? lastTrade : Math.min(lastSecondRecord._minRange, lastTrade);		
		if(reference > testSet.getTimer()) {
			double begin = dailyPerSecondRecordList.get(dailyPerSecondRecordList.size() - testSet.getTimer()).getLastTrade();
			if((begin == lastSecondRecord._maxRange || begin == lastSecondRecord._minRange)) {
				_maxRange = lastTrade;
				_minRange = lastTrade;
				for(int i = dailyPerSecondRecordList.size() - testSet.getTimer() + 1; i < dailyPerSecondRecordList.size(); i++) {
					_maxRange = Math.max(_maxRange, dailyPerSecondRecordList.get(i).getLastTrade());
					_minRange = Math.min(_minRange, dailyPerSecondRecordList.get(i).getLastTrade());	
				}				
			}
		}
		maxRange = tCounter > testSet.getTimer() ? _maxRange : 0;
		minRange = tCounter > testSet.getTimer() ? _minRange : 0;
	}
	
	public void initRange() {
		range = maxRange - minRange;
	}
	
	public void initUpper() {
		if(minRange != 0) {
			upper = maxRange - (maxRange - minRange + 1) / 3;
		}
	}
	
	public void initLower() {
		if(minRange != 0) {
			lower = minRange + (maxRange - minRange + 1) / 3;
		}
	}
	
	public void initCheck() {
		if(upper != 0) {
			if(lastTrade >= upper) check = 1;
			else if(lastTrade <= lower) check = -1;
		}
	}
	
	public void initStationaryCheck(PerSecondRecord lastSecondRecord, TestSet testSet) {
		if(tCounter > testSet.getTimer()) {
			if(lastSecondRecord.getStationaryCheck() == 0 && check == 0 || lastSecondRecord.getCheck() != 0 && check == 0) stationaryCheck = 1;
			else if(lastSecondRecord.getStationaryCheck() != 0 && check == 0) stationaryCheck = lastSecondRecord.getStationaryCheck() + 1;
		}
	}
	
	public void initStationarySlope(List<PerSecondRecord> dailyPerSecondRecordList) {
		if(!(stationaryCheck == 0 || stationaryCheck == 1)) {
			stationarySlope = (lastTrade - dailyPerSecondRecordList.get(reference - stationaryCheck).getLastTrade()) / stationaryCheck;
		}
	}
	
	public void initAction(PerSecondRecord lastSecondRecord, TestSet testSet) {
		if(lastSecondRecord.getCheck() == 0 && check == 1 && lastSecondRecord.getStationarySlope() < 0 && lastSecondRecord.getStationaryCheck() >= testSet.getAction()) action = 1;
		else if(lastSecondRecord.getCheck() == 0 && check == -1 && lastSecondRecord.getStationarySlope() > 0 && lastSecondRecord.getStationaryCheck() >= testSet.getAction()) action = -1;
	}
	
	public void initSmoothAction(PerSecondRecord lastSecondRecord, TestSet testSet) {
		if(this.checkMarketTime != 0) {
			if(lastSecondRecord.getAction() == 0 && action != 0) smoothAction = action;
			else if(lastSecondRecord.getSmoothAction() != 0 && (check == lastSecondRecord.getSmoothAction() || check == 0) && (action == lastSecondRecord.getSmoothAction() || action == 0) && lastSecondRecord.getMtm() >= -1 * testSet.getAbsoluteTradeStopLoss()) smoothAction = lastSecondRecord.getSmoothAction();
		}
	}
	
	public void initPosition(PerSecondRecord lastSecondRecord) {
		if (this.checkMarketTime == 0) {
			this.position = 0;
		} else if(lastSecondRecord.getPosition() != 0 && this.smoothAction == lastSecondRecord.getSmoothAction()) {
			this.position = lastSecondRecord.getPosition();
		} else if(this.smoothAction != 0) {
			this.position = this.lastTrade;
		}
	}
	
	public void initPosCounting(PerSecondRecord lastSecondRecord) {
		if (this.position == 0) {
			this.posCounting = 0;
		} else if(this.position != lastSecondRecord.getPosition() || this.position != 0 && lastSecondRecord.getPosition() == 0) {
			this.posCounting = 1;
		} else {
			this.posCounting = lastSecondRecord.getPosCounting() + 1;
		}
	}
	
	public void initMtm() {
		if (this.position == 0) {
			this.mtm = 0;
		} else if(this.smoothAction == 1) {
			this.mtm = this.lastTrade - this.position;
		} else if (this.smoothAction == -1) {
			this.mtm = this.position - this.lastTrade;
		} 
	}
	
	public void initMaxMtm(List<PerSecondRecord> dailyPerSecondRecordList, PerSecondRecord lastSecondRecord) {
		if (this.posCounting == 0) {
			this.maxMtm = 0;
		} else {
//			this.maxMtm = this.mtm;
//			for(int i = this.reference - this.posCounting; i < this.reference - 1; i++) {
//				this.maxMtm = Math.max(this.maxMtm, dailyPerSecondRecordList.get(i).getMtm());
//			}
			
			this.maxMtm = this.mtm;			
			if(lastSecondRecord.getPosCounting() != 0 && this.posCounting == (lastSecondRecord.getPosCounting() + 1)) {
				this.maxMtm = Math.max(this.maxMtm, lastSecondRecord.getMaxMtm());
			} else {
				for(int i = this.reference - this.posCounting; i < this.reference - 1; i++) {
					this.maxMtm = Math.max(this.maxMtm, dailyPerSecondRecordList.get(i).getMtm());
				}				
			}
			
		}
	}
	
	public void initPnl(PerSecondRecord lastSecondRecord) {
		if (lastSecondRecord.getSmoothAction() == 0 && this.smoothAction == 0) {
			this.pnl = 0;
		} else if(lastSecondRecord.getSmoothAction() != 0 && this.smoothAction != lastSecondRecord.getSmoothAction()) {
			if(lastSecondRecord.getSmoothAction() > 0) {
				this.pnl = this.lastTrade - lastSecondRecord.getPosition();
			} else if(lastSecondRecord.getSmoothAction() < 0) {
				this.pnl = lastSecondRecord.getPosition() - this.lastTrade;
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
	
	public void initTotalPnl(PerSecondRecord lastSecondRecord) {
		this.totalPnl = lastSecondRecord.totalPnl + this.pnl;
	}
	
	public void initTradeCount(PerSecondRecord lastSecondRecord) {
		this.tradeCount = Math.abs(this.smoothAction - lastSecondRecord.getSmoothAction());		
	}
	
	public void initTotalTrades(PerSecondRecord lastSecondRecord) {
		this.totalTrade = lastSecondRecord.totalTrade + this.tradeCount; 
	}
	
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getTimeStr() {
		return timeStr;
	}

	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}

	public double getBidPrice() {
		return bidPrice;
	}

	public void setBidPrice(double bidPrice) {
		this.bidPrice = bidPrice;
	}

	public double getAskPrice() {
		return askPrice;
	}

	public void setAskPrice(double askPrice) {
		this.askPrice = askPrice;
	}

	public double getLastTrade() {
		return lastTrade;
	}

	public void setLastTrade(double lastTrade) {
		this.lastTrade = lastTrade;
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

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public double getPosition() {
		return position;
	}

	public void setPosition(double position) {
		this.position = position;
	}

	public double getMtm() {
		return mtm;
	}

	public void setMtm(double mtm) {
		this.mtm = mtm;
	}

	public int getSmoothAction() {
		return smoothAction;
	}

	public void setSmoothAction(int smoothAction) {
		this.smoothAction = smoothAction;
	}

	public int getPosCounting() {
		return posCounting;
	}

	public void setPosCounting(int posCounting) {
		this.posCounting = posCounting;
	}

	public double getMaxMtm() {
		return maxMtm;
	}

	public void setMaxMtm(double maxMtm) {
		this.maxMtm = maxMtm;
	}

	public double getPnl() {
		return pnl;
	}

	public void setPnl(double pnl) {
		this.pnl = pnl;
	}

	public double getTotalPnl() {
		return totalPnl;
	}

	public void setTotalPnl(double totalPnl) {
		this.totalPnl = totalPnl;
	}

	public int getTradeCount() {
		return tradeCount;
	}

	public void setTradeCount(int tradeCount) {
		this.tradeCount = tradeCount;
	}

	public int getTotalTrade() {
		return totalTrade;
	}

	public void setTotalTrade(int totalTrade) {
		this.totalTrade = totalTrade;
	}

	public double getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(double maxRange) {
		this.maxRange = maxRange;
	}

	public double getMinRange() {
		return minRange;
	}

	public void setMinRange(double minRange) {
		this.minRange = minRange;
	}

	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range = range;
	}

	public double getUpper() {
		return upper;
	}

	public void setUpper(double upper) {
		this.upper = upper;
	}

	public double getLower() {
		return lower;
	}

	public void setLower(double lower) {
		this.lower = lower;
	}

	public int getCheck() {
		return check;
	}

	public void setCheck(int check) {
		this.check = check;
	}

	public int getStationaryCheck() {
		return stationaryCheck;
	}

	public void setStationaryCheck(int stationaryCheck) {
		this.stationaryCheck = stationaryCheck;
	}

	public double getStationarySlope() {
		return stationarySlope;
	}

	public void setStationarySlope(double stationarySlope) {
		this.stationarySlope = stationarySlope;
	}

	public int gettCounter() {
		return tCounter;
	}

	public void settCounter(int tCounter) {
		this.tCounter = tCounter;
	}

	public int getPc() {
		return pc;
	}

	public void setPc(int pc) {
		this.pc = pc;
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

}
