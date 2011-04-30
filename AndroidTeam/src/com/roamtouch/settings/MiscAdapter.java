package com.roamtouch.settings;

import com.roamtouch.swiftee.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MiscAdapter extends BaseAdapter{

	
	Context mContext;
	String arr[] = {"Enable Tutor Menu","Tell a contact"};
	public MiscAdapter(Context context){
		mContext=context;
		
	}
	public int getCount() {
		return 2;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		MiscView mv=new MiscView(mContext,position);
		return mv;
	}
	
	public class MiscView extends LinearLayout{

		public MiscView(Context context,int position) {
			super(context);
			
			LayoutInflater.from(context).inflate(R.layout.misc_adapter, this);

			TextView v1= (TextView) findViewById(R.id.tv1);
			v1.setText(arr[position]);
		
			if(position == 1){
				CheckBox cb= (CheckBox) findViewById(R.id.checkbox);
				cb.setVisibility(INVISIBLE);
			}
			
			}
		}
		
	}

