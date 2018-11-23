package com.yoson.model;

import java.text.ParseException;

import com.yoson.date.DateUtils;

public class ScheduleData {
	private long id;
	private String dateStr;
	private String timeStr;
	private double askPrice;
	private double bidPrice;
	private double lastTrade;
	private double actualAskPrice;
	private double actualBidPrice;
	private double actualLastTrade;
	private int askSize;
	private int bidSize;
	private int lastTradeSize;
	private String askDataField;
	private String bidDataField;
	private String tradeDataField;
	private boolean lastMarketDayData;
	
	public ScheduleData() {
	}
	
	public ScheduleData(String dateStr, String timeStr, String askPrice, String bidPrice, String lastTrade, String actualAskPrice, String actualBidPrice, String actualLastTrade, String askDataField, String bidDataField, String tradeDataField) {
		try {
			this.id = DateUtils.yyyyMMddHHmmss().parse(dateStr + " " + timeStr).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.dateStr = dateStr;
		this.timeStr = timeStr;
		this.askPrice = Double.parseDouble(askPrice);
		this.bidPrice = Double.parseDouble(bidPrice);
		this.lastTrade = Double.parseDouble(lastTrade);
		this.actualAskPrice = Double.parseDouble(actualAskPrice);
		this.actualBidPrice = Double.parseDouble(actualBidPrice);
		this.actualLastTrade = Double.parseDouble(actualLastTrade);
		this.askDataField = askDataField;
		this.bidDataField = bidDataField;
		this.tradeDataField = tradeDataField;
	}
	
	public ScheduleData(String dateStr, String timeStr, Double askPrice, Double bidPrice, Double lastTrade, Double actualAskPrice, Double actualBidPrice, Double actualLastTrade, String askDataField, String bidDataField, String tradeDataField) {
		try {
			this.id = DateUtils.yyyyMMddHHmmss().parse(dateStr + " " + timeStr).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.dateStr = dateStr;
		this.timeStr = timeStr;
		this.askPrice = askPrice;
		this.bidPrice = bidPrice;
		this.lastTrade = lastTrade;
		this.actualAskPrice = actualAskPrice;
		this.actualBidPrice = actualBidPrice;
		this.actualLastTrade = actualLastTrade;
		this.askDataField = askDataField;
		this.bidDataField = bidDataField;
		this.tradeDataField = tradeDataField;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getAskPrice() {
		return askPrice;
	}

	public void setAskPrice(double askPrice) {
		this.askPrice = askPrice;
	}

	public double getBidPrice() {
		return bidPrice;
	}

	public void setBidPrice(double bidPrice) {
		this.bidPrice = bidPrice;
	}

	public double getLastTrade() {
		return lastTrade;
	}

	public void setLastTrade(double lastTrade) {
		this.lastTrade = lastTrade;
	}
	
	public int getAskSize() {
		return askSize;
	}

	public void setAskSize(int askSize) {
		this.askSize = askSize;
	}

	public int getBidSize() {
		return bidSize;
	}

	public void setBidSize(int bidSize) {
		this.bidSize = bidSize;
	}

	public int getLastTradeSize() {
		return lastTradeSize;
	}

	public void setLastTradeSize(int lastTradeSize) {
		this.lastTradeSize = lastTradeSize;
	}

	public String getTimeStr() {
		return timeStr;
	}

	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}
	
	public String getDateStr() {
		return this.dateStr;
	}
	
	public String getDateTimeStr() {
		return this.dateStr + " " + this.timeStr;
	}
	
	public Long getTimeLong() {
		return Long.parseLong(this.dateStr.replaceAll("-", "") + this.timeStr.replaceAll(":", ""));
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

	public String getTradeDataField() {
		return tradeDataField;
	}

	public void setTradeDataField(String tradeDataField) {
		this.tradeDataField = tradeDataField;
	}

	public String getAskDataField() {
		return askDataField;
	}

	public void setAskDataField(String askDataField) {
		this.askDataField = askDataField;
	}

	public String getBidDataField() {
		return bidDataField;
	}

	public void setBidDataField(String bidDataField) {
		this.bidDataField = bidDataField;
	}

	public boolean isLastMarketDayData() {
		return lastMarketDayData;
	}

	public void setLastMarketDayData(boolean lastMarketDayData) {
		this.lastMarketDayData = lastMarketDayData;
	}
	
	public ScheduleData copyAndSetAsLastMarketDayData() {
		ScheduleData scheduleData = new ScheduleData();				
		scheduleData.id = this.id;
		scheduleData.dateStr = this.dateStr;
		scheduleData.timeStr = this.timeStr;
		scheduleData.askPrice = this.askPrice;
		scheduleData.bidPrice = this.bidPrice;
		scheduleData.lastTrade = this.lastTrade;
		scheduleData.actualAskPrice = this.actualAskPrice;
		scheduleData.actualBidPrice = this.actualBidPrice;
		scheduleData.actualLastTrade = this.actualLastTrade;
		scheduleData.askDataField = this.askDataField;
		scheduleData.bidDataField = this.bidDataField;
		scheduleData.tradeDataField = this.tradeDataField;		
		scheduleData.lastMarketDayData = true;
		return scheduleData;
	}
}
