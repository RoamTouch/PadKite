package com.roamtouch.swiftee;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.Socket; 
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import org.acra.ErrorReporter;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.roamtouch.database.DBConnector;
import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.utils.AsyncImageLoader;

import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;
import android.gesture.Prediction;
import com.roamtouch.menu.TabButton;
import com.roamtouch.menu.WindowTabs;
import com.roamtouch.swiftee.R;
import com.roamtouch.utils.AsyncImageLoader.AsyncImageCallback;
import com.roamtouch.utils.Base64;
import com.roamtouch.utils.COLOR;
import com.roamtouch.utils.GetDomainName;
import com.roamtouch.utils.PaintTextUtils;
import com.roamtouch.utils.ResizeArray;
import com.roamtouch.utils.ScreenLocation;
import com.roamtouch.view.EventViewerArea;
import com.roamtouch.view.SelectionGestureView;
import com.roamtouch.view.SwifteeGestureView;
import com.roamtouch.view.SwifteeOverlayView;
import com.roamtouch.landingpage.LandingPage;
import com.roamtouch.visuals.PointerHolder;
import com.roamtouch.visuals.RingController;
import com.roamtouch.visuals.SuggestionController;
import com.roamtouch.visuals.TextCursorHolder;
import com.roamtouch.visuals.TipController;
import com.roamtouch.view.TutorArea;
import com.roamtouch.menu.WindowTabs;

import com.roamtouch.utils.SDInfoReceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.net.ConnectivityManager;
import android.net.MailTo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.speech.RecognizerIntent;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;

import com.roamtouch.webhook.ProxyBridge;
import com.roamtouch.webhook.WebHitTestResult;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.Spinner;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;

