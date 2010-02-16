package com.roamingkeyboards.domain.slide.position;

public class Coordinates {
	
	private float x;
	private float y;
	
	private Coordinates(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public static Coordinates make(float x,float y) {
		
		return new Coordinates(x,y);
	}

	public Coordinates sub(Coordinates subCoordinates) {
		//TODO: ADD some validation
		float x = this.x - subCoordinates.getX();
		float y = this.y - subCoordinates.getY();
		
		return Coordinates.make(x, y);
	}
	
	public Coordinates add(Coordinates subCoordinates) {
		//TODO: ADD some validation
		float x = this.x + subCoordinates.getX();
		float y = this.y + subCoordinates.getY();
		
		return Coordinates.make(x, y);
	}
	
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinates other = (Coordinates) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}
	
}
