package com.yoson.tws;

import java.util.List;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.yoson.web.InitServlet;

public class EClientSocketUtils {
	public static EClientSocket socket;
	public static ConnectionInfo connectionInfo;
	public static List<Contract> contracts;
	public static String id;	
	
	public static boolean connect(ConnectionInfo connectionInfo)
	{
		EClientSocketUtils.connectionInfo = connectionInfo;
		if (!isConnected()) {
			socket = new EClientSocket(new YosonEWrapper());
			socket.eConnect(connectionInfo.getHost(), connectionInfo.getPort(), connectionInfo.getClientId());
		}
		return isConnected();
	}	
	
	public static boolean reconnectUsingPreConnectSetting()
	{
		return connect(connectionInfo);
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
	
	public static void cancelMktData(int tickerId)
	{
		if(socket != null)
			socket.cancelMktData(tickerId);
	}			

	public static String initAndReturnLiveDataFolder() {
		return InitServlet.createFoderAndReturnPath(InitServlet.createLiveDataFoderAndReturnPath(), id);
	}
}
