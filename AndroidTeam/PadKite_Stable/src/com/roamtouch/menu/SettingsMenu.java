//******************************************************************************** 
//**	Copyright (c) 2011, Roaming Keyboards LLC doing business as RoamTouch�	**	       
//**	All rights reserved.													**
//********************************************************************************
package com.roamtouch.menu;

import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.settings.BrowserSettingActivity;
import com.roamtouch.settings.GestureEditor;
import com.roamtouch.settings.GestureRecorder;
import com.roamtouch.settings.MiscListActivity;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.SwifteeApplication;
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
	bookmark_edit,
	browser_settings,
	set_homepage,
	resize_hitarea,
	gesture_kit_editor,
	miscellaneous,
	//help_online,
	download,
	history,
	back_menu
}

public class SettingsMenu extends CircularLayout implements OnTouchListener {
	
	private FloatingCursor mFloatingCursor;
	private BrowserActivity mParent;
	private MenuButton setHomePage;
	private MenuButton bookmarkEdit;
	
	
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
			
			if ((v instanceof MenuButton)){
				MenuButton b = (MenuButton) v;
				if(b.getFunction().equals("set_homepage"))
					setHomePage = b;				
				if(b.getFunction().equals("bookmark_edit"))
					bookmarkEdit = b;
			}
			
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
			
			//Edit Bookmark
			case bookmark_edit:
				Intent bE = new Intent(mParent,GestureRecorder.class);
				bE.putExtra("Gesture_Name",  mFloatingCursor.getCurrentTitle());
				bE.putExtra("url", mFloatingCursor.getCurrentURL());
				bE.putExtra("isNewBookmark", true);
				bE.putExtra("Gesture_Type", SwifteeApplication.BOOKMARK_GESTURE);
				mParent.startActivity(bE);
			break;	
			
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
			/*case help_online:
				mFloatingCursor.loadPage(BrowserActivity.WEB_PAGES_PATH + "/help.html");
				break;*/
			
			//Practice gesture	
			case history:		
				mFloatingCursor.loadPage( "file:////" + BrowserActivity.WEB_PAGES_PATH + "/history.html");
				mFloatingCursor.enableProgressBar();	
				break;
			
			case download:				
				mFloatingCursor.loadPage("file:////" + BrowserActivity.WEB_PAGES_PATH + "/download.html");
				mFloatingCursor.enableProgressBar();
				break;
				
			case back_menu:
				mFloatingCursor.setCurrentMenu(0);
				break;

			case none:
			default:
				// Nothing to be done
				break;
			}
		}
		return false;
	}
	
	public void setHomePageEnabled(boolean b){
		if(setHomePage!=null)
			setHomePage.setEnabled(b);
	};
	
	public void setBookmarkEdit(boolean b){
		if(bookmarkEdit!=null)
			bookmarkEdit.setEnabled(b);
	};
	
}
