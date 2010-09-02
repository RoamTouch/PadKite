package com.roamtouch.view;

import roamtouch.webkit.WebView;

import com.roamtouch.floatingcursor.FloatingCursor;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class SelectionGestureView extends FrameLayout {

	protected FloatingCursor mFloatingCursor = null;

	protected EventViewerArea mEventViewer;

	Handler mHandler;
	Runnable mRunnable;
	
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
	
	// Getter / Setter functions
	
	public void setFloatingCursor(FloatingCursor floatingCursor)
	{
		mFloatingCursor = floatingCursor;
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
	
	protected void startAutoSelection(int selectionDir)
	{
		if (mAutoSelectionStarted)
			return;
		mAutoSelectionStarted = true;
		mSelectionDirection = selectionDir;

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
	
	int mSteps = 1;
	
	protected int continueAutoSelection()
	{
		for (int i = 0; i < mSteps; i++)
			runAutoSelection();
		return 100;
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

		// We do assume that we are only enabled when we are over text

		float X = event.getX();
		float Y = event.getY();
		
		// FIXME: Check pointer count thingies ...
		
		//mEventViewer.setText("downX:" + downX + " downY:" + downY + " X:" + event.getX() + " Y:" + event.getY() + " idx:" + event.getPointerCount());
		
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			downX = X;
			downY = Y;
			selectionType = null;
			mEventViewer.setText("Starting selection gesture ...");
			mFloatingCursor.startSelectionCommand();
		}
		else if (event.getAction() == MotionEvent.ACTION_MOVE)
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
					// Text automatic selection
					if (downX-X < 0)
						startAutoSelection(SEL_DIR_RIGHT);
					else
						startAutoSelection(SEL_DIR_LEFT);

					selectionType = SelectionTypes.TextAutomatic;
					mEventViewer.setText("Detected text automatic selection gesture ...");
				}
				else if(deltaY >= TOUCH_TOLERANCE)
				{
					// Line automatic selection
					if (downY-Y < 0)
						startAutoSelection(SEL_DIR_DOWN);
					else
						startAutoSelection(SEL_DIR_UP);

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
		else if (event.getAction() == MotionEvent.ACTION_UP)
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
		else if (event.getAction() == MotionEvent.ACTION_CANCEL)
		{
			downX = -1;
			downY = -1;
		}

		return true;
	}	
}
