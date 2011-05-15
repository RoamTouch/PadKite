//******************************************************************************** 
//**	Copyright (c) 2011, Roaming Keyboards LLC doing business as RoamTouch®	**	       
//**	All rights reserved.													**
//********************************************************************************
package com.roamtouch.menu;

import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.settings.BrowserSettingActivity;
import com.roamtouch.settings.GestureEditor;
import com.roamtouch.settings.MiscListActivity;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.view.WebPage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;

enum SettingsMenuFunctions {
	none,
	browser_settings,
	set_homepage,
	resize_hitarea,
	gesture_kit_editor,
	miscellaneous,
	help_online,
	download,
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
				mFloatingCursor.setEventText("Help onlin");
				break;
			
			//History	
			case history:
				mFloatingCursor.setEventText("History");
				break;
			
			// Download
			case download:
				mFloatingCursor.setEventText("Download");
				break;	
				
			default:
				mFloatingCursor.setEventText("No function defined for: " + b.getFunction());
				break;
		}
		}
		if(event.getAction() == MotionEvent.ACTION_UP){

			String policy = b.getPolicy();
			
			if (policy.equals("keep")) { // Keep current menu opened
				;
			}
			else /* policy == "close" or "none" */
			{
				mFloatingCursor.hideMenuFast();
			}
			
			switch(button_function){
			
			//Browser Settings
			case browser_settings:
				Intent i = new Intent(mParent,BrowserSettingActivity.class);
				mParent.startActivity(i);
				break;
				
			//Set home page
			case set_homepage:
				AlertDialog alertDialog = new AlertDialog.Builder(mParent).create();
				alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			    alertDialog.setMessage("Do you want to set "+mFloatingCursor.getCurrentURL()+" as your home page?");
			    alertDialog.setTitle("Set home page");
			    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			    	  	SharedPreferences sharedPreferences = mParent.getApplicationContext().getSharedPreferences("Shared_Pref_AppSettings", Context.MODE_WORLD_READABLE);
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putString("home_page",mFloatingCursor.getCurrentURL());
						editor.commit();
						return;

			    } }); 
			    alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
				      public void onClick(DialogInterface dialog, int which) {
				    	  	//alertDialog.cancel();
							return;

				    } }); 
			  	alertDialog.show();			
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
				//WebPage page = new WebPage();
				//mFloatingCursor.loadData(page.getBrowserHistory(mParent));				
				break;
			
			case download:
				//Intent i = new Intent(mParent,WebPage.class);
				//i.putExtra("webUrl", "http://www.padkite.com/downloads");
				//mParent.startActivity(i);
				mFloatingCursor.loadPage("file:///android_asset/Web Pages/download.html");
				//WebPage page = new WebPage();
				//mFloatingCursor.loadData(page.getDownLoadHistory(mParent));
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
