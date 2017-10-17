package com.yoson.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.yoson.date.BrokenDate;
import com.yoson.date.DateUtils;

public class MainUIParam extends TestSet {
	private String sourcePath;
	private String dataRootPath;
	private String paramPath;
	private String stepPath;
	private String logPath;
	@Expose
	private String source;
	private String version;

	private int tShortTo;
	private int tShortLiteral;
	private int tLongTo;
	private int tLongLiteral;
	private int tLong2To;
	private int tLong2Literal;
	private double hldTo;
	private double hldLiteral;
	private double stopLossTo;
	private double stopLossLiteral;
	private double tradeStopLossTo;
	private double tradeStopLossLiteral;
	private double instantTradeStoplossTo;
	private double instantTradeStoplossLiteral;
	private double itsCounterTo;
	private double itsCounterLiteral;
	private double stopGainPercentTo;
	private double stopGainPercentLiteral;
	private double stopGainTriggerTo;
	private double stopGainTriggerLiteral;
	@Expose
	private double pnlThreshold;
	@Expose
	private double orderTicker;

	private boolean outputChart;
	@Expose
	private String tradeDataField;
	@Expose
	private String askDataField;
	@Expose
	private String bidDataField;
	private List<BrokenDate> brokenDateList;
	
	private String startStr;
	private String endStr;
	public MainUIParam() {}
	
	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int gettShortTo() {
		return tShortTo;
	}

	public void settShortTo(int tShortTo) {
		this.tShortTo = tShortTo;
	}

	public int gettShortLiteral() {
		return tShortLiteral;
	}

	public void settShortLiteral(int tShortLiteral) {
		this.tShortLiteral = tShortLiteral;
	}

	public int gettLongTo() {
		return tLongTo;
	}

	public void settLongTo(int tLongTo) {
		this.tLongTo = tLongTo;
	}

	public int gettLongLiteral() {
		return tLongLiteral;
	}

	public void settLongLiteral(int tLongLiteral) {
		this.tLongLiteral = tLongLiteral;
	}

	public int gettLong2To() {
		return tLong2To;
	}

	public void settLong2To(int tLong2To) {
		this.tLong2To = tLong2To;
	}

	public int gettLong2Literal() {
		return tLong2Literal;
	}

	public void settLong2Literal(int tLong2Literal) {
		this.tLong2Literal = tLong2Literal;
	}

	public double getHldTo() {
		return hldTo;
	}

	public void setHldTo(double hldTo) {
		this.hldTo = hldTo;
	}

	public double getHldLiteral() {
		return hldLiteral;
	}

	public void setHldLiteral(double hldLiteral) {
		this.hldLiteral = hldLiteral;
	}

	public double getStopLossTo() {
		return stopLossTo;
	}

	public void setStopLossTo(double stopLossTo) {
		this.stopLossTo = stopLossTo;
	}

	public double getStopLossLiteral() {
		return stopLossLiteral;
	}

	public void setStopLossLiteral(double stopLossLiteral) {
		this.stopLossLiteral = stopLossLiteral;
	}

	public double getTradeStopLossTo() {
		return tradeStopLossTo;
	}

	public void setTradeStopLossTo(double tradeStopLossTo) {
		this.tradeStopLossTo = tradeStopLossTo;
	}

	public double getTradeStopLossLiteral() {
		return tradeStopLossLiteral;
	}

	public void setTradeStopLossLiteral(double tradeStopLossLiteral) {
		this.tradeStopLossLiteral = tradeStopLossLiteral;
	}

	public double getInstantTradeStoplossTo() {
		return instantTradeStoplossTo;
	}

	public void setInstantTradeStoplossTo(double instantTradeStoplossTo) {
		this.instantTradeStoplossTo = instantTradeStoplossTo;
	}

	public double getInstantTradeStoplossLiteral() {
		return instantTradeStoplossLiteral;
	}

	public void setInstantTradeStoplossLiteral(double instantTradeStoplossLiteral) {
		this.instantTradeStoplossLiteral = instantTradeStoplossLiteral;
	}

	public double getItsCounterTo() {
		return itsCounterTo;
	}

	public void setItsCounterTo(double itsCounterTo) {
		this.itsCounterTo = itsCounterTo;
	}

	public double getItsCounterLiteral() {
		return itsCounterLiteral;
	}

	public void setItsCounterLiteral(double itsCounterLiteral) {
		this.itsCounterLiteral = itsCounterLiteral;
	}

	public double getStopGainPercentTo() {
		return stopGainPercentTo;
	}

	public void setStopGainPercentTo(double stopGainPercentTo) {
		this.stopGainPercentTo = stopGainPercentTo;
	}

	public double getStopGainPercentLiteral() {
		return stopGainPercentLiteral;
	}

	public void setStopGainPercentLiteral(double stopGainPercentLiteral) {
		this.stopGainPercentLiteral = stopGainPercentLiteral;
	}

	public double getStopGainTriggerTo() {
		return stopGainTriggerTo;
	}

	public void setStopGainTriggerTo(double stopGainTriggerTo) {
		this.stopGainTriggerTo = stopGainTriggerTo;
	}

