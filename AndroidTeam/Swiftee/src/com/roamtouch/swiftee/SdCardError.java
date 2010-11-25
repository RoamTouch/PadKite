package com.roamtouch.swiftee;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SdCardError extends Activity{


	SwifteeApplication appState;
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sdcard_error);
        
        appState = ((SwifteeApplication)getApplicationContext());
        
        if(appState.isSdCardReady()){
			Intent intent = new Intent();
			intent.setClass(SdCardError.this,BrowserActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
		}
        else{
        	Button b = (Button) findViewById(R.id.quit);
        	b.setOnClickListener(new OnClickListener(){

        		public void onClick(View v) {
        			System.exit(1);
        		}

        	});
        	IntentFilter filter = new IntentFilter (Intent.ACTION_MEDIA_MOUNTED); 
    		filter.addDataScheme("file"); 
    		registerReceiver(this.mSDInfoReceiver, new IntentFilter(filter));
        }
    }
    
    private BroadcastReceiver mSDInfoReceiver = new BroadcastReceiver(){
	    @Override
	    public void onReceive(Context arg0, Intent intent) {
	    	intent = new Intent();
			intent.setClass(SdCardError.this,BrowserActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
	    }
	 }; 
}
