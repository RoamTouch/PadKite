package com.roamtouch.swiftee;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.util.Timer;
import java.util.TimerTask;

public class HowToActivity extends Activity implements  OnClickListener, OnCheckedChangeListener
{
	

	Button btn_skip;
	CheckBox chk_dntShowAgn;
	boolean dont_start = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
       
        
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		dont_start = sharedPref.getBoolean("DontShowAgain", false);
		
		dont_start = true;

		if(dont_start){
			Intent intent = new Intent();
			intent.setClass(this,BrowserActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
		}
		else{
			 setContentView(R.layout.tutorial);
		        

		        ImageView img = (ImageView)findViewById(R.id.simple_anim);
		        img.setBackgroundResource(R.anim.simple_animation);

		        
		        MyAnimationRoutine mar = new MyAnimationRoutine();
		        MyAnimationRoutine2 mar2 = new MyAnimationRoutine2();
		        
		        Timer t = new Timer(false);
		        t.schedule(mar, 100);
		        Timer t2 = new Timer(false);
		        t2.schedule(mar2, 5000);
			
			btn_skip = (Button) findViewById(R.id.skip);
			btn_skip.setOnClickListener(this);

			chk_dntShowAgn = (CheckBox) findViewById(R.id.dont_show_again);
			chk_dntShowAgn.setOnCheckedChangeListener(this);
		}
        
    }

    public void onClick(View v) {
		if(v == btn_skip){
			Intent intent = new Intent();
			intent.setClass(this,BrowserActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
		}		
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putBoolean("DontShowAgain", true);
			editor.commit();
		}
		else{
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putBoolean("DontShowAgain", false);
			editor.commit();
		}		
	}
    class MyAnimationRoutine extends TimerTask
    {
    	MyAnimationRoutine()
    	{
    	}
    	
    	
    	public void run()
    	{
        	ImageView img = (ImageView)findViewById(R.id.simple_anim);
            // Get the background, which has been compiled to an AnimationDrawable object.
            AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

            // Start the animation (looped playback by default).
            frameAnimation.start();
    	}
    }


    class MyAnimationRoutine2 extends TimerTask
    {
    	MyAnimationRoutine2()
    	{
    	}
    	
    	
    	public void run()
    	{
        	ImageView img = (ImageView)findViewById(R.id.simple_anim);
            // Get the background, which has been compiled to an AnimationDrawable object.
            AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

            // stop the animation (looped playback by default).
            frameAnimation.stop();
    	}
    }





}



