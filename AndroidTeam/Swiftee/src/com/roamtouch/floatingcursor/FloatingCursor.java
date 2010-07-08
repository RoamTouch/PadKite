package com.roamtouch.floatingcursor;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import roamtouch.webkit.WebChromeClient;
import roamtouch.webkit.WebHitTestResult;
import roamtouch.webkit.WebView;
import roamtouch.webkit.WebViewClient;
//import roamtouch.webkit.WebView.HitTestResult;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.Toast;
import com.roamtouch.view.EventViewerArea;
import com.roamtouch.view.TopBarArea;
import com.roamtouch.menu.CircularLayout;
import com.roamtouch.menu.CircularMenu;
import com.roamtouch.menu.SettingsMenu;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;


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
	 * Logical inner touch circle coordinates and radius
	 */
		private float mTouchCircleX,mTouchCircleY,mTouchCircleradius=RADIUS*0.3f;
	
		
	/**
	 * 	FloatingCursor child views
	 */
		private FloatingCursorView fcView,  fcPointerView = null;
		private CircularProgressBar fcProgressBar;
		private ImageView pointer;
		private CircularMenu fcMainMenu;
		private SettingsMenu fcSettingsMenu;

		
	/**
	 *  indicating whether link executes
	 */
		private boolean shouldLinkExec = false; 
		
		private int touchCount,prevCount;
		
	/**
	 *  integer showing which menu among main,settings and tabs is currently displayed 
	 */
		private CircularLayout currentMenu;
		
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

			final float y = ev.getY();
			final float x = ev.getX();

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
			final float y = ev.getY();
			final float x = ev.getX();
        
       
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
			// FIXME
			/* ****************** CHANGED CODE END *******************/
			mScroller.fling(getScrollX(), getScrollY(), velocityX, velocityY, -w/2, w/2, -h/2, h/2);
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
		
			fcPointerView = new FloatingCursorView(getContext());
			fcPointerView.setRadius((int)(RADIUS*0.3f));
			fcPointerView.setQuality(0);
			
		
			fcProgressBar=new CircularProgressBar(getContext(),(int)(RADIUS*0.3f)+20);

			fcMainMenu = new CircularMenu(context);
			fcMainMenu.setfcRadius(RADIUS);
			fcMainMenu.setVisibility(INVISIBLE);
			fcMainMenu.setFloatingCursor(this);
			currentMenu = fcMainMenu;
			
			fcSettingsMenu =  new SettingsMenu(context);
			fcSettingsMenu.setfcRadius(RADIUS);
			fcSettingsMenu.setVisibility(INVISIBLE);
			fcSettingsMenu.setFloatingCursor(this);
			
			addView(fcView);
			addView(fcProgressBar);
			addView(fcPointerView);
			addView(pointer);
			addView(fcMainMenu);
			addView(fcSettingsMenu);
		}


		public FloatingCursor(Context context, AttributeSet attrs) {
			super(context, attrs);
			init(context);
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
	
		public void setWebView(WebView wv) {
			mWebView = wv;
			mWebView.setWebChromeClient(new WebClient());
			mWebView.setWebViewClient(new GestureWebViewClient());	
		}
		
		public void setEventViewerArea(EventViewerArea eventViewer) {
			this.eventViewer = eventViewer;
		}

		public void setParent(BrowserActivity p){
			mParent = p;
			fcMainMenu.setParent(mParent);
			fcSettingsMenu.setParent(mParent);
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
					//fcWindowTabs.setVisibility(INVISIBLE);
					break;
			case 1: currentMenu = fcSettingsMenu;
					fcSettingsMenu.setVisibility(VISIBLE);
					fcMainMenu.setVisibility(INVISIBLE);
					break;
			case 2: break;
			}
			
		}
		public void toggleMenuVisibility(){
			if(currentMenu.getVisibility() == INVISIBLE){
				currentMenu.setVisibility(VISIBLE);
				mParent.setTopBarVisibility(VISIBLE);
				mParent.setTopBarMode(TopBarArea.ADDR_BAR_MODE);
			}
			else if(currentMenu.getVisibility() == VISIBLE){
				currentMenu.setVisibility(INVISIBLE);
				mParent.setTopBarVisibility(INVISIBLE);
			}
		}
		
		@Override 
		protected void onSizeChanged(int w, int h, int oldw, int oldh) { 
			super.onSizeChanged(w, h, oldw, oldh); 
			mTouchCircleX=w/2;
			mTouchCircleY=h/2;
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
	
		private WebHitTestResult mWebHitTestResult ;
	
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
				String extra = mWebHitTestResult.getExtra();
				
				Log.i(TAG, "Extra:"  + extra);
			
				int cursorImage = 0;
			
				switch (resultType) {
				
				case WebHitTestResult.TEXT_TYPE: {
					cursorImage = R.drawable.text_cursor;
					break;
				}
				case WebHitTestResult.VIDEO_TYPE: {
					cursorImage = R.drawable.link_cursor;
					break;
				}
				
				case WebHitTestResult.ANCHOR_TYPE: {
					resultType = WebHitTestResult.ANCHOR_TYPE;
					cursorImage = R.drawable.link_cursor;
					eventViewer.splitText(WebHitTestResult.ANCHOR_TYPE,extra);
					if(shouldLinkExec){
						mWebView.loadUrl(extra);
						shouldLinkExec = false;
					}
					break;
				}

				case WebHitTestResult.EDIT_TEXT_TYPE: {
					cursorImage = R.drawable.text_cursor;
					break;
				}
			
				case WebHitTestResult.SRC_IMAGE_ANCHOR_TYPE:
				case WebHitTestResult.SRC_ANCHOR_TYPE: 
				{
					resultType = WebHitTestResult.ANCHOR_TYPE;
					cursorImage = R.drawable.link_cursor;
					eventViewer.splitText(WebHitTestResult.ANCHOR_TYPE,extra);
					if(shouldLinkExec){
						mWebView.loadUrl(extra);
						shouldLinkExec = false;
					}
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
					eventViewer.splitText(-1,"");
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
			}

			mTouchPointValid = false;
		}
	
		public void onPageFinished() {
			pointer.setImageResource(R.drawable.no_target_cursor);
			removeTouchPoint();
		}
	
		protected void clickSelection(int X, int Y)
		{
			if (mWebHitTestResult.getType() == WebHitTestResult.ANCHOR_TYPE)
			{
				sendEvent(MotionEvent.ACTION_DOWN, X, Y);
				pointer.setImageResource(R.drawable.address_bar_cursor);
				removeTouchPoint();
				sendEvent(MotionEvent.ACTION_UP, X, Y);
			}
			else if (mWebHitTestResult.getType() == WebHitTestResult.IMAGE_TYPE)
			{
				Toast.makeText(mContext, "Downloading image ...", Toast.LENGTH_LONG).show();
				pointer.setImageResource(R.drawable.address_bar_cursor);
				removeTouchPoint();
			// FIXME: Add Downloading of image
			}
			else
			{
				removeTouchPoint();
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
				sendEvent(MotionEvent.ACTION_UP, X, Y);
				pointer.setImageResource(R.drawable.no_target_cursor);
				mSelectionMode = false;
			
				if (X == selX && Y == selY)
					clickSelection(X,Y);
				else
				{
					removeTouchPoint();
					mParent.startGesture();
			
				}
			}
		}
	
		protected void moveSelection(int X, int Y)
		{
			if (mSelectionMode)
			{
				if (!mSelectionStarted)
				{
					pointer.setImageResource(R.drawable.text_cursor);
					mWebView.onKeyDown(KeyEvent.KEYCODE_SHIFT_LEFT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT));
					sendEvent(MotionEvent.ACTION_DOWN, X, Y);
					mSelectionStarted = true;
					selX = selY = -1;
				}
			
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
	
		public boolean dispatchTouchEventFC(MotionEvent event) {
				
		
			
			boolean status;
		
			int X,Y;
		
			X=(int)event.getX();
			Y=(int)event.getY();
		
			
		
			if (mIsDisabled)
				return true;  
		
			int fcX = -(int)pointer.getScrollX() + -(int)getScrollX() + w/2;
			int fcY = -(int)pointer.getScrollY() + -(int)getScrollY() + h/2;
			touchCount = event.getPointerCount();
			if(touchCount>1){
				//fcX = (int) event.getX(1);
				//fcY = (int) event.getY(1);		
				prevCount = touchCount;
			}
			
			//		Log.d("X,Y and fcX,fcY",X+","+Y+" and "+fcX+","+fcY);
		
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{			
				final int CircleX = -(int)getScrollX() + w/2;
				final int CircleY = -(int)getScrollY() + h/2;
			
				final int r = fcView.getRadius();
			
				/** Check for inner circle click and show Circular menu*/
				final int innerCirRad = fcPointerView.getRadius();
				
				final int innerCircleX = -(int)fcPointerView.getScrollX() + CircleX;
				final int innerCircleY = -(int)fcPointerView.getScrollY() + CircleY;
				
					
				if(X > innerCircleX-innerCirRad && X < innerCircleX+innerCirRad && Y > innerCircleY-innerCirRad && Y < innerCircleY+innerCirRad){
					//Toast.makeText(mContext, "Circular Menu", 100).show();
					toggleMenuVisibility();
					currentMenu = fcMainMenu;
					return true;
				}
				if(currentMenu.getVisibility() == VISIBLE)
					return false;

				if ((X < CircleX-r || X > CircleX+r || Y < CircleY-r || Y > CircleY+r) && mScroller.isFinished())
				{	
					fcView.setVisibility(View.INVISIBLE);
					removeTouchPoint();
					
					mHandleTouch = false;
				
					return false;
				}
			
				fcView.setVisibility(View.VISIBLE);
				//fcTouchView.setVisibility(View.VISIBLE);

				mHandleTouch = true;
			
				// TODO: Paint touch point
			
				final int touchCircleX = -(int)mTouchCircleX + CircleX;
				final int touchCircleY = -(int)mTouchCircleY + CircleY;
			
				final int ir = (int)mTouchCircleradius;

				if ((X < touchCircleX-ir || X > touchCircleX+ir || Y < touchCircleY-ir || Y > touchCircleY+ir) || !mTouchPointValid)
				{	
			
					// Save coordinates
					//				mLastTouchX = X;
					//				mLastTouchY = Y;
					mTouchPointValid = true;
			
					pointer.scrollTo(X - CircleX, Y - CircleY);
					fcPointerView.scrollTo(X - CircleX, Y - CircleY);
			
					mTouchCircleX=CircleX - X;
					mTouchCircleY=CircleY - Y;	
					//fcTouchView.scrollTo(CircleX - X, CircleY - Y);
					//fcTouchView.setVisibility(View.VISIBLE);
				
					fcX = -(int)pointer.getScrollX() + -(int)getScrollX() + w/2;
					fcY = -(int)pointer.getScrollY() + -(int)getScrollY() + h/2;
				
					stopSelection(fcX, fcY);
					startHitTest(fcX, fcY);
	
				}
				else
				{
					//Toast.makeText(mContext, "Selection or menu would start now", Toast.LENGTH_LONG).show();
				
					stopHitTest(fcX, fcY, false);
					startSelection(fcX, fcY);
					mWebView.executeSelectionCommand(fcX, fcY, WebView.SELECT_WORD_OR_LINK);
					//Toast.makeText(mContext, "XY:" + X + "," + Y + " - CXY: " + 
					//	innerCircleX + "," + innerCircleY + "R: " + ir, Toast.LENGTH_LONG).show();
				}
			}
				
			if (event.getAction() == MotionEvent.ACTION_UP)
			{
				fcView.setVisibility(View.VISIBLE);
			
				stopSelection(fcX, fcY);
				stopHitTest(fcX, fcY,false);
			
				//fcTouchView.setVisibility(View.INVISIBLE);

				if (mHandleTouch == false)
					return false;
			
				mHandleTouch = false;
			}
			else if (!mHandleTouch)
				return false;
	
			if (event.getAction() == MotionEvent.ACTION_MOVE)
			{	
				if(event.getPointerCount()>1){
					shouldLinkExec = true;
				}
					
				moveSelection(fcX, fcY);
				moveHitTest(fcX, fcY);
				if(fcX>BrowserActivity.DEVICE_WIDTH && fcX<mContentWidth)
					scrollWebView(10, 0);
			}
		
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
			public void  onProgressChanged  (WebView  view, int newProgress){
			fcProgressBar.setProgress(newProgress);
		}
	}
		
		private class GestureWebViewClient extends WebViewClient {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		
			@Override
			public void onPageFinished(WebView view, String url) {
				fcProgressBar.disable();
				mContentWidth = view.getContentWidth();
				mContentHeight = view.getContentHeight();
			}
		
			public void onPageStarted(WebView view, String url,Bitmap b) {
				fcProgressBar.enable();
			}
		}
}
