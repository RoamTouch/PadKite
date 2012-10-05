package com.roamtouch.webhook;

import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.visuals.RingController;

import android.app.Activity;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import com.roamtouch.webhook.WebHitTestResult;

/**
 * JScript Bridge 
 * On onPageFinished at fC a Java script snippet is loaded. 
 * Also on fC set the snippet cords mWebView.loadUrl("javascript:whereInWorld("+event.getX()+","+event.getY()+")");		
 */
public class ProxyBridge extends Activity {
										
	BrowserActivity mP;
	FloatingCursor fC;
	RingController rCtrl;
	
	WebHitTestResult wHitResult;
	
	
	
	public ProxyBridge() {		
	
	}
	
	public void setParent(BrowserActivity mP, FloatingCursor fC, RingController rCtrl){
		this.mP = mP; 
		this.rCtrl = rCtrl;
		this.fC = fC;			
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void currentSearch(String currentSearch){
		fC.setCurrentSearch(currentSearch);
	}	
	
	private String phrase = "";			
	Handler proxyHandler = new Handler();
	String[] inputTextArray = new String[3];
	
	public void getKeyboardInput(String[] inputTextArray){			
		
		//Javascript Char Codes 
		//http://www.cambiaresearch.com/articles/15/javascript-char-codes-key-codes
		
		SwifteeApplication.setPadKiteInputSpinnerStatus(SwifteeApplication.SUGGESTION_DATA_CALLED);
		
		this.inputTextArray = inputTextArray;
		proxyHandler.removeCallbacks(proxyRunnable);
		proxyHandler.postDelayed(proxyRunnable, 1000);			
		
	}
	
	Runnable proxyRunnable = new Runnable() {
		
	    public void run() {			    	
	    	
	    	String keyNum 		= inputTextArray[0];		
			String letter 		= inputTextArray[1];				
			String phrase 		= inputTextArray[2]+letter;
			
			if (phrase.equals("")){
				phrase = letter; 
			}
	    	
	    	if ( keyNum.equals("13") ) {
	    		
				try{
					if (phrase.equals("\r")){
						fC.setEmptyInput(true);
						mP.setInputBoxText("");
					}
				} catch (Exception e){
					Log.v("e", ""+e);
				}
				
			}  else {
				
				mP.setInputBoxText(phrase);					
				
				fC.setEmptyInput(false);
				
				if(!fC.isPasteTextIntoInputText()){					
					
					if (keyNum.equals("32")){
						phrase.substring(0, phrase.length() - 1);
					}	
					
					mP.launchSuggestionSearch(mP.getInputBoxText());
					//proxyHandler.postDelayed(proxyRunnable, 3000);
					
				} else {
					
					fC.setPasteTextIntoInputText(false);
					
				}
			}    	
	    	
	    	proxyHandler.removeCallbacks(proxyRunnable);
	    }
	};		
	
	Runnable inputRunnable = new Runnable() {
		
	    public void run() {			    	
	    	proxyHandler.postDelayed(proxyRunnable, 3000);			    	
	    	proxyHandler.removeCallbacks(proxyRunnable);
	    }
	};	
	
	public void getText(String text){
		SwifteeApplication.setInputText(text);			
	};	
	
	public void moveHitFontHeight(String tSize_1, String tSize_2){
		int size = 0;
		if (!tSize_1.equals("")){
			String[] splSize = tSize_1.split("px");
			String sizeStr = splSize[0];
			size = Integer.parseInt(sizeStr);					
		} else if (!tSize_2.equals("")){
			size = Integer.parseInt(tSize_2);
		}
		SwifteeApplication.setHitTestFontSize(size);
	}		
	
	public void passedData(String[] passed){
		Log.v("passed", "" + passed[0] + " " + passed[1] + " " + passed[2] );		
	};
	
	int logoX; 
	int logoY; 
	int logoWidth; 
	int logoHeight;	
	
	int imputFeft;
	int inputTop;
	int screenWidth;
	int screenHeight;	
	
	public void getJavascriptVariables(String[] inputArray){	
		
		imputFeft = Integer.parseInt(inputArray[0]);
		inputTop = Integer.parseInt(inputArray[1]);
		screenWidth = Integer.parseInt(inputArray[2]);
		screenHeight = Integer.parseInt(inputArray[3]);										
		SwifteeApplication.setLandingInputTop(inputTop);				
		
	}				
	
	public void fCContVisibility(String status){
		boolean is = false;
		if (status.equals("hidden")){
			is=true;
		} else if (status.equals("visible")){ 
			is=false;
		}
		SwifteeApplication.setLandingShrinked(is);
	}
	
	static int inX;
	static int inY;
	
	
	public void inputTouch(String inputXPos, String inputYPos){
			
		inX = Integer.parseInt(inputXPos) + logoX + logoWidth;
		this.mP.setInputX(inX);
		inY = Integer.parseInt(inputYPos) + 20;
		this.mP.setInputY(inY);
		
		runOnUiThread(new Runnable() {		        	
        	public void run() {        	
        		fC.loadPage("javascript:setInputFocus()");	
        		fC.invalidateWebView();
        		fC.invalidateAll();	        		
        		mP.runTouchInput(inX, inY);	        		
            }
        });
	}	
	
	public void what(String[] my){
		Log.v("my",""+my[0]+" "+my[1]+" "+my[2]+" "+my[3]+" "+my[4]);				
	}
	
	public void flat(String f){
		String flat = f;		
		Log.v("flat", ""+f);
	}
	
	
	private Rect rectResult;
	private String callback;
	private String nodeName;
	private String identifier;						
	private String href;
	private String src;	
	private String innerHTML;
	private String tagName;
	
	
	/**
	 * HitTestResutl Javascript
	 **/			
	public void setHitTestRestultJavaScript( 
			String _callBack,
			String x, String y, 
			String width, String height, 
			String _nodeName, 
			String _id,
			String _href, 
			String _src,
			String _innerHTML,
			String _tagName
			) { 				
		
		Log.v("node",""
				+ "callback: " +_callBack	
				+ " x: " +x
				+ " y: " +y 
				+ " width: " +width 
				+ " height: " +height
				+ " _nodeName: " +_nodeName 	
				+ " _id: " +_id
				+ " _href: " +_href
				+ " _src: " +_src
				+ " _innerHTML: " +_innerHTML
				+ " _tagName: " +_tagName 
				);
		
		callback = _callBack;
		nodeName = _nodeName;
		identifier = _id;	
		innerHTML = _innerHTML; 
		tagName = _tagName;
		
		if (!innerHTML.equals("") || !innerHTML.equals("undefined")){
			SwifteeApplication.setInnerHTML(innerHTML);
		}
		
		SwifteeApplication.setIdentifier(Integer.parseInt(identifier));
		
		if (!_href.equals("undefined"))
			href = _href;
		
		if (!_src.equals("undefined"))
			src = _src;
		
		rectResult = new Rect();
		rectResult.left = Integer.parseInt(x) - 5;
		rectResult.top = Integer.parseInt(y) - 5;
		rectResult.right = rectResult.left + Integer.parseInt(width) + 10;
		rectResult.bottom = rectResult.top + Integer.parseInt(height) + 10;
		
		//HIT TEST
		wHitResult= new WebHitTestResult();
		wHitResult.setCallBack(Integer.parseInt(callback));
		wHitResult.setRect(rectResult);		
		wHitResult.setIdentifier(Integer.parseInt(identifier));
		
		SwifteeApplication.setMasterRect(rectResult);
		
		runOnUiThread(new Runnable() {		        	
        	
			public void run() {  
				
        		if (nodeName.equals("A")){
        			
        			wHitResult.setType(wHitResult.TYPE_SRC_ANCHOR_TYPE);
        			
        			fC.proxyMoveHitTest(wHitResult);       			       			
					
				} else if (nodeName.equals("IMG")){
					
					if (src.equals("")){								
						
						wHitResult.setType(wHitResult.TYPE_IMAGE_ANCHOR_TYPE);
						
						setHitTestCallBack(wHitResult);
						
						//fC.applyImageResource(R.drawable.link_image_cursor, "link_image_cursor");			
						
					} else {
						
						wHitResult.setType(wHitResult.TYPE_IMAGE_ANCHOR_TYPE);		
						
						setHitTestCallBack(wHitResult);
	
						//fC.applyImageResource(R.drawable.link_image_cursor, "link_image_cursor");
						
					}				
					
				} else if (nodeName.equals("P")){
					
					wHitResult.setType(wHitResult.TYPE_TEXT_TYPE);			
					
					setHitTestCallBack(wHitResult);
					
					//fC.applyImageResource(R.drawable.text_cursor, "text_cursor");		
					
					
				}          		
        		
            }
        });					
		
	}
	
	
	public void setHitTestCallBack(WebHitTestResult result){		
				
		switch (result.getCallBack()){
		
			case WebHitTestResult.CALLBACK_MOVEHIT:
				fC.proxyMoveHitTest(wHitResult);
				break;
				
			case WebHitTestResult.CALLBACK_SINGLE_TOUCH:
				fC.proxyTouchHitTest(wHitResult);
				break;
				
			case WebHitTestResult.CALLBACK_REFRESH_MENU:
				fC.refreshMenu(result.getRect());
				break;
				
		}		
		
		
	}
	
	
	/**
	 * Panel HitTestResutl Javascript
	 **/			
	public void setPanelHitTestRestultJavaScript( 
			
			String x, String y, 
			String width, String height, 
			String _nodeName, 
			String _id,
			String _href, 
			String _src) { 				
		
		//Log.v("_nodeName",""+_nodeName);
		
		nodeName = _nodeName;
		identifier = _id;	
		
		SwifteeApplication.setIdentifier(Integer.parseInt(identifier));
		
		if (!_href.equals("undefined"))
			href = _href;
		
		if (!_src.equals("undefined"))
			src = _src;
		
		rectResult = new Rect();
		rectResult.left = Integer.parseInt(x) - 5;
		rectResult.top = Integer.parseInt(y) - 5;
		rectResult.right = rectResult.left + Integer.parseInt(width) + 10;
		rectResult.bottom = rectResult.top + Integer.parseInt(height) + 10;
		
		//SwifteeApplication.setPanelRect(rectResult);				
		
	}
	

	
	
	
	
	
	
}	
	

