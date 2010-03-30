package com.roamtouch.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Gallery;

public class CarouselActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
//		final Gallery g = (Gallery)findViewById(R.id.gallery);
//		g.setAdapter(new ImageAdapter(this));
	}
}