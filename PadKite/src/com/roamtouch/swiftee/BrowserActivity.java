package com.roamtouch.swiftee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import roamtouch.webkit.CookieManager;
import roamtouch.webkit.CookieSyncManager;
import roamtouch.webkit.WebView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;
import android.gesture.Prediction;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RemoteViews;

import com.roamtouch.database.DBConnector;
import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.menu.TabButton;
import com.roamtouch.menu.WindowTabs;
import com.roamtouch.view.EventViewerArea;
import com.roamtouch.view.SelectionGestureView;
import com.roamtouch.view.SwifteeGestureView;
import com.roamtouch.view.SwifteeOverlayView;
import com.roamtouch.view.TutorArea;


public class BrowserActivity extends Activity implements OnGesturePerformedListener, OnGestureListener {
	
	public static int DEVICE_WIDTH,DEVICE_HEIGHT;

	public static String version = "Version RC8-v1.00-eclair build #086962/b73262";
	public static String version_code = "Version RC8-v1.00";
	
	final public static boolean developerMode = false;
	public boolean isInParkingMode = false;
	
	final public static String BASE_PATH = "/sdcard/PadKite";
	final public static String THEME_PATH = BASE_PATH + "/Default Theme";

	private int activeWebViewIndex = 0;
	
	private WebView webView;
	private SwifteeOverlayView overlay;
	private SelectionGestureView mSelectionGesture;

	private FloatingCursor floatingCursor;
	private EventViewerArea eventViewer;
	private GestureLibrary mLibrary;
//	private TopBarArea mTopBarArea;
	
	private FrameLayout webLayout;
	private SwifteeGestureView mGestures;
	private HorizontalScrollView mTutor;
	
		
	private int currentGestureLibrary;
	private int mGestureType = SwifteeApplication.CURSOR_TEXT_GESTURE;
	
	private SwifteeApplication appState;
    private SharedPreferences sharedPreferences;
    
    private TranslateAnimation ta;
    
	//For Download 
	private NotificationManager mNotificationManager;
	private static final int NOTIFICATION_ID = 9;
	private Notification mNf;
	private RemoteViews mRv;

	// For data tracker
	TrackHelper mTrackHelper;
	
    public void closeDialog()
    {
		AlertDialog alertDialog;

    	alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	    alertDialog.setMessage("You got " + floatingCursor.getWindowCount() + " open windows left. Do you really want to quit?");
	    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	      public void onClick(DialogInterface dialog, int which) {
	    	//mParent.finish();  
	        goExit(0);

	    } }); 
	    alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
	      public void onClick(DialogInterface dialog, int which) {
	        return;
	    }}); 			  
	  	alertDialog.show();
    }
    
    // A wrapper to to exit so that we can do something here.
    private void goExit(int code) {
    	if(mTrackHelper != null) {
    		mTrackHelper.saveData();
    	}
    	System.exit(code);
    }

    public boolean onKeyDown(int keyCode, android.view.KeyEvent event){
	        
	    	if (keyCode == KeyEvent.KEYCODE_MENU) { 
	    		floatingCursor.toggleMenuVisibility();
	    	}
	    	else if(keyCode == KeyEvent.KEYCODE_BACK){
	    		if(floatingCursor.isCircularZoomEnabled()){
	    			floatingCursor.disableCircularZoom();
	    		}
	    		else if (floatingCursor.isMenuVisible())
	    		{
		    		floatingCursor.hideMenuFast();
	    		}
	    		else if(mTutor.getVisibility() == View.VISIBLE){
	    			cancelGesture(true);
	    		}
	    		else if(floatingCursor.canGoBackward())
	    			floatingCursor.goBackward();
	    		else
	    			//BrowserActivity.this.finish();
	    			closeDialog();
	    	}
	   		return false;
	  }
	 
	 public boolean isOnline() {
		 ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo ni = cm.getActiveNetworkInfo();
		 if(ni == null)
			 return false;
		 //boolean b = ni.isConnectedOrConnecting();
		 return true;
		 
		}

    /** Called when the activity is resumed. */
    @Override
    protected void onResume() {
    	super.onResume();
    }
    
    public static final int FacebookRequestCode = 100042;
    public static final int SDCardRequestCode = 100050;
    
    String mFacebookAccessToken = null;
    long mFacebookAccessExpires = 0;
    
    public static final int FacebookStatusSuccess = 1;
    public static final int FacebookStatusError = 2;
    public static final int FacebookStatusLogout = 3;
    
    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data)
    {
    	super.onActivityResult(requestCode, resultCode, data);
    	//Log.v("onActivityResult", "requestCode = " + requestCode + ", resCode = " + resultCode);
    	if (requestCode == FacebookRequestCode && data != null)
    	{
    		final int status = data.getIntExtra("status", 0);

    		//Log.v("onActivityResult", "status = " + status);

   			if (status == FacebookStatusSuccess)
   			{
   				mFacebookAccessToken = data.getStringExtra("accessToken");
   				mFacebookAccessExpires = data.getLongExtra("accessExpires", 0);
   	    		//Log.v("onActivityResult", "accessToken = " + mFacebookAccessToken);
   			}
   			else {
   				mFacebookAccessToken = null;
   				mFacebookAccessExpires = 0;
   			}
    	}
    	if (requestCode == SDCardRequestCode && data != null)
    	{
    		final boolean status = data.getBooleanExtra("quit", false);

    		if (status == true)
    			goExit(1);
    	}
    }

    @Override
	 protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    	String data = null;
    	
    	if (intent != null) {
    		data = intent.getDataString();
    		enterParkingMode(true);
    	}
		
    	if(data!=null)
			floatingCursor.loadPage(data);
	}

    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        appState = ((SwifteeApplication)getApplicationContext());
        
        // create CookieSyncManager with current Context
        CookieSyncManager.createInstance(this);
        // remove all expired cookies
        CookieManager.getInstance().removeExpiredCookie();
        CookieManager.getInstance().removeSessionCookie();

