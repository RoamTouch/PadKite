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
import java.util.Hashtable;
import java.util.Vector;

import com.roamtouch.database.DBConnector;

import android.app.Application;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;
//import android.os.Environment;

//import org.acra.*;
//import org.acra.annotation.*;

import com.roamtouch.webhook.WebHitTestResult;

//@ReportsCrashes(formKey = "dFZDbUZHbnVGamZqdDJQQUlZX2tzc1E6MQ") 

public class SwifteeApplication extends Application{
	
	private static DBConnector database;
	
	/**
	 * GLOBAL VARIABLES
	 * **/
	
	//screen resolution width.
	private static int screen_width;   
	public static int getScreenWidth() {return screen_width; }
	public static void setScreenWidth(int sW) { screen_width = sW; }   
	
	//screen resolution height.
	private static int screen_height;   
	public static int getScreenHeight() {return screen_height; }
	public static void setScreenHeight(int sH) { screen_height = sH; }
	
	//Device Ip Address
	private static String ip_adress;   
	public static String getIpAdress() {return ip_adress; }
	public static void setIpAdress(String ip) { ip_adress = ip; }
	
	// Open link types	
	public static final int OPEN_LINK		 			= 80;
	public static final int OPEN_LINK_IN_NEW_WINDOW		= 81;
	public static final int OPEN_LINK_IN_BACKGROUND		= 82;
	private static int openType = OPEN_LINK;   
	public static int getOpenType() {return openType; }
	public static void setOp0enTye(int oT) { openType = oT; }
	
	//Master X touch
	private static float masterX;   
	public static float getMasterX() {return masterX; }
	public static void setMasterX(float mX) { masterX = mX; }
	    
	//Master Y touch
	private static float masterY;   
	public static float getMasterY() {return masterY; }
	public static void setMasterY(float mY) { masterY = mY; }
	
	//CURSOR RELOCATION POSITIONS
	public static final int RELOCATE_FROM_POINTER			= 300;
	public static final int RELOCATE_FROM_FINGER_X_POSITION	= 301;
	public static final int RELOCATE_FROM_FINGER_Y_POSITION	= 302;
	public static final int RESTORE_NO_VALUE				= -1;
	
	// POSITION 0 = "Cursor Gestures"	
	public static final int CURSOR_TEXT_GESTURE 		= 1;
	public static final int CURSOR_LINK_GESTURE 		= 2;
	public static final int CURSOR_IMAGE_GESTURE 		= 3;
	public static final int CURSOR_NOTARGET_GESTURE 	= 4;
	public static final int CURSOR_VIDEO_GESTURE 		= 5;
	
	// POSITION 6 = "Circular Menu Gestures"	
	public static final int BOOKMARK_GESTURE 			= 7;
	public static final int SHARE_GESTURE 				= 8;
	public static final int CUSTOM_GESTURE 				= 9;	
	
	//Single or multi finger operations, true defaul. //SFOM 
	private static boolean finger_mode = true;   
	public static boolean getFingerMode() {return finger_mode; }
    public static void setFingerMode(boolean mode) { finger_mode = mode; }
    
    //Sets steps, ring colors for single finger operation, 2 and 3.
	private static int single_finger_steps = 3;   
	public static int getSingleFingerSteps() {return single_finger_steps; }
    public static void setSingleFingerSteps(int sfs) { single_finger_steps = sfs; }    
	
    
  //HitTest innerHTML
    private static String innerHTML;   
	public static String getInnerHTML() {return innerHTML; }
    public static void setInnerHTML(String iH) { innerHTML = iH; }
    
    /**ORIENTATION**/
    public static final int ORIENTATION_LANDSCAPE					= 3001;
    public static final int ORIENTATION_PORTRAIT					= 3002;
    //Single or multi finger operations, true defaul. //SFOM 
  	private static int orientation;   
  	public static int getOrientation() {return orientation; }
    public static void setOrientation(int o) { 
    	
    	orientation = o; 
    
    }
    
	/**
	 * FLOATING 
	 * CURSOR
	 ***/
    
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

    //Lading page FD-Container hidden
  	private static boolean landing_shrinked;   
  	public static boolean getLandingShrinked() {return landing_shrinked; }
    public static void setLandingShrinked(boolean lPS) { landing_shrinked = lPS; }    
       
    //Panel UID	
  	private static String panel_uid = "3495877598435";   
  	public static String getPanelUID() {return panel_uid; }
    public static void setPanelUID(String pu) { panel_uid = pu; }    
   
    //Landing Root path
	public static String langingPageRootPath = "file:////" + Environment.getExternalStorageDirectory()+"/PadKite/Web Assets/loadPage";
    public static String landingPath = langingPageRootPath + ".html";
    public static String getLangingPageRootPath() {
		return langingPageRootPath;
	}
 
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
    
    //Search Wiki.
   	private static String wiki_search;   
   	public static String getWikiSearch() {return wiki_search; }
    public static void setWikiSearch(String search) { wiki_search = search; }
    
    //Search Wiki.
   	private static String wiki_abstract;   
   	public static String getWikiAbstract() {return wiki_abstract; }
    public static void setWikiAbstract(String abs) { wiki_abstract = abs; }
    
    //YouTube Watch
   	private static String youtube_watch;   
   	public static String getYouTubeWatch() {return youtube_watch; }
    public static void setYouTubeWatch(String watch) { youtube_watch = watch; }   
    
    //Google Suggestions for Input text.
	private static String google_suggestion;   
	public static String getGoogleSuggestion() {return google_suggestion; }
    public static void setGoogleSuggestion(String google) { google_suggestion = google; }  
    
    //Wikipedia Suggestions for Input text.
  	private static String wikipedia_suggestion;   
  	public static String getWikipediaSuggestion() {return wikipedia_suggestion; }
    public static void setWikipediaSuggestion(String wiki) { wikipedia_suggestion = wiki; }  
    
    //YouTube Suggestions for Input text.
  	private static String youtube_suggestion;   
  	public static String getYouTubeSuggestion() {return youtube_suggestion; }
    public static void setYouTubeSuggestion(String you) { youtube_suggestion = you; }
    
    //Tabs Database Vector.
	private static Vector tab_vector;   
	public static Vector getTabVector() {return tab_vector; }
    public static void setTabVector(Vector tV) { tab_vector = tV; }
    
