package com.roamingkeyboards.domain.slide.strategy;

import com.roamingkeyboards.domain.slide.pointer.SlidePointer;
import com.roamingkeyboards.domain.slide.position.Coordinates;

public abstract class SlideStrategy {

	public Coordinates slide(final SlidePointer slidePointer,final Coordinates fingerCoordinates) {

		final Coordinates calculatedCoordinate = calculateNewCoordinates(slidePointer,fingerCoordinates);
		
		//TODO: Add new template logic
		
		return calculatedCoordinate;
	}
	
	protected abstract Coordinates calculateNewCoordinates(final SlidePointer slidePointer,final Coordinates fingerCoordinates);

}
