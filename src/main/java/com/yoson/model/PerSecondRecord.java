package com.yoson.model;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public class PerSecondRecord {

	private long time;
	private String timeStr;
	private double bidPrice;
	private double askPrice;
	private double lastTrade;
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
	
	public PerSecondRecord(List<ScheduleData> dailyScheduleData, TestSet testSet, List<PerSecondRecord> dailyPerSecondRecordList, ScheduleData scheduleDataPerSecond, int checkMarketTime) throws ParseException {
		this(dailyScheduleData, testSet, dailyPerSecondRecordList, scheduleDataPerSecond, checkMarketTime, null);
	}
	
	public PerSecondRecord(List<ScheduleData> dailyScheduleData, TestSet testSet, List<PerSecondRecord> dailyPerSecondRecordList, ScheduleData scheduleDataPerSecond, int checkMarketTime, Map<Double, Integer> lastTradeCountMap) throws ParseException {
		PerSecondRecord lastSecondRecord = dailyPerSecondRecordList.size() == 0 ? new PerSecondRecord() : dailyPerSecondRecordList.get(dailyPerSecondRecordList.size() - 1);
		this.time = scheduleDataPerSecond.getId();
		this.timeStr = scheduleDataPerSecond.getDateTimeStr();
		this.askPrice = scheduleDataPerSecond.getAskPrice();
		this.bidPrice = scheduleDataPerSecond.getBidPrice();
		this.lastTrade = scheduleDataPerSecond.getLastTrade();
		this.reference = lastSecondRecord.getReference() + 1;
		//2015-01-19  9:41:16
//		if ("2015-01-19 11:58:00".equals(DateUtils.yyyyMMddHHmmss().format(new Date(time)))) {
//			System.out.println("debug point");
//		}
		this.checkMarketTime = checkMarketTime;
		this.tCounter = this.checkMarketTime == 1 || testSet.isIncludeMorningData() ? lastSecondRecord.tCounter + 1 : 0;
		initMaxRangeAndMinRange(dailyPerSecondRecordList, testSet);
		initRange();
		initUpper();
		initLower();
		initCheck();
		initStationaryCheck(lastSecondRecord, testSet);
		initStationarySlope(dailyPerSecondRecordList);				
		initAction(lastSecondRecord, testSet);		
		initSmoothAction(lastSecondRecord, testSet);
		initPosition(lastSecondRecord);
		initPosCounting(lastSecondRecord);
		initMtm();
		initMaxMtm(dailyPerSecondRecordList, lastSecondRecord);
		initPnl(lastSecondRecord);
		initTradeCount(lastSecondRecord);
		initTotalTrades(lastSecondRecord);
		initTotalPnl(lastSecondRecord);
	}
	
	public void initMaxRangeAndMinRange(List<PerSecondRecord> dailyPerSecondRecordList, TestSet testSet) {
		double begin = dailyPerSecondRecordList.get(dailyPerSecondRecordList.size() - testSet.getTimer()).getLastTrade();
		if(reference > testSet.getTimer() && (begin == _maxRange || begin == _minRange)) {
			_maxRange = lastTrade;
			_minRange = lastTrade;
			for(int i = dailyPerSecondRecordList.size() - testSet.getTimer(); i < dailyPerSecondRecordList.size(); i++) {
				_maxRange = Math.max(_maxRange, dailyPerSecondRecordList.get(i).getLastTrade());
				_minRange = Math.max(_minRange, dailyPerSecondRecordList.get(i).getLastTrade());	
			}			
		} else {			
			_maxRange = Math.max(_maxRange, lastTrade);
			_minRange = Math.max(_minRange, lastTrade);
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
			upper = maxRange + (maxRange + minRange + 1) / 3;
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
			else if(lastSecondRecord.getStationaryCheck() != 0 && check == 0) stationaryCheck = stationaryCheck + 1;
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

}
