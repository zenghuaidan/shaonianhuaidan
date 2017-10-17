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
	private double pnlThreshold;

	private boolean outputChart;
	
	private String tradeDataField;
	private String askDataField;
	private String bidDataField;
	private List<BrokenDate> brokenDateList;
	
	private String startStr;
	private String endStr;
	public MainUIParam() {}

//	public void initStartEndDateStr() {
//		Date start = null, end = null;
//		for (BrokenDate brokenDate : this.brokenDateList) {
//			if (start == null || start.after(brokenDate.from))
//				start = brokenDate.from;
//			if (end == null || end.after(brokenDate.to))
//				end = brokenDate.to;
//		}
//		this.startStr = start != null ? DateUtils.yyyyMMdd.format(start) : "";
//		this.endStr = end != null ? DateUtils.yyyyMMdd.format(end) : "";
//	}
	
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

}
