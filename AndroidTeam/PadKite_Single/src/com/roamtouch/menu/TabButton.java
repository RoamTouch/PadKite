package com.roamtouch.menu;

import roamtouch.webkit.WebView;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TabButton extends ImageView{

		private double angle;
		private int centerX,centerY;
		private WebView mWebView;
		private int mTabIndex;
		
		public TabButton(Context context, AttributeSet attrs) {
			super(context, attrs);					
		}

		public TabButton(Context context) {
			super(context);		
			
		}
		
		public void setAngle(double angle) {
			this.angle = angle;
		}

		public double getAngle() {
			return angle;
		}

		public void setTabIndex(int index) {
			mTabIndex = index;
		}

		public int getTabIndex() {
			return mTabIndex;
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
/*		
		public void calCloseButCenter(int h,int k,int r,double angle){
			closeButCenterX  = (h+(int) (r*Math.cos(Math.toRadians(angle))));
			closeButCenterY = (k+(int) (r*Math.sin(Math.toRadians(angle))));
		}
		
		public int getCloseButCenterX() {
			return closeButCenterX;
		}

		public int getCloseButCenterY() {
			return closeButCenterY;
		}
*/		
		public boolean shouldDraw() {
			if(angle < -90 || angle > 270)
				return false;
			return true;
		}

		public void setWebView(WebView wv) {
			this.mWebView = wv;
		}

		public WebView getWebView() {
			return mWebView;
		}
}
