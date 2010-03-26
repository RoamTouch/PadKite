package com.roamtouch.drawing;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebView;

import com.roamtouch.activity.R;

public class MyWebView extends WebView {

	private FloatingCursorOverlayView drawing;

	private int noTargetCursor = R.drawable.no_target_cursor;

	
	public MyWebView(Context context, AttributeSet attrs) {
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

		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			int mHitTestResult = getHitTestResult().getType();
			Log.i("hitTestResult", String.valueOf(mHitTestResult));
			Log.i("MyWebView - MotionEvent(event,x,y)","("+String.valueOf(event.getAction())+","+String.valueOf(event.getX()) +","+ String.valueOf(event.getY())+")");

		switch (mHitTestResult) {

			case HitTestResult.ANCHOR_TYPE: {
				noTargetCursor = R.drawable.link_cursor;
				Log.i("hitTestResult", "ANCHOR_TYPE");
				break;
			}
			
			case HitTestResult.SRC_ANCHOR_TYPE: {
				noTargetCursor = R.drawable.link_cursor;
				Log.i("hitTestResult", "SRC_ANCHOR_TYPE");
				break;
			}
	
			case HitTestResult.IMAGE_ANCHOR_TYPE: {
				noTargetCursor = R.drawable.image_cursor;
				Log.i("hitTestResult", "IMAGE_ANCHOR_TYPE");
				break;
			}
	
			case HitTestResult.IMAGE_TYPE: {
				noTargetCursor = R.drawable.image_cursor;
				Log.i("hitTestResult", "IMAGE_TYPE");
				break;
			}
			default: {
				Log.i("DESCONOCIDO",String.valueOf(mHitTestResult));
				noTargetCursor = R.drawable.no_target_cursor;
				break;
			}
		}
		}
			drawing.setCursorImage(noTargetCursor);
			return super.onTouchEvent(event);
	}
}
