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
	
	private int cpCounting;
	private double cp;
	private double cps;
	private int cpAccount;
	private double cpsAverage;
	private double cpsToatl;
	private double previousMaxCPAC;
	private int countingAfterCP;
	private double est;
	private String offOn;
	private int preAction;
	private int action;
	private int smoothAction;
	private double position;
	private int posCounting;
	private double pnl;
	private int tradeCount;
	private int totalTrade;
	private double totalPnl;
	private double mtm;
	private double maxMtm;
	private int pc;
	
	public PerSecondRecord() {
	}
	
	public PerSecondRecord(List<ScheduleData> dailyScheduleData, TestSet testSet, List<PerSecondRecord> dailyPerSecondRecordList, ScheduleData scheduleDataPerSecond, int checkMarketTime) throws ParseException {
		this(dailyScheduleData, testSet, dailyPerSecondRecordList, scheduleDataPerSecond, checkMarketTime, null);
	}
	
	public PerSecondRecord(List<ScheduleData> dailyScheduleData, TestSet testSet, List<PerSecondRecord> dailyPerSecondRecordList, ScheduleData scheduleDataPerSecond, int checkMarketTime, Map<Integer, Integer> lastTradeMap) throws ParseException {
		PerSecondRecord lastSecondRecord = dailyPerSecondRecordList.size() == 0 ? new PerSecondRecord() : dailyPerSecondRecordList.get(dailyPerSecondRecordList.size() - 1);
		this.time = scheduleDataPerSecond.getId();
		this.timeStr = scheduleDataPerSecond.getDateTimeStr();
		this.askPrice = scheduleDataPerSecond.getAskPrice();
		this.bidPrice = scheduleDataPerSecond.getBidPrice();
		this.lastTrade = scheduleDataPerSecond.getLastTrade();
		this.reference = lastSecondRecord.getReference() + 1;
		//2015-01-19  9:41:16
//		if ("2015-01-19 15:45:00".equals(DateUtils.yyyyMMddHHmmss().format(new Date(time)))) {
//			System.out.println("debug point");
//		}
		this.checkMarketTime = checkMarketTime;
		initCPCounting(dailyScheduleData, testSet, lastSecondRecord, lastTradeMap);
		initCP(testSet);
		initCPS(lastSecondRecord, testSet);
		initCPAccount(lastSecondRecord);
		initCPSAverageAndPreviousMaxCPAC(dailyScheduleData, dailyPerSecondRecordList, lastSecondRecord);
		initCountingAfterCP(lastSecondRecord);
		initEst(lastSecondRecord, testSet);
		initOffOn(lastSecondRecord);
		initAction(lastSecondRecord, testSet);
		initPreAction(lastSecondRecord);
		initSmoothAction(lastSecondRecord, testSet);
		initPosition(lastSecondRecord);
		initMtm();
		initPosCounting(lastSecondRecord);
		initMaxMtm(dailyPerSecondRecordList, lastSecondRecord);
		initPnl(lastSecondRecord);
		initTotalPnl(lastSecondRecord);
		initTradeCount(lastSecondRecord);
		initTotalTrades(lastSecondRecord);
		initPc(lastSecondRecord);
	}
	
	public void initCPCounting(List<ScheduleData> dailyScheduleData, TestSet testSet, PerSecondRecord lastSecondRecord, Map<Integer, Integer> lastTradeMap) {
//				if (this.reference > testSet.getCpTimer()) {
//					for(int i = this.reference - testSet.getCpTimer(); i < this.reference - 1; i++) {
//						if(dailyScheduleData.get(i).getLastTrade() >= this.lastTrade - testSet.getCpBuffer() && dailyScheduleData.get(i).getLastTrade() <= this.lastTrade + testSet.getCpBuffer()) {
//							this.cpCounting++;
//						}
//					}
//					this.cpCounting++;
//				}
		
		if(lastTradeMap != null) {
			int lastTradeInt = (int)this.lastTrade;
			if(lastTradeMap.containsKey(lastTradeInt)) lastTradeMap.replace(lastTradeInt, lastTradeMap.get(lastTradeInt) + 1);
			else lastTradeMap.put(lastTradeInt, 1);			
			if (this.reference > testSet.getCpTimer()) {
				int obsoluteLastTrade = (int)dailyScheduleData.get(this.reference - testSet.getCpTimer() - 1).getLastTrade();				
				lastTradeMap.replace(obsoluteLastTrade, lastTradeMap.get(obsoluteLastTrade) - 1);
				for(int i = lastTradeInt - testSet.getCpBuffer(); i <= lastTradeInt + testSet.getCpBuffer(); i++) {
					if(lastTradeMap.containsKey(i)) {
						this.cpCounting += lastTradeMap.get(i);
					}
				}
			}
		} else {			
			if (this.reference > testSet.getCpTimer()) {
				if(this.lastTrade == lastSecondRecord.lastTrade) {
					this.cpCounting = lastSecondRecord.cpCounting;
					int index = this.reference - testSet.getCpTimer() - 1;
					if(index >= 0 && isWithinCpBuffer(dailyScheduleData.get(index).getLastTrade(), this.lastTrade, testSet.getCpBuffer())) {
						this.cpCounting--;
					}
				} else {
					for(int i = this.reference - testSet.getCpTimer(), j = this.reference - 2; i <= j; i++,j--) {
						if(isWithinCpBuffer(dailyScheduleData.get(i).getLastTrade(), this.lastTrade, testSet.getCpBuffer())) {
							this.cpCounting++;
						}
						if(i!=j && isWithinCpBuffer(dailyScheduleData.get(j).getLastTrade(), this.lastTrade, testSet.getCpBuffer())) {
							this.cpCounting++;
						}
					}
				}
				this.cpCounting++;
			}			
		}
		
		
	}
	
	public boolean isWithinCpBuffer(double lastTrade, double currentLastTrade, double cpBuffer){
		return lastTrade >= currentLastTrade - cpBuffer && lastTrade <= currentLastTrade + cpBuffer;
	}
	
	public void initCP(TestSet testSet) {
		if (this.cpCounting != 0 && this.cpCounting >= testSet.getCpHitRate()) {
			this.cp = this.lastTrade;
		}
	}
	
	public void initCPS(PerSecondRecord lastSecondRecord, TestSet testSet) {
		if(this.cp != 0) {
			this.cps = cp;
		} else if (lastSecondRecord.getCps() != 0 && Math.abs(this.lastTrade - lastSecondRecord.getCps()) > testSet.getCpSmooth()) {
			this.cps = 0;
		} else {
			this.cps = lastSecondRecord.getCps();
		}
	}
	
	public void initCPAccount(PerSecondRecord lastSecondRecord) {
		if (lastSecondRecord.getCps() == 0 && this.cps != 0) {
			this.cpAccount = 1;
		} else if (lastSecondRecord.getCps() != 0 && this.cps != 0) {
			this.cpAccount = lastSecondRecord.getCpAccount() + 1;
		}
	}
	
	public void initCPSAverageAndPreviousMaxCPAC(List<ScheduleData> dailyScheduleData, List<PerSecondRecord> dailyPerSecondRecordList, PerSecondRecord lastSecondRecord) {
		if (this.cpAccount == 0) {
			this.cpsAverage = lastSecondRecord.getCpsAverage();
			this.previousMaxCPAC = lastSecondRecord.getPreviousMaxCPAC();
		} else {
//			this.previousMaxCPAC = this.cpAccount;
//			double total = this.lastTrade;
//			int count = 1;
//			for(int i = this.reference - this.cpAccount; i < this.reference - 1; i++) {
//				this.previousMaxCPAC = Math.max(this.previousMaxCPAC, dailyPerSecondRecordList.get(i).getCpAccount());
//				total += dailyScheduleData.get(i).getLastTrade();
//				count++;
//			}
//			this.cpsAverage = total / count;
			
			this.previousMaxCPAC = this.cpAccount;
			if(lastSecondRecord.getCpAccount() != 0 && this.cpAccount == (lastSecondRecord.getCpAccount() + 1)) {
				this.cpsToatl = lastSecondRecord.getCpsToatl() + this.lastTrade;
				this.previousMaxCPAC = Math.max(this.previousMaxCPAC, lastSecondRecord.getPreviousMaxCPAC());
			} else {
				this.cpsToatl = this.lastTrade;
				for(int i = this.reference - this.cpAccount; i < this.reference - 1; i++) {
					this.previousMaxCPAC = Math.max(this.previousMaxCPAC, dailyPerSecondRecordList.get(i).getCpAccount());
					this.cpsToatl += dailyScheduleData.get(i).getLastTrade();
				}
			}
			this.cpsAverage = this.cpsToatl / this.cpAccount;
		}
	}

	public void initCountingAfterCP(PerSecondRecord lastSecondRecord) {
		if (this.cps != 0) {
			this.countingAfterCP = 0;
		} else if(lastSecondRecord.getCountingAfterCP() != 0) {
			this.countingAfterCP += lastSecondRecord.getCountingAfterCP() + 1;
		} else if (lastSecondRecord.getCps() != 0) {
			this.countingAfterCP = 1;
		}
	}
	
	public void initEst(PerSecondRecord lastSecondRecord, TestSet testSet) {
		if (this.cpsAverage !=0 && lastSecondRecord.getCountingAfterCP() != 0) {
			if(this.cpsAverage != lastSecondRecord.getCpsAverage() 
					&& lastSecondRecord.getCpsAverage() != 0
					&& this.cpsAverage != 0 && Math.abs(this.cpsAverage - lastSecondRecord.getCpsAverage()) >= testSet.getEstimationBuffer()) {
				this.est = (lastSecondRecord.getCountingAfterCP() + this.countingAfterCP) * (this.getCpsAverage() - lastSecondRecord.getCpsAverage()) / lastSecondRecord.getCountingAfterCP() + lastSecondRecord.getCpsAverage(); 
			} else {
				this.est = lastSecondRecord.getEst();
			}
		}
	}
	
	public static String ON = "ON";
	public static String OFF = "OFF";
	public void initOffOn(PerSecondRecord lastSecondRecord) {
		if (this.countingAfterCP == 0) {
			this.offOn = OFF;
		} else if (lastSecondRecord.getAction() == 1 || lastSecondRecord.getAction() == -1) {
			this.offOn = OFF;
		} else if(lastSecondRecord.getPreviousMaxCPAC() == this.getPreviousMaxCPAC() && lastSecondRecord.getCountingAfterCP() == 0) {
			this.offOn = ON;
		} else {
			this.offOn = lastSecondRecord.getOffOn();
		}
	}
	
	public void initAction(PerSecondRecord lastSecondRecord, TestSet testSet) {
		if(this.est == 0 || this.offOn.equals(OFF)) {
			this.action = 0;
		} else if (this.countingAfterCP >= testSet.getActionCounting() 
				&& (this.lastTrade - this.cpsAverage > testSet.getActionTrigger()) 
				&& lastSecondRecord.getPreAction() != 1) {
			this.action = 1;
		} else if(this.countingAfterCP >= testSet.getActionCounting() 
				&& (this.cpsAverage - this.lastTrade) >= testSet.getActionTrigger() 
				&& lastSecondRecord.getPreAction() != -1) {
			this.action = -1;
		}
	}
	
	public void initPreAction(PerSecondRecord lastSecondRecord) {
		if(this.cps != 0) {
			this.preAction = 0;
		} else if (this.action == 0) {
			this.preAction = lastSecondRecord.getPreAction();
		} else if (this.action == 1) {
			this.preAction = 1;
		} else if(this.action == -1) {
			this.preAction = -1;
		} else {
			this.preAction = lastSecondRecord.getPreAction();
		}
	}
	
	public void initSmoothAction(PerSecondRecord lastSecondRecord, TestSet testSet) {
		if (this.checkMarketTime == 0) {
			this.smoothAction = 0;
		} else if (lastSecondRecord.getMtm() < 0 && lastSecondRecord.getMtm() >= -testSet.getAbsoluteTradeStopLoss() && lastSecondRecord.getSmoothAction() != 0) {
			this.smoothAction = lastSecondRecord.getSmoothAction();
		} else if(lastSecondRecord.getSmoothAction() == 1 && this.action == 0 
				&& (lastSecondRecord.getMaxMtm() >= testSet.getTradeStopLossTrigger() 
				&& (this.lastTrade - lastSecondRecord.getPosition()) >= (1 - testSet.getTradeStopLossTriggerPercent())*lastSecondRecord.getMaxMtm()
				|| lastSecondRecord.getMaxMtm() < testSet.getTradeStopLossTrigger()
				&& (this.lastTrade - lastSecondRecord.getPosition()) >= lastSecondRecord.getMaxMtm() - testSet.getAbsoluteTradeStopLoss())) {
			this.smoothAction = 1;
		} else if (lastSecondRecord.getSmoothAction() == -1 && this.action == 0
				&& (lastSecondRecord.getMaxMtm() >= testSet.getTradeStopLossTrigger() 
				&& (lastSecondRecord.getPosition() - this.lastTrade) >= (1 - testSet.getTradeStopLossTriggerPercent())*lastSecondRecord.getMaxMtm()
				|| lastSecondRecord.getMaxMtm() < testSet.getTradeStopLossTrigger()
				&& (lastSecondRecord.getPosition() - this.lastTrade) >= lastSecondRecord.getMaxMtm() - testSet.getAbsoluteTradeStopLoss())) {
			this.smoothAction = -1;
		} else if(lastSecondRecord.getSmoothAction() == 0 && this.action != 0 
				|| lastSecondRecord.getSmoothAction() == this.action 
				|| lastSecondRecord.getSmoothAction() != 0 && this.action != 0) {
			this.smoothAction = this.action;
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
	
	public void initMtm() {
		if (this.position == 0) {
			this.mtm = 0;
		} else if(this.smoothAction == 1) {
			this.mtm = this.lastTrade - this.position;
		} else if (this.smoothAction == -1) {
			this.mtm = this.position - this.lastTrade;
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
	
	public void initTotalPnl(PerSecondRecord lastSecondRecord) {
		this.totalPnl = lastSecondRecord.totalPnl + this.pnl;
	}
	
	public void initTradeCount(PerSecondRecord lastSecondRecord) {
		this.tradeCount = Math.abs(this.smoothAction - lastSecondRecord.getSmoothAction());		
	}
	
	public void initTotalTrades(PerSecondRecord lastSecondRecord) {
		this.totalTrade = lastSecondRecord.totalTrade + this.tradeCount; 
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

	public int getCpCounting() {
		return cpCounting;
	}

	public void setCpCounting(int cpCounting) {
		this.cpCounting = cpCounting;
	}

	public double getCp() {
		return cp;
	}

	public void setCp(double cp) {
		this.cp = cp;
	}

	public double getCps() {
		return cps;
	}

	public void setCps(double cps) {
		this.cps = cps;
	}

	public int getCpAccount() {
		return cpAccount;
	}

	public void setCpAccount(int cpAccount) {
		this.cpAccount = cpAccount;
	}

	public double getCpsAverage() {
		return cpsAverage;
	}

	public void setCpsAverage(double cpsAverage) {
		this.cpsAverage = cpsAverage;
	}

	public double getCpsToatl() {
		return cpsToatl;
	}

	public void setCpsToatl(double cpsToatl) {
		this.cpsToatl = cpsToatl;
	}

	public double getPreviousMaxCPAC() {
		return previousMaxCPAC;
	}

	public void setPreviousMaxCPAC(double previousMaxCPAC) {
		this.previousMaxCPAC = previousMaxCPAC;
	}

	public int getCountingAfterCP() {
		return countingAfterCP;
	}

	public void setCountingAfterCP(int countingAfterCP) {
		this.countingAfterCP = countingAfterCP;
	}

	public double getEst() {
		return est;
	}

	public void setEst(double est) {
		this.est = est;
	}

	public String getOffOn() {
		return offOn;
	}

	public void setOffOn(String offOn) {
		this.offOn = offOn;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public int getPreAction() {
		return preAction;
	}

	public void setPreAction(int preAction) {
		this.preAction = preAction;
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

	public int getPc() {
		return pc;
	}

	public void setPc(int pc) {
		this.pc = pc;
	}

}