    //Expanded.
  	private static boolean expanded;   
  	public static boolean getExpanded() {return expanded; }
    public static void setExpanded(boolean e) { 
    	
    	
		 Throwable t = new Throwable(); StackTraceElement[] elements =
		 t.getStackTrace(); String calleeMethod =
		 elements[0].getMethodName(); String callerMethodName =
		 elements[1].getMethodName(); String callerClassName =
		 elements[1].getClassName(); Log.v("call",
		 "callerMethodName: "+callerMethodName+
		 " callerClassName: "+callerClassName );
		
    	
    	expanded = e; 
    	
   }
    
    //Expanded AMOUNTS factor.
    public static final int PADKITE_EXPANDED 	= 5;
    public static final int FINGER_EXPANDED 	= 8;
    
    //Tab height.
  	private static int tab_height = 35;   
  	public static int getTabHeight() {return tab_height; }
    public static void setTabHeight(int tH) { tab_height = tH; }
    
    //ActiveTab.
  	private static int active_tab_index;    
  	public static int getActiveTabIndex() {return active_tab_index; }
    public static void setActiveTabIndex(int aT) { 
    	
       	active_tab_index = aT; 
    	
    }  
    
    //Percentage size of tabs per cType
       
    public static final int PERCENTAGE_TAB_WINDOWS_MANAGER			= 50;
    public static final int PERCENTAGE_TAB_TEXT						= 50;
    public static final int PERCENTAGE_TAB_PADKITE_IMPUT			= 30;
    public static final int PERCENTAGE_TAB_PANEL					= 40;    
          
    //Tab CERO Rectangle (horizontal dimension only)
    private static Rect TAB_CERO_RECT = new Rect();      
	public static Rect getTabCeroRect() {return TAB_CERO_RECT; }
    public static void setTabCeroRect(Rect tCR) { TAB_CERO_RECT = tCR; }
    
    //Tab FIRST Rectangle (horizontal dimension only)
    private static Rect TAB_FIRST_RECT = new Rect();         
	public static Rect getTabFirstRect() {return TAB_FIRST_RECT; }
    public static void setTabFirstRect(Rect tFR) { TAB_FIRST_RECT = tFR; }
    
    //Tab SECOND Rectangle (horizontal dimension only)
    private static Rect TAB_SECOND_RECT= new Rect();      
	public static Rect getTabSecondRect() {return TAB_SECOND_RECT; }
    public static void setTabSecondRect(Rect tSR) { TAB_SECOND_RECT = tSR; }
    
    //Tab THIRD Rectangle (horizontal dimension only)
    private static Rect TAB_THIRD_RECT = new Rect();    
	public static Rect getTabThirdRect() {return TAB_THIRD_RECT; }
    public static void setTabThirdRect(Rect tTR) { TAB_THIRD_RECT = tTR; }
    
    //Tab FOURTH Rectangle (horizontal dimension only)
    private static Rect TAB_FOURTH_RECT = new Rect();      
	public static Rect getTabFourthRect() {return TAB_FOURTH_RECT; }
    public static void setTabFourthRect(Rect tFR) { TAB_FOURTH_RECT = tFR; }  
    
    
    //ANCHOR Tab height, VARIABLE.
    public static int PERCENTAGE_TAB_ANCHOR					= 60;    
  	public static int getPercentegeTabAnchor() {return PERCENTAGE_TAB_ANCHOR; }
    public static void SetPercentegeTabAnchor(int tA) { PERCENTAGE_TAB_ANCHOR = tA; }  
    /**SOCKET CLIENT STATUS**/
    
    public static final int CLIENT_DISCONNECTED			= 830;
    public static final int CLIENT_CONNECTION_OPENED	= 831;
    public static final int CLIENT_CONNECTED			= 832;
    public static final int CLIENT_CONNECTION_CLOSED	= 833;
    
    public static final int CLIENT_MOUSE_DOWN			= 834; 
    public static final int CLIENT_MOUSE_MOVE			= 835;
    public static final int CLIENT_MOUSE_UP				= 836;
    public static final int CLIENT_MOUSE_DOUBLE			= 837;
    public static final int CLIENT_MESSAGE_URL			= 838;  
    public static final int CLIENT_MESSAGE_KEY			= 839;
   
  	private static int socket_status = CLIENT_DISCONNECTED;   
  	public static int getSocketStatus() {return socket_status; }
    public static void setSocketStatus(int sS) {	socket_status = sS; }    
    //Tab height.
    //public static int PERCENTAGE_TAB_PANEL_INPUT					= 60; //with 2;    
  	//public static int getPercentegeTabInput() {return PERCENTAGE_TAB_PANEL_INPUT; }
    //public static void SetPercentegeTabInput(int tH) { PERCENTAGE_TAB_PANEL_INPUT = tH; }  
    
    
    //Standard Tab height.
  	/*private static int standard_tab_height = 35;   
  	public static int getStandardTabHeight() {return standard_tab_height; }    
    
    //Link Tab height.
  	private static int link_tab_height = 30;   
  	public static int getLinkTabHeight() {return link_tab_height; }
  	
  	//	Text Tab height.
  	private static int text_tab_height = 40;   
  	public static int getTextTabHeight() {return text_tab_height; }*/ 	
  	
  	
    //Tabs edge size.
  	private static int tabs_edge = 20;   
  	public static int getEdge() {return tabs_edge; }
    public static void setEdge(int e) { tabs_edge = e; }
    
    //Tabs edge size.
  	private static int amount_of_tabs;   
  	public static int getTabsAmountOf() {return amount_of_tabs; }
    public static void setTabsAmountOf(int aT) { amount_of_tabs = aT; }    
    
    //Suggestion Row Height.
  	private static int suggestion_row_height;   
  	public static int getSuggestionRowHeight() {return suggestion_row_height; }
    public static void setSuggestionRowHeight(int sRH) { suggestion_row_height = sRH; }
    
    //Suggestion Row Amount - Dynamic.
  	private static int suggestion_row_amount;   
  	public static int getRowAmount() {return suggestion_row_amount; }
    public static void setRowAmount(int sRA) { suggestion_row_amount = sRA; }
    
