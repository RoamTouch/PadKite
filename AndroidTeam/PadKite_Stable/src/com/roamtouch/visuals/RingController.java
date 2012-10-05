package com.roamtouch.visuals;

import java.util.Vector;

import com.roamtouch.webhook.WebHitTestResult;

import android.webkit.WebView;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.utils.COLOR;
import com.roamtouch.utils.ColorUtils;
import com.roamtouch.visuals.RingView;

public class RingController extends FrameLayout {
	
	private RingView rV = null;	
	
	private WebView cW;
	private BrowserActivity mP;
	private FloatingCursor fC;
	
	Rect re = new Rect();   
	Rect mRect = new Rect();
	
	int[] c;
	int r;
	int g;
	int b;
	String t;
	int w;
	int l;
	int TYPE;

	int scrollX;
	int scrollY;	
	
	int rotatedTab;
	
	private String[] titles;
	private int[][] tabColors;
	private int[] ringColors;
	private Vector ringVectors = new Vector();

	private int identifier;
	private int hitPKIndex;
	private int hitPKTabIndex;
	
	Object[][] data;
	
	int accumulator = 0;
	
	private boolean ring;
	
	private int storeType;
	
	private int amountTabs;
	
	int x;
	int W;
	int y;
  	int H;    
	
  	int cType;
  	int tabHeight;
  	
  	int percentTab;
  	
  	int yRing;
  	int hRing;
  		
	int edge = SwifteeApplication.getEdge();
	
