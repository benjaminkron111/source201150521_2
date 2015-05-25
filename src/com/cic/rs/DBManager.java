package com.cic.rs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBManager {
	private String m_dataBaseName;
	private SQLiteDatabase m_db = null;
	private String m_dataResourceDir;
	private String m_dataLocalDir;
	private Context m_context;
	static public final String[] USER_COLUMNS = {"id" ,"password"};
	static public final String[] ORDER_COLUMNS = {"id" ,"isNew" ,"reqDate","qty", "storeName", "comment"};
	
	public DBManager(Context context ,String dbName ,String dataLocalDir ,String dataResourceDir){

		this.m_context = context;
		this.m_dataBaseName = dbName;
		this.m_dataLocalDir = dataLocalDir;
		this.m_dataResourceDir = dataResourceDir;
		
	}
	
	public boolean initialize(){
		
		boolean bReturnVal = true;
		try{
			if(!getExistsDataBaseFile()){
				bReturnVal = copyDBFile(this.m_context);
			}
			if(bReturnVal){
				openDataBase();
			}
		}catch (Exception e){
			bReturnVal = false;
		}
		return bReturnVal;
	}
	
	private void openDataBase(){
		if(m_db == null){
			m_db = SQLiteDatabase.openDatabase(m_dataLocalDir + m_dataBaseName, null, SQLiteDatabase.CREATE_IF_NECESSARY);
		}
	}
		
	public void closeDataBase(){
		if(m_db != null){
			m_db.close();
			m_db = null;
		}
	}
	
	private boolean getExistsDataBaseFile(){
		
		try{
			File isfile = new File(m_dataLocalDir + m_dataBaseName);
			return isfile.exists();

		}catch (Exception ex){
			return false;
		}
	}
	
	private boolean copyDBFile(Context context){
		
		try
		{
			InputStream is = null;
			OutputStream os = null;
			File dt_dir = new File(m_dataLocalDir);
			if (!dt_dir.exists()){
				dt_dir.mkdir();
			}
			File db_file = new File(m_dataLocalDir + m_dataBaseName);
			
			if (!db_file.exists()) {
				
				is = context.getAssets().open(m_dataResourceDir + m_dataBaseName);

				if(is == null){
					return false;
				}
				os = new FileOutputStream(db_file);
				byte[] data = new byte[is.available()];
				is.read(data);
				os.write(data);
				is.close();
				os.close();
			}
		}
		catch (Exception ex)
		{
			return false;
		}
		return true;
	}
	
	public boolean saveRecords(String table, String value) throws SQLException{
		
		resetRecords(table);
		
		ContentValues values = new ContentValues();
	
		values.put("data", value);
		long success = m_db.insert(table, null, values);
		if (success == -1) {
			return false;
		}
		return true;
	}
	
	public boolean updateOrder(String value) throws SQLException{
		
		ContentValues values = new ContentValues();
		values.put("data", value);
		
		long success = m_db.update("order_string", values, null, null);
		if (success == -1) {
			return false;
		}
		return true;
	}
	
	public void resetRecords(String table) throws SQLException
	{
		m_db.delete(table, "", null);
	}
	
	public String loadRecords(String table) throws SQLException {
		
		String ret = "";
		
		String[] columns = {"data"};
		Cursor cursor = m_db.query(table, columns, null, null, null, null, null);
		cursor.moveToNext();
		
		if ( cursor.getCount() >0)
		{
			ret = cursor.getString(0);
		}

		cursor.close();			

		return ret;
	}	
		
	
	
	

	
	
	
}
