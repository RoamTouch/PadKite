package com.roamtouch.menu;

import java.util.Hashtable;
import java.util.Vector;

import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import com.roamtouch.swiftee.SwifteeApplication;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.app.Instrumentation;

/**
 * Circular layout containing menu items and performing circular wheel animation
 * 
 */
public class CircularLayout extends ViewGroup {	
	   
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
//		private int mMinimumVelocity;
//		private int mMaximumVelocity;
	  
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
	   
		private int childEndPoint;
		
		private String m_name;
		
		private MenuButton hotBut;
		public static String PATH = BrowserActivity.THEME_PATH + "/";
			
		public String getName()
		{
			return m_name;
		}
		
		protected void setName(String name)
		{
			m_name = name;
		}
		
		protected MenuBGView menuBackground= null;
		
		private void initBG()
		{		
			menuBackground = new MenuBGView(this.context);
			menuBackground.setRadius(INNER_RADIUS);		
			addView(menuBackground);
		}		
		
		public CircularLayout(Context context) {
			super(context);
			//setOnTouchListener(this);
			mScroller = new Scroller(context);
			setFocusable(true);
			setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
			setWillNotDraw(false);
			final ViewConfiguration configuration = ViewConfiguration.get(context);
			mTouchSlop = configuration.getScaledTouchSlop();	
			this.context = context;		
			initBG();
		   }
		
		public CircularLayout(Context context, AttributeSet attrs) {
			super(context, attrs);
			mScroller = new Scroller(context);
			setFocusable(true);
			setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
			setWillNotDraw(false);
			final ViewConfiguration configuration = ViewConfiguration.get(context);
			mTouchSlop = configuration.getScaledTouchSlop();
			//Log.d("Touch slope", ""+mTouchSlop);
			//mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
			//mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
			initBG();
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
		   //Toast.makeText(context, ""+currentMode, 100).show();
	   }

		@Override
	   protected void onLayout(boolean changed, int left, int top, int right, int bot) {

//		   Log.d("---inside onLayout-----", "---------inside onlayout-------");
		   final int count = getChildCount();
		   double t = 0;

		   t=55;
  
		   childEndPoint = count - 2;

		   ImageView cone = (ImageView)getChildAt(childEndPoint);
		   
		   if (cone.getVisibility() != GONE) {         
			   cone.layout(a-mfcRadius, b-mfcRadius, a+mfcRadius, b+mfcRadius);
			   //cone.setClickable(false);
		   }
		   
		   MenuButton hotKey = (MenuButton)getChildAt(childEndPoint+1);		   
		   if(hotKey.getVisibility() != GONE){
			   int diff =  BUTTON_RADIUS + 12;//* (int)1.6;
			   hotKey.layout(a-diff-(int)1.5, b-inR-diff-(int)10.5, a+diff-(int)1.5, b-inR+diff-(int)10.5);
		   }

		  // MenuBGView bg = (MenuBGView)getChildAt(0);
		   
		   if (menuBackground.getVisibility() != GONE) {         
			   menuBackground.layout(a-mfcRadius, b-mfcRadius, a+mfcRadius, b+mfcRadius);
			   menuBackground.setClickable(false);
		   }
		   
			if(mAngleChange!=0){
				moveChilds();
				return;
			}
			
		   for (int i = 1; i < childEndPoint; i++) {
			   MenuButton child = (MenuButton)getChildAt(i);
			   if (child.getVisibility() != GONE) {    
			   double angle;				   
            	// Calc coordinates around the circle at the center of cart. system
			    
			    angle = (i-1)*t;
                angle = angle - 90 + 46;
            
            	child.setAngle(angle);
            	child.calculateCenter(a,b,inR,angle);
            	
            	//Log.i("Circle before if angle,x,y" , "("+ x +","+ y +")angle"+angle);            
            	//Log.i("Circle x,y" , "("+ x +","+ y +")");
 
                final int childLeft = child.getCenterX() - BUTTON_RADIUS;
                final int childTop = child.getCenterY() - BUTTON_RADIUS;
                final int lb = child.getCenterX() + BUTTON_RADIUS;
                final int rb = child.getCenterY() + BUTTON_RADIUS;
               
                if(child.shouldDraw()) {
                    child.layout(childLeft, childTop, lb, rb);                	
                }
            }
        }			   
	}
		
