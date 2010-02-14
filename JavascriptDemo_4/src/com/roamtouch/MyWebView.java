package com.roamtouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

public class MyWebView extends WebView {

	private Paint transparentPaint;

	private Paint pointerPaint;

	public float cx=10.0f, cy=10.0f;

	/**
	 * Initialize the color of the paints
	 */
	private void makePaints() {

		pointerPaint = new Paint();
		pointerPaint.setARGB(255, 75, 75, 75);
		pointerPaint.setAntiAlias(true);
		pointerPaint.setStrokeWidth(2.0f);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {

		Log.i("HitTest", String.valueOf(getHitTestResult().getType()));
		
		switch (getHitTestResult().getType()) {

		case HitTestResult.ANCHOR_TYPE: {
			pointerPaint.setARGB(255, 255,0 , 0);
			break;
		}

		case HitTestResult.EDIT_TEXT_TYPE: {
			pointerPaint.setARGB(255, 0, 255,0);
			break;
		}
		case HitTestResult.EMAIL_TYPE: {
			pointerPaint.setARGB(255, 0, 0, 255);
			break;

		}
		case HitTestResult.IMAGE_ANCHOR_TYPE: {
			pointerPaint.setARGB(255, 255, 255, 0);
			break;
		}
		case HitTestResult.IMAGE_TYPE: {
			pointerPaint.setARGB(255, 255, 0, 255);
			break;
		}
		case HitTestResult.PHONE_TYPE: {
			pointerPaint.setARGB(255, 0, 255, 255);
			break;
		}
		case HitTestResult.SRC_ANCHOR_TYPE: {
			pointerPaint.setARGB(255, 0, 75, 10);
			break;
		}
		case HitTestResult.SRC_IMAGE_ANCHOR_TYPE: {
			pointerPaint.setARGB(255, 4, 5, 5);
			break;
		}
		case HitTestResult.UNKNOWN_TYPE: {
			pointerPaint.setARGB(255, 75, 75, 75);
			break;
		}
		}
		
		canvas.drawCircle(cx, cy, 10.0f, pointerPaint);
		
		
		super.dispatchDraw(canvas);
	}


	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		makePaints();
	}

}
