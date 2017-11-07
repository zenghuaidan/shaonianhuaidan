package com.yoson.model;

import com.google.gson.annotations.Expose;

public class TestSet {
	@Expose
	private int cpTimer;
	@Expose
	private int cpBuffer;
	@Expose
	private int cpHitRate;
	@Expose
	private int cpSmooth;
	@Expose
	private int estimationBuffer;
	@Expose
	private int actionTrigger;
	@Expose
	private int actionCounting;
	@Expose
	private int tradeStopLossTrigger;
	@Expose
	private double tradeStopLossTriggerPercent;
	@Expose
	private int absoluteTradeStopLoss;
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

	public TestSet() {}

	public TestSet(int cpTimer, int cpBuffer, int cpHitRate, int cpSmooth, int estimationBuffer, int actionTrigger,
			int actionCounting, int tradeStopLossTrigger, double tradeStopLossTriggerPercent, int absoluteTradeStopLoss, double unit,
			String marketStartTime, String lunchStartTimeFrom, String lunchStartTimeTo, String marketCloseTime,
			double cashPerIndexPoint, double tradingFee, double otherCostPerTrade, int lastNumberOfMinutesClearPosition,
			int lunchLastNumberOfMinutesClearPosition) {
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
	}

	public int getCpTimer() {
		return cpTimer;
	}

	public void setCpTimer(int cpTimer) {
		this.cpTimer = cpTimer;
	}

	public int getCpBuffer() {
		return cpBuffer;
	}

	public void setCpBuffer(int cpBuffer) {
		this.cpBuffer = cpBuffer;
	}

	public int getCpHitRate() {
		return cpHitRate;
	}

	public void setCpHitRate(int cpHitRate) {
		this.cpHitRate = cpHitRate;
	}

	public int getCpSmooth() {
		return cpSmooth;
	}

	public void setCpSmooth(int cpSmooth) {
		this.cpSmooth = cpSmooth;
	}

	public int getEstimationBuffer() {
		return estimationBuffer;
	}

	public void setEstimationBuffer(int estimationBuffer) {
		this.estimationBuffer = estimationBuffer;
	}

	public int getActionTrigger() {
		return actionTrigger;
	}

	public void setActionTrigger(int actionTrigger) {
		this.actionTrigger = actionTrigger;
	}

	public int getActionCounting() {
		return actionCounting;
	}

	public void setActionCounting(int actionCounting) {
		this.actionCounting = actionCounting;
	}

	public int getTradeStopLossTrigger() {
		return tradeStopLossTrigger;
	}

	public void setTradeStopLossTrigger(int tradeStopLossTrigger) {
		this.tradeStopLossTrigger = tradeStopLossTrigger;
	}

	public double getTradeStopLossTriggerPercent() {
		return tradeStopLossTriggerPercent;
	}

	public void setTradeStopLossTriggerPercent(double tradeStopLossTriggerPercent) {
		this.tradeStopLossTriggerPercent = tradeStopLossTriggerPercent;
	}

	public int getAbsoluteTradeStopLoss() {
		return absoluteTradeStopLoss;
	}

	public void setAbsoluteTradeStopLoss(int absoluteTradeStopLoss) {
		this.absoluteTradeStopLoss = absoluteTradeStopLoss;
	}

	public String getMarketStartTime() {
		return marketStartTime;
	}

	public void setMarketStartTime(String marketStartTime) {
		this.marketStartTime = marketStartTime;
	}

	public String getLunchStartTimeFrom() {
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
		return unit;
	}

	public void setUnit(double unit) {
		this.unit = unit;
	}

}
