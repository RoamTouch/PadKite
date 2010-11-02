package com.roamtouch.menu;

import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.settings.BrowserSettingActivity;
import com.roamtouch.settings.GestureEditor;
import com.roamtouch.settings.MiscListActivity;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.view.WebPage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

enum SettingsMenuFunctions {
	none,
	browser_settings,
	set_homepage,
	resize_hitarea,
	gesture_kit_editor,
	miscellaneous,
	help_online,
	history,
}

public class SettingsMenu extends CircularLayout implements OnTouchListener{
	
	private FloatingCursor mFloatingCursor;
	private BrowserActivity mParent;
	
	public SettingsMenu(Context context) {
		super(context);

		this.setName("Settings Menu");
		
		//LayoutInflater.from(context).inflate(R.layout.settings_menu, this);
		MenuInflater.inflate(BrowserActivity.THEME_PATH + "/settings_menu.xml", context, this);
		
		init();
		
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
		
		if (!(v instanceof MenuButton))
			return false;

		MenuButton b = (MenuButton)v;

		String function = b.getFunction();
		
		SettingsMenuFunctions button_function = null;
		
		try {
			button_function = SettingsMenuFunctions.valueOf(function);
		}
		catch (Exception e)
		{
			button_function = SettingsMenuFunctions.none;
		}
	
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			switch(button_function){
			//Browser Settings
			case browser_settings:
				mFloatingCursor.setEventText("Browser Settings ");
				break;
				
			//Set home page
			case set_homepage:
				mFloatingCursor.setEventText("Set home page ");
				break;
				
			//Resize hit area
			case resize_hitarea:
				mFloatingCursor.setEventText("Resize hit area ");
				break;
				
			//Gesture kit editor
			case gesture_kit_editor:
				mFloatingCursor.setEventText("Gesture kit editor ");
				break;
					
			//Miscellaneous
			case miscellaneous:
				mFloatingCursor.setEventText("Miscellaneous ");
				break;
			
			//Help online	
			case help_online:
				mFloatingCursor.setEventText("Help online	");
				break;
			
			//History	
			case history:
				mFloatingCursor.setEventText("History");
				break;
				
			default:
				mFloatingCursor.setEventText("No function defined for: " + b.getFunction());
				break;
		}
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			switch(button_function){
			
			//Browser Settings
			case browser_settings:
				Intent i = new Intent(mParent,BrowserSettingActivity.class);
				mParent.startActivity(i);
				break;
				
			//Set home page
			case set_homepage:
				SharedPreferences sharedPreferences = mParent.getApplicationContext().getSharedPreferences("Shared_Pref_AppSettings", Context.MODE_WORLD_READABLE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("home_page",mFloatingCursor.getCurrentURL());
				editor.commit();
				break;
				
			//Resize hit area
			case resize_hitarea:
				break;
				
			//Gesture kit editor
			case gesture_kit_editor:
				i = new Intent(mParent,GestureEditor.class);
				mParent.startActivity(i);
				break;

			//Miscellaneous
			case miscellaneous:
				i = new Intent(mParent,MiscListActivity.class);
				mParent.startActivity(i);
				break;
			
			//Help online	
			case help_online:
				mFloatingCursor.loadPage("file:///android_asset/Web Pages/help.html");
				break;
			
			//Practice gesture	
			case history:		
				mFloatingCursor.loadPage("file:///android_asset/Web Pages/history.html");
				break;

			case none:
			default:
				// Nothing to be done
				break;
		}
		}
		return false;
	}
}
