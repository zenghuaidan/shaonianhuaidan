package com.yoson.cms.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.context.Theme;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ib.client.Contract;
import com.opencsv.CSVReader;
import com.yoson.callback.StatusCallBack;
import com.yoson.csv.BackTestCSVWriter;
import com.yoson.csv.BigExcelReader;
import com.yoson.date.DateUtils;
import com.yoson.model.MainUIParam;
import com.yoson.model.ScheduleData;
import com.yoson.sql.SQLUtils;
import com.yoson.task.BackTestTask;
import com.yoson.tws.ConnectionInfo;
import com.yoson.tws.EClientSocketUtils;
import com.yoson.tws.RawDataCSVWriter;
import com.yoson.tws.Record;
import com.yoson.tws.ScheduledDataCSVWriter;
import com.yoson.tws.ScheduledDataRecord;
import com.yoson.tws.Strategy;
import com.yoson.tws.YosonEWrapper;
import com.yoson.web.InitServlet;
import com.yoson.zip.ZipUtils;

@Controller
public class IndexController  implements StatusCallBack {
	public static MainUIParam mainUIParam;
	public static StringBuilder statusStr = new StringBuilder();

	@RequestMapping("/")
	public String index(Model model) {
		model.addAttribute("mainUIParam", BackTestTask.running || IndexController.mainUIParam != null ? IndexController.mainUIParam : MainUIParam.getMainUIParam());
		model.addAttribute("sources", new ArrayList<String>());
		model.addAttribute("tickers", SQLUtils.getTickers());
		model.addAttribute("connectionInfo", EClientSocketUtils.connectionInfo == null ? getDefaultConnectionInfo() : EClientSocketUtils.connectionInfo);
		model.addAttribute("contract", EClientSocketUtils.contract == null ? getDefaultContract() : EClientSocketUtils.contract);
		model.addAttribute("strategies", EClientSocketUtils.strategies);
		model.addAttribute("title", InitServlet.getVersion());
		return "index";
	}
	
	public ConnectionInfo getDefaultConnectionInfo() {
		ConnectionInfo connectionInfo = new ConnectionInfo();
		connectionInfo.setHost("127.0.0.1");
		connectionInfo.setPort(7496);
		connectionInfo.setClientId(Integer.parseInt(InitServlet.getVersionIndex()));
		connectionInfo.setAccount("U8979091");
		return connectionInfo;
	}
	
	public Contract getDefaultContract() {
		Contract contract = new Contract();
		contract.m_secType = "FUT";
		contract.m_symbol = "HSI";
		contract.m_currency = "HKD";
	    contract.m_exchange = "HKFE";
	    contract.m_localSymbol = "";
	    contract.m_expiry = DateUtils.yyyyMM().format(new Date());
	    contract.tif = "IOC";
	    return contract;
	}
	
	@ResponseBody
	@RequestMapping("test")
	public MainUIParam test() {
	  return MainUIParam.getMainUIParam();
	}
	
	@ResponseBody
	@RequestMapping("connect")
	public boolean connect(@RequestBody ConnectionInfo connectionInfo) {
		return EClientSocketUtils.connect(connectionInfo);
	}
	
	@ResponseBody
	@RequestMapping(path="disconnect", method= {RequestMethod.GET})
	public boolean disconnect() {
		return EClientSocketUtils.disconnect();
	}
	
	@ResponseBody
	@RequestMapping("isConnect")
	public boolean isConnect() {
		return EClientSocketUtils.isConnected();
	}
	
	@ResponseBody
	@RequestMapping("search")
	public String search(@RequestBody Contract contract) {
		try {
			Date startTime = DateUtils.yyyyMMddHHmm().parse(contract.getStartTime());
			Date endTime = DateUtils.yyyyMMddHHmm().parse(contract.getEndTime());
			if((startTime.equals(endTime) || startTime.before(endTime)) && contract.getStartTime().split(" ")[0].equals(contract.getEndTime().split(" ")[0])) {
				boolean success =  EClientSocketUtils.reqMktData(0, contract);
				if(success) {
					return "Success";
				} else {
					return "Connect failed, please check your connection";
				}
			} else {
				return "The start time should before end time, and they should be the same day";
			}
		} catch (ParseException e) {
			return "Please input valdate time!";
		}
	}
	
	@ResponseBody
	@RequestMapping(path = "addStrategy", method = {RequestMethod.POST})
	public boolean addStrategy(@RequestBody Strategy strategy) {
		String strategyName = strategy.getStrategyName();
		if(StringUtils.isEmpty(strategy.getStrategyName())) {
			strategyName = "Strategy";
		}
		int max = 0;
		for (Strategy strategy2 : EClientSocketUtils.strategies) {
			if(strategy2.getStrategyName().startsWith(strategyName)) {
				String index = strategy2.getStrategyName().substring(strategyName.length(), strategy2.getStrategyName().length());
				try {
					int iIndex = StringUtils.isEmpty(index) ? 1 : (Integer.parseInt(index) + 1);
					max = Math.max(max, iIndex);
				} catch (Exception e) {
				}
			}
		}
		if (max > 0)
			strategyName += max;
		strategy.setStrategyName(strategyName);
		EClientSocketUtils.strategies.add(strategy);
		return true;
	}
	
	@ResponseBody
	@RequestMapping(path = "deleteStrategy", method = {RequestMethod.POST})
	public boolean deleteStrategy(@RequestParam String strategyName) {
		for (Strategy strategy : EClientSocketUtils.strategies) {
			if (strategy.getStrategyName().equals(strategyName)) {
				EClientSocketUtils.strategies.remove(strategy);
				return true;
			}
		}
		return false;
	}
	
	@ResponseBody
	@RequestMapping(path = "chageStrategyStatus", method = {RequestMethod.POST})
	public boolean chageStrategyStatus(@RequestParam String strategyName) {
		for (Strategy strategy : EClientSocketUtils.strategies) {
			if (strategy.getStrategyName().equals(strategyName)) {
				if(strategy.isActive()) {
					strategy.inactive();
				} else {
					strategy.active();
				}
				return true;
			}
		}
		return false;
	}
	
	@ResponseBody
	@RequestMapping(path = "strategyStatus", method = {RequestMethod.POST})
	public boolean strategyStatus(@RequestParam String strategyName) {
		for (Strategy strategy : EClientSocketUtils.strategies) {
			if (strategy.getStrategyName().equals(strategyName)) {
				return strategy.isActive();
			}
		}
		return false;
	}
	
	@ResponseBody
	@RequestMapping(path = "inactiveAllStrategy", method = {RequestMethod.POST})
	public boolean inactiveAllStrategy() {
		if(EClientSocketUtils.strategies.size() > 0) {
			for (Strategy strategy : EClientSocketUtils.strategies) {
				strategy.inactive();			
			}
			return true;
		}
		return false;
	}
	
	@ResponseBody
	@RequestMapping(path = "deleteAllStrategy", method = {RequestMethod.POST})
	public boolean deleteAllStrategy() {
		if(EClientSocketUtils.strategies.size() > 0) {
			EClientSocketUtils.strategies.clear();
			return true;
		}
		return false;
	}
	
	@ResponseBody
	@RequestMapping(path = "activeAllStrategy", method = {RequestMethod.POST})
	public boolean activeAllStrategy() {
		if(EClientSocketUtils.strategies.size() > 0) {
			for (Strategy strategy : EClientSocketUtils.strategies) {
				strategy.active();				
			}	
			return true;
		}
		return false;
	}
	
	@ResponseBody
	@RequestMapping(path = "getMarketData", method = {RequestMethod.GET})
	public String getMarketData() {
		ScheduledDataRecord scheduledDataRecord = YosonEWrapper.scheduledDataRecords == null || YosonEWrapper.scheduledDataRecords.size() == 0 ?
				new ScheduledDataRecord() :
				YosonEWrapper.scheduledDataRecords.get(YosonEWrapper.scheduledDataRecords.size() - 1);
		double ask = scheduledDataRecord.getAsklast();
		double bid = scheduledDataRecord.getBidlast();
		double trade = scheduledDataRecord.getTradelast();
		
		Format FMT2 = new DecimalFormat( "#,##0.00");
		Format PCT = new DecimalFormat( "0.0%");
		
		String change = YosonEWrapper.close == 0 ? "" : PCT.format( (trade - YosonEWrapper.close) / YosonEWrapper.close);
		String desc = EClientSocketUtils.contract != null && EClientSocketUtils.isConnected() && YosonEWrapper.isValidateTime(new Date()) ? EClientSocketUtils.contract.getSymbol() : "Waiting for input";
		String time = YosonEWrapper.lastTime == null ? "" : DateUtils.yyyyMMddHHmmss().format(YosonEWrapper.lastTime);
		return desc + "@" + FMT2.format(bid) + "@" + YosonEWrapper.bidSize + "@" + FMT2.format(ask) + "@" + YosonEWrapper.askSize + "@" + FMT2.format(trade) + "@" + YosonEWrapper.tradeSize + "@" + time + "@" + change;
	}
	
	@ResponseBody
	@RequestMapping(path = "getAllStrategy", method = {RequestMethod.GET})
	public List<Strategy> getAllStrategy() {
		return EClientSocketUtils.strategies;
	}
	