	public double getStopGainTriggerLiteral() {
		return stopGainTriggerLiteral;
	}

	public void setStopGainTriggerLiteral(double stopGainTriggerLiteral) {
		this.stopGainTriggerLiteral = stopGainTriggerLiteral;
	}

	public boolean isOutputChart() {
		return outputChart;
	}

	public void setOutputChart(boolean outputChart) {
		this.outputChart = outputChart;
	}

	public String getTradeDataField() {
		return tradeDataField;
	}

	public void setTradeDataField(String tradeDataField) {
		this.tradeDataField = tradeDataField;
	}

	public String getAskDataField() {
		return askDataField;
	}

	public void setAskDataField(String askDataField) {
		this.askDataField = askDataField;
	}

	public String getBidDataField() {
		return bidDataField;
	}

	public void setBidDataField(String bidDataField) {
		this.bidDataField = bidDataField;
	}

	public List<BrokenDate> getBrokenDateList() {
		return brokenDateList;
	}

	public void setBrokenDateList(List<BrokenDate> brokenDateList) {
		this.brokenDateList = brokenDateList;
	}

	public String getStartStr() {
		return startStr;
	}

	public void setStartStr(String startStr) {
		this.startStr = startStr;
	}

	public String getEndStr() {
		return endStr;
	}

	public void setEndStr(String endStr) {
		this.endStr = endStr;
	}

	public double getPnlThreshold() {
		return pnlThreshold;
	}

	public void setPnlThreshold(double pnlThreshold) {
		this.pnlThreshold = pnlThreshold;
	}

	public String getDataRootPath() {
		return dataRootPath;
	}

	public void setDataRootPath(String dataRootPath) {
		this.dataRootPath = dataRootPath;
	}

	public String getParamPath() {
		return paramPath;
	}

	public void setParamPath(String paramPath) {
		this.paramPath = paramPath;
	}

	public String getStepPath() {
		return stepPath;
	}

	public void setStepPath(String stepPath) {
		this.stepPath = stepPath;
	}

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	public double getOrderTicker() {
		return orderTicker;
	}

	public void setOrderTicker(double orderTicker) {
		this.orderTicker = orderTicker;
	}
	
	public static final MainUIParam getMainUIParam() {
		MainUIParam mainUIParam = new MainUIParam();
		mainUIParam.settShort(120);
		mainUIParam.settShortTo(120);
		mainUIParam.settShortLiteral(1);
		
		mainUIParam.settLong(600);
		mainUIParam.settLongTo(600);
		mainUIParam.settLongLiteral(600);
		
		mainUIParam.settLong2(1200);
		mainUIParam.settLong2To(1200);
		mainUIParam.settLong2Literal(1200);
		
		mainUIParam.setHld(0.001);
		mainUIParam.setHldTo(0.001);
		mainUIParam.setHldLiteral(0.001);
		
		
		mainUIParam.setStopLoss(200);
		mainUIParam.setStopLossTo(200);
		mainUIParam.setStopLossLiteral(200);
		
		mainUIParam.setTradeStopLoss(50);
		mainUIParam.setTradeStopLossTo(50);
		mainUIParam.setTradeStopLossLiteral(50);

		mainUIParam.setInstantTradeStoploss(0.6);
		mainUIParam.setInstantTradeStoplossTo(0.6);
		mainUIParam.setInstantTradeStoplossLiteral(0.6);
		
		mainUIParam.setItsCounter(50);
		mainUIParam.setItsCounterTo(50);
		mainUIParam.setItsCounterLiteral(50);
		
		mainUIParam.setStopGainPercent(0.8);
		mainUIParam.setStopGainPercentTo(0.8);
		mainUIParam.setStopGainPercentLiteral(0.8);
		
		mainUIParam.setStopGainTrigger(30000);
		mainUIParam.setStopGainTriggerTo(30000);
		mainUIParam.setStopGainTriggerLiteral(30000);
		
		mainUIParam.setUnit(1);
		mainUIParam.setOrderTicker(10);
		
		mainUIParam.setMarketStartTime("09:15:00");
		mainUIParam.setLunchStartTimeFrom("12:00:00");
		mainUIParam.setLunchStartTimeTo("13:00:00");
		mainUIParam.setMarketCloseTime("16:15:00");
		
		mainUIParam.setCashPerIndexPoint(50);
		mainUIParam.setTradingFee(18);
		mainUIParam.setOtherCostPerTrade(0);
		
		mainUIParam.setLastNumberOfMinutesClearPosition(2);
		mainUIParam.setLunchLastNumberOfMinutesClearPosition(2);
														
		mainUIParam.setSource("BBG_HSI");
		mainUIParam.setVersion("6");
		
		mainUIParam.setOutputChart(false);
		
		mainUIParam.setTradeDataField("tradelast");	   
		mainUIParam.setAskDataField("asklast");			   
		mainUIParam.setBidDataField("bidlast");	
		
		List<BrokenDate> brokenDateList = new ArrayList<BrokenDate>();
		brokenDateList.add(new BrokenDate("2014-01-01", DateUtils.yyyyMMdd().format(new Date())));
		mainUIParam.setBrokenDateList(brokenDateList);
		
		return mainUIParam;
	}

}
