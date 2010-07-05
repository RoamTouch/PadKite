package com.roamtouch.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class MenuButton extends Button{

	private double angle;
	private int centerX,centerY;
	
	public MenuButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public double getAngle() {
		return angle;
	}

	public void calculateCenter(int h,int k,int r,double angle){
		centerX=h+(int) (r*Math.cos(Math.toRadians(angle)));
		centerY=k+(int) (r*Math.sin(Math.toRadians(angle)));
	}
	
	public int getCenterX() {
		return centerX;
	}

	public int getCenterY() {
		return centerY;
	}
	
	public boolean shouldDraw() {
		if(angle < -90 || angle > 270)
			return false;
		return true;
	}

}
