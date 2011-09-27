 package com.roamtouch.visuals;

import roamtouch.webkit.WebView;

import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.visuals.TipView;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


public class TipController extends FrameLayout implements Runnable {
	
	private TipView tV; 	
	private BrowserActivity mP;
	
	 private TipTimerTask task;
	 public Timer timer;
	 
	 public int time;
	 
	public TipController(Context context, AttributeSet attrs) {
		super(context, attrs);	
		init(context);
	}			
	
	private void init(Context context) {
		tV = new TipView(getContext());		
		addView(tV);        
	}
	
	public void setParent(BrowserActivity p) {		
		mP = p;		
	}
	
	public void setTipComment(Object[] param, int isFor){		
		
		Rect rectTip  	= (Rect) param[0];		
		String[] tText  = (String[]) param[1];
		time  			= (Integer) param[2];
		int[] cord 	= (int[]) param[3];
		
		int x = cord[0];
		int y = cord[1];
			
		tV.xPoint = x;
		tV.yPoint = y;
		
		int tipType = 0;
		
		switch (isFor) {
		
			case SwifteeApplication.IS_FOR_WEB_TIPS:
				
				 
				Vector cuagrant34 = new Vector();
				
				cuagrant34 = mP.getCuadrant34(x, y);		
				int vertical = (Integer) cuagrant34.get(0);
				int horizontal = (Integer) cuagrant34.get(1);		
				Rect xRect =  (Rect) cuagrant34.get(2);	
				tipType = (Integer) cuagrant34.get(3);
				
				if (horizontal==1){
					tV.yPos = rectTip.bottom + 120;				
				} else {
					tV.yPos = rectTip.top - 120;	
				}
				
				tV.xPos = xRect.left;		
				tV.width = xRect.right;
				
				break;
				
			case SwifteeApplication.IS_FOR_CIRCULAR_MENU_TIPS:
				
				tV.yPos = rectTip.top - 70;			
				tV.xPos = rectTip.left - 15;
				tV.width = rectTip.right;
				tipType = SwifteeApplication.SET_TIP_TO_CENTER_DOWN;
				
				break;
			
		}		
		
		tV.setTextLines(tText.length);
		tV.setTipText(tText);		
 				
		tV.setDraw(tipType);    			
		task = new TipTimerTask();
		timer = new Timer();  
		timer.schedule(task,time); 		
	}   

	
	private class TipTimerTask extends TimerTask {	  
		  public final void run(){	
			  //DO
			  timer.cancel();
			  timer.purge();
			  task.cancel();
			  tV.setDraw(SwifteeApplication.DRAW_NONE);
			  //tV.postInvalidate();			  
		  }
	}
	
	public void invalidateTips(){
		tV.postInvalidate();
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}
		
}
