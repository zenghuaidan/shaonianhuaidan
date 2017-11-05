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
	
	public synchronized void doTrade() throws ParseException {
		long startTime = System.currentTimeMillis();
		try {
			Date now = new Date();
			boolean validateTime = YosonEWrapper.isValidateTime(now);
			String dateTimeStr = DateUtils.yyyyMMddHHmmss2().format(now);
			if(!validateTime) {
				if(EClientSocketUtils.isConnected()) {
					Date endTime = DateUtils.yyyyMMddHHmm().parse(EClientSocketUtils.contract.getEndTime());
					if((DateUtils.addSecond(endTime, 1) + "").equals(dateTimeStr)) {
						//trigger auto backtest
						EClientSocketUtils.disconnect();
					}
					
				}
				return;
			}
			BackTestCSVWriter.writeText(YosonEWrapper.getLogPath(), "*************************Do Trade:<" + dateTimeStr + ">*************************" + Global.lineSeparator, true);
			long lastSecond = DateUtils.addSecond(now, -1);
			if(EClientSocketUtils.isConnected()) {
				boolean first = true;
				Map<String, List<ScheduleData>> scheduleDataMap = new HashMap<String, List<ScheduleData>>();
				for (Strategy strategy : EClientSocketUtils.strategies) {
//					if (strategy.isActive() && strategy.getMainUIParam().isMarketTime(DateUtils.getTimeStr(dateTimeStr))) {
					if (strategy.isActive()) {
						String key = strategy.getMainUIParam().getAskDataField() +
									 strategy.getMainUIParam().getBidDataField() + 
									 strategy.getMainUIParam().getTradeDataField();
						List<ScheduleData> scheduleDatas = null;
						if(scheduleDataMap.containsKey(key)) {
							scheduleDatas = scheduleDataMap.get(key);
						} else {
							scheduleDatas = YosonEWrapper.toScheduleDataList(YosonEWrapper.scheduledDataRecords, strategy.getMainUIParam(), lastSecond);							
						}
						
						if(first) {
							for(int i = scheduleDatas.size() - 1; i >= scheduleDatas.size()-2 && i >= 0; i--) {
								ScheduleData s = scheduleDatas.get(i);
								BackTestCSVWriter.writeText(YosonEWrapper.getLogPath(), s.getDateStr() + " " + s.getTimeStr() + "," + s.getAskPrice() + "," + s.getBidPrice() + "," + s.getLastTrade()  + Global.lineSeparator, true);
							}
							first = false;
						}
						
						List<PerSecondRecord> perSecondRecords = new ArrayList<PerSecondRecord>();
						for (ScheduleData scheduleDataPerSecond : scheduleDatas) {
							int checkMarketTime = strategy.getMainUIParam().isCheckMarketTime(scheduleDataPerSecond.getTimeStr());
							perSecondRecords.add(new PerSecondRecord(scheduleDatas, strategy.getMainUIParam(), 
									perSecondRecords, scheduleDataPerSecond, checkMarketTime));
						}						
						strategy.setPnl(perSecondRecords.size() > 0 ? perSecondRecords.get(perSecondRecords.size() - 1).getTotalPnl() : 0);
						strategy.setTradeCount(perSecondRecords.size() > 0 ? (int)perSecondRecords.get(perSecondRecords.size() - 1).getTradeCount() : 0);
						
						
						if (perSecondRecords.size() >= 2 && YosonEWrapper.currentOrderId != null) {
							PerSecondRecord lastSecondRecord = perSecondRecords.get(perSecondRecords.size() - 2); 
							PerSecondRecord currentSecondRecord = perSecondRecords.get(perSecondRecords.size() - 1);
							placeAnOrder(strategy, dateTimeStr, currentSecondRecord, lastSecondRecord);
						}
					}
				}			
			}
			BackTestCSVWriter.writeText(YosonEWrapper.getLogPath(), "Calculation finish with " + (System.currentTimeMillis() - startTime)  + Global.lineSeparator, true);
		} catch (Exception e) {
			BackTestCSVWriter.writeText(YosonEWrapper.getLogPath(), "Sytem Exception:" + e.toString() + ">" + e.getMessage()  + Global.lineSeparator, true);
			for(StackTraceElement s : e.getStackTrace()) {
				System.out.println("%%%%%%%%%%%%" + s.toString() + "%%%%%%%%%%%%%%%");
			}
		}
	}

	public void placeAnOrder(Strategy strategy, String time, PerSecondRecord currentSecondRecord, PerSecondRecord lastSecondRecord) {
		YosonEWrapper.currentOrderId++;
		int newOrderId = YosonEWrapper.currentOrderId;
		if (currentSecondRecord.getSmoothAction() != lastSecondRecord.getSmoothAction()) {
			int quantity = currentSecondRecord.getSmoothAction() - lastSecondRecord.getSmoothAction();
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
			
			strategy.getOrderMap().put(newOrderId, newOrder);
			strategy.setOrderTime(new Date());
			EClientSocketUtils.placeOrder(newOrderId, newOrder);
			BackTestCSVWriter.writeText(YosonEWrapper.getLogPath(), "Limit Order(" + time + ") : " + strategy.getStrategyName() + ", orderId:" + newOrderId + ", action:" + newOrder.m_action + ", quantity:" + totalQuantity + Global.lineSeparator, true);
		}
	}
}
