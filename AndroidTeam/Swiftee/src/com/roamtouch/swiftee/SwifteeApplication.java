package com.roamtouch.swiftee;

//import java.io.File;
//import java.net.URI;
//import java.net.URL;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.roamtouch.database.DBConnector;

import android.app.Application;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.os.Environment;
//import android.os.Environment;

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
	
	@Override
	public void onCreate(){
		super.onCreate(); 
		database = new DBConnector(this);
		database.open();
		
		if(isSdCardReady()){
			// FIXME: For now force an update!		
			copyFilestoSdcard("Default Theme", true);
			copyFilestoSdcard("Gesture Library", false);
		}
		if(database.checkIfBookmarkAdded())
			database.addBookmark();
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
		}		
		return mLibrary;
		
	}
	
}
