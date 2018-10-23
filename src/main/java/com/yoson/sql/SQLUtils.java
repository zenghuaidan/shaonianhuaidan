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

public class SQLUtils {
	
	public static final String schedule_data = "schedule_data";
	
	public static final String expiry_date = "expiry_date";
    
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