package com.yoson.scheduler;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.ib.client.Order;
import com.yoson.cms.controller.Global;
import com.yoson.cms.controller.IndexController;
import com.yoson.csv.BackTestCSVWriter;
import com.yoson.date.DateUtils;
import com.yoson.model.PerSecondRecord;
import com.yoson.model.ScheduleData;
import com.yoson.tws.EClientSocketUtils;
import com.yoson.tws.Strategy;
import com.yoson.tws.YosonEWrapper;

public class TaskScheduler {
	public static Long expectNextExecuteTime;
	public synchronized void doTrade() throws ParseException {
		if(!EClientSocketUtils.isConnected() || StringUtils.isEmpty(EClientSocketUtils.id)) {
			return;
		}
		long start = System.nanoTime();
		long startTime = System.currentTimeMillis();
		StringBuffer log = new StringBuffer();
		try {
			Date now = new Date();
			boolean validateTime = YosonEWrapper.isValidateTime(now);
			long nowDateTimeLong = Long.parseLong(DateUtils.yyyyMMddHHmmss2().format(now));
			
			if(IndexController.mainUIParam != null 
					&& !EClientSocketUtils.lunchBTStart 
					&& StringUtils.isNotEmpty(EClientSocketUtils.id)
					&& EClientSocketUtils.contract != null
					&& StringUtils.isNotEmpty(EClientSocketUtils.contract.getSymbol())) {
				long lunchStartTimeFrom = DateUtils.HHmmss().parse(IndexController.mainUIParam.getLunchStartTimeFrom()).getTime();
				long lunchStartTimeTo = DateUtils.HHmmss().parse(IndexController.mainUIParam.getLunchStartTimeTo()).getTime();
				long time = DateUtils.HHmmss().parse(DateUtils.HHmmss().format(now)).getTime();
				boolean isLunchTime = time >= lunchStartTimeFrom && time <= lunchStartTimeTo;
				if (isLunchTime) {					
					EClientSocketUtils.lunchBTStart = true;
					// auto trigger the BT during lunch time
					IndexController.runBTWithLiveData(EClientSocketUtils.contract.getSymbol() + "_" + EClientSocketUtils.id);
				}
			}
			
			if(!validateTime) {
				if(StringUtils.isNotEmpty(EClientSocketUtils.id) 
						&& EClientSocketUtils.contract != null
						&& StringUtils.isNotEmpty(EClientSocketUtils.contract.startTime) 
						&& StringUtils.isNotEmpty(EClientSocketUtils.contract.endTime)
						&& StringUtils.isNotEmpty(EClientSocketUtils.contract.getSymbol())) {
					Date endTime = DateUtils.yyyyMMddHHmm().parse(EClientSocketUtils.contract.getEndTime());
					if(DateUtils.addSecond(endTime, 1) <= nowDateTimeLong && EClientSocketUtils.isConnected()) {
						EClientSocketUtils.disconnect();

						//trigger auto backtest, only the live have stop then can do the BT
						IndexController.runBTWithLiveData(EClientSocketUtils.contract.getSymbol() + "_" + EClientSocketUtils.id);
						EClientSocketUtils.id = null;
					}					
				}
				return;
			}
			log.append("*************************Do Trade:<" + nowDateTimeLong + ">*************************" + Global.lineSeparator);
			if(expectNextExecuteTime != null && expectNextExecuteTime != nowDateTimeLong) {
				log.append("Warning: Miss executing for " + expectNextExecuteTime + Global.lineSeparator);
			}
			expectNextExecuteTime = DateUtils.addSecond(now, 1);
			long lastSecond = DateUtils.addSecond(now, -1);
			Map<String, List<List<ScheduleData>>> scheduleDataMap = new HashMap<String, List<List<ScheduleData>>>();
			for (Strategy strategy : EClientSocketUtils.strategies) {
				if (strategy.isActive()) {
					String key = strategy.getMainUIParam().getAskDataField() + "," +
								 strategy.getMainUIParam().getBidDataField() + "," +
								 strategy.getMainUIParam().getTradeDataField() + "," +
								 strategy.getMainUIParam().getMarketStartTime() + "," +
								 strategy.getMainUIParam().getLunchStartTimeFrom() + "," +
								 strategy.getMainUIParam().getLunchStartTimeTo() + "," +
								 strategy.getMainUIParam().getMarketCloseTime();
					List<List<ScheduleData>> resultDatas = null;
					if(scheduleDataMap.containsKey(key)) {
						resultDatas = scheduleDataMap.get(key);
					} else {
						resultDatas = YosonEWrapper.toScheduleDataList(YosonEWrapper.scheduledDataRecords, strategy.getMainUIParam(), lastSecond);	
						scheduleDataMap.put(key, resultDatas);
					}
					
					boolean hasAfternoonData = false;
					List<ScheduleData> scheduleDatas = new ArrayList<ScheduleData>();
					if(!strategy.getMainUIParam().isIgnoreLunchTime() && strategy.getMainUIParam().isIncludeMorningData()) {
						// Don't ignore lunch time and including morning data, then combine morning and afternoon data
						scheduleDatas.addAll(resultDatas.get(0));
						scheduleDatas.addAll(resultDatas.get(1));
					} else if(resultDatas.size() == 2 && resultDatas.get(1).size() > 0) { // afternoon data come
						// this means have afternoon data, then it is set to don't ingore lunch time but not including morning data, then just add afternoon data 
						scheduleDatas.addAll(resultDatas.get(1));
						hasAfternoonData = true;
					} else if(resultDatas.size() > 0) {
						// this means ignore lunch time is set to true or afternoon is not coming yet
						scheduleDatas.addAll(resultDatas.get(0));
					}
					
					List<PerSecondRecord> perSecondRecords = new ArrayList<PerSecondRecord>();
					for (int i = 0; i < scheduleDatas.size(); i++) {
						ScheduleData scheduleData = scheduleDatas.get(i);
						int checkMarketTime = strategy.getMainUIParam().isCheckMarketTime(scheduleData.getTimeStr());
						PerSecondRecord currentSecondRecord = new PerSecondRecord(scheduleDatas, strategy.getMainUIParam(), 
								perSecondRecords, scheduleData, checkMarketTime);
						perSecondRecords.add(currentSecondRecord);
						if (i >= 1) {
							PerSecondRecord lastSecondRecord = perSecondRecords.get(i - 1);			
							int quantity = currentSecondRecord.getSmoothAction() - lastSecondRecord.getSmoothAction();
							if (quantity != 0 && !strategy.getActoinMap().containsKey(scheduleData.getId()) && scheduleData.getId() > strategy.getActiveTime()) {
								strategy.getActoinMap().put(scheduleData.getId(), quantity);
								for(int j = 1; j <= Math.abs(quantity); j++) {
									log.append(placeAnOrder(strategy, scheduleData.getDateTimeStr(), quantity < 0 ? -1: 1));
								}
							}
						}
					}
					strategy.setPnl(perSecondRecords.size() > 0 ? perSecondRecords.get(perSecondRecords.size() - 1).getTotalPnl() : 0);
					if(!hasAfternoonData) {
						// set the morning pnl before afternoon data come
						strategy.setMorningPnl(strategy.getPnl());							
					}
					else if(!strategy.getMainUIParam().isIncludeMorningData()) {
						//	not including morning data is set then should count back the morning pnl
						strategy.setPnl(strategy.getPnl() + strategy.getMorningPnl());
					}
					
					log.append(retryCancelOrder(strategy, nowDateTimeLong + ""));
					
					if(EClientSocketUtils.connectionInfo.isPaperTrading()) {
						log.append(retryMissingOrder(strategy, nowDateTimeLong + "", now.getTime()));						
					}
				}
			}
			log.append("Calculation finish with " + (System.currentTimeMillis() - startTime) + "(" + start + "-" + System.nanoTime() + ")" + Global.lineSeparator);
		} catch (Exception e) {
			log.append("Sytem Exception:" + e.toString() + ">" + e.getMessage()  + Global.lineSeparator);
			for(StackTraceElement s : e.getStackTrace()) {
				System.out.println(s.toString());
			}
		} finally {
			if(log.length() > 0) {
				BackTestCSVWriter.writeText(YosonEWrapper.getLogPath(), log.toString(), true);
			}
		}
	}
	
