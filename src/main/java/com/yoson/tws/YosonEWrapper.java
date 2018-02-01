package com.yoson.tws;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.ib.client.Contract;
import com.opencsv.CSVReader;
import com.yoson.cms.controller.Global;
import com.yoson.csv.BackTestCSVWriter;
import com.yoson.date.DateUtils;
import com.yoson.model.MainUIParam;
import com.yoson.model.PerSecondRecord;
import com.yoson.model.ScheduleData;

public class YosonEWrapper extends BasicEWrapper {
	
	public static double bid;
	public static double ask;
	public static double trade;
	public static int bidSize;
	public static int askSize;
	public static int tradeSize;
	public static double close;
	
	public static List<ScheduledDataRecord> scheduledDataRecords;
	
	public static int currentOrderId = -1;
	public static Date lastTime = new Date();
	
	public final static String BID = "Bid";
	public final static String ASK = "Ask";
	public final static String TRADE = "Trade";
	public final static String VOLUME = "Volume";
	public final static String CLOSE = "Close";
	
	private static String livePath() {
		return getPath("");
	} 
	
	private static String bidPath() {
		return getPath(BID);
	} 
	private static String askPath() {
		return getPath(ASK);
	}
	private static String tradePath() {
		return getPath(TRADE);
	}
	private static String volumePath() {
		return getPath(VOLUME);
	}
	private static String closePath() {
		return getPath(CLOSE);
	}
	
	public static String getConnectionPath() {
		return FilenameUtils.concat(EClientSocketUtils.initAndReturnLiveDataFolder(), "connection.txt");
	}
	
	public static String getLogPath() {
		return FilenameUtils.concat(EClientSocketUtils.initAndReturnLiveDataFolder(), "log.txt");
	}
	
	public static String getOrderStatusLogPath() {
		return FilenameUtils.concat(EClientSocketUtils.initAndReturnLiveDataFolder(), "orderStatus.txt");
	}
	
	private static String getPath(String type) {
		return getPath(EClientSocketUtils.initAndReturnLiveDataFolder(), type);
	}
	
	private static String getPath(String folderPath, String type) {
		return FilenameUtils.concat(folderPath, "live" + type + ".csv");
	}
	
	public static void initData() {
		scheduledDataRecords = new CopyOnWriteArrayList<ScheduledDataRecord>();
	}
	
	public static void clear() {
		scheduledDataRecords = null;
		
		lastTime = null;
		currentOrderId = -1;
	}
	
	public static synchronized  int increaseOrderId() {
		if (currentOrderId < 0) return currentOrderId;
		currentOrderId++;
		return currentOrderId;			
	}
	
