package com.yoson.tws;

import java.util.List;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;

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
	
	public static void reset() {
		cancelHistoricalData();
		currentTickerId = 1;
		requesting = false;
		running = false;
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
	
	public static String currentDateTime = null;
	public static int currentTickerId = -1;
	public static boolean requesting = false;
	public static boolean running = false;
	public static void requestData(List<Contract> contracts) {
		EClientSocketUtils.contracts = contracts;
		reset();
		running = true;
	}
	
	public static void next() {
		if (currentTickerId == -1) currentTickerId = 0;
		
	} 
}