    //Amount of Rows.
  	private static int amount_of_rows_expanded = 5;   
  	public static int getAmountOfRowsExpanded() {return amount_of_rows_expanded; }
  	public static void setAmountOfRowsExpanded(int aRE) { amount_of_rows_expanded = aRE; }

    //Amount of Rows.
  	private static int amount_of_rows = 6;   
  	public static int getAmountOfRows() {return amount_of_rows; }
  	public static void setAmountOfRows(int aR) { amount_of_rows = aR; }
  	
  	//Amount of Rows.
  	private static boolean for_suggestion_array = false;   
  	public static boolean getSetForSuggestionArray() {return for_suggestion_array; }
  	public static void setSetForSuggestionArray(boolean fSA) { for_suggestion_array = fSA; }
    
    //IndexHit.
  	private static int object_index_hit;   
  	public static int getObjectIndexHit() {return object_index_hit; }
    public static void setObjectIndexHit(int oIH) { object_index_hit = oIH; }  
  
    //PadKite Index.
  	private static int pk_index = -1;   
  	public static int getPKIndex() {return pk_index; }
    public static void setPKIndex(int pkI) { pk_index = pkI; }
    
    //IndexHit.
  	private static int pk_index_hit;   
  	public static int getPKTabIndex() {return pk_index_hit; }
    public static void setPKTabIndex(int pkIH) { pk_index_hit = pkIH; }  
    
    //Suggestion Input Text.
  	private static String input_text = null;   
  	public static String getInputText() {return input_text; }
  	public static void setInputText(String iT) { input_text = iT; } 
  	//Landing Input Top.
  	private static int landing_input_top;   
  	public static int getLandingInputTop() {return landing_input_top; }
    public static void setLandingInputTop(int lIT) {	landing_input_top = lIT; }
    
    //Right button arrow enabled
  	private static boolean up_row_enabled;   
  	public static boolean getUpRowEnabled() {return up_row_enabled; }
    public static void setUpRowEnabled(boolean uRE) { up_row_enabled = uRE; }
    
    /**
     * Size of rows
     ***/
    //	More
  	private static int array_more_bigger_row;   
  	public static int getArrayMoreBiggerRow() {return array_more_bigger_row; }
    public static void setArrayMoreBiggerRow(int higherMore) { array_more_bigger_row = higherMore; }   
    
    // Help
  	private static int array_help_bigger_row;   
  	public static int getArrayHelpBiggerRow() {return array_help_bigger_row; }
    public static void setArrayHelpBiggerRow(int higherHelp) { array_help_bigger_row = higherHelp; }     
        
    //	Open
  	private static int array_open_bigger_row;   
  	public static int getArrayOpenBiggerRow() {return array_open_bigger_row; }
    public static void setArrayOpenBiggerRow(int higherOpen) { array_open_bigger_row = higherOpen; }     
    
    //More Anchor
  	private static int array_more_anchor_bigger_row;   
  	public static int getArrayMoreAnchorBiggerRow() {return array_more_anchor_bigger_row; }
    public static void setArrayMoreAnchorBiggerRow(int higherMoreAnchor) { array_more_anchor_bigger_row = higherMoreAnchor; }   

    //Text
  	private static int array_text_bigger_row;   
  	public static int getArrayTextBiggerRow() {return array_text_bigger_row; }
    public static void setArrayTextBiggerRow(int higherText) { array_text_bigger_row = higherText; }
    
    //ClipBoard
  	//private static int array_clipboard_bigger_row;   
  	//public static int getArrayClipBoardBiggerRow() {return array_clipboard_bigger_row; }
    //public static void setArrayClipBoardBiggerRow(int higherClipBoard) { array_clipboard_bigger_row = higherClipBoard; }
    
    /**
     * RINGS
     * AND
     * TIPS 
     * **/    
	

    //Tab according to index  
    public static final int	TABINDEX_NOTHING	  				= -1;
    public static final int TABINDEX_CERO						= 0;
    public static final int TABINDEX_FIRST						= 1;
    public static final int TABINDEX_SECOND						= 2;    
    public static final int TABINDEX_THIRD						= 3;
    public static final int TABINDEX_FOURTH						= 4;
    public static final int TABINDEX_FIFTH						= 5;  
    
    public static final int DRAW_TABS							= 100;
    public static final int DRAW_INPUT_TABS						= 101;
    public static final int DRAW_DEFAULT_TABS				 	= 102;
    public static final int DRAW_RING							= 103;
    public static final int DRAW_NOTHING						= 104;
    public static final int DRAW_TIP							= 105;
    public static final int DRAW_RING_AND_TAB 					= 106;   

    public static final int PERSIST_CERO_STAGE 		= 600;	
	public static final int PERSIST_FIRST_STAGE 	= 601;
	public static final int PERSIST_SECOND_STAGE 	= 602;
	public static final int PERSIST_THIRD_STAGE 	= 603;
	public static final int PERSIST_FOURTH_STAGE 	= 604;	
	
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
    public static final int IS_FOR_CONTENT_OBJECT		= 509;    
    
    public static final int TYPE_PADKITE_WINDOWS_MANAGER	= 20;
    public static final int TYPE_PADKITE_TAB				= 21;
    public static final int TYPE_PADKITE_ROW				= 22;
    public static final int TYPE_PADKITE_BUTTON				= 23;
    public static final int TYPE_PADKITE_BACKGROUND			= 24;
    public static final int TYPE_PADKITE_PANEL				= 25;
    public static final int TYPE_PADKITE_INPUT				= 26;
    public static final int TYPE_PADKITE_TIP_BUTTON			= 27;
    public static final int TYPE_PADKITE_MORE_LINKS			= 28;
    public static final int TYPE_PADKITE_SERVER				= 30;
    public static final int TYPE_PADKITE_BACKGROUND_ROW		= 31;
    
    public static final int TAB_ROUNDED_ANGLE_UP				= 1000;
    public static final int TAB_ROUNDED_ANGLE_DOWN				= 1001;
    public static final int TAB_ALL_ROUDED						= 1002;
    public static final int TAB_SUGGESTIONS						= 1003;   
    
    public static final int SUGGESTION_FRAME_FOR_BUTTONS				= 1004;
    
