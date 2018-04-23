package com.yoson.sql;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.yoson.date.DateUtils;
import com.yoson.tws.Record;
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
	
	public static void saveRawDataRecord(List<Record> rawDataRecords, String source, boolean isReplace) {
		Session session = null;
		try {
			session = getSession();
			session.beginTransaction();
			String sql = (isReplace ? "REPLACE INTO " : "INSERT IGNORE INTO ") + " rawdata(date,time,price,size,type,source) VALUES";
			List<String> values = new ArrayList<String>();
			for (Record rawDataRecord : rawDataRecords) {
				String dateStr = DateUtils.yyyyMMdd().format(rawDataRecord.getTime());
				String timeStr = DateUtils.HHmmss().format(rawDataRecord.getTime());
				values.add("('"+ dateStr + "','" + timeStr + "'," 
						+ rawDataRecord.getData() + "," + rawDataRecord.getSize() + ",'" + rawDataRecord.getType() +"','" + source +"')");
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
	
	public static void saveScheduledDataRecord(List<ScheduledDataRecord> scheduledDataRecords, String source, boolean isReplace) {
		Session session = null;
		try {
			session = getSession();
			session.beginTransaction();
			String sql = (isReplace ? "REPLACE INTO " : "INSERT IGNORE INTO ") + " schedule_data(ticker,date,time,bidopen,bidavg,bidlast,bidmax,bidmin,askopen,askavg,asklast,askmax,askmin,tradeopen,tradeavg,tradelast,trademax,trademin,source) VALUES";
			List<String> values = new ArrayList<String>();
			for (ScheduledDataRecord scheduledDataRecord : scheduledDataRecords) {
				String dateTimeStr = scheduledDataRecord.getTime();
				String dateStr = DateUtils.getDateStr(dateTimeStr);
				String timeStr = DateUtils.getTimeStr(dateTimeStr);
				values.add("('"+ source +"','" + dateStr + "','" + timeStr + "'," 
						+ scheduledDataRecord.getBidopen() + "," + scheduledDataRecord.getBidavg() + "," + scheduledDataRecord.getBidlast() + "," + scheduledDataRecord.getBidmax() + "," + scheduledDataRecord.getBidmin() + ","
						+ scheduledDataRecord.getAskopen() + "," + scheduledDataRecord.getAskavg() + "," + scheduledDataRecord.getAsklast() + "," + scheduledDataRecord.getAskmax() + "," + scheduledDataRecord.getAskmin() + ","
						+ scheduledDataRecord.getTradeopen() + "," + scheduledDataRecord.getTradeavg() + "," + scheduledDataRecord.getTradelast() + "," + scheduledDataRecord.getTrademax() + "," + scheduledDataRecord.getTrademin() + ","
						+ "'BBG_" + source +"')");
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