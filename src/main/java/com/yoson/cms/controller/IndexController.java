package com.yoson.cms.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ib.client.Contract;
import com.yoson.callback.StatusCallBack;
import com.yoson.date.DateUtils;
import com.yoson.model.MainUIParam;
import com.yoson.sql.SQLUtils;
import com.yoson.task.BackTestTask;
import com.yoson.tws.ConnectionInfo;
import com.yoson.tws.EClientSocketUtils;
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
		if(InitServlet.noQuery()) {
			model.addAttribute("sources", new ArrayList<String>());
			model.addAttribute("tickers", new ArrayList<String>());			
		} else {
			model.addAttribute("sources", SQLUtils.getSources());
			model.addAttribute("tickers", SQLUtils.getTickers());			
		}
		model.addAttribute("connectionInfo", EClientSocketUtils.connectionInfo == null ? getDefaultConnectionInfo() : EClientSocketUtils.connectionInfo);
		model.addAttribute("contract", EClientSocketUtils.contract == null ? getDefaultContract() : EClientSocketUtils.contract);
		model.addAttribute("strategies", EClientSocketUtils.strategies);
		model.addAttribute("title", InitServlet.getVersion());
		model.addAttribute("comment", InitServlet.getComment());
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
	    //contract.m_expiry = DateUtils.yyyyMM().format(new Date());
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
	@RequestMapping("reqContractDetails")
	public boolean reqContractDetails(@RequestBody Contract contract) {
		EClientSocketUtils.reqContractDetails(contract);
		return true;
	}
	
	@ResponseBody
	@RequestMapping("isValidContract")
	public boolean isValidContract() {
		return EClientSocketUtils.validateContract;
	}
	
	@ResponseBody
	@RequestMapping("search")
	public String search(@RequestBody Contract contract) {
		try {
			Date startTime = DateUtils.yyyyMMddHHmm().parse(contract.getStartTime());
			Date endTime = DateUtils.yyyyMMddHHmm().parse(contract.getEndTime());
			if((startTime.equals(endTime) || startTime.before(endTime)) && contract.getStartTime().split(" ")[0].equals(contract.getEndTime().split(" ")[0])) {				
				boolean success =  EClientSocketUtils.reqMktData(contract);
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
		} catch (Exception e) {
			return "Server error!";
		}
	}
	
	@ResponseBody
	@RequestMapping(path = "hasIncomingMarketData", method = {RequestMethod.GET})
	public boolean hasIncomingMarketData() {
		return EClientSocketUtils.hasIncomingMarketData;
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
	
	@ResponseBody
	@RequestMapping(path = "getTradeLog", method = {RequestMethod.GET})
	public String getTradeLog() {
		String summary = "Total SELL: " + EClientSocketUtils.totalSell + ", Total BUY: " + EClientSocketUtils.totalBuy + "<br/>";
		String result = String.join("<br/>", EClientSocketUtils.tradeLogs == null ? new ArrayList<>() : EClientSocketUtils.tradeLogs);
		return summary + result;
	}
	
	@RequestMapping("saveAllStrategy")
	public void saveAllStrategy(HttpServletResponse response, HttpServletRequest request) throws IOException {
		Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
		
		String json = gson.toJson(EClientSocketUtils.strategies);			
		JsonElement jsonTree = gson.toJsonTree(EClientSocketUtils.strategies);
		List<String> headers = new ArrayList<String>();
		StringBuilder items = new StringBuilder();
		for(JsonElement strategiesJson : jsonTree.getAsJsonArray()) {
			JsonObject asJsonObject = strategiesJson.getAsJsonObject();			
			for(String key : asJsonObject.keySet()) {
				boolean isMainUIParam = "mainUIParam".equals(key);
				if(!headers.contains(key) && !isMainUIParam) {
					headers.add(key);
				}
				JsonElement itemJson = asJsonObject.get(key);
				if(itemJson.isJsonObject()) {
					JsonObject mainUIParamJson = itemJson.getAsJsonObject();
					for(String key2 : mainUIParamJson.keySet()) {
						if(!headers.contains(key2)) {
							headers.add(key2);
						}
						items.append(mainUIParamJson.get(key2).getAsString() + ",");
					}					
				} else if(!isMainUIParam) {
					items.append(itemJson.getAsString() + ",");
				}
			}
			items.append(System.lineSeparator());
		}
		String result = String.join(",", headers) + System.lineSeparator() + items;
		response.setContentType("APPLICATION/OCTET-STREAM");  
		response.setHeader("Content-Disposition","attachment; filename=saveTemplate"+DateUtils.yyyyMMddHHmmss2().format(new Date()) + ".csv");
		IOUtils.write(result, response.getOutputStream());
		response.flushBuffer();	
	}
	
	@ResponseBody
	@RequestMapping("loadTemplate")
	public boolean loadTemplate(MultipartFile template, HttpServletResponse response, HttpServletRequest request) throws IOException {
		try {
			String ext = template == null ? "" : template.getOriginalFilename().substring(template.getOriginalFilename().lastIndexOf('.')).toLowerCase();
			if(ext.equals(".csv")) {
				
				List<String> readLines = IOUtils.readLines(template.getInputStream());
				String [] headers = null;
				boolean first = true;
				JsonArray jsonArray = new JsonArray();
				List<String> strategyNames = new ArrayList<String>();
				for(String line : readLines) {
					if(first) {
						first = false;
						headers = line.split(",");
						continue;
					}
					String[] values = line.split(",");
					String starategyName = values[0];
					if(strategyNames.contains(starategyName)) {
						continue;
					}
					strategyNames.add(starategyName);
					JsonObject jsonObject1 = new JsonObject();
					jsonObject1.addProperty(headers[0], starategyName);
					
					JsonObject jsonObject2 = new JsonObject();
					for(int i = 1; i < values.length; i++) {
						jsonObject2.addProperty(headers[i], values[i]);
					}
					jsonObject1.add("mainUIParam", jsonObject2);
					jsonArray.add(jsonObject1);
				}
				
				Type type = new TypeToken<ArrayList<Strategy>>() {}.getType();  
				EClientSocketUtils.strategies = new Gson().fromJson(jsonArray, type);	
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
	
	public static void genCleanLogByDate(String basePath) {
		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			String fileIn = FilenameUtils.concat(basePath, "log.txt");
			String fileOut = FilenameUtils.concat(basePath, "log_by_date.txt");
			fileInputStream = new FileInputStream(fileIn);
			List<String> readLines = IOUtils.readLines(fileInputStream);
			fileOutputStream = new FileOutputStream(fileOut);
			for (String line : readLines) {
				if (line.trim().length() == 0 || !line.contains("action:BUY,") && !line.contains("action:SELL,"))
					continue;
				IOUtils.write(line + System.lineSeparator(), fileOutputStream);
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
	
	@RequestMapping("runBTWithLiveData")
	@ResponseBody
	public boolean runBTWithLiveData(@RequestParam String id, HttpServletResponse response, HttpServletRequest request) throws IOException, ParseException {
		if (!BackTestTask.running) {
			runBTWithLiveData(id);
			return true;
		}
		return false;
	}
	
	@RequestMapping("stopBTWithLiveData")
	@ResponseBody
	public boolean stopBTWithLiveData(@RequestParam String id, HttpServletResponse response, HttpServletRequest request) throws IOException, ParseException {
		boolean runningBT = BackTestTask.running && BackTestTask.isLiveData && mainUIParam != null && !StringUtils.isBlank(mainUIParam.getSourcePath()) && mainUIParam.getSourcePath().contains(id);
		if (runningBT) {
			BackTestTask.running = false;
			return true;
		}
		return false;
	}		
	
	public static String runBTWithLiveData(String id) {
		String dataFolder = InitServlet.createLiveDataFoderAndReturnPath();
		String downloadFolder = FilenameUtils.concat(dataFolder, id);
		new IndexController().runTest(downloadFolder, true);
		return downloadFolder;
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
//		String rawDataFilePath = FilenameUtils.concat(downloadFolder, id + "_rawData.csv");
//		String scheduledDataFilePath = FilenameUtils.concat(downloadFolder, id + "_scheduledData.csv");
//		List<Record> tradeList = new ArrayList<Record>();
//		List<Record> askList = new ArrayList<Record>();
//		List<Record> bidList = new ArrayList<Record>();
//		YosonEWrapper.getRecordList(downloadFolder, tradeList, askList, bidList);
//		String instrumentName = id.split("_")[0];
//		RawDataCSVWriter.WriteCSV(rawDataFilePath, instrumentName, tradeList, askList, bidList);
		
		List<ScheduledDataRecord> scheduledDataRecords = YosonEWrapper.extractScheduledDataRecord(downloadFolder);
//		ScheduledDataCSVWriter.WriteCSV(scheduledDataFilePath, instrumentName, scheduledDataRecords);
		
		YosonEWrapper.genTradingDayPerSecondDetails(downloadFolder, scheduledDataRecords);
		
		genCleanLog(downloadFolder);
		genCleanLogByDate(downloadFolder);
	
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
		boolean runningLiveTrading = EClientSocketUtils.isConnected() && !StringUtils.isBlank(EClientSocketUtils.id) && EClientSocketUtils.id.equals(id);
		boolean runningBT = BackTestTask.running && BackTestTask.isLiveData && mainUIParam != null && !StringUtils.isBlank(mainUIParam.getSourcePath()) && mainUIParam.getSourcePath().contains(id); 
		if (!runningLiveTrading && !runningBT) {
			File file = new File(sourceFolder);
			if (file.exists()) {
				FileUtils.forceDelete(file);
			}			
			return true;
		}	
		return false;
	}
	
	public static List<String> getExpiryDates() {
		String expiryFoderAndReturnPath = InitServlet.createExpiryFoderAndReturnPath();
		String result = FilenameUtils.concat(expiryFoderAndReturnPath, "result.txt");
		File resultFile = new File(result);
		List<String> list = new ArrayList<String>();
		if(resultFile.exists()) {
			try {
				FileInputStream input = new FileInputStream(resultFile);
				list = IOUtils.readLines(input);
				input.close();				
			} catch (Exception e) {
			}
		}
		return list;
	}
	
	public static void setExpiryDates(List<String> list) {
		String expiryFoderAndReturnPath = InitServlet.createExpiryFoderAndReturnPath();
		String result = FilenameUtils.concat(expiryFoderAndReturnPath, "result.txt");
		File resultFile = new File(result);
		try {
			FileOutputStream output = new FileOutputStream(resultFile);
			IOUtils.writeLines(list, null, output);
			output.close();			
		} catch (Exception e) {
		}
		
	}
	
	@ResponseBody
	@RequestMapping("getExpiryDates")
	public List<String> expiryDates() throws IOException {
		return getExpiryDates();		
	}
	
	@ResponseBody
	@RequestMapping("addExpiryDate")
	public boolean addExpiryDate(@RequestParam String id, HttpServletRequest request) throws IOException {
		List<String> expiryDates = getExpiryDates();
		if(!expiryDates.contains(id) && !StringUtils.isBlank(id)) {
			expiryDates.add(id);
			setExpiryDates(expiryDates);
		}
		return true;
	}
	
	@ResponseBody
	@RequestMapping("deleteExpiryDate")
	public boolean deleteExpiryDate(@RequestParam String id, HttpServletRequest request) throws IOException {
		List<String> expiryDates = getExpiryDates();
		expiryDates.remove(id);
		setExpiryDates(expiryDates);		
		return true;
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
			BackTestTask.isLiveData = false;
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
    	  IndexController.mainUIParam = mainUIParam;
    	  String dataFolder = InitServlet.createDataFoderAndReturnPath();
    	  runTest(dataFolder, false);
	  }
	  return "index";
	}
	
	@RequestMapping(path = "/runWithLiveTradingDataClick", method = {RequestMethod.POST})
	public String runWithLiveTradingDataClick(@RequestBody MainUIParam mainUIParam, HttpServletRequest request) {
      if (!BackTestTask.running) {
    	  IndexController.mainUIParam = mainUIParam;
	  }
	  return "index";
	}

	public void runTest(String dataFolder, boolean isLiveData) {
		if (!BackTestTask.running) {
		  String id = isLiveData ? "BT_Result" : DateUtils.yyyyMMddHHmmss2().format(new Date());
		  if(mainUIParam == null) mainUIParam = MainUIParam.getMainUIParam();
		  mainUIParam.setDataRootPath(dataFolder);
		  mainUIParam.setSourcePath(FilenameUtils.concat(dataFolder, id));
		  mainUIParam.setParamPath(getParamFilePath(dataFolder, id));
		  mainUIParam.setStepPath(getStepFilePath(dataFolder, id));
		  mainUIParam.setLogPath(getLogFilePath(dataFolder, id));
		  mainUIParam.setVersion(InitServlet.getVersion());
		  BackTestTask.running = true;
		  BackTestTask.isLiveData = isLiveData;
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
	}
	
	@ResponseBody
	@RequestMapping(path = "getStartDateBySource", method = {RequestMethod.GET})
	public String getStartDateBySource(@RequestParam String source) {
		return SQLUtils.getStartDateBySource(source);
	}
	
	@ResponseBody
	@RequestMapping(path = "getStartDateByTicker", method = {RequestMethod.GET})
	public String getStartDateByTicker(@RequestParam String ticker) {
		return SQLUtils.getStartDateByTicker(ticker);
	}
	
	@ResponseBody
	@RequestMapping(path = "/status", method = {RequestMethod.GET})
	public String status() {
	  return IndexController.statusStr.toString();
	}
	
	@Override
	public void updateStatus(String status) {
		if (statusStr.length() >= 10000) {
			statusStr = new StringBuilder();
		}
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
