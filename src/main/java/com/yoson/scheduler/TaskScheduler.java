package com.yoson.scheduler;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.ib.client.Contract;
import com.yoson.cms.controller.IndexController;
import com.yoson.tws.EClientSocketUtils;
import com.yoson.tws.YosonEWrapper;

public class TaskScheduler {
	public synchronized void doTrade() throws ParseException {
		if(!EClientSocketUtils.isConnected() || EClientSocketUtils.contracts == null || EClientSocketUtils.contracts.size() == 0) {
			return;
		}
		Date now = new Date();
		
		boolean validateTime = false;
		for(Contract contract : EClientSocketUtils.contracts) {
			if(YosonEWrapper.isValidateTime(contract, now)) {
				validateTime = true;
				break;
			}
		}
		if(!validateTime) {
			if(StringUtils.isNotEmpty(EClientSocketUtils.id)){
				IndexController.uploadData(EClientSocketUtils.id);				
				EClientSocketUtils.disconnect();
				EClientSocketUtils.id = null;
			}
		}
		
	}

}
