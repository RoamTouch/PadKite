package com.roamtouch.menu;

import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import com.roamtouch.view.TopBarArea;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class CircularMenu extends CircularLayout implements OnClickListener{

	private FloatingCursor mFloatingCursor;
	private BrowserActivity mParent;
	
	public CircularMenu(Context context) {
		super(context);
		
		LayoutInflater.from(context).inflate(R.layout.main_menu, this);
		
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
				mFloatingCursor.setCurrentMenu(1);
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
			case R.id.windows:break;
			
		}
		
	}

}
