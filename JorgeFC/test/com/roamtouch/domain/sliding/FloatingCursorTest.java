package com.roamtouch.domain.sliding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.roamtouch.domain.sliding.Coordinates;

public class FloatingCursorTest {

	@Test
	public void testEquals(){
		assertEquals(Coordinates.make(1,1),Coordinates.make(1,1));
	}

	@Test
	public void testGetX(){
		assertTrue(Coordinates.make(1,2).getX() == 1f);
	}
	
	@Test
	public void testGetY(){
		assertTrue(Coordinates.make(1,2).getY() == 2f);
	}
}
