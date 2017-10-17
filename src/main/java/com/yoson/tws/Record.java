package com.yoson.tws;

import java.util.Date;

public class Record{
	private Date time;
	private double data;
	private int size;
	
	public Record(Date time, double data, int size) {
		this.time = time;
		this.data = data;
		this.size = size;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public double getData() {
		return data;
	}
	public void setData(double data) {
		this.data = data;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	
}