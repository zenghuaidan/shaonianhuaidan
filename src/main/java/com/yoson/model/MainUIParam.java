package com.yoson.model;

import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.opencsv.CSVReader;
import com.yoson.date.BrokenDate;
import com.yoson.date.DateUtils;

public class MainUIParam extends TestSet {
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
	private String ticker;
	private boolean fromSource;
	private String version;
	private String nForPnl;
	private boolean matrixFile;
	
	private int timerTo;
	private int timerLiteral;
	private double smoothTo;
	private double smoothLiteral;
	private double actionTo;
	private double actionLiteral;	
	private double absoluteTradeStopLossTo;
	private double absoluteTradeStopLossLiteral;
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
	
	public String getnForPnl() {
		return nForPnl;
	}

	public void setnForPnl(String nForPnl) {
		this.nForPnl = nForPnl;
	}

	public int getTimerTo() {
		return timerTo;
	}

	public void setTimerTo(int timerTo) {
		this.timerTo = timerTo;
	}

	public int getTimerLiteral() {
		return timerLiteral;
	}

	public void setTimerLiteral(int timerLiteral) {
		this.timerLiteral = timerLiteral;
	}

	public double getSmoothTo() {
		return smoothTo;
	}

	public void setSmoothTo(double smoothTo) {
		this.smoothTo = smoothTo;
	}

	public double getSmoothLiteral() {
		return smoothLiteral;
	}

	public void setSmoothLiteral(double smoothLiteral) {
		this.smoothLiteral = smoothLiteral;
	}

	public double getActionTo() {
		return actionTo;
	}

	public void setActionTo(double actionTo) {
		this.actionTo = actionTo;
	}

	public double getActionLiteral() {
		return actionLiteral;
	}

	public void setActionLiteral(double actionLiteral) {
		this.actionLiteral = actionLiteral;
	}

	public double getAbsoluteTradeStopLossTo() {
		return absoluteTradeStopLossTo;
	}

	public void setAbsoluteTradeStopLossTo(double absoluteTradeStopLossTo) {
		this.absoluteTradeStopLossTo = absoluteTradeStopLossTo;
	}

	public double getAbsoluteTradeStopLossLiteral() {
		return absoluteTradeStopLossLiteral;
	}

	public void setAbsoluteTradeStopLossLiteral(double absoluteTradeStopLossLiteral) {
		this.absoluteTradeStopLossLiteral = absoluteTradeStopLossLiteral;
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
		mainUIParam.setTimer(1800);
		mainUIParam.setTimerTo(1800);
		mainUIParam.setTimerLiteral(1);
		
		mainUIParam.setSmooth(10);
		mainUIParam.setSmoothTo(10);
		mainUIParam.setSmoothLiteral(1);
		
		mainUIParam.setAction(30);
		mainUIParam.setActionTo(30);
		mainUIParam.setActionLiteral(1);
		
		mainUIParam.setAbsoluteTradeStopLoss(100);
		mainUIParam.setAbsoluteTradeStopLossTo(100);
		mainUIParam.setAbsoluteTradeStopLossLiteral(1);
		
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
		brokenDateList.add(new BrokenDate("2015-01-19", "2015-01-19"));
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
			mainUIParam.setTimer(Integer.parseInt(params.get(index++)[1]));
			mainUIParam.setSmooth(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setAction(Double.parseDouble(params.get(index++)[1]));			
			mainUIParam.setAbsoluteTradeStopLoss(Double.parseDouble(params.get(index++)[1]));
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
