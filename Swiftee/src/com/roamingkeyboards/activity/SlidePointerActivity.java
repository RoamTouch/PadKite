package com.roamingkeyboards.activity;

import java.util.ArrayList;


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
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
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
import com.roamingkeyboards.view.pointer.PointerWebView;

public class SlidePointerActivity extends Activity implements OnGesturePerformedListener {
	
	private Handler mHandler = new Handler();
	
	private GestureLibrary mLibrary;
	private GestureOverlayView gestures;
//	private SlidingPointerView slidingPointerView;
	
	private PointerWebView webView;
	private EditText urlField;
	private Button goButton;
	private SlidePointerView slidePointerView;
	private SlidePointer slidePointer;
	private Coordinates initialFingerCoordinates;
	private boolean showSlidePointer = false;
	private boolean showSlidePointer_lock = false;
	private String selection = null;
	private int slidePointerDelta = 0;
	private boolean selectionState = false;
	private boolean slidePointerIsDown = false;
	private boolean slidePointerDoDown = false;
	private boolean slidePointerDoUp = false;
	
	private Coordinates oldFingerCoordinates;
	
	private void updateMe()
	{
		if (showSlidePointer_lock)
			goButton.setText("Draw S!");
		else if (showSlidePointer)
			goButton.setText("Hide Me!");
		else
			goButton.setText("Pick Me!");
	}

	private void updateGo()
	{
		if (urlField.getText().length() <= 6)
			updateMe();
		else
			goButton.setText("Go!");
	}
	
	private void openURL() {
		webView.loadUrl(urlField.getText().toString());
		webView.requestFocus();
		urlField.setText("");
		updateMe();
	}

