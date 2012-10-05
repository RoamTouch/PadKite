package com.roamtouch.menu;

import java.io.ByteArrayOutputStream;
import java.util.Vector;

import android.webkit.WebView;

import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.utils.GetDomainName;
import com.roamtouch.utils.Base64;
import com.roamtouch.utils.FindColor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
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
		private int BUTTON_RADIUS2;
		private int BUTTON_MENU_RADIUS;
		
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
		private int inR2=INNER_RADIUS;
		private int inR3=INNER_RADIUS;
	   
		private float mAngleChange;
	   
		private int childStartPoint = 4;
		
		private int activetabIndex = 2;
		
		protected MenuBGView menuBackground= null;
		//protected ImageView currentWindowIcon = null;
		protected ImageView hotBorder = null;	
		
		private String m_name = "Windows";
		
		private GetDomainName gdn = new GetDomainName();
		
		private FindColor findColor;
		
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
		
			menuBackground = new MenuBGView(this.context);
			menuBackground.setRadius(INNER_RADIUS);
			
			ImageView empty = new ImageView(this.context);

			addView(empty);
			addView(menuBackground);
		}		
		
		int z=0;
		
		public void drawHotTip() {		
			   
			int[] loc = new int[2];
			int w = hotTab.getWidth();
			int h = hotTab.getHeight();
			hotTab.getLocationOnScreen(loc);
			int x = loc[0]-4;
			int y = loc[1]-16;
			Rect re = new Rect(x, y, w, h);	
			
			String tT = hotTab.getHotTitle();			
		
			if (tT!=null){
				
				if (tT.equals("Landing Page") || tT.equals("Back")) {					
					
					String[] textTitle = { tT };
					re.left = re.left - 13; 
					BrowserActivity.drawTip(re, textTitle, hotTab.getCenterX(), y);
					
				} else {
					
					String tU = hotTab.getTabURL();	
					tU = gdn.getDomain(tU);				
					
					if (tU==null){
											
						if (!hotTab.isHasOptional()){								
							String[] textURL = {tT};
							BrowserActivity.drawTip(re, textURL, hotTab.getCenterX(), y);						
						} else {							
							String[] textURL = { (String)hotTab.getOptionalMessage()[z] , tT };
							z++; if(z==3){ z=0; }
							BrowserActivity.drawTip(re, textURL, hotTab.getCenterX(), y);
						}
						
					} else {
						
						re.top = re.top - 50; 
						
						if (!hotTab.isHasOptional()){
							String[] textURL = { tT, tU };
							BrowserActivity.drawTip(re, textURL, hotTab.getCenterX(), y);
						} else {
							String[] textURL = { (String)hotTab.getOptionalMessage()[z], tT };
							z++; if(z==3){ z=0; }
							BrowserActivity.drawTip(re, textURL, hotTab.getCenterX(), y);
							
						}
						
					}
				}
			}
			invalidate();			
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
		   }
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		   final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		   final int widthSpecSize =  MeasureSpec.getSize(widthMeasureSpec);
        
		   a = widthSpecSize /2;
        
//		   Log.i("widthSpecSize:", ""+widthSpecSize);        

		   final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		   final int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);
        
		   b = heightSpecSize /2;
        
