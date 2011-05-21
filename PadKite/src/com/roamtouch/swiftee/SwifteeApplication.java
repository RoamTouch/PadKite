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

import com.roamtouch.landingpage.LandingPage;

import android.app.Application;
import android.content.Context;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.os.Environment;
//import android.os.Environment;
import android.util.Log;

public class SwifteeApplication extends Application{

	// POSITION 0 = "Cursor Gestures"	
	public static final int CURSOR_TEXT_GESTURE = 1;
	public static final int CURSOR_LINK_GESTURE = 2;
	public static final int CURSOR_IMAGE_GESTURE = 3;
	public static final int CURSOR_NOTARGET_GESTURE = 4;
	public static final int CURSOR_VIDEO_GESTURE = 5;
	// POSITION 6 = "Circular Menu Gestures"	
	public static final int BOOKMARK_GESTURE = 7;
	public static final int SHARE_GESTURE = 8;
	public static final int CUSTOM_GESTURE = 9;	
	
	//Single or multi finger operations, true defaul. //SFOM 
	private static boolean finger_mode = true;   
	public static boolean getFingerMode() {return finger_mode; }
    public static void setFingerMode(boolean mode) { finger_mode = mode; }
    
    //Search parameter for landing page Twitter trends.
	private static String twitter_key;   
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
    
	private DBConnector database;
	
	@Override
	public void onCreate(){
		super.onCreate(); 
		database = new DBConnector(this);
		database.open();
		
		if(isSdCardReady()){
			// FIXME: For now force an update!		
			copyFilestoSdcard("Default Theme", true);
			copyFilestoSdcard("Gesture Library", false);
			//copyHomepagetoSdcard(false);//home page
		}
		if(database.checkIfBookmarkAdded()) {
			database.addBookmark();
			copyBookmarksToSdcard();
		}		
	}
	
	public static void remoteConnections(){		
		LandingPage.loadRemoteData(1, "urlAssets.json");
		LandingPage.loadRemoteData(2, "popularSites.json");
		Log.v("ORTO", "TS: "+twitter_search);
		//LandingPage.loadRemoteData(3, twitter_search);				
		String landingString = LandingPage.getLandingPageString();			
		try {
			createLanding(landingString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void createLanding(String content) throws IOException {		 
		 File landing = null;
		 try {
			 File root = Environment.getExternalStorageDirectory();
			    if (root.canWrite()){
			       String destination = Environment.getExternalStorageDirectory()+"/PadKite/loadPage.html"; 
			       landing = new File(destination); 			     	      
			       if( landing.exists() ){
			    	   landing.delete();
			       }
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
	 
	public boolean isSdCardReady(){
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);  
	}
	
	public void copyBookmarksToSdcard() {
		String dir = "Gesture Library";
		String name = "bookmarks";
		try {
			InputStream is = getAssets().open(dir+"/"+name);
			FileOutputStream myOutput = new FileOutputStream(BrowserActivity.BASE_PATH + "/"+dir+"/"+name);
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
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/*public void copyHomepagetoSdcard(boolean force){
		try{
			String arr[] = { 
					"content.html"};
			
			int count = arr.length;

			File file;
			for(int i=0;i<count;i++){
				file = new File(BrowserActivity.BASE_PATH + "/" + arr[i]);
				if(file.exists() && !force) {
					continue;
				}
				InputStream is = getAssets().open(arr[i]);

				FileOutputStream myOutput = new FileOutputStream(BrowserActivity.BASE_PATH + "/"+arr[i]);
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
		catch(Exception e){
			e.printStackTrace();
		}
	}*/

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
	public void restoreDefaults(){
		database.restoreDefaults();
		copyFilestoSdcard("Gesture Library", true);
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
			case SHARE_GESTURE:				
				//mLibrary = GestureLibraries.fromRawResource(this, R.raw.bookmarks);
				mLibrary = GestureLibraries.fromFile(BrowserActivity.BASE_PATH + "/Gesture Library/share_gestures");
				mLibrary.load();
				break;
		}		
		return mLibrary;
		
	}
	
}
