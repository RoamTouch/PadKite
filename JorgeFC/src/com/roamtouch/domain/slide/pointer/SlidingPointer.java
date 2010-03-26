package com.roamtouch.domain.slide.pointer;

import com.roamtouch.domain.slide.position.Coordinates;
import com.roamtouch.domain.slide.strategy.SlidingStrategy;
import com.roamtouch.domain.slide.strategy.TraslationSlidingStrategy;

/**
 * Represents a sliding pointer that slides based upon a <code>SlidingStrategy</code> so 
 * its slide algorithm is able to change.
 * 
 * @see SlidingStrategy
 * 
 * @author jorge.bo
 *
 */
public abstract class SlidingPointer {

	/**
	 * Default coordinate to locate the SlidingPointer
	 */
	public static Coordinates DEFAULT_COORDINATES = Coordinates.make(0.0f, 0.0f);
	
	/**
	 * Default sliding strategy to calculate the next location of the SlidingPointer
	 */
	public static SlidingStrategy DEFAULT_STRATEGY = new TraslationSlidingStrategy(DEFAULT_COORDINATES);
	
	private Coordinates currentCoordinates;
	
	private SlidingStrategy slideStrategy;

	/**
	 * Default constructor
	 */
	public SlidingPointer() {
		this(null,null);
	}

	/**
	 * Constructs a SlidingPointer with a current coordinates 
	 * <p>
	 * If <code>currentCooridnates</code> are null it takes DEFAULT_COORDINATES as its value
	 * 
	 * @param currentCoordinates the coordinates to position the SlidingPointer
	 */
	public SlidingPointer(final Coordinates currentCoordinates) {
		this(currentCoordinates,null);
	}

	/**
	 * Constructs a SlidingPointer with a current coordinates and a slide strategy 
	 * <p>
	 * If <code>currentCooridnates</code> are null it takes DEFAULT_COORDINATES as its value,
	 * if <code>slideStrategy</code> is null DEFAULT_STRATEGY is selected
	 * 
	 * @param currentCoordinates the coordinates to position the SlidingPointer
	 * @param slideStrategy the sliding strategy to calculate the SlidingPointer next position
	 */
	public SlidingPointer(final Coordinates currentCoordinates, final SlidingStrategy slideStrategy) {
		
		this.currentCoordinates = currentCoordinates == null ? DEFAULT_COORDINATES : currentCoordinates;
		this.slideStrategy = slideStrategy == null ? DEFAULT_STRATEGY : slideStrategy;
	}


	/**
	 * Update <code>currentCoordinates</code> of this SlidingPointer based on 
	 * the <code>slideStrategy</code> assigned.
	 * <p>
	 * If <code>fingerCoordinates</code> are null <code>currentCoordinates</code>
	 * are not updated 
	 * 
	 * @param fingerCoordinates finger/touch coordinates on the touch device
	 */
	public void updateCoordinates(final Coordinates fingerCoordinates) {
		
		if (fingerCoordinates != null) {
			currentCoordinates = slideStrategy.slide(this,fingerCoordinates);
		}
	}
	
	public float getXCoordinate() {
		return currentCoordinates.getX();
	}

	public float getYCoordinate() {
		return currentCoordinates.getY();
	}


	public SlidingStrategy getSlideStrategy() {
		return slideStrategy;
	}


	public void setSlideStrategy(SlidingStrategy slideStrategy) {
		this.slideStrategy = slideStrategy;
	}
}
