package com.roamtouch.floatingcursor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.metalev.multitouch.controller.MultiTouchController;
import org.metalev.multitouch.controller.MultiTouchController.MultiTouchObjectCanvas;
import org.metalev.multitouch.controller.MultiTouchController.PointInfo;
import org.metalev.multitouch.controller.MultiTouchController.PositionAndScale;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.webkit.WebChromeClient;

import com.roamtouch.webhook.WebHitTestResult;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.VideoView;

import com.roamtouch.utils.COLOR;
import com.roamtouch.utils.GetDomainName;
import com.roamtouch.utils.ScreenLocation;
import com.roamtouch.utils.StringUtils;
import com.roamtouch.utils.XmlFunctions;
import com.roamtouch.view.EventViewerArea;
import com.roamtouch.view.SelectionGestureView;
import com.roamtouch.view.WebPage;
import com.roamtouch.visuals.PointerHolder;
import com.roamtouch.visuals.RingController;
import com.roamtouch.visuals.SuggestionController;
import com.roamtouch.visuals.TextCursorHolder;
import com.roamtouch.visuals.TipController;
import com.roamtouch.visuals.SuggestionView.ArrowDownTask;
import com.roamtouch.database.DBConnector;
import com.roamtouch.menu.CircularLayout;
import com.roamtouch.menu.CircularTabsLayout;
import com.roamtouch.menu.MainMenu;
import com.roamtouch.menu.SettingsMenu;
import com.roamtouch.menu.TabButton;
import com.roamtouch.menu.WindowTabs;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.BrowserActivity.TouchInput;
import com.roamtouch.swiftee.R;
import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.swiftee.BrowserActivity.TouchInput;

import android.app.SearchManager;

