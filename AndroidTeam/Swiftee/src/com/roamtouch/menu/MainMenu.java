package com.roamtouch.menu;

import com.roamtouch.database.DBConnector;
import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.settings.GestureRecorder;
import com.roamtouch.settings.RegisterActivity;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.view.EventViewerArea;
import com.roamtouch.view.WebPage;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

enum MainMenuFunctions {
	none,
	settings,
	refresh,
	forward,
	stop,
	backward,
	zoom,
	homepage,
	windows,
	custom_gesture,
	history,
	download,
	close,
	new_window,
	bookmark
}

public class MainMenu extends CircularLayout implements OnTouchListener{

	private FloatingCursor mFloatingCursor;
	private BrowserActivity mParent;
	public static boolean USER_REGISTERED = true;
	private DBConnector database;
	private SwifteeApplication appState;
//	private EventViewerArea eventViewer;
	 
	public MainMenu(Context context) {
		super(context);
		
		this.setName("Main Menu");
		
		//LayoutInflater.from(context).inflate(R.layout.main_menu, this);
		MenuInflater.inflate("/sdcard/Swiftee/Default Theme/main_menu.xml", context, this);
		
		appState = ((SwifteeApplication)context.getApplicationContext());
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
	public void setEventViewer(EventViewerArea ev){
//		eventViewer = ev;
	}
	public boolean onTouch(View v, MotionEvent event) {

		if (!(v instanceof MenuButton))
				return false;

		MenuButton b = (MenuButton)v;
		
		String function = b.getFunction();
		
		Log.d("MainMenu", "Function = " + function);
		
		MainMenuFunctions button_function = MainMenuFunctions.valueOf(function);
		
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			switch(button_function) {
			//Settings
			case settings:
				mFloatingCursor.setEventText("Settings");
				break;
				
			//Refresh
			case refresh:
				mFloatingCursor.setEventText("Refresh");
				break;
				
			//Stop
			case stop:
				mFloatingCursor.setEventText("Stop");
				break;
				
			//Zoom
			case zoom:
				mFloatingCursor.setEventText("Circular Zoom");
				break;
					
			//Find text
			case homepage:
				mFloatingCursor.setEventText("Go to Home");
				break;
			
			//Windows	
			case windows:
				mFloatingCursor.setEventText("Windows");
				break;
										
			//Custom Gesture	
			case custom_gesture:
				mFloatingCursor.setEventText("Custom and Bookmark Gestures");
				break;	

			case download:
				mFloatingCursor.setEventText("Download");
				break;	

			case forward:
				mFloatingCursor.setEventText("Forward");
				break;	
				
			case backward:
				mFloatingCursor.setEventText("Backward");
				break;
				
			case close:
				mFloatingCursor.setEventText("Close Application");
				break;
				
			case bookmark:
				mFloatingCursor.setEventText("Bookmark");
				break;
			default:
				mFloatingCursor.setEventText("No function defined for: " + b.getFunction());
				break;
			}
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			mParent.setEventViewerMode(EventViewerArea.TEXT_ONLY_MODE);
			switch(button_function){
			
			//Settings
			case settings:
				boolean is_registered = database.checkUserRegistered();

				if(is_registered)
					mFloatingCursor.setCurrentMenu(1);
				else{
					Intent i = new Intent(mParent,RegisterActivity.class);
					mParent.startActivity(i);
				}
				break;
				
			//Refresh
			case refresh:
				mParent.refreshWebView();
				break;
				
			//Stop
			case stop:
				mFloatingCursor.setEventText("Stop ");
				break;
				
			//Zoom
			case zoom:
				//mFloatingCursor.disableCircularZoom();
				mFloatingCursor.enableCircularZoom();
				break;
					
			//Find text
			case homepage:
				mFloatingCursor.loadHomePage();
				//InputMethodManager imm = (InputMethodManager)((Activity)getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
				//imm.showSoftInput(eventViewer.getTextView(), InputMethodManager.SHOW_IMPLICIT);
				//imm.showSoftInput(eventViewer, InputMethodManager.SHOW_FORCED, new ResultReceiver(new Handler()));
				// FIXME: Remove
				//this.setVisibility(INVISIBLE);
				break;
			//Windows	
			case windows:
				mFloatingCursor.setCurrentMenu(2);
				mParent.setEventViewerMode(EventViewerArea.WINDOWS_MODE);
				break;
			
			//Backward	
			case backward:
				mFloatingCursor.goBackward();
				break;
				
			//forward	
			case forward:
				mFloatingCursor.goForward();
				break;
				
			//Custom Gesture	
			case custom_gesture:
				this.setVisibility(INVISIBLE);
				mParent.startGesture(SwifteeApplication.BOOKMARK_GESTURE, false);
				break;
				
			case download:
				Intent i = new Intent(mParent,WebPage.class);
				i.putExtra("webUrl", "http://www.padkite.com/downloads");
				mParent.startActivity(i);
				break;
			
			case bookmark:
				i = new Intent(mParent,GestureRecorder.class);
				i.putExtra("Gesture_Name", "Android");
				i.putExtra("isNewBookmark", true);
				i.putExtra("Gesture_Type", SwifteeApplication.BOOKMARK_GESTURE);
				mParent.startActivity(i);
				break;
			case close:
				System.exit(1);
				break;
			default:
				/* Do nothing */
				break;
			}
		}
		return false;
	}

}
