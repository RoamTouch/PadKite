package com.roamtouch.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebPage extends Activity{

		/** Called when the activity is first created. */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
	
			getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		 
			String url = getIntent().getStringExtra("webUrl");
			WebView mWebView= new WebView(this);

			mWebView.setScrollbarFadingEnabled(true);
			mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
			mWebView.setMapTrackballToArrowKeys(false); // use trackball directly
			// Enable the built-in zoom
			mWebView.getSettings().setBuiltInZoomControls(true);
			mWebView.getSettings().setJavaScriptEnabled(true);	
			mWebView.setWebViewClient(new MyWebViewClient());	
			mWebView.loadUrl(url);
			
			setContentView(mWebView);
		}
		private class MyWebViewClient extends WebViewClient {
		    @Override
		    public boolean shouldOverrideUrlLoading(WebView view, String url) {
		        view.loadUrl(url);
		        return true;
		    }
		}
	
}
