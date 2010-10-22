package com.roamtouch.view;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;

import roamtouch.webkit.WebView;

import com.roamtouch.floatingcursor.FloatingCursor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class SelectionGestureView extends FrameLayout {

	protected FloatingCursor mFloatingCursor = null;

	protected EventViewerArea mEventViewer;

	private Handler mHandler;
	private Runnable mRunnable;
	
	public class DebugView extends View {

		public DebugView(Context context) {
			super(context);
			init();
		}
		
		public DebugView(Context context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}

		public DebugView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			init();
		}

		private Paint mLinePaintTouchPointCircle = new Paint();			

		
		protected void init()
		{
			mLinePaintTouchPointCircle.setColor(Color.BLACK);
			mLinePaintTouchPointCircle.setStrokeWidth(15.0f);
			mLinePaintTouchPointCircle.setStyle(Style.STROKE);
			mLinePaintTouchPointCircle.setAntiAlias(true);
			mLinePaintTouchPointCircle.setStrokeJoin(Paint.Join.ROUND);
			mLinePaintTouchPointCircle.setStrokeCap(Paint.Cap.ROUND);
			mLinePaintTouchPointCircle.setDither(true);
		}
		
		private boolean mShowDebugInfo = true;

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
		
			if (mShowDebugInfo)
				drawMultitouchDebugMarks(canvas);
		}
		
		public void setCurrTouchPoint(PointInfo p, int action)
		{

			if (action == MotionEvent.ACTION_DOWN)
				initTouchPoint.set(p);
			
			currTouchPoint.set(p);
			invalidate();
		}
		
		private PointInfo initTouchPoint = new PointInfo();
		private PointInfo currTouchPoint = new PointInfo();
				
		private void drawMultitouchDebugMarks(Canvas canvas) {
			/*if (currTouchPoint.isDown()) {
				float[] xs = currTouchPoint.getXs();
				float[] ys = currTouchPoint.getYs();
				float[] pressures = currTouchPoint.getPressures();
				int numPoints = Math.min(currTouchPoint.getNumTouchPoints(), 2);
				for (int i = 0; i < numPoints; i++)
					canvas.drawCircle(xs[i], ys[i], 50 + pressures[i] * 80, mLinePaintTouchPointCircle);
				if (numPoints == 2)
					canvas.drawLine(xs[0], ys[0], xs[1], ys[1], mLinePaintTouchPointCircle);
			}*/
			if (currTouchPoint.isDown())
			{
				float[] xsI = initTouchPoint.getXs();
				float[] ysI = initTouchPoint.getYs();
				
				float[] xs = currTouchPoint.getXs();
				float[] ys = currTouchPoint.getYs();

				//canvas.drawCircle(xs[1], ys[1], 50, mLinePaintTouchPointCircle);
				//canvas.drawCircle(xsI[1], ysI[1], 50, mLinePaintTouchPointCircle);

				canvas.drawLine(xsI[1], ysI[1], xs[1], ys[1], mLinePaintTouchPointCircle);
			}
		}		
	}
	
	private DebugView mDebugView;
	
	protected void init(Context context)
	{
		mHandler = new Handler();
		mRunnable = new Runnable() {
			public void run()
			{
				int delayMillis = continueAutoSelection();
				if (delayMillis > 0)
					mHandler.postDelayed(this, delayMillis);
			}
		};
		mDebugView = new DebugView(context);
		addView(mDebugView);
	}
	
	public SelectionGestureView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	public SelectionGestureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public SelectionGestureView(Context context) {
		super(context);
		init(context);
	}
	
	
	// Getter / Setter functions
	
	public void setFloatingCursor(FloatingCursor floatingCursor)
	{
		mFloatingCursor = floatingCursor;
		mFloatingCursor.setSelectionGesture(this);
	}
	
	public FloatingCursor getFloatingCursor()
	{
		return mFloatingCursor;
	}
		
	public void setEventViewer(EventViewerArea eventViewer)
	{
		mEventViewer = eventViewer;
	}
	
	// Handle Touch Events
	
	protected float mDownX = -1, mDownY = -1;
	
	protected long mStartEventTime = 0;
	
	double TOUCH_TOLERANCE = 150.0;

	enum SelectionTypes { Paragraph, TextAutomatic, LineAutomatic, LongTouch };
	
	SelectionTypes selectionType = null;
	
	boolean mAutoSelectionStarted = false;
	final int SEL_DIR_LEFT = 0;
	final int SEL_DIR_RIGHT = 1;
	final int SEL_DIR_DOWN = 2;
	final int SEL_DIR_UP = 3;

	int mSelectionDirection = -1;
	int mSteps = 1;
	int mDelay = 1;
	
	protected void startAutoSelection(int selectionDir, int steps, int delay, boolean restart)
	{
		//Log.i("startAutoSelection", "Params: " + selectionDir + "," + steps + "," + delay);

		if (mAutoSelectionStarted)
		{
			mSelectionDirection = selectionDir;
			mSteps = steps;
			mDelay = delay;
			
			return;
		}
		mFloatingCursor.onAutoSelectionStart(restart);

		mAutoSelectionStarted = true;
		mSelectionDirection = selectionDir;
		mSteps = steps;
		mDelay = delay;

		mHandler.post(mRunnable);
	}
	
	final int MAX_DELAY_X = 300;
	final int MAX_DELAY_Y = 500;

	final int MIN_DELAY = 100;
	
	final int radius = 250;
	
	protected void updateAutoSelection(float X, float Y)
	{
		float deltaX = Math.abs(X - mDownX);
		float deltaY = Math.abs(Y - mDownY);
		
		int direction;
		float speed;
		
		if (deltaX > deltaY)
		{
			if (mDownX-X < 0)
				direction=SEL_DIR_RIGHT;
			else
				direction=SEL_DIR_LEFT;
			if (deltaX > radius)
				deltaX = radius;
			speed = deltaX / radius;
			mDelay = (int)(MAX_DELAY_X * (1-speed));
		}
		else
		{
			if (mDownY-Y < 0)
				direction=SEL_DIR_DOWN;
			else
				direction=SEL_DIR_UP;
			
			if (deltaY > radius)
				deltaY = radius;
			speed = deltaY / radius;
			mDelay = (int)(MAX_DELAY_Y * (1-speed));
		}
		
		mSelectionDirection = direction;

		mSteps = 1;

		if (mDelay == 0)
			mDelay = 1;
		
		if (mDelay < MIN_DELAY)
		{
			mSteps = MIN_DELAY / mDelay;

			if (mSteps > 10)
				mSteps = 10; 
			
			mDelay = MIN_DELAY;
		}

		if (!mAutoSelectionStarted)
			startAutoSelection(mSelectionDirection, mSteps, mDelay, true);
	}

	protected void runAutoSelection()
	{
		switch (mSelectionDirection)
		{
			case SEL_DIR_LEFT:
				mFloatingCursor.executeSelectionCommand(WebView.EXTEND_SELECTION_LEFT);
				break;
			case SEL_DIR_RIGHT:
				mFloatingCursor.executeSelectionCommand(WebView.EXTEND_SELECTION_RIGHT);
				break;
			case SEL_DIR_DOWN:
				mFloatingCursor.executeSelectionCommand(WebView.EXTEND_SELECTION_DOWN);
				break;
			case SEL_DIR_UP:
				mFloatingCursor.executeSelectionCommand(WebView.EXTEND_SELECTION_UP);
				break;
		}
	}
	
	
	protected int continueAutoSelection()
	{
		for (int i = 0; i < mSteps; i++)
			runAutoSelection();
		
		return mDelay;
	}

	protected void stopAutoSelection()
	{
		mFloatingCursor.onAutoSelectionEnd();

		if (!mAutoSelectionStarted)
			return;

		mAutoSelectionStarted = false;
		
		try {
			mHandler.removeCallbacks(mRunnable);
		}
		catch (Exception e)
		{
			; // Do nothing
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		
		if (!this.isEnabled())
			return super.dispatchTouchEvent(event);
		
		return dispatchTouchEventFC(event.getX(), event.getY(), event.getAction(), event.getEventTime());
	}
	
	boolean initPointIsValid = true;
	
	public boolean dispatchTouchEventMT(PointInfo touchPoint, int action) {
		
		/* Wait for settlement code */
		if (action == MotionEvent.ACTION_DOWN)
		{
			initPointIsValid = false;
			return false;
		}
		
		if (!initPointIsValid && touchPoint.isDown())
		{
			action = MotionEvent.ACTION_DOWN;
			initPointIsValid = true;
		}
		
		float[] xs = touchPoint.getXs();
		float[] ys = touchPoint.getYs();

		mDebugView.setCurrTouchPoint(touchPoint, action);
		
		//Log.d("dispatchTouchEventMT", "X,Y,action: " + xs[1] + "," + ys[1] + "," + action);

		return dispatchTouchEventFC(xs[1], ys[1], action, touchPoint.getEventTime());
	}

	public boolean dispatchTouchEventFC(float X, float Y, int action, long eventTime) {

		//mEventViewer.setText("downX:" + downX + " downY:" + downY + " X:" + event.getX() + " Y:" + event.getY() + " idx:" + event.getPointerCount());
		
		if (action == MotionEvent.ACTION_DOWN)
		{
			mDownX = X;
			mDownY = Y;
			mStartEventTime = eventTime;
			selectionType = null;
			mEventViewer.setText("Starting selection gesture ...");
			mFloatingCursor.startSelectionCommand();
		}
		else if (action == MotionEvent.ACTION_MOVE)
		{			
			if (selectionType == null || selectionType == SelectionTypes.LongTouch)
			{
				float deltaX = Math.abs(X - mDownX);
				float deltaY = Math.abs(Y - mDownY);
				
				if (eventTime-mStartEventTime >= 250 && selectionType == null)
				{
					selectionType = SelectionTypes.LongTouch;
					mFloatingCursor.onLongTouch();
				}

				/*if (deltaX >= TOUCH_TOLERANCE && deltaY >= TOUCH_TOLERANCE)
				{
					// Paragraph selection
					selectionType = SelectionTypes.Paragraph;
					mEventViewer.setText("Detected paragraph selection gesture ...");
				}
				else*/ if(deltaX >= TOUCH_TOLERANCE)
				{
					// FIXME: Use config variables
					
					// Text automatic selection
					if (mDownX-X < 0)
						startAutoSelection(SEL_DIR_RIGHT, 1, 700, false);
					else
						startAutoSelection(SEL_DIR_LEFT, 1, 700, false);

					updateAutoSelection(X,Y);
					
					selectionType = SelectionTypes.TextAutomatic;
					mEventViewer.setText("Detected text automatic selection gesture ...");
				}
				else if(deltaY >= TOUCH_TOLERANCE)
				{
					// FIXME: Use config variables
					
					// Line automatic selection
					if (mDownY-Y < 0)
						startAutoSelection(SEL_DIR_DOWN, 1, 700, false);
					else
						startAutoSelection(SEL_DIR_UP, 1, 700, false);
					
					updateAutoSelection(X,Y);

					selectionType = SelectionTypes.LineAutomatic;
					mEventViewer.setText("Detected line automatic selection gesture ...");
				}
			}
			if (selectionType != null)
			{				
				switch (selectionType)
				{
					case Paragraph:
						mFloatingCursor.executeSelectionCommand(WebView.SELECT_PARAGRAPH);
						break;
					case TextAutomatic:
						updateAutoSelection(X, Y);
						//mFloatingCursor.executeSelectionCommand(WebView.EXTEND_SELECTION_RIGHT);
						break;
					case LineAutomatic:
						updateAutoSelection(X, Y);

						//mFloatingCursor.executeSelectionCommand(WebView.EXTEND_SELECTION_DOWN);
						break;
					case LongTouch:
				}
			}
		}
		else if (action == MotionEvent.ACTION_UP)
		{	
/*			mDownX = -1;
			mDownY = -1;*/
			
			if (selectionType == null)
			{
				mFloatingCursor.onClick();
			}
			else if (selectionType == SelectionTypes.LongTouch)
				mFloatingCursor.onLongTouchUp();
			else
				stopAutoSelection();

//				mEventViewer.setText("Selection gesture done: Selecting ...");
//				mFloatingCursor.stopSelectionCommand();
		}
		else if (action == MotionEvent.ACTION_CANCEL)
		{
			mDownX = -1;
			mDownY = -1;
		}

		return true;
	}	
}
