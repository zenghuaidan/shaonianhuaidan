package com.yoson.tws;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.mapping.Array;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.ib.client.TagValue;
import com.yoson.cms.controller.IndexController;
import com.yoson.date.DateUtils;
import com.yoson.sql.SQLUtils;

public class EClientSocketUtils {
	public static EClientSocket socket;
	public static ConnectionInfo connectionInfo;
	public static List<Contract> contracts;	
	
	public static boolean connect(ConnectionInfo connectionInfo)
	{
		if(EClientSocketUtils.connectionInfo == null 
				|| !EClientSocketUtils.connectionInfo.getHost().equals(connectionInfo.getHost())
				|| EClientSocketUtils.connectionInfo.getPort() != connectionInfo.getPort()
				|| EClientSocketUtils.connectionInfo.getClientId() != connectionInfo.getClientId()
				|| EClientSocketUtils.connectionInfo.getTimeZone() != connectionInfo.getTimeZone()
			) {
			if (isConnected())
				EClientSocketUtils.disconnect();
			EClientSocketUtils.connectionInfo = connectionInfo;
			reset();
		}
		if (!isConnected() && EClientSocketUtils.connectionInfo != null) {
			socket = new EClientSocket(new YosonEWrapper());
			socket.eConnect(connectionInfo.getHost(), connectionInfo.getPort(), connectionInfo.getClientId());
		}
		return isConnected();
	}
	
