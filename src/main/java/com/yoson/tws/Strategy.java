package com.yoson.tws;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.ib.client.Order;
import com.yoson.date.DateUtils;
import com.yoson.model.MainUIParam;

public class Strategy implements Serializable {
	@Expose
	private String strategyName;
	private boolean active = false;
	private long activeTime;
	private int tradeCount;
	private double pnl;
	private double morningPnl;
	@Expose
	private MainUIParam mainUIParam;
	// all the orders for this strategy
	private Map<Integer, Order> orderMap;
	// the order with the cancel count, key=orderId, value=cancelCount
	private Map<Integer, Integer> orderCountMap;
	// cancel order for retry
	private Map<Integer, Boolean> cancelOrder;
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

	public long getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(long activeTime) {
		this.activeTime = activeTime;
	}

	public void active() {
		if(!active) {
			this.active = true;		
			this.orderMap = new ConcurrentHashMap<Integer, Order>();
			this.cancelOrder = new ConcurrentHashMap<Integer, Boolean>();
			this.orderCountMap = new ConcurrentHashMap<Integer, Integer>();
			this.actoinMap = new ConcurrentHashMap<Long, Integer>();
			this.pnl = 0;
			this.morningPnl = 0;
			this.tradeCount = 0;
			this.activeTime = new Date().getTime();
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
		int currentOrderId = -1;
		if(orderMap == null) return currentOrderId;
		for(int orderId : orderMap.keySet()) {
			currentOrderId = Math.max(orderId, currentOrderId);
		}
		return currentOrderId;
	}
	
	public int getQuantity() {
		if(orderMap == null) return 0;
		int id = getOrderId();
		return orderMap.containsKey(id) ? orderMap.get(id).m_totalQuantity : 0;
	}
	
	public String getAction() {
		if(orderMap == null) return "";
		int id = getOrderId();
		return orderMap.containsKey(id) ? orderMap.get(id).m_action : "";
	}

	public String getOrderTime() {
		if (orderTime == null)
			return "";
		return DateUtils.yyyyMMddHHmmss().format(orderTime);
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public Map<Integer, Boolean> getCancelOrder() {
		return cancelOrder;
	}

	public void setCancelOrder(Map<Integer, Boolean> cancelOrder) {
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
