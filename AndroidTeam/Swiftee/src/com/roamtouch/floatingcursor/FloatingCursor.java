package com.roamtouch.floatingcursor;

import org.metalev.multitouch.controller.MultiTouchController;
import org.metalev.multitouch.controller.MultiTouchController.MultiTouchObjectCanvas;
import org.metalev.multitouch.controller.MultiTouchController.PointInfo;
import org.metalev.multitouch.controller.MultiTouchController.PositionAndScale;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.ClipboardManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import roamtouch.webkit.WebChromeClient;
import roamtouch.webkit.WebHitTestResult;
import roamtouch.webkit.WebVideoInfo;
import roamtouch.webkit.WebView;
import roamtouch.webkit.WebViewClient;
//import roamtouch.webkit.WebView.HitTestResult;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.AdapterView.OnItemClickListener;

import com.roamtouch.view.EventViewerArea;
import com.roamtouch.view.SelectionGestureView;
import com.roamtouch.database.DBConnector;
import com.roamtouch.menu.CircularLayout;
import com.roamtouch.menu.CircularTabsLayout;
import com.roamtouch.menu.MainMenu;
import com.roamtouch.menu.SettingsMenu;
import com.roamtouch.menu.TabButton;
import com.roamtouch.menu.WindowTabs;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import com.roamtouch.swiftee.SwifteeApplication;


public class FloatingCursor extends FrameLayout implements MultiTouchObjectCanvas<FloatingCursor.FCObj> {
	
	
		private DBConnector dbConnector;		
	 	private BrowserActivity mParent;	 	
		private int w = 0, h = 0;		
		/* Maximum jump that is tolerated */
		private final int MAX_JUMP = 128;
		
	/**
	 * Calculate the touching radius for FP 
	 */
		private final float RADIUS_DIP = 120; // 64dip=10mm, 96dip=15mm, 192dip=30mm expressed in DIP
		private final float scale = getContext().getResources().getDisplayMetrics().density;
		private final int RADIUS = (int) (RADIUS_DIP * scale + 0.5f); //Converting to Pixel
		private final int INNER_RADIUS = (int) (RADIUS*0.3f);
			
	/**
	 * 	FloatingCursor child views
	 */
		private FloatingCursorView fcView = null;
		private FloatingCursorInnerView fcPointerView = null;
		//private CircularProgressBar fcProgressBar;
		private ImageView pointer;
		private MainMenu fcMainMenu;
		private SettingsMenu fcSettingsMenu;
		private WindowTabs fcWindowTabs;
		private ZoomWebView zoomView;
//		private ImageView menuBackground;
				
	/**
	 *  integer showing which menu among main,settings and tabs is currently displayed 
	 */
		private ViewGroup currentMenu;

		
		private boolean mIsDisabled = false;
		
		private WebView mWebView = null;
	
	/**
	 * Boundary conditions for scrolling web view with FloatingCursor
	 */
		int mContentWidth,mContentHeight;
	
	/** Application context*/
		private Context mContext;
	
		static final String TAG = "ScrollView";

		static final int ANIMATED_SCROLL_GAP = 250;
		static final float MAX_SCROLL_FACTOR = 0.5f;
	
		private Scroller mScroller;
	
    /**
     * Position of the last motion event.
     */
		private float mLastMotionX;
		private float mLastMotionY;

    /**
     * True if the user is currently dragging this ScrollView around. This is
     * not the same as 'is being flinged', which can be checked by
     * mScroller.isFinished() (flinging begins when the user lifts his finger).
     */
		private boolean mIsBeingDragged = false;
	
    /**
     * Determines speed during touch scrolling
     */
		private VelocityTracker mVelocityTracker;

    /**
     * Area for displaying event information 
     */
		private EventViewerArea eventViewer;
   

		private int mTouchSlop;
		private int mMinimumVelocity;
		private int mMaximumVelocity;

		private boolean mCanBeDisabled = false;
		
		private float mPrevX,mPrevY;
    
		private Handler handler;
		private Runnable runnable;
		private boolean timerStarted = false;//ms

	/**
	 * Vibrator for device vibration	
	 */
		private Vibrator vibrator;
		
		private void initScrollView() {
			mScroller = new Scroller(mContext);
			setFocusable(true);
			setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
			setWillNotDraw(false);
			final ViewConfiguration configuration = ViewConfiguration.get(mContext);
			mTouchSlop = configuration.getScaledTouchSlop();
			mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
			mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
		}
		
		public boolean onInterceptTouchEventFC(MotionEvent ev) {
			/*
			 * This method JUST determines whether we want to intercept the motion.
			 * If we return true, onMotionEvent will be called and we do the actual
			 * scrolling there.
			 */

			/*
			 * Shortcut the most recurring case: the user is in the dragging
			 * state and he is moving his finger.  We want to intercept this
			 * motion.
			 */
			final int action = ev.getAction();
			if ((action == MotionEvent.ACTION_MOVE) && (mIsBeingDragged)) {
				return true;
			}

	        float y = ev.getY();
			float x = ev.getX();
			int touchCount = ev.getPointerCount(); // touchCount == 2 means ACTION!			
	        
	        if (mActivePointerId != INVALID_POINTER_ID  && touchCount >= 2)
	        {
		        final int pointerIndex = ev.findPointerIndex(mActivePointerId);
	        	y = ev.getY(pointerIndex);
	        	x = ev.getX(pointerIndex);
	        }
	     
	        switch (action) {
            	case MotionEvent.ACTION_MOVE:
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */

                /*
                * Locally do absolute value. mLastMotionY is set to the y value
                * of the down event.
                */
                final int yDiff = (int) Math.abs(y - mLastMotionY);
                final int xDiff = (int) Math.abs(x - mLastMotionX);

                if (yDiff > mTouchSlop || xDiff > mTouchSlop) {
                    mIsBeingDragged = true;
                }
                break;

            case MotionEvent.ACTION_DOWN:
                /* Remember location of down touch */
                mLastMotionY = y;
                mLastMotionX = x;

                /*
                * If being flinged and user touches the screen, initiate drag;
                * otherwise don't.  mScroller.isFinished should be false when
                * being flinged.
                */
                mIsBeingDragged = !mScroller.isFinished();
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            	final boolean oldDrag = mIsBeingDragged;
                /* Release the drag */
                mIsBeingDragged = false;
                return oldDrag;
			}

			/*
			 * The only time we want to intercept motion events is if we are in the
			 * drag mode.
			 */
			return mIsBeingDragged;
		}

