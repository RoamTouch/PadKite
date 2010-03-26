package com.roamtouch.drawing;

import com.roamtouch.activity.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class UrlView extends EditText {

	private FloatingCursorOverlayView drawing;

	public UrlView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FloatingCursorOverlayView getDrawing() {
		return drawing;
	}

	public void setDrawing(FloatingCursorOverlayView drawing) {
		this.drawing = drawing;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		drawing.setCursorImage(R.drawable.icon);
		return super.onTouchEvent(event);
	}
}
