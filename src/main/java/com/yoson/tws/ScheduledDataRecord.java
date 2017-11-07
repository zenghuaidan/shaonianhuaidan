package com.yoson.tws;

public class ScheduledDataRecord implements Comparable<ScheduledDataRecord>{
	private String time;
	private double bidavg;
	private double bidlast;
	private double bidmax;
	private double bidmin;
	private double bidTotal;
	private int bidCount;
	
	private double askavg;
	private double asklast;
	private double askmax;
	private double askmin;
	private double askTotal;
	private int askCount;
	
	private double tradeavg;
	private double tradelast;
	private double trademax;
	private double trademin;
	private double tradeTotal;
	private int tradeCount;
	
	public ScheduledDataRecord()
	{
	}
	
	public ScheduledDataRecord(String time)
	{
		this.time = time;
	}
	
	public ScheduledDataRecord(String time, ScheduledDataRecord record, ScheduledDataRecord lastSecondRecord) {
		this.time = time;
		this.bidavg = lastSecondRecord != null && record.bidavg == 0 ? lastSecondRecord.getBidavg() : record.bidavg;
		this.bidlast = lastSecondRecord != null && record.bidlast == 0 ? lastSecondRecord.getBidlast() : record.bidlast;
		this.bidmin = lastSecondRecord != null && record.bidmin == 0 ? lastSecondRecord.getBidmin() : record.bidmin;
		this.bidmax = lastSecondRecord != null && record.bidmax == 0 ? lastSecondRecord.getBidmax() : record.bidmax;
		
		this.askavg = lastSecondRecord != null && record.askavg == 0 ? lastSecondRecord.getAskavg() : record.askavg;
		this.asklast = lastSecondRecord != null && record.asklast == 0 ? lastSecondRecord.getAsklast() : record.asklast;
		this.askmin = lastSecondRecord != null && record.askmin == 0 ? lastSecondRecord.getAskmin() : record.askmin;
		this.askmax = lastSecondRecord != null && record.askmax == 0 ? lastSecondRecord.getAskmax() : record.askmax;
		
		this.tradeavg = lastSecondRecord != null && record.tradeavg == 0 ? lastSecondRecord.getTradeavg() : record.tradeavg;
		this.tradelast = lastSecondRecord != null && record.tradelast == 0 ? lastSecondRecord.getTradelast() : record.tradelast;
		this.trademin = lastSecondRecord != null && record.trademin == 0 ? lastSecondRecord.getTrademin() : record.trademin;
		this.trademax = lastSecondRecord != null && record.trademax == 0 ? lastSecondRecord.getTrademax() : record.trademax;
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

	public double getBidTotal() {
		return bidTotal;
	}

	public void setBidTotal(double bidTotal) {
		this.bidTotal = bidTotal;
	}

	public int getBidCount() {
		return bidCount;
	}

	public void setBidCount(int bidCount) {
		this.bidCount = bidCount;
	}

	public double getAskTotal() {
		return askTotal;
	}

	public void setAskTotal(double askTotal) {
		this.askTotal = askTotal;
	}

	public int getAskCount() {
		return askCount;
	}

	public void setAskCount(int askCount) {
		this.askCount = askCount;
	}

	public double getTradeTotal() {
		return tradeTotal;
	}

	public void setTradeTotal(double tradeTotal) {
		this.tradeTotal = tradeTotal;
	}

	public int getTradeCount() {
		return tradeCount;
	}

	public void setTradeCount(int tradeCount) {
		this.tradeCount = tradeCount;
	}

	  
}
