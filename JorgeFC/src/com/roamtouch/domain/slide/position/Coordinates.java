package com.roamtouch.domain.slide.position;

/**
 * Helps in determination of a point position
 *  
 * @author jorge.bo
 *
 */
public class Coordinates {
	
	private float x;
	private float y;
	
	/**
	 * Constructs a coordinates from x y values
	 * 
	 * @param x x value
	 * @param y y value
	 */
	private Coordinates(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Factory method to create a coordinates
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * 
	 * @return a new <code>Coordinates</code> ready to use
	 */
	public static Coordinates make(float x,float y) {
		
		return new Coordinates(x,y);
	}

	/**
	 * Subtracts <code>subCoordinates</code> from this coordinate
	 * <p>
	 * It subtracts them coordinate to coordinate
	 * Example: this.x - subCoordinates.x ; this.y - subCoodinates.y
	 * 
	 * @param subCoordinates subtraction coordinate
	 * 
	 * @return the result coordinate
	 */
	public Coordinates sub(final Coordinates subCoordinates) {
		
		return Coordinates.make(x - subCoordinates.getX(), y - subCoordinates.getY());
	}
	
	/**
	 * Adds <code>addCoordinates</code> to this coordinate
	 * <p>
	 * Adds them , coordinate to coordinate
	 * Example: this.x + addCoordinates.x ; this.y + addCoodinates.y
	 * 
	 * @param subCoordinates addition coordinate
	 * 
	 * @return the result coordinate
	 */
	public Coordinates add(Coordinates subCoordinates) {

		return Coordinates.make(x + subCoordinates.getX(), y + subCoordinates.getY());
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
