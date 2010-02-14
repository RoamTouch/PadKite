package com.roamtouch.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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

import com.roamtouch.domain.slide.pointer.SlidingPointer;
import com.roamtouch.domain.slide.pointer.SlidingPointerImpl;
import com.roamtouch.domain.slide.position.Coordinates;
import com.roamtouch.domain.slide.strategy.AbsoluteSlidingStrategy;
import com.roamtouch.view.pointer.SlidingPointerView;

public class SlidingPointerActivity extends Activity {
	
	private void openURL(WebView webView,EditText url) {
		webView.loadUrl(url.getText().toString());
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

		//Configure webview
		final WebView webView = (WebView) findViewById(R.id.webview);
		
		webView.setWebViewClient(new GestureWebViewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("http://www.google.com.ar");
		webView.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i("getHitTestResult().getType()",String.valueOf(((WebView) v).getHitTestResult().getType()));
				Log.i("getHitTestResult().getExtra()",String.valueOf(((WebView) v).getHitTestResult().getExtra()));
				return false;
			}});
		
		//Configure edittext
		final EditText url = (EditText) findViewById(R.id.url);
		url.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					openURL(webView,url);
					return true;
				} else {
					return false;
				}
			}
		});
		
		
		final SlidingPointer slidingPointer = new SlidingPointerImpl(Coordinates.make(100,100));
		
		final SlidingPointerView slidingPointerView = (SlidingPointerView) findViewById(R.id.transparentview);
		slidingPointerView.setSlidingPointer(slidingPointer);
		
		//Configure button
		final Button go = (Button) findViewById(R.id.pointer);
		go.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				slidingPointerView.getSlidingPointer().setSlideStrategy(new AbsoluteSlidingStrategy());
				slidingPointerView.setShowPointer(!slidingPointerView.isShowPointer());;
			}
		});

		
		//Configure transparentView
		slidingPointerView.setWebView(webView);
		slidingPointerView.setUrl(url);
		slidingPointerView.setGo(go);
	
	}
}
