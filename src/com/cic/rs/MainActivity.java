package com.cic.rs;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import java.net.URL;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.cic.rs.R;
import android.os.AsyncTask;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


/**
 * This demonstrates how you can schedule an alarm that causes a service to
 * be started.  This is useful when you want to schedule alarms that initiate
 * long-running operations, such as retrieving recent e-mails.
 */
public class MainActivity extends ListActivity {
    
	Context 							m_context;
	private PendingIntent 				mAlarmSender;
	SharedPreferences 					m_sharedPreference;
	
	ImageView 							btn_download;
	ImageView 							btn_clean;
	ImageView 							btn_signout;
	GridView 					        m_dataList;
	ViewAdapter 						m_gridAdapter;
	ArrayList<String>					m_arrData;
	
	private HttpClient 					m_HttpClient;
	private  Handler 					m_Handler;
	
	RecordSet 							m_rs;
	User 								m_user;
	Status 								m_status;
	Boolean 							m_running;
	
	long 								m_downloadResult;
	String 								m_strReceivedData;
	Thread 								m_thr;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        System.out.println("--------------MainActivity start-------------------"); 
        
        m_context = this;
        /*mAlarmSender = PendingIntent.getService(MainActivity.this,
                0, new Intent(MainActivity.this, AlarmService_Service.class), 0);*/
        //m_sharedPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        
        m_user = new User(getApplicationContext());   
        m_user.loadFromPR();
        
        if ( m_user.getId() == "")
        {
        	
        	Intent i = new Intent(getApplicationContext(), RegistActivity.class);
			startActivity(i);		
			finish();	
		}        
        else{
        	
        	btn_download = (ImageView)findViewById(R.id.btn_download);
            btn_download.setOnClickListener(mDownloadListener);     
            
            btn_clean = (ImageView)findViewById(R.id.btn_clean);
            btn_clean.setOnClickListener(mCleanListener);        
            
            btn_signout = (ImageView)findViewById(R.id.btn_signout);
            btn_signout.setOnClickListener(userResetListener);
            
            m_gridAdapter = new ViewAdapter(this , true);
            
            m_dataList = (GridView)findViewById(R.id.dataList);
    		m_dataList.setOnItemClickListener(new OnItemClickListener() {
    			
    			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    	    	
    				int position1 = m_rs.rows.size() - position - 1;

    				Intent i = new Intent(getApplicationContext(), ViewActivity.class);
    				i.putExtra("position", position1);
    				startActivity(i);  
    				finish();
    			}
    	    });
    		    		
            
        	m_rs = new RecordSet(getApplicationContext());
            m_rs.loadFromPRF();
            
            //m_status = new Status(getApplicationContext());
            // m_status.loadFromPR();
        	
            
            new HttpCom().execute();
            
            m_Handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                  
                	super.handleMessage(msg);
                	