public class BrowserActivity extends Activity implements OnGesturePerformedListener, OnGestureListener,
	AsyncImageLoader.AsyncImageCallback {
	
	public static int DEVICE_WIDTH,DEVICE_HEIGHT;

	//public static String version = "Version Beta-v2.0.5 - Android 2.3";
	public static String version = "Version Beta-v2.0.5 - Android 2.2";
	//public static String version = "Version Beta-v2.0.5 - Android 2.1";
	
	public static String version_code = "Version Beta-v2.0.5";
	
	final public static boolean developerMode = false;
	public boolean inParkingMode = false;
	
	final public static String BASE_PATH = Environment.getExternalStorageDirectory() + "/PadKite";
	
	final public static String THEME_PATH = BASE_PATH + "/DefaultTheme";
	final public static String GESTURES_PATH = BASE_PATH + "/GestureLibrary";
	final public static String WEB_PAGES_PATH = BASE_PATH + "/WebPages";
	final public static String WEB_ASSETS_PATH = BASE_PATH + "/WebAssets";
	final public static String IMAGES_ASSETS_PATH = BASE_PATH + "/Images";
	public static String getImagesAssetsPath() { return IMAGES_ASSETS_PATH; }
	//final public static String FONTS_PATH = BASE_PATH + "/Fonts";
    
	//Font to dras on suggestion list
	public Typeface suggestionFont; 
	
	private static int activeWebViewIndex = 0;
	
	public WebView webView;
	private SwifteeOverlayView overlay;
	private SelectionGestureView mSelectionGesture;

	private static FloatingCursor floatingCursor;
	public static EventViewerArea eventViewer;
	private GestureLibrary mLibrary;	
	
	private static FrameLayout webLayout;
	private SwifteeGestureView mGestures;
	private HorizontalScrollView mTutor;
	
	private SDInfoReceiver mSDInfoReceiver;
		
	private int currentGestureLibrary;
	private static int mGestureType = SwifteeApplication.CURSOR_TEXT_GESTURE;
	
	private SwifteeApplication appState;
    private SharedPreferences sharedPreferences;
   
    private TranslateAnimation ta;
    
    public final LandingPage lp = new LandingPage(this); //HERE ONLY PLACE TO INSTANSIATE LANDINGPAGE.
    
    private String landingPath = "file:////" + BrowserActivity.WEB_ASSETS_PATH + "/loadPage.html";
          
    //Visuals
    private static RingController rCtrl;
    private static TipController tCtrl;
    private static SuggestionController sCtrl; 
    private PointerHolder pHold;
    private TextCursorHolder tHold;

	private WindowTabs wTabs;
    private AlertDialog closeDialog;
    public String[][] arrayCERO; // 	= new String[0];
    public String[][] arrayFIRST; // 	= new String[0];
    public String[][] arraySECOND; // = new String[0];
    public String[][] arrayTHIRD; // 	= new String[0];   
    public String[][] arrayFOURTH; // = new String[0];
    public String[][] arrayFIFTH; // 	= new String[0];
    
    public String[][] arraySuggestion; 
    public String[][] arrayClipBoard; 
    public String[][] arrayRecent;
    public String[][] arrayVoice;
    public String[][] arrayMoreInput;
    
    public String[][] arrayMostVisited;
    public String[][] arrayBookmarks; 
    public String[][] arrayHistory; 
    public String[][] arrayDownload;
    public String[][] arrayHelp; 
    
    public String[][] arrayOpen;    
    public String[][] arrayMoreAnchor;
    public String[][] arrayExtraTab;  

	public String[][] arrayText;    
    
    public String[][] arrayOpenWindowsSet;
    public String[][] arrayWindowsSet;
    public String[][] arrayMoreWindowsSet; 
     
    public String[][] arraySiteLinks;
    public String[][] arraySiteVideos;
    public String[][] arraySiteImages;
    
    public String[][] arrayWiki;
    
    public Hashtable<Integer, Object> hashSiteLinks = new Hashtable<Integer, Object>();
    public Hashtable<Integer, Object> hashSiteImages = new Hashtable<Integer, Object>();
    public Hashtable<Integer, Object> hashSiteVideos = new Hashtable<Integer, Object>();
    
    public String[][] arrayServer;
    public String[][] arrayMoreServer;
    
    public static boolean landingLoaded=false;
	private String currentPageBridge;
	
	//JavaScript Bridge Settings 
	private static WebSettings wSet;
	
	/**AUTOCOMPLETE**/
	//Input Box Rectangle	
	private static boolean tabsActivated;
	private static boolean suggestionActivated = false;
	private static boolean tipsActivated = false;
	private static boolean suggestionListActivated;
	private static boolean suggestionActivatedExpanded;
	private static final int HTTP_TIMEOUT_MS = 1000;
	private static final String HTTP_TIMEOUT = "http.connection-manager.timeout";	
	private Context context;
	
	//private RelativeLayout relSuggestion;
	
	//Close Window set name.
	private String wMname;
	
	//Google Analitics tracking.
	private GoogleAnalyticsTracker tracker;
	
    public void closeDialog()
    {	    	
    	boolean has = false;    	
    	
		Vector tVector = wTabs.getTabVector();
		
		int size = tVector.size();		
		
		Vector<TabButton> tempVector = new Vector<TabButton>();
		
    	for (int i=0; i< size; i++){	
    		
    		View something = (View) tVector.get(i); 		
    		
			if (something instanceof TabButton) { // && ((TabButton) something).getWebView()!= null) { 
					
				TabButton tab = (TabButton) something;							
				
				WebView wv = tab.getWebView();		
				BitmapDrawable bitmapDrawable = tab.getBitmapDrawable();
				
				if (bitmapDrawable==null)
					continue;
							
				String tabTitle = wv.getTitle();
				
    			if (!tabTitle.equals("Landing Page")){    							
    				has = true;
    				tempVector.add(tab);
    			}
			}				
    	} 
    	
    	if (has){
    		
    		//SaveTabs
        	/*AlertDialog alertDialog;
        	alertDialog = new AlertDialog.Builder(this).create();    	
        	
        	alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        	
			Dialog.setMessage("There are " + floatingCursor.getWindowCount() + 
    				" opened windows left. Do you really want to save Windows Set. " +
    		"If you do please chek the landing page to open it.?");
        	
        	alertDialog.setTitle("You are about to close PadKite");	
    		
        	// Set an EditText view to get user input 
        	final EditText input = new EditText(this);
        	alertDialog.setView(input);
    	    
    	    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
    	      
    	    	public void onClick(DialogInterface dialog, int which) {	    
    	    		wMname = input.getText().toString().trim();   		
    	    		System.exit(0);

    	    } }); 
    	    alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
    	      public void onClick(DialogInterface dialog, int which) {	    	  
    	    	  //appState.getDatabase().removeTabs();  	  
    	        return;
    	    }}); 			  
    	  	alertDialog.show();*/
    		
    		//boolean exist = appState.getDatabase().existWindowsSet(wMname);
    		//if (exist){ }
    		
    		//int id = appState.getDatabase().insertWindowSet("TEST_1");//wMname);
    		
    		for (int j=0; j<tempVector.size();j++){  
    			
    			//int idExist = (Integer) tempVector.get(j);
    								
    			TabButton tab = tempVector.get(j);	
    			
    			WebView wv = tab.getWebView();		
				String tabTitle = wv.getTitle();			
    			
    			BitmapDrawable bitmapDrawable = tab.getBitmapDrawable();
				Bitmap bitmap = ((BitmapDrawable)bitmapDrawable).getBitmap();
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); 		
				
                byte[] image = baos.toByteArray();
                String encodedImage = Base64.encodeBytes(image);			
				
				String url = wv.getUrl();		
			
				//appState.getDatabase().insertTabs(id, tabTitle, url, encodedImage);
   		
    			}
    		}
			System.exit(0);
    	}    	
    	
    	//wTabs.saveTabs(closeDialog, context, floatingCursor.getWindowCount());
		//System.exit(0);  
		
		/*closeDialog = new AlertDialog.Builder(this).create();		
		closeDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);	    
		closeDialog.setMessage("You got " + floatingCursor.getWindowCount() + " open windows left. Do you really want to quit?");	    
		closeDialog.setButton("OK", new DialogInterface.OnClickListener() {
	      
	    	public void onClick(DialogInterface dialog, int which) {    		
	    		wTabs.saveTabs(closeDialog, context);
	    		System.exit(0);   		
	    	} });
	    
		closeDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
	      public void onClick(DialogInterface dialog, int which) {	    	  
	    	  //appState.getDatabase().removeTabs();  	  
	        return;
	    }}); 			  
		closeDialog.show();*/
   // } 
  
    
    long start;
    long current;
    boolean touchUpMenu;
    boolean reStart;
    
    @Override	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event){    	
               
	    	if (keyCode == KeyEvent.KEYCODE_MENU) { 	    		
	    		current = System.currentTimeMillis();		
	    		if (!floatingCursor.isMenuVisible()){	    			
	    			start = System.currentTimeMillis();
	    			floatingCursor.toggleMenuVisibility();
	    			floatingCursor.eraseDraws();
	    			return touchUpMenu;	   			
	    		} 	 	    		
	    		if (reStart){ 
	    			floatingCursor.setCurrentMenu(0);
	    			start = System.currentTimeMillis();
	    			reStart=false;	    			
	    		}	    		
	    		long check4WM = current-start;	    		
	    		if (check4WM > 1200 && floatingCursor.isMenuVisible() && floatingCursor.getCurrentMenu()==0){	    			
	    			floatingCursor.setCurrentMenu(2);	    			
	    			return touchUpMenu;	    			
	    		} 	    		
	    		long check4Settings = current-start;	    		
	    		if (check4Settings > 2400 && floatingCursor.isMenuVisible() && floatingCursor.getCurrentMenu()==2){	    			
	    			floatingCursor.setCurrentMenu(1);	    			
	    			mHandler.postDelayed(new Runnable() {	    				
	            		public void run() {
	            			reStart=true;			
	            		}	    			
	            	}, 1200); 				    			
	    			return touchUpMenu;
	    		}    		
	    	} else if(keyCode == KeyEvent.KEYCODE_BACK){ 
	    		
	    		if (isTipsActivated()){   			
	    			floatingCursor.eraseDraws();	    			
	    		}	    		
	    		
	    		if (floatingCursor.isMenuVisible()) {
	    			
		    		floatingCursor.toggleMenuVisibility();
		    		floatingCursor.eraseDraws();
		    		
	    		} else if(mTutor.getVisibility() == View.VISIBLE){
	    			
	    			cancelGesture(true);
	    			
	    		} else if(isTipsActivated()){
	    			
	    			
	    			
	    		} else if(webView.canGoBack()){
	    			
	    			webView.goBack();
	    			
	    		} else if (floatingCursor.isExpandToFinger()){
	    			
	    			floatingCursor.eraseDraws();
	    			floatingCursor.enableFC();	    			
	    			clearViewsIdentifiers();
	    			
	    			/*if (isSuggestionListActivated()) {    			
	    				((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setText("");
	    				Rect re = SwifteeApplication.getActiveRect(); 
	    				floatingCursor.pasteTextIntoInputText(re.left+10, re.top+10, null);	    			    			
	    				floatingCursor.loadPage("javascript:clearInput()");
	    				floatingCursor.eraseDraws();    			
	    				floatingCursor.setFocusNodeAt(0,0);
	    			}*/
	    			
	    		} else {
	    			
	    			closeDialog();
	    			
	    		}
	    	}	    		    	
	    	return touchUpMenu;
	  }
	 
    
    @Override	
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event){
    	touchUpMenu=true;
		return false;	    	
    }
    
    public boolean keyboardOpened;
    
    
 // from the link above
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        
    	super.onConfigurationChanged(newConfig);
        
        int[] screenSize = getDeviceWidthHeight();
        Log.v("screenSize", "screenSize: " + screenSize[0] + " " + screenSize[1]);
        
        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
        	keyboardOpened=true;
            //Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
        } 
        else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
        	keyboardOpened=false;
        	
        	//tCtrl.drawNothing();        
        	//sCtrl.drawNothing();    		
            //Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();        	
        } 
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	
        	orientationHandler.postDelayed(orientationRunnable, 500);	
        	
        	SwifteeApplication.setOrientation(SwifteeApplication.ORIENTATION_LANDSCAPE);
            
        } 
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            
        	orientationHandler.postDelayed(orientationRunnable, 500);	
        	
        	SwifteeApplication.setOrientation(SwifteeApplication.ORIENTATION_PORTRAIT);
        	
        }
        
    }  
    
    Handler orientationHandler = new Handler();
    
    Runnable orientationRunnable = new Runnable() {
	    public void run() {
	    	Rect master;
	    	if ( floatingCursor.getcType() == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE ) { 
	    		master = SwifteeApplication.getMasterRect();
	    	} else {
	    		master = SwifteeApplication.getOriginalAnchorRect();
	    	}	    	
		    if (master!=null){
		    	
		    	floatingCursor.refreshMenusRequest();    	
		    	
		    	//int[] screenSize = getDeviceWidthHeight();
		    	//Log.v("screenSize", "screenSize: " + screenSize[0] + " " + screenSize[1]);
		    }
		    orientationHandler.removeCallbacks(orientationRunnable);
		}
	};
    
    /*public void hideKeyboard(){
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hi();
    }*/
	
	 
	 public boolean isOnline() {
		 ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo ni = cm.getActiveNetworkInfo();
		 if(ni == null)
			 return false;
		 //boolean b = ni.isConnectedOrConnecting();
		 return true;

		}
	 
	 public static boolean checkWindowsTabOpened(){
		 boolean tabOpened;
		 if (floatingCursor.currentMenu == floatingCursor.fcWindowTabs){
			 tabOpened = true;		 
		 } else {
			 tabOpened = false;
		 }
		 return tabOpened;		 
	 }
	 
	 public static int getCurrentMenu(){
		 return floatingCursor.getCurrentMenu();
	 }
	 

    /** Called when the activity is resumed. */
    @Override
    protected void onResume() {
    	super.onResume();
    	System.gc();
    }
    
    public static final int FacebookRequestCode = 100042;   

    
    String mFacebookAccessToken = null;
    long mFacebookAccessExpires = 0;
    
    public static final int FacebookStatusSuccess = 1;
    public static final int FacebookStatusError = 2;
    public static final int FacebookStatusLogout = 3;
    
    //Voice recognition. 
    public ArrayList<String> matchesVoice;
    private static final int REQUEST_CODE = 1234;
    public static final int SDCardRequestCode = 100050;
    
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
    		floatingCursor.pasteTextIntoInputText(record_X_location, record_Y_location, null);
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
			webView.loadUrl(data);
	}

    class GlobalExceptionHandler implements
    	
    	Thread.UncaughtExceptionHandler {
         
          public void uncaughtException(Thread thread, Throwable ex) {
        	  
        	  String exToStr = ex.toString();
        	  String exMessage = ex.getMessage();
        	  
        

                Log.d("GLOBAL EXCEPTION", "Exception :" + ex.toString()
                            + " and Message:" + ex.getMessage());
               
                ex.printStackTrace();              

          }
         

    }

    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	 //Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());
    	
        super.onCreate(savedInstanceState);
        
        /**
         * Disable Title Bar by default, 
         * enable it to read memory monitor onDraws.
         **/   
        if(!SwifteeApplication.getMemoryStatusEnabled()){
        	getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        appState = ((SwifteeApplication)getApplicationContext());
        
        /**IP ADDRESS FRO SERVER**/
        String ipAddress = getIp();
        SwifteeApplication.setIpAdress(ipAddress);       
        
        SharedPreferences wifiPref = PreferenceManager.getDefaultSharedPreferences(this);        
        String storedIp = wifiPref.getString("IpAddress", "new");
        
        if (storedIp.equals("new") || !storedIp.equals(ipAddress)){
        	SharedPreferences.Editor editor = wifiPref.edit();
			editor.putString("IpAddress", ipAddress);
			editor.commit();
			//Send to the server
			//String android_id = Secure.getString(getBaseContext().getContentResolver(),
	        //        Secure.ANDROID_ID); 
        } 	
      
        /** LANDING PAGE **/		
		String landingString = null;
		boolean re = lp.remoteConnections();		
		landingString = lp.generateLandingPageString();
		
		 /** SUGGESTIONS **/        
        httpClient = new DefaultHttpClient();
		httpClient.getParams().setLongParameter(HTTP_TIMEOUT, HTTP_TIMEOUT_MS);
		context = this;	
		//suggestionFont = Typeface.createFromAsset(getAssets(), "Fonts/DroidSans.ttf");
				
		/** WINDOWS MANAGER **/
		wTabs = new WindowTabs(this);		
		
		try {			
			String[] landing = SwifteeHelper.getHomepage(1);
			SwifteeApplication.createWebAssets(landing[0], landing[2], landingString);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       
    	
    	DEVICE_WIDTH  =  getWindow().getWindowManager().getDefaultDisplay().getWidth();
    	DEVICE_HEIGHT =  getWindow().getWindowManager().getDefaultDisplay().getHeight();
    	
		sharedPreferences = getApplicationContext().getSharedPreferences("Shared_Pref_AppSettings", MODE_WORLD_READABLE);

		// FIXME: First show loading screen ...
        setContentView(R.layout.main);
        
        //check online
        checkOnline(true);  
        
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
        }, 10000); 
        
        
        
        webLayout = (FrameLayout) findViewById(R.id.webviewLayout);
        
        //Instansiate WebView. Here the 
        try {
        
	        webView = new WebView(this);
        
        } catch(Exception e){
        		
        		//http://android.amberfog.com/?p=98
	       
	        	//ErrorReporter.getInstance().handleException(e);
        	
	   }
        webView.setScrollbarFadingEnabled(true);
    
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setMapTrackballToArrowKeys(false); // use trackball directly
        
        // Enable the built-in zoom
        webView.getSettings().setBuiltInZoomControls(false);	        
        webView.getSettings().setJavaScriptEnabled(true);   
        //webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        
        //Set Layout params on WebView
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);		
		webView.setLayoutParams(params);			
		webLayout.addView(webView);		      	
      
       	//JavaScript ProxyBridge
		ProxyBridge pBridge = new ProxyBridge();
		webView.addJavascriptInterface(pBridge, "pBridge");	
		
		//Load Lading Page
		String data = getIntent().getDataString();
		if(data!=null) {
			webView.loadUrl(data);
		}
		else {			
			String[] home = SwifteeHelper.getHomepage(0);
			webView.loadUrl(home[0]);
		}
		
		//Event Viewer
		eventViewer= (EventViewerArea) findViewById(R.id.eventViewer);
		eventViewer.setParent(this);

		//General FC Overlay
		overlay = (SwifteeOverlayView) findViewById(R.id.overlay);
		
		//Ring controller.
		rCtrl = (RingController)findViewById(R.id.ringController);
		
		//Suggestion controller
		sCtrl = (SuggestionController)findViewById(R.id.suggestionController);		
		
		//Pointer Holder controller.
		pHold = (PointerHolder)findViewById(R.id.pointerHolder);
		
		//Pointer Holder controller.
		tHold = (TextCursorHolder)findViewById(R.id.textCursorHolder);	
		
		//Tips controller.
 		tCtrl = (TipController)findViewById(R.id.tipController);	 			
		
		floatingCursor = (FloatingCursor)findViewById(R.id.floatingCursor);	
		floatingCursor.setWebView(webView,true);
		floatingCursor.setEventViewerArea(eventViewer);
		floatingCursor.setParent(this, rCtrl, tCtrl, pHold, sCtrl, tHold);    	
		
		//Set proper parents to access from RingController.
 		rCtrl.setParent(this, floatingCursor, webView); 
 		sCtrl.setParent(this, floatingCursor, webView); 		
 		tCtrl.setParent(this, floatingCursor, webView);	
 		
 		// JavaScript ProxyBridge Parent
 		pBridge.setParent(this, floatingCursor, rCtrl);
 		
 		ScreenLocation sLoc = new ScreenLocation();
 		sLoc.setParent(this, floatingCursor);
 		 		
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
			
		//This is a dummy user entry...neeed to remove after
		appState.getDatabase().registerUser("dummy", "dummy", "dummy@example.com");		
		
		appState.getDatabase().checkCreatedTables();
		
		//OJO CON ESTO
		appState.getDatabase().deleteAllBookmarks();
		appState.getDatabase().addBookmark();		
		
		//ERASE SITE LINKS CACHE.
		appState.getDatabase().deleteSiteLinks();	
		
		String clipboard = (String) ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).getText();
		if (!clipboard.equals("")){
			boolean exist = appState.getDatabase().existClipBoard(clipboard); 
			if (!exist){		
				appState.getDatabase().insertClipBoard(clipboard);
			}
		}
		
		//appState.getDatabase().deleteWindowSetAndList();
		
		appState.getDatabase().deleteMostVisited();		
		appState.getDatabase().insertMostVisited(10, "Google", "www.google.com");
		appState.getDatabase().insertMostVisited(5, "Twitter", "www.twitter.com");
		appState.getDatabase().insertMostVisited(10, "Facebook", "www.facebook.com");
		appState.getDatabase().insertMostVisited(10, "Facebook", "www.facebook.com");	
		
		/**DETECT UNMOUNTED SDCARD.*/
		IntentFilter filter = new IntentFilter (Intent.ACTION_MEDIA_UNMOUNTED);		
		filter.addDataScheme("file"); 
		mSDInfoReceiver = new SDInfoReceiver(this);
		registerReceiver(mSDInfoReceiver, new IntentFilter(filter));
		
		// Query voice recognition and stores variable on SwifteeApplication. 
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0){
        	SwifteeApplication.setVoiceRecognitionEnabled(false);
        }		
        
        display = getWindowManager().getDefaultDisplay();
        
        tracker = GoogleAnalyticsTracker.getInstance();
        
        // Start the tracker in manual dispatch mode...
        tracker.startNewSession("UA-21271893-1", this);

       /*try {
			loadTabs(appState);
       } catch (IOException e) {				
			e.printStackTrace();
	   }*/        
        
        //Store screen size variables
        getDeviceWidthHeight();
        
        /**DATABASE**/
        //DBConnector database = appState.getDatabase();
        //database.clearAllTabs();        
        //appState.getDatabase().deleteAllClipBoards();       
        
        /**INPUT**/
        
        arrayMoreInput = new String[4][2];
        arrayMoreInput[0][0] = "COPY";
        arrayMoreInput[0][1] = "";
        arrayMoreInput[1][0] = "PASTE";
        arrayMoreInput[1][1] = "";        
        arrayMoreInput[2][0] = "CUT";
        arrayMoreInput[2][1] = "";       
        arrayMoreInput[3][0] = "SHARE";
        arrayMoreInput[3][1] = "";       
        
        
        /**PANEL**/
        
        arrayHelp = new String[4][2];
        arrayHelp[0][0] = "VIDEOS";       
        arrayHelp[0][1] = "";
        arrayHelp[1][0] = "USER GUIDE";
        arrayHelp[1][1] = "";
        arrayHelp[2][0] = "FORUM";
        arrayHelp[2][1] = "";       
        arrayHelp[3][0] = "FEEDBACK";
        arrayHelp[3][1] = "";
        
        /*arrayHelp[4][0] = "TWITTER";
        arrayHelp[4][1] = "";
        arrayHelp[5][0] = "FACEBOOK";
        arrayHelp[5][1] = "";*/
        
        /**ANCHOR**/
        
        arrayOpen = new String[3][2];
        arrayOpen[0][0] = "OPEN";       
        arrayOpen[0][1] = "";
        arrayOpen[1][0] = "NEW WINDOW";
        arrayOpen[1][1] = "";
        arrayOpen[2][0] = "BACKGROUND";
        arrayOpen[2][1] = "";        
       
        arrayMoreAnchor = new String[4][2];
        arrayMoreAnchor[0][0] = "COPY URL";       
        arrayMoreAnchor[0][1] = "";
        arrayMoreAnchor[1][0] = "COPY TITLE";
        arrayMoreAnchor[1][1] = "";
        arrayMoreAnchor[2][0] = "GESTURE";
        arrayMoreAnchor[2][1] = "";       
        arrayMoreAnchor[3][0] = "SHARE";
        arrayMoreAnchor[3][1] = "";
        
        /*arrayMoreAnchor[4][0] = "DOWNLOAD";
        arrayMoreAnchor[4][1] = "";
        arrayMoreAnchor[4][0] = "ADD BOOKMARK";
        arrayMoreAnchor[4][1] = "";*/
        
        /**TEXT**/        
        arrayText = new String[4][2];
        arrayText[0][0] = "WORD";       
        arrayText[0][1] = "";
        arrayText[1][0] = "LINE";
        arrayText[1][1] = "";
        arrayText[2][0] = "PHARAGRAPH";
        arrayText[2][1] = "";       
        arrayText[3][0] = "ALL";
        arrayText[3][1] = "";
        
        /**WINDOWS SET**/
        
        arrayOpenWindowsSet = new String[2][2];
        arrayOpenWindowsSet[0][0] = "LOAD SET";       
        arrayOpenWindowsSet[0][1] = "";
        arrayOpenWindowsSet[1][0] = "APPEND SET";
        arrayOpenWindowsSet[1][1] = "";
        
        arrayMoreWindowsSet = new String[3][2];
        arrayMoreWindowsSet[0][0] = "DELETE";       
        arrayMoreWindowsSet[0][1] = "";
        arrayMoreWindowsSet[1][0] = "DUPLICATE";       
        arrayMoreWindowsSet[1][1] = "";
        arrayMoreWindowsSet[2][0] = "SHARE";
        arrayMoreWindowsSet[2][1] = "";        

        /**SERVER**/
        
        arrayServer = new String[1][2];
        arrayServer[0][0] = "START SERVER";       
        arrayServer[0][1] = "";
        //arrayServer[1][0] = "STOP SERVER";
        //arrayServer[1][1] = "";       
        
        arrayMoreServer = new String[1][2];
        arrayMoreServer[0][0] = "SHARE LOCATION";       
        arrayMoreServer[0][1] = "";  

        
        //TAKE THIS OUT.
        /*try
        {
            android.os.Debug.dumpHprofData("/sdcard/heapdump.hprof");
        }
        catch (IOException e)
        {
              
        }*/      
     
        
    }  
        
    private String getIp(){
    	WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    	WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    	int ipAddress = wifiInfo.getIpAddress();
		String iPAddress = intToIp(ipAddress);	
		return iPAddress;
    }

    public String intToIp(int i) {
    	//return ((i >> 24 ) & 0xFF ) + "." + ((i >> 16 ) & 0xFF) + "." + ((i >> 8 ) & 0xFF) + "." + ( i & 0xFF) ; }
    	return ( i & 0xFF) + "." + ((i >> 8 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) + "." + ((i >> 24 ) & 0xFF ); 
    }
    
    public void checkOnline(boolean startUp) {   	
   	 
   	 	if(!isOnline()){
        	
   		 	if(startUp) {    		 
   		 		
   		 		/*AlertDialog.Builder startUpDialog= new AlertDialog.Builder(this);
   		 		startUpDialog.setMessage("Network not available. PadKite functionality is not guarantee. Please come back later.");
   		 		startUpDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() { 		

						public void onClick(DialogInterface dialog, int id) {         			         				
		         			//finish();         			
		         		}        	
				
   		 		});*/

   		 		//startUpDialog.show();
        
   		 	} else if (!startUp && floatingCursor.getWindowCount()>1) {
   		 		
   		 		/*AlertDialog.Builder connectedDialog= new AlertDialog.Builder(this);
   		 		connectedDialog.setMessage("Network not available. PadKite functionality is not guarantee. Would you like to save your Windows Set Session.");
   		 		
		 			connectedDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		 				public void onClick(DialogInterface dialog, int id) {         			         				
		 					
		 					
		 					/**
		 					 * SAVE	 					
		 					 * WINDOWS 
		 					 * SET 
		 					 * HERE
		 					 * **/
		 					
		 					
	         	/*		}    		 				
		 			});
   		 			
		 			connectedDialog.setNegativeButton("Do not save Set", new DialogInterface.OnClickListener() {
		 				public void onClick(DialogInterface dialog, int which) {	    	  
		 					finish();    		 			        
		 			    }});
		 			connectedDialog.show();  */ 		 		
   		 	}
   	 	}
    }
   
    public void getLinksFromPage(int identifier, String url){   
    	
    	boolean reachable = pingPage(url);   
    	
		boolean hasIdentifier = hashSiteLinks.containsKey(identifier);
		Log.v("identifier",""+identifier);
		if (!hasIdentifier){
    		Object[] obj = new Object[2];    		
	    	if (reachable){     	    		
	    		getLink = new GetLink();
	    		getLink.execute(identifier, url);    		
	    		obj[0] = SwifteeApplication.JSOUP_NOTHING;
	    		obj[1] = url;   		
	    	} else {    		
	    		obj[0] = SwifteeApplication.JSOUP_SITE_NOT_REACHABLE;    		
	    	}  	
	    	hashSiteLinks.put(identifier, obj);
    	}
    } 
    
    
    private AsyncTask getLink = new GetLink();   
    
    private class GetLink extends AsyncTask  {    
          
		@Override
		protected Object doInBackground(Object... arg) {					
			
			Document doc = null;
			
			int id = (Integer) arg[0];		
			String url = (String) arg[1];
			
			if(url!=null) {		
				
				if (hashSiteLinks!=null) { 
					
					Object[] obj = (Object[]) hashSiteLinks.get(id);					
					obj[0] = SwifteeApplication.JSOUP_CALLED;			
				
					try {
						doc = Jsoup.connect(url).get();
						
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					//http://jsoup.org/apidocs/org/jsoup/select/Selector.html			
					//http://www.gotoquiz.com/web-coding/programming/java-programming/web-scraping-in-java-with-jsoup-part-2-how-to/				
					Elements links = doc.select("a[href]"); 
					
					//Elements videos = doc.select("iframe[src~=(youtube\\.com|vimeo\\.com)], object[data~=(youtube\\.com|vimeo\\.com)], embed[src~=(youtube\\.com|vimeo\\.com)]");
								
					if (links.size()>0) {
						
						obj[0] = SwifteeApplication.JSOUP_LOADED;			
						obj[1] = links;
						hashSiteLinks.put((Integer) arg[0], obj);						
					}		 
					
					/*if (videos.size()>0) {
						hashSiteVideos.put((Integer) arg[0], videos);
					}*/	
							
					if (tabsActivated){
						
						int cType = floatingCursor.getcType(); 
						int identifier = floatingCursor.getIdentifier();
								
						int lastKnownWebHitType = floatingCursor.getlastKnownHitType();
		
						if  ( ( cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE 
							|| lastKnownWebHitType ==  WebHitTestResult.TYPE_SRC_ANCHOR_TYPE )
							&& (identifier == (Integer) arg[0])
							){ 
							
							if (links.size()>0) {
								parseSiteLinks((Integer) arg[0], (String) arg[1], true);
							}
							/*if (videos.size()>0) {
								parseSiteVideos((Integer) arg[0], (String) arg[1]);
							}*/
							
						}				
					}
				}
			}
			//SwifteeApplication.setAchorSpinnerOn(false);
			
	    	return false;
		}		
	} 
    
    public void parseSiteLinks(int identifier, String masterUrl, boolean fromGetLink){   
    	
    	Object[] obj = (Object[]) hashSiteLinks.get(identifier); 	    	
    	Elements links = (Elements) obj[1];
    	
    	int num=0;
    	int size = 0;
    	if (links.size()>51){
    		size = 51;
    	} else {
    		size = links.size();
    	}
    	arraySiteLinks = new String[size][2];    
    	int[] screenSize = getDeviceWidthHeight();
    	int screenWidth = screenSize[0];
    	
    	for (Element link : links){	
    		
    		String title = link.text();    		
    		
    		Paint pText = new Paint();
    		int textSize = (int) pText.measureText(title);   				   		
    		
    		String[] hr = link.toString().split("href=\"");
    		String[] ef = hr[1].split("\"");		    		
    		String url = "";
    		
    		if (!ef[0].contains("http")){
    			url = link.baseUri() + ef[0];		    				    			
    		} else {   			
    			url = ef[0];		    			
    		}
    		
    		if (title.equals("")){    			
    			title = url;  
    			String base = link.baseUri();
    			String t = title.replace(base, "");
    			title = t;
    		}
    		
    		if ( textSize > (screenWidth/3) ){
    			title = title.substring(0, 25);
    			title += "...";
    		}
    		if (title==null){
    			Log.v("stop","stop");
    		}
    		
    		arraySiteLinks[num][0] = title;
    		arraySiteLinks[num][1] = url;    		
    		
    		//appState.getDatabase().insertSiteLinks(masterUrl, identifier, title, url);
    		
    		Log.v("link", " id: " + identifier + " num: " + num +  
    				" :: title: "+ title +    							
    				" url: " + url);  	  
    		
    		num++;
    		   		    		
    		if (num==20)
    			break;  	
    		
    	} 	

    	obj[0] = SwifteeApplication.JSOUP_PARSED;  	

    	arraySECOND = arraySiteLinks;   
    	
    	//Set links tab
    	//floatingCursor.setLinkTab(true);
    	
    	if (tabsActivated && fromGetLink){ 
    		clearViewsIdentifiers();    		
    		boolean expanded = SwifteeApplication.getExpanded();   
    		SwifteeApplication.setActiveTabIndex(SwifteeApplication.TABINDEX_SECOND);
    		floatingCursor.switchTabIndex(SwifteeApplication.TABINDEX_SECOND);
    		floatingCursor.ceroAnchor(expanded, true);   		
    		floatingCursor.refreshSuggestions(SwifteeApplication.TABINDEX_SECOND, arraySiteLinks, SwifteeApplication.TABINDEX_SECOND, COLOR.BLACK, expanded);
    		rCtrl.setTabToTop(SwifteeApplication.TABINDEX_SECOND, SwifteeApplication.DRAW_INPUT_TABS);		
    	}    	
    		
    }
    
    public boolean pingPage(String url){    	
    	Socket socket = null;
    	boolean reachable = false;    	
    	try {    		
    	    try {    	    	
				socket = new Socket(url, 80);				
			} catch (UnknownHostException e) {				
				e.printStackTrace();				
			} catch (IOException e) {				
				e.printStackTrace();				
			}    	    
    	    reachable = true;    	    
    	} finally {             		
    	    if (socket != null) try { socket.close(); } catch(IOException e) {}
    	}    	
    	return reachable;    	
    }
    
	public void getImagesFromPage(int identifier, String url){  
	    
		SwifteeApplication.setPadKiteInputSpinnerStatus(SwifteeApplication.SUGGESTION_DATA_CALLED);
		
		//url = url + "#p=0";
		
    	boolean reachable = pingPage(url);    	
		
    	Object[] obj = new Object[2];   	
    	
    	if (reachable){     	    		
    		getImage = new GetImages();
    		getImage.execute(identifier, url);   				
    	} 
    	hashSiteImages.put(identifier, obj);
    } 

    private AsyncTask getImage = new GetImages();  
    
    
    private class GetImages extends AsyncTask  {    
          
		@Override
		protected Object doInBackground(Object... arg) {					
			
			Document doc = null;
			
			Object[] obj = (Object[]) hashSiteImages.get((Integer) arg[0]);
			obj[0] = SwifteeApplication.JSOUP_CALLED;		
		
			try {		
								
				doc = Jsoup.connect((String) arg[1]).timeout(20000).get();				
				//String ua = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.122 Safari/534.30";
				//String page = (String) arg[1];
				//doc = Jsoup.connect(page).userAgent(ua).get();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//Elements videos = doc.select("iframe[src~=(youtube\\.com|vimeo\\.com)], object[data~=(youtube\\.com|vimeo\\.com)], embed[src~=(youtube\\.com|vimeo\\.com)]");
			//Elements images = doc.select("div:matches()");
			
			Elements images = doc.select("img");	
			
			int num = 0;
	    	int size = images.size();
	    	if (size>51){
	    		size = 51;
	    	} else {
	    		size = images.size();
	    	}
	    	
	    	arraySiteImages = new String[size-1][2];  	//-2 cause we dont want google icon
	    		    	
	    	for (Element image : images){  	    		
	    		String[] raw = image.toString().split("<img src=\"");
	    		String[] urlRaw = raw[1].split("\"");
	    		String url = urlRaw[0]; 
	    		if (!url.equals("http://www.gstatic.com/m/images/logo_small.gif")){
	    			arraySiteImages[num][0] = url;	    			
	    			arraySiteImages[num][1] = url;
	    			num++;
	    		}    			    		
	    	}
	    	
	    	imageArraySize = size-1;
	    	imageBitmaps = new Bitmap[size-1];
	    	loadImage();
	    	return false;
		}		
	}  
    
    private void loadImage() {
    	for (int i=0; i<arraySiteImages.length; i++){
    		String url = arraySiteImages[i][0];
    		new AsyncImageLoader(url, this, 0);
    	}        
    	countI=0;
    }
    
    public Bitmap[] imageBitmaps; 
    public boolean hasImageBitmaps() {
    	if (imageBitmaps!=null){
    		if (imageBitmaps.length>0){
    			return true;
    		}    			
    	}
		return false;
	}
    
    int imageArraySize;
    public Bitmap getImageBitmaps(int i) { return imageBitmaps[i]; }
	int countI;
    
    @Override
	public void onImageReceived(String url, Bitmap bm) {   	
    	
    	if (countI==0){   		
    		arrayCERO = null;		
    		arrayCERO  = new String[imageArraySize][2];
    		arraySiteImages = new String[imageArraySize][2];
    		imageBitmaps = new Bitmap[imageArraySize+1];    			
    	}    	
    	
    	arraySiteImages[countI][0] = url;   	
    	imageBitmaps[countI] = bm;
    	
    	if (countI==imageArraySize-1){
    		arrayCERO = arraySiteImages;   		
    		reloadCeroAfterSuggestion();
    		SwifteeApplication.setPadKiteInputSpinnerStatus(SwifteeApplication.SUGGESTION_DATA_LOADED);
     	} else {
     		countI++;
     	}    	
	}    
    
	public int getImageArraySize() {
		return imageArraySize;
	}
	
	
	/**GET VIDEOS**/
	
	public void getVideosFromPage(int identifier, String url){  
	    
		SwifteeApplication.setPadKiteInputSpinnerStatus(SwifteeApplication.SUGGESTION_DATA_CALLED);
		
		//url = url + "#p=0";
		
    	boolean reachable = pingPage(url);    	
		
    	Object[] obj = new Object[2];   	
    	
    	if (reachable){     	    		
    		getVideo = new GetVideos();
    		getVideo.execute(identifier, url);   				
    	} 
    	hashSiteVideos.put(identifier, obj);
    } 
	
	
	private AsyncTask getVideo = new GetVideos();  
    private Vector vectorImagesThumbnails = new Vector();
    
    private class GetVideos extends AsyncTask  {    
          
		@Override
		protected Object doInBackground(Object... arg) {					
			
			Document doc = null;
			
			Object[] obj = (Object[]) hashSiteVideos.get((Integer) arg[0]);
			obj[0] = SwifteeApplication.JSOUP_CALLED;		
		
			try {				
				doc = doc = Jsoup.connect((String) arg[1]).userAgent("Mozilla").timeout(10000).get();			
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//Elements videos = doc.select("iframe[src~=(youtube\\.com|vimeo\\.com)], object[data~=(youtube\\.com|vimeo\\.com)], embed[src~=(youtube\\.com|vimeo\\.com)]");
			//Elements images = doc.select("div:matches()");
			
			//http://jsoup.org/apidocs/org/jsoup/select/Selector.html
			
			/*Elements links = doc.select("a[href]");*/    	
			
			Elements tbody = doc.select("tbody");
			
			int finalSize =0;
			for (Element body : tbody){
				String raw = body.toString();
				if (raw.contains("rtsp")){
					finalSize++;
				}
			}
			
			arraySiteVideos = new String[finalSize-1][3];  
			
	    	int count =0;
				
			int cV = 0;
			String from = null;
				
	    	
			for (Element body : tbody){
				
				boolean srcCheck 		= false;
				boolean titleUrlCheck	= false;				
				
				String videoId 		= "";
	    		String imageId 		= "";
	    		String imgUrl 		= "";
	    		String watch 		= "";
	    		String videoTitle 	= "";	  
	    		String repro		= "";
	    		String videoUrl 	= "";
	    		String extra 		= "";
	    		
	    		String raw = body.toString();
	    		
	    		if (raw.contains("rtsp")){    			
	    		
		    		if (raw.contains("src")){
						String[] rawSrc = raw.split("src");					
		    			String rawImg = rawSrc[1];  	 
		    			String[] rawUrl = rawImg.split("\"");
		    			imgUrl = rawUrl[1];     			
		    			srcCheck=true;
					}  				
					
					if (raw.contains("accesskey")){
						
						String[] warKey = raw.split("accesskey");
						String[] hrefMain = warKey[1].split("href");
						String[] hrefRaw = hrefMain[1].split("\"");
						videoUrl = hrefRaw[1];    			
						
						String videoAllTitle = body.text();
						String[] spT = videoAllTitle.split(":"); 
						String tB = spT[0]; 
						videoTitle =  tB.substring(0, tB.length()-2);
						String tT = spT[1];
							
						extra = tT.substring(4, tT.length());		
						
		    			titleUrlCheck=true;
		    		}			
		    			
		    		if (srcCheck && titleUrlCheck){
	    				arraySiteVideos[count][0] = videoTitle;	
		    			arraySiteVideos[count][1] = videoUrl;	    	
		    			arraySiteVideos[count][2] = extra;
		    			vectorImagesThumbnails.add(imgUrl);
	    				count++;	    				
	    			}
	    		}
	    		
			}
			
	    	videoArraySize = finalSize-1;
	    	videoBitmaps = new Bitmap[finalSize-1];
	    	loadVideoImage();
	    	return false;
	    	
		}		
	}  
    
    private void loadVideoImage() {
    	for (int i=0; i<vectorImagesThumbnails.size(); i++){
    		String url = (String) vectorImagesThumbnails.get(i);
    		new AsyncImageLoader(url, this, 1);
    	}        
    	countI=0;
    }
    
    public Bitmap[] videoBitmaps;   
    public boolean hasVideoBitmaps() {
    	if (videoBitmaps!=null){
    		if (videoBitmaps.length>0){
    			return true;
    		}    			
    	}
		return false;
	}

	int videoArraySize;
    public Bitmap getVideoBitmaps(int i) {    	
    	return videoBitmaps[i]; 
    }
	int countV; 
	
	
    public int getVideoBitmapsArraySize() {    	
    	return videoBitmaps.length; 
    }
	
	@Override
	public void onVideoReceived(String url, Bitmap bm) {   	
    	
    	if (countV==0){   		
    		arrayCERO = null;		
    		arrayCERO  = new String[videoArraySize][2];    	
    		videoBitmaps = new Bitmap[videoArraySize];    			
    	} 
    	
    	videoBitmaps[countV] = bm;
    	
    	if (countV==videoArraySize-1){
    		arrayCERO = arraySiteVideos;   		
    		SwifteeApplication.setCType(SwifteeApplication.TYPE_PADKITE_INPUT);
    		SwifteeApplication.setIsArrayVideo(true);
    		reloadCeroAfterSuggestion();
    		SwifteeApplication.setPadKiteInputSpinnerStatus(SwifteeApplication.SUGGESTION_DATA_LOADED);    		
     	} else {
     		countV++;
     	}    	
	}  
	
	public int getVideoArraySize() {
		return videoArraySize;
	}		
	/**END OF VIDEOS**/
	
	/**GET WIKI**/
	public void getWikiFromApi(String url){  
	    
		SwifteeApplication.setPadKiteInputSpinnerStatus(SwifteeApplication.SUGGESTION_DATA_CALLED);	
		
    	boolean reachable = pingPage(url);    	
		
    	Object[] obj = new Object[2];   	
    	
    	if (reachable){     	    		
    		getWiki = new GetWikis();
    		getWiki.execute(url);   				
    	}    	
    	
    } 

    private AsyncTask getWiki = new GetWikis();     
    
    private class GetWikis extends AsyncTask {    
          
		@Override
		protected Object doInBackground(Object... arg) {		
			
			String searchText = (String) arg[0];
		
			ret = getJSONSuggestions(searchText, true);
			int length = ret.length();
			arrayWiki = new String[length][4];
			SwifteeApplication.setIsArrayWiki(true);			
					
			arrayCERO = null;		
			arrayCERO  = new String[length][2];
				
			try {
				
				arrayCERO=null;
				arrayCERO  = new String[ret.length()][2];
				
		        for (int i = 0; i < ret.length(); i++) {
		        	
		        	Log.v("status", "json: "+ ret.getString(i));
		        	String lineRes = ret.getString(i);
		        	
		        	boolean hasExtra = false;
		        	String extra = "";    	
		        		
	        		JSONObject json_data = ret.getJSONObject(i);
	        		
	        		if (json_data.has("url")){
	        			String url = json_data.getString("url");
	        			arrayWiki[i][1] = url;	 
	        		} 		
	        		
	        		if (json_data.has("title")){
	        			String title = json_data.getString("title");
	        			arrayWiki[i][0] = title;	
	        		}        		
	        		
	        		if (json_data.has("abstract")){
	        			String abstrac = json_data.getString("abstract");	  
	        			arrayWiki[i][2] = abstrac;		        			
	        		}
	        		
	        		if (json_data.has("wikipedia_id")){
	        			String wikipedia_id = json_data.getString("wikipedia_id");
	        			arrayWiki[i][3] = wikipedia_id;		        			
	        		}      		
		        
		        }       
		       
		        	        	
		        hasWiki=true;
		        arrayCERO = arrayWiki;
		             
		        
		        runOnUiThread(new Runnable() {		        	
		        	public void run() {        		
		        		reloadCeroAfterSuggestion();
		            }
		        });	
		        
			} catch (JSONException e) {
				Log.v("error", "error:" +e);			    
			}
			
			return false;		
			
		}
			
	}  
	
	/**END OF WIKI**/
	
	
	public void clearViewsIdentifiers(){
    	rCtrl.setIdentifier(0);
    	sCtrl.setIdentifier(0);
    }
    
    /*public void parseSiteVideos(int identifier, String masterUrl){   
    	
    	Object[] obj = (Object[]) hashSiteVideos.get(identifier); 	    	
    	Elements videos = (Elements) obj[1];
    	
    	int num=0;
    	int size = 0;
    	if (videos.size()>51){
    		size = 51;
    	} else {
    		size = videos.size();
    	}
    	arraySiteVideos = new String[size][2];    
    	int[] screenSize = getDeviceWidthHeight();
    	int screenWidth = screenSize[0];
    	
    	for (Element video : videos){	
    		
    		String title = video.text();
    		
    		Log.v("link", " id: " + identifier + " num: " + num +  
    				" :: title: "+ title );  	    		
    		num++;
    		
    		if (num==20)
    			break;  	
    		
    	}
    	
    }*/
  
    
    /*private boolean fileChecker(String link){    	
    	if (link.endsWith(".jpg") || link.endsWith(".jpeg")
				|| link.endsWith(".png")
				|| link.endsWith(".gif")
				|| link.endsWith(".bmp")
				|| link.endsWith(".pdf")
				|| link.endsWith(".doc")
				|| link.endsWith(".txt")){
    		return true;
    	}
    	return false;
    }*/
	
	public int siteIdentifierLoaded(int identifier){
		
		int status = SwifteeApplication.LINK_DATA_NOT_CALLED;
		
		boolean containsId = hashSiteLinks.containsKey(identifier);
		
		if (containsId){			
			
			Object[] obj = (Object[]) hashSiteLinks.get(identifier);
			int currentStatus = (Integer) obj[0];
			
			switch (currentStatus){			
				case SwifteeApplication.JSOUP_SITE_NOT_REACHABLE:	
					status = SwifteeApplication.LINK_DATA_NOT_REACHABLE;
					break;
				case SwifteeApplication.JSOUP_NOTHING:	
					status = SwifteeApplication.LINK_DATA_NOT_CALLED;
					break;
				case SwifteeApplication.JSOUP_CALLED:
					status = SwifteeApplication.LINK_DATA_CALLED;
					break;
				case SwifteeApplication.JSOUP_LOADED:
					status = SwifteeApplication.LINK_DATA_LOADED;
					break;
				case SwifteeApplication.JSOUP_PARSED:
					status = SwifteeApplication.LINK_DATA_PARSED;
					break;
			}			
		}							
		return status;
	}
    
    public boolean arrayHasData(int index){
    	boolean is = false;
    	int length;
    	switch (index){
			case 0:
				if (arrayCERO!=null){					
					length = arrayCERO.length;								
					if (length>0){
						is=true;
					} else {
						is=false;	
					}
				} else {
					is=false;	
				}
				break;
			case 1:
				if (arrayFIRST!=null){				
					length = arrayFIRST.length;								
					if (length>0){
						is=true;
					} else {
						is=false;	
					}	
				} else {
					is=false;	
				}
				break;
			case 2:
				if (arraySECOND!=null){			
					length = arraySECOND.length;									
					if (length>0){
						is=true;
					} else {
						is=false;	
					}	
				} else {
					is=false;	
				}								
				break;
			case 3:		
				if (arrayTHIRD!=null){			
					length = arrayTHIRD.length;			
					if (length>0){
						is=true;
					} else {
						is=false;	
					}	
				} else {
					is=false;	
				}									
				break;
			case 4:				
				if (arrayFOURTH!=null){
					length = arrayFOURTH.length;
					if (length>0){
						is=true;
					} else {
						is=false;	
					}	
				} else {
					is=false;	
				}												
				break;	
    	}
		return is;
    }
    
    public boolean arrayBiggerThanList(int index){
    	
    	boolean isBigger = false;    	
    	int amountOfRows = SwifteeApplication.getAmountOfRows();
    	
    	switch (index){
			case 0:
				if (arrayCERO.length>amountOfRows){					
					isBigger = true;
				} else {
					isBigger = false;	
				}
				break;
			case 1:	
				if (arrayFIRST.length>amountOfRows){					
					isBigger = true;
				} else {
					isBigger = false;	
				}				
				break;
			case 2:
				if (arraySECOND.length>amountOfRows){					
					isBigger = true;
				} else {
					isBigger = false;	
				}							
				break;
			case 3:		
				if (arrayTHIRD.length>amountOfRows){					
					isBigger = true;
				} else {
					isBigger = false;	
				}									
				break;
			case 4:				
				if (arrayFOURTH.length>amountOfRows){					
					isBigger = true;
				} else {
					isBigger = false;	
				}											
				break;	
    	}
		return isBigger;
    }

    
    public void clearArrays() {
    	this.arrayCERO = null;			
    	this.arrayFIRST = null;
    	this.arraySECOND = null;
    	this.arrayTHIRD = null;
    	this.arrayFOURTH = null;
		
	}
   
    
    @Override
    protected void onDestroy() {
      super.onDestroy();
      System.gc();
      // Stop the tracker when it is no longer needed.
      tracker.stopSession();
      if(mSDInfoReceiver!=null)
		  unregisterReceiver(mSDInfoReceiver);
    }    
    
    public void queryPackageManager(){
    	
    	// get the package manager object
        PackageManager pm = getPackageManager();
        // create an Intent with ACTION_MAIN and CATEGORY LAUNCHER
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
 
        // query the package manager for the desired intent
        // obtain the list of ResolveInfo objects
        List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);
        for (ResolveInfo resolveInfo : list) {
 
            ActivityInfo activityInfo = resolveInfo.activityInfo;
 
            // get the name of the Main activity class
            String activityName = activityInfo.name;
 
            ApplicationInfo applicationInfo = activityInfo.applicationInfo;
            // get the name of the application class
            String applicationName = applicationInfo.className;
 
            if (applicationName != null) {
                Log.v("APP_NAME", applicationName);
 
            }
 
            if (activityName != null) {
                Log.v("ACTIVITY_NAME", activityName);
            }
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

    public void send(String title, String share)
	{
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        if (title != null)
        intent.putExtra(Intent.EXTRA_SUBJECT, title);        
        intent.putExtra(Intent.EXTRA_TEXT, share);
        startActivity(Intent.createChooser(intent, "Share ..."));		
	}
    
    public void openMap(String url){
    	Intent searchAddress = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); 
        startActivity(searchAddress); 
    }
    
    public void sendMail(String url){
    	MailTo mt = MailTo.parse(url);
        Intent i = newEmailIntent(this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
        startActivity(i);
    }
    
    public void playVideo(String url){    	
    	Intent intent = new Intent(Intent.ACTION_VIEW); 
		intent.setDataAndType(Uri.parse(url), "video/3gpp");
		intent.setClassName("com.google.android.youtube","com.google.android.youtube.PlayerActivity");
		startActivity(intent);
    }
    
    public static Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }
    
    public static void drawTip(Rect re, String[] comment, float X, float Y){    	
    	floatingCursor.drawTip(re, comment, X, Y, SwifteeApplication.IS_FOR_CIRCULAR_MENU_TIPS);
    }
    
    public static void drawNothingTip(){
    	Rect re = new Rect();
    	String[] rectTip = {""};
    	int[] cord = {0,0};
    	Object[] param = {re, rectTip, cord, floatingCursor.getVertical(), 0}; 
    	tCtrl.drawNothing();
    	tCtrl.setTipComment(param, SwifteeApplication.IS_FOR_CIRCULAR_MENU_TIPS);
    }
    
    public void drawNothingRing(){    	
    	rCtrl.drawNothing();
    }   
    
    public void insertMostVisited(){
    	
    }
   
    public void insertClipBoard(String clip){    	
    	appState.getDatabase().insertClipBoard(clip);
    }
    
    public void insertRecent(String recent){    	
    	appState.getDatabase().insertRecent(recent);
    }
    
    public void injectSuggestionArray(String[][] suggestion){
    	
    	for (int i=0; i<suggestion.length; i++){
    		String text = suggestion[i][0];
    		appState.getDatabase().insertSuggestion(text);
    	}
    	
    }
    
    public void loadClipBoardIntoArray(){    
    	
	    Cursor clip = appState.getDatabase().getClipBoard();  				
	    if (clip!=null){
			
			int count = clip.getCount();
			clip.moveToPosition(0);
			int num = 0;
			arrayClipBoard = new String[count][2];			
			while (!clip.isAfterLast() && num < count){			
				int clipCol = clip.getColumnIndex("desc");
				String clipboard = clip.getString(clipCol);
				if ( (!clipboard.equals("")) && (clipboard!=null) && (!clipboard.equals("null")) ){
					arrayClipBoard[num][0] = clipboard;
					arrayClipBoard[num][1] = "";
				}
				num++;						
				clip.moveToNext();		
			}		
			clip.close();
		}  
        	    
    }     
    
    public void loadRecentIntoArray(){    	
    	
	    Cursor recent = appState.getDatabase().getRecent();
	    if (recent!=null){			
			int count = recent.getCount();
			recent.moveToPosition(0);
			int num = 0;
			arrayRecent = new String [count][2];			
			while (!recent.isAfterLast() && num < count){
				int re = recent.getColumnIndex("desc");
				String second = recent.getString(re);
				arrayRecent[num][0] = second;			
				arrayRecent[num][1] = "";
				num++;						
				recent.moveToNext();		
			}		
			recent.close();
		}	    
	    
    }
    
     
    
	/*Cursor links = appState.getDatabase().getSiteLinks(identifier);  				
	if (links!=null){				
		int count = links.getCount();
		links.moveToPosition(0);
		int num = 0;
		arraySiteLinks = new String[count][2];			
		while (!links.isAfterLast() && num < count){			
			int ti = links.getColumnIndex("title");
			String title = links.getString(ti);
			int ur = links.getColumnIndex("url");
			String url = links.getString(ur);
			arraySiteLinks[num][0] = title;
			arraySiteLinks[num][1] = url;				
			num++;						
			links.moveToNext();		
		}		
		links.close();
	}*/ 
	 
    
    public void insertVoice(String voice){    	
    	appState.getDatabase().insertVoice(voice);
    }
    
    public void loadVoiceIntoArray(){    	
	    Cursor voice = appState.getDatabase().getVoice();  				
	    if (voice!=null){			
			int count = voice.getCount();
			voice.moveToPosition(0);
			int num = 0;
			arrayVoice = new String [count][2];			
			while (!voice.isAfterLast() && num < count){			
				String suggestion = voice.getString(1);
				arrayVoice[num][0] = suggestion;			
				arrayVoice[num][1] = "";
				num++;						
				voice.moveToNext();		
			}		
			voice.close();
		}
    }    
    
    public String[][] getArrayVoice() {
		return arrayVoice;
	}


	public void loadMostVisitedIntoArray(){    	
	    Cursor most = appState.getDatabase().getMostVisited();  				
	    if (most!=null){
	    	int count = most.getCount();	
	    	most.moveToFirst();
	    	int num = 0;
	    	arrayMostVisited = new String[count][2];
	    	while ( !most.isAfterLast() && num < count ) {
	    		String title = most.getString(most.getColumnIndex("title"));
				String url = most.getString(most.getColumnIndex("url"));
				String amount = most.getString(most.getColumnIndex("amount"));								
				arrayMostVisited[num][0] = title; 
				arrayMostVisited[num][1] = url+"|"+amount;				
				num++;
				most.moveToNext();
			}
	    	most.close();    
	    	arrayCERO = arrayMostVisited;
		}
    }   
    
    public void loadBookMarkIntoArray(){    	
	    Cursor book = appState.getDatabase().getBookmarks();  				
	    if (book!=null){
	    	int count = book.getCount();
	    	book.moveToFirst();
	    	int num = 0;
	    	arrayBookmarks = new String[count][2];
	    	while ( !book.isAfterLast() && num < count ) {    		
				int nameIndex = book.getColumnIndex("name");	
				String name = book.getString(nameIndex);
				//if(name.equals("Cancel")){
				//	book.moveToNext();
				//}
				int urlIndex = book.getColumnIndex("url");	
				String url = book.getString(urlIndex);				
				arrayBookmarks[num][0] = name; 
				arrayBookmarks[num][1] = url;				
				num++;
				book.moveToNext();
			}
			book.close();  
			arrayFIRST = arrayBookmarks;
		}
    }   
   
    public void loadHistoryIntoArray(){    	
	    Cursor hist = appState.getDatabase().getFromHistoryIntoArray(1);  				
	    if (hist!=null){
	    	int count = hist.getCount();			
	    	int num = 0;	    	
	    	arrayHistory = new String[count][2];
	    	hist.moveToFirst();
	    	while ( !hist.isAfterLast() && num < count ) {    		
				String url = hist.getString(hist.getColumnIndex("url"));						
				String title = hist.getString(hist.getColumnIndex("title"));
				arrayHistory[num][0] = title; 
				arrayHistory[num][1] = url;				
				num++;
				hist.moveToNext();
			}
	    	hist.close();  
	    	arraySECOND = arrayHistory;
		}
    }  
    
    public void loadDownloadIntoArray(){   
    	
		arrayDownload = new String[20][2];
		arrayDownload[0][0] = "0";
		arrayDownload[0][1] = "";
		arrayDownload[1][0] = "1";
		arrayDownload[1][1] = "";
		arrayDownload[2][0] = "2";
		arrayDownload[2][1] = "";
		arrayDownload[3][0] = "3";
		arrayDownload[3][1] = "";
		arrayDownload[4][0] = "4";
		arrayDownload[4][1] = "";
		arrayDownload[5][0] = "5";
		arrayDownload[5][1] = "";
		arrayDownload[6][0] = "6";
		arrayDownload[6][1] = "";
		arrayDownload[7][0] = "7";
		arrayDownload[7][1] = "";
		arrayDownload[8][0] = "8";
		arrayDownload[8][1] = "";
		arrayDownload[9][0] = "9";
		arrayDownload[9][1] = "";
		arrayDownload[10][0] = "10";   
		arrayDownload[10][1] = "";
		arrayDownload[11][0] = "11";
		arrayDownload[11][1] = "";
		arrayDownload[12][0] = "12";
		arrayDownload[12][1] = "";
		arrayDownload[13][0] = "13";
		arrayDownload[13][1] = "";
		arrayDownload[14][0] = "14";
		arrayDownload[14][1] = "";
		arrayDownload[15][0] = "15";
		arrayDownload[15][1] = "";
		arrayDownload[16][0] = "16";
		arrayDownload[16][1] = "";
		arrayDownload[17][0] = "17";
		arrayDownload[17][1] = "";
		arrayDownload[18][0] = "18";
		arrayDownload[18][1] = "";
		arrayDownload[19][0] = "19";
		arrayDownload[19][1] = "";
		
        
        arrayTHIRD = arrayDownload;
        
        /*Cursor down = appState.getDatabase().getFromHistory(2);  				
	    if (down!=null){
	    	int count = down.getCount();			
	    	int num = 0;
	    	arrayDownload = new String[count][2];
	    	while ( !down.isAfterLast() && num < count ) {    		
				String url = down.getString(down.getColumnIndex("url"));						
				String title = down.getString(down.getColumnIndex("title"));				
				arrayDownload[num][0] = title; 
				arrayDownload[num][1] = url;				
				num++;
				down.moveToNext();
			}
	    	down.close();
	    	arrayTHIRD = arrayDownload;
		}*/
    }  
    public void loadSuggestionIntoArray(String like){    	
	    Cursor sugg = appState.getDatabase().getSuggestion(like);		
	    if (sugg!=null){			
			int count = sugg.getCount();
			sugg.moveToPosition(0);
			int num = 0;
			arraySuggestion = new String[count][2];			
			while (!sugg.isAfterLast() && num < count){
				int columnIndex = sugg.getColumnIndex("desc");
				String suggestion = sugg.getString(columnIndex);
				arraySuggestion[num][0] = suggestion;			
				num++;						
				sugg.moveToNext();		
			}		
			sugg.close();
			arrayCERO = arraySuggestion; 
		}
    }
	    
    public void loadWindowsSetIntoArryaById(int id){    	
	    Cursor win = appState.getDatabase().getWindowsManagerListById(id);		
	    if (win!=null){			
			int count = win.getCount();
			win.moveToPosition(0);
			int num = 0;
			arrayWindowsSet = new String[count][2];			
			while (!win.isAfterLast() && num < count){
				int titleIndex = win.getColumnIndex("title");
				String title = win.getString(titleIndex);
				if (title.length()>50){
					
				}
				arrayWindowsSet[num][0] = title;
				int urlIndex = win.getColumnIndex("url");
				String url = win.getString(urlIndex);
				arrayWindowsSet[num][1] = url;			
				num++;						
				win.moveToNext();		
			}		
			win.close();
			arrayCERO = arrayWindowsSet; 
		}
    }
    
    Random generator = new Random();
    JSONArray ret;
    
    public void launchSuggestionSearch(String phrase) {	      
    	
   	 checkOnline(false);
   	
		int suggestionEngine = SwifteeApplication.getActiveSearchCheck();
		
		phrase = convertURL(phrase);
		
		String searchText = null;   		
		
		switch(suggestionEngine){
			case SwifteeApplication.WEB_SEARCH:
				searchText = SwifteeApplication.getGoogleSuggestion()+phrase;
				break;
				
			case SwifteeApplication.WIKI_BUTTON:
				//Wikipedia moe info http://code.google.com/p/jwpl/
				//searchText = SwifteeApplication.getWikipediaSuggestion()+phrase;
				searchText = SwifteeApplication.getWikiAbstract()+phrase;		
				getWikiFromApi(searchText);									
				return;
				
			case SwifteeApplication.VIDEO_BUTTON:
				searchText = SwifteeApplication.getYouTubeSearch()+phrase;
				getVideosFromPage(10000, searchText);
				return;
			
			case SwifteeApplication.IMAGE_BUTTON:
				searchText = SwifteeApplication.getImageSearch()+phrase;				
				getImagesFromPage(10001, searchText);
				return;
				
			default:
				searchText = SwifteeApplication.getGoogleSuggestion()+phrase;
				break;    	
		}
		
		Log.v("status", ""+searchText);

		ret = null;
		ret = getJSONSuggestions(searchText, false);
		
		int length = ret.length();
		
		arrayCERO = null;		
		arrayCERO  = new String[length][2];
			
		
		try {
			arrayCERO=null;
			arrayCERO  = new String[ret.length()][2];
	        for (int i = 0; i < ret.length(); i++) {
	        	Log.v("status", "json: "+ ret.getString(i));
	        	arrayCERO[i][0] = ret.getString(i);
	        	arrayCERO[i][1] = "";
	        } 	        
	        runOnUiThread(new Runnable() {		        	
	        	public void run() {        		
	        		reloadCeroAfterSuggestion();
	            }
	        });	
		} catch (JSONException e) {
			Log.v("error", "error:" +e);			    
		}		
	}   
    
    private boolean hasWiki;
    
    public boolean getHasWiki(){
    	return hasWiki;
    }
    
    public boolean setHasWiki(boolean has){
    	return hasWiki;
    }
    
    private void reloadCeroAfterSuggestion(){ 	
    	floatingCursor.setRowsHeightAndCount(SwifteeApplication.TABINDEX_NOTHING, arrayCERO, true);
		String[] buttons = {"■", "IMAGES", "VIDEOS", "WIKI", "▼", "▲" };		
		Object[] param_TEXT = { COLOR.PANTONE_192C_MAIN, arrayCERO, buttons, true, floatingCursor.getHitPKIndex()};
		int randomIdentifier = generator.nextInt();
		sCtrl.setDrawStyle(SwifteeApplication.TAB_SUGGESTIONS, param_TEXT, randomIdentifier); 
		injectSuggestionArray(arrayCERO);
		SwifteeApplication.setPadKiteInputSpinnerStatus(SwifteeApplication.SUGGESTION_DATA_LOADED);
		floatingCursor.switchTabIndex(SwifteeApplication.TABINDEX_CERO); //ReserReadyToCero
    }
    
	private HttpClient httpClient;
	
	public JSONArray getJSONSuggestions(String url, boolean wiki) {
		HttpGet method = new HttpGet(url);
		HttpResponse response;
		try {				
			response = httpClient.execute(method);			
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				
				//if (youtube){					
				//	callback = callback.substring(17, callback.length()-1);					
				//} 
				
				if (wiki){	
					
					HttpEntity entity = response.getEntity();
					JSONArray wikiArray = null;
					
					if (entity != null) {
	                    // A Simple JSON Response Read
	                    InputStream instream = entity.getContent();
	 
	                    // Load the requested page converted to a string into a JSONObject.
	                    JSONObject wikiObj = new JSONObject(convertStreamToString(instream));                   
	 
	                    // Make array of the suggestions
	                    wikiArray = wikiObj.getJSONArray("results");	 
	 
	                    // Cose the stream.
	                    instream.close();
	                }
					return wikiArray;
					//return new JSONArray(callback).getJSONArray(0);
				} else {
					String callback  = EntityUtils.toString(response.getEntity());
					return new JSONArray(callback).getJSONArray(1);					
				}
							
			}
		} catch(Exception e) {
			Log.v("error", "error:" +e);			
		}
		return null;
	}
	
	 private static String convertStreamToString(InputStream is) {
	        /*
	         * To convert the InputStream to String we use the BufferedReader.readLine()
	         * method. We iterate until the BufferedReader return null which means
	         * there's no more data to read. Each line will appended to a StringBuilder
	         * and returned as String.
	         */
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        StringBuilder sb = new StringBuilder();
	 
	        String line = null;
	        try {
	            while ((line = reader.readLine()) != null) {
	            	Log.v("line", ""+line);
	                sb.append(line + "\n");
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                is.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return sb.toString();
	    }
	 
		
	public JSONArray getJSONPageRank(String url) {
		String apiKey = "6f9da27a636e1c3f9111348d0687cb7f";
		String target = "http://apps.compete.com/sites/"+url+"/trended/METRIC/?apikey="+apiKey;
		HttpGet method = new HttpGet(target);
		HttpResponse response;
		try {				
			response = httpClient.execute(method);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return new JSONArray(EntityUtils.toString(response.getEntity())).getJSONArray(1);				 
			}
		} catch(Exception e) {
			Log.v("error", "error:" +e);			
		}
		return null;
	}	
	
	public static String convertURL(String str) {

	   String url = null;
	    try{
	    url = new String(str.trim().replace(" ", "%20").replace("&", "%26")
	            .replace(",", "%2c").replace("(", "%28").replace(")", "%29")
	            .replace("!", "%21").replace("=", "%3D").replace("<", "%3C")
	            .replace(">", "%3E").replace("#", "%23").replace("$", "%24")
	            .replace("'", "%27").replace("*", "%2A").replace("-", "%2D")
	            .replace(".", "%2E").replace("/", "%2F").replace(":", "%3A")
	            .replace(";", "%3B").replace("?", "%3F").replace("@", "%40")
	            .replace("[", "%5B").replace("\\", "%5C").replace("]", "%5D")
	            .replace("_", "%5F").replace("`", "%60").replace("{", "%7B")
	            .replace("|", "%7C").replace("}", "%7D"));
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    return url;
	}
	
	public String getClipBoardNameFromId(int id) {	
		return arraySECOND[0][id];		
	}

	public void setarraySECOND(String[][] arraySECOND) {
		this.arraySECOND = arraySECOND;
	}
	
	public void cleanArraySECOND(){
		this.arraySECOND = null;
	}
	
	public String[][] getArrayExtraTab() {
		return arrayExtraTab;
	}


	public void setArrayExtraTab(String[][] arrayExtraTab) {
		this.arrayExtraTab = arrayExtraTab;
	}
	
	//http://apps.compete.com/sites/facebook.com/trended/uv/?apikey=6f9da27a636e1c3f9111348d0687cb7f&start_date=201012&end_date=201012
	//http://apps.compete.com/sites/DOMAIN/trended/METRIC/?apikey=APIKEY
	
	
    public static void setCurrentMenu(int menu){
    	floatingCursor.setCurrentMenu(menu);
    }
    
    public static void setToggleMenuVisibility(){
    	floatingCursor.toggleMenuVisibility();
    }   
    
    public void startTextGesture()
    {
    	drawNothingTip();
		eventViewer.setText("Please make text selection gesture now.");
    	mSelectionGesture.setEnabled(true);
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
        else if("Chooser".equals(action)){
			actions.chooser();
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

	public void refreshWebView(){
		webView.reload();
	}

	public static void setActiveWebViewIndex(int act) {
		
		int count = webLayout.getChildCount();
		
		if(act > -1 && act < count){
			activeWebViewIndex = act;
		} else { 
			return;
		}
		
		for(int i=0;i<count;i++){
			if(i == activeWebViewIndex){
				WebView wv = (WebView)webLayout.getChildAt(i);
				wv.setVisibility(View.VISIBLE);
				floatingCursor.setWebView(wv,false);
				rCtrl.setWebView(wv);
			} else {
				webLayout.getChildAt(i).setVisibility(View.INVISIBLE);
			}
				
		}		
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
	
	public static void removeWebView(){
		webLayout.removeViewAt(activeWebViewIndex);	
		setActiveWebViewIndex(activeWebViewIndex);
		int count = webLayout.getChildCount();
		for(int i= activeWebViewIndex;i<count;i++){
			WebView wv = (WebView) webLayout.getChildAt(i);
			wv.setId(wv.getId()-1);
		}
		if(webLayout.getChildCount()==0){
			floatingCursor.addNewWindow(false, false);
		}	
		int currentPage = SwifteeApplication.getNewLandingPagesOpened();
		if (currentPage>0){
			SwifteeApplication.setNewLandingPagesOpened(currentPage-1);
		}		
	}

	public void adjustTabIndex(WindowTabs winTabs){
		int count = winTabs.getChildCount() - 4;
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
				//String url = child.getWebView().getUrl();
				//eventViewer.setText(url);
			}
		}		
	}
	
	public static int getTabCount(){
		int wvCount = webLayout.getChildCount();
		return wvCount;
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
	
	/*public void initFloatingCursos(){	
		//floatingCursor.toggleMenuVisibility();
		int width = (getDisplayWidth()/2);
		int height = (getDisplayHeight());
		floatingCursor.scrollTo(width, height);
	}*/
	
	/*
	 * Implemented relocation of the FC to the next convenient and proximate side.
	 * I divided the screen in four cuadrants and compare x,y distances to the x,y sides.
	 * The FC snaps to the one near.
	 * TODO, animate. Jose. 
	 */
	public void enterParkingMode(boolean moveToParkingPosition) {
		 
		
		inParkingMode = true;		
		
		//Shrink to a half the size
		floatingCursor.enterParkingMode();		 
        
        WindowTabs.getCurrentTab();
        
        String currentURL = webView.getUrl();
                
		if(moveToParkingPosition) {   
			
			floatingCursor.stopFling();
			
			int xLoc = floatingCursor.getScrollX();
			int yLoc = floatingCursor.getScrollY();	
			
			Hashtable<Integer, Object[]> location = getCuadrant4(xLoc, yLoc);			
			
			Object[] objCuarant = location.get(1);
			int cuadrant = (Integer) objCuarant[0];		
			
			Object[] hW = location.get(2);		
			int w = (Integer) hW[0];
			int h = (Integer) hW[1];
			
			int c_x;
    		if (floatingCursor.getProgressEnabled()){
    			c_x = (int) (w/2 - (floatingCursor.RADIUS_DIP/2)-3);
    		} else {
    			c_x = w/2;
    		}
			
			int c_y;
    		if (floatingCursor.getProgressEnabled()){
    			c_y = (int) (h/2 - (floatingCursor.RADIUS_DIP/2)-3);
    		} else {
    			c_y = h/2;
    		} 		
			
			switch (cuadrant){
			
			case 1:
				//Fisrt cuadrant vars
	            final int c1X;        
	            final int c1Y;
	            //Calculate distance to upper right corner. 
	        	c1X = w/2 - xLoc;
	        	c1Y = h/2 - yLoc;        	
	        	if (c1X >= c1Y){ // y is shorter, snap y.    		
	        		floatingCursor.scrollTo(xLoc, c_y);	        		
	        	} else if (c1X <= c1Y) { // x is shorter snap to x.   	
	        		floatingCursor.scrollTo(c_x, yLoc);	        		
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
        			floatingCursor.scrollTo(xLoc, c_y);
        			//animateDocking(xLoc, yLoc, xLoc, h/2);
        		} else if (c2X <= c2Y) { //x is shorter snap to y.
        			//Log.v("","x is shorter, snap y");
        			floatingCursor.scrollTo(-c_x, yLoc);
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
        			floatingCursor.scrollTo( xLoc, -c_y);
        			//animateDocking(xLoc, -w/2, yLoc, -h/2);
        		} else if (c3X <= c3Y) { //x is shorter snap to x.
        			//Log.v("","x is shorter, snap y");
        			//animateDocking(xLoc, -w/2, yLoc, yLoc);
        			floatingCursor.scrollTo( c_x, yLoc);
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
        			floatingCursor.scrollTo( xLoc, -c_y);
        		} else if (c4X <= c4Y) { //x is shorter snap to x.
        			//Log.v("","x is shorter, snap y");
        			//animateDocking(xLoc, -w/2, yLoc, yLoc);
        			floatingCursor.scrollTo( -c_x, yLoc);
        		}
				break;			
			}
			
		} else { //Landing Page			
			int pkInpoutBottom = SwifteeApplication.getLandingInputTop();	
			int dHeight = (getDisplayHeight()/2)-pkInpoutBottom;
			//if (SwifteeApplication.getOrientation()==SwifteeApplication.ORIENTATION_LANDSCAPE)
			floatingCursor.scrollTo(0, dHeight);					
		}		
		SwifteeApplication.setFCAmountOfDots(20);
		SwifteeApplication.setFCDotDiam(6);
		
	};
	
	
	Display display;
	
	public int getDisplayWidth(){
		int w = display.getWidth();
		return w;
	}
	
	public int getDisplayHeight(){
		int h = display.getHeight();
		return h;
	}
	
	public Hashtable<Integer, Object[]> getCuadrant4(int X, int Y) {
		
		//Object cuadrant[] = {};
		Hashtable<Integer, Object[]> cuadrant = new Hashtable<Integer, Object[]>();
		Object[] loc = new Object[1];
		Object[] hW = new Object[2];	
		
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
        } else if (xLoc > 0 && yLoc <= 0){ //DOWN LEFT CUADRANT - C3.		
        	loc[0] = 3;
        } else if (xLoc < 0 && yLoc < 0){ //DOWN RIGHT CUADRANT - C4.		
        	loc[0] = 4;
        }
        
        hW[0] = w;
        hW[1] = h;        
        
        cuadrant.put(1, loc);
        cuadrant.put(2, hW);    
        
        loc=null;
        hW=null;
        
        return cuadrant;
        
	}
	
	public boolean isInParkingMode() {
		return inParkingMode;
	}


	public void setInParkingMode(boolean inParkingMode) {
		this.inParkingMode = inParkingMode;
	}

	
	/*public int reachRightScreenLateral(int X){	
		Display display = getWindowManager().getDefaultDisplay();		
        int w = display.getWidth();	
		int xModule = w/5;			
		if (X > xModule && X < xModule){ //Reached left lateral    
			return 1;
		} else if (X > xModule && X < xModule*2){ //Reached right lateral         
			return 2;
		} else if (X > xModule && X < xModule*4){ //Reached right lateral
			return 4;
		} else if (X > xModule && X < xModule*5){ //Reached right lateral        	
        	return 5;
        }		
        return 0;       
	}*/
	
	/**
	 * Provides coordinates to locate tips
	 * @param X
	 * @param Y
	 * @return
	 */
	public Vector<Object> getCuadrant34(int X, int Y) {
		
		Vector<Object> cuadrant = new Vector<Object>();
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
        
        if (X > 0 && X < xModule){ //First Vertical Sector      
        	
        	xRect.left = 20;
        	xRect.right = xModule+20;
        	verticals = SwifteeApplication.VERTICAL_LEFT_COLUMN;        
        	
        	if (horizontals==1){
        		tipType = SwifteeApplication.SET_TIP_TO_LEFT_UP;
        	} else {
        		tipType = SwifteeApplication.SET_TIP_TO_LEFT_DOWN;
        	}
        	
        } else if (X > xModule && X < xModule*2){ //Second Vertical Sector
        	
        	xRect.left = xModule-20;
        	xRect.right = xModule+20;   
        	verticals = SwifteeApplication.VERTICAL_CENTER_COLUMN;
        	
        	if (horizontals==1){
        		tipType = SwifteeApplication.SET_TIP_TO_CENTER_UP;
        	} else {
        		tipType = SwifteeApplication.SET_TIP_TO_CENTER_DOWN;
        	}
        	 
        } else if (X > xModule*2 && X < xModule*3){ //Third Vertical Sector
        	
        	xRect.left = w/2-20;
        	xRect.right = xModule+20;        	
        	verticals = SwifteeApplication.VERTICAL_RIGHT_COLUMN;
        	
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
			int orientation = display.getOrientation();
			switch(orientation){
				case 0:
					SwifteeApplication.setOrientation(SwifteeApplication.ORIENTATION_PORTRAIT);
				break;
				
				case 1:
					SwifteeApplication.setOrientation(SwifteeApplication.ORIENTATION_LANDSCAPE);
					break; 
					
				case 2:
					SwifteeApplication.setOrientation(SwifteeApplication.ORIENTATION_PORTRAIT);
					break;
				case 3:
					SwifteeApplication.setOrientation(SwifteeApplication.ORIENTATION_LANDSCAPE);
					break;
				
			}		
			SwifteeApplication.setScreenWidth(display.getWidth());
			SwifteeApplication.setScreenHeight(display.getHeight());			
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
	
	public void toggleMenuVisibility(){
		floatingCursor.toggleMenuVisibility();
	}
	
	public void exitParkingMode() {
		inParkingMode = false;
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
	
	@Override
	protected void onPause(){
		  super.onPause();
		  System.gc();
 		  if(mSDInfoReceiver!=null)
			  unregisterReceiver(mSDInfoReceiver);
		  
	}
	
	/*private BroadcastReceiver mSDInfoReceiver = new BroadcastReceiver(){    
	 };	*/ 
	 
	 public static java.sql.Timestamp getCurrentTimeStamp(){
	    	Calendar calendar = Calendar.getInstance();	
			java.util.Date now = calendar.getTime();	
			java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());	
			return currentTimestamp;
	    }
	 
	 public String[][] getArrayCERO() {
			return arrayCERO;
		}

		public void setArrayCERO(String[][] arrayCERO) {
			this.arrayCERO = arrayCERO;
		}
	 
	 public String[][] getArrayFIRST() {
			return arrayFIRST;
		}

		public void setArrayFIRST(String[][] arrayFIRST) {
			this.arrayFIRST = arrayFIRST;
		}

		public String[][] getArraySECOND() {
			return arraySECOND;
		}

		public void setArraySECOND(String[][] arraySECOND) {
			this.arraySECOND = arraySECOND;
		}

		public String[][] getArrayTHIRD() {
			return arrayTHIRD;
		}

		public void setArrayTHIRD(String[][] arrayTHIRD) {
			this.arrayTHIRD = arrayTHIRD;
		}

		public String[][] getArrayFOURTH() {
			return arrayFOURTH;
		}

		public void setArrayFOURTH(String[][] arrayFOURTH) {
			this.arrayFOURTH = arrayFOURTH;
		}

		public String[][] getArrayFIFTH() {
			return arrayFIFTH;
		}

		public void setArrayFIFTH(String[][] arrayFIFTH) {
			this.arrayFIFTH = arrayFIFTH;
		}
		
		public int getArrayAmountByIndex(int index){
			int amount = 0;
			switch (index){
				case 0:
					if (arrayCERO != null)
					amount = arrayCERO.length;
					break;
				case 1:
					if (arrayFIRST != null)
					amount = arrayFIRST.length;
					break;
				case 2:
					if (arraySECOND != null)
					amount = arraySECOND.length;
					break;
				case 3:
					if (arrayTHIRD != null)
					amount = arrayTHIRD.length;
					break;
				case 4:
					if (arrayFOURTH != null)
					amount = arrayFOURTH.length;
					break;		
			}
			return amount;			
		}
		
		public String[][] getArrayByIndex(int index){
			int amount = 0;
			String[][] array = null;
			switch (index){
				case 0:
					if (arrayCERO != null)
					array = arrayCERO;
					break;
				case 1:
					if (arrayFIRST != null)
					array = arrayFIRST;
					break;
				case 2:
					if (arraySECOND != null)
					array = arraySECOND;
					break;
				case 3:
					if (arrayTHIRD != null)
					array = arrayTHIRD;
					break;
				case 4:
					if (arrayFOURTH != null)
					array = arrayFOURTH;
					break;		
			}
			return array;			
		}
		
		
		 public String getInputBoxText() {
			 if (inputBoxText!=null){
				 return inputBoxText;
			 } else {
				 return "";
			 }
			
		}

		public void setInputBoxText(String inputBoxText) {
			this.inputBoxText = "";
			this.inputBoxText = inputBoxText;
		}

		private String inputBoxText;
		
		int inputX;
		public int getInputY() {
			return inputY;
		}
		
		public void setInputY(int i) {
			inputY = i;
		}

		int inputY;
		public int getInputX() {
			return inputX;
		}
		
		public void setInputX(int i) {
			inputX = i;
		}

		
		 public AsyncTask touchInput = new TouchInput();   
		    
		 public class TouchInput extends AsyncTask  {    
		          
			@Override
			protected Object doInBackground(Object... arg) {					
				
				int x = (Integer) arg[0];
				int y = (Integer) arg[1];			
				
	        	floatingCursor.sendEvent(MotionEvent.ACTION_DOWN, x, y, true);
        		floatingCursor.sendEvent(MotionEvent.ACTION_UP, x, y, true);  
        		    		
		    	return false;
			}		
		} 
		 
		public void runTouchInput(int x, int y){
			touchInput = new TouchInput();
			touchInput.execute(x, y); 	
		}
		
		private int wmId;
		private String wmTitle;
		private Hashtable windowsSetHash = new Hashtable(); 
		
		public Hashtable getWindowsSetHash() {
			return windowsSetHash;
		}

		public void setWindowsSetHash(Hashtable windowsSetHash) {
			this.windowsSetHash = windowsSetHash;
		}

		
		public void loadWindowsManager(int id, String title){			
			
			wmId = id;
			wmTitle = title;
			
			AlertDialog alertWindowsSet;
			alertWindowsSet = new AlertDialog.Builder(this).create();			
			alertWindowsSet.getWindow().requestFeature(Window.FEATURE_NO_TITLE);		    
			alertWindowsSet.setMessage("Are you sure you want to load Windows Set \""+title+"\"?. Warning. Your current set will be removed.");
		    
			alertWindowsSet.setButton("OK", new DialogInterface.OnClickListener() {		      
		    	
		    	public void onClick(DialogInterface dialog, int which) {	    		
					
		    		DBConnector database = appState.getDatabase();
					Cursor wmCursor = database.getWindowsManagerListById(wmId);    	
					
			    	if (wmCursor!=null){
			    		
			    		floatingCursor.fcWindowTabs.removeAllTabs();   		
						
						int count = wmCursor.getCount();
						wmCursor.moveToPosition(0);    	
				    	int num = 0;		    	    
				    	
				    	floatingCursor.toggleMenuVisibility();
						floatingCursor.setCurrentMenu(2);
						
				    	//if (count > 3){ count=3; }
				    	
						while (!wmCursor.isAfterLast() && num < count){
							
				 			String title = wmCursor.getString(0);
				 			String dataUrl = wmCursor.getString(1);
				 			String encodedBitmap = wmCursor.getString(2);		 				 		
				 			
				 			byte[] data = null;
							try {
								data = Base64.decode(encodedBitmap);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}				
				 			
				 			Bitmap BitmapOrg = BitmapFactory.decodeByteArray(data, 0, data.length);
				 			BitmapDrawable dataBmd = new BitmapDrawable(BitmapOrg); // to 

				 			if (num==0){
				 				floatingCursor.fcWindowTabs.addWindow(dataUrl, false);			 				
				 			}
				 			
				 			Object[] dataWM = new Object[3];
				 			dataWM[0] = title;
				 			dataWM[0] = dataUrl;
				 			dataWM[0] = dataBmd;

				 			
				 			//new WindowsManagerAsync().execute();	 
				 			/*TabButton tab = floatingCursor.fcWindowTabs.addWindowFromDatabase(dataUrl, dataBmd, title);
				 		
				 			LoadUrlAsync load = new LoadUrlAsync(tab, floatingCursor.getWebView(), dataUrl);
				 			load.execute();*/
				 			
				 			wmCursor.moveToNext();	 			
							
						}			
						wmCursor.close();	    		   	
			    	}  	    		
		    		
		    	} }); 
			
			alertWindowsSet.setButton2("Cancel", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {	    	  
		    	  	//Dont know what to do here. 
		        return;
		    }}); 			  
			alertWindowsSet.show();		
			
		}
		
		/*private class LoadUrlAsync extends AsyncTask<String, Void, String> {
			
	        private final WebView wV;
	        private String url;
	        private TabButton tab;
	      
	        public LoadUrlAsync(TabButton t, final WebView w, final String p) {
	           
	        	this.wV = webView;
	            this.url = p;
	            tab = t;
	            
	            tab.setHasOptional(true);
	 			String[] optional = {"Loading....","Loading...","Loading..","Loading.","Loading"};
	 			tab.setOptionalMessage(optional);
	 			floatingCursor.drawTip();
	            wV.loadUrl(url);	           
	        }

	        @Override
	        protected String doInBackground(final String... params) {	        	 	        	 
	        	 return params[0];
	        }

	        @Override
	        protected void onPostExecute(final String content) {
	        	wV.setWebViewClient(new WebViewClient() {		        		
	                @Override
	                public void onPageFinished(WebView view, String url) {          	
	  		    	  	tab.setHasOptional(false);
	  		    	  	tab.setOptionalMessage(null);	
	  		 			addWebView(tab.getWebView());
	  		 			int active = getActiveWebViewIndex()+1;
	  		 			tab.setId(active);		
	  		 			setActiveWebViewIndex(getActiveWebViewIndex()+1);
	  		 			
	                }
	         });
	        	
	        }
	    }*/
		
		
		/* private class LoadUrlAsync extends AsyncTask<String, Void, String> {
			 
			 TabButton tab;
			 String url;
			 
			 public LoadUrlAsync(TabButton t, String url) {
			        super();			        
			        tab = t;
			        url = url;
			        Log.v("tab","is loadign: "+floatingCursor.mIsLoading);
			 }
		      
		      // can use UI thread here
		      protected void onPreExecute() {		    	  	
		    	  	floatingCursor.loadPage(url);		    		
		 					  
		      }
		 
		      // automatically done on worker thread (separate from UI thread)
		      protected String doInBackground(final String... args) {	    	  
		    	  floatingCursor.drawTip();	  		    	  
		         return null;
		      }
		 
		      // can use UI thread here
		      protected void onPostExecute(final String result) { 
		    	  		    	  
		    	  Log.v("tab","loading: "+floatingCursor.mIsLoading);
		    	  tab.setHasOptional(false);
		 		   tab.setOptionalMessage(null);		 			

		 			addWebView(tab.getWebView());
		 			int active = getActiveWebViewIndex()+1;
		 			tab.setId(active);		
		 			setActiveWebViewIndex(getActiveWebViewIndex()+1);	 			
		 			
		 			floatingCursor.fcWindowTabs.resetMenu();
		      }
		   }	*/
		
		
		public void deleteWindowSet(final int id, String title){	
			
			AlertDialog alertDialog;
			alertDialog = new AlertDialog.Builder(this).create();			
	    	alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);		    
			alertDialog.setMessage("Are you sure you want to remove Windows Set "+title+" ?");
		    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {		      
		    	
		    	public void onClick(DialogInterface dialog, int which) {	    		
					DBConnector database = appState.getDatabase();
					boolean delete = database.deleteWindowSetById(id);
					if (delete){						
						///HERE build Landing Page again and reload it, all of them if exist.						
					}
		    	} }); 
		    alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {	    	  
		    	  	//Dont know what to do here. 
		        return;
		    }}); 			  
		  	alertDialog.show();		
	
		}


		public static boolean isTabsActivated() {
			return tabsActivated;
		}


		public static void setTabsActivated(boolean tabsActivated) {
			floatingCursor.setDrawErased(false);
			BrowserActivity.tabsActivated = tabsActivated;
		}


		public static boolean isSuggestionActivated() {
			return suggestionActivated;
		}

		public static void setSuggestionActivated(boolean suggestionActivated) {
			BrowserActivity.suggestionActivated = suggestionActivated;
		}


		public static boolean isSuggestionListActivated() {
			return suggestionListActivated;
		}


		public static void setSuggestionListActivated(
				boolean suggestionListActivated) {
			BrowserActivity.suggestionListActivated = suggestionListActivated;
		}


		public static boolean isTipsActivated() {
			return tipsActivated;
		}


		public static void setTipsActivated(boolean tipsActivated) {
			BrowserActivity.tipsActivated = tipsActivated;
		}


		public static boolean isSuggestionActivatedExpanded() {
			return suggestionActivatedExpanded;
		}


		public static void setSuggestionActivatedExpanded(
			boolean suggestionActivatedExpanded) {
			BrowserActivity.suggestionActivatedExpanded = suggestionActivatedExpanded;
		}		
		
		public void startSDCardIntent() {				
			Intent i = new Intent(this ,SdCardError.class);
			i.putExtra("isAppLaunched", true);
			i.putExtra("numWindows", floatingCursor.getWindowCount());
			startActivityForResult(i, SDCardRequestCode);
		}
		


}

			
	 
	

