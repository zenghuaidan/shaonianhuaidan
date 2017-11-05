package com.yoson.tws;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import com.yoson.cms.controller.Global;
import com.yoson.csv.BackTestCSVWriter;
import com.yoson.date.DateUtils;
import com.yoson.model.MainUIParam;
import com.yoson.model.ScheduleData;

public class YosonEWrapperTest {
	public static int bidCount = 14;
	public static int askCount = 20;
	public static int tradeCount = 7;
	public static void outputlog() {
		if(bidCount == 0 && askCount == 0 && tradeCount == 0)  {
			try {
				List<ScheduleData> scheduleDataList = YosonEWrapper.toScheduleDataList(YosonEWrapper.scheduledDataRecords, MainUIParam.getMainUIParam(), Long.parseLong(DateUtils.yyyyMMddHHmmss2().format(new Date())));
				for(ScheduleData s : scheduleDataList) {
					System.out.println(s.getDateTimeStr() + "  " + s.getAskPrice() + "," + s.getBidPrice() + "," + s.getLastTrade());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
//				for(ScheduledDataRecord s : YosonEWrapper.scheduledDataRecords) {
//					System.out.println(s.getTime() 
//							+ "," + TRADE + s.getTrademin()+","+s.getTrademax()+"," + s.getTradeavg()+","+s.getTradelast()
//							+ "," + ASK + s.getAskmin()+","+s.getAskmax()+"," + s.getAskavg()+","+s.getAsklast()
//							+ "," + BID + s.getBidmin()+","+s.getBidmax()+"," + s.getBidavg()+","+s.getBidlast());
//				}
		}
	}
	public static void main(String[] args) throws ParseException {
		YosonEWrapper.scheduledDataRecords = new CopyOnWriteArrayList<ScheduledDataRecord>();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(bidCount > 0) {
					Date date = new Date();
					double value = Math.round(2580 * new Random().nextDouble());
					YosonEWrapper.addLiveData(YosonEWrapper.scheduledDataRecords, date, value, YosonEWrapper.BID);
					BackTestCSVWriter.writeText("C:\\Users\\Qu88n\\Downloads\\HSI_20171103130310\\log1.txt", DateUtils.yyyyMMddHHmmss2().format(date) + "," + YosonEWrapper.BID + "," + value + Global.lineSeparator, true);
					bidCount--;
					try {
						Thread.sleep(new Random().nextInt(2000));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				outputlog();
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(tradeCount > 0) {
					Date date = new Date();
					double value = Math.round(2580 * new Random().nextDouble());
					YosonEWrapper.addLiveData(YosonEWrapper.scheduledDataRecords, date, value, YosonEWrapper.TRADE);
					BackTestCSVWriter.writeText("C:\\Users\\Qu88n\\Downloads\\HSI_20171103130310\\log1.txt", DateUtils.yyyyMMddHHmmss2().format(date) + "," + YosonEWrapper.TRADE + "," + value + Global.lineSeparator, true);
					tradeCount--;
					try {
						Thread.sleep(new Random().nextInt(2000));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				outputlog();
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(askCount > 0) {
					Date date = new Date();
					double value = Math.round(2580 * new Random().nextDouble());
					YosonEWrapper.addLiveData(YosonEWrapper.scheduledDataRecords, date, value, YosonEWrapper.ASK);
					BackTestCSVWriter.writeText("C:\\Users\\Qu88n\\Downloads\\HSI_20171103130310\\log1.txt", DateUtils.yyyyMMddHHmmss2().format(date) + "," + YosonEWrapper.ASK + "," + value + Global.lineSeparator, true);
					askCount--;
					try {
						Thread.sleep(new Random().nextInt(2000));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				outputlog();
			}

		}).start();
		
//		Calendar calendar = Calendar.getInstance();
//		Date now = new Date();
//		calendar.setTime(now);
//		CopyOnWriteArrayList<ScheduledDataRecord> scheduledDataRecords2 = new CopyOnWriteArrayList<ScheduledDataRecord>();
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 1, BID);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 2, ASK);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 3, BID);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 4, BID);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 5, TRADE);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 6, ASK);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 8, TRADE);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 7, ASK);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 9, BID);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 10, BID);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 11, ASK);		
//		
//		calendar.setTime(now);
//		calendar.add(Calendar.SECOND, 3);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 11, BID);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 10, ASK);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 9, BID);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 8, BID);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 7, TRADE);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 6, ASK);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 5, TRADE);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 4, ASK);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 3, BID);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 2, BID);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), -1, ASK);	
//		
//		calendar.setTime(now);
//		calendar.add(Calendar.SECOND, 8);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 2, BID);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 4, ASK);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 6, BID);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 8, BID);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 10, TRADE);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 12, ASK);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 14, TRADE);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), -1, ASK);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 16, BID);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 18, BID);
//		addLiveData(scheduledDataRecords2, calendar.getTime(), 22, ASK);	
//		
//		calendar.setTime(now);
//		calendar.add(Calendar.SECOND, 15);
//		long lastSecond = Long.parseLong(DateUtils.yyyyMMddHHmmss2().format(calendar.getTime()));
//		
////		toScheduleDataList(scheduledDataRecords2, MainUIParam.getMainUIParam(), lastSecond);
//		
//		calendar.setTime(now);
//		calendar.add(Calendar.SECOND, 5);
//		lastSecond = Long.parseLong(DateUtils.yyyyMMddHHmmss2().format(calendar.getTime()));
//		
//		toScheduleDataList(scheduledDataRecords2, MainUIParam.getMainUIParam(), lastSecond);
	}
}
