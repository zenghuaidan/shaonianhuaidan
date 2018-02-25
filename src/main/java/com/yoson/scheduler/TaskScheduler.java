package com.yoson.scheduler;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;

import com.ib.client.Contract;
import com.yoson.cms.controller.IndexController;
import com.yoson.sql.SQLUtils;
import com.yoson.tws.EClientSocketUtils;
import com.yoson.tws.RawDataCSVWriter;
import com.yoson.tws.Record;
import com.yoson.tws.ScheduledDataCSVWriter;
import com.yoson.tws.ScheduledDataRecord;
import com.yoson.tws.YosonEWrapper;
import com.yoson.web.InitServlet;

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
			}
		}
		
	}

}
