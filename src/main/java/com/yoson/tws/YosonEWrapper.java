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
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.ib.client.Contract;
import com.ib.client.Order;
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
	public static double close;
	
	public static List<ScheduledDataRecord> scheduledDataRecords;
	
	public static Map<Long, List<Double>> tradeSizeMap;
	public static Map<Long, List<Double>> askSizeMap;
	public static Map<Long, List<Double>> bidSizeMap;
	public static Map<Long, List<Double>> closeMap;
	public static Integer currentOrderId;
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
		closeMap = new TreeMap<Long, List<Double>>();
		
		tradeSizeMap = new TreeMap<Long, List<Double>>();
		askSizeMap = new TreeMap<Long, List<Double>>();
		bidSizeMap = new TreeMap<Long, List<Double>>();
	}
	
	public static void clear() {
		scheduledDataRecords = null;
		closeMap = null;
		
		tradeSizeMap = null;
		askSizeMap = null;
		bidSizeMap = null;	
		
		lastTime = null;
		currentOrderId = null;
	}
	
	public static List<ScheduleData> toScheduleDataList(List<ScheduledDataRecord> scheduledDataRecords, MainUIParam mainUIParam, long lastSecond) throws ParseException {
		List<ScheduleData> scheduleDatas = new ArrayList<ScheduleData>();
		if(scheduledDataRecords == null || scheduledDataRecords.size() == 0) return scheduleDatas;
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
		return scheduleDatas;
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
			MainUIParam mainUIParam = loadMainUIParamFromCSV(strategyFile);
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
		List<ScheduleData> dailyScheduleData = toScheduleDataList(scheduledDataRecords, strategy.getMainUIParam(), Long.parseLong(scheduledDataRecords.get(scheduledDataRecords.size() - 1).getTime()));
		List<PerSecondRecord> dailyPerSecondRecord = new ArrayList<PerSecondRecord>();
		for (ScheduleData scheduleDataPerSecond : dailyScheduleData) {				
			int checkMarketTime = strategy.getMainUIParam().isCheckMarketTime(scheduleDataPerSecond.getTimeStr());
			dailyPerSecondRecord.add(new PerSecondRecord(dailyScheduleData, strategy.getMainUIParam(), 
					dailyPerSecondRecord, scheduleDataPerSecond, checkMarketTime));
		}
		TradePerSecondDetailsCSVWriter.WriteCSV(folderPath, strategy, symbol, dailyPerSecondRecord);
	}
	
	private static MainUIParam loadMainUIParamFromCSV(File strategyFile) {
		try {
			CSVReader csvReader = new CSVReader(new FileReader(strategyFile), ',', '\n', 0);
			String [] lines;
			MainUIParam mainUIParam = new MainUIParam();
			List<String []> params = new ArrayList<String []>();
			int index = 0;
			while ((lines = csvReader.readNext()) != null && index <= 23 )  {
				params.add(lines);
				index++;
			}
			index = 0;
			mainUIParam.setSource(params.get(index++)[1]);
			mainUIParam.setTradeDataField(params.get(index++)[1]);
			mainUIParam.setAskDataField(params.get(index++)[1]);
			mainUIParam.setBidDataField(params.get(index++)[1]);
			mainUIParam.settShort(Integer.parseInt(params.get(index++)[1]));
			mainUIParam.settLong(Integer.parseInt(params.get(index++)[1]));
			mainUIParam.settLong2(Integer.parseInt(params.get(index++)[1]));
			mainUIParam.setHld(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setStopLoss(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setTradeStopLoss(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setInstantTradeStoploss(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setItsCounter(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setStopGainPercent(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setStopGainTrigger(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setMarketStartTime(params.get(index++)[1]);
			mainUIParam.setLunchStartTimeFrom(params.get(index++)[1]);
			mainUIParam.setLunchStartTimeTo(params.get(index++)[1]);
			mainUIParam.setMarketCloseTime(params.get(index++)[1]);
			mainUIParam.setCashPerIndexPoint(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setTradingFee(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setOtherCostPerTrade(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setUnit(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setLastNumberOfMinutesClearPosition(Integer.parseInt(params.get(index++)[1]));
			mainUIParam.setLunchLastNumberOfMinutesClearPosition(Integer.parseInt(params.get(index++)[1]));
			csvReader.close();
			return mainUIParam;
		} catch (Exception e) {
			return null;
		}
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
	
	@Override
	public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {
		Date now = new Date();
		if(!isValidateTime(now))
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
		if(!isValidateTime(now))
			return;
		String time = DateUtils.yyyyMMddHHmmss2().format(now);
		switch (field) {
		case 0:
			addLiveData(bidSizeMap, now, size);
			addLiveData(scheduledDataRecords, now, bid, BID);
			String liveResult = time + "," + bid + "," + size + Global.lineSeparator;
			BackTestCSVWriter.writeText(livePath(), BID + "," + liveResult, true);
			BackTestCSVWriter.writeText(bidPath(), liveResult, true);
			break;
		case 3:
			addLiveData(askSizeMap, now, size);
			addLiveData(scheduledDataRecords, now, ask, ASK);
			String askResult = time + "," + ask + "," + size + Global.lineSeparator;
			BackTestCSVWriter.writeText(livePath(), ASK + "," + askResult, true);
			BackTestCSVWriter.writeText(askPath(), askResult, true);
			break;
		case 5:
			addLiveData(tradeSizeMap, now, size);
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
		String time = DateUtils.yyyyMMddHHmmss2().format(new Date());
		
		// match any strategy
		boolean found = false;
		
		// the flag of the order status which cancel by api call
		boolean cancel = false;
		
		boolean activeStrategyOrder = false;
		for (Strategy strategy : EClientSocketUtils.strategies) {
			if(strategy.getOrderMap().containsKey(orderId)) {
				activeStrategyOrder = strategy.isActive();
				found = true;
				break;
			}
			if(strategy.getCancelOrder().contains(orderId)) {
				cancel = true;
			}
		}
		BackTestCSVWriter.writeText(getOrderStatusLogPath(),
				time
				+ (found ? "Hit Strategy(" + (activeStrategyOrder ? "Active" : "Inactive") + ")," : "Miss Strategy,")
				+ (cancel ? "A Cancel Order Result(Cancelled by API)," : "")
				+  "=>orderId:" + orderId 
				+ ", status:" + status
				+ ", filled:" + filled
				+ ", remaining:" + remaining
				+ ", avgFillPrice:" + avgFillPrice
				+ ", permId:" + permId
				+ ", parentId:" + parentId
				+ ", lastFillPrice:" + lastFillPrice
				+ ", clientId:" + clientId
				+ ", whyHeld:" + whyHeld + Global.lineSeparator, true);

		if(cancel) return;
		for (Strategy strategy : EClientSocketUtils.strategies) {
			if(strategy.isActive() && strategy.getOrderMap().containsKey(orderId)) {
				Order order = strategy.getOrderMap().get(orderId);
				if((status.equals("Cancelled") || status.equals("Inactive")) && remaining > 0) {
					YosonEWrapper.currentOrderId++;
					int newOrderId = YosonEWrapper.currentOrderId;
					int orderCount = 2;
					if(strategy.getOrderCountMap().containsKey(orderId)) {
						orderCount = strategy.getOrderCountMap().get(orderId) + 1;
					}
					Order newOrder = new Order();
					newOrder.m_account = EClientSocketUtils.connectionInfo.getAccount();
					newOrder.m_tif = EClientSocketUtils.contract.getTif();
					newOrder.m_action = order.m_action;
					newOrder.m_totalQuantity = remaining;
					if (orderCount > 10) {
						newOrder.m_orderType = Global.MKT;
					} else {
						newOrder.m_orderType = Global.LMT;
						newOrder.m_auxPrice = 0;
						double lmtPrice = YosonEWrapper.trade + ((order.m_action.equals(Global.BUY) ? 1 : -1 ) * strategy.getMainUIParam().getUnit() * strategy.getMainUIParam().getOrderTicker());
						newOrder.m_lmtPrice = Math.round(lmtPrice * 100) / 100D;
					}
					EClientSocketUtils.placeOrder(newOrderId, newOrder);
					BackTestCSVWriter.writeText(YosonEWrapper.getLogPath(), "Retry For(" + orderId + "), " + (orderCount > 10 ? "Market Order" : "Limit Order") + "(" + time + ") : " + strategy.getStrategyName() + ", orderId:" + newOrderId + ", action:" + newOrder.m_action + ", quantity:" + remaining + Global.lineSeparator, true);
					
					strategy.getOrderMap().put(newOrderId, newOrder);
					strategy.getOrderCountMap().put(newOrderId, orderCount);
					strategy.setOrderTime(new Date());
					EClientSocketUtils.cancelOrder(orderId);
					strategy.getCancelOrder().add(orderId);						
				} else if(remaining > 0) {
					BackTestCSVWriter.writeText(getOrderStatusLogPath(), "Warning:" + status + " order with remaining=" + remaining + Global.lineSeparator, true);
				}
			}
		}
		
	}
	
	@Override
	public void nextValidId(int orderId) {
		System.out.println("Next OrderId : " + orderId);
		currentOrderId = currentOrderId != null && currentOrderId <= orderId ? currentOrderId + 1 : orderId;
		retryTimes = 0;
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
