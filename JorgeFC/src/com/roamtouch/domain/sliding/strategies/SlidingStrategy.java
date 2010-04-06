package com.roamtouch.domain.sliding.strategies;

import com.roamtouch.domain.sliding.Coordinates;
import com.roamtouch.domain.sliding.Slider;

/**
 * Define the algorithm to slide a <code>Slider</code>
 * <p>
 * Sliding strategies used the <code>slidedCoordinates</code> to calculate the new <code>slider</code>
 * coordinates/location.
 * It lets the real algorithm implementation to its descendant classes, where they will use this information
 * whenever it is useful
 *
 * @see AbsoluteSlidingStrategy
 * @see TraslationSlidintStrategy
 *  
 * @author jorge.bo
*/
public abstract class SlidingStrategy {
	
	public Coordinates slide(final Slider slider,final Coordinates slidedCoordinates) {
		final Coordinates calculatedCoordinate = calculateNewCoordinates(slider,slidedCoordinates);
		//TODO: calculateCollisionCoordinates 
		return calculatedCoordinate;
	}
	
	/**
	 * Template method that allows to override the sliding algorithm
	 * 
	 * @param slider the slider to update its coordinates
	 * @param slidedCoordinates the slided coordinates to help in the calculation
	 * @return the new calculated coordinates
	 */
	protected abstract Coordinates calculateNewCoordinates(final Slider slider,final Coordinates slidedCoordinates);
}
