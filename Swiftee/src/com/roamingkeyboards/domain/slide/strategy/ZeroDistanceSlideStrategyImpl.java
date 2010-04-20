package com.roamingkeyboards.domain.slide.strategy;

import com.roamingkeyboards.domain.slide.pointer.SlidePointer;
import com.roamingkeyboards.domain.slide.position.Coordinates;

public class ZeroDistanceSlideStrategyImpl extends SlideStrategy {

	private Coordinates currentFingerCoordinates;
	
	public Coordinates getCurrentFingerCoordinates() {
		return currentFingerCoordinates;
	}

	public void setCurrentFingerCoordinates(Coordinates currentFingerCoordinates) {
		this.currentFingerCoordinates = currentFingerCoordinates;
	}


	@Override
	protected Coordinates calculateNewCoordinates(final SlidePointer slidePointer,final Coordinates newFingerCoordinates) {
		
		return newFingerCoordinates;
	}

}