		public boolean onTouchEventFC(MotionEvent ev) {
    	
			if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
				// Don't handle edge touches immediately -- they may actually belong to one of our
				// descendants.
				return false;
			}	
        
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
			}
			mVelocityTracker.addMovement(ev);

			final int action = ev.getAction();
			
			float y = ev.getY();
			float x = ev.getX();
			int touchCount = ev.getPointerCount(); // touchCount == 2 means ACTION!			
		        
		    if (mActivePointerId != INVALID_POINTER_ID && touchCount >= 2)
		    {
		    	final int pointerIndex = ev.findPointerIndex(mActivePointerId);
		      	y = ev.getY(pointerIndex);
		       	x = ev.getX(pointerIndex);
		    }
       
			//if (action != MotionEvent.ACTION_MOVE)
    		//Toast.makeText(getContext(), "New touch event OV" + action + "," + x + "," + y , Toast.LENGTH_SHORT ).show();
        
			switch (action) {
            	case MotionEvent.ACTION_DOWN:
                /*
                * If being flinged and user touches, stop the fling. isFinished
                * will be false if being flinged.
                */
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                // Remember where the motion event started
                mLastMotionX = x;
                mLastMotionY = y;

                break;
            case MotionEvent.ACTION_MOVE:
                // Scroll to follow the motion event
                final int deltaY = (int) (mLastMotionY - y);
                final int deltaX = (int) (mLastMotionX - x);

                if (Math.abs(deltaX) > 1 || Math.abs(deltaY) > 1)
                {                	
                	mLastMotionY = y;
                	mLastMotionX = x;
 
                	scrollBy(deltaX, deltaY);
                }
                
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int initialVelocityY = (int) velocityTracker.getYVelocity();
                int initialVelocityX = (int) velocityTracker.getXVelocity();
            	//Toast.makeText(mContext, "fling: " + 
            		//	getScrollX() + "," + getScrollY() + "-" + initialVelocityX + "," + initialVelocityY + "-" + 
            			//getWidth() + "," + getHeight(), Toast.LENGTH_SHORT).show();

                if ((Math.abs(initialVelocityY) > mMinimumVelocity) || (Math.abs(initialVelocityX) > mMinimumVelocity)) {
                	fling(-initialVelocityX, -initialVelocityY);
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
			}
			return true;
		}
 
		public void fling(int velocityX, int velocityY) 
		{
			//Toast.makeText(mContext, "fling (2): XY: " + 
    		//	getScrollX() + "," + getScrollY() + "- VXY: " + velocityX + "," + velocityY + "- WH: " + 
    		//	getWidth() + "," + getHeight(), Toast.LENGTH_LONG).show();    	
    	
			/* ****************** CHANGED CODE *******************/
			// FIXME: Remove touch point -> own function
			removeTouchPoint();
			//pointer.setImageResource(R.drawable.no_target_cursor);
			//cancelSelection();
			
			// FIXME: Change so that Size of WebView is adjusted instead of being overlayed
			int r = fcPointerView.getRadius();
			int hd_1 = r;
			int hd_2 = 50 + r;

//			if (currentMenu.getVisibility() == VISIBLE)
	//			hd_2 = -80;

			// FIXME
			/* ****************** CHANGED CODE END *******************/
			mScroller.fling(getScrollX(), getScrollY(), velocityX, velocityY, -w/2 + r , w/2 - r, -h/2 + hd_1, h/2 - hd_2);
			invalidate();
		}
    
		@Override
		public void computeScroll() {
			if (mScroller.computeScrollOffset()) {
				//Toast.makeText(mContext, "scrollTo:" + mScroller.getCurrX() +
				//	 "," + mScroller.getCurrY(), Toast.LENGTH_SHORT).show();
				scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            
				// FIXME: Update cursor image

				// Keep on drawing until the animation has finished.
				postInvalidate();
			}
		}
    
		private void init(Context context)
		{
			// Save Context
			mContext = context;
		
			initScrollView();
		
			// Setup FC Views
			pointer = new ImageView(getContext());
			pointer.setImageResource(R.drawable.kite_cursor);
			pointer.setScaleType(ImageView.ScaleType.CENTER); 
		
			//pointer.setPadding(140, 140, 0, 0);
			//pointer.scrollTo(-140, -140);
        
			fcView = new FloatingCursorView(getContext());
			fcView.setRadius(RADIUS);
	
			removeTouchPoint();
		
			fcPointerView = new FloatingCursorInnerView(getContext());
			fcPointerView.setRadius(INNER_RADIUS);
//			fcPointerView.setQuality(0);
			
		
			//fcProgressBar=new CircularProgressBar(getContext(),(int)(RADIUS*0.3f)+20);

			fcMainMenu = new MainMenu(context);
			fcMainMenu.setfcRadius(RADIUS);
			fcMainMenu.setVisibility(INVISIBLE);
			fcMainMenu.setFloatingCursor(this);
			currentMenu = fcMainMenu;
			
			fcSettingsMenu =  new SettingsMenu(context);
			fcSettingsMenu.setfcRadius(RADIUS);
			fcSettingsMenu.setVisibility(INVISIBLE);
			fcSettingsMenu.setFloatingCursor(this);
			
			fcWindowTabs = new WindowTabs(context);
			fcWindowTabs.setfcRadius(RADIUS);
			fcWindowTabs.setVisibility(INVISIBLE);
			fcWindowTabs.setFloatingCursor(this);
			
			zoomView = new ZoomWebView(context);
			zoomView.setFloatingCursor(this);
			zoomView.setFCRadius(RADIUS);
			zoomView.setVisibility(INVISIBLE);
			

			addView(fcView);
			//addView(fcProgressBar);
			addView(fcPointerView);
			addView(pointer);
			addView(fcMainMenu);
			addView(fcSettingsMenu);
			addView(fcWindowTabs);
			addView(zoomView);
			
			vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
			
			handler = new Handler();
			runnable = new Runnable(){

				public void run() {
					if(currentMenu.getVisibility() == VISIBLE && timerStarted){
						toggleMenuVisibility();	
						timerStarted = false;
					}
					else {
						timerStarted = true;
						handler.postDelayed(this,10000);
					}
				}
				
			};
		}


		public FloatingCursor(Context context, AttributeSet attrs) {
			super(context, attrs);
			init(context);
			
			SwifteeApplication app = (SwifteeApplication)context.getApplicationContext();
			dbConnector = app.getDatabase();
		}
        public String getCurrentURL(){
        	return mWebView.getUrl();
        }
        public String getCurrentTitle(){
        	return mWebView.getTitle();
        }
		public void gestureDisableFC()
		{
			//mCanBeDisabled  = true;
			// Before that works, we need to make sure that the hit area is no longer clicked on
			//fcView.setVisibility(View.INVISIBLE);
			//disableFC();

			mIsDisabled = true;
			stopHitTest(0,0, true);
			this.setVisibility(View.INVISIBLE);	
		}
		
		public void gestureEnableFC()
		{
			mCanBeDisabled = false;
			//fcView.setVisibility(View.VISIBLE);

			if (mIsDisabled)
			{
				fcView.setVisibility(View.VISIBLE);
				removeTouchPoint();
				removeSelection();
				enableFC();
			}
		}

		public void disableFC()
		{
			mIsDisabled = true;
			cancelSelection();
			stopHitTest(0,0, true);
			this.setVisibility(View.INVISIBLE);	
		}
	
		public void enableFC()
		{
			mIsDisabled = false;
			this.setVisibility(View.VISIBLE);
 		}
	
		public void setWebView(WebView wv,boolean isFirst) {
			/*
			 * setTab() method is called only when isFirst = true.
			 */
			if(isFirst)
				fcWindowTabs.setTab(wv);
			mWebView = wv;
			mWebView.setDrawingCacheEnabled(true);
			mWebView.setWebChromeClient(new WebClient());
			mWebView.setWebViewClient(new GestureWebViewClient());	
			
			fcMainMenu.setBackEabled(mWebView.canGoBack());
			fcMainMenu.setFwdEabled(mWebView.canGoForward());
		}
		
		public WebView getWebView(){
			return mWebView;
		}
		public void setEventViewerArea(EventViewerArea eventViewer) {
			this.eventViewer = eventViewer;
			eventViewer.setWindowTabs(fcWindowTabs);
			fcMainMenu.setEventViewer(eventViewer);
			fcWindowTabs.setEventViewer(eventViewer);
		}

		public void setParent(BrowserActivity p){
			mParent = p;
			fcMainMenu.setParent(mParent);
			fcSettingsMenu.setParent(mParent);
			fcWindowTabs.setParent(mParent);
		}
		
		/**
		 * 
		 * @param index is menu index currently displayed such as 
		 * 0 for main menu,1 for settings menu,2 for window tabs
		 */
		public void setCurrentMenu(int index){
			
			switch(index){
			case 0: currentMenu = fcMainMenu;
					fcMainMenu.setVisibility(VISIBLE);
					fcSettingsMenu.setVisibility(INVISIBLE);
					fcWindowTabs.setVisibility(INVISIBLE);
					break;
			case 1: currentMenu = fcSettingsMenu;
					fcSettingsMenu.setVisibility(VISIBLE);
					fcMainMenu.setVisibility(INVISIBLE);
					fcWindowTabs.setVisibility(INVISIBLE);
					break;
			case 2: currentMenu = fcWindowTabs;
					fcWindowTabs.setVisibility(VISIBLE);
					fcSettingsMenu.setVisibility(INVISIBLE);
					fcMainMenu.setVisibility(INVISIBLE);
					break;
		
			}
			
		}

		public void addNewWindow(){
			fcWindowTabs.addWindow(selectedLink);
			selectedLink = "";
		}

		public int getWindowCount(){
			return fcWindowTabs.getWindowCount();
		}
		
		protected void checkFCMenuBounds()
		{
			final int r = fcView.getRadius();

			int dx = 0;
			int dy = 0;
						
			if (fcX - r < 0)
				dx = fcX - r;
			else if (fcX + r > this.w)
				dx = (fcX + r)-this.w;

			if (fcY - (r + 50) < 0)
				dy = fcY - (r + 50);
			else if (fcY + r > this.h)
				dy = (fcY + r) - this.h;
			
			// Abort fling animation
			mScroller.forceFinished(true);
			scrollBy(dx, dy);
			
			// Update fc coordinates
			fcX = -(int)pointer.getScrollX() + -(int)getScrollX() + w/2;
			fcY = -(int)pointer.getScrollY() + -(int)getScrollY() + h/2;
			invalidate();
		}
		
		public boolean isMenuVisible()
		{
			return (currentMenu.getVisibility() == VISIBLE);
		}
		
		public void hideMenuFast() {
			eventViewer.setMode(EventViewerArea.TEXT_ONLY_MODE);
			currentMenu.setVisibility(INVISIBLE);
			fcView.setVisibility(View.VISIBLE);
			mMenuDown = false;

			currentMenu = fcMainMenu;

			// Reset FC
			pointer.setImageResource(R.drawable.kite_cursor);
			removeTouchPoint();
		}
		
		protected boolean animationLock = false;

		public void toggleMenuVisibility(){
			
			if (animationLock)
				return;
			
			animationLock = true;
			
			eventViewer.setMode(EventViewerArea.TEXT_ONLY_MODE);
			AlphaAnimation menuAnimation;
			
			// Reset FC
			removeTouchPoint();

			if(currentMenu.getVisibility() == INVISIBLE){

				pointer.setImageResource(R.drawable.kite_cursor);
				mMenuDown = true;
				fcView.setVisibility(View.INVISIBLE);
				checkFCMenuBounds();
				
				menuAnimation = new AlphaAnimation(0.0f, 1.0f);
				menuAnimation.setDuration(250);
				menuAnimation.setAnimationListener(new AnimationListener(){

					public void onAnimationEnd(Animation animation) {
						currentMenu.setVisibility(VISIBLE);
						animationLock = false;
						handler.postDelayed(runnable, 10000);
					}
					public void onAnimationRepeat(Animation animation) {}

					public void onAnimationStart(Animation animation) {}
					
				});
				currentMenu.startAnimation(menuAnimation);
				vibrator.vibrate(25);
				if (currentMenu instanceof CircularLayout)
					eventViewer.setText(((CircularLayout)currentMenu).getName());
				else if (currentMenu instanceof CircularTabsLayout)
					eventViewer.setText(((CircularTabsLayout)currentMenu).getName());
				
				//				mParent.setTopBarVisibility(VISIBLE);
//				mParent.setTopBarMode(TopBarArea.ADDR_BAR_MODE);
			}
			else if(currentMenu.getVisibility() == VISIBLE){

				mMenuDown = false;
				pointer.setImageResource(R.drawable.kite_cursor);
				fcView.setVisibility(VISIBLE);

				menuAnimation = new AlphaAnimation(1.0f, 0.0f);
				menuAnimation.setDuration(250);
				menuAnimation.setAnimationListener(new AnimationListener(){
					
					public void onAnimationEnd(Animation animation) {
						currentMenu.setVisibility(INVISIBLE);
						currentMenu = fcMainMenu;
						animationLock = false;
						handler.removeCallbacks(runnable);
					}
					
					public void onAnimationRepeat(Animation animation) {}

					public void onAnimationStart(Animation animation) {}
					
				});
				currentMenu.startAnimation(menuAnimation);
				vibrator.vibrate(25);
				//eventViewer.setText("");

//				mParent.setTopBarVisibility(INVISIBLE);
			}		
		}
		
		@Override 
		protected void onSizeChanged(int w, int h, int oldw, int oldh) { 
			super.onSizeChanged(w, h, oldw, oldh);
			fcView.setPosition(w/2,h/2);
			fcPointerView.setPosition(w/2,h/2);
			//fcProgressBar.setPosition(w/2, h/2);
			fcMainMenu.setPosition(w/2, h/2);
			this.w=w;
			this.h=h;
			scrollTo(0,0);
			Log.d("OnSizeChanged:(w,h)","("+w+","+h+")" );
		}

		private boolean mHandleTouch = false;
	
	
		// FIXME: TODO: Put this into their own classes
