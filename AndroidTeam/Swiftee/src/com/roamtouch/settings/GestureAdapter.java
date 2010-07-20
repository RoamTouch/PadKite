package com.roamtouch.settings;

import java.util.ArrayList;
import java.util.Set;

import com.roamtouch.swiftee.R;
import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GestureAdapter extends BaseAdapter{

		private GestureLibrary mLibrary;
		private Context mContext;	
		private int gestureCount;
		private Object str[];
	
		
		public GestureAdapter(Context context){
			mContext=context;
			mLibrary = GestureLibraries.fromRawResource(mContext, R.raw.gestures);
			if (!mLibrary.load()) {
			}
			Set<String> s=mLibrary.getGestureEntries();
			str = s.toArray();
			gestureCount = str.length;
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
                v = vi.inflate(R.layout.gesture_list_item, null);           
            }
        	
            ImageView gestureImage = (ImageView)v.findViewById(R.id.gestureImage);
            ArrayList<Gesture> list = mLibrary.getGestures(str[position].toString());
			Bitmap bit = list.get(0).toBitmap(70, 70, 0, Color.BLACK);
			BitmapDrawable d = new BitmapDrawable(bit);
			gestureImage.setBackgroundDrawable(d);
			           
			TextView v1= (TextView) v.findViewById(R.id.gestureText);
			
			v1.setText(str[position].toString());          
			return v;
		}
}

