package com.roamtouch.swiftee;

import java.util.ArrayList;

import com.roamtouch.swiftee.CircularDialog;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
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
import android.os.Message;
import android.text.ClipboardManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.Toast;

public class BrowserActivity extends Activity implements OnGesturePerformedListener {
	
     public static final int DIALOG_CIRCULAR = 1;
	
	 @Override
	 protected Dialog onCreateDialog(int id) {
	        switch (id) {
	        	case DIALOG_CIRCULAR:
	        		CircularDialog circularDialog = new CircularDialog(this, R.style.Theme_Transparent);
	        		return circularDialog;
	        }
	        return null;
	 }
	 
	private final Handler mHandler = new Handler();
	 
	   /*// Define the Handler that receives messages from the thread and update the progress
	    final Handler handler = new Handler() {
	        public void handleMessage(Message msg) {
	        	int dialogID = msg.getData().getInt("ID");
	        	
	        	if (dialogID == DIALOG_CIRCULAR)
	        		showDialog(DIALOG_CIRCULAR);
	        }
	    };*/
	    	
	private class GestureWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
		
		@Override
        public void onPageFinished(WebView view, String url) {
			floatingCursor.onPageFinished();
        }
	}
	
	static final int DIALOG_MENU_ID = 0;
	
	private WebView webView;
	private SwifteeOverlayView overlay;
	private FloatingCursor floatingCursor;

	private GestureLibrary mLibrary;

	private GestureOverlayView mGestures;
	private HorizontalScrollView mTutor;
		
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.main);
        
		webView = (WebView) findViewById(R.id.webview);
		webView.setWebViewClient(new GestureWebViewClient());
		webView.loadUrl("http://www.google.com.ar");		

		overlay = (SwifteeOverlayView) findViewById(R.id.overlay);
				
/*		overlay.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				Toast.makeText(BrowserActivity.this, "New touch event", Toast.LENGTH_SHORT ).show();
				return true;
			}
		});*/
		
		/*webView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				Toast.makeText(BrowserActivity.this, "New touch event WV", Toast.LENGTH_SHORT ).show();
				showDialog(DIALOG_CIRCULAR);
				return false;
			}
		});*/
				
		floatingCursor = (FloatingCursor)findViewById(R.id.floatingCursor);	
		floatingCursor.setWebView(webView);
		floatingCursor.setParent(this);
		//floatingCursor.setHandler(handler);

		overlay.setFloatingCursor(floatingCursor);
		
		mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		if (!mLibrary.load()) {
			finish();
		}
		
        //showDialog(DIALOG_MENU_ID);
        
	    Button sB,eB,cB; //,fB,tB,hB,iB,lB;
        sB=(Button)this.findViewById(R.id.s);
        eB=(Button)this.findViewById(R.id.e);
        cB=(Button)this.findViewById(R.id.c);
        
        OnClickListener listener=new OnClickListener(){

			public void onClick(View v) {
				int id=v.getId();
				switch(id){
				case R.id.s:gestureDone(GESTURE_S);break;
				case R.id.e:gestureDone(GESTURE_e);break;
				case R.id.c:gestureDone(GESTURE_c);break;
/*				case R.id.f:gestures.drawGesture("f");break;
				case R.id.t:gestures.drawGesture("t");break;
				case R.id.h:gestures.drawGesture("h");break;
				case R.id.i:gestures.drawGesture("i");break;
				case R.id.l:gestures.drawGesture("l");break;*/
				}
			}
        	
        };
        sB.setOnClickListener(listener);
        eB.setOnClickListener(listener);
        cB.setOnClickListener(listener);
        //fB.setOnClickListener(listener); 
        
       /* final ImageButton button = (ImageButton) findViewById(R.id.android_button);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
                Toast.makeText(BrowserActivity.this, "Beep Bop", Toast.LENGTH_SHORT).show();
               // showDialog(DIALOG_MENU_ID);
                
                //ProgressDialog dialog = ProgressDialog.show(BrowserActivity.this, "", 
                  //      "Loading. Please wait...", true);
            	
            	Menu dialog = new Menu(BrowserActivity.this);
            	dialog.show();
            }
        });*/
		
		mGestures = (GestureOverlayView) findViewById(R.id.gestures);
		mGestures.addOnGesturePerformedListener(this);
		mGestures.setEnabled(false);
		
		mTutor = (HorizontalScrollView) findViewById(R.id.gestureScrollView);
		mTutor.setVisibility(View.INVISIBLE);
    }
    
    private String mSelection;
    //private NotificationManager mNotificationService = null;
    
    public void startGesture()
    {
    	/*if (mNotificationService == null)
			mNotificationService = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
		mNotificationService.cancelAll(); //.cancelAllNotifications(BrowserActivity.this.getPackageName());*/
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
    
    final int GESTURE_S = 1;
    final int GESTURE_e = 2;
    final int GESTURE_c = 3;
    
    public void gestureDone(int gestureID)
    {
    	switch (gestureID)
    	{
    		case GESTURE_S:
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
                    if (predictions.get(0).score > 1.0) {
                            String action = predictions.get(0).name;
                            if ("S".equals(action)) {
                            	gestureDone(GESTURE_S);
                            } else if ("e".equals(action)) {
                            	gestureDone(GESTURE_e);
                            } else if ("c".equals(action)) {
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
    
    /*
    private Dialog createProgressDialog()
    {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        
        return progressDialog;
    	
    	return dialog;
    }
    
    protected Dialog onCreateDialog(int id) {
    	 Dialog dialog;
    	 switch(id) {
    	  	case DIALOG_MENU_ID:
    	  		dialog = createProgressDialog();
    	  		break;
    	   	default:
    	   		dialog = null;
    	 }
    	 return dialog;
    }*/
}