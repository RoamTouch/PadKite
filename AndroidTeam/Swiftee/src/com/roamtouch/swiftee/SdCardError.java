package com.roamtouch.swiftee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SdCardError extends Activity{

	private Handler handler;
	private Runnable runnable;
	SwifteeApplication appState;
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sdcard_error);
        
        appState = ((SwifteeApplication)getApplicationContext());
        
        Button b = (Button) findViewById(R.id.quit);
        b.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				System.exit(1);
			}
        	
        });
        
        handler = new Handler();
        runnable = new Runnable(){

			public void run() {
				if(appState.isSdCardReady()){
					Intent intent = new Intent();
					intent.setClass(SdCardError.this,BrowserActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					finish();
				}
				else
				{
					handler.post(runnable);
				}
			}
        };
        handler.post(runnable);
    }
}
