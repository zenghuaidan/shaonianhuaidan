package com.yoson.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.yoson.cms.controller.Global;

public class InitServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static WebApplicationContext wc;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		//初始化spring的工厂
		wc = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
	}
	
	public static WebApplicationContext getWc() {
		return wc;
	}
	
	public static String getVersion() {
		String temp = wc.getServletContext().getContextPath();
		return temp.substring(1, temp.length()).toUpperCase();
	}

	public static String getWebappsPath() {
		String temp = wc.getServletContext().getRealPath("/");
		return temp.substring(0, temp.substring(0, temp.length() - 1).lastIndexOf("\\"));
	}
	
	public static String createUploadFoderAndReturnPath() {
		return createFoderAndReturnPath(getWebappsPath(), "upload");
	}
	
	public static String createDataFoderAndReturnPath() {
		return createFoderAndReturnPath(getWebappsPath(), getVersion() + "_data");
	}
	
	public static String createLiveDataFoderAndReturnPath() {
		return createFoderAndReturnPath(getWebappsPath(), getVersion() + "_livedata");
	}

	public static String createFoderAndReturnPath(String path, String folderName) {
		String dataFolder = FilenameUtils.concat(path, folderName);
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
}