	@RequestMapping("saveAllStrategy")
	public void saveAllStrategy(HttpServletResponse response, HttpServletRequest request) throws IOException {
		Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
		String json = gson.toJson(EClientSocketUtils.strategies);			
		
		response.setContentType("APPLICATION/OCTET-STREAM");  
		response.setHeader("Content-Disposition","attachment; filename=saveTemplate"+DateUtils.yyyyMMddHHmmss2().format(new Date()) + ".txt");
		IOUtils.write(json, response.getOutputStream());
		response.flushBuffer();	
	}
	
	@ResponseBody
	@RequestMapping("loadTemplate")
	public boolean loadTemplate(MultipartFile template, HttpServletResponse response, HttpServletRequest request) throws IOException {
		try {
			String ext = template == null ? "" : template.getOriginalFilename().substring(template.getOriginalFilename().lastIndexOf('.')).toLowerCase();
			if(ext.equals(".txt")) {
				Type type = new TypeToken<ArrayList<Strategy>>() {}.getType();  
				EClientSocketUtils.strategies = new Gson().fromJson(IOUtils.toString(template.getInputStream()), type);	
				for (Strategy strategy : EClientSocketUtils.strategies) {
					strategy.inactive();
				}
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return false;
	}
	
	@RequestMapping("downloadSampleDate")
	public void downloadSampleDate(String sampleDate, String ticker, HttpServletResponse response) throws IOException{
		response.setContentType("application/msexcel");  
		response.setHeader("Content-Disposition","attachment; filename=" + sampleDate + ".csv");
		IOUtils.write(SQLUtils.getScheduledDataRecordByDate(sampleDate, ticker), response.getOutputStream());
	}
	
	
	public static boolean deleting = false;
	@RequestMapping("deleteSampleDate")
	@ResponseBody
	public String deleteSampleDate(String sampleDate, String ticker, HttpServletResponse response) throws IOException{
		if(!deleting) {
			deleting = true;
			SQLUtils.deleteScheduledDataRecordByDate(sampleDate, ticker, isToDatabase);
			deleting = false;
			return "Data with date=" + sampleDate + " and ticker=" + ticker + " have been deleted from database.";
		} else {
			return "A deletion is runing, please wait for previous deletion is done!!!";
		}
	}
	
	@RequestMapping("downloadSummary")
	public void downloadSummary(String ticker, HttpServletResponse response) throws IOException{
		response.setContentType("application/msexcel");  
		response.setHeader("Content-Disposition","attachment; filename=" + ticker + "_summary.csv");
		IOUtils.write(SQLUtils.getSummary(ticker), response.getOutputStream());
	}
	
	
	@RequestMapping("combineSummary")
	public void combineSummary(MultipartFile summaryResult1, MultipartFile summaryResult2, HttpServletResponse response, HttpServletRequest request) throws IOException {
				
//		Test no.,key,version,Source,CP timer,CP Buffer,CP Hit Rate,CP smooth,estimation buffer,action trigger,action counting,% trade stoploss trigger,% trade stoploss,Absolute trade stoploss,Morning Start Time,Lunch Start Time,Cash per index point,Trading fee,Other cost per trade,No. of days,Total PnL,Average PnL ,Total trades,Average trades,No. of winning days,No. of losing days,Winning %,Average gain per +ve trade,Average gain per -ve trade,Average 0 PnL trades,Average no. of positive trade,Average no. of negative trade,Average holding time,Adjusted Profit after fee,Worst Lossing Day,Best Profit Day,Worst Lossing Streak,Best Winning Streak,Lossing Streak freq,Winning Streak freq,Sum Of Lossing Streak,Sum Of Winning Streak,Avg Of Lossing Streak,Avg Of Winning Streak,Max Lossing Streak Length,Max Winning Streak Length," + yearColumnStr +"Start Time,End Time,Including Morning Data,Ignore Lunch Time,Average Step Size,Include Last Market Day Data," + monthColumStr + "\n";
//
//		totalDays = SUM
//		totalPnL = SUM
//		averagePnL = totalPnL/totalDays
//		totalTrades = SUM
//		averageTrades = totalTrades/totalDays
//		totalWinningDays = SUM
//		totalLosingDays = SUM
//		winningPercentage = totalWinningDays/totalDays * 100
//		averageProfitOfPositiveTrade = SUM(_averageProfitOfPositiveTrade * _totalDays)/totalDays
//		averageProfitOfNegativeTrade = SUM(_averageProfitOfNegativeTrade * _totalDays)/totalDays
//		averageZeroPnLTrade = SUM(_averageZeroPnLTrade * _totalDays)/totalDays
//		averageNoPositiveTrade = SUM(_averageNoPositiveTrade * _totalDays)/totalDays
//		averageNoNegativeTrade = SUM(_averageNoNegativeTrade * _totalDays)/totalDays
//		averageHoldingTime = SUM(_averageHoldingTime * _totalDays)/totalDays
//		adjustedPnLAfterFee = (testSet.getCashPerIndexPoint()*totalPnL) - ((testSet.getTradingFee() + testSet.getOtherCostPerTrade())*totalTrades)
//		worstLossDay = MIN
//		bestProfitDay = MAX
//		worstLossingStreak = MIN
//		bestWinningStreak = MAX
//		lossingStreakfreq = SUM
//		winningStreakFreq = SUM
//		sumOfLossingStreak = SUM
//		sumOfWinningStreak = SUM
//		averageOfLossingStreak = sumOfLossingStreak/lossingStreakfreq;
//		averageOfWinningStreak = sumOfWinningStreak/winningStreakFreq;
//		maxLossingStreakLength = MAX
//		maxWinningStreakLength = MAX
		
		List<List<String>> summaryResultData1 = extractSummaryData(summaryResult1);
		List<List<String>> summaryResultData2 = extractSummaryData(summaryResult2);
		
		boolean sameCombination = true;
		if(summaryResultData1.size() != summaryResultData2.size()) {
			sameCombination = false;
		}
//		List<String> needCombineColumns = Arrays.asList("No. of days,Total PnL,Average PnL ,Total trades,Average trades,No. of winning days,No. of losing days,Winning %,Average gain per +ve trade,Average gain per -ve trade,Average 0 PnL trades,Average no. of positive trade,Average no. of negative trade,Average holding time,Adjusted Profit after fee,Worst Lossing Day,Best Profit Day,Worst Lossing Streak,Best Winning Streak,Lossing Streak freq,Winning Streak freq,Sum Of Lossing Streak,Sum Of Winning Streak,Avg Of Lossing Streak,Avg Of Winning Streak,Max Lossing Streak Length,Max Winning Streak Length".split(","));
		String NO_OF_DAYS = "No. of days"; //FIRST
		String MAX_WINNING_STREAK_LENGTH = "Max Winning Streak Length"; //LAST
		
		String MORNING_START_TIME = "Morning Start Time"; //LAST
		String LUNCH_START_TIME = "Lunch Start Time"; //LAST
		List<String> header = summaryResultData1.get(0);
		int end = header.indexOf(NO_OF_DAYS);
		
		int morningStartTimeIndex = header.indexOf(MORNING_START_TIME);
		int lunchStartTimeIndex = header.indexOf(LUNCH_START_TIME);
		for(int i = 1; i < summaryResultData1.size(); i++) {
			List<String> line1 = summaryResultData1.get(i);					
			String a = line1.subList(0, morningStartTimeIndex).toString() + line1.subList(lunchStartTimeIndex + 1, end).toString();
			List<String> line2 = summaryResultData2.get(i);
			String b = line2.subList(0, morningStartTimeIndex).toString() + line2.subList(lunchStartTimeIndex + 1, end).toString();
			
			if (!a.equals(b)) {
				sameCombination = false;
				break;
			}
		}
		
		if (sameCombination) {
			response.setContentType("application/msexcel");  
			response.setHeader("Content-Disposition","attachment; filename=BT_Summary.csv");
		
			StringBuffer sb = new StringBuffer(String.join(",", header.subList(0, header.indexOf(MAX_WINNING_STREAK_LENGTH) + 1)) + "\n");
			for(int i = 1; i < summaryResultData1.size(); i++) {		
				double totalDays = Double.valueOf(summaryResultData1.get(i).get(end)) + Double.valueOf(summaryResultData2.get(i).get(end));
				double totalPnL = Double.valueOf(summaryResultData1.get(i).get(end + 1)) + Double.valueOf(summaryResultData2.get(i).get(end + 1));
				double averagePnL = totalPnL / totalDays;
				double totalTrades = Double.valueOf(summaryResultData1.get(i).get(end + 3)) + Double.valueOf(summaryResultData2.get(i).get(end + 3));
				double averageTrades = totalTrades / totalDays;
				double totalWinningDays = Double.valueOf(summaryResultData1.get(i).get(end + 5)) + Double.valueOf(summaryResultData2.get(i).get(end + 5));
				double totalLosingDays = Double.valueOf(summaryResultData1.get(i).get(end + 6)) + Double.valueOf(summaryResultData2.get(i).get(end + 6));
				double winningPercentage = totalWinningDays / totalDays * 100;
				double averageProfitOfPositiveTrade = (Double.valueOf(summaryResultData1.get(i).get(end + 8)) * Integer.parseInt(summaryResultData1.get(i).get(end)) + Double.valueOf(summaryResultData2.get(i).get(end + 8)) * Integer.parseInt(summaryResultData2.get(i).get(end))) / totalDays;
				double averageProfitOfNegativeTrade = (Double.valueOf(summaryResultData1.get(i).get(end + 9)) * Integer.parseInt(summaryResultData1.get(i).get(end)) + Double.valueOf(summaryResultData2.get(i).get(end + 9)) * Integer.parseInt(summaryResultData2.get(i).get(end))) / totalDays;
				double averageZeroPnLTrade = (Double.valueOf(summaryResultData1.get(i).get(end + 10)) * Integer.parseInt(summaryResultData1.get(i).get(end)) + Double.valueOf(summaryResultData2.get(i).get(end + 10)) * Integer.parseInt(summaryResultData2.get(i).get(end))) / totalDays;				
				double averageNoPositiveTrade = (Double.valueOf(summaryResultData1.get(i).get(end + 11)) * Integer.parseInt(summaryResultData1.get(i).get(end)) + Double.valueOf(summaryResultData2.get(i).get(end + 11)) * Integer.parseInt(summaryResultData2.get(i).get(end))) / totalDays;
				double averageNoNegativeTrade = (Double.valueOf(summaryResultData1.get(i).get(end + 12)) * Integer.parseInt(summaryResultData1.get(i).get(end)) + Double.valueOf(summaryResultData2.get(i).get(end + 12)) * Integer.parseInt(summaryResultData2.get(i).get(end))) / totalDays;
				double averageHoldingTime = (Double.valueOf(summaryResultData1.get(i).get(end + 13)) * Integer.parseInt(summaryResultData1.get(i).get(end)) + Double.valueOf(summaryResultData2.get(i).get(end + 13)) * Integer.parseInt(summaryResultData2.get(i).get(end))) / totalDays;
				
				double cashPerIndexPoint = Double.valueOf(summaryResultData1.get(i).get(header.indexOf("Cash per index point")));
				double tradingFee = Double.valueOf(summaryResultData1.get(i).get(header.indexOf("Trading fee")));
				double otherCostPerTrade = Double.valueOf(summaryResultData1.get(i).get(header.indexOf("Other cost per trade")));
				double adjustedPnLAfterFee = (cashPerIndexPoint*totalPnL) - ((tradingFee + otherCostPerTrade)*totalTrades);				
				
				double worstLossDay = Math.min(Double.valueOf(summaryResultData1.get(i).get(end + 15)), Double.valueOf(summaryResultData2.get(i).get(end + 15)));
				double bestProfitDay = Math.max(Double.valueOf(summaryResultData1.get(i).get(end + 16)), Double.valueOf(summaryResultData2.get(i).get(end + 16)));
				double worstLossingStreak = Math.min(Double.valueOf(summaryResultData1.get(i).get(end + 17)), Double.valueOf(summaryResultData2.get(i).get(end + 17)));
				double bestWinningStreak = Math.max(Double.valueOf(summaryResultData1.get(i).get(end + 18)), Double.valueOf(summaryResultData2.get(i).get(end + 18)));				
				double lossingStreakfreq = Double.valueOf(summaryResultData1.get(i).get(end + 19)) + Double.valueOf(summaryResultData2.get(i).get(end + 19));				
				double winningStreakFreq = Double.valueOf(summaryResultData1.get(i).get(end + 20)) + Double.valueOf(summaryResultData2.get(i).get(end + 20));
				double sumOfLossingStreak = Double.valueOf(summaryResultData1.get(i).get(end + 21)) + Double.valueOf(summaryResultData2.get(i).get(end + 21));
				double sumOfWinningStreak = Double.valueOf(summaryResultData1.get(i).get(end + 22)) + Double.valueOf(summaryResultData2.get(i).get(end + 22));	
				double averageOfLossingStreak = lossingStreakfreq == 0 ? 0 : sumOfLossingStreak / lossingStreakfreq;
				double averageOfWinningStreak = lossingStreakfreq == 0 ? 0 : sumOfWinningStreak / winningStreakFreq;				
				double maxLossingStreakLength = Math.max(Double.valueOf(summaryResultData1.get(i).get(end + 25)), Double.valueOf(summaryResultData2.get(i).get(end + 25)));
				double maxWinningStreakLength = Math.max(Double.valueOf(summaryResultData1.get(i).get(end + 26)), Double.valueOf(summaryResultData2.get(i).get(end + 26)));
				
				sb.append(String.join(",", summaryResultData1.get(i).subList(0, end)) + ",")
				.append(totalDays + ",")
				.append(totalPnL + ",")
				.append(averagePnL + ",")
				.append(totalTrades + ",")
				.append(averageTrades + ",")
				.append(totalWinningDays + ",")
				.append(totalLosingDays + ",")
				.append(winningPercentage + ",")
				.append(averageProfitOfPositiveTrade + ",")
				.append(averageProfitOfNegativeTrade + ",")
				.append(averageZeroPnLTrade + ",")
				.append(averageNoPositiveTrade + ",")
				.append(averageNoNegativeTrade + ",")
				.append(averageHoldingTime + ",")
				.append(adjustedPnLAfterFee + ",")
				.append(worstLossDay + ",")
				.append(bestProfitDay + ",")
				.append(worstLossingStreak + ",")
				.append(bestWinningStreak + ",")
				.append(lossingStreakfreq + ",")
				.append(winningStreakFreq + ",")
				.append(sumOfLossingStreak + ",")
				.append(sumOfWinningStreak + ",")
				.append(averageOfLossingStreak + ",")
				.append(averageOfWinningStreak + ",")
				.append(maxLossingStreakLength + ",")
				.append(maxWinningStreakLength + "\n");
			}
			
			IOUtils.write(sb.toString(), response.getOutputStream());			
		} else {
			response.setContentType("text/html;charset=utf-8");		
			response.getWriter().write("<script>history.go(-1);alert('Fail to combine the result as the two summary result are with different combination');</script>");
		}
		
	}
	
	public List<List<String>> extractSummaryData(MultipartFile summaryResult) throws IOException {
		CSVReader summaryResultReader = new CSVReader(new InputStreamReader(summaryResult.getInputStream()), ',', '\n', 0);		
		String[] lines;
		List<List<String>> data = new ArrayList<List<String>>();
		while ((lines = summaryResultReader.readNext()) != null)  {
			data.add(Arrays.asList(lines));
		}
		
		summaryResultReader.close();
		return data;
	}
	
	public static List<String> uploadStatus = new ArrayList<String>();
	@ResponseBody
	@RequestMapping("uploadData")
	public boolean uploadData(String startDateStr, String endDateStr, String source, String ticker, String dataType, String ignoreLunchTime, String toDatabase, String toCSV, String csvPath, String dataStartTime, String lunchStartTime, String lunchEndTime, String dataEndTime, String uploadAction, MultipartFile liveData, HttpServletResponse response, HttpServletRequest request) throws IOException {
		String FINISHED = "Finished";
		boolean success = false;
		if(uploadStatus.size() > 0 && uploadStatus.get(uploadStatus.size() - 1).indexOf(FINISHED) < 0) {
			return success;
		}
		try{
			startDate = DateUtils.yyyyMMdd().parse(startDateStr);
		} catch (Exception e) {
			startDate = null;
		}
		try{
			endDate = DateUtils.yyyyMMdd().parse(endDateStr);
		} catch (Exception e) {
			endDate = null;
		}
		isToCSV = toCSV != null && "on".equals(toCSV.toLowerCase());
		isToDatabase = toDatabase != null && "on".equals(toDatabase.toLowerCase());
		isIgnoreLunchTime = ignoreLunchTime != null && "on".equals(ignoreLunchTime.toLowerCase());
		uploadDataType = dataType;
		uploadSource = source;
		uploadTicker = ticker;
		csvDownloadFolder=FilenameUtils.concat(System.getProperty("java.io.tmpdir"),DateUtils.yyyyMMddHHmmss2().format(new Date()));
		new File(csvDownloadFolder).mkdirs();
		try{
			if(new File(csvPath).isDirectory()) {
				csvDownloadFolder = csvPath;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		boolean isCheck = "check".equals(uploadAction);
		uploadStatus = new ArrayList<String>();
		boolean isReplace = "replace".equals(uploadAction);
		boolean isSkip = "skip".equals(uploadAction);
		boolean isTransfer = "transfer".equals(uploadAction);
		if(isCheck) {
			uploadStatus.add("Start with checking(<font size='3' color='blue'>data will not be uploaded</font>)...");		
		} else if(isReplace) {
			uploadStatus.add("Start upload with <font size='3' color='blue'>Replace</font> mode...");
		} else if(isSkip) {
			uploadStatus.add("Start upload with <font size='3' color='blue'>Skip</font> mode...");
		} else {
			uploadStatus.add("Start upload with data transfer");
		}
		if(isToCSV && (isReplace || isSkip) || isTransfer) {
			uploadStatus.add("The csv result will store on the folder:<font size='5' color='blue'>" + csvDownloadFolder + "</font>");
		}
//		Date startTime = null;
//		Date lunchTimeFrom = null;
//		Date lunchTimeTo = null;
//		Date endTime = null;
//		try {
//			startTime = DateUtils.HHmmss().parse(dataStartTime);
//			lunchTimeFrom = DateUtils.HHmmss().parse(lunchStartTime);
//			lunchTimeTo = DateUtils.HHmmss().parse(lunchEndTime);
//			endTime = DateUtils.HHmmss().parse(dataEndTime);
//			if(startTime.equals(lunchTimeFrom) || startTime.before(lunchTimeFrom) 
//					&& lunchTimeFrom.equals(lunchTimeTo) || lunchTimeFrom.before(lunchTimeTo) 
//					&& lunchTimeTo.equals(endTime) || lunchTimeTo.before(endTime)) {				
				String unzipFolder = null;
				try {
					String ext = liveData == null ? "" : liveData.getOriginalFilename().substring(liveData.getOriginalFilename().lastIndexOf('.')).toLowerCase();
					if(ext.equals(".zip")) {
						
						// create temp folder
						String tempFolder = FilenameUtils.concat(InitServlet.createUploadFoderAndReturnPath(), DateUtils.yyyyMMddHHmmss2().format(new Date()));
						File tempFolderFile = new File(tempFolder);
						if(tempFolderFile.exists())
							FileUtils.deleteQuietly(tempFolderFile);
						tempFolderFile.mkdirs();
						
						uploadStatus.add("Uploading....");
						// save zip file to local disk
						String zipFile = FilenameUtils.concat(tempFolder, liveData.getOriginalFilename());
						FileUtils.copyInputStreamToFile(liveData.getInputStream(), new File(zipFile));
						uploadStatus.add("Upload completed");
						
						// unzip to a folder
						uploadStatus.add("Unzipping....");
						unzipFolder = FilenameUtils.concat(tempFolder, FilenameUtils.getBaseName(liveData.getOriginalFilename()));
						File unzipFolderFile = new File(unzipFolder);
						unzipFolderFile.mkdirs();				
						uploadStatus.add(ZipUtils.decompress(zipFile, unzipFolder, true));
						uploadStatus.add("Unzip completed");
						
						// delete zip file
						new File(zipFile).delete();
						
						// retrieve the excel files
						Collection<File> files = FileUtils.listFiles(unzipFolderFile, new SuffixFileFilter(new ArrayList<String>(){{add("xlsm"); add("xls"); add("xlsx"); add("csv"); add("txt");}}), TrueFileFilter.TRUE);
						
						if(files.size() > 0) {
							if (isCheck) {
								if(uploadDataType.equals("1")) {
									check(dataStartTime, lunchStartTime, lunchEndTime, dataEndTime, files);								
								}
								
								// delete the unzip folder, just keep the zip file
								FileUtils.deleteQuietly(tempFolderFile);
							} else if(isReplace || isSkip) {
								// write audit log
								FileOutputStream logFileOutputStream = new FileOutputStream(new File(FilenameUtils.concat(tempFolder, "log.txt")));
								IOUtils.write(dataStartTime + "	" + lunchStartTime + "	" + lunchEndTime + "	"  + dataEndTime + "	" + uploadAction, logFileOutputStream);
								logFileOutputStream.close();
								
								uploadWithAction(dataStartTime, lunchStartTime, lunchEndTime, dataEndTime, files, isReplace);				
								
								// delete the unzip folder, just keep the zip file
								FileUtils.deleteQuietly(unzipFolderFile);
							} else {
								uploadWithTransfer(dataStartTime, lunchStartTime, lunchEndTime, dataEndTime, files);
								// delete the unzip folder, just keep the zip file
								FileUtils.deleteQuietly(unzipFolderFile);
							}
							success = true;
						} else {
							uploadStatus.add("No excel file found from the upload zip file");
						}
					} else {
						uploadStatus.add("Please upload a zip file");
					}
				} catch (Exception ex) {
					ex.printStackTrace();			
					uploadStatus.add("Upload with exception => " + ex.getMessage());
				} finally {
					if(unzipFolder != null) {
						FileUtils.deleteQuietly(new File(unzipFolder));
					}
				}
				
//			} else {
//				uploadStatus.add("Please check your input time");
//			}
//		} catch (ParseException e) {
//			uploadStatus.add("Please input valdate time!");
//		}
		uploadStatus.add((isCheck ? "Check " : ((isReplace || isSkip) ?  "Upload " : "Transfer ")) + FINISHED);
		return success;
	}

	private void check(String dataStartTime, String lunchStartTime, String lunchEndTime, String dataEndTime, Collection<File> files) throws ParseException, IOException, OpenXML4JException, SAXException {
		for(File file : files) {
			String name = "<font size='3' color='blue'>" + FilenameUtils.getName(file.getName()) + "</font>";
			uploadStatus.add("Doing checking for " + name + " ...");
	        new BigExcelReader(file) {  
	        	@Override  
	        	protected void outputRow(int sheetIndex, int rowIndex, int curCol, List<String> datas) {  
	        			if(sheetIndex >= startSheet && rowIndex == 0) {
	        				boolean validateDate = false;
	        				String dateStr = null;
	        				try {
	        					dateStr = datas.get(2);
	        					DateUtils.yyyyMMdd().parse(dateStr);	        					
	        					validateDate = true;
	        				} catch (Exception e) {
	        					validateDate = false;
							}
	        				String sheet = "<font size='3' color='red'>Sheet" + (sheetIndex + 1) + "</font>";
	        				if (validateDate) {
	        					String _dataStartTime = dateStr + " " + dataStartTime; 
	        					String _lunchStartTime = dateStr + " " + lunchStartTime; 
	        					String _lunchEndTime = dateStr + " " + lunchEndTime;
	        					String _dataEndTime = dateStr + " " + dataEndTime;
	        					int totalCount = SQLUtils.checkScheduledDataExisting(dateStr, uploadSource, uploadTicker);	
//	        					int totalCount2 = SQLUtils.checkScheduledDataExisting(_lunchEndTime, _dataEndTime, uploadSource, uploadTicker);
	        					if (totalCount == 0) {
	        						// no data in db
	        						uploadStatus.add("Not exists data within period(" + _dataStartTime +" to " + _dataEndTime + ") in database. The data(" + dateStr + ") at " + sheet + " will be <font size='4' color='blue'>uploaded</font>");
	        					} else {
	        						// exists data in db
	        						uploadStatus.add("Exists data within period(" + _dataStartTime +"-" + _dataEndTime + ") in database. The database data may be <font size='4' color='red'>replaced</font> with the data(" + dateStr + ") at " + sheet);
	        					}															
	        				} else {
	        					uploadStatus.add("Can not detect the <font size='3' color='red'>Date cell(C1)</font> at " + sheet + ", this sheet will be <font size='4' color='red'>skipped</font>");
	        				}
	        			}
	        	}  
	        };
		}
	}

//	private String genSouce(String source) {
//		if (StringUtils.isEmpty(source))
//			return source;
//		String[] sources = source.split(" ");
//		return sources[0];
//	}

	private static int startSheet = 2;
	private static Map<Long, List<Double>> tradeMap = null;
	private static Map<Long, List<Double>> askMap = null;
	private static Map<Long, List<Double>> bidMap = null;
	private static boolean validateSheet = false;
	private static Date date = null;
	private static Date startDate = null;
	private static Date endDate = null;
//	private static String source = "";
	private static String sheet="";
	private int previousSheetIndex = 0;
	private String csvDownloadFolder;
	private boolean isToCSV;
	private boolean isToDatabase;
	private boolean isIgnoreLunchTime;
	private String uploadDataType;
	private String uploadSource;
	private String uploadTicker;
	private void uploadWithTransfer(String dataStartTime, String lunchStartTime, String lunchEndTime, String dataEndTime, Collection<File> files) throws IOException, OpenXML4JException, SAXException {
		for(File file : files) {
			String name = "<font size='3' color='blue'>" + FilenameUtils.getName(file.getName()) + "</font>";
			uploadStatus.add("Retriving data from " + name + " ...");
			List<String> entities = new ArrayList<String>();
//			entity->yyyy-mm-dd->time id->scheduledata
			List<Map<String, Map<Long, ScheduleData>>> dataMap = new ArrayList<Map<String, Map<Long, ScheduleData>>>();
			new BigExcelReader(file) {  
	        	@Override  
	        	protected void outputRow(int sheetIndex, int rowIndex, int curCol, List<String> datas) {
	        		if(sheetIndex != 0) return;
	        		if(rowIndex == 0) {
	        			for(String data : datas) {
	        				if(!StringUtils.isBlank(data)) {
	        					entities.add(data);
	        				}
	        			}
	        		} else {
	        			for(int i = 0; i < entities.size(); i++) {
	        				try {
	        					int index = i * 5;
								Date date = DateUtils.yyyyMMddHHmmss().parse(datas.get(index));
	        					String dateStr = DateUtils.yyyyMMdd().format(date);
	        					long id = date.getTime();
        						String type = datas.get(index + 1).trim();
        						double price = Double.parseDouble(datas.get(index + 2).trim());
        						String preDateStr = "";
        						try {
        							preDateStr = (String)dataMap.get(i).keySet().toArray()[0];
        						} catch (Exception e) {
								}
        						int size = Integer.parseInt(datas.get(index + 3).trim());
        						if(dataMap.size() == i) dataMap.add(new HashMap<String, Map<Long, ScheduleData>>());
        						if(!dataMap.get(i).containsKey(dateStr)) dataMap.get(i).put(dateStr, new HashMap<Long, ScheduleData>());
        						Map<Long, ScheduleData> sMap = dataMap.get(i).get(dateStr);
        						ScheduleData scheduleData = new ScheduleData(date, 0, 0, 0, 0, 0, 0);
        						if(sMap.containsKey(id)) {
        							scheduleData = sMap.get(id);
        						}
        						switch (type) {
									case "ASK":
										scheduleData.setAskPrice(price);
										scheduleData.setAskSize(size);
										break;
									case "BID":
										scheduleData.setBidPrice(price);
										scheduleData.setBidSize(size);
										break;
									case "TRADE":
										scheduleData.setLastTrade(price);
										scheduleData.setLastTradeSize(size);
										break;	
									default:
										break;
								}
        						if(sMap.containsKey(id)) {
        							sMap.replace(id, scheduleData);
        						} else {
        							sMap.put(id, scheduleData);
        						}
        						if(dataMap.get(i).keySet().size() == 2) {
        							List<ScheduleData> scheduledDataRecords = new ArrayList<ScheduleData>(dataMap.get(i).get(preDateStr).values());
        							String folder = FilenameUtils.concat(csvDownloadFolder, preDateStr);
        							if (!new File(folder).exists()) {
        								new File(folder).mkdirs();
        							}
        							String fileName = FilenameUtils.concat(folder, entities.get(i) + ".csv");
        							transferData(scheduledDataRecords, fileName, dataStartTime, lunchStartTime, lunchEndTime, dataEndTime);
        							dataMap.get(i).remove(preDateStr);
        						}
	        				} catch (Exception e) {
							}
	        			}
	        		}
	        	}
        	};
		
        	for(int i = 0; i < entities.size(); i++) {
        		try {
        			for(String key : dataMap.get(i).keySet()) {
						String folder = FilenameUtils.concat(csvDownloadFolder, key);
						if (!new File(folder).exists()) {
							new File(folder).mkdirs();
						}
						String fileName = FilenameUtils.concat(folder, entities.get(i) + ".csv");
        				List<ScheduleData> scheduleDatas = new ArrayList<ScheduleData>(dataMap.get(i).get(key).values());
        				transferData(scheduleDatas, fileName, dataStartTime, lunchStartTime, lunchEndTime, dataEndTime);
        			}        			
        		} catch (Exception e) {
				}
        	}
		}
	}
	
	private void transferData(List<ScheduleData> scheduleDatas, String fileName, String dataStartTime, String lunchStartTime, String lunchEndTime,
			String dataEndTime) throws ParseException {
		if(scheduleDatas.size() == 0) return;
		scheduleDatas.sort(new Comparator<ScheduleData>() {

			@Override
			public int compare(ScheduleData o1, ScheduleData o2) {
				// TODO Auto-generated method stub
				return new Long(o1.getId()).compareTo(o2.getId());
			}
		});
		StringBuffer sb = new StringBuffer("Time,BID,bid size,ASK,ask size,TRADE,trade size" + System.lineSeparator());
		long start = scheduleDatas.get(0).getId();
		double askPrice = 0; 
		int askSize = 0;
		double bidPrice = 0;
		int bidSize = 0;
		double lastTrade = 0;
		int lastTradeSize = 0;
		Date startTime = DateUtils.yyyyMMddHHmmss().parse(scheduleDatas.get(0).getDateStr() + " " + dataStartTime); 
		long _start = startTime.getTime();
		while(_start < start) {
			ScheduleData s = new ScheduleData(new Date(_start), 
					askPrice, askSize, 
					bidPrice, bidSize, 
					lastTrade, lastTradeSize);
			sb.append(toTransferRecord(s, dataStartTime, lunchStartTime, lunchEndTime, dataEndTime));
			_start = _start + 1000;
		}
		for(int j = 0; j < scheduleDatas.size(); j++) {
			ScheduleData scheduledData = scheduleDatas.get(j);
			if(scheduledData.getAskPrice() > 0) {
				askPrice = scheduledData.getAskPrice(); 
				askSize = scheduledData.getAskSize();
			}
			if(scheduledData.getBidPrice() > 0) {
				bidPrice = scheduledData.getBidPrice();
				bidSize = scheduledData.getBidSize();			
			}
			if(scheduledData.getLastTrade() > 0) {
				lastTrade = scheduledData.getLastTrade();
				lastTradeSize = scheduledData.getLastTradeSize();
			}
			long current = scheduledData.getId();
			while(start < current) {
				ScheduleData s = new ScheduleData(new Date(start), 
						askPrice, askSize, 
						bidPrice, bidSize, 
						lastTrade, lastTradeSize);
				sb.append(toTransferRecord(s, dataStartTime, lunchStartTime, lunchEndTime, dataEndTime));
				start = start + 1000;
			}
			ScheduleData s = new ScheduleData(new Date(start), 
					scheduledData.getAskPrice() == 0 ? askPrice : scheduledData.getAskPrice(), scheduledData.getAskPrice() == 0 ? askSize : scheduledData.getAskSize(), 
					scheduledData.getBidPrice() == 0 ? bidPrice : scheduledData.getBidPrice(), scheduledData.getBidPrice() == 0 ? bidSize : scheduledData.getBidSize(), 
					scheduledData.getLastTrade() == 0 ? lastTrade : scheduledData.getLastTrade(), scheduledData.getLastTrade() == 0 ? lastTradeSize :scheduledData.getLastTradeSize());
			sb.append(toTransferRecord(s, dataStartTime, lunchStartTime, lunchEndTime, dataEndTime));				
			start = start + 1000;
		}
		Date endTime = DateUtils.yyyyMMddHHmmss().parse(scheduleDatas.get(0).getDateStr() + " " + dataEndTime); 
		long lastSecond = endTime.getTime();
		while(start <= lastSecond) {
			ScheduleData s = new ScheduleData(new Date(start), 
					askPrice, askSize, 
					bidPrice, bidSize, 
					lastTrade, lastTradeSize);
			sb.append(toTransferRecord(s, dataStartTime, lunchStartTime, lunchEndTime, dataEndTime));
			start = start + 1000;
		}
		BackTestCSVWriter.writeText(fileName, sb.toString(), false);
		uploadStatus.add("Writting " + "<font size='3' color='blue'>" + fileName + "</font>" + " successfully");
	}

	private String toTransferRecord(ScheduleData s, String dataStartTime, String lunchStartTime, String lunchEndTime,
			String dataEndTime) {
		Date when = new Date(s.getId());		
		String _dataStartTime = DateUtils.yyyyMMdd().format(when) + " " + dataStartTime; 
		String _lunchStartTime = DateUtils.yyyyMMdd().format(when) + " " + lunchStartTime; 
		String _lunchEndTime = DateUtils.yyyyMMdd().format(when) + " " + lunchEndTime;
		String _dataEndTime = DateUtils.yyyyMMdd().format(when) + " " + dataEndTime;
		boolean flag = false;
		if(isIgnoreLunchTime) {
			flag = DateUtils.isValidateTime(when, _dataStartTime, _dataEndTime);
		} else {
			flag = DateUtils.isValidateTime(when, _dataStartTime, _lunchStartTime)
					|| DateUtils.isValidateTime(when, _lunchEndTime, _dataEndTime);
		}
		if(flag)
			return s.getDateTimeStr() + ","
				+ s.getBidPrice() + ","
				+ s.getBidSize() + ","
				+ s.getAskPrice() + ","
				+ s.getAskSize() + ","
				+ s.getLastTrade() + ","
				+ s.getLastTradeSize() + System.lineSeparator();
		return "";
	}
	public static List<String> expiryDates = null;//Arrays.asList("080130","080228","080328","080429","080529","080627","080730","080828","080929","081030","081127","081230","090129","090226","090330","090429","090527","090629","090730","090828","090929","091029","091127","091230","100128","100225","100330","100429","100528","100629","100729","100830","100929","101028","101129","101230","110128","110225","110330","110428","110530","110629","110728","110830","110929","111028","111129","111229","120130","120228","120329","120427","120530","120628","120730","120830","120927","121030","121129","121228","130130","130227","130327","130429","130530","130627","130730","130829","130927","131030","131128","131230","140129","140227","140328","140429","140529","140627","140730","140828","140929","141030","141127","141230","150129","150226","150330","150429","150528","150629","150730","150828","150929","151029","151127","151230","160128","160226","160330","160428","160530","160629","160728","160830","160929","161028","161129","161229","170126","170227","170330","170427","170529","170629","170728","170830","170928","171030","171129","171228","180130","180227","180328","180427","180530","180628","180730","180830","180927","181030","181129","181228","190328","190627","191230","201230","211230","221229","231228");
	
	public String getExpiryMonth(String date) throws ParseException {
		for(String expiryDate : expiryDates) {
			if(expiryDate.substring(0, 4).equals(date.substring(2, 6))) {
				if(date.substring(2, 8).compareTo(expiryDate) >= 0) {
				    Calendar now = Calendar.getInstance();
					now.setTime(DateUtils.yyyyMMdd2().parse(date));
					now.add(Calendar.MONTH, 1);
					return DateUtils.yyyyMMdd2().format(now.getTime()).substring(2, 6);
				} else {
					return date.substring(2, 6);
				}
			}
		}
		return date.substring(2, 6);
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(new IndexController().getExpiryMonth("20080105"));
		System.out.println(new IndexController().getExpiryMonth("20080129"));
		System.out.println(new IndexController().getExpiryMonth("20080130"));
		System.out.println(new IndexController().getExpiryMonth("20080201"));
		System.out.println(new IndexController().getExpiryMonth("20080228"));
		System.out.println(new IndexController().getExpiryMonth("20280228"));
		System.out.println(ZipUtils.decompress("C:\\Users\\yuanke\\Desktop\\2012.zip", "C:\\Users\\yuanke\\Desktop\\2012\\", true));
	}
	
	private void uploadWithAction(String dataStartTime, String lunchStartTime, String lunchEndTime, String dataEndTime, Collection<File> files, boolean isReplace) throws IOException, OpenXML4JException, SAXException, ParseException {
		if (uploadDataType.equals("4")) {
			Map<String, List<String>> datas = new HashMap<String, List<String>>();
			uploadStatus.add("Start combining data ...");
			List<Date> expiryDates2 = SQLUtils.getExpiryDates();
			expiryDates = new CopyOnWriteArrayList<String>();
			for(Date d : expiryDates2) {
				expiryDates.add(DateUtils.yyMMdd().format(d));
			}
			
			for(File file : files) {
				if(!file.getAbsolutePath().endsWith("_TR.txt") && !file.getAbsolutePath().endsWith("_BA.txt"))
					continue;//only parse _TR.txt and _BA.txt file for HKEX data
				FileInputStream input = null;				
				try {
					boolean isBA = file.getAbsolutePath().endsWith("_BA.txt");
					input = new FileInputStream(file);
					List<String> lines = IOUtils.readLines(input);
					for(String line : lines) {
						boolean isHSI = line.substring(0, 6).trim().equals("HSI");
						boolean isF = line.substring(6, 7).equals("F");
						String expiryMonth = line.substring(7, 11);
						String dateTime = line.substring(29, 43);
						String date = line.substring(29, 37);
						String strickPrice = line.substring(11, 28);
						if(!isHSI || !isF) continue;
						if(!isBA) {
							String type = line.substring(68, 71);
							if(!Arrays.asList("000", "001", "002").contains(type)) continue;
						}
						if(!expiryMonth.equals(getExpiryMonth(date))) continue;
						String type = "";
						String price = "";
						if(isBA) {							
							boolean isA = line.substring(43, 44).equals("A");
							type = isA ? "A" : "B";
							price = line.substring(44, 61);
						} else {														
							price = line.substring(43, 60);
							type = "T";
						}
						datas.putIfAbsent(date, new ArrayList<String>());
						datas.get(date).add(dateTime + "," + type + "," + price);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(input != null) {
						input.close();						
					}					
				}				
			}
			List<String> days = new ArrayList<String>();
			for(String key : datas.keySet()) {
				days.add(key);
			}
			Collections.sort(days);
			for(String day : days) {		
			    
				if(startDate != null || endDate != null) {
					Date now = DateUtils.yyyyMMdd2().parse(day);
					if (startDate != null && now.before(startDate) || endDate != null && now.after(endDate)) {
						uploadStatus.add("Skip for " + day + " as it is not in the date range");
						continue;	
					}
				}
				
				tradeMap = new TreeMap<Long, List<Double>>();
				askMap = new TreeMap<Long, List<Double>>();
				bidMap = new TreeMap<Long, List<Double>>();				
				uploadStatus.add("Uploading for " + day);																
				for(String line : datas.get(day)) {
					try {
						String type = line.split(",")[1];
						Date livedate = DateUtils.yyyyMMddHHmmss2().parse(line.split(",")[0]);
						date = livedate;
						double price = Double.valueOf(line.split(",")[2]);
						switch (type) {
						case "B":							
							YosonEWrapper.addLiveData(bidMap, livedate, price);									
							break;
						case "A":							
							YosonEWrapper.addLiveData(askMap, livedate, price);																		
							break;
						case "T":							
							YosonEWrapper.addLiveData(tradeMap, livedate, price);																																		
							break;
						}
					} catch (Exception e) {
					}											
				}
				String dateStr = DateUtils.yyyyMMdd().format(date);
				SQLUtils.deleteScheduledDataRecordByDate(dateStr, uploadTicker, isToDatabase);						
				if(tradeMap.size() == 0) uploadStatus.add("<font size='3' color='red'>Warning: Not Trade data for " + day + "</font>");
				if(askMap.size() == 0 && bidMap.size() == 0) uploadStatus.add("<font size='3' color='red'>Warning: Not BA data for " + day + "</font>");
				writingDatabase(dataStartTime, lunchStartTime, lunchEndTime, dataEndTime, isReplace, YosonEWrapper.extractScheduledDataRecord(tradeMap, askMap, bidMap));
				uploadStatus.add("Upload complete for " + day);
			}
			uploadStatus.add("All upload complete!!!");
			return;
		}
		
		for(File file : files) {
			String name = "<font size='3' color='blue'>" + FilenameUtils.getName(file.getName()) + "</font>";
			uploadStatus.add("Retriving data from " + name + " ...");
			previousSheetIndex = 0;
			tradeMap = null;
			askMap = null;
			bidMap = null;
			validateSheet = false;
			date = null;
//			source = "";
			sheet="";
			previousSheetIndex = 0;
			if (uploadDataType.equals("3")) {				
				String fileName = FilenameUtils.getBaseName(file.getName());
//				source = fileName.split("_")[0];
				uploadStatus.add("Parsing file " + name);
				
				FileInputStream fileInputStream = new FileInputStream(file);
				List<String> readLines = IOUtils.readLines(fileInputStream);
				fileInputStream.close();
				List<ScheduledDataRecord> scheduledDataRecords = new ArrayList<ScheduledDataRecord>();
				
				List<String> dates = new ArrayList<String>();
				for(int i = 3; i <= readLines.size() - 1; i++) {
					ScheduledDataRecord scheduledDataRecord = new ScheduledDataRecord();
					String line = readLines.get(i);
					try {						
						date = DateUtils.yyyyMMddHHmmss().parse(line.split(",")[0]);
						String time = DateUtils.yyyyMMddHHmmss2().format(date); 
						scheduledDataRecord.setTime(time);
						scheduledDataRecord.setTradeavg(Double.parseDouble(line.split(",")[1]));
						scheduledDataRecord.setTradelast(Double.parseDouble(line.split(",")[2]));
						scheduledDataRecord.setTrademax(Double.parseDouble(line.split(",")[3]));
						scheduledDataRecord.setTrademin(Double.parseDouble(line.split(",")[4]));
						
						scheduledDataRecord.setAskavg(Double.parseDouble(line.split(",")[6]));
						scheduledDataRecord.setAsklast(Double.parseDouble(line.split(",")[7]));
						scheduledDataRecord.setAskmax(Double.parseDouble(line.split(",")[8]));
						scheduledDataRecord.setAskmin(Double.parseDouble(line.split(",")[9]));
						
						scheduledDataRecord.setBidavg(Double.parseDouble(line.split(",")[11]));
						scheduledDataRecord.setBidlast(Double.parseDouble(line.split(",")[12]));
						scheduledDataRecord.setBidmax(Double.parseDouble(line.split(",")[13]));
						scheduledDataRecord.setBidmin(Double.parseDouble(line.split(",")[14]));
						
						scheduledDataRecords.add(scheduledDataRecord);
						String dateStr = DateUtils.yyyyMMdd().format(date);
						if(!dates.contains(dateStr)) {
							dates.add(dateStr);
							SQLUtils.deleteScheduledDataRecordByDate(dateStr, uploadTicker, isToDatabase);
						}
					} catch (Exception e) {
					}											
				}
				writingDatabase(dataStartTime, lunchStartTime, lunchEndTime, dataEndTime, isReplace, scheduledDataRecords);
			} else if (uploadDataType.equals("2")) {
				tradeMap = new TreeMap<Long, List<Double>>();
				askMap = new TreeMap<Long, List<Double>>();
				bidMap = new TreeMap<Long, List<Double>>();
				String fileName = FilenameUtils.getBaseName(file.getName());
//				source = fileName.split("_")[0];
				uploadStatus.add("Parsing file " + name);
				
				FileInputStream fileInputStream = new FileInputStream(file);
				List<String> readLines = IOUtils.readLines(fileInputStream);
				fileInputStream.close();
				List<String> dates = new ArrayList<String>();
				for(String line : readLines) {
					try {
						String type = line.split(",")[0];
						Date livedate = DateUtils.yyyyMMddHHmmss2().parse(line.split(",")[1]);
						date = livedate;
						double price = Double.valueOf(line.split(",")[2]);
						switch (type) {
						case "Bid":							
							YosonEWrapper.addLiveData(bidMap, livedate, price);									
							break;
						case "Ask":							
							YosonEWrapper.addLiveData(askMap, livedate, price);																		
							break;
						case "Trade":							
							YosonEWrapper.addLiveData(tradeMap, livedate, price);																																		
							break;
						}
						String dateStr = DateUtils.yyyyMMdd().format(date);
						if(!dates.contains(dateStr)) {
							dates.add(dateStr);
							SQLUtils.deleteScheduledDataRecordByDate(dateStr, uploadTicker, isToDatabase);
						}
					} catch (Exception e) {
					}											
				}
				
				writingDatabase(dataStartTime, lunchStartTime, lunchEndTime, dataEndTime, isReplace, YosonEWrapper.extractScheduledDataRecord(tradeMap, askMap, bidMap));
			} else if (uploadDataType.equals("1")) {
				new BigExcelReader(file) {  
					@Override  
					protected void outputRow(int sheetIndex, int rowIndex, int curCol, List<String> datas) {
						if(validateSheet && rowIndex == 0 && previousSheetIndex != sheetIndex) {
							try {
								writingDatabase(dataStartTime, lunchStartTime, lunchEndTime, dataEndTime, isReplace, YosonEWrapper.extractScheduledDataRecord(tradeMap, askMap, bidMap));
							} catch (Exception e) {
							}
						}
						previousSheetIndex = sheetIndex;
						if(sheetIndex >= startSheet) {
							if(rowIndex == 0) {
								tradeMap = new TreeMap<Long, List<Double>>();
								askMap = new TreeMap<Long, List<Double>>();
								bidMap = new TreeMap<Long, List<Double>>();
								sheet = "<font size='3' color='blue'>Sheet" + (sheetIndex + 1) + "</font>";
								date = null;
//								source = "";
								try {
									date = DateUtils.yyyyMMdd().parse(datas.get(2));
								} catch (Exception e) {
									try {
										date = DateUtils.yyyyMMdd3().parse(datas.get(2));
									} catch (Exception e2) {
									}
								}
								if (date == null) {
									uploadStatus.add("Can not detect the <font size='3' color='red'>Date cell(C1)</font> at " + sheet + ", this sheet will be <font size='4' color='red'>skipped</font>");
									validateSheet = false;
								} else {
									uploadStatus.add("Parsing data(" + DateUtils.yyyyMMdd().format(date) + ") for " + sheet);
									validateSheet = true;
								}
								SQLUtils.deleteScheduledDataRecordByDate(DateUtils.yyyyMMdd().format(date), uploadTicker, isToDatabase);	
							} else if(validateSheet && rowIndex >= 3) {
								try {
									Date tradeDate = DateUtils.yyyyMMddHHmmss().parse(datas.get(1));
									if(org.apache.commons.lang.time.DateUtils.isSameDay(date, tradeDate)) {
										Double tradePrice = Double.valueOf(datas.get(3).toString());
										YosonEWrapper.addLiveData(tradeMap, tradeDate, tradePrice);																									
									}
								} catch (Exception e) {
									try {
										Date tradeDate = DateUtils.yyyyMMddHHmmss3().parse(datas.get(1));
										if(org.apache.commons.lang.time.DateUtils.isSameDay(date, tradeDate)) {
											Double tradePrice = Double.valueOf(datas.get(3).toString());
											YosonEWrapper.addLiveData(tradeMap, tradeDate, tradePrice);																									
										}
									} catch (Exception e2) {
										
									}
								}
								
								try {
									Date askDate = DateUtils.yyyyMMddHHmmss().parse(datas.get(6));
									if(org.apache.commons.lang.time.DateUtils.isSameDay(date, askDate)) {
										Double askPrice = Double.valueOf(datas.get(8).toString());
										YosonEWrapper.addLiveData(askMap, askDate, askPrice);									
									}
								} catch (Exception e) {
									try {
										Date askDate = DateUtils.yyyyMMddHHmmss3().parse(datas.get(6));
										if(org.apache.commons.lang.time.DateUtils.isSameDay(date, askDate)) {
											Double askPrice = Double.valueOf(datas.get(8).toString());
											YosonEWrapper.addLiveData(askMap, askDate, askPrice);									
										}
									} catch (Exception e2) {
									}
								}
								
								try {
									Date bidDate = DateUtils.yyyyMMddHHmmss().parse(datas.get(11));
									if(org.apache.commons.lang.time.DateUtils.isSameDay(date, bidDate)) {
										Double bidPrice = Double.valueOf(datas.get(13).toString());
										YosonEWrapper.addLiveData(bidMap, bidDate, bidPrice);									
									}
								} catch (Exception e) {
									try {
										Date bidDate = DateUtils.yyyyMMddHHmmss3().parse(datas.get(11));
										if(org.apache.commons.lang.time.DateUtils.isSameDay(date, bidDate)) {
											Double bidPrice = Double.valueOf(datas.get(13).toString());
											YosonEWrapper.addLiveData(bidMap, bidDate, bidPrice);									
										}
									} catch (Exception e2) {
									}	
								}											
							}
						}
					}
				};
			}
			if (uploadDataType.equals("1")) {
				if(previousSheetIndex < startSheet) {
					uploadStatus.add("The excel contains less than " + startSheet + " Sheets, so this file will be <font size='4' color='red'>skipped</font>");
				} if(validateSheet) { //write last sheet
					writingDatabase(dataStartTime, lunchStartTime, lunchEndTime, dataEndTime, isReplace, YosonEWrapper.extractScheduledDataRecord(tradeMap, askMap, bidMap));
				}				
			}
		}
	}
	
	private void writingDatabase(String dataStartTime, String lunchStartTime, String lunchEndTime, String dataEndTime, boolean isReplace, List<ScheduledDataRecord> scheduledDataRecords) {
		try {
			String _dataStartTime = DateUtils.yyyyMMdd().format(date) + " " + dataStartTime; 
			String _lunchStartTime = DateUtils.yyyyMMdd().format(date) + " " + lunchStartTime; 
			String _lunchEndTime = DateUtils.yyyyMMdd().format(date) + " " + lunchEndTime;
			String _dataEndTime = DateUtils.yyyyMMdd().format(date) + " " + dataEndTime;
			if(isToDatabase) {
				uploadStatus.add("Writing database ...");	
				if(isIgnoreLunchTime) {
					SQLUtils.saveScheduledDataRecord(scheduledDataRecords, _dataStartTime, _dataEndTime, uploadSource, uploadTicker, isReplace);
				} else {
					SQLUtils.saveScheduledDataRecord(scheduledDataRecords, _dataStartTime, _lunchStartTime, uploadSource, uploadTicker, isReplace);
					SQLUtils.saveScheduledDataRecord(scheduledDataRecords, _lunchEndTime, _dataEndTime, uploadSource, uploadTicker, isReplace);
				}
			}
			
			if(isToCSV) {
				uploadStatus.add("Writing scheduled data csv ...");
				CollectionUtils.filter(scheduledDataRecords, new Predicate() {
					@Override
					public boolean evaluate(Object o) {
						ScheduledDataRecord s = (ScheduledDataRecord) o;
						try {
							if(isIgnoreLunchTime) {
								return DateUtils.isValidateTime(DateUtils.yyyyMMddHHmmss2().parse(s.getTime()), _dataStartTime, _dataEndTime);
							} else {
								return DateUtils.isValidateTime(DateUtils.yyyyMMddHHmmss2().parse(s.getTime()), _dataStartTime, _lunchStartTime)
										|| DateUtils.isValidateTime(DateUtils.yyyyMMddHHmmss2().parse(s.getTime()), _lunchEndTime, _dataEndTime);
							}
						} catch (ParseException e) {							
						}
						return false;
					}
				});
				String scheduledDataFilePath = FilenameUtils.concat(csvDownloadFolder, uploadSource + "_" + DateUtils.yyyyMMdd().format(date) + "_scheduledData.csv");
				ScheduledDataCSVWriter.WriteCSV(scheduledDataFilePath, uploadSource, scheduledDataRecords);
			}
		} catch (Exception e) {
		}
	}
	
	@ResponseBody
	@RequestMapping(path = "/uploadStatus", method = {RequestMethod.GET})
	public String uploadStatus() {
	  return String.join("<br/>", IndexController.uploadStatus);
	}
	
	@ResponseBody
	@RequestMapping("list")
	public List<String> list(HttpServletRequest request) {
		String dataFolder = InitServlet.createDataFoderAndReturnPath();
		List<String> fileList = new ArrayList<String>();
		
		File[] files = new File(dataFolder).listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        File f = new File(dir, name);
		        if (f.isDirectory())
		            return true;
		        else
		            return false;
		    }
		});
		for(File file : files) {
			String status = "(Not Started)";
			try {
				File stepFile = new File(getStepFilePath(dataFolder, file.getName()));
				String sourceFolder = FilenameUtils.concat(dataFolder, file.getName());
				if(BackTestTask.running && IndexController.mainUIParam.getSourcePath().equals(sourceFolder)) {
					status = "(Running)";
				} else if(stepFile.exists()) {
					FileInputStream input = new FileInputStream(stepFile);
					String step = IOUtils.toString(input);
					input.close();
					if (step.split(",")[0].equals(step.split(",")[1])) {
						status = "(Finished)";
					} else {
						status = "(Paused)";
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			fileList.add(file.getName() + "," + status);			
		}
		return fileList;
	}
	
	@ResponseBody
	@RequestMapping("listLiveData")
	public List<String> listLiveData() {
		String dataFolder = InitServlet.createLiveDataFoderAndReturnPath();
		List<String> fileList = new ArrayList<String>();
		
		File[] files = new File(dataFolder).listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        File f = new File(dir, name);
		        if (f.isDirectory())
		            return true;
		        else
		            return false;
		    }
		});
		for(File file : files) {
			fileList.add(file.getName());			
		}
		return fileList;
	}
	
	@RequestMapping("download")
	public void download(@RequestParam String id, HttpServletResponse response, HttpServletRequest request) throws IOException {
		String dataFolder = InitServlet.createDataFoderAndReturnPath();
		String downloadFolder = FilenameUtils.concat(dataFolder, id);
		execDownload(response, downloadFolder, id);
	}
	
	public static void genCleanLog(String basePath) {
		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			String fileIn = FilenameUtils.concat(basePath, "log.txt");
			String fileOut = FilenameUtils.concat(basePath, "log_only_buy_sell.txt");
			fileInputStream = new FileInputStream(fileIn);
			List<String> readLines = IOUtils.readLines(fileInputStream);
			fileOutputStream = new FileOutputStream(fileOut);
			Map<String, Map<String, List<String>>> map = new HashMap<String, Map<String, List<String>>>();
			String BUY = "BUY";
			String SELL = "SELL";
			for (String line : readLines) {
				if (line.trim().length() == 0 || !line.contains("action:BUY,") && !line.contains("action:SELL,"))
					continue;
				String substring = line.substring(0, line.indexOf(", orderId"));
				String categoryName = substring.substring(substring.lastIndexOf(":") + 1).trim();
				if(!map.containsKey(categoryName)) {				
					Map<String, List<String>> map2 = new HashMap<String, List<String>>();
					map2.put(BUY, new ArrayList<String>());
					map2.put(SELL, new ArrayList<String>());
					map.put(categoryName, map2);
				}
				if (line.contains("action:BUY,")) {				
					map.get(categoryName).get(BUY).add(line + System.lineSeparator());
				}
				if (line.contains("action:SELL,")) {
					map.get(categoryName).get(SELL).add(line + System.lineSeparator());
				}
			}
			
			for (String key : map.keySet()) {
				IOUtils.write("------------------------------------" + key + "------------------------------------" + System.lineSeparator(), fileOutputStream);			
				for (String line : map.get(key).get(BUY)) {			
					IOUtils.write(line, fileOutputStream);
				}
				IOUtils.write(System.lineSeparator(), fileOutputStream);
				for (String line : map.get(key).get(SELL)) {			
					IOUtils.write(line, fileOutputStream);
				}			
				IOUtils.write(System.lineSeparator() + System.lineSeparator(), fileOutputStream);
			}			
			
		} catch (Exception e) {
		} finally {
			try {
				fileInputStream.close();
				fileOutputStream.close();								
			} catch (Exception e) {
			}
		}
	}
	
	@RequestMapping("downloadlive")
	public void downloadlive(@RequestParam String id, HttpServletResponse response, HttpServletRequest request) throws IOException, ParseException {
		String downloadFolder = genLiveResult(id);
		execDownload(response, downloadFolder, id);
	}

	private void execDownload(HttpServletResponse response, String downloadFolder, String downloadFileName) throws IOException {
		File downloadFolderFile = new File(downloadFolder);
		if (downloadFolderFile.exists()) {
			String zipName = downloadFileName + ".zip";
			response.setContentType("APPLICATION/OCTET-STREAM");  
			response.setHeader("Content-Disposition","attachment; filename="+zipName);
			ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
			try {
				File[] files = new File(downloadFolder).listFiles();
				for(File file : files) {
					ZipUtils.doCompress(file, out);
					response.flushBuffer();					
				}				
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				out.close();
			}
		}
	}

	public static String genLiveResult(String id) throws ParseException {
		String dataFolder = InitServlet.createLiveDataFoderAndReturnPath();
		String downloadFolder = FilenameUtils.concat(dataFolder, id);
		String rawDataFilePath = FilenameUtils.concat(downloadFolder, id + "_rawData.csv");
		String scheduledDataFilePath = FilenameUtils.concat(downloadFolder, id + "_scheduledData.csv");
		List<Record> tradeList = new ArrayList<Record>();
		List<Record> askList = new ArrayList<Record>();
		List<Record> bidList = new ArrayList<Record>();
		YosonEWrapper.getRecordList(downloadFolder, tradeList, askList, bidList);
		String instrumentName = id.split("_")[0];
		RawDataCSVWriter.WriteCSV(rawDataFilePath, instrumentName, tradeList, askList, bidList);
		
		List<ScheduledDataRecord> scheduledDataRecords = YosonEWrapper.extractScheduledDataRecord(downloadFolder);
		ScheduledDataCSVWriter.WriteCSV(scheduledDataFilePath, instrumentName, scheduledDataRecords);
		
		YosonEWrapper.genTradingDayPerSecondDetails(downloadFolder, scheduledDataRecords);
		
		genCleanLog(downloadFolder);
		return downloadFolder;
	}
	
	@ResponseBody
	@RequestMapping("delete")
	public boolean delete(@RequestParam String id, HttpServletRequest request) throws IOException {
		String dataFolder = InitServlet.createDataFoderAndReturnPath();
		String sourceFolder = FilenameUtils.concat(dataFolder, id);
		if (!BackTestTask.running
				|| IndexController.mainUIParam == null 
				|| !IndexController.mainUIParam.getSourcePath().equals(sourceFolder)) {
			List<File> files = Arrays.asList(
					new File(sourceFolder), 
					new File(getParamFilePath(dataFolder, id)),
					new File(getStepFilePath(dataFolder, id)), 
					new File(getLogFilePath(dataFolder, id))
					);
			for (File file : files) {
				if (file.exists()) {
					FileUtils.forceDelete(file);
				}			
			}
			return true;
		}	
		return false;
	}
	
	@ResponseBody
	@RequestMapping("deleteLiveItem")
	public boolean deleteLiveItem(@RequestParam String id, HttpServletRequest request) throws IOException {
		String dataFolder = InitServlet.createLiveDataFoderAndReturnPath();
		String sourceFolder = FilenameUtils.concat(dataFolder, id);
		if (!EClientSocketUtils.isConnected()
				|| EClientSocketUtils.id == null 
				|| !EClientSocketUtils.id.equals(id)) {
			File file = new File(sourceFolder);
			if (file.exists()) {
				FileUtils.forceDelete(file);
			}			
			return true;
		}	
		return false;
	}
	
	@ResponseBody
	@RequestMapping("stop")
	public boolean stop(@RequestParam String id, HttpServletRequest request) throws IOException {
		String dataFolder = InitServlet.createDataFoderAndReturnPath();
		String sourceFolder = FilenameUtils.concat(dataFolder, id);
		if (BackTestTask.running
				&& IndexController.mainUIParam != null 
				&& IndexController.mainUIParam.getSourcePath().equals(sourceFolder)) {
			BackTestTask.running = false;
			return true;
		}	
		return false;
	}
	
	@ResponseBody
	@RequestMapping("start")
	public boolean start(@RequestParam String id, HttpServletRequest request) throws IOException {
		String dataFolder = InitServlet.createDataFoderAndReturnPath();
		File paramFile = new File(getParamFilePath(dataFolder, id));
		if (!BackTestTask.running
				&& paramFile.exists()) {
			FileInputStream input = new FileInputStream(paramFile);
			String json = IOUtils.toString(input);
			input.close();
			if (StringUtils.isNotEmpty(json)) {
				IndexController.mainUIParam = new Gson().fromJson(json, MainUIParam.class);				
			}
			
			BackTestTask.running = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					statusStr = new StringBuilder();
					new Thread(new BackTestTask(IndexController.mainUIParam, IndexController.this)).start();
				}
			}).start();
			return true;
		}		
		return false;
	}
	
	public static String getParamFilePath(String dataFolder, String id) {		
		return FilenameUtils.concat(dataFolder, id + "_Param.txt");
	}
	
	public static String getStepFilePath(String dataFolder, String id) {		
		return FilenameUtils.concat(dataFolder, id + "_Step.txt");
	}
	
	public static String getLogFilePath(String dataFolder, String id) {		
		return FilenameUtils.concat(dataFolder, id + "_Log.txt");
	}
	
	@RequestMapping(path = "/", method = {RequestMethod.POST})
	public String index(@RequestBody MainUIParam mainUIParam, HttpServletRequest request) {
	  if (!BackTestTask.running) {
		  String dataFolder = InitServlet.createDataFoderAndReturnPath();
		  String id = DateUtils.yyyyMMddHHmmss2().format(new Date());
		  mainUIParam.setDataRootPath(dataFolder);
		  mainUIParam.setSourcePath(FilenameUtils.concat(dataFolder, id));
		  mainUIParam.setParamPath(getParamFilePath(dataFolder, id));
		  mainUIParam.setStepPath(getStepFilePath(dataFolder, id));
		  mainUIParam.setLogPath(getLogFilePath(dataFolder, id));
		  mainUIParam.setVersion(InitServlet.getVersion());
		  IndexController.mainUIParam = mainUIParam;
		  BackTestTask.running = true;	
			try {
				FileUtils.forceMkdir(new File(mainUIParam.getSourcePath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		  new Thread(new Runnable() {
				@Override
				public void run() {
					statusStr = new StringBuilder();
					new Thread(new BackTestTask(IndexController.mainUIParam, IndexController.this)).start();
				}
			}).start();
	  }
	  return "index";
	}
	
	@ResponseBody
	@RequestMapping(path = "/status", method = {RequestMethod.GET})
	public String status() {
	  return IndexController.statusStr.toString();
	}
	
	@Override
	public void updateStatus(String status) {
		statusStr.append(status + "<br/>");
//		statusStr.insert(0, status + "<br/>");
	}
	
	@Override
	public void invalidatePath(String status) {
		statusStr.append(status + "<br/>");
//		statusStr.insert(0, status + "<br/>");
	}
	
	@Override
	public void done() {
		statusStr.append("Task Completed!" + "<br/>");
		BackTestTask.running = false;		
	}

}
