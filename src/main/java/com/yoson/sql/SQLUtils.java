package com.yoson.sql;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.StringType;

import com.mysql.jdbc.StringUtils;
import com.opencsv.CSVReader;
import com.yoson.csv.BackTestCSVWriter;
import com.yoson.date.BrokenDate;
import com.yoson.date.DateUtils;
import com.yoson.model.MainUIParam;
import com.yoson.model.ScheduleData;

public class SQLUtils {
	
	public static final String schedule_data = "schedule_data";
	
	public static final String expiry_date = "expiry_date";
	
	public static final String schedule_date = "schedule_date";
    
	public static List<String> initScheduleData(MainUIParam mainUIParam) {
		Session session = null;
		List<BrokenDate> datePeriods = mainUIParam.getBrokenDateList();
		String askDataField = mainUIParam.getAskDataField(); 
		String bidDataField = mainUIParam.getBidDataField(); 
		String tradeDataField = mainUIParam.getTradeDataField();
		try {
			session = getSession();
			String sql = "select CONCAT_WS(',',date,time, " + askDataField + ", " + bidDataField + ", " + tradeDataField + ", asklast, bidlast, tradelast) as sdata from  " + schedule_data + "  " 
			+ (mainUIParam.isFromSource() ? (" where source = '" + mainUIParam.getSource() + "'") : (" where ticker = '" + mainUIParam.getTicker() + "'"));
			
			if (datePeriods != null && datePeriods.size() > 0) {
				List<String> datePeriodCriteria = new ArrayList<String>();
				for (BrokenDate datePeriod : datePeriods) {
					datePeriodCriteria.add(" (date >= '" + datePeriod.from + "' and date <= '" + datePeriod.to + "')");
				}
				sql += " and (" + String.join(" or ", datePeriodCriteria) + ")";
			}
			
			if(!StringUtils.isNullOrEmpty(mainUIParam.getMarketStartTime()) 
					&& !StringUtils.isNullOrEmpty(mainUIParam.getLunchStartTimeFrom()) 
					&& !StringUtils.isNullOrEmpty(mainUIParam.getLunchStartTimeTo()) 
					&& !StringUtils.isNullOrEmpty(mainUIParam.getMarketCloseTime())) {
				sql += " and (time >='" + mainUIParam.getMarketStartTime() + "' and time <= '" + mainUIParam.getLunchStartTimeFrom() + "' or time >='" + mainUIParam.getLunchStartTimeTo() + "' and time <= '" + mainUIParam.getMarketCloseTime() + "')";
			}
			sql += " order by date asc, time asc";
			SQLQuery sqlQuery = session.createSQLQuery(sql).addScalar("sdata", StringType.INSTANCE);
			return sqlQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				session.close();				
			} catch (Exception e) {
			}
		}
		return new ArrayList<String>();
	}
	
	public static String getLastMarketDay(String today) {
		List<String> scheduleDates = SQLUtils.getScheduleDates();
		if (scheduleDates != null && scheduleDates.indexOf(today) >= 0 && scheduleDates.indexOf(today) < (scheduleDates.size() - 1)) {
			return scheduleDates.get(scheduleDates.indexOf(today) + 1);
		}
		return "";
	}
	
