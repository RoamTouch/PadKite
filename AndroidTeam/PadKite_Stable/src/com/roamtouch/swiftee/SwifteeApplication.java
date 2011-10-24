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
import java.util.Vector;

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
	
	private static DBConnector database;
	
	/**
	 * GLOBAL VARIABLES
	 * **/
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
    
    //Sets steps, ring colors for single finger operation, 2 and 3.
	private static int single_finger_steps = 3;   
	public static int getSingleFingerSteps() {return single_finger_steps; }
    public static void setSingleFingerSteps(int sfs) { single_finger_steps = sfs; }    
	
	/**
	 * FLOATING 
	 * CURSOR
	 * **/
    
    //Sets and gets the amount of dots within the circle.	
    private static int fc_mount_of_dots = 40;   
	public static int getFCAmountOfDots() {return fc_mount_of_dots; }
	public static void setFCAmountOfDots(int amount) { fc_mount_of_dots = amount; }
    
    //Sets and gets the diameter of the dots of the FC.
    private static double fc_dots_initial_diam = 4;   
	public static double getFCDotInitialDiam() {return fc_dots_initial_diam; }    
	
	//Sets and gets the diameter of the dots of the FC.
    private static double fc_dots_diam = fc_dots_initial_diam;   
	public static double getFCDotDiam() {return fc_dots_diam; }
    public static void setFCDotDiam(double d) { fc_dots_diam = d; }
        
    //Sets and gets the FC visible.
    private static boolean fc_circle_visible = true;   
	public static boolean getFCVisible() {return fc_circle_visible; }
    public static void setFCVisible(boolean FCV) { fc_circle_visible = FCV; }
    
    /**
	 * LANDDING  
	 * PAGE
	 * **/
    //Landing page path. 
    //private static String landing_page_load_path = "file:///android_asset/loadPage.html";
    //public static String getLandingPageLoadPath() {return landing_page_load_path; }
    public static String landing_page_store_path = Environment.getExternalStorageDirectory()+"/PadKite/loadPage.html";
    public static String getLandingPageStorePath() {return landing_page_store_path; }
    
    public static String landingPath = Environment.getExternalStorageDirectory()+"/PadKite/Web Assets/loadPage.html";
    
    //Amount of new landing pages opened.
	private static int new_landing_amount=0;   
	public static int getNewLandingPagesOpened() {return new_landing_amount; }
    public static void setNewLandingPagesOpened(int nlpo) { new_landing_amount = nlpo; }    
    
    //Get Sets Landing page string.
	private static String landing_page;   
	public static String getLandingPage() {return landing_page; }
    public static void setLandingPage(String landing) { landing_page = landing; }    
    
    /**
     * GLOBAL 
     * SEARCH 
     * VARIABLES
     * **/
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
    
    
    //Tabs Database Vector.
	private static Vector tab_vector;   
	public static Vector getTabVector() {return tab_vector; }
    public static void setTabVector(Vector tV) { tab_vector = tV; }    
    
      
    /**
     * RINGS
     * AND
     * TIPS 
     * **/	
    public static final int DRAW_TAB						= 100;
    public static final int DRAW_RING						= 101;
    public static final int DRAW_NONE						= 102;
    public static final int DRAW_TIP						= 103;
    public static final int DRAW_RING_AND_TAB 				= 104;    

    public static final int PAINT_GRAY	 	= 200;
	public static final int PAINT_GREEN	 	= 201;
	public static final int PAINT_BLUE	 	= 202;	
	public static final int PAINT_YELLOW	= 203;
	public static final int PAINT_VIOLET	= 204;
	public static final int PAINT_RED		= 205;
	public static final int PAINT_ORANGE	= 206;
	public static final int PAINT_BLACK		= 207;	
	public static final int PAINT_RED_MAP	= 208;
	public static final int PAINT_TURQUOISE	= 209;
	
	//RING COLORS
	public static int[] YELLOW 			= {255, 203, 0};	
	public static int[] LIGHT_YELLOW 	= {255, 237, 168};	
	public static int[] GREEN 			= {0, 170, 0};
	public static int[] VIOLET 			= {223, 43, 240};
	public static int[] BLUE 			= {0, 114, 225};
	public static int[] RED 			= {255, 59, 20};
	public static int[] GRAY 			= {180, 180, 180};	
	public static int[] DARK_GRAY 		= {10, 10, 10};
	public static int[] ORANGE	 		= {240, 210, 43};
	public static int[] BLACK	 		= {0, 0, 0};
	public static int[] RED_MAP	 		= {252, 120, 108};
	public static int[] TURQUOISE 		= {0, 212, 159};
	public static int[] LIGHT_GRAY 		= {50, 50, 50};	
	
	//DOTS COLORS
	public static int[] DOTS_GREEN 			= {124, 160, 21};
	public static int[] DOTS_GREEN_BORDER 	= {63, 82, 11};
	
	public static int[] DOTS_ORANGE			= {255, 140, 28};
	public static int[] DOTS_ORANGE_BORDER	= {141, 77, 14};
	
	public static int[] DOTS_YELLOW 		= {255, 233, 43};
	public static int[] DOTS_YELLOW_BORDER	= {137, 125, 4};
	
	public static int[] DOTS_TURQUOISE 			= {238, 99, 161};
	public static int[] DOTS_TURQUOISE_BORDER	= {137, 58, 36};
	
	public static int[] DOTS_LIGHT_BLUE			= {11, 177, 240};
	public static int[] DOTS_LIGHT_BLUE_BORDER	= {6, 81, 110};
	
	public static int[] DOTS_VIOLET				= {226, 115, 255};
	public static int[] DOTS_VIOLET_BORDER		= {120	,162 ,135};
		
	public static int[] DOTS_PINK			= {233, 62, 135};
	public static int[] DOTS_PINK_BORDER	= {115, 41, 69};
	
	public static int[] DOTS_APPLE			= {192, 223, 62};
	public static int[] DOTS_APPLE_BORDER	= {101, 107, 36};
	
	public static int[] DOTS_RED			= {236, 20, 20};
	public static int[] DOTS_RED_BORDER		= {107, 10, 10};
		
	public static int[] DOTS_CYAN			= {68, 238, 217};
	public static int[] DOTS_CYAN_BORDER	= {37, 133, 121};
			
	public static int[] DOTS_LIGHT_ORANGE			= {251, 225, 253};
	public static int[] DOTS_LIGHT_ORANGE_BORDER	= {39, 85, 20};
	
	public static final int PERSIST_FIRST_STAGE 	= 600;	
	public static final int PERSIST_SECOND_STAGE 	= 601;
	public static final int PERSIST_THIRD_STAGE 	= 602;	
	
	/**TIPS**/
    // Variables for Tips
    public static final int SET_TIP_TO_LEFT_UP 			= 500;
    public static final int SET_TIP_TO_CENTER_UP		= 501;
    public static final int SET_TIP_TO_RIGHT_UP			= 502;
       
    public static final int SET_TIP_TO_LEFT_DOWN 		= 503;
    public static final int SET_TIP_TO_CENTER_DOWN		= 504;
    public static final int SET_TIP_TO_RIGHT_DOWN		= 505;
    
    public static final int IS_FOR_WEB_TIPS				= 506;
    public static final int IS_FOR_CIRCULAR_MENU_TIPS	= 507;
    public static final int IS_FOR_CLOSE_WINDOW			= 508;
    
	/**
	 * MISC
	 */
    
	//Set voice recognition settings.
	private static boolean voice_recognition=true;   
	public static boolean getVoiceRecognitionEnabled() {return voice_recognition; }
    public static void setVoiceRecognitionEnabled(boolean voice) { voice_recognition = voice; }   
        
    
    
       
    // Tip message
    private static String tip_message;   
	public static String getTipMessage() {return tip_message; }
    public static void setTipMessage(String tM) { tip_message = tM; }
    
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
	public static DBConnector getDatabase(){
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
			case SHARE_GESTURE:				
				//mLibrary = GestureLibraries.fromRawResource(this, R.raw.bookmarks);
				mLibrary = GestureLibraries.fromFile(BrowserActivity.BASE_PATH + "/Gesture Library/share_gestures");
				mLibrary.load();
				break;
		}		
		return mLibrary;		
	}	
	
	public static void createWebAssets(String path, String landingEnd, String content) throws IOException {		 
		 File landing = null;
		 try {
			 File root = Environment.getExternalStorageDirectory();
			    if (root.canWrite()){			    	
			    	for (int i=0;i<7; i++){
			    		String destination = path + i + landingEnd;		       
					    landing = new File(destination);
					    if (landing!=null){
					    	landing.delete();						       
					    	landing.createNewFile();
					    	FileWriter gpxwriter = new FileWriter(landing);
					    	BufferedWriter out = new BufferedWriter(gpxwriter);
					    	out.write(content);
					    	out.close();
					    }
			    	}			       		            
			    }	       
		} catch (IOException e) {
		    Log.v("TAG", "Error" + e);			
		}		
	 }	
	
}
