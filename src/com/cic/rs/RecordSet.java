package com.cic.rs;

import java.util.ArrayList;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class RecordSet extends Activity {

	public String delim = "r###r";
	public ArrayList<Record> rows;
	public SharedPreferences m_sharedPreference;
	DBManager m_db;
	
	public RecordSet(Context context)
	{
		this.rows = new ArrayList<Record>();

		m_db = new DBManager(context, "RS.sqlite", "/data/data/com.cic.rs/data/", "data/");
        
        m_db.initialize();
        
	}
	
	public String toString()
	{
		String str = "";
		for(int i=0; i<this.rows.size(); i++)
		{
			if ( str.equals(""))
			{
				str = this.rows.get(i).toString();
			}
			else
			{
				str += this.delim + this.rows.get(i).toString();
			}
		}
		
		return str;
	}
	
	public Boolean canFindItem(String id)
	{
		
		for( int i=0; i<this.rows.size(); i++)
		{
			if (id.equals(this.rows.get(i).dno))
				
				return true;
		}
		
		return false;
	}
	
	public void markAsRead(int index)
	{
		this.rows.get(index).status = "2";
		saveToPRF();
		
	}
	
	public int addItems(String str) throws Exception 
	{
		
		int added = 0;
		
		if ( str.equals("")) return 0;
		
		String[] rows = str.split(this.delim);
		
		if ( rows.length == 0) return 0;
		
		for( int i=0; i<rows.length; i++)
		{
			Record row = new Record();
			row.fromString(rows[i]);	
			if (!canFindItem(row.dno))
			{
				this.rows.add(row);
				added ++;
			}
			
		}
		
		
		if(added > 0)//updated by kjg 
			
			saveToPRF();
		
		return added;
	}
	
	public void updateOrder(int rowIndex, Record record)
	{
		try
		{
			if ( this.rows.get(rowIndex) != null)
			{
				this.rows.get(rowIndex).shipCount = record.shipCount;
				this.rows.get(rowIndex).amount = record.amount;
				this.rows.get(rowIndex).remark = record.remark;
			}			
		}
		catch(Exception e)
		{
			
		}

		String str = this.toString();
        m_db.updateOrder(str);		

	}
	
	public void updateItem(int rowIndex, int colIndex, String val)
	{
		try
		{
			if ( this.rows.get(rowIndex) != null)
			{
				this.rows.get(rowIndex).update(colIndex, val);
			}			
		}
		catch(Exception e)
		{
			
		}

	}
	
	public void reset()
	{
		this.rows.clear();
		saveToPRF();
	}
	
	public void saveToPRF()
	{

		String str = this.toString();
        m_db.saveRecords("order_string", str);

	}
	
	public void loadFromPRF()
	{
		
		String history = m_db.loadRecords("order_string");
    	
    	if ( !history.equals(""))
    	{
    		this.rows.clear();
    		
    		String[] rows = history.split(this.delim);
    		
    		for(int i = 0; i < rows.length ; i ++){
    			
    			Record row = new Record();
    			row.fromString(rows[i]);
    			
    			this.rows.add(row);
    			
    		}    		
    	}
    			
		
	}
	
	@Override
    public void onDestroy() {
		m_db.closeDataBase();
	}

}