//    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
    	DEVICE_WIDTH  =  getWindow().getWindowManager().getDefaultDisplay().getWidth();
    	DEVICE_HEIGHT =  getWindow().getWindowManager().getDefaultDisplay().getHeight();
    	
		sharedPreferences = getApplicationContext().getSharedPreferences("Shared_Pref_AppSettings", MODE_WORLD_READABLE);


		// FIXME: First show loading screen ...
        setContentView(R.layout.main);
        
        if(!isOnline()){
        	AlertDialog.Builder dialog= new AlertDialog.Builder(this);
        	dialog.setMessage("Network not available");
        	dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        		
        			}
        		
        	});
        	dialog.show();
        
        }
        // Update remote content if needed.
        Downloader.updateRemoteContentIfNeeded();

        // We may need a data tracker.
        if(TrackHelper.TRACKER_ENABLED) {
        	mTrackHelper = TrackHelper.createInstance(this);
        	mTrackHelper.submitSavedData(10);
        }

        /*
        mHandler.postDelayed(new Runnable() {
       
        	public void run() {
        	
        	if(getExpired()){
        		AlertDialog.Builder dialog= new AlertDialog.Builder(BrowserActivity.this);
        		dialog.setTitle("Beta version is expired");
        		dialog.setMessage("This version of PadKite Beta has expired. Please download a new version from http://padkite.com/. The application will close automatically in 30 seconds.");
        		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {	
        				public void onClick(DialogInterface dialog, int id) {
        				}        		
        		});

        		dialog.show();
        	
        		mHandler.postDelayed(new Runnable() {
			
        			public void run()
        			{
        				goExit(1);
        			}
			
        		}, 30000);
        	}
        }   	
        }, 10000);*/ 
        
        webLayout = (FrameLayout) findViewById(R.id.webviewLayout);
        
        webView = new WebView(this);
        webView.setScrollbarFadingEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setMapTrackballToArrowKeys(false); // use trackball directly
        // Enable the built-in zoom
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		//params.setMargins(0, 20, 0, 0);
		
		webView.setLayoutParams(params);
		
		//webView.setDragTracker(tracker);	
		webLayout.addView(webView);
		//webView.loadUrl("http://padkite.com/start");
		
		String data = getIntent().getDataString();
		if(data!=null) {
			webView.loadUrl(data);
		}
		else {
			//webView.loadUrl("file:///android_asset/loadPage.html");
			webView.loadUrl(SwifteeHelper.getHomepage());
		}
		
		webView.setSelectionColor(0xAAb4d5fe);
		webView.setSearchHighlightColor(0xAAb4d5fe);
		
		webView.setCursorOuterColors(0xff74b1fc, 0xff46b000, 0xff74b1fc, 0xff36c000);
		webView.setCursorInnerColors(0xffa0c9fc, 0xff8cd900, 0xffa0c9fc, 0xff7ce900);
		webView.setCursorPressedColors(0x80b4d5fe, 0x807ce900);
		
		eventViewer= (EventViewerArea) findViewById(R.id.eventViewer);
		eventViewer.setParent(this);
		
		//webView.findAll("image");
		
		overlay = (SwifteeOverlayView) findViewById(R.id.overlay);
		
		floatingCursor = (FloatingCursor)findViewById(R.id.floatingCursor);	
		floatingCursor.setWebView(webView,true);
		floatingCursor.setEventViewerArea(eventViewer);
		floatingCursor.setParent(this);
		//floatingCursor.setHandler(handler);
		
		if(data!=null) {
			enterParkingMode(true);
		}
		
		overlay.setFloatingCursor(floatingCursor);

		mSelectionGesture = (SelectionGestureView) findViewById(R.id.selectionGesture);
		mSelectionGesture.setEventViewer(eventViewer);
		mSelectionGesture.setFloatingCursor(floatingCursor);
		mSelectionGesture.setEnabled(false);
		
		mGestures = (SwifteeGestureView) findViewById(R.id.gestures);
		mGestures.setParent(this);
		mGestures.addOnGesturePerformedListener(this);
		mGestures.addOnGestureListener(this);
		mGestures.setEnabled(false);
		
		// FIXME: Change dynamically based on gesture library used
		mGestures.setUncertainGestureColor(0xAA000000);
		mGestures.setGestureColor(Color.BLACK);
		mGestures.setGestureStrokeWidth(15.0f);
		
		mTutor = (HorizontalScrollView) findViewById(R.id.gestureScrollView);
		
		mTutor.setVisibility(View.INVISIBLE);
		
