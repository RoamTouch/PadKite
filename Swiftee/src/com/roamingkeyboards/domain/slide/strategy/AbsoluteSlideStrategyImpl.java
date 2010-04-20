package com.roamingkeyboards.domain.slide.strategy;

import com.roamingkeyboards.domain.slide.pointer.SlidePointer;
import com.roamingkeyboards.domain.slide.position.Coordinates;

public class AbsoluteSlideStrategyImpl extends SlideStrategy {

	@Override
	protected Coordinates calculateNewCoordinates(final SlidePointer slidePointer,final Coordinates newFingerCoordinates) {
		
		return newFingerCoordinates;
	}

}
