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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import com.yoson.callback.StatusCallBack;
import com.yoson.csv.BigExcelReader;
import com.yoson.date.DateUtils;
import com.yoson.model.MainUIParam;
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
		model.addAttribute("sources", SQLUtils.getSources());
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
	public void downloadSampleDate(String sampleDate, HttpServletResponse response) throws IOException{
		response.setContentType("application/msexcel");  
		response.setHeader("Content-Disposition","attachment; filename=" + sampleDate + ".csv");
		IOUtils.write(SQLUtils.getScheduledDataRecordByDate(sampleDate), response.getOutputStream());
	}

	public static List<String> uploadStatus = new ArrayList<String>();
	@ResponseBody
	@RequestMapping("uploadData")
	public boolean uploadData(String toDatabase, String toCSV, String csvPath, String dataStartTime, String lunchStartTime, String lunchEndTime, String dataEndTime, String uploadAction, MultipartFile liveData, HttpServletResponse response, HttpServletRequest request) throws IOException {
		isToCSV = toCSV != null && "on".equals(toCSV.toLowerCase());
		isToDatabase = toDatabase != null && "on".equals(toDatabase.toLowerCase());
		String FINISHED = "Finished";
		boolean success = false;
		csvDownloadFolder=FilenameUtils.concat(System.getProperty("java.io.tmpdir"),DateUtils.yyyyMMddHHmmss2().format(new Date()));
		new File(csvDownloadFolder).mkdirs();
		try{
			if(new File(csvPath).isDirectory()) {
				csvDownloadFolder = toCSV;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(uploadStatus.size() > 0 && uploadStatus.get(uploadStatus.size() - 1).indexOf(FINISHED) < 0) {
			return success;
		}
		if(isToCSV) {
			uploadStatus.add("The csv result will store on the folder:" + csvDownloadFolder);
		}
		boolean isCheck = "check".equals(uploadAction);
		uploadStatus = new ArrayList<String>();
		boolean isReplace = "replace".equals(uploadAction);
		if(isCheck) {
			uploadStatus.add("Start with checking(<font size='3' color='blue'>data will not be uploaded</font>)...");		
		} else if(isReplace) {
			uploadStatus.add("Start upload with <font size='3' color='blue'>Replace</font> mode...");
		} else {
			uploadStatus.add("Start upload with <font size='3' color='blue'>Skip</font> mode...");
		}
		Date startTime = null;
		Date lunchTimeFrom = null;
		Date lunchTimeTo = null;
		Date endTime = null;
		try {
			startTime = DateUtils.HHmmss().parse(dataStartTime);
			lunchTimeFrom = DateUtils.HHmmss().parse(lunchStartTime);
			lunchTimeTo = DateUtils.HHmmss().parse(lunchEndTime);
			endTime = DateUtils.HHmmss().parse(dataEndTime);
			if(startTime.equals(lunchTimeFrom) || startTime.before(lunchTimeFrom) 
					&& lunchTimeFrom.equals(lunchTimeTo) || lunchTimeFrom.before(lunchTimeTo) 
					&& lunchTimeTo.equals(endTime) || lunchTimeTo.before(endTime)) {				
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
						String unzipFolder = FilenameUtils.concat(tempFolder, FilenameUtils.getBaseName(liveData.getOriginalFilename()));
						File unzipFolderFile = new File(unzipFolder);
						unzipFolderFile.mkdirs();				
						ZipUtils.decompress(zipFile, unzipFolder);
						uploadStatus.add("Unzip completed");
						
						// retrieve the excel files
						Collection<File> files = FileUtils.listFiles(unzipFolderFile, new SuffixFileFilter(new ArrayList<String>(){{add("xlsm"); add("xls"); add("xlsx");}}), TrueFileFilter.TRUE);
						
						if(files.size() > 0) {
							if (isCheck) {
								check(dataStartTime, lunchStartTime, lunchEndTime, dataEndTime, files);
								
								// delete the unzip folder, just keep the zip file
								FileUtils.deleteQuietly(tempFolderFile);
							} else {
								// write audit log
								FileOutputStream logFileOutputStream = new FileOutputStream(new File(FilenameUtils.concat(tempFolder, "log.txt")));
								IOUtils.write(dataStartTime + "	" + lunchStartTime + "	" + lunchEndTime + "	"  + dataEndTime + "	" + uploadAction, logFileOutputStream);
								logFileOutputStream.close();
								
								uploadWithAction(dataStartTime, lunchStartTime, lunchEndTime, dataEndTime, files, isReplace);				
								
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
				}
			} else {
				uploadStatus.add("Please check your input time");
			}
		} catch (ParseException e) {
			uploadStatus.add("Please input valdate time!");
		}
		uploadStatus.add((isCheck ? "Check " : "Upload ") + FINISHED);
		return success;
	}

	private void check(String dataStartTime, String lunchStartTime, String lunchEndTime, String dataEndTime, Collection<File> files) throws ParseException, IOException, OpenXML4JException, SAXException {
		for(File file : files) {
			String name = "<font size='3' color='blue'>" + FilenameUtils.getName(file.getName()) + "</font>";
			uploadStatus.add("Doing checking for " + name + " ...");
	        new BigExcelReader(file) {  
	        	@Override  
	        	protected void outputRow(int sheetIndex, int rowIndex, boolean isLastSheet, List<Object> datas, List<Integer> rowTypes) {  
	        			if(sheetIndex >= startSheet && rowIndex == 0) {
	        				boolean validateSource = false;
	        				boolean validateDate = false;
	        				if(datas.size() > 0 && !StringUtils.isEmpty(datas.get(0).toString())) {
	        					validateSource = true;
	        				}
	        				if(datas.size() > 1 && datas.get(1) instanceof Date) {
	        					validateDate = true;
	        				}
	        				String sheet = "<font size='3' color='red'>Sheet" + (sheetIndex + 1) + "</font>";
	        				if (validateDate && validateSource) {
	        					String source = genSouce((String)datas.get(0));						
	        					Date date = (Date)datas.get(1);
	        					String dateStr = DateUtils.yyyyMMdd().format(date);
//						if(!org.apache.commons.lang.time.DateUtils.isSameDay(date, DateUtils.yyyyMMddHHmm().parse(dataStartTime))) {
//							uploadStatus.add("The data(" + DateUtils.yyyyMMdd().format(date) + ") at " + sheet + " is NOT within you selected period(" + dataStartTime +"-" + dataEndTime + "), this sheet may be <font size='4' color='red'>skipped</font>");
//						} else {
	        					String _dataStartTime = dateStr + " " + dataStartTime; 
	        					String _lunchStartTime = dateStr + " " + lunchStartTime; 
	        					String _lunchEndTime = dateStr + " " + lunchEndTime;
	        					String _dataEndTime = dateStr + " " + dataEndTime;
	        					int totalCount1 = SQLUtils.checkScheduledDataExisting(_dataStartTime, _lunchStartTime, source);	
	        					int totalCount2 = SQLUtils.checkScheduledDataExisting(_lunchEndTime, _dataEndTime, source);
	        					if (totalCount1 == 0 && totalCount2 == 0 ) {
	        						// no data in db
	        						uploadStatus.add("Not exists data within period(" + _dataStartTime +" to " + _dataEndTime + ") in database. The data(" + dateStr + ") at " + sheet + " will be <font size='4' color='blue'>uploaded</font>");
	        					} else {
	        						// exists data in db
	        						uploadStatus.add("Exists data within period(" + _dataStartTime +"-" + _dataEndTime + ") in database. The database data may be <font size='4' color='red'>replaced</font> with the data(" + dateStr + ") at " + sheet);
	        					}															
//						}
	        				} else {
	        					uploadStatus.add("Can not detect the" + (validateSource ? "" : " <font size='3' color='red'>Source cell(B1)</font> ") + (validateDate ? "" : " <font size='3' color='red'>Date cell(C1)</font> ") + " at " + sheet + ", this sheet will be <font size='4' color='red'>skipped</font>");
	        				}
	        			}
	        	}  
	        };
		}
	}

	private String genSouce(String source) {
		if (StringUtils.isEmpty(source))
			return source;
		String[] sources = source.split(" ");
		return sources[0];
	}

	private static int startSheet = 2;
	private static Map<Long, List<Double>> tradeMap = null;
	private static Map<Long, List<Double>> askMap = null;
	private static Map<Long, List<Double>> bidMap = null;
	private static boolean validateSheet = false;
	private static Date date = null;
	private static String source = "";
	private static String sheet="";
	private int previousSheetIndex = 0;
	private String csvDownloadFolder;
	private boolean isToCSV;
	private boolean isToDatabase;
	private void uploadWithAction(String dataStartTime, String lunchStartTime, String lunchEndTime, String dataEndTime, Collection<File> files, boolean isReplace) throws IOException, OpenXML4JException, SAXException {
		for(File file : files) {
			String name = "<font size='3' color='blue'>" + FilenameUtils.getName(file.getName()) + "</font>";
			uploadStatus.add("Retriving data from " + name + " ...");
			previousSheetIndex = 0;
			tradeMap = null;
			askMap = null;
			bidMap = null;
			validateSheet = false;
			date = null;
			source = "";
			sheet="";
			previousSheetIndex = 0;
	        new BigExcelReader(file) {  
	        	@Override  
	        	protected void outputRow(int sheetIndex, int rowIndex, boolean isLastSheet, List<Object> datas, List<Integer> rowTypes) {
	        		if(validateSheet && rowIndex == 0 && previousSheetIndex != sheetIndex) {
	        			wrtingDatabase(dataStartTime, lunchStartTime, lunchEndTime, dataEndTime, isReplace);
	        		}
	        		previousSheetIndex = sheetIndex;
	        		if(sheetIndex >= startSheet) {
    					if(rowIndex == 0) {
    						tradeMap = new TreeMap<Long, List<Double>>();
    						askMap = new TreeMap<Long, List<Double>>();
    						bidMap = new TreeMap<Long, List<Double>>();
    						sheet = "<font size='3' color='blue'>Sheet" + (sheetIndex + 1) + "</font>";
    						date = null;
    						source = "";
    						try {
    							source = genSouce((String)datas.get(0));						
    							date = (Date)datas.get(1);
    						} catch (Exception e) {
    						}
    						if (StringUtils.isEmpty(source) || date == null) {
    							uploadStatus.add("Can not detect the" + (StringUtils.isEmpty(source) ? "" : " <font size='3' color='red'>Source cell(B1)</font> ") + (date == null ? "" : " <font size='3' color='red'>Date cell(C1)</font> ") + " at " + sheet + ", this sheet will be <font size='4' color='red'>skipped</font>");
    							validateSheet = false;
    						} else {
    							uploadStatus.add("Parsing data(" + DateUtils.yyyyMMdd().format(date) + ") for " + sheet + ", the source is " + source + " ...");
    							validateSheet = true;
    						}
    						
    					} else if(validateSheet && rowIndex >= 3) {
    						try {
    							Date tradeDate = HSSFDateUtil.getJavaDate(Double.parseDouble(datas.get(0).toString()));
    							if(org.apache.commons.lang.time.DateUtils.isSameDay(date, tradeDate)) {
    								Double tradePrice = Double.valueOf(datas.get(2).toString());
    								YosonEWrapper.addLiveData(tradeMap, tradeDate, tradePrice);																									
    							}
    						} catch (Exception e) {
    						}
    						
    						try {
    							Date askDate = HSSFDateUtil.getJavaDate(Double.parseDouble(datas.get(4).toString()));
    							if(org.apache.commons.lang.time.DateUtils.isSameDay(date, askDate)) {
    								Double askPrice = Double.valueOf(datas.get(5).toString());
    								YosonEWrapper.addLiveData(askMap, askDate, askPrice);									
    							}
    						} catch (Exception e) {
    						}
    						
    						try {
    							Date bidDate = HSSFDateUtil.getJavaDate(Double.parseDouble(datas.get(8).toString()));
    							if(org.apache.commons.lang.time.DateUtils.isSameDay(date, bidDate)) {
    								Double bidPrice = Double.valueOf(datas.get(10).toString());
    								YosonEWrapper.addLiveData(bidMap, bidDate, bidPrice);									
    							}
    						} catch (Exception e) {
    						}											
        				}
	        		}
	        	}
        	};
			if(previousSheetIndex < startSheet) {
				uploadStatus.add("The excel contains less than " + startSheet + " Sheets, so this file will be <font size='4' color='red'>skipped</font>");
			} if(validateSheet) { //write last sheet
				wrtingDatabase(dataStartTime, lunchStartTime, lunchEndTime, dataEndTime, isReplace);
			}
		}
	}
	
	private void wrtingDatabase(String dataStartTime, String lunchStartTime, String lunchEndTime, String dataEndTime, boolean isReplace) {
		try {
			String _dataStartTime = DateUtils.yyyyMMdd().format(date) + " " + dataStartTime; 
			String _lunchStartTime = DateUtils.yyyyMMdd().format(date) + " " + lunchStartTime; 
			String _lunchEndTime = DateUtils.yyyyMMdd().format(date) + " " + lunchEndTime;
			String _dataEndTime = DateUtils.yyyyMMdd().format(date) + " " + dataEndTime;
			List<ScheduledDataRecord> scheduledDataRecords = YosonEWrapper.extractScheduledDataRecord(tradeMap, askMap, bidMap);
			if(isToDatabase) {
				uploadStatus.add("Writing database for " + sheet + ", the source is " + source + " ...");							
				SQLUtils.saveScheduledDataRecord(scheduledDataRecords, _dataStartTime, _lunchStartTime, source, isReplace);
				SQLUtils.saveScheduledDataRecord(scheduledDataRecords, _lunchEndTime, _dataEndTime, source, isReplace);
			}
			
			if(isToCSV) {
				String scheduledDataFilePath = FilenameUtils.concat(csvDownloadFolder, source + "_" + DateUtils.yyyyMMdd().format(date) + "_scheduledData.csv");
				ScheduledDataCSVWriter.WriteCSV(scheduledDataFilePath, source, scheduledDataRecords);
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
