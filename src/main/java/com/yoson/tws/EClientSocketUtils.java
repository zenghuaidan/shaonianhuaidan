package com.yoson.tws;

import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FilenameUtils;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.ib.client.TagValue;
import com.yoson.csv.BackTestCSVWriter;
import com.yoson.date.DateUtils;
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
	
	public static void requestDate(Date now, List<Contract> contracts) {
		if(EClientSocketUtils.isConnected()) {							
			// stop previous market data if have
			if (EClientSocketUtils.contracts != null && EClientSocketUtils.contracts.size() > 0) {
				for(int i = 0; i <= EClientSocketUtils.contracts.size() - 1; i++) {
					EClientSocketUtils.cancelMktData(i);
				}
			}
			
			EClientSocketUtils.contracts = contracts;
			YosonEWrapper.priceMap = new ConcurrentHashMap<String, Double>();
			EClientSocketUtils.id = DateUtils.yyyyMMddHHmmss2().format(now);
			String folder = EClientSocketUtils.initAndReturnLiveDataFolder();
			
			// start new market data
			StringBuilder log = new StringBuilder();
			for(int i = 0; i <= EClientSocketUtils.contracts.size() - 1; i++) {
				EClientSocketUtils.socket.reqMktData(i, EClientSocketUtils.contracts.get(i), null, false, new Vector<TagValue>());	
				log.append((i + 1) + ":" + EClientSocketUtils.contracts.get(i).startTime + "," + EClientSocketUtils.contracts.get(i).endTime + System.lineSeparator());
			}
			
			BackTestCSVWriter.writeText(FilenameUtils.concat(folder, "log.txt"), log.toString(), true);
			EClientSocketUtils.socket.reqCurrentTime();
		}
	}
}
