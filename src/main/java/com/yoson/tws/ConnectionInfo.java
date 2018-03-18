package com.yoson.tws;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.yoson.web.InitServlet;

public class ConnectionInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String host;
	private int port;
	private int clientId;
	private String account;
	private String timeZone;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	public static ConnectionInfo getDefaultConnectionInfo() {
		FileInputStream input = null;
		ConnectionInfo connectionInfo = null;
		try {
			input = new FileInputStream(new File(FilenameUtils.concat(InitServlet.createUploadFoderAndReturnPath(), "connect.txt")));
			List<String> readLines = IOUtils.readLines(input);
			ConnectionInfo _connectionInfo = new ConnectionInfo();
			_connectionInfo.setHost(readLines.get(0));
			_connectionInfo.setPort(Integer.parseInt(readLines.get(1)));
			_connectionInfo.setClientId(Integer.parseInt(readLines.get(2)));
			_connectionInfo.setTimeZone(readLines.get(3));
			connectionInfo = _connectionInfo;
		}catch (Exception e) {
		} finally {
			try {
				input.close();
			} catch (Exception e2) {
			}
		}
		if (connectionInfo != null) return connectionInfo;
		connectionInfo = new ConnectionInfo();
		connectionInfo.setHost("127.0.0.1");
		connectionInfo.setPort(7496);
		connectionInfo.setClientId(15);
		connectionInfo.setAccount("U8979091");
		connectionInfo.setTimeZone("Hongkong");
		return connectionInfo;
	}
}
