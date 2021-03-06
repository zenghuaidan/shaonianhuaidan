package com.yoson.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class DateUtils {
	public static ThreadLocal<SimpleDateFormat> yyyyMM = new ThreadLocal<SimpleDateFormat>();//yyyyMM
	public static ThreadLocal<SimpleDateFormat> yyyyMMdd = new ThreadLocal<SimpleDateFormat>();//yyyy-MM-dd
	public static ThreadLocal<SimpleDateFormat> HHmmss = new ThreadLocal<SimpleDateFormat>();//HH:mm:ss
	public static ThreadLocal<SimpleDateFormat> yyyyMMddHHmm = new ThreadLocal<SimpleDateFormat>();//yyyy-MM-dd HH:mm
	public static ThreadLocal<SimpleDateFormat> yyyyMMddHHmmss = new ThreadLocal<SimpleDateFormat>();//yyyy-MM-dd HH:mm:ss
	public static ThreadLocal<SimpleDateFormat> yyyyMMddHHmmss2 = new ThreadLocal<SimpleDateFormat>();//yyyyMMddHHmmss
	
	
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
		try {		
			if (StringUtils.isEmpty(startTimeStr) && StringUtils.isEmpty(endTimeStr))
				return false;
			if(StringUtils.isNotEmpty(startTimeStr) && StringUtils.isNotEmpty(endTimeStr))
				return DateUtils.yyyyMMddHHmm().parse(startTimeStr).before(when) && DateUtils.yyyyMMddHHmm().parse(endTimeStr).after(when);
			else if(StringUtils.isNotEmpty(startTimeStr))
				return DateUtils.yyyyMMddHHmm().parse(startTimeStr).before(when);
			else
				return DateUtils.yyyyMMddHHmm().parse(endTimeStr).after(when);
		} catch (ParseException e) {
			return false;
		}
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
	
	public static String getDateStr(String dateTimeStr) {
		return dateTimeStr.substring(0, 4) + "-" + dateTimeStr.substring(4, 6) + "-" + dateTimeStr.substring(6, 8);
	}
	
	public static String getTimeStr(String dateTimeStr) {
		return  dateTimeStr.substring(8, 10) + ":" + dateTimeStr.substring(10, 12) + ":" + dateTimeStr.substring(12, 14);		
	}
}
