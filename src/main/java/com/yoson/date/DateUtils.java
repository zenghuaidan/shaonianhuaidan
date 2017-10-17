package com.yoson.date;

import java.text.SimpleDateFormat;

public class DateUtils {
	public static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat HHmmss = new SimpleDateFormat("HH:mm:ss");
	public static SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat yyyyMMddHHmmss2 = new SimpleDateFormat("yyyyMMddHHmmss");
	
	
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
	
//	private static final Object lockObj = new Object();
//	
//	public static SimpleDateFormat yyyyMMdd() {
//        SimpleDateFormat sf = yyyyMMdd.get();
//        if (sf == null) {
//            synchronized (lockObj) {
//                yyyyMMdd.set();
//            }
//        }
//        return yyyyMMdd.get();
//	}
//	
//	public static SimpleDateFormat yyyyMMddHHmmss() {
//        SimpleDateFormat sf = yyyyMMddHHmmss.get();
//        if (sf == null) {
//            synchronized (lockObj) {
//            	yyyyMMddHHmmss.set();
//            }
//        }
//        return yyyyMMddHHmmss.get();
//	}
//	
//	public static SimpleDateFormat yyyyMMddHHmmss2() {
//        SimpleDateFormat sf = yyyyMMddHHmmss2.get();
//        if (sf == null) {
//            synchronized (lockObj) {
//            	yyyyMMddHHmmss2.set();
//            }
//        }
//        return yyyyMMddHHmmss2.get();
//	}	
}
