package com.roamtouch.menu;

import android.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.StateSet;
import android.widget.Button;

public class MenuButton extends Button{

		private double angle;
		private int centerX,centerY;
		private Drawable drawable;
		private Drawable selectDrawable;

		
		public MenuButton(Context context, AttributeSet attrs) {
			super(context, attrs);					
		}

		public MenuButton(Context context) {
			super(context);		
			
		}
		public void setDrawables(String drawableStr,String selectDrawableStr){
			drawable = Drawable.createFromPath(drawableStr);
			selectDrawable = Drawable.createFromPath(selectDrawableStr);
			this.setBackgroundDrawable(drawable);
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

		@Override
		protected void drawableStateChanged() {
			super.drawableStateChanged();

			int[] states = getDrawableState();

			if (StateSet.stateSetMatches(new int[] { R.attr.state_pressed }, states) || StateSet.stateSetMatches(new int[] { R.attr.state_focused }, states)) {
				setBackgroundDrawable(selectDrawable);
			} else {
				setBackgroundDrawable(drawable);
			}

		}

}
