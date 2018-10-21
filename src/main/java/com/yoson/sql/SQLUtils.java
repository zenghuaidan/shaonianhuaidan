package com.yoson.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import com.yoson.date.DateUtils;
import com.yoson.tws.ScheduledDataRecord;

public class SQLUtils {
	public static String SCHEDULE_DATA_TABLE = "schedule_data";

	private static Session getSession() {
		try {
			SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
			return sessionFactory.openSession();			
		} catch (Exception e) {
			return null;
		}
	}
	
	public static int checkScheduledDataExisting(String dateStr, String source, String ticker) {
		Session session = null;		
		try {
			session = getSession();
//			String sql = "select distinct CONCAT_WS(' ',date,time) as sdata from schedule_data where source = '" + source + "' and (date >= '" + from + "' and date <= '" + to + "') order by date asc, time asc";
			String sql = "select count(1) as totalCount from " + SCHEDULE_DATA_TABLE + " where ticker = '" + ticker + "' and source = '" + source + "' and date = '" + dateStr + "'";
			SQLQuery sqlQuery = session.createSQLQuery(sql).addScalar("totalCount", IntegerType.INSTANCE);
			return (Integer)sqlQuery.uniqueResult();			
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			try {
				session.close();				
			} catch (Exception e) {
			}
		}
	}
	
	public static void saveScheduledDataRecord(List<ScheduledDataRecord> scheduledDataRecords, String dataStartTime, String dataEndTime, String source, String ticker, boolean isReplace) {
		if(scheduledDataRecords != null && scheduledDataRecords.size() > 0) {
			List<String> dates = new ArrayList<String>();
			for (ScheduledDataRecord scheduledDataRecord : scheduledDataRecords) {
				String dateTimeStr = scheduledDataRecord.getTime();
				String dateStr = DateUtils.getDateStr(dateTimeStr);
				if(!dates.contains(dateStr)) {
					dates.add(dateStr);
					deleteScheduledDataRecordByDate(dateStr, ticker);
				}
			}
		}
		Session session = null;
		try {
			session = getSession();
			session.beginTransaction();
			String sql = (isReplace ? "REPLACE INTO " : "INSERT IGNORE INTO ")  + SCHEDULE_DATA_TABLE + "(ticker,date,time,bidopen,bidavg,bidlast,bidmax,bidmin,askopen,askavg,asklast,askmax,askmin,tradeopen,tradeavg,tradelast,trademax,trademin,source) VALUES";
			List<String> values = new ArrayList<String>();
			for (ScheduledDataRecord scheduledDataRecord : scheduledDataRecords) {
				if(DateUtils.isValidateTime(DateUtils.yyyyMMddHHmmss2().parse(scheduledDataRecord.getTime()), dataStartTime, dataEndTime)) {
					String dateTimeStr = scheduledDataRecord.getTime();
					String dateStr = DateUtils.getDateStr(dateTimeStr);
					String timeStr = DateUtils.getTimeStr(dateTimeStr);
					values.add("('"+ ticker +"','" + dateStr + "','" + timeStr + "'," 
							+ scheduledDataRecord.getBidopen() + "," + scheduledDataRecord.getBidavg() + "," + scheduledDataRecord.getBidlast() + "," + scheduledDataRecord.getBidmax() + "," + scheduledDataRecord.getBidmin() + ","
							+ scheduledDataRecord.getAskopen() + "," + scheduledDataRecord.getAskavg() + "," + scheduledDataRecord.getAsklast() + "," + scheduledDataRecord.getAskmax() + "," + scheduledDataRecord.getAskmin() + ","
							+ scheduledDataRecord.getTradeopen() + "," + scheduledDataRecord.getTradeavg() + "," + scheduledDataRecord.getTradelast() + "," + scheduledDataRecord.getTrademax() + "," + scheduledDataRecord.getTrademin() + ","
							+ "'" + source +"')");
					if(values.size() == 10000) {
						session.createSQLQuery(sql + String.join(",", values)).executeUpdate();
						values.clear();
					}
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
				e.printStackTrace();
			}
		}
	}
	
	public static String getScheduledDataRecordByDate(String dateStr, String ticker) {
		Session session = null;
		List<String> values = new ArrayList<String>();
		values.add("ticker,date,time,bidopen,bidavg,bidlast,bidmax,bidmin,askopen,askavg,asklast,askmax,askmin,tradeopen,tradeavg,tradelast,trademax,trademin,source");
		try {
			session = getSession();
			String sql = "select CONCAT(ticker,',',date,',',time,',',bidopen,',',bidavg,',',bidlast,',',bidmax,',',bidmin,',',askopen,',',askavg,',',asklast,',',askmax,',',askmin,',',tradeopen,',',tradeavg,',',tradelast,',',trademax,',',trademin,',',source) as sdata from " + SCHEDULE_DATA_TABLE + " where date='" + dateStr + "' and ticker='" + ticker+ "'";
			sql += " order by date asc, time asc";
			SQLQuery sqlQuery = session.createSQLQuery(sql).addScalar("sdata", StringType.INSTANCE);
			values.addAll(sqlQuery.list());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				session.close();				
			} catch (Exception e) {
			}
		}
		return String.join(System.lineSeparator(), values);
	}
	
