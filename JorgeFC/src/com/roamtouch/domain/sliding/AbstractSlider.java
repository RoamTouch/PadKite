package com.roamtouch.domain.sliding;

import com.roamtouch.domain.sliding.strategies.SlidingStrategy;

/**
 * AbstractSlider represents a sliding element that slides based upon a <code>SlidingStrategy</code> 
 * so its slide algorithm is able to change.
 * This is an implementation of the Strategy pattern.
 * 
 * @see SlidingStrategy
 * 
 * @author jorge.bo
 *
 */
public abstract class AbstractSlider implements Slider {
	private Coordinates currentLocationCoordinates;
	private SlidingStrategy slidingStrategy;

	protected AbstractSlider(final Coordinates currentLocationCoordinates, final SlidingStrategy slidingStrategy) {
		this.currentLocationCoordinates = currentLocationCoordinates;
		this.slidingStrategy = slidingStrategy;
	}

	/**
	 * Updates <code>currentLocationCoordinates</code> of this <code>Slider</code> by delegating in 
	 * the underlying <code>slidingStrategy</code>.
	 * 
	 * @param slidedCoordinates the slided coordinates to be used in the calculation
	 */
	public void slide(final Coordinates slidedCoordinates) {
		currentLocationCoordinates = slidingStrategy.slide(this,slidedCoordinates);
	}
	
	public float getXCurrentCoordinate() {
		return currentLocationCoordinates.getX();
	}

	public float getYCurrentCoordinate() {
		return currentLocationCoordinates.getY();
	}

	public SlidingStrategy getSlideStrategy() {
		return slidingStrategy;
	}

	public void setSlideStrategy(SlidingStrategy slideStrategy) {
		this.slidingStrategy = slideStrategy;
	}
}
