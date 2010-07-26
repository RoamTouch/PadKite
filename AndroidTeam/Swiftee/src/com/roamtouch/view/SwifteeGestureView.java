package com.roamtouch.view;

import java.util.ArrayList;
import android.content.Context;
import android.gesture.GestureOverlayView;
import android.gesture.GesturePoint;
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

	Paint paint;
	Path path;
	Handler handler;
	Runnable runnable,runnable2;
	ArrayList<GesturePoint> list;
	int l;
	int count=1;
	String s;
	boolean isfinished=true; 
	public SwifteeGestureView(final Context context, AttributeSet attrs) {
		super(context, attrs);
		paint=new Paint();
		path=new Path();
		
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(10);
		paint.setStyle(Style.STROKE);
		paint.setStrokeCap(Cap.ROUND);
		handler=new Handler();
		runnable =new Runnable(){
			
			public void run() {
				 float x=list.get(count).x;
				 float y=list.get(count).y;
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
					handler.postDelayed(this ,100);
				}
				else{
					Toast.makeText(context, "Gesture "+s+" done" , Toast.LENGTH_SHORT).show();
					handler.postDelayed(runnable2 ,1000);
				}
			
			}
		};
		runnable2=new Runnable(){

			public void run() {
				path.reset();
				invalidate();
				isfinished=true;
			}
			
		};
	}
	public void onDraw(Canvas canvas){
		canvas.drawPath(path, paint);
		
	}
	 private float mX, mY;
     private static final float TOUCH_TOLERANCE = 4;

	public void drawGesture(String s){
		if(isfinished){
		isfinished=false;
		this.s=s;
		
		
		path.reset();
		l=list.size();
		count=0;
		path.moveTo(list.get(0).x, list.get(0).y);
        mX = list.get(0).x;
        mY = list.get(0).y;
        
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

}
