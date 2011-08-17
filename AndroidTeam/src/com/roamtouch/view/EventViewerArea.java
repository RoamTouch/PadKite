package com.roamtouch.view;

import roamtouch.webkit.WebHitTestResult;
import com.roamtouch.menu.WindowTabs;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EventViewerArea extends LinearLayout implements Runnable{
	
	
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
	private static final int TIME_TO_WAIT = 2000;
	private int timeToWait = TIME_TO_WAIT;//ms
	private SharedPreferences sharedPreferences;
	private Context mContext;
	private Handler handler;
	private Runnable runnable;
	private boolean isEnabled;
	private boolean isForceEnabled = false;
	
	public void init(Context context)  {
		mContext = context;
		handler = new Handler();
		
		sharedPreferences = mContext.getApplicationContext().getSharedPreferences("Shared_Pref_AppSettings", Context.MODE_WORLD_READABLE);;
		isEnabled = sharedPreferences.getBoolean("enable_event_viewer", true);

		tv1 = new TextView(context);
		tv1.setTextColor(Color.BLACK);
		tv1.setPadding(10, 10, 10, 10);
		tv1.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
		tv1.setText(Html.fromHtml("<font style='font-family:Lucida Grande,Verdana' color=\"black\">Action |</font> <font color=\"black\">FloatingCursor (" + BrowserActivity.version + ") </font"));
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
		
		handler.post(this);
	}
	
	public EventViewerArea(Context context, AttributeSet attrs)  {
		super(context, attrs);		
		init(context);
	}

	public EventViewerArea(Context context)  {
		super(context);
		init(context);
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
		sharedPreferences = mContext.getApplicationContext().getSharedPreferences("Shared_Pref_AppSettings", Context.MODE_WORLD_READABLE);
		isEnabled = sharedPreferences.getBoolean("enable_event_viewer", true);
		if(isEnabled){
			this.setVisibility(View.VISIBLE);
			tv1.setText(Html.fromHtml("<font color=\"black\">" + txt + "</font>"));
			timeToWait = TIME_TO_WAIT;
		}
	}
	
	public void setTimedText(String txt, int myTimeToWait, boolean force)
	{
		isForceEnabled = force;
		setText(txt);
		timeToWait = myTimeToWait;
		isForceEnabled = false;
	}
	
	public void setTimedSplittedText(String txt, String txt2, int myTimeToWait, boolean force)
	{
		isForceEnabled = force;
		setSplitedText(txt, txt2);
		timeToWait = myTimeToWait;
		isForceEnabled = false;
	}

	
	public void setSplitedText(String txt,String txt2)
	{
		sharedPreferences = mContext.getApplicationContext().getSharedPreferences("Shared_Pref_AppSettings", Context.MODE_WORLD_READABLE);;
		isEnabled = sharedPreferences.getBoolean("enable_event_viewer", true);
		if(isEnabled){
			this.setVisibility(View.VISIBLE);
			tv1.setText(Html.fromHtml("<font color=\"black\">" + txt + "</font><font color=\"white\">" + txt2 + "</font>"));
			timeToWait = TIME_TO_WAIT;
		}
	}
	public void splitText(int type, String extra){
		sharedPreferences = mContext.getApplicationContext().getSharedPreferences("Shared_Pref_AppSettings", Context.MODE_WORLD_READABLE);;
		isEnabled = sharedPreferences.getBoolean("enable_event_viewer", true);
		if(isEnabled){
			this.setVisibility(View.VISIBLE);
			timeToWait = TIME_TO_WAIT;
			switch(type){

			case WebHitTestResult.ANCHOR_TYPE:
				tv1.setText(Html.fromHtml("<font color=\"white\">FloatingCursor over link |</font> <font color=\"black\">"+extra+"</font>"));
				break;
			case WebHitTestResult.VIDEO_TYPE:
				tv1.setText(Html.fromHtml("<font color=\"white\">FloatingCursor over video |</font> <font color=\"black\">"+extra+"</font>"));
				break;
			case WebHitTestResult.IMAGE_TYPE:
				Spanned s = Html.fromHtml("<font color=\"white\">Protocol:</font> <font color=\"black\">Markup Language</font><br>" +
						"<font color=\"white\">Type:</font><font color=\"black\">Image JPEG</font><br>" +
						"<font color=\"white\">Address:(URL):</font>    <font color=\"black\">http://www.images.com/1.jpeg</font><br>" +
						"<font color=\"white\">Size:</font>  <font color=\"black\">43395 bytes</font><br>"+ 
				"<font color=\"white\">Dimensions:</font> <font color=\"black\">300 x 170 pixels</font>");
				tv1.setText(s);
				break;
			case WebHitTestResult.TEXT_TYPE:
				tv1.setText(Html.fromHtml("<font color=\"white\">FloatingCursor over text |</font> <font color=\"black\">"+extra+"</font>"));
				break;
			case -1:
				tv1.setText("");
			}
		}
	}
	
	public void setMode(int mode){
		if(mode == WINDOWS_MODE){
			update.setVisibility(VISIBLE);
			update.setText("");
			update.setBackgroundResource(R.drawable.cross);
			tv1.setWidth(windowEnabledWidth);
			//tv1.setPadding(20, 0, 0, 0);
		}
		else if(mode == UPDATE_MODE){
			tv1.setWidth(updateEnabledWidth);
			//tv1.setPadding(20, 0, 0, 0);
		}
		else{
			update.setVisibility(GONE);
			//tv1.setPadding(20, 0, 0, 0);
		}
	}
	
	
	public void run() {
		if(timeToWait < 0){
			; // Stub
		}
		else if(timeToWait==0){
			this.setVisibility(View.INVISIBLE);
			timeToWait = -1;
		}
		else{
			timeToWait-=100;
			if (timeToWait < 0)
				timeToWait = 0;
		}
		
		// FIXME: This can be handled better ...
		handler.postDelayed(this, 100);	
	}
	
}
