package com.yoson.tws;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.ib.client.Order;
import com.yoson.date.DateUtils;
import com.yoson.model.MainUIParam;

public class Strategy implements Serializable {
	@Expose
	private String strategyName;
	private boolean active = false;
	private int tradeCount;
	private double pnl;
	@Expose
	private MainUIParam mainUIParam;
	private Map<Integer, Order> orderMap;
	private Map<Integer, Integer> orderCountMap;
	private Set<Integer> cancelOrder;
	private Date orderTime;

	public String getStrategyName() {
		return strategyName;
	}

	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}

	public MainUIParam getMainUIParam() {
		return mainUIParam;
	}

	public void setMainUIParam(MainUIParam mainUIParam) {
		this.mainUIParam = mainUIParam;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void active() {
		if(!active) {
			this.active = true;		
			this.orderMap = new TreeMap<Integer, Order>(new Comparator<Integer>(){  
	            public int compare(Integer o1, Integer o2) {  
	                return o2.compareTo(o1);  
	            }     
	        });
			this.cancelOrder = new HashSet<Integer>();
			this.orderCountMap = new HashMap<Integer, Integer>();
		}
	}

	public void inactive() {
		this.active = false;
		this.orderMap = null;
		this.cancelOrder = null;
		this.orderCountMap = null;
		this.orderTime = null;
	}

	public int getTradeCount() {
		return tradeCount;
	}

	public void setTradeCount(int tradeCount) {
		this.tradeCount = tradeCount;
	}

	public double getPnl() {
		return pnl;
	}

	public void setPnl(double pnl) {
		this.pnl = pnl;
	}

	@JsonIgnore
	public Map<Integer, Order> getOrderMap() {
		return orderMap;
	}

	public void setOrderMap(Map<Integer, Order> orderMap) {
		this.orderMap = orderMap;
	}

	public int getOrderId() {
		if(orderMap == null) return 0;
		for(int orderId : orderMap.keySet()) {
			return orderId;
		}
		return 0;
	}
	
	public int getQuantity() {
		if(orderMap == null) return 0;
		for(int orderId : orderMap.keySet()) {
			return orderMap.get(orderId).m_totalQuantity;
		}
		return 0;
	}
	
	public String getAction() {
		if(orderMap == null) return "";
		for(int orderId : orderMap.keySet()) {
			return orderMap.get(orderId).m_action;
		}
		return "";
	}

	public String getOrderTime() {
		if (orderTime == null)
			return "";
		return DateUtils.yyyyMMddHHmmss().format(orderTime);
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public Set<Integer> getCancelOrder() {
		return cancelOrder;
	}

	public void setCancelOrder(Set<Integer> cancelOrder) {
		this.cancelOrder = cancelOrder;
	}

	public Map<Integer, Integer> getOrderCountMap() {
		return orderCountMap;
	}

	public void setOrderCountMap(Map<Integer, Integer> orderCountMap) {
		this.orderCountMap = orderCountMap;
	}
	
}
