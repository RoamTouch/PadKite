package com.roamtouch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebView;

public class MyWebView extends WebView {

	private Paint pointerPaint;

	public float cx = 10.0f, cy = 10.0f;

	private int mHitTestResult;

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
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);

		int noTargetCursor = 0;

		switch (mHitTestResult) {

			case HitTestResult.ANCHOR_TYPE: {
				noTargetCursor = R.drawable.link_cursor;
				break;
			}
			
			case HitTestResult.SRC_ANCHOR_TYPE: {
				noTargetCursor = R.drawable.link_cursor;
				break;
			}
	
			case HitTestResult.IMAGE_ANCHOR_TYPE: {
				noTargetCursor = R.drawable.image_cursor;
				break;
			}
	
			case HitTestResult.IMAGE_TYPE: {
				noTargetCursor = R.drawable.image_cursor;
				break;
			}
			default: {
				Log.i("DESCONOCIDO",String.valueOf(mHitTestResult));
				noTargetCursor = R.drawable.no_target_cursor;
				break;
			}
		}

		Log.i("cursorType",String.valueOf(noTargetCursor));

		final Bitmap mBitmap = BitmapFactory.decodeResource(getContext().getResources(), noTargetCursor);

		canvas.drawBitmap(mBitmap, cx, cy, pointerPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		mHitTestResult = getHitTestResult().getType();

		Log.i("hitTestResult", String.valueOf(mHitTestResult));

		cx = event.getX();
		cy = event.getY();
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			event.setAction(MotionEvent.ACTION_DOWN);
			invalidate();
		}

		return super.onTouchEvent(event);
	}

	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		makePaints();
	}

}
