package com.yoson.tws;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

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
		if(EClientSocketUtils.connectionInfo == null 
				|| !EClientSocketUtils.connectionInfo.getHost().equals(connectionInfo.getHost())
				|| EClientSocketUtils.connectionInfo.getPort() != connectionInfo.getPort()
				|| EClientSocketUtils.connectionInfo.getClientId() != connectionInfo.getClientId()
				|| EClientSocketUtils.connectionInfo.getTimeZone() != connectionInfo.getTimeZone()
			) {			
			EClientSocketUtils.disconnect();
			EClientSocketUtils.connectionInfo = connectionInfo;
			List<String> lines = new ArrayList<String>();
			lines.add(connectionInfo.getHost());
			lines.add(connectionInfo.getPort() + "");
			lines.add(connectionInfo.getClientId() +"");
			lines.add(connectionInfo.getTimeZone());
			FileOutputStream output = null;
			try {
				output = new FileOutputStream(new File(FilenameUtils.concat(InitServlet.createUploadFoderAndReturnPath(), "connect.txt")));
				IOUtils.writeLines(lines, System.lineSeparator(), output);				
			}catch (Exception e) {
			} finally {
				try {
					output.close();
				} catch (Exception e2) {
				}
			}
		}
//		if (!isConnected()) {
//			socket = new EClientSocket(new YosonEWrapper());
//			socket.eConnect(connectionInfo.getHost(), connectionInfo.getPort(), connectionInfo.getClientId());
//		}
//		return isConnected();
		return true;
	}	
	
	public static boolean reconnectUsingPreConnectSetting()
	{		
		if (!isConnected()) {
			if (connectionInfo == null) {
				connectionInfo = ConnectionInfo.getDefaultConnectionInfo();
			}
			socket = new EClientSocket(new YosonEWrapper());
			socket.eConnect(connectionInfo.getHost(), connectionInfo.getPort(), connectionInfo.getClientId());				
		}
		return isConnected();
	}
	
	public static boolean disconnect()
	{
		try {
			cancelData(contracts);
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
	
	public static void cancelData(List<Contract> contracts) {
		if(EClientSocketUtils.isConnected()) {							
			// stop previous market data if have
			if (EClientSocketUtils.contracts != null && EClientSocketUtils.contracts.size() > 0) {
				for(int i = 0; i <= EClientSocketUtils.contracts.size() - 1; i++) {
					EClientSocketUtils.cancelMktData(i);
				}
			}
		}
		EClientSocketUtils.id = null;
		EClientSocketUtils.contracts = contracts;
	}
	
	public static void requestData(Date now) {
		if (!EClientSocketUtils.isConnected())
			EClientSocketUtils.reconnectUsingPreConnectSetting();		
		if(EClientSocketUtils.isConnected() && StringUtils.isEmpty(EClientSocketUtils.id)) {													
			YosonEWrapper.priceMap = new ConcurrentHashMap<String, Double>();
			EClientSocketUtils.id = DateUtils.yyyyMMdd().format(now);
			String folder = EClientSocketUtils.initAndReturnLiveDataFolder();
			
			// start new market data
			StringBuilder log = new StringBuilder();
			for(int i = 0; i <= EClientSocketUtils.contracts.size() - 1; i++) {
				EClientSocketUtils.socket.reqMktData(i, EClientSocketUtils.contracts.get(i), null, false, new Vector<TagValue>());	
				log.append((i + 1) + ":" + EClientSocketUtils.contracts.get(i).startTime + "," + EClientSocketUtils.contracts.get(i).endTime + System.lineSeparator());
			}
			
			BackTestCSVWriter.writeText(FilenameUtils.concat(folder, "log.txt"), log.toString(), false);
			EClientSocketUtils.socket.reqCurrentTime();
		}
	}
}