/*		mTopBarArea=(TopBarArea)this.findViewById(R.id.topbararea);
		mTopBarArea.setVisibility(View.GONE);
		mTopBarArea.setWebView(webView);
*/		
		
		
		//This is a dummy user entry...neeed to remove after
		appState.getDatabase().registerUser("dummy", "dummy", "dummy@example.com");
		//appState.getDatabase().deleteAllBookmarks();
		//appState.getDatabase().addBookmark();
		
		IntentFilter filter = new IntentFilter (Intent.ACTION_MEDIA_UNMOUNTED); 
		filter.addDataScheme("file"); 
		registerReceiver(this.mSDInfoReceiver, new IntentFilter(filter));
    }
   
/*    public void setWebView(WebView wv){
    	webLayout.removeViewAt(0);
    	webLayout.addView(wv);
    	floatingCursor.setWebView(wv,false);
//    	mTopBarArea.setWebView(wv);
    }
*/    
    
    public void startTextGesture()
    {
		eventViewer.setText("Please make text selection gesture now.");

    	mSelectionGesture.setEnabled(true);
    	//floatingCursor.gestureDisableFC();
    }

    public void stopTextGesture()
    {
    	if (mSelectionGesture.isEnabled())
    	{
        	//floatingCursor.gestureEnableFC();
    		eventViewer.setText("Text Selection Gesture cancelled.");
    	}
    	
    	mSelectionGesture.setEnabled(false);
    }
    
    private String mSelection;
    
    public void initGestureLibrary(int id){
    	currentGestureLibrary = id;
    	mLibrary = appState.getGestureLibrary(currentGestureLibrary);
    	
    	TutorArea tArea=(TutorArea)mTutor.getChildAt(0);
		tArea.setGestureLibrary(mLibrary);
		tArea.setParent(this);
    }

    public int getGestureType()
    {
    	return mGestureType;
    }

    
    public void setGestureType(int gestureType)
    {
    	mGestureType = gestureType;
    }

    public void setSelection(String selection)
    {
    	mSelection = selection;
    }

    
    public void startGesture(boolean useSelection)
    {
    	initGestureLibrary(mGestureType);
		stopTextGesture();
    	floatingCursor.gestureDisableFC();
		webLayout.setEnabled(false);
		
		if (useSelection)
		{
			mSelection = (String) ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).getText();		
			eventViewer.setText("Please now make gesture for: " + mSelection);
		}
		
		mGestures.setEnabled(true);
		if(sharedPreferences.getBoolean("enable_tutor", true))
			mTutor.setVisibility(View.VISIBLE);
	}
    
    public void cancelGesture(boolean show)
    {
    	if (show)
    		eventViewer.setText("Gesture cancelled.");
    	stopGesture();
    }
    
    public void stopGesture()
    {
		mTutor.setVisibility(View.INVISIBLE);
		mGestures.setEnabled(false);
		mGestureType = SwifteeApplication.CURSOR_TEXT_GESTURE;
    	floatingCursor.gestureEnableFC();
    	floatingCursor.removeSelection();
		webLayout.setEnabled(true);
    	mSelection = null;
    }
    
    private boolean mCancelGesture = false;
    
    public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
    	mCancelGesture = true;
