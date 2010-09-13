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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class SelectionGestureView extends FrameLayout {

	protected FloatingCursor mFloatingCursor = null;

	protected EventViewerArea mEventViewer;

	private Handler mHandler;
	private Runnable mRunnable;
	
	private Paint mLinePaintTouchPointCircle = new Paint();
	
	private boolean mShowDebugInfo = true;

	
	protected void init()
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
		
		mLinePaintTouchPointCircle.setColor(Color.YELLOW);
		mLinePaintTouchPointCircle.setStrokeWidth(5);
		mLinePaintTouchPointCircle.setStyle(Style.STROKE);
		mLinePaintTouchPointCircle.setAntiAlias(true);
	}
	
	public SelectionGestureView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	public SelectionGestureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public SelectionGestureView(Context context) {
		super(context);
		init();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	
		if (mShowDebugInfo)
			drawMultitouchDebugMarks(canvas);
	}
	
	private PointInfo currTouchPoint = new PointInfo();
	
	private void drawMultitouchDebugMarks(Canvas canvas) {
		if (currTouchPoint.isDown()) {
			float[] xs = currTouchPoint.getXs();
			float[] ys = currTouchPoint.getYs();
			float[] pressures = currTouchPoint.getPressures();
			int numPoints = Math.min(currTouchPoint.getNumTouchPoints(), 2);
			for (int i = 0; i < numPoints; i++)
				canvas.drawCircle(xs[i], ys[i], 50 + pressures[i] * 80, mLinePaintTouchPointCircle);
			if (numPoints == 2)
				canvas.drawLine(xs[0], ys[0], xs[1], ys[1], mLinePaintTouchPointCircle);
		}
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
	
	protected float downX = -1, downY = -1;
	
	double TOUCH_TOLERANCE = 16.0;

	enum SelectionTypes { Paragraph, TextAutomatic, LineAutomatic };
	
	SelectionTypes selectionType = null;
	
	boolean mAutoSelectionStarted = false;
	final int SEL_DIR_LEFT = 0;
	final int SEL_DIR_RIGHT = 1;
	final int SEL_DIR_DOWN = 2;
	final int SEL_DIR_UP = 3;

	int mSelectionDirection = -1;
	int mSteps = 1;
	int mDelay = 1;
	
	protected void startAutoSelection(int selectionDir, int steps, int delay)
	{
		if (mAutoSelectionStarted)
			return;
		mAutoSelectionStarted = true;
		mSelectionDirection = selectionDir;
		mSteps = steps;
		mDelay = delay;

		mHandler.post(mRunnable);
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
	
	public boolean dispatchTouchEventMT(PointInfo touchPoint, int action) {
		
		float[] xs = touchPoint.getXs();
		float[] ys = touchPoint.getYs();

		currTouchPoint.set(touchPoint);
		invalidate();
		
		return dispatchTouchEventFC(xs[1], ys[1], action, touchPoint.getEventTime());
	}

		
	public boolean dispatchTouchEventFC(float X, float Y, int action, long eventTime) {

		//mEventViewer.setText("downX:" + downX + " downY:" + downY + " X:" + event.getX() + " Y:" + event.getY() + " idx:" + event.getPointerCount());
		
		if (action == MotionEvent.ACTION_DOWN)
		{
			downX = X;
			downY = Y;
			selectionType = null;
			mEventViewer.setText("Starting selection gesture ...");
			mFloatingCursor.startSelectionCommand();
		}
		else if (action == MotionEvent.ACTION_MOVE)
		{
			if (selectionType == null)
			{
				float deltaX = Math.abs(X - downX);
				float deltaY = Math.abs(Y - downY);

				if (deltaX >= TOUCH_TOLERANCE && deltaY >= TOUCH_TOLERANCE)
				{
					// Paragraph selection
					selectionType = SelectionTypes.Paragraph;
					mEventViewer.setText("Detected paragraph selection gesture ...");
				}
				else if(deltaX >= TOUCH_TOLERANCE)
				{
					// FIXME: Use config variables
					
					// Text automatic selection
					if (downX-X < 0)
						startAutoSelection(SEL_DIR_RIGHT, 10, 100);
					else
						startAutoSelection(SEL_DIR_LEFT, 10, 100);

					selectionType = SelectionTypes.TextAutomatic;
					mEventViewer.setText("Detected text automatic selection gesture ...");
				}
				else if(deltaY >= TOUCH_TOLERANCE)
				{
					// FIXME: Use config variables
					
					// Line automatic selection
					if (downY-Y < 0)
						startAutoSelection(SEL_DIR_DOWN, 1, 700);
					else
						startAutoSelection(SEL_DIR_UP, 1, 700);

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
						//mFloatingCursor.executeSelectionCommand(WebView.EXTEND_SELECTION_RIGHT);
						break;
					case LineAutomatic:
						//mFloatingCursor.executeSelectionCommand(WebView.EXTEND_SELECTION_DOWN);
						break;
				}
			}
		}
		else if (action == MotionEvent.ACTION_UP)
		{	
			downX = -1;			
			downY = -1;
			if (selectionType == null)
			{
				/* FIXME: Run default action */
				mFloatingCursor.executeSelectionCommand(WebView.STOP_SELECTION);
				mFloatingCursor.executeSelectionCommand(WebView.SELECT_WORD_OR_LINK);
			}
			else
				stopAutoSelection();

			mEventViewer.setText("Selection gesture done: Selecting ...");
			mFloatingCursor.stopSelectionCommand();
		}
		else if (action == MotionEvent.ACTION_CANCEL)
		{
			downX = -1;
			downY = -1;
		}

		return true;
	}	
}
