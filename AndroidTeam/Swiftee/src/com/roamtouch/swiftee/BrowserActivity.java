package com.roamtouch.swiftee;

import java.util.ArrayList;

import com.roamtouch.database.DBConnector;
import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.swiftee.R;
import com.roamtouch.view.EventViewerArea;
import com.roamtouch.view.SwifteeOverlayView;
import com.roamtouch.view.TopBarArea;
import com.roamtouch.view.TutorArea;
import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
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
import roamtouch.webkit.WebView;
import android.widget.HorizontalScrollView;


public class BrowserActivity extends Activity implements OnGesturePerformedListener {
	
	public static int DEVICE_WIDTH,DEVICE_HEIGHT;

	public static String version = "Version Alpha-v1.06 build #eacd86/4d284d";

	private WebView webView;
	private SwifteeOverlayView overlay;
	private FloatingCursor floatingCursor;
	private EventViewerArea eventViewer;
	private GestureLibrary mLibrary;
	private TopBarArea mTopBarArea;
	
	private GestureOverlayView mGestures;
	private HorizontalScrollView mTutor;
	
	private final Handler mHandler = new Handler();
		
	private int currentGestureLibrary;
	public static final int CURSOR_GESTURES = 0; 
	public static final int BOOKMARK_GESTURES = 1; 
	private SwifteeApplication appState;
	
	 public boolean onKeyDown(int keyCode, android.view.KeyEvent event){
	        
	    	if (keyCode == KeyEvent.KEYCODE_MENU) { 
	    		floatingCursor.toggleMenuVisibility();
	    	}
	    	else if(keyCode == KeyEvent.KEYCODE_BACK){
	    		if (floatingCursor.isMenuVisible())
	    		{
		    		floatingCursor.toggleMenuVisibility();
	    		}
	    		else if(mTutor.getVisibility() == View.VISIBLE){
	    			mTutor.setVisibility(View.INVISIBLE);
	    			mGestures.setEnabled(false);
	    	    	floatingCursor.enableFC();
	    	        mSelection = null;
	    		}
	    		else if(webView.canGoBack())
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
        
        DBConnector dbConnector = new DBConnector(this);
        dbConnector.open();
        
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
		
		mGestures = (GestureOverlayView) findViewById(R.id.gestures);
		mGestures.addOnGesturePerformedListener(this);
		mGestures.setEnabled(false);
		
		mTutor = (HorizontalScrollView) findViewById(R.id.gestureScrollView);
		initGestureLibrary(CURSOR_GESTURES);
		
		mTutor.setVisibility(View.INVISIBLE);
				
		mTopBarArea=(TopBarArea)this.findViewById(R.id.topbararea);
		mTopBarArea.setWebView(webView);
    }
    private String mSelection;
    
    public void initGestureLibrary(int id){
    	currentGestureLibrary = id;
    	switch(id){
    		case 0: 
    				appState = ((SwifteeApplication)getApplicationContext());
    				mLibrary = appState.getGestureLibrary(SwifteeApplication.CURSOR_TEXT_GESTURE);
    				break;
    		case 1:
    				appState = ((SwifteeApplication)getApplicationContext());
    				mLibrary = appState.getGestureLibrary(SwifteeApplication.BOOKMARK_GESTURE);
    				break;
    		case 2:break;
    	}
    	TutorArea tArea=(TutorArea)mTutor.getChildAt(0);
		tArea.setGestureLibrary(mLibrary);
		tArea.setParent(this);
    }
    
    public void startGesture()
    {
    	floatingCursor.disableFC();

		mHandler.post(new Runnable() {
            public void run() {
           	 try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				mSelection = (String) ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).getText();
				
				mGestures.setEnabled(true);
				mTutor.setVisibility(View.VISIBLE);

				eventViewer.setText("Please now make S or e gesture for: " + mSelection);
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
				eventViewer.setText("S (search) gesture done, searching for: " + mSelection);
				webView.loadUrl("http://www.google.com/search?q=" + mSelection);
				setTopBarURL("http://www.google.com/search?q=" + mSelection);
				break;
    		case GESTURE_e:
				eventViewer.setText("e (email) gesture done");
				   
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
				eventViewer.setText("c (copy or cancel) gesture done");
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
                         
                         if ("Cancel".equals(action)) 
                         	gestureDone(GESTURE_c);
                         
                         if(currentGestureLibrary == CURSOR_GESTURES){
                        	 cursorGestures(action);
                         }
                         else if(currentGestureLibrary == BOOKMARK_GESTURES){
                        	 bookmarkGestures(action);
                         }
                        
                 }
                 else                
   				   eventViewer.setText("Unrecognized gesture. Please draw 'S', 'e' or 'c'.");
         }
         else                                                                          
        	 eventViewer.setText("Unrecognized gesture. Please draw 'S', 'e' or 'c'.");
	}
	private void cursorGestures(String action){
		if ("Search".equals(action)) 
         	gestureDone(GESTURE_s);
        else if ("Email".equals(action))
         	gestureDone(GESTURE_e);
        else                
			eventViewer.setText("Unrecognized gesture. Please draw 'S', 'e' or 'c'.");
	}
	private void bookmarkGestures(String action){
		if ("Google".equals(action))
			webView.loadUrl("http://www.google.com");
        else if ("Yahoo".equals(action))
        	webView.loadUrl("http://www.yahoo.com");
        else if ("Wikipedia".equals(action))
        	webView.loadUrl("http://www.wikipedia.com");
        else if ("Picasa".equals(action))
        	webView.loadUrl("http://www.picasa.google.com");
        else                
			eventViewer.setText("Unrecognized gesture. Please draw 'g', 'y' 'p' or 'c'.");
		stopGesture();
	}
	public void setTopBarVisibility(int visibility){
			mTopBarArea.setVisibility(visibility);
	}
	
	public void setTopBarMode(int mode){
		mTopBarArea.setMode(mode);
	}
	
	public void setTopBarURL(String url)
	{
		mTopBarArea.setURL(url);
	}
	
	public void refreshWebView(){
		webView.reload();
	}

}