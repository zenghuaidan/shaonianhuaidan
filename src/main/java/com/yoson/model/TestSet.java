package com.yoson.model;

import com.google.gson.annotations.Expose;

public class TestSet {
	@Expose
	private int timer;	
	@Expose
	private double smooth;
	@Expose
	private double action;	
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

	public TestSet() {}

	public TestSet(int timer, double smooth, double action, double absoluteTradeStopLoss, double unit,
			String marketStartTime, String lunchStartTimeFrom, String lunchStartTimeTo, String marketCloseTime,
			double cashPerIndexPoint, double tradingFee, double otherCostPerTrade, int lastNumberOfMinutesClearPosition,
			int lunchLastNumberOfMinutesClearPosition, boolean includeMorningData, int avgStep, boolean includeLastMarketDayData) {
		this.timer = timer;
		this.smooth = smooth;
		this.action = action;		
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
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public double getSmooth() {
		return smooth;
	}

	public void setSmooth(double smooth) {
		this.smooth = smooth;
	}

	public double getAction() {
		return action;
	}

	public void setAction(double action) {
		this.action = action;
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

	public String getKey() {
		return getTimer()  +  "_" +
		getSmooth() + "_" +
		getAction() + "_" +		
		getAbsoluteTradeStopLoss();
	}
	
}
