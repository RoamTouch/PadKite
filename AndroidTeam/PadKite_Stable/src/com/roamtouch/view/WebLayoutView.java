package com.roamtouch.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class WebLayoutView extends FrameLayout {
	
	public WebLayoutView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public WebLayoutView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public WebLayoutView(Context context) {
		super(context);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		if (!this.isEnabled())
			return false;
		
		return super.dispatchTouchEvent(event);
	}

}
