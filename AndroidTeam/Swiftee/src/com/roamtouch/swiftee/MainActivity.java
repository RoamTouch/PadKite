package com.roamtouch.swiftee;

import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.menu.CircularLayout;
import com.roamtouch.swiftee.R;
import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
//import android.widget.LinearLayout;

public class MainActivity extends Activity implements OnGesturePerformedListener,OnClickListener {
	
	
	Button refreshButton,goButton;
	
	EditText eText;
	private WebView webView;
	private SwifteeOverlayView overlay;
	private FloatingCursor floatingCursor;
	private CircularLayout circularMenu;
	private CircularLayout settingsMenu;
	private EventViewerArea eventViewer;
	private GestureLibrary mLibrary;
	private TopBarArea mTopBarArea;
	//private LinearLayout menuLayout;
	
	private GestureOverlayView mGestures;
	private HorizontalScrollView mTutor;
	
	 public boolean onKeyDown(int keyCode, android.view.KeyEvent event){
	        
	    	if (keyCode == KeyEvent.KEYCODE_MENU) { 
	    		if(circularMenu.getVisibility()==View.GONE){
	    			circularMenu.setVisibility(View.INVISIBLE);
	    			mTopBarArea.setVisibility(View.GONE);
					settingsMenu.setVisibility(View.GONE);
	    		}
	    		else{
	    			circularMenu.setVisibility(View.VISIBLE);
	    			mTopBarArea.setVisibility(View.VISIBLE);
	    			mTutor.setVisibility(View.GONE);
					mTopBarArea.setMode(TopBarArea.ADDR_BAR_MODE);
	    		}
	    	}
	    	else if(keyCode == KeyEvent.KEYCODE_BACK){
	    		System.exit(1);
	    	}
	   		return false;
	  }
	 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        
        //menuLayout = (LinearLayout) findViewById(R.id.menuLayout);
        
        webView = (WebView) findViewById(R.id.webview);
		webView.loadUrl("http://www.finance.yahoo.com.com");		

		eventViewer= (EventViewerArea) findViewById(R.id.eventViewer);
		//webView.findAll("image");
		
		overlay = (SwifteeOverlayView) findViewById(R.id.overlay);
		
		floatingCursor = (FloatingCursor)findViewById(R.id.floatingCursor);	
		floatingCursor.setWebView(webView);
		floatingCursor.setEventViewerArea(eventViewer);
		floatingCursor.setParent(this);
		//floatingCursor.setHandler(handler);

		overlay.setFloatingCursor(floatingCursor);
		
		mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		if (!mLibrary.load()) {
			finish();
		}
	
		mGestures = (GestureOverlayView) findViewById(R.id.gestures);
		mGestures.addOnGesturePerformedListener(this);
		mGestures.setEnabled(false);
		
		mTutor = (HorizontalScrollView) findViewById(R.id.gestureScrollView);
		//TutorArea tArea=(TutorArea)mTutor.getChildAt(0);
		//tArea.setGestureList(mLibrary.getGestures("search"));
		
		mTutor.setVisibility(View.INVISIBLE);
		
		circularMenu=(CircularLayout)this.findViewById(R.id.circleMenu);
		int count=circularMenu.getChildCount();
		for(int i=0;i<count;i++){
			circularMenu.getChildAt(i).setOnClickListener(this);
		}
		
		settingsMenu=(CircularLayout)this.findViewById(R.id.settingsMenu);
		count=settingsMenu.getChildCount();
		for(int i=0;i<count;i++){
			settingsMenu.getChildAt(i).setOnClickListener(this);
		}

		mTopBarArea=(TopBarArea)this.findViewById(R.id.topbararea);
		mTopBarArea.setWebView(webView);
    }

	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		
	}
	public void onClick(View v) {
		int id=v.getId();
		switch(id){

			case R.id.close:{
				circularMenu.setVisibility(View.INVISIBLE);
				mTopBarArea.setVisibility(View.GONE);
				settingsMenu.setVisibility(View.GONE);
				break;
			}
			case R.id.settings:{
				circularMenu.setVisibility(View.GONE);
				settingsMenu.setVisibility(View.VISIBLE);
				break;
			}
			case R.id.findtext:{
				mTopBarArea.setMode(TopBarArea.SEARCH_BAR_MODE);
				circularMenu.setVisibility(View.INVISIBLE);
				break;
			}
			case R.id.refresh:{
				circularMenu.setVisibility(View.INVISIBLE);
				mTopBarArea.setVisibility(View.GONE);
				settingsMenu.setVisibility(View.GONE);
				webView.reload();
				break;
			}
			case R.id.stop:break;
			case R.id.zoom:break;
			case R.id.resizeHit:break;
			case R.id.windows:break;
			
		}
		
	}
}