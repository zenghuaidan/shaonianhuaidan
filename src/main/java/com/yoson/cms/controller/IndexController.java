package com.yoson.cms.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ib.client.Contract;
import com.ib.client.TagValue;
import com.yoson.csv.ExcelUtil;
import com.yoson.date.DateUtils;
import com.yoson.tws.ConnectionInfo;
import com.yoson.tws.EClientSocketUtils;
import com.yoson.tws.RawDataCSVWriter;
import com.yoson.tws.Record;
import com.yoson.tws.ScheduledDataCSVWriter;
import com.yoson.tws.ScheduledDataRecord;
import com.yoson.tws.YosonEWrapper;
import com.yoson.web.InitServlet;
import com.yoson.zip.ZipUtils;

@Controller
public class IndexController {	
	public static String startTime;
	public static String endTime;
	public static boolean isMarketData = true;
	public static boolean isFundamentalData = true;

	@RequestMapping("/")
	public String index(Model model) {		
		model.addAttribute("connectionInfo", EClientSocketUtils.connectionInfo == null ? getDefaultConnectionInfo() : EClientSocketUtils.connectionInfo);		
		model.addAttribute("contracts", EClientSocketUtils.contracts == null ? new ArrayList<Contract>() : EClientSocketUtils.contracts);		
		model.addAttribute("startTime", startTime);
		model.addAttribute("endTime", endTime);
		model.addAttribute("isMarketData", isMarketData);
		model.addAttribute("isFundamentalData", isFundamentalData);
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
	public String search(String startTime, String endTime, String marketData, String fundamentalData, MultipartFile contractTemplate, HttpServletResponse response, HttpServletRequest request) throws IOException {
		IndexController.startTime = startTime;
		IndexController.endTime = endTime;
		IndexController.isMarketData = !StringUtils.isBlank(marketData) && "on".equals(marketData);
		IndexController.isFundamentalData = !StringUtils.isBlank(fundamentalData) && "on".equals(fundamentalData);
		
		try {
			if(!EClientSocketUtils.isConnected()) {				
				return "Connect failed, please check your connection";
			}
			Date _startTime = DateUtils.yyyyMMddHHmm().parse(startTime);
			Date _endTime = DateUtils.yyyyMMddHHmm().parse(endTime);
			if((_startTime.equals(_endTime) || _startTime.before(_endTime)) && startTime.split(" ")[0].equals(endTime.split(" ")[0])) {// within same day and start time must equal or before end time
				String tempFolder = FilenameUtils.concat(InitServlet.createUploadFoderAndReturnPath(), DateUtils.yyyyMMddHHmmss2().format(new Date()));
				File tempFolderFile = new File(tempFolder);
				if(tempFolderFile.exists())
					FileUtils.deleteQuietly(tempFolderFile);
				tempFolderFile.mkdirs();
				
				String file = FilenameUtils.concat(tempFolder, contractTemplate.getOriginalFilename());
				FileUtils.copyInputStreamToFile(contractTemplate.getInputStream(), new File(file));
				ArrayList<ArrayList<ArrayList<Object>>> list = ExcelUtil.readExcel(new File(file));
				
				List<Contract> contracts = new CopyOnWriteArrayList<Contract>();
				if(list.size() > 0) {
					ArrayList<ArrayList<Object>> sheet = list.get(0);
					for(int i = 1; i <= sheet.size() - 1; i++) {
						ArrayList<Object> row = sheet.get(i);
						Contract contract = new Contract();
						int j = 0;
						contract.m_secType = row.size() >= (j + 1) ? row.get(j++).toString().trim() : "";
						contract.m_symbol = row.size() >= (j + 1) ? row.get(j++).toString().trim() : "";
						contract.m_currency = row.size() >= (j + 1) ? row.get(j++).toString().trim() : "";
					    contract.m_exchange = row.size() >= (j + 1) ? row.get(j++).toString().trim() : "";
					    contract.m_localSymbol = row.size() >= (j + 1) ? row.get(j++).toString().trim() : "";
					    contract.m_expiry = row.size() >= (j + 1) ? row.get(j++).toString().trim() : "";
					    try {
					    	// if time in excel is not validate, then use the GUI time
					    	_startTime = (Date)row.get(j++);
					    	_endTime = (Date)row.get(j++);
					    	contract.startTime = DateUtils.yyyyMMddHHmm().format(_startTime);
					    	contract.endTime = DateUtils.yyyyMMddHHmm().format(_endTime);
					    	if(!((_startTime.equals(_endTime) || _startTime.before(_endTime)) && contract.startTime.split(" ")[0].equals(contract.endTime.split(" ")[0]))) {
					    		throw new Exception("Invalidate time");
					    	}
					    } catch (Exception e) {
					    	contract.startTime = startTime;
					    	contract.endTime = endTime;
						}					    
					    contract.tif = "IOC";
					    if (!StringUtils.isBlank(contract.m_secType) 
					    		&& !StringUtils.isBlank(contract.m_symbol) 
					    		&& !StringUtils.isBlank(contract.m_currency) 
					    		&& !StringUtils.isBlank(contract.m_exchange)
					    		&& !StringUtils.isBlank(contract.startTime)
					    		&& !StringUtils.isBlank(contract.endTime)) {
					    	contracts.add(contract);
					    }
					}
				}
				if (contracts.size() > 0) {
					// stop previous market data if have
					if (EClientSocketUtils.contracts != null && EClientSocketUtils.contracts.size() > 0) {
						for(int i = 0; i <= EClientSocketUtils.contracts.size() - 1; i++) {
							EClientSocketUtils.cancelMktData(i);
						}
					}
					
					EClientSocketUtils.contracts = contracts;
					YosonEWrapper.priceMap = new ConcurrentHashMap<String, Double>();
					EClientSocketUtils.id = DateUtils.yyyyMMddHHmmss2().format(new Date());
					EClientSocketUtils.initAndReturnLiveDataFolder();
					
					// start new market data
					for(int i = 0; i <= EClientSocketUtils.contracts.size() - 1; i++) {
						EClientSocketUtils.socket.reqMktData(i, EClientSocketUtils.contracts.get(i), null, false, new Vector<TagValue>());						
					}
					
					EClientSocketUtils.socket.reqCurrentTime();
					return "Success";
				}								
				return "Can not parse any contract from the excel, please check your excel data format";
			} else {
				return "The start time should before end time, and they should be the same day";
			}
		} catch (ParseException e) {
			return "Please input valdate time!";
		}
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
		
		Collection<File> files = FileUtils.listFilesAndDirs(new File(downloadFolder), FalseFileFilter.FALSE, TrueFileFilter.TRUE);
		for(File file : files) {
			String contractDataPath = file.getAbsolutePath();
			if (!file.isDirectory() || !new File(YosonEWrapper.getPath(contractDataPath)).exists()) continue;
			String rawDataFilePath = FilenameUtils.concat(contractDataPath, "rawData.csv");
			String scheduledDataFilePath = FilenameUtils.concat(contractDataPath, "scheduledData.csv");
			List<Record> tradeList = new ArrayList<Record>();
			List<Record> askList = new ArrayList<Record>();
			List<Record> bidList = new ArrayList<Record>();
			YosonEWrapper.getRecordList(contractDataPath, tradeList, askList, bidList);
			String instrumentName = id.split("_")[0];
			RawDataCSVWriter.WriteCSV(rawDataFilePath, instrumentName, tradeList, askList, bidList);
			
			List<ScheduledDataRecord> scheduledDataRecords = YosonEWrapper.extractScheduledDataRecord(contractDataPath);
			ScheduledDataCSVWriter.WriteCSV(scheduledDataFilePath, instrumentName, scheduledDataRecords);			
		}
		
		return downloadFolder;
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

}
