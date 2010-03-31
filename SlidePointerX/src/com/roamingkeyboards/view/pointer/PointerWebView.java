package com.roamingkeyboards.view.pointer;


import com.roamingkeyboards.activity.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;

public class PointerWebView extends WebView {


	private Paint pointerPaint;

	public float cx = 10.0f, cy = 10.0f;
//	public float fx = 10.0f, fy = 10.0f;  // f for finger
//	public float oldFx = 10.0f, oldFy = 10.0f;  // F for finger

	private int mHitTestResult;
	private boolean cursorVisible = false;
	private boolean selectingText = false;
	
	final int SELECTING_TEXT = -1;

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

		int cursorImage = 0;

		switch (mHitTestResult) {

			case SELECTING_TEXT:{
				cursorImage = R.drawable.text_cursor;
				break;				
			}
		
			case HitTestResult.ANCHOR_TYPE: {
				cursorImage = R.drawable.link_cursor;
				break;
			}

			case HitTestResult.EDIT_TEXT_TYPE: {
				cursorImage = R.drawable.text_cursor;
				break;
			}
			
			case HitTestResult.SRC_ANCHOR_TYPE: {
				cursorImage = R.drawable.link_cursor;
				break;
			}
	
			case HitTestResult.IMAGE_ANCHOR_TYPE: {
				cursorImage = R.drawable.image_cursor;
				break;
			}
	
			case HitTestResult.IMAGE_TYPE: {
				cursorImage = R.drawable.image_cursor;
				break;
			}
			default: {
				Log.i("DESCONOCIDO",String.valueOf(mHitTestResult));
				cursorImage = R.drawable.no_target_cursor;
				break;
			}
		}


		if ( cursorVisible )
		{	
			final Bitmap mBitmap = BitmapFactory.decodeResource(getContext().getResources(), cursorImage);
			canvas.drawBitmap(mBitmap, cx, cy, pointerPaint);			
		}
	}

	public void toggleCursorVisibility()
	{
    	cursorVisible = !cursorVisible;
	}
	
	public void toggleSelectingText()
	{
		if (mHitTestResult == -1)
		{
			mHitTestResult = 0;
		} else {
			mHitTestResult = -1;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		//if -1 we suspend cursor switching
		if (mHitTestResult != -1)
		{
			mHitTestResult = getHitTestResult().getType();


			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				event.setAction(MotionEvent.ACTION_DOWN);
				invalidate();
			}
		}
		
		return super.onTouchEvent(event);
	}

	public PointerWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		makePaints();
	}
	
	public void updateCoordinates(MotionEvent event, float cx, float cy){	
		
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	//we need to set oldF to this location, and ignore the change
            	event.setLocation(cx, cy);	
                invalidate();        		
                break;
            case MotionEvent.ACTION_MOVE:
//    			TODO: need to change cx, cy for the displaced location of the event
            	this.cx = cx;
            	this.cy = cy;
            	event.setLocation(cx, cy);		
    			invalidate();
                break;
            case MotionEvent.ACTION_UP:
                invalidate();
                break;
        }		        
        	
	}	

}
