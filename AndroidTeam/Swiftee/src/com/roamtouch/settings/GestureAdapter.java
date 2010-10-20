package com.roamtouch.settings;

import java.util.ArrayList;
import java.util.Set;

import com.roamtouch.database.DBConnector;
import com.roamtouch.swiftee.R;
import com.roamtouch.swiftee.SwifteeApplication;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GestureAdapter extends BaseAdapter{

		private GestureLibrary mLibrary;
		private Context mContext;	
		private int gestureCount;
		private Object str[];
		private OnClickListener listener;
		private int gestureType;
		private DBConnector database;
		
		public GestureAdapter(Context context,int type){
			mContext=context;
			this.gestureType = type;
			
			SwifteeApplication appState = ((SwifteeApplication)context.getApplicationContext());
			mLibrary = appState.getGestureLibrary(gestureType);
			database = appState.getDatabase();
			Set<String> s=mLibrary.getGestureEntries();
			str = s.toArray();
			gestureCount = str.length;
		
			listener = new OnClickListener(){

				public void onClick(View v) {
					
					String gestureName =  v.getTag().toString();
					
					Intent i = new Intent(mContext,GestureRecorder.class);
					i.putExtra("Gesture_Type", gestureType);
					i.putExtra("Gesture_Name", gestureName);
					mContext.startActivity(i);
				}
			};
		}
		public int getCount() {
			return gestureCount ;
		}

		public Object getItem(int position) { 
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			
            if (v == null) {
            	LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            	if(gestureType==SwifteeApplication.BOOKMARK_GESTURE)
            		v = vi.inflate(R.layout.gesture_list_item2, null);
            	else
            		v = vi.inflate(R.layout.gesture_list_item, null);
            }
        	
            ImageView gestureImage = (ImageView)v.findViewById(R.id.gestureImage);
            ArrayList<Gesture> list = mLibrary.getGestures(str[position].toString());
			Bitmap bit = list.get(0).toBitmap(70, 70, 0, Color.BLACK);
			BitmapDrawable d = new BitmapDrawable(bit);
			gestureImage.setBackgroundDrawable(d);
			           
			TextView v1= (TextView) v.findViewById(R.id.gestureText);			
			v1.setText(str[position].toString());  
			
			Button rec = (Button) v.findViewById(R.id.recordButton);
			rec.setTag(str[position].toString());
			//rec.setId(position);
			rec.setOnClickListener(listener);
			
			if(gestureType==SwifteeApplication.BOOKMARK_GESTURE){
				TextView url= (TextView) v.findViewById(R.id.url);			
				url.setText(database.getBookmark(str[position].toString()));
			}
				
			return v;
		}
		
}

