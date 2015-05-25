package com.cic.rs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class User {
	public SharedPreferences m_sharedPreference;
	public String m_userId = "";
	public String m_userPasswd = "";
	public String delim = ",";
	DBManager m_db;
	
	public User(Context context)
	{
		
		m_db = new DBManager(context, "RS.sqlite", "/data/data/com.cic.rs/data/", "data/");
        
        m_db.initialize();		
	}
	
	
	public void loadFromPR()
	{
		String history = m_db.loadRecords("user_string");
		
		if ( history.equals("")){
			
		} else {
			String[] cols = history.split(this.delim);
			
			if ( cols.length == 2) {
				
				this.m_userId = cols[0];
				this.m_userPasswd = cols[1];				
			}
			
						
		}
		
	}

	public void saveToPRF()
	{
		m_db.saveRecords("user_string", this.toString());
		
	}
	
	public void setId(String id, String pwd)
	{
		this.m_userId = id;
		this.m_userPasswd = pwd;
		this.saveToPRF();
	}	
	
	public String getId()
	{
		return this.m_userId;
	}

	public String getPwd()
	{
		return this.m_userPasswd;
	}		
	
	public String toString()
	{
		String str = this.m_userId + this.delim 
				+ this.m_userPasswd;
		
		return str;
	}	
	
	
}
