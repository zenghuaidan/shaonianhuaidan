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


	public ScheduleData(String dateStr, String timeStr, String askPrice, String bidPrice, String lastTrade) {
		try {
			this.id = DateUtils.yyyyMMddHHmmss.parse(dateStr + " " + timeStr).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.dateStr = dateStr;
		this.timeStr = timeStr;
		this.askPrice = Double.parseDouble(askPrice);
		this.bidPrice = Double.parseDouble(bidPrice);
		this.lastTrade = Double.parseDouble(lastTrade);
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
}
