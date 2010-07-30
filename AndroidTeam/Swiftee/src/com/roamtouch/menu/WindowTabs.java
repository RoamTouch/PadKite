package com.roamtouch.menu;

import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.settings.MiscListActivity;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class WindowTabs extends CircularTabsLayout implements OnClickListener{

	private FloatingCursor mFloatingCursor;
	private BrowserActivity mParent;
	private Context mContext;
	private TabControl tabControl;
	
	public WindowTabs(Context context) {
		super(context);
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.window_tabs, this);
		
		setMode(CircularLayout.DYNAMIC_MODE);
		int count = getChildCount();
		for(int i=0;i<count;i++ ){
			View v = getChildAt(i);
			v.setOnClickListener(this);
		}
		
	}


	public void setFloatingCursor(FloatingCursor mFloatingCursor) {
		this.mFloatingCursor = mFloatingCursor;
	}
	public void setParent(BrowserActivity parent){
		mParent = parent;
		tabControl = new TabControl(mParent);
		tabControl.setWindowTabsView(this);
	}
	public void onClick(View v) {
		int id=v.getId();
		int count = getChildCount();
		if(v == getChildAt(count-1)){
			TabButton but = new TabButton(mContext);
			but.setBackgroundResource(R.drawable.settings_btn);
			//but.setDrawables(drawableStr, selectDrawableStr);
			addView(but,1);
		}
		
		switch(id){

		case 0: removeViewAt(getActiveTabIndex());
			
		}
		
	}
}
