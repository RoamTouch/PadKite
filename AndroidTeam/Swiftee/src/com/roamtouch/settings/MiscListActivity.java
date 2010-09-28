package com.roamtouch.settings;

import java.util.Locale;

import com.roamtouch.swiftee.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MiscListActivity extends Activity implements OnItemClickListener {
	
	
	private ListView mMiscList;
	private String phoneLanguage;
	
	  /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        
    	setContentView(R.layout.misc_list);
    	mMiscList = (ListView)findViewById(R.id.miscList);
    	mMiscList.setAdapter(new MiscListAdapter());
    	mMiscList.setOnItemClickListener(this);
    	
    	Locale l=Locale.getDefault();
    	phoneLanguage = l.getDisplayLanguage();
    	
    }

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
			switch(position){
				case 2:
					final TextView tv2 = (TextView)view.findViewById(R.id.tv2);
					String langs[] = getResources().getStringArray(R.array.language_preference);
					final Dialog d = new Dialog(this);
					d.requestWindowFeature(Window.FEATURE_NO_TITLE);
					d.setContentView(R.layout.languages_dialog);
					ListView list = (ListView)d.findViewById(R.id.list);
					list.setOnItemClickListener(new OnItemClickListener(){

						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							TextView v = (TextView)view;
							tv2.setText(v.getText());
							d.cancel();
						}
						
					});
					list.setAdapter(new ArrayAdapter<String>(this,R.layout.simple_list_item_1,langs));
					d.show();
					break;
				
				case 5:
					Intent i= new Intent(MiscListActivity.this,Contacts.class);
					startActivity(i);
					break;
			}
	}
	
	public class TranslateView extends LinearLayout {
		String arr[] = {"Origin:","Translate to:"};
		public TranslateView(Context context,int position) {
			super(context);
			LayoutInflater.from(context).inflate(R.layout.translate_adapter, this);		
			TextView v1= (TextView) findViewById(R.id.tv1);
			v1.setText(arr[position-1]);
			
			TextView v2= (TextView) findViewById(R.id.tv2);
			if(position == 1){
				v2.setText(phoneLanguage);
				ImageView image = (ImageView)findViewById(R.id.image);
				image.setVisibility(INVISIBLE);
			}
			else
				v2.setText("Italian");
		}
		
	}
	public class MiscView extends LinearLayout{
		String arr[] = {"Enable Tutor Menu","Tell a contact"};
		CheckBox cb;
		public MiscView(Context context,int position) {
			super(context);
			
			LayoutInflater.from(context).inflate(R.layout.misc_adapter, this);
			cb= (CheckBox) findViewById(R.id.checkbox);
			TextView v1= (TextView) findViewById(R.id.tv1);
			v1.setText(arr[position-4]);
			if(position == 4 ){
				this.setOnClickListener(new OnClickListener(){

					public void onClick(View v) {
						if(cb.isChecked())
							cb.setChecked(false);
						else
							cb.setChecked(true);
					}
					
				});
			}
			if(position == 5){
				
				cb.setVisibility(GONE);
				
				ImageView image = (ImageView)findViewById(R.id.image);
				image.setVisibility(VISIBLE);
			}
			
			}
	}
	public class AboutView extends LinearLayout{

		public AboutView(Context context) {
			super(context);
			LayoutInflater.from(context).inflate(R.layout.about_text, this);		
		}
		
	}
	
	public class MiscListAdapter extends BaseAdapter{

		public int getCount() {
			return 8;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if(position == 0){
				TextView tv = new TextView(MiscListActivity.this);
		    	tv.setHeight(60);
		    	tv.setPadding(5, 0, 0, 0);
				tv.setText("Translate");
				tv.setTextColor(Color.WHITE);		
				tv.setBackgroundColor(Color.BLACK);
				tv.setGravity(Gravity.CENTER_VERTICAL);
				return tv; 
			}
			else if(position == 3){
				TextView tv = new TextView(MiscListActivity.this);
		    	tv.setHeight(60);
		    	tv.setPadding(5, 0, 0, 0);
				tv.setText("Miscellaneous");
				tv.setTextColor(Color.WHITE);		
				tv.setBackgroundColor(Color.BLACK);
				tv.setGravity(Gravity.CENTER_VERTICAL);
				return tv; 
			}
			else if(position == 6){
				TextView tv = new TextView(MiscListActivity.this);
		    	tv.setHeight(60);
		    	tv.setPadding(5, 0, 0, 0);
				tv.setText("About");
				tv.setTextColor(Color.WHITE);		
				tv.setBackgroundColor(Color.BLACK);
				tv.setGravity(Gravity.CENTER_VERTICAL);
				return tv; 
			}
			else if(position == 7){
				AboutView about=new AboutView(MiscListActivity.this);
				return about;
			}
			else if(position == 1 || position == 2){
				TranslateView translateView=new TranslateView(MiscListActivity.this,position);
				return translateView;
			}
			MiscView miscView = new MiscView(MiscListActivity.this,position);
			return miscView;
		}
		
	}

}
