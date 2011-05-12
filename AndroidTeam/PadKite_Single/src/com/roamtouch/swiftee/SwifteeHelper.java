//******************************************************************************** 
//**	Copyright (c) 2011, Roaming Keyboards LLC doing business as RoamTouch®	**	       
//**	All rights reserved.													**
//********************************************************************************
package com.roamtouch.swiftee;

import android.os.Environment;

public class SwifteeHelper {
	
	private static String HOME_PAGE_PATH;
	private static final String HOME_PAGE = "loadPage.html";	

	// A unique method to get homepage.
	public static String getHomepageUrl() {
		if(HOME_PAGE_PATH == null) {
			HOME_PAGE_PATH = "file://" + Environment.getExternalStorageDirectory() + "/PadKite/" + HOME_PAGE;			
		}
		return HOME_PAGE_PATH;
	}  
	
}