	public static void deleteScheduledDataRecordByDate(String dateStr, String ticker) {
		Session session = null;
		try {
			session = getSession();
			session.beginTransaction();
			String sql = "delete from " + SCHEDULE_DATA_TABLE + " where date='" + dateStr + "' and ticker='" + ticker+ "'";
			SQLQuery sqlQuery = session.createSQLQuery(sql).addScalar("sdata", StringType.INSTANCE);
			sqlQuery.executeUpdate();
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
	
	public static String getSummary(String ticker) {
		Session session = null;
		List<String> values = new ArrayList<String>();
		values.add("date,total,start_time,end_time,source,ticker,day_min,day_max,day_amp,previousDay_close,avg trade,last trade,first trade,performance");
		try {
			session = getSession();
			String sql = "SELECT  " +
			        "CONCAT(date,',', " +
			        "COUNT(0),',', " +
			        "MIN(time),',', " +
			        "MAX(time),',', " +
			        "source,',', " +
			        "ticker,',', " +
			        "MIN(tradelast),',', " +
			        "MAX(tradelast),',', " +
			        "(MAX(tradelast) - MIN(tradelast)),',', " +
			        "tradelast,',', " +
			        "AVG(tradelast),',', " +
			        "SUBSTRING_INDEX(GROUP_CONCAT(CAST(tradelast AS CHAR CHARSET UTF8) " +
			                    "ORDER BY time DESC " +
			                    "SEPARATOR ','), " +
			                "',', " +
			                "1),',', " +
			        "SUBSTRING_INDEX(GROUP_CONCAT(tradelast " +
			                    "SEPARATOR ','), " +
			                "',', " +
			                "1),',', " +
			        "(SUBSTRING_INDEX(GROUP_CONCAT(CAST(tradelast AS CHAR CHARSET UTF8) " +
			                    "ORDER BY time DESC " +
			                    "SEPARATOR ','), " +
			                "',', " +
			                "1) - SUBSTRING_INDEX(GROUP_CONCAT(tradelast " +
			                    "SEPARATOR ','), " +
			                "',', " +
			                "1))) as sdata " +
			    "FROM " + SCHEDULE_DATA_TABLE +
			    " WHERE ticker = '" + ticker + "' " +
			    "GROUP BY date;";
			System.out.println(sql);
			
			if(!StringUtils.isBlank(ticker)) {
				SQLQuery sqlQuery = session.createSQLQuery(sql).addScalar("sdata", StringType.INSTANCE);
				values.addAll(sqlQuery.list());				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				session.close();				
			} catch (Exception e) {
			}
		}
		return String.join(System.lineSeparator(), values);
	}

	public static List<String> getTickers() {
		Session session = null;		
		try {
			session = getSession();
			String sql = "select distinct ticker from  " + SCHEDULE_DATA_TABLE + "  order by ticker asc";
			SQLQuery sqlQuery = session.createSQLQuery(sql);
			return sqlQuery.list();			
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<String>();
		} finally {
			try {
				session.close();				
			} catch (Exception e) {
			}
		}
	}
	
}