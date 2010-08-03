package com.roamtouch.menu;

import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.settings.GestureEditor;
import com.roamtouch.settings.MiscListActivity;
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

public class SettingsMenu extends CircularLayout implements OnTouchListener{

	private FloatingCursor mFloatingCursor;
	private BrowserActivity mParent;
	
	public SettingsMenu(Context context) {
		super(context);

		//LayoutInflater.from(context).inflate(R.layout.settings_menu, this);
		MenuInflater.inflate("/sdcard/Swiftee/Default Theme/settings_menu.xml", context, this);
		
		setMode(CircularLayout.STATIC_MODE);
		
		int count = getChildCount();
		for(int i=0;i<count-1;i++ ){
			View v = getChildAt(i);
			v.setId(i);
			v.setOnTouchListener(this);
		}
	}


	public void setFloatingCursor(FloatingCursor mFloatingCursor) {
		this.mFloatingCursor = mFloatingCursor;
	}
	public void setParent(BrowserActivity parent){
		mParent = parent;
	}
	public void onClick(View v) {
		int id=v.getId();
		switch(id){

			case R.id.browsersettings:break;
			case R.id.setHome:break;
			case R.id.resizeHit:break;
			case R.id.gestureEditor:{
				
			}
			case R.id.misc:{
				Intent i = new Intent(mParent,MiscListActivity.class);
				mParent.startActivity(i);
				break;
			}
			case R.id.onlineHelp:{
				
				break;
			}
			case R.id.practiceGesture:{
						
				break;
			}
			
		}
		
	}
	public boolean onTouch(View v, MotionEvent event) {
		int id=v.getId();
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			switch(id){
			//Browser Settings
			case 0:
				mFloatingCursor.setEventText("Browser Settings ");
				break;
				
			//Set home page
			case 1:
				mFloatingCursor.setEventText("Set home page ");
				break;
				
			//Resize hit area
			case 2:
				mFloatingCursor.setEventText("Resize hit area ");
				break;
				
			//Gesture kit editor
			case 3:
				mFloatingCursor.setEventText("Gesture kit editor ");
				break;
					
			//Miscellaneous
			case 4:
				mFloatingCursor.setEventText("Miscellaneous ");
				break;
			
			//Help online	
			case 5:
				mFloatingCursor.setEventText("Help online	");
				break;
			
			//Practice gesture	
			case 6:
				mFloatingCursor.setEventText("Practice gesture");
				break;
		}
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			switch(id){
			
			//Browser Settings
			case 0:
				
				break;
				
			//Set home page
			case 1:
				break;
				
			//Resize hit area
			case 2:
				break;
				
			//Gesture kit editor
			case 3:
				Intent i = new Intent(mParent,GestureEditor.class);
				mParent.startActivity(i);
				break;			
			//Miscellaneous
			case 4:
				i = new Intent(mParent,MiscListActivity.class);
				mParent.startActivity(i);
				break;
			
			//Help online	
			case 5:
				break;
			
			//Practice gesture	
			case 6:				
				break;
		}
		}
		return false;
	}

}
