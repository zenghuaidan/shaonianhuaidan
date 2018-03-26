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
		}
		if (!isConnected() && EClientSocketUtils.connectionInfo != null) {
			socket = new EClientSocket(new YosonEWrapper());
			socket.eConnect(connectionInfo.getHost(), connectionInfo.getPort(), connectionInfo.getClientId());
		}
		return isConnected();
	}	
	
	public static boolean reconnectUsingPreConnectSetting()
	{		
		if (!isConnected()) {
			connect(EClientSocketUtils.connectionInfo);				
		}
		return isConnected();
	}
	
	public static boolean disconnect()
	{
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
	
	public static void cancelHistoricalData(int tickerId)
	{
		if(socket != null)
			socket.cancelHistoricalData(tickerId);
	}
	
	public static void requestData(List<Contract> contracts) {					
		// stop previous market data if have
		if (EClientSocketUtils.contracts != null && EClientSocketUtils.contracts.size() > 0) {
			for(int i = 0; i <= EClientSocketUtils.contracts.size() - 1; i++) {
				EClientSocketUtils.cancelHistoricalData(i);
			}
		}
		EClientSocketUtils.contracts = contracts;
		// TODO				
	}
}
