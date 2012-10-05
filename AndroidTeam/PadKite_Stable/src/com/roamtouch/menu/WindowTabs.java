package com.roamtouch.menu;


import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Vector;

import android.webkit.WebView;
import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.R;
import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.swiftee.SwifteeHelper;
import com.roamtouch.utils.Base64;
import com.roamtouch.view.EventViewerArea;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.Toast;

public class WindowTabs extends CircularTabsLayout implements OnClickListener, OnTouchListener {

//	private FloatingCursor mFloatingCursor;
	private BrowserActivity mParent;
	private Context mContext;
	private EventViewerArea eventViewer;;
	private static int currentTab = 2;

	public WindowTabs(Context context) {
		super(context);
		mContext = context;	
		LayoutInflater.from(context).inflate(R.layout.window_tabs, this);
		
		appState = ((SwifteeApplication)context.getApplicationContext());
		
		init();
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
		setActiveTabIndex(tab1);
	}
	public void setFloatingCursor(FloatingCursor mFloatingCursor) {
//		this.mFloatingCursor = mFloatingCursor;
	}
	public void setParent(BrowserActivity parent){
		mParent = parent;		
	}
	
	public void setCurrentThumbnail(BitmapDrawable bd,WebView wv){		
		
		int count = getChildCount();
		for(int i =2;i<count-3;i++){
			
			View something = getChildAt(i);			
			if (something instanceof TabButton) { //&& (loaded==true)){			
				
				TabButton tab = (TabButton) getChildAt(i);
				
				if(wv == tab.getWebView()){
					
					//tab.setImageDrawable(bd);
					tab.setBackgroundDrawable(bd);
					tab.invalidate();
					tab.setBitmapDrawable(bd);
					String title = wv.getTitle();
					if (!title.equals("Landing Page")){
						//|| !title.equals("Download")
						//|| !title.equals("History")){						
						if (tabVector.size()==0){
							tabVector.add(tab);
						} else {
							for (int j=0; j<tabVector.size(); j++){
								TabButton tabV = (TabButton) tabVector.get(j);
								if (!(tab==tabV)){
									tabVector.add(tab);
								}					
							}
						}
					}
				}
			}
		}
	}
	
	public void setHotThumbnail(BitmapDrawable bd, WebView wv){		
		hotTab.setBackgroundDrawable(bd);
		hotTab.invalidate();
		String tabTitle = wv.getTitle();
		hotTab.setHotTitle(tabTitle);
		String tabUrl = wv.getUrl();
		hotTab.setTabURL(tabUrl);
		hotTab.setWebView(wv);
		hotTab.setBitmapDrawable(bd);	
	}
	
	public void onClick(View v) {
		int id = v.getId();
		
		/*if(id == 33){
			//addWindow();
			mParent.removeWebView();
			removeWindow();
			return;
		}
		else*/ 
		
		if (v instanceof TabButton){
			TabButton child = (TabButton)v;
			mParent.setActiveWebViewIndex(id);
			setActiveTabIndex(child);	
			currentTab = child.getTabIndex();
			String url = child.getWebView().getUrl();
			eventViewer.setText(url);
			return;
		}
	}
	
	public boolean onTouch(View v, MotionEvent m) {
	    
    	switch (m.getAction())
    	{
	        case MotionEvent.ACTION_DOWN:
	        {
	        	mParent.removeWebView();	        	
				removeWindow();
				cleanClose();
				invalidate();
				resetMenu();
	        }
    	}       
    	return true;
	}	
	

	
	public void setCurrentTab(int i){
		int count = getChildCount()-3;
		if(i > 1 && i < count){
			currentTab = i;
		}
	}
	public static int getCurrentTab(){
		return currentTab;
	}
	
	public void addWindow(String url, boolean background){		
		
		TabButton but = new TabButton(mContext);
		WebView wv = createWebView(url, background);
		but.setWebView(wv);			
		but.setOnClickListener(this);
		but.setTabIndex(2);
		addView(but,2);
		
		int count = getChildCount();
		for(int i =3;i<count-3;i++){
			View something = getChildAt(i);
			if (something instanceof TabButton) {
				TabButton tab = (TabButton) getChildAt(i);			
				tab.setTabIndex(i);
			}			
		}
		
		//tabVector.add(but);
		
		mParent.addWebView(but.getWebView());
		int active = mParent.getActiveWebViewIndex()+1;
		but.setId(active);		
		mParent.setActiveWebViewIndex(mParent.getActiveWebViewIndex()+1);
		
		currentTab = 2;
		setActiveTabIndex(but);
	}
	
