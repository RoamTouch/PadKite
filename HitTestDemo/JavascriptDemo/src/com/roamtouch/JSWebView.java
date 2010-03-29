package com.roamtouch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class JSWebView extends Activity {
	
	 private Handler mHandler = new Handler();

	 private class GestureWebChromeClient extends WebChromeClient {
	        @Override
	        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
	            Log.i("Alert", message);
	            result.confirm();
	            return true;
	        }
	    }

		private class GestureWebViewClient extends WebViewClient {

			@Override
			public void onPageFinished(WebView view, String url) {
				
				final String snippet = 
					"javascript:"+
						"function whereInWorld(x,y) {"+
							"pBridge.type(document.elementFromPoint(x,y).tagName);"+
						"}";
				
				view.loadUrl(snippet);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		}

	public class ProxyBridge {
		
		public void type(final String type) {
			
			Log.i("TYPE",type);
		}
		
		public int one () {
			return 1;
		}
	}

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        MyWebView wView = (MyWebView)findViewById(R.id.wv1);
        
        ProxyBridge pBridge = new ProxyBridge();
        
        wView.addJavascriptInterface(pBridge, "pBridge");
        
        WebSettings wSet = wView.getSettings();
        wSet.setJavaScriptEnabled(true);
		wView.setWebViewClient(new GestureWebViewClient());
		wView.setWebChromeClient(new GestureWebChromeClient());
		wView.loadUrl("http://www.google.com.ar");
		wView.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(final View v, final MotionEvent event) {
				
				((MyWebView)v).updateCoordinates(event, event.getX(), event.getY());
				
//				((MyWebView)v).cx = event.getX();
//				((MyWebView)v).cy = event.getY();
//				if (event.getAction() == MotionEvent.ACTION_MOVE) {
//					event.setAction(MotionEvent.ACTION_DOWN);
//					Log.i("ONTOUCH",String.valueOf(((MyWebView)v).getHitTestResult().getType()));
//					Log.i("ONTOUCH",String.valueOf(((MyWebView)v).getHitTestResult().getExtra()));
//					 mHandler.post(new Runnable() {
//			                public void run() {
//								((WebView)v).loadUrl("javascript:whereInWorld("+event.getX()+","+event.getY()+")");
//			                }
//			            });
//					v.invalidate();
//				}
				
				return false;
			}});
    }
}