	public RingController(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);	
	}
	
	private void init(Context context) {
		rV = new RingView(getContext(), this);			
		addView(rV);		
	}
	
	public void setParent(BrowserActivity p, FloatingCursor f, WebView w) {
		mP = p;		
		cW = w;	
		fC = f;
		rV.setGrandParent(mP, fC);	
	}			
	
	/*public void ringToOriginalColor(){		
		//Turn color back to NONE, in case 3 colors back to GRAY.			 
		if (SwifteeApplication.getSingleFingerSteps()== 3){
			setRingcolor(COLOR.PAINT_GRAY);				
		} else if (SwifteeApplication.getSingleFingerSteps()== 2){		
			setRingcolor(COLOR.PAINT_BLUE);
		}		
	}*/	
	
	/**
	 * Paing WebKit ring blue on start if two fingers.
	 */
	/*public void paintRingBlue(){
		setRingcolor(COLOR.PAINT_BLUE);
	}*/
	
	public void invalidateTips(){
		rV.postInvalidate();
	}
		
	/**
	 * Identifyer checker
	 */
	public void setDrawStyle(int type, Object[] param, int identifier){		
						
		 Throwable t = new Throwable(); StackTraceElement[] elements =
		 t.getStackTrace(); String calleeMethod =
		 elements[0].getMethodName(); String callerMethodName =
		 elements[1].getMethodName(); String callerClassName =
		 elements[1].getClassName(); Log.v("call",
		 "callerMethodName: "+callerMethodName+
		 " callerClassName: "+callerClassName );
		
		if (this.identifier != identifier) { //   || (identifier==id && fC.isFromParser())){ //first run or GetLink call FromParser.			
			renderAssets(type, param, true);		
		} else if ( this.identifier == identifier ) {
			int index = SwifteeApplication.getPKTabIndex();
			setTabToTop(index,type);			
			renderAssets(type, param, false);
		}	
		
		this.identifier = identifier;
		
	} 
	
	private void renderAssets(int type, Object[] param, boolean redrawRect){	
		
		
		rV.setIdentifier(identifier);		
		rV.setDrawType(type);
		
		cType = SwifteeApplication.getCType(); 	
		edge = SwifteeApplication.getEdge();		
		
		/** No need to set rect again 
		 * if identifier is the same**/
		if (redrawRect) {
			
			re = (Rect) param[0];	
			rV.setInitRect(re);
			
			storeType = type;		
		
			ringVectors = new Vector();				
			
			titles = (String[]) param[1];
			rV.setTitles(titles);			
			
			tabColors = (int[][]) param[2];
			rV.setTabColors(tabColors);
			
			if (param.length>3){				
				ringColors = (int[]) param[3];
				rV.setRingColors(ringColors);
			} else if (param.length<3) {
				ring=true;
			}	
			
			rV.setHitPKIndex(hitPKIndex);
		}
		 	
	 	if (re!=null){ 			   			
	 		
        	int xPos = re.left - cW.getScrollX();
        	int yPos = re.top - cW.getScrollY();   
	 		
	 		scrollX = cW.getScrollX();
		 	scrollY = cW.getScrollY();
        	
        	re.left 	= re.left 	+ scrollX;
        	re.right 	= re.right 	- scrollX;
        	re.top 	 	= re.top 	- scrollY;
        	re.bottom 	= re.bottom - scrollY;
        	
        	rV.setRect(re);      	    	
        	rV.setEdge(edge);  
        	
        	SwifteeApplication.setMasterScrollRect(re);
        	      	
        	if (cType == WebHitTestResult.TYPE_TEXT_TYPE){        		
        		mRect.left = re.left - 10;        		 
        		mRect.top = re.top - 10;        		
        		mRect.right = re.right + 10;
        		mRect.bottom = re.bottom + 10;        		
        	}  else {        		
        		mRect = re;
        	}
        	
        	x = mRect.left - (edge/4) + 1;
        	W = mRect.right - mRect.left + edge - 10;
        	
        	int percentage;
        	SwifteeApplication.setTabHeight(SwifteeApplication.getTabHeight());
			
        	switch (cType) {										
					
	    		case SwifteeApplication.TYPE_PADKITE_INPUT:
	    			percentage = SwifteeApplication.PERCENTAGE_TAB_PADKITE_IMPUT; 
	    			percentTab = (percentage*W)/100;	    			
	    			break;
	    		
	    		case SwifteeApplication.TYPE_PADKITE_PANEL:
	    			percentage = SwifteeApplication.PERCENTAGE_TAB_PANEL; 
	    			percentTab = (percentage*W)/100;	    			
	    			break;
	    			
	    		case SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER:	    			
	    			percentage = SwifteeApplication.PERCENTAGE_TAB_WINDOWS_MANAGER; 
	    			percentTab = (percentage*W)/100;	    			
	    			break;
	    			
	    		case SwifteeApplication.TYPE_PADKITE_SERVER:	
	    		case WebHitTestResult.TYPE_SRC_ANCHOR_TYPE:	    			
	    			if (titles.length<=2){
	    				percentage = SwifteeApplication.PERCENTAGE_TAB_ANCHOR;
	    			} else {
	    				percentage = 40;
	    			}
	    			percentTab = (percentage*W)/100;	   			
	    			break;
	    			
	    		case WebHitTestResult.TYPE_TEXT_TYPE:
	    			percentage = SwifteeApplication.PERCENTAGE_TAB_TEXT; 
	    			percentTab = (percentage*W)/100;   			
	    			break;      		
		    }			
			
			if ( cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE
				|| cType == SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER
				|| cType == SwifteeApplication.TYPE_PADKITE_SERVER){
								
				//y = mRect.bottom - 4;
				y = mRect.top - SwifteeApplication.getTabHeight() - 6;
      	    	H = SwifteeApplication.getTabHeight();
      	    	
			} else if ( cType == SwifteeApplication.TYPE_PADKITE_INPUT 
					|| cType == SwifteeApplication.TYPE_PADKITE_PANEL ) {	
				
				y = mRect.top - SwifteeApplication.getTabHeight() - 6;
      	    	H = SwifteeApplication.getTabHeight();
      	    	
      	    } else if ( cType == WebHitTestResult.TYPE_TEXT_TYPE ){
      	    	
      	    	yRing = mRect.top;      	    	
      	    	hRing = mRect.bottom - mRect.top;   	
      	    	rV.setXYWH(x, yRing, W-(edge/4), hRing);   	
      	    	y = mRect.top - SwifteeApplication.getTabHeight();      	    	
      	    	H = SwifteeApplication.getTabHeight();
      	    	
      	    }		
			  	
        	int remaining = W - percentTab;
        	amountTabs = titles.length;
        	SwifteeApplication.setTabsAmountOf(amountTabs);
        	int spacer = remaining/(amountTabs-1);       	
        	data = new Object[amountTabs][14];
        	 	        	
        	for (int i=0; i<amountTabs; i++){   		
        		     		       		
        		//rV.setOver(over);	        			        		
        		data[i][0] = x;
        		data[i][1] = y;	      
        		data[i][2] = percentTab;        		  		
        		data[i][3] = H;
        		data[i][4] = titles[i]; 
        		data[i][5] = tabColors[i];
        		data[i][6] = x + 30; //Text
        		
        		if (cType != WebHitTestResult.TYPE_TEXT_TYPE){       		
        			data[i][7] = ringColors[i];
        		}
        		
        		Rect re = new Rect(); 
        		re.left = x;
        		re.right = 200;//xTabPos+xWidth;
        		re.top = y+H;
        		re.bottom = H;
        		data[i][8] = re;  	
          		data[i][9] = i;  	
          		data[i][10] = spacer;	
          		data[i][11] = amountTabs;
          		data[i][12] = tabColors[i]; 
          		
          		x += spacer;     		     			
        	}	     	        	
        	
        	//Set Top with new Vector	        	
        	rV.setData(data);
        	hitPKTabIndex = SwifteeApplication.getPKTabIndex();
        	if (hitPKTabIndex==-1){ hitPKTabIndex=0;}
        	setTabToTop(hitPKTabIndex,type);   
        	if ( cType == WebHitTestResult.TYPE_TEXT_TYPE ){ H = yRing + hRing; 	}
        	
        	boolean expanded = SwifteeApplication.getExpanded();  				
        			
        	rV.setBitmap(x, y, W, H + (edge*2) + re.height());	 //note: the edge/2 comes from the below arc left right.
        	
        	//Reset rects due to text rect adjustment mRect
        	re = new Rect();
    	 	mRect = new Rect();
    	 	
        }
	 	accumulator = 0;
	 	
	 	//Draw Upside down tab while upper scrolling. 
	 	//if (re.top < 20){
	 	//	rV.setRotatedTab(1, mRect.centerX(), mRect.centerY());
	 	//}	 	
	}
	
	public void refresh(Rect r){	
		re = r;
		renderAssets(storeType, null, false);
	}
	
	int topId;
	
	public void setTabToTop(int tabIndex, int type){	 
		
 		rV.setTopId(tabIndex);
		topId = tabIndex;
		
		Object[][] top = new Object[amountTabs][10];
		
		boolean isParser = fC.isFromParser();
		
		if ( cType != WebHitTestResult.TYPE_TEXT_TYPE && !isParser ){			
			int col = (Integer) data[tabIndex][7];		
			//setRingcolor(col);
		}	
		
		switch (amountTabs){
			case 1:
				top = top;
				break;
			case 2:
				top = adjustTopTwo(top, tabIndex);
				break;
			case 3:
				top = adjustTopThree(top, tabIndex);
				break;
			case 4:
				top = adjustTopFour(top, tabIndex);
				break;
			case 5:
				top = adjustTopFive(top, tabIndex);
				break;
		}
		
		
		mP.setTabsActivated(true);
		rV.setData(top);		
	}
	
	private Object[][] adjustTopTwo(Object[][] top, int tabIndex){		
		switch(tabIndex){				
			case 0:
				top[0] = data[1];
				data[0][13] = 1;
				
				top[1] = data[0];
				data[1][13] = 0;
				break;		
			case 1:				
				top[0] = data[0];
				data[0][13] = 0;
				
				top[1] = data[1];
				data[1][13] = 1;
				break;							
		}		
		return top;		
	}
	
	private Object[][] adjustTopThree(Object[][] top, int tabIndex){		
		switch(tabIndex){				
			case 0:
				
				top[0] = data[2];
				data[0][13] = 2;
				
				top[1] = data[1];
				data[1][13] = 1;
				
				if (cType == WebHitTestResult.TYPE_TEXT_TYPE){					
					data[2][5] = data[2][12];
					data[1][5] = data[1][12];
					int[] col = (int[]) data[0][5]; 
					data[0][5] = ColorUtils.checkDarkColor(col);					
				} 
				
				top[2] = data[0];
				data[2][13] = 0;
				break;		
				
			case 1:				
				top[0] = data[2];
				data[0][13] = 2;
				
				top[1] = data[0];
				data[1][13] = 0;
				
				if (cType == WebHitTestResult.TYPE_TEXT_TYPE){
					data[2][5] = data[2][12]; 
					data[0][5] = data[0][12];
					int[] col = (int[]) data[1][5];
					data[1][5] = ColorUtils.checkDarkColor(col);				 
				}	
				
				top[2] = data[1];
				data[2][13] = 1;
				break;	
			case 2:				
				top[0] = data[0];
				data[0][13] = 0;
				
				top[1] = data[1];
				data[1][13] = 1;
				
				if (cType == WebHitTestResult.TYPE_TEXT_TYPE){
					data[0][5] = data[0][12];
					data[1][5] = data[1][12];
					int[] col = (int[]) data[2][5];
					data[2][5] = ColorUtils.checkDarkColor(col);									
				}
				
				top[2] = data[2];
				data[2][13] = 2;
				
				break;				
		}		
		return top;		
	}
	
	private Object[][] adjustTopFour(Object[][] top, int tabIndex){		
		
		switch(tabIndex){	
		
			case 0:
				top[0] = data[3];
				data[0][13] = 3;
				
				top[1] = data[2];
				data[1][13] = 2;
				
				top[2] = data[1];
				data[2][13] = 1;
				
				top[3] = data[0];
				data[3][13] = 0;
				break;	
				
			case 1:				
				top[0] = data[3];
				data[0][13] = 3;
				
				top[1] = data[2];
				data[1][13] = 2;
				
				top[2] = data[0];
				data[2][13] = 0;
				
				top[3] = data[1];
				data[3][13] = 1;				
				break;	
				
			case 2:
				top[0] = data[0];
				data[0][13] = 0;
				
				top[1] = data[3];
				data[1][13] = 3;
				
				top[2] = data[1];
				data[2][13] = 1;
								
				top[3] = data[2];
				data[3][13] = 2;				
				break;
			case 3:
				top[0] = data[0];
				data[0][13] = 0;
				
				top[1] = data[1];
				data[1][13] = 1;
				
				top[2] = data[2];
				data[2][13] = 2;
				
				top[3] = data[3];
				data[3][13] = 3;
				break;		
		}		
		return top;		
	}
	
	private Object[][] adjustTopFive(Object[][] top, int tabIndex){		
		
		switch(tabIndex){
		
			case 0:
				
				top[0] = data[4];
				data[0][13] = 4;
				
				top[1] = data[3];
				data[1][13] = 3;
				
				top[2] = data[2];
				data[2][13] = 2;
				
				top[3] = data[1];
				data[3][13] = 1;
				
				top[4] = data[0];
				data[4][13] = 0;				
				break;
				
			case 1:
				
				top[0] = data[4];
				data[0][13] = 4;
				
				top[1] = data[3];
				data[1][13] = 3;
				
				top[2] = data[2];
				data[2][13] = 2;
				
				top[3] = data[0];
				data[3][13] = 0;
				
				top[4] = data[1];
				data[4][13] = 1;				
				break;	
				
			case 2:
				top[0] = data[0];
				data[0][13] = 0;
				
				top[1] = data[4];
				data[1][13] = 4;
				
				top[2] = data[3];
				data[2][13] = 3;
				
				top[3] = data[1];
				data[3][13] = 1;
				
				top[4] = data[2];
				data[4][13] = 2;
				break;
				
			case 3:
				
				top[0] = data[0];
				data[0][13] = 0;
				
				top[1] = data[1];
				data[1][13] = 1;
				
				top[2] = data[2];
				data[2][13] = 2;
				
				top[3] = data[4];
				data[3][13] = 4;
				
				top[4] = data[3];
				data[4][13] = 3;				
				break;
				
			case 4:
				top[0] = data[0];
				data[0][13] = 0;	
				
				top[1] = data[1];
				data[1][13] = 1;	
				
				top[2] = data[2];
				data[2][13] = 2;	
				
				top[3] = data[3];
				data[3][13] = 3;	
				
				top[4] = data[4];
				data[4][13] = 4;	
				break;
				
		}	
		return top;
	}
	
	
	private boolean persist;

	/*public void peristTab(int id, int type){	
		rV.setTopId(id);
		topId = id;
		Object[][] top = new Object[5][10];
		if (type != SwifteeApplication.DRAW_RING) {	
			Object[] dataPersist = (Object[]) data[id];			
			int color = (Integer) dataPersist[7];
			setRingcolor(color);
			//if (fC.mLongTouchHack){
			cW.focusNodeAt(fC.getStX(), fC.getStY());
			//}
		}	
		top[id] = data[id];	
		mP.tabsActivated=true;
		persist=true;
		rV.setData(top);
		rV.setDrawType(SwifteeApplication.DRAW_TABS);
	}*/
	
	 public boolean isTabArmed(){	 
		 if( data!=null){    	    	   		
   	   		if (data.length>0){
   	   			return true;
   	   		}
	   }
	   return false;
   }
	
	
	public int getTopId() {
		return topId;
	}
	
	public void clearData(){
		this.data = null;		
	}
	
	//Enlarges the tab by setting a lower y;
	/*private void enlargeTab(int id){
		Vector tV = new Vector();
		Object[] data = (Object[]) tempVector.get(id);	  
		int yTabPos = (Integer) data[2];
		int newHeight = yTabPos - enlarged;	
		data[2] = newHeight;
		tempVector.add(data);		 
	}*/
				
	/**Draw Nothing**/
	public void drawNothing(){		
		if (ringVectors!=null){
			ringVectors = null;
		}		
		mP.setTabsActivated(false);	
		rV.setDrawType(SwifteeApplication.DRAW_NOTHING);		
	}
	
 	/**
	 * Sets the link ring color to blue, green and red
	 * @param color
	 */
	/*private int colorId;	
	public void setRingcolor(int colorId){		
		this.colorId = colorId;				
		loopRingColor();		
		//loopRingColor();
		//setRingColor = new RingColor();
		//setRingColor.execute();
		//setRingColor.execute();
	};*/
		
	/*public void persistRingColor(){		
		//Thread ring = new Thread(){			
			//public void run(){				
				
			while(BrowserActivity.isTabsActivated()){	   
				fC.setFocusNodeAtRect(re);							
			    loopRingColor();
			}
				    
														 
		    	//}				
			//}			
		//};
		//ring.start();
	}*/
	
	
	
	//private int m_interval = 600; // 5 seconds by default, can be changed later
	
	/* private AsyncTask setRingColor = new RingColor(); 
	 
	 private class RingColor extends AsyncTask  { 
		 
		 @Override
			protected Object doInBackground(Object... arg) {		
			 
			 while(BrowserActivity.isSuggestionActivated()){				 
	    		 loopRingColor();
	    	 }
			 
			 return true;
		 	}
		 
	 }*/
	
	/*private Handler m_handler = new Handler();
	
	Runnable m_statusChecker = new Runnable()
	{
	     @Override 
	     public void run() {
	    	 
	    	 while(BrowserActivity.isSuggestionActivated()){
	    		 loopRingColor(colorId);
	    	 }
	         // m_handler.postDelayed(m_statusChecker, m_interval);
	     }
	};

	private void startRepeatingTask()
	{
	    m_statusChecker.run(); 
	}

	private void stopRepeatingTask()
	{
	    m_handler.removeCallbacks(m_statusChecker);
	}*/
	
	
	 /*public	void loopRingColor(){
		
		switch (colorId){
		
		case SwifteeApplication.PAINT_GRAY: //gray	
									
			cW.setSelectionColor(0xffB0B0B0);
			cW.setCursorOuterColors(0xff6E6E6E, 0xff6E6E6E, 0xff6E6E6E, 0xff6E6E6E);
			cW.setCursorInnerColors(0xffD4D4D4, 0xffD4D4D4, 0xffD4D4D4, 0xffD4D4D4);
			cW.invalidate();
			break;
			
		case SwifteeApplication.PAINT_RED: //red	
			
			cW.setSelectionColor(0xffFF9F8C);
			cW.setCursorOuterColors(0xffFF6A4D, 0xffFF6A4D, 0xffFF6A4D, 0xffFF6A4D);
			cW.setCursorInnerColors(0xffFFCEC4, 0xffFFCEC4, 0xffFFCEC4, 0xffFFCEC4);
			cW.invalidate();
			break;
			
		case SwifteeApplication.PAINT_BLUE: //blue	
			
			cW.setSelectionColor(0xffb4d5fe);				
			cW.setCursorOuterColors(0xff0072FF, 0xff0072FF, 0xff0072FF, 0xff0072FF);
			cW.setCursorInnerColors(0xffA3CCFF, 0xffA3CCFF, 0xffA3CCFF, 0xffA3CCFF);
			cW.invalidate();
			break;
			
		case SwifteeApplication.PAINT_GREEN: //green				
			
			cW.setSelectionColor(0xffA7FCA4);				
			cW.setCursorOuterColors(0xff06A800, 0xff06A800, 0xff06A800, 0xff06A800);
			cW.setCursorInnerColors(0xffA9FFA6, 0xffA9FFA6, 0xffA9FFA6, 0xffA9FFA6);
			cW.invalidate();
			break;
			
		case SwifteeApplication.PAINT_YELLOW: //yeloww
			
			cW.setSelectionColor(0xffF5CD31);	
			cW.setCursorOuterColors(0xffF5CD31, 0xffF5CD31, 0xffF5CD31, 0xffF5CD31);
			cW.setCursorInnerColors(0xffFFEFAD, 0xffFFEFAD, 0xffFFEFAD, 0xffFFEFAD);
			cW.invalidate();
			break;
			
		case SwifteeApplication.PAINT_VIOLET: //violet
			
			cW.setSelectionColor(0xffF8ABFF);	
			cW.setCursorOuterColors(0xffDF2BF0, 0xffDF2BF0, 0xffDF2BF0, 0xffDF2BF0);
			cW.setCursorInnerColors(0xffEAADF0, 0xffEAADF0, 0xffEAADF0, 0xffEAADF0);
			cW.invalidate();
			break;
		
		case SwifteeApplication.PAINT_ORANGE: //orange
			
			cW.setSelectionColor(0xffFFEBAB);	
			cW.setCursorOuterColors(0xffF0CC2B, 0xffF0CC2B, 0xffF0CC2B, 0xffF0CC2B);
			cW.setCursorInnerColors(0xffF0E7AD, 0xffF0E7AD, 0xffF0E7AD, 0xffF0E7AD);
			cW.invalidate();
			break;
		
		case SwifteeApplication.PAINT_BLACK: //black
			
			cW.setSelectionColor(0xff000000);	
			cW.setCursorOuterColors(0xff000000, 0xff000000, 0xff000000, 0xff000000);
			cW.setCursorInnerColors(0xff000000, 0xff000000, 0xff000000, 0xff000000);
			
			//cW.setSelectionColor(0xffFFEBAB);	
			/cW.setCursorOuterColors(0xff1C1805, 0xff1C1805, 0xff1C1805, 0xff1C1805);
			//cW.setCursorInnerColors(0xff858585, 0xff858585, 0xff858585, 0xff858585);
			
			cW.invalidate();
			break;
			
		case SwifteeApplication.PAINT_RED_MAP: //red map
			
			cW.setSelectionColor(0xffFFEBAB);	
			cW.setCursorOuterColors(0xffFC786C, 0xffFC786C, 0xffFC786C, 0xffFC786C);
			cW.setCursorInnerColors(0xffFAB3AC, 0xffFAB3AC, 0xffFAB3AC, 0xffFAB3AC);
			cW.invalidate();
			break;
			
		case SwifteeApplication.PAINT_TURQUOISE: //turquoise
			
			cW.setSelectionColor(0xff00D49F);	
			cW.setCursorOuterColors(0xff00D49F, 0xff00D49F, 0xff00D49F, 0xff00D49F);
			cW.setCursorInnerColors(0xff70E0C4, 0xff70E0C4, 0xff70E0C4, 0xff70E0C4);
			cW.invalidate();
			break;
			
		case SwifteeApplication.PAINT_LIGHT_BLUE: //light blue
			
			cW.setSelectionColor(0xff18BAF0);	
			cW.setCursorOuterColors(0xff18BAF0, 0xff18BAF0, 0xff18BAF0, 0xff18BAF0);
			cW.setCursorInnerColors(0xff83D6F2, 0xff83D6F2, 0xff83D6F2, 0xff83D6F2);
			cW.invalidate();
			break;

		case SwifteeApplication.PAINT_LIGHT_GRAY: //light gray
			
			cW.setSelectionColor(0xffB5B5B5);	
			cW.setCursorOuterColors(0xffB5B5B5, 0xffB5B5B5, 0xffB5B5B5, 0xffB5B5B5);
			cW.setCursorInnerColors(0xff8F8F8F, 0xff8F8F8F, 0xff8F8F8F, 0xff8F8F8F);
			cW.invalidate();
			break;
			
		case SwifteeApplication.PAINT_FUXIA: //fuxia
			
			cW.setSelectionColor(0xff8C02F5);	
			cW.setCursorOuterColors(0xff8C02F5, 0xff8C02F5, 0xff8C02F5, 0xff8C02F5);
			cW.setCursorInnerColors(0xffD4B6FA, 0xffD4B6FA, 0xffD4B6FA, 0xffD4B6FA);
			cW.invalidate();
			break;	
			
		case SwifteeApplication.PAINT_APPLE: //fuxia
			
			cW.setSelectionColor(0xff9DC217);	
			cW.setCursorOuterColors(0xff9DC217, 0xff9DC217, 0xff9DC217, 0xff9DC217);
			cW.setCursorInnerColors(0xffCEDE95, 0xffCEDE95, 0xffCEDE95, 0xffCEDE95);
			cW.invalidate();
			break;
			
		case SwifteeApplication.PAINT_BLUE_PANEL: //fuxia
			
			cW.setSelectionColor(0xff6C7FA1);	
			cW.setCursorOuterColors(0xff6C7FA1, 0xff6C7FA1, 0xff6C7FA1, 0xff6C7FA1);
			cW.setCursorInnerColors(0xff6C7FA1, 0xff6C7FA1, 0xff6C7FA1, 0xff6C7FA1);
			cW.invalidate();
			break;		
			
		//PANTONE INPUT COLORS
			
		case SwifteeApplication.RING_PANTONE_192C_MAIN: 				
			cW.setSelectionColor(0xffe70d47);	
			cW.setCursorOuterColors(0xffe70d47, 0xffe70d47, 0xffe70d47, 0xffe70d47);
			cW.setCursorInnerColors(0xffe70d47, 0xffe70d47, 0xffe70d47, 0xffe70d47);
			cW.invalidate();
			break;	
			
		case SwifteeApplication.RING_PANTONE_YellowC_MAIN: 
			cW.setSelectionColor(0xfffee000);	
			cW.setCursorOuterColors(0xfffee000, 0xfffee000, 0xfffee000, 0xfffee000);
			cW.setCursorInnerColors(0xfffee000, 0xfffee000, 0xfffee000, 0xfffee000);
			cW.invalidate();
			break;	
			
		case SwifteeApplication.RING_PANTONE_246C_MAIN: 
			cW.setSelectionColor(0xffc821ac);	
			cW.setCursorOuterColors(0xffc821ac, 0xffc821ac, 0xffc821ac, 0xffc821ac);
			cW.setCursorInnerColors(0xffc821ac, 0xffc821ac, 0xffc821ac, 0xffc821ac);
			cW.invalidate();
			break;		
			
		case SwifteeApplication.RING_PANTONE_631C_MAIN: 
			cW.setSelectionColor(0xff34b5d0);	
			cW.setCursorOuterColors(0xff34b5d0, 0xff34b5d0, 0xff34b5d0, 0xff34b5d0);
			cW.setCursorInnerColors(0xff34b5d0, 0xff34b5d0, 0xff34b5d0, 0xff34b5d0);
			cW.invalidate();
			break;		
			
		case SwifteeApplication.RING_PANTONE_444C_MAIN: 
			cW.setSelectionColor(0xff717f81);	
			cW.setCursorOuterColors(0xff717f81, 0xff717f81, 0xff717f81, 0xff717f81);
			cW.setCursorInnerColors(0xff717f81, 0xff717f81, 0xff717f81, 0xff717f81);
			cW.invalidate();
			break;
			
		//PANTONE PANEL COLORS
			
		case SwifteeApplication.RING_PANTONE_375C_MAIN: 
			cW.setSelectionColor(0xff8fd400);	
			cW.setCursorOuterColors(0xff8fd400, 0xff8fd400, 0xff8fd400, 0xff8fd400);
			cW.setCursorInnerColors(0xff8fd400, 0xff8fd400, 0xff8fd400, 0xff8fd400);
			cW.invalidate();
			break;		
		
		case SwifteeApplication.RING_PANTONE_130C_MAIN: 
			cW.setSelectionColor(0xfff4aa00);	
			cW.setCursorOuterColors(0xfff4aa00, 0xfff4aa00, 0xfff4aa00, 0xfff4aa00);
			cW.setCursorInnerColors(0xfff4aa00, 0xfff4aa00, 0xfff4aa00, 0xfff4aa00);
			cW.invalidate();
			break;	
			
		
		case SwifteeApplication.RING_PANTONE_2736C_MAIN: 
			cW.setSelectionColor(0xff1c29a7);	
			cW.setCursorOuterColors(0xff1c29a7, 0xff1c29a7, 0xff1c29a7, 0xff1c29a7);
			cW.setCursorInnerColors(0xff1c29a7, 0xff1c29a7, 0xff1c29a7, 0xff1c29a7);
			cW.invalidate();
			break;	
			
			
		case SwifteeApplication.RING_PANTONE_ProcessBlueC_MAIN: 
			cW.setSelectionColor(0xff0085cf);	
			cW.setCursorOuterColors(0xff0085cf, 0xff0085cf, 0xff0085cf, 0xff0085cf);
			cW.setCursorInnerColors(0xff0085cf, 0xff0085cf, 0xff0085cf, 0xff0085cf);
			cW.invalidate();
			break;
			
		//PANTONE ANCHOR COLORS
			
		case SwifteeApplication.RING_PANTONE_340C_MAIN: 
			cW.setSelectionColor(0xff009661);	
			cW.setCursorOuterColors(0xff009661, 0xff009661, 0xff009661, 0xff009661);
			cW.setCursorInnerColors(0xff009661, 0xff009661, 0xff009661, 0xff009661);
			cW.invalidate();
			break;
			
		//PANTONE SET COLORS
			
		case SwifteeApplication.RING_PANTONE_158C_MAIN: 
			cW.setSelectionColor(0xffea7125);	
			cW.setCursorOuterColors(0xffea7125, 0xffea7125, 0xffea7125, 0xffea7125);
			cW.setCursorInnerColors(0xffea7125, 0xffea7125, 0xffea7125, 0xffea7125);
			cW.invalidate();
			break;
		}
		
	}*/
	
	
	public void setWebView(WebView wv){
		cW = wv;
	}

	public void setIdentifier(int ide) {
		identifier = ide;		
	}
	
	public Vector getringVectors() {
		return ringVectors;
	}

	public void setringVectors(Vector ringVectors) {
		this.ringVectors = ringVectors;
	}	
	
	
	public void drawRing(int identifier, int type, Rect re, int[] color) {	
		
		if (this.identifier != identifier) { 	
			rV.setRingColors(color);
			rV.setXYWH(re.left, re.top, re.right, re.bottom);		
			rV.setDrawType(SwifteeApplication.DRAW_RING);		
		}	
		
		this.identifier = identifier;
		
		
	}
	
}
