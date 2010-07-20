package com.roamtouch.settings;

import com.roamtouch.swiftee.R;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MiscListActivity extends Activity implements OnItemClickListener {
	
	
	private ListView mTranslateList,mMiscList,aboutList;
	
	  /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.misc_list);
    	
    /** Translate List */    
    	
    	TextView tv = new TextView(this);
    	tv.setBackgroundColor(Color.DKGRAY);
    	tv.setTextColor(Color.LTGRAY);
    	tv.setText(R.string.translate);
    	tv.setHeight(28);
    	tv.setGravity(Gravity.CENTER_VERTICAL);
    	tv.setPadding(5, 0, 0, 0);
    	    	
    	mTranslateList = (ListView) findViewById(R.id.translateList);
    	
    	mTranslateList.addHeaderView(tv);
 
    	mTranslateList.setAdapter(new TranslateAdapter(this));
    	mTranslateList.setOnItemClickListener(this);

    	
    /** Miscellaneous List */   	
    	
    	tv = new TextView(this);
    	tv.setBackgroundColor(Color.DKGRAY);
    	tv.setTextColor(Color.LTGRAY);
    	tv.setText(R.string.Miscellaneous);
    	tv.setHeight(28);
    	tv.setGravity(Gravity.CENTER_VERTICAL);
    	tv.setPadding(5, 0, 0, 0);
    	    	
    	mMiscList = (ListView) findViewById(R.id.miscList);
    	mMiscList.addHeaderView(tv);
 
    	mMiscList.setAdapter(new MiscAdapter(this));
    	mMiscList.setOnItemClickListener(this);
    	mMiscList.setItemsCanFocus(false);
    	mMiscList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    	
   	/** About List */   	
    	
    	tv = new TextView(this);
    	tv.setBackgroundColor(Color.DKGRAY);
    	tv.setTextColor(Color.LTGRAY);
    	tv.setText(R.string.About);
    	tv.setHeight(28);
    	tv.setGravity(Gravity.CENTER_VERTICAL);
    	tv.setPadding(5, 0, 0, 0);
    	
    	
    	aboutList = (ListView) findViewById(R.id.aboutList);
    	aboutList.addHeaderView(tv);
 
    	aboutList.setAdapter(new AboutAdapter(this));
    	
    	aboutList.setOnItemClickListener(this);
    	
    }

	public void onItemClick(AdapterView<?> parent, View view, int arg2, long arg3) {
		
			int id = parent.getId();
			switch(id){
				case R.id.translateList:
					//LanguagesDialog dialog = new LanguagesDialog(MiscListActivity.this);
					//dialog.show();
					//PopupWindow window = new PopupWindow(200,300);
					//window.showAtLocation(, Gravity.CENTER, 10, 20);
					
					break;
				case R.id.miscList:
					Toast.makeText(MiscListActivity.this, "Misc", 20).show();
								break;
				case R.id.aboutList:
					Toast.makeText(MiscListActivity.this, "about", 20).show();
								break;
			}
	}

}
