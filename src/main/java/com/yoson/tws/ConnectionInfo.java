package com.yoson.tws;

import java.io.Serializable;

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
		ConnectionInfo connectionInfo = new ConnectionInfo();
		connectionInfo.setHost("127.0.0.1");
		connectionInfo.setPort(7496);
		connectionInfo.setClientId(15);
		connectionInfo.setAccount("U8979091");
		connectionInfo.setTimeZone("Hongkong");
		return connectionInfo;
	}
}
