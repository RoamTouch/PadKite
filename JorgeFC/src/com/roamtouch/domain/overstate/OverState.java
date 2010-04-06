package com.roamtouch.domain.overstate;

import com.roamtouch.domain.gesture.GestureType;

/**
 * Implements the State pattern
 * 
 * Abstracts the different states of a <code>cursor</code>, to filter the available gestures
 * 
 * @author jorge.bo
 *
 */
public abstract class OverState {
	
	public void doGesture(final String payLoad,final GestureType gestureType) {
		handleCommonGesture(payLoad,gestureType);
		handleSpecificGesture(payLoad,gestureType);
	}

	//TODO Implement this method
	private void handleCommonGesture(final String payLoad,GestureType gestureType) {
	}

	/**
	 * Template method pattern to handle actions for an specific gesture
	 * 
	 * @param payLoad the data to operate on
	 * @param gestureType the gesture type
	 */
	protected abstract void handleSpecificGesture(String payLoad, GestureType gestureType);
}
