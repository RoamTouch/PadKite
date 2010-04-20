package com.roamingkeyboards.domain.slide.pointer;

import com.roamingkeyboards.domain.slide.position.Coordinates;
import com.roamingkeyboards.domain.slide.strategy.SlideStrategy;

public abstract class SlidePointer {
	
	private Coordinates currentCoordinates;
	
	private SlideStrategy slideStrategy;

	
	public void setSlideStrategy(SlideStrategy slideStrategy) {
		this.slideStrategy = slideStrategy;
	}


	public SlidePointer(final Coordinates currentCoordinates, final SlideStrategy slideStrategy) {
		super();
		this.currentCoordinates = currentCoordinates;
		this.slideStrategy = slideStrategy;
	}


	public float getXCoordinate() {
		return currentCoordinates.getX();
	}

	public float getYCoordinate() {
		return currentCoordinates.getY();
	}


	public void updateCoordinates(final Coordinates fingerCoordinates) {
		
		if (fingerCoordinates != null) {
			final Coordinates updatedCoordinates = slideStrategy.slide(this,fingerCoordinates);
			currentCoordinates = updatedCoordinates;
		}
	}

}
