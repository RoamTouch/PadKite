package com.roamtouch.menu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

/**
 * Circular layout containing menu items and performing circular wheel animation
 * 
 */
public class CircularLayout extends ViewGroup implements OnTouchListener{

	   private boolean mIsBeingDragged = false;
	   private float mLastMotionY,mLastMotionX;
	   private int mTouchSlop;
	   private int mMinimumVelocity;
	   private int mMaximumVelocity;
	   
	   private ImageView mImage; 
	    private Bitmap buffer = null;
		private Canvas bufferCanvas = new Canvas();
	   
	   private VelocityTracker mVelocityTracker;
	   //Centre of the circular menu
	   private int a,b;
	   
    
	   //radius
	   private int outR;
	   
	   private int inR=110;
	   
	   private float mAngleChange;
	   
	   public CircularLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnTouchListener(this);
		 final ViewConfiguration configuration = ViewConfiguration.get(context);
	        mTouchSlop = 10;
	        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
	        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	        Log.d("MinVelocity,MaxVelocity",mMinimumVelocity +","+mMaximumVelocity);
	       // addImageBuffer();
	   }

	   @Override
	   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		   final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		   final int widthSpecSize =  MeasureSpec.getSize(widthMeasureSpec);
        
		   a = widthSpecSize /2;
        
		   getChildAt(0).getWidth();

		   Log.i("widthSpecSize:", ""+widthSpecSize);        

		   final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		   final int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);
        
		   b = heightSpecSize /2;
        
		   Log.i("heightSpecSize:", ""+heightSpecSize);        

		   // for now we take the width of the view as the radius 
		   outR = widthSpecSize/2;
        
		   if (widthSpecMode == MeasureSpec.UNSPECIFIED || heightSpecMode == MeasureSpec.UNSPECIFIED) {
			   throw new RuntimeException("CircularLayout cannot have UNSPECIFIED dimensions");
		   }
        
		   setMeasuredDimension(widthSpecSize, heightSpecSize);
	   }
	   @Override
	   protected void onLayout(boolean changed, int left, int top, int right, int bot) {

		   final int count = getChildCount();
		   double t = 0;
		   int x = 0, y = 0;

		   t = (360 / (count-1));
        
		   
/*		   MenuButton child = (MenuButton)getChildAt(0);
		   if (child.getVisibility() != GONE) {         
			   child.layout(a-40, b-40, a+40, b+40);
		   }
*/		   
		   for (int i = 0; i < count-2; i++) {
			   MenuButton child = (MenuButton)getChildAt(i);
			   if (child.getVisibility() != GONE) {            
            	// Calc coordinates around the circle at the center of cart. system
            	double angle = i*t;
            	angle=angle-40;
            	child.setAngle(angle);
            	child.calculateCenter(a,b,inR,angle);
            	
            	Log.i("Circle before if angle,x,y" , "("+ x +","+ y +")angle"+angle);
            
            	
            	//  final int measuredW = child.getMeasuredWidth();
            	//  final int measuredH = child.getMeasuredHeight();

            	Log.i("Circle x,y" , "("+ x +","+ y +")");
 
                final int childLeft = child.getCenterX() - 40;
                final int childTop = child.getCenterY() - 40;
                final int lb = child.getCenterX() + 40;
                final int rb = child.getCenterY() + 40;
               
                child.layout(childLeft, childTop, lb, rb);

            }
        }	
		   

		   ImageView cone = (ImageView)getChildAt(count-2);
		   if (cone.getVisibility() != GONE) {         
			   cone.layout(0, b-outR, 2*a, b+outR);
			   cone.setClickable(false);
		   }
		   
		   MenuButton child = (MenuButton)getChildAt(count-1);
		   if (child.getVisibility() != GONE) {           
			   child.layout(a-40, b-40, a+40, b+40);
		   }
	}

	   protected void addImageBuffer()
		{
		   	mImage=new ImageView(getContext());
			buffer=Bitmap.createBitmap(2*a, 2*b,Bitmap.Config.ARGB_8888);
			bufferCanvas.setBitmap(buffer);
			bufferCanvas.drawArc(new RectF(0,b-outR,2*a,b+outR), -120, 60, true, new Paint());	        
			mImage.setImageBitmap(buffer);
			this.addView(mImage);
		}
	@Override
	protected void dispatchDraw(Canvas canvas) {

		Paint p=new Paint();
		p.setColor(Color.GRAY);
		canvas.drawCircle(a, b, outR, p);

		//canvas.drawArc(new RectF(0,b-outR,2*a,b+outR), -120, 60, true, new Paint());
		super.dispatchDraw(canvas);
	}

	public boolean onTouch(View v, MotionEvent event) {
		
		 final int action = event.getAction();
	     final float x = event.getX();
	     final float y = event.getY();

		if(y<80 || y>400)
			return false;
		if(Math.pow(x-a, 2)+Math.pow(y-b, 2)-Math.pow(inR-40, 2)<0 && Math.pow(x-a, 2)+Math.pow(y-b, 2)-Math.pow(outR, 2)>0)
			return false;
		
		if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            // Don't handle edge touches immediately -- they may actually belong to one of our
            // descendants.
            return false;
        }
		
		
		 if (mVelocityTracker == null) {
	            mVelocityTracker = VelocityTracker.obtain();
	        }
	       
		switch(action){
		
		case MotionEvent.ACTION_DOWN:{
			
            break;
		}
		case MotionEvent.ACTION_MOVE:{
			
			final int yDiff = (int) Math.abs(y - mLastMotionY);
            final int xDiff = (int) Math.abs(x - mLastMotionX);
            
           if (yDiff > 3 || xDiff > 3) {
            
            mVelocityTracker.addMovement(event);
     	   // checkForMotionDir(x,y);
            computeAngle(x,y);
			int count=getChildCount();
        	
        	//Log.i("x,y" , "("+ x +","+ y +")");
			for (int i = 0; i < count-2; i++) {
	            MenuButton child = (MenuButton)getChildAt(i);
	            if (child.getVisibility() != GONE) {            
	            	// Calc coordinates around the circle at the center of cart. system
	            	//child.setClickable(false);
	            
	            	double angle=child.getAngle();
	            	angle+=mAngleChange;
	            	
	            	child.setAngle(angle);
	            	
	            	child.calculateCenter(a,b,inR,angle);
	          
	                final int childLeft = child.getCenterX() - 40;
	                final int childTop = child.getCenterY() - 40;
	                final int lb = child.getCenterX() + 40;
	                final int rb = child.getCenterY() + 40;
	               
	                child.layout(childLeft, childTop, lb, rb);

	                
	            }
	        }	
			}
			mLastMotionX = x;
            mLastMotionY = y;
		}
		case MotionEvent.ACTION_UP:
//		    Log.i("inside onTouchEvent ACTION_UP mLastMotionX,mLastMotionY" , "("+ mLastMotionX +","+ mLastMotionY +")");
		    mIsBeingDragged=false;
		}
		
		
		return true;
	}
	   @Override
	    public boolean onInterceptTouchEvent(MotionEvent ev) {
	      
	        final int action = ev.getAction();
	        if (mIsBeingDragged)  {
	            return true;
	        }
	        final float x = ev.getX();
	        final float y = ev.getY();

	        switch (action) {
            case MotionEvent.ACTION_MOVE:
              
//            	 Log.i("inside ACTION_MOVE mLastMotionX,mLastMotionY" , "("+ mLastMotionX +","+ mLastMotionY +")");
                final int yDiff = (int) Math.abs(y - mLastMotionY);
                final int xDiff = (int) Math.abs(x - mLastMotionX);
                
                if (yDiff > mTouchSlop || xDiff > mTouchSlop) {
                    mIsBeingDragged = true;
                }
                break;

            case MotionEvent.ACTION_DOWN:
                /* Remember location of down touch */
            	mLastMotionX = x;
                mLastMotionY = y;

                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                /* Release the drag */
                mIsBeingDragged = false;
//                Log.i("inside ACTION_UP mLastMotionX,mLastMotionY" , "("+ mLastMotionX +","+ mLastMotionY +")");
                break;
        }

	        /*
	        * The only time we want to intercept motion events is if we are in the
	        * drag mode.
	        */
	        return false;
	    }
	   
