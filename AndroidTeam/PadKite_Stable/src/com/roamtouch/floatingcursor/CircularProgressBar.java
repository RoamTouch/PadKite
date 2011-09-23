package com.roamtouch.floatingcursor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.view.View;

/* This is NO LONGER USED */

/**
 *   Circular progress bar is visible when a web view is loading a new URL 
 *   WebChromeClient OnProgressChanged gives page loading progress  
 *
 */
public class CircularProgressBar extends View {

		private int r;
		private float x = 0;
		private float y = 0;
		
		private Paint mPaints;

	    private boolean[] mUseCenters;
	    private RectF[] mOvals;
	    private RectF mBigOval;
	    
	    private float mStart;
	    private float mSweep;
	    private int mColor;
	    private int mBigIndex;

	    private int mProgress;
	    private int mColorValue=220;
	    
	    public boolean isEnabled=false;
	    
	    Handler handler; 
		
		
		public CircularProgressBar(Context context,int r) {
			super(context);
			
		  	mPaints = new Paint();
			mUseCenters = new boolean[4];
			mOvals = new RectF[4];
			
			mPaints = new Paint(mPaints);
			mPaints.setStyle(Paint.Style.STROKE);
			mPaints.setStrokeWidth(8);
			mPaints.setColor(Color.rgb(255, 220, 21));
			mPaints.setAntiAlias(true);
			mPaints.setStrokeCap(Paint.Cap.BUTT);
			mPaints.setColor(Color.LTGRAY);		
			
			this.r=r;
		
		}		
		

		@Override
		protected void onDraw(Canvas canvas) {			
			super.onDraw(canvas);	
       	
			if (isEnabled){
				canvas.drawColor(Color.alpha(Color.CYAN));				
				mBigOval = new RectF(x-r,y-r,x+r,y+r);
				mPaints.setColor(Color.rgb(mColorValue-mColor*6, mColorValue-mColor*6, mColorValue-mColor*6));
				drawArcs(canvas, mBigOval, mUseCenters[2], mPaints);       					
	        	invalidate();	        	
			}  
		}
		
		private void drawArcs(Canvas canvas, RectF oval, boolean useCenter, Paint paint) {
			canvas.drawArc(oval, -90, mSweep, useCenter, paint);
		} 

		public void setProgress(int progress){			
			
			mProgress = progress;
			mSweep = (mProgress * 360) / 100;
			mColor = (mProgress * 20) / 100;
			
		}
		
		public void enable(){
			isEnabled = true;
			this.setVisibility(VISIBLE);			
		}
		
		
		public void disable(){			
			mProgress=0;	
        	mSweep = 0;
						
			
			//Keep showing progress for a moment.
			handler = new Handler();		
			handler.postDelayed(new Runnable() { 
	            public void run() { 
	            	isEnabled=false;
	            	//setVisibility(INVISIBLE);
	            } 
			}, 600); 	
		}		

		protected void setPosition(float x, float y) {
			this.x = x;
			this.y = y;
			invalidate();
		}


	
}
