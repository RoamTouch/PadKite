package com.roamtouch.domain.cursor;

import com.roamtouch.domain.gesture.GestureType;
import com.roamtouch.domain.overstate.OverImageState;
import com.roamtouch.domain.overstate.OverLinkState;
import com.roamtouch.domain.overstate.OverNoTargetState;
import com.roamtouch.domain.overstate.OverState;
import com.roamtouch.domain.overstate.OverTextState;
import com.roamtouch.domain.sliding.AbstractSlider;
import com.roamtouch.domain.sliding.Coordinates;
import com.roamtouch.domain.sliding.strategies.AbsoluteSlidingStrategy;
import com.roamtouch.domain.sliding.strategies.SlidingStrategy;

/**
 * Implementation of a <code>Cursor</code>, that can receive <code>DoGestureOrder</code> to make action over it
 *  
 * @author jorge.bo
 */
public class Cursor extends AbstractSlider {
	private OverState overState;
	
	/**
	 * Factory method to make a new <code>Cursor</code>
	 * 
	 * @param coordinates the coordinates where to initially set it
	 * @return a Cursor located in coordinates
	 */
	public Cursor makeAbsoluteSlidingInCoordinates(final Coordinates coordinates) {
		final Cursor newSlidingPointer = new Cursor(coordinates,new AbsoluteSlidingStrategy());
		overState = new OverNoTargetState();
		return newSlidingPointer;
	}

	private Cursor(final Coordinates coordinates, final SlidingStrategy slidingStrategy) {
		super(coordinates,slidingStrategy);
	}

	public void doGesture(String payLoad,GestureType gestureType) {
		overState.doGesture(payLoad,gestureType);
	}
	
	public Cursor isOverLink() {
		overState = new OverLinkState();
		return this;
	}

	public Cursor isOverText() {
		overState = new OverTextState();
		return this;
	}
	
	public Cursor isOverImage() {
		overState = new OverImageState();
		return this;
	}
}
