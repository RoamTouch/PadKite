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
public class CircularProgressBar extends View implements Runnable{

		private float x = 0;
		private float y = 0;
		private final Paint[] mPaints = new Paint[3];
		private int r;
//		private int mStartPoint=0,mEndPoint=6;
		private Handler mHandler;
		private int mColorValue=220;
		private int mProgress;
		private boolean isEnabled=false;
		public CircularProgressBar(Context context,int r) {
			super(context);
			mPaints[0] = new Paint();
			mPaints[0].setAntiAlias(true);
			mPaints[0].setColor(0xFF0000); // 0xFFFF0000
			mPaints[0].setStrokeWidth(1);
			mPaints[0].setAntiAlias(true);
			mPaints[0].setStyle(Paint.Style.STROKE);
			
			mPaints[1]=new Paint();
			mPaints[1].setColor(Color.WHITE);

			mPaints[2]=new Paint();
			mPaints[2].setColor(Color.LTGRAY);

			this.r=r;
			mHandler =new Handler();
		}
	
		protected void setPosition(float x, float y)
		{
			this.x = x;
			this.y = y;
			invalidate();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			float startAngle,sweepAngle;
			for(int i=0;i<20;i++){
				startAngle=i*18-90;
				sweepAngle=16;
				mPaints[2].setColor(Color.rgb(230, 230, 230));
				
/*				if(mStartPoint>=9){
					mEndPoint=mStartPoint-9;
					if(i<=mEndPoint || i>=mStartPoint)
						mPaints[2].setColor(Color.rgb(mColorValue-i*10, mColorValue-i*10, mColorValue-i*10));
				}
				else{
					if(i>=mStartPoint && i<=mEndPoint)
						mPaints[2].setColor(Color.rgb(mColorValue-i*10, mColorValue-i*10, mColorValue-i*10));

				}
				
	*/			if(i<=mProgress-1)
						mPaints[2].setColor(Color.rgb(mColorValue-i*10, mColorValue-i*10, mColorValue-i*10));

				canvas.drawArc(new RectF(x-r,y-r,x+r,y+r), startAngle, sweepAngle, true, mPaints[2]);
			}

			canvas.drawCircle(x, y, r-15, mPaints[1]);
			canvas.drawCircle(x, y, r, mPaints[0]);
			
			
		}

		public void setProgress(int progress){
			mProgress=(int)progress/5;
		}
		public void enable(){
			isEnabled=true;
			this.setVisibility(VISIBLE);
			mHandler.post(this);
		}
		public void disable(){
			isEnabled=false;
			this.setVisibility(INVISIBLE);
		}
		public void run() {
			invalidate();
			if(isEnabled){
				mHandler.postDelayed(this, 200);
			}
			

		}

	
}
