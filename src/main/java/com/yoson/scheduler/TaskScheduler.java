package com.yoson.scheduler;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.ib.client.Contract;
import com.yoson.cms.controller.IndexController;
import com.yoson.date.DateUtils;
import com.yoson.tws.EClientSocketUtils;

public class TaskScheduler {
	public synchronized void doTrade() throws ParseException {
		if(EClientSocketUtils.contracts == null || EClientSocketUtils.contracts.size() == 0) {
			return;
		}
		Date now = new Date();
		long nowDateTimeLong = Long.parseLong(DateUtils.yyyyMMddHHmmss2().format(now));
		boolean validateTime = false;
		for(Contract contract : EClientSocketUtils.contracts) {
			Date endTime = DateUtils.yyyyMMddHHmmss().parse(DateUtils.yyyyMMdd().format(now) + " " + contract.getEndTime());
			if(DateUtils.addSecond(endTime, 1) > nowDateTimeLong) {				
				validateTime = true;
				break;
			}
		}
		if(!validateTime) {
			if(EClientSocketUtils.isConnected() && StringUtils.isNotEmpty(EClientSocketUtils.id)) {
				IndexController.uploadData(EClientSocketUtils.id);				
				EClientSocketUtils.disconnect();
				EClientSocketUtils.id = null;
			}
		} else {
			if (!EClientSocketUtils.isConnected())
				EClientSocketUtils.reconnectUsingPreConnectSetting();
			if(StringUtils.isEmpty(EClientSocketUtils.id)) {
				EClientSocketUtils.requestData(now);
			}
		}
		
	}

}