public class FloatingCursor extends FrameLayout implements
		MultiTouchObjectCanvas<FloatingCursor.FCObj> {

	private DBConnector dbConnector;
	private BrowserActivity mP;
	public int w = 0, h = 0;	
	/* Maximum jump that is tolerated */
	private final int MAX_JUMP = 128;

	/**
	 * Calculate the touching radius for FP
	 */
	public final float RADIUS_DIP = 130; // 64dip=10mm, 96dip=15mm, 192dip=30mm
					// expressed in DIP
	private final float scale = getContext().getResources().getDisplayMetrics().density;
	private final int RADIUS = (int) (RADIUS_DIP * scale + 0.5f); // Converting
					// to Pixel
	private final int INNER_RADIUS = (int) (RADIUS * 0.3f);
	private final int FC_RADIUS = (int) (RADIUS * 0.9f); // Converting to Pixel

	/**
	 * FloatingCursor child views
	 */
	private FloatingCursorView fcView = null;
	private FloatingCursorInnerView fcPointerView = null;
	private CircularProgressBar fcProgressBar;
	private ImageView pointer;
	public MainMenu fcMainMenu;
	public SettingsMenu fcSettingsMenu;
	public WindowTabs fcWindowTabs;
	private ZoomWebView zoomView;
	// private ImageView menuBackground;
	private View TemporaryView;

	/**
	 * integer showing which menu among main,settings and tabs is currently
	 * displayed
	 */
	public ViewGroup currentMenu;
	private boolean mIsDisabled = false;
	private boolean mIsLoading = false;
	public WebView mWebView = null;

	/**
	 * Boundary conditions for scrolling web view with FloatingCursor
	 */
	int mContentWidth, mContentHeight;

	/** Application context */
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

	private float mPrevX, mPrevY;

	private Handler handler;
	private Runnable runnable;
	private Runnable parkingRunnable;

	private Runnable runnableCheck;

	// private KiteRunnable runnableKiteAni;

	private boolean timerStarted = false;// ms
	private boolean parkTimerStarted = false;// ms

	/** Vibrator for device vibration **/
	private Vibrator vibrator;

	protected boolean animationLock = false;
	private boolean mSoftKeyboardVisible = false;

	// Ring View where rings and tabs are set.
	private RingController rCtrl;
	private TipController tCtrl;
	private PointerHolder pHold;
	private TextCursorHolder tHold;
	private SuggestionController sCtrl;

	// Rect that gets the size of what is below the pointer.
	private Rect rect;

	// FC Scroll speed
	private final int FC_SCROLL_SPEED = 35;

	// Message Ids
	private static final int FOCUS_NODE_HREF = 102;
	private String linkTitle;
	private String linkUrl;
	private GetDomainName gdn = new GetDomainName();

	public String currentPage;
	public boolean isLandingPage;	
	
	public boolean isLandingPage() {
		return isLandingPage;
	}

	public void setLandingPage(boolean isLandingPage) {
		
		this.isLandingPage = isLandingPage;	
		if (SwifteeApplication.getOrientation() == SwifteeApplication.ORIENTATION_LANDSCAPE){
			loadPage("javascript:setInputLandscape()");
		} else if (SwifteeApplication.getOrientation() == SwifteeApplication.ORIENTATION_PORTRAIT){
			loadPage("javascript:setInputPortrait()");
		}
	}

	private int edge = SwifteeApplication.getEdge();

	private boolean expandToFinger;

	private boolean fingerTouch;

	private boolean remote;

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
		 * Shortcut the most recurring case: the user is in the dragging state
		 * and he is moving his finger. We want to intercept this motion.
		 */
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mIsBeingDragged)) {
			return true;
		}

		float y = ev.getY();
		float x = ev.getX();
		int touchCount = ev.getPointerCount(); // touchCount == 2 means ACTION!

		if (mActivePointerId != INVALID_POINTER_ID && touchCount >= 2) {
			final int pointerIndex = ev.findPointerIndex(mActivePointerId);
			y = ev.getY(pointerIndex);
			x = ev.getX(pointerIndex);
		}

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			/*
			 * mIsBeingDragged == false, otherwise the shortcut would have
			 * caught it. Check whether the user has moved far enough from his
			 * original down touch.
			 */

			/*
			 * Locally do absolute value. mLastMotionY is set to the y value of
			 * the down event.
			 */
			final int yDiff = (int) Math.abs(y - mLastMotionY);
			final int xDiff = (int) Math.abs(x - mLastMotionX);

			if (yDiff > mTouchSlop || xDiff > mTouchSlop) {
				mIsBeingDragged = true;
			}

			/**
			 * REPOSITION FL X ON REACHING LATERALS
			 * **/
			/*
			 * int X = (int) x;
			 * 
			 * int pos = mP.reachRightScreenLateral(X);
			 * 
			 * if ( pos == 1 || pos == 5 ){ //reached left
			 * 
			 * int _pHeight = getHeight();
			 * 
			 * int fcX_ver = 0; int fcY_ver = 0;
			 * 
			 * int gapX = (int) (_pHeight * 0.11);
			 * 
			 * // Draw new cursor on pHold. if (xFlag == 0) { if (pointer !=
			 * null) { removeView(pointer); } if (yFlag == 0) {
			 * pHold.addView(pointer); } xFlag = 1; }
			 * 
			 * int scrollX = 0; if (pos==1){ scrollX = X + gapX; } else if
			 * (pos==2) { scrollX = X - gapX; }
			 * 
			 * pHold.scrollTo(scrollX, fcY);
			 * 
			 * } else { if (xFlag==1){ relocatePointerToFCView(); } }
			 */

			break;

		case MotionEvent.ACTION_DOWN:

			// Set FC dots smaller while dragging.
			SwifteeApplication.setFCDotDiam(4);

			/* Remember location of down touch */
			mLastMotionY = y;
			mLastMotionX = x;

			/*
			 * If being flinged and user touches the screen, initiate drag;
			 * otherwise don't. mScroller.isFinished should be false when being
			 * flinged.
			 */
			mIsBeingDragged = !mScroller.isFinished();
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:

			// Set FC dot to original size again.
			SwifteeApplication.setFCDotDiam(SwifteeApplication
					.getFCDotInitialDiam());

			// Erase draws.

			// rCtrl.drawNothing();

			// rCtrl.setDrawStyle(SwifteeApplication.DRAW_NONE, null,
			// identifier);

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
			// Don't handle edge touches immediately -- they may actually belong
			// to one of our
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

		if (mActivePointerId != INVALID_POINTER_ID && touchCount >= 2) {
			final int pointerIndex = ev.findPointerIndex(mActivePointerId);
			y = ev.getY(pointerIndex);
			x = ev.getX(pointerIndex);
		}

		// if (action != MotionEvent.ACTION_MOVE)
		// Toast.makeText(getContext(), "New touch event OV" + action + "," + x
		// + "," + y , Toast.LENGTH_SHORT ).show();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			/*
			 * If being flinged and user touches, stop the fling. isFinished
			 * will be false if being flinged.
			 */
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
				updateFC();
			}

			// Remember where the motion event started
			mLastMotionX = x;
			mLastMotionY = y;

			break;
		case MotionEvent.ACTION_MOVE:
			// Scroll to follow the motion event
			final int deltaY = (int) (mLastMotionY - y);
			final int deltaX = (int) (mLastMotionX - x);

			if (Math.abs(deltaX) > 1 || Math.abs(deltaY) > 1) {
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
			// Toast.makeText(mContext, "fling: " +
			// getScrollX() + "," + getScrollY() + "-" + initialVelocityX + ","
			// + initialVelocityY + "-" +
			// getWidth() + "," + getHeight(), Toast.LENGTH_SHORT).show();

			if ((Math.abs(initialVelocityY) > mMinimumVelocity)
					|| (Math.abs(initialVelocityX) > mMinimumVelocity)) {
				fling(-initialVelocityX, -initialVelocityY);
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
		}
		return true;
	}

	public void fling(int velocityX, int velocityY) {
		// Toast.makeText(mContext, "fling (2): XY: " +
		// getScrollX() + "," + getScrollY() + "- VXY: " + velocityX + "," +
		// velocityY + "- WH: " +
		// getWidth() + "," + getHeight(), Toast.LENGTH_LONG).show();

		/* ****************** CHANGED CODE ****************** */
		// FIXME: Remove touch point -> own function
		removeTouchPoint();
		// pointer.setImageResource(R.drawable.no_target_cursor);
		// cancelSelection();

		// FIXME: Change so that Size of WebView is adjusted instead of being
		// overlayed
		int r = fcPointerView.getRadius();
		int hd_1 = r;
		int hd_2 = 50 + r;

		// if (currentMenu.getVisibility() == VISIBLE)
		// hd_2 = -80;

		// FIXME
		/* ****************** CHANGED CODE END ****************** */
		mScroller.fling(getScrollX(), getScrollY(), velocityX, velocityY, -w
				/ 2 + r, w / 2 - r, -h / 2 + hd_1, h / 2 - hd_2);
		invalidate();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			// Toast.makeText(mContext, "scrollTo:" + mScroller.getCurrX() +
			// "," + mScroller.getCurrY(), Toast.LENGTH_SHORT).show();
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

			// FIXME: Update cursor image
			updateFC();

			// Keep on drawing until the animation has finished.
			postInvalidate();
		}
	}

	private void init(Context context) {
		// Save Context
		mContext = context;

		initScrollView();

		// Setup FC Views
		pointer = new ImageView(getContext());
		applyImageResource(R.drawable.kite_cursor, "kite_cursor");
		// pointer.setImageResource(R.drawable.kite_cursor);
		pointer.setScaleType(ImageView.ScaleType.CENTER);

		// pointer.setPadding(140, 140, 0, 0);
		// pointer.scrollTo(-140, -140);

		fcView = new FloatingCursorView(getContext());
		fcView.setRadius(FC_RADIUS);

		removeTouchPoint();

		fcPointerView = new FloatingCursorInnerView(getContext());
		fcPointerView.setRadius(INNER_RADIUS);
		// fcPointerView.setQuality(0);

		fcProgressBar = new CircularProgressBar(getContext(),
				(int) (RADIUS * 0.3f) - 6, this);

		int circRadius = (int) (RADIUS * 110 / 120);

		fcMainMenu = new MainMenu(context);
		fcMainMenu.setfcRadius(circRadius);
		fcMainMenu.setVisibility(INVISIBLE);
		fcMainMenu.setFloatingCursor(this);
		currentMenu = fcMainMenu;

		fcSettingsMenu = new SettingsMenu(context);
		fcSettingsMenu.setfcRadius(circRadius);
		fcSettingsMenu.setVisibility(INVISIBLE);
		fcSettingsMenu.setFloatingCursor(this);

		fcWindowTabs = new WindowTabs(context);
		fcWindowTabs.setfcRadius(circRadius);
		fcWindowTabs.setVisibility(INVISIBLE);
		// fcWindowTabs.setFloatingCursor(this);

		zoomView = new ZoomWebView(context);
		zoomView.setFloatingCursor(this);
		zoomView.setFCRadius(circRadius);
		zoomView.setVisibility(INVISIBLE);

		TemporaryView = new View(context); // ViewGroup(context);

		addView(fcView);
		addView(fcProgressBar);
		addView(fcPointerView);
		addView(pointer);
		addView(fcMainMenu);
		addView(fcSettingsMenu);
		addView(fcWindowTabs);
		addView(zoomView);
		addView(TemporaryView);

		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);

		handler = new Handler();

		/*
		 * runnableCheck = new Runnable() { public void run() { Log.v("cords",
		 * "x: " + pointer.getScrollX()+ "  y:" + pointer.getScrollX()); } };
		 */

		runnable = new Runnable() {

			public void run() {
				if (currentMenu.getVisibility() == VISIBLE && timerStarted) {
					toggleMenuVisibility();
					timerStarted = false;
				} else {
					timerStarted = true;
					handler.postDelayed(this, 10000);
				}
			}

		};

		parkingRunnable = new Runnable() {

			public void run() {
				
				if (!mP.isInParkingMode() && parkTimerStarted) {

					parkTimerStarted = false;
					if (mIsLoading) {
						fcView.startScaleUpAndRotateAnimation(100);
					}
							
					if (isLandingPage && !SwifteeApplication.getLandingShrinked()) {
						mP.enterParkingMode(false);
					} else {
						mP.enterParkingMode(true);
					}
					
					if (!mP.isTipsActivated()){
						updateFC();
					}
					
					checkFCParkingBounds();
					// Reomve the callback to avoid always running in the
					// background.
					handler.removeCallbacks(parkingRunnable);
				} else {
					parkTimerStarted = true;
					handler.postDelayed(this, 900); // Go parking mode timer
	// faster
				}
			}
		};

	}

	public FloatingCursor(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

		SwifteeApplication app = (SwifteeApplication) context
				.getApplicationContext();
		dbConnector = app.getDatabase();
	}

	public String getCurrentURL() {
		return mWebView.getUrl();
	}

	public String getCurrentTitle() {
		return mWebView.getTitle();
	}

	public boolean inLoad() {
		return mIsLoading;
	}

	public boolean getProgressEnabled() {
		boolean is = fcProgressBar.getIsEnabled();
		return is;
	}

	public void gestureDisableFC() {
		// mCanBeDisabled = true;
		// Before that works, we need to make sure that the hit area is no
		// longer clicked on
		// fcView.setVisibility(View.INVISIBLE);
		// disableFC();

		mIsDisabled = true;
		stopHitTest(0, 0, true);
		this.setVisibility(View.INVISIBLE);
	}

	public void gestureEnableFC() {
		mCanBeDisabled = false;
		if (mIsDisabled) {
			fcView.setVisibility(View.VISIBLE);
			removeTouchPoint();
			removeSelection();
			enableFC();
		}
	}

	public void disableFC() {
		mIsDisabled = true;
		cancelSelection();
		stopHitTest(0, 0, true);
		this.setVisibility(View.INVISIBLE);
	}

	public void enableFC() {
				
		 Throwable t = new Throwable(); StackTraceElement[] elements =
		 t.getStackTrace(); String calleeMethod =
		 elements[0].getMethodName(); String callerMethodName =
		 elements[1].getMethodName(); String callerClassName =
		 elements[1].getClassName(); Log.v("call",
		 "callerMethodName: "+callerMethodName+
		 " callerClassName: "+callerClassName );		 
		
		mIsDisabled = false;
		this.setVisibility(View.VISIBLE);
	}

	public void updateFC() {
		fcX = -(int) pointer.getScrollX() + -(int) getScrollX() + w / 2;
		fcY = -(int) pointer.getScrollY() + -(int) getScrollY() + h / 2;
	}

	public void updateMasterXY() {
		mX = (int) SwifteeApplication.getMasterX();
		mY = (int) SwifteeApplication.getMasterY();
	}

	public void enterParkingMode() {
		// Relocate the FC again into pointer in case was snapped to a side.
		if (xFlag == 1 || yFlag == 1) {
			// relocatePointerToFCView(SwifteeApplication.RELOCATE_FROM_POINTER);
			relocatePointerToFCView();
			pointer.scrollTo(0, 0);
		}
		// Scale down cursor
		fcView.setRadius(RADIUS * 1 / 3);
		// Reset the cursor.
		applyImageResource(R.drawable.kite_cursor, "kite_cursor");
		// pointer.setImageResource(R.drawable.kite_cursor);
	}

	/** COMPLETE **/
	public Rect rectSuggestion;
	private WebHitTestResult hitInputResult;
	private boolean setInputType;
	private boolean fromFC;
	private int lastIdentifier = -1;

	public void setWebView(WebView wv, boolean isFirst) {
		/*
		 * setTab() method is called only when isFirst = true.
		 */
		if (isFirst) {
			fcWindowTabs.setTab(wv);
		}

		mWebView = wv;
		mWebView.setDrawingCacheEnabled(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		mWebView.getSettings().setSaveFormData(true);

		mWebView.setWebChromeClient(new WebClient());
		mWebView.setWebViewClient(new GestureWebViewClient());
		fcMainMenu.setBackEabled(mWebView.canGoBack());
		fcMainMenu.setFwdEabled(mWebView.canGoForward());

		// suggestion
		mWebView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				int x;
				int y;			
				
				fromFC = false;
				
				boolean landingShrinked = SwifteeApplication.getLandingShrinked();
				
				if ( landingShrinked && !mSoftKeyboardVisible && !mP.isTipsActivated()) {							
					
					x = (int) event.getRawX();
					y = (int) event.getRawY();					
					fromFC=true;
					
				} else if ( lastKnownHitType == SwifteeApplication.TYPE_PADKITE_INPUT 
						|| cType == SwifteeApplication.TYPE_PADKITE_INPUT) {
					
					if (mP.isSuggestionActivated() || mP.isTipsActivated()){
						
						x = (int) event.getX();
						y = (int) event.getY();
					
					} else {
						
						x = (int) event.getRawX();
						y = (int) event.getRawY();
						
					}
					fromFC=true;
					
				} else {
					
					x = (int) event.getX();
					y = (int) event.getY();
					fromFC=false;
				}	
				
				stX = x;
				stY = y;

				setFingerTouch(true);

				boolean ret = false;

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					if (setInputType) {
						setInputType = false;
						return false;
					}
					
					int hitPK = getHitPKResult(x, y);				
					setHitPKIndex(hitPK);
					
					if (hitPK != SwifteeApplication.TABINDEX_NOTHING) {
							
						//ACA trae 1 en tab y tiene que traer 21
						
						//cType = getHitPKIndex();					
						actionTabHit(getHitPKIndex(), false);
						SwifteeApplication.setPKIndex(getHitPKIndex());
						
					} else if (getHitPKIndex() == SwifteeApplication.TABINDEX_NOTHING) {
						
						boolean hidden = SwifteeApplication.getLandingShrinked();				
						
						mWebView.invalidate();
						
						if (mP.isTipsActivated() || (mSoftKeyboardVisible == true && !hidden)) {
							ret = true;
							return true;
						}	
						
						mWebView.loadUrl("javascript:hover('" + x + "','" +  y + "','" +  WebHitTestResult.CALLBACK_SINGLE_TOUCH + "');");
															
					}
					
					checkIsAnchorTab(x, y);	 
					
				}

				/**
				 * SINGLE FINGER OPERATIONS PADKITE OBJECTS
				 */
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					switch (cType) {

						case SwifteeApplication.TYPE_PADKITE_INPUT: 
		
							if (SwifteeApplication.getLandingShrinked()){						
															
								String inputText = getLandingInputText();
								if (!inputText.equals("")) {
									mP.loadSuggestionIntoArray(inputText);
									
								}
								switchTabIndex(SwifteeApplication.TABINDEX_CERO);
								hitPKIndex = SwifteeApplication.TABINDEX_NOTHING;
								ceroPkInput(true);
								lastKnownHitType = WebHitTestResult.TYPE_EDIT_TEXT_TYPE;
								ret = true;
								
							} else {								
								//loadPage("javascript:setInputFocus()");					
								loadPage("javascript:toogleFCContainer()");							
							} 							
							break;						
						
						case SwifteeApplication.TYPE_PADKITE_TAB: 
							
							if (getLastKnownHitType()== WebHitTestResult.TYPE_SRC_ANCHOR_TYPE){
								cType = WebHitTestResult.TYPE_SRC_ANCHOR_TYPE; 											
							} //else if (getLastKnownHitType()== WebHitTestResult.EDIT_TEXT_TYPE){
								//cType = WebHitTestResult.EDIT_TEXT_TYPE; 					
							//}
							
							SwifteeApplication.setCType(cType);
							SwifteeApplication.setLastKnownHitType(getLastKnownHitType());				
							
							if (!mP.isTipsActivated()) {							
								rCtrl.setTabToTop(getHitPKIndex(), SwifteeApplication.DRAW_INPUT_TABS);								
								identifier = getHitPKIndex();							
								//int index = getTabIndexByHit(hitPKIndex);
								switchSuggestion(hitPKIndex, true);							
							}
									
							ret = true;
							break;			
						
											
						case SwifteeApplication.TYPE_PADKITE_ROW: 
							
								if (getLastKnownHitType()== WebHitTestResult.TYPE_SRC_ANCHOR_TYPE){
									cType = WebHitTestResult.TYPE_SRC_ANCHOR_TYPE; 											
								} 
								
								//else if (getLastKnownHitType()== WebHitTestResult.EDIT_TEXT_TYPE){
								//cType = WebHitTestResult.EDIT_TEXT_TYPE; 					
								//}
								
								SwifteeApplication.setCType(cType);
								SwifteeApplication.setLastKnownHitType(getLastKnownHitType());	
								switchTabIndex(SwifteeApplication.getActiveTabIndex());
								rowOver(getHitPKIndex(), SwifteeApplication.getPKTabIndex());								
								ret = true;
								break;						
						
						case SwifteeApplication.TYPE_PADKITE_BUTTON:
							
							buttonOver(getHitPKIndex());
							
							if (getLastKnownHitType()== WebHitTestResult.TYPE_SRC_ANCHOR_TYPE){
								cType = WebHitTestResult.TYPE_SRC_ANCHOR_TYPE;
								SwifteeApplication.setCType(WebHitTestResult.TYPE_SRC_ANCHOR_TYPE);								
							} else if (getLastKnownHitType()== WebHitTestResult.TYPE_EDIT_TEXT_TYPE){
								cType = WebHitTestResult.TYPE_EDIT_TEXT_TYPE;
								SwifteeApplication.setCType(WebHitTestResult.TYPE_EDIT_TEXT_TYPE);
							}	
							
							ret = true;
							break;						
						
						case SwifteeApplication.TYPE_PADKITE_BACKGROUND: 
							hitBack(getHitPKIndex());
							ret = true;
							break;						
			
						case SwifteeApplication.TYPE_PADKITE_TIP_BUTTON: 
							tCtrl.setButtonOver(cType);
							ret = true;
							break;						
						
						case WebHitTestResult.TYPE_SRC_ANCHOR_TYPE:
							ret = false;
							break;
							
						case SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER:
						case SwifteeApplication.TYPE_PADKITE_PANEL: 
							ret = true;
							break;						
					}
				}
				
				else if (event.getAction() == MotionEvent.ACTION_UP) {
					
					if (setInputType) {
						setInputType = false;
						return false;
					}
					
					switch (cType) {
					
						case SwifteeApplication.TYPE_PADKITE_PANEL:
							setHitPKIndex(SwifteeApplication.TABINDEX_NOTHING);
							setLastKnownHitType(WebHitTestResult.TYPE_SRC_ANCHOR_TYPE);
							setRowsHeightAndCount(SwifteeApplication.TABINDEX_CERO,	mP.arrayCERO, true);
							ceroPKPanel(true);
							ret = true;
							break;						
						
						case WebHitTestResult.TYPE_EDIT_TEXT_TYPE: 
							setLastKnownHitType(resultType);
							// HERE LOAD PASTE, RECENT ETC
							sendEvent(MotionEvent.ACTION_DOWN, x, y, false);
							sendEvent(MotionEvent.ACTION_UP, x, y, false);							
							ret = true;
							break;						
			
						case SwifteeApplication.TYPE_PADKITE_INPUT: 			
							if (SwifteeApplication.getLandingShrinked()){								
								setRowsHeightAndCount(SwifteeApplication.TABINDEX_CERO,	mP.arrayCERO, true);													
								sendEvent(MotionEvent.ACTION_DOWN, x, y, false);
								sendEvent(MotionEvent.ACTION_UP, x, y, false);								
								removeTouchPoint();				
							} 							
							ret = true;
							break;										
										
						case SwifteeApplication.TYPE_PADKITE_TAB:
							ret = true;
							/*if (!mP.isTipsActivated()) {
								onTouchUp(true);
							}*/
							break;
												
						case SwifteeApplication.TYPE_PADKITE_BUTTON:							
							String iT = mP.getInputBoxText();
							if (!iT.equals("")) {
								onTouchUp(true);
							} 
							ret = false;
							break;						
						
						case SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER:							
							setHitPKIndex(SwifteeApplication.TABINDEX_NOTHING);
							setLastKnownHitType(WebHitTestResult.TYPE_SRC_ANCHOR_TYPE);
							SwifteeApplication.setExpanded(true);	
							//setFocusNodeAtRect(rect);
							ceroPKSet(true);
							ret = true;
							break;						
							
						case WebHitTestResult.TYPE_SRC_ANCHOR_TYPE:	
							handler.removeCallbacks(longTouchRunnable);
							mLongTouchCheck = false;
							/*setHitPKIndex(SwifteeApplication.TABINDEX_NOTHING);
							setLastKnownHitType(WebHitTestResult.SRC_ANCHOR_TYPE);
							SwifteeApplication.setExpanded(true);					
							setFocusNodeAtRect(rect);
							ceroAnchor(true, false);*/
							ret = false;						
							//setFingerTimers(mWebHitTestResult.getType(),  mWebHitTestResult.getIdentifier());
							//onTouchUp(true);
							break;				
							
						case SwifteeApplication.TYPE_PADKITE_ROW:
							onTouchUp(true);
							ret = true;
							break;
							
						case SwifteeApplication.TYPE_PADKITE_TIP_BUTTON:
							onTouchUp(true);
							ret = true;
							break;
						
					}
					
					setFingerTouch(false);
				}

				/**
				 * MOVE CANCELS THE LONG TOUCH HACK.
				 */
				else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					handler.removeCallbacks(longTouchRunnable);
					mLongTouchCheck = false;
					ret = true;
				}

				return ret;

			}

		});

		/*
		 * webView.setWebViewClient(new WebViewClient() {
		 * 
		 * @Override public boolean shouldOverrideKeyEvent(WebView view,
		 * KeyEvent event) { if ((event.getKeyCode() == KeyEvent.KEYCODE_BACK)
		 * && view.canGoBack()) { view.goBack(); return true; }
		 * 
		 * return super.shouldOverrideKeyEvent(view, event); }
		 * 
		 * 
		 * });
		 */

	}
	
	
	public void proxyTouchHitTest(WebHitTestResult result){		
			
		//mWebHitTestResult = mWebView.getHitTestResultAt(x, y);			
		
		resultType = result.getType();					
		
		if (resultType != 0 && resultType != 1 && resultType != -1) {
			
			cType=resultType; 
			rect = result.getRect();							
			SwifteeApplication.setMasterRect(rect);					
			identifier = result.getIdentifier();		
			SwifteeApplication.setIdentifier(identifier);
			mWebHitTestResultIdentifer = identifier;
			
			if (resultType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE
					|| resultType == WebHitTestResult.TYPE_ANCHOR_TYPE
					|| resultType == WebHitTestResult.TYPE_SRC_IMAGE_ANCHOR_TYPE) {
				
				selectedLink = result.getExtra();
				selectedHref = result.getHref();
			
				if (selectedLink.equals("")) {
					checkAnchorLinks(selectedHref);
				} else {
					checkAnchorLinks(selectedLink);
				}
			
			} else if (resultType == WebHitTestResult.TYPE_EDIT_TEXT_TYPE) {
				if (isLandingPage) {
					cType = SwifteeApplication.TYPE_PADKITE_INPUT;									
				}
			}
			
			setLastKnownHitType(resultType);
			
			if (rect.left == 0) {
				Log.v("fuck", "fuck");
			}
		}	
	
	}
	
	private void checkIsAnchorTab(int x, int y){
		
		if (!
				(getLastKnownHitType()== WebHitTestResult.TYPE_SRC_ANCHOR_TYPE
				&& cType == SwifteeApplication.TYPE_PADKITE_TAB)
				//|| 
				//(getLastKnownHitType()== WebHitTestResult.EDIT_TEXT_TYPE 
				//&& cType == WebHitTestResult.EDIT_TEXT_TYPE)						
				){
			
				SwifteeApplication.setCType(cType);
				SwifteeApplication.setLastKnownHitType(getLastKnownHitType());		
				
				if (cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE){
					
					if (lastIdentifier != identifier){
						anX = x;
						anY = y;
						lastIdentifier = identifier;
						anchorTab = false;
					}
					
				}			
				
		} else {			
			anchorTab= true;
			
		}		
	}

	public boolean isFromFC() {
		return fromFC;
	}

	public int getHitPKIndex() {
		return hitPKIndex;
	}
	

	public int getHitPKResult(float x, float y) {

		int active = 0;
		boolean check;

		int X = (int) x;
		int Y = (int) y;

		Rect tR0 = SwifteeApplication.getTab_0_Rect();
		check = checkTouchOnDraw(tR0, X, Y);
		if (check){
			SwifteeApplication.setActiveTabIndex(0);	
			return 0;
		}

		Rect tR1 = SwifteeApplication.getTab_1_Rect();
		check = checkTouchOnDraw(tR1, X, Y);
		if (check){
			SwifteeApplication.setActiveTabIndex(1);
			return 1;
		}

		Rect tR2 = SwifteeApplication.getTab_2_Rect();
		check = checkTouchOnDraw(tR2, X, Y);
		if (check){
			SwifteeApplication.setActiveTabIndex(2);
			return 2;
		}

		Rect tR3 = SwifteeApplication.getTab_3_Rect();
		check = checkTouchOnDraw(tR3, X, Y);
		if (check){
			SwifteeApplication.setActiveTabIndex(3);		
			return 3;
		}
		
		Rect tR4 = SwifteeApplication.getTab_4_Rect();
		check = checkTouchOnDraw(tR4, X, Y);
		if (check){
			SwifteeApplication.setActiveTabIndex(4);
			return 4;
		}

		if (mP.isSuggestionActivated()) {

			Rect bR0 = SwifteeApplication.getSuggestionButton_0_Rect();
			check = checkTouchOnDraw(bR0, X, Y);
			if (check)
				return 20;

			Rect bR1 = SwifteeApplication.getSuggestionButton_1_Rect();
			check = checkTouchOnDraw(bR1, X, Y);
			if (check)
				return 21;

			Rect bR2 = SwifteeApplication.getSuggestionButton_2_Rect();
			check = checkTouchOnDraw(bR2, X, Y);
			if (check)
				return 22;

			Rect bR3 = SwifteeApplication.getSuggestionButton_3_Rect();
			check = checkTouchOnDraw(bR3, X, Y);
			if (check)
				return 23;

			Rect bR4 = SwifteeApplication.getSuggestionButton_4_Rect();
			check = checkTouchOnDraw(bR4, X, Y);
			if (check)
				return 24;

			Rect bR5 = SwifteeApplication.getSuggestionButton_5_Rect();
			check = checkTouchOnDraw(bR5, X, Y);
			if (check)
				return 25;

			if (BrowserActivity.isSuggestionListActivated()) {

				Rect rR0 = SwifteeApplication.getSuggestionRow_0_Rect();
				check = checkTouchOnDraw(rR0, X, Y);
				if (check)
					return 10;
				
				Rect rR1 = SwifteeApplication.getSuggestionRow_1_Rect();
				check = checkTouchOnDraw(rR1, X, Y);
				if (check)
					return 11;

				Rect rR2 = SwifteeApplication.getSuggestionRow_2_Rect();
				check = checkTouchOnDraw(rR2, X, Y);
				if (check)
					return 12;

				Rect rR3 = SwifteeApplication.getSuggestionRow_3_Rect();
				check = checkTouchOnDraw(rR3, X, Y);
				if (check)
					return 13;

				Rect rR4 = SwifteeApplication.getSuggestionRow_4_Rect();
				check = checkTouchOnDraw(rR4, X, Y);
				if (check)
					return 14;

				Rect rR5 = SwifteeApplication.getSuggestionRow_5_Rect();
				check = checkTouchOnDraw(rR5, X, Y);
				if (check)
					return 15;
			}		
			
			Rect back2 = SwifteeApplication.getBottomFormButton_2_Rect();
			check = checkTouchOnDraw(back2, X, Y);
			if (check)
				return 32;
			
			Rect back0 = SwifteeApplication.getBottomFormButton_0_Rect();
			check = checkTouchOnDraw(back0, X, Y);
			if (check)
				return 30;

			Rect back1 = SwifteeApplication.getBottomFormButton_1_Rect();
			check = checkTouchOnDraw(back1, X, Y);
			if (check)
				return 31;			
			
		}

		if (mP.isTipsActivated()) {

			Rect tipButtonRect = SwifteeApplication.getTipButton_Rect();

			check = checkTouchOnDraw(tipButtonRect, X, Y);
			if (check)
				return 40;
		}

		return -1;
	}

	private boolean checkTouchOnDraw(Rect re, int x, int y) {
		if (
			x >= re.left 
			&& 
			x < (re.left + (re.right - re.left)) 
			&& 
			y >= re.top
			&& 
			y < (re.top + (re.bottom - re.top))
		) {
			return true;
		}
		return false;
	}

	public WebView getWebView() {
		return mWebView;
	}

	/*public void setFocusNodeAt(int x, int y) {
		mWebView.focusNodeAt(x, y);
	}*/

	/*public void setFocusNodeAtRect(Rect re) {
		int x = re.left + 10;
		int y = re.top + 10;
		mWebView.focusNodeAt(x, y);
	}*/

	public void touchDownUpAtMasterRect(Rect re) {
		int x = re.left + 10;
		int y = re.top + 10;
		sendEvent(MotionEvent.ACTION_DOWN, x, y, false);
		sendEvent(MotionEvent.ACTION_UP, x, y, false);
	}

	public void setEventViewerArea(EventViewerArea eventViewer) {
		this.eventViewer = eventViewer;
		eventViewer.setWindowTabs(fcWindowTabs);
		fcMainMenu.setEventViewer(eventViewer);
		fcWindowTabs.setEventViewer(eventViewer);
	}

	public void setParent(BrowserActivity p, RingController rC,
			TipController tC, PointerHolder pH, SuggestionController sC,
			TextCursorHolder tH) {
		
		mP = p;
		fcMainMenu.setParent(mP);
		fcSettingsMenu.setParent(mP);
		fcWindowTabs.setParent(mP);
		rCtrl = rC;
		tCtrl = tC;
		pHold = pH;
		sCtrl = sC;
		tHold = tH;		
	}

	/**
	 * 
	 * @param index
	 *            is menu index currently displayed such as 0 for main menu,1
	 *            for settings menu,2 for window tabs
	 */
	public void setCurrentMenu(int index) {

		switch (index) {

		case 0:
			currentMenu = fcMainMenu;
			fcMainMenu.setVisibility(VISIBLE);
			fcSettingsMenu.setVisibility(INVISIBLE);
			fcWindowTabs.setVisibility(INVISIBLE);
			fcView.setVisibility(INVISIBLE);
			if (currentMenu instanceof CircularLayout) {
				((CircularLayout) currentMenu).resetMenu();
				((CircularLayout) currentMenu).drawHotTip();
			}
			break;
		case 1:
			currentMenu = fcSettingsMenu;
			fcSettingsMenu.setVisibility(VISIBLE);
			fcSettingsMenu.requestFocus();
			fcMainMenu.setVisibility(INVISIBLE);
			fcWindowTabs.setVisibility(INVISIBLE);
			fcView.setVisibility(INVISIBLE);
			if (currentMenu instanceof CircularLayout) {
				((CircularLayout) currentMenu).resetMenu();
				((CircularLayout) currentMenu).drawHotTip();
			}
			break;
		case 2:
			currentMenu = fcWindowTabs;
			fcWindowTabs.setVisibility(VISIBLE);
			fcSettingsMenu.setVisibility(INVISIBLE);
			fcMainMenu.setVisibility(INVISIBLE);
			fcView.setVisibility(INVISIBLE);
			if (currentMenu instanceof CircularTabsLayout) {
				((CircularTabsLayout) currentMenu).resetMenu();
				((CircularTabsLayout) currentMenu).drawHotTip();
			}
			break;
		}
	}

	public void drawTip() {
		if (currentMenu instanceof CircularTabsLayout) {
			((CircularTabsLayout) currentMenu).drawHotTip();
		}
	}

	public int getCurrentMenu() {
		int current = 0;
		if (currentMenu == fcMainMenu) {
			current = 0;
		} else if (currentMenu == fcSettingsMenu) {
			current = 1;
		}
		if (currentMenu == fcWindowTabs) {
			current = 2;
		}
		return current;
	}

	public void enableProgressBar() {
		fcProgressBar.enable();
	}

	public void disableProgressBar() {
		fcProgressBar.enable();
	}

	public void addNewWindow(boolean useSelection, boolean background) {
		if (getWindowCount() > 7) {
			AlertDialog alertDialog;
			alertDialog = new AlertDialog.Builder(mP).create();
			alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			alertDialog
					.setMessage("You have reached the limit of windows. Please close one in the Windows Manager.");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// mP.finish();
				}
			});
			alertDialog.show();
			return;
		}

		removeSelection();

		if (useSelection && selectedLink != "") {
			fcWindowTabs.addWindow(selectedLink, background);
		} else {
			fcWindowTabs.addWindow("", false);
		}

		selectedLink = "";
	}

	public void addNewWindow(String url) {
		removeSelection();

		if (selectedLink != null) {
			fcWindowTabs.addWindow(url, false);
		} else {
			fcWindowTabs.addWindow("", false);
		}
		selectedLink = "";
	}

	public int getWindowCount() {
		return fcWindowTabs.getWindowCount();
	}

	protected boolean isFCOutofBounds() {
		int r = fcView.getRadius();
		r = r / 2;

		if (fcX - r < 0)
			return true;
		else if (fcX + r > this.w)
			return true;

		if (fcY - (r + 50) < 0)
			return true;
		else if (fcY + r > this.h)
			return true;

		return false;
	}

	protected void checkFCParkingBounds() {
		int r = fcView.getRadius();
		r = r / 2;

		int dx = 0;
		int dy = 0;

		if (fcX - r < 0)
			dx = fcX - r;
		else if (fcX + r > this.w)
			dx = (fcX + r) - this.w;

		if (fcY - (r + 50) < 0)
			dy = fcY - (r + 50);
		else if (fcY + r > this.h)
			dy = (fcY + r) - this.h;

		scrollBy(dx, dy);

		// Update fc coordinates
		updateFC();
		invalidate();
	}
	
	public void invalidateWebView(){
		mWebView.invalidate();
	}
	
	public void invalidateAll(){
		mWebView.invalidate();	
		sCtrl.invalidate();
		rCtrl.invalidate();
		fcView.invalidate();
		fcPointerView.invalidate();
		this.invalidate();	
	}

	protected void checkFCMenuBounds() {
		final int r = fcView.getRadius();

		int dx = 0;
		int dy = 0;

		if (fcX - r < 0)
			dx = fcX - r;
		else if (fcX + r > this.w)
			dx = (fcX + r) - this.w;

		if (fcY - (r + 50) < 0)
			dy = fcY - (r + 50);
		else if (fcY + r > this.h)
			dy = (fcY + r) - this.h;

		// Abort fling animation
		mScroller.forceFinished(true);
		scrollBy(dx, dy);

		// Update fc coordinates
		updateFC();
		invalidate();
	}

	public boolean isMenuVisible() {
		return (currentMenu.getVisibility() == VISIBLE);
	}

	public void hideMenuFast() {
		eventViewer.setMode(EventViewerArea.TEXT_ONLY_MODE);
		currentMenu.setVisibility(INVISIBLE);
		fcView.setVisibility(View.VISIBLE);
		mMenuDown = false;

		currentMenu = fcMainMenu;

		// Reset FC
		applyImageResource(R.drawable.kite_cursor, "kite_cursor");
		// pointer.setImageResource(R.drawable.kite_cursor);
		removeTouchPoint();
	}

	public void toggleMenuVisibility() {

		tCtrl.drawNothing();

		if (animationLock)
			return;

		animationLock = true;

		// eventViewer.setMode(EventViewerArea.TEXT_ONLY_MODE);
		AlphaAnimation menuAnimation;

		// Reset FC
		removeTouchPoint();

		if (currentMenu.getVisibility() == INVISIBLE) {
			
			
			if (SwifteeApplication.getOrientation()==SwifteeApplication.ORIENTATION_LANDSCAPE){
				//int w = SwifteeApplication.getScreenWidth();
				//int h = SwifteeApplication.getScreenHeight();
				scrollTo( 0, 50);
			}

			// Reset menu to Main menu (as WM sometimes gives wrong occurence)
			currentMenu = fcMainMenu;

			// FC was touched, get out of parking mode
			if (mP.isInParkingMode()) {
				mP.exitParkingMode();
				fcView.setRadius(FC_RADIUS); // Restore radius size
			}

			applyImageResource(R.drawable.kite_cursor, "kite_cursor");
			// pointer.setImageResource(R.drawable.kite_cursor);
			mMenuDown = true;
			fcView.setVisibility(View.INVISIBLE);
			checkFCMenuBounds();

			menuAnimation = new AlphaAnimation(0.0f, 1.0f);
			menuAnimation.setDuration(250);
			menuAnimation.setAnimationListener(new AnimationListener() {

				public void onAnimationEnd(Animation animation) {
					currentMenu.setVisibility(VISIBLE);
					animationLock = false;
					handler.postDelayed(runnable, 10000);
					handler.removeCallbacks(parkingRunnable);

					if (currentMenu instanceof CircularLayout) {
						((CircularLayout) currentMenu).resetMenu();
						((CircularLayout) currentMenu).drawHotTip();
						//Log.d("Reset menu", "---------------------------");
					}
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationStart(Animation animation) {
				}

			});
			currentMenu.startAnimation(menuAnimation);
			vibrator.vibrate(25);

			/*
			 * if (currentMenu instanceof CircularLayout)
			 * eventViewer.setText(((CircularLayout) currentMenu).getName());
			 * else if (currentMenu instanceof CircularTabsLayout)
			 * eventViewer.setText(((CircularTabsLayout)
			 * currentMenu).getName());
			 */

			// mP.setTopBarVisibility(VISIBLE);
			// mP.setTopBarMode(TopBarArea.ADDR_BAR_MODE);

		} else if (currentMenu.getVisibility() == VISIBLE) {

			mMenuDown = false;
			applyImageResource(R.drawable.kite_cursor, "kite_cursor");
			// pointer.setImageResource(R.drawable.kite_cursor);
			fcView.setVisibility(VISIBLE);

			menuAnimation = new AlphaAnimation(1.0f, 0.0f);
			menuAnimation.setDuration(250);
			menuAnimation.setAnimationListener(new AnimationListener() {
				public void onAnimationEnd(Animation animation) {
					currentMenu.setVisibility(INVISIBLE);
					currentMenu = fcMainMenu;
					animationLock = false;
					handler.removeCallbacks(runnable);
					// runnableKiteAni.start();
				}
				public void onAnimationRepeat(Animation animation) {}
				public void onAnimationStart(Animation animation) {}
			});
			
			currentMenu.startAnimation(menuAnimation);
			vibrator.vibrate(25);
			// eventViewer.setText("");
			// mP.setTopBarVisibility(INVISIBLE);
			// Since menu is hidden we start the timer to park the PadKite.
			handler.removeCallbacks(parkingRunnable);
			handler.post(parkingRunnable);
		}
	}
	

	public boolean isAnimationLock() {
		return animationLock;
	}

	public void setAnimationLock(boolean animationLock) {
		this.animationLock = animationLock;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		setXYPosition(w / 2, h / 2);
		scrollTo(0, 0);

		if (mP.isInParkingMode()) {
			mP.exitParkingMode();
			fcView.setRadius(FC_RADIUS);
		}

		this.w = w;
		this.h = h;
		
		if (w == oldw && h != oldh) {
			
			if (h < oldh) { // Soft Keyboard now visible
				
				sofwareKeyboardVisible();
				
			} else { // Soft Keyboard now hidden
				
				sofwareKeyboardInVisible();
				
			}
		}

		// Restart timer to park the mouse if needed.
		if ((oldw != 0 || oldh != 0) && !isMenuVisible()) {
			handler.removeCallbacks(parkingRunnable);
			handler.post(parkingRunnable);
		}
		// Log.e("OnSizeChanged:(w,h,ow,oh)","("+w+","+h+","+oldw+","+oldh+")"
		// );

		// Log.d("OnSizeChanged:(w,h)","("+w+","+h+")" );
	}

	private void sofwareKeyboardVisible(){		
		mSoftKeyboardVisible = true;
		disableFC();		
	}
	
	private void sofwareKeyboardInVisible(){
		mSoftKeyboardVisible = false;				
		if (!mP.isTipsActivated()) {
			enableFC();
			eraseDraws();
			if (SwifteeApplication.getLandingShrinked()){   				
				mP.enterParkingMode(false);
				mWebView.loadUrl("javascript:toogleFCContainer()");			
			}
		}	
	}
	
	public void setXYPosition(int x, int y) {
		fcView.setPosition(x, y);
		fcPointerView.setPosition(x, y);
		fcProgressBar.setPosition(x, y);
		fcMainMenu.setPosition(x, y);
	}

	public int getFCViewXPosition() {
		return fcView.getLeft();
	}

	public int getFCViewYPosition() {
		return fcView.getTop();
	}

	public void setFCViewVisible() {
		fcView.setVisibility(View.VISIBLE);
	}

	private boolean mHandleTouch = false;

	// FIXME: TODO: Put this into their own classes
	// private boolean mSelectionMode = false;
	private boolean mHitTestMode = false;

	private boolean mSelectionStarted = false;
	private int selX = -1, selY = -1;
	private boolean mGesturesEnabled = false;

	private WebHitTestResult mWebHitTestResult;
	private int mWebHitTestResultType = -1;
	private int mWebHitTestResultIdentifer = -1;

	private  AsyncTask touchInput;
	private MotionEvent me;
		
	Instrumentation inst = new Instrumentation();
	
	public void sendEvent(int action, int X, int Y, boolean instrumentation) {
		
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis();

		me = MotionEvent.obtain(downTime, eventTime, action, X, Y, 0);
		if (instrumentation){	
			
			Object[] params = new Object[3];
			
			params[0] = X;
			params[1] = Y;
			params[2] = action;
 			
			instrumentationEvent = new InstrumentationEvent();
			instrumentationEvent.execute(params);
			
		} else {
			mWebView.onTouchEvent(me);
		}
		me.recycle();
		me = null;
	}
	
	private AsyncTask instrumentationEvent = new InstrumentationEvent(); 
	
	 public class InstrumentationEvent extends AsyncTask  {    
		 
		@Override
		protected Object doInBackground(Object... arg) {
			
			long downTime = SystemClock.uptimeMillis();
			long eventTime = SystemClock.uptimeMillis();	
			
			Object[] arguments = arg;
			
			int X = (Integer) arguments[0];
			int Y = (Integer) arguments[1];			
			int action = (Integer) arguments[2];
			
			MotionEvent instEvent = MotionEvent.obtain(downTime, eventTime, action, X, Y, 0);
			
			Instrumentation inst = new Instrumentation();
			
			inst.sendPointerSync(instEvent);	
			
			instEvent.recycle();
			instEvent = null;
			
	    	return true;
	    }		
	} 
	

	protected void stopHitTest(int X, int Y, boolean setIcon) {
		
		if (mHitTestMode) {

			// FIXME: ?
			// sendEvent(MotionEvent.ACTION_MOVE, X, Y);
			// sendEvent(MotionEvent.ACTION_UP, 0, 0);

			sendEvent(MotionEvent.ACTION_CANCEL, X, Y, false);
			if (setIcon)
				applyImageResource(R.drawable.kite_cursor, "kite_cursor");
			// pointer.setImageResource(R.drawable.kite_cursor);

			mWebHitTestResultIdentifer = -1;
			mHitTestMode = false;
		}
	}

	/**
	 * HitTestResult
	 * 
	 * @param X
	 * @param Y
	 *            --------------------------------------------- No Target
	 *            UNKNOWN_TYPE = 0 Phone PHONE_TYPE = 2 Note: Phone not working,
	 *            returning 0 however link is there and opens phone app. Geo
	 *            GEO_TYPE = 3 Mail EMAIL_TYPE = 4 Image IMAGE_TYPE = 5 Image
	 *            Link IMAGE_ANCHOR_TYPE = 6 Note: Implemented image into link
	 *            as image. It can be selected or executed. Text link
	 *            ANCHOR_TYPE = 7 Image link SRC_IMAGE_ANCHOR_TYPE = 8 Input
	 *            text EDIT_TEXT_TYPE = 9 Note: TODO check != "" to set text
	 *            edition cursor. Video VIDEO_TYPE = 10 Note: HTML5 tags only.
	 *            WebVideoInfo videoInfo = mWebHitTestResult.getVideoInfo();
	 *            Text TEXT_TYPE = 11
	 *            --------------------------------------------- Button
	 *            INPUT_TYPE = 12 CheckBox INPUT_TYPE = 12 RadioButon INPUT_TYPE
	 *            = 12 ComboBox SELECT_TYPE = 13
	 *            ---------------------------------------------
	 */

	private int cType;

	public int getcType() {
		return cType;
	}

	private int resultType;
	private int identifier;

	private int idWm;
	private String nameWm;

	private int hitPKIndex = SwifteeApplication.TABINDEX_NOTHING;
	private int activeTabIdentifier;

	private int lastKnownHitType;

	public int getlastKnownHitType() {
		return getLastKnownHitType();
	}

	private String textClipboard;
	private String temporaryClipboard;

	private int wmId;

	private boolean fCisMoving;

	private int extraIdentifier;

	public void moveHitTest(int X, int Y) {

		if (mHitTestMode) {

			/*
			 Throwable t = new Throwable(); StackTraceElement[] elements =
			 t.getStackTrace(); String calleeMethod =
			 elements[0].getMethodName(); String callerMethodName =
			 elements[1].getMethodName(); String callerClassName =
			 elements[1].getClassName(); Log.v("call",
			 "callerMethodName: "+callerMethodName+
			 " callerClassName: "+callerClassName );
			 */

			fCisMoving = true;

			fcX = X;
			fcY = Y;

			Log.v("cord", "fcX: " + fcX + " fcY: " + fcY);

			//mWebHitTestResult = mWebView.getHitTestResultAt(X, Y);
			
			mWebView.loadUrl("javascript:hover('" + X + "','" +  Y + "','" +  WebHitTestResult.CALLBACK_MOVEHIT  + "');");

			if (mP.isTabsActivated()
					&& getHitPKIndex() == SwifteeApplication.TABINDEX_NOTHING) {

				setHitPKIndex(getHitPKResult(X, Y));
				
				int pkIndex = getHitPKIndex();

				SwifteeApplication.setPKIndex(pkIndex);

				Log.v("hitPKIndex", "hitPKIndex: " + getHitPKIndex());

				if (getHitPKIndex() != SwifteeApplication.TABINDEX_NOTHING) {

					//int cursorTabImage = R.drawable.tab_cursor;
					//pointer.setImageResource(cursorTabImage);
					
					if (pkIndex<=4){ //TAB
						if ( lastIndex != pkIndex ) {				
							actionTabHit(getHitPKIndex(), true);
							lastIndex = hitPKIndex;				
						} 	
					} else {
						actionTabHit(getHitPKIndex(), true);
					}
					
					setHitPKIndex(SwifteeApplication.TABINDEX_NOTHING);

					return;
				}
			}		
			
		}	

			
	}; // End Of HitTestResutl
	
	
	public void proxyMoveHitTest(WebHitTestResult result){
		
		resultType = result.getType();
		if (resultType != 0 && resultType != 1 && resultType != -1) {
			rect = result.getRect();
			SwifteeApplication.setMasterRect(rect);
			if (rect.left == 0) {
				Log.v("fuck", "fuck");
			}
		} else {
			
			if (getLastKnownHitType() == SwifteeApplication.TYPE_PADKITE_PANEL){
				String panelText_default = "PERSONALIZED PANEL";
				String colorId_default = "-1";
				loadPage("javascript:switchPanel('" + panelText_default + "', '" + colorId_default + "');");
			}	
			
			//nothing
			cType = resultType;
			
		}	
		
		setFingerTouch(false);
		mWebHitTestResult = result;
		identifier = result.getIdentifier();
		SwifteeApplication.setIdentifier(identifier);
		selectedLink = result.getExtra();
		selectedHref = result.getHref();

		// Link Tittle Message
		final Message msg = mHandler
				.obtainMessage(FOCUS_NODE_HREF, 0, 0, 0);
		mWebView.requestFocusNodeHref(msg);

		int cursorImage = 0;

		// Single Finger: reset timers on change identifier.
		if (SwifteeApplication.getFingerMode()) {
			resetTimersOnChangeId(identifier);
		} else {
			// Set color ring to permanent blue if multifinger.
			//rCtrl.paintRingBlue();
		}

		switch (resultType) {

		case WebHitTestResult.TYPE_PHONE_TYPE: {
			cType = 2; // Phone
			resultType = WebHitTestResult.TYPE_ANCHOR_TYPE;
			applyImageResource(R.drawable.phone_cursor, "phone_cursor");
			setLastKnownHitType(cType);
			break;
		}

		case WebHitTestResult.TYPE_TEXT_TYPE: {
			cType = 11; // Text
			String imageName = (String) pointer.getTag();
			if (imageName != "select_text_cursor") {
				applyImageResource(R.drawable.text_cursor, "text_cursor");
			}
			// setPointerTextSize();
			setLastKnownHitType(cType);
			break;
		}

		case WebHitTestResult.TYPE_VIDEO_TYPE: {
			cType = 10; // Video HTML5
			resultType = WebHitTestResult.TYPE_ANCHOR_TYPE;
			applyImageResource(R.drawable.video_cursor, "video_cursor");
			setLastKnownHitType(cType);
			break;
		}

		case WebHitTestResult.TYPE_SRC_ANCHOR_TYPE:
		case WebHitTestResult.TYPE_ANCHOR_TYPE: {
			cType = 7; // Text link
			resultType = WebHitTestResult.TYPE_ANCHOR_TYPE;
			applyImageResource(R.drawable.link_cursor, "link_cursor");
			String tooltip = result.getToolTip();

			// CHECK LINKS
			/*
			 * if (resultType ==WebHitTestResult.SRC_ANCHOR_TYPE){
			 * checkAnchorLinks(selectedHref); } else {
			 * checkAnchorLinks(selectedLink); }
			 */

			if (selectedLink.equals("")) {
				checkAnchorLinks(selectedHref);
			} else {
				checkAnchorLinks(selectedLink);
			}

			if (tooltip.length() > 10) {
				// eventViewer.splitText(WebHitTestResult.ANCHOR_TYPE,tooltip);
			}
			setLastKnownHitType(cType);				
			
			checkIsAnchorTab(fcX, fcY);				
			
			break;
		}

		case WebHitTestResult.TYPE_EDIT_TEXT_TYPE: {
			cType = 9; // Input text
			resultType = WebHitTestResult.TYPE_ANCHOR_TYPE;
			applyImageResource(R.drawable.keyboard_cursor, "keyboard_cursor");
			setLastKnownHitType(cType);
			if (isLandingPage) {
				cType = SwifteeApplication.TYPE_PADKITE_INPUT;
			}
			break;
		}

		case WebHitTestResult.TYPE_INPUT_TYPE: {
			cType = 12; // Button
			resultType = WebHitTestResult.TYPE_ANCHOR_TYPE;
			applyImageResource(R.drawable.link_button_cursor, "link_button_cursor");
			// cursorImage = R.drawable.link_button_cursor;
			setLastKnownHitType(cType);
			break;
		}

		case WebHitTestResult.TYPE_SELECT_TYPE: {
			cType = 13; // ComboBox
			resultType = WebHitTestResult.TYPE_ANCHOR_TYPE;
			applyImageResource(R.drawable.link_combo_cursor,
			"link_combo_cursor");
			setLastKnownHitType(cType);
			break;
		}

		case WebHitTestResult.TYPE_SRC_IMAGE_ANCHOR_TYPE: {
			cType = 8; // Image link
			resultType = WebHitTestResult.TYPE_ANCHOR_TYPE;
			applyImageResource(R.drawable.link_image_cursor, "link_image_cursor");

			if (selectedHref.contains("padkite.local.panel")) {
				cType = SwifteeApplication.TYPE_PADKITE_PANEL;
				applyImageResource(R.drawable.panel_cursor, "panel_cursor");
				// cursorImage = R.drawable.panel_cursor;
			}			
			
			setLastKnownHitType(cType);
			break;
		}

		case WebHitTestResult.TYPE_IMAGE_ANCHOR_TYPE: {
			cType = 6; // Image Link
			resultType = WebHitTestResult.TYPE_ANCHOR_TYPE;
			applyImageResource(R.drawable.link_cursor, "link_cursor");
			// cursorImage = R.drawable.link_cursor;
			// String tooltip = mWebHitTestResult.getToolTip();

			if (selectedLink == "")
				selectedLink = result.getExtra();
			// eventViewer.setText(selectedLink);

			int type = getLinkType(selectedLink);
			if (type == 1) { /* Image */
				cType = 5;
				applyImageResource(R.drawable.image_cursor, "image_cursor");
			} else if (type == 2) { /* Video */
				cType = 10;
				applyImageResource(R.drawable.video_cursor, "video_cursor");
			}
			setLastKnownHitType(cType);
			break;
		}

		case WebHitTestResult.TYPE_IMAGE_TYPE: {
			cType = 5; // Image
			resultType = WebHitTestResult.TYPE_ANCHOR_TYPE;
			applyImageResource(R.drawable.image_cursor, "image_cursor");
			// HACK: Mobile YouTube images are not detected, fake it.
			if (selectedLink.startsWith("http://i.ytimg.com/vi/")
					|| isYouTube(selectedLink)) {
				// We fake a link to the current URL
				cType = 10;
				resultType = WebHitTestResult.TYPE_ANCHOR_TYPE;
				mWebHitTestResult.setType(resultType);
				mWebHitTestResult.setHref(mWebView.getUrl());
				applyImageResource(R.drawable.video_cursor, "video_cursor");
				// cursorImage = R.drawable.video_cursor;
			}
			setLastKnownHitType(cType);
			break;
		}

		case WebHitTestResult.TYPE_GEO_TYPE: {
			cType = 3; // Address map
			resultType = WebHitTestResult.TYPE_ANCHOR_TYPE;
			applyImageResource(R.drawable.geo_cursor, "geo_cursor");
			// cursorImage = R.drawable.geo_cursor;
			setLastKnownHitType(cType);
			break;
		}

		case WebHitTestResult.TYPE_EMAIL_TYPE: {
			cType = 4; // Mail
			resultType = WebHitTestResult.TYPE_ANCHOR_TYPE;
			applyImageResource(R.drawable.geo_cursor, "geo_cursor");
			// cursorImage = R.drawable.email_cursor;
			setLastKnownHitType(cType);
			break;
		}

		default:
			// Avoid stopping.
			resultType = -1;
			if (getHitPKIndex() != SwifteeApplication.TABINDEX_NOTHING) {
				resetTimersOnChangeId(identifier);
			} else {
				setHitPKIndex(SwifteeApplication.TABINDEX_NOTHING);
				if (!mP.isTipsActivated()
						&& !mP.isSuggestionListActivated()) {
						eraseDraws();
				}
			}
			applyImageResource(R.drawable.no_target_cursor, "no_target_cursor");
			// cursorImage = R.drawable.no_target_cursor;
			break;
		}

		// Set single finger timers
		setFingerTimers(resultType, identifier);

		// Apply pointer after all.
		// pointer.setImageResource(cursorImage);

		// Was there a node change?
		if (identifier != mWebHitTestResultIdentifer) {
			if (resultType == WebHitTestResult.TYPE_ANCHOR_TYPE) {
				// if (mSoftKeyboardVisible == false) { //Removing,
				// unnecesary Jose

				// if ( remote==true &&
				// SwifteeApplication.getSocketStatus()!=SwifteeApplication.CLIENT_MOUSE_DOWN
				// )
				//mWebView.focusNodeAt(X, Y);
				
				rCtrl.drawRing(identifier, SwifteeApplication.DRAW_RING, result.getRect(), COLOR.RING_OVER_COLOR);	
				
				// }
			} else if (mWebHitTestResultType == WebHitTestResult.TYPE_ANCHOR_TYPE) {
				sendEvent(MotionEvent.ACTION_CANCEL, fcX, fcY, false);
				// FIXME: Use proper API for that.
			}
		}
		mWebHitTestResultType = resultType;
		mWebHitTestResultIdentifer = identifier;
		
		SwifteeApplication.setCType(cType);
		fromFC = true;	
	}
		
		
	
	

	private void checkLinks(int identifier, int linkStatus) {

		if (linkStatus == SwifteeApplication.LINK_DATA_LOADED) {
			String link = null;
			if (!selectedLink.equals("")) {
				link = selectedLink;
			} else {
				link = selectedHref;
			}
			mP.parseSiteLinks(identifier, link, false);

		}

	}

	public void checkAnchorLinks(String selectedLink) {

		if (selectedLink.contains("padkite.local.windows")) {

			String[] split = selectedLink.split("#");
			wmId = Integer.parseInt(split[1]);
			cType = SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER;
			
			if (!isFingerTouch()) {
				applyImageResource(R.drawable.windows_manager_cursor,
						"windows_manager_cursor");
			}
			
		}

		else if (selectedLink.contains("padkite.local.tab")) {

			if (!extraLoaded) {

				extraIdentifier = identifier;
				String[] split = selectedLink.split("#");
				String arrayTab = (String) split[1];
				extraTab = true;
				String[] array = selectedLink.split("&");
				int listAmount = (array.length - 1) / 2;
				String[][] tempStore = new String[listAmount][2];
				String block;
				int num = 0;
				for (int i = 0; i < array.length; i++) {
					if (i == 0) {
					String tN = array[0];
					String[] tName = tN.split("=");
					extraTabName = tName[1];
										} else {
					if (StringUtils.isOdd(i)) {
						block = array[i];
						String titleCehck = "title" + num + "=";
						if (block.contains(titleCehck)) {
							String[] tA = block.split(titleCehck);
							String title = tA[1];
							if (tA[1].contains("%20")) {
								title = title.replace("%20", " ");
							}
							tempStore[num][0] = title;
						}
						block = array[i + 1];
						String urlCehck = "url" + num + "=";
						if (block.contains(urlCehck)) {
							String[] uA = block.split(urlCehck);
							tempStore[num][1] = uA[1];
						}
						num++;
					}
					}
				}
				extraLoaded = true;
				mP.setArrayExtraTab(tempStore);
				mP.setArrayTHIRD(tempStore);
			}
		}

		else if (selectedLink.contains("padkite.local.panel")
				|| selectedLink.contains("PadKite/Images/personalized.png")) {

			cType = SwifteeApplication.TYPE_PADKITE_PANEL;

			if (!isFingerTouch()) {
				applyImageResource(R.drawable.panel_cursor, "panel_cursor");
			}

		} else if (selectedLink.contains("padkite.local.server")) {
			
			cType = SwifteeApplication.TYPE_PADKITE_SERVER;
							
		} else if (selectedLink.contains("padkite.local.more")) {
			
			cType = SwifteeApplication.TYPE_PADKITE_MORE_LINKS;
		
		} else {

			extraTab = false;

			int linkState = mP.siteIdentifierLoaded(identifier);
			
			if (linkState == SwifteeApplication.LINK_DATA_NOT_CALLED) {
				// LOAD LINK DATA
				if (remote==false){
					mP.getLinksFromPage(identifier, selectedLink);
				}
			}   
			
			cType =  WebHitTestResult.TYPE_SRC_ANCHOR_TYPE;
		}
	}

	public void applyImageResource(int image, String imageName) {
		pointer.setImageResource(image);
		pointer.setTag(imageName);
	}

	public void actionTabHit(int hitPKIndex, boolean forHitTest) {

		int cursorTabImage = 0;
		int index = getTabIndexByHit(hitPKIndex);
		SwifteeApplication.setPKTabIndex(hitPKIndex);

		switch (index) {

		case SwifteeApplication.TABINDEX_CERO:
			
			if (forHitTest) {
				
				rCtrl.setTabToTop(hitPKIndex,SwifteeApplication.DRAW_INPUT_TABS);				
				
				if (!isEmptyInput()) {
					mP.launchSuggestionSearch(mP.getInputBoxText());
				}	

				switchSuggestion(hitPKIndex, false);

				applyImageResource(R.drawable.tab_cursor, "tab_cursor");

			}				
			SwifteeApplication.setPKTabIndex(SwifteeApplication.TABINDEX_CERO);
			cType = SwifteeApplication.TYPE_PADKITE_TAB;
			break;

		case SwifteeApplication.TABINDEX_FIRST:			
			if (forHitTest) {
				applyImageResource(R.drawable.cursor, "cursor");
				rowOver(hitPKIndex, SwifteeApplication.getPKTabIndex());
			}			
			SwifteeApplication.setPKTabIndex(SwifteeApplication.TABINDEX_FIRST);
			cType = SwifteeApplication.TYPE_PADKITE_ROW;			
			break;

		case SwifteeApplication.TABINDEX_SECOND:
			if (forHitTest) {
				buttonOver(hitPKIndex);
				applyImageResource(R.drawable.cursor, "cursor");
			}
			SwifteeApplication.setPKTabIndex(SwifteeApplication.TABINDEX_SECOND);
			cType = SwifteeApplication.TYPE_PADKITE_BUTTON;
			break;

		case SwifteeApplication.TABINDEX_THIRD:
			if (forHitTest) {
				hitBack(hitPKIndex);	
				applyImageResource(R.drawable.cursor, "cursor");
			}
			cType = SwifteeApplication.TYPE_PADKITE_BACKGROUND;
			if (BrowserActivity.isSuggestionActivated()){
				if (hitPKIndex==32) {
					cType = SwifteeApplication.TYPE_PADKITE_BACKGROUND_ROW;					
				} else {
					sCtrl.cleanRowsOver();
				}		
			}
			SwifteeApplication.setPKTabIndex(SwifteeApplication.TABINDEX_THIRD);
			break;

		case SwifteeApplication.TABINDEX_FOURTH:
			tipButtonOver(hitPKIndex);
			SwifteeApplication.setPKTabIndex(SwifteeApplication.TABINDEX_FOURTH);
			cType = SwifteeApplication.TYPE_PADKITE_TIP_BUTTON;
			break;

		}
	}

	boolean switchImageOnce;
	int lastIndex;

	private void switchSuggestion(int hitPKIndex, boolean expanded) {
		
		Log.v("heavy", ".............................................  "+ hitPKIndex + "..............................................");
		
		//if (lastIndex != hitPKIndex) {				
		//	lastIndex = hitPKIndex;				
		//} 
	
		/** LANDING INPUT **/
		if ((cType == SwifteeApplication.TYPE_PADKITE_INPUT)

				|| (cType == SwifteeApplication.TYPE_PADKITE_TAB && getLastKnownHitType() == SwifteeApplication.TYPE_PADKITE_INPUT)

				|| (cType == SwifteeApplication.TYPE_PADKITE_TAB && getLastKnownHitType() == WebHitTestResult.TYPE_EDIT_TEXT_TYPE)
				
				|| (cType == SwifteeApplication.TYPE_PADKITE_BACKGROUND && getLastKnownHitType() == WebHitTestResult.TYPE_EDIT_TEXT_TYPE)

		) {
			
			if (cType == SwifteeApplication.TYPE_PADKITE_BACKGROUND && getLastKnownHitType() == WebHitTestResult.TYPE_EDIT_TEXT_TYPE){
				cType = SwifteeApplication.TYPE_PADKITE_INPUT;
			}

			//stopAllTimers();

			if (hitPKIndex == SwifteeApplication.TABINDEX_CERO) { // WRITE

				int tabIndex = getTabIndexByHit(hitPKIndex);
				if (mP.arrayHasData(tabIndex)) {
					String inputText = getLandingInputText();
					if (!inputText.equals("") && inputText.length() > 1) {
						mP.launchSuggestionSearch(inputText);
					}
				}

				refreshSuggestions(hitPKIndex, mP.arraySuggestion,
						SwifteeApplication.TABINDEX_CERO,
						COLOR.PANTONE_192C_MAIN, expanded);
				
			} else if (hitPKIndex == SwifteeApplication.TABINDEX_FIRST) { // PASTE

				refreshSuggestions(hitPKIndex, mP.arrayClipBoard,
						SwifteeApplication.TABINDEX_FIRST,
						COLOR.PANTONE_YellowC_MAIN, expanded);
				
			} else if (hitPKIndex == SwifteeApplication.TABINDEX_SECOND) { // RECENT

				refreshSuggestions(hitPKIndex, mP.arrayRecent,
						SwifteeApplication.TABINDEX_SECOND,
						COLOR.PANTONE_246C_MAIN, expanded);
				
			} else if (hitPKIndex == SwifteeApplication.TABINDEX_THIRD) { // VOICE

				refreshSuggestions(hitPKIndex, mP.getArrayVoice(),
						SwifteeApplication.TABINDEX_THIRD,
						COLOR.PANTONE_631C_MAIN, expanded);
				
			} else if (hitPKIndex == SwifteeApplication.TABINDEX_FOURTH) { // MORE

				refreshSuggestions(hitPKIndex, mP.arrayMoreInput,
							SwifteeApplication.TABINDEX_FOURTH,
							COLOR.PANTONE_444C_MAIN, expanded);
			}
		}

		/** PANEL **/
		else if ((cType == SwifteeApplication.TYPE_PADKITE_PANEL)
				|| (cType == SwifteeApplication.TYPE_PADKITE_TAB && getLastKnownHitType() == SwifteeApplication.TYPE_PADKITE_PANEL)) {
			

			if (hitPKIndex == SwifteeApplication.TABINDEX_CERO) {
				
				String panelText_0 = "NAVIGATE MOST VISITES SITES";
				String colorId_0 = "0";
				loadPage("javascript:switchPanel('" + panelText_0 + "', '" + colorId_0 + "');");			
				
				refreshSuggestions(hitPKIndex, mP.arrayMostVisited,
						SwifteeApplication.TABINDEX_CERO,
						COLOR.PANTONE_375C_MAIN, expanded);
				
				//rCtrl.setRingcolor(COLOR.RING_PANTONE_375C_MAIN);				
							
			}

			else if (hitPKIndex == SwifteeApplication.TABINDEX_FIRST) {

				String panelText_1 = "NAVIGATE BOOKMARKS";
				String colorId_1 = "1";
				loadPage("javascript:switchPanel('" + panelText_1 + "', '" + colorId_1 + "');");
							
				refreshSuggestions(hitPKIndex, mP.arrayBookmarks,
						SwifteeApplication.TABINDEX_FIRST,
						COLOR.PANTONE_130C_MAIN, expanded);			
				
				//rCtrl.setRingcolor(COLOR.RING_PANTONE_130C_MAIN);		
				
			}

			else if (hitPKIndex == SwifteeApplication.TABINDEX_SECOND) {

				String panelText_2 = "NAVIGATE BROWSING HISTORY";
				String colorId_2 = "2";
				loadPage("javascript:switchPanel('" + panelText_2 + "', '" + colorId_2 + "');");
								
				refreshSuggestions(hitPKIndex, mP.arrayHistory,
						SwifteeApplication.TABINDEX_SECOND,
						COLOR.PANTONE_2736C_MAIN, expanded);		
				
				//rCtrl.setRingcolor(COLOR.RING_PANTONE_2736C_MAIN);
				
			}

			else if (hitPKIndex == SwifteeApplication.TABINDEX_THIRD) {

				String panelText_3 = "CHECK DOWNLOADED FILES";
				String colorId_3 = "3";
				loadPage("javascript:switchPanel('" + panelText_3 + "', '" + colorId_3 + "');");
							
				refreshSuggestions(hitPKIndex, mP.arrayDownload,
						SwifteeApplication.TABINDEX_THIRD,
						COLOR.PANTONE_ProcessBlueC_MAIN, expanded);				
				
				//rCtrl.setRingcolor(COLOR.RING_PANTONE_ProcessBlueC_MAIN);
				
			}

			else if (hitPKIndex == SwifteeApplication.TABINDEX_FOURTH) {

				String panelText_4 = "HELP RESOURCES";
				String colorId_4 = "4";
				loadPage("javascript:switchPanel('" + panelText_4 + "', '" + colorId_4 + "');");
							
				refreshSuggestions(hitPKIndex, mP.arrayHelp,
						SwifteeApplication.TABINDEX_FOURTH,
						COLOR.PANTONE_444C_MAIN, expanded);		
				
				//rCtrl.setRingcolor(COLOR.RING_PANTONE_444C_MAIN);
				
			}		

		/** ANCHOR **/
		} else if ((cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE)
				|| (cType == SwifteeApplication.TYPE_PADKITE_TAB && getLastKnownHitType() == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE)  
				|| (getLastKnownHitType() == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE && cType == SwifteeApplication.TYPE_PADKITE_TAB)
				|| (getLastKnownHitType() == SwifteeApplication.TYPE_PADKITE_TAB && cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE)) {

			if (hitPKIndex == SwifteeApplication.TABINDEX_CERO) {

				refreshSuggestions(hitPKIndex, mP.arrayOpen,
						SwifteeApplication.TABINDEX_CERO,
						COLOR.PANTONE_340C_MAIN, expanded);

			} else if (hitPKIndex == SwifteeApplication.TABINDEX_FIRST) {

				refreshSuggestions(hitPKIndex, mP.arrayMoreAnchor,
						SwifteeApplication.TABINDEX_FIRST,
						COLOR.PANTONE_444C_MAIN, expanded);

			} else if (hitPKIndex == SwifteeApplication.TABINDEX_SECOND) {

				if (mP.getArrayAmountByIndex(SwifteeApplication.TABINDEX_SECOND) > 0) {

					refreshSuggestions(hitPKIndex, mP.arraySiteLinks,
						SwifteeApplication.TABINDEX_SECOND,
						COLOR.BLACK, expanded);

				} else {
					sCtrl.drawNothing();
				}

			} else if ((extraTab)
					&& (hitPKIndex == SwifteeApplication.TABINDEX_THIRD)
					&& (identifier == extraIdentifier)) {

				refreshSuggestions(hitPKIndex, mP.arrayExtraTab,
							SwifteeApplication.TABINDEX_THIRD,
							COLOR.PANTONE_YellowC_MAIN, expanded);

			}

		/** TEXT **/
		} else if (cType == WebHitTestResult.TYPE_TEXT_TYPE) {

			if (hitPKIndex == SwifteeApplication.TABINDEX_CERO) {

				// refreshSuggestions(hitPKIndex, mP.arrayText,
				// SwifteeApplication.TABINDEX_CERO, SwifteeApplication.BLACK);

			} else if (hitPKIndex == SwifteeApplication.TABINDEX_FIRST) {

				// refreshSuggestions(hitPKIndex, mP.arrayText,
				// SwifteeApplication.TABINDEX_FIRST, SwifteeApplication.GRAY);

			} else if (hitPKIndex == SwifteeApplication.TABINDEX_SECOND) {

				// refreshSuggestions(hitPKIndex, mP.arrayText,
				// SwifteeApplication.TABINDEX_SECOND, SwifteeApplication.GRAY);

			} else if (hitPKIndex == SwifteeApplication.TABINDEX_THIRD) {

				// refreshSuggestions(hitPKIndex, mP.arrayText,
				// SwifteeApplication.TABINDEX_THIRD, SwifteeApplication.GRAY);

			} else if (hitPKIndex == SwifteeApplication.TABINDEX_FOURTH) {

				// refreshSuggestions(hitPKIndex, mP.arrayText,
				// SwifteeApplication.TABINDEX_FOURTH, SwifteeApplication.GRAY);

			}

		/** WINDOWS MANAGER **/
		} else if (cType == SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER
				|| (getLastKnownHitType() == SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER && cType == SwifteeApplication.TYPE_PADKITE_TAB)
				|| (getLastKnownHitType() == SwifteeApplication.TYPE_PADKITE_TAB && cType == SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER)) {

			if (hitPKIndex == SwifteeApplication.TABINDEX_CERO) {

				refreshSuggestions(hitPKIndex, mP.arrayOpenWindowsSet,
					SwifteeApplication.TABINDEX_CERO,
					COLOR.PANTONE_158C_MAIN, expanded);

			} else if (hitPKIndex == SwifteeApplication.TABINDEX_FIRST) {

				refreshSuggestions(hitPKIndex, mP.arrayWindowsSet,
					SwifteeApplication.TABINDEX_FIRST,
					COLOR.BLACK, expanded);

			} else if (hitPKIndex == SwifteeApplication.TABINDEX_SECOND) {

				refreshSuggestions(hitPKIndex, mP.arrayMoreWindowsSet,
					SwifteeApplication.TABINDEX_SECOND,
					COLOR.PANTONE_444C_MAIN, expanded);
			}
		}
		
		/** SERVER **/
		else if (cType == SwifteeApplication.TYPE_PADKITE_SERVER
				|| (getLastKnownHitType() == SwifteeApplication.TYPE_PADKITE_SERVER && cType == SwifteeApplication.TYPE_PADKITE_TAB)
				|| (getLastKnownHitType() == SwifteeApplication.TYPE_PADKITE_TAB && cType == SwifteeApplication.TYPE_PADKITE_SERVER)) {
	
			if (hitPKIndex == SwifteeApplication.TABINDEX_CERO) {
	
				refreshSuggestions(hitPKIndex, mP.arrayServer,
					SwifteeApplication.TABINDEX_CERO,
					COLOR.PANTONE_246C_MAIN, expanded);
	
			} else if (hitPKIndex == SwifteeApplication.TABINDEX_FIRST) {
	
				refreshSuggestions(hitPKIndex, mP.arrayMoreServer,
					SwifteeApplication.TABINDEX_FIRST,
					COLOR.PANTONE_444C_MAIN, expanded);	
			}			
		}	
	}

	/**
	 * ALL CERO.
	 */
	private boolean extraTab;
	private String extraTabName;
	private boolean extraLoaded;
	private Rect anchorRect;
	private boolean fromParser;
	private int vertical;

	public int getVertical() {
		return vertical;
	}

	public boolean isFromParser() {
		
		return fromParser;
	}

	public void setFromParser(boolean fromParser) {
		this.fromParser = fromParser;
	}

	public void ceroAnchor(boolean expanded, boolean fromParser) {

		rCtrl.clearData();
			
		this.fromParser = fromParser;

		// if (!fromParser)
		// mP.cleanArraySECOND();

		int linkStatus = mP.siteIdentifierLoaded(identifier);

		checkLinks(identifier, linkStatus);

		String[] titles;
		int[][] tabColors;
		int[] ringColors;

		String[] tStart = { "OPEN", "MORE" };
		int[][] tabColorsStart = { COLOR.PANTONE_340C_MAIN,
				COLOR.PANTONE_444C_MAIN };
		int[] ringColorsStart = { COLOR.RING_PANTONE_340C_MAIN,
				COLOR.RING_PANTONE_444C_MAIN };

		titles = tStart;
		tabColors = tabColorsStart;
		ringColors = ringColorsStart;

		if (linkStatus == SwifteeApplication.LINK_DATA_LOADED
				|| linkStatus == SwifteeApplication.LINK_DATA_PARSED) {

			if (!fromParser
					&& linkStatus != SwifteeApplication.LINK_DATA_PARSED) {
				mP.parseSiteLinks(identifier, selectedLink, false);
			}

			String[] tLinks = { "OPEN", "MORE", "LINKS" };
			
			int[][] cLinks = { COLOR.PANTONE_340C_MAIN,
					COLOR.PANTONE_444C_MAIN, 
					COLOR.BLACK };
			
			int[] rLinks = { COLOR.RING_PANTONE_340C_MAIN,
					COLOR.RING_PANTONE_444C_MAIN,
					COLOR.PAINT_BLACK };

			titles = tLinks;
			tabColors = cLinks;
			ringColors = rLinks;

			if (extraTab) {

				String[] tExtra = { "OPEN", "MORE", "LINKS", "EXTRA" };
				int[][] cExtra = { COLOR.PANTONE_340C_MAIN,
						COLOR.PANTONE_444C_MAIN, COLOR.BLACK,
						COLOR.PANTONE_YellowC_MAIN };
				
				int[] rExtra = { COLOR.RING_PANTONE_340C_MAIN,
						COLOR.RING_PANTONE_444C_MAIN,
						COLOR.PAINT_BLACK,
						COLOR.RING_PANTONE_YellowC_MAIN };

				titles = tExtra;
				tabColors = cExtra;
				ringColors = rExtra;
			}

		} else {
			
		}

		if (extraTab) {

			String[] tExtra = { "OPEN", "MORE", "LINKS", "EXTRA" };
			int[][] cExtra = { COLOR.PANTONE_340C_MAIN,
					COLOR.BLUE, COLOR.BLUE,
					COLOR.PANTONE_YellowC_MAIN };
			int[] rExtra = { COLOR.RING_PANTONE_340C_MAIN,
					COLOR.PAINT_BLUE,
					COLOR.PAINT_BLACK,
					COLOR.RING_PANTONE_YellowC_MAIN };

			titles = tExtra;
			tabColors = cExtra;
			ringColors = rExtra;

		}

		int maxAnchorSize = SwifteeApplication.getArrayOpenBiggerRow();

		/*
		 * if (rect.width() < maxAnchorSize){ //HERE DO SOMETHING WITH THE SIZE
		 * }
		 */

		getExtendedRect(expanded);

		SwifteeApplication.setAnchorRect(anchorRect);	
		
		SwifteeApplication.setExpanded(expanded);
		SwifteeApplication.setTabsAmountOf(titles.length);
		SwifteeApplication.setPKTabIndex(SwifteeApplication.TABINDEX_CERO);

		Object[] param_ANCHOR = { anchorRect, titles, tabColors, ringColors };
		rCtrl.setDrawStyle(SwifteeApplication.DRAW_TABS, param_ANCHOR,
				identifier);

		setRowsHeightAndCount(getHitPKIndex(), mP.arrayOpen, expanded);

		Object[] param_TEXT = { COLOR.PANTONE_340C_MAIN, mP.arrayOpen, null,
				false, getHitPKIndex() };
		sCtrl.setDrawStyle(SwifteeApplication.TAB_SUGGESTIONS, param_TEXT,
				identifier);

		SwifteeApplication.setActiveRect(rect);	
		SwifteeApplication.setActiveColor(COLOR.PANTONE_340C_MAIN);
		
	}

	private void getExtendedRect(boolean expanded){
	
		Vector<Object> cuagrant34 = new Vector<Object>();
		
		/*
		 * int xCord = 0;
		int yCord = 0;
		
		if (isFromFC()){						
			
			
			if (isAnchorTab()){
				
				xCord = getAnX();
				yCord = getAnY();
				
			} else {
				
				xCord = getFcX();
				yCord = getFcY();
				
			}
			
		} else {
			
			if (isAnchorTab()){
				
				xCord = getAnX();
				yCord = getAnY();
				
			} else {
				
				xCord = getStX();
				yCord = getStY();
				
			}
		}*/
		
		/*if (isFromFC()){
			
			xCord = getFcX();
			yCord = getFcY();
			
		} else {
			
			if (isAnchorTab()){
				
				xCord = getAnX();
				yCord = getAnY();
				
			} else {
				
				xCord = getStX();
				yCord = getStY();
				
			}
		}*/
		
		/*if (isFromFC()){
			xCord = getFcX();
			yCord = getFcY();
		} else {
			xCord = getStX();
			yCord = getStY();						
		}*/
		
		/*cuagrant34 = mP.getCuadrant34(xCord, yCord);
		vertical = (Integer) cuagrant34.get(0);*/	
		
		
		anchorRect = new Rect();
		
		anchorRect.top = rect.top;
		anchorRect.bottom = rect.bottom;		
		
		Rect completeRect = new Rect();
		completeRect.top =	rect.top; 
		completeRect.bottom = rect.bottom;
		
		vertical = ScreenLocation.getVerticalLocation();
		
		switch (vertical) {
	
			case SwifteeApplication.VERTICAL_CENTER_COLUMN:
			case SwifteeApplication.VERTICAL_LEFT_COLUMN:	
				
				if (expanded) {
					anchorRect.right = rect.right + (edge * SwifteeApplication.FINGER_EXPANDED);
				} else {
					anchorRect.right = rect.right + (edge * SwifteeApplication.PADKITE_EXPANDED);
				}
				anchorRect.left = rect.left;
				SwifteeApplication.setVerticalPosition(SwifteeApplication.VERTICAL_LEFT_COLUMN);
				
				completeRect.left = rect.left;
				completeRect.right = anchorRect.right;			
				
				break;
		
			case SwifteeApplication.VERTICAL_RIGHT_COLUMN:
				
				if (expanded) {
					anchorRect.left = rect.left - (edge * SwifteeApplication.FINGER_EXPANDED);
				} else {
					anchorRect.left = rect.left - (edge * SwifteeApplication.PADKITE_EXPANDED);
				}				
				//anchorRect.left = rect.left - (edge * 2);
				anchorRect.right = rect.right;
				SwifteeApplication.setVerticalPosition(SwifteeApplication.VERTICAL_RIGHT_COLUMN);
				
				completeRect.left = anchorRect.left;
				completeRect.right = rect.right;	
				
				break;
				
		}		
		
		SwifteeApplication.setCompleteRect(completeRect);
	
	}
	
	boolean pointerTextFlag = true;

	public void ceroText() {
		
		 mP.clearArrays();
		  
		 /*temporaryClipboard = (String) ((ClipboardManager)
		 mP.getSystemService(Context.CLIPBOARD_SERVICE)).getText();
		 mWebView.executeSelectionCommand(fcX, fcY, WebView.SELECT_PARAGRAPH);
		 mWebView.executeSelectionCommand(fcX, fcY, WebView.COPY_TO_CLIPBOARD); 
		 mWebView.executeSelectionCommand(fcX, fcY, WebView.CLEAR_SELECTION);
		 clipBoardTextHandler.postDelayed(clipBoardTextTask = new ClipBoardTask(), 500);*/		 

	}
	
	
	public void refreshMenusRequest(){    	
    	mWebView.loadUrl("javascript:hover('', '', '" +  WebHitTestResult.CALLBACK_MOVEHIT  + "');");
    }
	
	public void refreshMenu(Rect re){		
		rCtrl.refresh(re);
    	sCtrl.refresh(re, null);	
	}

	private Handler clipBoardTextHandler = new Handler();
	private ClipBoardTask clipBoardTextTask;

	public class ClipBoardTask implements Runnable {
		public void run() {

			textClipboard = (String) ((ClipboardManager) mP
					.getSystemService(Context.CLIPBOARD_SERVICE)).getText();
			((ClipboardManager) mP.getSystemService(Context.CLIPBOARD_SERVICE))
					.setText(temporaryClipboard);

			String[] arr = textClipboard.split(" ");

			int words = arr.length;
			String[] titles = null;
			int[][] tabColors = null;

			if (words < 2) {

				String[] t = { "PICK", "ALL" };

				int[][] tC = { COLOR.BLACK, COLOR.GRAY };

				/*
				 * SwifteeApplication.BLUE, SwifteeApplication.LIGTH_BLUE };
				 */

				titles = t;
				tabColors = tC;

			} else if (words >= 2 && words < 10) {

				String[] t = { "PICK", "WORD", "ALL" };

				int[][] tC = { COLOR.GRAY_DARK, COLOR.GRAY, COLOR.GRAY };

				/*
				 * SwifteeApplication.BLUE, SwifteeApplication.TURQUOISE,
				 * SwifteeApplication.LIGTH_BLUE};
				 */

				titles = t;
				tabColors = tC;

			} else if (words >= 10) {

				String[] t = { "PICK", "WORD", "LINE", "ALL" };

				int[][] tC = { COLOR.GRAY_DARK, 
						COLOR.GRAY, 
						COLOR.GRAY,
						COLOR.GRAY };

				/*
				 * SwifteeApplication.BLUE, SwifteeApplication.TURQUOISE,
				 * SwifteeApplication.FUXIA, SwifteeApplication.LIGTH_BLUE};
				 */

				titles = t;
				tabColors = tC;
			}

			Object[] paramEDIT_TEXT = { rect, titles, tabColors };
			rCtrl.setDrawStyle(SwifteeApplication.DRAW_RING_AND_TAB,
					paramEDIT_TEXT, identifier);
			SwifteeApplication.setActiveColor(COLOR.BLUE);

			if (pointerTextFlag) {
				// removeView(pointer);
				// tHold.addView(pointer);

				applyImageResource(R.drawable.select_text_cursor, "select_text_cursor");
				// pointer.setImageResource(R.drawable.select_text_cursor);

				// setPointerTextSize();
				pointerTextFlag = false;
				pointerTextHandler.postDelayed(
				textPointerTask = new TextPointerTask(), 1000);
				countCursor = -1;
			}

			SwifteeApplication.setActiveRect(rect);
			pointerTextHandler.removeCallbacks(this);
		}
	}

	private void setPointerTextSize() {
		int textSize = SwifteeApplication.getHitTestFontSize();
		int orgWidth = pointer.getDrawable().getIntrinsicWidth();
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				orgWidth, textSize * 4);
		pointer.setLayoutParams(params);
		pointer.setScaleType(ImageView.ScaleType.CENTER);
	}

	private Handler pointerTextHandler = new Handler();
	private TextPointerTask textPointerTask;
	private int countCursor;

	public class TextPointerTask implements Runnable {
		public void run() {
			if (pointer.getVisibility() == View.VISIBLE) {
				countCursor++;
				pointer.setVisibility(View.INVISIBLE);
				fcXMatch = fcX;
				if (countCursor == 1) {
					startSelection(true);
					pointerTextHandler.postDelayed(textPointerMovingTask = new TextPointerMoving(),	100);
					startCursorMovingTime = SystemClock.uptimeMillis() / 1000;
				} else {
					if (!fCisMoving) {
						cleanTextSelectionTimers();
						applyImageResource(R.drawable.text_cursor,"text_cursor");
					}
				}
			} else {
				pointer.setVisibility(View.VISIBLE);
			}
			pointerTextHandler.postDelayed(this, 250);
		}
	}

	private void cleanTextSelectionTimers() {
		startSelection(false);
		pointerTextHandler.removeCallbacks(textPointerTask);
		textPointerTask = null;
		pointerTextHandler.removeCallbacks(textPointerMovingTask);
		textPointerMovingTask = null;
		countCursor = 0;
		pointerTextFlag = true;
	}

	private TextPointerMoving textPointerMovingTask;
	private long startCursorMovingTime;
	private long matchCursorMovingTime;

	int fcXMatch;

	public class TextPointerMoving implements Runnable {

		public void run() {

			matchCursorMovingTime = SystemClock.uptimeMillis() / 1000;
			long sum = startCursorMovingTime - matchCursorMovingTime;
			sum = sum * (-1);

			Log.v("time", "sum: " + sum);
			Log.v("time", "fcXMatch: " + fcXMatch + " fcX: " + fcX);
			Log.v("time", "----------------------------------");

			if (sum > 2 && (fcXMatch == fcX)) {
				fCisMoving = false;
			}
			pointerTextHandler.postDelayed(this, 500);
		}
	}

	public void ceroInput() {

	}

	public void ceroPkInput(boolean expanded) {

		rCtrl.clearData();
			
		// MAL refrescar array cada vez que se actualiza.
		String inputText = getLandingInputText();
		if (!inputText.equals("")) {
			mP.loadSuggestionIntoArray(inputText);
		}
		mP.loadClipBoardIntoArray();
		mP.loadRecentIntoArray();
		mP.loadVoiceIntoArray();

		setRowsHeightAndCount(getHitPKIndex(), null, expanded);

		SwifteeApplication.setExpanded(expanded);

		//rCtrl.setRingcolor(COLOR.RING_PANTONE_192C_MAIN);

		String[] titles = { "WRITE", "PASTE", "RECENT", "VOICE", "MORE" };
		int[][] tabColors = { 
				COLOR.PANTONE_192C_MAIN,
				COLOR.PANTONE_YellowC_MAIN, 
				COLOR.PANTONE_246C_MAIN,
				COLOR.PANTONE_631C_MAIN, 
				COLOR.PANTONE_444C_MAIN };
		
		int[] ringColors = { COLOR.RING_PANTONE_192C_MAIN,
				COLOR.RING_PANTONE_YellowC_MAIN,
				COLOR.RING_PANTONE_246C_MAIN,
				COLOR.RING_PANTONE_631C_MAIN , 
				COLOR.RING_PANTONE_444C_MAIN};

		Object[] paramEDIT_TEXT = { rect, titles, tabColors, ringColors };
		rCtrl.setDrawStyle(SwifteeApplication.DRAW_TABS, paramEDIT_TEXT,
				identifier);

		String[] buttons = { "■", "IMAGES", "VIDEOS", "WIKI" };
		Object[] param_TEXT = { COLOR.PANTONE_192C_MAIN, null, buttons, true,
				getHitPKIndex() };
		sCtrl.setDrawStyle(SwifteeApplication.TAB_SUGGESTIONS, param_TEXT,
				identifier);

		SwifteeApplication.setPKTabIndex(SwifteeApplication.TABINDEX_CERO);
		SwifteeApplication.setActiveColor(COLOR.PANTONE_192C_MAIN);
		SwifteeApplication.setActiveRect(rect);

	}

	public void ceroPKPanel(boolean expanded) {	
		
		rCtrl.clearData();

		mP.loadMostVisitedIntoArray();
		mP.loadBookMarkIntoArray();
		mP.loadHistoryIntoArray();
		mP.loadDownloadIntoArray();

		//rCtrl.setRingcolor(COLOR.RING_PANTONE_192C_MAIN);

		String[] titles = { "MOST VISITED", "BOOKMARKS", "HISTORY",
				"DOWNLOADS", "HELP" };

		int[][] tabColors = { 
				COLOR.PANTONE_375C_MAIN,				
				COLOR.PANTONE_130C_MAIN, 
				COLOR.PANTONE_2736C_MAIN,
				COLOR.PANTONE_ProcessBlueC_MAIN, 
				COLOR.PANTONE_444C_MAIN 
		};
		
		int[] ringColors = { 
				COLOR.RING_PANTONE_375C_MAIN,				
				COLOR.RING_PANTONE_130C_MAIN,
				COLOR.RING_PANTONE_2736C_MAIN,
				COLOR.RING_PANTONE_ProcessBlueC_MAIN, 
				COLOR.RING_PANTONE_444C_MAIN };

		setRowsHeightAndCount(getHitPKIndex(), mP.arrayMostVisited, expanded);

		SwifteeApplication.setExpanded(expanded);

		Object[] paramEDIT_TEXT = { rect, titles, tabColors, ringColors };
		rCtrl.setDrawStyle(SwifteeApplication.DRAW_TABS, paramEDIT_TEXT,
				identifier);

		setRowsHeightAndCount(getHitPKIndex(), mP.arrayMostVisited, expanded);

		String[] buttons = getButtonsArray(getHitPKIndex());

		boolean hasButtons;
		if (buttons == null) {
			hasButtons = false;
		} else {
			hasButtons = true;
		}

		Object[] param_TEXT = { COLOR.PANTONE_375C_MAIN,
				mP.arrayMostVisited, buttons, hasButtons, getHitPKIndex() };
		sCtrl.setDrawStyle(SwifteeApplication.TAB_SUGGESTIONS, param_TEXT,
				identifier);
		
		String panelText_0 = "NAVIGATE MOST VISITES SITES";
		String colorId_0 = "0";
		loadPage("javascript:switchPanel('" + panelText_0 + "', '" + colorId_0 + "');");

		SwifteeApplication.setActiveColor(COLOR.BLUE_PANEL);
		SwifteeApplication.setActiveRect(rect);

		
	}

	public void ceroPKSet(boolean expanded) {	
		
		rCtrl.clearData();
		
		mP.loadWindowsSetIntoArryaById(wmId);

		//rCtrl.setRingcolor(COLOR.RING_PANTONE_158C_MAIN);

		String[] titles = { "SET", "LINKS", "MORE" };

		int[][] tabColors = { COLOR.PANTONE_158C_MAIN,
				COLOR.BLACK, COLOR.PANTONE_444C_MAIN };
		
		int[] ringColors = { COLOR.RING_PANTONE_158C_MAIN,
				COLOR.PAINT_BLACK, COLOR.RING_PANTONE_444C_MAIN };

		SwifteeApplication.setExpanded(expanded);		
		getExtendedRect(expanded);
		SwifteeApplication.setAnchorRect(anchorRect);

		Object[] paramEDIT_WS = { anchorRect, titles, tabColors, ringColors };
		rCtrl.setDrawStyle(SwifteeApplication.DRAW_TABS, paramEDIT_WS, identifier);

		int pkIn = getHitPKIndex();
		setRowsHeightAndCount(pkIn, mP.arrayOpenWindowsSet, expanded);

		Object[] param_TEXT = { COLOR.PANTONE_158C_MAIN,
				mP.arrayOpenWindowsSet, null, false, getHitPKIndex() };
		
		sCtrl.setDrawStyle(SwifteeApplication.TAB_SUGGESTIONS, param_TEXT,
				identifier);

		SwifteeApplication.setActiveColor(COLOR.PANTONE_158C_MAIN);
		SwifteeApplication.setActiveRect(rect);
	}	
	
	

	public void ceroPKServer(boolean expanded){	
		
		rCtrl.clearData();

		//rCtrl.setRingcolor(COLOR.RING_PANTONE_444C_MAIN);

		String[] titles = { "SERVER", "MORE" };

		int[][] tabColors = { COLOR.PANTONE_246C_MAIN,				
				COLOR.PANTONE_444C_MAIN };
		
		
		int[] ringColors = { COLOR.RING_PANTONE_246C_MAIN,
				COLOR.RING_PANTONE_444C_MAIN };

		SwifteeApplication.setExpanded(expanded);

		Object[] paramEDIT_WS = { rect, titles, tabColors, ringColors };
		rCtrl.setDrawStyle( SwifteeApplication.DRAW_TABS, paramEDIT_WS, identifier);

		setRowsHeightAndCount(getHitPKIndex(), mP.arrayServer, expanded);

		Object[] param_TEXT = { COLOR.PANTONE_246C_MAIN,
				mP.arrayServer, null, false, getHitPKIndex() };
		
		sCtrl.setDrawStyle(SwifteeApplication.TAB_SUGGESTIONS, param_TEXT, identifier);

		SwifteeApplication.setActiveColor(COLOR.PANTONE_246C_MAIN);
		SwifteeApplication.setActiveRect(rect);	
	}	
	
	public void ceroMap() {

	}

	public void ceroMail() {

	}

	public void ceroDefault() {

	}

	public void refreshSuggestions(int hitPKIndex, String[][] array, int index,
			int[] color, boolean expanded) {
	
		int tabIndex = switchTabIndex(index);

		String[] buttons = getButtonsArray(hitPKIndex);
		setRowsHeightAndCount(hitPKIndex, array, expanded);
		SwifteeApplication.setExpanded(expanded);

		setRowsHeightAndCount(hitPKIndex, array, expanded);

		boolean hasButtons;
		if (buttons == null) {
			hasButtons = false;
		} else {
			hasButtons = true;
		}
		Rect re = new Rect();	

		if (cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE) {
			
			re = anchorRect;
			
		} else {
			re = rect;
		}
		if (re.left == 0) {
			Log.v("fuck", "fuck");
		}

		SwifteeApplication.setMasterScrollRect(re);
		Object[] params = { color, array, buttons, hasButtons, hitPKIndex };
		sCtrl.setDrawStyle(SwifteeApplication.TAB_SUGGESTIONS, params,
				identifier);
		SwifteeApplication.setPKTabIndex(tabIndex);
		SwifteeApplication.setActiveColor(color);

	}

	public int switchTabIndex(int index) {
		int tabIndex = 0;
		switch (index) {
		case 0:
			tabIndex = SwifteeApplication.TABINDEX_CERO;
			mReadyToCero = true;
			setFalseAllExept(index);
			break;
		case 1:
			mReadyToFirst = true;
			tabIndex = SwifteeApplication.TABINDEX_FIRST;
			setFalseAllExept(index);
			break;
		case 2:
			mReadyToSecond = true;
			tabIndex = SwifteeApplication.TABINDEX_SECOND;
			setFalseAllExept(index);
			break;
		case 3:
			mReadyToThird = true;
			tabIndex = SwifteeApplication.TABINDEX_THIRD;
			setFalseAllExept(index);
			break;
		case 4:
			mReadyToFourth = true;
			tabIndex = SwifteeApplication.TABINDEX_FOURTH;
			setFalseAllExept(index);
			break;
		}
		SwifteeApplication.setPKTabIndex(tabIndex);
		return tabIndex;
	}

	private void setFalseAllExept(int index) {
		switch (index) {
		case 0:
			mReadyToFirst = false;
			mReadyToSecond = false;
			mReadyToThird = false;
			mReadyToFourth = false;
			break;
		case 1:
			mReadyToCero = false;
			mReadyToSecond = false;
			mReadyToThird = false;
			mReadyToFourth = false;
			break;
		case 2:
			mReadyToCero = false;
			mReadyToFirst = false;
			mReadyToThird = false;
			mReadyToFourth = false;
			break;
		case 3:
			mReadyToCero = false;
			mReadyToFirst = false;
			mReadyToSecond = false;
			mReadyToFourth = false;
			break;
		case 4:
			mReadyToCero = false;
			mReadyToFirst = false;
			mReadyToSecond = false;
			mReadyToThird = false;
			break;

		}
	}

	public String[] getButtonsArray(int index) {

		String[] buttons = null;

		/** PADKITE INPUT **/
		if ((cType == SwifteeApplication.TYPE_PADKITE_INPUT)
				|| (cType == SwifteeApplication.TYPE_PADKITE_TAB && getLastKnownHitType() == SwifteeApplication.TYPE_PADKITE_INPUT)
				|| (cType == SwifteeApplication.TYPE_PADKITE_TAB && getLastKnownHitType() == WebHitTestResult.TYPE_EDIT_TEXT_TYPE)) {

			if (mP.arrayHasData(index)) {

				if (mP.arrayBiggerThanList(index)) {

					String[] arr = { "■", "IMAGES", "VIDEOS", "WIKI", "▼", "▲" };
					buttons = arr;

				} else {

					String[] arr = { "■", "IMAGES", "VIDEOS", "WIKI" };
					buttons = arr;
				}

			} else {

				String[] flat = { "■", "IMAGES", "VIDEOS", "WIKI" };
				buttons = flat;
			}

			/** PADKITE PANEL **/
		} else if ((cType == SwifteeApplication.TYPE_PADKITE_PANEL)
				|| (cType == SwifteeApplication.TYPE_PADKITE_TAB && getLastKnownHitType() == SwifteeApplication.TYPE_PADKITE_PANEL)
				|| (cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE)
				|| (cType == SwifteeApplication.TYPE_PADKITE_TAB && getLastKnownHitType() == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE)
				) {

			if (mP.arrayHasData(index)) {

				if (mP.arrayBiggerThanList(index)) {

					String[] arrows = { "▼", "▲" };
					buttons = arrows;

				} else {

					String[] flat = null;
					buttons = flat;

				}

			} else {

				String[] flat = null;
				buttons = flat;
			}

		}

		return buttons;
	}

	private void rowOver(int hitPKIndex, int tabNumber) {

		// String line = getRowTextByIndex(tabNumber,
		// SwifteeApplication.getTabIndexHit());
		// if (line.contains("...")){
		// Resize the suggestion here
		// }

		sCtrl.setRowOver(hitPKIndex);
		cType = SwifteeApplication.TYPE_PADKITE_ROW;
	}

	private void buttonOver(int hitPKIndex) {
		sCtrl.setButtonOver(hitPKIndex);
		cType = SwifteeApplication.TYPE_PADKITE_BUTTON;
	}

	private void hitBack(int hitPKIndex) {
		 
		if (hitPKIndex!=32){		 
			sCtrl.cleanRowsOver();
		}
		
		if (cType == WebHitTestResult.TYPE_EDIT_TEXT_TYPE) {
			cType = SwifteeApplication.TYPE_PADKITE_BUTTON;
		}
	}

	private void tipButtonOver(int hitPKIndex) {
		tCtrl.setButtonOver(hitPKIndex);
		cType = SwifteeApplication.TYPE_PADKITE_TIP_BUTTON;
	}

	private boolean drawErased;

	public void eraseDraws() {
	
		if (!drawErased) {
			SwifteeApplication.cleanAllRects(); // Put all tabs rects to 0;
			//rCtrl.ringToOriginalColor(); // Set gray again
			rCtrl.drawNothing(); // Erase draws Rings.
			sCtrl.drawNothing(); // Erase draws Suggestions.
			tCtrl.drawNothing(); // Erase tips.
			BrowserActivity.setTabsActivated(false);
			BrowserActivity.setSuggestionActivated(false);
			BrowserActivity.setSuggestionListActivated(false);
			BrowserActivity.setTipsActivated(false);
			if (isLandingPage) {
				loadPage("javascript:clearInput()");
			}
			rCtrl.setIdentifier(-1);
			drawErased = true;
			//lastKnownHitType = -1;
		}
	}

	public boolean isDrawErased() {
		return drawErased;
	}

	public void setDrawErased(boolean drawErased) {
		this.drawErased = drawErased;
	}

	private int getTabIndexByHit(int hitPKIndex) {
		int index = -1;
		if (hitPKIndex < 10 && hitPKIndex >= 0) {
			index = SwifteeApplication.TABINDEX_CERO;
		} else if (hitPKIndex >= 10 && hitPKIndex < 20) {
			index = SwifteeApplication.TABINDEX_FIRST;
		} else if (hitPKIndex >= 20 && hitPKIndex < 30) {
			index = SwifteeApplication.TABINDEX_SECOND;
		} else if (hitPKIndex >= 30 && hitPKIndex < 40) {
			index = SwifteeApplication.TABINDEX_THIRD;
		} else if (hitPKIndex >= 40) {
			index = SwifteeApplication.TABINDEX_FOURTH;
		}
		SwifteeApplication.setObjectIndexHit(index);
		return index;
	}

	// Single Finger: set first and second timers.
	public void setFingerTimers(int resultType, int identifier) {

		if (SwifteeApplication.getFingerMode() == true && resultType != -1) {

			if (identifier == mWebHitTestResultIdentifer
					&& mCeroTimerStarted == false) {
				setstartCero(identifier);
			}
			
			/*
			 * if (identifier == mWebHitTestResultIdentifer &&
			 * mFirstTimerStarted == false) { setStartFirst(); } if (identifier
			 * == mWebHitTestResultIdentifer && mSecondTimerStarted == false) {
			 * setStartSecond(); } if (identifier == mWebHitTestResultIdentifer
			 * && mThirdTimerStarted == false) { setStartThird(); } if
			 * (identifier == mWebHitTestResultIdentifer && mFourthTimerStarted
			 * == false) { setStartFourth(); } if (identifier ==
			 * mWebHitTestResultIdentifer && mFifthTimerStarted == false) {
			 * setStartFifth(); }
			 */

			if (mCeroTimerStarted && mReadyToCero) {
				persistRingsTabs(SwifteeApplication.PERSIST_CERO_STAGE);
			}
			/*
			 * if (mFirstTimerStarted && mReadyToFirst) {
			 * persistRingsTabs(SwifteeApplication.PERSIST_FIRST_STAGE); } if
			 * (mSecondTimerStarted && mReadyToSecond) {
			 * persistRingsTabs(SwifteeApplication.PERSIST_SECOND_STAGE); } if
			 * (mThirdTimerStarted && mReadyToThird) {
			 * persistRingsTabs(SwifteeApplication.PERSIST_THIRD_STAGE); } if
			 * (mFourthTimerStarted && mReadyToFourth) {
			 * persistRingsTabs(SwifteeApplication.PERSIST_FOURTH_STAGE); }
			 */

			// Node changed:
			/*
			 * if (WebHitTestResult.ANCHOR_TYPE != resultType &&
			 * mWebHitTestResultType == WebHitTestResult.ANCHOR_TYPE) {
			 * stopAllTimers(); }
			 */

		}
	}

	private void stopAllTimers() {
		stopCero(true);
		/*
		 * stopFirst(true); stopSecond(true); stopThird(true); stopFourth(true);
		 * stopFifth(true);
		 */
	}

	/**
	 * Message to handle title of the link.
	 **/
	private Handler mHandler = new Handler() {
		/** THIS IS FOR WHEN WE WANT TO READ THE TITLE OF THE LINK **/
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FOCUS_NODE_HREF: {
				linkUrl = (String) msg.getData().get("url");
				if (linkUrl == null || linkUrl.length() == 0) {
					break;
				}
				linkTitle = (String) msg.getData().get("title");
				// linkTitle = "<b>" + tt + "</b>";
				Log.v("EXTRA", "title: " + linkTitle + " url: " + linkUrl);

			}
			}
		}
	};

	/**
	 * SINGLE_FINGER_OPERATION_MODE constant on SwifteeApplication is true the
	 * timers on first are not enabled therefore the mouse has to be operated
	 * with two fingers. If true the mouse can be single finger operated.
	 * */

	public boolean mCeroTimerStarted = false;
	public boolean mReadyToCero = false;

	// boolean mFirstTimerStarted = false;
	boolean mReadyToFirst = false;

	// boolean mSecondTimerStarted = false;
	boolean mReadyToSecond = false;

	// boolean mThirdTimerStarted = false;
	boolean mReadyToThird = false;

	// boolean mFourthTimerStarted = false;
	boolean mReadyToFourth = false;

	// boolean mFifthTimerStarted = false;
	boolean mReadyToFifth = false;

	// Single Finger: Reset SFOM timers
	private void resetTimersOnChangeId(int identifier) {
		if (identifier != mWebHitTestResultIdentifer) {

			if (mCeroTimerStarted) {
				stopCero(true);
			}
			/*
			 * if (mFirstTimerStarted) { stopFirst(false); removeSelection(); }
			 * if (mSecondTimerStarted) { stopSecond(true); removeSelection(); }
			 * if (mThirdTimerStarted) { stopThird(true); } if
			 * (mFourthTimerStarted) { stopFourth(true); } if
			 * (mFifthTimerStarted) { stopFifth(true); }
			 */
			if (!mP.isSuggestionActivatedExpanded() && !mP.isTipsActivated()) {
				eraseDraws();
			}
		}
	};

	// Single Finger: Set timer for first
	private void setstartCero(int identifier) {
		if (!mCeroTimerStarted) {
			startCero();
		} else {
			// If the focus is on another link we reset the armed state.
			if (identifier != mWebHitTestResultIdentifer) {
				stopCero(true);
				startCero();
			}
		}
	};

	// Single Finger: Start first timer
	private void startCero() {
		mCeroTimerStarted = true;
		mReadyToCero = false;
		// int time = 0;
		/*
		 * if (cType == WebHitTestResult.TEXT_TYPE) { time = 1000; // First
		 * timer } else { time = 1000; }
		 */
		handler.postDelayed(mCeroTimer, 1500);
	};
		

	// Single Finger: Stop first
	private void stopCero(boolean dragging) {		
		mCeroTimerStarted = false;
		mReadyToCero = false;
		handler.removeCallbacks(mCeroTimer);
		if (!dragging) {
			applyImageResource(R.drawable.kite_cursor, "kite_cursor");
			// pointer.setImageResource(R.drawable.kite_cursor);
		}
	};

	// Arms cero runnable
	Runnable mCeroTimer = new Runnable() {
		public void run() {
			setCeroRingTab();
			mReadyToCero = true;
			/*
			 * mReadyToFirst = false; mReadyToSecond = false; mReadyToThird =
			 * false; mReadyToFourth = false; mReadyToFifth = false;
			 */
		}
	};

	// Single Finger: Set timer for second
	/*
	 * private void setStartFirst() { if (!mFirstTimerStarted) { startFirst(); }
	 * };
	 * 
	 * // Single Finger: Start selection private void startFirst() {
	 * mFirstTimerStarted = true; mReadyToFirst = false; int time = 00; if
	 * (cType == WebHitTestResult.TEXT_TYPE) { time = 2500; // First timer }
	 * else { time = 1500; } handler.postDelayed(mFirstTimer, time); };
	 * 
	 * // Single Finger: Stop selection private void stopFirst(boolean dragging)
	 * { mFirstTimerStarted = false; mReadyToFirst = false;
	 * handler.removeCallbacks(mFirstTimer); if (!dragging) {
	 * applyImageResource(R.drawable.kite_cursor, "kite_cursor");
	 * //pointer.setImageResource(R.drawable.kite_cursor); } };
	 * 
	 * // Single Finger: Set timer for first private void setStartSecond() { if
	 * (!mSecondTimerStarted) { startSecond(); } };
	 * 
	 * // Single Finger: Start selection private void startSecond() {
	 * mSecondTimerStarted = true; mReadyToSecond = false; int time = 0; if
	 * (cType == WebHitTestResult.TEXT_TYPE) { time = 4000; // First timer }
	 * else { time = 3000; } handler.postDelayed(mSecondTimer, time); };
	 * 
	 * // Single Finger: Stop selection private void stopSecond(boolean
	 * dragging) { mSecondTimerStarted = false; mReadyToSecond = false;
	 * handler.removeCallbacks(mSecondTimer); if (!dragging) {
	 * applyImageResource(R.drawable.kite_cursor, "kite_cursor");
	 * //pointer.setImageResource(R.drawable.kite_cursor); } };
	 * 
	 * private void setStartThird() { if (!mThirdTimerStarted) { startThird(); }
	 * };
	 * 
	 * private void startThird() { mThirdTimerStarted = true; mReadyToThird =
	 * false; handler.postDelayed(mThirdTimer, 4500); };
	 * 
	 * private void stopThird(boolean dragging) { mThirdTimerStarted = false;
	 * mReadyToThird = false; handler.removeCallbacks(mThirdTimer); if
	 * (!dragging) { applyImageResource(R.drawable.kite_cursor, "kite_cursor");
	 * //pointer.setImageResource(R.drawable.kite_cursor); } };
	 * 
	 * private void setStartFourth() { if (!mFourthTimerStarted) {
	 * startFourth(); } };
	 * 
	 * // Single Finger: Start selection private void startFourth() {
	 * mFourthTimerStarted = true; mReadyToFourth = false;
	 * handler.postDelayed(mFourthTimer, 6000); };
	 * 
	 * // Single Finger: Stop selection private void stopFourth(boolean
	 * dragging) { mFourthTimerStarted = false; mReadyToFourth = false;
	 * handler.removeCallbacks(mFourthTimer); if (!dragging) {
	 * applyImageResource(R.drawable.kite_cursor, "kite_cursor");
	 * //pointer.setImageResource(R.drawable.kite_cursor); } };
	 * 
	 * private void setStartFifth() { if (!mFifthTimerStarted) { startFifth(); }
	 * };
	 * 
	 * 
	 * private void startFifth() { mFifthTimerStarted = true; mReadyToFifth =
	 * false; handler.postDelayed(mFifthTimer, 6500); };
	 * 
	 * private void stopFifth(boolean dragging) { mFifthTimerStarted = false;
	 * mReadyToFifth = false; handler.removeCallbacks(mFifthTimer); if
	 * (!dragging) { applyImageResource(R.drawable.kite_cursor, "kite_cursor");
	 * //pointer.setImageResource(R.drawable.kite_cursor); } };
	 * 
	 * //Arms first runnable Runnable mFirstTimer = new Runnable() { public void
	 * run() { setFirstRingTab(); mReadyToCero = false; mReadyToFirst = true;
	 * mReadyToSecond = false; mReadyToThird = false; mReadyToFourth = false;
	 * mReadyToFifth = false; } };
	 * 
	 * //Arms second runnable Runnable mSecondTimer = new Runnable() { public
	 * void run() { setSecondRingTab(); mReadyToCero = false; mReadyToFirst =
	 * false; mReadyToSecond = true; mReadyToThird = false; mReadyToFourth =
	 * false; mReadyToFifth = false; } };
	 * 
	 * //Arms third runnable Runnable mThirdTimer = new Runnable() { public void
	 * run() { setThirdRingTab(); mReadyToCero = false; mReadyToFirst = false;
	 * mReadyToSecond = false; mReadyToThird = true; mReadyToFourth = false;
	 * mReadyToFifth = false; } };
	 * 
	 * //Arms fourth runnable Runnable mFourthTimer = new Runnable() { public
	 * void run() { setFourthRingTab(); mReadyToCero = false; mReadyToFirst =
	 * false; mReadyToSecond = false; mReadyToThird = false; mReadyToFourth =
	 * true; mReadyToFifth = false; } };
	 */

	/**
	 * Single Finger: Arms media cursors for selection. Sets mReadyToFirst on.
	 * Sets mReadyToThird to restart cycle after finished.
	 **/
	/*
	 * Runnable mFifthTimer = new Runnable() { public void run() {
	 * 
	 * mCeroTimerStarted = false; mReadyToCero = true;
	 * 
	 * if (mLongTouchHack){ setFingerTimers(hitInputResult.getType(),
	 * hitInputResult.getIdentifier()); }
	 * 
	 * mFirstTimerStarted = false; mReadyToFirst = false;
	 * 
	 * mSecondTimerStarted = false; mReadyToSecond = false;
	 * 
	 * mThirdTimerStarted = false; mReadyToThird = false;
	 * 
	 * mFourthTimerStarted = false; mReadyToFourth = false;
	 * 
	 * mFifthTimerStarted = false; mReadyToFifth = false; } };
	 */

	/**
	 * EXECUTE TIMERS
	 * **/
	private void setCeroRingTab() {
		switch (cType) {
		case WebHitTestResult.TYPE_TEXT_TYPE:
			//ceroText();
			break;
		case WebHitTestResult.TYPE_EDIT_TEXT_TYPE:
			ceroInput();
		case SwifteeApplication.TYPE_PADKITE_INPUT:
			ceroPkInput(false);
			break;
		case WebHitTestResult.TYPE_GEO_TYPE:
			ceroMap();
		case WebHitTestResult.TYPE_EMAIL_TYPE:
			ceroMail();
			break;
		case SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER:
			ceroPKSet(false);
			break;
		case SwifteeApplication.TYPE_PADKITE_PANEL:
			ceroPKPanel(false);
			break;

		case WebHitTestResult.TYPE_SRC_ANCHOR_TYPE:
		case WebHitTestResult.TYPE_ANCHOR_TYPE:
			ceroAnchor(false, false);
			break;
			
		case SwifteeApplication.TYPE_PADKITE_SERVER:
			ceroPKServer(false);
			break;

		default:
			ceroDefault();
			break;
		}
	};

	/*
	 * private void setFirstRingTab() { switch (cType) { case
	 * WebHitTestResult.TEXT_TYPE: firstText(); break; case
	 * WebHitTestResult.EDIT_TEXT_TYPE: firstInput(); break; case
	 * SwifteeApplication.TYPE_PADKITE_INPUT: firstPadKiteInput(); break; case
	 * WebHitTestResult.GEO_TYPE: firstMap(); break; case
	 * WebHitTestResult.EMAIL_TYPE: firstMail(); break; default: firstDefault();
	 * break; } }; private void setSecondRingTab() { switch (cType) { case
	 * WebHitTestResult.TEXT_TYPE: secondText(); break; case
	 * WebHitTestResult.EDIT_TEXT_TYPE: secondInput(); break; default:
	 * secondDefault(); break; } }; private void setThirdRingTab() { switch
	 * (cType) { case WebHitTestResult.TEXT_TYPE: thirdText(); break; case
	 * WebHitTestResult.EDIT_TEXT_TYPE: thirdInput(); break; default:
	 * thirdDefault(); break; } }; private void setFourthRingTab() { switch
	 * (cType) { case WebHitTestResult.TEXT_TYPE: fourthText(); break; case
	 * WebHitTestResult.EDIT_TEXT_TYPE: fourthInput(); break; default:
	 * fourthDefault(); break; } };
	 */

	/**
	 * EXECUTE ALL FIRTS.
	 */
	/*
	 * public void firstText() { }
	 * 
	 * public void firstInput() {
	 * 
	 * }
	 * 
	 * public void firstPadKiteInput() { rCtrl.setTabToTop(1, cType); }
	 * 
	 * public void firstMap() {
	 * 
	 * }
	 * 
	 * public void firstMail() {
	 * 
	 * }
	 * 
	 * public void firstDefault() {
	 * 
	 * }
	 */

	/**
	 * EXECUTE ALL SECOND.
	 */
	/*
	 * public void secondText() {
	 * 
	 * }
	 * 
	 * public void secondInput() {
	 * 
	 * }
	 * 
	 * public void secondPadKiteInput() { rCtrl.setTabToTop(2, cType); }
	 * 
	 * public void secondDefault() {
	 * 
	 * }
	 */

	/**
	 * EXECUTE ALL THIRD.
	 */
	/*
	 * public void thirdText() {
	 * 
	 * }
	 * 
	 * public void thirdInput() {
	 * 
	 * }
	 * 
	 * public void thirdPadKiteInput() { rCtrl.setTabToTop(3, cType); }
	 * 
	 * public void thirdDefault() {
	 * 
	 * }
	 */

	/**
	 * EXECUTE ALL FOURTH.
	 */

	/*
	 * public void fourthText() {
	 * 
	 * }
	 * 
	 * public void fourthInput() {
	 * 
	 * }
	 * 
	 * public void fourthPadKiteInput() { rCtrl.setTabToTop(4, cType); }
	 * 
	 * public void fourthDefault() {
	 * 
	 * }
	 */

	/**
	 * PERSIST CURSORS ON SAME IDENTIFIER.
	 */
	private void persistRingsTabs(int stage) { // MAL, debe contemplar el tipo
		switch (stage) {
		case SwifteeApplication.PERSIST_CERO_STAGE:
			switch (cType) {
			case WebHitTestResult.TYPE_TEXT_TYPE:
				//ceroText();
				break;
			case WebHitTestResult.TYPE_EDIT_TEXT_TYPE:
				ceroInput();
				break;
			case WebHitTestResult.TYPE_GEO_TYPE:
				ceroMap();
				break;
			case WebHitTestResult.TYPE_EMAIL_TYPE:
				ceroMail();
				break;
			default:
				ceroDefault();
				break;
			}
			break;
		/*
		 * case SwifteeApplication.PERSIST_FIRST_STAGE: switch (cType) { case
		 * WebHitTestResult.TEXT_TYPE: firstText(); break; case
		 * WebHitTestResult.EDIT_TEXT_TYPE: firstInput(); break; case
		 * WebHitTestResult.GEO_TYPE: firstMap(); break; case
		 * WebHitTestResult.EMAIL_TYPE: firstMail(); break; default:
		 * firstDefault(); break; } break; case
		 * SwifteeApplication.PERSIST_SECOND_STAGE: switch (cType) { case
		 * WebHitTestResult.TEXT_TYPE: secondText(); break; case
		 * WebHitTestResult.EDIT_TEXT_TYPE: secondInput(); break; default:
		 * secondDefault(); break; } break; case
		 * SwifteeApplication.PERSIST_THIRD_STAGE: switch (cType) { case
		 * WebHitTestResult.TEXT_TYPE: thirdText(); break; case
		 * WebHitTestResult.EDIT_TEXT_TYPE: thirdInput(); break; default:
		 * thirdDefault(); break; } break; case
		 * SwifteeApplication.PERSIST_FOURTH_STAGE: switch (cType) { case
		 * WebHitTestResult.TEXT_TYPE: fourthText(); break; case
		 * WebHitTestResult.EDIT_TEXT_TYPE: fourthInput(); break; default:
		 * fourthDefault(); break; } break;
		 */
		}
		;
	};

	public void drawTip(Rect re, String[] comment, float X, float Y, int isFor) {
		
		disableFC();
		
		int _x = Math.round(X);
		int _y = Math.round(Y);
		int[] initCoor = { _x, _y };
		Object[] paramTip = { re, comment, initCoor, vertical, 2000 };
		tCtrl.setTipComment(paramTip, isFor);
	}

	public Rect getRect() {
		return rect;
	};

	public int getResultType() {
		return resultType;
	};

	public int getIdentifier() {
		return identifier;
	};

	private boolean isYouTube(String lselectedLink) {
		if (lselectedLink.contains("youtube.com/watch")
				|| lselectedLink.contains("m.youtube.com/#/watch"))
			return true;
		return false;
	}

	/**
	 * checks for link type and returns whether it is of type image or video
	 * return 1 for image type return 2 for video type else return 0
	 **/
	private int getLinkType(String lselectedLink) {
		if (lselectedLink.endsWith(".mp4") || lselectedLink.endsWith(".flv")
				|| lselectedLink.endsWith(".mpeg")
				|| lselectedLink.endsWith(".wmv")
				|| lselectedLink.endsWith(".mpg")
				|| lselectedLink.endsWith(".rm")
				|| lselectedLink.endsWith(".mov") || isYouTube(lselectedLink))
			return 2;
		if (lselectedLink.endsWith(".jpg") || lselectedLink.endsWith(".jpeg")
				|| lselectedLink.endsWith(".png")
				|| lselectedLink.endsWith(".gif")
				|| lselectedLink.endsWith(".bmp")
				|| lselectedLink.endsWith(".pdf")
				|| lselectedLink.endsWith(".doc")
				|| lselectedLink.endsWith(".txt"))
			return 1;
		return 0;
	}

	public void startHitTest(int X, int Y) {

		if (!mHitTestMode) {
			mHitTestMode = true;
			moveHitTest(X, Y);
		}
	}

	private void removeTouchPoint() {

		if (fcPointerView != null) {
			pointer.scrollTo(0, 0);
			fcPointerView.scrollTo(0, 0);
			updateFC();
			fcProgressBar.scrollTo(0, 0);
		}
		mTouchPointValid = false;
	}

	public void onPageFinished() {
		applyImageResource(R.drawable.kite_cursor, "kite_cursor");
		// pointer.setImageResource(R.drawable.kite_cursor);
		removeTouchPoint();
	}

	public void enableGestures() {
		mGesturesEnabled = true;
	}

	public void disableGestures() {
		mGesturesEnabled = false;
	}

	/* public interface */

	public void removeSelection() {
		//mWebView.executeSelectionCommand(fcX, fcY, WebView.CLEAR_SELECTION);
	}

	public void startSelectionCommand() {
		startHitTest(fcX, fcY);
		//mWebView.executeSelectionCommand(fcX, fcY, WebView.START_SELECTION);
	}

	public void executeSelectionCommand(int cmd) {
		//mWebView.executeSelectionCommand(fcX, fcY, cmd);
	}

	public void stopSelectionCommand() {
		//mWebView.executeSelectionCommand(fcX, fcY, WebView.STOP_SELECTION);
		//mWebView.executeSelectionCommand(fcX, fcY, WebView.COPY_TO_CLIPBOARD);
	}

	public void onClick() {
		if (mWebHitTestResult == null)
			return;

		mLongTouchEnabled = false;
		//executeSelectionCommand(WebView.STOP_SELECTION);

		if (mWebHitTestResult.getType() == WebHitTestResult.TYPE_ANCHOR_TYPE
				|| mWebHitTestResult.getType() == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE
				|| mWebHitTestResult.getType() == WebHitTestResult.TYPE_SRC_IMAGE_ANCHOR_TYPE) {
			// eventViewer.setText("Executing link ...");

			sendEvent(MotionEvent.ACTION_DOWN, fcX, fcY, false);
			sendEvent(MotionEvent.ACTION_UP, fcX, fcY, false);
			startHitTest(fcX, fcY);
		} else if (mWebHitTestResult.getType() == WebHitTestResult.TYPE_EDIT_TEXT_TYPE
				|| mWebHitTestResult.getType() == WebHitTestResult.TYPE_UNKNOWN_TYPE
				|| mWebHitTestResult.getType() == WebHitTestResult.TYPE_INPUT_TYPE
				|| mWebHitTestResult.getType() == WebHitTestResult.TYPE_SELECT_TYPE
				|| mWebHitTestResult.getType() == -1) {
			// eventViewer.setText("Clicking ...");
			cancelSelection();

			sendEvent(MotionEvent.ACTION_DOWN, fcX, fcY, false);
			sendEvent(MotionEvent.ACTION_UP, fcX, fcY, false);
		} else {

			if (mWebHitTestResult.getType() == WebHitTestResult.TYPE_IMAGE_TYPE) {

				// eventViewer.setText("Selecting image ...");

				//mWebView.executeSelectionCommand(fcX, fcY, WebView.SELECT_OBJECT);
				//mWebView.executeSelectionCommand(fcX, fcY, WebView.COPY_HTML_FRAGMENT_TO_CLIPBOARD);
			} else if (mWebHitTestResult.getType() == WebHitTestResult.TYPE_TEXT_TYPE) {
				// eventViewer.setText("Selecting word ...");

				//mWebView.executeSelectionCommand(fcX, fcY, WebView.SELECT_WORD);
				//mWebView.executeSelectionCommand(fcX, fcY, WebView.COPY_TO_CLIPBOARD);
			}

			startSelection(false);
		}
	}

	public void onAutoSelectionStart(boolean restart) {

		// Nothing for now
	}

	public void onAutoSelectionEnd() {
		// Copy selection to clipboard and such activate gestures
		stopSelectionCommand();
		startSelection(false);
	}

	boolean mLongTouchEnabled = false;
	private String selectedLink = "";
	private String selectedHref = "";

	public void onLongTouch() {
		if (mWebHitTestResult == null)
			return;
		if (mWebHitTestResult.getType() == WebHitTestResult.TYPE_IMAGE_TYPE) {
			// eventViewer.setText("Detected Long-Touch. Selecting image ...");
			//mWebView.executeSelectionCommand(fcX, fcY, WebView.SELECT_OBJECT);
			//mWebView.executeSelectionCommand(fcX, fcY, WebView.COPY_HTML_FRAGMENT_TO_CLIPBOARD);
			mLongTouchEnabled = true;
		} else if (mWebHitTestResult.getType() == WebHitTestResult.TYPE_TEXT_TYPE) {
			// eventViewer.setText("Detected Long-Touch. Selecting word ...");
			//mWebView.executeSelectionCommand(fcX, fcY, WebView.SELECT_WORD);
			//mWebView.executeSelectionCommand(fcX, fcY,WebView.COPY_TO_CLIPBOARD);
			mLongTouchEnabled = true;
		} else if (mWebHitTestResult.getType() == WebHitTestResult.TYPE_ANCHOR_TYPE
				|| mWebHitTestResult.getType() == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE
				|| mWebHitTestResult.getType() == WebHitTestResult.TYPE_SRC_IMAGE_ANCHOR_TYPE) {
			// eventViewer.setText("Detected Long-Touch. Selecting link ...");
			selectedLink = mWebHitTestResult.getExtra();
			Point focusCenter = mWebHitTestResult.getPoint();
			focusCenter.y += 100;
			focusCenter.x += 50;

			// Log.e("SELECT_LINK", "x: " + focusCenter.x + ", y: " +
			// focusCenter.y + ", fcX: " + fcX + ", fcY: " + fcY);

			//mWebView.executeSelectionCommand(focusCenter.x, focusCenter.y,WebView.SELECT_LINK);

			// VER
			((ClipboardManager) mP.getSystemService(Context.CLIPBOARD_SERVICE))
					.setText(selectedLink);

			mP.setSelection(selectedLink);
			// mWebView.executeSelectionCommand(fcX, fcY,
			// WebView.COPY_TO_CLIPBOARD);
			mLongTouchEnabled = true;
		} else if (mWebHitTestResult.getType() == WebHitTestResult.TYPE_EDIT_TEXT_TYPE) {
			// eventViewer.setText("Detected Long-Touch. Pasting clipboard contents ...");
			final String selection = (String) ((ClipboardManager) mP
					.getSystemService(Context.CLIPBOARD_SERVICE)).getText();

			if (selection != "") {
				pasteTextIntoInputText(fcX, fcY, null);
				mLongTouchEnabled = true;
			}

			// mLongTouchEnabled = true;
		}

		vibrator.vibrate(25);
	}

	public void onLongTouchUp() {
		if (mLongTouchHack) {
			mLongTouchHack = false;
			// FIXME: Perhaps replace again at end of function
			mWebHitTestResult = mLongTouchHackObj;
		}

		if (!mLongTouchEnabled) {
			onClick();
			return;
		}

		if (mWebHitTestResult.getType() == WebHitTestResult.TYPE_EDIT_TEXT_TYPE) {
			cancelSelection();
			disableGestures();
			return;
		}

		mLongTouchEnabled = false;

		stopSelectionCommand();
		startSelection(true);

		// Added for distinguishing various selection

		boolean linkFlag = false;

		if (mWebHitTestResult.getType() == WebHitTestResult.TYPE_IMAGE_TYPE) {
			mP.setGestureType(SwifteeApplication.CURSOR_IMAGE_GESTURE);
			linkFlag = true;
		} else if (mWebHitTestResult.getType() == WebHitTestResult.TYPE_VIDEO_TYPE) {
			mP.setGestureType(SwifteeApplication.CURSOR_VIDEO_GESTURE);
			linkFlag = true;
		} else if (mWebHitTestResult.getType() == WebHitTestResult.TYPE_TEXT_TYPE) {
			mP.setGestureType(SwifteeApplication.CURSOR_TEXT_GESTURE);
		} else if (mWebHitTestResult.getType() == WebHitTestResult.TYPE_ANCHOR_TYPE
				|| mWebHitTestResult.getType() == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE
				|| mWebHitTestResult.getType() == WebHitTestResult.TYPE_SRC_IMAGE_ANCHOR_TYPE) {
			int type = getLinkType(selectedLink);
			if (type == 0)
				mP.setGestureType(SwifteeApplication.CURSOR_LINK_GESTURE);
			else if (type == 1)
				mP.setGestureType(SwifteeApplication.CURSOR_IMAGE_GESTURE);
			else
				mP.setGestureType(SwifteeApplication.CURSOR_VIDEO_GESTURE);

			linkFlag = true;
		}

		if (linkFlag) {
			stopSelection();
		}
	}

	public void onLongTouchHack(int fX, int fY) {

		switch (cType) {

		case WebHitTestResult.TYPE_EDIT_TEXT_TYPE:
			setFingerTimers(hitInputResult.getType(),
			hitInputResult.getIdentifier());
			cType = WebHitTestResult.TYPE_EDIT_TEXT_TYPE;
			break;

		case WebHitTestResult.TYPE_SRC_IMAGE_ANCHOR_TYPE:
		case WebHitTestResult.TYPE_SRC_ANCHOR_TYPE:
			cType = 7;
			setLastKnownHitType(WebHitTestResult.TYPE_SRC_ANCHOR_TYPE);
			SwifteeApplication.setCType(cType);
			SwifteeApplication.setExpanded(true);
			setHitPKIndex(SwifteeApplication.TABINDEX_NOTHING);
			disableFC();
			getExtendedRect(true);
			SwifteeApplication.setAnchorRect(anchorRect);		
			ceroAnchor(true, false);
			break;

		case SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER:
			setHitPKIndex(cType);
			ceroPKSet(true);
			disableFC();
			break;

		case SwifteeApplication.TYPE_PADKITE_SERVER:
			setHitPKIndex(cType);
			ceroPKServer(true);
			disableFC();
			break;  
			
		case SwifteeApplication.TYPE_PADKITE_PANEL:
			setHitPKIndex(cType);
			ceroPKPanel(true);
			disableFC();
			break;

		/*
		 * case WebHitTestResult.EDIT_TEXT_TYPE:{ lastKnownHitType = resultType;
		 * if (isLandingPage){ cType = SwifteeApplication.TYPE_PADKITE_INPUT;
		 * SwifteeApplication.setCType(cType); setFocusNodeAtRect();
		 * String inputText = getLandingInputText(); if (!inputText.equals("")){
		 * mP.loadSuggestionIntoArray(inputText); } //resultType = sResultType;
		 * hitPKIndex = SwifteeApplication.TABINDEX_NOTHING; lastKnownHitType =
		 * cType; setRowsHeightAndCount(SwifteeApplication.TABINDEX_CERO,
		 * mP.arrayCERO, true); ceroPkInput(); }
		 * sendEvent(MotionEvent.ACTION_DOWN, x, y);
		 * sendEvent(MotionEvent.ACTION_UP, x, y); focusNodeAt(x, y);
		 * rCtrl.refresh(rect); sCtrl.refresh(rect); return true; }
		 */

		}

		if (mWebHitTestResult != null) {
			mLongTouchHack = true;
			mLongTouchHackObj = mWebHitTestResult;
			// onLongTouch(); REAM LONG TOUCH
		}

		// Put them back to execute touch on touch up
		// fcX = stX;
		// fcY = stY;

		mHitTestMode = false;
	}

	/**
	 * checks for link type and returns whether it is of type image or video
	 * return 1 for image type return 2 for video type else return 0
	 * 
	 * @return
	 */
	private int getLinkType() {
		if (selectedLink.endsWith(".mp4") || selectedLink.endsWith(".flv")
				|| selectedLink.endsWith(".mpeg")
				|| selectedLink.endsWith(".wmv")
				|| selectedLink.endsWith(".mpg")
				|| selectedLink.endsWith(".rm")
				|| selectedLink.endsWith(".mov"))
			return 2;
		if (selectedLink.endsWith(".jpg") || selectedLink.endsWith(".jpeg")
				|| selectedLink.endsWith(".png")
				|| selectedLink.endsWith(".gif")
				|| selectedLink.endsWith(".bmp")
				|| selectedLink.endsWith(".pdf")
				|| selectedLink.endsWith(".doc")
				|| selectedLink.endsWith(".txt"))
			return 1;
		return 0;
	}

	private boolean more = false;
	private int moreIndex = 0;
	private String lineText = null;
	private String loadPage;
	
	

	// ACTIONS
	public void onTouchUp(boolean fingerTouch) {

		// Erase any ring/tip around on touch up.

		int _x = 0;
		int _y = 0;

		if (fingerTouch) {
			_x = stX;
			_y = stY;
		} else if (remote){
			_x = _xRemote;
			_y = _yRemote;
		} else {
			_x = fcX;
			_y = fcY;
		}

		int pkIndex = SwifteeApplication.getPKIndex();
		int tabNumber = SwifteeApplication.getPKTabIndex();

		if (getLastKnownHitType() == WebHitTestResult.TYPE_EDIT_TEXT_TYPE
				&& isLandingPage) {
			setLastKnownHitType(SwifteeApplication.TYPE_PADKITE_INPUT);
		}

		// Tabs included
		// else
		
		if ((resultType != 0 && resultType != 1 && resultType != -1)
				&& !mP.isTabsActivated() ) {
			
			if (cType == WebHitTestResult.TYPE_SRC_IMAGE_ANCHOR_TYPE){				
				sendEvent(MotionEvent.ACTION_DOWN, _x, _y, false);
				sendEvent(MotionEvent.ACTION_UP, _x, _y, false);
			}
			
			return;
		}
			

		if (mReadyToCero) {

			switch (cType) {

			case WebHitTestResult.TYPE_TEXT_TYPE:

				cleanTextSelectionTimers();
				applyImageResource(R.drawable.kite_cursor, "kite_cursor");

				// pointer.setImageResource(R.drawable.text_cursor);

				/*
				 * mGesturesEnabled = true;
				 * mP.setGestureType(SwifteeApplication.CURSOR_TEXT_GESTURE);
				 * mP.startGesture(true); final String selection = (String)
				 * ((ClipboardManager) mP
				 * .getSystemService(Context.CLIPBOARD_SERVICE)).getText();
				 */

				// String[] ceroText = trimSelectedTextForTip();
				// drawTip(rect, ceroText, _x, _y,
				// SwifteeApplication.IS_FOR_WEB_TIPS);

				break;

			case WebHitTestResult.TYPE_SRC_IMAGE_ANCHOR_TYPE:
				break;

			case WebHitTestResult.TYPE_EDIT_TEXT_TYPE:			
				break;

			case SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER:
				mP.loadWindowsManager(idWm, linkTitle);
				break;

			case SwifteeApplication.TYPE_PADKITE_PANEL:
				break;

			case SwifteeApplication.TYPE_PADKITE_BUTTON:				
				
				int pkBtIndex = pkIndex - 20;
				String[] buttonsArray = SwifteeApplication.getSuggestionButtonsArray();
				String buttonString = buttonsArray[pkBtIndex];

				if (!buttonString.equals("▲") && !buttonString.equals("▼")
						&& !buttonString.equals("□")
						&& !buttonString.equals("■")) {
					
					int searchEngine;
					
					switch (pkBtIndex){
						case 0:
							searchEngine = SwifteeApplication.WEB_SEARCH;
							break;
						case 1:
							searchEngine = SwifteeApplication.IMAGE_BUTTON;
							break;
						case 2:
							searchEngine = SwifteeApplication.VIDEO_BUTTON;
							break;
						case 3:
							searchEngine = SwifteeApplication.WIKI_BUTTON;
							break;	
						default:
							searchEngine = SwifteeApplication.WEB_SEARCH;
							break;
					}
					
					setSearchByButton(searchEngine);
					SwifteeApplication.setCType(SwifteeApplication.TYPE_PADKITE_INPUT);					
					String iT = mP.getInputBoxText();					
					mP.launchSuggestionSearch(iT);				
				}				

				break;

			case SwifteeApplication.TYPE_PADKITE_TIP_BUTTON:

				switch (getLastKnownHitType()) {

				case SwifteeApplication.TYPE_PADKITE_INPUT:
					stopLoading();
					tCtrl.drawNothing();
					drawPadKiteInputToCERO();
					break;

				}

				break;

			case SwifteeApplication.TYPE_PADKITE_TAB:
			case SwifteeApplication.TYPE_PADKITE_INPUT:			
		
					if (!remote){
						disableFC();
					} 
					
					//sendEvent(MotionEvent.ACTION_DOWN, _x, _y, false);
					//sendEvent(MotionEvent.ACTION_UP, _x, _y, false);		
				
				/*setRowsHeightAndCount(SwifteeApplication.TABINDEX_CERO, mP.arrayCERO, true);
				Rect re = SwifteeApplication.getMasterRect();
				rCtrl.setTabToTop(SwifteeApplication.TABINDEX_CERO, SwifteeApplication.DRAW_INPUT_TABS);
				rCtrl.refresh(re);
				sCtrl.refresh(re, null);
				setFocusNodeAtRect(re);
				touchDownUpAtMasterRect(re);*/				
				
				break;

			case SwifteeApplication.TYPE_PADKITE_ROW:

				sCtrl.cleanRowsOver();
				int rowIndex = SwifteeApplication.getPKIndex();

				switch (lastKnownHitType) {

					case SwifteeApplication.TYPE_PADKITE_INPUT:
					
					/*if (SwifteeApplication.getLandingShrinked()){	
						
						if (!remote){
							disableFC();
						}
						
						handler.removeCallbacks(mCeroTimer);
						
						eraseDraws();
						SwifteeApplication.cleanAllRects();
						
			    		sendEvent(MotionEvent.ACTION_DOWN, _x, _y, true);
			    		sendEvent(MotionEvent.ACTION_UP, _x, _y, true);   	

 					} else {*/				
	
 						if (SwifteeApplication.getIsArrayWiki()) { 
 							
 							//wiki here
 							//http://p-xr.com/android-tutorial-how-to-parseread-xml-data-into-android-listview/
 							lineText = getRowTextByIndex(tabNumber, (pkIndex - 10));		
 							
 							String wikiId = mP.arrayCERO[pkIndex][4];
 							
 							//Get the xml string from the server
 							String url = "http://en.wikipedia.org/w/api.php?action=query&prop=info&pageids="+wikiId+"&inprop=url"; 							
 							String xml = XmlFunctions.getXML(url); 							
 							Document doc = XmlFunctions.XMLfromString(xml);						 

 							NodeList nodes = doc.getElementsByTagName("pages"); 	
 							String pageUrl = null;
 							
 					        for (int i = 0; i < nodes.getLength(); i++) {   
 					        	
 					            Element e = (Element)nodes.item(i);					         
 					           pageUrl = XmlFunctions.getValue(e, "fullurl");
 					         }  
 					        
 					       loadPage = pageUrl;
 					       handler.postDelayed(messageRunnable, 2000);
 							
 						 } else  {
 							 
 							lineText = getRowTextByIndex(tabNumber, (pkIndex - 10));
 							mP.insertRecent(lineText);
 							String activeSearch = SwifteeApplication.getActiveSearch();
 		
 							
 							int as = SwifteeApplication.getActiveSearchCheck();
 							
 							if ( as == -1) {
 								
 								//loadPage("javascript:setInputText('" + lineText + "');");
 																
 								Object[] params = new Object[2];
 								params[0] = true;
 								params[1] = lineText;
 								
 								pasteTextIntoInputText(mP.getInputX(), mP.getInputY(), params);				
 								
 								
 							} else {
 								
 								String activeSearchTerm = SwifteeApplication.getActiveSearchTerm();
	 							String[] searchMsg = { activeSearchTerm, "for: " + lineText };
	 		
	 							drawPadKiteMessage(searchMsg, "STOP");
	 		
	 							loadPage = activeSearch + lineText;
	 							
	 							handler.postDelayed(messageRunnable, 2000);
 							}
 						 }	
						
	
						sCtrl.cleanRowsOver();
 					//}
					
					break;

				case WebHitTestResult.TYPE_SRC_ANCHOR_TYPE:
				case WebHitTestResult.TYPE_SRC_IMAGE_ANCHOR_TYPE:

					int openRowIndex = rowIndex - 10;

					String whatMessage = null;

					switch (openRowIndex) {

					case SwifteeApplication.TABINDEX_CERO:
						SwifteeApplication.setOp0enTye(SwifteeApplication.OPEN_LINK);
						whatMessage = "Opening link";
						break;
					case SwifteeApplication.TABINDEX_FIRST:
						SwifteeApplication.setOp0enTye(SwifteeApplication.OPEN_LINK_IN_NEW_WINDOW);
						whatMessage = "Opening link in new Window";
						break;
					case SwifteeApplication.TABINDEX_SECOND:
						SwifteeApplication.setOp0enTye(SwifteeApplication.OPEN_LINK_IN_BACKGROUND);
						whatMessage = "Opening link on the background";
						break;
					}

					if (!selectedLink.equals("")) {
						selectedLink = selectedLink;
					}
								
					String[] openMessage = { whatMessage, selectedLink };
					loadPage = selectedLink;
					mP.setTipsActivated(true);
					drawPadKiteMessage(openMessage, "STOP");
					handler.postDelayed(messageRunnable, 2000);
					sCtrl.cleanRowsOver();

					break;

				}

				break;

			case WebHitTestResult.TYPE_ANCHOR_TYPE:
				/*
				 * Vector actions = getActionText("Opening "); if
				 * (actions.size()==1){ String[] t1 = { (String) actions.get(0)
				 * }; drawTip(rect, t1, _x, _y,
				 * SwifteeApplication.IS_FOR_WEB_TIPS); } else if
				 * (actions.size()==2) { String[] t2 = { (String)
				 * actions.get(0), (String) actions.get(1) }; drawTip(rect, t2,
				 * _x, _y, SwifteeApplication.IS_FOR_WEB_TIPS); }
				 */
				break;
			}
			/*
			 * if (mWebHitTestResult.getType() != WebHitTestResult.TEXT_TYPE) {
			 * sendEvent(MotionEvent.ACTION_DOWN, fcX, fcY);
			 * sendEvent(MotionEvent.ACTION_UP, fcX, fcY); }
			 */

			//stopAllTimers();

		} else if (mReadyToFirst) {

			switch (cType) {

			case WebHitTestResult.TYPE_TEXT_TYPE:

				/*
				 * mGesturesEnabled = true;
				 * mP.setGestureType(SwifteeApplication.CURSOR_TEXT_GESTURE);
				 * mP.startGesture(true);
				 */

				// String[] secondText = trimSelectedTextForTip();
				// drawTip(rect, secondText, _x, _y,
				// SwifteeApplication.IS_FOR_WEB_TIPS);

				break;

			case WebHitTestResult.TYPE_SRC_IMAGE_ANCHOR_TYPE:
				// eventViewer.setText("Opening image in link...");
				//mWebView.executeSelectionCommand(_x, _y, WebView.SELECT_OBJECT);
				//mWebView.executeSelectionCommand(_x, _y, WebView.COPY_TO_CLIPBOARD);
				break;

			case WebHitTestResult.TYPE_EDIT_TEXT_TYPE:
				pasteTextIntoInputText(fcX, fcY, null);
				break;

			// PASTE
			case SwifteeApplication.TYPE_PADKITE_TAB:
			case SwifteeApplication.TYPE_PADKITE_INPUT:
				
				if (!remote){
					disableFC();
				}				
				break;
				
			case SwifteeApplication.TYPE_PADKITE_ROW:
				
				int last = getLastKnownHitType();
				
				switch (last){
				
					case WebHitTestResult.TYPE_SRC_ANCHOR_TYPE:			
						
						((ClipboardManager) mP.getSystemService(Context.CLIPBOARD_SERVICE)).setText(linkTitle);
						
						String[] searchMsg = { linkTitle, "Has been copied into the clipboard" };
		
						drawPadKiteMessage(searchMsg, "OK");	
						
						mP.insertClipBoard(linkTitle);
							
						handler.postDelayed(messageRunnable, 2000);					
						
						break;	
			
				}	
				break;
				/*if (!SwifteeApplication.getLandingShrinked()){
				
					if (!remote){
						disableFC();
					}
									
		    		sendEvent(MotionEvent.ACTION_DOWN, _x, _y, true);
		    		sendEvent(MotionEvent.ACTION_UP, _x, _y, true);   	
	    		
				} else {

					String clipboard = (String) ((ClipboardManager) mP.getSystemService(Context.CLIPBOARD_SERVICE)).getText();
	
					if (!clipboard.equals("")) {
						pasteTextIntoInputText(_x, _y, null);
						setRowsHeightAndCount(SwifteeApplication.TABINDEX_FIRST, mP.arrayFIRST, true);
						Rect re = SwifteeApplication.getMasterRect();
						rCtrl.refresh(re);						
						Object[] param = new Object[1];
						param[0] = SwifteeApplication.PANTONE_YellowC_MAIN;					
						sCtrl.refresh(re, param);
						setFocusNodeAtRect(re);
						touchDownUpAtMasterRect(re);
					} else {
						String[] message = { "Clipboard is empty." };
						drawPadKiteMessage(message, "OK");
					}
					
				}*/				

			default:
				((ClipboardManager) mP.getSystemService(Context.CLIPBOARD_SERVICE)).setText(selectedLink);
				mP.setSelection(selectedLink);

				Vector actions = getActionText("Copied to clipboard");
				if (actions.size() == 1) {
					String[] t1 = { (String) actions.get(0) };
					drawTip(rect, t1, _x, _y, SwifteeApplication.IS_FOR_WEB_TIPS);
				} else if (actions.size() == 2) {
					String[] t2 = { (String) actions.get(0),(String) actions.get(1) };
					drawTip(rect, t2, _x, _y, SwifteeApplication.IS_FOR_WEB_TIPS);
				}

				break;
			}

			/*
			 * stopFirst(false); stopSecond(false); stopThird(false);
			 */

		} else if (mReadyToSecond) {

			switch (cType) {

			case SwifteeApplication.TYPE_PADKITE_TAB:
			case SwifteeApplication.TYPE_PADKITE_INPUT:
				
				if (!SwifteeApplication.getLandingShrinked()){
					
					if (!remote){
						disableFC();
					}
									
		    		sendEvent(MotionEvent.ACTION_DOWN, _x, _y, true);
		    		sendEvent(MotionEvent.ACTION_UP, _x, _y, true);   	
	    		
				} else {
				
					SwifteeApplication.setPKIndex(SwifteeApplication.TABINDEX_CERO);
					setRowsHeightAndCount(SwifteeApplication.TABINDEX_CERO, mP.arrayCERO, true);
					Rect re = SwifteeApplication.getMasterRect();
					rCtrl.setTabToTop(SwifteeApplication.TABINDEX_CERO, SwifteeApplication.DRAW_INPUT_TABS);
					rCtrl.refresh(re);
					Object[] param = new Object[1];
					param[0] = COLOR.PANTONE_246C_MAIN;
					sCtrl.refresh(re, param);					
					touchDownUpAtMasterRect(re);
					
				}

				/*
				 * setRowsHeightAndCount(SwifteeApplication.TABINDEX_SECOND,
				 * mP.arraySECOND, true); Rect re =
				 * SwifteeApplication.getMasterRect();
				 * rCtrl.setTabToTop(SwifteeApplication.TABINDEX_SECOND,
				 * SwifteeApplication.DRAW_INPUT_TABS); rCtrl.refresh(re);
				 * sCtrl.refresh(re); setFocusNodeAtRect(re);
				 * touchDownUpAtMasterRect(re);
				 */

				break;
				
			case SwifteeApplication.TYPE_PADKITE_ROW:
			
				lineText = getRowTextByIndex(tabNumber, (pkIndex - 10));
				String url = mP.arraySECOND[pkIndex][1];				
				
				SwifteeApplication.setOp0enTye(SwifteeApplication.OPEN_LINK);
				String whatMessage = "Opening link";				

				String[] openMessage = { whatMessage, selectedLink };
				loadPage = selectedLink;
				mP.setTipsActivated(true);
				drawPadKiteMessage(openMessage, "STOP");
				handler.postDelayed(messageRunnable, 2000);
				
				sCtrl.cleanRowsOver();			
				
				//loadPage(url);
				break;
			
			case WebHitTestResult.TYPE_TEXT_TYPE:

				/*
				 * mGesturesEnabled = true;
				 * mP.setGestureType(SwifteeApplication.CURSOR_TEXT_GESTURE);
				 * mP.startGesture(true);
				 */

				// String[] thirdText = trimSelectedTextForTip();
				// drawTip(rect, thirdText, _x, _y,
				// SwifteeApplication.IS_FOR_WEB_TIPS);
				break;

			/*
			 * case WebHitTestResult.SRC_IMAGE_ANCHOR_TYPE:
			 * eventViewer.setText("Opening image in link...");
			 * mWebView.executeSelectionCommand(fcX, fcY,
			 * WebView.SELECT_OBJECT); mWebView.executeSelectionCommand(fcX,
			 * fcY, WebView.COPY_TO_CLIPBOARD); break;
			 */

			case WebHitTestResult.TYPE_EDIT_TEXT_TYPE:
				// eventViewer.setText("Opening voice recognition...");
				mP.startVoiceRecognitionActivity(_x, _y);
				break;

			default:				
				((ClipboardManager) mP.getSystemService(Context.CLIPBOARD_SERVICE)).setText(selectedLink);
				mGesturesEnabled = true;
				mP.setGestureType(SwifteeApplication.CURSOR_LINK_GESTURE);
				mP.startGesture(true);

				Vector actions = getActionText("Draw a gesture for");
				if (actions.size() == 1) {
					String[] t1 = { (String) actions.get(0) };
					drawTip(rect, t1, _x, _y, SwifteeApplication.IS_FOR_WEB_TIPS);
				} else if (actions.size() == 2) {
					String[] t2 = { (String) actions.get(0), (String) actions.get(1) };
					drawTip(rect, t2, _x, _y, SwifteeApplication.IS_FOR_WEB_TIPS);
				}
				break;
			}

			// stopSecond(false);
			// stopThird(false);

		} else if (mReadyToThird) {

			switch (cType) {

			case SwifteeApplication.TYPE_PADKITE_TAB:
			case SwifteeApplication.TYPE_PADKITE_INPUT:
				setRowsHeightAndCount(SwifteeApplication.TABINDEX_THIRD, mP.arrayTHIRD, true);
				Rect re = SwifteeApplication.getMasterRect();
				rCtrl.refresh(re);
				Object[] param = new Object[1];
				param[0] = COLOR.PANTONE_246C_MAIN;
				sCtrl.refresh(re, param);				
				touchDownUpAtMasterRect(re);
				break;

			}

		} else if (mReadyToFourth) {

			switch (cType) {

			case SwifteeApplication.TYPE_PADKITE_TAB:
			case SwifteeApplication.TYPE_PADKITE_INPUT:
				setRowsHeightAndCount(SwifteeApplication.TABINDEX_FOURTH, mP.arrayFOURTH, true);
				Rect re3 = SwifteeApplication.getMasterRect();
				rCtrl.refresh(re3);
				sCtrl.refresh(re3, null);				
				touchDownUpAtMasterRect(re3);
				break;

			case SwifteeApplication.TYPE_PADKITE_TIP_BUTTON:

				switch (tabNumber) {

				case SwifteeApplication.TABINDEX_CERO:
					break;

				case SwifteeApplication.TABINDEX_FOURTH:

					switch (getLastKnownHitType()) {

					case SwifteeApplication.TYPE_PADKITE_INPUT:
						drawPadKiteInputToCERO();
						break;
					}

					break;

				}

				break;

			case SwifteeApplication.TYPE_PADKITE_ROW:

				sCtrl.cleanRowsOver();
				int rowIndex = SwifteeApplication.getPKIndex();

				switch (getLastKnownHitType()) {

				case SwifteeApplication.TYPE_PADKITE_INPUT:

					String inputText = getLandingInputText();				

					/** INPUT DOES NOT HAVE TEXT **/
					if (inputText.equals("")
						&& (rowIndex-10) != SwifteeApplication.TABINDEX_FIRST) {
					
						String rowText = getRowTextByIndex(tabNumber, (pkIndex-10)).toLowerCase();
					
						rowText = " " + rowText + ".";
						String[] message = {"The input box is empty to" + rowText,"Please enter new text." };
						
						drawPadKiteMessage(message, "CLOSE");
						return;

					} else {
						
						/** INPUT HAS TEXT **/
						
						int moreRowIndex = rowIndex - 10;
						
						switch (moreRowIndex) {
						
						case SwifteeApplication.TABINDEX_CERO: // COPY
							storeInputText(inputText, false);
							String[] m0 = { "The input text has been copied",
									"into the clipboard" };
							drawPadKiteMessage(m0, "CLOSE");
							break;
											
						//paste
						case SwifteeApplication.TABINDEX_FIRST: // PASTE	
							
							String mem = (String) ((ClipboardManager) mP.getSystemService(Context.CLIPBOARD_SERVICE)).getText();
							
							//String hola;
							
							if (mem.equals("")){
								
								String[] m1 = { "The clipboard is empty",
								"please copy the content again" };
								drawPadKiteMessage(m1, "CLOSE");	

							} else {
								
								Object[] params = new Object[2];
								params[0] = true;
								params[1] = mem;					
								
								pasteTextIntoInputText(mP.getInputX(), mP.getInputY(), params);
								
							}		
							
							
							break;
						
						case SwifteeApplication.TABINDEX_SECOND: // CUT ALL
							storeInputText(inputText, true);
							String[] m1 = { "The input text has been cut",
									"and copied the clipboard" };
							drawPadKiteMessage(m1, "CLOSE");
							break;	
							
						case SwifteeApplication.TABINDEX_THIRD: // SHARE	
							String lText = getLandingInputText();
							String[] m2 = { "Sharing input box with",
									"other applications..." };
							mP.insertRecent(lText);
							drawPadKiteMessage(m2, "STOP");
							mP.send("PadKite input box text", lText);
							break;
							
						/*case SwifteeApplication.TABINDEX_THIRD: // GET URL LINKS
							
							String url = getLandingInputText();
							boolean reachable = mP.pingPage(url);
							if (reachable) {
						
							} else {
								String[] m3 = {
										"The url provided cannot be reached",
										"Please provide an online web page." };
								drawPadKiteMessage(m3, "CLOSE");
							}
						
							break;*/
						
						/*case SwifteeApplication.TABINDEX_FOURTH: // IMPUT TYPE
							Rect rectInput = SwifteeApplication.getMasterRect();
							setInputType = true;
							setFocusNodeAtRect(rectInput);
							touchDownUpAtMasterRect(rectInput);
							break;
						}*/
						}

					}
					
					/*
					 arrayMoreInput = new String[5][2];
			        arrayMoreInput[0][0] = "COPY";
			        arrayMoreInput[0][1] = "";
			        arrayMoreInput[1][0] = "PASTE";
			        arrayMoreInput[1][1] = "";        
			        arrayMoreInput[2][0] = "CUT";
			        arrayMoreInput[2][1] = "";       
			        arrayMoreInput[3][0] = "SHARE";
			        arrayMoreInput[3][1] = "";    
					  */

					break;
				}

				break;
			}

		} else {
		
			if (cType==SwifteeApplication.TYPE_PADKITE_INPUT){	
				
				if (!remote){
					disableFC();
				}
				
				handler.removeCallbacks(mCeroTimer);
				
				eraseDraws();
				SwifteeApplication.cleanAllRects();			
	    		
				loadPage("javascript:setInputFocus()");					
				loadPage("javascript:toogleFCContainer()");	

			}			
			
		}
		applyImageResource(R.drawable.kite_cursor, "kite_cursor");
		// pointer.setImageResource(R.drawable.kite_cursor);

	}
	
	

	
	/*private AsyncTask touchUpEvent = new TouchUpEvent();   
    
	 public class TouchUpEvent extends AsyncTask  {    
	          
		@Override
		protected Object doInBackground(Object... arg) {		
			
			handler.removeCallbacks(mCeroTimer);
			
			int x;
			int y;
			
			if (remote){
				x = _xRemote;
				y = _yRemote;
			} else {
				x = fcX;
				y = fcY;
			}	
			
    		sendEvent(MotionEvent.ACTION_DOWN, x, y, true);
    		sendEvent(MotionEvent.ACTION_UP, x, y, true);
			
	    	return true;
		}		
	} */


	private void setSearchByButton(int buttonId) {
		switch (buttonId) {
		case 0:
			buttonId = SwifteeApplication.WIKI_BUTTON;
			break;
		case 1:
			buttonId = SwifteeApplication.IMAGE_BUTTON;
			break;
		case 2:
			buttonId = SwifteeApplication.VIDEO_BUTTON;
			break;
		}
		SwifteeApplication.setActiveSearch(buttonId);
	}

	public void drawPadKiteInputToCERO() {

		tCtrl.drawNothing();
		mP.setTipsActivated(false);
		if (isLandingPage) {
			cType = SwifteeApplication.TYPE_PADKITE_INPUT;
		} else {
			cType = WebHitTestResult.TYPE_EDIT_TEXT_TYPE;
		}
		Rect re4 = SwifteeApplication.getMasterRect();
		SwifteeApplication.setCType(cType);

		setLastKnownHitType(WebHitTestResult.TYPE_EDIT_TEXT_TYPE);
		SwifteeApplication.setLastKnownHitType(getLastKnownHitType());

		SwifteeApplication.setPKTabIndex(SwifteeApplication.TABINDEX_CERO);
		rCtrl.refresh(re4);
		rCtrl.setTabToTop(SwifteeApplication.TABINDEX_CERO,
				SwifteeApplication.DRAW_INPUT_TABS);
		ceroPkInput(true);
		
		touchDownUpAtMasterRect(re4);

	}

	Runnable messageRunnable = new Runnable() {
		public void run() {

			int openType = SwifteeApplication.getOpenType();

			switch (openType) {
				case SwifteeApplication.OPEN_LINK:
					loadPage(loadPage);
					break;
				case SwifteeApplication.OPEN_LINK_IN_NEW_WINDOW:
					addNewWindow(true, false);
					break;
				case SwifteeApplication.OPEN_LINK_IN_BACKGROUND:
					addNewWindow(true, true);
					break;
			}

			eraseDraws();
			enableFC();
			mP.setTipsActivated(false);
		}
	};

	private void drawPadKiteMessage(String[] message, String buttonText) {
		
		disableFC();
		
		Vector vText = new Vector();
		vText.add(message);
		
		if (buttonText!=null){
			vText.add(buttonText);
			SwifteeApplication.setHasTipButton(true);
		} else {
			SwifteeApplication.setHasTipButton(false);			
		}
		
		mP.setTipsActivated(true);		
		Rect re = new Rect();	
		
		if (getLastKnownHitType()== WebHitTestResult.TYPE_SRC_ANCHOR_TYPE){
			
			re = SwifteeApplication.getCompleteRect();		
			int width = re.width();
			
			if (width < 300){
				
				switch (vertical) {
				
					case SwifteeApplication.VERTICAL_CENTER_COLUMN:
					case SwifteeApplication.VERTICAL_LEFT_COLUMN:					
						
						re.right = re.right + 500;			
						
						break;
				
					case SwifteeApplication.VERTICAL_RIGHT_COLUMN:
						
						re.left = re.left - 500;			
						
						break;
						
					}
				
				SwifteeApplication.setTipMessageBigger(true);
				
			} else {
				
				SwifteeApplication.setTipMessageBigger(false);
			}
			
		} else  if (getLastKnownHitType()== WebHitTestResult.TYPE_EDIT_TEXT_TYPE){
			re = SwifteeApplication.getMasterRect();
			vertical = -1;
		}
		
		int[] active_color_for_tip =  SwifteeApplication.getActiveColor();		
		Object[] paramTip = { re, vText, active_color_for_tip, vertical, 2000 };
		tCtrl.setTipComment(paramTip, SwifteeApplication.IS_FOR_CONTENT_OBJECT);
		
		if (remote==false){
			enableFC();
		}
		
		sCtrl.drawNothing();
	}

	private String getRowTextByIndex(int tabNumber, int tabIndex) {
		String line = null;
		switch (tabNumber) {
		case SwifteeApplication.TABINDEX_CERO:
			line = mP.arrayCERO[tabIndex][0];
			break;

		case SwifteeApplication.TABINDEX_FIRST:
			line = mP.arrayFIRST[tabIndex][0];
			break;

		case SwifteeApplication.TABINDEX_SECOND:
			line = mP.arraySECOND[tabIndex][0];
			break;

		case SwifteeApplication.TABINDEX_THIRD:
			line = mP.arrayTHIRD[tabIndex][0];
			break;

		case SwifteeApplication.TABINDEX_FOURTH:
			more = true;
			moreIndex = tabIndex;
			line = mP.arrayFOURTH[tabIndex][0];
			break;
		}
		return line;
	}

	/*
	 * private String checkLink(){
	 * 
	 * String linkeLabel = null; if ( linkTitle==null ){
	 * 
	 * if ( linkUrl==null ){ linkeLabel = selectedLink; } else { linkeLabel =
	 * linkUrl; } linkeLabel = gdn.getDomain(linkeLabel);
	 * 
	 * } else { linkeLabel = linkTitle; } return linkeLabel; }
	 */
	
	public String getLandingInputText() {
		String text = new String();
		loadPage("javascript:getInputText()");
		text = SwifteeApplication.getInputText();
		if (text == null) {
			return "";
		}
		return text;
	}

	public void storeInputText(String text, boolean cut) {
		((ClipboardManager) mP.getSystemService(Context.CLIPBOARD_SERVICE))
				.setText(text);
		mP.insertClipBoard(text);
		mP.loadClipBoardIntoArray();
		if (cut) {
			loadPage("javascript:clearInput()");
		}
	}

	public void setRowsHeightAndCount(int hitPKIndex, String[][] activeArray,
			boolean expanded) {
	
		SwifteeApplication.setExpanded(expanded);

		expandToFinger = expanded;

		int cType = SwifteeApplication.getCType();

		switch (hitPKIndex) {
		
			case SwifteeApplication.TABINDEX_CERO:
				mP.arrayCERO = activeArray;
				break;
			case SwifteeApplication.TABINDEX_FIRST:
				mP.arrayFIRST = activeArray;
				break;
			case SwifteeApplication.TABINDEX_SECOND:
				mP.arraySECOND = activeArray;
				break;
			case SwifteeApplication.TABINDEX_THIRD:
				mP.arrayTHIRD = activeArray;
				break;
			case SwifteeApplication.TABINDEX_FOURTH:
				mP.arrayFOURTH = activeArray;
				break;
				
		}

		int amount = 0;
		int length = 0;

		if ( remote //&& SwifteeApplication.getOrientation() == SwifteeApplication.ORIENTATION_LANDSCAPE 
				&& cType == SwifteeApplication.TYPE_PADKITE_PANEL ){
			
			amount = 3; //HARDCODED 
			SwifteeApplication.setRowAmount(amount);
			
		} else if (activeArray != null) {
		
					length = activeArray.length;
		
					if (length > SwifteeApplication.getAmountOfRows()) {
		
						if (expanded) {
		
							if (SwifteeApplication.getIsArrayVideo()) {
								amount = SwifteeApplication.getVideoRowsAmount();
							} else if (SwifteeApplication.getIsArrayWiki()) { 
								amount = SwifteeApplication.getWikiRowsAmount();						
							} else	{
								amount = SwifteeApplication.getAmountOfRowsExpanded();
							}
		
						} else {
							amount = SwifteeApplication.getAmountOfRows();
						}
					} else {
						amount = length;
					}
		
				} else {
					amount = 0;
					SwifteeApplication.setRowAmount(amount);
				}
		

		if (amount != 0) {
 
			if (

			/**
			 * PADKITE INPUT PADKITE PANEL
			 * **/
			(cType == SwifteeApplication.TYPE_PADKITE_INPUT || cType == SwifteeApplication.TYPE_PADKITE_PANEL)

					|| (cType == SwifteeApplication.TYPE_PADKITE_TAB && getLastKnownHitType() == SwifteeApplication.TYPE_PADKITE_INPUT)

					|| (cType == SwifteeApplication.TYPE_PADKITE_TIP_BUTTON && getLastKnownHitType() == SwifteeApplication.TYPE_PADKITE_INPUT)

					|| (cType == SwifteeApplication.TYPE_PADKITE_TAB && getLastKnownHitType() == SwifteeApplication.TYPE_PADKITE_PANEL)

					|| (cType == SwifteeApplication.TYPE_PADKITE_TAB && getLastKnownHitType() == WebHitTestResult.TYPE_EDIT_TEXT_TYPE)
					
					|| (cType == SwifteeApplication.TYPE_PADKITE_BACKGROUND && getLastKnownHitType() == WebHitTestResult.TYPE_EDIT_TEXT_TYPE)) 
				
					
			{

				if (expanded) {

					SwifteeApplication.setSuggestionRowTextSize(30);
					
					if (SwifteeApplication.getIsArrayVideo()) {
						
						SwifteeApplication.setSuggestionRowHeight(100);
						SwifteeApplication.setIsArrayVideo(false);
						
					} else if (SwifteeApplication.getIsArrayWiki()) {
						
						SwifteeApplication.setSuggestionRowHeight(70);
						SwifteeApplication.setIsArrayWiki(false);
						
					} else {
						SwifteeApplication.setSuggestionRowHeight(50);
					}
					
					SwifteeApplication.setTabHeight(50);
					BrowserActivity.setSuggestionActivatedExpanded(true);

				} else {

					SwifteeApplication.setSuggestionRowTextSize(24);
					SwifteeApplication.setSuggestionRowHeight(35);
					SwifteeApplication.setTabHeight(35);
					BrowserActivity.setSuggestionActivatedExpanded(false);

				}

				SwifteeApplication.setRowAmount(amount);

			} else if (

			/**
			 * ANCHOR WINDOWS MANAGER
			 **/

			(cType == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE 
					|| cType == SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER
					|| cType == SwifteeApplication.TYPE_PADKITE_SERVER)

					|| (cType == SwifteeApplication.TYPE_PADKITE_TAB && getLastKnownHitType() == SwifteeApplication.TYPE_PADKITE_WINDOWS_MANAGER)
					
					|| (cType == SwifteeApplication.TYPE_PADKITE_TAB && getLastKnownHitType() == SwifteeApplication.TYPE_PADKITE_SERVER)

					|| (cType == SwifteeApplication.TYPE_PADKITE_TAB && getLastKnownHitType() == WebHitTestResult.TYPE_SRC_ANCHOR_TYPE)) {

				if (expanded) {

					SwifteeApplication.setSuggestionRowTextSize(30);
					SwifteeApplication.setSuggestionRowHeight(40);
					SwifteeApplication.setTabHeight(50);
					BrowserActivity.setSuggestionActivatedExpanded(true);

				} else {

					SwifteeApplication.setSuggestionRowTextSize(24);
					SwifteeApplication.setSuggestionRowHeight(35);
					SwifteeApplication.setTabHeight(35);
					BrowserActivity.setSuggestionActivatedExpanded(false);
				}

				SwifteeApplication.setRowAmount(amount);
			}
		} else {

			if (expanded) {

				SwifteeApplication.setTabHeight(45);

			} else {

				SwifteeApplication.setTabHeight(35);

			}
		}
	}

	public boolean isExpandToFinger() {
		return expandToFinger;
	}

	private Vector getActionText(String what) {

		String action = null;
		String url = null;
		Vector ret = new Vector();

		if (linkTitle == null) {

			if (selectedLink == null) {
				url = linkUrl;
			} else {

				url = selectedLink;
			}

		} else {
			url = linkTitle;
			action = what + " Link";
		}

		if (mWebHitTestResult.getType() == WebHitTestResult.TYPE_GEO_TYPE) {

			action = what + " Map";

		} else if (url.contains("@")) {

			action = "Sending Mail To";
			StringTokenizer tokens = new StringTokenizer(url, "?");
			String first = tokens.nextToken();// this will contain "Fruit".
			url = first;
			// url = first.substring(7, first.length());
			linkUrl = null;

		} else if (url.startsWith("vnd.youtube:")) {

			action = what + " Video";

		} else if (url.startsWith("http://")) {

			action = what + "Link";
			url = gdn.getDomain(url);

		} else if (url.contains("html#video")) {

			action = "Search Video";
			url = null;

		} else if (url.contains("html#image")) {

			action = "Search Image";
			url = null;
		}

		ret.add(action);

		if (url != null) {
			ret.add(url);
		}

		return ret;

	}

	/*
	 * private String[] trimSelectedTextForTip() { String selectionText = null;
	 * final String selText = (String) ((ClipboardManager) mP
	 * .getSystemService(Context.CLIPBOARD_SERVICE)).getText(); if
	 * (!mReadyToCero) { selectionText = selText.substring(0, 8) + "..."; } else
	 * { selectionText = selText; } String word = "<b>" + selectionText + "</b>"
	 * + " selected"; String[] t1 = { word, "draw a gesture" }; return t1; }
	 */

	public int getFcX() {
		return fcX;
	}

	public int getFcY() {
		return fcY;
	}
	
	/**
	 * Finally paste the text into the Input Text with stored x,y.
	 * **/
	// final Instrumentation instrumentation = new Instrumentation();

	int _x_;
	int _y_;
	public String fakeClipboard;
	public boolean clipbopard;
	private boolean textIntoInputText;

	public void pasteTextIntoInputText(int X, int Y, Object[] params) {

		textIntoInputText = true;

		_x_ = X;
		_y_ = Y;

		if (params != null) {
			clipbopard = (Boolean) params[0];
			fakeClipboard = (String) params[1];
		}

		sendEvent(MotionEvent.ACTION_DOWN, X, Y, false);
		sendEvent(MotionEvent.ACTION_UP, X, Y, false);

		handler.postDelayed(new Runnable() {

			public void run() {

				String selection = null;
				if (!clipbopard) {
					selection = (String) ((ClipboardManager) mP.getSystemService(Context.CLIPBOARD_SERVICE)).getText();
				} else {
					selection = fakeClipboard;
				}
				//mWebView.focusNodeAt(_x_, _y_);
				//mWebView.pasteText(selection);
				
				String toPaste = null;
				
				if(isEmptyInput()){
					toPaste = mP.getInputBoxText() + selection;
				} else {
					toPaste = selection;					
				}
				mP.setInputBoxText(toPaste);			
			}

		}, 500);
		// mWebView.focusNodeAt(0,0);

	};

	public boolean isPasteTextIntoInputText() {
		return textIntoInputText;
	}

	public void setPasteTextIntoInputText(boolean p) {
		this.textIntoInputText = p;
	}

	protected boolean mMovableSelection = false;
	protected boolean mSelectionActive = false;
	protected boolean mSelectionMoved = false;

	protected void startSelection(boolean movable) {
		mMovableSelection = movable;
		mSelectionActive = true;
		if (movable) {
			mSelectionStarted = false;
			selX = fcX;
			selY = fcY;
		}
	}

	protected void stopSelection() {
		if (!mSelectionActive)
			return;

		mSelectionActive = false;
		mMovableSelection = false;
		mSelectionStarted = false;

		enableGestures();

		//mWebView.executeSelectionCommand(fcX, fcY, WebView.STOP_SELECTION);
		//mWebView.executeSelectionCommand(fcX, fcY, WebView.COPY_TO_CLIPBOARD);
	}

	protected void stopSelectionMovable() {
		if (!mSelectionActive)
			return;

		mMovableSelection = false;
		mSelectionStarted = false;
	}

	protected void moveSelection() {
		if (!mMovableSelection)
			return;

		if (!mSelectionStarted) {
			final int yDiff = (int) Math.abs(fcY - selY);
			final int xDiff = (int) Math.abs(fcX - selX);

			if (yDiff > mTouchSlop || xDiff > mTouchSlop) {
				// eventViewer.setText("Please select now more text with the FC ...");
				mSelectionStarted = true;
				stopHitTest(fcX, fcY, true);
				applyImageResource(R.drawable.text_cursor, "text_cursor");
				// pointer.setImageResource(R.drawable.text_cursor);
			}
		} 
			//else
			//mWebView.executeSelectionCommand(fcX, fcY, WebView.EXTEND_SELECTION);
	}

	protected void cancelSelection() {
		if (!mSelectionActive)
			return;

		mSelectionActive = false;
		mSelectionStarted = false;

		//mWebView.executeSelectionCommand(fcX, fcY, WebView.CLEAR_SELECTION);
	}

	private boolean mTouchPointValid = false;

	private static final int INVALID_POINTER_ID = -1;

	private int mActivePointerId = INVALID_POINTER_ID;

	public boolean dispatchKeyEventFC(KeyEvent event) {

		// Toast.makeText(mContext, "KeyEvent: " + event.getAction(),
		// Toast.LENGTH_LONG).show();

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			stopHitTest(fcX, fcY, false);
			// startSelection(fcX, fcY);
		} else if (event.getAction() == KeyEvent.ACTION_UP) {
			// checkClickSelection(fcX, fcY);
		}

		return true;
	}

	// FC x,y coordinates
	public int fcX = 0;
	public int fcY = 0;

	// GET MASTER FINGER POSITION FOR WHEN FINGER GET OUT OF BOUNDS
	int mX = (int) SwifteeApplication.getMasterX();
	int mY = (int) SwifteeApplication.getMasterY();

	// Single touch x,y coordinates
	public int stX = 0;
	public int stY = 0;

	// Text Poiner coordinates
	public int tpX = 0;
	public int tpY = 0;
	
	// Initian anchor x,y coordinates before tab hit. 
	public int anX = 0;
	public int anY = 0;
	public boolean anchorTab;

	// Text Poiner coordinates
	public int _xRemote = 0;	
	public int _yRemote = 0;
	
	public int get_xRemote() {
		return _xRemote;
	}

	public void set_xRemote(int _xRemote) {
		this._xRemote = _xRemote;
	}
	
	public int get_yRemote() {
		return _yRemote;
	}

	public void set_yRemote(int _yRemote) {
		this._yRemote = _yRemote;
	}

	public int getStX() {
		return stX;
	}

	public void setStX(int stX) {
		this.stX = stX;
	}

	public int getStY() {
		return stY;
	}

	public void setStY(int stY) {
		this.stY = stY;
	}
	
	public int getAnX() {
		return anX;
	}

	public void setAnX(int anX) {
		this.anX = anX;
	}

	public int getAnY() {
		return anY;
	}

	public void setAnY(int anY) {
		this.anY = anY;
	}
	
	public boolean isAnchorTab() {
		return anchorTab;
	}

	int mPrevMoveX = 0, mPrevMoveY = 0;
	boolean mMoveFrozen = false;

	boolean mForwardTouch = false;
	boolean mMenuDown = false;
	int mOldTouchCount = 0;

	private Runnable longTouchRunnable;
	private boolean mLongTouchCheck;
	private long lastTouchTime = -1;

	public boolean mLongTouchHack = false;
	private WebHitTestResult mLongTouchHackObj = null;

	// Boundaries to cursor variables.
	private int xFcTop = 0;
	private int yFcTop = 0;

	private int yFlag;
	private int xFlag;

	private int pointerScrollX;
	private int pointerScrollY;

	public boolean dispatchTouchEventFC(MotionEvent event) {

		int action = event.getAction() & MotionEvent.ACTION_MASK;

		// if (action == MotionEvent.ACTION_DOWN)
		// runnableKiteAni.stop();

		boolean status;

		int X, Y;
		int touchCount = event.getPointerCount(); // touchCount == 2 means
	// ACTION!

		if (mActivePointerId != INVALID_POINTER_ID && touchCount >= 2) {
			final int pointerIndex = event.findPointerIndex(mActivePointerId);

			X = (int) event.getX(pointerIndex);
			Y = (int) event.getY(pointerIndex);
		} else {
			X = (int) event.getX();
			Y = (int) event.getY();
		}

		// Set X,Y for JavaScript snippet.
		if (cType == WebHitTestResult.TYPE_TEXT_TYPE) {
			mWebView.loadUrl("javascript:getTextSize(" + event.getX() + ","
					+ event.getY() + ")");
		}

		if (mIsDisabled)
			return false;

		fcX = -(int) pointer.getScrollX() + -(int) getScrollX() + w / 2;
		fcY = -(int) pointer.getScrollY() + -(int) getScrollY() + h / 2;

		/**
		 * Set side tops boundaries to cursor.
		 */
		// START TOPS.

		int _pWidth = getWidth();
		int _pHeight = getHeight();

		int gapY = (int) (_pHeight * 0.08);
		int gapX = (int) (_pHeight * 0.11);

		int fcX_hor = 0;
		int fcY_hor = 0;
		int fcX_ver = 0;
		int fcY_ver;

		// Master finger touch
		updateMasterXY();

		// TOP-BOTTOM SIDES
		if (fcY < 40 || fcY > _pHeight) {

			if (fcY < 40) {
				fcY_hor = (_pHeight - _pWidth) + gapY - pointerScrollY;
				fcY = 0;
			} else if (fcY > _pHeight) {
				fcY_hor = (_pWidth - _pHeight) - gapY - pointerScrollY;
				fcY = _pHeight;
			}

			// Draw new cursor on pHold.
			if (yFlag == 0) {
				if (pointer != null) {
					removeView(pointer);
				}
				if (xFlag == 0) {
					pHold.addView(pointer);
				}
				yFlag = 1;
			}

			// Left-Right Tops
			if (fcX < 0) {
				fcX_ver = (_pHeight - _pWidth) - gapX - pointerScrollX;
			} else if (fcX > _pWidth) {
				fcX_ver = (_pWidth - _pHeight) + gapX - pointerScrollX;
			} else {
				fcX_hor = fcX - (_pWidth / 2) + pointerScrollX;
				fcX_hor = fcX_hor * -1;
			}

			pHold.scrollTo(fcX_hor, fcY_hor);

		} else if (fcY > 40 && yFlag == 1) {

			relocatePointerToFCView();

		}

		// LEFT-RIGHT SIDES
		if (fcX < 0 || fcX > _pWidth) {

			if (fcX <= 0) {
				fcX = 0;
				fcX_ver = (_pHeight - _pWidth) - gapX - pointerScrollX;
			} else if (fcX >= _pWidth) {
				fcX_ver = (_pWidth - _pHeight) + gapX - pointerScrollX;
				fcX = _pWidth;
			}

			// Draw new cursor on pHold.
			if (xFlag == 0) {
				if (pointer != null) {
					removeView(pointer);
				}
				if (yFlag == 0) {
					pHold.addView(pointer);
				}
				xFlag = 1;
			}

			// Top-bottom tops
			if (fcY <= 0) {
				fcY_ver = (_pHeight - _pWidth) + gapY - pointerScrollY;
			} else if (fcY >= _pHeight) {
				fcY_ver = (_pWidth - _pHeight) - gapY - pointerScrollY;
			} else {
				fcY_ver = fcY - (_pHeight / 2) + pointerScrollY;
				fcY_ver = fcY_ver * -1;
			}

			pHold.scrollTo(fcX_ver, fcY_ver);

		} else if (fcX > 0 && xFlag == 1) {

			relocatePointerToFCView();

		}
		// END OF TOPS

		// TOP-BOTTOM SIDES
		/*
		 * if ((fcY < 40 || fcY > _pHeight) || (mY < 40 || mY > _pHeight - 40
		 * )){
		 * 
		 * // Draw new cursor on pHold. if (yFlag == 0) { if (pointer != null) {
		 * removeView(pointer); } if (xFlag == 0) { pHold.addView(pointer); } }
		 * 
		 * if ((fcY <= 40) || (mY <= 40)) { fcY_hor = (_pHeight - _pWidth) +
		 * gapY - pointerScrollY; fcY = 0; mY = 0; } else if ((fcY >= _pHeight)
		 * || (mY >= _pHeight - 40)) { fcY_hor = (_pWidth - _pHeight) - gapY -
		 * pointerScrollY; fcY = _pHeight; mY = _pHeight - 40; }
		 * 
		 * // Left-Right Tops if (fcX < 0) { fcX_ver = (_pHeight - _pWidth) -
		 * gapX - pointerScrollX; } else if (fcX > _pWidth) { fcX_ver = (_pWidthf
		 * - _pHeight) + gapX - pointerScrollX; } else { fcX_ver = fcX -
		 * (_pWidth / 2) + pointerScrollX; fcX_ver = fcX_hor * -1; }
		 * 
		 * pHold.scrollTo(fcX_ver, fcY_hor); moveHitTest(fcX_ver, fcY_hor);
		 * 
		 * } else if (fcY > 40 && yFlag == 1) {
		 * relocatePointerToFCView(SwifteeApplication.RELOCATE_FROM_POINTER); }
		 * else if (mY > 40 && yFlag == 1) {
		 * relocatePointerToFCView(SwifteeApplication
		 * .RELOCATE_FROM_FINGER_Y_POSITION); }
		 * 
		 * // LEFT-RIGHT SIDES if ((fcX < 0 || fcX > _pWidth) || (mX < 40 || mX
		 * > _pWidth - 40 )) {
		 * 
		 * // Draw new cursor on pHold. if (xFlag == 0) { if (pointer != null) {
		 * removeView(pointer); } if (yFlag == 0) { pHold.addView(pointer); } }
		 * 
		 * if ((fcX <= 0) || (mX <= 40)) {
		 * 
		 * fcX_ver = (_pHeight - _pWidth) - gapX - pointerScrollX;
		 * 
		 * if ((fcX <= 0)||(mX > fcX)) { fcX = 0; xFlag = 1; } else if ((mX <=
		 * 40)||(mX < fcX)) { mX = 0; xFlag = 2; }
		 * 
		 * } else if ((fcX >= _pWidth) || (mX >= _pWidth - 40) ) {
		 * 
		 * fcX_ver = (_pWidth - _pHeight) + gapX - pointerScrollX;
		 * 
		 * if ((fcX >= _pWidth) || (mX < fcX)) { fcX = _pWidth; xFlag = 1; }
		 * else if ((mX >= _pWidth-40) || (mX > fcX)) { mX = _pWidth -40; xFlag
		 * = 2; } }
		 * 
		 * // Top-bottom tops if (fcY <= 0) { fcY_ver = (_pHeight - _pWidth) +
		 * gapY - pointerScrollY; } else if (fcY >= _pHeight) { fcY_ver =
		 * (_pWidth - _pHeight) - gapY - pointerScrollY; } else { fcY_ver = fcY
		 * - (_pHeight / 2) + pointerScrollY; fcY_ver = fcY_ver * -1; }
		 * 
		 * pHold.scrollTo(fcX_ver, fcY_ver); startHitTest(fcX_ver, fcY_ver);
		 * 
		 * //moveHitTest(fcX_ver, fcY_ver); Log.v("mierda", " fcX_ver : " +
		 * fcX_ver + " fcY_ver: " + fcY_ver); Log.v("mierda", " mX : " + mX +
		 * " mY: " + mY);
		 * 
		 * 
		 * } else if (fcX > 0 && xFlag == 1 ){
		 * relocatePointerToFCView(SwifteeApplication.RELOCATE_FROM_POINTER); }
		 * else if (mX > 0 && xFlag == 2) {
		 * relocatePointerToFCView(SwifteeApplication
		 * .RELOCATE_FROM_FINGER_X_POSITION); }
		 * 
		 * // END OF CURSOR TOPS
		 */

		/**
		 * TEXT SELECTRION TOPS
		 **/

		/*
		 * if (mSelectionActive){
		 * 
		 * int _tWidth = rect.width(); int _tHeight = rect.height();
		 * 
		 * int tGapY = (int) (_tHeight * 0.08); int tGapX = (int) (_tHeight *
		 * 0.11);
		 * 
		 * int fcX_tHor = 0; int fcY_tHor = 0; int fcX_tVer = 0; int fcY_tVer;
		 * 
		 * 
		 * //TOPS TEXT SELECTION if (fcY < rect.top || fcY > rect.bottom ) {
		 * 
		 * if (fcY < rect.top) { fcY_tHor = (_tHeight - _tWidth) + tGapY -
		 * pointerScrollY; fcY = 0; } else if (fcY > _tHeight) { fcY_tHor =
		 * (_tWidth - _tHeight) - tGapY - pointerScrollY; fcY = _tHeight; }
		 * 
		 * // Draw new cursor on pHold. if (yFlag == 0) { if (pointer != null) {
		 * removeView(pointer); } if (xFlag == 0) { pHold.addView(pointer); }
		 * yFlag = 1; }
		 * 
		 * // Text selection Left-Right Tops if (fcX < rect.left) { fcX_tVer =
		 * (_tHeight - _tWidth) - gapX - pointerScrollX; } else if (fcX >
		 * _pWidth) { fcX_tVer = (_tWidth - _tHeight) + gapX - pointerScrollX; }
		 * else { fcX_tHor = fcX - (_tWidth / 2) + pointerScrollX; fcX_tHor =
		 * fcX_tHor * -1; }
		 * 
		 * pHold.scrollTo(fcX_tHor, fcY_tHor);
		 * 
		 * } else if (fcY > 40 && yFlag == 1) {
		 * 
		 * relocatePointerToFCView();
		 * 
		 * }
		 * 
		 * // Text selection LEFT-RIGHT SIDES if (fcX < rect.left || fcX >
		 * _pWidth) {
		 * 
		 * if (fcX <= rect.left ) { fcX = 0; fcX_tVer = (_tHeight - _pWidth) -
		 * tGapX - pointerScrollX; } else if (fcX >= _tWidth) { fcX_tVer =
		 * (_pWidth - _tHeight) + tGapX - pointerScrollX; fcX = _tWidth; }
		 * 
		 * // Draw new cursor on pHold. if (xFlag == 0) { if (pointer != null) {
		 * removeView(pointer); } if (yFlag == 0) { pHold.addView(pointer); }
		 * xFlag = 1; }
		 * 
		 * // Text selection Top-bottom tops if (fcY <= rect.left) { fcY_tVer =
		 * (_tHeight - _tWidth) + tGapY - pointerScrollY; } else if (fcY >=
		 * _tHeight) { fcY_tVer = (_tWidth - _tHeight) - tGapY - pointerScrollY;
		 * } else { fcY_tVer = fcY - (_tHeight / 2) + pointerScrollY; fcY_tVer =
		 * fcY_tVer * -1; }
		 * 
		 * pHold.scrollTo(fcX_ver, fcY_tVer);
		 * 
		 * } else if (fcX > rect.left && xFlag == 1) {
		 * 
		 * relocatePointerToFCView();
		 * 
		 * }
		 * 
		 * //END OF TOPS TEXT SELECTION }
		 */

		if (mForwardTouch) {
			if (action == MotionEvent.ACTION_UP
					|| action == MotionEvent.ACTION_CANCEL) {
				mForwardTouch = false;
				if (!(currentMenu == fcWindowTabs)) {
					fcView.setVisibility(View.VISIBLE);
				}
			}

			mWebView.dispatchTouchEvent(event);

			return true;
		}

		// Log.d("dispatchTouchEventFC", "X,Y,action" + X + "," + Y + "," +
		// action);

		if (mMoveFrozen) {
			// We continue the movement from MT
			if (action == MotionEvent.ACTION_MOVE
					|| action == MotionEvent.ACTION_UP) {
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

				pt.set(2, xs, ys, pressure, ptIdxs, action,
action != MotionEvent.ACTION_UP, event.getEventTime());
				mSelectionGestures.dispatchTouchEventMT(pt, action);
			}

			if (action == MotionEvent.ACTION_UP)
				mMoveFrozen = false;

			if (action == MotionEvent.ACTION_MOVE)
				return true;
		}

		if (action == MotionEvent.ACTION_MOVE && mOldTouchCount == 2
				&& touchCount == 1) {
			int dMX = Math.abs(X - mPrevMoveX);
			int dMY = Math.abs(Y - mPrevMoveY);

			if ((dMX >= MAX_JUMP || dMY >= MAX_JUMP)) // && mGesturesEnabled)
			{
				action = MotionEvent.ACTION_UP;
				mMoveFrozen = true;
				this.setVisibility(View.INVISIBLE);
				mHandleTouch = false; // Don't let user drag at this stage
				return true;
			}
		}

		if (action == MotionEvent.ACTION_MOVE) {
			mOldTouchCount = touchCount;

			mPrevMoveX = X;
			mPrevMoveY = Y;
		}

		// MT stuff
		if (!mMenuDown) {
			status = multiTouchController.onTouchEvent(event);

			if (status)
				return true; // Got handled by MT Controller
		}

		// MT stuff end

		if (action == MotionEvent.ACTION_DOWN) {

			timerStarted = false;
			parkTimerStarted = false;

			handler.removeCallbacks(runnable);
			handler.removeCallbacks(parkingRunnable);

			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
				updateFC();
			}

			mPrevX = X;
			mPrevY = Y;
			mPrevMoveX = X;
			mPrevMoveY = Y;
			mMoveFrozen = false;

			mActivePointerId = event.getPointerId(0);

			// Toast.makeText(mContext, "mActivePointerId: " + mActivePointerId,
			// 100).show();

			final int CircleX = -(int) getScrollX() + w / 2;
			final int CircleY = -(int) getScrollY() + h / 2;

			int r = fcView.getRadius();

			// Check for inner circle click and show Circular menu
			final int innerCirRad = (int) (fcPointerView.getRadius() * 0.6f);

			// We need to factor the inner circle relocation so
			// it does not get out of the outer circle
			// Fixed Size: 60 % from our radius
			float radFact = 0.6f * r; // (float)(2*innerCirRad) / (float)r;

			// Toast.makeText(mContext, "radFact: " + radFact, 100).show();

			final int innerCircleX = -(int) fcPointerView.getScrollX()
					+ CircleX;
			final int innerCircleY = -(int) fcPointerView.getScrollY()
					+ CircleY;

			int scrollX = X - CircleX;
			int scrollY = Y - CircleY;
			double length = Math.hypot(scrollX, scrollY);

			// Double tap hack
			/*
			 * if (!mSoftKeyboardVisible) { long thisTime =
			 * System.currentTimeMillis(); if (thisTime - lastTouchTime < 250) {
			 * 
			 * // this.getController().zoomInFixing((int) ev.getX(), (int) //
			 * ev.getY()); lastTouchTime = -1; if (isCircularZoomEnabled())
			 * disableCircularZoom(); else {
			 * 
			 * toggleMenuVisibility(); // Should return true here so touch event
			 * will not be // handled again by CircularLayout (in parking mode).
			 * return true; } } else { // Too slow :) lastTouchTime = thisTime;
			 * } }
			 */

			/*
			 * if (X > innerCircleX - innerCirRad && X < innerCircleX +
			 * innerCirRad && Y > innerCircleY - innerCirRad && Y < innerCircleY
			 * + innerCirRad) { // Toast.makeText(mContext, "Circular Menu",
			 * 100).show(); if (isCircularZoomEnabled()) disableCircularZoom();
			 * else { toggleMenuVisibility(); // Should return true here so
			 * touch event will not be // handled again by CircularLayout (in
			 * parking mode). return true; }
			 * 
			 * } else
			 */

			if (isCircularZoomEnabled()) {
				zoomView.onTouchEvent(event);
				return true;
			}
			/*
			 * else if ((X < CircleX-r || X > CircleX+r || Y < CircleY-r || Y >
			 * CircleY+r) && mScroller.isFinished())
			 */
			else if ((length >= (r * 1.1f)) && mScroller.isFinished()) {

				// fcView.setVisibility(View.INVISIBLE); JOSE, allowing FC to
				// stay visible while dragging.
				if (currentMenu.getVisibility() != View.VISIBLE) {
					handler.removeCallbacks(parkingRunnable);
					handler.post(parkingRunnable);
				}

				removeTouchPoint();

				mHandleTouch = false;

				// startHitTest(fcX, fcY); // Also do the HitTest when the
				// webview
				// window is scrolled
				if (!mMenuDown) {
					mForwardTouch = true;
					// Start timer for long touch
					final int fX = X;
					final int fY = Y;

					longTouchRunnable = new Runnable() {

						public void run() {
							mLongTouchCheck = false;
							onLongTouchHack(fX, fY);
						}

					};

					handler.postDelayed(longTouchRunnable, 800);
					mLongTouchCheck = true;

					mWebView.dispatchTouchEvent(event);
					return true;
				}

				return false;
			} else if (currentMenu.getVisibility() == VISIBLE) {
				mHandleTouch = false; // Don't let user drag at this stage
				return false;
			} else {
				fcView.setVisibility(View.VISIBLE);
				// fcTouchView.setVisibility(View.VISIBLE);

				mHandleTouch = true;
				// FC was touched, get out of parking mode
				if (mP.isInParkingMode()) {
					mP.exitParkingMode();
					fcView.setRadius(FC_RADIUS); // Restore radius size
					// Now recalculate some vars
					r = fcView.getRadius();
					radFact = 0.6f * r;
				}

				if (mIsLoading) {
					fcView.startScaleUpAndRotateAnimation(100);
				}

				// Save coordinates
				// mLastTouchX = X;
				// mLastTouchY = Y;
				mTouchPointValid = true;

				if (SwifteeApplication.getSocketStatus() == SwifteeApplication.CLIENT_MOUSE_DOWN
					|| 	SwifteeApplication.getSocketStatus() == SwifteeApplication.CLIENT_MOUSE_MOVE ) {

					fcView.setVisibility(View.INVISIBLE);

				} else {

					fcView.setVisibility(View.VISIBLE);

					scrollX *= (radFact / length);
					scrollY *= (radFact / length);

					pointer.scrollTo(scrollX, scrollY);
					fcPointerView.scrollTo(-scrollX, -scrollY);

					pointerScrollX = scrollX;
					pointerScrollY = scrollY;

				}

				updateFC();
				startHitTest(fcX, fcY); // aca

			}
			/*
			 * else { //Toast.makeText(mContext,
			 * "Selection or menu would start now", Toast.LENGTH_LONG).show();
			 * 
			 * stopHitTest(fcX, fcY, false); startSelection(fcX, fcY);
			 * mWebView.executeSelectionCommand(fcX, fcY,
			 * WebView.SELECT_WORD_OR_LINK); //Toast.makeText(mContext, "XY:" +
			 * X + "," + Y + " - CXY: " + // innerCircleX + "," + innerCircleY +
			 * "R: " + ir, Toast.LENGTH_LONG).show(); } }
			 */
			if (mCanBeDisabled) {
				if (mHandleTouch) {
					mP.stopGesture();
					mP.stopTextGesture();
				} else
					disableFC();
			}
		}

		if (isCircularZoomEnabled()) {
			zoomView.onTouchEvent(event);
			return true;
		}
		if (action == MotionEvent.ACTION_CANCEL) {
			mActivePointerId = INVALID_POINTER_ID;
		}

		/*
		 * if (action == MotionEvent.ACTION_MOVE) { if (mMoveFrozen) return
		 * false;
		 * 
		 * int dMX = Math.abs(X-mPrevMoveX); int dMY = Math.abs(Y-mPrevMoveY);
		 * 
		 * if (dMX >= MAX_JUMP || dMY >= MAX_JUMP) { action =
		 * MotionEvent.ACTION_UP; mMoveFrozen = true; mHandleTouch = false; //
		 * Don't let user drag at this stage }
		 * 
		 * mPrevMoveX = X; mPrevMoveY = Y; }
		 */

		if (action == MotionEvent.ACTION_UP) {
			if (currentMenu.getVisibility() == View.VISIBLE)
				handler.postDelayed(runnable, 10000);
			// else
			// runnableKiteAni.start();

			if (currentMenu == fcWindowTabs) {
				if (mPrevX > X + 100)
					nextWebPage();
				else if (mPrevX + 100 < X) {
					if (fcWindowTabs.getCurrentTab() > 2)
prevWebPage();
				}
				mPrevX = 0;
			}
			mActivePointerId = INVALID_POINTER_ID;

			if ((!mMenuDown) && (!(currentMenu == fcWindowTabs))) {
				fcView.setVisibility(View.VISIBLE);
			}

			stopSelection();
			stopHitTest(fcX, fcY, false);
			// eventViewer.setText("Handling Touch on up ...");

			if (mHandleTouch == true && mWebHitTestResult != null) {
				// eventViewer.setText("Handling Touch on up ...");

				/*
				 * REMOVE TOUCH UP SELECTION
				 * 
				 * if (mWebHitTestResult.getType() ==
				 * WebHitTestResult.TEXT_TYPE) { mP.startTextGesture(); } else {
				 * clickSelection(fcX, fcY); }
				 */
				onTouchUp(false);

				mWebHitTestResult = null;

			}

			removeTouchPoint();

			// checkFCMenuBounds();
			if (currentMenu.getVisibility() != View.VISIBLE)
				handler.post(parkingRunnable);

			if (mIsLoading) {
				fcView.startScaleDownAndRotateAnimation(100);
			}

			// fcTouchView.setVisibility(View.INVISIBLE);

			if (mHandleTouch == false)
				return false;

			mHandleTouch = false;
		} else if (!mHandleTouch) {
			// Even though we did not handle the touch, we still move the
			// HitTest
			moveHitTest(fcX, fcY);
			return false;
		}

		if (action == MotionEvent.ACTION_MOVE) {

			/*
			 * if (touchCount >= 2) { stopHitTest(fcX,fcY,false);
			 * startSelection(fcX, fcY); } else checkClickSelection(fcX, fcY);
			 */

			moveSelection();
			moveHitTest(fcX, fcY);

			// FF: This will not work ...

			int r = fcPointerView.getRadius();

			// TOP.
			if ((fcY - (r)) <= 10) {
				scrollWebView(-FC_SCROLL_SPEED, 1, fcX, fcY);
			} else if ((Y - (r)) <= 10) {
				scrollWebView(-FC_SCROLL_SPEED, 1, fcX, fcY);
			}
			// RIGHT
			if ((fcX + r) > this.w - 10) {
				scrollWebView(FC_SCROLL_SPEED, 0, fcX, fcY);
			} else if (X + r > this.w - 10) {
				scrollWebView(FC_SCROLL_SPEED, 0, fcX, fcY);
			}

			// BOTTOM
			if ((fcY + r) > this.h - 10) {
				scrollWebView(FC_SCROLL_SPEED, 1, fcX, fcY);
			} else if (Y + r > this.h - 10) {
				scrollWebView(FC_SCROLL_SPEED, 1, fcX, fcY);
			}

			// LEFT
			if ((fcX - r) <= 10) {
				scrollWebView(-FC_SCROLL_SPEED, 0, fcX, fcY);
			} else if ((X - r) <= 10) {
				scrollWebView(-FC_SCROLL_SPEED, 0, fcX, fcY);
			}
		}
		/*
		 * 
		 * if(action == MotionEvent.ACTION_POINTER_DOWN){
		 * 
		 * stopHitTest(fcX,fcY,false); startSelection(fcX, fcY); } if(action ==
		 * MotionEvent.ACTION_POINTER_UP){ checkClickSelection(fcX, fcY); }
		 */
		/* FC: Drag + Fling Support */

		/* If there is a second finger press, ignore */
		// if (touchCount == 2)
		// return false;

		/*
		 * if (touchCount == 2) { Toast.makeText(mContext, "0: " + event.getX(0)
		 * + "," + event.getY(0) + "- 1: " + event.getX(1) + "," +
		 * event.getY(1), Toast.LENGTH_SHORT).show(); }
		 */

		status = onInterceptTouchEventFC(event);

		if (status == false)
			return true;

		status = onTouchEventFC(event);

		if (status == false)
			return false;

		// We handled it
		return true;

	}

	public boolean isRemote() {
		return remote;
	}

	public void setRemote(boolean remote) {
		this.remote = remote;	
		if(isLandingPage){
			loadPage("javascript:hideFooter()");
		}
	}

	public void relocatePointerToFCView() {
		if (pointer != null) {
			pHold.removeView(pointer);
		}
		pointer = new ImageView(getContext());
		pointer.setImageResource(R.drawable.kite_cursor);
		pointer.setScaleType(ImageView.ScaleType.CENTER);
		yFlag = 0;
		xFlag = 0;
		addView(pointer);
		pointer.scrollTo(pointerScrollX, pointerScrollY);
		fcPointerView.scrollTo(-pointerScrollX, -pointerScrollY);
	}

	/*
	 * public void relocatePointerToFCView(int relocate) {
	 * 
	 * if (pointer != null) { pHold.removeView(pointer); }
	 * 
	 * pointer = new ImageView(getContext());
	 * applyImageResource(R.drawable.kite_cursor, "kite_cursor");
	 * //pointer.setImageResource(R.drawable.kite_cursor);
	 * pointer.setScaleType(ImageView.ScaleType.CENTER); yFlag = 0; xFlag = 0;
	 * addView(pointer);
	 * 
	 * switch(relocate){
	 * 
	 * case SwifteeApplication.RELOCATE_FROM_POINTER:
	 * pointer.scrollTo(pointerScrollX, pointerScrollY);
	 * fcPointerView.scrollTo(-pointerScrollX, -pointerScrollY); break;
	 * 
	 * case SwifteeApplication.RELOCATE_FROM_FINGER_X_POSITION:
	 * //updateMasterXY(); int X = (int) SwifteeApplication.getMasterX();
	 * pointer.scrollTo(X, pointerScrollY); fcPointerView.scrollTo(-X,
	 * -pointerScrollY);
	 * 
	 * Log.v("mierda", " X : " + X + " pointerScrollY: " + pointerScrollY);
	 * Log.v("mierda", "-----------------------------------"); break;
	 * 
	 * case SwifteeApplication.RELOCATE_FROM_FINGER_Y_POSITION:
	 * //updateMasterXY(); int Y = (int) SwifteeApplication.getMasterY();
	 * pointer.scrollTo(pointerScrollX, Y);
	 * fcPointerView.scrollTo(-pointerScrollX, -Y); break;
	 * 
	 * }
	 * 
	 * }
	 */

	/*
	 * private void removeAddPointer() { if (pointer!=null){
	 * removeView(pointer); } pHold.addView(pointer); }
	 */

	public boolean canGoForward() {
		return mWebView.canGoForward();
	}

	public boolean canGoBackward() {
		return mWebView.canGoBack();
	}

	public void goBackward() {
		if (mWebView.canGoBack())
			mWebView.goBack();
	}

	public void stopLoading() {
		mWebView.stopLoading();
	}

	public void goForward() {
		if (mWebView.canGoForward())
			mWebView.goForward();
	}

	/*
	 * Circular zoom functions
	 */

	boolean mZoomOpenMenu = true;

	public void enableCircularZoom(boolean openMenuAfterFinish) {
		zoomView.setVisibility(VISIBLE);
		zoomView.setClickable(true);
		/*
		 * long downTime = SystemClock.uptimeMillis(); long eventTime =
		 * SystemClock.uptimeMillis();
		 * 
		 * MotionEvent event = MotionEvent.obtain(downTime,
		 * eventTime,MotionEvent.ACTION_UP, 10, 10, 0);
		 * fcMainMenu.onTouch(fcMainMenu.getChildAt(3), event);
		 */
		zoomView.setAngle((float) fcMainMenu.getZoomAngle());
		eventViewer
				.setText("Circular Zooming enabled. Click back to disable it");
		currentMenu.setVisibility(INVISIBLE);
		fcView.setVisibility(View.VISIBLE);
		mZoomOpenMenu = openMenuAfterFinish;
	}

	public void disableCircularZoom() {
		currentMenu.setVisibility(VISIBLE);
		fcView.setVisibility(View.INVISIBLE);
		zoomView.setVisibility(INVISIBLE);
		eventViewer.setText("Circular Zooming disabled");
		if (!mZoomOpenMenu) {
			hideMenuFast();
		}
	}

	public boolean isCircularZoomEnabled() {
		if (zoomView.getVisibility() == VISIBLE)
			return true;
		return false;
	}

	public void circularZoomIn() {
		//mWebView.zoom(fcX, fcY, 1.25f);
	}

	public void circularZoomOut() {
		//mWebView.zoom(fcX, fcY, 0.8f);
	}

	public void circularZoom(float zoomVal) {
		//mWebView.zoom(fcX, fcY, zoomVal);
	}

	public void setEventText(String str) {
		eventViewer.setText(str);
	}

	public void showVideo(String videoId, boolean showAlert) {
		if (showAlert) {
			eventViewer
					.setText("Download not yet implemented. Showing video instead ...");
		}
		String url = "vnd.youtube:"
				+ videoId
				+ "?vndapp=youtube_mobile&vndclient=mv-google&vndel=watch&vndxl=xl_blazer";
		Intent intent;

		try {
			intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
		} catch (URISyntaxException ex) {
			return;
		}

		if (mP.getPackageManager().resolveActivity(intent, 0) == null)
			return;

		// Security settings - Sanitize access
		intent.addCategory(Intent.CATEGORY_BROWSABLE);
		intent.setComponent(null);

		try {
			mP.startActivity(intent);
		} catch (ActivityNotFoundException e) {
		}
	}

	/**
	 * Open Map when url starts with geo:'
	 * 
	 * @param url
	 */
	public void loadPage(String url) {
		mWebView.loadUrl(url);
	}

	public void loadData(String data) {
		mWebView.loadDataWithBaseURL("file:///android_asset/images/", data,
				"text/html", "utf-8", null);
	}

	public void nextWebPage() {
		mP.setActiveWebViewIndex(mP.getActiveWebViewIndex() - 1);
		// fcWindowTabs.setCurrentTab(fcWindowTabs.getCurrentTab()+1);
		TabButton child = (TabButton) fcWindowTabs.findViewById(mP
				.getActiveWebViewIndex());
		fcWindowTabs.setCurrentTab(child.getTabIndex());
		// fcWindowTabs.setActiveTabIndex(child);

	}

	public void prevWebPage() {
		mP.setActiveWebViewIndex(mP.getActiveWebViewIndex() + 1);
		// fcWindowTabs.setCurrentTab(fcWindowTabs.getCurrentTab()-1);
		TabButton child = (TabButton) fcWindowTabs.findViewById(mP
				.getActiveWebViewIndex());
		fcWindowTabs.setCurrentTab(child.getTabIndex());
		fcWindowTabs.setActiveTabIndex(child);
	}

	boolean byPassTop;

	/*
	 * WebView scrolling with FloatingCursor
	 */
	public void scrollWebView(int speed, int direction, int _fcX, int _fcY) {

		if (mP.isSuggestionActivated()) {
			return;
		}
		/*
		 * direction = 0 if web view scrolls along X axis direction = 1 if web
		 * view scrolls along Y axis
		 */

		int sx = mWebView.getScrollX();
		int sy = mWebView.getScrollY();

		final int xLoc = this.getScrollX();
		final int yLoc = this.getScrollY();

		int dims[] = mP.getDeviceWidthHeight();
		int coords[] = mP.getFCLocation(xLoc, yLoc, dims[0], dims[1]);

		if (direction == 0) { // X AXIS

			/**
			 * Check bug here while scrolling to the bottom.
			 */

			int WebViewWidth = mWebView.getWidth();
			int FloatingCursorWidth = this.getWidth();

			/**
			 * If the WebView with is smaller than the FC width return, else
			 * scroll.
			 */
			if (WebViewWidth <= FloatingCursorWidth) {
				return;
			}

			sx += speed;

			if (sx >= WebViewWidth) {
				sx = WebViewWidth;
			}

			if (sx < 0) {
				sx = 0;
			}
			// Apply move to scroll according to speed distance.
			mWebView.scrollTo(sx, sy);

			// Show scroll icon optional?.
			// showScrollCursor(coords[0], direction, coords[1], coords[2]);

		} else { // Y AXIS

			int WebViewHeight = mWebView.getContentHeight();
			int FloatingCursorHeight = this.getHeight();

			int fcViewHeight = fcView.getRadius();
			int fcPointerViewHeight = fcPointerView.getRadius() * 2;

			/**
			 * BUG: This condition does not reach a small space at the bottom of
			 * the page, I am adding the FC radius to reach.
			 */
			int totalHeight = FloatingCursorHeight
					- (fcViewHeight + fcPointerViewHeight);

			if (WebViewHeight <= totalHeight) {
				return;
			}

			sy += speed;

			if (sy >= WebViewHeight) {
				sy = WebViewHeight;
			}

			if (sy < 0) {
				sy = 0;
			}

			mWebView.scrollTo(sx, sy);
			// showScrollCursor(coords[0], direction, coords[1], coords[2]);
		}
		// TODO, the scrolling cursors are not applied per page moving but per
		// scrollTo.
		// It will be better to show them on page movement listener rather than
		// page to be moved.
	}

	public class WebClient extends WebChromeClient implements
			OnCompletionListener, OnErrorListener {

		private VideoView mCustomVideoView;
		private FrameLayout mCustomViewContainer;
		private WebChromeClient.CustomViewCallback mCustomViewCallback;
		private FrameLayout mContentView;

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			super.onShowCustomView(view, callback);
			if (view instanceof FrameLayout) {
				mCustomViewContainer = (FrameLayout) view;
				mCustomViewCallback = callback;
				mContentView = (FrameLayout) view;
				if (mCustomViewContainer.getFocusedChild() instanceof VideoView) {
					mCustomVideoView = (VideoView) mCustomViewContainer
	.getFocusedChild();
					mContentView.setVisibility(View.GONE);
					mCustomViewContainer.setVisibility(View.VISIBLE);
					mP.setContentView(mCustomViewContainer);
					mCustomVideoView.setOnCompletionListener(this);
					mCustomVideoView.setOnErrorListener(this);
					mCustomVideoView.start();
				}
			}
		}

		/*
		 * public void onCompletion(MediaPlayer mp) {
		 * Log.d(TAG,"Video completo"); mP.setContentView(R.layout.main);
		 * WebView wb = (WebView) mP.findViewById(R.id.webView);
		 * mP.refreshWebView(); }
		 */

		public void onHideCustomView() {
			if (mCustomVideoView == null)
				return;
			// Hide the custom view.
			mCustomVideoView.setVisibility(View.GONE);
			// Remove the custom view from its container.
			mCustomViewContainer.removeView(mCustomVideoView);
			mCustomVideoView = null;
			mCustomViewContainer.setVisibility(View.GONE);
			mCustomViewCallback.onCustomViewHidden();
			// Show the content view.
			mContentView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onCompletion(MediaPlayer mp) {
			mp.stop();
			mCustomViewContainer.setVisibility(View.GONE);
			onHideCustomView();
			mP.setContentView(mContentView);
		}

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			mP.setContentView(R.layout.main);
			return true;
		}

		// Class used to use a dropdown for a <select> element
		private class InvokeListBox implements Runnable {
			// Whether the listbox allows multiple selection.
			private boolean mMultiple;
			// Passed in to a list with multiple selection to tell
			// which items are selected.
			private int[] mSelectedArray;
			// Passed in to a list with single selection to tell
			// where the initial selection is.
			private int mSelection;

			private Container[] mContainers;

			// Need these to provide stable ids to my ArrayAdapter,
			// which normally does not have stable ids. (Bug 1250098)
			private class Container extends Object {
				String mString;
				boolean mEnabled;
				int mId;

				public String toString() {
					return mString;
				}
			}

			/**
			 * Subclass ArrayAdapter so we can disable OptionGroupLabels, and
			 * allow filtering.
			 */
			private class MyArrayListAdapter extends ArrayAdapter<Container> {
				public MyArrayListAdapter(Context context, Container[] objects,
boolean multiple) {
					super(context,
	multiple ? R.layout.select_dialog_multichoice
			: R.layout.select_dialog_singlechoice,
	objects);
				}

				@Override
				public boolean hasStableIds() {
					// AdapterView's onChanged method uses this to determine
					// whether
					// to restore the old state. Return false so that the old
					// (out
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

			private InvokeListBox(String[] array, boolean[] enabled,
					int[] selected) {
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

			private InvokeListBox(String[] array, boolean[] enabled,
					int selection) {
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
			 * Whenever the data set changes due to filtering, this class
			 * ensures that the checked item remains checked.
			 */
			private class SingleDataSetObserver extends DataSetObserver {
				private long mCheckedId;
				private ListView mListView;
				private Adapter mAdapter;

				/*
				 * Create a new observer.
				 * 
				 * @param id The ID of the item to keep checked.
				 * 
				 * @param l ListView for getting and clearing the checked states
				 * 
				 * @param a Adapter for getting the IDs
				 */
				public SingleDataSetObserver(long id, ListView l, Adapter a) {
					mCheckedId = id;
					mListView = l;
					mAdapter = a;
				}

				public void onChanged() {
					// The filter may have changed which item is checked. Find
					// the
					// item that the ListView thinks is checked.
					int position = mListView.getCheckedItemPosition();
					long id = mAdapter.getItemId(position);
					if (mCheckedId != id) {
				// Clear the ListView's idea of the checked item, since
				// it is incorrect
				mListView.clearChoices();
				// Search for mCheckedId. If it is in the filtered list,
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

				public void onInvalidate() {
				}
			}

			public void run() {

				Looper.prepare();

				final ListView listView = (ListView) LayoutInflater.from(
							mContext).inflate(R.layout.select_dialog, null);
				final MyArrayListAdapter adapter = new MyArrayListAdapter(
							mContext, mContainers, mMultiple);
				AlertDialog.Builder b = new AlertDialog.Builder(mContext)
							.setView(listView).setCancelable(true)
							.setInverseBackgroundForced(true);

				if (mMultiple) {
					b.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						
				public void onClick(DialogInterface dialog,	int which) {
					//mWebView.setListBoxChoices(listView.getCheckedItemPositions(),adapter.getCount(), false);
			/*
			 * mWebViewCore.sendMessage(
			 * EventHub.LISTBOX_CHOICES,
			 * adapter.getCount(), 0,
			 * listView.getCheckedItemPositions());
			 */
				}
			});
							b.setNegativeButton(android.R.string.cancel,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int which) {
					
					//mWebView.setListBoxChoice(-2, true);
					
					/*
					 * mWebViewCore.sendMessage(
					 * EventHub.SINGLE_LISTBOX_CHOICE, -2, 0);
					 */
				}
			});
				}
				final AlertDialog dialog = b.create();
				listView.setAdapter(adapter);
				listView.setFocusableInTouchMode(true);
				// There is a bug (1250103) where the checks in a ListView with
				// multiple items selected are associated with the positions,
				// not
				// the ids, so the items do not properly retain their checks
				// when
				// filtered. Do not allow filtering on multiple lists until
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
					
					//mWebView.setListBoxChoice((int) id, false);
					
					/*
					 * mWebViewCore.sendMessage(
					 * EventHub.SINGLE_LISTBOX_CHOICE, (int)id, 0);
					 */
					dialog.dismiss();
				}
					});
					if (mSelection != -1) {
						listView.setSelection(mSelection);
						listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
						listView.setItemChecked(mSelection, true);
						DataSetObserver observer = new SingleDataSetObserver(
								adapter.getItemId(mSelection), listView,
								adapter);
						adapter.registerDataSetObserver(observer);
					}
				}
				dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
					//mWebView.setListBoxChoice(-2, true);
				/*
				 * mWebViewCore.sendMessage(
				 * EventHub.SINGLE_LISTBOX_CHOICE, -2, 0);
				 */
					}
				});
				dialog.show();

				Looper.loop();

			}
		}

		public void onProgressChanged(WebView view, int newProgress) {

			/*
			 * if (newProgress==0){ fcProgressBar.enable(); }
			 */

			fcView.setProgress(newProgress);
			fcProgressBar.setProgress(newProgress);

			if (newProgress == 100) {
				fcProgressBar.disable();
			}

		}

		// @Override
		public void onClipBoardUpdate(String type) {

			if (mGesturesEnabled) {
				// Log.d("in onClickBoardUpdate-------------------------------",
				// type);
				mP.startGesture(true);
				mGesturesEnabled = false;
			}

		}

		@Override
		public void onRequestFocus(WebView view) {
			// Log.d("INSIDE ONREQUEST FOCUS","--------------------------");
		}

		// @Override
		void onListBoxRequest(String[] array, boolean[] enabledArray,
				int[] selectedArray) {
			new Thread(new InvokeListBox(array, enabledArray, selectedArray))
					.start();
		}

		// @Override
		void onListBoxRequest(String[] array, boolean[] enabledArray,
				int selection) {
			new Thread(new InvokeListBox(array, enabledArray, selection))
					.start();
		}
	}

	public String currentSearch;

	public void setCurrentSearch(String currentSearch) {
		this.currentSearch = currentSearch;
	}

	public boolean loading;

	public void setloading(boolean lW, int cW) {
		this.loading = lW;
		this.amountWM = cW;
	}

	public Hashtable loadingWM = new Hashtable();
	public int countLoaded = 0;
	

	public int amountWM = 0;
	private boolean emptyInput = true;

	public void setEmptyInput(boolean emptyInput) {
		this.emptyInput = emptyInput;
	}
	
	public boolean isEmptyInput() {
		return emptyInput;
	}

	public void setcountLoaded(int countLoaded) {
		this.countLoaded = countLoaded;
	}

	public void setLoadingWMHash(int num, TabButton Tab) {
		loadingWM.put(num, Tab);
	}

	private class GestureWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
			if ((event.getKeyCode() == KeyEvent.KEYCODE_BACK)
					&& view.canGoBack()) {
				view.goBack();
				return true;
			}

			return super.shouldOverrideKeyEvent(view, event);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			
			// Check if device is online.
			mP.checkOnline(false);

			if (url.startsWith("geo:")) {

				mP.openMap(url);

			} else if (url.startsWith("mailto:")) {

				mP.sendMail(url);

			} else if (url.startsWith("vnd.youtube:")) {

				mP.playVideo(url);

			} else {

				eraseDraws();
				view.loadUrl(url);
				// disable FC touch here.
				fcProgressBar.enable();
			}

			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap b) {
			
			if (url.startsWith("file:///") && url.contains("box=#")) {

				String entry = currentSearch;

				int activeSearch = SwifteeApplication.getActiveSearchCheck();
				
				if (emptyInput){
					
					String[] empty = { "The input box is empty, ", "Please provide key input and try again." }; 
					drawPadKiteMessage(empty, "OKAY");					
					return;

				} else if (activeSearch == SwifteeApplication.VIDEO_BUTTON) {

					url = SwifteeApplication.getYouTubeSearch() + entry;

				} else if (activeSearch == SwifteeApplication.IMAGE_BUTTON) {

					url = SwifteeApplication.getImageSearch() + entry;

				} else if (activeSearch == SwifteeApplication.WIKI_BUTTON) {

					url = SwifteeApplication.getWikiSearch() + entry;

				} else if (entry.contains(".")) { // TODO: Match domain
	// extension list here.

					if (!entry.contains("http://")) {
							entry = "http://" + entry;
					}
					url = entry;

				} else {

					url = SwifteeApplication.getGoogleSearch() + entry;

				}

				if (!entry.equals("")) {
					mP.insertRecent(entry);
				}

				view.loadUrl(url);
				fcProgressBar.enable();
				currentSearch = null;
			}

			mIsLoading = true;
			if (!mP.isInParkingMode()) {
				fcView.startScaleDownAndRotateAnimation(1000);
			} else {
				fcView.startRotateAnimation();
			}
			fcMainMenu.toggleCloseORRefresh(false);
		}

		private int countHash = 1;

		@Override
		public void onPageFinished(WebView view, String url) {
			
			String hover = "javascript:" +
					
					"var overElem, lines;\n" + 
					
					"function hover(posX, posY, callback){ \n" +
					
					"	if(callback!='" + WebHitTestResult.CALLBACK_MOVEHIT + "'){\n" +	
							   
					"		window.pBridge.outPut('posX, posY: ' + posX + ' ' + posY);\n" +  		
					
					"		overElem = document.elementFromPoint(posX, posY);\n" + 	
					
					"	};\n" +	
						
					"	pBridge.outPut('overElem.nodeName: ' + overElem.nodeName);\n" +  
						
					"	if ( overElem.nodeName=='A' || overElem.nodeName=='IMG' || overElem.nodeName=='P' ) {\n" +						   	
				
					"		var id = overElem.getAttribute('data-identifier');\n" +		
							
					"		pBridge.outPut('id: ' + id);\n" +   
						
					"		if ( id===undefined || id==='' || id===null ){\n" +
					
					"			overElem.setAttribute('data-identifier', randomFromTo(500,5000));\n" +
					
					"		}\n" +							
								
					"		pBridge.setHitTestRestultJavaScript(" +
					
					"			callback," +
					
					"			findPosX(overElem)," +
					
					"			findPosY(overElem)," +
					
					"			overElem.offsetWidth," +
					
					"			overElem.offsetHeight," +
					
					"			overElem.nodeName," +
					
					"			overElem.getAttribute('data-identifier')," +
					
					"			overElem.href," +
					
					"			overElem.src," +
					
					"			overElem.innerHTML," +
					
					"			$(overElem.id).tagName" +			
					
					"		);\n" +						
								
					"	}\n" +
					
					"		else {\n" +
					
					"			pBridge.setCursorToNormal();\n" +
					
					"		}\n" +
					
					"}\n" +
					
					"function getTextByLineNumber(lineNumber){\n" +
						"return lines[lineNumber];\n" +
					"}\n" +
						

					"function randomFromTo(from, to){\n" +
				       "return Math.floor(Math.random() * (to - from + 1) + from);\n" +
				    "}\n" + 
				  
				 	/**ABSOLUTE POSITION OF ELEMENTS**/
					"function findPosX(obj){\n" + 
						"var curleft = 0;\n" + 
						"if(obj.offsetParent)\n" +
							"while(1)\n" + 
							"{\n" +
							  "curleft += obj.offsetLeft;\n" +
							  "if(!obj.offsetParent)\n" +
								"break;\n" +
							  "obj = obj.offsetParent;\n" +
							"}\n" +
						"else if(obj.x)\n"+
							"curleft += obj.x;\n" +
						"return curleft;\n" +
					"}\n" +

					"function findPosY(obj){\n"+
				    "var curtop = 0;\n" +
				    "if(obj.offsetParent)\n" +
				        "while(1){\n" +
				          "curtop += obj.offsetTop;\n" +
				          "if(!obj.offsetParent)\n" +
				            "break;\n" +
				          "obj = obj.offsetParent;\n" +
				        "}\n" +
				    "else if(obj.y)\n" +
				        "curtop += obj.y;\n" +
				    "return curtop;\n" +
				  "}\n" + 
				    
				  	"	function selectText() {\n" +
				  
				  	"	var selection = window.getSelection();\n" + 
				  		
					"	var range = document.createRange();\n" +
					    
					"	range.selectNodeContents(overElem);\n" +
					    
					"	selection.removeAllRanges();\n" + 
					    
					"	selection.addRange(range);\n"+			
					    
				  	"}\n" +   

			
				 	"function setTextHighlightLines(){\n" +	
				 					
					"	var identifier = overElem.getAttribute('data-identifier');\n" + 
						
					"	var originalInner = overElem.innerHTML;\n" + 
						
					"	var array = overElem.innerHTML.split(\" \");\n" + 
						
					"	var length = array.length;\n" + 
						
					"	var newInner = \"\";\n" +
						
					"	var idOr;\n" + 						
						
					"	for (i=0; i<length; i++){\n" +
						
					"		idOr = identifier + \"\" + i;\n" + 
							
					"		newInner += \"<span id='\" + idOr + \"'>\"+array[i]+\"</span> \";\n"+
							
					"	}\n" +
					
					"	overElem.innerHTML = newInner;\n" +
						
					"	var sampleY = identifier+\"0\";\n" +
						
					"	var sample = document.getElementById(sampleY);\n" +
						
					"	var initY = getYOffset(sample);\n" +  
						
					"	var idF;\n" +		
						
					"	var lines = \"\";\n" +
						
					"	var line;\n" +
						
					"	var top=0;\n" + 
						
					"	var lineNumber=0;\n" +
						
					"	var y=0;\n" +
						
					"	var highlighted = \"\";\n" + 
						
					"	var sPIds = [];\n" +
						
					"	pBridge.outPut('hightlight: llega_ length ' + length);\n" +
						
					"	for (j=0; j<length; j++){\n"+
						
					"		idF = identifier + \"\" + j;\n" +					
					"		var f = document.getElementById(idF);\n" + 	
					
					"		idS = identifier + \"\" + (j+1);\n" +
					"		var n = document.getElementById(idS);\n" +
					"		yN = getYOffset(n);\n" +			
							
					"		lines += array[j] + \" \";\n" +
							
					//"		pBridge.outPut('hightlight: -----------------------------------' );\n" +
					//" 		pBridge.outPut('hightlight: lines: ' + lines  );\n" +	
					//"		pBridge.outPut('hightlight: yN: ' + yN  + ' initY: ' + initY + ' array: ' +  array[j] + ' idF: ' +idF);\n" +
							
					"		if ((yN>initY) || (j==(length-1) && yN<=initY)){\n" +							 
								
					"			var idSp = \"row_\" + lineNumber + \"_\" + identifier;\n" +								
								
					"			var spanned = \"<span id='\" + idSp +  \"'>\" + lines  + \"</span>\";\n" +			
								
					"			sPIds.push(\" + spanned \");\n" +
																							
					"			highlighted += spanned;\n" +
								
					"			pBridge.outPut('hightlight: ' + highlighted);\n" +
								
					"			lines = \"\";\n" +
								
					"			initY = yN;\n" +
								
					"			lineNumber++;\n" +
					
					"		pBridge.outPut('hightlight: ::::::::::::::::::::::::::::::::::::::::::::::::::::' );\n" +
								
					"		}\n" +		
							
					"	}\n" +
						
					"	pBridge.setTextHightlightIds(sPIds);\n" +
						
					"	overElem.innerHTML = highlighted;\n" +
						
					"}\n\n" +
					
					"function getYOffset(el) {\n" +    
						"var _y = 0;\n" + 
						"while( el && !isNaN( el.offsetLeft ) && !isNaN( el.offsetTop ) ) {\n" +			
							"_y += el.offsetTop - el.scrollTop;\n" +		
							"el = el.offsetParent;\n" + 
						"}\n" + 
						"return _y;\n" +	
					"}\n\n" +
						
					"function setLineStyleById(id, color){\n" +
						"var sP = document.getElementById(id);\n" +
						"pBridge.outPut('hightlight: ' + id + ' ' + sP);\n" +  
						"sP.setAttribute(\"style\",\"width: 500px; background-color: \" + color + \";\");\n" +					
					"}\n\n";
			
	
			
			view.loadUrl(hover);
			

			if (loading) {

				int countWM = mP.getWindowsSetHash().size();

				Hashtable hashWM = mP.getWindowsSetHash();

				String dataUrl = (String) hashWM.get(countHash);
				fcWindowTabs.addWindow(dataUrl, false);
				countHash++;

				/*
				 * TabButton tab = (TabButton) loadingWM.get(countLoaded);
				 * 
				 * String tabUrl = tab.getTabURL();
				 * 
				 * if (tabUrl.equals(url)){
				 * 
				 * tab.setHasOptional(false); tab.setOptionalMessage(null);
				 * mP.addWebView(tab.getWebView()); int active =
				 * mP.getActiveWebViewIndex()+1; tab.setId(active);
				 * mP.setActiveWebViewIndex(mP.getActiveWebViewIndex()+1);
				 * 
				 * if( countLoaded > 1 && countLoaded < (amountWM-1) ){
				 * TabButton nextTab = (TabButton) loadingWM.get(countLoaded+1);
				 * String nextUrl = nextTab.getTabURL(); loadPage(nextUrl); } }
				 * 
				 * countLoaded++;
				 * 
				 * if (amountWM==countLoaded){
				 * 
				 * loading = false; }
				 */

			}				
			
			currentPage = url;
			if (url.startsWith("file:////" + BrowserActivity.WEB_ASSETS_PATH
					+ "/loadPage")) {
				setLandingPage(true);				
			} else {
				setLandingPage(false);
			}
			
			fcView.stopAllAnimation(); // Stop 'loading' animation
			mIsLoading = false;

			if (!mP.isInParkingMode()) {
				fcView.startScaleUpAnimation(1000); // Restore original size if
	// not in parking mode

				// Here we start the timer to park the PadKite.
				handler.removeCallbacks(parkingRunnable);
				handler.postDelayed(parkingRunnable, 400);
			}

			fcMainMenu.toggleCloseORRefresh(true);
			mContentWidth = view.getWidth();
			mContentHeight = view.getContentHeight();

			BitmapDrawable bd = new BitmapDrawable(getCircleBitmap(view));
			fcWindowTabs.setHotThumbnail(bd, view);
			fcWindowTabs.setCurrentThumbnail(bd, view);

			fcMainMenu.setBackEabled(view.canGoBack());
			fcMainMenu.setFwdEabled(view.canGoForward());

			if (url.startsWith("file:////" + BrowserActivity.WEB_ASSETS_PATH
					+ "/loadPage")
					|| url.equals("file:////" + BrowserActivity.WEB_PAGES_PATH
	+ "/history.html")
					|| url.equals("file:////" + BrowserActivity.WEB_PAGES_PATH
	+ "/download.html")) {
				fcSettingsMenu.setHomePageEnabled(false);
				fcSettingsMenu.setBookmarkEdit(false);
				fcMainMenu.setShareEabled(false);

			} else {

				fcSettingsMenu.setHomePageEnabled(true);
				fcSettingsMenu.setBookmarkEdit(true);
				fcMainMenu.setShareEabled(true);
			}

			if (url.startsWith(BrowserActivity.WEB_ASSETS_PATH
					+ "/history.html")) {
				int start = 0;
				String[] parts = url.split("\\?", 2);
				try {
					String[] t = parts[1].split("\\=", 2);
					if (t[0].equals("start"))
start = Integer.parseInt(t[1]);
				} catch (Exception e) {

				}
				WebPage page = new WebPage();
				loadData(page.getBrowserHistory(mP, parts[0], start));

			} else if (url.startsWith(BrowserActivity.WEB_ASSETS_PATH
					+ "/download.html")) {
				int start = 0;
				String[] parts = url.split("\\?", 2);
				try {
					String[] t = parts[1].split("\\=", 2);
					if (t[0].equals("start"))
start = Integer.parseInt(t[1]);
				} catch (Exception e) {

				}
				WebPage page = new WebPage();
				loadData(page.getDownloadHistory(mP, url, start));
			}

			// Send Javascript for proxy bridge to identify page. webview.
			// final String currentWeb = "javascript:"
			// + "pBridge.currentPage(theUrl);";
			// view.loadUrl(currentWeb);

			/*final String fontSizeSnippet = "javascript:"
					+ "function getTextSize(x,y){"
					+ "  var element = document.elementFromPoint(x,y);"
					+ "  var fontSize_1 = document.defaultView.getComputedStyle(element, null).fontSize;"
					+ "  var fontSize_2 = element.style.fontSize;"
					+ "  pBridge.moveHitFontHeight(fontSize_1, fontSize_2);"
					+ "}";
			view.loadUrl(fontSizeSnippet);*/
			
		}

		public Bitmap getCircleBitmap(WebView view) {
			Picture thumbnail = view.capturePicture();
			if (thumbnail == null) {
				return null;
			}
			
			Bitmap bm = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);

			Canvas canvas = new Canvas(bm);
			Path path = new Path();

			path.addCircle(25, 25, 25, Path.Direction.CCW);
			canvas.clipPath(path, Region.Op.INTERSECT);

			// May need to tweak these values to determine what is the
			// best scale factor
			int thumbnailWidth = thumbnail.getWidth();
			if (thumbnailWidth > 0) {
				float scaleFactor = (float) 50 / (float) thumbnailWidth;
				canvas.scale(scaleFactor, scaleFactor);
			}
			thumbnail.draw(canvas);

			System.gc();
			return bm;
		}

		@Override
		public void doUpdateVisitedHistory(WebView view, String url,
				boolean isReload) {
			if (!isReload && !url.startsWith("data:text/html")
					&& !url.startsWith("file:///android_asset/")) {

				dbConnector.addToHistory(System.currentTimeMillis() + "", url, view.getTitle(), 1);

				int amount = dbConnector.countFromHistory(view.getTitle());

				dbConnector.insertMostVisited(amount, view.getTitle(), url);

			}
		}

	}

	public void sandCursor() {
		applyImageResource(R.drawable.time_sand_cursor, "time_sand_cursor");
		// pointer.setImageResource(R.drawable.time_sand_cursor);
	}

	public void kiteCursor() {
		applyImageResource(R.drawable.kite_cursor, "kite_cursor");
		// pointer.setImageResource(R.drawable.kite_cursor);
	}

	/* MT Stuff */

	class FCObj {

	}

	private MultiTouchController<FCObj> multiTouchController = new MultiTouchController<FCObj>(
			this, false);

	FCObj fc_obj = new FCObj();

	public FCObj getDraggableObjectAtPoint(PointInfo touchPoint) {
		// Log.w("FC-MT", "getDraggableObjectAtPoint");

		// Dummy obj for now, as it can be done with full screen.
		// Could later be used for different pinch zooms.
		return fc_obj;
	}

	public void getPositionAndScale(FCObj obj,
			PositionAndScale objPosAndScaleOut) {
		// Log.w("FC-MT", "getPositionAndScale");
		// We fill good data, but we don't need that.
		objPosAndScaleOut.set(fcX, fcY, false, 1.0f, false, 1.0f, 1.0f, false,
				0.0f);
	}

	public void selectObject(FCObj obj, PointInfo touchPoint) {
		// float[] xs = touchPoint.getXs();
		// float[] ys = touchPoint.getYs();

		// Log.w("FC-MT", "selectObject: " + touchPoint.getAction() + " -> " +
		// xs[0] + "," + ys[0] + " " + xs[1] + "," + ys[1] + " = " +
		// touchPoint.getEventTime());

		if (mSelectionGestures != null && !mMoveFrozen)
			mSelectionGestures
					.dispatchTouchEventMT(touchPoint,
	touchPoint.isDown() ? MotionEvent.ACTION_DOWN
			: MotionEvent.ACTION_UP);
	}

	public boolean setPositionAndScale(FCObj obj,
			PositionAndScale newObjPosAndScale, PointInfo touchPoint) {

		// float[] xs = touchPoint.getXs();
		// float[] ys = touchPoint.getYs();

		// Log.w("FC-MT", "setPositionAndScale: " + touchPoint.getAction() +
		// " -> " + xs[0] + "," + ys[0] + " " + xs[1] + "," + ys[1] + " = " +
		// touchPoint.getEventTime());

		if (mSelectionGestures != null && !mMoveFrozen)
			mSelectionGestures.dispatchTouchEventMT(touchPoint,
					MotionEvent.ACTION_MOVE);

		return false;
	}

	SelectionGestureView mSelectionGestures = null;

	public void setSelectionGesture(SelectionGestureView v) {
		mSelectionGestures = v;
	}

	class KiteRunnable implements Runnable {

		boolean started = false;
		boolean animated = false;

		public void run() {
			if (false /* !getOption (animateKite) */)
				return;

			if (started) {
				if (!animated)
					fcView.startKiteAnimation();
				animated = true;
			} else {
				started = true;
				animated = false;
				handler.postDelayed(this, 2000);
			}
		}

		public void start() {
			handler.post(this);
		}

		public void stop() {
			if (animated)
				fcView.stopAllAnimation();
			handler.removeCallbacks(this);
			animated = false;
			started = false;
		}
	};

	public void stopFling() {
		if (!mScroller.isFinished()) {
			mScroller.abortAnimation();
		}
	}

	public void setHitPKIndex(int hitPKIndex) {
		this.hitPKIndex = hitPKIndex;
	}

	public void setLastKnownHitType(int lastKnownHitType) {
		this.lastKnownHitType = lastKnownHitType;
	}

	public int getLastKnownHitType() {
		return lastKnownHitType;
	}

	public void setFingerTouch(boolean fingerTouch) {
		this.fingerTouch = fingerTouch;
	}

	public boolean isFingerTouch() {
		return fingerTouch;
	}

}
