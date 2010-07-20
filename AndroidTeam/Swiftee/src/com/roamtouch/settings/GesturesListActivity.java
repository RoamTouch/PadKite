package com.roamtouch.settings;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.TextView;

import com.roamtouch.swiftee.R;

public class GesturesListActivity extends Activity{

	
		private int gestureCategory;
		
		private final int CURSOR_OVER_TEXT = 1;
		private final int CURSOR_OVER_LINK = 2;
		private final int CURSOR_OVER_IMAGE = 3;
		private final int CURSOR_OVER_NOTARGET = 4;
		private final int CURSOR_OVER_VIDEO = 5;
		private final int CUSTOM_GESTURE = 7;
		private final int BOOKMARK_GESTURE = 8;
		
	
	  	/** Called when the activity is first created. */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
  	
			gestureCategory = getIntent().getIntExtra("Gesture_Category", -1);		
			setContentView(R.layout.gesture_editor);
      
			ListView list = (ListView) this.findViewById(R.id.gesturesList);
			
			TextView tv = new TextView(this);
	    	tv.setHeight(60);
	    	tv.setPadding(5, 0, 0, 0);
			tv.setText("Cursor over text");
			tv.setTextColor(Color.WHITE);		
			tv.setBackgroundColor(Color.BLACK);
			tv.setGravity(Gravity.CENTER_VERTICAL);
			
			list.addHeaderView(tv);
			list.setAdapter(new GestureAdapter(this));
			
			
		}
		

}
