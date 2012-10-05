package com.roamtouch.splash;

import com.roamtouch.swiftee.R;

import android.app.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;


//http://www.xoriant.com/blog/mobile-application-development/android-async-task.html

public class SplashScreen extends Activity {

	 private LinearLayout linProgressBar;
	 private final Handler uiHandler=new Handler();
	 private boolean isUpdateRequired=false;
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
 
        basicInitializations();
    }
	 
	    public void basicInitializations(){
	 
	    	linProgressBar = (LinearLayout) findViewById(R.id.lin_progress_bar);
	        linProgressBar.setVisibility(View.VISIBLE);
	 
	        try {
	        	
	            new Thread(){
	                public void run() {
	                    initializeApp();
	                    uiHandler.post( new Runnable(){
	                        @Override
	                        public void run() {
	                            if(isUpdateRequired){
	                                //TODO:
	                            }else{
	                                linProgressBar.setVisibility(View.GONE);
	                                //startActivity( new Intent(WMXSplash.this, WMXLogin.class) );
	                                finish();
	                            }
	                        }
	                    } );
	                }
	                public void initializeApp(){
	                    // Initialize application data here
	                }
	        }.start();
	        }catch (Exception e) {}

	    }
}

