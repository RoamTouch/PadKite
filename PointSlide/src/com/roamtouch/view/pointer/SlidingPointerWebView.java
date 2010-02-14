package com.roamtouch.view.pointer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebView;

import com.roamtouch.domain.slide.pointer.SlidingPointer;
import com.roamtouch.domain.slide.position.Coordinates;
import com.roamtouch.domain.slide.strategy.TraslationSlidingStrategy;

public class SlidingPointerWebView extends WebView {

	private boolean slidingPointerEnabled;
	private SlidingPointer slidingPointer;
	private Coordinates currentFingerCoordinates;
	private Paint mBorderPaint;
	private Paint mInnerPaint;
	private boolean slideSlidingPointerEnabled;
	

	public boolean isSlidingPointerEnabled() {
		return slidingPointerEnabled;
	}


	public void setSlidingPointerEnabled(boolean slidingPointerEnabled) {
		this.slidingPointerEnabled = slidingPointerEnabled;
	}


	public SlidingPointer getSlidingPointer() {
		return slidingPointer;
	}


	public void setSlidingPointer(SlidingPointer slidingPointer) {
		this.slidingPointer = slidingPointer;
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		System.out.println("EVENT:" + event.toString());
		currentFingerCoordinates = captureClickCoordinates(event);
		
		if (slidingPointerEnabled) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				slidingPointer.setSlideStrategy(new TraslationSlidingStrategy(currentFingerCoordinates));
				slideSlidingPointerEnabled = false;
				Log.i("Type:",String.valueOf(this.getHitTestResult().getType()));
				Log.i("Extra:",String.valueOf(this.getHitTestResult().getExtra()));
			}
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				slideSlidingPointerEnabled = true;
				Log.i("Type:",String.valueOf(this.getHitTestResult().getType()));
				Log.i("Extra:",String.valueOf(this.getHitTestResult().getExtra()));
			}
		}
		
		return super.onTouchEvent(event);
	}


	private Coordinates captureClickCoordinates(MotionEvent event) {
		return Coordinates.make(event.getX(),event.getY());
	}


	private void makePaints() {

		this.mBorderPaint = new Paint();
		this.mBorderPaint.setARGB(255, 255, 255, 255);
		this.mBorderPaint.setAntiAlias(true);
		this.mBorderPaint.setStyle(Style.STROKE);
		this.mBorderPaint.setStrokeWidth(2);

		this.mInnerPaint = new Paint();
		this.mInnerPaint.setARGB(255, 75, 75, 75);
		this.mInnerPaint.setAntiAlias(true);
	}

	public SlidingPointerWebView(Context context, AttributeSet attrs) {
		super(context, attrs); 
		makePaints();
	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		
		if (slidingPointerEnabled) {
			
			if (slideSlidingPointerEnabled) {
				slidingPointer.updateCoordinates(currentFingerCoordinates);
			}
			
			canvas.drawCircle(slidingPointer.getXCoordinate(), slidingPointer.getYCoordinate(), 10, mBorderPaint);
			canvas.drawCircle(slidingPointer.getXCoordinate(), slidingPointer.getYCoordinate(), 10, mInnerPaint);
		}
		invalidate();
	}
}
