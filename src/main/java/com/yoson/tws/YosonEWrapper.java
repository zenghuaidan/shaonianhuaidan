package com.yoson.tws;

public class YosonEWrapper extends BasicEWrapper {
	
	@Override
	public void historicalData(int reqId, String date, double open, double high, double low,
            double close, int volume, int count, double WAP, boolean hasGaps) {
		
	}
	
	@Override
	public void connectionClosed() {
		EClientSocketUtils.reset();
	}
	
}
