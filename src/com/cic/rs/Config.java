package com.cic.rs;

public class Config {
	
	// Customize this
	public static final String SERVER_URL_BASE = "http://cobra11111111-001-site1.myasp.net/";
	
	// Don't change
	//public static final String RUN_MODE = "DEBUG";
	public static final String RUN_MODE = "PROD";	
	
	public static final int REFRESH_GRID_RATE = 5; // seconds
	public static final int CHECK_SERVER_RATE = 60; // seconds
	
	public static final int MESSAGE_ALERT = 0;
	public static final int MESSAGE_TOAST = 1;
	public static final int SUBMIT_DONE = 11;
	public static final int SUBMIT_GET_LIST = 22;
	public static final int MESSAGE_ARRIVED_NEW_ORDERS = 21;
	
	public static final String SERVER_URL_UPLOAD = "mobileProcess.aspx";
	public static final String SERVER_URL_DOWNLOAD = "mobileList.aspx";
	public static final String SERVER_URL_REGIST = "mobileLogin.aspx";

}
