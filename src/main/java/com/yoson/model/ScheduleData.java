package com.yoson.model;

import java.text.ParseException;
import java.util.Date;

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
	
	public ScheduleData() {
	}

	public ScheduleData(Date date, double askPrice, int askSize, double bidPrice, int bidSize, double lastTrade, int lastTradeSize, double actualAskPrice, double actualBidPrice, double actualLastTrade) {
		this.id = date.getTime();
		this.dateStr = DateUtils.yyyyMMdd().format(date);
		this.timeStr = DateUtils.HHmmss().format(date);
		this.askPrice = askPrice;
		this.bidPrice = bidPrice;
		this.lastTrade = lastTrade;
		this.askSize = askSize;
		this.bidSize = bidSize;
		this.lastTradeSize = lastTradeSize;
		this.actualAskPrice = actualAskPrice;
		this.actualBidPrice = actualBidPrice;
		this.actualLastTrade = actualLastTrade;
	}
	
	public ScheduleData(String dateStr, String timeStr, String askPrice, String bidPrice, String lastTrade, String actualAskPrice, String actualBidPrice, String actualLastTrade) {
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
	}
	
	public ScheduleData(String dateStr, String timeStr, Double askPrice, Double bidPrice, Double lastTrade, Double actualAskPrice, Double actualBidPrice, Double actualLastTrade) {
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
	
}
