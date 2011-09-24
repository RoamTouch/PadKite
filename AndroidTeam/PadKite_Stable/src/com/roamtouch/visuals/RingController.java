 package com.roamtouch.visuals;

import roamtouch.webkit.WebHitTestResult;
import roamtouch.webkit.WebView;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.visuals.RingView;

public class RingController extends FrameLayout {
	
	private RingView rV = null;	
	
	private WebView cW;
	private BrowserActivity mP;
	private FloatingCursor fC;
	
	Rect re;    		
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
	
	private int identifier;
	
	public RingController(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context) {
		rV = new RingView(getContext());		
		addView(rV);
	}
	
	public void setParent(BrowserActivity p, FloatingCursor f, WebView w) {
		mP = p;
		cW = w;	
		fC = f;
	}			
	
	public void ringToOriginalColor(){
		
		/** Turn color back to NONE, in case 3 colors back to GRAY.**/			 
		if (SwifteeApplication.getSingleFingerSteps()== 3){
			setRingcolor(SwifteeApplication.PAINT_GRAY);				
		} else if (SwifteeApplication.getSingleFingerSteps()== 2){		
			setRingcolor(SwifteeApplication.PAINT_BLUE);
		}
		
	}	
	
	/**
	 * Paing WebKit ring blue on start if two fingers.
	 */
	public void paintRingBlue(){
		setRingcolor(SwifteeApplication.PAINT_BLUE);
	}
	
	public void invalidateTips(){
		rV.postInvalidate();
	}
	
	public void setDrawStyle(int type, Object[] param, int id){	
		
		Throwable t = new Throwable(); 
		StackTraceElement[] elements = t.getStackTrace(); 

		String calleeMethod = elements[0].getMethodName(); 
		String callerMethodName = elements[1].getMethodName(); 
		String callerClassName = elements[1].getClassName();	
		
		//Log.v("id"," this.identifier : " + this.identifier+ " id: " + id);
	
		if ( this.identifier != id ) {	//first run.		
			
			this.identifier = id;
			
			renderAssets(type, param, true);			
			
		}  else if ( this.identifier == id ) {
			
			renderAssets(type, param, false);
		}
		
		rV.setDrawType(type);
	} 
    	
	
	private void renderAssets(int type, Object[] param, boolean redrawRect){
		
		/** No need to set rect again 
		 * if identifier is the same**/
		if (redrawRect) {
			
			re = (Rect) param[0];
			
			scrollX = cW.getScrollX();
		 	scrollY = cW.getScrollY();
		 	
		 	rV.setScrollX(scrollX);
		 	rV.setScrollY(scrollY);	 
		 	
		 	if (re!=null){
		 		
	        	int xPos = re.left - cW.getScrollX();
	        	int yPos = re.top - cW.getScrollY();
	        	
	        	rV.setxPosTab(xPos);         	
		 		rV.setyPosTab(yPos);	 		  
	        	
	        	re.left 	= re.left 	+ scrollX;
	        	re.right 	= re.right 	- scrollX;
	        	re.top 	 	= re.top 	- scrollY;
	        	re.bottom 	= re.bottom - scrollY;
	        	
	        	int x = re.left - 5; 
	      	    int y = re.top  - 4;
	      	    int W = re.width()  + 10;
	      	    int H = re.height() + 10;	        	
	        	int[] coords = { x, y, W, H };	        			    	
	        	rV.setCords(coords);
	        }
		 	
		 	//Draw Upside down tab while upper scrolling. 
		 	if (re.top < 20){
		 		rV.setRotatedTab(true, re.centerX(), re.centerY());
		 	}	
		 	
		 	rV.setRectRight(re.right);
		 	rV.setRectBottom(re.bottom);		
		 	
		}
		
		//Color 
		c = (int[]) param[1];
		r = c[0];
		g = c[1];
		b = c[2];		
		rV.fillColor = Color.rgb(r, g, b);			
		
		//Draw text and ring color
		if (type != SwifteeApplication.DRAW_RING){			
			t = (String) param[2];
			rV.text = t;
			l = (Integer) param[3];
			setRingcolor(l);
		}	
		
	}	
				
	/**Draw Nothing**/
	public void drawNothing(){
		//this.identifier = id;
		rV.setDrawType(SwifteeApplication.DRAW_NONE);
	}	
	 
 	/**
	 * Sets the link ring color to blue, green and red
	 * @param color
	 */
	public void setRingcolor(int colorId){		
		
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
				cW.invalidate();
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
				
				cW.setSelectionColor(0xffFFEBAB);	
				cW.setCursorOuterColors(0xff1C1805, 0xff1C1805, 0xff1C1805, 0xff1C1805);
				cW.setCursorInnerColors(0xff858585, 0xff858585, 0xff858585, 0xff858585);
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
		}
	};	
	
	public void setWebView(WebView wv){
		cW = wv;
	}
	
}
