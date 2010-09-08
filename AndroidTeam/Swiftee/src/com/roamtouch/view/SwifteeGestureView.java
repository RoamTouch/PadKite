package com.roamtouch.view;

import java.util.ArrayList;
import android.content.Context;
import com.roamtouch.gestures.Gesture;
import com.roamtouch.gestures.GestureOverlayView;
import com.roamtouch.gestures.GesturePoint;
import com.roamtouch.gestures.GestureStroke;
import com.roamtouch.swiftee.BrowserActivity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.Toast;

public class SwifteeGestureView extends GestureOverlayView{

	BrowserActivity mParent;
	Paint paint;
	Path path;
	Handler handler;
	Runnable runnable,runnable2;
	ArrayList<GesturePoint> gesturePoints;
	int l;
	int count=1;
	String s,action;
	boolean isfinished=true; 
	public SwifteeGestureView(final Context context, AttributeSet attrs) {
		super(context, attrs);
		paint=new Paint();
		path=new Path();
		
		paint.setAntiAlias(true);
		paint.setDither(true);

		// FIXME: Dynamic
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(20);
		paint.setStyle(Style.STROKE);
		paint.setStrokeCap(Cap.ROUND);
		handler=new Handler();
		runnable =new Runnable(){
			
			public void run() {
				 float x=gesturePoints.get(count).x;
				 float y=gesturePoints.get(count).y;
				 float dx = Math.abs(x - mX);
		         float dy = Math.abs(y - mY);
		         if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
		             path.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
		             mX = x;
		             mY = y;
		         }
				
				count++;
				invalidate();
				if(count<l){
					handler.postDelayed(this ,70);
				}
				else{
					//Toast.makeText(context, "Gesture "+s+" done" , Toast.LENGTH_SHORT).show();
					handler.postDelayed(runnable2 ,1000);
				}
			
			}
		};
		runnable2=new Runnable(){

			public void run() {
				path.reset();
				invalidate();
				isfinished=true;
				mParent.cursorGestures(action);
			}
			
		};
	}
	public void setParent(BrowserActivity parent){
		mParent = parent;
	}
	public void onDraw(Canvas canvas){
		canvas.drawPath(path, paint);
		//canvas.drawLine(100, 100, 266, 300, paint);
	}
	 private float mX, mY;
     private static final float TOUCH_TOLERANCE = 4;

	public void drawGesture(ArrayList<GesturePoint>  gesturePoints,String action){
		
		this.action = action;
		this.gesturePoints = gesturePoints;
		path.reset();
		l=this.gesturePoints.size();
		count=0;
		path.moveTo(this.gesturePoints.get(0).x, this.gesturePoints.get(0).y);
        mX = this.gesturePoints.get(0).x;
        mY = this.gesturePoints.get(0).y;
        
        try
		{
			handler.removeCallbacks(runnable);
			handler.postDelayed(runnable ,100);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
