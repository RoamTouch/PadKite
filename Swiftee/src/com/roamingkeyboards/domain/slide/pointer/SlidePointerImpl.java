package com.roamingkeyboards.domain.slide.pointer;

import com.roamingkeyboards.domain.slide.position.Coordinates;
import com.roamingkeyboards.domain.slide.strategy.TraslationSlideStrategyImpl;
import com.roamingkeyboards.domain.slide.strategy.SlideStrategy;

public class SlidePointerImpl extends SlidePointer {

	static class DefaultSlideStrategyHelper {
		
		static SlideStrategy slideStrategy = new TraslationSlideStrategyImpl(Coordinates.make(0.0f,0.0f));
	
		static SlideStrategy getDefaultSlideStrategy() {
			return slideStrategy;
		}
	}
	
	public SlidePointerImpl() {
		this(Coordinates.make(0.0f,0.0f));
	}

	public SlidePointerImpl(final Coordinates initialCoordinates) {
		
		super(initialCoordinates,DefaultSlideStrategyHelper.getDefaultSlideStrategy());
	}
}
