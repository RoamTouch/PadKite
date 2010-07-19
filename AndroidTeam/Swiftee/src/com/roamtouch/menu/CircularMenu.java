package com.roamtouch.menu;

import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.settings.MiscListActivity;
import com.roamtouch.settings.RegisterActivity;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import com.roamtouch.view.TopBarArea;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class CircularMenu extends CircularLayout implements OnClickListener{

	private FloatingCursor mFloatingCursor;
	private BrowserActivity mParent;
	public static boolean USER_REGISTERED = false;
	
	public CircularMenu(Context context) {
		super(context);
		
		LayoutInflater.from(context).inflate(R.layout.main_menu, this);
		
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

			case R.id.settings:{
				if(USER_REGISTERED)
					mFloatingCursor.setCurrentMenu(1);
				else{
					Intent i = new Intent(mParent,RegisterActivity.class);
					mParent.startActivity(i);
				}
				break;
			}
			case R.id.findtext:{
				mParent.setTopBarMode(TopBarArea.SEARCH_BAR_MODE);
				this.setVisibility(INVISIBLE);
				break;
			}
			case R.id.refresh:{
				mParent.refreshWebView();
				break;
			}
			case R.id.stop:break;
			case R.id.zoom:break;
			case R.id.resizeHit:break;
			case R.id.windows:
				mFloatingCursor.setCurrentMenu(2);
				break;
			case R.id.bookmark:
				mParent.startGesture();
				mParent.initGestureLibrary(BrowserActivity.BOOKMARK_GESTURES);
				break;
			
		}
		
	}

}
