package com.roamtouch.menu;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
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
	
	/**
	 * Calculate the radius for menu buttons and inner radius 
	 */
		private final float scale = getContext().getResources().getDisplayMetrics().density;
		private final float INNER_RADIUS_DIP = 110; // 64dip=10mm, 96dip=15mm, 192dip=30mm expressed in DIP
		private final int INNER_RADIUS = (int) (INNER_RADIUS_DIP * scale + 0.5f); //Converting to Pixel
		private final float BUTTON_RADIUS_DIP = 40; // 64dip=10mm, 96dip=15mm, 192dip=30mm expressed in DIP
		private final int BUTTON_RADIUS = (int) (BUTTON_RADIUS_DIP * scale + 0.5f); //Converting to Pixel

		private boolean mIsBeingDragged = false;
		private float mLastMotionY,mLastMotionX, mLastMotionAngle=-999;
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
	   
		private int inR=INNER_RADIUS;
	   
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

		   //t = (360 / (count-1));
		   //t = 360 / 7;
		   t=55;
        
		   
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
            	angle=angle-90 + 46;
            	child.setAngle(angle);
            	child.calculateCenter(a,b,inR,angle);
            	
            	Log.i("Circle before if angle,x,y" , "("+ x +","+ y +")angle"+angle);
            
            	
            	//  final int measuredW = child.getMeasuredWidth();
            	//  final int measuredH = child.getMeasuredHeight();

            	Log.i("Circle x,y" , "("+ x +","+ y +")");
 
                final int childLeft = child.getCenterX() - BUTTON_RADIUS;
                final int childTop = child.getCenterY() - BUTTON_RADIUS;
                final int lb = child.getCenterX() + BUTTON_RADIUS;
                final int rb = child.getCenterY() + BUTTON_RADIUS;
               
                if(child.shouldDraw()) {
                    child.layout(childLeft, childTop, lb, rb);                	
                }

            }
        }	
		   

		   ImageView cone = (ImageView)getChildAt(count-2);
		   if (cone.getVisibility() != GONE) {         
			   cone.layout(0, b-outR, 2*a, b+outR);
			   cone.setClickable(false);
		   }
		   
		   MenuButton child = (MenuButton)getChildAt(count-1);
		   if (child.getVisibility() != GONE) {           
			   child.layout(a-BUTTON_RADIUS, b-BUTTON_RADIUS, a+BUTTON_RADIUS, b+BUTTON_RADIUS);
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

	public boolean canScroll(float angleDiff,int childCount) {
		MenuButton first = (MenuButton)getChildAt(0);
		MenuButton last = (MenuButton)getChildAt(childCount-3);
		
		if((first.getAngle()+angleDiff)>4)
			return false;
		else if(last.getAngle()+angleDiff < 174)
			return false;
		return true;
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
			
//			final int yDiff = (int) Math.abs(y - mLastMotionY);
//            final int xDiff = (int) Math.abs(x - mLastMotionX);
            
            float currentAngle, angleDiff;
            
            if(mLastMotionAngle == -999) {
            	mLastMotionAngle = (float)computeAngle1(a, b, x, y);
    			mLastMotionX = x;
                mLastMotionY = y;
                
                currentAngle = mLastMotionAngle;
            }
            currentAngle = (float)computeAngle1(a, b, x, y);
            angleDiff = currentAngle-mLastMotionAngle;
           //if (yDiff > 3 || xDiff > 3) {
            if(Math.abs(angleDiff) > 2) {
            
            mVelocityTracker.addMovement(event);
     	    //checkForMotionDir(x,y);
            //computeAngle(x,y);
            mAngleChange = angleDiff;
			int count=getChildCount();
            if(!canScroll(angleDiff, count))
            	return true;
            //if(mLastMotionAngle>270 && currentAngle<90)
            //	mAngleChange = currentAngle;
            
                  
    	
        	//Log.i("x,y" , "("+ x +","+ y +")");
			for (int i = 0; i < count-2; i++) {
	            MenuButton child = (MenuButton)getChildAt(i);
	            
	            if (child.getVisibility() != GONE) {            
	            	//child.setClickable(false);
	            	double angle=child.getAngle();
	            	angle+=mAngleChange;
	            	child.setAngle(angle);
	            	child.calculateCenter(a,b,inR,angle);
	          
	                final int childLeft = child.getCenterX() - BUTTON_RADIUS;
	                final int childTop = child.getCenterY() - BUTTON_RADIUS;
	                final int lb = child.getCenterX() + BUTTON_RADIUS;
	                final int rb = child.getCenterY() + BUTTON_RADIUS;
	               
	                if(child.shouldDraw()) {
	                	child.setVisibility(View.VISIBLE);
	                	child.layout(childLeft, childTop, lb, rb);
	                }
	                else
	                	child.setVisibility(View.INVISIBLE);
	            }
	        }
			mLastMotionAngle = currentAngle;
			mLastMotionX = x;
            mLastMotionY = y;
            Log.d("x,y,angle: ", x+","+y+","+mLastMotionAngle);
			}
            break;
		}
		case MotionEvent.ACTION_UP:
			int countC = getChildCount();
			MenuButton first = (MenuButton)getChildAt(0);
			MenuButton last = (MenuButton)getChildAt(countC-3);
			
			if(first.getAngle()>-44) {
				float angleD = (float)(-44) - (float)first.getAngle();
				BounceBackAnimation bounceBack = new BounceBackAnimation(angleD,countC);
				bounceBack.start();
				// Animate back the buttons
				//return false;
			}
			else if(last.getAngle() < 224) {
				float angleD = (float)(224) - (float)last.getAngle();
				BounceBackAnimation bounceBack = new BounceBackAnimation(angleD,countC);
				bounceBack.start();
				// Animate back the buttons
				//return false;
			}
			//return true;
//		    Log.i("inside onTouchEvent ACTION_UP mLastMotionX,mLastMotionY" , "("+ mLastMotionX +","+ mLastMotionY +")");
		    mIsBeingDragged=false;
		    mLastMotionAngle = -999;
		    mLastMotionX =0;
		    mLastMotionY =0;
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
	   
	   
	   public void computeAngle(float x,float y){
		   
		   double prevRadius, currentRadius;
		   prevRadius = Math.sqrt((mLastMotionX - a)*(mLastMotionX - a) + (mLastMotionY - b)*(mLastMotionY - b));
		   currentRadius = Math.sqrt((x-a)*(x-a)+(y-b)+(y-b));
		   
		   Log.d("prev rad, curr radius", prevRadius+","+currentRadius);
		   
		   //prevRadius = currentRadius = inR;
		  // float r=distanceFromCenter(x, y);
		   double d1=(mLastMotionX-a)/prevRadius;
		   double d2=(x-a)/currentRadius;
		   
		   if(d1>1 || d1<-1 || d2>1 || d2<-1)
			   return;
		   
		   float lastAngle=(float) Math.toDegrees(Math.acos(d1));
		   float currAngle=(float) Math.toDegrees(Math.acos(d2));
		   Log.d("Before:: CurrentAngle,LastAngle  :: x, y:", currAngle+","+lastAngle+"("+x+","+y+")");
		/*   if(y>160)
			   mAngleChange=currAngle-lastAngle;
		   else
			   mAngleChange=lastAngle-currAngle;
			   */
		   
		   if(y<b) {
			   currAngle = 180 + (180 - currAngle);
		   }
		   if(mLastMotionY < b) {
			   lastAngle = 180 + (180 - lastAngle);
		   }
		   Log.d("After:: CurrentAngle,LastAngle", currAngle+","+lastAngle);
		   mAngleChange = currAngle - lastAngle;
		   Log.d("Difference:: (a,b) ", ""+mAngleChange+"("+a+","+b+")");
		   //Log.d("ComputeAngle", ""+mAngleChange);
	   }
	
	   public float distanceFromCenter(float x,float y){
			//Distance Formula d=sqrt( sqr(x2-x1) + sqr(y2-y1))
			float d=(float) Math.sqrt( Math.pow((a-x), 2) +Math.pow((b-y),2));
			return d;
		}
	   
	   
	   public class BounceBackAnimation{
			Handler handler;
			Runnable runnable;
			int count=0,i=5;
			float angleD, angleChange;
			public BounceBackAnimation(final float angleDiff,int childCount){
				handler=new Handler();
				angleD=angleDiff;
				count = childCount;
				runnable=new Runnable(){
					public void run() {
						while(Math.abs(angleD) > 2) {
							Log.d("Here in animation", "AngleDiff: "+angleD);
							
						if(Math.abs(angleD) > 5) 
							angleChange=angleD/i;
						else {
							if(angleD < 0)
								angleChange = -1;
							else
								angleChange = 1;
						}
						i++;
						angleChange = angleD;		// *** THIS IS SET FOR NO ANIMATION!!
						angleD = angleD-angleChange;
						//selectedAlphabets.get(highlightedIndex).setY(yPos-30);
						for (int i = 0; i < count-2; i++) {
				            MenuButton child = (MenuButton)getChildAt(i);
				            
				            if (child.getVisibility() != GONE) {            
				            	//child.setClickable(false);
				            	double angle=child.getAngle();
				            	angle+=angleChange;
				            	child.setAngle(angle);
				            	child.calculateCenter(a,b,inR,angle);
				          
				                final int childLeft = child.getCenterX() - BUTTON_RADIUS;
				                final int childTop = child.getCenterY() - BUTTON_RADIUS;
				                final int lb = child.getCenterX() + BUTTON_RADIUS;
				                final int rb = child.getCenterY() + BUTTON_RADIUS;
				               
				                if(child.shouldDraw()) {
				                	child.setVisibility(View.VISIBLE);
				                	child.layout(childLeft, childTop, lb, rb);
				                }
				                else
				                	child.setVisibility(View.INVISIBLE);
				            }
				        }
						try{
							Thread.sleep(100);
						}catch(InterruptedException ie){}
						}
					}
				};
			}
			public void start(){
				try{
					handler.postDelayed(runnable,25);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	   
}
