package com.roamtouch.swiftee;

//import java.io.File;
//import java.net.URI;
//import java.net.URL;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import com.roamtouch.database.DBConnector;

import android.app.Application;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.os.Environment;
import android.util.Log;
//import android.os.Environment;

import org.acra.*;
import org.acra.annotation.*;

@ReportsCrashes(formKey = "dFZDbUZHbnVGamZqdDJQQUlZX2tzc1E6MQ") 

public class SwifteeApplication extends Application{
	public static final int CURSOR_TEXT_GESTURE = 1;
	public static final int CURSOR_LINK_GESTURE = 2;
	public static final int CURSOR_IMAGE_GESTURE = 3;
	public static final int CURSOR_NOTARGET_GESTURE = 4;
	public static final int CURSOR_VIDEO_GESTURE = 5;
	public static final int BOOKMARK_GESTURE = 7;
	public static final int CUSTOM_GESTURE = 8;
	public static final int MAIL_GESTURE = 8;
	
	private DBConnector database;
	
	/**
	 * GLOBAL VARIABLES
	 * **/
	
	//Single or multi finger operations, true defaul. //SFOM 
	private static boolean finger_mode = true;   
	public static boolean getFingerMode() {return finger_mode; }
    public static void setFingerMode(boolean mode) { finger_mode = mode; }
    
    //Sets steps, ring colors for single finger operation, 2 and 3.
	private static int single_finger_steps = 3;   
	public static int getSingleFingerSteps() {return single_finger_steps; }
    public static void setSingleFingerSteps(int sfs) { single_finger_steps = sfs; }    
	
	//Sets and gets the diameter of the dots of the FC.
    private static double fc_dots_initial_diam = 5;   
	public static double getFCDotInitialDiam() {return fc_dots_initial_diam; }    
	
	//Sets and gets the diameter of the dots of the FC.
    private static double fc_dots_diam = fc_dots_initial_diam;   
	public static double getFCDotDiam() {return fc_dots_diam; }
    public static void setFCDotDiam(double d) { fc_dots_diam = d; }
        
    //Sets and gets the FC visible.
    private static boolean fc_circle_visible = true;   
	public static boolean getFCVisible() {return fc_circle_visible; }
    public static void setFCVisible(boolean FCV) { fc_circle_visible = FCV; }
    
    //Landing page path. 
    //private static String landing_page_load_path = "file:///android_asset/loadPage.html";
    //public static String getLandingPageLoadPath() {return landing_page_load_path; }
    public static String landing_page_store_path = Environment.getExternalStorageDirectory()+"/PadKite/loadPage.html";
    public static String getLandingPageStorePath() {return landing_page_store_path; }
    
    public static String landingPath = Environment.getExternalStorageDirectory()+"/PadKite/Web Assets/loadPage.html";
    
    
    /**GLOBAL SEARCH VARIABLES**/
	//Search parameter for landing page Twitter trends.
	public static String twitter_key;   
	public static String getTwitterKey() {return twitter_key; }
    public static void setTwitterKey(String key) { twitter_key = key; }
    
    //Search page for Twitter JSON.
	private static String twitter_search;   
	public static String getTwitterSearch() {return twitter_search; }
    public static void setTwitterSearch(String search) { twitter_search = search; } 
	
    //Search page for Images.
	private static String image_search;   
	public static String getImageSearch() {return image_search; }
    public static void setImageSearch(String search) { image_search = search; }
    
   //Search page for Google.
	private static String google_search;   
	public static String getGoogleSearch() {return google_search; }
    public static void setGoogleSearch(String search) { google_search = search; }
    
