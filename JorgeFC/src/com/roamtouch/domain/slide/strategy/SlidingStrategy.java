package com.roamtouch.domain.slide.strategy;

import com.roamtouch.domain.slide.pointer.SlidingPointer;
import com.roamtouch.domain.slide.position.Coordinates;

/**
 * Define the algorithm to slide a <code>SlidingPointer</code>
 * <p>
 * Sliding strategies used the <code>fingerCoordinates</code> to calculate the new <code>slidingPointer</code>
 * coordinates.
 * It lets the real algorithm implementation to its descendant classes, where they will use this information
 * whenever it is useful
 *
 * @see AbsoluteSlidingStrategy
 * @see TraslationSlidintStrategy
 *  
 * @author jorge.bo
*/
public abstract class SlidingStrategy {

	/**
	 * Calculate the new <code>coordinates</code> of the <code>slidingPointer</code>
	 * 
	 * @param slidingPointer the sliding pointer to update its coordinates
	 * @param fingerCoordinates the finger/mouse pointing device coordinates
	 * 
	 * @return the updated coordinates
	 */
	public Coordinates slide(final SlidingPointer slidingPointer,final Coordinates fingerCoordinates) {

		final Coordinates calculatedCoordinate = calculateNewCoordinates(slidingPointer,fingerCoordinates);
		//TODO: calculateCollisionCoordinates 
		
		return calculatedCoordinate;
	}
	
	/**
	 * Implements the algorithm
	 * 
	 * @param slidingPointer the sliding pointer
	 * @param fingerCoordinates the finger coordinates
	 * @return
	 */
	protected abstract Coordinates calculateNewCoordinates(final SlidingPointer slidePointer,final Coordinates fingerCoordinates);

}
