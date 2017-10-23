package com.yoson.scheduler;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ib.client.Order;
import com.yoson.date.DateUtils;
import com.yoson.model.PerSecondRecord;
import com.yoson.model.ScheduleData;
import com.yoson.tws.EClientSocketUtils;
import com.yoson.tws.ScheduledDataRecord;
import com.yoson.tws.Strategy;
import com.yoson.tws.YosonEWrapper;

public class TaskScheduler {
	
	public void doTrade() throws ParseException {
		try {
			Calendar calendar = Calendar.getInstance();  
			Date now = new Date();
			boolean validateTime = YosonEWrapper.isValidateTime(now);
			String dateTimeStr = DateUtils.yyyyMMddHHmmss2().format(now);
//			System.out.println();
//			System.out.println("*************************Do Trade:<" + time + ">*************************");
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
			
			calendar.setTime(now);  
			calendar.add(Calendar.SECOND, -1);
			long lastSecond = Long.parseLong(DateUtils.yyyyMMddHHmmss2().format(calendar.getTime()));
			if(EClientSocketUtils.isConnected()) {
				for (Strategy strategy : EClientSocketUtils.strategies) {
					if (strategy.isActive() && strategy.getMainUIParam().isMarketTime(DateUtils.getTimeStr(dateTimeStr))) {
						if(strategy.isFirstRun()) {
							List<ScheduledDataRecord> scheduledDataRecords = YosonEWrapper.extractScheduledDataRecord();
//							System.out.println("scheduledDataRecords:" + scheduledDataRecords.size());
							if(scheduledDataRecords.size() > 0 && scheduledDataRecords.get(scheduledDataRecords.size() - 1).getTime().equals(dateTimeStr)) {
								scheduledDataRecords.remove(scheduledDataRecords.size() - 1);
							}
//							System.out.println("Calculation for " + strategy.getStrategyName() + " at " + time);
							List<ScheduleData> scheduleDatas = YosonEWrapper.toScheduleDataList(scheduledDataRecords, strategy.getMainUIParam());
							if (scheduleDatas.size() > 0) {
								for(long start = Long.parseLong(scheduledDataRecords.get(scheduledDataRecords.size() - 1).getTime()) + 1; start <= lastSecond; ) {
									scheduleDatas.add(YosonEWrapper.toScheduleData(new ScheduledDataRecord(start + ""), strategy.getMainUIParam())); 
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
							strategy.setFirstRun(false);
							strategy.setPerSecondRecords(perSecondRecords);
							strategy.setScheduleDatas(scheduleDatas);
						} else {
							ScheduledDataRecord lastSecondScheduledDataRecord = YosonEWrapper.getLastSecondScheduledDataRecord(lastSecond);			
							List<PerSecondRecord> perSecondRecords = strategy.getPerSecondRecords();
							List<ScheduleData> scheduleDatas = strategy.getScheduleDatas();
							ScheduleData scheduleData = YosonEWrapper.toScheduleData(lastSecondScheduledDataRecord, strategy.getMainUIParam());
//							System.out.println("scheduleData for " + strategy.getStrategyName() + " at " + time + ", ask:" + scheduleData.getAskPrice() + ", bid:" + scheduleData.getBidPrice() + ", trade:" + scheduleData.getLastTrade());
							scheduleDatas.add(scheduleData);
							int checkMarketTime = strategy.getMainUIParam().isCheckMarketTime(scheduleData.getTimeStr());
							
							perSecondRecords.add(new PerSecondRecord(scheduleDatas, strategy.getMainUIParam(), perSecondRecords, scheduleData, checkMarketTime));
//							System.out.println("Calculation for " + strategy.getStrategyName() + " at " + time + ", perSecondRecords:" + perSecondRecords.size() + ", scheduleDatas:" + scheduleDatas.size());
						}
						strategy.setPnl(strategy.getPerSecondRecords().size() > 0 ? strategy.getPerSecondRecords().get(strategy.getPerSecondRecords().size() - 1).getTotalPnl() : 0);
						placeAnOrder(strategy, dateTimeStr);
					}
				}			
			}
		} catch (Exception e) {
//			System.out.println(e.getMessage() + ">" + e.getCause().getMessage());
//			for(StackTraceElement s : e.getStackTrace()) {
//				System.out.println(s.toString());
//			}
		}
	}

	public void placeAnOrder(Strategy strategy, String time) {
		if (strategy.getPerSecondRecords().size() < 2 || YosonEWrapper.currentOrderId == null) {
//			System.out.println("Checking fail for " + strategy.getStrategyName());
			return;
		}
		PerSecondRecord lastSecondRecord = strategy.getPerSecondRecords().get(strategy.getPerSecondRecords().size() - 2); 
		PerSecondRecord currentSecondRecord = strategy.getPerSecondRecords().get(strategy.getPerSecondRecords().size() - 1);
//		System.out.println("last smooth:" + lastSecondRecord.getSmoothAction() + ", current smooth:" + currentSecondRecord.getSmoothAction() + ", fail trade count:" + strategy.getFailTradeCount());
		YosonEWrapper.currentOrderId++;
		if(strategy.getFailTradeCount() > 3 && lastSecondRecord.getSmoothAction() != 0) {
			Order newOrder = new Order();
			newOrder.m_account = EClientSocketUtils.connectionInfo.getAccount();
			newOrder.m_orderType = "MKT"; 
			newOrder.m_tif = EClientSocketUtils.contract.getTif();
			newOrder.m_totalQuantity = 1;
			newOrder.m_action = lastSecondRecord.getSmoothAction() == -1 ? "BUY" : "SELL";
			strategy.getOrderMap().put(YosonEWrapper.currentOrderId, newOrder);
			strategy.setOrderTime(new Date());
			EClientSocketUtils.placeOrder(YosonEWrapper.currentOrderId, newOrder);
//			System.out.println("FailCount Action(" + time + ") : " + strategy.getStrategyName() + ", orderId:" + YosonEWrapper.currentOrderId + ", action:" + newOrder.m_action + ", quantity:" + 1);
		} else if (currentSecondRecord.getSmoothAction() != lastSecondRecord.getSmoothAction()) {
			int quantity = currentSecondRecord.getSmoothAction() - lastSecondRecord.getSmoothAction();
			int totalQuantity = Math.abs(quantity);
			boolean isBuy = quantity > 0;
			String action = isBuy ? "BUY" : "SELL";
			Order newOrder = new Order();
			newOrder.m_account = EClientSocketUtils.connectionInfo.getAccount();
			newOrder.m_orderType = "LMT";
			newOrder.m_auxPrice = 0;
			newOrder.m_tif = EClientSocketUtils.contract.getTif();
			newOrder.m_action = action;
			newOrder.m_totalQuantity = totalQuantity;
			
			double lmtPrice = YosonEWrapper.trade + ((isBuy ? 1 : -1 ) * strategy.getMainUIParam().getUnit() * strategy.getMainUIParam().getOrderTicker());
			newOrder.m_lmtPrice = Math.round(lmtPrice * 100) / 100D;
			
			strategy.getOrderMap().put(YosonEWrapper.currentOrderId, newOrder);
			strategy.setOrderTime(new Date());
			EClientSocketUtils.placeOrder(YosonEWrapper.currentOrderId, newOrder);
//			System.out.println("Normal Action(" + time + ") : " + strategy.getStrategyName() + ", orderId:" + YosonEWrapper.currentOrderId + ", action:" + newOrder.m_action + ", quantity:" + totalQuantity);
		}
	}
}