    private class GestureWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                        JsResult result) {
                Log.i("Alert", message);
                Toast.makeText(SlidePointerActivity.this, message, Toast.LENGTH_SHORT);
                result.confirm();
                return true;
        }
    }


	private class GestureWebViewClient extends WebViewClient {
 
		@Override
        public void onPageFinished(WebView view, String url) {

                final String snippet = "javascript:"
                                + "function whereInWorld(x,y) {"
                                	+ "var obj = { \"type\": null, \"content\": null };"
                                	+ ""
                                	+ "var elem = document.elementFromPoint(x,y);"
                                	+ ""
                                	+ "if (elem.tagName == \"IMG\")"
                                		+ "obj = {\"type\": \"image\", \"content\": elem.src };"
                                	+ "else if (elem.tagName == \"INPUT\")"
                                		+ "obj = {\"type\": \"input\", \"content\": elem.value };"
                                	+ "else if (elem.tagName == \"P\" || elem.tagName == \"DIV\")"
                                	+ "{"
                                		+ "var html = elem.innerHTML;"
                                		+ "var newh = \"<span>\" + html.replace(/[ \\n]/g,\"</span> <span>\") + \"</span>\";"
                                		+ "elem.innerHTML=newh;"
                                		+ "var newElem = document.elementFromPoint(x,y);"
                                		+ "obj.type=\"text\";" 
                                		+ "obj.content = newElem.firstChild.wholeText;"
                                		+ "elem.innerHTML=html;" 
                                	+ "}"
                                	+ "else"
                                	+ "{"
                                		+ "obj.type = elem.tagName;"
                                		+ "obj.content = elem.firstChild.wholeText;"
                                	+ "}"
                                	+ "if (obj.content == undefined )"
                                		+ "obj.content = \"Unrecognized contents\";"
                               		+ "pBridge.type(obj.type, obj.content);"
                                + "}";

                view.loadUrl(snippet);
        }

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
	
    public class ProxyBridge {

        public void type(final String type, final String content) {
        		
        		final String prefix = "Please draw 'S' or 'e' gesture now for: ";
                Toast.makeText(SlidePointerActivity.this, prefix + type + ":" + content,
                                Toast.LENGTH_SHORT).show();
                selection=content;
        }
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
  	    ProxyBridge pBridge = new ProxyBridge();
		
		webView = (PointerWebView) findViewById(R.id.webview);
		
		WebSettings wSet = webView.getSettings();
        wSet.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(pBridge, "pBridge");
		
		urlField = (EditText) findViewById(R.id.url);
		goButton = (Button) findViewById(R.id.go_button);

		webView.setWebViewClient(new GestureWebViewClient());
        webView.setWebChromeClient(new GestureWebChromeClient());
		webView.getSettings().setJavaScriptEnabled(true);
//		webView.loadUrl("http://www.lionsad.de/Jose/");
		webView.loadUrl("http://news.google.com");		
		updateMe();

		webView.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View view, int keyCode, KeyEvent event) {

				return false;
			}
		});
		
		webView.setOnTouchListener(new OnTouchListener(){

			public boolean onTouch(final View v, final MotionEvent event) {
								
				if (showSlidePointer && selectionState == false) {
					final PointerWebView webView = (PointerWebView) v;
					final Coordinates currentFingerCoordinates = Coordinates.make(event.getX(),event.getY());
	
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						slidePointer.setSlideStrategy(new TraslationSlideStrategyImpl(currentFingerCoordinates));
						slidePointerDelta = 0;
						oldFingerCoordinates = Coordinates.make(event.getX(),event.getY());
						slidePointerIsDown = true;
					}
					
					if (event.getAction() == MotionEvent.ACTION_UP) {
						slidePointerIsDown = false;
		                //Toast.makeText(SlidePointerActivity.this, "SlidePointer: " + (slidePointerDelta > 10?"moved":"clicked"), Toast.LENGTH_SHORT).show();
		        		if (slidePointerDelta <= 10 && selectionState == false)
		        		{
		        			 mHandler.post(new Runnable() {
                                 public void run() {
                                    ((WebView)v).loadUrl("javascript:whereInWorld("+event.getX()+","+event.getY()+")");
                                    //((WebView)v).loadUrl("javascript:pBridge.type(\"x\",\"y\");");
                                 }
		        			});

		        			//webView.emulateShiftHeld();
		        			//Toast.makeText(SlidePointerActivity.this, "Please draw a 'S' gesture now for: <selection>", Toast.LENGTH_SHORT).show();    	
		        			gestures.setEnabled(true);
							webView.removeView(slidePointerView);
							showSlidePointer = false;
							showSlidePointer_lock = true;
							updateMe();
		        		}
		        	}
					
					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						webView.removeView(slidePointerView);
						slidePointerView = new SlidePointerView(getParent(),slidePointer,currentFingerCoordinates); 
						webView.addView(slidePointerView);
						final Coordinates deltaCoordinates = oldFingerCoordinates.sub(currentFingerCoordinates);
						slidePointerDelta += Math.abs(deltaCoordinates.getX()) + Math.abs(deltaCoordinates.getY());
						oldFingerCoordinates = Coordinates.make(event.getX(),event.getY());
						
						webView.updateCoordinates(event, slidePointer.getXCoordinate(), slidePointer.getYCoordinate());
						
						return false;
					}
					return true;
				}
				if (showSlidePointer_lock)
					return true;
				
				// SELECTION PART
				
				if (selectionState)
				{
					final Coordinates currentFingerCoordinates = Coordinates.make(event.getX(),event.getY());
					
					if (slidePointerDoDown == true) {
                        Toast.makeText(SlidePointerActivity.this, "Emulating down event: ", Toast.LENGTH_SHORT).show();    	
						event.setAction(MotionEvent.ACTION_DOWN);
						slidePointerDoDown = false;
					}
					 
					if (slidePointerDoUp == true) {
                        Toast.makeText(SlidePointerActivity.this, "Emulating up event: ", Toast.LENGTH_SHORT).show();    	
						event.setAction(MotionEvent.ACTION_UP);
						slidePointerDoUp = false;
					}
					
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						slidePointer.setSlideStrategy(new TraslationSlideStrategyImpl(currentFingerCoordinates));
						slidePointerDelta = 0;
						oldFingerCoordinates = Coordinates.make(event.getX(),event.getY());

						webView.updateCoordinates(event, slidePointer.getXCoordinate(), slidePointer.getYCoordinate());
						
						event.setLocation(slidePointer.getXCoordinate(), slidePointer.getYCoordinate());
					}
					
					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						webView.removeView(slidePointerView);
						slidePointerView = new SlidePointerView(getParent(),slidePointer,currentFingerCoordinates); 
						webView.addView(slidePointerView);
						
						event.setLocation(slidePointer.getXCoordinate(), slidePointer.getYCoordinate());
						webView.updateCoordinates(event, slidePointer.getXCoordinate(), slidePointer.getYCoordinate());
					
					}
					else if (event.getAction() == MotionEvent.ACTION_UP) {
						selectionState = false;
						webView.onTouchEvent(event);
						
	        			mHandler.post(new Runnable() {
                             public void run() {
                            	 try {
									Thread.sleep(50);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
                                 selection = (String) ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).getText();
                                 Toast.makeText(SlidePointerActivity.this, "Please draw 'S' or 'e' gesture now for: " + selection, Toast.LENGTH_SHORT).show();    	
                                 gestures.setEnabled(true);
                                 webView.removeView(slidePointerView);
                                 showSlidePointer = false;
                                 showSlidePointer_lock = true;
                                 updateMe();
                             }
	        			});

	        			webView.toggleSelectingText();
						webView.toggleCursorVisibility();
	        			
						return true;
					}
					return false;
				}
				
				/* Bubble up test */
				return false;
			}

		});

		slidePointer = new SlidePointerImpl();

		goButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View view) {

				if (urlField.getText().toString().length() >= 6)
				{	
					openURL();
				}
				updateGo();
				
				if (showSlidePointer_lock)
					return;
				
				if (!showSlidePointer) {
					initialFingerCoordinates = Coordinates.make(webView.getMeasuredWidth()/2,webView.getMeasuredHeight()/2);
					slidePointer.setSlideStrategy(new AbsoluteSlideStrategyImpl());
					slidePointerView = new SlidePointerView(getParent(),slidePointer,initialFingerCoordinates);
					
					//Activating floating cursor					
					//webView.addView(slidePointerView);	
					webView.toggleCursorVisibility();
				} else {
					//De-activating floating cursor					
					//webView.removeView(slidePointerView);
					webView.toggleCursorVisibility();
				}
				showSlidePointer = !showSlidePointer;
				updateGo();
			}
		});

		urlField.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View view, int keyCode, KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_MENU && showSlidePointer == true) {
					/*	if (event.getAction() == KeyEvent.ACTION_DOWN && selectionState == false) {
							selectionState = !selectionState;
							// Add shift key
							webView.onKeyDown(KeyEvent.KEYCODE_SHIFT_LEFT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT));
							Toast.makeText(SlidePointerActivity.this, "Please select a text.", Toast.LENGTH_SHORT).show();    	
						}
						else if (event.getAction() == KeyEvent.ACTION_UP)
						{
							selectionState = false;
							webView.onKeyUp(KeyEvent.KEYCODE_SHIFT_LEFT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
							Toast.makeText(SlidePointerActivity.this, "Thank you.", Toast.LENGTH_SHORT).show();    							
						}*/
						
						// For emulator use press + release, press+release to toggle modes
						if (event.getAction() == KeyEvent.ACTION_DOWN)
						{
							selectionState = !selectionState;
							webView.toggleSelectingText();							
						}
					
						if (selectionState) {
							// Add shift key
							webView.onKeyDown(KeyEvent.KEYCODE_SHIFT_LEFT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT));
							Toast.makeText(SlidePointerActivity.this, "Please select a text.", Toast.LENGTH_SHORT).show();    	
						}
						else
						{
							webView.onKeyUp(KeyEvent.KEYCODE_SHIFT_LEFT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
							Toast.makeText(SlidePointerActivity.this, "Thank you.", Toast.LENGTH_SHORT).show();    							
						}
						return true;
					} else {
						//Toast.makeText(SlidePointerActivity.this, "Key down", Toast.LENGTH_SHORT).show();    	
				}
				if (keyCode == KeyEvent.KEYCODE_SPACE) {
					if (event.getAction() == KeyEvent.ACTION_DOWN  && showSlidePointer == true && selectionState == false) {
						// Add shift key
						selectionState = true;
						webView.toggleSelectingText();
						webView.onKeyDown(KeyEvent.KEYCODE_SHIFT_LEFT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT));
						Toast.makeText(SlidePointerActivity.this, "Please select a text (2).", Toast.LENGTH_SHORT).show();    	
						if (slidePointerIsDown == true)
							slidePointerDoDown = true;
					}
					else if (event.getAction() == KeyEvent.ACTION_UP  && showSlidePointer == true && selectionState == true)
					{
						//selectionState = false;
						webView.onKeyUp(KeyEvent.KEYCODE_SHIFT_LEFT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
						Toast.makeText(SlidePointerActivity.this, "Thank you.", Toast.LENGTH_SHORT).show();    							
						if (slidePointerIsDown == true)
							slidePointerDoUp = true;
					}
					return true;
				}
				
				updateGo();
				
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
                                       initialFingerCoordinates = Coordinates.make(webView.getMeasuredWidth()/2,webView.getMeasuredHeight()/2);
                   					   slidePointer.setSlideStrategy(new AbsoluteSlideStrategyImpl());
                   					   slidePointerView = new SlidePointerView(getParent(),slidePointer,initialFingerCoordinates);
                   					   webView.addView(slidePointerView);
                   					   showSlidePointer_lock = false;
                   					   urlField.setText("http://www.google.com/?q=" + selection);                   					   
                   					   selection=null;
                   					   updateGo();
                   					   openURL();
                               } else if ("e".equals(action)) {
                                   Toast.makeText(this, "e gesture done", Toast.LENGTH_SHORT).show();
                                   gestures.setEnabled(false);
                                   showSlidePointer = true;
                                   initialFingerCoordinates = Coordinates.make(webView.getMeasuredWidth()/2,webView.getMeasuredHeight()/2);
               					   slidePointer.setSlideStrategy(new AbsoluteSlideStrategyImpl());
               					   slidePointerView = new SlidePointerView(getParent(),slidePointer,initialFingerCoordinates);
               					   webView.addView(slidePointerView);
               					   showSlidePointer_lock = false;
               					   
               					   // Here we need to fire the intent to write an email with the content just pasted
               					   
               					   //  This will not work in the emulator, because the emulator does not have gmail. 
	               				   Intent intent = new Intent(Intent.ACTION_SENDTO);
	               	               intent.setData(Uri.parse("mailto:"));
	               	               intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
	               	               intent.putExtra(Intent.EXTRA_TEXT, selection);
	               	               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	               	               startActivity(intent); 
               	                
//               					   urlField.setText("http://www.google.com/?q=" + selection);
               					   selection=null;
               					   updateGo();
                               } else                                                                          
                            	   Toast.makeText(this, "Unrecognized gesture. Please draw 'S' or 'e'.", Toast.LENGTH_SHORT);
                       }
                       else                                                                          
                    	   Toast.makeText(this, "Unrecognized gesture. Please draw 'S' or 'e'.", Toast.LENGTH_SHORT);
               }
               else                                                                          
            	   Toast.makeText(this, "Unrecognized gesture. Please draw a 'S' or 'e'.", Toast.LENGTH_SHORT);
       }
}