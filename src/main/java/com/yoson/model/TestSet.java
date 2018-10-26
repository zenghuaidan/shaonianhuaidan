package com.yoson.model;

import com.google.gson.annotations.Expose;

public class TestSet {
	@Expose
	private int tShort;
	@Expose
	private int tLong;
	@Expose
	private int mas;
	@Expose
	private int mal;
	@Expose
	private double mat;	
	@Expose
	private double stopLoss;
	@Expose
	private double tradeStopLoss;
	@Expose
	private double instantTradeStoploss;
	@Expose
	private double itsCounter;	
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

	public TestSet() {}

	public TestSet(int tShort, int tLong, int mas, int mal, double mat, double stopLoss, double tradeStopLoss,
			double instantTradeStoploss, double itsCounter, double unit,
			String marketStartTime, String lunchStartTimeFrom, String lunchStartTimeTo, String marketCloseTime,
			double cashPerIndexPoint, double tradingFee, double otherCostPerTrade, int lastNumberOfMinutesClearPosition,
			int lunchLastNumberOfMinutesClearPosition, boolean includeMorningData, int avgStep) {
		this.tShort = tShort;
		this.tLong = tLong;
		this.mas = mas;
		this.mal = mal;
		this.mat = mat;
		this.stopLoss = stopLoss;
		this.tradeStopLoss = tradeStopLoss;
		this.instantTradeStoploss = instantTradeStoploss;
		this.itsCounter = itsCounter;		
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
	}

	public int gettShort() {
		return tShort;
	}

	public void settShort(int tShort) {
		this.tShort = tShort;
	}

	public int gettLong() {
		return tLong;
	}

	public void settLong(int tLong) {
		this.tLong = tLong;
	}

	public int getMas() {
		return mas;
	}

	public void setMas(int mas) {
		this.mas = mas;
	}

	public int getMal() {
		return mal;
	}

	public void setMal(int mal) {
		this.mal = mal;
	}

	public double getMat() {
		return mat;
	}

	public void setMat(double mat) {
		this.mat = mat;
	}

	public double getStopLoss() {
		return stopLoss * unit;
	}
	
	public double getNegativeStopLoss() {
		return getStopLoss() * -1;
	}

	public void setStopLoss(double stopLoss) {
		this.stopLoss = stopLoss;
	}

	public double getTradeStopLoss() {
		return tradeStopLoss * unit;
	}

	public void setTradeStopLoss(double tradeStopLoss) {
		this.tradeStopLoss = tradeStopLoss;
	}

	public double getInstantTradeStoploss() {
		return instantTradeStoploss;
	}

	public void setInstantTradeStoploss(double instantTradeStoploss) {
		this.instantTradeStoploss = instantTradeStoploss;
	}

	public double getItsCounter() {
		return itsCounter;
	}

	public void setItsCounter(double itsCounter) {
		this.itsCounter = itsCounter;
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

	public double getUnit() {
		return unit;
	}

	public void setUnit(double unit) {
		this.unit = unit;
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

	public String getKey() {
		return gettShort() + "_" +
		gettLong() + "_" +
		getMas() + "_" +
		getMal() + "_" +
		getMat() + "_" +
		getStopLoss() + "_" +
		getTradeStopLoss() + "_" +
		getInstantTradeStoploss() + "_" +
		getItsCounter();
	}
}
