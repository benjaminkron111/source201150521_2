package com.cic.rs;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		/*
		 SharedPreferences spre = PreferenceManager.getDefaultSharedPreferences(context);
		 spre.edit().putInt("firstrun", 1).commit();
		 */
		System.out.println("--------------bootUpReceiver start-------------------");
		
		Intent i = new Intent(context, MainActivity.class);
		
		i.putExtra("firstrun",  1);
		
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);  
	}
}
