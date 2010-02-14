package com.roamtouch.domain.slide.strategy;

import com.roamtouch.domain.slide.pointer.SlidingPointer;
import com.roamtouch.domain.slide.position.Coordinates;
/**
 * <code>slidingPointer</code> coordinates are calculated as a translation movement from 
 * <code>currentFingerCoordiantes</code>, <code>SlidePointer.currentCoordinates</code>
 * and <code>newFingerCoordinates</code>
 * 
 * //TODO: Add an example to be more clear
 * 
 * @see SlidingStrategy
 * 
 * @author jorge.bo
 */
public class TraslationSlidingStrategy extends SlidingStrategy {

	private Coordinates currentFingerCoordinates;
	
	public TraslationSlidingStrategy(final Coordinates currentFingerCoordinates) {
		super();
		this.currentFingerCoordinates = currentFingerCoordinates;
	}

	@Override
	protected Coordinates calculateNewCoordinates(final SlidingPointer slidePointer,final Coordinates newFingerCoordinates) {
		
		final Coordinates currentSliderPointerCoordinates = Coordinates.make(slidePointer.getXCoordinate(),slidePointer.getYCoordinate());
		
		final Coordinates newSliderPointerCoordinates = currentSliderPointerCoordinates.sub(currentFingerCoordinates).add(newFingerCoordinates);
		
		currentFingerCoordinates = newFingerCoordinates;
		
		return newSliderPointerCoordinates;
	}
	
	public Coordinates getCurrentFingerCoordinates() {
		return currentFingerCoordinates;
	}


}
