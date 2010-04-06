package com.roamtouch.domain.sliding.strategies;

import com.roamtouch.domain.sliding.Coordinates;
import com.roamtouch.domain.sliding.Slider;

/**
 * Return the slidedCoordinate as the new <code>slider</code> coordinates
 * 
 * @see SlidingStrategy
 * 
 * @author jorge.bo
 */
public class AbsoluteSlidingStrategy extends SlidingStrategy {

	/**
	 * Factory method for <code>AbsoluteSlidingStrategy</code>
	 * 
	 * @return the new <code>AbsoluteSlidingStrategy</code>
	 */
	public static AbsoluteSlidingStrategy make() {
		final AbsoluteSlidingStrategy newAbsoluteSlidingStrategy = new AbsoluteSlidingStrategy();
		return newAbsoluteSlidingStrategy;
	}
	
	/**
	 * Implement this algorithm
	 */
	@Override
	protected Coordinates calculateNewCoordinates(final Slider slider,final Coordinates slidedCoordinates) {
		return slidedCoordinates;
	}

}