//		private boolean mSelectionMode = false;
		private boolean mHitTestMode = false;
	
		private boolean mSelectionStarted = false;
		private int selX = -1, selY = -1;
		private boolean mGesturesEnabled = false;

	
		private WebHitTestResult mWebHitTestResult ;
		private int mWebHitTestResultType = -1 ;
		private int mWebHitTestResultIdentifer = -1;
		
		protected void sendEvent(int action, int X, int Y)
		{
			long downTime = SystemClock.uptimeMillis();
			long eventTime = SystemClock.uptimeMillis();

			MotionEvent event = MotionEvent.obtain(downTime, eventTime,action, X, Y, 0);
			mWebView.onTouchEvent(event);
			event.recycle();
			event = null;
		}
	
		protected void stopHitTest(int X, int Y, boolean setIcon)
		{
			if (mHitTestMode)
			{
				// FIXME: ?
				//sendEvent(MotionEvent.ACTION_MOVE, X, Y);
				//sendEvent(MotionEvent.ACTION_UP, 0, 0);
				
				sendEvent(MotionEvent.ACTION_CANCEL, X, Y);
				if (setIcon)
					pointer.setImageResource(R.drawable.kite_cursor);

				mWebHitTestResultIdentifer = -1;
				mHitTestMode = false;
			}
		}
	
