package com.yoson.callback;

public interface StatusCallBack {
	public void updateStatus(String status);
	
	public void invalidatePath(String status);
	
	public void done();
}
