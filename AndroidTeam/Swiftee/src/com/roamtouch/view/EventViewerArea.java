package com.roamtouch.view;

import roamtouch.webkit.WebHitTestResult;
import com.roamtouch.swiftee.BrowserActivity;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EventViewerArea extends LinearLayout {

	private TextView tv1;
	private Button update;
	
	public EventViewerArea(Context context, AttributeSet attrs) {
		super(context, attrs);
		tv1=new TextView(getContext());
		tv1.setText(Html.fromHtml("<font color=\"yellow\">Action |</font> <font color=\"white\">FloatingCursor (" + BrowserActivity.version + ") </font"));

		update=new Button(getContext());
		update.setText("Update");
		update.setVisibility(View.GONE);
		
		this.addView(tv1);
		this.addView(update);
	}
	
	public void setText(String txt)
	{
		tv1.setText(Html.fromHtml("<font color=\"white\">" + txt + "</font>"));
	}
	
	public void splitText(int type, String extra){
		
		switch(type){
		
		case WebHitTestResult.ANCHOR_TYPE:
				tv1.setText(Html.fromHtml("<font color=\"white\">FloatingCursor over link |</font> <font color=\"yellow\">"+extra+"</font>"));
				break;
		case WebHitTestResult.IMAGE_TYPE:
				Spanned s = Html.fromHtml("<font color=\"white\">Protocol:</font> <font color=\"yellow\">Markup Language</font><br>" +
						"<font color=\"white\">Type:</font><font color=\"yellow\">Image JPEG</font><br>" +
						"<font color=\"white\">Address:(URL):</font>    <font color=\"yellow\">http://www.images.com/1.jpeg</font><br>" +
						"<font color=\"white\">Size:</font>  <font color=\"yellow\">43395 bytes</font><br>"+ 
						"<font color=\"white\">Dimensions:</font> <font color=\"yellow\">300 x 170 pixels</font>");
 				tv1.setText(s);
 				break;
		case WebHitTestResult.TEXT_TYPE:
			tv1.setText(Html.fromHtml("<font color=\"white\">FloatingCursor over text |</font> <font color=\"yellow\">"+extra+"</font>"));
			break;
		case -1:
				tv1.setText("");
		}
	}
}
