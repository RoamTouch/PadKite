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
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;

import com.roamtouch.drawing.FloatingCursorOverlayView;
import com.roamtouch.drawing.MyWebView;
import com.roamtouch.drawing.UrlView;

public class SlidingPointerActivity extends Activity {

	private FloatingCursorOverlayView drawing;
	private MyWebView webView;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		webView = (MyWebView) findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new GestureWebChromeClient());
		webView.loadUrl("http://www.google.com.ar");
		webView.requestFocus();

		// Configure edittext
		final UrlView url = (UrlView) findViewById(R.id.url);
		url.clearFocus();
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

		final ImageView cursor = (ImageView) findViewById(R.id.pointer);
		cursor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				drawing.setCursorEnabled(!drawing.isCursorEnabled());
			}
		});
		
		drawing = (FloatingCursorOverlayView) findViewById(R.id.drawing);
		webView.setDrawing(drawing);
		url.setDrawing(drawing);
	}
}