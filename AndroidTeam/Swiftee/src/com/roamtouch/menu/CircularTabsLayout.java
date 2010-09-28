package com.roamtouch.menu;

import com.roamtouch.swiftee.R;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Circular layout containing menu items and performing circular wheel animation
 * 
 */
public class CircularTabsLayout extends ViewGroup {
	
	/**
	 * Calculate the radius for menu buttons and inner radius 
	 */
		private final float scale = getContext().getResources().getDisplayMetrics().density;
		private final float INNER_RADIUS_DIP = 110; // 64dip=10mm, 96dip=15mm, 192dip=30mm expressed in DIP
		private final int INNER_RADIUS = (int) (INNER_RADIUS_DIP * scale + 0.5f); //Converting to Pixel

		private int mfcRadius ;
		private int BUTTON_RADIUS;
		
		private boolean mIsBeingDragged = false;
		private float mLastMotionY,mLastMotionX, mLastMotionAngle=-999;
		private int mTouchSlop;

		/**
		 * flag indicating moving direction
		 *  1 for clockwise
		 * -1 for anti clockwise
		 */
		private int direction;
		
		private Scroller mScroller;
		
		private VelocityTracker mVelocityTracker;
		
		//Centre of the circular menu
		private int a,b;
	   
		private Context context;
    
		//radius
		private int outR;
	   
		private int inR=INNER_RADIUS;
	   
		private float mAngleChange;
	   
		private int childStartPoint;
		
		private int activetabIndex = 2;
		
		protected MenuBGView menuBackground= null;
		
		private String m_name = "Windows";
		
		public String getName()
		{
			return m_name;
		}
		
		protected void setName(String name)
		{
			m_name = name;
		}
		
		private void initBG()
		{
			// FIXME
			/*Bitmap menuBG = BitmapFactory.decodeFile("/sdcard/Swiftee/Default Theme/circle.png");
			//Log.d("FormatTest","Resource32: " + menuBG.getConfig());

			ImageView menuBackground = new ImageView(context);
			//menuBackground.setImageResource(R.drawable.circle);
			menuBackground.setImageBitmap(menuBG);
		
			// Enable dithering when our 32-bit image gets drawn.
			//Drawable drawable32 = menuBackground.getDrawable();
			//drawable32.setDither(true);
		
			menuBackground.setScaleType(ImageView.ScaleType.CENTER);*/
			menuBackground = new MenuBGView(this.context);
			menuBackground.setRadius(INNER_RADIUS);
		
			addView(menuBackground);
		}		
		
		public CircularTabsLayout(Context context) {
			super(context);
			mScroller = new Scroller(context);
			setFocusable(true);
			setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
			setWillNotDraw(false);
			final ViewConfiguration configuration = ViewConfiguration.get(context);
			mTouchSlop = configuration.getScaledTouchSlop();
			  
			this.context = context;			

			initBG();
			
			ImageView redCircle = new ImageView(context);
			redCircle.setBackgroundResource(R.drawable.red_circle);
			addView(redCircle);
		   }
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		   final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		   final int widthSpecSize =  MeasureSpec.getSize(widthMeasureSpec);
        
		   a = widthSpecSize /2;
        
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
		    t=55;


		    childStartPoint = 2;
		    Button but = (Button)getChildAt(count-1);
		    int diff = BUTTON_RADIUS/2;
		    but.layout(a-diff, b-inR-diff,a+diff, b-inR+diff);

		    ImageView cone = (ImageView)getChildAt(count-2);	   
		    if (cone.getVisibility() != GONE) {         
		    	cone.layout(a-mfcRadius, b-mfcRadius, a+mfcRadius, b+mfcRadius);
		    	cone.setClickable(false);
		    }

			if (menuBackground.getVisibility() != GONE) {         
				menuBackground.layout(a-mfcRadius, b-mfcRadius, a+mfcRadius, b+mfcRadius);
				menuBackground.setClickable(false);
			}