//		private boolean mEditTextCancel = false;
		protected void moveHitTest(int X, int Y)
		{
			if (mHitTestMode)
			{
//				sendEvent(MotionEvent.ACTION_DOWN, X, Y);

				mWebHitTestResult = mWebView.getHitTestResultAt(X,Y);
				int resultType = mWebHitTestResult.getType();
				
				int identifier = mWebHitTestResult.getIdentifier();
				
			
				int cursorImage = 0;
			
			switch (resultType) {
				
				case WebHitTestResult.TEXT_TYPE: {
					cursorImage = R.drawable.text_cursor;
					//eventViewer.splitText(WebHitTestResult.TEXT_TYPE,"");
					break;
				}
				case WebHitTestResult.VIDEO_TYPE: {
					cursorImage = R.drawable.video_cursor;
					WebVideoInfo videoInfo = mWebHitTestResult.getVideoInfo();					
					//eventViewer.splitText(WebHitTestResult.VIDEO_TYPE, "");
					break;
				}
				
				case WebHitTestResult.ANCHOR_TYPE: {
					resultType = WebHitTestResult.ANCHOR_TYPE;
					cursorImage = R.drawable.link_cursor;
					String tooltip = mWebHitTestResult.getToolTip();
					if (tooltip.length() > 5)
						eventViewer.splitText(WebHitTestResult.ANCHOR_TYPE,tooltip);
	/*				if(shouldLinkExec){
						mWebView.loadUrl(extra);
						shouldLinkExec = false; 
					}  */
					break;
				}

				case WebHitTestResult.EDIT_TEXT_TYPE: {
					cursorImage = R.drawable.keyboard_cursor;
					break;
				}
			
				case WebHitTestResult.SRC_IMAGE_ANCHOR_TYPE:
				case WebHitTestResult.SRC_ANCHOR_TYPE: 
				{
					resultType = WebHitTestResult.ANCHOR_TYPE;
					cursorImage = R.drawable.link_cursor;
					String tooltip = mWebHitTestResult.getToolTip();
					if (tooltip.length() > 5)
						eventViewer.splitText(WebHitTestResult.ANCHOR_TYPE,tooltip);
	
					break;
				}
	
				case WebHitTestResult.IMAGE_ANCHOR_TYPE: {
					resultType = WebHitTestResult.IMAGE_TYPE;
					cursorImage = R.drawable.image_cursor;

					String tooltip = mWebHitTestResult.getToolTip();
					if (tooltip.length() > 5)
						eventViewer.splitText(WebHitTestResult.ANCHOR_TYPE,tooltip);
					break;
				}
	
				case WebHitTestResult.IMAGE_TYPE: {
					cursorImage = R.drawable.image_cursor;
		//			eventViewer.splitText(WebHitTestResult.IMAGE_TYPE,"");
					break;
				}
				default: {
					resultType = -1;
					cursorImage = R.drawable.no_target_cursor;

					// FIXME
					//eventViewer.splitText(-1,"");
					break;
				}
			}
			
			/*if (mHitTestResult == HitTestResult.EDIT_TEXT_TYPE && mEditTextCancel == false)
			{
				sendEvent(MotionEvent.ACTION_CANCEL, X, Y);
				mEditTextCancel = true;
			}
			else
				mEditTextCancel = false;*/

				pointer.setImageResource(cursorImage);
				
				// Was there a node change?

				if (identifier != mWebHitTestResultIdentifer) {
					if (resultType == WebHitTestResult.ANCHOR_TYPE)
						mWebView.focusNodeAt(X,Y);
					else if (mWebHitTestResultType == WebHitTestResult.ANCHOR_TYPE)
						sendEvent(MotionEvent.ACTION_CANCEL, X, Y); // FIXME: Use proper API for that
				}
				
				mWebHitTestResultType = resultType;
				mWebHitTestResultIdentifer = identifier;

	//			else
//					focusNodeAt(-1,-1);					
			}
		}

		protected void startHitTest(int X, int Y)
		{
			if (!mHitTestMode)
			{
				mHitTestMode = true;
				moveHitTest(X,Y);
			}
		}
	
		private void removeTouchPoint()
		{
		
			if (fcPointerView != null)
			{
				pointer.scrollTo(0,0);
				fcPointerView.scrollTo(0,0);
				//fcProgressBar.scrollTo(0,0);
			}

			mTouchPointValid = false;
		}
	
		public void onPageFinished() {
			pointer.setImageResource(R.drawable.kite_cursor);
			removeTouchPoint();
		}
		
		public void enableGestures()
		{
			mGesturesEnabled = true;
		}

		public void disableGestures()
		{
			mGesturesEnabled = false;
		}

		/* public interface */
		
		public void removeSelection()
		{
			mWebView.executeSelectionCommand(fcX, fcY, WebView.CLEAR_SELECTION);
		}
		
		public void startSelectionCommand()
		{
			startHitTest(fcX,fcY);
			mWebView.executeSelectionCommand(fcX, fcY, WebView.START_SELECTION);		
		}
		
		public void executeSelectionCommand(int cmd)
		{
			mWebView.executeSelectionCommand(fcX, fcY, cmd);
		}
	
		public void stopSelectionCommand()
		{
			mWebView.executeSelectionCommand(fcX, fcY, WebView.STOP_SELECTION);
			mWebView.executeSelectionCommand(fcX, fcY, WebView.COPY_TO_CLIPBOARD);
		}
		
		public void onClick()
		{
			mLongTouchEnabled = false;
			executeSelectionCommand(WebView.STOP_SELECTION);

			if (mWebHitTestResult.getType() == WebHitTestResult.ANCHOR_TYPE || mWebHitTestResult.getType() == WebHitTestResult.SRC_ANCHOR_TYPE || mWebHitTestResult.getType() == WebHitTestResult.SRC_IMAGE_ANCHOR_TYPE)
			{
				eventViewer.setText("Executing link ...");

				sendEvent(MotionEvent.ACTION_DOWN, fcX, fcY);
				//pointer.setImageResource(R.drawable.address_bar_cursor);
				sendEvent(MotionEvent.ACTION_UP, fcX, fcY);		
				startHitTest(fcX,fcY);
			}
			else if(mWebHitTestResult.getType() == WebHitTestResult.EDIT_TEXT_TYPE || mWebHitTestResult.getType() == WebHitTestResult.UNKNOWN_TYPE || mWebHitTestResult.getType() == -1){
				eventViewer.setText("Clicking ...");

				sendEvent(MotionEvent.ACTION_DOWN, fcX, fcY);
				//pointer.setImageResource(R.drawable.address_bar_cursor);
				sendEvent(MotionEvent.ACTION_UP, fcX, fcY);	
			}
			else {
				
				if (mWebHitTestResult.getType() == WebHitTestResult.IMAGE_TYPE)
				{
					eventViewer.setText("Selecting image ...");
				
					mWebView.executeSelectionCommand(fcX, fcY, WebView.SELECT_OBJECT);
					mWebView.executeSelectionCommand(fcX, fcY, WebView.COPY_HTML_FRAGMENT_TO_CLIPBOARD);
				}
				else if (mWebHitTestResult.getType() == WebHitTestResult.TEXT_TYPE)
				{
					eventViewer.setText("Selecting word ...");

					mWebView.executeSelectionCommand(fcX, fcY, WebView.SELECT_WORD);
					mWebView.executeSelectionCommand(fcX, fcY, WebView.COPY_TO_CLIPBOARD);
				}
				
				startSelection(false);
			}
		}
		
		public void onAutoSelectionStart(boolean restart)
		{
			// Nothing for now
		}

		public void onAutoSelectionEnd()
		{
			// Copy selection to clipboard and such activate gestures
			stopSelectionCommand();
			startSelection(false);
		}

		boolean mLongTouchEnabled = false;
		private String selectedLink = "";
		
		public void onLongTouch() 
		{			
			if (mWebHitTestResult.getType() == WebHitTestResult.IMAGE_TYPE)
			{
				eventViewer.setText("Detected Long-Touch. Selecting image ...");
			
				mWebView.executeSelectionCommand(fcX, fcY, WebView.SELECT_OBJECT);
				mWebView.executeSelectionCommand(fcX, fcY, WebView.COPY_HTML_FRAGMENT_TO_CLIPBOARD);
				mLongTouchEnabled = true;
			}
			else if (mWebHitTestResult.getType() == WebHitTestResult.TEXT_TYPE)
			{
				eventViewer.setText("Detected Long-Touch. Selecting word ...");
				
				mWebView.executeSelectionCommand(fcX, fcY, WebView.SELECT_WORD);
				mWebView.executeSelectionCommand(fcX, fcY, WebView.COPY_TO_CLIPBOARD);
				mLongTouchEnabled = true;
			}
			else if ( mWebHitTestResult.getType() == WebHitTestResult.ANCHOR_TYPE || mWebHitTestResult.getType() == WebHitTestResult.SRC_ANCHOR_TYPE || mWebHitTestResult.getType() == WebHitTestResult.SRC_IMAGE_ANCHOR_TYPE)
			{
				eventViewer.setText("Detected Long-Touch. Selecting link ...");
				selectedLink = mWebHitTestResult.getExtra();
				mWebView.executeSelectionCommand(fcX, fcY, WebView.SELECT_LINK);
				((ClipboardManager) mParent.getSystemService(Context.CLIPBOARD_SERVICE)).setText(selectedLink);
				mParent.setSelection(selectedLink);
				//mWebView.executeSelectionCommand(fcX, fcY, WebView.COPY_TO_CLIPBOARD);
				mLongTouchEnabled = true;
			}
		}
		
		public void onLongTouchUp() 
		{
			if (!mLongTouchEnabled)
			{
				onClick();
				return;
			}
			
			mLongTouchEnabled = false;

			stopSelectionCommand();
			startSelection(true);
			
//Added for distinguishing various selection
			
			if (mWebHitTestResult.getType() == WebHitTestResult.IMAGE_TYPE)
				mParent.setGestureType(SwifteeApplication.CURSOR_IMAGE_GESTURE);
			else if (mWebHitTestResult.getType() == WebHitTestResult.TEXT_TYPE)
				mParent.setGestureType(SwifteeApplication.CURSOR_TEXT_GESTURE);
			else if ( mWebHitTestResult.getType() == WebHitTestResult.ANCHOR_TYPE || mWebHitTestResult.getType() == WebHitTestResult.SRC_ANCHOR_TYPE || mWebHitTestResult.getType() == WebHitTestResult.SRC_IMAGE_ANCHOR_TYPE)
			{
				int type = getLinkType();
				if(type == 0)
					mParent.setGestureType(SwifteeApplication.CURSOR_LINK_GESTURE);
				else if(type == 1)
					mParent.setGestureType(SwifteeApplication.CURSOR_IMAGE_GESTURE);
				else 
					mParent.setGestureType(SwifteeApplication.CURSOR_VIDEO_GESTURE);

				stopSelection();
				disableGestures();
				
				mParent.setSelection(selectedLink);
				mParent.startGesture(false);
			}
		}
		/**
		 * checks for link type and returns whether it is of type image or video
		 * return 1 for image type 
		 * return 2 for video type
		 * else return 0
		 * @return
		 */
		private int getLinkType(){
				if(
					selectedLink.endsWith(".mp4") || 
					selectedLink.endsWith(".flv") || 
					selectedLink.endsWith(".mpeg")||
					selectedLink.endsWith(".wmv")||
					selectedLink.endsWith(".mpg")||
					selectedLink.endsWith(".rm")||
					selectedLink.endsWith(".mov"))
				return 2;
				if(
					selectedLink.endsWith(".jpg") || 
					selectedLink.endsWith(".jpeg") || 
					selectedLink.endsWith(".png")||
					selectedLink.endsWith(".gif")||
					selectedLink.endsWith(".bmp"))
				return 1;
			return 0;
		}
		public void onTouchUp()
		{

/*			if(mWebHitTestResult.getType() == WebHitTestResult.EDIT_TEXT_TYPE){
				sendEvent(MotionEvent.ACTION_DOWN, fcX, fcY);
				//pointer.setImageResource(R.drawable.address_bar_cursor);
				sendEvent(MotionEvent.ACTION_UP, fcX, fcY);	
			}
		
			if (mWebHitTestResult.getType() == WebHitTestResult.ANCHOR_TYPE || mWebHitTestResult.getType() == WebHitTestResult.SRC_ANCHOR_TYPE || mWebHitTestResult.getType() == WebHitTestResult.SRC_IMAGE_ANCHOR_TYPE)
			{
			/*	sendEvent(MotionEvent.ACTION_DOWN, fcX, fcY);
				pointer.setImageResource(R.drawable.address_bar_cursor);
				sendEvent(MotionEvent.ACTION_UP, fcX, fcY);/
			}
			else {
				sendEvent(MotionEvent.ACTION_DOWN, fcX, fcY);
				sendEvent(MotionEvent.ACTION_UP, fcX, fcY);	
			}*/
			
			pointer.setImageResource(R.drawable.kite_cursor);
			
		}
		
		protected boolean mMovableSelection = false;
		protected boolean mSelectionActive = false;
		protected boolean mSelectionMoved = false; 
		
		protected void startSelection(boolean movable)
		{
			mMovableSelection = movable;
			mSelectionActive = true;
			if (movable)
			{
				mSelectionStarted = false;
				selX = fcX;
				selY = fcY;
			}
		}

		protected void stopSelection()
		{
			if (!mSelectionActive)
				return;

			mSelectionActive = false;
   			mMovableSelection = false;
			mSelectionStarted = false;

			enableGestures();

			mWebView.executeSelectionCommand(fcX, fcY, WebView.STOP_SELECTION);
			mWebView.executeSelectionCommand(fcX, fcY, WebView.COPY_TO_CLIPBOARD);
		}

		protected void stopSelectionMovable()
		{
			if (!mSelectionActive)
				return;

			mMovableSelection = false;
			mSelectionStarted = false;
		}
		
				
		protected void moveSelection()
		{
			if (!mMovableSelection)
				return;

			if (!mSelectionStarted)
			{
                final int yDiff = (int) Math.abs(fcY - selY);
                final int xDiff = (int) Math.abs(fcX - selX);

				if (yDiff > mTouchSlop || xDiff > mTouchSlop)
				{
					eventViewer.setText("Please select now more text with the FC ...");
					mSelectionStarted = true;
					stopHitTest(fcX,fcY,true);
					pointer.setImageResource(R.drawable.text_cursor);
				}
			}
			else
				mWebView.executeSelectionCommand(fcX, fcY, WebView.EXTEND_SELECTION);
		}
		
		protected void cancelSelection()
		{
			if (!mSelectionActive)
				return;
		
			mSelectionActive = false;
			mSelectionStarted = false;
			
			mWebView.executeSelectionCommand(fcX, fcY, WebView.CLEAR_SELECTION);
		}
		
		/*

		protected void clickSelection(int X, int Y)
		{
			if (mWebHitTestResult == null)
				return;
			
			//Toast.makeText(mContext, "Clicking sel ..." + mWebHitTestResult.getType(), Toast.LENGTH_LONG).show();

			if (mWebHitTestResult.getType() == WebHitTestResult.ANCHOR_TYPE || mWebHitTestResult.getType() == WebHitTestResult.SRC_ANCHOR_TYPE || mWebHitTestResult.getType() == WebHitTestResult.SRC_IMAGE_ANCHOR_TYPE)
			{
//				Toast.makeText(mContext, "Clicking link ...", Toast.LENGTH_LONG).show();
/**
				sendEvent(MotionEvent.ACTION_DOWN, X, Y);
				pointer.setImageResource(R.drawable.address_bar_cursor);
				sendEvent(MotionEvent.ACTION_UP, X, Y);		
				startHitTest(X,Y); * /
				
				mWebView.focusNodeAt(X,Y);

				mGesturesEnabled = true;
				mWebView.executeSelectionCommand(X, Y, WebView.START_SELECTION);
				mWebView.executeSelectionCommand(X, Y, WebView.STOP_SELECTION);
				mWebView.executeSelectionCommand(X, Y, WebView.SELECT_WORD_OR_LINK);
				mWebView.executeSelectionCommand(X, Y, WebView.COPY_TO_CLIPBOARD);		
//				mParent.startGesture(SwifteeApplication.CURSOR_LINK_GESTURE);
			}
			if(mWebHitTestResult.getType() == WebHitTestResult.EDIT_TEXT_TYPE){
				sendEvent(MotionEvent.ACTION_DOWN, X, Y);
				//pointer.setImageResource(R.drawable.address_bar_cursor);
				sendEvent(MotionEvent.ACTION_UP, X, Y);	
			}
			else if (mWebHitTestResult.getType() == WebHitTestResult.IMAGE_TYPE)
			{
				eventViewer.setText("Selecting image ...");
				
				mGesturesEnabled = true;
				mWebView.executeSelectionCommand(X, Y, WebView.SELECT_OBJECT);
				mWebView.executeSelectionCommand(X, Y, WebView.COPY_HTML_FRAGMENT_TO_CLIPBOARD);
				//pointer.setImageResource(R.drawable.address_bar_cursor);
				//removeTouchPoint();

			// FIXME: Add Downloading of image
			}
			else if (mWebHitTestResult.getType() == WebHitTestResult.TEXT_TYPE)
			{	
				eventViewer.setText("Selecting word ...");

				mGesturesEnabled = true;
				mWebView.executeSelectionCommand(X, Y, WebView.SELECT_WORD_OR_LINK);
				mWebView.executeSelectionCommand(X, Y, WebView.COPY_TO_CLIPBOARD);	
				
				pointer.setImageResource(R.drawable.no_target_cursor);
				// Re-start HitTest functionality
				startHitTest(X,Y);
//				mParent.startGesture(SwifteeApplication.CURSOR_TEXT_GESTURE);
				//removeTouchPoint();

				// This is called by onClipBoardUpdate changed if mGesturesEnabled is true
				// mParent.startGesture();	
			}
		}
	
		protected void cancelSelection()
		{
			sendEvent(MotionEvent.ACTION_CANCEL, selX, selY);
		}
	
		protected void stopSelection(int X, int Y)
		{
			if (mSelectionMode)
			{
				mSelectionMode = false;
			
				if (!mSelectionStarted)
					clickSelection(X,Y);
				else
				{
					sendEvent(MotionEvent.ACTION_UP, X, Y);
					pointer.setImageResource(R.drawable.no_target_cursor);
					mParent.startGesture(SwifteeApplication.CURSOR_TEXT_GESTURE);			
				}
				removeTouchPoint();
			}
		}
		
		protected void checkClickSelection(int X, int Y)
		{
			if (mSelectionMode)
			{
				if (!mSelectionStarted)
				{
					clickSelection(X,Y);
					mSelectionMode = false;
				}
			}
		}
	
		protected void moveSelection(int X, int Y)
		{
			if (mSelectionMode)
			{
				if (!mSelectionStarted)
				{
	                final int yDiff = (int) Math.abs(Y - selY);
	                final int xDiff = (int) Math.abs(X - selX);

					if (yDiff > mTouchSlop || xDiff > mTouchSlop)
					{
						// FIXME: Change to Velu's selection API
						pointer.setImageResource(R.drawable.text_cursor);
						mWebView.onKeyDown(KeyEvent.KEYCODE_SHIFT_LEFT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT));
						sendEvent(MotionEvent.ACTION_DOWN, X, Y);
						//mWebView.executeSelectionCommand(X, Y, WebView.SELECT_WORD_OR_LINK);
						mSelectionStarted = true;
					}
				}
				else		
					sendEvent(MotionEvent.ACTION_MOVE, X, Y);
			}
		}

		protected void startSelection(int X, int Y)
		{
			if (!mSelectionMode)
			{
				mSelectionStarted = false;
				mSelectionMode = true;
				selX = X;
				selY = Y;
				eventViewer.setText("Start selection gesture now ...");
			}
		}*/
	
		private boolean mTouchPointValid = false;
	
		private static final int INVALID_POINTER_ID = -1;
		
		private int mActivePointerId = INVALID_POINTER_ID;
		
		public boolean dispatchKeyEventFC(KeyEvent event) {
			
			//Toast.makeText(mContext, "KeyEvent: " + event.getAction(), Toast.LENGTH_LONG).show();
			
			if ( event.getAction() == KeyEvent.ACTION_DOWN)
			{
					stopHitTest(fcX,fcY,false);
					//startSelection(fcX, fcY);
			}
			else if (event.getAction() == KeyEvent.ACTION_UP)
			{
				//checkClickSelection(fcX, fcY);				
			}
			
			return true;			
		}

		int fcX = 0, fcY = 0;

		int mPrevMoveX = 0, mPrevMoveY = 0;
		boolean mMoveFrozen = false;
		
		boolean mForwardTouch = false;
		boolean mMenuDown = false;
		int mOldTouchCount = 0;
		
		public boolean dispatchTouchEventFC(MotionEvent event) {
			
			int action = event.getAction() & MotionEvent.ACTION_MASK;
			
			boolean status;

			int X,Y;		
			int touchCount = event.getPointerCount(); // touchCount == 2 means ACTION!			

	        if (mActivePointerId != INVALID_POINTER_ID && touchCount >= 2)
	        {
		        final int pointerIndex = event.findPointerIndex(mActivePointerId);
		        
	        	X=(int)event.getX(pointerIndex);
	        	Y=(int)event.getY(pointerIndex);
	        }
	        else
	        {
	        	X=(int)event.getX();
	        	Y=(int)event.getY();
	        }
				
			if (mIsDisabled)
				return false;  

			fcX = -(int)pointer.getScrollX() + -(int)getScrollX() + w/2;
			fcY = -(int)pointer.getScrollY() + -(int)getScrollY() + h/2;

			if (mForwardTouch)
			{
				if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)
				{
					mForwardTouch = false;
					fcView.setVisibility(View.VISIBLE);
				}

				mWebView.dispatchTouchEvent(event);

				return true;
			}

			//Log.d("dispatchTouchEventFC", "X,Y,action" + X + "," + Y + "," + action);

			if (mMoveFrozen)
			{
				// We continue the movement from MT
				if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP)
				{
					float xs[] = new float[2];
					float ys[] = new float[2];
					float pressure[] = new float[2];
					int ptIdxs[] = new int[2];
					
					xs[0] = X;
					xs[1] = X;

					ys[0] = Y;
					ys[1] = Y;
					
					ptIdxs[0] = 0;
					ptIdxs[1] = 0;
					
					pressure[0] = pressure[1] = event.getPressure();
 
					// FIXME: Performance
					PointInfo pt = new PointInfo();

					pt.set(2, xs, ys, pressure, ptIdxs, action, action!=MotionEvent.ACTION_UP, event.getEventTime());
					mSelectionGestures.dispatchTouchEventMT(pt, action);
				}

				if (action == MotionEvent.ACTION_UP)
					mMoveFrozen = false;
								
				if (action == MotionEvent.ACTION_MOVE)
					return true;
			}
			
			if (action == MotionEvent.ACTION_MOVE && mOldTouchCount == 2 && touchCount == 1)
			{		
				int dMX = Math.abs(X-mPrevMoveX);
				int dMY = Math.abs(Y-mPrevMoveY);

				if (dMX >= MAX_JUMP || dMY >= MAX_JUMP)
				{
					action = MotionEvent.ACTION_UP;
					mMoveFrozen = true;
					this.setVisibility(View.INVISIBLE);	
					mHandleTouch = false; // Don't let user drag at this stage
					return true;
				}
			}

			if (action == MotionEvent.ACTION_MOVE)
			{
				mOldTouchCount = touchCount;
				
				mPrevMoveX = X;
				mPrevMoveY = Y;
			}

			// MT stuff
			if (!mMenuDown)
			{
				status = multiTouchController.onTouchEvent(event);
			
				if (status)
					return true; // Got handled by MT Controller
			}
			
			// MT stuff end
			
			if (action == MotionEvent.ACTION_DOWN)
			{
				
				timerStarted = false;
				handler.removeCallbacks(runnable);
				
				mPrevX = X;
				mPrevY = Y;
				mPrevMoveX = X;
				mPrevMoveY = Y;
				mMoveFrozen = false;

				mActivePointerId = event.getPointerId(0);
				
				//Toast.makeText(mContext, "mActivePointerId: " + mActivePointerId, 100).show();

				final int CircleX = -(int)getScrollX() + w/2;
				final int CircleY = -(int)getScrollY() + h/2;

				final int r = fcView.getRadius();
				
				// Check for inner circle click and show Circular menu
				final int innerCirRad = (int)(fcPointerView.getRadius() * 0.6f);
				
				// We need to factor the inner circle relocation so
				// it does not get out of the outer circle
				// Fixed Size: 60 % from our radius
				final float radFact = 0.6f*r; //(float)(2*innerCirRad) / (float)r;

				//Toast.makeText(mContext, "radFact: " + radFact, 100).show();
				
				final int innerCircleX = -(int)fcPointerView.getScrollX() + CircleX;
				final int innerCircleY = -(int)fcPointerView.getScrollY() + CircleY;

				int scrollX = X - CircleX;
				int scrollY = Y - CircleY;
				double length = Math.hypot(scrollX, scrollY);
					
				if(X > innerCircleX-innerCirRad && X < innerCircleX+innerCirRad && Y > innerCircleY-innerCirRad && Y < innerCircleY+innerCirRad){
					//Toast.makeText(mContext, "Circular Menu", 100).show();
					if (isCircularZoomEnabled())
						disableCircularZoom();
					else
						toggleMenuVisibility();
					
					mHandleTouch = false; // FIXME: Change, do let user drag and fling menu
				}
				else if(isCircularZoomEnabled()){
					zoomView.onTouchEvent(event);
					return true;
				}
				/*else if ((X < CircleX-r || X > CircleX+r || Y < CircleY-r || Y > CircleY+r) && mScroller.isFinished())*/
				else if ((length >= (r*1.1f)) && mScroller.isFinished())
				{		
					fcView.setVisibility(View.INVISIBLE);
					removeTouchPoint();
					
					mHandleTouch = false;
					
					//startHitTest(fcX, fcY);	 // Also do the HitTest when the webview 
								 // window is scrolled
					if (!mMenuDown)
					{
						mForwardTouch = true;
						mWebView.dispatchTouchEvent(event);
						return true;
					}
										
					return false;
				}
				else if(currentMenu.getVisibility() == VISIBLE)
				{
					mHandleTouch = false; // Don't let user drag at this stage
					return false;
				}
				else
				{			
					fcView.setVisibility(View.VISIBLE);
					//fcTouchView.setVisibility(View.VISIBLE);

					mHandleTouch = true;
				
					// Save coordinates
					//				mLastTouchX = X;
					//				mLastTouchY = Y;
					mTouchPointValid = true;
			
					scrollX *= (radFact/length);
					scrollY *= (radFact/length);
					
					pointer.scrollTo(scrollX, scrollY);
					fcPointerView.scrollTo(scrollX, scrollY);
					//fcProgressBar.scrollTo(scrollX, scrollY);
			
					//fcTouchView.scrollTo(CircleX - X, CircleY - Y);
					//fcTouchView.setVisibility(View.VISIBLE);
				
					fcX = -(int)pointer.getScrollX() + -(int)getScrollX() + w/2;
					fcY = -(int)pointer.getScrollY() + -(int)getScrollY() + h/2;
				
					//stopSelection(fcX, fcY);
					startHitTest(fcX, fcY);	
				}
/*				else
				{
					//Toast.makeText(mContext, "Selection or menu would start now", Toast.LENGTH_LONG).show();
				
					stopHitTest(fcX, fcY, false);
					startSelection(fcX, fcY);
					mWebView.executeSelectionCommand(fcX, fcY, WebView.SELECT_WORD_OR_LINK);
					//Toast.makeText(mContext, "XY:" + X + "," + Y + " - CXY: " + 
					//	innerCircleX + "," + innerCircleY + "R: " + ir, Toast.LENGTH_LONG).show();
				}
				}*/
				if (mCanBeDisabled)
				{
					if (mHandleTouch)
					{
						mParent.stopGesture();
						mParent.stopTextGesture();
					}
					else
						disableFC();
				}
			}

			if(isCircularZoomEnabled()){
				zoomView.onTouchEvent(event);
				return true;
			}
			if (action == MotionEvent.ACTION_CANCEL)
			{
				mActivePointerId = INVALID_POINTER_ID;
			}

			/*if (action == MotionEvent.ACTION_MOVE)
			{	
				if (mMoveFrozen)
					return false;
				
				int dMX = Math.abs(X-mPrevMoveX);
				int dMY = Math.abs(Y-mPrevMoveY);

				if (dMX >= MAX_JUMP || dMY >= MAX_JUMP)
				{
					action = MotionEvent.ACTION_UP;
					mMoveFrozen = true;
					mHandleTouch = false; // Don't let user drag at this stage
				}
				
				mPrevMoveX = X;
				mPrevMoveY = Y;
			}*/
			
			if (action == MotionEvent.ACTION_UP)
			{
				if(currentMenu.getVisibility() == View.VISIBLE)
					handler.postDelayed(runnable, 10000);
				
				if(currentMenu == fcWindowTabs){
					if(mPrevX > X+100)
						nextWebPage();
					else if(mPrevX+100 < X){
						if(fcWindowTabs.getCurrentTab()>2)
							prevWebPage();
					}
					mPrevX = 0;
				}
				mActivePointerId = INVALID_POINTER_ID;

				if (!mMenuDown)
					fcView.setVisibility(View.VISIBLE);

				stopSelection();
				stopHitTest(fcX, fcY,false);
				//eventViewer.setText("Handling Touch on up ...");
				
				if (mHandleTouch == true && mWebHitTestResult != null)
				{
					//eventViewer.setText("Handling Touch on up ...");
					
					/*
					 * REMOVE TOUCH UP SELECTION
					 * 
					 * if (mWebHitTestResult.getType() == WebHitTestResult.TEXT_TYPE)
					{
						mParent.startTextGesture();
					}
					else
					{
						clickSelection(fcX, fcY);
					}*/
					onTouchUp();
					
					mWebHitTestResult = null;
				}
				
				removeTouchPoint();
				fcX = -(int)pointer.getScrollX() + -(int)getScrollX() + w/2;
				fcY = -(int)pointer.getScrollY() + -(int)getScrollY() + h/2;
				checkFCMenuBounds();

				//fcTouchView.setVisibility(View.INVISIBLE);

				if (mHandleTouch == false)
					return false;
			
				mHandleTouch = false;
			}		
			else if (!mHandleTouch)
			{
				// Even though we did not handle the touch, we still move the HitTest
				moveHitTest(fcX, fcY);
				return false;
			}

			if (action == MotionEvent.ACTION_MOVE)
			{	
				
				/*if (touchCount >= 2)
				{
					stopHitTest(fcX,fcY,false);
					startSelection(fcX, fcY);
				}
				else
					checkClickSelection(fcX, fcY);
				*/	
				moveSelection();
				moveHitTest(fcX, fcY);
				
				// FF: This will not work ...
				
				int r = fcPointerView.getRadius();
				
				
				
				if((fcX+r) > this.w)
					scrollWebView(10, 0);
				else if(X+r > this.w)
					scrollWebView(10, 0);
				
				if((fcX-r) <= 0)
					scrollWebView(-10, 0);
				else if ((X-r) <= 0)
					scrollWebView(-10, 0);
				
				if((fcY+r) > this.h)
					scrollWebView(10, 1);
				else if(Y+r > this.h)
					scrollWebView(10, 1);

				if((fcY-(r+50)) <= 0)
					scrollWebView(-10, 1);
				else if ((Y-(r+50)) <= 0)
					scrollWebView(-10, 1);
			}
			/*
			 
			 if(action == MotionEvent.ACTION_POINTER_DOWN){
				
				stopHitTest(fcX,fcY,false);
				startSelection(fcX, fcY);
			}
			if(action == MotionEvent.ACTION_POINTER_UP){
				checkClickSelection(fcX, fcY);
			}
			
			*/
			/* FC: Drag + Fling Support */
			
			/* If there is a second finger press, ignore */
			//if (touchCount == 2)
				//return false;

			/*if (touchCount == 2)
			{
				Toast.makeText(mContext, "0: " + event.getX(0) + "," + event.getY(0) + "- 1: " +  event.getX(1) + "," + event.getY(1), Toast.LENGTH_SHORT).show();
			}*/
			
			
			status = onInterceptTouchEventFC(event);
			
			if (status == false)
				return true;
				
			status = onTouchEventFC(event);

			if (status == false)
				return false;
		
			// We handled it
			return true;

		}
		
		public boolean canGoForward(){
			return mWebView.canGoForward();
		}
		public boolean canGoBackward(){
			return mWebView.canGoBack();
		}
		public void goBackward(){
			if(mWebView.canGoBack())
				mWebView.goBack();
		}

		public void stopLoading(){
				mWebView.stopLoading();
		}
		
		public void goForward(){
			if(mWebView.canGoForward())
				mWebView.goForward();
		}
		/*
		 * Circular zoom functions 
		 * 
		 */
			public void enableCircularZoom(){
				zoomView.setVisibility(VISIBLE);
				zoomView.setClickable(true);
			/*	long downTime = SystemClock.uptimeMillis();
				long eventTime = SystemClock.uptimeMillis();

				MotionEvent event = MotionEvent.obtain(downTime, eventTime,MotionEvent.ACTION_UP, 10, 10, 0);
				fcMainMenu.onTouch(fcMainMenu.getChildAt(3), event);
			*/	
				zoomView.setAngle((float)fcMainMenu.getZoomAngle());
				eventViewer.setText("Circular Zooming enabled.Click back to disable it");
				currentMenu.setVisibility(INVISIBLE);
				fcView.setVisibility(View.VISIBLE);
			}
			public void disableCircularZoom(){
				currentMenu.setVisibility(VISIBLE);
				fcView.setVisibility(View.INVISIBLE);
				zoomView.setVisibility(INVISIBLE);
				eventViewer.setText("Circular Zooming disabled");
			}
			public boolean isCircularZoomEnabled(){
				if(zoomView.getVisibility() == VISIBLE)
					return true;
				return false;
			}
			public void circularZoomIn(){
				mWebView.zoom(fcX, fcY, 1.25f);
			}
			public void circularZoomOut(){
				mWebView.zoom(fcX, fcY, 0.8f);
			}
			public void circularZoom(float zoomVal){
				mWebView.zoom(fcX, fcY, zoomVal);
			}
			
		public void setEventText(String str){
			eventViewer.setText(str);
		}
		
		public void loadPage(String url){
			
			mWebView.loadUrl(url);
		}
		public void loadData(String data){
			
			mWebView.loadDataWithBaseURL("file:///android_asset/images/", data,  "text/html", "utf-8", null);
		}
		public void nextWebPage(){
			mParent.setActiveWebViewIndex(mParent.getActiveWebViewIndex()-1);
			//fcWindowTabs.setCurrentTab(fcWindowTabs.getCurrentTab()+1);
			TabButton child = (TabButton) fcWindowTabs.findViewById(mParent.getActiveWebViewIndex());
			fcWindowTabs.setCurrentTab(child.getTabIndex());
			fcWindowTabs.setActiveTabIndex(child);
			
		}
		public void prevWebPage(){
			mParent.setActiveWebViewIndex(mParent.getActiveWebViewIndex()+1);
			//fcWindowTabs.setCurrentTab(fcWindowTabs.getCurrentTab()-1);
			TabButton child = (TabButton) fcWindowTabs.findViewById(mParent.getActiveWebViewIndex());
			fcWindowTabs.setCurrentTab(child.getTabIndex());
			fcWindowTabs.setActiveTabIndex(child);
		}
		/*
		 * WebView scrolling with FloatingCursor
		 */
		public void scrollWebView(int value,int direction){
		
		/*
		 * direction = 0 if web view scrolls along X axis
		 * direction = 1 if web view scrolls along Y axis
		 */
			int sx = mWebView.getScrollX();
			int sy = mWebView.getScrollY();

			if(direction==0)
			{
				sx += value;
				
				if (mWebView.getContentWidth() <= this.getWidth() )
					return;
				
				//Log.i("scrollX","sx,gCW,gSX,tgW"+sx+","+mWebView.getContentWidth() + "," + mWebView.getScrollX() + "," + "," + this.getWidth());
				
				if (sx >= mWebView.getContentWidth())
					sx =  mWebView.getContentWidth();
				if (sx < 0)
					sx = 0;
				mWebView.scrollTo(sx, sy);
			}
			else
			{
				if (mWebView.getContentHeight() <= this.getHeight() )
					return;
				
				sy += value;
				if (sy >= mWebView.getContentHeight())
					sy = mWebView.getContentHeight();
				if (sy < 0)
					sy = 0;

				mWebView.scrollTo(sx, sy);
			}
		}
	
		public class WebClient extends WebChromeClient
		{
			 // Class used to use a dropdown for a <select> element
		    private class InvokeListBox implements Runnable {
		        // Whether the listbox allows multiple selection.
		        private boolean     mMultiple;
		        // Passed in to a list with multiple selection to tell
		        // which items are selected.
		        private int[]       mSelectedArray;
		        // Passed in to a list with single selection to tell
		        // where the initial selection is.
		        private int         mSelection;

		        private Container[] mContainers;

		        // Need these to provide stable ids to my ArrayAdapter,
		        // which normally does not have stable ids. (Bug 1250098)
		        private class Container extends Object {
		            String  mString;
		            boolean mEnabled;
		            int     mId;

		            public String toString() {
		                return mString;
		            }
		        }

		        /**
		         *  Subclass ArrayAdapter so we can disable OptionGroupLabels,
		         *  and allow filtering.
		         */
		        private class MyArrayListAdapter extends ArrayAdapter<Container> {
		            public MyArrayListAdapter(Context context, Container[] objects, boolean multiple) {
		                super(context,
		                            multiple ? R.layout.select_dialog_multichoice :
		                            R.layout.select_dialog_singlechoice,
		                            objects);
		            }

		            @Override
		            public boolean hasStableIds() {
		                // AdapterView's onChanged method uses this to determine whether
		                // to restore the old state.  Return false so that the old (out
		                // of date) state does not replace the new, valid state.
		                return false;
		            }

		            private Container item(int position) {
		                if (position < 0 || position >= getCount()) {
		                    return null;
		                }
		                return (Container) getItem(position);
		            }

		            @Override
		            public long getItemId(int position) {
		                Container item = item(position);
		                if (item == null) {
		                    return -1;
		                }
		                return item.mId;
		            }

		            @Override
		            public boolean areAllItemsEnabled() {
		                return false;
		            }

		            @Override
		            public boolean isEnabled(int position) {
		                Container item = item(position);
		                if (item == null) {
		                    return false;
		                }
		                return item.mEnabled;
		            }
		        }

		        private InvokeListBox(String[] array,
		                boolean[] enabled, int[] selected) {
		            mMultiple = true;
		            mSelectedArray = selected;

		            int length = array.length;
		            mContainers = new Container[length];
		            for (int i = 0; i < length; i++) {
		                mContainers[i] = new Container();
		                mContainers[i].mString = array[i];
		                mContainers[i].mEnabled = enabled[i];
		                mContainers[i].mId = i;
		            }
		        }

		        private InvokeListBox(String[] array, boolean[] enabled, int
		                selection) {
		            mSelection = selection;
		            mMultiple = false;

		            int length = array.length;
		            mContainers = new Container[length];
		            for (int i = 0; i < length; i++) {
		                mContainers[i] = new Container();
		                mContainers[i].mString = array[i];
		                mContainers[i].mEnabled = enabled[i];
		                mContainers[i].mId = i;
		            }
		        }

		        /*
		         * Whenever the data set changes due to filtering, this class ensures
		         * that the checked item remains checked.
		         */
		        private class SingleDataSetObserver extends DataSetObserver {
		            private long        mCheckedId;
		            private ListView    mListView;
		            private Adapter     mAdapter;

		            /*
		             * Create a new observer.
		             * @param id The ID of the item to keep checked.
		             * @param l ListView for getting and clearing the checked states
		             * @param a Adapter for getting the IDs
		             */
		            public SingleDataSetObserver(long id, ListView l, Adapter a) {
		                mCheckedId = id;
		                mListView = l;
		                mAdapter = a;
		            }

		            public void onChanged() {
		                // The filter may have changed which item is checked.  Find the
		                // item that the ListView thinks is checked.
		                int position = mListView.getCheckedItemPosition();
		                long id = mAdapter.getItemId(position);
		                if (mCheckedId != id) {
		                    // Clear the ListView's idea of the checked item, since
		                    // it is incorrect
		                    mListView.clearChoices();
		                    // Search for mCheckedId.  If it is in the filtered list,
		                    // mark it as checked
		                    int count = mAdapter.getCount();
		                    for (int i = 0; i < count; i++) {
		                        if (mAdapter.getItemId(i) == mCheckedId) {
		                            mListView.setItemChecked(i, true);
		                            break;
		                        }
		                    }
		                }
		            }

		            public void onInvalidate() {}
		        }

		        public void run() {
		        	
		            Looper.prepare();
		        	
		            final ListView listView = (ListView) LayoutInflater.from(mContext)
		                    .inflate(R.layout.select_dialog, null);
		            final MyArrayListAdapter adapter = new
		                    MyArrayListAdapter(mContext, mContainers, mMultiple);
		            AlertDialog.Builder b = new AlertDialog.Builder(mContext)
		                    .setView(listView).setCancelable(true)
		                    .setInverseBackgroundForced(true);

		            if (mMultiple) {
		                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int which) {
		                    	mWebView.setListBoxChoices(listView.getCheckedItemPositions(), adapter.getCount(), false);
		                        /*mWebViewCore.sendMessage(
		                                EventHub.LISTBOX_CHOICES,
		                                adapter.getCount(), 0,
		                                listView.getCheckedItemPositions());*/
		                    }});
		                b.setNegativeButton(android.R.string.cancel,
		                        new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int which) {
		                    	mWebView.setListBoxChoice(-2, true);
		                        /*mWebViewCore.sendMessage(
		                                EventHub.SINGLE_LISTBOX_CHOICE, -2, 0);*/
		                }});
		            }
		            final AlertDialog dialog = b.create();
		            listView.setAdapter(adapter);
		            listView.setFocusableInTouchMode(true);
		            // There is a bug (1250103) where the checks in a ListView with
		            // multiple items selected are associated with the positions, not
		            // the ids, so the items do not properly retain their checks when
		            // filtered.  Do not allow filtering on multiple lists until
		            // that bug is fixed.

		            listView.setTextFilterEnabled(!mMultiple);
		            if (mMultiple) {
		                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		                int length = mSelectedArray.length;
		                for (int i = 0; i < length; i++) {
		                    listView.setItemChecked(mSelectedArray[i], true);
		                }
		            } else {
		                listView.setOnItemClickListener(new OnItemClickListener() {
		                    public void onItemClick(AdapterView parent, View v,
		                            int position, long id) {
		                    	mWebView.setListBoxChoice((int)id, false);
		                        /*mWebViewCore.sendMessage(
		                                EventHub.SINGLE_LISTBOX_CHOICE, (int)id, 0);*/
		                        dialog.dismiss();
		                    }
		                });
		                if (mSelection != -1) {
		                    listView.setSelection(mSelection);
		                    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		                    listView.setItemChecked(mSelection, true);
		                    DataSetObserver observer = new SingleDataSetObserver(
		                            adapter.getItemId(mSelection), listView, adapter);
		                    adapter.registerDataSetObserver(observer);
		                }
		            }
		            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
		                public void onCancel(DialogInterface dialog) {
	                    	mWebView.setListBoxChoice(-2, true);
		                    /*mWebViewCore.sendMessage(
		                                EventHub.SINGLE_LISTBOX_CHOICE, -2, 0);*/
		                }
		            });
		            dialog.show();
		            
		            Looper.loop();
		            
		        }
		    }
		    
			public void onProgressChanged  (WebView  view, int newProgress) {
				fcView.setProgress(newProgress);
			}
			
			// @Override
			public void onClipBoardUpdate (String type) {
				if (mGesturesEnabled) {
					Log.d("in onClickBoardUpdate-------------------------------", type);
					mParent.startGesture(true);
					mGesturesEnabled=false;
				}
			}
			
			@Override
	        public void onRequestFocus(WebView view) {
				Log.d("INSIDE ONREQUEST FOCUS","--------------------------");
			}
			
			//@Override
			void onListBoxRequest(String[] array, boolean[]enabledArray, int[] selectedArray){
				new Thread(new InvokeListBox(array, enabledArray, selectedArray)).start();
			}
			// @Override
		    void onListBoxRequest(String[] array, boolean[]enabledArray, int selection) {
		    	new Thread(new InvokeListBox(array, enabledArray, selection)).start();
		    }	
		}
		
		private class GestureWebViewClient extends WebViewClient {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
//				mParent.setTopBarURL(url);
				view.loadUrl(url);
				return true;
			}
		
			@Override
			public void doUpdateVisitedHistory (WebView view, String url, boolean isReload){
				if(!isReload && !url.startsWith("data:text/html") && !url.startsWith("file:///android_asset/")){
					Log.d("---History--------", "url = "+url+"  Title ="+ view.getTitle());
					dbConnector.addToHistory(System.currentTimeMillis()+"", url, view.getTitle(), 1);
				}
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				
				fcView.stopAllAnimation(); // Stop 'loading' animation
				fcView.startScaleUpAnimation(); //Restore original size
				
				fcMainMenu.toggleCloseORRefresh(true);
				mContentWidth = view.getContentWidth();
				mContentHeight = view.getContentHeight();
			
				BitmapDrawable bd = new BitmapDrawable(getCircleBitmap(view));
				fcWindowTabs.setCurrentThumbnail(bd,view);
				
				fcMainMenu.setBackEabled(view.canGoBack());
				fcMainMenu.setFwdEabled(view.canGoForward());
				
			}
			public Bitmap getCircleBitmap(WebView view){
				Picture thumbnail = view.capturePicture();
		        if (thumbnail == null) {
		            return null;
		        }
		        Bitmap bm = Bitmap.createBitmap(50,
		                50, Bitmap.Config.ARGB_4444);
		        
		        Canvas canvas = new Canvas(bm);
		        Path path = new Path();
			    
				path.addCircle(25,25,25,Path.Direction.CCW);
				canvas.clipPath(path,Region.Op.INTERSECT);
				    
		        // May need to tweak these values to determine what is the
		        // best scale factor
		        int thumbnailWidth = thumbnail.getWidth();
		        if (thumbnailWidth > 0) {
		            float scaleFactor = (float) 50 /
		                    (float)thumbnailWidth;
		            canvas.scale(scaleFactor, scaleFactor);
		        }
		        thumbnail.draw(canvas);
		        System.gc();
		        return bm;
			}
			
