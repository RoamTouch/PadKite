package com.roamtouch.settings;


import com.roamtouch.swiftee.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GestureEditor extends Activity implements OnItemClickListener{

	boolean isForPracticeGesture;
	
	  /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        isForPracticeGesture = getIntent().getBooleanExtra("isForPracticeGesture", false);
        
        setContentView(R.layout.gesture_editor);
        ListView list = (ListView) this.findViewById(R.id.gesturesList);
        list.setOnItemClickListener(this);
    	//list.setSelector(R.drawable.cone);
        list.setAdapter(new GestureCategory(this));
    }
    
    public class GestureCategory extends BaseAdapter{

    	private int shiftPos = 1;
		Context mContext;	
		String[] cursorGestures,otherGesture; 
	
	/**
	 * Array storing gesture images 
	 */
		private int[] cursorgesturesImages= {R.drawable.text_cursor, R.drawable.link_cursor, R.drawable.image_cursor, 
									   R.drawable.no_target_cursor,R.drawable.video_cursor};
		
		private int[] othergesturesImages= {R.drawable.ge_bookmark,R.drawable.gesture ,R.drawable.bookmark };
		
		public GestureCategory(Context context){
			mContext=context;
			Resources res = mContext.getResources();
			cursorGestures = res.getStringArray(R.array.cursor_gestures_array);
			otherGesture = res.getStringArray(R.array.other_gestures_array);
		}
		public int getCount() {
			return cursorGestures.length + otherGesture.length +2;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			if(position == 0){
				TextView tv = new TextView(mContext);
		    	tv.setHeight(60);
		    	tv.setPadding(5, 0, 0, 0);
				tv.setText("Cursor Gestures");
				tv.setTextColor(Color.WHITE);		
				tv.setBackgroundColor(Color.BLACK);
				tv.setGravity(Gravity.CENTER_VERTICAL);
				return tv; 
			}
			if(position == cursorGestures.length+1){
				TextView tv = new TextView(mContext);
		    	tv.setHeight(60);
		    	tv.setPadding(5, 0, 0, 0);
				tv.setText("Other Gestures");
				tv.setTextColor(Color.WHITE);		
				tv.setBackgroundColor(Color.BLACK);
				tv.setGravity(Gravity.CENTER_VERTICAL);
				return tv; 
			}
			 LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 v = vi.inflate(R.layout.gesture_list_item, null);           
			 Button recordButton = (Button)v.findViewById(R.id.recordButton);
			 recordButton.setVisibility(View.GONE);

			 ImageView gestureImage = (ImageView)v.findViewById(R.id.gestureImage);
	         TextView v1= (TextView) v.findViewById(R.id.gestureText);
	        
	         
			if(position > cursorGestures.length+1){
				shiftPos = cursorGestures.length+2;
		         gestureImage.setBackgroundResource(othergesturesImages[position-shiftPos]);
				 v1.setText(otherGesture[position-shiftPos]);
			}
			else{
				 shiftPos = 1;
				 gestureImage.setBackgroundResource(cursorgesturesImages[position-shiftPos]);
				 v1.setText(cursorGestures[position-shiftPos]);
			}           
			return v;
		}
}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		AlertDialog alertDialog;
		if(position!=0 && position!=6){
			if(isForPracticeGesture){
				Intent i = new Intent();
				i.putExtra("Gesture_Category", position);
				setResult(1, i);
				finish();
			}
			else if(position == 4 || position == 8 || position == 9){
				alertDialog = new AlertDialog.Builder(GestureEditor.this).create();
				alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			    alertDialog.setMessage("This feature is currently not yet available. We are working really hard on it and it'll be there in future versions. Stay tuned.");
			    alertDialog.setTitle("Coming soon ...");
			    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			        return;

			    } }); 
			  	alertDialog.show();
			}
			else{
				Intent i = new Intent(this,GesturesListActivity.class);
				i.putExtra("Gesture_Category", position);
				this.startActivity(i);
			}
		}
	}


}