    public static final int TAB_SUGGESTION_FRAME						= 1006;
    public static final int TAB_SUGGESTION_FRAME_BIGGER_RIGHT			= 1007;
    public static final int TAB_SUGGESTION_FRAME_BIGGER_CENTER			= 1008;
    public static final int TAB_SUGGESTION_FRAME_BIGGER_LEFT			= 1009;    
    public static final int TAB_SUGGESTION_BUTTON						= 1010;
    
    public static final int TAB_LINK							= 1011;
    public static final int TAB_TEXT							= 1012;
    public static final int TIP_FOR_CONTENT_OBJECT				= 1013;
    
    public static final int ANCHOR_SPINNER_BACKGROUND					= 1014;
    public static final int ANCHOR_SPINNER_BACKGROUND_ORIENTED_RIGHT	= 1015;
    public static final int ANCHOR_SPINNER_BACKGROUND_ORIENTED_LEFT		= 1016;
    
    public static final int DRAW_ROW_TOP						= 1017;
    public static final int DRAW_ROW_MIDDLE						= 1018;
    public static final int DRAW_ROW_BOTTOM						= 1019;
    public static final int DRAW_ROW_ROUNDED					= 1020;    
    
    public static final int LINK_DATA_NOT_REACHABLE				= 5000;    
    public static final int LINK_DATA_NOT_CALLED				= 5001;    
    public static final int LINK_DATA_CALLED					= 5002;
    public static final int LINK_DATA_LOADED					= 5003;
    public static final int LINK_DATA_PARSED					= 5004;
    
    public static final int JSOUP_SITE_NOT_REACHABLE			= 5005;
    public static final int JSOUP_NOTHING						= 5006;
    public static final int JSOUP_CALLED						= 5007;
    public static final int JSOUP_LOADED						= 5008;
    public static final int JSOUP_PARSED						= 5009;
    
    public static final int SUGGESTION_NO_DATA_CALLED			= -1;
    public static final int SUGGESTION_DATA_CALLED				= 5011;
    public static final int SUGGESTION_DATA_LOADED				= 5012;
    
    public static final int PADKITE_INPUT_SPINNER_BACKGROUND				= 5013;
    public static final int PADKITE_INPUT_SPINNER_BACKGROUND_ORIENTED_RIGHT	= 5014;
    public static final int PADKITE_INPUT_SPINNER_BACKGROUND_ORIENTED_LEFT	= 5015;
    
    private static int padkite_input_spinner_status = SUGGESTION_NO_DATA_CALLED;   
  	public static int getPadKiteInputSpinnerStatus() {return padkite_input_spinner_status; }
  	
    public static void setPadKiteInputSpinnerStatus(int status) {   	
    	Log.v("status", "status Wsiftee :" + status);   	
    	padkite_input_spinner_status = status;     	
    }
    
    //Videos are loaded into array
    private static boolean is_array_videos;   
  	public static boolean getIsArrayVideo() {return is_array_videos; }
    public static void setIsArrayVideo(boolean iAV) { is_array_videos = iAV; }
    
    //VWiki are loaded into array
    private static boolean is_array_wikis;   
  	public static boolean getIsArrayWiki() {return is_array_wikis; }
    public static void setIsArrayWiki(boolean iAW) { is_array_wikis = iAW; }
    
    //Videos rowns amount :: IMPORTANT remote control does not work with this true.
    private static boolean memory_status_enabled = false;   
  	public static boolean getMemoryStatusEnabled() {return memory_status_enabled; }
    public static void setMemoryStatusEnabled(boolean mSE) { memory_status_enabled = mSE; }
    
    //Videos rowns amount
    private static int video_row_amounts = 3;   
  	public static int getVideoRowsAmount() {return video_row_amounts; }
    public static void setVideoRowsAmount(int vRA) { video_row_amounts = vRA; }
    
    //Wikis rowns amount	
    private static int wiki_row_amounts = 4;   
  	public static int getWikiRowsAmount() {return wiki_row_amounts; }
    public static void setWikiRowsAmount(int wRA) { wiki_row_amounts = wRA; }
    
    /**
     * SUGGESTION BUTTONS.*/   
    public static int active_search_check = -1;    
  	public static int getActiveSearchCheck() {return active_search_check;}  	
  	public static String getActiveSearchTerm() {return activeSearchTerm;}	   
  	public static String getActiveSearch() {return active_search;}
  	
  	 //Videos rowns amount
    private static String[] suggestion_buttons_array;   
  	public static String[] getSuggestionButtonsArray() {return suggestion_buttons_array; }
    public static void setSuggestionButtonsArray(String[] sBA) { suggestion_buttons_array = sBA; }
  	
  	public static String activeSearchTerm;
  	private static String active_search;
  	
  	public static void setActiveSearch(int aS) {	
 		
  		active_search_check = aS;
  		
  		switch(aS){
  			case SwifteeApplication.WEB_SEARCH:
				active_search = SwifteeApplication.getGoogleSearch();
				activeSearchTerm = "Searching on Google";
				break;
  			case SwifteeApplication.WIKI_BUTTON:
  				active_search = SwifteeApplication.getWikiSearch();
  				activeSearchTerm = "Searching on Wikipedia";
  				break;
  			case SwifteeApplication.VIDEO_BUTTON:
  				active_search = SwifteeApplication.getYouTubeSearch();
  				activeSearchTerm = "Searching on YouTube";
  				break;
  			case SwifteeApplication.IMAGE_BUTTON:
  				active_search = SwifteeApplication.getImageSearch();
  				activeSearchTerm = "Searching on Google Image";
  				break;  			
  		}  		
    }
    
  	public static final int WEB_SEARCH								= -1;
    public static final int WIKI_BUTTON								= 2000;
    public static final int VIDEO_BUTTON							= 2001;
    public static final int IMAGE_BUTTON							= 2002;  
   
    public static final int TAB_ORIENT_LEFT							= 2003;
    public static final int TAB_ORIENT_RIGHT						= 2004;    
    public static final int TAB_ORIENT_CENTER						= 2005;    
    
    public static final int TIP_CONTENT_ORIENT_UP					= 2006;
    public static final int TIP_CONTENT_ORIENT_BOTTOM				= 2007;    
    public static final int LINK_TAB_ORIENT_UP						= 2008;
    public static final int LINK_TAB_ORIENT_DOWN					= 2009;
    public static final int FRAME_ORIENT_DOWN						= 2010;
    public static final int FRAME_ORIENT_UP							= 2011;
    
