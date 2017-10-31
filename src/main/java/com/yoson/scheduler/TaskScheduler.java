package com.yoson.scheduler;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ib.client.Order;
import com.yoson.cms.controller.Global;
import com.yoson.csv.BackTestCSVWriter;
import com.yoson.date.DateUtils;
import com.yoson.model.PerSecondRecord;
import com.yoson.model.ScheduleData;
import com.yoson.tws.EClientSocketUtils;
import com.yoson.tws.ScheduledDataRecord;
import com.yoson.tws.Strategy;
import com.yoson.tws.YosonEWrapper;

public class TaskScheduler {
	
	public synchronized void doTrade() throws ParseException {
		try {
			Calendar calendar = Calendar.getInstance();  
			Date now = new Date();
			boolean validateTime = YosonEWrapper.isValidateTime(now);
			String dateTimeStr = DateUtils.yyyyMMddHHmmss2().format(now);
			if(!validateTime) {
				if(EClientSocketUtils.isConnected()) {
					Date endTime = DateUtils.yyyyMMddHHmm().parse(EClientSocketUtils.contract.getEndTime());
					calendar.setTime(endTime);  
					calendar.add(Calendar.SECOND, 1);
					if((Long.parseLong(DateUtils.yyyyMMddHHmmss2().format(calendar.getTime())) + "").equals(dateTimeStr)) {
						//trigger auto backtest
						EClientSocketUtils.disconnect();
					}
					
				}
				return;
			}
			BackTestCSVWriter.writeText(YosonEWrapper.getLogPath(), "*************************Do Trade:<" + dateTimeStr + ">*************************" + Global.lineSeparator, true);
			long startTime = System.currentTimeMillis();
			calendar.setTime(now);  
			calendar.add(Calendar.SECOND, -1);
			long lastSecond = Long.parseLong(DateUtils.yyyyMMddHHmmss2().format(calendar.getTime()));
			if(EClientSocketUtils.isConnected()) {
				List<ScheduledDataRecord> scheduledDataRecords = YosonEWrapper.extractScheduledDataRecord();
				if(scheduledDataRecords.size() > 0 && scheduledDataRecords.get(scheduledDataRecords.size() - 1).getTime().equals(dateTimeStr)) {
					scheduledDataRecords.remove(scheduledDataRecords.size() - 1);
				}
				for (Strategy strategy : EClientSocketUtils.strategies) {
					if (strategy.isActive() && strategy.getMainUIParam().isMarketTime(DateUtils.getTimeStr(dateTimeStr))) {
						List<ScheduleData> scheduleDatas = YosonEWrapper.toScheduleDataList(scheduledDataRecords, strategy.getMainUIParam());
						if (scheduleDatas.size() > 0) {
							long start = Long.parseLong(scheduledDataRecords.get(scheduledDataRecords.size() - 1).getTime());
							calendar.setTime(DateUtils.yyyyMMddHHmmss2().parse(start + ""));  
							calendar.add(Calendar.SECOND, 1);
							start = Long.parseLong(DateUtils.yyyyMMddHHmmss2().format(calendar.getTime()));
							ScheduleData lastScheduleData = scheduleDatas.get(scheduleDatas.size() - 1);
							for(; start <= lastSecond; ) {
								scheduleDatas.add(new ScheduleData(DateUtils.getDateStr(start + ""), DateUtils.getTimeStr(start + ""), lastScheduleData.getAskPrice(), lastScheduleData.getBidPrice(), lastScheduleData.getLastTrade())); 
								calendar.setTime(DateUtils.yyyyMMddHHmmss2().parse(start + ""));  
								calendar.add(Calendar.SECOND, 1);
								start = Long.parseLong(DateUtils.yyyyMMddHHmmss2().format(calendar.getTime()));
							}							
						}
						List<PerSecondRecord> perSecondRecords = new ArrayList<PerSecondRecord>();
						for (ScheduleData scheduleDataPerSecond : scheduleDatas) {
							int checkMarketTime = strategy.getMainUIParam().isCheckMarketTime(scheduleDataPerSecond.getTimeStr());
							perSecondRecords.add(new PerSecondRecord(scheduleDatas, strategy.getMainUIParam(), perSecondRecords, scheduleDataPerSecond, checkMarketTime));
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
//		System.out.println("last smooth:" + lastSecondRecord.getSmoothAction() + ", current smooth:" + currentSecondRecord.getSmoothAction() + ", fail trade count:" + strategy.getFailTradeCount());
		YosonEWrapper.currentOrderId++;
		if(strategy.getFailTradeCount() > 3 && lastSecondRecord.getSmoothAction() != 0) {
			Order newOrder = new Order();
			newOrder.m_account = EClientSocketUtils.connectionInfo.getAccount();
			newOrder.m_orderType = Global.MKT; 
			newOrder.m_tif = EClientSocketUtils.contract.getTif();
			newOrder.m_totalQuantity = 1;
			newOrder.m_action = lastSecondRecord.getSmoothAction() == -1 ? Global.BUY : Global.SELL;
			strategy.getOrderMap().put(YosonEWrapper.currentOrderId, newOrder);
			strategy.setOrderTime(new Date());
			EClientSocketUtils.placeOrder(YosonEWrapper.currentOrderId, newOrder);
			BackTestCSVWriter.writeText(YosonEWrapper.getLogPath(), "Market Order(" + time + ") : " + strategy.getStrategyName() + ", orderId:" + YosonEWrapper.currentOrderId + ", action:" + newOrder.m_action + ", quantity:" + 1 + Global.lineSeparator, true);
		} else if (currentSecondRecord.getSmoothAction() != lastSecondRecord.getSmoothAction()) {
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
			
			strategy.getOrderMap().put(YosonEWrapper.currentOrderId, newOrder);
			strategy.setOrderTime(new Date());
			EClientSocketUtils.placeOrder(YosonEWrapper.currentOrderId, newOrder);
			BackTestCSVWriter.writeText(YosonEWrapper.getLogPath(), "Limit Order(" + time + ") : " + strategy.getStrategyName() + ", orderId:" + YosonEWrapper.currentOrderId + ", action:" + newOrder.m_action + ", quantity:" + totalQuantity + Global.lineSeparator, true);
		}
	}
}
