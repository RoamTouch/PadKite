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
import android.widget.Toast;

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
							"var obj = { \"type\": null, \"content\": null };"+
		                
							"var elem = document.elementFromPoint(x,y);"+
							""+
							"obj.type = elem.tagName;"+

							"if (elem.tagName == \"IMG\")"+
								"obj = {\"type\": \"image\", \"content\": elem.src };"+
							"else if (elem.tagName == \"INPUT\")"+
								"obj = {\"type\": \"input\", \"content\": \"XYZ\" };"+
							"else if (elem.tagName == \"P\" || elem.tagName == \"DIV\")"+
							"{"+
		                        "var html = elem.innerHTML;"+
		                        "var newh = \"<span>\" + html.replace(/[ \\n]/g,\"</span> <span>\") + \"</span>\";"+
		                        "elem.innerHTML=newh;"+
		                        "var newElem = document.elementFromPoint(x,y);"+
		                        "obj.type=\"text\";"+
		                        "obj.content = newElem.innerHTML;"+
		                        "elem.innerHTML=html;"+ 		                                             
		                	"}"+                            
		                	"if (obj.content != null)"+
								"pBridge.type(obj.type, obj.content);"+
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
		
		public void type(final String type, final String content) {
			
			Log.i("TYPE",type);
            Toast.makeText(JSWebView.this, type+":"+content, Toast.LENGTH_SHORT).show();
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

			public boolean onTouch(final View v, final MotionEvent event) {
				
				((MyWebView)v).cx = event.getX();
				((MyWebView)v).cy = event.getY();
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					event.setAction(MotionEvent.ACTION_DOWN);
					Log.i("ONTOUCH",String.valueOf(((MyWebView)v).getHitTestResult().getType()));
					Log.i("ONTOUCH",String.valueOf(((MyWebView)v).getHitTestResult().getExtra()));
					 mHandler.post(new Runnable() {
			                public void run() {
								((WebView)v).loadUrl("javascript:whereInWorld("+event.getX()+","+event.getY()+")");
			                }
			            });
					v.invalidate();
				}
				
				return false;
			}});
    }
}