    public static final int TEXT_TAB_ORIENT_UP						= 2012;
    public static final int TEXT_TAB_ORIENT_DOWN					= 2013;    
   
    public static final int S_CONTROLLER_REDRAW_ALL					= 2014;
    public static final int S_CONTROLLER_REDRAW_FRAME_AND_BUTTONS 	= 2015;
    public static final int S_CONTROLLER_REDRAW_NOTHING				= 2016;
       
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
    
    // Tip message bigger
    private static boolean tip_message_bigger;   
	public static boolean getTipMessageBigger() {return tip_message_bigger; }
    public static void setTipMessageBigger(boolean tMB) { tip_message_bigger = tMB; }    
    
	@Override
	public void onCreate(){
		//ACRA.init(this);
		super.onCreate(); 
		database = new DBConnector(this);
		database.open();
		
		if(isSdCardReady()){
			// FIXME: For now force an update!		
			copyFilestoSdcard("WebPages", true);
			copyFilestoSdcard("DefaultTheme", true);
			copyFilestoSdcard("GestureLibrary", false);
			copyFilestoSdcard("WebAssets", true);
			copyFilestoSdcard("WebAssets/images", true);
			copyFilestoSdcard("Images", true);	
		}
		//if(database.checkIfBookmarkAdded()){
		//	database.addBookmark();
		//}
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
				mLibrary = GestureLibraries.fromFile(BrowserActivity.GESTURES_PATH + "/text_gestures");
				mLibrary.load();
				break;
			case CURSOR_LINK_GESTURE:
				//mLibrary = GestureLibraries.fromRawResource(this, R.raw.link_gestures);
				mLibrary = GestureLibraries.fromFile(BrowserActivity.GESTURES_PATH + "/link_gestures");
				mLibrary.load();
				break;
			case CURSOR_IMAGE_GESTURE:
				//mLibrary = GestureLibraries.fromRawResource(this, R.raw.image_gestures);
				mLibrary = GestureLibraries.fromFile(BrowserActivity.GESTURES_PATH + "/image_gestures");
				mLibrary.load();
				break;
			case CURSOR_NOTARGET_GESTURE:
				//mLibrary = GestureLibraries.fromRawResource(this, R.raw.notarget_gestures);
				mLibrary = GestureLibraries.fromFile(BrowserActivity.GESTURES_PATH + "/notarget_gestures");
				mLibrary.load();
				break;
			case CURSOR_VIDEO_GESTURE:
				//mLibrary = GestureLibraries.fromRawResource(this, R.raw.video_gestures);
				mLibrary = GestureLibraries.fromFile(BrowserActivity.BASE_PATH + "/Gesture Library/video_gestures");
				mLibrary.load();
				break;
			case CUSTOM_GESTURE:
				//mLibrary = GestureLibraries.fromRawResource(this, R.raw.custom_gestures);
				mLibrary = GestureLibraries.fromFile(BrowserActivity.GESTURES_PATH + "/custom_gestures");
				mLibrary.load();
				break;
			case BOOKMARK_GESTURE:
				//mLibrary = GestureLibraries.fromRawResource(this, R.raw.bookmarks);
				mLibrary = GestureLibraries.fromFile(BrowserActivity.GESTURES_PATH + "/bookmarks");
				mLibrary.load();
				break;
			case SHARE_GESTURE:				
				//mLibrary = GestureLibraries.fromRawResource(this, R.raw.bookmarks);
				mLibrary = GestureLibraries.fromFile(BrowserActivity.GESTURES_PATH + "/share_gestures");
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
	
	/**
	 * HitTest Font Size
	 **/
	
	//Font Size 
    private static int hit_test_font_size;   
	public static int getHitTestFontSize(){ return hit_test_font_size; }
    public static int setHitTestFontSize(int htfs) { return hit_test_font_size = htfs; }   
	
	/**
     * SUGESTION BUTTONS ASSETS
     */   
	
	//Input Spinner ON OFF
    private static boolean input_spinner_on;   
    public static boolean getInputSpinnerOn() {return input_spinner_on; }
    public static void setInputSpinnerOn(boolean sIO) { input_spinner_on = sIO; }
    
    // Anchor Spinner ON OFF
    private static boolean anchor_spinner_on;   
    public static boolean getAchorSpinnerOn() {return anchor_spinner_on; }
    public static void setAchorSpinnerOn(boolean aSO) { anchor_spinner_on = aSO; }
    
    // Anchor Spinner ON OFF
    private static int spinner_speed = 150;   //measured in radians 
    public static int getSpinnerSpeed() {return spinner_speed; }
    public static void setSpinnerSpeed(int sS) { spinner_speed = sS; }
    
    //Master rectangle coming from Hittest
    private static Rect master_rect;   
	public static Rect getMasterRect() {return master_rect; }
    public static void setMasterRect(Rect mR) { master_rect = mR; }  
    
    //HitTest Identifier
    private static int identifier;   
	public static int getIdentifier() {return identifier; }
    public static void setIdentifier(int i) { identifier = i; }
    
    //Master rectangle from ringControlled applied scroll
    private static Rect master_scroll_rect;   
	public static Rect getMasterScrollRect() {return master_scroll_rect; }
    public static void setMasterScrollRect(Rect mSR) { master_scroll_rect = mSR; }
    
    //Original rect when anchor for spinner
    private static Rect original_anchor_rect;   
   	public static Rect getOriginalAnchorRect() {return original_anchor_rect; }
    public static void setOriginalAnchorRect(Rect oAR) { original_anchor_rect = oAR; }
    
    private static Rect suggestion_rect;   
	public static Rect getSuggestionRect() {return suggestion_rect; }
    public static void setSuggestionRect(Rect r) { suggestion_rect = r; }  
    
    //Complete Rect anchor + master
    private static Rect complete_rect;   
   	public static Rect getCompleteRect() {return complete_rect; }
    public static void setCompleteRect(Rect cR) { complete_rect = cR; }
    
    //Tip Message rect
    private static Rect tip_message_rect;   
   	public static Rect getTipMessageRect() {return tip_message_rect; }
    public static void setTipMessageRect(Rect tMR) { tip_message_rect = tMR; }
    
    
    
    private static int[] suggestion_color;   
	public static int[] getSuggestionColor(){ return suggestion_color; }
    public static void setSuggestionColor(int[] r) { suggestion_color = r; }
    
    private static int[] active_color_for_tip;   
	public static int[] getActiveColor(){ return active_color_for_tip; }
    public static void setActiveColor(int[] a) { active_color_for_tip = a; }
    
    //Global result type written from Hittest 
    private static int result_type;   
	public static int getResultType(){ return result_type; }
    public static int setResultType(int r) { return result_type = r; }   
	
    //When suggestion text is bigger than the link rect. 
    private static Rect bigger_rect_resize;   
	public static Rect getBiggerRectResize(){ return bigger_rect_resize; }
    public static void setBiggerRectResize(Rect brr) { bigger_rect_resize = brr; } 
    
    //text bigger
    private static int text_is_bigger = -1;   
	public static int getTextIsBigger(){ return text_is_bigger; }
    public static void setTextIsBigger(int tib) { text_is_bigger = tib; }   
    
    public static final int TEXT_IS_NOT_BIGGER		= -1;
    public static final int TEXT_IS_BIGGER_LEFT		= 3000;
    public static final int TEXT_IS_BIGGER_RIGHT	= 3001;
    public static final int TEXT_IS_BIGGER_CENTER	= 3002;
    
    public static final int VERTICAL_LEFT_COLUMN	= 3010;
    public static final int VERTICAL_CENTER_COLUMN	= 3011;
    public static final int VERTICAL_RIGHT_COLUMN	= 3012;
    
    //vertical position
    private static int vertical_position;   
	public static int getVerticalPosition(){ return vertical_position; }
	
    public static void setVerticalPosition(int vP) {    	
    	
		 Throwable t = new Throwable(); StackTraceElement[] elements =
		 t.getStackTrace(); String calleeMethod =
		 elements[0].getMethodName(); String callerMethodName =
		 elements[1].getMethodName(); String callerClassName =
		 elements[1].getClassName(); Log.v("call",
		 "callerMethodName: "+callerMethodName+
		 " callerClassName: "+callerClassName );		 
    	
    	vertical_position = vP; 
    	
    }   

    //Scroll
    private static int scrolling_direction = -1;   
	public static int getScrollingDirection(){ return scrolling_direction; }
    public static void setScrollingDirection(int sD) { scrolling_direction = sD; }
    
    public static final int SCROLLING_DIRECTION_DOWN		= 4025;
    public static final int SCROLLING_DIRECTION_UP			= 4026;    
    
       
    //Left text bigger than link boolean
   /* private static boolean text_is_bigger_right;   
	public static boolean getTextIsBiggerRight(){ return text_is_bigger_right; }
    public static void setTextIsBiggerRight(boolean tib) { text_is_bigger_right = tib; }
    
    //Center text bigger than link boolean
    private static boolean text_is_bigger_center;   
	public static boolean getTextIsBiggerCenter(){ return text_is_bigger_center; }
    public static void setTextIsBiggerCenter(boolean tib) { text_is_bigger_center = tib; } 
    
    //Left text bigger than link boolean
    private static boolean text_is_bigger_left;   
	public static boolean getTextIsBiggerLeft(){ return text_is_bigger_left; }
    public static void setTextIsBiggerLeft(boolean tib) { text_is_bigger_left = tib; }*/ 
    
	//Tab 0
    private static Rect tab_0 = new Rect();;   
	public static Rect getTab_0_Rect() {return tab_0; }
    public static void setTab_0_Rect(Rect r) { tab_0 = r; }
    
	//Tab 1
    private static Rect tab_1 = new Rect();;   
	public static Rect getTab_1_Rect() {return tab_1; }
    public static void setTab_1_Rect(Rect r) { tab_1 = r; }
        
	//Tab 2
    private static Rect tab_2 = new Rect();;   
	public static Rect getTab_2_Rect() {return tab_2; }
    public static void setTab_2_Rect(Rect r) { tab_2 = r; }
    
	//Tab 3
    private static Rect tab_3 = new Rect();;   
	public static Rect getTab_3_Rect() {return tab_3; }
    public static void setTab_3_Rect(Rect r) { tab_3 = r; }
    
	//Tab 4
    private static Rect tab_4 = new Rect();   
	public static Rect getTab_4_Rect() {return tab_4; }
    public static void setTab_4_Rect(Rect r) { tab_4 = r; }
    
	//Tab 5
    private static Rect tab_5 = new Rect();   
	public static Rect getTab_5_Rect() {return tab_5; }
    public static void setTab_5_Rect(Rect r) { tab_5 = r; }    
  
	//Active Rect
    private static Rect active_rect = new Rect();   
	public static Rect getActiveRect() {return active_rect; }
    public static void setActiveRect(Rect r) { 
    	active_rect = r; 
    }
	
    //Anchor Rect
    private static Rect anchor_rect = new Rect();   
	public static Rect getAnchorRect() {return anchor_rect; }
    public static void setAnchorRect(Rect aR) {	anchor_rect = aR; }
    
    //Spinner Bottom Rect
    private static Rect spinner_bottom_rect = new Rect();   
	public static Rect getSpinnerBackgroundRect() {return spinner_bottom_rect; }
    public static void setSpinnerBackgroundRect(Rect sB) { spinner_bottom_rect = sB; }
    
	//Row 0
    private static Rect suggestion_row_0 = new Rect();   
	public static Rect getSuggestionRow_0_Rect() {return suggestion_row_0; }
    public static void setSuggestionRow_0_Rect(Rect r) { suggestion_row_0 = r; }
    
    //Row 1
    private static Rect suggestion_row_1 = new Rect();   
	public static Rect getSuggestionRow_1_Rect() {return suggestion_row_1; }
    public static void setSuggestionRow_1_Rect(Rect r) { suggestion_row_1 = r; }
    
    //Row 2
    private static Rect suggestion_row_2 = new Rect();   
	public static Rect getSuggestionRow_2_Rect() {return suggestion_row_2; }
    public static void setSuggestionRow_2_Rect(Rect r) { suggestion_row_2 = r; }
    
    //Row 3
    private static Rect suggestion_row_3 = new Rect();   
	public static Rect getSuggestionRow_3_Rect() {return suggestion_row_3; }
    public static void setSuggestionRow_3_Rect(Rect r) { suggestion_row_3 = r; }  
    
    //Row 4
    private static Rect suggestion_row_4 = new Rect();   
	public static Rect getSuggestionRow_4_Rect() {return suggestion_row_4; }
    public static void setSuggestionRow_4_Rect(Rect r) { suggestion_row_4 = r; }
    
    //Row 5
    private static Rect suggestion_row_5 = new Rect();   
	public static Rect getSuggestionRow_5_Rect() {return suggestion_row_5; }
    public static void setSuggestionRow_5_Rect(Rect r) { suggestion_row_5 = r; }    
    
    //Suggestion button 0
    private static Rect suggestion_button_0 = new Rect();   
	public static Rect getSuggestionButton_0_Rect() {return suggestion_button_0; }
    public static void setSuggestionButton_0_Rect(Rect r) { suggestion_button_0 = r; }
    
    //Suggestion button 1
    private static Rect suggestion_button_1 = new Rect();   
	public static Rect getSuggestionButton_1_Rect() {return suggestion_button_1; }
    public static void setSuggestionButton_1_Rect(Rect r) { suggestion_button_1 = r; }
    
    //Suggestion button 2
    private static Rect suggestion_button_2 = new Rect();   
	public static Rect getSuggestionButton_2_Rect() {return suggestion_button_2; }
    public static void setSuggestionButton_2_Rect(Rect r) { suggestion_button_2 = r; }
    
    //Suggestion button 3
    private static Rect suggestion_button_3 = new Rect();   
	public static Rect getSuggestionButton_3_Rect() {return suggestion_button_3; }
    public static void setSuggestionButton_3_Rect(Rect r) { suggestion_button_3 = r; } 
    
    //Suggestion button 2
    private static Rect suggestion_button_4 = new Rect();   
	public static Rect getSuggestionButton_4_Rect() {return suggestion_button_4; }
    public static void setSuggestionButton_4_Rect(Rect r) { suggestion_button_4 = r; }    
    
    //Suggestion button 5
    private static Rect suggestion_button_5 = new Rect();   
	public static Rect getSuggestionButton_5_Rect() {return suggestion_button_5; }
    public static void setSuggestionButton_5_Rect(Rect r) { suggestion_button_5 = r; }  
    
    //Bottom form big
    private static Rect bottom_form_button_0 = new Rect();   
	public static Rect getBottomFormButton_0_Rect() {return bottom_form_button_0; }
    public static void setBottomFormButton_0_Rect(Rect r) { bottom_form_button_0 = r; }    
    
    //Bottom form small
    private static Rect bottom_form_button_1 = new Rect();   
	public static Rect getBottomFormButton_1_Rect() {return bottom_form_button_1; }
    public static void setBottomFormButton_1_Rect(Rect r) { bottom_form_button_1 = r; }
    
    //Bottom form between lines
    private static Rect bottom_form_button_2 = new Rect();   
	public static Rect getBottomFormButton_2_Rect() {return bottom_form_button_2; }
    public static void setBottomFormButton_2_Rect(Rect r) { bottom_form_button_2 = r; }
    
    //Tip button
    private static Rect tip_button = new Rect();   
	public static Rect getTipButton_Rect() {return tip_button; }
    public static void setTipButton_Rect(Rect tR) { tip_button = tR; }
    
    //Tip withbutton
    private static boolean has_tip_button;   
	public static boolean getHasTipButton() {return has_tip_button; }
    public static void setHasTipButton(boolean tB) { has_tip_button = tB; }
    
    //**IMAGES BUTTONS**//
    
    //Image button # 0
    private static Rect image_button_0 = new Rect();   
	public static Rect get_Image_Button_0() {return image_button_0; }
    public static void set_Image_Button_0(Rect iB0) { image_button_0 = iB0; }
        
    //Image button # 1
    private static Rect image_button_1 = new Rect();   
	public static Rect get_Image_Button_1() {return image_button_1; }
    public static void set_Image_Button_1(Rect iB1) { image_button_1 = iB1; }
    
    //Image button # 2
    private static Rect image_button_2 = new Rect();   
	public static Rect get_Image_Button_2() {return image_button_2; }
    public static void set_Image_Button_2(Rect iB2) { image_button_2 = iB2; }
    
    //Image button # 3
    private static Rect image_button_3 = new Rect();   
	public static Rect get_Image_Button_3() {return image_button_3; }
    public static void set_Image_Button_3(Rect iB3) { image_button_3 = iB3; }
    
    //Image button # 4
    private static Rect image_button_4 = new Rect();   
	public static Rect get_Image_Button_4() {return image_button_4; }
    public static void set_Image_Button_4(Rect iB4) { image_button_4 = iB4; }
    
    //Image button # 5
    private static Rect image_button_5 = new Rect();   
	public static Rect get_Image_Button_5() {return image_button_5; }
    public static void set_Image_Button_5(Rect iB5) { image_button_5 = iB5; }
    
    //Image button # 6
    private static Rect image_button_6 = new Rect();   
	public static Rect get_Image_Button_6() {return image_button_6; }
    public static void set_Image_Button_6(Rect iB6) { image_button_6 = iB6; }
    
    //Image button # 7
    private static Rect image_button_7 = new Rect();   
	public static Rect get_Image_Button_7() {return image_button_7; }
    public static void set_Image_Button_7(Rect iB7) { image_button_7 = iB7; }    

    //Image button # 8
    private static Rect image_button_8 = new Rect();   
	public static Rect get_Image_Button_8() {return image_button_8; }
    public static void set_Image_Button_8(Rect iB8) { image_button_8 = iB8; }    

    //Suggestion Row Text Size
    private static int suggestion_row_text_size = 18;   
	public static int getSuggestionRowTextSize() {return suggestion_row_text_size; }
    public static void setSuggestionRowTextSize(int tS) { suggestion_row_text_size = tS; }   
    
    //FloatingCurstos cType
    private static int cType;   
	public static int getCType() {return cType; }
    public static void setCType(int cT) { cType = cT; }
    
    //FloatingCurstos Last Known Web Hit Type
    private static int lastKnownHitType;

	public static int getLastKnownHitType() {return lastKnownHitType; }
    public static void setLastKnownHitType(int lKHT) { lastKnownHitType = lKHT; }
    
    //Amount of params from the ring to the tab
    public static int getAmountOfObjectsPerType(int cType){
    	int objs = 0;
    	switch (cType){
    		case WebHitTestResult.TYPE_TEXT_TYPE:	
    		case WebHitTestResult.TYPE_SRC_ANCHOR_TYPE:	
    		case TYPE_PADKITE_INPUT:
    		case TYPE_PADKITE_PANEL:
    		case TYPE_PADKITE_WINDOWS_MANAGER:
    		case TYPE_PADKITE_TAB:
    		case TYPE_PADKITE_ROW:
    		case TYPE_PADKITE_TIP_BUTTON:
    		case TYPE_PADKITE_BUTTON:
    		case TYPE_PADKITE_SERVER:
    			objs = 4;    			
    			break;    		      	
    	}
    	return objs; 	
    } 

    
    public static void cleanAllRects(){
    	
    	tab_0.left  	= 0;
    	tab_0.right 	= 0;
    	tab_0.top 		= 0;
    	tab_0.bottom	= 0;
    	
    	tab_1.left  	= 0;
    	tab_1.right 	= 0;
    	tab_1.top 		= 0;
    	tab_1.bottom	= 0;
    	
    	tab_2.left  	= 0;
    	tab_2.right 	= 0;
    	tab_2.top 		= 0;
    	tab_2.bottom	= 0;
    	
    	tab_3.left  	= 0;
    	tab_3.right 	= 0;
    	tab_3.top 		= 0;
    	tab_3.bottom	= 0;
    	
    	tab_4.left  	= 0;
    	tab_4.right 	= 0;
    	tab_4.top 		= 0;
    	tab_4.bottom	= 0;   
    	
    	TAB_CERO_RECT.left 		= 0;
    	TAB_CERO_RECT.right 	= 0;
    	TAB_CERO_RECT.top 		= 0;
    	TAB_CERO_RECT.bottom 	= 0;

    	TAB_FIRST_RECT.left 	= 0;
    	TAB_FIRST_RECT.right 	= 0;
    	TAB_FIRST_RECT.top 		= 0;
    	TAB_FIRST_RECT.bottom 	= 0;
    	
    	TAB_SECOND_RECT.left 	= 0;
    	TAB_SECOND_RECT.right 	= 0;
    	TAB_SECOND_RECT.top 	= 0;
    	TAB_SECOND_RECT.bottom 	= 0;
    	
    	TAB_THIRD_RECT.left 	= 0;
    	TAB_THIRD_RECT.right 	= 0;
    	TAB_THIRD_RECT.top 		= 0;
    	TAB_THIRD_RECT.bottom 	= 0;
    	
    	TAB_FOURTH_RECT.left 	= 0;
    	TAB_FOURTH_RECT.right 	= 0;
    	TAB_FOURTH_RECT.top 	= 0;
    	TAB_FOURTH_RECT.bottom 	= 0;
    	
    	if (BrowserActivity.isSuggestionActivated()){
    		
    		suggestion_row_0.left 	= 0;
    		suggestion_row_0.right 	= 0;
    		suggestion_row_0.top 	= 0;
    		suggestion_row_0.bottom = 0;
    		suggestion_row_0.left 	= 0;
    		
    		suggestion_row_0.left 	= 0;
    		suggestion_row_1.right 	= 0;
    		suggestion_row_1.top 	= 0;
    		suggestion_row_1.bottom = 0;
    		suggestion_row_1.left 	= 0;
    		
    		suggestion_row_3.left 	= 0;
    		suggestion_row_2.right 	= 0;
    		suggestion_row_2.top 	= 0;
    		suggestion_row_2.bottom = 0;
    		suggestion_row_2.left 	= 0;
    		
    		suggestion_row_3.left 	= 0;
    		suggestion_row_3.right 	= 0;
    		suggestion_row_3.top 	= 0;
    		suggestion_row_3.bottom = 0;
    		suggestion_row_3.left 	= 0;
    		
    		suggestion_row_4.left 	= 0;
    		suggestion_row_4.right 	= 0;
    		suggestion_row_4.top 	= 0;
    		suggestion_row_4.bottom = 0;
    		suggestion_row_4.left 	= 0;
    		
    		suggestion_row_5.left 	= 0;
    		suggestion_row_5.right 	= 0;
    		suggestion_row_5.top 	= 0;
    		suggestion_row_5.bottom = 0;
    		suggestion_row_5.left 	= 0;   		
    		
    		
    		suggestion_button_0.left 	= 0;
    		suggestion_button_0.right 	= 0;
    		suggestion_button_0.top 	= 0;
    		suggestion_button_0.bottom 	= 0;
    		
    		suggestion_button_1.left 	= 0;
    		suggestion_button_1.right 	= 0;
    		suggestion_button_1.top 	= 0;
    		suggestion_button_1.bottom 	= 0;
    		
    		suggestion_button_2.left 	= 0;
    		suggestion_button_2.right 	= 0;
    		suggestion_button_2.top 	= 0;
    		suggestion_button_2.bottom 	= 0;
    		
    		suggestion_button_3.left 	= 0;
    		suggestion_button_3.right 	= 0;
    		suggestion_button_3.top 	= 0;
    		suggestion_button_3.bottom 	= 0;
    		    		
    		suggestion_button_4.left 	= 0;
    		suggestion_button_4.right 	= 0;
    		suggestion_button_4.top 	= 0;
    		suggestion_button_4.bottom 	= 0;
    		
    		suggestion_button_5.left 	= 0;
    		suggestion_button_5.right 	= 0;
    		suggestion_button_5.top 	= 0;
    		suggestion_button_5.bottom 	= 0;
    		
    		
    		bottom_form_button_0.left 	= 0;
    		bottom_form_button_0.top 	= 0;
    		bottom_form_button_0.right 	= 0;
    		bottom_form_button_0.bottom = 0;    		
    		
    		bottom_form_button_1.left 	= 0;
    		bottom_form_button_1.top 	= 0;
    		bottom_form_button_1.right 	= 0;
    		bottom_form_button_1.bottom = 0;    		  		
    		
    	}
    }
	
}
