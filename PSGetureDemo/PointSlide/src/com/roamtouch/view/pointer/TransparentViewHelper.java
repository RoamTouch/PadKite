package com.roamtouch.view.pointer;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Helps in determine which view under a transparent layer was selected by an event touch
 * 
 * @author jorgebo
 *
 */
public class TransparentViewHelper implements DelegateHelper {
	
	/**
	 * Returns the hit rectangle for a view
	 * 
	 * @param view the view
	 * @return the hit rect
	 */
	private Rect getHitRect(View view){
		
		final Rect hitRect = new Rect();
		view.getHitRect(hitRect);
		
		Log.i("HitRect",hitRect.toShortString());
		
		return hitRect;
	}
	
	/**
	 * Check if the event coordinates are in the view's hit rectangle
	 * 
	 * @param hitRect the view hit rectangle
	 * @param event the event
	 * @return true if the event coordinates are within the hit rectangle false otherwise
	 */
	private boolean isEventInHitRect(Rect hitRect,MotionEvent event){
		return hitRect.contains((int)event.getX(),(int)event.getY());
	}
	
	/**
	 * Update the offset of an event
	 * <p>
	 * This method intents to adjust the events coordinates so it can 
	 * be forwarded as the real/effective event in dispatching the onTouch's 
	 * view event
	 * 
	 * @param event the event to change its coordinates
	 * @param deltax the amount on x axis
	 * @param deltay the amount on y axis
	 * @return the updated event
	 */
	private MotionEvent scrollEvent(MotionEvent event,float deltax,float deltay) {
		
		final MotionEvent scrolledEvent = MotionEvent.obtain(event);
		scrolledEvent.offsetLocation(deltax,deltay);
		
		Log.i("ScrolledEvent",scrolledEvent.toString());

		return scrolledEvent;
	}

	
	@Override
	public DelegatedView delegateTouch(final MotionEvent event,final View[] views) {
		
		for (final View view : views) {
			if (isEventInHitRect(getHitRect(view),event)) {
				return DelegatedView.make(view,scrollEvent(event,0,-view.getTop()));
			}
		}
		return null;
	}
}
