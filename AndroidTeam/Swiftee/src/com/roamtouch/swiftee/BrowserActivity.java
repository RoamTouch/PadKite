package com.roamtouch.swiftee;

import java.util.ArrayList;
import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.menu.CircularLayout;
import com.roamtouch.menu.MenuButton;
import com.roamtouch.settings.SettingsTouchEvents;
import com.roamtouch.swiftee.R;
import com.roamtouch.view.EventViewerArea;
import com.roamtouch.view.SwifteeOverlayView;
import com.roamtouch.view.TopBarArea;
import com.roamtouch.view.TutorArea;
import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import roamtouch.webkit.WebView;
import android.widget.HorizontalScrollView;
import android.widget.Toast;


public class BrowserActivity extends Activity implements OnGesturePerformedListener,OnClickListener {
	
	public static int DEVICE_WIDTH,DEVICE_HEIGHT;
	
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
	
	private final Handler mHandler = new Handler();
	
	 public boolean onKeyDown(int keyCode, android.view.KeyEvent event){
	        
	    	if (keyCode == KeyEvent.KEYCODE_MENU) { 
	    		toggleMenuVisibility();
	    	}
	    	else if(keyCode == KeyEvent.KEYCODE_BACK){
	    		if(webView.canGoBack())
	    			webView.goBack();
	    		else
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
    	
    	DEVICE_WIDTH  =  getWindow().getWindowManager().getDefaultDisplay().getWidth();
    	DEVICE_HEIGHT =  getWindow().getWindowManager().getDefaultDisplay().getHeight();
    	
        setContentView(R.layout.main);
        
       // LinearLayout webLayout = (LinearLayout) findViewById(R.id.webviewLayout);
        
        webView = (WebView)findViewById(R.id.webview);
		webView.loadUrl("http://www.google.com");
		
		//webLayout.addView(webView);

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
		TutorArea tArea=(TutorArea)mTutor.getChildAt(0);
		tArea.setGestureLibrary(mLibrary);
		tArea.setParent(this);
		
		mTutor.setVisibility(View.INVISIBLE);
		
		circularMenu=(CircularLayout)this.findViewById(R.id.circleMenu);
		int count=circularMenu.getChildCount();
		for(int i=0;i<count;i++){
			circularMenu.getChildAt(i).setOnClickListener(this);
		}
		
		settingsMenu=(CircularLayout)this.findViewById(R.id.settingsMenu);
		SettingsTouchEvents.addTouchEvents(this,settingsMenu);	
		int c=settingsMenu.getChildCount();
		
		MenuButton sc=(MenuButton)settingsMenu.getChildAt(c-1);
		sc.setOnClickListener(this);
		
		mTopBarArea=(TopBarArea)this.findViewById(R.id.topbararea);
		mTopBarArea.setWebView(webView);
    }
    private String mSelection;
    
    public void startGesture()
    {
    	floatingCursor.disableFC();

		mHandler.post(new Runnable() {
            public void run() {
           	 try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				mSelection = (String) ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).getText();
				
				mGestures.setEnabled(true);
				mTutor.setVisibility(View.VISIBLE);

		        Toast.makeText(BrowserActivity.this, "Please now make S or e gesture for: " + mSelection, Toast.LENGTH_SHORT).show();
            }
		});
    }
    
    public void stopGesture()
    {
		mTutor.setVisibility(View.INVISIBLE);
		mGestures.setEnabled(false);
    	floatingCursor.enableFC();
        mSelection = null;
    }
    
    final int GESTURE_e = 1;
    final int GESTURE_t = 2;
    final int GESTURE_s = 3;
    final int GESTURE_c = 4;
    
    public void gestureDone(int gestureID)
    {
    	switch (gestureID)
    	{
    		case GESTURE_s:
                Toast.makeText(this, "S (search) gesture done, searching for: " + mSelection, Toast.LENGTH_SHORT).show();
				webView.loadUrl("http://www.google.com/?q=" + mSelection);   
				break;
    		case GESTURE_e:
                Toast.makeText(this, "e (email) gesture done", Toast.LENGTH_SHORT).show();
				   
				// Here we need to fire the intent to write an email with the content just pasted
				   
				//  This will not work in the emulator, because the emulator does not have gmail. 
				Intent intent = new Intent(Intent.ACTION_SENDTO);
	            intent.setData(Uri.parse("mailto:"));
	            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
	            intent.putExtra(Intent.EXTRA_TEXT, mSelection);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(intent);
	            break;
    		case GESTURE_c:
                Toast.makeText(this, "c (copy or cancel) gesture done", Toast.LENGTH_SHORT).show();
                break;
    			// cancel
    	}
		stopGesture();
    }

	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		
		 ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
         if (predictions.size() > 0) {
                 if (predictions.get(0).score > 1.5) {
                         String action = predictions.get(0).name;
                         if ("Search".equals(action)) {
                         	gestureDone(GESTURE_s);
                         } else if ("Email".equals(action)) {
                         	gestureDone(GESTURE_e);
                         } else if ("Cancel".equals(action)) {
                         	gestureDone(GESTURE_c);
         	            } else                                                                          
                      	   Toast.makeText(this, "Unrecognized gesture. Please draw 'S', 'e' or 'c'.", Toast.LENGTH_SHORT);
                 }
                 else                                                                          
              	   Toast.makeText(this, "Unrecognized gesture. Please draw 'S', 'e' or 'c'.", Toast.LENGTH_SHORT);
         }
         else                                                                          
      	   Toast.makeText(this, "Unrecognized gesture. Please draw a 'S',  'e' or 'c'.", Toast.LENGTH_SHORT);
	}
	
	public void toggleMenuVisibility(){
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
	
	public void onClick(View v) {
		int id=v.getId();
		switch(id){

			case R.id.close:{
				circularMenu.setVisibility(View.INVISIBLE);
				mTopBarArea.setVisibility(View.GONE);
				settingsMenu.setVisibility(View.GONE);
				break;
			}
			case R.id.settingsClose:{
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