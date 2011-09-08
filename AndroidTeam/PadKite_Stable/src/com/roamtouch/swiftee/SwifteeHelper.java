package com.roamtouch.swiftee;

import android.os.Environment;

public class SwifteeHelper {
	
	public static final String HOME_PAGE = "loadPage0.html";
	
	private static String landingPath = Environment.getExternalStorageDirectory()+"/PadKite/Web Assets/loadPage";
    private static String landingEnd = ".html";
	
	// A unique method to get homepage.
	public static String[] getHomepage(int init) {
		
		String[] loadPage = new String[3];
		
		if (init==0){
			String webPath = "file:///"+Environment.getExternalStorageDirectory()+"/PadKite/Web Assets/";
			String initLanding = webPath + HOME_PAGE;  
			loadPage[0] = initLanding;
		} else if (init==1 || init==2) {
			if (init==1){
				loadPage[0] = landingPath;
			} else {
				loadPage[0] = "file:///"+landingPath;
			}
			String amount = String.valueOf(SwifteeApplication.getNewLandingPagesOpened());			
			loadPage[1] = amount;
			loadPage[2] = landingEnd;
		}			
		return loadPage;
	}
	
	
}