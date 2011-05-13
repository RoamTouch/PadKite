package com.roamtouch.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class HelpPage extends Activity{

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		 getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		 
		 
	}
	
}
