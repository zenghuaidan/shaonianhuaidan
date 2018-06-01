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
	@Expose
	private boolean ignoreLunchTime;
	@Expose
	private String source;
	private String ticker;
	private boolean fromSource;
	private String version;
	private String nForPnl;
	private boolean matrixFile;
	
	private int cpTimerTo;
	private int cpTimerLiteral;
	private double cpBufferTo;
	private double cpBufferLiteral;
	private int cpHitRateTo;
	private int cpHitRateLiteral;
	private double cpSmoothTo;
	private double cpSmoothLiteral;
	private double estimationBufferTo;
	private double estimationBufferLiteral;
	private double actionTriggerTo;
	private double actionTriggerLiteral;
	private int actionCountingTo;
	private int actionCountingLiteral;
	private double tradeStopLossTriggerTo;
	private double tradeStopLossTriggerLiteral;
	private double tradeStopLossTriggerPercentTo;
	private double tradeStopLossTriggerPercentLiteral;
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

	public int getCpTimerTo() {
		return cpTimerTo;
	}

	public void setCpTimerTo(int cpTimerTo) {
		this.cpTimerTo = cpTimerTo;
	}

	public int getCpTimerLiteral() {
		return cpTimerLiteral;
	}

	public void setCpTimerLiteral(int cpTimerLiteral) {
		this.cpTimerLiteral = cpTimerLiteral;
	}

	public double getCpBufferTo() {
		return cpBufferTo;
	}

	public void setCpBufferTo(double cpBufferTo) {
		this.cpBufferTo = cpBufferTo;
	}

	public double getCpBufferLiteral() {
		return cpBufferLiteral;
	}

	public void setCpBufferLiteral(double cpBufferLiteral) {
		this.cpBufferLiteral = cpBufferLiteral;
	}

	public int getCpHitRateTo() {
		return cpHitRateTo;
	}

	public void setCpHitRateTo(int cpHitRateTo) {
		this.cpHitRateTo = cpHitRateTo;
	}

	public int getCpHitRateLiteral() {
		return cpHitRateLiteral;
	}

	public void setCpHitRateLiteral(int cpHitRateLiteral) {
		this.cpHitRateLiteral = cpHitRateLiteral;
	}

	public double getCpSmoothTo() {
		return cpSmoothTo;
	}

	public void setCpSmoothTo(double cpSmoothTo) {
		this.cpSmoothTo = cpSmoothTo;
	}

	public double getCpSmoothLiteral() {
		return cpSmoothLiteral;
	}

	public void setCpSmoothLiteral(double cpSmoothLiteral) {
		this.cpSmoothLiteral = cpSmoothLiteral;
	}

	public double getEstimationBufferTo() {
		return estimationBufferTo;
	}

	public void setEstimationBufferTo(double estimationBufferTo) {
		this.estimationBufferTo = estimationBufferTo;
	}

	public double getEstimationBufferLiteral() {
		return estimationBufferLiteral;
	}

	public void setEstimationBufferLiteral(double estimationBufferLiteral) {
		this.estimationBufferLiteral = estimationBufferLiteral;
	}

	public double getActionTriggerTo() {
		return actionTriggerTo;
	}

	public void setActionTriggerTo(double actionTriggerTo) {
		this.actionTriggerTo = actionTriggerTo;
	}

	public double getActionTriggerLiteral() {
		return actionTriggerLiteral;
	}

	public void setActionTriggerLiteral(double actionTriggerLiteral) {
		this.actionTriggerLiteral = actionTriggerLiteral;
	}

	public int getActionCountingTo() {
		return actionCountingTo;
	}

	public void setActionCountingTo(int actionCountingTo) {
		this.actionCountingTo = actionCountingTo;
	}

	public int getActionCountingLiteral() {
		return actionCountingLiteral;
	}

	public void setActionCountingLiteral(int actionCountingLiteral) {
		this.actionCountingLiteral = actionCountingLiteral;
	}

	public double getTradeStopLossTriggerTo() {
		return tradeStopLossTriggerTo;
	}

	public void setTradeStopLossTriggerTo(double tradeStopLossTriggerTo) {
		this.tradeStopLossTriggerTo = tradeStopLossTriggerTo;
	}

	public double getTradeStopLossTriggerLiteral() {
		return tradeStopLossTriggerLiteral;
	}

	public void setTradeStopLossTriggerLiteral(double tradeStopLossTriggerLiteral) {
		this.tradeStopLossTriggerLiteral = tradeStopLossTriggerLiteral;
	}

	public double getTradeStopLossTriggerPercentTo() {
		return tradeStopLossTriggerPercentTo;
	}

	public void setTradeStopLossTriggerPercentTo(double tradeStopLossTriggerPercentTo) {
		this.tradeStopLossTriggerPercentTo = tradeStopLossTriggerPercentTo;
	}

	public double getTradeStopLossTriggerPercentLiteral() {
		return tradeStopLossTriggerPercentLiteral;
	}

	public void setTradeStopLossTriggerPercentLiteral(double tradeStopLossTriggerPercentLiteral) {
		this.tradeStopLossTriggerPercentLiteral = tradeStopLossTriggerPercentLiteral;
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

	public static final MainUIParam getMainUIParam() {
		MainUIParam mainUIParam = new MainUIParam();
		mainUIParam.setCpTimer(3);
		mainUIParam.setCpTimerTo(3);
		mainUIParam.setCpTimerLiteral(1);
		
		mainUIParam.setCpBuffer(1);
		mainUIParam.setCpBufferTo(1);
		mainUIParam.setCpBufferLiteral(1);
		
		mainUIParam.setCpHitRate(250);
		mainUIParam.setCpHitRateTo(250);
		mainUIParam.setCpHitRateLiteral(1);
		
		mainUIParam.setCpSmooth(1);
		mainUIParam.setCpSmoothTo(1);
		mainUIParam.setCpSmoothLiteral(6);
		
		
		mainUIParam.setEstimationBuffer(2);
		mainUIParam.setEstimationBufferTo(2);
		mainUIParam.setEstimationBufferLiteral(1);
		
		mainUIParam.setActionTrigger(2);
		mainUIParam.setActionTriggerTo(2);
		mainUIParam.setActionTriggerLiteral(1);

		mainUIParam.setActionCounting(10);
		mainUIParam.setActionCountingTo(10);
		mainUIParam.setActionCountingLiteral(1);
		
		mainUIParam.setTradeStopLossTrigger(20);
		mainUIParam.setTradeStopLossTriggerTo(20);
		mainUIParam.setTradeStopLossTriggerLiteral(1);
		
		mainUIParam.setTradeStopLossTriggerPercent(0.8);
		mainUIParam.setTradeStopLossTriggerPercentTo(0.8);
		mainUIParam.setTradeStopLossTriggerPercentLiteral(0.1);
		
		mainUIParam.setAbsoluteTradeStopLoss(20);
		mainUIParam.setAbsoluteTradeStopLossTo(20);
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
		try {
			CSVReader csvReader = new CSVReader(new FileReader(strategyFile), ',', '\n', 0);
			String [] lines;
			MainUIParam mainUIParam = new MainUIParam();
			List<String []> params = new ArrayList<String []>();
			int index = 0;
			while ((lines = csvReader.readNext()) != null && index <= 25 )  {
				params.add(lines);
				index++;
			}
			index = 0;
			mainUIParam.setSource(params.get(index++)[1]);
			mainUIParam.setTradeDataField(params.get(index++)[1]);
			mainUIParam.setAskDataField(params.get(index++)[1]);
			mainUIParam.setBidDataField(params.get(index++)[1]);
			mainUIParam.setCpTimer(Integer.parseInt(params.get(index++)[1]));
			mainUIParam.setCpBuffer(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setCpHitRate(Integer.parseInt(params.get(index++)[1]));
			mainUIParam.setCpSmooth(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setEstimationBuffer(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setActionTrigger(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setActionCounting(Integer.parseInt(params.get(index++)[1]));
			mainUIParam.setTradeStopLossTrigger(Double.parseDouble(params.get(index++)[1]));
			mainUIParam.setTradeStopLossTriggerPercent(Double.parseDouble(params.get(index++)[1]));
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
			csvReader.close();
			return mainUIParam;
		} catch (Exception e) {
			return null;
		}
	}
	
	public int isCheckMarketTime(String timeStr) throws ParseException {
		long lastMinutes = this.getLastNumberOfMinutesClearPosition() * 60 * 1000;
		long lunchLastMinutes = this.getLunchLastNumberOfMinutesClearPosition() * 60 * 1000;
		long current = DateUtils.HHmmss().parse(timeStr).getTime();
		
		long morningStartTime = DateUtils.HHmmss().parse(this.getMarketStartTime()).getTime();
		long lunch_start_time = DateUtils.HHmmss().parse(this.getLunchStartTimeFrom()).getTime();
		long lunch_end_time = DateUtils.HHmmss().parse(this.getLunchStartTimeTo()).getTime();
		long market_close_time = DateUtils.HHmmss().parse(this.getMarketCloseTime()).getTime();
		
		if (current < (morningStartTime) || current >= market_close_time - lastMinutes)
		{
			return 0;
		} else // Within the trading hours
		{
			if (this.isIgnoreLunchTime() || (current < (lunch_start_time - lunchLastMinutes)) || (current >= lunch_end_time)) {
				return 1;
			} else // In exactly lunch time (not in the trading hour)
			{
				return 0;
			}
		}					
		
	}
}
