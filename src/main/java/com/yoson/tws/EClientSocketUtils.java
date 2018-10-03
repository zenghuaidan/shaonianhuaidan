package com.yoson.tws;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.FilenameUtils;

import com.google.gson.Gson;
import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.ib.client.Order;
import com.ib.client.TagValue;
import com.yoson.cms.controller.IndexController;
import com.yoson.csv.BackTestCSVWriter;
import com.yoson.date.DateUtils;
import com.yoson.web.InitServlet;

public class EClientSocketUtils {
	private static EClientSocket socket;
	public static ConnectionInfo connectionInfo;
	public static Contract contract;
	public static List<Strategy> strategies = new CopyOnWriteArrayList<Strategy>();
	public static String id;
	public static String CONTRACT = "contract.txt";
	public static List<String> tradeLogs;
	public static int totalBuy;
	public static int totalSell;
	public static boolean hasIncomingMarketData = false;
	
	public static boolean connect(ConnectionInfo connectionInfo)
	{
		EClientSocketUtils.connectionInfo = connectionInfo;
		EClientSocketUtils.connectionInfo.setPaperTrading(true);
		EClientSocketUtils.connectionInfo.setCancelAndRetryIfOrderExceedTolerantTime(true);
		if (!isConnected()) {
			socket = new EClientSocket(new YosonEWrapper());
			socket.eConnect(connectionInfo.getHost(), connectionInfo.getPort(), connectionInfo.getClientId());
		}
		return isConnected();
	}
	
	public static void cancelOrder(int id) {
		socket.cancelOrder(id);
	}
	
	public static void placeOrder(int id, Order order) {
		socket.placeOrder(id, contract, order);
		socket.reqIds(1);
		reqAllOpenOrders();
	}
	
	public static void reqAllOpenOrders() {		
		socket.reqAllOpenOrders();
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
		} finally {
			if(EClientSocketUtils.strategies != null) {
				for (Strategy strategy : EClientSocketUtils.strategies) {
//					strategy.inactive();
					strategy.setActive(false);
				}			
			}
			YosonEWrapper.clear();
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
	
	public static String genExpiry(String startTime) {
		try {
			List<String> expiryDates = IndexController.getExpiryDates();
			String startDate = startTime.split(" ")[0];
			Date startTimeDate = DateUtils.yyyyMMddHHmm().parse(startTime);
			if(expiryDates.contains(startDate)) {
		        Calendar rightNow = Calendar.getInstance();  
		        rightNow.setTime(startTimeDate);  
		        rightNow.add(Calendar.MONTH, 1);  		        				
				return DateUtils.yyyyMM().format(rightNow.getTime());
			} else {
				return DateUtils.yyyyMM().format(startTimeDate);
			}
		} catch (Exception e) {
			return "";
		}
	}
	
	public static boolean lunchBTStart = false;
	public static boolean reqMktData(Contract contract) {
		if(!isConnected()) {
			return false;
		}
		EClientSocketUtils.hasIncomingMarketData = false;
		lunchBTStart = false;
		YosonEWrapper.initData();
		EClientSocketUtils.contract = contract;
		EClientSocketUtils.contract.setExpirary(genExpiry(EClientSocketUtils.contract.getStartTime()));
		id = DateUtils.yyyyMMddHHmmss2().format(new Date());
		tradeLogs = new CopyOnWriteArrayList<String>();
		totalBuy = 0;
		totalSell = 0;
		String dataFolder = initAndReturnLiveDataFolder();
//		socket.cancelMktData(tickerId);
		socket.reqMktData(EClientSocketUtils.connectionInfo.getClientId(), contract, null, false, new Vector<TagValue>());
		socket.reqCurrentTime();
		String contractGson = new Gson().toJson(EClientSocketUtils.contract);
		BackTestCSVWriter.writeText(FilenameUtils.concat(dataFolder, CONTRACT), contractGson, false);
		return true;
	}
	
	public static boolean validateContract = true;
	public static void reqContractDetails(Contract contract) {
		if(isConnected()) {
			validateContract = true;
			EClientSocketUtils.contract = contract;
			EClientSocketUtils.contract.setExpirary(genExpiry(EClientSocketUtils.contract.getStartTime()));
			socket.reqContractDetails(EClientSocketUtils.connectionInfo.getClientId(), contract);
		}
	}

	public static String initAndReturnLiveDataFolder() {
		return InitServlet.createFoderAndReturnPath(InitServlet.createLiveDataFoderAndReturnPath(), contract.getSymbol() + "_" + id);
	}
}
