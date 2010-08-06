package com.roamtouch.menu;


import roamtouch.webkit.WebView;
import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class WindowTabs extends CircularTabsLayout implements OnClickListener{

	private FloatingCursor mFloatingCursor;
	private BrowserActivity mParent;
	private Context mContext;
	//private TabControl tabControl;
	private int currentTab = 1;

	public WindowTabs(Context context) {
		super(context);
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.window_tabs, this);
		
	
		setMode(CircularLayout.DYNAMIC_MODE);
		int count = getChildCount();
		for(int i=0;i<count;i++ ){
			View v = getChildAt(i);
			v.setOnClickListener(this);
		}
		
	}

	public void setTab(WebView wv){
		TabButton tab1 = (TabButton) findViewById(R.id.Tab1);
		tab1.setId(1);
		tab1.setWebView(wv);
	}
	public void setFloatingCursor(FloatingCursor mFloatingCursor) {
		this.mFloatingCursor = mFloatingCursor;
	}
	public void setParent(BrowserActivity parent){
		mParent = parent;		
	}
	
	public void setCurrentThumbnail(BitmapDrawable bd){
		TabButton tab = (TabButton) getChildAt(currentTab);
		tab.setBackgroundDrawable(bd);
	}
	public void onClick(View v) {
		int count = getChildCount();
		for(int i=0;i<count;i++){
			if(v == getChildAt(i)){
				if(i == count-1){
					addWindow();
					currentTab = 1;
					return;
				}
				else if(i == count-3 && count>5){
					removeWindow();
					return;
				}
				else{
					TabButton child = (TabButton)getChildAt(i);
					mParent.setWebView(child.getWebView());
					setActiveTabIndex(child);
					currentTab = i;
					return;
				}
			}
		}
		
	}
	
	
	public void addWindow(){
		TabButton but = new TabButton(mContext);
		but.setBackgroundResource(R.drawable.settings_btn);
		but.setId(1);
		but.setWebView(createWebView());
		but.setOnClickListener(this);
		addView(but,1);
		mParent.setWebView(but.getWebView());
	}
	public WebView createWebView(){
		WebView webView = new WebView(mContext);
		webView.setScrollbarFadingEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setMapTrackballToArrowKeys(false); // use trackball directly
        // Enable the built-in zoom
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		webView.setLayoutParams(params);
		//webView.loadUrl("http://www.google.com");
		return webView;
	}
	public void removeWindow(){
		removeViewAt(currentTab);
		if(currentTab>1)
			currentTab--;
		else
			currentTab=1;
		TabButton child = (TabButton)getChildAt(currentTab);
		mParent.setWebView(child.getWebView());
		setActiveTabIndex(child);
		//currentTab = getActiveTabIndex();
	}
	// Extra saved information for displaying the tab in the picker.
    public static class PickerData {
        String  mUrl;
        String  mTitle;
        Bitmap  mFavicon;
        float   mScale;
        int     mScrollX;
        int     mScrollY;
    }
    
}
