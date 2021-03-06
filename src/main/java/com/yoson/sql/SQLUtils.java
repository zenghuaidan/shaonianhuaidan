package com.yoson.sql;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.springframework.web.context.WebApplicationContext;

import com.mysql.jdbc.StringUtils;
import com.opencsv.CSVReader;
import com.yoson.date.BrokenDate;
import com.yoson.date.DateUtils;
import com.yoson.model.MainUIParam;
import com.yoson.tws.ScheduledDataRecord;
import com.yoson.web.InitServlet;

public class SQLUtils {
    
	public static List<String> initScheduleData(MainUIParam mainUIParam) {
		Session session = getSession();
		List<BrokenDate> datePeriods = mainUIParam.getBrokenDateList();
		String source = mainUIParam.getSource(); 
		String askDataField = mainUIParam.getAskDataField(); 
		String bidDataField = mainUIParam.getBidDataField(); 
		String tradeDataField = mainUIParam.getTradeDataField();
		try {
			String sql = "select CONCAT_WS(',',date,time, " + askDataField + ", " + bidDataField + ", " + tradeDataField + ") as sdata from schedule_data where source = '" + source + "'";

			if (datePeriods != null && datePeriods.size() > 0) {
				List<String> datePeriodCriteria = new ArrayList<String>();
				for (BrokenDate datePeriod : datePeriods) {
					datePeriodCriteria.add(" (date >= '" + datePeriod.from + "' and date <= '" + datePeriod.to + "')");
				}
				sql += " and (" + String.join(" or ", datePeriodCriteria) + ")";
			}
			sql += " order by date asc, time asc";
			SQLQuery sqlQuery = session.createSQLQuery(sql).addScalar("sdata", StringType.INSTANCE);
			return sqlQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		return new ArrayList<String>();
	}

	private static Session getSession() {
		WebApplicationContext wc = InitServlet.getWc();
		SessionFactory sessionFactory = (SessionFactory)wc.getBean("sessionFactory");
		Session session = sessionFactory.openSession();
		return session;
	}
	
	public static int checkScheduledDataExisting(String from, String to, String source) {
		Session session = getSession();		
		try {
//			String sql = "select distinct CONCAT_WS(' ',date,time) as sdata from schedule_data where source = '" + source + "' and (date >= '" + from + "' and date <= '" + to + "') order by date asc, time asc";
			String sql = "select count(*) as totalCount from schedule_data2 where ticker = '" + source + "' and (date >= '" + from + "' and date <= '" + to + "')";
			SQLQuery sqlQuery = session.createSQLQuery(sql).addScalar("totalCount", IntegerType.INSTANCE);
			return (Integer)sqlQuery.uniqueResult();			
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			session.close();
		}
	}
	
	public static List<String> getSources() {
		Session session = getSession();		
		try {
			String sql = "select distinct source from schedule_data order by source asc";
			SQLQuery sqlQuery = session.createSQLQuery(sql);
			return sqlQuery.list();			
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<String>();
		} finally {
			session.close();
		}
	}
	
	public static void saveScheduledDataRecord(List<ScheduledDataRecord> scheduledDataRecords, String dataStartTime, String dataEndTime, String source, boolean isReplace) {
		Session session = getSession();
		try {
			String sql = (isReplace ? "REPLACE INTO " : "INSERT IGNORE INTO ") + " schedule_data2(ticker,date,time,bidavg,bidlast,bidmax,bidmin,askavg,asklast,askmax,askmin,tradeavg,tradelast,trademax,trademin,source) VALUES";
			List<String> values = new ArrayList<String>();
			for (ScheduledDataRecord scheduledDataRecord : scheduledDataRecords) {
				if(DateUtils.isValidateTime(DateUtils.yyyyMMddHHmmss2().parse(scheduledDataRecord.getTime()), dataStartTime, dataEndTime)) {
					String dateTimeStr = scheduledDataRecord.getTime();
					String dateStr = DateUtils.getDateStr(dateTimeStr);
					String timeStr = DateUtils.getTimeStr(dateTimeStr);
					values.add("('"+ source +"','" + dateStr + "','" + timeStr + "'," 
							+ scheduledDataRecord.getBidavg() + "," + scheduledDataRecord.getBidlast() + "," + scheduledDataRecord.getBidmax() + "," + scheduledDataRecord.getBidmin() + ","
							+ scheduledDataRecord.getAskavg() + "," + scheduledDataRecord.getAsklast() + "," + scheduledDataRecord.getAskmax() + "," + scheduledDataRecord.getAskmin() + ","
							+ scheduledDataRecord.getTradeavg() + "," + scheduledDataRecord.getTradelast() + "," + scheduledDataRecord.getTrademax() + "," + scheduledDataRecord.getTrademin() + ","
							+ "'BBG_" + source +"')");
					if(values.size() == 10000) {
						session.createSQLQuery(sql + String.join(",", values)).executeUpdate();
						values.clear();
					}
				}
			}
			if (values.size() > 0) {
				session.createSQLQuery(sql + String.join(",", values)).executeUpdate();							
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	public static String saveTestSetResult(String path, String version) {
		File file = new File(path);
		if (!file.exists())
			return "Could not find the file.";
		Session session = getSession();
		try {
			CSVReader csvReader = new CSVReader(new FileReader(file), ',', '\n', 0);
			String [] lines;
			String sqlSchema = null;
			String tableName = "Idea2_" + version.replaceAll(" ", "");
			StringBuilder sql = new StringBuilder();
			boolean hasRecord = false;
			int columCount = 0;
			while ((lines = csvReader.readNext()) != null)  {
				if (sqlSchema == null) {
					List<String> schemaColumns = new ArrayList<String>();
					List<String> valueColumns = new ArrayList<String>();
					for (String column : lines) {
						if (!StringUtils.isNullOrEmpty(column)) {
							schemaColumns.add("`" + column.trim() + "` varchar(255) DEFAULT NULL");
							valueColumns.add("`" + column.trim() + "`");
							columCount++;
						}
					}
					sqlSchema = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + String.join(",", schemaColumns) + ")";
					sql.append("INSERT IGNORE INTO " + tableName + " (" + String.join(",", valueColumns) + ") VALUES ");
				} else {
					List<String> columnValues = new ArrayList<String>();
					for (int i = 0; i < columCount; i++) {
						columnValues.add("'" + lines[i].trim() + "'");
					}
					sql.append("(" + String.join(",", columnValues) + "),");
					hasRecord = true;
				}
			}
			csvReader.close();
			
			if (sqlSchema != null)
				session.createSQLQuery(sqlSchema).executeUpdate();
			if (hasRecord) {
				session.createSQLQuery("truncate table " + tableName).executeUpdate();				
				session.createSQLQuery(sql.substring(0, sql.length() - 1).toString()).executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		return "";
	}
	
}