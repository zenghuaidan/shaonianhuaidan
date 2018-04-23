package com.yoson.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.yoson.tws.ScheduledDataRecord;

public class SQLUtils {
    
	private static Session getSession() {
		try {
			SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
			return sessionFactory.openSession();			
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void saveScheduledDataRecord(Map<String, ScheduledDataRecord> scheduledDataRecords, String source, boolean isReplace) {
		Session session = null;
		try {
			session = getSession();
			session.beginTransaction();
			String sql = (isReplace ? "REPLACE INTO " : "INSERT IGNORE INTO ") + " schedule_data(ticker,date,time,bidopen,bidavg,bidlast,bidmax,bidmin,askopen,askavg,asklast,askmax,askmin,tradeopen,tradeavg,tradelast,trademax,trademin,source) VALUES";
			List<String> values = new ArrayList<String>();
			for (ScheduledDataRecord scheduledDataRecord : scheduledDataRecords.values()) {
				String dateTimeStr = scheduledDataRecord.getTime();
				String dateStr = dateTimeStr.split(" ")[0];
				String timeStr = dateTimeStr.split(" ")[1];
				values.add("('"+ source +"','" + dateStr + "','" + timeStr + "'," 
						+ scheduledDataRecord.getBidopen() + "," + scheduledDataRecord.getBidavg() + "," + scheduledDataRecord.getBidlast() + "," + scheduledDataRecord.getBidmax() + "," + scheduledDataRecord.getBidmin() + ","
						+ scheduledDataRecord.getAskopen() + "," + scheduledDataRecord.getAskavg() + "," + scheduledDataRecord.getAsklast() + "," + scheduledDataRecord.getAskmax() + "," + scheduledDataRecord.getAskmin() + ","
						+ scheduledDataRecord.getTradeopen() + "," + scheduledDataRecord.getTradeavg() + "," + scheduledDataRecord.getTradelast() + "," + scheduledDataRecord.getTrademax() + "," + scheduledDataRecord.getTrademin() + ","
						+ "'TWS_" + source +"')");
				if(values.size() == 10000) {
					session.createSQLQuery(sql + String.join(",", values)).executeUpdate();
					values.clear();
				}
			}
			if (values.size() > 0) {
				session.createSQLQuery(sql + String.join(",", values)).executeUpdate();							
			}
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				session.close();				
			} catch (Exception e) {
			}
		}
	}
	
}