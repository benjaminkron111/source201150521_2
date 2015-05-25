package com.cic.rs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Status {
	public String m_status = "";
	DBManager m_db;
	
	public Status(Context context)
	{
		
        m_db = new DBManager(context, "RS.sqlite", "/data/data/com.cic.rs/data/", "data/");
        
        m_db.initialize();		
	}
	
	public void loadFromPR()
	{
		m_status = m_db.loadRecords("run_string");
		
	}

	public void saveToPRF()
	{
		m_db.saveRecords("run_string", m_status);
	}
	
	public void startRunning()
	{
		m_status = "active";
		saveToPRF();
	}
	
	public void stopRunning()
	{
		m_status = "";
		saveToPRF();
	}
	
	public Boolean isRunning()
	{
		loadFromPR();
		if ( m_status.equals("active")){
			return true;
		}
		return false;
	}		
	

}