	public void setAngleChange(float angleChange){
		mAngleChange = angleChange;
	}
	
	//private int menu;
	
	public void resetMenu(){
		
		int current = BrowserActivity.getCurrentMenu(); 
		setHotKey(current);
		
		hotBut.setVisibility(View.VISIBLE);
		double t = 55;
		
		for (int i = 1; i < childEndPoint; i++) {
			
			final MenuButton child = (MenuButton)getChildAt(i);
			if (child.getVisibility() != GONE) {    
			   double angle;				   
         	// Calc coordinates around the circle at the center of cart. system
			    angle = (i-1)*t;
            angle=angle-90 + 46;
         
         	child.setAngle(angle);
         	child.calculateCenter(a,b,inR,angle); 

            final int childLeft = child.getCenterX() - BUTTON_RADIUS;
            final int childTop = child.getCenterY() - BUTTON_RADIUS;
            final int lb = child.getCenterX() + BUTTON_RADIUS;
            final int rb = child.getCenterY() + BUTTON_RADIUS;
            
            if(child.shouldDraw()) {
                child.layout(childLeft, childTop, lb, rb);                	
            }           
            
            //Draws the clicked button on the hotKey before executing
            child.setOnTouchListener(new OnTouchListener() {          	 
    			@Override
    		    public boolean onTouch(View v, MotionEvent event) {   				
    				int action = event.getAction();		
    				if (action == event.ACTION_DOWN){
    					//Set hotwith child
    					setHotWithChild(child);
    					drawHotTip();    	    					
    					//Execute armed hotBut
    					runHotWithDelay(400);    					
    				} 			
    				return true;}
    			}
    		);
     		
         }
     }			   
		mAngleChange = 0;
		mLastMotionY = 0;
		mLastMotionX = 0;
		mLastMotionAngle=-999;	
		moveChilds();	
	}	
	
	private void setHotKey(int menu){	
		
		if (menu==0){
			
	    	hotBut.setDrawables(MenuInflater.hotkey_image_main,MenuInflater.hotkey_highlight_main);	
			hotBut.setFunction(MenuInflater.hotkey_function_main);
			
		}  else if (menu==1) {
			
			hotBut.setDrawables(MenuInflater.hotkey_image_settings,MenuInflater.hotkey_highlight_settings);	
			hotBut.setFunction(MenuInflater.hotkey_function_settings);
		}
		hotName = hotBut.getDescription();
		hotBut.setIsArmed(true);
	}
	
	public double getZoomAngle(){
		MenuButton b = (MenuButton) getChildAt(4);
		return b.getAngle();
	}
	public void init(){

		//SEPARATOR
		ImageView coneSeparator = new ImageView(context);
		coneSeparator.setBackgroundResource(R.drawable.circleround_cone);
		addView(coneSeparator);
			
		hotBut = new MenuButton(context);		
		hotBut.setHotkey(true);
		hotBut.setOnTouchListener((OnTouchListener) this);
		int id = hotBut.getId();
		hotHash.put(id, hotBut); 
		addView(hotBut);		
	}   

	public boolean canScroll(float angleDiff,int childCount) {
		
		MenuButton first = (MenuButton)getChildAt(1);
		
		//MenuButton last = (MenuButton)getChildAt(childEndPoint-2);
		
		if((first.getAngle()+angleDiff)>-40){
			setHotKey(BrowserActivity.getCurrentMenu());
			return false;
		}
		
		//if((last.getAngle()+angleDiff)>-500){
		//	return false;
		//}
		
		return true;
	}
	
