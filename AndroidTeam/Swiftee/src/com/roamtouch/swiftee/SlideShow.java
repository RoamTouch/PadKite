package com.roamtouch.swiftee;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ViewFlipper;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SlideShow extends Activity implements OnGestureListener, OnClickListener, OnCheckedChangeListener{
	
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private ViewFlipper flipper;
	private GestureDetector gestureScanner;

	boolean dont_start = false;
	
	Button btn_skip;
	CheckBox chk_dntShowAgn;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

//		SharedPreferences.Editor editor = sharedPref.edit();
//		editor.putBoolean("DontShowAgain", false);
//		editor.commit();

		dont_start = sharedPref.getBoolean("DontShowAgain", false);

		if(dont_start){
			Intent intent = new Intent();
			intent.setClass(this,BrowserActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
		}
		else{
			setContentView(R.layout.slideshow);
			gestureScanner = new GestureDetector(this);
			flipper = (ViewFlipper) findViewById(R.id.flipper);
			
			btn_skip = (Button) findViewById(R.id.skip);
			btn_skip.setOnClickListener(this);

			chk_dntShowAgn = (CheckBox) findViewById(R.id.dont_show_again);
			chk_dntShowAgn.setOnCheckedChangeListener(this);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent me)
	{
		return gestureScanner.onTouchEvent(me);
	}
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		///ani_slide_in_left = AnimationUtils.loadAnimation(this, R.anim.slide_in_left); 
		//ani_slide_in_right = AnimationUtils.loadAnimation(this, R.anim.slide_in_right); 
		
		try {
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
				return false;
			// right to left swipe
			if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				flipper.setInAnimation(AnimationUtils.loadAnimation(this,
			             R.anim.slide_in_left));
			     flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
			             R.anim.slide_out_left));
				flipper.showNext();
			}  
			else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				 flipper.setInAnimation(AnimationUtils.loadAnimation(this,
				            R.anim.slide_in_right));
				 flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
			                R.anim.slide_out_right));
				flipper.showPrevious();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;		
	}

	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return true;
	}

	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return true;
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
}