/*			public Bitmap getCircleBitmap(Bitmap sourceBitmap){
				
				
				int targetWidth = 50;
			    int targetHeight = 50;
			    Bitmap targetBitmap = Bitmap.createBitmap(
			        targetWidth,
			        targetHeight,
			        Bitmap.Config.ARGB_8888);
			    Canvas canvas = new Canvas(targetBitmap);
			    Path path = new Path();
			
			    path.addCircle(
			    		25,
			    		25,
				        25,
				        Path.Direction.CCW);
			    canvas.clipPath(path,Region.Op.REPLACE);
			    	    
			   // canvas.drawCircle(27, 27, 30, mPaint);
			   // canvas.drawBitmap(buffer, 0, 0, mPaint);
			    canvas.drawBitmap(
			        sourceBitmap,
			        new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight()),
			        new Rect(0, 0, targetWidth-2, targetHeight-2),
			        null);
			    sourceBitmap.recycle();
			    path.reset();
			    return targetBitmap;
			}
*/			
			public void onPageStarted(WebView view, String url,Bitmap b) {
				//fcProgressBar.enable();
				fcView.startScaleDownAndRotateAnimation();
				fcMainMenu.toggleCloseORRefresh(false);
			}
		}
		
		/* MT Stuff */
		
		class FCObj {
			
		}
		
		private MultiTouchController<FCObj> multiTouchController = new MultiTouchController<FCObj>(this, false);

		FCObj fc_obj = new FCObj();

		public FCObj getDraggableObjectAtPoint(PointInfo touchPoint) {
//			Log.w("FC-MT", "getDraggableObjectAtPoint");

			// Dummy obj for now, as it can be done with full screen.
			// Could later be used for different pinch zooms.
			return fc_obj;
		}

		public void getPositionAndScale(FCObj obj,
				PositionAndScale objPosAndScaleOut) {
			//Log.w("FC-MT", "getPositionAndScale");
			// We fill good data, but we don't need that.
			objPosAndScaleOut.set(fcX, fcY, false, 1.0f, false, 1.0f, 1.0f, false, 0.0f);
		}

		public void selectObject(FCObj obj, PointInfo touchPoint) {
			//float[] xs = touchPoint.getXs();
			//float[] ys = touchPoint.getYs();

			//Log.w("FC-MT", "selectObject: " + touchPoint.getAction() + " -> " + xs[0] + "," + ys[0] + " " + xs[1] + "," + ys[1] + " = " + touchPoint.getEventTime());
			
			if (mSelectionGestures != null && !mMoveFrozen)
				mSelectionGestures.dispatchTouchEventMT(touchPoint, touchPoint.isDown()?MotionEvent.ACTION_DOWN:MotionEvent.ACTION_UP);
		}

		public boolean setPositionAndScale(FCObj obj,
				PositionAndScale newObjPosAndScale, PointInfo touchPoint) {
			
			//float[] xs = touchPoint.getXs();
			//float[] ys = touchPoint.getYs();

			//Log.w("FC-MT", "setPositionAndScale: " + touchPoint.getAction() + " -> " + xs[0] + "," + ys[0] + " " + xs[1] + "," + ys[1] + " = " + touchPoint.getEventTime());

			if (mSelectionGestures != null && !mMoveFrozen)
				mSelectionGestures.dispatchTouchEventMT(touchPoint, MotionEvent.ACTION_MOVE);
			
			return false;
		}
		
		SelectionGestureView mSelectionGestures = null;
		
		public void setSelectionGesture(SelectionGestureView v) {
			mSelectionGestures = v;
		}

}