	 public void setfcRadius(int mfcRadius) {
			this.mfcRadius = mfcRadius;
			BUTTON_RADIUS = (int)(mfcRadius / 3.5f);
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
			    //Log.d("inside mLastMotionAngle touch Down"," resetting position!!----------------------");
            break;
		}
		
		case MotionEvent.ACTION_OUTSIDE:{
			 mIsBeingDragged=false;
			    mLastMotionAngle = -999;
			    mLastMotionX =0;
			    mLastMotionY =0;
			    //Log.d("inside mLastMotionAngle touch outside"," resetting position!!----------------------");
		}
		
		case MotionEvent.ACTION_MOVE:{
			 
			
			//final int yDiff = (int) Math.abs(y - mLastMotionY);
			//final int xDiff = (int) Math.abs(x - mLastMotionX);
            
            float currentAngle, angleDiff;
            mVelocityTracker.addMovement(event);
            if(mLastMotionAngle == -999) {
            	mLastMotionAngle = (float)computeAngle1(a, b, x, y);
    			mLastMotionX = x;
                mLastMotionY = y;
                //Log.d("inside mLastMotionAngle,"," reseted initial position!!----------------------x= " + x+" y ="+y );
                currentAngle = mLastMotionAngle;
            }
            currentAngle = (float)computeAngle1(a, b, x, y);
            //currentAngle=currentAngle*2;
            //Log.d("Action move---------------Current angle",""+currentAngle);
            angleDiff = currentAngle-mLastMotionAngle;
            //Log.d("Angle diff---------------",""+angleDiff);
            
            // Special case to handle move from fourth quadrant to first quadrant
            if(Math.abs(angleDiff) > 200) {
            	if(currentAngle < mLastMotionAngle) {
            		if((mLastMotionAngle > 270) && (currentAngle < 90)) {
            			angleDiff = currentAngle + (360 - mLastMotionAngle);
            			//Log.d("Angle diff adjusted---------------",""+angleDiff);
            		}
            	}
           		else {
           			if((mLastMotionAngle < 90) && (currentAngle > 270)) {
            				angleDiff = mLastMotionAngle + (360 - currentAngle);
            				angleDiff = -1*angleDiff;
            				///Log.d("Angle diff adjusted---------------",""+angleDiff);
           			}
           		}
            }
            		
           //if (yDiff > 3 || xDiff > 3) {
            
            if(Math.abs(angleDiff) > 0.5 && Math.abs(angleDiff) < 250)  {            
	          
	     	    //checkForMotionDir(x,y);
	            //computeAngle(x,y);
	            mAngleChange = angleDiff;
	            if(mAngleChange<0)
	            	direction = -1;
	            else
	            	direction = 1;
				int count=getChildCount();
				
				mLastMotionAngle = currentAngle;
				mLastMotionX = x;
	            mLastMotionY = y;
	            
	            if(!canScroll(angleDiff, count)){
	            	drawHotTip();            	
	            	//caca
	            	String url = null;
	    			if (hotBut.getFunction().equals("new_window")){			
	    				url = "add_pressed.png";    			 		
	    			}  else if (hotBut.getFunction().equals("gesture_kit_editor")){
	    				url = "gesturekiteditor_pressed.png";
	    			}
	    			hotBut.setHotDrawables(PATH + url);    			
	            	return true;
	            }
	            
	            
	            //if(mLastMotionAngle>270 && currentAngle<90)
	            //	mAngleChange = currentAngle;
	            
	            //Log.v("top", "top: "+top);
	            //if (top){
	            // 	return true;            	
	            // }
	               
	            moveChilds();            
	            
	        	//Log.i("x,y" , "("+ x +","+ y +")");
	            //Log.d("x,y,angle: ", x+","+y+","+mLastMotionAngle);
			}
            break;
		}
		case MotionEvent.ACTION_UP:
			
