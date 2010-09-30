package com.roamtouch.menu;

import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.settings.RegisterActivity;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.view.TopBarArea;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class CircularMenu extends CircularLayout implements OnTouchListener{

	private FloatingCursor mFloatingCursor;
	private BrowserActivity mParent;
	public static boolean USER_REGISTERED = true;
	
	public CircularMenu(Context context) {
		super(context);
		
		LayoutInflater.from(context).inflate(R.layout.main_menu, this);
		
		//setMode(CircularLayout.STATIC_MODE);
		
		int count = getChildCount();
		for(int i=0;i<count-1;i++ ){
			View v = getChildAt(i);
			v.setOnTouchListener(this);
		}
	}


	public void setFloatingCursor(FloatingCursor mFloatingCursor) {
		this.mFloatingCursor = mFloatingCursor;
	}

	public void setParent(BrowserActivity parent){
		mParent = parent;
	}

	public void onTouch(View v) {
		int id=v.getId();
		switch(id){

			case R.id.settings:{
				if(USER_REGISTERED)
					mFloatingCursor.setCurrentMenu(1);
				else{
					Intent i = new Intent(mParent,RegisterActivity.class);
					mParent.startActivity(i);
				}
				break;
			}
			case R.id.findtext:{
				mParent.setTopBarMode(TopBarArea.SEARCH_BAR_MODE);
				this.setVisibility(INVISIBLE);
				break;
			}
			case R.id.refresh:{
				mParent.refreshWebView();
				break;
			}
			case R.id.stop:break;
			case R.id.zoom:
				
				mFloatingCursor.enableCircularZoom();
				break;
			case R.id.resizeHit:break;
			case R.id.windows:
				mFloatingCursor.setCurrentMenu(2);
				break;
			case R.id.bookmark:
				this.setVisibility(INVISIBLE);
				mParent.startGesture(SwifteeApplication.BOOKMARK_GESTURE, false);
				break;
			case R.id.customGesture:
				this.setVisibility(INVISIBLE);
				mParent.startGesture(SwifteeApplication.CUSTOM_GESTURE, false);
				break;	
			
		}
		
	}


	public boolean onTouch(View v, MotionEvent event) {
		int id=v.getId();
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			switch(id){

			case R.id.settings:
				mFloatingCursor.setEventText("Settings button");
				break;
			
			case R.id.findtext:
				mFloatingCursor.setEventText("Find Text button");
				break;
			
			case R.id.refresh:
				mFloatingCursor.setEventText("Refresh button");
				break;
			
			case R.id.stop:
				mFloatingCursor.setEventText("Stop button");
				break;
				
			case R.id.zoom:
				mFloatingCursor.setEventText("Circular zoom button");
				break;
				
			case R.id.resizeHit:
				mFloatingCursor.setEventText("Resize hit area button");
				break;
				
			case R.id.windows:
				mFloatingCursor.setEventText("Find Text Button");
				break;
				
			case R.id.bookmark:
				mFloatingCursor.setEventText("Find Text Button");
				break;
				
			case R.id.customGesture:
				mFloatingCursor.setEventText("Find Text Button");
				break;	
			
		}
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			switch(id){

			case R.id.settings:{
				if(USER_REGISTERED)
					mFloatingCursor.setCurrentMenu(1);
				else{
					Intent i = new Intent(mParent,RegisterActivity.class);
					mParent.startActivity(i);
				}
				break;
			}
			case R.id.findtext:{
				mParent.setTopBarMode(TopBarArea.SEARCH_BAR_MODE);
				this.setVisibility(INVISIBLE);
				break;
			}
			case R.id.refresh:{
				mParent.refreshWebView();
				break;
			}
			case R.id.stop:break;
			case R.id.zoom:				
				mFloatingCursor.enableCircularZoom();
				break;
			case R.id.resizeHit:break;
			case R.id.windows:
				mFloatingCursor.setCurrentMenu(2);
				break;
			case R.id.bookmark:
				this.setVisibility(INVISIBLE);
				mParent.startGesture(SwifteeApplication.BOOKMARK_GESTURE, false);
				break;
			case R.id.customGesture:
				this.setVisibility(INVISIBLE);
				mParent.startGesture(SwifteeApplication.CUSTOM_GESTURE, false);
				break;	
			
		}
		}
		return false;
	}

}
