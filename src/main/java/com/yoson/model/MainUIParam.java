package com.yoson.model;

import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.opencsv.CSVReader;
import com.yoson.date.BrokenDate;
import com.yoson.date.DateUtils;

public class MainUIParam extends TestSet {
	private String remark;
	private String sourcePath;
	private String dataRootPath;
	private String paramPath;
	private String stepPath;
	private String logPath;
	private String resultPath;
	@Expose
	private boolean ignoreLunchTime;
	@Expose
	private String source;
	@Expose
	private String ticker;
	@Expose
	private boolean fromSource;
	private String version;
	private String nForPnl;
	private boolean matrixFile;

	private int avgStepTo;
	private int avgStepLiteral;
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
	
	private boolean outputDataOpen = true;
	private boolean outputDataAvg = true;
	private boolean outputDataLast = true;
	private boolean outputDataMax = true;
	private boolean outputDataMin = true;
	public MainUIParam() {}
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getResultPath() {
		return resultPath;
	}

	public void setResultPath(String resultPath) {
		this.resultPath = resultPath;
	}

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

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public boolean isFromSource() {
		return fromSource;
	}

	public void setFromSource(boolean fromSource) {
		this.fromSource = fromSource;
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

	public String getnForPnl() {
		return nForPnl;
	}

	public void setnForPnl(String nForPnl) {
		this.nForPnl = nForPnl;
	}

	public int getAvgStepTo() {
		return avgStepTo;
	}

	public void setAvgStepTo(int avgStepTo) {
		this.avgStepTo = avgStepTo;
	}

	public int getAvgStepLiteral() {
		return avgStepLiteral;
	}

	public void setAvgStepLiteral(int avgStepLiteral) {
		this.avgStepLiteral = avgStepLiteral;
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

	public double getOrderTicker() {
		return orderTicker;
	}

	public void setOrderTicker(double orderTicker) {
		this.orderTicker = orderTicker;
	}

	public boolean isIgnoreLunchTime() {
		return ignoreLunchTime;
	}

	public void setIgnoreLunchTime(boolean ignoreLunchTime) {
		this.ignoreLunchTime = ignoreLunchTime;
	}

	public boolean isMatrixFile() {
		return matrixFile;
	}

	public void setMatrixFile(boolean matrixFile) {
		this.matrixFile = matrixFile;
	}	

	public boolean isOutputDataOpen() {
		return outputDataOpen;
	}

	public void setOutputDataOpen(boolean outputDataOpen) {
		this.outputDataOpen = outputDataOpen;
	}

	public boolean isOutputDataAvg() {
		return outputDataAvg;
	}

	public void setOutputDataAvg(boolean outputDataAvg) {
		this.outputDataAvg = outputDataAvg;
	}

	public boolean isOutputDataLast() {
		return outputDataLast;
	}

	public void setOutputDataLast(boolean outputDataLast) {
		this.outputDataLast = outputDataLast;
	}

	public boolean isOutputDataMax() {
		return outputDataMax;
	}

	public void setOutputDataMax(boolean outputDataMax) {
		this.outputDataMax = outputDataMax;
	}

	public boolean isOutputDataMin() {
		return outputDataMin;
	}

	public void setOutputDataMin(boolean outputDataMin) {
		this.outputDataMin = outputDataMin;
	}

	public static final MainUIParam getMainUIParam() {
		MainUIParam mainUIParam = new MainUIParam();
		mainUIParam.setAvgStep(5);
		mainUIParam.setAvgStepTo(5);
		mainUIParam.setAvgStepLiteral(1);
		
		mainUIParam.settShort(120);
		mainUIParam.settShortTo(120);
		mainUIParam.settShortLiteral(1);
		
		mainUIParam.settLong(600);
		mainUIParam.settLongTo(600);
		mainUIParam.settLongLiteral(600);
		
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
	
	public static MainUIParam loadMainUIParamFromCSV(File strategyFile) {
		MainUIParam mainUIParam = new MainUIParam();
		try {
			CSVReader csvReader = new CSVReader(new FileReader(strategyFile), ',', '\n', 0);
			String [] lines;
			List<String []> params = new ArrayList<String []>();
			int index = 0;
			while ((lines = csvReader.readNext()) != null && index <= 200 )  {
				params.add(lines);
				index++;
			}
			index = 0;
			mainUIParam.setSource(params.get(index++)[1]);
			mainUIParam.setTradeDataField(params.get(index++)[1]);
			mainUIParam.setAskDataField(params.get(index++)[1]);
			mainUIParam.setBidDataField(params.get(index++)[1]);
			mainUIParam.settShort(Integer.parseInt(params.get(index++)[1]));
			mainUIParam.settLong(Integer.parseInt(params.get(index++)[1]));			
			mainUIParam.setHld(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setStopLoss(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setTradeStopLoss(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setInstantTradeStoploss(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setItsCounter(Double.parseDouble(params.get(index++)[1]));			
			mainUIParam.setMarketStartTime(params.get(index++)[1]);
			mainUIParam.setLunchStartTimeFrom(params.get(index++)[1]);
			mainUIParam.setLunchStartTimeTo(params.get(index++)[1]);
			mainUIParam.setMarketCloseTime(params.get(index++)[1]);
			mainUIParam.setCashPerIndexPoint(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setTradingFee(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setOtherCostPerTrade(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setUnit(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setLastNumberOfMinutesClearPosition(Integer.parseInt(params.get(index++)[1]));
			mainUIParam.setLunchLastNumberOfMinutesClearPosition(Integer.parseInt(params.get(index++)[1]));
			mainUIParam.setIncludeMorningData(Boolean.parseBoolean(params.get(index++)[1]));
			mainUIParam.setIgnoreLunchTime(Boolean.parseBoolean(params.get(index++)[1]));
			mainUIParam.setAvgStep(Integer.parseInt(params.get(index++)[1]));
			mainUIParam.setIncludeLastMarketDayData(Boolean.parseBoolean(params.get(index++)[1]));
			mainUIParam.setFromSource(Boolean.parseBoolean(params.get(index++)[1]));
			if(!mainUIParam.isFromSource()) mainUIParam.setTicker(mainUIParam.getSource());
			csvReader.close();
		} catch (Exception e) {
		}
		return mainUIParam;
	}
	
	public int isCheckMarketTime(String timeStr) throws ParseException {
		long lastMinutes = this.getLastNumberOfMinutesClearPosition() * 60 * 1000;
		long lunchLastMinutes = this.getLunchLastNumberOfMinutesClearPosition() * 60 * 1000;
		long current = DateUtils.HHmmss().parse(timeStr).getTime();
		
		long morningStartTime = DateUtils.HHmmss().parse(this.getMarketStartTime()).getTime();
		long lunch_start_time = DateUtils.HHmmss().parse(this.getLunchStartTimeFrom()).getTime();
		long lunch_end_time = DateUtils.HHmmss().parse(this.getLunchStartTimeTo()).getTime();
		long market_close_time = DateUtils.HHmmss().parse(this.getMarketCloseTime()).getTime();
		
		if(this.isIgnoreLunchTime())
			lunch_start_time = lunch_end_time;
		
		if (current < (morningStartTime) || current >= market_close_time - lastMinutes)
		{
			return 0;
		} else // Within the trading hours
		{
			if ((current < (lunch_start_time - lunchLastMinutes)) || (current >= lunch_end_time)) {
				return 1;
			} else // In exactly lunch time (not in the trading hour)
			{
				return 0;
			}
		}					
		
	}
}
