 package com.roamtouch.visuals;

import android.webkit.WebView;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.utils.ScreenLocation;
import com.roamtouch.visuals.TipView;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


public class TipController extends FrameLayout implements Runnable {
	
	private TipView tV; 	
	private BrowserActivity mP;
	private FloatingCursor fC;
	private WebView wV;
	
	 private TipTimerTask task;
	 public Timer timer;
	 
	 public int time;
	 
	 private Rect rect;
	 
	 private int[] tipColor; 
	 
	public TipController(Context context, AttributeSet attrs) {
		super(context, attrs);	
		init(context);
	}			
	
	private void init(Context context) {
		tV = new TipView(getContext());		
		addView(tV);        
		tV.setGrandParent(mP, fC);		
	}
	
	public void setWebView(WebView w){
		wV = w;
	}
	
	
	public void setParent(BrowserActivity p, FloatingCursor f, WebView w) {		
		mP = p;		
		fC = f;
		wV = w;
	}
	
	int[] cord;
	int x;
	int y;
	int vertical;
	int horizontal;		
	private Rect xRect;
	private String[] tText;
	private Vector vText = new Vector();
	private String buttonText;
	
	public void setTipComment(Object[] param, int isFor){		
		
		
		 Throwable t = new Throwable(); StackTraceElement[] elements =
		 t.getStackTrace(); String calleeMethod =
		 elements[0].getMethodName(); String callerMethodName =
		 elements[1].getMethodName(); String callerClassName =
		 elements[1].getClassName(); Log.v("call",
		 "callerMethodName: "+callerMethodName+
		 " callerClassName: "+callerClassName );
		 
				
		rect = (Rect) param[0];		
		
		if (isFor==SwifteeApplication.IS_FOR_CIRCULAR_MENU_TIPS || isFor==SwifteeApplication.IS_FOR_WEB_TIPS){
			cord = (int[]) param[2];
			tText  = (String[]) param[1];
		} else if (isFor==SwifteeApplication.IS_FOR_CONTENT_OBJECT){
			vText = (Vector) param[1];
			tipColor = (int[]) param[2];	
		}	
		
		switch (isFor) {
		
			case SwifteeApplication.IS_FOR_WEB_TIPS:				
				 
				/*Vector cuagrant34 = new Vector();
				
				x = cord[0];
				y = cord[1];
					
				tV.xPoint = x;
				tV.yPoint = y;
				
				cuagrant34 = mP.getCuadrant34(x, y);		
				//vertical = (Integer) cuagrant34.get(0);
				horizontal = (Integer) cuagrant34.get(1);	*/
				
				vertical = ScreenLocation.getVerticalLocation();
				horizontal = ScreenLocation.getHorizontalLocation();	
				
				//xRect =  (Rect) cuagrant34.get(2);
				
				xRect = ScreenLocation.getRectCuardrant();
				
				//tV.setDraw((Integer) cuagrant34.get(3));
				
				tV.setDraw(ScreenLocation.getTipType());				
				
				if (horizontal==1){
					tV.yPos = rect.bottom + 90;				
				} else {
					tV.yPos = rect.top - 90;	
				}
				
				tV.xPos = xRect.left;		
				tV.width = xRect.right;
				
				tV.setIsFor(SwifteeApplication.IS_FOR_WEB_TIPS);
				
				break;
				
			case SwifteeApplication.IS_FOR_CIRCULAR_MENU_TIPS:
				
				
				time = (Integer) param[4];					
				
				//Fix SCROLLING
				int scrollY = wV.getScrollY();
				
				rect.top = rect.top - scrollY;
				
				x = cord[0];
				y = cord[1];
					
				tV.xPoint = x;
				tV.yPoint = y;
				
				
				tV.yPos = rect.top - 70;			
				tV.xPos = rect.left;
				tV.xCenter = x;
				tV.width = rect.right;
				tV.setDraw(SwifteeApplication.IS_FOR_CIRCULAR_MENU_TIPS);	
				
				
				tV.setTipFor(SwifteeApplication.SET_TIP_TO_CENTER_DOWN);
				
				tV.setIsFor(SwifteeApplication.IS_FOR_CIRCULAR_MENU_TIPS);
				
				break;
				
			case SwifteeApplication.IS_FOR_CONTENT_OBJECT:					
				
				/*cuagrant34 = mP.getCuadrant34(x, y);		
				vertical = (Integer) cuagrant34.get(0);
				horizontal = (Integer) cuagrant34.get(1);		
				xRect =  (Rect) cuagrant34.get(2);*/					
				/*if(horizontal==1){
					tV.setObjOrientation(SwifteeApplication.TIP_CONTENT_ORIENT_UP);
				} else {
					tV.setObjOrientation(SwifteeApplication.TIP_CONTENT_ORIENT_BOTTOM);
				}*/
				
				int vertical = (Integer) param[3];		
				
				tV.setRect(rect);				
				tV.setTipColor(tipColor);	
				
				vText = (Vector) param[1];
				tText = (String[]) vText.get(0);			 
				tV.setTipText(tText);
				
				String tB = (String) vText.get(1);
				tV.setTipButtonText(tB);
				
				tV.setVertical(vertical);
				
				tV.setDraw(SwifteeApplication.IS_FOR_CONTENT_OBJECT);
				
				break;
			
			case SwifteeApplication.DRAW_NOTHING:
				tV.setDraw(SwifteeApplication.DRAW_NOTHING);			
				break;
			
		}		
		
		tV.setTextLines(tText.length);
		tV.setTipText(tText);	
		
		if (isFor!=SwifteeApplication.IS_FOR_CIRCULAR_MENU_TIPS) {
			task = new TipTimerTask();
			timer = new Timer();  
			timer.schedule(task,time);
		}
		 		
	}   

	
	private class TipTimerTask extends TimerTask {	  
		  public final void run(){
			  timer.cancel();
			  timer.purge();
			  task.cancel();			 
			  //tV.setDraw(SwifteeApplication.DRAW_NOTHING);			  
		  }
	}
	
	/**Draw Nothing**/
	public void drawNothing(){
		tV.setDraw(SwifteeApplication.DRAW_NOTHING);
	}	
	
	public void invalidateTips(){
		tV.postInvalidate();
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}	

	//Paint Row Over
	public void setButtonOver(int id){		
		tV.setButtonOver(id);	
		invalidate();
	}

		
}
