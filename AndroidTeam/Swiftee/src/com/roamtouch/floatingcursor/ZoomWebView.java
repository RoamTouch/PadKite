package com.roamtouch.floatingcursor;



import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ZoomWebView extends View{

		//Centre of the circular menu
		private int a,b;
	
		private float mLastMotionY,mLastMotionX, mLastMotionAngle=-999;
		private float mAngleChange;
		private FloatingCursor floatingCursor;
		private Paint paint;
		private int fcRadius;
		
		public ZoomWebView(Context context) {
			super(context);
			paint = new Paint();
			paint.setColor(Color.argb(100, 125, 125, 125));
		}
		
		public void setFCRadius(int r){
			fcRadius = r;
		}
		public void setFloatingCursor(FloatingCursor floatingCursor){
			this.floatingCursor = floatingCursor;
		}
	
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		   final int widthSpecSize =  MeasureSpec.getSize(widthMeasureSpec);
        
		   a = widthSpecSize /2;
        
		   Log.i("widthSpecSize:", ""+widthSpecSize);        

		   final int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);
        
		   b = heightSpecSize /2;
        
		   Log.i("heightSpecSize:", ""+heightSpecSize);        

		   setMeasuredDimension(widthSpecSize, heightSpecSize);
	   }
		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			 final int action = event.getAction();
		     final float x = event.getX();
		     final float y = event.getY();

			
			if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
	           
	            return false;
	        }
			
			switch(action){
			
			case MotionEvent.ACTION_DOWN:{			
				    mLastMotionAngle = -999;
				    mLastMotionX =0;
				    mLastMotionY =0;
				    Log.d("inside mLastMotionAngle touch Down"," resetting position!!----------------------");
	            break;
			}
			case MotionEvent.ACTION_OUTSIDE:{
				    mLastMotionAngle = -999;
				    mLastMotionX =0;
				    mLastMotionY =0;
				    Log.d("inside mLastMotionAngle touch outside"," resetting position!!----------------------");
			}
			case MotionEvent.ACTION_MOVE:{
		
	            float currentAngle, angleDiff;
	            if(mLastMotionAngle == -999) {
	            	mLastMotionAngle = (float)computeAngle1(a, b, x, y);
	    			mLastMotionX = x;
	                mLastMotionY = y;
	                Log.d("inside mLastMotionAngle,"," reseted initial position!!----------------------");
	                currentAngle = mLastMotionAngle;
	            }
	            currentAngle = (float)computeAngle1(a, b, x, y);
	            //Log.d("Action move---------------Current angle",""+currentAngle);
	            angleDiff = currentAngle-mLastMotionAngle;
	            //Log.d("Angle diff---------------",""+angleDiff);
	            
	            // Special case to handle move from fourth quadrant to first quadrant
	            if(Math.abs(angleDiff) > 200) {
	            	if(currentAngle < mLastMotionAngle) {
	            		if((mLastMotionAngle > 270) && (currentAngle < 90)) {
	            			angleDiff = currentAngle + (360 - mLastMotionAngle);
	            			Log.d("Angle diff adjusted---------------",""+angleDiff);
	            		}
	            	}
	           		else {
	           			if((mLastMotionAngle < 90) && (currentAngle > 270)) {
	            				angleDiff = mLastMotionAngle + (360 - currentAngle);
	            				angleDiff = -1*angleDiff;
	            				Log.d("Angle diff adjusted---------------",""+angleDiff);
	           			}
	           		}
	            }
	            		
	           //if (yDiff > 3 || xDiff > 3) {
	            
	            if(Math.abs(angleDiff) > 25 && Math.abs(angleDiff) < 250)  {
	            
	            mAngleChange = angleDiff;
	            if(mAngleChange<0)
	            	floatingCursor.circularZoomIn();
	            	//direction = -1;
	            else
	            	floatingCursor.circularZoomOut();
				
				mLastMotionAngle = currentAngle;
				mLastMotionX = x;
	            mLastMotionY = y;
	            	          	           
				}
	            break;
			}
			case MotionEvent.ACTION_UP:
				
			    mLastMotionAngle = -999;
			    mLastMotionX =0;
			    mLastMotionY =0;
			    Log.d("inside mLastMotionAngle,"," resetting position!!----------------------");
			}
					
			return true;
		}
		
		public double computeAngle1(float centerX, float centerY, float currentX, float currentY) {
			double angle;
			double radius;

			radius = Math.sqrt(Math.pow((centerX-currentX), 2)+Math.pow((centerY-currentY), 2));
			double d1 = (currentX - centerX)/radius;

			angle = Math.acos(d1); 
			if(currentY<centerY)
				angle = 180 + 180 - Math.toDegrees(angle);
			else
				angle = Math.toDegrees(angle);
			return angle;
		}
		
		@Override
		 protected void onDraw(Canvas canvas) {
		     super.onDraw(canvas);
		     canvas.drawCircle(a, b, fcRadius, paint);
		 }
		
}
