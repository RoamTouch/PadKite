package com.roamtouch.settings;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;

public class GesturesListActivity extends Activity{

		private GestureAdapter adapter;
		private TextView tv;
		private int gestureCategory;
		private ListView list;
		
	  	/** Called when the activity is first created. */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
  	
			 getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			 
			gestureCategory = getIntent().getIntExtra("Gesture_Category", -1);		
			setContentView(R.layout.gesture_editor);
      
			list = (ListView) this.findViewById(R.id.gesturesList);
			
			tv = new TextView(this);
	    	tv.setHeight(60);
	    	tv.setPadding(5, 0, 0, 0);
			tv.setTextColor(Color.WHITE);		
			tv.setBackgroundColor(Color.BLACK);
			tv.setGravity(Gravity.CENTER_VERTICAL);
			
			setTitle(gestureCategory);
			
			list.addHeaderView(tv);
			
						
		}
		
		@Override
	    protected void onResume() {
	        super.onResume();
	        adapter = new GestureAdapter(this,gestureCategory,this);
	        list.setAdapter(adapter);
	    }
		
		
		public void refrshList(){
			adapter.notifyDataSetChanged();
		}
		public void setTitle(int gestureType){
			
			switch(gestureType){
			case 1:			
				tv.setText("Cursor over text");
				break;
			case 2:					
				tv.setText("Cursor over link");
				break;
			case 3:
				tv.setText("Cursor over image");
				break;
			case 4:
				tv.setText("Cursor over no target");
				break;
			case 5:
				tv.setText("Cursor over video");
				break;
			case 7:
				tv.setText("Bookmark gestures");
				break;
			case 8:
				tv.setText("Custom gestures");
				break;
			
			}
		}
		
		 public boolean onKeyDown(int keyCode, KeyEvent event){
		        
		    	if (keyCode == KeyEvent.KEYCODE_MENU && BrowserActivity.developerMode) { 
		    		adapter.addNewGesture();
		    		return false;
		    	}

		    	return super.onKeyDown(keyCode, event);
		 }
}
