package com.yoson.sql;

import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.StringType;
import org.springframework.web.context.WebApplicationContext;

import com.mysql.jdbc.StringUtils;
import com.opencsv.CSVReader;
import com.yoson.date.BrokenDate;
import com.yoson.date.DateUtils;
import com.yoson.model.MainUIParam;
import com.yoson.model.ScheduleData;
import com.yoson.task.BackTestTask;
import com.yoson.web.InitServlet;

public class SQLUtils {
    
	public static void initScheduleData(MainUIParam mainUIParam) {
		Session session = getSession();
		long lastMinutes = mainUIParam.getLastNumberOfMinutesClearPosition() * 60 * 1000;
		long lunchLastMinutes = mainUIParam.getLunchLastNumberOfMinutesClearPosition() * 60 * 1000;
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
			List<String> sdatas = sqlQuery.list();
			for (String sdata : sdatas) {
				ScheduleData sData = new ScheduleData(sdata.split(",")[0], sdata.split(",")[1], sdata.split(",")[2], sdata.split(",")[3], sdata.split(",")[4]);
				String dateStr = sData.getDateStr();
				BackTestTask.sumOfLastTrade.put(dateStr, BackTestTask.sumOfLastTrade.get(dateStr) == null ? sData.getLastTrade() : BackTestTask.sumOfLastTrade.get(dateStr) + sData.getLastTrade());
				if (BackTestTask.rowData.containsKey(dateStr)) {
					BackTestTask.rowData.get(dateStr).add(sData);
				} else {
					List<ScheduleData> dataList = new ArrayList<ScheduleData>();
					dataList.add(sData);
					BackTestTask.rowData.put(dateStr, dataList);
				}
//				System.out.println(sdata.split(",")[0] + " " + sdata.split(",")[1]);
				initCheckMarketTime(mainUIParam, sData.getTimeStr(), lastMinutes, lunchLastMinutes);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	private static Session getSession() {
		WebApplicationContext wc = InitServlet.getWc();
		SessionFactory sessionFactory = (SessionFactory)wc.getBean("sessionFactory");
		Session session = sessionFactory.openSession();
		return session;
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
	
	private static void initCheckMarketTime(MainUIParam mainUIParam, String timeStr, long lastMinutes, long lunchLastMinutes) throws ParseException {
		long current = DateUtils.HHmmss.parse(timeStr).getTime();
		if (BackTestTask.marketTimeMap.containsKey(timeStr)) {
			return;
		}
		
		long morningStartTime = DateUtils.HHmmss.parse(mainUIParam.getMarketStartTime()).getTime();
		long lunch_start_time = DateUtils.HHmmss.parse(mainUIParam.getLunchStartTimeFrom()).getTime();
		long lunch_end_time = DateUtils.HHmmss.parse(mainUIParam.getLunchStartTimeTo()).getTime();
		long market_close_time = DateUtils.HHmmss.parse(mainUIParam.getMarketCloseTime()).getTime();
		
		if (current < (morningStartTime) || current >= market_close_time - lastMinutes)
		{
			BackTestTask.marketTimeMap.put(timeStr, 0);
		} else // Within the trading hours
		{
			if ((current < (lunch_start_time - lunchLastMinutes)) || (current >= lunch_end_time)) {
				BackTestTask.marketTimeMap.put(timeStr, 1);
			} else // In exactly lunch time (not in the trading hour)
			{
				BackTestTask.marketTimeMap.put(timeStr, 0);
			}
		}
	}
	
}