package com.roamtouch.settings;

import com.roamtouch.swiftee.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class AboutAdapter extends BaseAdapter{

	AboutView about;
	Context mContext;
	  public AboutAdapter(Context context) {
          mContext = context;
      }
	  
	public int getCount() {
		return 1;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		about=new AboutView(mContext);
		return about;
	}

	public class AboutView extends LinearLayout{

		public AboutView(Context context) {
			super(context);
			LayoutInflater.from(context).inflate(R.layout.about_text, this);		
		}
		
	}
}
