package com.roamtouch.view.pointer;

import android.view.MotionEvent;
import android.view.View;

public interface DelegateHelper {
	
	/**
	 * Determine which one of <code>views</code> should be handle by the occurrence of <code>event</code>
	 * <p>
	 * @param event the event the occurred event
	 * @param views the view to which delegate
	 * @return a delegated view or null if no view should be handle by occurrence of event 
	 */
	public DelegatedView delegateTouch(final MotionEvent event,final View[] views);

}
