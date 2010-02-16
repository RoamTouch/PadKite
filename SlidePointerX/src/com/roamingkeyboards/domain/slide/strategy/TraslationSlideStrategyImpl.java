package com.roamingkeyboards.domain.slide.strategy;

import com.roamingkeyboards.domain.slide.pointer.SlidePointer;
import com.roamingkeyboards.domain.slide.position.Coordinates;

public class TraslationSlideStrategyImpl extends SlideStrategy {

	private Coordinates currentFingerCoordinates;
	
	public TraslationSlideStrategyImpl(final Coordinates currentFingerCoordinates) {
		super();
		this.currentFingerCoordinates = currentFingerCoordinates;
	}

	public Coordinates getCurrentFingerCoordinates() {
		return currentFingerCoordinates;
	}

	@Override
	protected Coordinates calculateNewCoordinates(final SlidePointer slidePointer,final Coordinates newFingerCoordinates) {
		
		final Coordinates currentSliderPointerCoordinates = Coordinates.make(slidePointer.getXCoordinate(),slidePointer.getYCoordinate());
		
		final Coordinates newSliderPointerCoordinates = currentSliderPointerCoordinates.sub(currentFingerCoordinates).add(newFingerCoordinates);
		
		currentFingerCoordinates = newFingerCoordinates;
		
		return newSliderPointerCoordinates;
	}

}