    //Search page for YouTube.
	private static String youtube_search;   
	public static String getYouTubeSearch() {return youtube_search; }
    public static void setYouTubeSearch(String search) { youtube_search = search; }    
    
        
	@Override
	public void onCreate(){
		ACRA.init(this);
		super.onCreate(); 
		database = new DBConnector(this);
		database.open();
		
		if(isSdCardReady()){
			// FIXME: For now force an update!		
			copyFilestoSdcard("Default Theme", true);
			copyFilestoSdcard("Gesture Library", false);
			copyFilestoSdcard("Web Assets", true);
		}
		if(database.checkIfBookmarkAdded()){
			database.addBookmark();
		}
	}
	public boolean isSdCardReady(){
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);  
	}
	
	public void copyFilestoSdcard(String dir, boolean force){
		try{
			String arr[] = getAssets().list(dir);
			
			File d = new File(BrowserActivity.BASE_PATH + "/"+dir);
			if(!d.exists() || force == true){
				d.mkdirs();

				int count = arr.length;

				for(int i=0;i<count;i++){
					InputStream is = getAssets().open(dir+"/"+arr[i]);

					FileOutputStream myOutput = new FileOutputStream(BrowserActivity.BASE_PATH + "/"+dir+"/"+arr[i]);
					byte[] buffer = new byte[1024];
					int length;
					while ((length = is.read(buffer))>0)
					{
						myOutput.write(buffer, 0, length);
					}

					//Close the streams
					myOutput.flush();
					myOutput.close();
					is.close();
				}
			}
				
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onTerminate(){
		super.onTerminate();
		database.close();
	}
	public DBConnector getDatabase(){
		return database;
	}
	public GestureLibrary getGestureLibrary(int gestureType){
		
		
/*		File f1 = this.getFilesDir();
		File f = new File(f1, "text_gestures");
		boolean b = f.exists();
		boolean b1 = f.canRead();
*/		
	
		GestureLibrary mLibrary = null;
		switch(gestureType){
			case CURSOR_TEXT_GESTURE:
				//mLibrary = GestureLibraries.fromRawResource(this, R.raw.text_gestures);
				mLibrary = GestureLibraries.fromFile(BrowserActivity.BASE_PATH + "/Gesture Library/text_gestures");
				mLibrary.load();
				break;
			case CURSOR_LINK_GESTURE:
				//mLibrary = GestureLibraries.fromRawResource(this, R.raw.link_gestures);
				mLibrary = GestureLibraries.fromFile(BrowserActivity.BASE_PATH + "/Gesture Library/link_gestures");
				mLibrary.load();
				break;
			case CURSOR_IMAGE_GESTURE:
				//mLibrary = GestureLibraries.fromRawResource(this, R.raw.image_gestures);
				mLibrary = GestureLibraries.fromFile(BrowserActivity.BASE_PATH + "/Gesture Library/image_gestures");
				mLibrary.load();
				break;
			case CURSOR_NOTARGET_GESTURE:
				//mLibrary = GestureLibraries.fromRawResource(this, R.raw.notarget_gestures);
				mLibrary = GestureLibraries.fromFile(BrowserActivity.BASE_PATH + "/Gesture Library/notarget_gestures");
				mLibrary.load();
				break;
			case CURSOR_VIDEO_GESTURE:
				//mLibrary = GestureLibraries.fromRawResource(this, R.raw.video_gestures);
				mLibrary = GestureLibraries.fromFile(BrowserActivity.BASE_PATH + "/Gesture Library/video_gestures");
				mLibrary.load();
				break;
			case CUSTOM_GESTURE:
				//mLibrary = GestureLibraries.fromRawResource(this, R.raw.custom_gestures);
				mLibrary = GestureLibraries.fromFile(BrowserActivity.BASE_PATH + "/Gesture Library/custom_gestures");
				mLibrary.load();
				break;
			case BOOKMARK_GESTURE:
				//mLibrary = GestureLibraries.fromRawResource(this, R.raw.bookmarks);
				mLibrary = GestureLibraries.fromFile(BrowserActivity.BASE_PATH + "/Gesture Library/bookmarks");
				mLibrary.load();
				break;
		}		
		return mLibrary;		
	}	
	
	public static void createWebAssets(String path, String content) throws IOException {		 
		 File landing = null;
		 try {
			 File root = Environment.getExternalStorageDirectory();
			    if (root.canWrite()){
			       String destination = path;		       
			       landing = new File(destination);
			       landing.delete();						       
			       landing.createNewFile();		            
			    }			
	        FileWriter gpxwriter = new FileWriter(landing);
	        BufferedWriter out = new BufferedWriter(gpxwriter);
	        out.write(content);
	        out.close();
		} catch (IOException e) {
		    Log.v("TAG", "Error" + e);			
		}		
	 }
	
}