	public String retryMissingOrder(Strategy strategy, String now, long nowLong) {
		StringBuffer log = new StringBuffer();
		if(strategy.getOrderTimeMap() != null && strategy.getOrderTimeMap().size() > 0) {			
			for(int orderId : strategy.getOrderTimeMap().keySet()) {				
				// more than 60 seconds without status return, and can not find in open order, then it is a missing order
				if(!strategy.getOrderStatusTimeMap().containsKey(orderId) && !strategy.getOpenOrders().contains(orderId) && (nowLong - strategy.getOrderTimeMap().get(orderId) > (80 * 1000))) {
					strategy.getOrderTimeMap().remove(orderId);
					Order order = strategy.getOrderMap().get(orderId);
					int orderCount = 2;
					if(strategy.getOrderCountMap().containsKey(orderId)) {
						orderCount = strategy.getOrderCountMap().get(orderId) + 1;
					}
					Order newOrder = new Order();
					newOrder.m_account = EClientSocketUtils.connectionInfo.getAccount();
					newOrder.m_tif = EClientSocketUtils.contract.getTif();
					newOrder.m_action = order.m_action;
					newOrder.m_totalQuantity = 1;
					if (orderCount > 10) {
						newOrder.m_orderType = Global.MKT;
					} else {
						newOrder.m_orderType = Global.LMT;
						newOrder.m_auxPrice = 0;
						double lmtPrice = YosonEWrapper.trade + ((order.m_action.equals(Global.BUY) ? 1 : -1 ) * strategy.getMainUIParam().getUnit() * strategy.getMainUIParam().getOrderTicker());
						newOrder.m_lmtPrice = Math.round(lmtPrice * 100) / 100D;
					}
					int newOrderId = YosonEWrapper.increaseOrderId();
					EClientSocketUtils.placeOrder(newOrderId, newOrder);
					
					strategy.getOrderMap().put(newOrderId, newOrder);
					strategy.getOrderTimeMap().put(newOrderId, new Date().getTime());
					strategy.getOrderCountMap().put(newOrderId, orderCount);
					strategy.setOrderTime(new Date());
					strategy.getCancelOrder().replace(orderId, true);						
					String tradeLog = "Retry For Missing Order(" + orderId + "), " + (orderCount > 10 ? "Market Order" : "Limit Order") + "(" + now + ") : " + strategy.getStrategyName() + ", orderId:" + newOrderId + ", action:" + newOrder.m_action + ", quantity:1"+ Global.lineSeparator;
					EClientSocketUtils.tradeLogs.add(tradeLog);
					log.append(tradeLog);					
				}
			}			
		}
			
		return log.toString();
	}
	
