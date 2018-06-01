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

	private int tShortTo;
	private int tShortLiteral;
	private int tLongTo;
	private int tLongLiteral;
	private int masTo;
	private int masLiteral;
	private int malTo;
	private int malLiteral;
	private double matTo;
	private double matLiteral;	
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
	
	public int gettShortTo() {
		return tShortTo;
	}

	public String getnForPnl() {
		return nForPnl;
	}

	public void setnForPnl(String nForPnl) {
		this.nForPnl = nForPnl;
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

	public int getMasTo() {
		return masTo;
	}

	public void setMasTo(int masTo) {
		this.masTo = masTo;
	}

	public int getMasLiteral() {
		return masLiteral;
	}

	public void setMasLiteral(int masLiteral) {
		this.masLiteral = masLiteral;
	}

	public int getMalTo() {
		return malTo;
	}

	public void setMalTo(int malTo) {
		this.malTo = malTo;
	}

	public int getMalLiteral() {
		return malLiteral;
	}

	public void setMalLiteral(int malLiteral) {
		this.malLiteral = malLiteral;
	}

	public double getMatTo() {
		return matTo;
	}

	public void setMatTo(double matTo) {
		this.matTo = matTo;
	}

	public double getMatLiteral() {
		return matLiteral;
	}

	public void setMatLiteral(double matLiteral) {
		this.matLiteral = matLiteral;
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

	public static final MainUIParam getMainUIParam() {
		MainUIParam mainUIParam = new MainUIParam();
		mainUIParam.settShort(2);
		mainUIParam.settShortTo(2);
		mainUIParam.settShortLiteral(1);
		
		mainUIParam.settLong(2);
		mainUIParam.settLongTo(2);
		mainUIParam.settLongLiteral(1);
		
		
		mainUIParam.setMas(2);
		mainUIParam.setMasTo(2);
		mainUIParam.setMasLiteral(1);
		
		mainUIParam.setMal(2);
		mainUIParam.setMalTo(2);
		mainUIParam.setMalLiteral(1);
		
		mainUIParam.setMat(6);
		mainUIParam.setMatTo(6);
		mainUIParam.setMatLiteral(1);
		
		mainUIParam.setStopLoss(200);
		mainUIParam.setStopLossTo(200);
		mainUIParam.setStopLossLiteral(200);
		
		mainUIParam.setTradeStopLoss(100);
		mainUIParam.setTradeStopLossTo(100);
		mainUIParam.setTradeStopLossLiteral(50);

		mainUIParam.setInstantTradeStoploss(0.6);
		mainUIParam.setInstantTradeStoplossTo(0.6);
		mainUIParam.setInstantTradeStoplossLiteral(0.6);
		
		mainUIParam.setItsCounter(1200);
		mainUIParam.setItsCounterTo(1200);
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
			while ((lines = csvReader.readNext()) != null && index <= 24 )  {
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
			mainUIParam.setMas(Integer.parseInt(params.get(index++)[1]));
			mainUIParam.setMal(Integer.parseInt(params.get(index++)[1]));
			mainUIParam.setMat(Double.parseDouble(params.get(index++)[1]));			
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
