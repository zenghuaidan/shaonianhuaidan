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
	private double morningPnl;
	@Expose
	private MainUIParam mainUIParam;
	// all the orders for this strategy
	private Map<Integer, Order> orderMap;
	// the order with the cancel count, key=orderId, value=cancelCount
	private Map<Integer, Integer> orderCountMap;
	// cancel order map by API
	private Set<Integer> cancelOrder;
	// action map, key=time, value=action
	private Map<Long, Integer> actoinMap;
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
			this.actoinMap = new HashMap<Long, Integer>();
			this.pnl = 0;
			this.morningPnl = 0;
			this.tradeCount = 0;
		}
	}

	public void inactive() {
		this.active = false;
		this.orderMap = null;
		this.cancelOrder = null;
		this.orderCountMap = null;
		this.orderTime = null;
		this.actoinMap = null;
		this.pnl = 0;
		this.morningPnl = 0;
		this.tradeCount = 0;
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

	public Map<Long, Integer> getActoinMap() {
		return actoinMap;
	}

	public void setActoinMap(Map<Long, Integer> actoinMap) {
		this.actoinMap = actoinMap;
	}

	public double getMorningPnl() {
		return morningPnl;
	}

	public void setMorningPnl(double morningPnl) {
		this.morningPnl = morningPnl;
	}
	
}
