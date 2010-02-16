package com.roamtouch.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.os.Bundle;
import android.os.Handler;
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

import com.roamtouch.domain.slide.pointer.SlidingPointer;
import com.roamtouch.domain.slide.pointer.SlidingPointerImpl;
import com.roamtouch.domain.slide.position.Coordinates;
import com.roamtouch.domain.slide.strategy.AbsoluteSlidingStrategy;
import com.roamtouch.view.pointer.SlidingPointerView;

public class SlidingPointerActivity extends Activity implements
		OnGesturePerformedListener {

	private Handler mHandler = new Handler();

	private GestureLibrary mLibrary;
	private GestureOverlayView gestures;
	private SlidingPointerView slidingPointerView;
	private WebView webView;

	private void openURL(WebView webView, EditText url) {
		webView.loadUrl(url.getText().toString());
		webView.requestFocus();

	}

	private class GestureWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			Log.i("Alert", message);
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
					+

					"var elem = document.elementFromPoint(x,y);"
					+ ""
					+ "obj.type = elem.tagName;"
					+

					"if (elem.tagName == \"IMG\")"
					+ "obj = {\"type\": \"image\", \"content\": elem.src };"
					+ "else if (elem.tagName == \"INPUT\")"
					+ "obj = {\"type\": \"input\", \"content\": \"XYZ\" };"
					+ "else if (elem.tagName == \"P\" || elem.tagName == \"DIV\")"
					+ "{"
					+ "var html = elem.innerHTML;"
					+ "var newh = \"<span>\" + html.replace(/[ \\n]/g,\"</span> <span>\") + \"</span>\";"
					+ "elem.innerHTML=newh;"
					+ "var newElem = document.elementFromPoint(x,y);"
					+ "obj.type=\"text\";" + "obj.content = newElem.innerHTML;"
					+ "elem.innerHTML=html;" + "}" + "if (obj.content != null)"
					+ "pBridge.type(obj.type, obj.content);" + "}";

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

			Log.i("TYPE", type);
			Toast.makeText(SlidingPointerActivity.this, type + ":" + content,
					Toast.LENGTH_SHORT).show();
		}

		public int one() {
			return 1;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ProxyBridge pBridge = new ProxyBridge();

		webView = (WebView) findViewById(R.id.webview);

		WebSettings wSet = webView.getSettings();
		wSet.setJavaScriptEnabled(true);
		webView.addJavascriptInterface(pBridge, "pBridge");
		webView.setWebViewClient(new GestureWebViewClient());
		webView.setWebChromeClient(new GestureWebChromeClient());
		webView.loadUrl("http://www.google.com.ar");
		webView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(final View v, final MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					event.setAction(MotionEvent.ACTION_DOWN);
					Log.i("ONTOUCH",String.valueOf(((WebView)v).getHitTestResult().getType()));
					Log.i("ONTOUCH",String.valueOf(((WebView)v).getHitTestResult().getExtra()));
					 mHandler.post(new Runnable() {
			                public void run() {
								((WebView)v).loadUrl("javascript:whereInWorld("+event.getX()+","+event.getY()+")");
			                }
			            });
					v.invalidate();
				}
				
				return false;
			}
		});

		webView.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				Log.i("KEY ON LISTENER", String.valueOf(keyCode));
				Log.i("ENABLED", String.valueOf(slidingPointerView
						.isShowPointer()));
				if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
						&& slidingPointerView.isShowPointer()) {
					slidingPointerView.setShowPointer(false);
					gestures.setEnabled(true);
					return true;
				} else {
					return false;
				}
			}
		});

		// Configure edittext
		final EditText url = (EditText) findViewById(R.id.url);
		url.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					openURL(webView, url);
					return true;
				} else {
					return false;
				}
			}
		});

		final SlidingPointer slidingPointer = new SlidingPointerImpl(
				Coordinates.make(100, 100));

		slidingPointerView = (SlidingPointerView) findViewById(R.id.transparentview);
		slidingPointerView.setSlidingPointer(slidingPointer);

		// Configure button
		final Button go = (Button) findViewById(R.id.pointer);
		go.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				slidingPointerView.setShowPointer(!slidingPointerView
						.isShowPointer());
				slidingPointerView.getSlidingPointer().setSlideStrategy(
						new AbsoluteSlidingStrategy());
			}
		});

		// Configure transparentView
		slidingPointerView.setWebView(webView);
		slidingPointerView.setUrl(url);
		slidingPointerView.setGo(go);

		mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		if (!mLibrary.load()) {
			finish();
		}

		gestures = (GestureOverlayView) findViewById(R.id.gestures);
		gestures.addOnGesturePerformedListener(this);
		gestures.setEnabled(false);
	}

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
		if (predictions.size() > 0) {
			if (predictions.get(0).score > 1.0) {
				String action = predictions.get(0).name;
				Log.i("GESTURE", action);
				if ("S".equals(action)) {
					Toast.makeText(this, "S gesture done", Toast.LENGTH_SHORT)
							.show();
					gestures.setEnabled(false);
					slidingPointerView.setShowPointer(true);
				}
			}
		}
	}
}
