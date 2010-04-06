package com.roamtouch.domain.gesture;

import com.roamtouch.domain.cursor.Cursor;
/**
 * Implementation of <code>Order</code> to handle gestures orders
 * 
 * @author jorge.bo
 *
 */
public class DoGestureOrder implements Order {
	private String payLoad;
	private Cursor cursor;
	private GestureType gestureType;
	
	public static DoGestureOrder makeSearchGestureForCursor(final Cursor cursor,final String payLoad) {
		final DoGestureOrder newGestureOrder = new DoGestureOrder(cursor,payLoad,GestureType.Search);
		return newGestureOrder;
	}

	private DoGestureOrder(final Cursor cursor,final String payLoad,final GestureType gestureType) {
		this.cursor = cursor;
		this.payLoad = payLoad;
		this.gestureType = gestureType;
	}

	@Override
	public void execute() {
		cursor.doGesture(payLoad, gestureType);
	}
}