/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cic.rs;


import java.net.URL;



import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.cic.rs.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class AlarmService_Service extends Service {
    NotificationManager mNM;
    User m_user;
    SharedPreferences m_sharedPreference;
    private HttpClient m_HttpClient = null;
    RecordSet m_rs;
    
    public  Handler m_Handler;
    
    String m_strResp;
    
    Status m_status;
    
    private Vibrator mVibrator;
    
    @Override
    public void onCreate() {
    	System.out.println("--------------AlarmService_Service start-------------------");
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        
        m_sharedPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        m_user = new User(getApplicationContext());   
        m_user.loadFromPR();
        
        m_rs = new RecordSet(getApplicationContext());
        m_rs.loadFromPRF();
        
        m_status = new Status(getApplicationContext());
        
        Thread thr = new Thread(null, mTask, "AlarmService_Service");
        thr.start();
        
        
      	m_Handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
              super.handleMessage(msg);
              messageProcess(msg);
            }
        };    

    }
    
    public void messageProcess(Message msg)
  	{
  		switch(msg.what){
	  		case Config.MESSAGE_ARRIVED_NEW_ORDERS:
		  		  Toast.makeText(this, "New order(s) arrived.", Toast.LENGTH_SHORT).show();
		  		  break;
	  		
  		}
  	}

    @Override
    public void onDestroy() {
        // Cancel the notification -- we use the same ID that we had used to start it
    	
    		if ( m_status.isRunning()) 
    		{
    			//mNM.cancel(R.string.app_name);
    		}

        
        //Toast.makeText(this, m_user.getId(), Toast.LENGTH_SHORT).show();
        
        int added=0;
        String str;
        try
        {
            added = m_rs.addItems(m_strResp);
            str = String.valueOf(added) + " added";
            
            if ( Config.RUN_MODE.equals("DEBUG"))
            {
            	Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
            }           
            
            
	      	if ( added > 0)
  	      	{
  	      		
  	      		str = String.valueOf(added) + " 新订单";
  	      		
  	      		/*
  	      		if ( m_status.isRunning()) 
  	      		{
  	      			showNotification("running");
  	      		}
  	      		else 
  	      		{
  	      			
  	      		}
  	      		*/
  	      		showNotification(str);
  	      		//mVibrator.vibrate(3000);
  	      		
  	      	
  	      		//Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
  	      		
  	      	} else {
  	      		//Toast.makeText(this, "Nothing new", Toast.LENGTH_SHORT).show();
  	      	}
            
        } catch (Exception e)
        {
        	if ( Config.RUN_MODE.equals("DEBUG"))
        	{
        		Toast.makeText(this, m_strResp, Toast.LENGTH_SHORT).show();
        	}
        }
        
    }
    Runnable mTask = new Runnable() {
        public void run() {
	
        	getData();
  	      	
            // Normally we would do some work here...  for our sample, we will
            // just sleep for 30 seconds.
        	
        	
            long endTime = System.currentTimeMillis() + 3*1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (mBinder) {
                    try {
                        mBinder.wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                    }
                }
            }
            

            // Done with our work...  stop the service!
            AlarmService_Service.this.stopSelf();
        }
        
        public int getData()
	      {
	    	int added = 0;
	      	//m_arrData.clear();
	      	try 
	      	{
    			URL url = new URL(Config.SERVER_URL_BASE + Config.SERVER_URL_DOWNLOAD + "?userid=" + m_user.getId() + "&passwd=" + m_user.getPwd());
    			
    			HttpGet getRequest = new HttpGet(url.toString());
	        	m_HttpClient = new DefaultHttpClient(); 
	        	HttpResponse resp = m_HttpClient.execute(getRequest);
	        	if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	        		m_strResp =  EntityUtils.toString(resp.getEntity());
	        	}
	    	} 
	      	catch (Exception e) 
	      	{
	    		
	    	}
	      return added;
	     }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Show a notification while this service is running.
     */
    @SuppressWarnings("deprecation")
	private void showNotification(String str) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = str;// "arrived new data";
        // Set the icon, scrolling text and timestamp
        
		Notification notification = new Notification(R.drawable.stat_notify_sms, text,
                System.currentTimeMillis());
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.app_name),
                       text, contentIntent);
        mNM.notify(R.string.app_name, notification);
    }

    private final IBinder mBinder = new Binder() {
        @Override
		protected boolean onTransact(int code, Parcel data, Parcel reply,
		        int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }
    };
    
    
}

