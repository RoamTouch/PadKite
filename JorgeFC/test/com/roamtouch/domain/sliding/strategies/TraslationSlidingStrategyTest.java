package com.roamtouch.domain.sliding.strategies;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.roamtouch.domain.sliding.Coordinates;
import com.roamtouch.domain.sliding.Slider;

public class TraslationSlidingStrategyTest {
	private Slider slider;
	private Coordinates sliderCurrentCoordinates;
	private Coordinates axisCoordinates;
	private Coordinates slidedCoordinates;
	private Coordinates trasladedCoordinates;
	private Mockery mockery;

	@Before
	public void setUp() {
		sliderCurrentCoordinates = Coordinates.make(2,2);
		axisCoordinates = Coordinates.make(1,1);
		slidedCoordinates = Coordinates.make(3,1);
		trasladedCoordinates = Coordinates.make(4,2);
		mockery = new Mockery();
		slider = mockery.mock(Slider.class);
	}
	
	@After
	public void tearDown() {
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void calculatedCoordinatesAreTraslatedBasedOnAxisCoordiantes(){
		mockery.checking(new Expectations() {
			{
				one(slider).getXCurrentCoordinate();
				will(returnValue(sliderCurrentCoordinates.getX()));
				one(slider).getYCurrentCoordinate();
				will(returnValue(sliderCurrentCoordinates.getY()));
			}
		});
		final Coordinates calculatedCoordiantes = TraslationSlidingStrategy.makeWithTraslationAxisCoordinates(axisCoordinates).slide(slider, slidedCoordinates);
		assertEquals(calculatedCoordiantes,trasladedCoordinates);
	}
}
