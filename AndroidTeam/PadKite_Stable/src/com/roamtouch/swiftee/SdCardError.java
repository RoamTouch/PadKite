package com.roamtouch.swiftee;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SdCardError extends Activity implements Runnable {

	SwifteeApplication appState;
	private boolean isAppLaunched = false;
	private int mNumWindows = 0;
	private Handler mHandler = new Handler();

	public void exitApp()
	{
        Intent i = new Intent();
        i.putExtra("quit", true);
        
        setResult(RESULT_OK, i);
        finish();
	}
	
	public void exitToApp()
	{
	  	if(isAppLaunched){
    		finish();
    	}
    	else{
    		Intent intent = new Intent();
    		intent.setClass(SdCardError.this,BrowserActivity.class);
    		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		startActivity(intent);
    		finish();
    	}
	}
	
	 public void closeDialog()
	    {
			AlertDialog alertDialog;

	    	alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		    alertDialog.setMessage("You got " + mNumWindows + " open windows left. Do you really want to quit?");
		    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		    	//mParent.finish();  
		        exitApp();

		    } }); 
		    alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		        return;
		    }}); 			  
		  	alertDialog.show();
	    }
	 
	 public void showCloseDialog() {
			if (mNumWindows > 0)
				closeDialog();
			else
				exitApp();
	 }
	 
	 public boolean onKeyDown(int keyCode, android.view.KeyEvent event){
	        
	    	if(keyCode == KeyEvent.KEYCODE_BACK){
	    		showCloseDialog();
	    	}
	   		return false;
	 }
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.sdcard_error);
        
        isAppLaunched = getIntent().getBooleanExtra("isAppLaunched", false);
        mNumWindows = getIntent().getIntExtra("numWindows", 0);
        
        appState = ((SwifteeApplication)getApplicationContext());
        
        if(appState.isSdCardReady()){
        	exitToApp();
		}
        else{
        	Button b = (Button) findViewById(R.id.quit);
        	b.setOnClickListener(new OnClickListener(){

        		public void onClick(View v) {
        			showCloseDialog();
        		}
        	});
        	
        	IntentFilter filter = new IntentFilter (Intent.ACTION_MEDIA_MOUNTED); 
    		filter.addDataScheme("file"); 
    		registerReceiver(this.mSDInfoReceiver, new IntentFilter(filter));
    		mBroadcastReceiverRegistered = true;
    		
    		/* Also check manually */
    		mHandler.postDelayed(this, 2000);
        }
    }
    
    public void run() {
        if(appState.isSdCardReady()){
        	exitToApp();
        }
        else {
    		mHandler.postDelayed(this, 2000);
        }
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	if (mBroadcastReceiverRegistered)
    		unregisterReceiver(mSDInfoReceiver);
    }
    
    private boolean mBroadcastReceiverRegistered = false;
    
    private BroadcastReceiver mSDInfoReceiver = new BroadcastReceiver(){
	    @Override
	    public void onReceive(Context arg0, Intent intent) {
	    	exitToApp();
	    }
	 }; 
}