/*	   public void checkForMotionDir(float x,float y){
		  
		  
	        mVelocityTracker.computeCurrentVelocity(1000, 360);
	        float xVelocity=mVelocityTracker.getXVelocity();
	        float yVelocity=mVelocityTracker.getYVelocity();
	        float temp=5;
	        Log.d("Velocity x,y","("+xVelocity+","+yVelocity+")");
	        
	        
	        
		   final float diffX = x - mLastMotionX;
           final float diffY = y - mLastMotionY;
	        //Check if menu rotates Clockwise or Anticlockwise
	        		if(x>160 && y<160){
	        			//inside first quadrant;
	        			if(xVelocity>0 && yVelocity >0){
	        				mAngleChange=temp;
	        			}
	        			else
	        				mAngleChange=-temp;
	        		}
	        		else if(x>160 && y>160){
	        			//inside second quadrant;	 
	        			if(xVelocity<0 && yVelocity >0){
	        				mAngleChange=temp;
	        			}
	        			else
	        				mAngleChange=-temp;
	        		}
	        		else if(x<160 && y>160){
	        			//inside third quadrant;
	        			if(xVelocity<0 && yVelocity <0){
	        				mAngleChange=temp;
	        			}
	        			else
	        				mAngleChange=-temp;
	        		}
	        		else {
	        			//inside fourth quadrant;	  
	        			if(xVelocity>0 && yVelocity <0){
	        				mAngleChange=temp;
	        			}
	        			else
	        				mAngleChange=-temp;
	        		}
	        		
	        		
	   }
*/	   
	   public void computeAngle(float x,float y){
		   
		  // float r=distanceFromCenter(x, y);
		   double d1=(mLastMotionX-a)/inR;
		   double d2=(x-a)/inR;
		   
		   if(d1>1 || d1<-1 || d2>1 || d2<-1)
			   return;
		   
		   float lastAngle=(float) Math.toDegrees(Math.acos(d1));
		   float currAngle=(float) Math.toDegrees(Math.acos(d2));
		   Log.d("CurrentAngle,LastAngle", currAngle+","+lastAngle);
		   if(y>160)
			   mAngleChange=currAngle-lastAngle;
		   else
			   mAngleChange=lastAngle-currAngle;
		   //Log.d("ComputeAngle", ""+mAngleChange);
	   }
	
	   public float distanceFromCenter(float x,float y){
			//Distance Formula d=sqrt( sqr(x2-x1) + sqr(y2-y1))
			float d=(float) Math.sqrt( Math.pow((a-x), 2) +Math.pow((b-y),2));
			return d;
		}
}
