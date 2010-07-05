package com.roamtouch.view;

import com.roamtouch.floatingcursor.FloatingCursor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class SwifteeOverlayView extends FrameLayout {

	protected FloatingCursor m_floatingCursor = null;
	
	public SwifteeOverlayView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public SwifteeOverlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public SwifteeOverlayView(Context context) {
		super(context);
	}
	
	// Getter / Setter functions
	
	public void setFloatingCursor(FloatingCursor floatingCursor)
	{
		m_floatingCursor = floatingCursor;
	}
	
	public FloatingCursor getFloatingCursor()
	{
		return m_floatingCursor;
	}
	
	// Handle Touch Events

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		//Toast.makeText(getContext(), "New touch event OV", Toast.LENGTH_SHORT ).show();
		
		//
		// This could be done directly via FC class extending FrameLayout, 
		// however this way we have much more control about what happens. 
		// Which is especially important for forwarding gestures for example.
		
		// Also we do not want FC to be dependent on any android interface.
		
		if (m_floatingCursor != null)
		{

			// Delegate to floatingCursor
			if (m_floatingCursor.dispatchTouchEventFC(event))
				return true;
		}
		
		return super.dispatchTouchEvent(event);
	}
	
	private int q = 0;
	
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_MENU )
		{
			//Toast.makeText(getContext(), "New key event OV", Toast.LENGTH_SHORT ).show();
			q++;
			//m_floatingCursor.setQuality(q);
		}
		return super.dispatchKeyEvent(event);
	}
}
