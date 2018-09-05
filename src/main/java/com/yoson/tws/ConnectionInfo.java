package com.yoson.tws;

import java.io.Serializable;

public class ConnectionInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String host;
	private int port;
	private int clientId;
	private String account;
	private boolean paperTrading = true;
	private boolean cancelAndRetryIfOrderExceedTolerantTime = false;

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

	public boolean isPaperTrading() {
		return paperTrading;
	}

	public void setPaperTrading(boolean paperTrading) {
		this.paperTrading = paperTrading;
	}

	public boolean isCancelAndRetryIfOrderExceedTolerantTime() {
		return cancelAndRetryIfOrderExceedTolerantTime;
	}

	public void setCancelAndRetryIfOrderExceedTolerantTime(boolean cancelAndRetryIfOrderExceedTolerantTime) {
		this.cancelAndRetryIfOrderExceedTolerantTime = cancelAndRetryIfOrderExceedTolerantTime;
	}

}
