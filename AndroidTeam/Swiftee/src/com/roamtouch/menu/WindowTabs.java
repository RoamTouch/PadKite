package com.roamtouch.menu;


import roamtouch.webkit.WebView;
import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class WindowTabs extends CircularTabsLayout implements OnClickListener{

	private FloatingCursor mFloatingCursor;
	private BrowserActivity mParent;
	private Context mContext;
	//private TabControl tabControl;
	private int currentTab = 2;

	public WindowTabs(Context context) {
		super(context);
				
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.window_tabs, this);
		
	
		setMode(CircularLayout.DYNAMIC_MODE);
		int count = getChildCount();
		for(int i=1;i<count;i++ ){
			View v = getChildAt(i);
			v.setOnClickListener(this);
		}		
	}

	public void setTab(WebView wv){
		TabButton tab1 = (TabButton) findViewById(R.id.Tab1);
		tab1.setId(0);
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
	//	tab.setBackgroundColor(Color.GRAY);
		tab.setImageDrawable(bd);
	}
	public void onClick(View v) {
		int id = v.getId();
		int count = getChildCount() - 4;
		if(id == 33){
			addWindow();
			currentTab = 2;
			return;
		}
		else{
			TabButton child = (TabButton)v;
			mParent.setActiveWebViewIndex(id);
			setActiveTabIndex(child);	
			currentTab = count - id+1;
			return;
		}
	}
	
	public void setCurrentTab(int i){
		int count = getChildCount()-4;
		if(i > -1 && i < count){
			currentTab = i;
		}
	}
	public int getCurrentTab(){
		return currentTab;
	}
	public void addWindow(){
		TabButton but = new TabButton(mContext);
		but.setBackgroundResource(R.drawable.settings_btn);
		but.setId(mParent.getActiveWebViewIndex()+1);
		mParent.setActiveWebViewIndex(mParent.getActiveWebViewIndex()+1);
		but.setWebView(createWebView());
		but.setOnClickListener(this);
		addView(but,2);
		mParent.addWebView(but.getWebView());
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
	//	webView.loadUrl("file:///android_asset/loadPage.html");
		webView.loadUrl("http://padkite.com/webkit");

		return webView;
	}
	public void removeWindow(){
		removeViewAt(currentTab);
		if(currentTab>2)
			currentTab--;
		else
			currentTab=2;
		TabButton child = (TabButton)getChildAt(currentTab);
		setActiveTabIndex(child);
		mParent.adjustTabIndex(this);
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
