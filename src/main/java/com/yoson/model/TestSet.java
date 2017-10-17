package com.yoson.model;

public class TestSet {
	private int tShort;
	private int tLong;
	private int tLong2;
	private double hld;
	private double stopLoss;
	private double tradeStopLoss;
	private double instantTradeStoploss;
	private double itsCounter;
	private double stopGainPercent;
	private double stopGainTrigger;
	private double unit;
	
	private String marketStartTime;
	private String lunchStartTimeFrom;
	private String lunchStartTimeTo;
	private String marketCloseTime;

	private double cashPerIndexPoint;
	private double tradingFee;
	private double otherCostPerTrade;
	
	private int lastNumberOfMinutesClearPosition;
	private int lunchLastNumberOfMinutesClearPosition;

	public TestSet() {}

	public TestSet(int tShort, int tLong, int tLong2, double hld, double stopLoss, double tradeStopLoss,
			double instantTradeStoploss, double itsCounter, double stopGainPercent, double stopGainTrigger, double unit,
			String marketStartTime, String lunchStartTimeFrom, String lunchStartTimeTo, String marketCloseTime,
			double cashPerIndexPoint, double tradingFee, double otherCostPerTrade, int lastNumberOfMinutesClearPosition,
			int lunchLastNumberOfMinutesClearPosition) {
		this.tShort = tShort;
		this.tLong = tLong;
		this.tLong2 = tLong2;
		this.hld = hld;
		this.stopLoss = stopLoss;
		this.tradeStopLoss = tradeStopLoss;
		this.instantTradeStoploss = instantTradeStoploss;
		this.itsCounter = itsCounter;
		this.stopGainPercent = stopGainPercent;
		this.stopGainTrigger = stopGainTrigger;
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

	public int gettLong2() {
		return tLong2;
	}

	public void settLong2(int tLong2) {
		this.tLong2 = tLong2;
	}

	public double getHld() {
		return hld;
	}

	public void setHld(double hld) {
		this.hld = hld;
	}

	public double getStopLoss() {
		return stopLoss * unit;
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

	public double getStopGainPercent() {
		return stopGainPercent;
	}

	public void setStopGainPercent(double stopGainPercent) {
		this.stopGainPercent = stopGainPercent;
	}

	public double getStopGainTrigger() {
		return stopGainTrigger * unit;
	}

	public void setStopGainTrigger(double stopGainTrigger) {
		this.stopGainTrigger = stopGainTrigger;
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

}
