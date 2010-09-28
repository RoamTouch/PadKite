package com.roamtouch.menu;

import com.roamtouch.swiftee.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.StateSet;
import android.widget.Button;

public class MenuButton extends Button{

		private double angle;
		private int centerX,centerY;
		private Drawable m_drawable;
		private Drawable m_selectDrawable;
		private String m_button_function = null;
		private boolean isHotkey = false;
		private void init(Context context)
		{
			m_drawable = context.getResources().getDrawable(R.drawable.default_normal);
			m_selectDrawable = context.getResources().getDrawable(R.drawable.default_pressed);
		}
		
		public MenuButton(Context context, AttributeSet attrs) {
			super(context, attrs);					
			init(context);
		}

		public MenuButton(Context context) {
			super(context);		
			init(context);
		}
		
		public void setDrawables(String drawableStr,String selectDrawableStr){
			Drawable drawable = Drawable.createFromPath(drawableStr);
			Drawable selectDrawable = Drawable.createFromPath(selectDrawableStr);
			if (drawable != null)
				m_drawable = drawable;
			if (selectDrawable != null)
				m_selectDrawable = selectDrawable;
			this.setBackgroundDrawable(m_drawable);
		}
		
		public String getFunction()
		{
			return (m_button_function==null)?"none":m_button_function;
		}

		public void setFunction(String button_function)
		{
			m_button_function = button_function;
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

			if (StateSet.stateSetMatches(new int[] { android.R.attr.state_pressed }, states) || StateSet.stateSetMatches(new int[] { android.R.attr.state_focused }, states)) {
				setBackgroundDrawable(m_selectDrawable);
			} else {
				setBackgroundDrawable(m_drawable);
			}

		}

		public void setHotkey(boolean isHotkey) {
			this.isHotkey = isHotkey;
		}

		public boolean isHotkey() {
			return isHotkey;
		}

}
