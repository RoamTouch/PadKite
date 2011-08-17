package com.roamtouch.settings;

import com.roamtouch.menu.CircularLayout;
import com.roamtouch.menu.MenuButton;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingsTouchEvents {

		static Context mContext;
		
		public static void addTouchEvents(Context c,CircularLayout settingsMenu){
			
			mContext = c;
			int count = settingsMenu.getChildCount();
			ClickListener listener = new ClickListener();
			
			for(int i=0;i<count-2;i++){
				MenuButton b=(MenuButton)settingsMenu.getChildAt(i);
				b.setOnClickListener(listener);
			}
		
		}
		public static class ClickListener implements OnClickListener{
			
			public void onClick(View v) {
				Intent i = new Intent(mContext,MiscListActivity.class);
				mContext.startActivity(i);
			}
		}
}
