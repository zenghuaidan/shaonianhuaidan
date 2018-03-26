package com.yoson.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

public class DateUtils {
	public static ThreadLocal<SimpleDateFormat> yyyyMM = new ThreadLocal<SimpleDateFormat>();//yyyyMM
	public static ThreadLocal<SimpleDateFormat> yyyyMMdd = new ThreadLocal<SimpleDateFormat>();//yyyy-MM-dd
	public static ThreadLocal<SimpleDateFormat> yyyyMMdd2 = new ThreadLocal<SimpleDateFormat>();//yyyyMMdd
	public static ThreadLocal<SimpleDateFormat> HHmmss = new ThreadLocal<SimpleDateFormat>();//HH:mm:ss
	public static ThreadLocal<SimpleDateFormat> HHmm = new ThreadLocal<SimpleDateFormat>();//HH:mm
	public static ThreadLocal<SimpleDateFormat> yyyyMMddHHmm = new ThreadLocal<SimpleDateFormat>();//yyyy-MM-dd HH:mm
	public static ThreadLocal<SimpleDateFormat> yyyyMMddHHmmss = new ThreadLocal<SimpleDateFormat>();//yyyy-MM-dd HH:mm:ss
	public static ThreadLocal<SimpleDateFormat> yyyyMMddHHmmss2 = new ThreadLocal<SimpleDateFormat>();//yyyyMMddHHmmss
	public static ThreadLocal<SimpleDateFormat> yyyyMMddHHmmss3 = new ThreadLocal<SimpleDateFormat>();//yyyyMMdd  HH:mm:ss
	
	
	public static void reset() {
		yyyyMM = new ThreadLocal<SimpleDateFormat>();//yyyyMM
		yyyyMMdd = new ThreadLocal<SimpleDateFormat>();//yyyy-MM-dd
		HHmmss = new ThreadLocal<SimpleDateFormat>();//HH:mm:ss
		HHmm = new ThreadLocal<SimpleDateFormat>();//HH:mm
		yyyyMMddHHmm = new ThreadLocal<SimpleDateFormat>();//yyyy-MM-dd HH:mm
		yyyyMMddHHmmss = new ThreadLocal<SimpleDateFormat>();//yyyy-MM-dd HH:mm:ss
		yyyyMMddHHmmss2 = new ThreadLocal<SimpleDateFormat>();//yyyyMMddHHmmss
		
	}
	
	public static String dateDiff(long diff) {     
        long nd = 1000 * 24 * 60 * 60;     
        long nh = 1000 * 60 * 60;     
        long nm = 1000 * 60;     
        long ns = 1000;     
        long day = diff / nd;     
        long hour = diff % nd / nh + day * 24;     
        long min = diff % nd % nh / nm + day * 24 * 60;     
        long sec = diff % nd % nh % nm / ns;
        
        hour = hour - day * 24;
        min = min - day * 24 * 60;
        
        if (day > 0) {
        	return day + "Days " + hour + "Hours " + min + "Mins " + sec + " Seconds";               	
        } else if (hour > 0) {
        	return hour + "Hours " + min + "Mins " + sec + " Seconds";
        } else if (min > 0) {
        	return min + "Mins " + sec + " Seconds";
        } else if (sec > 0) {
        	return sec + " Seconds";
        } else {
        	return diff + " Milliseconds";
        }
    } 
	
	public static boolean isValidateTime(Date when, String startTimeStr, String endTimeStr) {
		return isValidateTime(when, startTimeStr, endTimeStr, 2);
	}
	
