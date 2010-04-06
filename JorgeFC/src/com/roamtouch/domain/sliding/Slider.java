package com.roamtouch.domain.sliding;

import com.roamtouch.domain.sliding.strategies.SlidingStrategy;

/**
 * Slider represents a sliding element that slides based upon a <code>SlidingStrategy</code> 
 * so its slide algorithm is able to change.
 * This is an implementation of the Strategy pattern.
 * 
 * @see SlidingStrategy
 * 
 * @author jorge.bo
 *
 */
public interface Slider {
	float getYCurrentCoordinate();
	float getXCurrentCoordinate();
	void slide(final Coordinates slidedCoordinates);
}
