package com.roamtouch.view;

import roamtouch.webkit.WebHitTestResult;

import com.roamtouch.menu.WindowTabs;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;

import android.content.Context;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EventViewerArea extends LinearLayout {
	
	
	private final float scale = getContext().getResources().getDisplayMetrics().density;
	private final int windowEnabledWidth = (int) (288 * scale + 0.5f); //Converting to Pixel
	private final int updateEnabledWidth = (int) (260 * scale + 0.5f); //Converting to Pixel
	
	private TextView tv1;
	private Button update;
	
	private BrowserActivity mParent;
	private WindowTabs windowTabs;
	
	public static final int WINDOWS_MODE = 0;
	public static final int UPDATE_MODE = 1;
	public static final int TEXT_ONLY_MODE = -1;
	private int mode = WINDOWS_MODE;

	public EventViewerArea(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		tv1=new TextView(getContext());
		tv1.setText(Html.fromHtml("<font color=\"yellow\">Action |</font> <font color=\"white\">FloatingCursor (" + BrowserActivity.version + ") </font"));
//		tv1.setInputType(InputType.TYPE_CLASS_TEXT);
		
		update=new Button(getContext());
		update.setGravity(Gravity.RIGHT);
		update.setText("Update");
		update.setVisibility(View.GONE);
		
		this.addView(tv1);
		this.addView(update);
		
		update.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				if(mode == WINDOWS_MODE){
					mParent.removeWebView();
					windowTabs.removeWindow();
				}
				else{
					
				}
			}
			
		});
	}
	public TextView getTextView(){
		return tv1;
	}
	public void setParent(BrowserActivity parent){
		mParent = parent;
	}
	public void setWindowTabs(WindowTabs wt){
		windowTabs = wt;
	}
	public void setText(String txt)
	{
		tv1.setText(Html.fromHtml("<font color=\"yellow\">" + txt + "</font>"));
	}
	public void setSplitedText(String txt,String txt2)
	{
		tv1.setText(Html.fromHtml("<font color=\"yellow\">" + txt + "</font><font color=\"white\">" + txt2 + "</font>"));
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
	
	public void setMode(int mode){
		if(mode == WINDOWS_MODE){
			update.setVisibility(VISIBLE);
			update.setText("");
			update.setBackgroundResource(R.drawable.cross);
			tv1.setWidth(windowEnabledWidth);
		}
		else if(mode == UPDATE_MODE){
			tv1.setWidth(updateEnabledWidth);
		}
		else{
			update.setVisibility(GONE);
		}
	}
	
}
