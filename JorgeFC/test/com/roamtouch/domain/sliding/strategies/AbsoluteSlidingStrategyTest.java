package com.roamtouch.domain.sliding.strategies;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.roamtouch.domain.sliding.Coordinates;

public class AbsoluteSlidingStrategyTest {

	@Test
	public void calculatedCoordinatesAreSlidedCoordinates(){
		final Coordinates slidedCoordinates = Coordinates.make(1,2);
		final Coordinates calculatedCoordiantes = AbsoluteSlidingStrategy.make().slide(null, slidedCoordinates);
		assertEquals(calculatedCoordiantes,slidedCoordinates);
	}
}
