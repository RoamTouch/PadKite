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
		
	/**
	 * DRAW TABS
	 */
	public void drawGrayRing(){
		Object[] param =  { // DRAW TEXT RING				
				fC.getRect(),
				SwifteeApplication.GRAY,
				WebHitTestResult.EDIT_TEXT_TYPE
			};
		setDrawStyle(SwifteeApplication.DRAW_RING, param);	
	}
	
	public void drawRingAndTab(String text){ 
		Object[] param_TEXT =  { fC.getRect(),				
				SwifteeApplication.RED, 
				text,				
				SwifteeApplication.PAINT_RED,
				WebHitTestResult.EDIT_TEXT_TYPE
		}; 
		setDrawStyle(SwifteeApplication.DRAW_RING_AND_TAB, param_TEXT);
	}
	
	public void drawGreenTab(String text){
		Object[] param_default =  { fC.getRect(),			
				SwifteeApplication.GREEN,
				text,				
				SwifteeApplication.PAINT_GREEN					
			}; 
			setDrawStyle(SwifteeApplication.DRAW_TAB, param_default);		
	}	
	
	public void drawYellowTab(String text){
		Object[] param_default =  { fC.getRect(),
				SwifteeApplication.YELLOW,
				text,				
				SwifteeApplication.PAINT_YELLOW					
		}; 
		setDrawStyle(SwifteeApplication.DRAW_TAB, param_default);
	}
	
	public void drawBlueTab(String text){
		Object[] param =  { fC.getRect(),
				SwifteeApplication.BLUE,
				text,				
				SwifteeApplication.PAINT_BLUE							
		}; 
		setDrawStyle(SwifteeApplication.DRAW_TAB, param);	
	}
	
	/**
	 * DRAW RING AND TAB
	 */
	
	public void drawBlueRingAndTab(String text){
		Object[] param_TEXT =  { fC.getRect(),		
				SwifteeApplication.BLUE,
				text,					
				SwifteeApplication.PAINT_BLUE,
				WebHitTestResult.TEXT_TYPE
		}; 
		setDrawStyle(SwifteeApplication.DRAW_RING_AND_TAB, param_TEXT);	
	}
	
	public void drawYellowRingAndTab(String text){
		Object[] param_TEXT =  { fC.getRect(),
				SwifteeApplication.YELLOW,
				text,				
				SwifteeApplication.PAINT_YELLOW,
				WebHitTestResult.TEXT_TYPE
		}; 
		setDrawStyle(SwifteeApplication.DRAW_RING_AND_TAB, param_TEXT);
	}
	
	
	public void drawrRedRingAndTab(String text){
		Object[] param_TEXT =  { fC.getRect(),
				SwifteeApplication.YELLOW,
				text,				
				SwifteeApplication.PAINT_YELLOW,
				WebHitTestResult.TEXT_TYPE
		}; 
		setDrawStyle(SwifteeApplication.DRAW_RING_AND_TAB, param_TEXT);
	}	
	
	public void drawVioletRingAndTab(String text){
		Object[] param =  { fC.getRect(),				
				SwifteeApplication.VIOLET,
				text,					
				SwifteeApplication.PAINT_VIOLET,
				WebHitTestResult.EDIT_TEXT_TYPE
		}; 
		setDrawStyle(SwifteeApplication.DRAW_RING_AND_TAB, param);
	}
		
	
	/**
	 * DRAW TIPS
	 */
	/*public void drawTestTip(Rect rect){
		//TEST COMMENT
		String[] text = {"Lift your finger","to select the link"};
		Object[] paramTip =  { 
				rect,
				SwifteeApplication.SET_TIP_TO_RIGHT,
				text,
				500
			}; 
		tCtrl.setTipComment(SwifteeApplication.DRAW_TIP_TO_SELECT_LINK, paramTip);
	}*/
	
	public void ringToOriginalColor(){
		/** Turn color back to NONE, in case 3 colors back to GRAY.**/			 
		if (SwifteeApplication.getSingleFingerSteps()== 3){
			setRingcolor(SwifteeApplication.PAINT_GRAY);				
		} else if (SwifteeApplication.getSingleFingerSteps()== 2){		
			setRingcolor(SwifteeApplication.PAINT_BLUE);
		}
	}	
	
	/**
	 * Draw nothing on the screen erase what was there.
	 */
	public void drawNothing(){
		setDrawStyle(SwifteeApplication.DRAW_NONE, null);
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
	
	public void setDrawStyle(int type, Object[] param){			
	 	
	 	rV.scrollX = cW.getScrollX();
	 	rV.scrollY = cW.getScrollY();
	 	
		switch (type) {
    	 		
	    	case SwifteeApplication.DRAW_RING:	    
	    		
	    		re = (Rect) param[0];    		
	    		c = (int[]) param[1];
	    		r = c[0];
	    		g = c[1];
	    		b = c[2];	
	    		TYPE = (Integer) param[2];
	    		if (TYPE==WebHitTestResult.TEXT_TYPE){
	    			rV.setRingWidth(1);
	    		} else if (TYPE==WebHitTestResult.EDIT_TEXT_TYPE) {
	    			rV.setRingWidth(3);
	    		}
	    		rV.fillColor = Color.rgb(r, g, b);						    		
	    		rV.ringArc = 10;
	    		rV.ringRect = re;
	    		rV.setDrawType(SwifteeApplication.DRAW_RING);	  		    		
	    		break;
	    		
	    	case SwifteeApplication.DRAW_TAB: 		
	    		
	    		re = (Rect) param[0];		
	    		c = (int[]) param[1];
	    		r = c[0];
	    		g = c[1];
	    		b = c[2];	    		
	    		String t = (String) param[2];				
	    		rV.text = t;    				
	    		rV.tabRect = re;	    		
	    		rV.fillColor = Color.rgb(r, g, b);		
	    		rV.ringArc = 5;
	    		rV.ringRect = re;
	    		l = (Integer) param[3];
	    		setRingcolor(l);
	    		rV.setDrawType(SwifteeApplication.DRAW_TAB);
	    		break;
	    		
	    	case SwifteeApplication.DRAW_RING_AND_TAB:  
	    		
	    		re = (Rect) param[0];    		
	    		c = (int[]) param[1];
	    		r = c[0];
	    		g = c[1];
	    		b = c[2];		
	    		t = (String) param[2];
	    		TYPE = (Integer) param[3];
	    		if (TYPE==WebHitTestResult.TEXT_TYPE){
	    			rV.setRingWidth(1);
	    		} else if (TYPE==WebHitTestResult.EDIT_TEXT_TYPE) {
	    			rV.setRingWidth(3);
	    		}
	    		rV.tabRect = re;
	    		rV.ringRect = re;
	    		rV.fillColor = Color.rgb(r, g, b);						    		
	    		rV.ringArc = 10;
	    		rV.ringRect = re;	
	    		rV.text = t;	    		
	    		l = (Integer) param[3];
	    		setRingcolor(l);
	    		rV.setDrawType(SwifteeApplication.DRAW_RING_AND_TAB);
	    		break;
	    		
	    	case SwifteeApplication.DRAW_NONE:	    		
	    		rV.setDrawType(SwifteeApplication.DRAW_NONE);
	    		break;		    
    	}    	
    }		
	
	 
 	/**
	 * Sets the link ring color to blue, green and red
	 * @param color
	 */
	public void setRingcolor(int colorId){		
		
		switch (colorId){
		
			case SwifteeApplication.PAINT_GRAY: //gray	
				
				cW.invalidate();
				cW.setCursorOuterColors(0xff6E6E6E, 0xff6E6E6E, 0xff6E6E6E, 0xff6E6E6E);
				cW.setCursorInnerColors(0xffD4D4D4, 0xffD4D4D4, 0xffD4D4D4, 0xffD4D4D4);
				cW.invalidate();
				break;
				
			case SwifteeApplication.PAINT_RED: //red	
				
				cW.invalidate();
				cW.setCursorOuterColors(0xffFF6A4D, 0xffFF6A4D, 0xffFF6A4D, 0xffFF6A4D);
				cW.setCursorInnerColors(0xffFFCEC4, 0xffFFCEC4, 0xffFFCEC4, 0xffFFCEC4);
				cW.invalidate();
				break;
				
			case SwifteeApplication.PAINT_BLUE: //blue	
				
				cW.invalidate();
				cW.setSelectionColor(0xAAb4d5fe);
				cW.setSearchHighlightColor(0xAAb4d5fe);	
				cW.setCursorOuterColors(0xff0072FF, 0xff0072FF, 0xff0072FF, 0xff0072FF);
				cW.setCursorInnerColors(0xffA3CCFF, 0xffA3CCFF, 0xffA3CCFF, 0xffA3CCFF);
				cW.invalidate();
				break;
				
			case SwifteeApplication.PAINT_GREEN: //green	
				
				cW.invalidate();
				cW.setSelectionColor(0xAAD5B0);
				cW.setSearchHighlightColor(0xAAD5AD);	
				cW.setCursorOuterColors(0xff06A800, 0xff06A800, 0xff06A800, 0xff06A800);
				cW.setCursorInnerColors(0xffA9FFA6, 0xffA9FFA6, 0xffA9FFA6, 0xffA9FFA6);
				cW.invalidate();
				break;
				
			case SwifteeApplication.PAINT_YELLOW: //yeloww
				
				cW.invalidate();
				cW.setCursorOuterColors(0xffF5CD31, 0xffF5CD31, 0xffF5CD31, 0xffF5CD31);
				cW.setCursorInnerColors(0xffFFEFAD, 0xffFFEFAD, 0xffFFEFAD, 0xffFFEFAD);
				cW.invalidate();
				break;
				
			case SwifteeApplication.PAINT_VIOLET: //violet
				
				cW.invalidate();
				cW.setCursorOuterColors(0xffDF2BF0, 0xffDF2BF0, 0xffDF2BF0, 0xffDF2BF0);
				cW.setCursorInnerColors(0xffEAADF0, 0xffEAADF0, 0xffEAADF0, 0xffEAADF0);
				cW.invalidate();
				break;
		}
	};	
	
}
