package com.yoson.date;

import java.text.ParseException;
import java.util.Comparator;

public class DateComparator implements Comparator<String> {

	@Override
	public int compare(String date1, String date2) {
		try {
			return (DateUtils.yyyyMMdd().parse(date1).compareTo(DateUtils.yyyyMMdd().parse(date2)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

}
