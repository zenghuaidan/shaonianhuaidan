package com.yoson.cms.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ib.client.Contract;
import com.yoson.csv.ExcelUtil;
import com.yoson.date.DateUtils;
import com.yoson.tws.ConnectionInfo;
import com.yoson.tws.EClientSocketUtils;
import com.yoson.web.InitServlet;

@Controller
public class IndexController {
	public static String startDate;
	public static String endDate;
	public static String startTime;
	public static String endTime;
	public static String status = "";

	@RequestMapping("/")
	public String index(Model model) {		
		model.addAttribute("connectionInfo", EClientSocketUtils.connectionInfo == null ? ConnectionInfo.getDefaultConnectionInfo() : EClientSocketUtils.connectionInfo);		
		model.addAttribute("contracts", EClientSocketUtils.contracts == null ? new ArrayList<Contract>() : EClientSocketUtils.contracts);		
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("startTime", startTime);
		model.addAttribute("endTime", endTime);		
		model.addAttribute("timeZones", TimeZone.getAvailableIDs());				
		return "index";
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
	@RequestMapping("getNowTime")
	public String getNowTime() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sf.setTimeZone(TimeZone.getTimeZone(EClientSocketUtils.connectionInfo == null ? ConnectionInfo.getDefaultConnectionInfo().getTimeZone() : EClientSocketUtils.connectionInfo.getTimeZone()));
		return sf.format(new Date());
	}
	
	@ResponseBody
	@RequestMapping("getStatus")
	public String getStatus() {		
		return status;
	}
	
	@ResponseBody
	@RequestMapping("search")
	public String search(String startDate, String endDate, String startTime, String endTime, String marketData, String fundamentalData, MultipartFile contractTemplate, HttpServletResponse response, HttpServletRequest request) throws IOException {
		IndexController.startTime = startTime;
		IndexController.endTime = endTime;
		IndexController.startDate = startDate;
		IndexController.endDate = endDate;
		
		if(!EClientSocketUtils.isConnected()){
			return "Connect failed, please check your connection first";
		}
		if(EClientSocketUtils.uploading){
			return "Data is uploading to database, please re-try after data have been uploaded";
		}
		try {
			status = "";
			Date _startTime = DateUtils.HHmmss().parse(startTime);
			Date _endTime = DateUtils.HHmmss().parse(endTime);
			Date _startDate = DateUtils.yyyyMMdd().parse(startDate);
			Date _endDate = DateUtils.yyyyMMdd().parse(endDate);
			if((_startTime.equals(_endTime) || _startTime.before(_endTime)) && (_startDate.equals(_endDate) || _startDate.before(_endDate))) {
				String folderName = DateUtils.yyyyMMddHHmmss2().format(new Date());
				String tempFolder = FilenameUtils.concat(InitServlet.createUploadFoderAndReturnPath(), folderName);
				File tempFolderFile = new File(tempFolder);
				if(tempFolderFile.exists())
					FileUtils.deleteQuietly(tempFolderFile);
				tempFolderFile.mkdirs();
				
				File file = new File(FilenameUtils.concat(tempFolder, contractTemplate.getOriginalFilename()));
				FileUtils.copyInputStreamToFile(contractTemplate.getInputStream(), file);				
				List<Contract> contracts = initContracts(file, startDate, endDate, startTime, endTime);
				if (contracts.size() > 0) {
					EClientSocketUtils.requestData(contracts);
					return "Success";
				}
				return "Can not parse any contract from the excel, please check your excel data format";
			} else {
				return "The start date should before end date, and the start time should before end time";
			}
		} catch (ParseException e) {
			return "Please input valdate time!";
		}
	}

	public static List<Contract> initContracts(File file, String startDate, String endDate, String startTime, String endTime) {
		List<Contract> contracts = new CopyOnWriteArrayList<Contract>();		
		
		if(StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate) || StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)) return contracts;
		
		Date _startDate;
		Date _endDate;
		Date _startTime;
		Date _endTime;
		ArrayList<ArrayList<ArrayList<Object>>> list = ExcelUtil.readExcel(file);
		
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
			    contract.tif = "IOC";
			    try {
			    	// if date in excel is not validate, then use the GUI date
			    	_startDate = (Date)row.get(j++);
			    	_endDate = (Date)row.get(j++);
			    	contract.startDate = DateUtils.yyyyMMdd().format(_startDate);
			    	contract.endDate = DateUtils.yyyyMMdd().format(_endDate);
			    	if(!(_startDate.equals(_endDate) || _startDate.before(_endDate))) {
			    		throw new Exception("Invalidate date");
			    	}
			    } catch (Exception e) {
			    	contract.startDate = startDate;
			    	contract.endDate = endDate;
				}
			    try {
			    	// if time in excel is not validate, then use the GUI time
			    	_startTime = (Date)row.get(j++);
			    	_endTime = (Date)row.get(j++);
			    	contract.startTime = DateUtils.HHmmss().format(_startTime);
			    	contract.endTime = DateUtils.HHmmss().format(_endTime);
			    	if(!(_startTime.equals(_endTime) || _startTime.before(_endTime))) {
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
		return contracts;
	}

}