			/**
			 * EXECUTES HotBut on UP.
			 */
			if (isNew && BrowserActivity.getCurrentMenu()==0){ //Main
				hotFunction="new_window";
			} else if (isNew && BrowserActivity.getCurrentMenu()==1){ //Settings
				hotFunction="gesture_kit_editor";
			}
			if (hotBut.getIsArmed()){
				BrowserActivity.drawNothingTip();
				hotBut.setFunction(hotFunction);
				runHotWithDelay(100);
			} else {
				BrowserActivity.setToggleMenuVisibility();
			}
			
			final VelocityTracker velocityTracker = mVelocityTracker;
            velocityTracker.computeCurrentVelocity(1000);
            float initialVelocityY =  velocityTracker.getYVelocity();
            float initialVelocityX =  velocityTracker.getXVelocity();
            
            //Log.d("X and Y velocity","x:"+initialVelocityX+"y:"+initialVelocityY);
        	//Toast.makeText(mContext, "fling: " + 
        	//getScrollX() + "," + getScrollY() + "-" + initialVelocityX + "," + initialVelocityY + "-" + 
        	//getWidth() + "," + getHeight(), Toast.LENGTH_SHORT).show();

            if (Math.abs(initialVelocityX) > 0.2) {
            	fling=true;
            	fling(Math.abs(initialVelocityX), initialVelocityY);
            } else {
            	fling=false;
            }

            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
            
		    mIsBeingDragged=false;
		    mLastMotionAngle = -999;
		    mLastMotionX =0;
		    mLastMotionY =0;
		    
