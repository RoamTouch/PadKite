package com.roamtouch.swiftee;

import android.os.Environment;

public class SwifteeHelper {
	
	public static String HOME_PAGE_PATH;
	public static final String HOME_PAGE = "loadPage.html";
	
	// A unique method to get homepage.
	public static String getHomepage() {
		if(HOME_PAGE_PATH == null) {
			//HOME_PAGE_PATH = "file://" + Environment.getExternalStorageDirectory() + "/PadKite/" + HOME_PAGE;
			//HOME_PAGE_PATH = "file:///android_asset/" + HOME_PAGE;
			HOME_PAGE_PATH = "file:///"+Environment.getExternalStorageDirectory()+"/PadKite/"+HOME_PAGE;
		}
		return HOME_PAGE_PATH;
	}
}