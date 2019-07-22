package com.yoson.model;

import com.google.gson.annotations.Expose;

public class TestSet {
	@Expose
	private int oc;
	@Expose
	private int cpTimer;
	@Expose
	private double cpBuffer;
	@Expose
	private int cpHitRate;
	@Expose
	private double cpSmooth;
	@Expose
	private double estimationBuffer;
	@Expose
	private double actionTrigger;
	@Expose
	private int actionCounting;
	@Expose
	private double tradeStopLossTrigger;
	@Expose
	private double tradeStopLossTriggerPercent;
	@Expose
	private double absoluteTradeStopLoss;
	@Expose
	private double unit;
	@Expose
	private String marketStartTime;
	@Expose
	private String lunchStartTimeFrom;
	@Expose
	private String lunchStartTimeTo;
	@Expose
	private String marketCloseTime;
	@Expose
	private double cashPerIndexPoint;
	@Expose
	private double tradingFee;
	@Expose
	private double otherCostPerTrade;
	@Expose
	private int lastNumberOfMinutesClearPosition;
	@Expose
	private int lunchLastNumberOfMinutesClearPosition;	
	@Expose
	private boolean includeMorningData;
	@Expose
	private int avgStep;
	@Expose
	private boolean includeLastMarketDayData;
	@Expose
	private boolean ignoreLunchTime;

	public TestSet() {}

	public TestSet(int oc, int cpTimer, double cpBuffer, int cpHitRate, double cpSmooth, double estimationBuffer, double actionTrigger,
			int actionCounting, double tradeStopLossTrigger, double tradeStopLossTriggerPercent, double absoluteTradeStopLoss, double unit,
			String marketStartTime, String lunchStartTimeFrom, String lunchStartTimeTo, String marketCloseTime,
			double cashPerIndexPoint, double tradingFee, double otherCostPerTrade, int lastNumberOfMinutesClearPosition,
			int lunchLastNumberOfMinutesClearPosition, boolean includeMorningData, int avgStep, boolean includeLastMarketDayData, boolean ignoreLunchTime) {
		this.oc = oc;
		this.cpTimer = cpTimer;
		this.cpBuffer = cpBuffer;
		this.cpHitRate = cpHitRate;
		this.cpSmooth = cpSmooth;
		this.estimationBuffer = estimationBuffer;
		this.actionTrigger = actionTrigger;
		this.actionCounting = actionCounting;
		this.tradeStopLossTrigger = tradeStopLossTrigger;
		this.tradeStopLossTriggerPercent = tradeStopLossTriggerPercent;
		this.absoluteTradeStopLoss = absoluteTradeStopLoss;
		this.unit = unit;
		
		this.marketStartTime = marketStartTime;
		this.lunchStartTimeFrom = lunchStartTimeFrom;
		this.lunchStartTimeTo = lunchStartTimeTo;
		this.marketCloseTime = marketCloseTime;
		this.cashPerIndexPoint = cashPerIndexPoint;
		this.tradingFee = tradingFee;
		this.otherCostPerTrade = otherCostPerTrade;
		this.lastNumberOfMinutesClearPosition = lastNumberOfMinutesClearPosition;
		this.lunchLastNumberOfMinutesClearPosition = lunchLastNumberOfMinutesClearPosition;
		this.includeMorningData = includeMorningData;
		this.avgStep = avgStep;
		this.includeLastMarketDayData = includeLastMarketDayData;
		this.ignoreLunchTime = ignoreLunchTime;
	}

	public int getOc() {
		return oc;
	}

	public void setOc(int oc) {
		this.oc = oc;
	}

	public int getCpTimer() {
		return cpTimer;
	}

	public void setCpTimer(int cpTimer) {
		this.cpTimer = cpTimer;
	}

	public double getCpBuffer() {
		return cpBuffer;
	}

	public void setCpBuffer(double cpBuffer) {
		this.cpBuffer = cpBuffer;
	}

	public int getCpHitRate() {
		return cpHitRate;
	}

	public void setCpHitRate(int cpHitRate) {
		this.cpHitRate = cpHitRate;
	}

	public double getCpSmooth() {
		return cpSmooth;
	}

	public void setCpSmooth(double cpSmooth) {
		this.cpSmooth = cpSmooth;
	}

	public double getEstimationBuffer() {
		return estimationBuffer;
	}

	public void setEstimationBuffer(double estimationBuffer) {
		this.estimationBuffer = estimationBuffer;
	}

	public double getActionTrigger() {
		return actionTrigger;
	}

	public void setActionTrigger(double actionTrigger) {
		this.actionTrigger = actionTrigger;
	}