		    for (int i = 2; i < count-childStartPoint; i++) {
		    	
		    	View v = getChildAt(i);
		    	if (!(v instanceof TabButton))
		    		continue;
		    	
		    	TabButton child = (TabButton)v;
		    	if (child.getVisibility() != GONE) { 

		    		// Calculate coordinates around the circle at the centre of cart. system
		    		double angle = (i-2)*t;
		    		angle=angle-90 + 46;
		    		child.setAngle(angle);
		    		child.calculateCenter(a,b,inR,angle);
		    		//child.calCloseButCenter(a,b,inR-50,angle);            	

		    		final int childLeft = child.getCenterX() - BUTTON_RADIUS;
		    		final int childTop = child.getCenterY() - BUTTON_RADIUS;
		    		final int lb = child.getCenterX() + BUTTON_RADIUS;
		    		final int rb = child.getCenterY() + BUTTON_RADIUS;


		    		if(child.shouldDraw()) {
		    			if(i == activetabIndex){

		    				ImageView redCircle = (ImageView)getChildAt(1);
		    				redCircle.setBackgroundResource(R.drawable.red_circle);
		    				redCircle.layout(childLeft-4, childTop-4, lb+4, rb+4);   
		    			}
		    			child.layout(childLeft, childTop, lb, rb);                	
		    		}
		    	}
		    }			   
	   }
	   public void setMode(int mode){
		   
		
		 	ImageView coneSeparator = new ImageView(context);
		 	coneSeparator.setBackgroundResource(R.drawable.circleround_cone);
		 	addView(coneSeparator);

		 	Button but = new Button(context);
		 	but.setId(33);
		 	but.setBackgroundResource(R.drawable.add_tab);
		 	addView(but);

	   }   

	   public boolean canScroll(float angleDiff,int childCount) {
		   	TabButton first = (TabButton)getChildAt(2);
		   	TabButton last = (TabButton)getChildAt(childCount-3);

		   	if((first.getAngle()+angleDiff)>4)
		   		return false;
		   	else if(last.getAngle()+angleDiff < 174)
		   		return false;
		   	return true;
	   }
	
	   public void setActiveTabIndex(TabButton child){

		   	final int childLeft = child.getCenterX() - BUTTON_RADIUS;
		   	final int childTop = child.getCenterY() - BUTTON_RADIUS;
		   	final int lb = child.getCenterX() + BUTTON_RADIUS;
		   	final int rb = child.getCenterY() + BUTTON_RADIUS;

		   	ImageView redCircle = (ImageView)getChildAt(1);
		   	redCircle.setBackgroundResource(R.drawable.red_circle);
		   	redCircle.layout(childLeft-3, childTop-3, lb+3, rb+3);   

	   }
		public int getActiveTabIndex(){
			return activetabIndex;
		}
		public void setfcRadius(int mfcRadius) {
			this.mfcRadius = mfcRadius;
			BUTTON_RADIUS = (int)(mfcRadius / 4f);
			inR = (int) (mfcRadius-BUTTON_RADIUS*1.5f); //-5 ;
			outR = 2*mfcRadius;
			menuBackground.setRadius(mfcRadius);

		}

		public int getfcRadius() {
			return mfcRadius;
		}

		public void setPosition(int a, int b){
			this.a = a;
			this.b = b;
		}
		@Override
		public boolean onTouchEvent(MotionEvent event) {

			final int action = event.getAction();
			final float x = event.getX();
			final float y = event.getY();


			if(Math.pow(x-a, 2)+Math.pow(y-b, 2)-Math.pow(inR-40, 2)<0 && Math.pow(x-a, 2)+Math.pow(y-b, 2)-Math.pow(outR, 2)>0)
				return false;

			if(x< a-outR && x> a+outR && y<b-outR && y>b+outR)
				return false;
			
			if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
				// Don't handle edge touches immediately -- they may actually belong to one of our
				// descendants.
				return false;
			}


			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
				mVelocityTracker.setRotationCenter(a, b);
			}

			switch(action){

			case MotionEvent.ACTION_DOWN:{

				if (!mScroller.isFinished()) {
					mScroller.abortAnimation();
				}
				mIsBeingDragged=false;
				mLastMotionAngle = -999;
				mLastMotionX =0;
				mLastMotionY =0;
				Log.d("inside mLastMotionAngle touch Down"," resetting position!!----------------------");
				break;
			}
			case MotionEvent.ACTION_OUTSIDE:{
				mIsBeingDragged=false;
				mLastMotionAngle = -999;
				mLastMotionX =0;
				mLastMotionY =0;
				Log.d("inside mLastMotionAngle touch outside"," resetting position!!----------------------");
			}
			case MotionEvent.ACTION_MOVE:{


				float currentAngle, angleDiff;
				mVelocityTracker.addMovement(event);
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

				if(Math.abs(angleDiff) > 0.5 && Math.abs(angleDiff) < 250)  {

					mAngleChange = angleDiff;
					if(mAngleChange<0)
						direction = -1;
					else
						direction = 1;
					int count=getChildCount();

					mLastMotionAngle = currentAngle;
					mLastMotionX = x;
					mLastMotionY = y;

					if(!canScroll(angleDiff, count))
						return true;
					//if(mLastMotionAngle>270 && currentAngle<90)
					//	mAngleChange = currentAngle;


					moveChilds();
					//Log.i("x,y" , "("+ x +","+ y +")");
					//Log.d("x,y,angle: ", x+","+y+","+mLastMotionAngle);
				}
				break;
			}
			case MotionEvent.ACTION_UP:
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000);
				float initialVelocityY =  velocityTracker.getYVelocity();
				float initialVelocityX =  velocityTracker.getXVelocity();
				//Log.d("X and Y velocity","x:"+initialVelocityX+"y:"+initialVelocityY);
				if (Math.abs(initialVelocityX) > 0.2) {
					fling(Math.abs(initialVelocityX), initialVelocityY);
				}

				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
				mIsBeingDragged=false;
				mLastMotionAngle = -999;
				mLastMotionX =0;
				mLastMotionY =0;
				Log.d("inside mLastMotionAngle,"," resetting position!!----------------------");
			}


			return true;
		}
	
		public void moveChilds(){
			int count=getChildCount();
			for (int i = 2; i < count-childStartPoint; i++) {

				TabButton child = (TabButton)getChildAt(i);

				if (child.getVisibility() != GONE) {            
					double angle=child.getAngle();
					angle+=mAngleChange;
					child.setAngle(angle);
					child.calculateCenter(a,b,inR,angle);

					final int childLeft = child.getCenterX() - BUTTON_RADIUS;
					final int childTop = child.getCenterY() - BUTTON_RADIUS;
					final int lb = child.getCenterX() + BUTTON_RADIUS;
					final int rb = child.getCenterY() + BUTTON_RADIUS;

					ImageView redCircle = (ImageView)getChildAt(1);
					//Button but1 = (Button)getChildAt(count-3);
					if(child.shouldDraw()) {
						child.setVisibility(View.VISIBLE);
						child.layout(childLeft, childTop, lb, rb);
						if(i == activetabIndex){
							redCircle.setVisibility(View.VISIBLE);
						//	but1.setVisibility(View.VISIBLE);
							//child.calCloseButCenter(a,b,inR-50,angle);

						//	but1.layout(child.getCloseButCenterX()-25, child.getCloseButCenterY()-25,child.getCloseButCenterX()+25, child.getCloseButCenterY()+25);

							redCircle.setBackgroundResource(R.drawable.red_circle);
							redCircle.layout(childLeft-3, childTop-3, lb+3, rb+3);   
						}
					}
					else{
						if(i == activetabIndex){
							redCircle.setVisibility(View.INVISIBLE);
							child.setVisibility(View.INVISIBLE);
						//	but1.setVisibility(View.INVISIBLE);
						}
						child.setVisibility(View.INVISIBLE);
					}
				}
			}
	 }
	public void fling(float velocityX, float velocityY) 
	{
		//Log.d("Scroll X,Y",getScrollX()+","+getScrollY());
		mScroller.fling(getScrollX(), getScrollY(), velocityX, velocityY, 0,320, 0, 320);
		invalidate();
	}

	@Override
	public void computeScroll() {
		Log.d("INSIDE computeScroll","-----------------------------");
		if (mScroller.computeScrollOffset()) {
			Log.d("INSIDE computeScrolloffset","-----------------------------");
			mAngleChange = mScroller.getAngle();
			int count = getChildCount();
			mAngleChange *= direction;
			if(!canScroll(mAngleChange, count)){
					mScroller.forceFinished(true);
	            	return;
			}
			moveChilds();
			postInvalidate();
		}
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
              
            	 Log.i("inside ACTION_MOVE mLastMotionX,mLastMotionY" , "("+ mLastMotionX +","+ mLastMotionY +")");
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
	   

	   public float distanceFromCenter(float x,float y){
			//Distance Formula d=sqrt( sqr(x2-x1) + sqr(y2-y1))
			float d=(float) Math.sqrt( Math.pow((a-x), 2) +Math.pow((b-y),2));
			return d;
		}
	   
}