	public static boolean disconnect()
	{
		reset();
		try {
			if(socket != null) {
				if(socket.isConnected())
					socket.eDisconnect();
				socket = null;
			}
			Thread.sleep(2000);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static boolean isConnected()
	{
		return socket != null && socket.isConnected();
	}
	
	public static void cancelHistoricalData()
	{
		if(socket != null) {
			socket.cancelHistoricalData(currentTickerId * identify);
			socket.cancelHistoricalData(currentTickerId * identify + 1);
			socket.cancelHistoricalData(currentTickerId * identify + 2);
		}
	}
	
	public static Date currentDateTime = null;
	public static int currentTickerId = -1;
	public static boolean running = false;
	public static boolean uploading = false;
	public static int requestSecs = 100;
	public static int identify = 1000;
	public static String id = null;
	public static void requestData(List<Contract> contracts) {
		EClientSocketUtils.contracts = contracts;
		reset();			
		id = DateUtils.yyyyMMddHHmmss2().format(new Date());
		YosonEWrapper.getHistoricalDataLogPath();
		IndexController.status = "Start getting data....";
		running = true;		
	}
	
	public static boolean upload(String historicalDataLogPath) {
		if(!uploading)
			uploading = true;
		else
			return false;
		File file = new File(historicalDataLogPath);
		if (file.exists()) {
			FileReader input = null;
			try {
				Map<String, Map<String, ScheduledDataRecord>> map = new HashMap<String, Map<String, ScheduledDataRecord>>();
				input = new FileReader(file);
				List<String> readLines = IOUtils.readLines(input);
				Map<String, List<String>> sourceAndTicker = new HashMap<String, List<String>>();
				for(String line : readLines) {
					int i = 0;
					String source = line.split(",")[i++];
					String type = line.split(",")[i++];
					String time = line.split(",")[i++];
					double open = Double.parseDouble(line.split(",")[i++]);
					double last = Double.parseDouble(line.split(",")[i++]);
					double min  = Double.parseDouble(line.split(",")[i++]);
					double max = Double.parseDouble(line.split(",")[i++]);
					double avg = Double.parseDouble(line.split(",")[i++]);
					
					String sourceValue = source;
					String tickerValue = "TWS_" + source;
					try {
						String _sourceValue = line.split(",")[i++];
						String _tickerValue = line.split(",")[i++];						
						sourceValue = StringUtils.isBlank(_sourceValue) ? sourceValue : _sourceValue;
						tickerValue = StringUtils.isBlank(_tickerValue) ? tickerValue : _tickerValue;
					} catch (Exception e) {
					}
					
					if (!sourceAndTicker.containsKey(source)) {
						List<String> sourceAndTickerValue = new ArrayList<String>();
						sourceAndTickerValue.add(sourceValue);
						sourceAndTickerValue.add(tickerValue);
					}
					ScheduledDataRecord scheduledDataRecord = new ScheduledDataRecord(time);					
					Map<String, ScheduledDataRecord> map2 = new HashMap<String, ScheduledDataRecord>();
					if (map.containsKey(source)) {
						map2 = map.get(source);
					} else {
						map.put(source, map2);
					}
					if (map2.containsKey(time)) {
						scheduledDataRecord = map2.get(time);
					} else {
						map2.put(time, scheduledDataRecord);
					}
					switch (type) {
						case "0":
							scheduledDataRecord.setBidopen(open);
							scheduledDataRecord.setBidlast(last);
							scheduledDataRecord.setBidmin(min);
							scheduledDataRecord.setBidmax(max);
							scheduledDataRecord.setBidavg(avg);
							break;
						case "1":
							scheduledDataRecord.setAskopen(open);
							scheduledDataRecord.setAsklast(last);
							scheduledDataRecord.setAskmin(min);
							scheduledDataRecord.setAskmax(max);
							scheduledDataRecord.setAskavg(avg);
							break;
						case "2":
							scheduledDataRecord.setTradeopen(open);
							scheduledDataRecord.setTradelast(last);
							scheduledDataRecord.setTrademin(min);
							scheduledDataRecord.setTrademax(max);
							scheduledDataRecord.setTradeavg(avg);
							break;
					}
				}
				for(String source : map.keySet()) {
					SQLUtils.saveScheduledDataRecord(map.get(source), sourceAndTicker.get(source).get(0), sourceAndTicker.get(source).get(1), true);
				}
			} catch (Exception e) {
			} finally {
				try {
					input.close();
				} catch (Exception e2) {
				}
			}
		}
		uploading = false;
		return true;
	}
	
	public static void reset() {
		cancelHistoricalData();
		currentTickerId = -1;
		currentDateTime = null;
		running = false;
	}
	
	public static void next() {
		try {
			if (isConnected() && EClientSocketUtils.contracts != null && EClientSocketUtils.contracts.size() > 0 && running) {
				if (currentTickerId == -1) currentTickerId = 0;
				Contract contract = EClientSocketUtils.contracts.get(currentTickerId);
				if(currentDateTime != null) {
					String endTimeStr = contract.endDate + " " + contract.endTime;
					Date endTime = DateUtils.yyyyMMddHHmmss().parse(endTimeStr);
					if (currentDateTime.after(endTime)) {
						currentTickerId++;
						if (EClientSocketUtils.contracts.size() == currentTickerId) {
							// stop
							IndexController.status = "Uploading data to database....";
							upload(YosonEWrapper.getHistoricalDataLogPath());
							reset();
							IndexController.status = "Download historical data completed!";
						} else {
							contract = EClientSocketUtils.contracts.get(currentTickerId);
							currentDateTime = null;
						}
					} else {
						currentDateTime = DateUtils.addSecond(currentDateTime, requestSecs);
						if (currentDateTime.after(endTime)) currentDateTime = DateUtils.addSecond(endTime, 1);//include the last second			
					}
				}
				if (currentDateTime == null) {
					currentDateTime = DateUtils.yyyyMMddHHmmss().parse(contract.startDate + " " + contract.startTime);
					currentDateTime = DateUtils.addSecond(currentDateTime, requestSecs);
					String endTimeStr = contract.endDate + " " + contract.endTime;
					Date endTime = DateUtils.yyyyMMddHHmmss().parse(endTimeStr);
					if (currentDateTime.after(endTime)) currentDateTime = DateUtils.addSecond(endTime, 1);//include the last second
				}
				int hours = (TimeZone.getTimeZone("Hongkong").getRawOffset() / (3600 * 1000)) - (TimeZone.getTimeZone(connectionInfo.getTimeZone()).getRawOffset() / (3600 * 1000));
				currentDateTime = DateUtils.addSecond(currentDateTime, hours * 3600);
				String endDateTime = DateUtils.yyyyMMdd2().format(currentDateTime) + " " + DateUtils.HHmmss().format(currentDateTime) + " HKT";				
				socket.reqHistoricalData(currentTickerId * identify, contract, endDateTime, "100 S", "1 secs", "BID", 0, 1, new Vector<TagValue>());
				socket.reqHistoricalData(currentTickerId * identify + 1, contract, endDateTime, "100 S", "1 secs", "ASK", 0, 1, new Vector<TagValue>());
				socket.reqHistoricalData(currentTickerId * identify + 2, contract, endDateTime, "100 S", "1 secs", "TRADES", 0, 1, new Vector<TagValue>());
			}
		} catch (Exception e) {
			reset();
		}
	} 
}
