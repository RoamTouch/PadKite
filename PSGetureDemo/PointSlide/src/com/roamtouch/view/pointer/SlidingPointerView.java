package com.roamtouch.view.pointer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roamtouch.domain.slide.pointer.SlidingPointer;
import com.roamtouch.domain.slide.position.Coordinates;
import com.roamtouch.domain.slide.strategy.TraslationSlidingStrategy;

/**
 * SlidingPointerView is transparent view that overlays over the following components: 
 * a TextView used to enter url and a WebView where the content is rendered.
 * Besides will receive every event the user does on the app, and will dispatch this event
 * to the underlying components so they can handle this in an unobstrusive way.
 * Finally every picture such as the sliding pointer will be draw over this layer.
 * 
 * @author jorgebo
 */
public class SlidingPointerView extends LinearLayout {

	private TextView url;
	
	private Button go;
	
	private WebView webView;

	private SlidingPointer slidingPointer;
	
	private Paint transparentPaint;
	
	private Paint pointerPaint;
	
	private DelegateHelper delegate;
	
	private boolean showPointer;
	
	public boolean isShowPointer() {
		return showPointer;
	}

	public void setShowPointer(boolean showPointer) {
		this.showPointer = showPointer;
	}

	/**
	 * Draw a transparent panel/overlay over the complete screen
	 * 
	 * @param canvas the drawing canvas
	 * @return the RectF draw
	 */
	private RectF drawTransparentRect(Canvas canvas) {
		
		final RectF transparentRect = new RectF();
		transparentRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
		canvas.drawRoundRect(transparentRect, 5, 5, transparentPaint);
		return transparentRect;
	}
	
	/**
	 * Initialize the color of the paints
	 */
	private void makePaints() {

		transparentPaint = new Paint();
		transparentPaint.setARGB(0, 100, 75, 75);
		transparentPaint.setAntiAlias(true);
		
		pointerPaint = new Paint();
		pointerPaint.setARGB(255, 75, 75, 75);
		pointerPaint.setAntiAlias(true);
		pointerPaint.setStrokeWidth(2.0f);
	}

	public SlidingPointerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		makePaints();
		this.delegate = new TransparentViewHelper();
	}

	private void dispatchAndFocusEvent(View view,MotionEvent event) {
		view.dispatchTouchEvent(event);
		view.requestFocus();
	}
	
	private Coordinates captureClickCoordinates(MotionEvent event) {
		return Coordinates.make(event.getX(),event.getY());
	}
	
	//TODO:This method is not so clear, needs some refactoring, however it still is  usefull for testing
	@Override
    public boolean onTouchEvent(MotionEvent event) {

		Log.i("event",String.valueOf(event.getAction()));

		final Coordinates currentFingerCoordinates = captureClickCoordinates(event);
		
		if (showPointer) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				slidingPointer.setSlideStrategy(new TraslationSlidingStrategy(currentFingerCoordinates));
			}
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				slidingPointer.updateCoordinates(currentFingerCoordinates);
			}
		}
		

		final DelegatedView delegatedView = delegate.delegateTouch(event, new View[] {webView,url,go});
		
		if(showPointer) {
			delegatedView.getEvent().setLocation(slidingPointer.getXCoordinate(),slidingPointer.getYCoordinate()-webView.getTop());
		}
		
		dispatchAndFocusEvent(delegatedView.getView(),delegatedView.getEvent());
		
		invalidate();
		return true;
	}

	
	@Override
	protected void dispatchDraw(Canvas canvas) {

		
		drawTransparentRect(canvas);
		
		if (showPointer) {
			canvas.drawPoint(slidingPointer.getXCoordinate(),slidingPointer.getYCoordinate(), pointerPaint);
		}
		
		super.dispatchDraw(canvas);
	}

	public WebView getWebView() {
		return webView;
	}

	public void setWebView(WebView webView) {
		this.webView = webView;
	}

	public TextView getUrl() {
		return url;
	}

	public void setUrl(TextView url) {
		this.url = url;
	}
	
	public Button getGo() {
		return go;
	}

	public void setGo(Button go) {
		this.go = go;
	}
	
	public SlidingPointer getSlidingPointer() {
		return slidingPointer;
	}

	public void setSlidingPointer(SlidingPointer slidingPointer) {
		this.slidingPointer = slidingPointer;
	}

}