	public static List<List<ScheduleData>> toScheduleDataList(List<ScheduledDataRecord> scheduledDataRecords, MainUIParam mainUIParam, long lastSecond) throws ParseException {
		List<List<ScheduleData>> resultDatas = new ArrayList<List<ScheduleData>>();
		List<ScheduleData> allDatas = new ArrayList<ScheduleData>();
		List<ScheduleData> morningDatas = new ArrayList<ScheduleData>();
		List<ScheduleData> afternoonDatas = new ArrayList<ScheduleData>();
		List<ScheduleData> scheduleDatas = new ArrayList<ScheduleData>();
		if(scheduledDataRecords == null || scheduledDataRecords.size() == 0) return resultDatas;
		long start = Long.parseLong(scheduledDataRecords.get(0).getTime());
		int i = 0;		
		for(; i < scheduledDataRecords.size(); i++) {
			ScheduledDataRecord scheduledDataRecord = scheduledDataRecords.get(i);
			long current = Long.parseLong(scheduledDataRecord.getTime());
			if(current > lastSecond) break;
			while(start < current) {
				scheduleDatas.add(toScheduleData(scheduledDataRecords.get(i-1), scheduleDatas.get(scheduleDatas.size() - 1), mainUIParam, start + ""));
				start = DateUtils.addSecond(start, 1);
			}					
			scheduleDatas.add(toScheduleData(scheduledDataRecord, i != 0 ? scheduleDatas.get(scheduleDatas.size() - 1) : null, mainUIParam, start + ""));				
			start = DateUtils.addSecond(start, 1);
		}
		while(start <= lastSecond) {
			scheduleDatas.add(toScheduleData(scheduledDataRecords.get(i-1), scheduleDatas.get(scheduleDatas.size() - 1), mainUIParam, start + ""));
			start = DateUtils.addSecond(start, 1);
		}
		
		long marketStartTime = DateUtils.HHmmss().parse(mainUIParam.getMarketStartTime()).getTime();
		long lunchStartTimeFrom = DateUtils.HHmmss().parse(mainUIParam.getLunchStartTimeFrom()).getTime();
		long lunchStartTimeTo = DateUtils.HHmmss().parse(mainUIParam.getLunchStartTimeTo()).getTime();
		long marketCloseTime = DateUtils.HHmmss().parse(mainUIParam.getMarketCloseTime()).getTime();
		for (ScheduleData scheduleData : scheduleDatas) {
			if (mainUIParam.isIgnoreLunchTime()) {
				allDatas.add(scheduleData);
			} else {
				long time = DateUtils.HHmmss().parse(scheduleData.getTimeStr()).getTime();		
				boolean isMorning = time >= marketStartTime && time <= lunchStartTimeFrom;
				boolean isAfternoon = time >= lunchStartTimeTo && time <= marketCloseTime;
				if(isMorning)
					morningDatas.add(scheduleData);
				if (isAfternoon)
					afternoonDatas.add(scheduleData);				
			}
		}
		if (mainUIParam.isIgnoreLunchTime()) {
			resultDatas.add(allDatas);
		} else {
			resultDatas.add(morningDatas);
			resultDatas.add(afternoonDatas);
		}
		return resultDatas;
	}
	
	public static ScheduleData toScheduleData(ScheduledDataRecord scheduledDataRecord, ScheduleData lastSecondScheduleData, MainUIParam mainUIParam, String dateTimeStr) throws ParseException {
		dateTimeStr = dateTimeStr == null ? scheduledDataRecord.getTime() + "" : dateTimeStr;
		Date dateTime = DateUtils.yyyyMMddHHmmss2().parse(dateTimeStr);
		String dateStr = DateUtils.yyyyMMdd().format(dateTime);
		String timeStr = DateUtils.HHmmss().format(dateTime);
		Double askPrice = "askmax".equals(mainUIParam.getAskDataField()) ? scheduledDataRecord.getAskmax() : (
				"askmin".equals(mainUIParam.getAskDataField()) ? scheduledDataRecord.getAskmin() : (
						"askavg".equals(mainUIParam.getAskDataField()) ? scheduledDataRecord.getAskavg() : scheduledDataRecord.getAsklast()
					)
				);
		Double bidPrice= "bidmax".equals(mainUIParam.getBidDataField()) ? scheduledDataRecord.getBidmax() : (
				"bidmin".equals(mainUIParam.getBidDataField()) ? scheduledDataRecord.getBidmin() : (
						"bidavg".equals(mainUIParam.getBidDataField()) ? scheduledDataRecord.getBidavg() : scheduledDataRecord.getBidlast()
						)
				);
		Double lastTrade= "trademax".equals(mainUIParam.getTradeDataField()) ? scheduledDataRecord.getTrademax() : (
				"trademin".equals(mainUIParam.getTradeDataField()) ? scheduledDataRecord.getTrademin() : (
						"tradeavg".equals(mainUIParam.getTradeDataField()) ? scheduledDataRecord.getTradeavg() : scheduledDataRecord.getTradelast()
					)
				);
		return new ScheduleData(dateStr, timeStr, 
				lastSecondScheduleData != null && askPrice == 0 ? lastSecondScheduleData.getAskPrice() : askPrice, 
				lastSecondScheduleData != null && bidPrice == 0 ? lastSecondScheduleData.getBidPrice() : bidPrice, 
				lastSecondScheduleData != null && lastTrade == 0 ? lastSecondScheduleData.getLastTrade() : lastTrade);
	}
	
