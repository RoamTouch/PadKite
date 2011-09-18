package com.roamtouch.swiftee;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.acra.ErrorReporter;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import com.roamtouch.database.DBConnector;
import com.roamtouch.floatingcursor.FloatingCursor;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;
import android.gesture.Prediction;
import com.roamtouch.menu.TabButton;
import com.roamtouch.menu.WindowTabs;
import com.roamtouch.swiftee.R;
import com.roamtouch.view.EventViewerArea;
import com.roamtouch.view.SelectionGestureView;
import com.roamtouch.view.SwifteeGestureView;
import com.roamtouch.view.SwifteeOverlayView;
import com.roamtouch.landingpage.LandingPage;
import com.roamtouch.visuals.RingController;
import com.roamtouch.visuals.TipController;
import com.roamtouch.view.TutorArea;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import roamtouch.webkit.WebSettings;
import roamtouch.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;


public class BrowserActivity extends Activity implements OnGesturePerformedListener, OnGestureListener {
	
	public static int DEVICE_WIDTH,DEVICE_HEIGHT;

	public static String version = "Version Beta-v1.44.1-eclair build #862e37/105d79";
	public static String version_code = "Version Beta-v1.44.1";
	
	final public static boolean developerMode = false;
	public boolean isInParkingMode = false;
	
	final public static String BASE_PATH = "/sdcard/PadKite";
	final public static String THEME_PATH = BASE_PATH + "/Default Theme";

	private int activeWebViewIndex = 0;
	
	private WebView webView;
	private SwifteeOverlayView overlay;
	private SelectionGestureView mSelectionGesture;

	private FloatingCursor floatingCursor;
	public static EventViewerArea eventViewer;
	private GestureLibrary mLibrary;	
	
	private FrameLayout webLayout;
	private SwifteeGestureView mGestures;
	private HorizontalScrollView mTutor;
	
		
	private int currentGestureLibrary;
	private static int mGestureType = SwifteeApplication.CURSOR_TEXT_GESTURE;
	
	private SwifteeApplication appState;
    private SharedPreferences sharedPreferences;
    
    private TranslateAnimation ta;
    
    public final LandingPage lp = new LandingPage(this); //HERE ONLY PLACE TO INSTANSIATE LANDINGPAGE.
    
    private String landingPath = Environment.getExternalStorageDirectory()+"/PadKite/Web Assets/loadPage.html";
          
    //Visuals
    private RingController rCtrl;
    private TipController tCtrl;
    