//    	eventViewer.setText("Gesture started.");
    }

    public void onGesture(GestureOverlayView overlay, MotionEvent event) {
    	mCancelGesture = false;
    	//   	eventViewer.setText("Gesture continuing.");
    }

    public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
    	if (mCancelGesture)
    	{
    		mCancelGesture = false;
    		cancelGesture(true);
    	}
    	// eventViewer.setText("Gesture ended.");
    }

    public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
    	eventViewer.setText("Gesture cancelled.");
		stopGesture();
    }

    public void drawGesture(Gesture gesture){
    	
    	//mGestures.drawGesture();
    }
    
    public void gestureDetected(String action)
    {
        if (currentGestureLibrary == SwifteeApplication.BOOKMARK_GESTURE)
        {
        	eventViewer.setText("Detected " + action + " gesture.");      
        	bookmarkGestures(action);
        }
        else
        {
        	action = convertGestureItem(action);        	
        	eventViewer.setText("Detected " + action + " gesture.");      
        	cursorGestures(action);
        }
    }
    
	public static String convertGestureItem(String in)
	{
		String s = in;
		
		if (s.contains(":"))
		{
			String tmp[] = s.split(":");
			if (tmp[0].replaceAll("\\d+","").length() <= 0)
				s = tmp[1];
		}
		
		return s;
	}
    
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		
		TrackHelper.doTrack(TrackHelper.PERFORM_GESTURE, 1);

		 ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
         if (predictions.size() > 0) {
                 if (predictions.get(0).score > 1.5) {
                	 String action = predictions.get(0).name;
       				     
                	 gestureDetected(action);
                 }
                 else                
   				   eventViewer.setText("Unrecognized gesture.");
         }
         else                                                                          
        	 eventViewer.setText("Unrecognized gesture.");
	}
	
	private Handler mHandler = new Handler();
	
	public void cursorGestures(String action){
		final GestureActions actions = new GestureActions(this, mSelection);
		
		if ("Search".equals(action)) 
		{
			eventViewer.setText("S (search) gesture done, searching for: " + mSelection);
			actions.search(floatingCursor);
		}
		else if ("YouTube".equals(action)) 
		{
			eventViewer.setText("Y (YouTube) gesture done, searching YouTube for: " + mSelection);
			actions.searchYouTube(floatingCursor);
		}
		else if ("Picture".equals(action)) 
		{
			eventViewer.setText("P (Picture) gesture done, searching Google images for: " + mSelection);
			actions.searchPicture(floatingCursor);
		}
        else if ("Email".equals(action) || "Mail".equals(action))
        {
			eventViewer.setText("M (eMail) gesture done");
			actions.email();
        }
        else if("Calendar".equals(action)){

			actions.calendar();
        }
        else if("Facebook".equals(action)){
        	actions.facebook(mFacebookAccessToken, mFacebookAccessExpires);
        }
        else if("Twitter".equals(action)){
        	actions.twitter();
        }
        else if("Blog".equals(action)) {
        	actions.blog();
        }
        else if("Translate".equals(action)) {
        	final String languageTo = sharedPreferences.getString("language_to", "ENGLISH").toUpperCase();
        	eventViewer.setTimedText("Translating from ENGLISH to "+languageTo+". Please wait ...", -1, true);
        	eventViewer.invalidate();       
        	
        	mHandler.postDelayed(new Runnable() {
			
        		public void run()
        		{
        			String translated = actions.translate(languageTo);
        			eventViewer.setTimedSplittedText("ENGLISH to "+languageTo+": ",translated, -1, true);			
        		}
			
        	}, 100);
        }
        else if("Wikipedia".equals(action)){
        	eventViewer.setText("W (wikipedia) gesture done, wiki searching for: " + mSelection);
			actions.wikipedia(floatingCursor);
        }     
        else if("Add Link".equals(action) || "Bookmark".equals(action)){
        	actions.addLink();
        }     
        else if("Open Link".equals(action) || "New Window".equals(action)){
        	actions.openLink(floatingCursor);
        }     
        else if("Download".equals(action)){
        	eventViewer.setText("Downloading:"+mSelection);
			actions.download(floatingCursor);
        }     
        else if("Send To".equals(action)){
			actions.send();
        }     
        else if("Copy".equals(action)){
			actions.copy();
        }
        else {
			eventViewer.setText("Unrecognized gesture: " + action);
        }
		stopGesture();
	}
	
	private void bookmarkGestures(String action){
		
		String url = appState.getDatabase().getBookmark(action);
		if(url!= null && !url.equals("Gesture cancelled"))
			floatingCursor.loadPage(url);
		else if ("Cancel".equals(action))
			eventViewer.setText("Gesture cancelled.");
	    else  
			eventViewer.setText("Unrecognized gesture: " + action);
		stopGesture();
	}

	public void drawGestureToEducate(Gesture gesture, String action){
		ArrayList<GestureStroke> strokes = gesture.getStrokes();
		ArrayList<GesturePoint> points =generateGesturePoints(strokes.get(0).points);
    //    ArrayList<GesturePoint> points = strokes.get(0).getGesturePoints();
		     
		if (currentGestureLibrary == SwifteeApplication.BOOKMARK_GESTURE)
	        eventViewer.setText("Detected " + action + " gesture.");
		else
			eventViewer.setText("Detected " + convertGestureItem(action) + " gesture.");
        
        mGestures.drawGesture(points,action);
                        // mGestures.setGesture(gesture);
                        // cursorGestures(action);
		mTutor.setVisibility(View.INVISIBLE);
		mGestures.setEnabled(false);
	}

	private ArrayList<GesturePoint> generateGesturePoints(float p[]){
		ArrayList<GesturePoint> points = new ArrayList<GesturePoint>();
		int c = p.length;
		for(int i=0;i<c;i=i+2){
			GesturePoint gpoint = new GesturePoint(p[i], p[i+1], 0);
			points.add(gpoint);
		}
		return points;
	}
