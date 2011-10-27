package com.roamtouch.swiftee;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

public class LicenceAgreement extends Activity{
	
	boolean accepted = false;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aggrement);
        
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        accepted = sharedPref.getBoolean("isAccepted", false);
        if(accepted){
        	Intent intent = new Intent();
			intent.setClass(this,HowToActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
        }
        	
        WebView wv = (WebView) findViewById(R.id.webView);
        wv.loadUrl( "file:////" + BrowserActivity.WEB_PAGES_PATH + "/agreement.html");
        
        Button accept = (Button) findViewById(R.id.accept);
        Button decline = (Button) findViewById(R.id.decline);
        
     
        
        OnClickListener listener = new OnClickListener(){

			public void onClick(View v) {
				int id = v.getId();
				if(id == R.id.accept){
					SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LicenceAgreement.this);
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putBoolean("isAccepted", true);
					editor.commit();
					Intent intent = new Intent();
					intent.setClass(LicenceAgreement.this,HowToActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					finish();
				}
				else{
					SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LicenceAgreement.this);
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putBoolean("isAccepted", false);
					editor.commit();
					finish();
				}
			}
        	
        };
        accept.setOnClickListener(listener);
        decline.setOnClickListener(listener);
        
    }
}
