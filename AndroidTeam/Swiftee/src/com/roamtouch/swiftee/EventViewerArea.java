package com.roamtouch.swiftee;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.AttributeSet;
import android.webkit.WebView.HitTestResult;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EventViewerArea extends LinearLayout {

	private TextView tv1,tv2;
	public EventViewerArea(Context context, AttributeSet attrs) {
		super(context, attrs);
		tv1=new TextView(getContext());
		tv2=new TextView(getContext());
		
		tv1.setTextColor(Color.YELLOW);
		tv2.setTextColor(Color.WHITE);
		
		//tv1.setText("Protocol\nType\nAdrress\nSize");
		//tv2.setText(Html.fromHtml("FloatingCursor<Font Size=1><Sup>TM</Sup></Font> over text"));
		
		
		this.addView(tv1);
		this.addView(tv2);
	}
	
	public void splitText(int type, String str){
		tv2.setText(str);
		switch(type){
		case HitTestResult.ANCHOR_TYPE:
			tv1.setText("FloatingCursor over link |");
		case HitTestResult.IMAGE_TYPE:
			tv1.setText("Protocol:\nType:\nAdrress:\nSize:\nDimensions:");
		case -1:
			tv1.setText("");
		}
	}

}