/*	
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
*/	
	public void refreshWebView(){
		webView.reload();
	}

	public void setActiveWebViewIndex(int activeWebViewIndex) {
		int count = webLayout.getChildCount();
		if(activeWebViewIndex > -1 && activeWebViewIndex <count){
			this.activeWebViewIndex = activeWebViewIndex;
		}
		else
			return;
		for(int i=0;i<count;i++){
			if(i == activeWebViewIndex){
				WebView wv = (WebView)webLayout.getChildAt(i);
				wv.setVisibility(View.VISIBLE);
				floatingCursor.setWebView(wv,false);
			}
			else
				webLayout.getChildAt(i).setVisibility(View.INVISIBLE);
				
		}
	}
	
	public void setTopBarMode(int mode)
	{
		// FIXME: Stub
	}

	public int getActiveWebViewIndex() {
		return activeWebViewIndex;
	}
	
	public void addWebView(WebView wv){
		webLayout.addView(wv);
		floatingCursor.setWebView(wv,false);
	}
	public void removeWebView(){
		webLayout.removeViewAt(activeWebViewIndex);	
		setActiveWebViewIndex(activeWebViewIndex);
		int count = webLayout.getChildCount();
		for(int i= activeWebViewIndex;i<count;i++){
			WebView wv = (WebView) webLayout.getChildAt(i);
			wv.setId(wv.getId()-1);
		}
		if(webLayout.getChildCount()==0){
			floatingCursor.addNewWindow(false);
		}	
		
	}

	public void adjustTabIndex(WindowTabs winTabs){
		int count = winTabs.getChildCount() - 3;
		int wvCount = webLayout.getChildCount();
		for(int i= 2;i<count;i++){
			TabButton child = (TabButton)winTabs.getChildAt(i);
			wvCount--;
			child.setId(wvCount);
			child.setTabIndex(i);
			if(i == winTabs.getCurrentTab()){
				setActiveWebViewIndex(child.getId());
				winTabs.setActiveTabIndex(child);	
				winTabs.setCurrentTab(child.getTabIndex());
				String url = child.getWebView().getUrl();
				if(url != null) {
					eventViewer.setText(url);
				}
			}
		}		
	}
	public void setEventViewerMode(int mode){
			eventViewer.setMode(mode);		
	}
	
	public boolean getExpired()
	{
		String responseString = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			//Log.d("Expiration", "http://padkite.com/expiration/" + version_code.replace(" ", "_").toLowerCase());
			HttpPost httppost = new HttpPost("http://padkite.com/expiration/" + version_code.replace(" ", "_").toLowerCase());
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			responseString = getResponseBody(entity);
			httppost.abort();
			//Log.d("Connection successful.......", "-----------");
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		}
		
		//Log.d("Expiration", "Response: " + responseString);
		
		if (responseString.contains("EXPIRED")) {
			return true;
		}
		
		return false;
	}
	
	/*Get short link from server*/	
	public String getShortLink(String longUrl) {
		
		String responseString = longUrl;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://padkite.com/shurly/api/shorten?longUrl="+URLEncoder.encode(longUrl,"UTF-8")+"&format=txt");
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			responseString = getResponseBody(entity);
			httppost.abort();
			if (!responseString.startsWith("http"))
				responseString = longUrl;
			//Log.d("Connection successful.......", "-----------");
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		}
		return responseString;
	}
	private String getResponseBody(final HttpEntity entity) throws IOException, ParseException {

		if (entity == null) 
			throw new IllegalArgumentException("HTTP entity may not be null"); 
	
		InputStream instream = entity.getContent();

		if (instream == null) 
			return ""; 

		if (entity.getContentLength() > Integer.MAX_VALUE) 
			throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");

		String charset = getContentCharSet(entity);

		if (charset == null) 
			charset = HTTP.DEFAULT_CONTENT_CHARSET;

		Reader reader = new InputStreamReader(instream, charset);
		StringBuilder buffer = new StringBuilder();

		try {
				char[] tmp = new char[1024];
				int l;
				while ((l = reader.read(tmp)) != -1) {
					buffer.append(tmp, 0, l);
				}

		} finally {
			reader.close();
		}
		return buffer.toString();
	}

	private String getContentCharSet(final HttpEntity entity) throws ParseException {

		if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }

		String charset = null;

		if (entity.getContentType() != null) {
			HeaderElement values[] = entity.getContentType().getElements();

			if (values.length > 0) {
				NameValuePair param = values[0].getParameterByName("charset");
				if (param != null) 
					charset = param.getValue();
			}
		}
		return charset;
	}
	
	/*
	 * Implemented relocation of the FC to the next convenient and proximate side.
	 * I divided the screen in four cuadrants and compare x,y distances to the x,y sides.
	 * The FC snaps to the one near.
	 * TODO, animate. Jose. 
	 */
	public void enterParkingMode(boolean moveToParkingPosition) {
		
		isInParkingMode = true;
		
		//Shrink to a half the size
		floatingCursor.enterParkingMode();		
        Display display = getWindowManager().getDefaultDisplay();
        
        //General location vars.
        final int w = display.getWidth();
        final int h = display.getHeight();
        final int xLoc = floatingCursor.getScrollX();        
        final int yLoc = floatingCursor.getScrollY();	     
                
		if(moveToParkingPosition) {	        
			floatingCursor.stopFling();
			
			//UPPER LEFT CUADRANT - C1.	 
	        if (xLoc > 0 && yLoc > 0){	
	        	//Fisrt cuadrant vars
	            final int c1X;        
	            final int c1Y;
	            //Calculate distance to upper right corner. 
	        	c1X = w/2 - xLoc;
	        	c1Y = h/2 - yLoc;        	
	        	if (c1X >= c1Y){ // y is shorter, snap y. 
	        		//Log.v("","y is shorter, snap x");
	        		floatingCursor.scrollTo(xLoc, h/2);
	        	} else if (c1X <= c1Y) { // x is shorter snap to y.
	        		//Log.v("","x is shorter, snap y");
	        		floatingCursor.scrollTo(w/2, yLoc);
	        	}
	        }        
	        
	        //UPPER RIGHT CUADRANT - C2.	 
	        if (xLoc < 0 && yLoc > 0){	        	
	        	//Second cuadrant vars.
	            final int c2X;        
	            final int c2Y;
	            //Calculate distance to upper right corner. 
	        	c2X = w/2 + xLoc;
	        	c2Y = h/2 - yLoc;       		
        		if (c2X >= c2Y){ //y is shorter snap to x.
        			//Log.v("","y is shorter, snap x");	        			
        			floatingCursor.scrollTo(xLoc, h/2);
        		} else if (c2X <= c2Y) { //x is shorter snap to y.
        			//Log.v("","x is shorter, snap y");
        			floatingCursor.scrollTo(-w/2, yLoc);
        		}        		
	        }
	               			
			//DOWN LEFT CUADRANT - C3.	 			
			if (xLoc > 0 && yLoc < 0){
				//Third cuadrant vars.
	            final int c3X;        
	            final int c3Y;
	            //Calculate distance to upper right corner. 
	        	c3X = w/2 - xLoc;
	        	c3Y = h/2 + yLoc;       		
        		if (c3X >= c3Y){ //y is shorter snap to x.
        			//Log.v("","y is shorter, snap x");	
        			floatingCursor.scrollTo( xLoc, -h/2);
        		} else if (c3X <= c3Y) { //x is shorter snap to x.
        			//Log.v("","x is shorter, snap y");
        			floatingCursor.scrollTo( w/2, yLoc);
        		}
			}    

			//DOWN RIGHT CUADRANT - C4.	 			
			if (xLoc < 0 && yLoc < 0){
				//Fourth cuadrant vars.
	            final int c4X;        
	            final int c4Y;
	            //Calculate distance to upper right corner. 
	        	c4X = w/2 + xLoc;
	        	c4Y = h/2 + yLoc;        	
        		if (c4X >= c4Y){ //y is shorter snap to x.
        			//Log.v("","y is shorter, snap x");	
        			floatingCursor.scrollTo( xLoc, -h/2);
        		} else if (c4X <= c4Y) { //x is shorter snap to x.
        			//Log.v("","x is shorter, snap y");
        			floatingCursor.scrollTo( -w/2, yLoc);
        		}
			}		

			// TODO ANIMATE DOCKING here.		
			/*
			ta = new TranslateAnimation(0, w/2 - 50, 0, h/2 - 50);
	        ta.setDuration((long) 1000);
	        ta.setInterpolator(new AccelerateDecelerateInterpolator());
	        ta.setAnimationListener(new AnimationListener(){
	        	
	        	public void onAnimationEnd(Animation arg0) {
	        		floatingCursor.scrollTo( -w/2 + 50, -h/2 + 50);
	     	   }
	     	   
	     	   public void onAnimationRepeat(Animation arg0) {
	     		   //Do nothing
	            }

	            public void onAnimationStart(Animation arg0) {
	            	//Do nothing
	            }
	        });
	        floatingCursor.startAnimation(ta);
	        */
		}
	};

	/**
	 * Provides the windows size.
	 * returns an array of coordinates.
	 * User by FC scrollWebView(); 
	 */
	public int[] getDeviceWidthHeight(){
			int dims[] = new int[2];	
			Display display = getWindowManager().getDefaultDisplay();
			dims[0] = display.getWidth();
			dims[1] = display.getHeight();
			return dims;
	};	
	/**
	 * Return the cuadrant where the FC is located.
	 * User by FC scrollWebView();
	 */
	public int[] getFCLocation(int xLoc, int yLoc, int w, int h){
		int coords[] = new int[3];
		int cX;
		int cY;
		if (xLoc > 0 && yLoc > 0) {
			cX = w / 2 - xLoc;
			cY = h / 2 - yLoc;
			coords[0] = 1;	
			coords[1] = cX;	
			coords[2] = cY;	
		} else if (xLoc < 0 && yLoc > 0) {
			coords[0] = 2;
			coords[1] = cX = w / 2 + xLoc;
			coords[2] = cY = h / 2 - yLoc;			
		} else if (xLoc > 0 && yLoc < 0) {		
			coords[0] = 3;
			coords[1] = w / 2 - xLoc;
			coords[2] = h / 2 + yLoc;			
		} else if (xLoc < 0 && yLoc < 0) {
			coords[0] = 4;
			coords[1] = w / 2 + xLoc;
			coords[2] = h / 2 + yLoc;			
		} else if (xLoc == 0 && yLoc == 0){
			//CENTER
		}		
		return coords;
	};
	
	
	public void exitParkingMode() {
		isInParkingMode = false;
	}
	
	public class DownloadFilesTask extends AsyncTask<URL, Double, Long> {

		protected void onPreExecute(){
			//showDialog(DOWNLOADING);
		}

		URL url;
	     protected Long doInBackground(URL... urls) {
	         long totalSize = 0;
			Log.v("PADKITE","url for download is"+urls[0]+" == " +urls[1]+"   "+urls[2]);
			url = urls[0];

			if(urls[0] != null){
				try {
					InputStream myInputStream = null;

					HttpClient client = new DefaultHttpClient();
			        HttpGet request = new HttpGet();
			        request.setURI(urls[0].toURI());
		            HttpResponse response = client.execute(request);
					myInputStream = response.getEntity().getContent();

					long contentLength = response.getEntity().getContentLength();
					//Log.v("PADKITE","contentLength="+contentLength); 

					String s = urls[0].toString();
					String arr[] = s.split("/");
					int cnt = arr.length;
					String filename = URLEncoder.encode(arr[cnt-1]);
					FileOutputStream f = new FileOutputStream(new File(Environment.getExternalStorageDirectory()+"/download",filename));
					Log.v("PADKITE","downloading file... "+arr[cnt-1]);

					byte[] buffer = new byte[4096];
					int buffered = 0;
					long downloaded = 0;
					while ((buffered = myInputStream.read(buffer)) >= 0 ) {
						f.write(buffer, 0, buffered);
						downloaded += buffered;
						//Log.v("PADKITE","buffered = "+buffered);
						//Log.v("PADKITE","total downloaded = "+downloaded);
						//Log.v("PADKITE","contentLength = "+contentLength);

						double progress = ((double)downloaded/(double)contentLength)*100;
						//Log.v("PADKITE","progress percent is="+progress);
						publishProgress(progress);
					}
					f.close();
				} catch (Exception e) {
					e.printStackTrace();
				} 
	         }
	         return totalSize;
	     }

		protected void onProgressUpdate(Double... progress) {
			//Log.d("PADKITE" ,"onProgressUpdate...............");

			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mRv = new RemoteViews(getApplicationContext().getPackageName(), R.layout.download_remote_view);
			mRv.setTextViewText(R.id.data_meter_label,BrowserActivity.this.getResources().getText(R.string.downloading));
			mRv.setTextViewText(R.id.percent_label,String.valueOf((progress[0].intValue())+"%"));
			mRv.setImageViewResource(R.id.appIcon, android.R.drawable.stat_sys_download);
			mRv.setProgressBar(R.id.progress_bar, 100, progress[0].intValue(), false);
			//Log.v("PADKITE","down percent"+String.valueOf(progress[0])+" progress in RV "+progress[0].intValue());
			mNf = new Notification();
			mNf.icon = android.R.drawable.stat_sys_download;
			mNf.flags |= Notification.FLAG_ONGOING_EVENT;
			mNf.flags = Notification.FLAG_AUTO_CANCEL;
			mNf.contentView = mRv;

			// set the pending intent like below to launch the new activity which shows the progress of the each download.
			Intent notificationIntent = new Intent(getApplicationContext(), BrowserActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
			mNf.contentIntent = contentIntent;
			mNotificationManager.notify(NOTIFICATION_ID, mNf);
	     }

	     protected void onPostExecute(Long result) {
	    	 if (mNotificationManager != null)
	    		 mNotificationManager.cancel(NOTIFICATION_ID);
			String s = url.toString();
			String arr[] = s.split("/");
			int cnt = arr.length;
			String filename = URLEncoder.encode(arr[cnt-1]);
			eventViewer.setText("Download Complete: " + filename);

			SwifteeApplication appState = ((SwifteeApplication)BrowserActivity.this.getApplicationContext());
			DBConnector database = appState.getDatabase();
			database.addToHistory(System.currentTimeMillis()+"", url.toString(), filename, 2);
	     }
	 }
	
	private BroadcastReceiver mSDInfoReceiver = new BroadcastReceiver(){
	    @Override
	    public void onReceive(Context arg0, Intent intent) {
/*	    	AlertDialog alertDialog;

	    	alertDialog = new AlertDialog.Builder(BrowserActivity.this).create();
			alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		    alertDialog.setMessage("SD Card is not available or write protected. Please insert the SD Card or unmount it from USB.");
		    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		    	//mParent.finish();  

		    } }); 
		      
		  	alertDialog.show();
		  	
*/
	    	mHandler.post(new Runnable() {
	    	
	    		public void run() {
	    			Intent i = new Intent(BrowserActivity.this,SdCardError.class);
					i.putExtra("isAppLaunched", true);
					i.putExtra("numWindows", floatingCursor.getWindowCount());

					startActivityForResult(i, SDCardRequestCode);
	    		}
	    	});

	    	}
	 }; 
	
}
