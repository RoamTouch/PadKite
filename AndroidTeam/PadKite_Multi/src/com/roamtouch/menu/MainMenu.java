package com.roamtouch.menu;

import com.roamtouch.database.DBConnector;
import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.settings.GestureRecorder;
import com.roamtouch.settings.RegisterActivity;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.swiftee.TrackHelper;
import com.roamtouch.view.EventViewerArea;
import com.roamtouch.view.WebPage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.util.Log;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
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
	bookmark,
	help_online
}

public class MainMenu extends CircularLayout implements OnTouchListener{

	private FloatingCursor mFloatingCursor;
	private BrowserActivity mParent;
	public static boolean USER_REGISTERED = true;
	private DBConnector database;
	private MenuButton button,backButton,fwdButton;
	public static String PATH = BrowserActivity.THEME_PATH + "/";
	
//	private EventViewerArea eventViewer;
	 
	public MainMenu(Context context) {
		super(context);
		
		this.setName("Main Menu");
		
		//LayoutInflater.from(context).inflate(R.layout.main_menu, this);
		MenuInflater.inflate(PATH + "main_menu.xml", context, this);
		
		SwifteeApplication appState = ((SwifteeApplication)context.getApplicationContext());
    	database = appState.getDatabase();
    	
		init();
		
		int count = getChildCount();
		for(int i=0;i<count;i++ ){
			
			View v = getChildAt(i);
			v.setId(i);
			v.setOnTouchListener(this);
			
			if ((v instanceof MenuButton)){
				MenuButton b = (MenuButton) v;
				if(b.getFunction().equals("refresh"))
					button = b;
				if(b.getFunction().equals("backward"))
					backButton = b;
				if(b.getFunction().equals("forward"))
					fwdButton = b;
			}
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
	public void toggleCloseORRefresh(boolean isRefresh){
		if(button == null)
			return;
		if(isRefresh){
			button.setDrawables(PATH+"refresh_normal.png",PATH+"refresh_pressed.png");
			button.setFunction("refresh");
		}
		else{
			button.setDrawables(PATH+"stop_normal.png",PATH+"stop_pressed.png");
			button.setFunction("stop");
		}
	}
	public boolean onTouch(View v, MotionEvent event) {

		if (!(v instanceof MenuButton))
				return false;

		MenuButton b = (MenuButton)v;

		String function = b.getFunction();
		
//		Log.d("MainMenu", "Function = " + function);
		
		MainMenuFunctions button_function = null;
		
		try {
			button_function = MainMenuFunctions.valueOf(function);
		}
		catch (Exception e)
		{
			button_function = MainMenuFunctions.none;
		}
		
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
				
			case help_online:
				mFloatingCursor.setEventText("Help Online");
				break;

			case new_window:
				mFloatingCursor.setEventText("New Window");
				break;
					
			default:
				mFloatingCursor.setEventText("No function defined for: " + b.getFunction());
				break;
			}
		}
		if(event.getAction() == MotionEvent.ACTION_UP){			
			mParent.setEventViewerMode(EventViewerArea.TEXT_ONLY_MODE);

			String policy = b.getPolicy();
			
			if (policy.equals("keep")) { // Keep current menu opened
				;
			}
			else /* policy == "close" or "none" */
			{
				mFloatingCursor.hideMenuFast();
			}
			
			switch(button_function){
			
			//Settings
			case settings:
				mFloatingCursor.setCurrentMenu(1);

				/*boolean is_registered = database.checkUserRegistered();

				if(is_registered)
					mFloatingCursor.setCurrentMenu(1);
				else{
					Intent i = new Intent(mParent,RegisterActivity.class);
					mParent.startActivity(i);
				}*/
				break;
				
			//Refresh
			case refresh:
				mParent.refreshWebView();
				break;
				
			//Stop
			case stop:
				mFloatingCursor.setEventText("Stop ");
				mFloatingCursor.stopLoading();
				break;
				
			//Zoom
			case zoom:
				//mFloatingCursor.disableCircularZoom();
				mFloatingCursor.enableCircularZoom(policy.equals("keep"));
				break;
					
			//Find text
			case homepage:
				SharedPreferences sharedPreferences = mParent.getApplicationContext().getSharedPreferences("Shared_Pref_AppSettings", Context.MODE_WORLD_READABLE);
				String url = sharedPreferences.getString("home_page","http://www.padkite.com");
				mFloatingCursor.loadPage(url);				
				break;
			//Windows	
			case windows:
				mFloatingCursor.setCurrentMenu(2);
				//mParent.setEventViewerMode(EventViewerArea.WINDOWS_MODE);
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
				mParent.setGestureType(SwifteeApplication.BOOKMARK_GESTURE);
				mParent.startGesture(false);
				break;
				
			case download:
				//Intent i = new Intent(mParent,WebPage.class);
				//i.putExtra("webUrl", "http://www.padkite.com/downloads");
				//mParent.startActivity(i);
				mFloatingCursor.loadPage("file:///android_asset/Web Pages/download.html");
				//WebPage page = new WebPage();
				//mFloatingCursor.loadData(page.getDownLoadHistory(mParent));
				break;
			
			case bookmark:
				Intent i = new Intent(mParent,GestureRecorder.class);
				i.putExtra("Gesture_Name",  mFloatingCursor.getCurrentTitle());
				i.putExtra("url", mFloatingCursor.getCurrentURL());
				i.putExtra("isNewBookmark", true);
				i.putExtra("Gesture_Type", SwifteeApplication.BOOKMARK_GESTURE);
				mParent.startActivity(i);
				break;
			
			case help_online:
				if(mFloatingCursor.getWindowCount()>7){
					mFloatingCursor.loadPage("file:///android_asset/Web Pages/help.html");
				} else {
					mFloatingCursor.addNewWindow("file:///android_asset/Web Pages/help.html");
				}
				break;
				
			case new_window:
				TrackHelper.doTrack(TrackHelper.NEW_PAGE, 1);
				mFloatingCursor.addNewWindow(false);
				break;
				
			case close:
				mParent.closeDialog();
				break;
				
			default:
				/* Do nothing */
				break;
			}
		}
		return false;
	}
	public void setBackEabled(boolean b){
		if(backButton!=null)
			backButton.setEnabled(b);
	}
	public void setFwdEabled(boolean b){
		if(fwdButton!=null)
			fwdButton.setEnabled(b);
	}
}