	public static boolean isValidateTime(Date when, String startTimeStr, String endTimeStr, int bufferInMinutes) {
		try {		
			if (StringUtils.isEmpty(startTimeStr) && StringUtils.isEmpty(endTimeStr))
				return false;
			if(StringUtils.isNotEmpty(startTimeStr) && StringUtils.isNotEmpty(endTimeStr)) {
				Date start = org.apache.commons.lang.time.DateUtils.add(DateUtils.yyyyMMddHHmmss().parse(startTimeStr), Calendar.MINUTE, bufferInMinutes * -1);
				Date end = org.apache.commons.lang.time.DateUtils.add(DateUtils.yyyyMMddHHmmss().parse(endTimeStr), Calendar.MINUTE, bufferInMinutes);
				return start.equals(when) || 
						end.equals(when) || 
						start.before(when) && end.after(when);
			}
			else if(StringUtils.isNotEmpty(startTimeStr)) {
				Date start = org.apache.commons.lang.time.DateUtils.add(DateUtils.yyyyMMddHHmmss().parse(startTimeStr), Calendar.MINUTE, bufferInMinutes * -1);
				return start.before(when);
			}
			else {
				Date end = org.apache.commons.lang.time.DateUtils.add(DateUtils.yyyyMMddHHmmss().parse(endTimeStr), Calendar.MINUTE, bufferInMinutes);
				return end.after(when);
			}
		} catch (ParseException e) {
			return false;
		}
	}
	
	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+9"));
		Date now = new Date();
		System.out.println(yyyyMMddHHmmss().format(now));
		
		
		System.out.println(isValidateTime(new Date(117, 11,24,9,29,59),"2017-12-24 09:30:00", "2017-12-24 16:15:00"));
		System.out.println(isValidateTime(new Date(117, 11,24,9,30,0),"2017-12-24 09:30:00", "2017-12-24 16:15:00"));
		System.out.println(isValidateTime(new Date(117, 11,24,9,28,0),"2017-12-24 09:30:00", "2017-12-24 16:15:00"));
		System.out.println();
		System.out.println(isValidateTime(new Date(117, 11,24,16,14,59),"2017-12-24 09:30:00", "2017-12-24 16:15:00"));
		System.out.println(isValidateTime(new Date(117, 11,24,16,15,0),"2017-12-24 09:30:00", "2017-12-24 16:15:00"));
		System.out.println(isValidateTime(new Date(117, 11,24,16,15,1),"2017-12-24 09:30:00", "2017-12-24 16:15:00"));
	}
	
	private static final Object lockObj = new Object();
	
	public static SimpleDateFormat yyyyMM() {
        SimpleDateFormat sf = yyyyMM.get();
        if (sf == null) {
            synchronized (lockObj) {
            	yyyyMM.set(new SimpleDateFormat("yyyyMM"));
            }
        }
        return yyyyMM.get();
	}
	
	public static SimpleDateFormat yyyyMMdd2() {
        SimpleDateFormat sf = yyyyMMdd2.get();
        if (sf == null) {
            synchronized (lockObj) {
            	yyyyMMdd2.set(new SimpleDateFormat("yyyyMMdd"));
            }
        }
        return yyyyMMdd2.get();
	}
	
	public static SimpleDateFormat yyyyMMdd() {
        SimpleDateFormat sf = yyyyMMdd.get();
        if (sf == null) {
            synchronized (lockObj) {
            	yyyyMMdd.set(new SimpleDateFormat("yyyy-MM-dd"));
            }
        }
        return yyyyMMdd.get();
	}
	
	public static SimpleDateFormat HHmmss() {
        SimpleDateFormat sf = HHmmss.get();
        if (sf == null) {
            synchronized (lockObj) {
            	HHmmss.set(new SimpleDateFormat("HH:mm:ss"));
            }
        }
        return HHmmss.get();
	}
	
	public static SimpleDateFormat HHmm() {
        SimpleDateFormat sf = HHmm.get();
        if (sf == null) {
            synchronized (lockObj) {
            	HHmm.set(new SimpleDateFormat("HH:mm"));
            }
        }
        return HHmm.get();
	}
	
	public static SimpleDateFormat yyyyMMddHHmm() {
        SimpleDateFormat sf = yyyyMMddHHmm.get();
        if (sf == null) {
            synchronized (lockObj) {
            	yyyyMMddHHmm.set(new SimpleDateFormat("yyyy-MM-dd HH:mm"));
            }
        }
        return yyyyMMddHHmm.get();
	}
	
	public static SimpleDateFormat yyyyMMddHHmmss() {
        SimpleDateFormat sf = yyyyMMddHHmmss.get();
        if (sf == null) {
            synchronized (lockObj) {
            	yyyyMMddHHmmss.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            }
        }
        return yyyyMMddHHmmss.get();
	}
	
	public static SimpleDateFormat yyyyMMddHHmmss2() {
        SimpleDateFormat sf = yyyyMMddHHmmss2.get();
        if (sf == null) {
            synchronized (lockObj) {
            	yyyyMMddHHmmss2.set(new SimpleDateFormat("yyyyMMddHHmmss"));
            }
        }
        return yyyyMMddHHmmss2.get();
	}	
	
	public static SimpleDateFormat yyyyMMddHHmmss3() {
        SimpleDateFormat sf = yyyyMMddHHmmss3.get();
        if (sf == null) {
            synchronized (lockObj) {
            	yyyyMMddHHmmss3.set(new SimpleDateFormat("yyyyMMdd  HH:mm:ss"));
            }
        }
        return yyyyMMddHHmmss3.get();
	}
	
	public static String getDateStr(String dateTimeStr) {
		return dateTimeStr.substring(0, 4) + "-" + dateTimeStr.substring(4, 6) + "-" + dateTimeStr.substring(6, 8);
	}
	
	public static String getTimeStr(String dateTimeStr) {
		return  dateTimeStr.substring(8, 10) + ":" + dateTimeStr.substring(10, 12) + ":" + dateTimeStr.substring(12, 14);		
	}	
	
	public static Date addSecond(Date current, int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(current);
		calendar.add(Calendar.SECOND, second);
		return calendar.getTime();
	}
}
