package com.roamtouch.floatingcursor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Scroller;

import com.roamtouch.view.EventViewerArea;
import com.roamtouch.view.TopBarArea;
import com.roamtouch.menu.CircularLayout;
import com.roamtouch.menu.MainMenu;
import com.roamtouch.menu.SettingsMenu;
import com.roamtouch.menu.TabButton;
import com.roamtouch.menu.WindowTabs;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import com.roamtouch.swiftee.SwifteeApplication;


public class FloatingCursor extends FrameLayout {
	
	
	 	private BrowserActivity mParent;
	 	
		private int w = 0, h = 0;
	
	/**
	 * Calculate the touching radius for FP 
	 */
		private final float RADIUS_DIP = 120; // 64dip=10mm, 96dip=15mm, 192dip=30mm expressed in DIP
		private final float scale = getContext().getResources().getDisplayMetrics().density;
		private final int RADIUS = (int) (RADIUS_DIP * scale + 0.5f); //Converting to Pixel
		
			
	/**
	 * 	FloatingCursor child views
	 */
		private FloatingCursorView fcView = null;
		private FloatingCursorInnerView fcPointerView = null;
		private CircularProgressBar fcProgressBar;
		private ImageView pointer;
		private MainMenu fcMainMenu;
		private SettingsMenu fcSettingsMenu;
		private WindowTabs fcWindowTabs;
		private ZoomWebView zoomView;
		private ImageView menuBackground;
				
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

                mLastMotionY = y;
                mLastMotionX = x;
 
                scrollBy(deltaX, deltaY);

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
			pointer.setImageResource(R.drawable.no_target_cursor);
			cancelSelection();
			
			// FIXME: Change so that Size of WebView is adjusted instead of being overlayed
			int hd_1 = 40;
			int hd_2 = 0;
			if (currentMenu.getVisibility() == VISIBLE)
				hd_2 = -80;

			// FIXME
			/* ****************** CHANGED CODE END *******************/
			mScroller.fling(getScrollX(), getScrollY(), velocityX, velocityY, -w/2, w/2, -h/2 + hd_1, h/2 + hd_2);
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
			pointer.setImageResource(R.drawable.no_target_cursor);
			pointer.setScaleType(ImageView.ScaleType.CENTER); 
		
			//pointer.setPadding(140, 140, 0, 0);
			//pointer.scrollTo(-140, -140);
        
			fcView = new FloatingCursorView(getContext());
			fcView.setRadius(RADIUS);
	
			removeTouchPoint();
		
			fcPointerView = new FloatingCursorInnerView(getContext());
			fcPointerView.setRadius((int)(RADIUS*0.3f));
			fcPointerView.setQuality(0);
			
		
			fcProgressBar=new CircularProgressBar(getContext(),(int)(RADIUS*0.3f)+20);

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
			addView(fcProgressBar);
			addView(fcPointerView);
			addView(pointer);
			addView(fcMainMenu);
			addView(fcSettingsMenu);
			addView(fcWindowTabs);
			addView(zoomView);
			
			vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		}


		public FloatingCursor(Context context, AttributeSet attrs) {
			super(context, attrs);
			init(context);
		}

		public void gestureDisableFC()
		{
			mCanBeDisabled  = true;
			// Before that works, we need to make sure that the hit area is no longer clicked on
			//fcView.setVisibility(View.INVISIBLE);
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
		}
		
		public WebView getWebView(){
			return mWebView;
		}
		public void setEventViewerArea(EventViewerArea eventViewer) {
			this.eventViewer = eventViewer;
			eventViewer.setWindowTabs(fcWindowTabs);
			fcMainMenu.setEventViewer(eventViewer);
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
		
		public boolean isMenuVisible()
		{
			return (currentMenu.getVisibility() == VISIBLE);
		}
		
		public void toggleMenuVisibility(){
			eventViewer.setMode(EventViewerArea.TEXT_ONLY_MODE);
			AlphaAnimation menuAnimation;
			
			// Reset FC
			removeTouchPoint();

			if(currentMenu.getVisibility() == INVISIBLE){
				menuAnimation = new AlphaAnimation(0.0f, 1.0f);
				menuAnimation.setDuration(1000);
				menuAnimation.setAnimationListener(new AnimationListener(){

					public void onAnimationEnd(Animation animation) {
						currentMenu.setVisibility(VISIBLE);
					}
					public void onAnimationRepeat(Animation animation) {}

					public void onAnimationStart(Animation animation) {}
					
				});
				currentMenu.startAnimation(menuAnimation);
				vibrator.vibrate(25);
				eventViewer.setText(((CircularLayout)currentMenu).getName());
//				mParent.setTopBarVisibility(VISIBLE);
//				mParent.setTopBarMode(TopBarArea.ADDR_BAR_MODE);
			}
			else if(currentMenu.getVisibility() == VISIBLE){
				menuAnimation = new AlphaAnimation(1.0f, 0.0f);
				menuAnimation.setDuration(1000);
				menuAnimation.setAnimationListener(new AnimationListener(){

					public void onAnimationEnd(Animation animation) {
						currentMenu.setVisibility(INVISIBLE);
						currentMenu = fcMainMenu;
					}
					public void onAnimationRepeat(Animation animation) {}

					public void onAnimationStart(Animation animation) {}
					
				});
				currentMenu.startAnimation(menuAnimation);
				vibrator.vibrate(25);
//				mParent.setTopBarVisibility(INVISIBLE);
			}		
		}
		
		@Override 
		protected void onSizeChanged(int w, int h, int oldw, int oldh) { 
			super.onSizeChanged(w, h, oldw, oldh);
			fcView.setPosition(w/2,h/2);
			fcPointerView.setPosition(w/2,h/2);
			fcProgressBar.setPosition(w/2, h/2);
			fcMainMenu.setPosition(w/2, h/2);
			this.w=w;
			this.h=h;
			Log.d("OnSizeChanged:(w,h)","("+w+","+h+")" );
		}

		private boolean mHandleTouch = false;
	
	
		// FIXME: TODO: Put this into their own classes
		private boolean mSelectionMode = false;
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
					pointer.setImageResource(R.drawable.no_target_cursor);
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
					break;
				}
				case WebHitTestResult.VIDEO_TYPE: {
					cursorImage = R.drawable.video_cursor;
					WebVideoInfo videoInfo = mWebHitTestResult.getVideoInfo();
					break;
				}
				
				case WebHitTestResult.ANCHOR_TYPE: {
					resultType = WebHitTestResult.ANCHOR_TYPE;
					cursorImage = R.drawable.link_cursor;
					String tooltip = mWebHitTestResult.getToolTip();
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
					eventViewer.splitText(WebHitTestResult.ANCHOR_TYPE,tooltip);
	
					break;
				}
	
				case WebHitTestResult.IMAGE_ANCHOR_TYPE: {
					resultType = WebHitTestResult.IMAGE_TYPE;
					cursorImage = R.drawable.image_cursor;
					break;
				}
	
				case WebHitTestResult.IMAGE_TYPE: {
					cursorImage = R.drawable.image_cursor;
					eventViewer.splitText(WebHitTestResult.IMAGE_TYPE,"");
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
				fcProgressBar.scrollTo(0,0);
			}

			mTouchPointValid = false;
		}
	
		public void onPageFinished() {
			pointer.setImageResource(R.drawable.no_target_cursor);
			removeTouchPoint();
		}
		 
		/* public interface */
		
		public void removeSelection()
		{
			mWebView.executeSelectionCommand(fcX, fcY, WebView.CLEAR_SELECTION);
		}
		
		public void startSelectionCommand()
		{
			mWebView.executeSelectionCommand(fcX, fcY, WebView.START_SELECTION);		
		}
		
		public void executeSelectionCommand(int cmd)
		{
			mWebView.executeSelectionCommand(fcX, fcY, cmd);
		}
	
		public void stopSelectionCommand()
		{
			mGesturesEnabled = true;
			
			mWebView.executeSelectionCommand(fcX, fcY, WebView.STOP_SELECTION);
			mWebView.executeSelectionCommand(fcX, fcY, WebView.COPY_TO_CLIPBOARD);
		}
		
		public boolean defaultCommand()
		{
			if (mWebHitTestResult.getType() == WebHitTestResult.ANCHOR_TYPE || mWebHitTestResult.getType() == WebHitTestResult.SRC_ANCHOR_TYPE || mWebHitTestResult.getType() == WebHitTestResult.SRC_IMAGE_ANCHOR_TYPE)
			{
				sendEvent(MotionEvent.ACTION_DOWN, fcX, fcY);
				pointer.setImageResource(R.drawable.address_bar_cursor);
				sendEvent(MotionEvent.ACTION_UP, fcX, fcY);		
				startHitTest(fcX,fcY);
				return false;
			}
				// Gesture cancelled
			return true;
		}

		protected void clickSelection(int X, int Y)
		{
			if (mWebHitTestResult == null)
				return;
			
			//Toast.makeText(mContext, "Clicking sel ..." + mWebHitTestResult.getType(), Toast.LENGTH_LONG).show();

			if (mWebHitTestResult.getType() == WebHitTestResult.ANCHOR_TYPE || mWebHitTestResult.getType() == WebHitTestResult.SRC_ANCHOR_TYPE || mWebHitTestResult.getType() == WebHitTestResult.SRC_IMAGE_ANCHOR_TYPE)
			{
//				Toast.makeText(mContext, "Clicking link ...", Toast.LENGTH_LONG).show();
/*
				sendEvent(MotionEvent.ACTION_DOWN, X, Y);
				pointer.setImageResource(R.drawable.address_bar_cursor);
				sendEvent(MotionEvent.ACTION_UP, X, Y);		
				startHitTest(X,Y);*/
				
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
			}
		}
	
		private boolean mTouchPointValid = false;
	
		private static final int INVALID_POINTER_ID = -1;
		
		private int mActivePointerId = INVALID_POINTER_ID;
		
		public boolean dispatchKeyEventFC(KeyEvent event) {
			
			//Toast.makeText(mContext, "KeyEvent: " + event.getAction(), Toast.LENGTH_LONG).show();
			
			if ( event.getAction() == KeyEvent.ACTION_DOWN)
			{
					stopHitTest(fcX,fcY,false);
					startSelection(fcX, fcY);
			}
			else if (event.getAction() == KeyEvent.ACTION_UP)
			{
				checkClickSelection(fcX, fcY);				
			}
			
			return true;			
		}

		int fcX = 0, fcY = 0;
		
		public boolean dispatchTouchEventFC(MotionEvent event) {
			
			final int action = event.getAction() & MotionEvent.ACTION_MASK;
			 
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
			
			if (action == MotionEvent.ACTION_DOWN)
			{
				mPrevX = X;
				mPrevY = Y;
				mActivePointerId = event.getPointerId(0);
				
				//Toast.makeText(mContext, "mActivePointerId: " + mActivePointerId, 100).show();

				final int CircleX = -(int)getScrollX() + w/2;
				final int CircleY = -(int)getScrollY() + h/2;

				final int r = fcView.getRadius();
				
				// Check for inner circle click and show Circular menu
				final int innerCirRad = fcPointerView.getRadius();
				
				// We need to factor the inner circle relocation so
				// it does not get out of the outer circle
				// Fixed Size: 60 % from our radius
				final float radFact = 0.6f*r; //(float)(2*innerCirRad) / (float)r;

				//Toast.makeText(mContext, "radFact: " + radFact, 100).show();
				
				final int innerCircleX = -(int)fcPointerView.getScrollX() + CircleX;
				final int innerCircleY = -(int)fcPointerView.getScrollY() + CircleY;
					
				if(X > innerCircleX-innerCirRad && X < innerCircleX+innerCirRad && Y > innerCircleY-innerCirRad && Y < innerCircleY+innerCirRad){
					//Toast.makeText(mContext, "Circular Menu", 100).show();
					if(isCircularZoomEnabled())
						disableCircularZoom();
					toggleMenuVisibility();
					mHandleTouch = false; // FIXME: Change, do Let user drag and fling menu
					//return true;
				}
				
				else if ((X < CircleX-r || X > CircleX+r || Y < CircleY-r || Y > CircleY+r) && mScroller.isFinished())
				{	
					fcView.setVisibility(View.INVISIBLE);
					//removeTouchPoint();
					
					mHandleTouch = false;
					startHitTest(fcX, fcY);	 // Also do the HitTest when the webview 
								 // window is scrolled
					
					//return false;
				}
				else if(currentMenu.getVisibility() == VISIBLE)
				{
					mHandleTouch = false; // Don't let user drag at this stage
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
			
					int scrollX = X - CircleX;
					int scrollY = Y - CircleY;
					
					double length = Math.hypot(scrollX, scrollY);
					
					scrollX *= (radFact/length);
					scrollY *= (radFact/length);
					
					pointer.scrollTo(scrollX, scrollY);
					fcPointerView.scrollTo(scrollX, scrollY);
					fcProgressBar.scrollTo(scrollX, scrollY);
			
					//fcTouchView.scrollTo(CircleX - X, CircleY - Y);
					//fcTouchView.setVisibility(View.VISIBLE);
				
					fcX = -(int)pointer.getScrollX() + -(int)getScrollX() + w/2;
					fcY = -(int)pointer.getScrollY() + -(int)getScrollY() + h/2;
				
					stopSelection(fcX, fcY);
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

				
			if (action == MotionEvent.ACTION_UP)
			{
				if(currentMenu == fcWindowTabs){
					if(mPrevX > X+100)
						nextWebPage();
					else if(mPrevX < X+100){
						if(fcWindowTabs.getCurrentTab()>2)
							prevWebPage();
					}
					mPrevX = 0;
				}
				mActivePointerId = INVALID_POINTER_ID;

				fcView.setVisibility(View.VISIBLE);

				stopSelection(fcX, fcY);
				stopHitTest(fcX, fcY,false);
				
				if (mHandleTouch == true && mWebHitTestResult != null)
				{
					//eventViewer.setText("Handling Touch on up ...");
					
					if (mWebHitTestResult.getType() == WebHitTestResult.TEXT_TYPE)
					{
						mParent.startTextGesture();
					}
					else
					{
						clickSelection(fcX, fcY);
					}
					
					mWebHitTestResult = null;
				}
				
				//removeTouchPoint();

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
				moveSelection(fcX, fcY);
				moveHitTest(fcX, fcY);
				
				// FF: This will not work ...
				
				if(fcX>BrowserActivity.DEVICE_WIDTH && fcX<mContentWidth)
					scrollWebView(10, 0);
			}
			if(action == MotionEvent.ACTION_POINTER_DOWN){
				
				stopHitTest(fcX,fcY,false);
				startSelection(fcX, fcY);
			}
			if(action == MotionEvent.ACTION_POINTER_UP){
				checkClickSelection(fcX, fcY);
			}
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
				fcMainMenu.getChildAt(3).invalidate();
				
				eventViewer.setText("Circular Zooming enabled.Click back to disable it");
				currentMenu.setVisibility(INVISIBLE);
			}
			public void disableCircularZoom(){
				//currentMenu.setVisibility(VISIBLE);
				zoomView.setVisibility(INVISIBLE);
				eventViewer.setText("Circular Zooming disabled");
			}
			public boolean isCircularZoomEnabled(){
				if(zoomView.getVisibility() == VISIBLE)
					return true;
				return false;
			}
			public void circularZoomIn(){
				mWebView.zoomIn();
			}
			public void circularZoomOut(){
				mWebView.zoomOut();
			}
			
		public void setEventText(String str){
			eventViewer.setText(str);
		}
			
		public void nextWebPage(){
			mParent.setActiveWebViewIndex(mParent.getActiveWebViewIndex()-1);
			fcWindowTabs.setCurrentTab(fcWindowTabs.getCurrentTab()+1);
			TabButton child = (TabButton) fcWindowTabs.findViewById(mParent.getActiveWebViewIndex());
			fcWindowTabs.setActiveTabIndex(child);
			
		}
		public void prevWebPage(){
			mParent.setActiveWebViewIndex(mParent.getActiveWebViewIndex()+1);
			fcWindowTabs.setCurrentTab(fcWindowTabs.getCurrentTab()-1);
			TabButton child = (TabButton) fcWindowTabs.findViewById(mParent.getActiveWebViewIndex());
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
			if(direction==0)
				mWebView.scrollBy(value, 0);
			else
				mWebView.scrollBy(0, value);
		}
	
		public class WebClient extends WebChromeClient
		{
			public void onProgressChanged  (WebView  view, int newProgress) {
				fcProgressBar.setProgress(newProgress);
			}
			
			// @Override
			public void onClipBoardUpdate (String type) {
				if (mGesturesEnabled) {
					Log.d("in onClickBoardUpdate-------------------------------", type);
					mParent.startGesture(SwifteeApplication.CURSOR_TEXT_GESTURE);
					mGesturesEnabled=false;
				}
			}
			
			@Override
	        public void onRequestFocus(WebView view) {
				Log.d("INSIDE ONREQUEST FOCUS","--------------------------");
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
			public void onPageFinished(WebView view, String url) {
				fcProgressBar.disable();
				mContentWidth = view.getContentWidth();
				mContentHeight = view.getContentHeight();
				Bitmap b = mWebView.getDrawingCache();
				Bitmap cb = Bitmap.createBitmap(b, 30, 30, 80, 80);				
				BitmapDrawable bd = new BitmapDrawable(getCircleBitmap(cb));
				fcWindowTabs.setCurrentThumbnail(bd);
				//mWebView.clearCache(true);
			}
		
			public Bitmap getCircleBitmap(Bitmap sourceBitmap){
				
				/*			Paint mPaint = new Paint();
				mPaint.setAntiAlias(true);
				mPaint.setColor(0xFFAAAAAA); // 0xFFFF0000
				mPaint.setStrokeWidth(2.0f);
					
				Bitmap buffer=Bitmap.createBitmap(25, 25,Bitmap.Config.ARGB_8888);
				Canvas bufferCanvas = new Canvas();
				bufferCanvas.setBitmap(buffer);
				bufferCanvas.drawCircle(25, 25, 25, mPaint);
		*/
				int targetWidth = 50;
			    int targetHeight = 50;
			    Bitmap targetBitmap = Bitmap.createBitmap(
			        targetWidth,
			        targetHeight,
			        Bitmap.Config.ARGB_8888);
			    Canvas canvas = new Canvas(targetBitmap);
			    Path path = new Path();
			    
			   /* path.addCircle(
			        ((float)targetWidth - 1) / 2,
			        ((float)targetHeight - 1) / 2,
			        (Math.min(((float)targetWidth), ((float)targetHeight)) / 2),
			        Path.Direction.CW);
			    */
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
			    return targetBitmap;
			}
			
			public void onPageStarted(WebView view, String url,Bitmap b) {
				fcProgressBar.enable();				
			}
		}
}
