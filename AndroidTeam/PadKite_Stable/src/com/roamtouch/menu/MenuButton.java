package com.roamtouch.menu;

import roamtouch.webkit.WebView;

import com.roamtouch.swiftee.R;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;
import android.widget.Button;

public class MenuButton extends Button {

		private double angle;
		private int centerX,centerY;
		private Drawable m_drawable;
		private Drawable m_selectDrawable;
		private Drawable m_disabledDrawable;
		
		private String m_button_function = null;
		private String m_button_policy = null;
		
		private boolean isAnglePositive;
		
		private String description;
		
		private boolean isArmed;	
		
		//hotTab
		private String hotTitle;
		private String tabURL;	
		private Rect hotRect;	
		
		private WebView mWebView;
		private BitmapDrawable bitmapDrawable;
		
		private boolean isHidden;	
		
		public boolean isHidden() {
			return isHidden;
		}	

		public void setHidden(boolean isHidden) {
			this.isHidden = isHidden;
		}
		
		public boolean isClose() {
			return close;
		}

		public void setClose(boolean close) {
			this.close = close;
		}

		private boolean close;

		private boolean isHotkey = false;
		private void init(Context context)
		{
			m_drawable = context.getResources().getDrawable(R.drawable.default_normal);
			m_selectDrawable = context.getResources().getDrawable(R.drawable.default_pressed);
			m_disabledDrawable = context.getResources().getDrawable(R.drawable.default_normal);
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
		
		public void setHotDrawables(String drawableStr){
			Drawable drawable = Drawable.createFromPath(drawableStr);					
			this.setBackgroundDrawable(drawable);		
		}
		
		
		public void setDisabled(String drawableStr){
			Drawable drawable = Drawable.createFromPath(drawableStr);
			if (drawable != null)
				m_disabledDrawable = drawable;
		}
		public String getFunction()
		{
			return (m_button_function==null)?"none":m_button_function;
		}

		public void setFunction(String button_function)
		{
			m_button_function = button_function;
			description = setDescription();
		}
		
		public String getPolicy()
		{
			return (m_button_policy==null)?"none":m_button_policy;
		}

		public void setPolicy(String button_policy)
		{
			m_button_policy = button_policy;
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
			} else if(!StateSet.stateSetMatches(new int[] { android.R.attr.state_enabled }, states)){
				setBackgroundDrawable(m_disabledDrawable);
			}
			else
				setBackgroundDrawable(m_drawable);
		}

		public void setHotkey(boolean isHotkey) {
			this.isHotkey = isHotkey;
		}

		public boolean isHotkey() {
			return isHotkey;
		}
		
		public boolean isAnglePositive() {
			if(angle < 0){
				return false;
			}
			return true;			
		}	
		
		private String setDescription(){
			
			String desc = null;
			
			if (m_button_function.equals("new_window")){
				desc = "Add New Window";
			} else if (m_button_function.equals("backward")){
				desc = "Go Backward";
			} else if (m_button_function.equals("forward")){
				desc = "Go Forward";
			} else if (m_button_function.equals("refresh")){
				desc = "Refresh Page";
			} else if (m_button_function.equals("stop")){
				desc = "Stop loading";
			} else if (m_button_function.equals("windows")){
				desc = "Tabs Manager";
			} else if (m_button_function.equals("windows")){
				desc = "Tabs Manager";
			} else if (m_button_function.equals("bookmark")){
				desc = "Bookmarks";
			} else if (m_button_function.equals("homepage")){
					desc = "Home page";
			} else if (m_button_function.equals("share")){
				desc = "Share page";			
			} else if (m_button_function.equals("finger_model")){
				desc = "Finger model";
			} else if (m_button_function.equals("settings")){
				desc = "Settings";
			} else if (m_button_function.equals("close")){
				desc = "Exit PadKite";
			}
			
			else if (m_button_function.equals("browser_settings")){
				desc = "Browser Settings";
			} else if (m_button_function.equals("miscellaneous")){
				desc = "Miscellaneous";
			} else if (m_button_function.equals("set_homepage")){
				desc = "Set Homepage";
			} else if (m_button_function.equals("gesture_kit_editor")){
				desc = "Gesture Editor";
			} else if (m_button_function.equals("history")){
				desc = "History";
			} else if (m_button_function.equals("download")){
				desc = "Download";
			}	
			
			return desc;
		}
		
		public String getDescription(){
			return description;
		}
		
		public void setIsArmed(boolean is){
			isArmed = is;
		}
		
		public boolean getIsArmed(){
			return isArmed;
			
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
		
		public void applyInit(){
			this.setBackgroundDrawable(bitmapDrawable);
			this.hotTitle = mWebView.getTitle();
		}	
		
		
}
