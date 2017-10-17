package com.yoson.cms.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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

import com.google.gson.Gson;
import com.yoson.callback.StatusCallBack;
import com.yoson.date.BrokenDate;
import com.yoson.date.DateUtils;
import com.yoson.model.MainUIParam;
import com.yoson.task.BackTestTask;
import com.yoson.zip.ZipUtils;

@Controller
public class IndexController  implements StatusCallBack {
	public static MainUIParam mainUIParam;
	public static StringBuilder statusStr = new StringBuilder();
	public static List<String> sources = Arrays.asList("BBG", "BBGh", "BBGhp", "BBGhph", "BBG_HC1", "BBG_HSI", "BBG_KM1", "BBG_KM1L", "BBG_NK1", "BBG_NK1L", "TWS", "TWS_HSI");

	@RequestMapping("/")
	public String index(Model model) {
		model.addAttribute("mainUIParam", BackTestTask.running || IndexController.mainUIParam != null ? IndexController.mainUIParam : getMainUIParam());
		model.addAttribute("sources", sources);
		return "index";
	}
	
	@ResponseBody
	@RequestMapping("test")
	public MainUIParam test() {
	  return getMainUIParam();
	}
	
	@ResponseBody
	@RequestMapping("list")
	public List<String> list(HttpServletRequest request) {
		String dataFolder = createDataFoderAndReturnPath(request);
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
			String status = "";
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
				} else {
					status = "(Not Started)";
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			fileList.add(file.getName() + "," + status);			
		}
		return fileList;
	}
	
	@RequestMapping("download")
	public void download(@RequestParam String id, HttpServletResponse response, HttpServletRequest request) throws IOException {
		String dataFolder = createDataFoderAndReturnPath(request);
		String downloadFolder = FilenameUtils.concat(dataFolder, id);
		File downloadFolderFile = new File(downloadFolder);
		if (downloadFolderFile.exists()) {
			String zipName = id + ".zip";
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
	
	@ResponseBody
	@RequestMapping("delete")
	public boolean delete(@RequestParam String id, HttpServletRequest request) throws IOException {
		String dataFolder = createDataFoderAndReturnPath(request);
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
	@RequestMapping("stop")
	public boolean stop(@RequestParam String id, HttpServletRequest request) throws IOException {
		String dataFolder = createDataFoderAndReturnPath(request);
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
		String dataFolder = createDataFoderAndReturnPath(request);
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
		  String dataFolder = createDataFoderAndReturnPath(request);
		  String id = DateUtils.yyyyMMddHHmmss2.format(new Date());
		  mainUIParam.setDataRootPath(dataFolder);
		  mainUIParam.setSourcePath(FilenameUtils.concat(dataFolder, id));
		  mainUIParam.setParamPath(getParamFilePath(dataFolder, id));
		  mainUIParam.setStepPath(getStepFilePath(dataFolder, id));
		  mainUIParam.setLogPath(getLogFilePath(dataFolder, id));
		  mainUIParam.setVersion("V9");
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

	private String createDataFoderAndReturnPath(HttpServletRequest request) {
		String root = request.getSession().getServletContext().getRealPath("/");
		root = root.substring(0, root.substring(0, root.length() - 1).lastIndexOf("\\"));
		String dataFolder = FilenameUtils.concat(root, "v9_data");
		File dataFolderFile = new File(dataFolder);
		if (!dataFolderFile.exists()) {
			try {
				FileUtils.forceMkdir(dataFolderFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return dataFolder;
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
	
	public MainUIParam getMainUIParam() {
		MainUIParam mainUIParam = new MainUIParam();
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
		brokenDateList.add(new BrokenDate("2014-01-01", DateUtils.yyyyMMdd.format(new Date())));
		mainUIParam.setBrokenDateList(brokenDateList);
		
		return mainUIParam;
	}

}
