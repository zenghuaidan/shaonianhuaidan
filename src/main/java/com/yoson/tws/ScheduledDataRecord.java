package com.yoson.tws;

public class ScheduledDataRecord implements Comparable<ScheduledDataRecord>{
	private String time;
	private double bidavg;
	private double bidlast;
	private double bidmax;
	private double bidmin;
	
	private double askavg;
	private double asklast;
	private double askmax;
	private double askmin;
	
	private double tradeavg;
	private double tradelast;
	private double trademax;
	private double trademin;
	
	public ScheduledDataRecord()
	{
	}
	
	public ScheduledDataRecord(String time)
	{
		this.time = time;
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}


	public double getBidavg() {
		return bidavg;
	}

	public void setBidavg(double bidavg) {
		this.bidavg = bidavg;
	}

	public double getBidlast() {
		return bidlast;
	}

	public void setBidlast(double bidlast) {
		this.bidlast = bidlast;
	}

	public double getBidmax() {
		return bidmax;
	}

	public void setBidmax(double bidmax) {
		this.bidmax = bidmax;
	}

	public double getBidmin() {
		return bidmin;
	}

	public void setBidmin(double bidmin) {
		this.bidmin = bidmin;
	}

	public double getAskavg() {
		return askavg;
	}

	public void setAskavg(double askavg) {
		this.askavg = askavg;
	}

	public double getAsklast() {
		return asklast;
	}

	public void setAsklast(double asklast) {
		this.asklast = asklast;
	}

	public double getAskmax() {
		return askmax;
	}

	public void setAskmax(double askmax) {
		this.askmax = askmax;
	}

	public double getAskmin() {
		return askmin;
	}

	public void setAskmin(double askmin) {
		this.askmin = askmin;
	}

	public double getTradeavg() {
		return tradeavg;
	}

	public void setTradeavg(double tradeavg) {
		this.tradeavg = tradeavg;
	}

	public double getTradelast() {
		return tradelast;
	}

	public void setTradelast(double tradelast) {
		this.tradelast = tradelast;
	}

	public double getTrademax() {
		return trademax;
	}

	public void setTrademax(double trademax) {
		this.trademax = trademax;
	}

	public double getTrademin() {
		return trademin;
	}

	public void setTrademin(double trademin) {
		this.trademin = trademin;
	}

	@Override
	public int compareTo(ScheduledDataRecord arg0) {
		return Long.compare(Long.parseLong(this.time), Long.parseLong(arg0.time));
	}

	  
}
