package com.roamtouch.settings;

import com.roamtouch.swiftee.R;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LanguagesDialog extends Dialog{

	private Context context;
//	private String langs[];
	
	public LanguagesDialog(Context context) {
		super(context);
        this.context = context;
//        Resources res = context.getResources();
//		langs = res.getStringArray(R.array.language_preference);
		
		setContentView(R.layout.languages_dialog);
		ListView list = (ListView) findViewById(R.id.list);
		//ListAdapter adapter = new ListAdapter(this,android.R.layout.simple_list_item_1);
		list.setAdapter(new LanguagesAdapter());	
	}

	public class LanguagesAdapter extends BaseAdapter{

		private String langs[];
		
		public LanguagesAdapter(){
			Resources res = context.getResources();
			langs = res.getStringArray(R.array.language_preference);
		}
		public int getCount() {
			return langs.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if(v == null){
				TextView tv = new TextView(context);
				tv.setHeight(android.R.attr.listPreferredItemHeight);
				tv.setTextColor(Color.BLACK);
				tv.setBackgroundColor(Color.WHITE);
				v = tv;
			}
			((TextView) v).setText(langs[position]);

			return v;
		}
		
	}
}