	public static void genTradingDayPerSecondDetails(String folderPath, List<ScheduledDataRecord> scheduledDataRecords) throws ParseException {
		
		String concatPath = FilenameUtils.concat(folderPath, EClientSocketUtils.CONTRACT);
		File contractFile = new File(concatPath);
		Contract contract = null;
		if(contractFile.exists()) {
			String contractGson = BackTestCSVWriter.readText(concatPath);
			if(!StringUtils.isEmpty(contractGson)) {
				contract = new Gson().fromJson(contractGson, Contract.class);
			}
		}
		Collection<File> strategyFiles = FileUtils.listFiles(new File(folderPath), new IOFileFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return false;
			}
			
			@Override
			public boolean accept(File file) {
				return file.getName().indexOf("_TradingDayPerSecondDetails") >= 0;
			}
		}, TrueFileFilter.TRUE);
		for (File strategyFile : strategyFiles) {
			MainUIParam mainUIParam = MainUIParam.loadMainUIParamFromCSV(strategyFile);
			if(mainUIParam != null) {
				String strategyName = strategyFile.getName().split("_")[0];
				Strategy strategy = new Strategy();
				strategy.setStrategyName(strategyName);
				strategy.setMainUIParam(mainUIParam);
				genTradingDayPerSecondDetails(folderPath, scheduledDataRecords, contract != null ? contract.m_symbol : mainUIParam.getSource(), strategy);
			}
		}
		for (Strategy strategy : EClientSocketUtils.strategies) {
			genTradingDayPerSecondDetails(folderPath, scheduledDataRecords, contract != null ? contract.m_symbol : (EClientSocketUtils.contract != null ? EClientSocketUtils.contract.m_symbol : ""), strategy);
		}
	}
	private static void genTradingDayPerSecondDetails(String folderPath, List<ScheduledDataRecord> scheduledDataRecords, String symbol,
			Strategy strategy) throws ParseException {
		if(scheduledDataRecords.size() == 0) return;
		List<List<ScheduleData>> resultDatas = toScheduleDataList(scheduledDataRecords, strategy.getMainUIParam(), Long.parseLong(scheduledDataRecords.get(scheduledDataRecords.size() - 1).getTime()));		
		
		List<PerSecondRecord> allDailyPerSecondRecord = new ArrayList<PerSecondRecord>();
		if(!strategy.getMainUIParam().isIgnoreLunchTime() && strategy.getMainUIParam().isIncludeMorningData()) {
			List<ScheduleData> dailyScheduleData = new ArrayList<ScheduleData>();
			dailyScheduleData.addAll(resultDatas.get(0));
			dailyScheduleData.addAll(resultDatas.get(1));
			List<PerSecondRecord> dailyPerSecondRecord = new ArrayList<PerSecondRecord>();
			for (ScheduleData scheduleDataPerSecond : dailyScheduleData) {				
				int checkMarketTime = strategy.getMainUIParam().isCheckMarketTime(scheduleDataPerSecond.getTimeStr());
				dailyPerSecondRecord.add(new PerSecondRecord(dailyScheduleData, strategy.getMainUIParam(), 
						dailyPerSecondRecord, scheduleDataPerSecond, checkMarketTime));
			}
			allDailyPerSecondRecord.addAll(dailyPerSecondRecord);
		} else {
			double morningPnl = 0;
			for (int index = 0; index < resultDatas.size(); index++) {
				List<ScheduleData> dailyScheduleData = resultDatas.get(index);
				List<PerSecondRecord> dailyPerSecondRecord = new ArrayList<PerSecondRecord>();
				for (ScheduleData scheduleDataPerSecond : dailyScheduleData) {				
					int checkMarketTime = strategy.getMainUIParam().isCheckMarketTime(scheduleDataPerSecond.getTimeStr());
					PerSecondRecord perSecondRecord = new PerSecondRecord(dailyScheduleData, strategy.getMainUIParam(), 
							dailyPerSecondRecord, scheduleDataPerSecond, checkMarketTime);
					if(index == 0) {
						morningPnl = perSecondRecord.getTotalPnl();
					} else {
						perSecondRecord.setTotalPnl(morningPnl + perSecondRecord.getTotalPnl());
					}
					dailyPerSecondRecord.add(perSecondRecord);
				}
				allDailyPerSecondRecord.addAll(dailyPerSecondRecord);
			}
		}		
		TradePerSecondDetailsCSVWriter.WriteCSV(folderPath, strategy, symbol, allDailyPerSecondRecord);	
	}
	
	public static List<ScheduledDataRecord> extractScheduledDataRecord(String folderPath) throws ParseException {
		File file = new File(getPath(folderPath, ""));
		List<ScheduledDataRecord> records = new ArrayList<ScheduledDataRecord>();
		if (!file.exists())
			return new ArrayList<ScheduledDataRecord>();
		CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(new FileReader(file), ',', '\n', 0);
			String [] lines;
			while ((lines = csvReader.readNext()) != null)  {
				addLiveData(records, DateUtils.yyyyMMddHHmmss2().parse(lines[1]), Double.parseDouble(lines[2]), lines[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(csvReader != null)
				try {
					csvReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		if(records.size() == 0) return records;
		List<ScheduledDataRecord> _records = new ArrayList<ScheduledDataRecord>();
		long start = Long.parseLong(records.get(0).getTime()); 
		for(int i = 0; i < records.size(); i++) {
			ScheduledDataRecord record = records.get(i);
			long current = Long.parseLong(record.getTime());
			while(start < current) {
				_records.add(new ScheduledDataRecord(start + "", records.get(i - 1), _records.get(_records.size() - 1)));
				start = DateUtils.addSecond(start, 1);
			}
			_records.add(new ScheduledDataRecord(start + "", record, i != 0 ? _records.get(_records.size() - 1) : null));				
			start = DateUtils.addSecond(start, 1);
		}
		return _records;
	}
	
	private static ScheduledDataRecord genScheduledData(long time, List<Double> trades, List<Double> asks, List<Double> bids) {
		ScheduledDataRecord scheduledDataRecord = new ScheduledDataRecord();
		scheduledDataRecord.setTime(time+"");
		if(trades != null) {
			scheduledDataRecord.setTradeavg(avgList(trades));
			scheduledDataRecord.setTradelast(trades.get(trades.size() - 1));
			scheduledDataRecord.setTrademin(Collections.min(trades));
			scheduledDataRecord.setTrademax(Collections.max(trades));				
		}
		if(asks != null) {
			scheduledDataRecord.setAskavg(avgList(asks));
			scheduledDataRecord.setAsklast(asks.get(asks.size() - 1));
			scheduledDataRecord.setAskmin(Collections.min(asks));
			scheduledDataRecord.setAskmax(Collections.max(asks));				
		}
		
		if (bids != null) {
			scheduledDataRecord.setBidavg(avgList(bids));
			scheduledDataRecord.setBidlast(bids.get(bids.size() - 1));
			scheduledDataRecord.setBidmin(Collections.min(bids));
			scheduledDataRecord.setBidmax(Collections.max(bids));				
		}
		return scheduledDataRecord;
	}
	
	public static List<ScheduledDataRecord> extractScheduledDataRecord(Map<Long, List<Double>> tradeMap, Map<Long, List<Double>> askMap, Map<Long, List<Double>> bidMap) throws ParseException {
		List<ScheduledDataRecord> scheduledDataRecords = new ArrayList<ScheduledDataRecord>();
		List<Long> minList = new ArrayList<Long>();
		List<Long> maxList = new ArrayList<Long>();
		if(tradeMap.size() > 0) {
			minList.add(Collections.min(tradeMap.keySet()));
			maxList.add(Collections.max(tradeMap.keySet()));
//			System.out.println("tradeMapMin:" + Collections.min(tradeMap.keySet()) + ", tradeMapMax:" + Collections.max(tradeMap.keySet()));
		}
		if(askMap.size() > 0) {
			minList.add(Collections.min(askMap.keySet()));
			maxList.add(Collections.max(askMap.keySet()));
//			System.out.println("askMapMin:" + Collections.min(askMap.keySet()) + ", askMapMax:" + Collections.max(askMap.keySet()));
		}
		if(bidMap.size() > 0) {
			minList.add(Collections.min(bidMap.keySet()));			
			maxList.add(Collections.max(bidMap.keySet()));
//			System.out.println("bidMapMin:" + Collections.min(bidMap.keySet()) + ", bidMapMax:" + Collections.max(bidMap.keySet()));
		}
		
		if(minList.size() == 0)
			return scheduledDataRecords;

		long tradeStart = tradeMap.keySet().size() == 0 ? -1 : Collections.min(tradeMap.keySet());
		long tradeEnd = tradeMap.keySet().size() == 0 ? -1 : Collections.max(tradeMap.keySet());
		long askStart = askMap.keySet().size() == 0 ? -1 : Collections.min(askMap.keySet());
		long askEnd = askMap.keySet().size() == 0 ? -1 : Collections.max(askMap.keySet());
		long bidStart = bidMap.keySet().size() == 0 ? -1 : Collections.min(bidMap.keySet());
		long bidEnd = bidMap.keySet().size() == 0 ? -1 : Collections.max(bidMap.keySet());
		long start = Collections.min(minList);
		long end = Collections.max(maxList);
		
//		System.out.println("tradeMap:" + tradeMap.size() + ", askMap:" + askMap.size() + ", bidMap:" + bidMap.size() + ", start:" + start + ", end:" + end);
		List<Double> preTradeList = null;
		List<Double> preAskList = null;
		List<Double> preBidList = null;
		for (; start <= end; ) {			    
			if (tradeMap.size() > 0 && start >= tradeStart && start <= tradeEnd) {
				preTradeList = tradeMap.containsKey(start) ? tradeMap.get(start) : preTradeList;
			}
			if (askMap.size() > 0 && start >= askStart && start <= askEnd) {
				preAskList = askMap.containsKey(start) ? askMap.get(start) : preAskList;
			}
			if (bidMap.size() > 0 && start >= bidStart && start <= bidEnd) {
				preBidList = bidMap.containsKey(start) ? bidMap.get(start) : preBidList;
			}
			
			scheduledDataRecords.add(genScheduledData(start, preTradeList, preAskList, preBidList));
			
			start = DateUtils.addSecond(start, 1);
		}
		return scheduledDataRecords;
	}
	
	public static double avgList(List<Double> list) {
		double sum = 0;
		for(Double d : list) {
			sum += d;
		}
		return sum / list.size();
	}
	
	public static void getRecordList(String folderPath, List<Record> tradeList, List<Record> askList, List<Record> bidList) {
		extractRecordAsList(getPath(folderPath, ""), tradeList, askList, bidList);
	}
	
	public static void extractRecordAsList(String path, List<Record> tradeList, List<Record> askList, List<Record> bidList) {
		File file = new File(path);
		if (!file.exists())
			return;
		CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(new FileReader(file), ',', '\n', 0);
			String [] lines;
			while ((lines = csvReader.readNext()) != null)  {
				Record record = new Record(DateUtils.yyyyMMddHHmmss2().parse(lines[1]), Double.parseDouble(lines[2]), Integer.parseInt(lines[3]));
				String status = lines[0];
				if(status.equals(BID)) {
					bidList.add(record);
				}
				if(status.equals(ASK)) {
					askList.add(record);
				}
				if(status.equals(TRADE)) {
					tradeList.add(record);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(csvReader != null)
				try {
					csvReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	public static boolean isValidateTime(Date when) {
		if (EClientSocketUtils.contract == null)
			return false;
		return DateUtils.isValidateTime(when, EClientSocketUtils.contract.startTime, EClientSocketUtils.contract.endTime);
	}
	
	public static boolean isValidateTime(Date when, int bufferInMinutes) {
		if (EClientSocketUtils.contract == null)
			return false;
		return DateUtils.isValidateTime(when, EClientSocketUtils.contract.startTime, EClientSocketUtils.contract.endTime, bufferInMinutes);
	}
	
	@Override
	public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {
		Date now = new Date();
		if(!isValidateTime(now, 2))
			return;
		switch (field) {
		case 1:
			bid = price;
			break;
		case 2:
			ask = price;
			break;
		case 4:
			trade = price;
			break;
		case 9:
			close = price;
			BackTestCSVWriter.writeText(closePath(), DateUtils.yyyyMMddHHmmss2().format(now) + "," + close + Global.lineSeparator, true);
			break;
		}

	}
	
	@Override
	public void tickSize(int tickerId, int field, int size) {
		Date now = new Date();
		lastTime = now;
		if(!isValidateTime(now, 2))
			return;
		String time = DateUtils.yyyyMMddHHmmss2().format(now);
		switch (field) {
		case 0:
			bidSize = size;
			addLiveData(scheduledDataRecords, now, bid, BID);
			String liveResult = time + "," + bid + "," + size + Global.lineSeparator;
			BackTestCSVWriter.writeText(livePath(), BID + "," + liveResult, true);
			BackTestCSVWriter.writeText(bidPath(), liveResult, true);
			break;
		case 3:
			askSize = size;
			addLiveData(scheduledDataRecords, now, ask, ASK);
			String askResult = time + "," + ask + "," + size + Global.lineSeparator;
			BackTestCSVWriter.writeText(livePath(), ASK + "," + askResult, true);
			BackTestCSVWriter.writeText(askPath(), askResult, true);
			break;
		case 5:
			tradeSize = size;
			addLiveData(scheduledDataRecords, now, trade, TRADE);
			String tradeResult = time + "," + trade + "," + size + Global.lineSeparator;
			BackTestCSVWriter.writeText(livePath(), TRADE + "," + tradeResult, true);
			BackTestCSVWriter.writeText(tradePath(), tradeResult, true);
			break;
		case 8:
			BackTestCSVWriter.writeText(volumePath(), time + "," + size + Global.lineSeparator, true);
			break;
		}
	}
	
	public synchronized static void addLiveData(List<ScheduledDataRecord> list, Date date, double value, String type) {
		if(list == null || value == -1)
			return;
		String dateTimeStr = DateUtils.yyyyMMddHHmmss2().format(date);
		long time = Long.valueOf(dateTimeStr);
		ScheduledDataRecord scheduleData = null;
		for(int i = list.size() - 1; i >= 0; i--) {
			long _time = Long.parseLong(list.get(i).getTime());
			if(time > _time) {
				break;
			} else if(time == _time) {
				scheduleData = list.get(i);
				break;
			}
		}
		if (scheduleData == null) {
			scheduleData = new ScheduledDataRecord();
			scheduleData.setTime(dateTimeStr);
			list.add(scheduleData);
		}
		switch (type) {
			case TRADE:
				scheduleData.setTradeCount(scheduleData.getTradeCount() + 1);
				scheduleData.setTradeTotal(scheduleData.getTradeTotal() + value);
				scheduleData.setTradeavg(scheduleData.getTradeTotal() / scheduleData.getTradeCount());
				scheduleData.setTradelast(value);
				scheduleData.setTrademin(scheduleData.getTrademin() == 0 ? value : Math.min(scheduleData.getTrademin(), value));
				scheduleData.setTrademax(scheduleData.getTrademax() == 0 ? value : Math.max(scheduleData.getTrademax(), value));
				break;
			case ASK:
				scheduleData.setAskCount(scheduleData.getAskCount() + 1);
				scheduleData.setAskTotal(scheduleData.getAskTotal() + value);
				scheduleData.setAskavg(scheduleData.getAskTotal() / scheduleData.getAskCount());
				scheduleData.setAsklast(value);
				scheduleData.setAskmin(scheduleData.getAskmin() == 0 ? value : Math.min(scheduleData.getAskmin(), value));
				scheduleData.setAskmax(scheduleData.getAskmax() == 0 ? value : Math.max(scheduleData.getAskmax(), value));
				break;
			case BID:
				scheduleData.setBidCount(scheduleData.getBidCount() + 1);
				scheduleData.setBidTotal(scheduleData.getBidTotal() + value);
				scheduleData.setBidavg(scheduleData.getBidTotal() / scheduleData.getBidCount());
				scheduleData.setBidlast(value);
				scheduleData.setBidmin(scheduleData.getBidmin() == 0 ? value : Math.min(scheduleData.getBidmin(), value));
				scheduleData.setBidmax(scheduleData.getBidmax() == 0 ? value : Math.max(scheduleData.getBidmax(), value));
				break;
		}
	}
	
	public static void addLiveData(Map<Long, List<Double>> map, Date date, double value) {
		if(map == null || value == -1)
			return;
		long key = Long.parseLong(DateUtils.yyyyMMddHHmmss2().format(date));
		if (map.containsKey(key)) {
			map.get(key).add(value);
		} else {
			List<Double> values = new ArrayList<Double>();
			values.add(value);
			map.put(key, values);
		}
	}
	
	@Override
	public void tickString(int tickerId, int tickType, String value) {
		switch( tickType) {
		case 45:
			lastTime = new Date();
			break;
		}
	}
	
	@Override
	public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId,
			int parentId, double lastFillPrice, int clientId, String whyHeld) {
		long startTime = System.nanoTime();
		String time = DateUtils.yyyyMMddHHmmss2().format(new Date()) +"(" + startTime + ")";			
		StringBuffer orderLog = new StringBuffer();
		
		// match any strategy
		boolean found = false;
		
		// the flag of the order status which cancel by TWS
		boolean cancel = false;
		
		boolean isActiveStrategyOrder = false;
		
		for (Strategy strategy : EClientSocketUtils.strategies) {
			if(strategy.getOrderMap().containsKey(orderId)) {
				isActiveStrategyOrder = strategy.isActive();
				found = true;
			}
			if(strategy.getCancelOrder().containsKey(orderId)) {
				cancel = true;
			}
			
			if(strategy.isActive() && strategy.getOrderMap().containsKey(orderId)) {
				if((status.equals("Cancelled") || status.equals("Inactive")) && remaining > 0) {
					if (!strategy.getCancelOrder().containsKey(orderId)) {
						strategy.getCancelOrder().put(orderId, false);//set pending for retry
					}					
				} else if(remaining > 0) {
					orderLog.append("Warning:" + status + " order with remaining=" + remaining + Global.lineSeparator);
				}
			}
		}
		
		String log = 
				time
				+ (found ? "Hit Strategy(" + (isActiveStrategyOrder ? "Active" : "Inactive") + ")," : "Miss Strategy,")
				+ (cancel ? "A Previous Cancel Order Result," : "")
				+  "=>orderId:" + orderId 
				+ ", status:" + status
				+ ", filled:" + filled
				+ ", remaining:" + remaining
				+ ", avgFillPrice:" + avgFillPrice
				+ ", permId:" + permId
				+ ", parentId:" + parentId
				+ ", lastFillPrice:" + lastFillPrice
				+ ", clientId:" + clientId
				+ ", whyHeld:" + ", endTime:" + System.nanoTime() + Global.lineSeparator;
		
		BackTestCSVWriter.writeText(getOrderStatusLogPath(), log + orderLog.toString() + Global.lineSeparator, true);		
	}
	
	@Override
	public void nextValidId(int orderId) {
		currentOrderId = orderId;
		retryTimes = 0;
		System.out.println("currentOrderId:" + currentOrderId);
	}
	
	public static int retryTimes = 0;
	
	@Override
	public void connectionClosed() {
		BackTestCSVWriter.writeText(YosonEWrapper.getConnectionPath(), "Connection close at " + DateUtils.yyyyMMddHHmmss2().format(new Date())  + Global.lineSeparator, true);
		if(retryTimes < 10) {
			retryTimes++;
			EClientSocketUtils.reconnectUsingPreConnectSetting();			
		}
		
		if(retryTimes == 10) {
			EClientSocketUtils.disconnect();
		}
	}

}
