package com.roamtouch.floatingcursor;

import java.net.URISyntaxException;
import java.util.StringTokenizer;

import org.metalev.multitouch.controller.MultiTouchController;
import org.metalev.multitouch.controller.MultiTouchController.MultiTouchObjectCanvas;
import org.metalev.multitouch.controller.MultiTouchController.PointInfo;
import org.metalev.multitouch.controller.MultiTouchController.PositionAndScale;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.ClipboardManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
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
import roamtouch.webkit.WebChromeClient;
import roamtouch.webkit.WebHitTestResult;
import roamtouch.webkit.WebVideoInfo;
import roamtouch.webkit.WebView;
import roamtouch.webkit.WebViewClient;
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

import com.roamtouch.view.EventViewerArea;
import com.roamtouch.view.SelectionGestureView;
import com.roamtouch.view.WebPage;
import com.roamtouch.visuals.PointerHolder;
import com.roamtouch.visuals.RingController;
import com.roamtouch.visuals.TipController;
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

public class FloatingCursor extends FrameLayout implements
		MultiTouchObjectCanvas<FloatingCursor.FCObj> {

	private DBConnector dbConnector;
	private BrowserActivity mParent;
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
	private WebView mWebView = null;

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

	// Rect that gets the size of what is below the pointer.
	private Rect rect;

	// FC Scroll speed
	private final int FC_SCROLL_SPEED = 35;

	// Message Ids
	private static final int FOCUS_NODE_HREF = 102;
	private String linkTitle;

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

			rCtrl.drawNothing();
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
		pointer.setImageResource(R.drawable.kite_cursor);
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
				(int) (RADIUS * 0.3f) - 6);

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
				if (!mParent.isInParkingMode && parkTimerStarted) {
					parkTimerStarted = false;
					if (mIsLoading) {
						fcView.startScaleUpAndRotateAnimation(100);
					}
					mParent.enterParkingMode(true);
					updateFC();
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

		// runnableKiteAni = new KiteRunnable();
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
		// fcView.setVisibility(View.VISIBLE);

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
		mIsDisabled = false;
		this.setVisibility(View.VISIBLE);
	}

	public void updateFC() {
		fcX = -(int) pointer.getScrollX() + -(int) getScrollX() + w / 2;
		fcY = -(int) pointer.getScrollY() + -(int) getScrollY() + h / 2;

	}

	public void enterParkingMode() {
		// Relocate the FC again into pointer in case was snapped to a side.
		if (xFlag == 1 || yFlag == 1) {
			relocatePointerToFCView();
			pointer.scrollTo(0, 0);
		}
		// Scale down cursor
		fcView.setRadius(RADIUS * 1 / 3);
		// Reset the cursor.
		pointer.setImageResource(R.drawable.kite_cursor);
	}

	public void setWebView(WebView wv, boolean isFirst) {
		/*
		 * setTab() method is called only when isFirst = true.
		 */
		if (isFirst)
			fcWindowTabs.setTab(wv);

		mWebView = wv;
		mWebView.setDrawingCacheEnabled(true);
		mWebView.setWebChromeClient(new WebClient());
		mWebView.setWebViewClient(new GestureWebViewClient());

		fcMainMenu.setBackEabled(mWebView.canGoBack());
		fcMainMenu.setFwdEabled(mWebView.canGoForward());
	}

	public WebView getWebView() {
		return mWebView;
	}

	public void setEventViewerArea(EventViewerArea eventViewer) {
		this.eventViewer = eventViewer;
		eventViewer.setWindowTabs(fcWindowTabs);
		fcMainMenu.setEventViewer(eventViewer);
		fcWindowTabs.setEventViewer(eventViewer);
	}

	public void setParent(BrowserActivity p, RingController rC,
			TipController tC, PointerHolder pH) {
		mParent = p;
		fcMainMenu.setParent(mParent);
		fcSettingsMenu.setParent(mParent);
		fcWindowTabs.setParent(mParent);
		rCtrl = rC;
		tCtrl = tC;
		pHold = pH;
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
			break;
		case 1:
			currentMenu = fcSettingsMenu;
			fcSettingsMenu.setVisibility(VISIBLE);
			fcMainMenu.setVisibility(INVISIBLE);
			fcWindowTabs.setVisibility(INVISIBLE);
			break;
		case 2:
			currentMenu = fcWindowTabs;
			fcWindowTabs.setVisibility(VISIBLE);
			// fcWindowTabs.drawHotTip();
			fcSettingsMenu.setVisibility(INVISIBLE);
			fcMainMenu.setVisibility(INVISIBLE);
			fcView.setVisibility(INVISIBLE);
			if (currentMenu instanceof CircularTabsLayout) {
				((CircularTabsLayout) currentMenu).resetMenu();
			}
			break;
		}
	}

	public void enableProgressBar() {
		fcProgressBar.enable();
	}

	public void disableProgressBar() {
		fcProgressBar.enable();
	}

	public void addNewWindow(boolean useSelection) {
		if (getWindowCount() > 7) {
			AlertDialog alertDialog;
			alertDialog = new AlertDialog.Builder(mParent).create();
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
			fcWindowTabs.addWindow(selectedLink);
		} else {
			fcWindowTabs.addWindow("");
		}

		selectedLink = "";
	}

	public void addNewWindow(String url) {
		removeSelection();

		if (selectedLink != null)
			fcWindowTabs.addWindow(url);
		else
			fcWindowTabs.addWindow("");

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
		pointer.setImageResource(R.drawable.kite_cursor);
		removeTouchPoint();
	}

	public void toggleMenuVisibility() {

		tCtrl.drawNothing();

		if (animationLock)
			return;

		animationLock = true;

		eventViewer.setMode(EventViewerArea.TEXT_ONLY_MODE);
		AlphaAnimation menuAnimation;

		// Reset FC
		removeTouchPoint();

		if (currentMenu.getVisibility() == INVISIBLE) {

			// Reset menu to Main menu (as WM sometimes gives wrong occurence)
			currentMenu = fcMainMenu;

			// FC was touched, get out of parking mode
			if (mParent.isInParkingMode) {
				mParent.exitParkingMode();
				fcView.setRadius(FC_RADIUS); // Restore radius size
			}

			pointer.setImageResource(R.drawable.kite_cursor);
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
						// Log.d("Reset menu", "---------------------------");
					}
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationStart(Animation animation) {
				}

			});
			currentMenu.startAnimation(menuAnimation);
			vibrator.vibrate(25);
			if (currentMenu instanceof CircularLayout)
				eventViewer.setText(((CircularLayout) currentMenu).getName());
			else if (currentMenu instanceof CircularTabsLayout)
				eventViewer.setText(((CircularTabsLayout) currentMenu)
						.getName());

			// mP.setTopBarVisibility(VISIBLE);
			// mP.setTopBarMode(TopBarArea.ADDR_BAR_MODE);

		} else if (currentMenu.getVisibility() == VISIBLE) {

			mMenuDown = false;
			pointer.setImageResource(R.drawable.kite_cursor);
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

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationStart(Animation animation) {
				}

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

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		fcView.setPosition(w / 2, h / 2);
		fcPointerView.setPosition(w / 2, h / 2);
		fcProgressBar.setPosition(w / 2, h / 2);
		fcMainMenu.setPosition(w / 2, h / 2);
		scrollTo(0, 0);

		if (mParent.isInParkingMode) {
			mParent.exitParkingMode();
			fcView.setRadius(FC_RADIUS);
		}

		this.w = w;
		this.h = h;
		if (w == oldw && h != oldh) {
			if (h < oldh) { // Soft Keyboard now visible
				mSoftKeyboardVisible = true;
			} else { // Soft Keyboard now hidden
				mSoftKeyboardVisible = false;
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

	protected void sendEvent(int action, int X, int Y) {
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis();

		MotionEvent event = MotionEvent.obtain(downTime, eventTime, action, X,
				Y, 0);
		mWebView.onTouchEvent(event);
		event.recycle();
		event = null;
	}

	protected void stopHitTest(int X, int Y, boolean setIcon) {
		if (mHitTestMode) {
			// FIXME: ?
			// sendEvent(MotionEvent.ACTION_MOVE, X, Y);
			// sendEvent(MotionEvent.ACTION_UP, 0, 0);

			sendEvent(MotionEvent.ACTION_CANCEL, X, Y);
			if (setIcon)
				pointer.setImageResource(R.drawable.kite_cursor);

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
	private int resultType;
	private int identifier;

	protected void moveHitTest(int X, int Y) {
		if (mHitTestMode) {

			/*
			Throwable t = new Throwable(); StackTraceElement[] elements =
			t.getStackTrace();				 
			String calleeMethod = elements[0].getMethodName(); String
			callerMethodName = elements[1].getMethodName(); String
			callerClassName = elements[1].getClassName();				
			Log.v("call", "callerMethodName: "+callerMethodName+ " callerClassName: "+callerClassName );
			*/
			
			mWebHitTestResult = mWebView.getHitTestResultAt(X, Y);
			resultType = mWebHitTestResult.getType();
			identifier = mWebHitTestResult.getIdentifier();
			selectedLink = mWebHitTestResult.getExtra();

			// rect = new Rect();
			rect = mWebHitTestResult.getRect();

			// Link Tittle Message
			final Message msg = mHandler
					.obtainMessage(FOCUS_NODE_HREF, 0, 0, 0);
			mWebView.requestFocusNodeHref(msg);

			if (!rect.isEmpty()) { // rect.left==0 && rect.right==0){
				Log.v("identifier", "id: " + identifier);
				Log.v("identifier", "rect :" + rect);
				Log.v("identifier", "_____________________________");
			}

			// Log.v("rect", "rect: " + rect + " WB: " + mWebView.getScrollX() +
			// " " + mWebView.getScrollY());

			int cursorImage = 0;

			// Single Finger: reset timers on change identifier.
			if (SwifteeApplication.getFingerMode()) {
				resetTimersOnChangeId(identifier);
			} else {
				// Set color ring to permanent blue if multifinger.
				rCtrl.paintRingBlue();
			}

			// Log.v("result", "resutl: "+resultType+" id: "+identifier);

			switch (resultType) {

			case WebHitTestResult.PHONE_TYPE: {
				cType = 2; // Phone
				resultType = WebHitTestResult.ANCHOR_TYPE; // Draw ring
				cursorImage = R.drawable.phone_cursor;
				break;
			}

			case WebHitTestResult.TEXT_TYPE: {
				cType = 11; // Text
				Object[] paramText = { rect, SwifteeApplication.GRAY };
				rCtrl.setDrawStyle(SwifteeApplication.DRAW_RING, paramText,
						identifier);
				cursorImage = R.drawable.text_cursor;
				break;
			}

			case WebHitTestResult.VIDEO_TYPE: {
				cType = 10; // Video HTML5
				resultType = WebHitTestResult.ANCHOR_TYPE; // Draw ring
				cursorImage = R.drawable.video_cursor;
				break;
			}

			case WebHitTestResult.SRC_ANCHOR_TYPE:
			case WebHitTestResult.ANCHOR_TYPE: {
				cType = 7; // Text link
				resultType = WebHitTestResult.ANCHOR_TYPE; // Draw ring
				cursorImage = R.drawable.link_cursor;
				String tooltip = mWebHitTestResult.getToolTip();
				if (tooltip.length() > 10) {
					// eventViewer.splitText(WebHitTestResult.ANCHOR_TYPE,tooltip);
				}
				break;
			}

			case WebHitTestResult.EDIT_TEXT_TYPE: {
				cType = 9; // Input text
				resultType = WebHitTestResult.ANCHOR_TYPE; // Draw ring
				cursorImage = R.drawable.keyboard_cursor;
				break;
			}

			case WebHitTestResult.INPUT_TYPE: {
				cType = 12; // Button
				resultType = WebHitTestResult.ANCHOR_TYPE; // Draw ring
				cursorImage = R.drawable.link_button_cursor;
				break;
			}

			case WebHitTestResult.SELECT_TYPE: {
				cType = 13; // ComboBox
				resultType = WebHitTestResult.ANCHOR_TYPE; // Draw ring
				cursorImage = R.drawable.link_combo_cursor;
				break;
			}

			case WebHitTestResult.SRC_IMAGE_ANCHOR_TYPE: {
				cType = 8; // Image link
				resultType = WebHitTestResult.ANCHOR_TYPE; // Draw ring
				cursorImage = R.drawable.link_image_cursor;
				break;
			}

			case WebHitTestResult.IMAGE_ANCHOR_TYPE: {
				cType = 6; // Image Link
				resultType = WebHitTestResult.ANCHOR_TYPE; // Draw ring
				cursorImage = R.drawable.link_cursor;
				// String tooltip = mWebHitTestResult.getToolTip();

				/*
				 * boolean contains; contains =
				 * selectedLink.contains("padkite.local.contact"); if (contains)
				 * { String[] contactId = selectedLink.split("id="); }
				 */

				if (selectedLink == "")
					selectedLink = mWebHitTestResult.getExtra();
				// eventViewer.setText(selectedLink);

				int type = getLinkType(selectedLink);
				if (type == 1) { /* Image */
					cType = 5;
					cursorImage = R.drawable.image_cursor;
				} else if (type == 2) { /* Video */
					cType = 10;
					cursorImage = R.drawable.video_cursor;
				}
				break;
			}

			case WebHitTestResult.IMAGE_TYPE: {
				cType = 5; // Image
				resultType = WebHitTestResult.ANCHOR_TYPE; // Draw ring
				cursorImage = R.drawable.image_cursor;
				// HACK: Mobile YouTube images are not detected, fake it.
				if (selectedLink.startsWith("http://i.ytimg.com/vi/")
						|| isYouTube(selectedLink)) {
					// We fake a link to the current URL
					cType = 10;
					resultType = WebHitTestResult.ANCHOR_TYPE;
					mWebHitTestResult.setType(resultType);
					mWebHitTestResult.setHref(mWebView.getUrl());
					cursorImage = R.drawable.video_cursor;
				}
				break;
			}

			case WebHitTestResult.GEO_TYPE: {
				cType = 3; // Address map
				resultType = WebHitTestResult.ANCHOR_TYPE; // Draw ring
				cursorImage = R.drawable.geo_cursor;
				break;
			}

			case WebHitTestResult.EMAIL_TYPE: {
				cType = 4; // Mail
				resultType = WebHitTestResult.ANCHOR_TYPE; // Draw ring
				cursorImage = R.drawable.email_cursor;
				break;
			}

			default:
				resultType = -1;
				resetTimersOnChangeId(identifier);
				cursorImage = R.drawable.no_target_cursor;
				break;

			}

			// Single Finger: set first and second timers.
			if (SwifteeApplication.getFingerMode() == true && resultType != -1) {

				if (identifier == mWebHitTestResultIdentifer
						&& mFirstTimerStarted == false) {
					setStartFirst(identifier);
				}
				if (identifier == mWebHitTestResultIdentifer
						&& mSecondTimerStarted == false) {
					setStartSecond();
				}
				if (identifier == mWebHitTestResultIdentifer
						&& mThirdTimerStarted == false) {
					setStartThird();
				}
				if (identifier == mWebHitTestResultIdentifer
						&& mFourthTimerStarted == false) {
					setStartFourth();
				}

				if (mFirstTimerStarted && mReadyToFirst) {
					persistRingsTabs(SwifteeApplication.PERSIST_FIRST_STAGE);
				}
				if (mSecondTimerStarted && mReadyToSecond) {
					persistRingsTabs(SwifteeApplication.PERSIST_SECOND_STAGE);
				}
				if (mThirdTimerStarted && mReadyToThird) {
					persistRingsTabs(SwifteeApplication.PERSIST_THIRD_STAGE);
				}

				// Node changed:
				if (WebHitTestResult.ANCHOR_TYPE != resultType
						&& mWebHitTestResultType == WebHitTestResult.ANCHOR_TYPE) {
					stopFirst(true);
					stopSecond(true);
					stopThird(true);
					stopFourth(true);
				}

			}
			// Apply pointer after all.
			pointer.setImageResource(cursorImage);
			// Was there a node change?
			if (identifier != mWebHitTestResultIdentifer) {
				if (resultType == WebHitTestResult.ANCHOR_TYPE) {
					// if (mSoftKeyboardVisible == false) { //Removing,
					// unnecesary Jose
					mWebView.focusNodeAt(X, Y);
					// }
				} else if (mWebHitTestResultType == WebHitTestResult.ANCHOR_TYPE)
					sendEvent(MotionEvent.ACTION_CANCEL, X, Y);
				// FIXME: Use proper API for that.
			}
			mWebHitTestResultType = resultType;
			mWebHitTestResultIdentifer = identifier;
		}
	}; // End Of HitTestResutl

	/**
	 * Message to handle title of the link.
	 **/
	private Handler mHandler = new Handler() {
		/** THIS IS FOR WHEN WE WANT TO READ THE TITLE OF THE LINK **/
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FOCUS_NODE_HREF: {
				String url = (String) msg.getData().get("url");
				if (url == null || url.length() == 0) {
					break;
				}
				String tt = (String) msg.getData().get("title");
				linkTitle = "<b>" + tt + "</b>";
				// Log.v("EXTRA", "title: " + title + " url: " + url);

			}
			}
		}
	};

	/**
	 * SFOM If SINGLE_FINGER_OPERATION_MODE at SwifteeApplication is true the
	 * timers on first are not enabled therefore the mouse has to be operated
	 * with two fingers. If true the mouse can be single finger operated.
	 * */

	public boolean mFirstTimerStarted = false;
	public boolean mReadyToFirst = false;

	boolean mSecondTimerStarted = false;
	boolean mReadyToSecond = false;

	boolean mThirdTimerStarted = false;
	boolean mReadyToThird = false;

	boolean mFourthTimerStarted = false;
	boolean mReadyToFourth = false;

	// Single Finger: Reset SFOM timers
	private void resetTimersOnChangeId(int identifier) {
		if (identifier != mWebHitTestResultIdentifer) {

			if (mFirstTimerStarted) {
				stopFirst(true);
			}
			if (mSecondTimerStarted) {
				stopSecond(false);
				// if (selection)
				removeSelection();
			}
			if (mThirdTimerStarted) {
				stopThird(true);
				// if (selection)
				removeSelection();
			}
			if (mFourthTimerStarted) {
				stopFourth(true);
				// if (selection)
				// removeSelection();
			}
			rCtrl.ringToOriginalColor();
			rCtrl.drawNothing(); // Erase draws.
		}
	};

	// Single Finger: Set timer for first
	private void setStartFirst(int identifier) {
		if (!mFirstTimerStarted) {
			startFirst();
		} else {
			// If the focus is on another link we reset the armed state.
			if (identifier != mWebHitTestResultIdentifer) {
				stopFirst(true);
				startFirst();
			}
		}
	};

	// Single Finger: Start first timer
	private void startFirst() {
		mFirstTimerStarted = true;
		mReadyToFirst = false;

		int time = 0;
		if (cType == WebHitTestResult.TEXT_TYPE) {
			time = 1000; // First timer
		} else {
			time = 350;
		}

		handler.postDelayed(mFirstTimer, time);
	};

	// Single Finger: Stop first
	private void stopFirst(boolean dragging) {
		mFirstTimerStarted = false;
		mReadyToFirst = false;
		handler.removeCallbacks(mFirstTimer);
		if (!dragging) {
			pointer.setImageResource(R.drawable.kite_cursor);
		}
	};

	// Single Finger: Set timer for second
	private void setStartSecond() {
		if (!mSecondTimerStarted) {
			startSecond();
		}
	};

	// Single Finger: Start selection
	private void startSecond() {
		mSecondTimerStarted = true;
		mReadyToSecond = false;

		int time = 00;
		if (cType == WebHitTestResult.TEXT_TYPE) {
			time = 2500; // First timer
		} else {
			time = 1500;
		}

		handler.postDelayed(mSecondTimer, time);
	};

	// Single Finger: Stop selection
	private void stopSecond(boolean dragging) {
		mSecondTimerStarted = false;
		mReadyToSecond = false;
		handler.removeCallbacks(mSecondTimer);
		if (!dragging) {
			pointer.setImageResource(R.drawable.kite_cursor);
		}
	};

	// Single Finger: Set timer for second
	private void setStartThird() {
		if (!mThirdTimerStarted) {
			startThird();
		}
	};

	// Single Finger: Start selection
	private void startThird() {
		mThirdTimerStarted = true;
		mReadyToThird = false;

		int time = 0;
		if (cType == WebHitTestResult.TEXT_TYPE) {
			time = 4000; // First timer
		} else {
			time = 3000;
		}

		handler.postDelayed(mThirdTimer, time);
	};

	// Single Finger: Stop selection
	private void stopThird(boolean dragging) {
		mThirdTimerStarted = false;
		mReadyToThird = false;
		handler.removeCallbacks(mThirdTimer);
		if (!dragging) {
			pointer.setImageResource(R.drawable.kite_cursor);
		}
	};

	// Single Finger: Set timer for second
	private void setStartFourth() {
		if (!mFourthTimerStarted) {
			startFourth();
		}
	};

	// Single Finger: Start selection
	private void startFourth() {
		mFourthTimerStarted = true;
		mReadyToFourth = false;
		handler.postDelayed(mFourthTimer, 4500);
	};

	// Single Finger: Stop selection
	private void stopFourth(boolean dragging) {
		mFourthTimerStarted = false;
		mReadyToFourth = false;
		handler.removeCallbacks(mFourthTimer);
		if (!dragging) {
			pointer.setImageResource(R.drawable.kite_cursor);
		}
	};

	/**
	 * Arms link with link_cursor_armed icon after half a second passed after
	 * over on link. Sets mReadyToFirst on onTouchUp().
	 **/
	Runnable mFirstTimer = new Runnable() {
		public void run() {
			setFirstRingTab();
			mReadyToFirst = true;
			mReadyToSecond = false;
			mReadyToThird = false;
		}
	};

	/**
	 * Single Finger: Arms media cursors for selection. Sets mReadyToSecond on.
	 * Sets mReadyToFirst on onTouchUp().
	 **/
	Runnable mSecondTimer = new Runnable() {
		public void run() {
			setSecondRingTab();
			mReadyToFirst = false;
			mReadyToSecond = true;
			mReadyToThird = false;
			// teta
			// vA.drawTestTip(rect); Not implemented yet
			// onLongTouch(); //SELECT TEXT AFTER TIMEOUT.
		}
	};

	/**
	 * Single Finger: Arms media cursors for selection. Sets mReadyToSecond on.
	 * Sets mReadyToFirst on onTouchUp().
	 **/
	Runnable mThirdTimer = new Runnable() {
		public void run() {
			setThirdRingTab();
			mReadyToFirst = false;
			mReadyToSecond = false;
			mReadyToThird = true;
			// onLongTouch(); //SELECT TEXT AFTER TIMEOUT.
		}
	};

	/**
	 * Single Finger: Arms media cursors for selection. Sets mReadyToSecond on.
	 * Sets mReadyToFourth to restart cycle after finished.
	 **/
	Runnable mFourthTimer = new Runnable() {
		public void run() {

			mFirstTimerStarted = false;
			mReadyToFirst = true;

			mSecondTimerStarted = false;
			mReadyToSecond = false;

			mThirdTimerStarted = false;
			mReadyToThird = false;

			mFourthTimerStarted = false;
			mReadyToFourth = false;

		}
	};

	/*
	 * No Target UNKNOWN_TYPE = 0 Phone PHONE_TYPE = 2 Note: Phone not working,
	 * returning 0 however link is there and opens phone app. Geo GEO_TYPE = 3
	 * Mail EMAIL_TYPE = 4 Image IMAGE_TYPE = 5 Image Link IMAGE_ANCHOR_TYPE = 6
	 * Note: Implemented image into link as image. It can be selected or
	 * executed. Text link ANCHOR_TYPE = 7 Image link SRC_IMAGE_ANCHOR_TYPE = 8
	 * Input text EDIT_TEXT_TYPE = 9 Note: TODO check != "" to set text edition
	 * cursor. Video VIDEO_TYPE = 10 Note: HTML5 tags only. WebVideoInfo
	 * videoInfo = mWebHitTestResult.getVideoInfo(); Text TEXT_TYPE = 11
	 * --------------------------------------------- Button INPUT_TYPE = 12
	 * CheckBox INPUT_TYPE = 12 RadioButon INPUT_TYPE = 12 ComboBox SELECT_TYPE
	 * = 13
	 */

	/**
	 * PHONE_TYPE Call Copy Gesture
	 * 
	 * GEO_TYPE
	 */

	/**
	 * Draws tab over ring when first timer both on link or on Input Text. In
	 * the case of Input Text user can write.
	 **/
	private void setFirstRingTab() {
		switch (cType) {
		case WebHitTestResult.TEXT_TYPE:
			firstText();
			break;
		case WebHitTestResult.EDIT_TEXT_TYPE:
			firstInput();
			break;
		case WebHitTestResult.GEO_TYPE:
			firstMap();
		case WebHitTestResult.EMAIL_TYPE:
			firstMail();
			break;
		default:
			firstDefault();
			break;
		}
	};

	/**
	 * The same as first timer but with selection. In the case of input text
	 * user can paste text on touch up.
	 **/
	private void setSecondRingTab() {
		switch (cType) {
		case WebHitTestResult.TEXT_TYPE:
			secondText();
			break;
		case WebHitTestResult.EDIT_TEXT_TYPE:
			secondInput();
			break;
		case WebHitTestResult.GEO_TYPE:
			secondMap();
			break;
		case WebHitTestResult.EMAIL_TYPE:
			secondMail();
			break;
		default:
			secondDefault();
			break;
		}
	};

	/**
	 * The same as second but with selection. In the case of input text user can
	 * paste text on touch up.
	 **/
	private void setThirdRingTab() {
		switch (cType) {
		case WebHitTestResult.TEXT_TYPE:
			thirdText();
			break;
		case WebHitTestResult.EDIT_TEXT_TYPE:
			thirdInput();
			break;
		default:
			thirdDefault();
			break;
		}
	};

	/**
	 * Single Finger: When the cursor is moved on top of the same media the same
	 * cursor/ring/tab remains.
	 **/
	private void persistRingsTabs(int stage) { // MAL, debe contemplar el tipo

		switch (stage) {

		case SwifteeApplication.PERSIST_FIRST_STAGE:
			switch (cType) {
			case WebHitTestResult.TEXT_TYPE:
				firstText();
				break;
			case WebHitTestResult.EDIT_TEXT_TYPE:
				firstInput();
				break;
			case WebHitTestResult.GEO_TYPE:
				firstMap();
				break;
			case WebHitTestResult.EMAIL_TYPE:
				firstMail();
				break;
			default:
				firstDefault();
				break;
			}
			break;

		case SwifteeApplication.PERSIST_SECOND_STAGE:
			switch (cType) {
			case WebHitTestResult.TEXT_TYPE:
				secondText();
				break;
			case WebHitTestResult.EDIT_TEXT_TYPE:
				secondInput();
				break;
			case WebHitTestResult.GEO_TYPE:
				secondMap();
				break;
			case WebHitTestResult.EMAIL_TYPE:
				secondMail();
				break;
			default:
				secondDefault();
				break;
			}
			break;

		case SwifteeApplication.PERSIST_THIRD_STAGE:
			switch (cType) {
			case WebHitTestResult.TEXT_TYPE:
				thirdText();
				break;
			case WebHitTestResult.EDIT_TEXT_TYPE:
				thirdInput();
				break;
			default:
				thirdDefault();
				break;
			}
			break;
		}
		;
	};

	/**
	 * ALL FIRST.
	 */

	public void firstText() {
		Object[] param_TEXT = { rect, SwifteeApplication.BLUE, "WORD",
				SwifteeApplication.PAINT_BLUE };
		rCtrl.setDrawStyle(SwifteeApplication.DRAW_RING_AND_TAB, param_TEXT,
				identifier);
		mWebView.executeSelectionCommand(fcX, fcY, WebView.SELECT_WORD);
		mWebView.executeSelectionCommand(fcX, fcY, WebView.COPY_TO_CLIPBOARD);
	}

	public void firstInput() {
		Object[] paramEDIT_TEXT = { rect, SwifteeApplication.VIOLET, "WRITE",
				SwifteeApplication.PAINT_VIOLET };
		rCtrl.setDrawStyle(SwifteeApplication.DRAW_TAB, paramEDIT_TEXT,
				identifier);
	}

	public void firstMap() {
		Object[] param_default = { rect, SwifteeApplication.RED_MAP,
				"OPEN MAP", SwifteeApplication.PAINT_RED_MAP };
		rCtrl.setDrawStyle(SwifteeApplication.DRAW_TAB, param_default,
				identifier);
	}

	public void firstMail() {
		Object[] param_default = { rect, SwifteeApplication.TURQUOISE,
				"SEND EMAIL", SwifteeApplication.PAINT_TURQUOISE };
		rCtrl.setDrawStyle(SwifteeApplication.DRAW_TAB, param_default,
				identifier);
	}

	public void firstDefault() {
		Object[] param_default = { rect, SwifteeApplication.GREEN, "OPEN",
				SwifteeApplication.PAINT_GREEN };
		Log.v("rect", "first : " + rect + " identifier: " + identifier);
		rCtrl.setDrawStyle(SwifteeApplication.DRAW_TAB, param_default,
				identifier);
	}

	/**
	 * ALL SECOND.
	 */

	public void secondText() {
		Object[] param_TEXT = { rect, SwifteeApplication.BLUE, "LINE",
				SwifteeApplication.PAINT_BLUE };
		rCtrl.setDrawStyle(SwifteeApplication.DRAW_RING_AND_TAB, param_TEXT,
				identifier);
		mWebView.executeSelectionCommand(fcX, fcY, WebView.SELECT_LINE);
		mWebView.executeSelectionCommand(fcX, fcY, WebView.COPY_TO_CLIPBOARD);
	}

	public void secondInput() {
		Object[] paramEDIT_TEXT = { rect, SwifteeApplication.ORANGE, "PASTE",
				SwifteeApplication.PAINT_ORANGE };
		rCtrl.setDrawStyle(SwifteeApplication.DRAW_TAB, paramEDIT_TEXT,
				identifier);
	}

	public void secondMap() {
		Object[] paramEDIT_TEXT = { rect, SwifteeApplication.BLUE,
				"COPY LOCATION", SwifteeApplication.PAINT_BLUE };
		rCtrl.setDrawStyle(SwifteeApplication.DRAW_TAB, paramEDIT_TEXT,
				identifier);
	}

	public void secondMail() {
		Object[] paramEDIT_TEXT = { rect, SwifteeApplication.BLUE,
				"COPY EMAIL", SwifteeApplication.PAINT_BLUE };
		rCtrl.setDrawStyle(SwifteeApplication.DRAW_TAB, paramEDIT_TEXT,
				identifier);
	}

	public void secondDefault() {
		Object[] paramEDIT_TEXT = { rect, SwifteeApplication.BLUE, "COPY",
				SwifteeApplication.PAINT_BLUE };
		rCtrl.setDrawStyle(SwifteeApplication.DRAW_TAB, paramEDIT_TEXT,
				identifier);
	}

	/**
	 * ALL THIRD.
	 */

	public void thirdText() {
		Object[] param_TEXT_TYPE = { rect, SwifteeApplication.BLUE,
				"PHARAGRAPH", SwifteeApplication.PAINT_BLUE };
		rCtrl.setDrawStyle(SwifteeApplication.DRAW_RING_AND_TAB,
				param_TEXT_TYPE, identifier);
		mWebView.executeSelectionCommand(fcX, fcY, WebView.SELECT_PARAGRAPH);
		mWebView.executeSelectionCommand(fcX, fcY, WebView.COPY_TO_CLIPBOARD);
	}

	public void thirdInput() {
		Object[] param_default = { rect, SwifteeApplication.RED, "VOICE",
				SwifteeApplication.PAINT_RED };
		rCtrl.setDrawStyle(SwifteeApplication.DRAW_TAB, param_default,
				identifier);
	}

	public void thirdDefault() {
		Object[] param_default = { rect, SwifteeApplication.BLACK, "GESTURE",
				SwifteeApplication.PAINT_BLACK };
		rCtrl.setDrawStyle(SwifteeApplication.DRAW_TAB, param_default,
				identifier);
		Log.v("rect", "third : " + rect + " identifier: " + identifier);
	}

	// Erase draws.
	public void eraseDraws() {
		rCtrl.drawNothing();
	}

	public void drawTip(Rect re, String[] comment, float X, float Y, int isFor) {
		
		int _x = Math.round(X);
		int _y = Math.round(Y);
		int[] initCoor = { _x, _y };
		Object[] paramTip = { re, comment, initCoor, 1000 };
		tCtrl.setTipComment(paramTip, isFor);
	}

	public void drawNone() {
		rCtrl.drawNothing();
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

	protected void startHitTest(int X, int Y) {

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
		pointer.setImageResource(R.drawable.kite_cursor);
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
		mWebView.executeSelectionCommand(fcX, fcY, WebView.CLEAR_SELECTION);
	}

	public void startSelectionCommand() {
		startHitTest(fcX, fcY);
		mWebView.executeSelectionCommand(fcX, fcY, WebView.START_SELECTION);
	}

	public void executeSelectionCommand(int cmd) {
		mWebView.executeSelectionCommand(fcX, fcY, cmd);
	}

	public void stopSelectionCommand() {
		mWebView.executeSelectionCommand(fcX, fcY, WebView.STOP_SELECTION);
		mWebView.executeSelectionCommand(fcX, fcY, WebView.COPY_TO_CLIPBOARD);
	}

	public void onClick() {
		if (mWebHitTestResult == null)
			return;

		mLongTouchEnabled = false;
		executeSelectionCommand(WebView.STOP_SELECTION);

		if (mWebHitTestResult.getType() == WebHitTestResult.ANCHOR_TYPE
				|| mWebHitTestResult.getType() == WebHitTestResult.SRC_ANCHOR_TYPE
				|| mWebHitTestResult.getType() == WebHitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
			eventViewer.setText("Executing link ...");

			sendEvent(MotionEvent.ACTION_DOWN, fcX, fcY);
			// pointer.setImageResource(R.drawable.address_bar_cursor);
			sendEvent(MotionEvent.ACTION_UP, fcX, fcY);
			startHitTest(fcX, fcY);
		} else if (mWebHitTestResult.getType() == WebHitTestResult.EDIT_TEXT_TYPE
				|| mWebHitTestResult.getType() == WebHitTestResult.UNKNOWN_TYPE
				|| mWebHitTestResult.getType() == WebHitTestResult.INPUT_TYPE
				|| mWebHitTestResult.getType() == WebHitTestResult.SELECT_TYPE
				|| mWebHitTestResult.getType() == -1) {
			eventViewer.setText("Clicking ...");
			cancelSelection();

			sendEvent(MotionEvent.ACTION_DOWN, fcX, fcY);
			// pointer.setImageResource(R.drawable.address_bar_cursor);
			sendEvent(MotionEvent.ACTION_UP, fcX, fcY);
		} else {

			if (mWebHitTestResult.getType() == WebHitTestResult.IMAGE_TYPE) {
				eventViewer.setText("Selecting image ...");

				mWebView.executeSelectionCommand(fcX, fcY,
						WebView.SELECT_OBJECT);
				mWebView.executeSelectionCommand(fcX, fcY,
						WebView.COPY_HTML_FRAGMENT_TO_CLIPBOARD);
			} else if (mWebHitTestResult.getType() == WebHitTestResult.TEXT_TYPE) {
				eventViewer.setText("Selecting word ...");

				mWebView.executeSelectionCommand(fcX, fcY, WebView.SELECT_WORD);
				mWebView.executeSelectionCommand(fcX, fcY,
						WebView.COPY_TO_CLIPBOARD);
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

	public void onLongTouch() {
		if (mWebHitTestResult == null)
			return;
		if (mWebHitTestResult.getType() == WebHitTestResult.IMAGE_TYPE) {
			eventViewer.setText("Detected Long-Touch. Selecting image ...");
			mWebView.executeSelectionCommand(fcX, fcY, WebView.SELECT_OBJECT);
			mWebView.executeSelectionCommand(fcX, fcY,
					WebView.COPY_HTML_FRAGMENT_TO_CLIPBOARD);
			mLongTouchEnabled = true;
		} else if (mWebHitTestResult.getType() == WebHitTestResult.TEXT_TYPE) {
			eventViewer.setText("Detected Long-Touch. Selecting word ...");
			mWebView.executeSelectionCommand(fcX, fcY, WebView.SELECT_WORD);
			mWebView.executeSelectionCommand(fcX, fcY,
					WebView.COPY_TO_CLIPBOARD);
			mLongTouchEnabled = true;
		} else if (mWebHitTestResult.getType() == WebHitTestResult.ANCHOR_TYPE
				|| mWebHitTestResult.getType() == WebHitTestResult.SRC_ANCHOR_TYPE
				|| mWebHitTestResult.getType() == WebHitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
			eventViewer.setText("Detected Long-Touch. Selecting link ...");
			selectedLink = mWebHitTestResult.getExtra();
			Point focusCenter = mWebHitTestResult.getPoint();
			focusCenter.y += 100;
			focusCenter.x += 50;

			// Log.e("SELECT_LINK", "x: " + focusCenter.x + ", y: " +
			// focusCenter.y + ", fcX: " + fcX + ", fcY: " + fcY);
			mWebView.executeSelectionCommand(focusCenter.x, focusCenter.y,
					WebView.SELECT_LINK);
			((ClipboardManager) mParent
					.getSystemService(Context.CLIPBOARD_SERVICE))
					.setText(selectedLink);
			mParent.setSelection(selectedLink);
			// mWebView.executeSelectionCommand(fcX, fcY,
			// WebView.COPY_TO_CLIPBOARD);
			mLongTouchEnabled = true;
		} else if (mWebHitTestResult.getType() == WebHitTestResult.EDIT_TEXT_TYPE) {
			eventViewer
					.setText("Detected Long-Touch. Pasting clipboard contents ...");

			final String selection = (String) ((ClipboardManager) mParent
					.getSystemService(Context.CLIPBOARD_SERVICE)).getText();

			if (selection != "") {
				pasteTextIntoInputText(fcX, fcY, false);
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

		if (mWebHitTestResult.getType() == WebHitTestResult.EDIT_TEXT_TYPE) {
			cancelSelection();
			disableGestures();
			return;
		}

		mLongTouchEnabled = false;

		stopSelectionCommand();
		startSelection(true);

		// Added for distinguishing various selection

		boolean linkFlag = false;

		if (mWebHitTestResult.getType() == WebHitTestResult.IMAGE_TYPE) {
			mParent.setGestureType(SwifteeApplication.CURSOR_IMAGE_GESTURE);
			linkFlag = true;
		} else if (mWebHitTestResult.getType() == WebHitTestResult.VIDEO_TYPE) {
			mParent.setGestureType(SwifteeApplication.CURSOR_VIDEO_GESTURE);
			linkFlag = true;
		} else if (mWebHitTestResult.getType() == WebHitTestResult.TEXT_TYPE) {
			mParent.setGestureType(SwifteeApplication.CURSOR_TEXT_GESTURE);
		} else if (mWebHitTestResult.getType() == WebHitTestResult.ANCHOR_TYPE
				|| mWebHitTestResult.getType() == WebHitTestResult.SRC_ANCHOR_TYPE
				|| mWebHitTestResult.getType() == WebHitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
			int type = getLinkType(selectedLink);
			if (type == 0)
				mParent.setGestureType(SwifteeApplication.CURSOR_LINK_GESTURE);
			else if (type == 1)
				mParent.setGestureType(SwifteeApplication.CURSOR_IMAGE_GESTURE);
			else
				mParent.setGestureType(SwifteeApplication.CURSOR_VIDEO_GESTURE);

			linkFlag = true;
		}

		if (linkFlag) {
			stopSelection();
		}
	}

	public void onLongTouchHack(int fX, int fY) {

		int old_fcX = fcX;
		int old_fcY = fcY;

		fcX = fX;
		fcY = fY;

		// This is false by default for this case
		// mHitTestMode = true;
		// moveHitTest(fcX, fcY);

		if (mWebHitTestResult != null) {
			mLongTouchHack = true;
			mLongTouchHackObj = mWebHitTestResult;
			onLongTouch();
		}

		fcX = old_fcX;
		fcY = old_fcY;
		// Removing HitTest from here since overlaps with Circular Menu opened
		// moveHitTest(fcX, fcY);

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

	// ACTIONS
	public void onTouchUp() {
		// Erase any ring/tip around on touch up.
		eraseDraws();

		/*
		 * No Target UNKNOWN_TYPE = 0 Phone PHONE_TYPE = 2 Note: Phone not
		 * working, returning 0 however link is there and opens phone app. Geo
		 * GEO_TYPE = 3 Mail EMAIL_TYPE = 4 Image IMAGE_TYPE = 5 Image Link
		 * IMAGE_ANCHOR_TYPE = 6 Note: Implemented image into link as image. It
		 * can be selected or executed. Text link ANCHOR_TYPE = 7 Image link
		 * SRC_IMAGE_ANCHOR_TYPE = 8 Input text EDIT_TEXT_TYPE = 9 Note: TODO
		 * check != "" to set text edition cursor. Video VIDEO_TYPE = 10 Note:
		 * HTML5 tags only. WebVideoInfo videoInfo =
		 * mWebHitTestResult.getVideoInfo(); Text TEXT_TYPE = 11
		 * --------------------------------------------- Button INPUT_TYPE = 12
		 * CheckBox INPUT_TYPE = 12 RadioButon INPUT_TYPE = 12 ComboBox
		 * SELECT_TYPE = 13
		 */

		if (mReadyToFirst) {

			switch (mWebHitTestResult.getType()) {

			case WebHitTestResult.TEXT_TYPE:
				mGesturesEnabled = true;
				mParent.setGestureType(SwifteeApplication.CURSOR_TEXT_GESTURE);
				mParent.startGesture(true);
				final String selection = (String) ((ClipboardManager) mParent
						.getSystemService(Context.CLIPBOARD_SERVICE)).getText();

				String[] firstText = trimSelectedTextForTip();
				drawTip(rect, firstText, fcX, fcY,
						SwifteeApplication.IS_FOR_WEB_TIPS);
				break;

			case WebHitTestResult.SRC_IMAGE_ANCHOR_TYPE:
				eventViewer.setText("Opening image in link...");
				break;

			case WebHitTestResult.EDIT_TEXT_TYPE:
				eventViewer.setText("Opening keyboard...");
				break;

			default:
				// eventViewer.setText("Executing link...");
				String[] t3 = { "Opening ", linkTitle };
				drawTip(rect, t3, fcX, fcY, SwifteeApplication.IS_FOR_WEB_TIPS);
				break;
			}
			if (mWebHitTestResult.getType() != WebHitTestResult.TEXT_TYPE) {
				sendEvent(MotionEvent.ACTION_DOWN, fcX, fcY);
				sendEvent(MotionEvent.ACTION_UP, fcX, fcY);
			}

			stopFirst(false);
			stopSecond(false);
			stopThird(false);
			stopFourth(false);

		} else if (mReadyToSecond) {

			switch (mWebHitTestResult.getType()) {

			case WebHitTestResult.TEXT_TYPE:
				mGesturesEnabled = true;
				mParent.setGestureType(SwifteeApplication.CURSOR_TEXT_GESTURE);
				mParent.startGesture(true);
				String[] secondText = trimSelectedTextForTip();
				drawTip(rect, secondText, fcX, fcY,
						SwifteeApplication.IS_FOR_WEB_TIPS);
				break;

			case WebHitTestResult.SRC_IMAGE_ANCHOR_TYPE:
				eventViewer.setText("Opening image in link...");
				mWebView.executeSelectionCommand(fcX, fcY,
						WebView.SELECT_OBJECT);
				mWebView.executeSelectionCommand(fcX, fcY,
						WebView.COPY_TO_CLIPBOARD);
				break;

			case WebHitTestResult.EDIT_TEXT_TYPE:
				pasteTextIntoInputText(fcX, fcY, false);
				final String selection = (String) ((ClipboardManager) mParent
						.getSystemService(Context.CLIPBOARD_SERVICE)).getText();
				eventViewer.setText("Pasting text..." + selection);
				break;

			default:
				forceRing(fcX, fcY);
				((ClipboardManager) mParent
						.getSystemService(Context.CLIPBOARD_SERVICE))
						.setText(selectedLink);
				mParent.setSelection(selectedLink);
				String[] t3 = { linkTitle, "copied to clipboard" };
				drawTip(rect, t3, fcX, fcY, SwifteeApplication.IS_FOR_WEB_TIPS);
				break;
			}

			stopSecond(false);
			stopThird(false);
			stopFourth(false);

		} else if (mReadyToThird) {

			switch (mWebHitTestResult.getType()) {

			case WebHitTestResult.TEXT_TYPE:
				mGesturesEnabled = true;
				mParent.setGestureType(SwifteeApplication.CURSOR_TEXT_GESTURE);
				mParent.startGesture(true);
				String[] thirdText = trimSelectedTextForTip();
				drawTip(rect, thirdText, fcX, fcY,
						SwifteeApplication.IS_FOR_WEB_TIPS);
				break;

			/*
			 * case WebHitTestResult.SRC_IMAGE_ANCHOR_TYPE:
			 * eventViewer.setText("Opening image in link...");
			 * mWebView.executeSelectionCommand(fcX, fcY,
			 * WebView.SELECT_OBJECT); mWebView.executeSelectionCommand(fcX,
			 * fcY, WebView.COPY_TO_CLIPBOARD); break;
			 */

			case WebHitTestResult.EDIT_TEXT_TYPE:
				eventViewer.setText("Opening voice recognition...");
				mParent.startVoiceRecognitionActivity(fcX, fcY);
				break;

			default:
				forceRing(fcX, fcY);
				((ClipboardManager) mParent
						.getSystemService(Context.CLIPBOARD_SERVICE))
						.setText(selectedLink);
				mGesturesEnabled = true;
				mParent.setGestureType(SwifteeApplication.CURSOR_LINK_GESTURE);
				mParent.startGesture(true);
				String[] t3 = { "Draw a gesture for", linkTitle };
				drawTip(rect, t3, fcX, fcY, SwifteeApplication.IS_FOR_WEB_TIPS);
				break;
			}

			stopThird(false);
			stopFourth(false);

		} else {

			stopFirst(false);
			stopSecond(false);
			stopThird(false);
			stopFourth(false);
		}
		pointer.setImageResource(R.drawable.kite_cursor);

	}

	private String[] trimSelectedTextForTip() {
		String selectionText = null;
		final String selText = (String) ((ClipboardManager) mParent
				.getSystemService(Context.CLIPBOARD_SERVICE)).getText();
		if (!mReadyToFirst) {
			selectionText = selText.substring(0, 8) + "...";
		} else {
			selectionText = selText;
		}
		String word = "<b>" + selectionText + "</b>" + " selected";
		String[] t1 = { word, "draw a gesture" };
		return t1;
	}

	public void forceRing(int X, int Y) {
		mWebView.focusNodeAt(X, Y);
	}

	/**
	 * Finally paste the text into the Input Text with stored x,y.
	 * **/
	// final Instrumentation instrumentation = new Instrumentation();

	public void pasteTextIntoInputText(int X, int Y, boolean ENTER) {

		sendEvent(MotionEvent.ACTION_DOWN, X, Y);
		sendEvent(MotionEvent.ACTION_UP, X, Y);

		handler.postDelayed(new Runnable() {
			public void run() {
				String selection = (String) ((ClipboardManager) mParent
						.getSystemService(Context.CLIPBOARD_SERVICE)).getText();
				mWebView.pasteText(selection);
			}
		}, 500);
		// mWebView.focusNodeAt(0,0);
		if (ENTER) {
			// instrumentation.sendKeyDownUpSync(66);
		}
	};

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

		mWebView.executeSelectionCommand(fcX, fcY, WebView.STOP_SELECTION);
		mWebView.executeSelectionCommand(fcX, fcY, WebView.COPY_TO_CLIPBOARD);
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
				eventViewer
						.setText("Please select now more text with the FC ...");
				mSelectionStarted = true;
				stopHitTest(fcX, fcY, true);
				pointer.setImageResource(R.drawable.text_cursor);
			}
		} else
			mWebView.executeSelectionCommand(fcX, fcY, WebView.EXTEND_SELECTION);
	}

	protected void cancelSelection() {
		if (!mSelectionActive)
			return;

		mSelectionActive = false;
		mSelectionStarted = false;

		mWebView.executeSelectionCommand(fcX, fcY, WebView.CLEAR_SELECTION);
	}

	/*
	 * 
	 * protected void clickSelection(int X, int Y) { if (mWebHitTestResult ==
	 * null) return;
	 * 
	 * //Toast.makeText(mContext, "Clicking sel ..." +
	 * mWebHitTestResult.getType(), Toast.LENGTH_LONG).show();
	 * 
	 * if (mWebHitTestResult.getType() == WebHitTestResult.ANCHOR_TYPE ||
	 * mWebHitTestResult.getType() == WebHitTestResult.SRC_ANCHOR_TYPE ||
	 * mWebHitTestResult.getType() == WebHitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
	 * // Toast.makeText(mContext, "Clicking link ...",
	 * Toast.LENGTH_LONG).show(); /** sendEvent(MotionEvent.ACTION_DOWN, X, Y);
	 * pointer.setImageResource(R.drawable.address_bar_cursor);
	 * sendEvent(MotionEvent.ACTION_UP, X, Y); startHitTest(X,Y); * /
	 * 
	 * mWebView.focusNodeAt(X,Y);
	 * 
	 * mGesturesEnabled = true; mWebView.executeSelectionCommand(X, Y,
	 * WebView.START_SELECTION); mWebView.executeSelectionCommand(X, Y,
	 * WebView.STOP_SELECTION); mWebView.executeSelectionCommand(X, Y,
	 * WebView.SELECT_WORD_OR_LINK); mWebView.executeSelectionCommand(X, Y,
	 * WebView.COPY_TO_CLIPBOARD); //
	 * mParent.startGesture(SwifteeApplication.CURSOR_LINK_GESTURE); }
	 * if(mWebHitTestResult.getType() == WebHitTestResult.EDIT_TEXT_TYPE){
	 * sendEvent(MotionEvent.ACTION_DOWN, X, Y);
	 * //pointer.setImageResource(R.drawable.address_bar_cursor);
	 * sendEvent(MotionEvent.ACTION_UP, X, Y); } else if
	 * (mWebHitTestResult.getType() == WebHitTestResult.IMAGE_TYPE) {
	 * eventViewer.setText("Selecting image ...");
	 * 
	 * mGesturesEnabled = true; mWebView.executeSelectionCommand(X, Y,
	 * WebView.SELECT_OBJECT); mWebView.executeSelectionCommand(X, Y,
	 * WebView.COPY_HTML_FRAGMENT_TO_CLIPBOARD);
	 * //pointer.setImageResource(R.drawable.address_bar_cursor);
	 * //removeTouchPoint();
	 * 
	 * // FIXME: Add Downloading of image } else if (mWebHitTestResult.getType()
	 * == WebHitTestResult.TEXT_TYPE) {
	 * eventViewer.setText("Selecting word ...");
	 * 
	 * mGesturesEnabled = true; mWebView.executeSelectionCommand(X, Y,
	 * WebView.SELECT_WORD_OR_LINK); mWebView.executeSelectionCommand(X, Y,
	 * WebView.COPY_TO_CLIPBOARD);
	 * 
	 * pointer.setImageResource(R.drawable.no_target_cursor); // Re-start
	 * HitTest functionality startHitTest(X,Y); //
	 * mParent.startGesture(SwifteeApplication.CURSOR_TEXT_GESTURE);
	 * //removeTouchPoint();
	 * 
	 * // This is called by onClipBoardUpdate changed if mGesturesEnabled is
	 * true // mParent.startGesture(); } }
	 * 
	 * protected void cancelSelection() { sendEvent(MotionEvent.ACTION_CANCEL,
	 * selX, selY); }
	 * 
	 * protected void stopSelection(int X, int Y) { if (mSelectionMode) {
	 * mSelectionMode = false;
	 * 
	 * if (!mSelectionStarted) clickSelection(X,Y); else {
	 * sendEvent(MotionEvent.ACTION_UP, X, Y);
	 * pointer.setImageResource(R.drawable.no_target_cursor);
	 * mParent.startGesture(SwifteeApplication.CURSOR_TEXT_GESTURE); }
	 * removeTouchPoint(); } }
	 * 
	 * protected void checkClickSelection(int X, int Y) { if (mSelectionMode) {
	 * if (!mSelectionStarted) { clickSelection(X,Y); mSelectionMode = false; }
	 * } }
	 * 
	 * protected void moveSelection(int X, int Y) { if (mSelectionMode) { if
	 * (!mSelectionStarted) { final int yDiff = (int) Math.abs(Y - selY); final
	 * int xDiff = (int) Math.abs(X - selX);
	 * 
	 * if (yDiff > mTouchSlop || xDiff > mTouchSlop) { // FIXME: Change to
	 * Velu's selection API pointer.setImageResource(R.drawable.text_cursor);
	 * mWebView.onKeyDown(KeyEvent.KEYCODE_SHIFT_LEFT, new
	 * KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT));
	 * sendEvent(MotionEvent.ACTION_DOWN, X, Y);
	 * //mWebView.executeSelectionCommand(X, Y, WebView.SELECT_WORD_OR_LINK);
	 * mSelectionStarted = true; } } else sendEvent(MotionEvent.ACTION_MOVE, X,
	 * Y); } }
	 * 
	 * protected void startSelection(int X, int Y) { if (!mSelectionMode) {
	 * mSelectionStarted = false; mSelectionMode = true; selX = X; selY = Y;
	 * eventViewer.setText("Start selection gesture now ..."); } }
	 */

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

	int fcX = 0, fcY = 0;

	int mPrevMoveX = 0, mPrevMoveY = 0;
	boolean mMoveFrozen = false;

	boolean mForwardTouch = false;
	boolean mMenuDown = false;
	int mOldTouchCount = 0;

	private Runnable longTouchRunnable;
	private boolean mLongTouchCheck;
	private long lastTouchTime = -1;

	boolean mLongTouchHack = false;
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

		if (mIsDisabled)
			return false;

		fcX = -(int) pointer.getScrollX() + -(int) getScrollX() + w / 2;
		fcY = -(int) pointer.getScrollY() + -(int) getScrollY() + h / 2;

		Log.v("out", "fcX_1: " + fcX);

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

			// Double tap
			if (!mSoftKeyboardVisible) {
				long thisTime = System.currentTimeMillis();
				if (thisTime - lastTouchTime < 250) {

					// this.getController().zoomInFixing((int) ev.getX(), (int)
					// ev.getY());
					lastTouchTime = -1;
					if (isCircularZoomEnabled())
						disableCircularZoom();
					else {

						toggleMenuVisibility();
						// Should return true here so touch event will not be
						// handled again by CircularLayout (in parking mode).
						return true;
					}
				} else {
					// Too slow :)
					lastTouchTime = thisTime;
				}
			}

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

					handler.postDelayed(longTouchRunnable, 500);
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
				if (mParent.isInParkingMode) {
					mParent.exitParkingMode();
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

				scrollX *= (radFact / length);
				scrollY *= (radFact / length);

				pointer.scrollTo(scrollX, scrollY);
				fcPointerView.scrollTo(-scrollX, -scrollY);

				pointerScrollX = scrollX;
				pointerScrollY = scrollY;

				updateFC();

				// stopSelection(fcX, fcY);
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
					mParent.stopGesture();
					mParent.stopTextGesture();
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
				 * WebHitTestResult.TEXT_TYPE) { mParent.startTextGesture(); }
				 * else { clickSelection(fcX, fcY); }
				 */
				onTouchUp();

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
		mWebView.zoom(fcX, fcY, 1.25f);
	}

	public void circularZoomOut() {
		mWebView.zoom(fcX, fcY, 0.8f);
	}

	public void circularZoom(float zoomVal) {
		mWebView.zoom(fcX, fcY, zoomVal);
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

		if (mParent.getPackageManager().resolveActivity(intent, 0) == null)
			return;

		// Security settings - Sanitize access
		intent.addCategory(Intent.CATEGORY_BROWSABLE);
		intent.setComponent(null);

		try {
			mParent.startActivity(intent);
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
		mParent.setActiveWebViewIndex(mParent.getActiveWebViewIndex() - 1);
		// fcWindowTabs.setCurrentTab(fcWindowTabs.getCurrentTab()+1);
		TabButton child = (TabButton) fcWindowTabs.findViewById(mParent
				.getActiveWebViewIndex());
		fcWindowTabs.setCurrentTab(child.getTabIndex());
		//fcWindowTabs.setActiveTabIndex(child);

	}

	public void prevWebPage() {
		mParent.setActiveWebViewIndex(mParent.getActiveWebViewIndex() + 1);
		// fcWindowTabs.setCurrentTab(fcWindowTabs.getCurrentTab()-1);
		TabButton child = (TabButton) fcWindowTabs.findViewById(mParent
				.getActiveWebViewIndex());
		fcWindowTabs.setCurrentTab(child.getTabIndex());
		fcWindowTabs.setActiveTabIndex(child);
	}

	boolean byPassTop;

	/*
	 * WebView scrolling with FloatingCursor
	 */
	public void scrollWebView(int speed, int direction, int _fcX, int _fcY) {

		/*
		 * direction = 0 if web view scrolls along X axis direction = 1 if web
		 * view scrolls along Y axis
		 */

		int sx = mWebView.getScrollX();
		int sy = mWebView.getScrollY();

		final int xLoc = this.getScrollX();
		final int yLoc = this.getScrollY();

		int dims[] = mParent.getDeviceWidthHeight();
		int coords[] = mParent.getFCLocation(xLoc, yLoc, dims[0], dims[1]);

		if (direction == 0) { // X AXIS

			/**
			 * Check bug here while scrolling to the bottom.
			 */

			int WebViewWidth = mWebView.getContentWidth();
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

	public class WebClient extends WebChromeClient // implements
													// OnCompletionListener,
													// OnErrorListener
	{

		/*
		 * @Override public void onShowCustomView(View view, CustomViewCallback
		 * callback) { super.onShowCustomView(view, callback); if (view
		 * instanceof FrameLayout){ FrameLayout frame = (FrameLayout) view; if
		 * (frame.getFocusedChild() instanceof VideoView){ VideoView video =
		 * (VideoView) frame.getFocusedChild(); frame.removeView(video);
		 * mParent.setContentView(video); video.setOnCompletionListener(this);
		 * video.setOnErrorListener(this); video.start(); } } }
		 * 
		 * public void onCompletion(MediaPlayer mp) { Log.d(TAG,
		 * "Video completo"); mParent.setContentView(R.layout.main); WebView wb
		 * = (WebView) mParent.findViewById(R.id.webView);
		 * mParent.refreshWebView(); }
		 */

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
								public void onClick(DialogInterface dialog,
										int which) {
									mWebView.setListBoxChoices(
											listView.getCheckedItemPositions(),
											adapter.getCount(), false);
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
									mWebView.setListBoxChoice(-2, true);
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
							mWebView.setListBoxChoice((int) id, false);
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
						mWebView.setListBoxChoice(-2, true);
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
				mParent.startGesture(true);
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

	public boolean searchVideo;
	public boolean searchImage;
	public String currentSearch;

	public void setSearchVideo(boolean searchVideo) {
		this.searchVideo = searchVideo;
	}

	public void setSearchImage(boolean searchImage) {
		this.searchImage = searchImage;
	}

	public void setCurrentSearch(String currentSearch) {
		this.currentSearch = currentSearch;
	}

	private class GestureWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			if (url.startsWith("geo:")) {

				mParent.openMap(url);

			} else if (url.startsWith("mailto:")) {

				mParent.sendMail(url);

			} else {

				view.loadUrl(url);
				fcProgressBar.enable();

			}

			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap b) {

			if (url.startsWith("file:///") && url.contains("box=#")) {

				String entry = currentSearch;

				if (searchVideo) {

					url = SwifteeApplication.getYouTubeSearch() + entry;
					searchVideo = false;

				} else if (searchImage) {

					url = SwifteeApplication.getImageSearch() + entry;
					searchImage = false;

				} else if (entry.contains(".")) { // TODO: Match domain
													// extension list here.

					if (!entry.contains("http://")) {
						entry = "http://" + entry;
					}
					url = entry;

				} else {

					url = SwifteeApplication.getGoogleSearch() + entry;

				}
				view.loadUrl(url);
				fcProgressBar.enable();
				currentSearch = null;
			}

			mIsLoading = true;
			if (!mParent.isInParkingMode) {
				fcView.startScaleDownAndRotateAnimation(1000);
			} else {
				fcView.startRotateAnimation();
			}
			fcMainMenu.toggleCloseORRefresh(false);
		}

		@Override
		public void onPageFinished(WebView view, String url) {

			fcView.stopAllAnimation(); // Stop 'loading' animation
			mIsLoading = false;

			if (!mParent.isInParkingMode) {
				fcView.startScaleUpAnimation(1000); // Restore original size if
													// not in parking mode

				// Here we start the timer to park the PadKite.
				handler.removeCallbacks(parkingRunnable);
				handler.postDelayed(parkingRunnable, 400);
			}

			fcMainMenu.toggleCloseORRefresh(true);
			mContentWidth = view.getContentWidth();
			mContentHeight = view.getContentHeight();

			BitmapDrawable bd = new BitmapDrawable(getCircleBitmap(view));
			fcWindowTabs.setHotThumbnail(bd, view);
			fcWindowTabs.setCurrentThumbnail(bd, view);

			fcMainMenu.setBackEabled(view.canGoBack());
			fcMainMenu.setFwdEabled(view.canGoForward());

			if (url.startsWith("file:///android_asset/Web%20Pages/history.html")) {
				int start = 0;
				String[] parts = url.split("\\?", 2);
				try {
					String[] t = parts[1].split("\\=", 2);
					if (t[0].equals("start"))
						start = Integer.parseInt(t[1]);
				} catch (Exception e) {

				}
				WebPage page = new WebPage();
				loadData(page.getBrowserHistory(mParent, parts[0], start));

			} else if (url
					.startsWith("file:///android_asset/Web%20Pages/download.html")) {
				int start = 0;
				String[] parts = url.split("\\?", 2);
				try {
					String[] t = parts[1].split("\\=", 2);
					if (t[0].equals("start"))
						start = Integer.parseInt(t[1]);
				} catch (Exception e) {

				}
				WebPage page = new WebPage();
				loadData(page.getDownloadHistory(mParent, url, start));
			}

			// Send Javascript for proxy bridge to identify page. webview.
			final String currentWeb = "javascript:"
					+ "pBridge.currentPage(theUrl);";
			view.loadUrl(currentWeb);

			/*
			 * if (url.startsWith("file:////mnt/sdcard/PadKite/")){ if
			 * (url.contains("loadPage.html")){ mParent.landingLoaded = true; }
			 * }
			 */
			// "file:////mnt/sdcard/PadKite/Web%Assets/loadPage.html"
		}

		public Bitmap getCircleBitmap(WebView view) {
			Picture thumbnail = view.capturePicture();
			if (thumbnail == null) {
				return null;
			}
			Bitmap bm = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_4444);

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
				// Log.d("---History--------", "url = "+url+"  Title ="+
				// view.getTitle());
				dbConnector.addToHistory(System.currentTimeMillis() + "", url,
						view.getTitle(), 1);
			}
		}

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

}
