package com.yoson.tws;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FilenameUtils;

import com.ib.client.Contract;
import com.opencsv.CSVReader;
import com.yoson.cms.controller.Global;
import com.yoson.csv.BackTestCSVWriter;
import com.yoson.date.DateUtils;

public class YosonEWrapper extends BasicEWrapper {
	
	public static ConcurrentHashMap<String, Double> priceMap = new ConcurrentHashMap<String, Double>();
	
	public final static String BID = "Bid";
	public final static String ASK = "Ask";
	public final static String TRADE = "Trade";	
	
	public static String getPath(String folderPath) {
		return FilenameUtils.concat(folderPath, "live.csv");
	}
	
	public static List<ScheduledDataRecord> extractScheduledDataRecord(String folderPath) throws ParseException {
		File file = new File(getPath(folderPath));
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
	
	public static void getRecordList(String folderPath, List<Record> tradeList, List<Record> askList, List<Record> bidList) {
		extractRecordAsList(getPath(folderPath), tradeList, askList, bidList);
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
	
	public static boolean isValidateTime(Contract contract, Date when) {
		if (contract == null)
			return false;
		return DateUtils.isValidateTime(when, contract.startTime, contract.endTime);
	}
	
	@Override
	public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {
		Date now = new Date();
		if(EClientSocketUtils.contracts == null || EClientSocketUtils.contracts.size() < (tickerId + 1) || !isValidateTime(EClientSocketUtils.contracts.get(tickerId), now))
			return;
		switch (field) {
		case 1:			
			priceMap.put(tickerId + BID, price);
			break;
		case 2:
			priceMap.put(tickerId + ASK, price);
			break;
		case 4:
			priceMap.put(tickerId + TRADE, price);
			break;		
		}

	}
	
	@Override
	public void tickSize(int tickerId, int field, int size) {
		Date now = new Date();
		if(EClientSocketUtils.contracts == null || EClientSocketUtils.contracts.size() < (tickerId + 1) || !isValidateTime(EClientSocketUtils.contracts.get(tickerId), now))
			return;
		String time = DateUtils.yyyyMMddHHmmss2().format(now);
		Contract contract = EClientSocketUtils.contracts.get(tickerId);
		String folder = FilenameUtils.concat(EClientSocketUtils.initAndReturnLiveDataFolder(), (tickerId + 1) + "_" + contract.m_secType + "_" + contract.m_symbol + "_" + contract.m_currency + "_" + contract.m_exchange);
		if(!new File(folder).exists()) {
			new File(folder).mkdir();
		}
		String path = getPath(folder);
		switch (field) {
		case 0:
			String bidResult = time + "," + (priceMap.contains(tickerId + BID) ? priceMap.get(tickerId + BID) : 0) + "," + size + Global.lineSeparator;
			BackTestCSVWriter.writeText(path, BID + "," + bidResult, true);			
			break;
		case 3:
			String askResult = time + "," + (priceMap.contains(tickerId + ASK) ? priceMap.get(tickerId + ASK) : 0) + "," + size + Global.lineSeparator;
			BackTestCSVWriter.writeText(path, ASK + "," + askResult, true);			
			break;
		case 5:
			String tradeResult = time + "," + (priceMap.contains(tickerId + TRADE) ? priceMap.get(tickerId + TRADE) : 0) + "," + size + Global.lineSeparator;
			BackTestCSVWriter.writeText(path, TRADE + "," + tradeResult, true);			
			break;		
		}
	}
	
	@Override
	public void tickString(int tickerId, int tickType, String value) {		
	}
	
	@Override
	public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId,
			int parentId, double lastFillPrice, int clientId, String whyHeld) {	
	}
	
	@Override
	public void nextValidId(int orderId) {
		
	}	
	
	@Override
	public void connectionClosed() {
		
	}

//	@Override
//    public void tickPrice( int tickerId, int field, double price, int canAutoExecute) {
//		String path = getPath(EClientSocketUtils.initAndReturnLiveDataFolder());
//		BackTestCSVWriter.writeText(path, "tickPrice:" 
//				+ tickerId + ","
//				+ field + ","
//				+ price + ","
//				+ canAutoExecute + "," + Global.lineSeparator, true);		
//	}
//    
//	@Override
//	public void tickSize( int tickerId, int field, int size) {
//		String path = getPath(EClientSocketUtils.initAndReturnLiveDataFolder());
//		BackTestCSVWriter.writeText(path, "tickSize:" 
//				+ tickerId + ","
//				+ field + ","
//				+ size + "," + Global.lineSeparator, true);		
//	}
//    
//	@Override
//	public void tickOptionComputation( int tickerId, int field, double impliedVol,
//    		double delta, double optPrice, double pvDividend,
//    		double gamma, double vega, double theta, double undPrice) {
//		String path = getPath(EClientSocketUtils.initAndReturnLiveDataFolder());
//		BackTestCSVWriter.writeText(path, "tickOptionComputation:" 
//				+ tickerId + ","
//				+ field + ","
//				+ impliedVol + ","
//				+ delta + ","
//				+ optPrice + ","
//				+ pvDividend + ","
//				+ gamma + ","
//				+ vega + ","
//				+ theta + ","
//				+ undPrice + "," + Global.lineSeparator, true);		
//	}
//	
//	@Override
//	public void tickGeneric(int tickerId, int tickType, double value) {
//		String path = getPath(EClientSocketUtils.initAndReturnLiveDataFolder());
//		BackTestCSVWriter.writeText(path, "tickGeneric:" 
//				+ tickerId + ","
//				+ tickType + ","
//				+ value + "," + Global.lineSeparator, true);			
//	}
//	
//	@Override
//	public void tickString(int tickerId, int tickType, String value) {
//		String path = getPath(EClientSocketUtils.initAndReturnLiveDataFolder());
//		BackTestCSVWriter.writeText(path, "tickString:" 
//				+ tickerId + ","
//				+ tickType + ","
//				+ value + "," + Global.lineSeparator, true);			
//	}
//	
//	@Override
//	public void tickEFP(int tickerId, int tickType, double basisPoints,
//			String formattedBasisPoints, double impliedFuture, int holdDays,
//			String futureExpiry, double dividendImpact, double dividendsToExpiry) {
//		String path = getPath(EClientSocketUtils.initAndReturnLiveDataFolder());
//		BackTestCSVWriter.writeText(path, "tickEFP:" 
//				+ tickerId + ","
//				+ tickType + ","
//				+ basisPoints + ","
//				+ formattedBasisPoints + ","
//				+ impliedFuture + ","
//				+ holdDays + ","
//				+ futureExpiry + ","
//				+ dividendImpact + ","
//				+ dividendsToExpiry + "," + Global.lineSeparator, true);
//	}

	
}
