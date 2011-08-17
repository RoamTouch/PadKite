package com.roamtouch.settings;

import com.roamtouch.swiftee.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TranslateAdapter extends BaseAdapter{

	private Context mContext;
	private TranslateView mTranslateView;
	private String str[] = {"Origin:","Translate to:"};
	private String languages[] = {"Spanish","English"};
	
 	public TranslateAdapter(Context context){
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
			mTranslateView = new TranslateView(mContext);
			
			TextView tv1 = (TextView) mTranslateView.findViewById(R.id.tv1);
			tv1.setText(str[position]);
		
			TextView tv2 = (TextView) mTranslateView.findViewById(R.id.tv2);
			tv2.setText(languages[position]);
			return mTranslateView;
	}
	
	public class TranslateView extends LinearLayout {

		public TranslateView(Context context) {
			super(context);
			LayoutInflater.from(context).inflate(R.layout.translate_adapter, this);			
		}
		
	}
	
}
