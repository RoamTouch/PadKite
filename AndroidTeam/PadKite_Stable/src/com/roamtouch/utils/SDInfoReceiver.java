package com.roamtouch.utils;

import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.SdCardError;
import com.roamtouch.swiftee.SwifteeApplication;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.Window;

import com.roamtouch.swiftee.BrowserActivity;

public class SDInfoReceiver extends BroadcastReceiver {
	
		private BrowserActivity mP;		
		
		public static final int SDCardRequestCode = 100050;
	
		public SDInfoReceiver(BrowserActivity browserActivity ) {
			this.mP = browserActivity;			
		}
		
		private Handler mHandler = new Handler();
	
		Context cont;
		
	   @Override
	    public void onReceive(Context arg0, Intent intent) { 	
	    	
		   AlertDialog alertDialog;
		   alertDialog = new AlertDialog.Builder(mP).create();
			alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		    alertDialog.setMessage("SD Card is not available or write protected. Please insert the SD Card or unmount it from USB.");
		    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		    	  mP.finish();  

		    } }); 
		    alertDialog.show();		  	

	    	mHandler.post(new Runnable() {
	    	
	    		public void run() {
	    			mP.startSDCardIntent();
	    		}
	    	});		
   	
	    	
	    	}    	
	    	

}