	public int getActionCounting() {
		return actionCounting;
	}

	public void setActionCounting(int actionCounting) {
		this.actionCounting = actionCounting;
	}

	public double getTradeStopLossTrigger() {
		return tradeStopLossTrigger;
	}

	public void setTradeStopLossTrigger(double tradeStopLossTrigger) {
		this.tradeStopLossTrigger = tradeStopLossTrigger;
	}

	public double getTradeStopLossTriggerPercent() {
		return tradeStopLossTriggerPercent;
	}

	public void setTradeStopLossTriggerPercent(double tradeStopLossTriggerPercent) {
		this.tradeStopLossTriggerPercent = tradeStopLossTriggerPercent;
	}

	public double getAbsoluteTradeStopLoss() {
		return absoluteTradeStopLoss;
	}

	public void setAbsoluteTradeStopLoss(double absoluteTradeStopLoss) {
		this.absoluteTradeStopLoss = absoluteTradeStopLoss;
	}

	public String getMarketStartTime() {
		return marketStartTime;
	}

	public void setMarketStartTime(String marketStartTime) {
		this.marketStartTime = marketStartTime;
	}

	public String getLunchStartTimeFrom() {
		if(ignoreLunchTime) return lunchStartTimeTo;
		return lunchStartTimeFrom;
	}

	public void setLunchStartTimeFrom(String lunchStartTimeFrom) {
		this.lunchStartTimeFrom = lunchStartTimeFrom;
	}

	public String getLunchStartTimeTo() {
		return lunchStartTimeTo;
	}

	public void setLunchStartTimeTo(String lunchStartTimeTo) {
		this.lunchStartTimeTo = lunchStartTimeTo;
	}

	public String getMarketCloseTime() {
		return marketCloseTime;
	}

	public void setMarketCloseTime(String marketCloseTime) {
		this.marketCloseTime = marketCloseTime;
	}

	public double getCashPerIndexPoint() {
		return cashPerIndexPoint;
	}

	public void setCashPerIndexPoint(double cashPerIndexPoint) {
		this.cashPerIndexPoint = cashPerIndexPoint;
	}

	public double getTradingFee() {
		return tradingFee;
	}

	public void setTradingFee(double tradingFee) {
		this.tradingFee = tradingFee;
	}

	public double getOtherCostPerTrade() {
		return otherCostPerTrade;
	}

	public void setOtherCostPerTrade(double otherCostPerTrade) {
		this.otherCostPerTrade = otherCostPerTrade;
	}

	public int getLastNumberOfMinutesClearPosition() {
		return lastNumberOfMinutesClearPosition;
	}

	public void setLastNumberOfMinutesClearPosition(int lastNumberOfMinutesClearPosition) {
		this.lastNumberOfMinutesClearPosition = lastNumberOfMinutesClearPosition;
	}

	public int getLunchLastNumberOfMinutesClearPosition() {
		return lunchLastNumberOfMinutesClearPosition;
	}

	public void setLunchLastNumberOfMinutesClearPosition(int lunchLastNumberOfMinutesClearPosition) {
		this.lunchLastNumberOfMinutesClearPosition = lunchLastNumberOfMinutesClearPosition;
	}

	public double getUnit() {
		return unit == 0 ? 1 : unit;
	}

	public void setUnit(double unit) {
		this.unit = unit;
	}

	public boolean isIncludeMorningData() {
		return includeMorningData;
	}

	public void setIncludeMorningData(boolean includeMorningData) {
		this.includeMorningData = includeMorningData;
	}
	
	public int getAvgStep() {
		return avgStep;
	}

	public void setAvgStep(int avgStep) {
		this.avgStep = avgStep;
	}

	public boolean isIncludeLastMarketDayData() {
		return includeLastMarketDayData;
	}

	public void setIncludeLastMarketDayData(boolean includeLastMarketDayData) {
		this.includeLastMarketDayData = includeLastMarketDayData;
	}
	
	public boolean isIgnoreLunchTime() {
		return ignoreLunchTime;
	}

	public void setIgnoreLunchTime(boolean ignoreLunchTime) {
		this.ignoreLunchTime = ignoreLunchTime;
	}
	public String getKey() {
		return getOc()  +  "_" +
		getCpTimer()  +  "_" +
		getCpBuffer() + "_" +
		getCpHitRate() + "_" +
		getCpSmooth() + "_" +
		getEstimationBuffer() + "_" +
		getActionTrigger() + "_" +
		getActionCounting() + "_" +
		getTradeStopLossTrigger() + "_" +
		getTradeStopLossTriggerPercent() + "_" +
		getAbsoluteTradeStopLoss();
	}
	
}
