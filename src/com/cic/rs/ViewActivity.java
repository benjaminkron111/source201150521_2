package com.cic.rs;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.cic.rs.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewActivity extends Activity implements OnClickListener, OnKeyListener {
	
	public RecordSet m_rs;
	public Record m_record;
	
	SharedPreferences m_sharedPreference;
	
	int m_nPos;
	public Handler m_Handler;
	public String m_message = "";
	int m_submitResult = 0;
	
	User m_user;
	
	TextView m_amountObj;
	EditText m_shipcount;
	
	ImageView btn_submit;
	ImageView btn_cancel;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view);
        
        Bundle extra = getIntent().getExtras();

        int pos = extra.getInt("position");
        m_nPos = pos;
        
        m_sharedPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        
        m_rs = new RecordSet(getApplicationContext());
        
        m_rs.loadFromPRF();        
        m_record = m_rs.rows.get(pos);
 
        m_user = new User(getApplicationContext());   
        m_user.loadFromPR();
       
        btn_cancel = (ImageView)findViewById(R.id.buttonCancel);
        btn_cancel.setOnClickListener(cancelListener);
        
        btn_submit = (ImageView)findViewById(R.id.buttonSubmit);
        btn_submit.setOnClickListener(submitListener);
                
        populate();
       
      	m_Handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
              super.handleMessage(msg);
              messageProcess(msg);
            }
        };          
        
        
    }
    
    private void showAlertDialog(String msg)
    {
        m_message = msg;
        m_Handler.sendEmptyMessageDelayed(Config.MESSAGE_TOAST, 10);    	
    }
    
    private void showToast(String msg)
    {
    	m_message = msg;
    	m_Handler.sendEmptyMessageDelayed(Config.MESSAGE_ALERT, 10);
    }    
    
    public void messageProcess(Message msg)
  	{
  		switch(msg.what){
	  		case Config.MESSAGE_TOAST:
		  		  Toast.makeText(this, m_message, Toast.LENGTH_SHORT).show();
		  		  break;
	  		case Config.MESSAGE_ALERT:
	  				showMsgDialog(m_message);
		  		  break;	  	
		  		  
	  		case Config.SUBMIT_DONE:
	  			
	  			String msg1;
	  			if ( m_submitResult == 0) //  success
	  			{
	  				msg1 = getString(R.string.msg_successfully_uploaded);
	  				m_record.markAsRead();
	  				
	  				if ( m_record.remark.equals(""))
	  				{
	  					m_record.remark = " ";
	  				}
	  				m_rs.updateOrder(m_nPos, m_record);
	  				
	  			}
	  			else if ( m_submitResult == 1) // fail
	  			{
	  				msg1 = getString(R.string.msg_upload_failed);
	  			}
	  			else // connection problem
	  			{
	  				msg1 = getString(R.string.msg_cannot_connect_server);
	  			}
	  			
	  			
 				 AlertDialog.Builder builder = new AlertDialog.Builder(this);

	  			  	builder
	  			  	.setMessage(msg1)
	  			    .setCancelable(false)
	  			    .setNegativeButton(R.string.button_ok, new DialogInterface.OnClickListener(){
	  			    	public void onClick(DialogInterface dialog, int id) {
	  			    		Intent i = new Intent(getApplicationContext(), MainActivity.class);
	  						startActivity(i);  
	  						finish();
	  			    	}
	  			    });
	  			  	AlertDialog alert = builder.create();
	  			  	alert.show();
	  			

				
	  			break;
  		}
  	}
    
    private void showMsgDialog(String msg)
	{
    	
    	new AlertDialog.Builder(this)
      
        .setTitle(R.string.dialog_button_yes)  
        .setMessage(msg)
        .setNeutralButton(R.string.dialog_button_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        })              
        .show();
	}
    
    private void populate()
    {

    	TextView tv;
    	EditText et;
    	
    	tv = (TextView)findViewById(R.id.textDateValue);
    	tv.setText("   " + m_record.date);
    	
    	tv = (TextView)findViewById(R.id.textOrderValue);
    	tv.setText(m_record.orderid); 
    	
    	tv = (TextView)findViewById(R.id.textShopValue);
    	tv.setText(m_record.branch);
    	
    	tv = (TextView)findViewById(R.id.textComValue);
    	tv.setText(m_record.commodity);
    	
    	tv = (TextView)findViewById(R.id.textUnitValue);
    	tv.setText(m_record.unit);
    	
    	tv = (TextView)findViewById(R.id.textPriceValue);
    	tv.setText(m_record.price + " 元");
    	
    	tv = (TextView)findViewById(R.id.textCountValue);
    	tv.setText(m_record.count);
    	
    	m_amountObj = (TextView)findViewById(R.id.textAmountValue);
    	m_amountObj.setText(m_record.amount  + " 元");
    	
    	m_shipcount = (EditText)findViewById(R.id.editShipCountValue);
    	m_shipcount.setText(m_record.shipCount);   
    	
    	m_shipcount.setOnKeyListener(this);
    	
    	TextView tv_shipcount = (TextView)findViewById(R.id.textShipCountValue);
    	tv_shipcount.setText(m_record.shipCount);
    	
    	EditText et_remark = (EditText)findViewById(R.id.editRemarkValue);
    	et_remark.setText(m_record.remark);    
    	TextView tv_remark = (TextView)findViewById(R.id.textRemarkValue);
    	tv_remark.setText(m_record.remark);  
    	
    	
    	
    	
    	if ( m_record.status.equals("1")) // wait
    	{
    		tv_shipcount.setVisibility(View.GONE);
    		tv_remark.setVisibility(View.GONE);
    		
    		m_shipcount.setVisibility(View.VISIBLE); //
    		et_remark.setVisibility(View.VISIBLE);     
    		
    		btn_submit.setVisibility(View.VISIBLE);
    	}
    	else // done state
    	{
    		tv_shipcount.setVisibility(View.VISIBLE);
    		tv_remark.setVisibility(View.VISIBLE);
    		
    		m_shipcount.setVisibility(View.GONE); // gone
    		et_remark.setVisibility(View.GONE);
    		
    		btn_submit.setVisibility(View.GONE);
    		
    	}  	 
    	

    }

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.buttonCancel:

			break;
			
		}
	}
	
	
    private OnClickListener cancelListener = new OnClickListener() {
        public void onClick(View v) {
        	
			Intent i = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(i);  
			finish();

        }
    }; 
    
    private OnClickListener submitListener = new OnClickListener() {
        public void onClick(View v) {
        	
    		EditText et = (EditText)findViewById(R.id.editRemarkValue);
    		String str_remark = et.getText().toString();
    		
    		et = (EditText)findViewById(R.id.editShipCountValue);
    		String str_shipcount = et.getText().toString();
    		
    		Double amount = 0.0;
    		if ( str_shipcount.equals(""))
    		{
    			showMsgDialog(getApplicationContext().getString(R.string.msg_shipcount_cannot_be_empty));		
    		} else {
    			try {
            		amount = Double.parseDouble(m_record.price) * Double.parseDouble(str_shipcount);
        		} catch( Exception e) {
        			
        		}
        		
        		String str_amount = myFormat(amount, 1);
        		
        		m_record.shipCount = str_shipcount;
        		m_record.remark = str_remark;
        		m_record.amount = str_amount;
        		
           		if ( m_record.shipCount.equals("")) {
        			m_record.shipCount = "0";
        		}    		
        		
           		new HttpCom().execute();
        		btn_submit.setEnabled(false);
        		btn_cancel.setEnabled(false);   			
    		}
    		
        }
    };     
    
    public String myFormat(Double d, int dec)
    {
		
		
		int digit = (int) Math.pow(10, dec);
		
		int x1 = (int)(d * digit) % digit;
		int b1 = (int) (d * 1.0);
		
		String r1 = "";
		if ( x1 == 0) {
			r1 = String.valueOf(b1);
		} else {
		    r1 = String.valueOf(b1) + "." + String.valueOf(x1);
		}
		
		
		return r1;
    }
    
    
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		EditText et_shipcount = (EditText)findViewById(R.id.editShipCountValue);

		
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);

		  	builder
		  	.setMessage(R.string.msg_are_you_sure)
		    .setCancelable(false)
		    .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int id) {
		    	  
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(i);  
		  
				finish();
		      }
		     })
		    .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener(){
		    	public void onClick(DialogInterface dialog, int id) {
		    		
		    	}
		    });
		  	AlertDialog alert = builder.create();
		  	alert.show();
		}
		
		return false;
	}	
    
	
	private class HttpCom extends AsyncTask<URL, Integer, Long> {
  	  	public long m_result;
  	      protected Long doInBackground(URL... urls) {
  		      	
  	      	
  	      	m_result = postData();
  	      	  	      	
  	      	m_Handler.sendEmptyMessageDelayed(Config.SUBMIT_DONE, 10);
  	      
  	      	return m_result;
  	      }
  	      
  	      public long postData()
  	      {
  	      	try{
  	      		HttpClient client = new DefaultHttpClient();  
  	      		
  	      	
  	  				String postURL = Config.SERVER_URL_BASE + Config.SERVER_URL_UPLOAD + "?userid=" + m_user.getId() + "&passwd=" + m_user.getPwd();
  	  				HttpPost post = new HttpPost(postURL); 
  	  				List<NameValuePair> params = new ArrayList<NameValuePair>();
  	  				params.add(new BasicNameValuePair("dno", m_record.dno));
  	  				params.add(new BasicNameValuePair("remark", m_record.remark));
  	  				params.add(new BasicNameValuePair("shipCount", m_record.shipCount));
  	  				params.add(new BasicNameValuePair("amount", m_record.amount));
  	  				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
  	  				post.setEntity(ent);
  	  				HttpResponse responsePOST = client.execute(post);  
  	  				HttpEntity resEntity = responsePOST.getEntity();
  	  				if (resEntity != null)
  	  				{    
  	  					String result =  EntityUtils.toString(resEntity); 
  	  					
  	  					if ( result.equals("1")){
  	  						m_submitResult = 0;
  	  						return 0;
  	  					}
  	        		
  	  					else if ( result.equals("0")){
  	  						m_submitResult = 1;
  	  						return 1;
  	  					} else {
  	  						m_submitResult = 1;
  	  					return 1;
  	  					}
  	  				}
  	      	}catch(Exception e){
  	      		m_submitResult = 2;
  	      		return 2;
  	      	}
  	      	
  	      	return 0;
  	      }
  	      @Override
  	      protected void onProgressUpdate(Integer... progress) {
  	      	
  	      }
  	      @Override
  	      protected void onPostExecute(Long result) {
  	      	

  	      }
  	  }

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		
		double price = 0;
		double shipcount = 0;
		try {
			String str = m_shipcount.getText().toString();
			shipcount = Double.parseDouble(str);
			
			
			str = m_record.price;
			price = Double.parseDouble(str);
		} catch ( Exception e)
		{
			
		}
		
		double amount = price * shipcount;
		
		String str_amount = myFormat(amount, 1); 
		
		m_amountObj.setText(str_amount + " 元");
		// TODO Auto-generated method stub
		return false;
	}	

}
