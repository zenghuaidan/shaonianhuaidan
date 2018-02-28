package com.yoson.tws;

import java.util.Date;

public class Record{
	private Date time;
	private double data;
	private int size;
	private String type;
	
	public Record(Date time, double data, int size, String type) {
		this.time = time;
		this.data = data;
		this.size = size;
		this.type = type;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}