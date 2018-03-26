package com.yoson.tws;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.ib.client.TagValue;
import com.yoson.date.DateUtils;

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
		if(socket != null)
			socket.cancelHistoricalData(currentTickerId);
	}
	
	public static Date currentDateTime = null;
	public static int currentTickerId = -1;
	public static boolean requesting = false;
	public static boolean running = false;
	public static int requestSecs = 100;
	public static void requestData(List<Contract> contracts) {
		EClientSocketUtils.contracts = contracts;
		reset();
		running = true;
		next();
	}
	
	public static void reset() {
		cancelHistoricalData();
		currentTickerId = -1;
		currentDateTime = null;
		requesting = false;
		running = false;
	}
	
	public static void next() {
		try {
			if (isConnected() && EClientSocketUtils.contracts != null && EClientSocketUtils.contracts.size() > 0 && running) {
				if (currentTickerId == -1) currentTickerId = 0;
				Contract contract = EClientSocketUtils.contracts.get(currentTickerId);
				if(currentDateTime != null) {
					String endTimeStr = contract.endDate + " " + contract.endTime;
					if (DateUtils.yyyyMMddHHmmss().format(currentDateTime).equals(endTimeStr)) {
						currentTickerId++;
						if (EClientSocketUtils.contracts.size() == currentTickerId) {
							// stop
							reset();
						} else {
							contract = EClientSocketUtils.contracts.get(currentTickerId);
							currentDateTime = null;
						}
					} else {
						currentDateTime = DateUtils.addSecond(currentDateTime, requestSecs);
						Date endTime = DateUtils.yyyyMMddHHmmss().parse(endTimeStr);
						if (currentDateTime.after(endTime)) currentDateTime = endTime;						
					}
				}
				if (currentDateTime == null) {
					currentDateTime = DateUtils.yyyyMMddHHmmss().parse(contract.startDate + " " + contract.startTime);									
				}
				int hours = TimeZone.getTimeZone("Hongkong").getRawOffset() / (3600 * 1000) - TimeZone.getTimeZone(connectionInfo.getTimeZone()).getRawOffset() / (3600 * 1000);
				currentDateTime = DateUtils.addSecond(currentDateTime, hours * 3600);
				String endDateTime = DateUtils.yyyyMMdd2().format(currentDateTime) + " " + DateUtils.HHmmss().format(currentDateTime) + " HKT";
				socket.reqHistoricalData(currentTickerId, contract, endDateTime, "100 S", "1 secs", "BID", 0, 1, new Vector<TagValue>());
			}
		} catch (Exception e) {
			reset();
		}
	} 
}
