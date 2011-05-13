package com.roamtouch.settings;

import java.util.ArrayList;
import com.roamtouch.database.DBConnector;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import com.roamtouch.swiftee.SwifteeApplication;
import android.app.Dialog;
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
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GestureAdapter extends BaseAdapter{

		private GestureLibrary mLibrary;
		private Context mContext;	
		private int gestureCount;
		private String str[];
		private OnClickListener listener;
		private OnLongClickListener longListener;
		private OnLongClickListener editLongListener;
		public OnKeyListener newGestureListener;

		private int gestureType;
		private DBConnector database;
		
		private GesturesListActivity mList;
		
		public void addNewGesture()
		{
			Intent i = new Intent(mContext,GestureRecorder.class);
			i.putExtra("Gesture_Type", gestureType);
			i.putExtra("Gesture_Name", "");	
			i.putExtra("isEditable", true);
			i.putExtra("isNew", true);

			mContext.startActivity(i);
		}
		
		protected void refresh(boolean update)
		{
			Object tmp[] = mLibrary.getGestureEntries().toArray();
			
			str = new String[tmp.length];
			
			for (int i = 0; i < tmp.length; i++)
				str[i] = tmp[i].toString();
			
			java.util.Arrays.sort( str );
			
			gestureCount = str.length;

			if (update)
				mList.refrshList();
		}
		
		public GestureAdapter(Context context,int type,final GesturesListActivity list){
			mContext=context;
			this.gestureType = type;
			
			SwifteeApplication appState = ((SwifteeApplication)context.getApplicationContext());
			mLibrary = appState.getGestureLibrary(gestureType);
			database = appState.getDatabase();
			mList = list;
			refresh(false);
		
			longListener = new OnLongClickListener(){

				public boolean onLongClick(View v) {
					final Dialog dialog = new Dialog(mContext);
					dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
					final String gestureName =  v.getTag().toString();					
					Button delete = new Button(mContext);
					delete.setPadding(20, 20, 20, 20);
					delete.setText("Delete  "+convertItem(gestureName));
					delete.setTextSize(20);
					delete.setBackgroundColor(Color.WHITE);
					delete.setTextColor(Color.BLACK);
					delete.setOnClickListener(new OnClickListener(){

						public void onClick(View v) {	
							mLibrary.removeGesture(gestureName, mLibrary.getGestures(gestureName).get(0));
							mLibrary.save();
							database.deleteBookmark(gestureName);

							refresh(true);
							dialog.cancel();
						}
						
					});
					dialog.setContentView(delete);
					dialog.show();
					
					return true;
				}
				
			};
			
			editLongListener = new OnLongClickListener(){

				public boolean onLongClick(View v) {
					final Dialog dialog = new Dialog(mContext);
					dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
					final String gestureName =  v.getTag().toString();
					
					Button delete = new Button(mContext);
					delete.setPadding(20, 20, 20, 20);
					delete.setText("Delete  "+convertItem(gestureName));
					delete.setTextSize(20);
					delete.setBackgroundColor(Color.WHITE);
					delete.setTextColor(Color.BLACK);
					delete.setOnClickListener(new OnClickListener(){

						public void onClick(View v) {	
							mLibrary.removeGesture(gestureName, mLibrary.getGestures(gestureName).get(0));
							mLibrary.save();
							refresh(true);
							dialog.cancel();
						}
						
					});
					dialog.setContentView(delete);
					dialog.show();
					
					return true;
				}
				
			};
			
			listener = new OnClickListener(){

				public void onClick(View v) {
					
					String gestureName =  v.getTag().toString();
					
					Intent i = new Intent(mContext,GestureRecorder.class);
					i.putExtra("Gesture_Type", gestureType);
					i.putExtra("Gesture_Name", gestureName);
					if(gestureType == SwifteeApplication.BOOKMARK_GESTURE){
						i.putExtra("isStoredBookmark", true);
						i.putExtra("url", database.getBookmark(gestureName));
					}

					if (BrowserActivity.developerMode)
						i.putExtra("isEditable", true);

					mContext.startActivity(i);
					refresh(true);
				}
			};
			
    		

		}
		public int getCount() {
			return gestureCount ;
		}
		
		public String convertItem(String in)
		{
			if(gestureType==SwifteeApplication.BOOKMARK_GESTURE)
				return in;

			if (BrowserActivity.developerMode)
				return in;
			
			return BrowserActivity.convertGestureItem(in);
		}

		public Object getItem(int position) { 
			return str[position];
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
        	if(gestureType == SwifteeApplication.BOOKMARK_GESTURE)
        		v.setOnLongClickListener(longListener);
        	else if (BrowserActivity.developerMode)
        	{
        		v.setOnLongClickListener(editLongListener);
        		v.setOnKeyListener(newGestureListener);
        		
        	}
            ImageView gestureImage = (ImageView)v.findViewById(R.id.gestureImage);
            ArrayList<Gesture> list = mLibrary.getGestures(str[position]);
			Bitmap bit = list.get(0).toBitmap(70, 70, 0, Color.BLACK);
			BitmapDrawable d = new BitmapDrawable(bit);
			gestureImage.setBackgroundDrawable(d);
			           
			TextView v1= (TextView) v.findViewById(R.id.gestureText);			
			v1.setText(convertItem(str[position]));  
			v.setTag(str[position]);
			
			Button rec = (Button) v.findViewById(R.id.recordButton);
			rec.setTag(str[position].toString());
			//rec.setId(position);
			rec.setOnClickListener(listener);
			
			if(gestureType==SwifteeApplication.BOOKMARK_GESTURE){
				TextView url= (TextView) v.findViewById(R.id.url);			
				url.setText(database.getBookmark(str[position]));
			}
				
			return v;
		}
		
}

