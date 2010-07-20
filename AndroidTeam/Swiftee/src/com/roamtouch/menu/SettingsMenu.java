package com.roamtouch.menu;

import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.settings.GestureEditor;
import com.roamtouch.settings.MiscListActivity;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingsMenu extends CircularLayout implements OnClickListener{

	private FloatingCursor mFloatingCursor;
	private BrowserActivity mParent;
	
	public SettingsMenu(Context context) {
		super(context);

		LayoutInflater.from(context).inflate(R.layout.settings_menu, this);
		
		setMode(CircularLayout.STATIC_MODE);
		
		int count = getChildCount();
		for(int i=0;i<count-1;i++ ){
			View v = getChildAt(i);
			v.setOnClickListener(this);
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
				Intent i = new Intent(mParent,GestureEditor.class);
				mParent.startActivity(i);
				break;
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
}