                	if(msg.what == Config.SUBMIT_GET_LIST){
                  	  
                		Log.v("System.out", "**MainActivity->m_Handler = new Handler()->handleMessage("+ msg+")->if(msg.what == SUBMIT_GET_LIST)----");
                		if (m_downloadResult == 0 && m_rs.rows.size()==0) //updated by kjg
                  	  		
                  	  		Toast.makeText(getApplicationContext(), R.string.msg_nothing_new, Toast.LENGTH_LONG).show();  	      		
                  	  	
                  	  	else refreshTable();
                    
                	}          	
                }
            };            
            
            Log.v("System.out", "---------------------mark4-------------------------");
            
        	/*Bundle extras = getIntent().getExtras();
            if ( extras != null && extras.getInt("firstrun") == 1)
            {
            	//@hoping
            	registAsAlarm();
                finish(); 
            } 
           
            NotificationManager mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            mNM.cancelAll();
            
            if ( m_status.isRunning() == false) 
            {
            	m_status.startRunning();
            	registAsAlarm();
            }*/        
            
                     
                    
            /*m_thr = new Thread(null, mTask, "refreshTable");
            m_running = true;*/
            
    		//m_dataList.setAdapter(m_gridAdapter);
    		
            //refreshTable();
    	}       
        
    }
    
    public void registAsAlarm()
    {
    	
        long firstTime = SystemClock.elapsedRealtime();
        // Schedule the alarm!
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        firstTime,Config.CHECK_SERVER_RATE *1000, 
                        mAlarmSender
        );      	
    }
    
    public void OnDestroy(Bundle savedInstanceState)
    {
        Status m_status = new Status(getApplicationContext());
        m_status.stopRunning();

    }
    
    private OnClickListener mStartAlarmListener = new OnClickListener() {
        
    	public void onClick(View v) {
            // We want the alarm to go off 30 seconds from now.
            long firstTime = SystemClock.elapsedRealtime();
            // Schedule the alarm!
            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            firstTime, Config.CHECK_SERVER_RATE *1000, mAlarmSender);
            // Tell the user about what we did.
            //Toast.makeText(MainActivity.this, "started alarm",Toast.LENGTH_LONG).show();
            
        }
    };
    
    private OnClickListener mStopAlarmListener = new OnClickListener() {
        
    	public void onClick(View v) {
            // And cancel the alarm.
            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
            am.cancel(mAlarmSender);

            // Tell the user about what we did.
            //Toast.makeText(MainActivity.this, "stop alarm",Toast.LENGTH_LONG).show();

        }
    };     
    
    private OnClickListener mDownloadListener = new OnClickListener() {
        
    	public void onClick(View v) {
        	
        	new HttpCom().execute();        	
 	      	
        }
    }; 
    
    private OnClickListener mCleanListener = new OnClickListener() {
        
    	public void onClick(View v) {
        	
        	m_rs.reset();

        	new HttpCom().execute();
        	
 	    }
    }; 
    
    private void refreshTable()
    {
    	m_rs.loadFromPRF();
    	m_dataList.setAdapter(m_gridAdapter);    	
    }
    
    private OnClickListener mViewListener = new OnClickListener() {
        
    	public void onClick(View v) {
        	
        	//m_rs.loadFromPRF();
        	//m_dataList.setAdapter(m_gridAdapter);
        	
        	refreshTable();
        	

        }
    };     
    
    
    private OnClickListener userResetListener = new OnClickListener() {
        
    	public void onClick(View v) {
        	
        	m_user.setId("", "");
        	
        	m_status.stopRunning();
        	
			Intent i = new Intent(getApplicationContext(), RegistActivity.class);
			startActivity(i);  
			finish();
        }
    };
    
       
    
  	@Override
  	public boolean onCreateOptionsMenu(Menu menu) {
  		// Inflate the menu; this adds items to the action bar if it is present.
  		getMenuInflater().inflate(R.menu.activity_main, menu);
  		return true;
  	}
  	
  	private class HttpCom extends AsyncTask<URL, Integer, Long> {
  	  	
  	  	protected Long doInBackground(URL... urls) {
  	
  	  		m_downloadResult = getData();
			
			m_Handler.sendEmptyMessageDelayed(Config.SUBMIT_GET_LIST, 10);
  	
			return m_downloadResult;
  	  	}
  	    
  	  	public int getData()
  	    {
  	  		int added = 0;

  	    	try {
  	      		
  	    		URL url = new URL(Config.SERVER_URL_BASE + Config.SERVER_URL_DOWNLOAD + "?userid=" + m_user.getId() + "&passwd=" + m_user.getPwd());
					
				HttpGet getRequest = new HttpGet(url.toString());
				m_HttpClient = new DefaultHttpClient(); 
				
				HttpResponse resp = m_HttpClient.execute(getRequest);
				
  	        	if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
  	        		
  	        		m_strReceivedData = EntityUtils.toString(resp.getEntity());
  	        		
  	        		added = m_rs.addItems(m_strReceivedData);
  	        		
  	        	}
  	    	
  	    	} catch (Exception e) { }
  	    	
  	      return added;
  	    }
  	      
  	      
  	    @Override
  	    protected void onProgressUpdate(Integer... progress) {
  	      	
  	    }
  	    @Override
  	    protected void onPostExecute(Long result) {
  	      	//m_dataList.setAdapter(m_gridAdapter);

  	    }
  	}
  	
  	public class ViewAdapter extends BaseAdapter { 
  	  	
  		boolean state;
  	  	LayoutInflater layoutInflater;
  	  	private Context mContext; 
  	  	TextView txtData;
  	  	
  	    public ViewAdapter(Context c , boolean status) {
  	        
  	    	mContext = c;
  	        state = status;
  	        layoutInflater = (LayoutInflater)((Activity) c).getSystemService(LAYOUT_INFLATER_SERVICE);
  	    }
  	    
  	    public int getCount() {
  	        
  	    	return m_rs.rows.size();
  	    }
  	    
  	    public Object getItem(int position) {
  	    	
  	    	return getCount() - position;
  	    }
  	    
  	    public long getItemId(int position) {
  	    	//return getCount() - position;
  	        return position;
  	    }
  	  	
  	    public View getView(int position1, View convertView, ViewGroup parent) {
  	  		
			View view = convertView;
 			if (view == null) {
 				
 				view = layoutInflater.inflate(R.layout.item_rows, null);
 			}
 			
 			ImageView iv1 = (ImageView)view.findViewById(R.id.iv1);
 			
 			TextView tv_date = (TextView)view.findViewById(R.id.tv1);
 			TextView tv_com = (TextView)view.findViewById(R.id.tv2);
 			TextView tv_count = (TextView)view.findViewById(R.id.tv3); 
 			TextView tv_branch = (TextView)view.findViewById(R.id.tv4);
		
 			int position = m_rs.rows.size() - position1 - 1;
 			if ( m_rs.rows.get(position).status.equals("1")) { // wait
 				
 				iv1.setImageResource(R.drawable.wait);
 				tv_count.setText(m_rs.rows.get(position).count + " " + m_rs.rows.get(position).unit);
 			
 			} else {
 				
 				iv1.setImageResource(R.drawable.done);
 				tv_count.setText(m_rs.rows.get(position).shipCount + " " + m_rs.rows.get(position).unit);
 			}
 			
 			
 			tv_date.setText(m_rs.rows.get(position).date);
 			tv_com.setText(m_rs.rows.get(position).commodity);
 			
 			tv_branch.setText(m_rs.rows.get(position).branch);
 			return view;
 		}
  	}
  		

  	  	
  	public boolean onKeyDown(int keyCode, KeyEvent event) {
  		
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);

		  	builder
		  	.setMessage(R.string.msg_are_you_sure_to_exit)
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
		//return super.onKeyDown(keyCode, event);
		return false;
	}
  	
  	 /*Runnable mTask = new Runnable() {
         public void run() {
 	
             // Normally we would do some work here...  for our sample, we will
             // just sleep for 30 seconds.
         	
         	while(m_running) {
         		long endTime = System.currentTimeMillis() + Config.REFRESH_GRID_RATE *1000;
                while (System.currentTimeMillis() < endTime) {
                    synchronized (mBinder) {
                        try {
                            mBinder.wait(endTime - System.currentTimeMillis());
                        } catch (Exception e) {
                        }
                    }
                }
                
                m_Handler.sendEmptyMessageDelayed(100, 10);         		
         	}
             

             // Done with our work...  stop the service!
             
         }
     };   	
  	
     private final IBinder mBinder = new Binder() {
         @Override
 		protected boolean onTransact(int code, Parcel data, Parcel reply,
 		        int flags) throws RemoteException {
             return super.onTransact(code, data, reply, flags);
         }
     };*/
     
        
     
}

