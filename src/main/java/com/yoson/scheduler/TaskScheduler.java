package com.yoson.scheduler;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ib.client.Order;
import com.yoson.cms.controller.Global;
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
		if(!EClientSocketUtils.isConnected()) {
			return;
		}
		long startTime = System.currentTimeMillis();
		StringBuffer log = new StringBuffer();
		try {
			Date now = new Date();
			boolean validateTime = YosonEWrapper.isValidateTime(now);
			long nowDateTimeLong = Long.parseLong(DateUtils.yyyyMMddHHmmss2().format(now));
			if(!validateTime) {
				Date endTime = DateUtils.yyyyMMddHHmm().parse(EClientSocketUtils.contract.getEndTime());
				if(DateUtils.addSecond(endTime, 1) <= nowDateTimeLong && EClientSocketUtils.isConnected()) {
					//trigger auto backtest
					EClientSocketUtils.disconnect();
				}					
				return;
			}
			log.append("*************************Do Trade:<" + nowDateTimeLong + ">*************************" + Global.lineSeparator);
			if(expectNextExecuteTime != null && expectNextExecuteTime != nowDateTimeLong) {
				log.append("Warning: Miss executing for " + expectNextExecuteTime + Global.lineSeparator);
			}
			expectNextExecuteTime = DateUtils.addSecond(now, 1);
			long lastSecond = DateUtils.addSecond(now, -1);
			if(EClientSocketUtils.isConnected()) {
				Map<String, List<ScheduleData>> scheduleDataMap = new HashMap<String, List<ScheduleData>>();
				for (Strategy strategy : EClientSocketUtils.strategies) {
//					if (strategy.isActive() && strategy.getMainUIParam().isMarketTime(DateUtils.getTimeStr(dateTimeStr))) {
					if (strategy.isActive()) {
						String key = strategy.getMainUIParam().getAskDataField() + "," +
									 strategy.getMainUIParam().getBidDataField() + "," +
									 strategy.getMainUIParam().getTradeDataField();
						List<ScheduleData> scheduleDatas = null;
						if(scheduleDataMap.containsKey(key)) {
							scheduleDatas = scheduleDataMap.get(key);
						} else {
							scheduleDatas = YosonEWrapper.toScheduleDataList(YosonEWrapper.scheduledDataRecords, strategy.getMainUIParam(), lastSecond);	
							scheduleDataMap.put(key, scheduleDatas);
							if (scheduleDatas.size() > 0) {
								ScheduleData scheduleData = scheduleDatas.get(scheduleDatas.size() - 1);
								log.append(scheduleData.getDateTimeStr() + "," + scheduleData.getAskPrice() + "," + scheduleData.getBidPrice() + "," + scheduleData.getLastTrade() + "," + key  + Global.lineSeparator);
							}
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
								if (quantity != 0 && !strategy.getActoinMap().containsKey(scheduleData.getId())) {
									strategy.getActoinMap().put(scheduleData.getId(), quantity);
									log.append(placeAnOrder(strategy, scheduleData.getDateTimeStr(), quantity));
								}
							}
						}						
						strategy.setPnl(perSecondRecords.size() > 0 ? perSecondRecords.get(perSecondRecords.size() - 1).getTotalPnl() : 0);
						log.append("Total pnl for " + strategy.getStrategyName() + " is " + strategy.getPnl()  + Global.lineSeparator);
					}
				}			
			}
			log.append("Calculation finish with " + (System.currentTimeMillis() - startTime)  + Global.lineSeparator);
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

	public String placeAnOrder(Strategy strategy, String now, int quantity) {
		if(YosonEWrapper.currentOrderId == null) return "";
		YosonEWrapper.currentOrderId++;
		int newOrderId = YosonEWrapper.currentOrderId;

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
		strategy.setOrderTime(new Date());
		EClientSocketUtils.placeOrder(newOrderId, newOrder);
		return "Limit Order(" + now + ") : " + strategy.getStrategyName() + ", orderId:" + newOrderId + ", action:" + newOrder.m_action + ", quantity:" + totalQuantity + Global.lineSeparator;
	}
}
