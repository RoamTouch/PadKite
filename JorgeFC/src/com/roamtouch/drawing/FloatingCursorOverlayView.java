package com.roamtouch.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.roamtouch.activity.R;

public class FloatingCursorOverlayView extends FrameLayout {

	/**
	 * Cursor paint
	 */
	private Paint pointerPaint;
	private Paint circlePaint;
	
	/**
	 * Initial cursor coordinates
	 */
	private float cursorx=50,cursory=100;
	
	/**
	 * Holds the current cursor image
	 */
	private int cursorImage = R.drawable.no_target_cursor;
	
	/**
	 * Flag that indicates whether the cursor should be draw on the canvas
	 */
	private boolean cursorEnabled;
	private boolean isTouching = false;
	
	private final float RADIUS_DIP = 96; // 96dip=15mm, 192dip=30mm expressed in DIP
	final float scale = getContext().getResources().getDisplayMetrics().density;
	final int RADIUS = (int) (RADIUS_DIP * scale + 0.5f); //Converting to Pixel
	final int RADIUS_SQUARE = RADIUS * RADIUS;  //radius square
	
	
	/**
	 * Initialize the cursor paint
	 */
	private void init() {
		pointerPaint = new Paint();
		pointerPaint.setARGB(255, 75, 75, 75);
		pointerPaint.setAntiAlias(true);
		pointerPaint.setStrokeWidth(2.0f);

		circlePaint = new Paint();
		circlePaint.setARGB(100, 75, 75, 75);
		circlePaint.setAntiAlias(true);
		circlePaint.setStrokeWidth(2.0f);

		
	}

	public FloatingCursorOverlayView(Context context) {
		super(context);
		init();

	}

	public FloatingCursorOverlayView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public FloatingCursorOverlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		final boolean dontScrollView = true;
		
		// This needs to be more robust
		//    right now only ACTION_MOVE is taken into account
		
		//final boolean isActionMove = event.getAction() == MotionEvent.ACTION_MOVE;
		
		final int eventType = event.getAction();
		
		if (isCursorEnabled()) {			
			
			Log.i("MotionEvent(event,x,y)","("+String.valueOf(event.getAction())+","+String.valueOf(cursorx) +","+ String.valueOf(cursory)+")");					
			
			if (isTouching){
				updateFloatingCursorCoordinates(event);
				invalidate();
			}
			
			//  What kind of touch event do we have:			
			switch (eventType) {
				case MotionEvent.ACTION_DOWN: {					
					if (!isTouching && spacing(event)<=RADIUS_SQUARE){										
						isTouching = true;
					}
					break;
				}
				case MotionEvent.ACTION_MOVE: {
					if (isTouching){
						fakeActionDown(event);						
					}
					break;
				}
				case MotionEvent.ACTION_UP: {
					if (isTouching){
						isTouching = false;	
					}
					break;
				}			
			}			
																
			return isTouching ? dontScrollView : super.dispatchTouchEvent(event);
		}

		return super.dispatchTouchEvent(event);
	}

	/**
	 * Sends a <code>MotionEvent.ACTION_DOWN</code> to the below components when dispatching
	 * <p>
	 * With this we are able to detect the <code>getHitTestResult</code> for <code>MyWebView</code>, 
	 * and get focus for <code>UrlView</code>
	 * @param event
	 */
	private void fakeActionDown(MotionEvent event) {
		
		event.setAction(MotionEvent.ACTION_DOWN); //simulate down in order to get focus, and hittestresult
		super.dispatchTouchEvent(event); //para que lo propague a lo de abajo
	}

	/**
	 * Updates the coordinates
	 * 
	 * @param event
	 */
	private void updateFloatingCursorCoordinates(MotionEvent event) {
		
		cursorx = event.getX();
		cursory = event.getY();
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {

		super.dispatchDraw(canvas);
		
		if (isCursorEnabled()) {
			final Bitmap mBitmap = BitmapFactory.decodeResource(getContext().getResources(), cursorImage);
			canvas.drawCircle(cursorx, cursory, RADIUS, circlePaint);
			
			//We need the calc. the coordinates to center the cursor.
			final int cCursorx = (int)cursorx - (mBitmap.getHeight()/2); 
			final int cCursory = (int)cursory - (mBitmap.getWidth()/2);
			
			//lets draw the cursor
			canvas.drawBitmap(mBitmap, cCursorx, cCursory, pointerPaint);
		}
	}

	/**
	 * Check if the cursor is enabled
	 * 
	 * @return true if cursor is enabled to be draw on the canvas, false otherwise
	 */
	public boolean isCursorEnabled() {
		return cursorEnabled;
	}

	/**
	 * Set the cursor enabled
	 * <p>
	 * Indicates if the cursor should be draw on the canvas
	 * 
	 * @param cursorEnabled true if the cursor should be enabled on the canvas, false otherwise
	 */
	public void setCursorEnabled(boolean cursorEnabled) {
		this.cursorEnabled = cursorEnabled;
		invalidate();
	}

	public int getCursorImage() {
		return cursorImage;
	}

	public void setCursorImage(int cursorImage) {
		this.cursorImage = cursorImage;
	}
	
	private int spacing(MotionEvent event) {
		   int x = (int) (event.getX() - cursorx);
		   int y = (int) (event.getY() - cursory);
		   return (x * x + y * y);
	}

}
