package com.roamtouch.menu;

import com.roamtouch.database.DBConnector;
import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.settings.RegisterActivity;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.view.TopBarArea;
import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MainMenu extends CircularLayout implements OnTouchListener{

	private FloatingCursor mFloatingCursor;
	private BrowserActivity mParent;
	public static boolean USER_REGISTERED = true;
	private DBConnector database;
	 
	public MainMenu(Context context) {
		super(context);
		
		//LayoutInflater.from(context).inflate(R.layout.main_menu, this);
		MenuInflater.inflate("/sdcard/Swiftee/Default Theme/main_menu.xml", context, this);
		
		SwifteeApplication appState = ((SwifteeApplication)context.getApplicationContext());
    	database = appState.getDatabase();
    	
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

	public boolean onTouch(View v, MotionEvent event) {
		int id=v.getId();
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			switch(id){
			//Settings
			case 0:
				mFloatingCursor.setEventText("Settings ");
				break;
				
			//Refresh
			case 1:
				mFloatingCursor.setEventText("Refresh ");
				break;
				
			//Stop
			case 2:
				mFloatingCursor.setEventText("Stop ");
				break;
				
			//Zoom
			case 3:
				mFloatingCursor.setEventText("Circular zoom ");
				break;
					
			//Find text
			case 4:
				mFloatingCursor.setEventText("Find Text ");
				break;
			
			//Windows	
			case 5:
				mFloatingCursor.setEventText("Windows");
				break;
			
			//Bookmark	
			case 6:
				mFloatingCursor.setEventText("Bookmarks");
				break;
				
			//forward	
			case 7:
				mFloatingCursor.setEventText("Forward");
				break;
				
			//Custom Gesture	
			case 8:
				mFloatingCursor.setEventText("Custom Gesture");
				break;	
			
		}
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			switch(id){
			
			//Settings
			case 0:
				boolean b = database.checkUserRegistered();
				if(b)
					mFloatingCursor.setCurrentMenu(1);
				else{
					Intent i = new Intent(mParent,RegisterActivity.class);
					mParent.startActivity(i);
				}
				break;
				
			//Refresh
			case 1:
				mParent.refreshWebView();
				break;
				
			//Stop
			case 2:
				mFloatingCursor.setEventText("Stop ");
				break;
				
			//Zoom
			case 3:
				mFloatingCursor.enableCircularZoom();
				break;
					
			//Find text
			case 4:
				mParent.setTopBarMode(TopBarArea.SEARCH_BAR_MODE);
				this.setVisibility(INVISIBLE);
				break;
			
			//Windows	
			case 5:
				mFloatingCursor.setCurrentMenu(2);
				break;
			
			//Bookmark	
			case 6:
				this.setVisibility(INVISIBLE);
				mParent.startGesture(SwifteeApplication.BOOKMARK_GESTURE);
				break;
				
			//forward	
			case 7:
				mFloatingCursor.setEventText("Forward");
				break;
				
			//Custom Gesture	
			case 8:
				this.setVisibility(INVISIBLE);
				mParent.startGesture(SwifteeApplication.CUSTOM_GESTURE);
				break;	
		}
		}
		return false;
	}

}
