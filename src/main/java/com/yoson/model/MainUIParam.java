package com.yoson.model;

import java.util.List;

import com.yoson.date.BrokenDate;

public class MainUIParam extends TestSet {
	private String sourcePath;
	private String dataRootPath;
	private String paramPath;
	private String stepPath;
	private String logPath;
	private String source;
	private String version;

	private int tShortTo;
	private int tShortLiteral;
	private int tLongTo;
	private int tLongLiteral;
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
	private double pnlThreshold;

	private boolean outputChart;
	
	private String tradeDataField;
	private String askDataField;
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

}