	public TabButton addWindowFromDatabase(String url, BitmapDrawable bd,  String title){		
		
		TabButton tabDatabase = new TabButton(mContext);	
		tabDatabase.setBackgroundDrawable(bd);
		tabDatabase.invalidate();
		
		//WebView wv = createWebView(url);		
		//tabDatabase.setWebView(wv);		
		//String tabTitle = wv.getTitle();	
		
		tabDatabase.setHotTitle(title);	
		tabDatabase.setBitmapDrawable(bd);
		addView(tabDatabase,2);
		
		int count = getChildCount();
		for(int i =3;i<count-3;i++){
			View something = getChildAt(i);
			if (something instanceof TabButton) {
				TabButton tab = (TabButton) getChildAt(i);			
				tab.setTabIndex(i);
			}			
		}
		
		//tabVector.add(tabDatabase);
		
		currentTab = 2;
		setActiveTabIndex(tabDatabase);
		
		return tabDatabase;
		
	}
	
	/*private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
	     protected Long doInBackground(URL... urls) {
	         int count = urls.length;
	         long totalSize = 0;
	         for (int i = 0; i < count; i++) {
	             totalSize += Downloader.downloadFile(urls[i]);
	             publishProgress((int) ((i / (float) count) * 100));
	         }
	         return totalSize;
	     }

	     protected void onProgressUpdate(Integer... progress) {
	         setProgressPercent(progress[0]);
	     }

	     protected void onPostExecute(Long result) {
	         showDialog("Downloaded " + result + " bytes");
	     }
	 }*/
	
	public int getWindowCount()
	{
		return getChildCount() - 5;
	}
	
	public WebView createWebView(String url, boolean background){
		
		WebView webView = new WebView(mContext);
		
		if (background){
			webView.setVisibility(INVISIBLE);
		}
		
		webView.setId(mParent.getActiveWebViewIndex()+1);
		webView.setScrollbarFadingEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setMapTrackballToArrowKeys(false); // use trackball directly
        
        // Enable the built-in zoom
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		webView.setLayoutParams(params);
		
		if(url.equals("")){
			
			String[] u = SwifteeHelper.getHomepage(2);
			String _url = u[0]+u[1]+u[2];
			webView.loadUrl(_url);			
			
		} else {
			
			webView.loadUrl(url);
			
			/*webView.setSelectionColor(0xAAb4d5fe);
			webView.setSearchHighlightColor(0xAAb4d5fe);
	
			webView.setCursorOuterColors(0xff74b1fc, 0xff46b000, 0xff74b1fc, 0xff36c000);
			webView.setCursorInnerColors(0xffa0c9fc, 0xff8cd900, 0xffa0c9fc, 0xff7ce900);
			webView.setCursorPressedColors(0x80b4d5fe, 0x807ce900);*/
			
		}
		
		return webView;
		
	}
	public void removeWindow(){
		if(getChildCount()>5){
			TabButton child = (TabButton)getChildAt(currentTab);
			removeViewAt(currentTab);
			for (int i=0; i<tabVector.size();i++){
				TabButton tabV = (TabButton)tabVector.get(i);
				if (tabV==child){
					tabVector.remove(i);
				}
			}
			if(currentTab>2){
				currentTab--;
			} else {			
				currentTab=2;
			}
			setActiveTabIndex(child);
			mParent.adjustTabIndex(this);
		}
	}

	public void setEventViewer(EventViewerArea eventViewer) {
		this.eventViewer = eventViewer;
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
      
    public void removeAllTabs(){
		int count = getChildCount();
		for(int i =3;i<count-3;i++){
			View something = getChildAt(i);
			if (something instanceof TabButton) {
				TabButton tab = (TabButton) getChildAt(i);			
				removeView(tab);
			}			
		}
	}
    
    
}