//	public static String getLastMarketDay(MainUIParam mainUIParam, String today) {
//		if(StringUtils.isNullOrEmpty(today) || mainUIParam.isFromSource() && StringUtils.isNullOrEmpty(mainUIParam.getSource()) || !mainUIParam.isFromSource() && StringUtils.isNullOrEmpty(mainUIParam.getTicker())) return "";
//		Session session = null;				
//		try {
//			session = getSession();
//			String sql = "select max(date) from  " + schedule_data + "  " 
//			+ (mainUIParam.isFromSource() ? (" where source = '" + mainUIParam.getSource() + "'") : (" where ticker = '" + mainUIParam.getTicker() + "'"));
//						
//			sql += " and date < '" + today + "' order by date asc, time asc";
//			
//			SQLQuery sqlQuery = session.createSQLQuery(sql);
//			return DateUtils.yyyyMMdd().format((Date)sqlQuery.uniqueResult());
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				session.close();				
//			} catch (Exception e) {
//			}
//		}
//		return "";
//	}
	
	public static List<ScheduleData> getLastMarketDayScheduleData(MainUIParam mainUIParam, String lastMarketDay, boolean onlyAfternoonData) {
		if(StringUtils.isNullOrEmpty(lastMarketDay) || mainUIParam.isFromSource() && StringUtils.isNullOrEmpty(mainUIParam.getSource()) || !mainUIParam.isFromSource() && StringUtils.isNullOrEmpty(mainUIParam.getTicker())) return new ArrayList<ScheduleData>();
		Session session = null;		
		String askDataField = mainUIParam.getAskDataField(); 
		String bidDataField = mainUIParam.getBidDataField(); 
		String tradeDataField = mainUIParam.getTradeDataField();
		try {
			session = getSession();
			String sql = "select CONCAT_WS(',',date,time, " + askDataField + ", " + bidDataField + ", " + tradeDataField + ", asklast, bidlast, tradelast) as sdata from  " + schedule_data + "  " 
			+ (mainUIParam.isFromSource() ? (" where source = '" + mainUIParam.getSource() + "'") : (" where ticker = '" + mainUIParam.getTicker() + "'"));
						
			sql += " and date = '" + lastMarketDay + "' ";
			
			if(!StringUtils.isNullOrEmpty(mainUIParam.getMarketStartTime()) 
					&& !StringUtils.isNullOrEmpty(mainUIParam.getLunchStartTimeFrom()) 
					&& !StringUtils.isNullOrEmpty(mainUIParam.getLunchStartTimeTo()) 
					&& !StringUtils.isNullOrEmpty(mainUIParam.getMarketCloseTime())) {
				if(onlyAfternoonData)
					sql += " and (time >='" + mainUIParam.getLunchStartTimeTo() + "' and time <= '" + mainUIParam.getMarketCloseTime() + "')";
				else
					sql += " and (time >='" + mainUIParam.getMarketStartTime() + "' and time <= '" + mainUIParam.getLunchStartTimeFrom() + "' or time >='" + mainUIParam.getLunchStartTimeTo() + "' and time <= '" + mainUIParam.getMarketCloseTime() + "')";
			}
			sql += " order by date asc, time asc";
			SQLQuery sqlQuery = session.createSQLQuery(sql).addScalar("sdata", StringType.INSTANCE);
			List<String> sdatas = sqlQuery.list();
			List<ScheduleData> resultDatas = new ArrayList<ScheduleData>();
			for (String sdata : sdatas) {
				ScheduleData sData = new ScheduleData(sdata.split(",")[0], sdata.split(",")[1], sdata.split(",")[2], sdata.split(",")[3], sdata.split(",")[4], sdata.split(",")[5], sdata.split(",")[6], sdata.split(",")[7], mainUIParam.getAskDataField(), mainUIParam.getBidDataField(), mainUIParam.getTradeDataField());
				sData.setLastMarketDayData(true);
				resultDatas.add(sData);
			}
			return resultDatas;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				session.close();				
			} catch (Exception e) {
			}
		}
		return new ArrayList<ScheduleData>();
	}

	private static Session getSession() {
		try {
			SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
			return sessionFactory.openSession();			
		} catch (Exception e) {
			return null;
		}
	}
	
	public static List<String> getSources() {
		Session session = null;		
		try {
			session = getSession();
			String sql = "select distinct source from  " + schedule_data + "  order by source asc";
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
	
	public static List<String> getTickers() {
		Session session = null;		
		try {
			session = getSession();
			String sql = "select distinct ticker from  " + schedule_data + "  order by ticker asc";
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
	
	public static List<String> getExpiryDates() {
		Session session = null;		
		try {
			session = getSession();
			String sql = "select distinct date from  " + expiry_date + "  order by date desc";
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
	
	public static boolean existExpiryDate(String date) {
		Session session = null;		
		try {
			String firstDayOfThisMonth = DateUtils.yyyyMMdd().format(DateUtils.yyyyMMdd2().parse(DateUtils.yyyyMM().format(DateUtils.yyyyMMdd().parse(date)) + "01"));
			session = getSession();
			String sql = "select distinct date from " + expiry_date + " where date <= '" + date + "' and date >= '" + firstDayOfThisMonth + "'";
			SQLQuery sqlQuery = session.createSQLQuery(sql);
			return sqlQuery.list().size() > 0;			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				session.close();				
			} catch (Exception e) {
			}
		}
	}
	
	public static void addExpiryDate(String date) {
		Session session = null;		
		try {
			session = getSession();
			session.getTransaction().begin();
			String sql = "insert into " + expiry_date + "(date) values ('" + date + "')";
			session.createSQLQuery(sql).executeUpdate();
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
	
	public static void deleteExpiryDate(String date) {
		Session session = null;		
		try {
			session = getSession();
			session.getTransaction().begin();
			String sql = "delete from  " + expiry_date + " where date ='" + date + "'";
			session.createSQLQuery(sql).executeUpdate();
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
	
	public static List<String> getScheduleDates() {
		Session session = null;		
		try {
			session = getSession();
			String sql = "select distinct date from  " + schedule_date + "  order by date desc";
			SQLQuery sqlQuery = session.createSQLQuery(sql);
			List<String> dates = new ArrayList<String>();
			List<Date> list = sqlQuery.list();
			for(Date date : list) {
				dates.add(DateUtils.yyyyMMdd().format(date));
			}
			return dates;			
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
	
	public static void addScheduleDate(String date) {
		Session session = null;		
		try {
			session = getSession();
			session.getTransaction().begin();
			String sql = "insert into " + schedule_date + "(date) values ('" + date + "')";
			session.createSQLQuery(sql).executeUpdate();
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
	
	public static void deleteScheduleDate(String date) {
		Session session = null;		
		try {
			session = getSession();
			session.getTransaction().begin();
			String sql = "delete from  " + schedule_date + " where date ='" + date + "'";
			session.createSQLQuery(sql).executeUpdate();
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
	
	public static String getStartDateBySource(String source) {
		Session session = null;		
		try {
			session = getSession();
			String sql = "select min(date) from  " + schedule_data + "  where source = '" + source + "'";
			SQLQuery sqlQuery = session.createSQLQuery(sql);
			return DateUtils.yyyyMMdd().format((Date)sqlQuery.uniqueResult());			
		} catch (Exception e) {
			e.printStackTrace();
			return "2014-01-01";
		} finally {
			try {
				session.close();				
			} catch (Exception e) {
			}
		}
	}
	
	public static String getStartDateByTicker(String ticker) {
		Session session = null;		
		try {
			session = getSession();
			String sql = "select min(date) from  " + schedule_data + "  where ticker = '" + ticker + "'";
			SQLQuery sqlQuery = session.createSQLQuery(sql);
			return DateUtils.yyyyMMdd().format((Date)sqlQuery.uniqueResult());			
		} catch (Exception e) {
			e.printStackTrace();
			return "2014-01-01";
		} finally {
			try {
				session.close();				
			} catch (Exception e) {
			}
		}
	}
	
	public static String saveTestSetResult(String path, String tableName) {
		File file = new File(path);
		if (!file.exists())
			return "Could not find the file.";
		Session session = null;
		try {
			session = getSession();
			session.beginTransaction();
			CSVReader csvReader = new CSVReader(new FileReader(file), ',', '\n', 0);
			String [] lines;
			String sqlSchema = null;
			StringBuilder sql = new StringBuilder();
			boolean hasRecord = false;
			boolean pnlColumnFound = false;
			String pnlColumnName = "Total Pnl by Year";
			List<String> columnNames = new ArrayList<String>();
			List<String> years = new ArrayList<String>();
			while ((lines = csvReader.readNext()) != null)  {
				if (sqlSchema == null) {
					List<String> schemaColumns = new ArrayList<String>();
					List<String> valueColumns = new ArrayList<String>();
					for (String column : lines) {
						if (!StringUtils.isNullOrEmpty(column)) {
							if(column.indexOf(BackTestCSVWriter.TotalPnl) != -1) {
								years.add(column.substring(column.lastIndexOf(" ") + 1));
								if(!pnlColumnFound) {
									column = pnlColumnName;
									pnlColumnFound = true;									
								} else {
									continue;
								}
							}
							schemaColumns.add("`" + column.trim() + "` varchar(255) DEFAULT NULL");
							valueColumns.add("`" + column.trim() + "`");
							columnNames.add(column);
						}
					}
					sqlSchema = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + String.join(",", schemaColumns) + ")";
					sql.append("REPLACE INTO " + tableName + " (" + String.join(",", valueColumns) + ") VALUES ");
				} else {
					List<String> columnValues = new ArrayList<String>();
					for (int i = 0, j = 0; i < columnNames.size(); i++, j++) {
						if(columnNames.get(i).equals(pnlColumnName)) {
							List<String> values = new ArrayList<String>();
							for(String year : years) {
								values.add(year + ":" + lines[j++]);
							}
							j--;
							columnValues.add("'" + String.join(";", values) + "'");
						} else {
							columnValues.add("'" + lines[j].trim() + "'");							
						}
					}
					sql.append("(" + String.join(",", columnValues) + "),");
					hasRecord = true;
				}
			}
			csvReader.close();
			
			if (sqlSchema != null)
				session.createSQLQuery(sqlSchema).executeUpdate();
			if (hasRecord) {			
				session.createSQLQuery(sql.substring(0, sql.length() - 1).toString()).executeUpdate();
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
		return "";
	}
	
}