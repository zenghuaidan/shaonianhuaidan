package com.yoson.date;

public class BrokenDate {
	public BrokenDate(){
		
	}
	public BrokenDate(String from, String to) {
		this.from = from;
		this.to = to;
	}

	public String from;
	public String to;
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	
	
}