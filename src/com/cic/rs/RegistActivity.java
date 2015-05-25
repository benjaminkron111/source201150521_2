package com.cic.rs;

import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.cic.rs.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegistActivity extends Activity {
	
	public User m_user;
	SharedPreferences m_sharedPreference;
	
	public  Handler m_Handler;
	
	int m_submitResult;
	
	ImageView btn_regist;
	ImageView btn_exit;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_regist);
        
        btn_regist = (ImageView)findViewById(R.id.buttonRegist);;
        btn_regist.setOnClickListener(registListener);    
        
        btn_exit = (ImageView)findViewById(R.id.buttonEnd);
        btn_exit.setOnClickListener(exitListener);           
        
        m_sharedPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        m_user = new User(getApplicationContext());     
        
      	m_Handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	
              super.handleMessage(msg);
              
              messageProcess(msg);
            }
        };           
        
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);

		  	builder
		  	.setMessage(R.string.msg_are_you_sure)
		    .setCancelable(false)
		    .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener(){
		    	public void onClick(DialogInterface dialog, int id) {
		    		
		    	}
		    })	    
		    .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int id) {
		    	  finish();
		      }
		     });

		  	AlertDialog alert = builder.create();
		  	alert.show();
		}
		return false;
	}	
    
    
    
    private OnClickListener exitListener = new OnClickListener() {
        public void onClick(View v) {
        	finish();
        }
    };
    
    private OnClickListener registListener = new OnClickListener() {
        public void onClick(View v) {
        	
        	EditText et = (EditText)findViewById(R.id.editID);
    		String user_id = et.getText().toString();
    		
    		et = (EditText)findViewById(R.id.editPWD);
    		String user_pwd = et.getText().toString();
    		
    		if ( user_id.equals("") || user_pwd.equals(""))
    		{
    			showMsgDialog(getApplicationContext().getString(R.string.msg_id_or_password_cannot_be_empty));
    			
    		}
    		else
    		{
    			
    			m_user.setId(user_id, user_pwd);
    	 		
    	 		new HttpCom().execute();
        		btn_regist.setEnabled(false);
        		btn_exit.setEnabled(false);
    		}
        }
    };    
    
    private void showMsgDialog(String msg)
	{
    	
    	new AlertDialog.Builder(this)
        .setIcon(R.drawable.alert_dialog_icon)        
        .setTitle(R.string.login_dialog_title)  
        .setMessage(msg)
        .setNeutralButton(R.string.dialog_button_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	btn_regist.setEnabled(true);
        		btn_exit.setEnabled(true);
            }
        })              
        .show();
	}
    
    public void messageProcess(Message msg)
  	{
    	switch(msg.what){
	  		case Config.SUBMIT_DONE:
	  			if ( m_submitResult == 0) 
	  			{
			    		Intent i = new Intent(getApplicationContext(), MainActivity.class);
						startActivity(i);  
						finish();
						
						  				
	  			} else if ( m_submitResult ==1 )
	  			{
	  				m_user.setId("", "");
	  				showMsgDialog(getApplicationContext().getString(R.string.msg_invalid_user_or_password));
	  			}
	  			else
	  			{
	  				m_user.setId("", "");
	  				showMsgDialog(getApplicationContext().getString(R.string.msg_cannot_connect_server));
	  			}
	  			

				
	  			break;
  		}
  	}    
    
    
    private class HttpCom extends AsyncTask<URL, Integer, Long> {
  	  	public long m_result;
  	  	
  	    protected Long doInBackground(URL... urls) {
  	    	  
  	    	m_result = postData();
  	      	
  	      	m_Handler.sendEmptyMessageDelayed(Config.SUBMIT_DONE, 10);
  	      
  	      	return m_result;
  	    }
  	      
  	    public int postData()
  	    {
  	    	
  	    	long result = 0;
  	      	try{
      			
  	      		HttpClient client = new DefaultHttpClient();  
  				String postURL = Config.SERVER_URL_BASE + Config.SERVER_URL_REGIST;
  				
  				HttpPost post = new HttpPost(postURL); 
  				List<NameValuePair> params = new ArrayList<NameValuePair>();
  				params.add(new BasicNameValuePair("userid", m_user.getId()));
  				params.add(new BasicNameValuePair("passwd", m_user.getPwd()));
  				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
  				post.setEntity(ent);
  				
  				HttpResponse responsePOST = client.execute(post);  
  				HttpEntity resEntity = responsePOST.getEntity();
  				if (resEntity != null)
  				{    
  					String submit_result =  EntityUtils.toString(resEntity); 
  					if ( submit_result.equals("1"))
  					{
  						m_submitResult = 0; // ok
  					}
  					else if ( submit_result.equals("0"))
  					{
  						m_submitResult = 1; // invalid id or passwd
  					}
  					else 
  					{
  						m_submitResult = 2; // connect failed
  					}
        		
  				}
  	      	}catch(Exception e){
  	      		m_submitResult = 2;
  	      	}
  	      	
  	      	return 1;
  	      }
  	      @Override
  	      protected void onProgressUpdate(Integer... progress) {
  	      	
  	      }
  	      @Override
  	      protected void onPostExecute(Long result) {
  	      	

  	      }
  	  }	    
}
