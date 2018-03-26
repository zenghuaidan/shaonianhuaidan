package com.yoson.scheduler;

import java.text.ParseException;

import com.yoson.tws.EClientSocketUtils;


public class TaskScheduler {
	public synchronized void doTrade() throws ParseException {
		if (EClientSocketUtils.running) {
			EClientSocketUtils.next();			
		}
	}

}