	public String retryCancelOrder(Strategy strategy, String now) {
		StringBuffer log = new StringBuffer();
		for(int orderId : strategy.getCancelOrder().keySet()) {
			if(!strategy.getCancelOrder().get(orderId)) {//pending for retry
				Order order = strategy.getOrderMap().get(orderId);
				int orderCount = 2;
				if(strategy.getOrderCountMap().containsKey(orderId)) {
					orderCount = strategy.getOrderCountMap().get(orderId) + 1;
				}
				Order newOrder = new Order();
				newOrder.m_account = EClientSocketUtils.connectionInfo.getAccount();
				newOrder.m_tif = EClientSocketUtils.contract.getTif();
				newOrder.m_action = order.m_action;
				newOrder.m_totalQuantity = 1;
				if (orderCount > 10) {
					newOrder.m_orderType = Global.MKT;
				} else {
					newOrder.m_orderType = Global.LMT;
					newOrder.m_auxPrice = 0;
					double lmtPrice = YosonEWrapper.trade + ((order.m_action.equals(Global.BUY) ? 1 : -1 ) * strategy.getMainUIParam().getUnit() * strategy.getMainUIParam().getOrderTicker());
					newOrder.m_lmtPrice = Math.round(lmtPrice * 100) / 100D;
				}
				int newOrderId = YosonEWrapper.increaseOrderId();
				EClientSocketUtils.placeOrder(newOrderId, newOrder);
				
				strategy.getOrderMap().put(newOrderId, newOrder);
				strategy.getOrderTimeMap().put(newOrderId, new Date().getTime());
				strategy.getOrderCountMap().put(newOrderId, orderCount);
				strategy.setOrderTime(new Date());
				strategy.getCancelOrder().replace(orderId, true);						
				String tradeLog = "Retry For(" + orderId + "), " + (orderCount > 10 ? "Market Order" : "Limit Order") + "(" + now + ") : " + strategy.getStrategyName() + ", orderId:" + newOrderId + ", action:" + newOrder.m_action + ", quantity:1"+ Global.lineSeparator;
				EClientSocketUtils.tradeLogs.add(tradeLog);
				log.append(tradeLog);
			}
		}				
		return log.toString();
	}

	public String placeAnOrder(Strategy strategy, String now, int quantity) {
		int newOrderId = YosonEWrapper.increaseOrderId();
		if(newOrderId < 0) return "";	

		int totalQuantity = Math.abs(quantity);
		boolean isBuy = quantity > 0;
		String action = isBuy ? Global.BUY : Global.SELL;
		Order newOrder = new Order();
		newOrder.m_account = EClientSocketUtils.connectionInfo.getAccount();
		newOrder.m_orderType = Global.LMT;
		newOrder.m_auxPrice = 0;
		newOrder.m_tif = EClientSocketUtils.contract.getTif();
		newOrder.m_action = action;
		newOrder.m_totalQuantity = totalQuantity;
		
		double lmtPrice = YosonEWrapper.trade + ((isBuy ? 1 : -1 ) * strategy.getMainUIParam().getUnit() * strategy.getMainUIParam().getOrderTicker());
		newOrder.m_lmtPrice = Math.round(lmtPrice * 100) / 100D;
		
		strategy.setTradeCount(strategy.getTradeCount() + totalQuantity);
		strategy.getOrderMap().put(newOrderId, newOrder);
		strategy.getOrderTimeMap().put(newOrderId, new Date().getTime());
		strategy.setOrderTime(new Date());
		EClientSocketUtils.placeOrder(newOrderId, newOrder);
		String tradeLog = "Limit Order(" + now + ") : " + strategy.getStrategyName() + ", orderId:" + newOrderId + ", action:" + newOrder.m_action + ", quantity:" + totalQuantity + Global.lineSeparator;
		EClientSocketUtils.tradeLogs.add(tradeLog);
		return tradeLog;
	}
}
