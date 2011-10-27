package com.roamtouch.menu;

import roamtouch.webkit.WebView;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;

public class TabButton extends Button {

		private double angle;
		private int centerX,centerY;
		private WebView mWebView;
		private int mTabIndex;
		private BitmapDrawable bitmapDrawable;
		private boolean hidden;

		//HotTab
		private boolean isHotkey = false;
		private String hotTitle;
		private String tabURL;
		private Rect hotRect;	
		private boolean close;
		private ImageView border;
		
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

		public boolean shouldDraw() {
			if(angle < -90 || angle > 270)
				return false;
			return true;
		}	

		public void setWebView(WebView mWebView) {
			this.mWebView = mWebView;
		}

		public WebView getWebView() {
			return mWebView;
		}
		public void setBitmapDrawable(BitmapDrawable bitmapDrawable) {
			this.bitmapDrawable = bitmapDrawable;
		}
		
		public BitmapDrawable getBitmapDrawable() {
			return bitmapDrawable;
		}

		public boolean isHidden() {
			return hidden;
		}

		public void setHidden(boolean hidden) {
			this.hidden = hidden;
		}	
		
		public void setHotkey(boolean isHotkey) {
			this.isHotkey = isHotkey;
		}

		public boolean isHotkey() {
			return isHotkey;
		}
		

		public void setHotTitle(String hotTitle) {
			this.hotTitle = hotTitle;
		}
		
		public String getHotTitle() {
			return hotTitle;
		}
		
		public void setTabURL(String tabURL) {
			this.tabURL = tabURL;
		}
		
		public String getTabURL() {
			return tabURL;
		}
		
		public void setHotRect(int le, int to, int ri, int bo) {
			Rect hotRect = new Rect(le, to, ri, bo);			
			this.hotRect = hotRect;
		}
		
		public Rect getHotRect() {
			return hotRect;
		}

		public void applyInit(){
			this.setBackgroundDrawable(bitmapDrawable);
			this.hotTitle = mWebView.getTitle();
			this.tabURL = mWebView.getUrl();
		}
		
		public boolean isClose() {
			return close;
		}

		public void setClose(boolean close) {
			this.close = close;
		}
	
		
				
}
