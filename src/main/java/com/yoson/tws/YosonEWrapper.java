package com.yoson.tws;

import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.ib.client.Contract;
import com.yoson.cms.controller.IndexController;
import com.yoson.date.DateUtils;
import com.yoson.web.InitServlet;

public class YosonEWrapper extends BasicEWrapper {
	
	@Override
	public void historicalData(int reqId, String date, double open, double high, double low,
            double close, int volume, int count, double WAP, boolean hasGaps) {
		Contract contract = EClientSocketUtils.contracts.get(reqId / EClientSocketUtils.identify);
		String source = contract.m_secType + "_" + contract.m_symbol;
		int type = reqId % EClientSocketUtils.identify;
		if (!date.contains("finished")) {
			try {
				int hours = (TimeZone.getTimeZone("Hongkong").getRawOffset() / (3600 * 1000)) - (TimeZone.getTimeZone(EClientSocketUtils.connectionInfo.getTimeZone()).getRawOffset() / (3600 * 1000));
				Date current = DateUtils.yyyyMMddHHmmss3().parse(date);
				current = DateUtils.addSecond(current, hours * 3600 * -1);
				String log = source + "," + type + "," + DateUtils.yyyyMMddHHmmss().format(current) + "," + open + "," + close + "," + low + "," + high + "," + WAP + System.lineSeparator();
				writeText(getHistoricalDataLogPath(), log, true);	
			} catch (Exception e) {
			}
		} else {
			String typeStr = "";
			switch (type) {
				case 0:
					typeStr = "BID";
					break;
				case 1:
					typeStr = "ASK";
					break;
				case 2:
					typeStr = "TRADE";
					break;
			}
			IndexController.status = "source=" + source + ", type=" + typeStr + "," + date;
		}
	}
	
	public static String getHistoricalDataLogPath() {
		return FilenameUtils.concat(InitServlet.createLiveDataFoderAndReturnPath(), EClientSocketUtils.id + ".csv");
	}
	
	public static void writeText(String filePath, String result, boolean append) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(filePath, append);
			IOUtils.write(result, fileOutputStream, Charset.forName("utf-8"));
			fileOutputStream.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
	
	@Override
	public void connectionClosed() {
		EClientSocketUtils.reset();
	}
	
}