//		   Log.i("heightSpecSize:", ""+heightSpecSize);        

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
		  
		    int diffHot = BUTTON_RADIUS2+13;
		    hotBorder.layout(a-diffHot-(int)1.5, b-inR2-diffHot-(int)10.5, a+diffHot-(int)1.5, b-inR2+diffHot-(int)10.5);
		    		    
		    int diff = BUTTON_RADIUS2-3;
		    int le = a-diff-(int)1.5;
		    int to = b-inR2-diff-(int)10.5;
		    int ri = a+diff-(int)1.5;
		    int bo = b-inR2+diff-(int)10.5;
		    hotTab.layout(le, to, ri, bo); 
		    hotTab.setHotRect(le,to,ri,bo);
		    
		    if (coneSeparator.getVisibility() != GONE) {         
		    	coneSeparator.layout(a-mfcRadius, b-mfcRadius, a+mfcRadius, b+mfcRadius);
		    	coneSeparator.setClickable(false);
		    }

			if (menuBackground.getVisibility() != GONE) {         
				menuBackground.layout(a-mfcRadius, b-mfcRadius, a+mfcRadius, b+mfcRadius);
				menuBackground.setClickable(false);
			}		   
	   }
	   
	   public final static String PATH = BrowserActivity.THEME_PATH + "/";
	   
	   protected TabButton hotTab;
	   protected MenuButton backBut;
	   protected ImageView coneSeparator;
	   protected static Vector tabVector = new Vector();
	   
	   public void init(){		   
		
		    coneSeparator = new ImageView(context);
		    coneSeparator.setBackgroundResource(R.drawable.circleround_cone);
		 	addView(coneSeparator);		 
		 	
		 	backBut = new MenuButton(context);
		 	backBut.setDrawables(
		 			BrowserActivity.THEME_PATH + "/backward_normal.png",
		 			BrowserActivity.THEME_PATH + "/backward_pressed.png");	
		 	backBut.setFunction("backward");
		 	addView(backBut);	 	
		 	
		 	hotTab = new TabButton(context);		 	
		 	hotTab.setHotkey(true);
		 	hotTab.setOnTouchListener((OnTouchListener) this);
		 	addView(hotTab);
		 	
		 	//tabVector.add(hotTab);
		 	
		 	hotBorder = new ImageView(context);
		 	hotBorder.setBackgroundResource(R.drawable.wm_tab_border);
			addView(hotBorder);		
		 	
	   }   
	   
	   public static Vector getTabVector(){		   
		   return tabVector;
	   }   
	   
	   boolean topStop;
	   
	   public boolean canScroll(float angleDiff,int childCount) {
		   
		   /*TabButton first = (TabButton)getChildAt(2);
		   	TabButton last = (TabButton)getChildAt(childCount-4);

		   	if((first.getAngle()+angleDiff)>4)
		   		return false;
		   	else if(last.getAngle()+angleDiff < 174)
		   		return false;*/
		  
		   return true;
	   }
	
	   public void setActiveTabIndex(TabButton child){

		   	final int childLeft = child.getCenterX() - BUTTON_RADIUS;
		   	final int childTop = child.getCenterY() - BUTTON_RADIUS;
		   	final int lb = child.getCenterX() + BUTTON_RADIUS;
		   	final int rb = child.getCenterY() + BUTTON_RADIUS;

		   	activetabIndex = child.getId();
		   	//currentWindowIcon.layout(childLeft-10, childTop-10, lb+10, rb+10);   
		   	//currentWindowIcon.setVisibility(VISIBLE);
		   	
	   }
	   
		public int getActiveTabIndex(){
			return activetabIndex;
		}
		public void setfcRadius(int mfcRadius) {
			this.mfcRadius = mfcRadius;
			
			BUTTON_RADIUS = (int)(mfcRadius / 4.5f);
			inR = (int) (mfcRadius-BUTTON_RADIUS*1.5f) - 10; //-5 ;

			BUTTON_RADIUS2 = (int)(mfcRadius / 3.5f);
			inR2 = (int) (mfcRadius-BUTTON_RADIUS2*1.5f); //-5 ;
			
			BUTTON_MENU_RADIUS = (int)(mfcRadius / 4.5f) + 10;
			inR3 = (int) (mfcRadius-BUTTON_MENU_RADIUS*1.5f); //-5 ;
			
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
				break;
			}
			case MotionEvent.ACTION_OUTSIDE:{
				mIsBeingDragged=false;
				mLastMotionAngle = -999;
				mLastMotionX =0;
				mLastMotionY =0;
			}
			case MotionEvent.ACTION_MOVE:{


				float currentAngle, angleDiff;
				mVelocityTracker.addMovement(event);
				if(mLastMotionAngle == -999) {
					mLastMotionAngle = (float)computeAngle1(a, b, x, y);
					mLastMotionX = x;
					mLastMotionY = y;
					currentAngle = mLastMotionAngle;
				}
				currentAngle = (float)computeAngle1(a, b, x, y);				
				angleDiff = currentAngle-mLastMotionAngle;
				
				// Special case to handle move from fourth quadrant to first quadrant
				if(Math.abs(angleDiff) > 200) {
					if(currentAngle < mLastMotionAngle) {
						if((mLastMotionAngle > 270) && (currentAngle < 90)) {
							angleDiff = currentAngle + (360 - mLastMotionAngle);
						}
					}
					else {
						if((mLastMotionAngle < 90) && (currentAngle > 270)) {
							angleDiff = mLastMotionAngle + (360 - currentAngle);
							angleDiff = -1*angleDiff;
						}
					}
				}

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

					int topAngle = resetTops[BrowserActivity.getTabCount()-1];				
	    			if ( topAngle < backBut.getAngle() ) {
	    				moveChilds(false);
	    			} else {
	    				moveChilds(true);
	    			}  			
	    			
				}
				break;
			}
			case MotionEvent.ACTION_UP:
				
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000);
				float initialVelocityY =  velocityTracker.getYVelocity();
				float initialVelocityX =  velocityTracker.getXVelocity();
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
				
				
				handler.removeCallbacks(runnableClose);	
            	handler.removeCallbacks(runnableHideClose);	
				
				//Go back to main menu
			    if (hotTab.getHotTitle().equals("Back")){
			    	
			    	BrowserActivity.setCurrentMenu(0);	
			    	resetMenu();
			    	
	            } else if (hotTab.isClose()){	            	
            	
	            	AlertDialog alertDialog;
	            	alertDialog = new AlertDialog.Builder(context).create();	        		
	            	alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);	        	    
	        		
	            	String message = "Do you want to close web page "	            			
            			+ hotTab.getHotTitle();
	            	
	            	if (hotTab.getHotTitle().equals("Landing Page")){
	            		//message
	            	}
	            	
	            	alertDialog.setMessage("Do you want to close web page "	            			
	            			+ hotTab.getHotTitle()
	            			+ "\n" 
	            			+ hotTab.getTabURL());	        		
	        		
	        	    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	        	    	
	        	    	public void onClick(DialogInterface dialog, int which) {
	        	    		
	        	    		//Force touch hotKey loaded.
	        	    		long downTime = SystemClock.uptimeMillis();
	        				long eventTime = SystemClock.uptimeMillis();								
	        				MotionEvent hotEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, cX, cY, 0);    				
	        				hotTab.dispatchTouchEvent(hotEvent);
	        				resetMenu();
	        				
	        	    	 } }); 
	        	    
	        	    alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
	        	    	public void onClick(DialogInterface dialog, int which) { 	 
	        	    		cleanClose();
	        	    		return;
	        	    }});
	        	    
	        	  	alertDialog.show();          	
	            	         	           	
	            	
	            } else {            	
	            	
	            	BrowserActivity.setToggleMenuVisibility();
	            	resetMenu();
	            }			
			}
			return true;
		}
		
		
		//int [] backTops = {100, 60, 82, 329, 180};	
		
		int cX; 
		int cY;
		private Handler handler = new Handler();
	
		public void moveChilds(boolean move){
			
				int count = getChildCount();
				
				for (int i = 3; i < count; i++) {
					
			    	View something = getChildAt(i);
			    	
			    	if (something instanceof MenuButton){
			    		
			    		MenuButton back = (MenuButton) something;
			    		
			    		Log.v("back", "back andgle: "+back.getAngle() + " tabs:" +BrowserActivity.getTabCount());
			    				    		
			    		double angle=back.getAngle();
		            	angle+=mAngleChange;
		            	back.setAngle(angle);
		            	back.calculateCenter(a,b,inR3,angle);           	
		            	
		                if (back.shouldDraw()){	
		                	
		                	final int childLeft = back.getCenterX() - BUTTON_MENU_RADIUS;
			                final int childTop = back.getCenterY() - BUTTON_MENU_RADIUS;
			                final int lb = back.getCenterX() + BUTTON_MENU_RADIUS;
			                final int rb = back.getCenterY() + BUTTON_MENU_RADIUS;             			    			
			                
			                back.setVisibility(View.VISIBLE);  
			                if (move){
			                	back.layout(childLeft, childTop, lb, rb);
			                }
			                               
			               if (back.isHidden()){	                	
			            	   
			            	   int countTabs = BrowserActivity.getTabCount();
			            	   
			            	   if (countTabs<=1){
			            		   
			            	   	   hotTab.applyInit();
			            	   	   
			            	   } else {
			            	  
				            	   	TabButton lastTab = (TabButton) getChildAt(countTabs+1);
				                	BitmapDrawable bitmapDrawable = lastTab.getBitmapDrawable();
				                	WebView wv = lastTab.getWebView();
				                	String tabTitle = wv.getTitle();		                	
				                	hotTab.setHotTitle(tabTitle);	
				                	String tabUrl = wv.getUrl();
				                	hotTab.setTabURL(tabUrl);					                	
				                	hotTab.setBackgroundDrawable(bitmapDrawable);	
				                	hotTab.invalidate();
				                	
			            	   }
				                
				                back.setHidden(false);
			                	drawHotTip();		
			                	
			                }           
			                
		                } else {           	
		                	
		                	cleanClose();
		                	
		                	if (!back.isAnglePositive()){             		
		                		          		
		                		Drawable drawable = Drawable.createFromPath("/sdcard/PadKite/Default Theme/backward_pressed_big.png");          			                		
		                		hotTab.setBackgroundDrawable(drawable);
		                		hotTab.invalidate();
		                		hotTab.setHotTitle("Back");            		
		                		drawHotTip();	              		
	                			
		                	}             	
		                	
		                	back.setVisibility(View.INVISIBLE);	
		                	back.setHidden(true);
		                	
		            	}     		    		
			    	
			    	} else if (something instanceof TabButton){		    		
			    	
				    	TabButton child = (TabButton) something;
						
				    	if (child.isHotkey()) {
				    		
				    		int diffHot = BUTTON_RADIUS2+13;
							hotBorder.layout(a-diffHot-(int)1.5, b-inR2-diffHot-(int)10.5, a+diffHot-(int)1.5, b-inR2+diffHot-(int)10.5);
						    		    
						    int diff = BUTTON_RADIUS2-3;
						    int le = a-diff-(int)1.5;
						    int to = b-inR2-diff-(int)10.5;
						    int ri = a+diff-(int)1.5;
						    int bo = b-inR2+diff-(int)10.5;  				
						    hotTab.layout(le, to, ri, bo);
				    		
				    	} else {
				    	
							if (child.getVisibility() != GONE) {  
			        
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
									if (move){
										child.layout(childLeft, childTop, lb, rb);
									}
									
									if (child.isHidden()){
										
										int id = child.getId();							
										child.setHidden(false);						
										int countTabs = BrowserActivity.getTabCount();
										
										cleanClose();									
										
										if (id < (countTabs-2)){			
											
											BrowserActivity.setActiveWebViewIndex(id+4);									
											TabButton previous = (TabButton) getChildAt(id+4);
											BitmapDrawable bd = previous.getBitmapDrawable();
											WebView wv = previous.getWebView();
											String ttl = wv.getTitle();											
											hotTab.setHotTitle(ttl);
											String url = wv.getUrl();
											hotTab.setBackgroundDrawable(bd);
											hotTab.invalidate();
											hotTab.setTabURL(url);
											
										} else if (id == (countTabs-2)) {									
											
											BrowserActivity.setActiveWebViewIndex(id+1);	
											hotTab.applyInit();										
											handler.postDelayed(runnableClose, 2000);
											
										}							
									}
									
									drawHotTip();	
									
								} else {		
											
									
									child.setVisibility(View.INVISIBLE);
									BitmapDrawable bd = child.getBitmapDrawable();
									WebView wv = child.getWebView();
									String ttl = wv.getTitle();											
									hotTab.setHotTitle(ttl);
									String url = wv.getUrl();
									hotTab.setTabURL(url);
									hotTab.setBackgroundDrawable(bd);
									hotTab.invalidate();
									child.setHidden(true);
									
									BrowserActivity.setActiveWebViewIndex(child.getId());		
									
									handler.postDelayed(runnableClose, 2000);
									
									drawHotTip();	
									
								}
							}
						}
			    	}
				}			
	 }	
	
	
	protected void cleanClose(){
		hotTab.setClose(false);
		handler.removeCallbacks(runnableClose);	
		hotBorder.setBackgroundResource(R.drawable.wm_tab_border);
	}
		
	Runnable runnableClose = new Runnable(){		 
		public void run() {		
	  		hotBorder.setBackgroundResource(R.drawable.wm_close);	 
	  		hotTab.setClose(true);
			handler.removeCallbacks(runnableClose);	
			//dontMoveChilds = true;
			handler.postDelayed(runnableHideClose, 4000);
		}					
	};	
	
	Runnable runnableHideClose = new Runnable(){		 
		public void run() {		
			hotBorder.setBackgroundResource(R.drawable.wm_tab_border);
	  		hotTab.setClose(false);
			handler.removeCallbacks(runnableHideClose);
		}					
	};	
	
	protected SwifteeApplication appState;
		
	public void storeWindows(){
		
		int count=getChildCount();
		
		for (int i = 0; i < count; i++) {

	    	View something = getChildAt(i);		
	    	
	    	if (something instanceof TabButton) {				
				
				TabButton tab = (TabButton) getChildAt(i);				
							
				BitmapDrawable bitmapDrawable = tab.getBitmapDrawable();
				Bitmap bitmap = ((BitmapDrawable)bitmapDrawable).getBitmap();
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object			
				
                byte[] image = baos.toByteArray();
                String encodedImage = Base64.encodeBytes(image);
                
                //String encodedImage = Base64.encodeToString(image, Base64.DEFAULT);
                
				WebView wv = tab.getWebView();
				
				String tabTitle = wv.getTitle();
				String url = wv.getUrl();
				
				Log.v("","");
				//SwifteeApplication.getDatabase().insertWindows(tabTitle, url, encodedImage);
				
			
	    	}
		}	
		
	}
		
		
		public void resetMenu(){
						
			int count=getChildCount();
			
			for (int i = 3; i < count; i++) {

		    	View something = getChildAt(i);		
				
				if (something instanceof MenuButton) {
					
					MenuButton but = (MenuButton) getChildAt(i);				
						
					double angle = getBackAngle();
					backBut.setAngle(angle);
					backBut.calculateCenter(a,b,inR3,angle);		

		    		final int childLeft = but.getCenterX() - BUTTON_MENU_RADIUS;
		    		final int childTop = but.getCenterY() - BUTTON_MENU_RADIUS;
		    		final int lb = but.getCenterX() + BUTTON_MENU_RADIUS;
		    		final int rb = but.getCenterY() + BUTTON_MENU_RADIUS;
					
					if(backBut.shouldDraw()) {					
						backBut.layout(childLeft, childTop, lb, rb); 
						backBut.setHidden(false);
					}			
					
					/*invalidate();	
					int[] p = getLocation(backBut);		
					int xB = p[0];
					int yB = p[1];
					int color = FindColor.Find(something, xB, yB);		
					if (color==1){
						
					}*/
					
				} else if (something instanceof TabButton) {				
				
					TabButton tab = (TabButton) getChildAt(i);
					
					if (tab.isHotkey()) {
						
						int diffHot = BUTTON_RADIUS2+13;
						hotBorder.layout(a-diffHot-(int)1.5, b-inR2-diffHot-(int)10.5, a+diffHot-(int)1.5, b-inR2+diffHot-(int)10.5);
					    		    
					    int diff = BUTTON_RADIUS2-3;
					    int le = a-diff-(int)1.5;
					    int to = b-inR2-diff-(int)10.5;
					    int ri = a+diff-(int)1.5;
					    int bo = b-inR2+diff-(int)10.5;  				
					    hotTab.layout(le, to, ri, bo);
					    hotTab.applyInit();	
						
					} else {	
											
						//ImageView tabBorder = tab.getBorderImage();					
						BitmapDrawable bitmapDrawable = tab.getBitmapDrawable();
						
						int t=55;
						double angle = (i-2)*t;
			    		angle= angle - 90;
			    		tab.setAngle(angle);
			    		tab.calculateCenter(a,b,inR,angle);
	
			    		final int childLeft = tab.getCenterX() - BUTTON_RADIUS;
			    		final int childTop = tab.getCenterY() - BUTTON_RADIUS;
			    		final int lb = tab.getCenterX() + BUTTON_RADIUS;
			    		final int rb = tab.getCenterY() + BUTTON_RADIUS;
			    		   		
			    		if(tab.shouldDraw()) {   			
			    			tab.setBackgroundDrawable(bitmapDrawable);
			    			tab.invalidate();
			    			//tabBorder.layout(childLeft-10, childTop-10, lb+10, rb+10);
			    			tab.layout(childLeft, childTop, lb, rb);	    			
			    		}	
					}  		
				
			}                
		}	
	}	
		
	private int[] getLocation(View v){		
		int[] loc = new int[2]; 		
		int w = v.getWidth();
		int h = v.getHeight();
		v.getLocationOnScreen(loc);
		int x = loc[0];
		int y = loc[1];
		int[] pair = new int[2];
		pair[0] = x; pair[1] = y;
		return pair;
	}
		
	int [] resetTops = {-37, 20, 75, 155, 180};	
		
	private double getBackAngle() {
	   double angle = 0;	   
	   int countTabs = BrowserActivity.getTabCount();
	   angle = resetTops[countTabs-1];	
	   return angle;		   
	}
		
		
	public void fling(float velocityX, float velocityY) 
	{
		//Log.d("Scroll X,Y",getScrollX()+","+getScrollY());
		mScroller.fling(getScrollX(), getScrollY(), velocityX, velocityY, 0,320, 0, 320);
		invalidate();
	}

	@Override
	public void computeScroll() {
///		Log.d("INSIDE computeScroll","-----------------------------");
		if (mScroller.computeScrollOffset()) {
			Log.d("INSIDE computeScrolloffset","-----------------------------");
			mAngleChange = mScroller.getAngle();
			int count = getChildCount();
			mAngleChange *= direction;
			if(!canScroll(mAngleChange, count)){
					mScroller.forceFinished(true);
	            	return;
			}
			moveChilds(true);
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
