package com.roamtouch.domain.slide.strategy;

import com.roamtouch.domain.slide.pointer.SlidingPointer;
import com.roamtouch.domain.slide.position.Coordinates;

/**
 * Return the fingerCoordinate as the new <code>slidingPointer</code> coordinates
 * 
 * @see SlidingStrategy
 * 
 * @author jorge.bo
 */
public class AbsoluteSlidingStrategy extends SlidingStrategy {

	/**
	 * Implement this algorithm
	 */
	@Override
	protected Coordinates calculateNewCoordinates(final SlidingPointer slidePointer,final Coordinates fingerCoordinates) {
		
		return fingerCoordinates;
	}

}