    public void closeDialog()
    {
		AlertDialog alertDialog;

    	alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	    alertDialog.setMessage("You got " + floatingCursor.getWindowCount() + " open windows left. Do you really want to quit?");
	    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	      public void onClick(DialogInterface dialog, int which) {
	    	//mParent.finish();  
	        System.exit(0);

	    } }); 
	    alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
	      public void onClick(DialogInterface dialog, int which) {
	        return;
	    }}); 			  
	  	alertDialog.show();
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
	    		else if(webView.canGoBack())
	    			webView.goBack();
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
    
    //Voice recognition. 
    public ArrayList<String> matchesVoice;
    private static final int REQUEST_CODE = 1234;
    
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
    			System.exit(1);
    	}
    	
    	//Voice recognition.
    	if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
    		matchesVoice = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);       
    		((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setText(matchesVoice.get(0));
    		floatingCursor.pasteTextIntoInputText(record_X_location, record_Y_location, true);
        }      	
    }      
	 
    /**
     * Voice Recording for WebView Input
     */
    int record_X_location;
    int record_Y_location;   
    
    public void startVoiceRecognitionActivity(int X, int Y)
    {    	
    	record_X_location = X;
    	record_Y_location = Y;
    	
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
        startActivityForResult(intent, REQUEST_CODE);        
    };


    @Override
	 protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    	String data = null;
    	
    	if (intent != null) {
    		data = intent.getDataString();
    		enterParkingMode(true);
    	}
		
    	if(data!=null)
			webView.loadUrl(data);
	}

    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        appState = ((SwifteeApplication)getApplicationContext());
      
        /** LANDING PAGE **/		
		String landingString = null;
		boolean re = lp.remoteConnections();
		landingString = lp.generateLandingPageString();
		
		try {			
			String[] landing = SwifteeHelper.getHomepage(1);
			SwifteeApplication.createWebAssets(landing[0], landing[2], landingString);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
        
//    	getWindow().setFlags(Windowt.LayoutParams.FLAG_FULLSCREEN, 
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
        				System.exit(1);
        			}
			
        		}, 30000);
        	}
        }   	
        }, 10000);*/ 
        
        webLayout = (FrameLayout) findViewById(R.id.webviewLayout);
        
        try {
        
	        webView = new WebView(this);
	        webView.setScrollbarFadingEnabled(true);
	        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
	        webView.setMapTrackballToArrowKeys(false); // use trackball directly
	        // Enable the built-in zoom
	        webView.getSettings().setBuiltInZoomControls(false);
	        webView.getSettings().setJavaScriptEnabled(true);   
	        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
	        
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
			//params.setMargins(0, 20, 0, 0);
			
			webView.setLayoutParams(params);
			
			//webView.setDragTracker(tracker);	
			
			webLayout.addView(webView);
			//webView.loadUrl("http://padkite.com/start");
        } 
       	catch(Exception e)
       	{
        	ErrorReporter.getInstance().handleException(e);
    	}
       	
		String data = getIntent().getDataString();
		if(data!=null) {
			webView.loadUrl(data);
		}
		else {			
			String[] home = SwifteeHelper.getHomepage(0);
			webView.loadUrl(home[0]);
		}
		
		/*webView.setSelectionColor(0xAAb4d5fe);
		webView.setSearchHighlightColor(0xAAb4d5fe);
		
		webView.setCursorOuterColors(0xff74b1fc, 0xff46b000, 0xff74b1fc, 0xff36c000);
		webView.setCursorInnerColors(0xffa0c9fc, 0xff8cd900, 0xffa0c9fc, 0xff7ce900);
		webView.setCursorPressedColors(0x80b4d5fe, 0x807ce900);*/
		
		eventViewer= (EventViewerArea) findViewById(R.id.eventViewer);
		eventViewer.setParent(this);
		
		//webView.findAll("image");
		
		overlay = (SwifteeOverlayView) findViewById(R.id.overlay);
		
		//Ring controller.
		rCtrl = (RingController)findViewById(R.id.ringController);
		
		//Tips controller.
		tCtrl = (TipController)findViewById(R.id.tipController);		
		tCtrl.setParent(this);
		
		floatingCursor = (FloatingCursor)findViewById(R.id.floatingCursor);	
		floatingCursor.setWebView(webView,true);
		floatingCursor.setEventViewerArea(eventViewer);
		floatingCursor.setParent(this, rCtrl, tCtrl);		
		//floatingCursor.setHandler(handler);
		
		//Set proper parents to access from RingController.
 		rCtrl.setParent(this, floatingCursor, webView);
		
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
		
		// Query voice recognition and sotres variable on SwifteeApplication. 
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0){
        	SwifteeApplication.setVoiceRecognitionEnabled(false);
        }		
		
    }
    
    public void openMap(String url){
    	Intent searchAddress = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); 
        startActivity(searchAddress); 
    }
    
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
    
    public String mSelection;
    
    public void initGestureLibrary(int id){
    	currentGestureLibrary = id;
    	mLibrary = appState.getGestureLibrary(currentGestureLibrary);
    	
    	TutorArea tArea=(TutorArea)mTutor.getChildAt(0);
		tArea.setGestureLibrary(mLibrary);
		tArea.setParent(this);
    }
    
    public static int getGestureType()
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
    	
    	/*Throwable t = new Throwable(); 
		StackTraceElement[] elements = t.getStackTrace(); 

		String calleeMethod = elements[0].getMethodName(); 
		String callerMethodName = elements[1].getMethodName(); 
		String callerClassName = elements[1].getClassName();*/
    	
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
        else if ("Email".equals(action))
        {
			eventViewer.setText("e (email) gesture done");
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
        			eventViewer.setTimedSplittedText("Translated from ENGLISH to "+languageTo+": ",translated, 10000, true);			
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
        	try {
				new DownloadFilesTask().execute(new URL(mSelection), null, null);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }     
        else if("Send To".equals(action)){
			actions.send();
        }     
        else if("Copy".equals(action)){
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
				rCtrl.setWebView(wv);
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
		rCtrl.setWebView(wv);
		int currentPage = SwifteeApplication.getNewLandingPagesOpened();
		SwifteeApplication.setNewLandingPagesOpened(currentPage+1);			
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
		int currentPage = SwifteeApplication.getNewLandingPagesOpened();
		if (currentPage>0){
			SwifteeApplication.setNewLandingPagesOpened(currentPage-1);
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
				eventViewer.setText(url);
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
		
		String responseString = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://padkite.com/shurly/api/shorten?longUrl="+URLEncoder.encode(longUrl,"UTF-8")+"&format=txt");
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			responseString = getResponseBody(entity);
			httppost.abort();
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
        
        WindowTabs.getCurrentTab();
        
        String currentURL = webView.getUrl();
                
		if(moveToParkingPosition) {   
			
			floatingCursor.stopFling();
			
			int xLoc = floatingCursor.getScrollX();
			int yLoc = floatingCursor.getScrollY();	
			
			Hashtable location = getCuadrant4(xLoc, yLoc);			
			
			Object[] objCuarant = (Object[]) location.get(1);
			int cuadrant = (Integer) objCuarant[0];		
			
			Object[] hW = (Object[]) location.get(2);		
			int w = (Integer) hW[0];
			int h = (Integer) hW[1];
			
			switch (cuadrant){
			
			case 1:
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
				break;
			
			case 2:
				//Second cuadrant vars.
	            final int c2X;        
	            final int c2Y;
	            //Calculate distance to upper right corner. 
	        	c2X = w/2 + xLoc;
	        	c2Y = h/2 - yLoc;       		
        		if (c2X >= c2Y){ //y is shorter snap to x.
        			//Log.v("","y is shorter, snap x");	        			
        			floatingCursor.scrollTo(xLoc, h/2);
        			//animateDocking(xLoc, yLoc, xLoc, h/2);
        		} else if (c2X <= c2Y) { //x is shorter snap to y.
        			//Log.v("","x is shorter, snap y");
        			floatingCursor.scrollTo(-w/2, yLoc);
        			//animateDocking(xLoc, yLoc, -w/2, yLoc);
        		}        		
				break;
			case 3:
				//Third cuadrant vars.
	            final int c3X;        
	            final int c3Y;
	            //Calculate distance to upper right corner. 
	        	c3X = w/2 - xLoc;
	        	c3Y = h/2 + yLoc;       		
        		if (c3X >= c3Y){ //y is shorter snap to x.
        			//Log.v("","y is shorter, snap x");	
        			floatingCursor.scrollTo( xLoc, -h/2);
        			//animateDocking(xLoc, -w/2, yLoc, -h/2);
        		} else if (c3X <= c3Y) { //x is shorter snap to x.
        			//Log.v("","x is shorter, snap y");
        			//animateDocking(xLoc, -w/2, yLoc, yLoc);
        			floatingCursor.scrollTo( w/2, yLoc);
        		}
				break;
			case 4:
				//Fourth cuadrant vars.
	            final int c4X;        
	            final int c4Y;
	            //Calculate distance to upper right corner. 
	        	c4X = w/2 + xLoc;
	        	c4Y = h/2 + yLoc;        	
        		if (c4X >= c4Y){ //y is shorter snap to x.
        			//Log.v("","y is shorter, snap x");	
        			//animateDocking(xLoc, xLoc, yLoc, -h/2);
        			floatingCursor.scrollTo( xLoc, -h/2);
        		} else if (c4X <= c4Y) { //x is shorter snap to x.
        			//Log.v("","x is shorter, snap y");
        			//animateDocking(xLoc, -w/2, yLoc, yLoc);
        			floatingCursor.scrollTo( -w/2, yLoc);
        		}
				break;			
			}			
		
		}
	};
	
	
	public Hashtable getCuadrant4(int X, int Y) {
		
		//Object cuadrant[] = {};
		Hashtable cuadrant = new Hashtable();
		Object[] loc = new Object[1];
		Object[] hW = new Object[2];		
		
		Display display = getWindowManager().getDefaultDisplay();
		
		 //General location vars.
        int w = display.getWidth();
        int h = display.getHeight();
        
        int xLoc = X; //floatingCursor.getScrollX();        
        int yLoc = Y; //floatingCursor.getScrollY();	
        
        if ((xLoc > 0 && yLoc > 0)
				||(xLoc==0 && yLoc==0)) {	//UPPER LEFT CUADRANT - C1.
        	loc[0] = 1;
        } else if (xLoc < 0 && yLoc > 0){ //UPPER RIGHT CUADRANT - C2.    
        	loc[0] = 2;
        } else if (xLoc > 0 && yLoc < 0){ //DOWN LEFT CUADRANT - C3.		
        	loc[0] = 3;
        } else if (xLoc < 0 && yLoc < 0){ //DOWN RIGHT CUADRANT - C4.		
        	loc[0] = 4;
        }
        
        hW[0] = w;
        hW[1] = h;        
        
        cuadrant.put(1, loc);
        cuadrant.put(2, hW);         
        
        return cuadrant;
        
	}
	
	/**
	 * Provides coordinates to locate tips
	 * @param X
	 * @param Y
	 * @return
	 */
	public Vector getCuadrant34(int X, int Y) {
		
		Vector cuadrant 		= new Vector();
		int verticals 	= 0;
		int horizontals = 0;
		Rect xRect = new Rect(); 
		int tipType = 0;
		
		Display display = getWindowManager().getDefaultDisplay();
		
        int w = display.getWidth();       
        int h = display.getHeight();        
      	
        int yModule = h/4;
        
        if (Y > 0 && Y < yModule){
        	horizontals = 1;       	
        } else if (Y > yModule && Y < yModule*2){
        	horizontals = 2;
        } else if (Y > yModule*2 && Y < yModule*3){
        	horizontals = 3;
        } else if (Y > yModule*3 && Y < yModule*4){
        	horizontals = 4;
        }  
        
        int xModule = w/3;
        
        if (X > 0 && X < xModule){
        	
        	verticals = 1;        	
        	xRect.left = 20;
        	xRect.right = xModule+20;
        	
        	if (horizontals==1){
        		tipType = SwifteeApplication.SET_TIP_TO_LEFT_UP;
        	} else {
        		tipType = SwifteeApplication.SET_TIP_TO_LEFT_DOWN;
        	}
        	
        } else if (X > xModule && X < xModule*2){
        	
        	verticals = 2;
        	xRect.left = xModule-20;
        	xRect.right = xModule+20;   
        	
        	if (horizontals==1){
        		tipType = SwifteeApplication.SET_TIP_TO_CENTER_UP;
        	} else {
        		tipType = SwifteeApplication.SET_TIP_TO_CENTER_DOWN;
        	}
        	
        } else if (X > xModule*2 && X < xModule*3){
        	
        	xRect.left = w/2-20;
        	xRect.right = xModule+20;        	
        	verticals = 3;
        	
        	if (horizontals==1){
        		tipType = SwifteeApplication.SET_TIP_TO_RIGHT_UP;
        	} else {
        		tipType = SwifteeApplication.SET_TIP_TO_RIGHT_DOWN;
        	}        	
        }
      
        
        cuadrant.add(verticals);
        cuadrant.add(horizontals);
        cuadrant.add(xRect);
        cuadrant.add(tipType);
        //cuadrant.add(xModule);
        //cuadrant.add(yModule);
        
        return cuadrant;        
	}
		
	
	/**
	 * Provides the windows size.
	 * returns an array of coordinates.
	 * User by FC scrollWebView(); 
	 */
	public int[] getDeviceWidthHeight(){ //aca scroll
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
	
	public class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
		URL url;
	     protected Long doInBackground(URL... urls) {
	         int count = urls.length;
	         long totalSize = 0;
	         for (int i = 0; i < count; i++) {
	             Downloader.downloadFile(urls[i]);
	             if(urls[i]!=null)
	            	 url = urls[i];
	             publishProgress((int) ((i / (float) count) * 100));
	         }
	         return totalSize;
	     }

	     protected void onProgressUpdate(Integer... progress) {
	         //setProgressPercent(progress[0]);
	     }

	     protected void onPostExecute(Long result) {
	    	 eventViewer.setText("Download Complete");
	         //showDialog("Downloaded " + result + " bytes");
	    	 SwifteeApplication appState = ((SwifteeApplication)BrowserActivity.this.getApplicationContext());
	     	 DBConnector database = appState.getDatabase();
	     	 //Log.d("Inside async task download-----------", database + "url="+url);
	     	 database.addToHistory(System.currentTimeMillis()+"", url.toString(), "", 2);
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
