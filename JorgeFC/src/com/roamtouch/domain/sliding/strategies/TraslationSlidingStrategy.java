package com.roamtouch.domain.sliding.strategies;

import com.roamtouch.domain.sliding.Coordinates;
import com.roamtouch.domain.sliding.Slider;

/**
 * TraslationSlidingStrategy updates <code>Slider</code>'s coordinates as a traslation from 
 * <code>traslationAxisCoordiantes</code> and the <code>slidedCoordinates</code>
 * 
 * TODO Add an example to be more clear
 * 
 * @see SlidingStrategy
 * 
 * @author jorge.bo
 */
public class TraslationSlidingStrategy extends SlidingStrategy {
	private Coordinates traslationAxisCoordinates;

	/**
	 * Factory method for <code>TraslationSlidingStrategy</code>
	 * 
	 * @param axisCoordinates the axis coordinates
	 * @return the new <code>TraslationSlidingStrategy</code> with <code>axisCoordinates</code> as its translation axis
	 */
	public static TraslationSlidingStrategy makeWithTraslationAxisCoordinates(final Coordinates axisCoordinates) {
		final TraslationSlidingStrategy newTraslationSlidingStrategy = new TraslationSlidingStrategy(axisCoordinates);
		return newTraslationSlidingStrategy;
	}
	
	private TraslationSlidingStrategy(final Coordinates traslationAxisCoordinates) {
		this.traslationAxisCoordinates = traslationAxisCoordinates;
	}

	@Override
	protected Coordinates calculateNewCoordinates(final Slider slider,final Coordinates slidedCoordinates) {
		final Coordinates currentSliderCoordinates = Coordinates.make(slider.getXCurrentCoordinate(),slider.getYCurrentCoordinate());
		final Coordinates newSliderPointerCoordinates = currentSliderCoordinates.sub(traslationAxisCoordinates).add(slidedCoordinates);
		traslationAxisCoordinates = slidedCoordinates;
		return newSliderPointerCoordinates;
	}
}
