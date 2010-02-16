package com.roamingkeyboards.activity;

import java.util.ArrayList;


import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.roamingkeyboards.domain.slide.pointer.SlidePointer;
import com.roamingkeyboards.domain.slide.pointer.SlidePointerImpl;
import com.roamingkeyboards.domain.slide.position.Coordinates;
import com.roamingkeyboards.domain.slide.strategy.AbsoluteSlideStrategyImpl;
import com.roamingkeyboards.domain.slide.strategy.TraslationSlideStrategyImpl;
import com.roamingkeyboards.view.pointer.SlidePointerView;

public class SlidePointerActivity extends Activity implements OnGesturePerformedListener {
	
	private GestureLibrary mLibrary;
	private GestureOverlayView gestures;
//	private SlidingPointerView slidingPointerView;
	
	private WebView webView;
	private EditText urlField;
	private Button goButton;
	private SlidePointerView slidePointerView;
	private SlidePointer slidePointer;
	private Coordinates initialFingerCoordinates;
	private boolean showSlidePointer = false;
	private int slidePointerDelta = 0;
	private Coordinates oldFingerCoordinates;
	
	
	private void openURL() {
		webView.loadUrl(urlField.getText().toString());
		webView.requestFocus();
	}

	private class GestureWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		webView = (WebView) findViewById(R.id.webview);
		urlField = (EditText) findViewById(R.id.url);
		goButton = (Button) findViewById(R.id.go_button);

		webView.setWebViewClient(new GestureWebViewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("http://www.google.com.ar");

		webView.setOnTouchListener(new OnTouchListener(){

			public boolean onTouch(View v, MotionEvent event) {
								
				if (showSlidePointer) {
					final WebView webView = (WebView) v;
					final Coordinates currentFingerCoordinates = Coordinates.make(event.getX(),event.getY());
	
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						slidePointer.setSlideStrategy(new TraslationSlideStrategyImpl(currentFingerCoordinates));
						slidePointerDelta = 0;
						oldFingerCoordinates = Coordinates.make(event.getX(),event.getY());
					}
					
					if (event.getAction() == MotionEvent.ACTION_UP) {
		                //Toast.makeText(SlidePointerActivity.this, "SlidePointer: " + (slidePointerDelta > 10?"moved":"clicked"), Toast.LENGTH_SHORT).show();
		        		if (slidePointerDelta <= 10)
		        		{
		        			Toast.makeText(SlidePointerActivity.this, "Please draw a gesture now for: <selection>", Toast.LENGTH_SHORT).show();    	
		        			gestures.setEnabled(true);
		        		}
		        	}
					
					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						webView.removeView(slidePointerView);
						slidePointerView = new SlidePointerView(getParent(),slidePointer,currentFingerCoordinates); 
						webView.addView(slidePointerView);
						final Coordinates deltaCoordinates = oldFingerCoordinates.sub(currentFingerCoordinates);
						slidePointerDelta += Math.abs(deltaCoordinates.getX()) + Math.abs(deltaCoordinates.getY());
						oldFingerCoordinates = Coordinates.make(event.getX(),event.getY());
					}
				}
				return true;
			}

		});

		slidePointer = new SlidePointerImpl();

		goButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View view) {
				
				if (!showSlidePointer) {
					initialFingerCoordinates = Coordinates.make(webView.getMeasuredWidth()/2,webView.getMeasuredHeight()/2);
					slidePointer.setSlideStrategy(new AbsoluteSlideStrategyImpl());
					slidePointerView = new SlidePointerView(getParent(),slidePointer,initialFingerCoordinates);
					webView.addView(slidePointerView);
				} else {
					webView.removeView(slidePointerView);
				}
				showSlidePointer = !showSlidePointer;
			}
		});

		urlField.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					openURL();
					return true;
				} else {
					return false;
				}
			}
		});
		
		mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		if (!mLibrary.load()) {
			finish();
		}
		
		gestures = (GestureOverlayView) findViewById(R.id.gestures);
		gestures.addOnGesturePerformedListener(this);
		gestures.setEnabled(false);
	}
	
       public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
               ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
               if (predictions.size() > 0) {
                       if (predictions.get(0).score > 1.0) {
                               String action = predictions.get(0).name;
                               if ("S".equals(action)) {
                                       Toast.makeText(this, "S gesture done", Toast.LENGTH_SHORT).show();
                                       gestures.setEnabled(false);
                                       showSlidePointer = true;
                               }
                       }
               }
	        }

}