		    ///Log.d("inside mLastMotionAngle,"," resetting position!!----------------------");
		}		
		
		return true;
	}
	
	//private Runnable runnableHot;		
	private Handler handler = new Handler();
	 
	private String hotName;
	private String hotFunction;
	private String hotFlag;
		 
	int cX; // = hotBut.getCenterX();
	int cY; // = hotBut.getCenterY();
	boolean top;	  
	private Hashtable hotHash = new Hashtable(); 
	boolean fling;
	boolean isNew;	
	private MenuButton setBack;
	Handler buttonHandler = new Handler();

	public void moveChilds(){		
		
		for (int i = 1; i < childEndPoint; i++) {
				
	            MenuButton child = (MenuButton)getChildAt(i);
	            
	            if (child.getVisibility() != GONE) {         
	            	
	            	if(child.isHotkey()){
					   int diff = BUTTON_RADIUS/2;
					   child.layout(a-diff, b-inR-diff,a+diff, b-inR+diff);						   
					   continue;
					}
	            	
	            	//child.setClickable(false);
	            	double angle=child.getAngle();
	            	angle+=mAngleChange;
	            	child.setAngle(angle);
	            	child.calculateCenter(a,b,inR,angle);
	          
	                final int childLeft = child.getCenterX() - BUTTON_RADIUS;
	                final int childTop = child.getCenterY() - BUTTON_RADIUS;
	                final int lb = child.getCenterX() + BUTTON_RADIUS;
	                final int rb = child.getCenterY() + BUTTON_RADIUS;              
	                
	                //jose    
	                int count = getChildCount();
	                Log.v("angle", "angle: "+angle);
	               
	                if(child.shouldDraw()) {
	                	
	                	child.setVisibility(View.VISIBLE);
	                	child.layout(childLeft, childTop, lb, rb);	                              	
	                	
	                } else {
	                	
	                	child.setVisibility(View.INVISIBLE);
	                	
	                	if (!child.isAnglePositive()){	   
         		
	                		int id = child.getId();
	                		//boolean has = hotHash.containsKey(id);
	                	
	                		hotName = child.getDescription();
	                		hotFunction = child.getFunction();	  
	                		
	                		MenuButton checkChid = (MenuButton) hotHash.get(id);                		
                			
                    		String childName = child.getFunction();
                			String checkName = null;
                			if (checkChid!=null){
                				checkName = checkChid.getFunction();
                			}        			       			
                			
                			String url = null;
                			hotBut.setIsArmed(true);
                			url = hotFunction + "_pressed.png";
                			
                			if (hotFunction.equals("finger_model")){                				
                				if (!SwifteeApplication.getFingerMode()){
                					 url = "Finger_model_single_pressed.png";
                			 	} else {
                			 		url = "Finger_model_multi_pressed.png";
                			 	} 			
                			 } 
                			 
                			 if (child.getFunction().equals("backward")){  
                				 if (!child.isEnabled()){
                					 url = "backward_disabled_hot.png";          
                					 hotName = "<g>Go Backward";
                					 hotBut.setIsArmed(false);    
                				 }
                				 
                			 } else if (child.getFunction().equals("forward")){  
                				 if (!child.isEnabled()){
                					 url = "forward_disabled_hot.png";          
                					 hotName = "<g>Go Forward";
                					 hotBut.setIsArmed(false);    
                				 }
                				 
                			 } else if (child.getFunction().equals("set_homepage")){  
                				 if (!child.isEnabled()){
                					 url = "sethomepage_disabled_hot.png";          
                					 hotName = "<g>Set Homepage";
                					 hotBut.setIsArmed(false);    
                				 }
                				 
                			 }	else if (child.getFunction().equals("bookmark_edit")){  
                				 if (!child.isEnabled()){
                					 url = "bookmark_edit_normal_hot.png";          
                					 hotName = "<g>Add Bookmark";
                					 hotBut.setIsArmed(false);    
                				 }
                				 
                			 }  else if (child.getFunction().equals("share")){  
                				 if (!child.isEnabled()){
                					 url = "share_normal_hot.png";          
                					 hotName = "<g>Share page";
                					 hotBut.setIsArmed(false);    
                				 }
                			 }                     			         			 
                			 
                			 
                			 hotBut.setVisibility(View.VISIBLE);
                			 hotBut.setHotDrawables(PATH + url);
                			 
                			drawHotTip();         		
                			
                    		if ( direction==-1 && !childName.equals(checkName) ){
                    			isNew=false;                    			
                    			
                    			//Backward-Forward switch
                    			/*if (child.getFunction().equals("backward")){  
                        			if (child.isEnabled()){
                        				setBack = child;             			       
                        				buttonHandler.postDelayed(backForButs, 1500);
                        			}
                        		}*/

                    			//Backward-Forward switch
                    			/*if (child.getFunction().equals("homepage")){  
                        			setBack = child;             			       
                        			buttonHandler.postDelayed(bookMarkButs, 1500);                      			
                        		}*/	
                    		}                              		
	                	}	                	
	            	}          
	            }
	      }	 
	 } 
	 
	 private void setHotWithChild(MenuButton child){		 
		String childFunc = null;
		childFunc = child.getFunction();			
		hotName = child.getDescription();
		hotFunction = child.getFunction();
		String url = childFunc + "_pressed.png";
		hotBut.setHotDrawables(PATH + url); 	
		hotBut.setFunction(childFunc);
		drawHotTip();				  
	}
	 
	 private void runHotWithDelay(int delay){		 
		 Handler showHotHandler = new Handler(); 
			showHotHandler.postDelayed(new Runnable() { 
		         public void run() { 
		        	long downTime = SystemClock.uptimeMillis();
		        	long eventTime = SystemClock.uptimeMillis();	        	
		        	MotionEvent hotEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, cX, cY, 0);				
		        	hotBut.dispatchTouchEvent(hotEvent);
		        	hotEvent.recycle();		        	
		        	hotBut.setIsArmed(false);	
		         } 
		  }, delay);			
	 }
	 
	 public void drawHotTip(){		         						
		 String[] text = {hotName};		
		 int[] loc = new int[2]; 		
		 int w = hotBut.getWidth();
		 int h = hotBut.getHeight();
		 hotBut.getLocationOnScreen(loc);
		 int x = loc[0];
		 int y = loc[1];
		 Rect re = new Rect(x, y, w, h);
		 int centerX = hotBut.getCenterX();
		 BrowserActivity.drawTip(re, text, centerX, y);
		 invalidate();
	 }
		 
	public void fling(float velocityX, float velocityY) 
	{
		//Log.d("Scroll X,Y",getScrollX()+","+getScrollY());		
		//handler.removeCallbacks(runnableHot);	
		mScroller.fling(getScrollX(), getScrollY(), velocityX, velocityY, 0,320, 0, 320);
		invalidate();
	}

	@Override
	public void computeScroll() {
		//Log.d("INSIDE computeScroll","-----------------------------");
		if (mScroller.computeScrollOffset()) {
			//Log.d("INSIDE computeScrolloffset","-----------------------------");
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
              
            	//Log.i("inside ACTION_MOVE mLastMotionX,mLastMotionY" , "("+ mLastMotionX +","+ mLastMotionY +")");
                final int yDiff = (int) Math.abs(y - mLastMotionY);
                final int xDiff = (int) Math.abs(x - mLastMotionX);
                
                if (yDiff > mTouchSlop || xDiff > mTouchSlop) {
                    mIsBeingDragged = true;
                }
                break;

            case MotionEvent.ACTION_DOWN:
                /* Remember location of down touch */
		
            //caca
			String url = null;
			if (hotBut.getFunction().equals("new_window")){			
				url = "add_pressed.png";    			 		
			 }  else if (hotBut.getFunction().equals("gesture_kit_editor")){
 				url = "gesturekiteditor_pressed.png";
 			}
			isNew=true;
			hotBut.setHotDrawables(PATH + url);

            	mLastMotionX = x;
                mLastMotionY = y;

                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                /* Release the drag */
                mIsBeingDragged = false;
                //Log.i("inside ACTION_UP mLastMotionX,mLastMotionY" , "("+ mLastMotionX +","+ mLastMotionY +")");
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
	   
	   
	   public void computeAngle(float x,float y){
		   
		   double prevRadius, currentRadius;
		   prevRadius = Math.sqrt((mLastMotionX - a)*(mLastMotionX - a) + (mLastMotionY - b)*(mLastMotionY - b));
		   currentRadius = Math.sqrt((x-a)*(x-a)+(y-b)+(y-b));
		   
		   ///Log.d("prev rad, curr radius", prevRadius+","+currentRadius);
		   
		   //prevRadius = currentRadius = inR;
		  // float r=distanceFromCenter(x, y);
		   double d1=(mLastMotionX-a)/prevRadius;
		   double d2=(x-a)/currentRadius;
		   
		   if(d1>1 || d1<-1 || d2>1 || d2<-1)
			   return;
		   
		   float lastAngle=(float) Math.toDegrees(Math.acos(d1));
		   float currAngle=(float) Math.toDegrees(Math.acos(d2));
		   ///Log.d("Before:: CurrentAngle,LastAngle  :: x, y:", currAngle+","+lastAngle+"("+x+","+y+")");
		   /*   if(y>160)
			   		mAngleChange=currAngle-lastAngle;
		   		else
			   		mAngleChange=lastAngle-currAngle;*/
		   
		   if(y<b) {
			   currAngle = 180 + (180 - currAngle);
		   }
		   if(mLastMotionY < b) {
			   lastAngle = 180 + (180 - lastAngle);
		   }
		   ///Log.d("After:: CurrentAngle,LastAngle", currAngle+","+lastAngle);
		   mAngleChange = currAngle - lastAngle;
		   ///Log.d("Difference:: (a,b) ", ""+mAngleChange+"("+a+","+b+")");
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
							//Log.d("Here in animation", "AngleDiff: "+angleD);
							
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
						for (int i = 1; i < count-2; i++) {
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

/*Runnable backForButs = new Runnable() {
public void run() {
	if (setBack.getFunction().equals(hotFunction)){
		setHotWithChild(setBack);
	} else {
		buttonHandler.removeCallbacks(backForButs); 
	}
}
};*/

/*Runnable bookMarkButs = new Runnable() {
	public void run() {
		if (setBack.getFunction().equals(hotFunction)){
			setHotWithChild(setBack);
		} else {
			buttonHandler.removeCallbacks(bookMarkButs); 
		}
	}
 };*/
