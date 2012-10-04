//******************************************************************************** 
//**	Copyright (c) 2011, Roaming Keyboards LLC doing business as RoamTouch®	**	       
//**	All rights reserved.													**
//********************************************************************************
package com.roamtouch.landingpage;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;


import com.google.gson.Gson;
import com.roamtouch.database.DBConnector;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.utils.Base64;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;


public class LandingPage {

	private static  String landing = "";
	public static ArrayList<String> popSites = new ArrayList<String>();
	public static ArrayList<String> history = new ArrayList<String>();
	public static ArrayList<String> windows_set = new ArrayList<String>();
		
	SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a");
	
	private static BrowserActivity mParent = null;	
	
	private SwifteeApplication appState;
	
	DBConnector database;
	
	public LandingPage(BrowserActivity parent) 	{		
		mParent = parent;	
		database = appState.getDatabase();
	}
	
	public String generateLandingPageString() {	
		
		String padkite = "PadKite is the first multitouch mobile mouse that allows " +
				"you to quickly identify the content below your finger and fast operate " +
				"with gestures to perform actions. Drag the circle to start operating. Have fun!.";
		
		String mailBoby = "Hi,\n\n" +
				"My feedback is\n\n" +
				"Regards,\n Customer";
		
		String panelUid = "sdfsadfds"; 
		String serverUid = "sdfsadfds";
		String moreUid = "sdfsadfds";
		String panelDesc = "PERSONALIZED PANEL";
					
		int id = 1;
		
		/*String tabData = "?tabName=EXTRAS" 
				+"&title0=Public%20Relations"
				+"&url0=www.publicrelations.com"
				+"&title1=Legal%20Information"
				+"&url1=www.legalinformation.com"
				+"&title2=Website%20Feedback"
				+"&url2=www.websitefeedback.com";*/	
		
		//+"<br><br><br><div align=\"center\"><a href=\"padkite.local.tabs#"+tabData+"\">hello this is a test to try the extra tab loaded</a></h2></div><br>"
		
		//String panelImagePath = BrowserActivity.IMAGES_ASSETS_PATH+"/personalized_landing.png";
		String logoImagePath = BrowserActivity.IMAGES_ASSETS_PATH+"/padkite_logo_landing.png";
		String iconImagePath = BrowserActivity.IMAGES_ASSETS_PATH+"/icon_landing.png";		
		
		landing += "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n"
				
			+"<html>\n"			
			+"<head>\n"
			+"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"			
			+"<title>PadKite Landing Page</title>\n"					
		   	
			
			/**STYLES**/		
			+"<link href=\"style.css\" rel=\"stylesheet\" type=\"text/css\"/>\n"			
			
			/**SCRIPT**/	
			+"<script type=\"text/javascript\" src=\"bridge.js\"></script>\n"		
			
			+"</head>\n"			
			
			/**BODY**/
			+"<body onload=\"bodyLoaded()\">\n"
			
			//Main Container
			+"<div id=\"container\">\n"
			
			//Header
			+"	<div id=\"container-header\">"			
			+"		<p class=\"header-left\">PadKite Beta 2.0 experimental version.</p>"				
			+"		<p id=\"page-number\" class=\"header-right\">Landing Page: <b>1</b></p>"						
			+"	</div>"	

			//FloatingCursor
			+"	<div id=\"fc-container\">\n"			
			+"		<div id=\"fc-left\">\n"		
			+"			<a id=\"logo-link\" href=\"http://www.padkite.com\">\n"
			+"				<img id=\"logo\" src=\""+logoImagePath+"\">\n"			
			+"			</a>\n"					
			+"		<p class=\"thepower\">the power of lifting</p>"	
			+"		</div>\n"
			+"		<div id=\"fc-right\">\n"	
			+"			<div id=\"padkite-online-header\">\n"
			+"				<p>online</p><img src=\""+iconImagePath+"\"/>\n"
			+"			</div>\n"			
			+"			<div id=\"padkite-online-links\">\n"			
			+"				<a class=\"pk-online-links\" href=\"http://www.padkite.com/beta-program\">beta</a>\n"				
			+"				<a class=\"pk-online-links\" href=\"http://www.padkite.com/user-guides\">guides</a>\n"
			+"				<a class=\"pk-online-links\" href=\"http://www.padkite.com/faq\">faq</a><br>\n"
			+"				<a class=\"pk-online-links\" href=\"http://www.padkite.com/blog\">blog</a>\n"			
			+"				<a class=\"pk-online-links\" href=\"http://www.padkite.com/contact-us\">contact</a><br>\n"
			+"				<a class=\"pk-online-links\" href=\"http://www.padkite.com/remote\">remote</a>\n"
			+"			</div>\n"	
			+"		</div>\n"
			+"		<div id=\"fc-middle\">\n"			
			+"		</div>\n"					
			+"	</div>\n"
			
			//Input
			+"	<div id=\"input-container\">\n"		
			+"		<form id=\"urlform\" action=\"#\" onsubmit=\"return currentSearch()\">\n"
			+"			<input id=\"pk-input\" name=\"box\" type=\"text\"" 
			+"			onkeypress=\"handleKeyPress(event)\"/>"  // dblclick=\"toogleFCContainer()\"/>" //	onFocus=\"handleFocus(event)\" onBlur=\"handleFocusLost(event)\"/>"					
			+"		</form>\n"		
			+"	</div>\n"		
			
			//Panel
			+"	<div id=\"panel-container\">\n"					
			+"		<a id=\"panel-link\" class=\"panel-link\" href=\"padkite.local.panel#"+panelUid+"\">\n"
			+"			<div id=\"cont-per\"><div id=\"panel-div\"><p id=\"panel-text\">" + panelDesc + "</p></div></div>\n"
			+"		</a>\n"				
			+"	</div>\n"
			
			//Body
			+"	<div id=\"body-container\">\n"	
			+"		<div id=\"body-left\">\n"
			+"			<div id=\"body-my-header-left\"><p class=\"body-header\">my windows</p></div>\n"
			+"			<div id=\"body-my-body-left\">\n"		
			+"				<a class=\"pk-online-links\" href=\"padkite.local.windows#1\">beta</a>\n";		
						String myWindowsSets = generateWindowsSets(1);
						landing += myWindowsSets									
			+"			</div>\n" 
			+"		</div>\n"
			+"		<div id=\"body-right\">\n"
			+"			<div id=\"body-my-header-right\"><p class=\"body-header\">popular sites</p></div>\n"
			+"			<div id=\"body-my-body-right\">\n";					
						String popularString = generatePopularSitesString();			
						landing += popularString					
			+"				<a class=\"pklink\" href=\"padkite.local.more#"+moreUid+"\">More...</a><br>\n"
			+"			</div>\n" 
			+"		</div>\n"
			+"		<div id=\"body-center\">\n"
			+"			<div id=\"body-my-header-center\"><p class=\"body-header\">default windows</p></div>\n"
			+"			<div id=\"body-my-body-center\">\n";						
							String defaultWindowsSets = generateWindowsSets(0);
							landing += defaultWindowsSets							
			+"			</div>\n" 
			+"		</div>\n"		
			+"	</div>\n"
			
			//Remote Control
			+"	<div id=\"remote-container\">\n"
			+"		<div id=\"remote-control\">\n"			
			+"			<p>remote control</p>\n"
			+"			<a href=\"padkite.local.server#"+serverUid+"\">\n"				
			+"				<font>"+SwifteeApplication.getIpAdress()+":8887</font>\n"				
			+"			</a>\n"	
			+"			<p id=\"server-status\">server stopped</p>\n"
			+"			<p id=\"client-status\">client dissconected</p>\n"
			+"		</div>\n"				
			+"	</div>\n"
			
			//Feedback
			+"	<div id=\"feedback-container\">\n"			
			+"		<a class=\"feedback-link\" href=\"mailto:feedback@roamtouch.com?&subject=PadKite Feddback\">send feedback</a>\n"			
			+"	</div>\n"
			
			//Footer
			+"	<div id=\"footer-container\">\n"						
			+"		<p class=\"footer-text\">Copyright © 2010 by RoamTouchT All rights reserved.</p>\n"		
			+"	</div>\n"
			
			+"</div>\n"
			+"</body>\n"
			+"</html>\n";				
			
		return landing;
	}	
	
	public String getLandingString(){
		return landing;
	}
	
	public void setLandingString(String lp){
		landing = lp;
	}
	
	public String generatePopularSitesString(){	
		String popularSites = "";
		int popSize = popSites.size();		
		if (popSize>0){					
			//for (int i = 0; i < popSize ; i++) {
			for (int i = 0; i < 9 ; i++) {
				String pop = popSites.get(i);		    	
		    	StringTokenizer pTok = new StringTokenizer(pop, "|");
		    	String pPage = pTok.nextToken();
		    	String pUrl = pTok.nextToken();	
		    	popularSites += "<a class=\"pklink\" href=\""+pUrl+"\">"+pPage+"</a><br>\n";				 				
			}
		}		
		return popularSites;
	}
	
	public String generateWindowsSets(int defa){		
		String windowsSets = "";		
		Cursor wsC = database.getWindowsSets(defa);	
		if (wsC!=null){			
			int count = wsC.getCount();
			wsC.moveToPosition(0);    	
	    	int num = 0;	    	    	
			while (!wsC.isAfterLast() && num < count){			
	 			String id = wsC.getString(0);
	 			String name = wsC.getString(1);	 			
	 			windowsSets += "<a class=\"pklink\" href=\"padkite.local.windows#"+id+"\">"+name+"</a><br>\n";	 			
	 			wsC.moveToNext();	 							
			}			
			wsC.close();	    		   	
    	}    
		return windowsSets;	
	}
	
	/*public String generatePopularSitesString(){	
		String popularSites = "";
		int popSize = popSites.size();
		int block = popSize/3;	
		boolean block1=true, block2=true, block3=true;
		if (popSize>0){
			popularSites += "<div><table><tr align=\"center\"><font size=2>";		
			for (int j = 0; j < popSize ; j++) {
				String pop = popSites.get(j);		    	
		    	StringTokenizer pTok = new StringTokenizer(pop, "|");
		    	String pPage = pTok.nextToken();
		    	String pUrl = pTok.nextToken();		
				if (j<block){
					if (block1){
						popularSites += "<td>";
						block1=false;
					}					
					popularSites += "<a href=\""+pUrl+"\">"+pPage+"</a><br>";
					if (j==(block-1)){
						popularSites += "</td>";
					}
				}
				if ( j<(block*2) && j>block){
					if (block2){
						popularSites += "<td>";
						block2=false;
					}					
					popularSites += "<a href=\""+pUrl+"\">"+pPage+"</a><br>";
					if (j==((block*2)-1)){
						popularSites += "</td>";
					}
				}
				if (j<popSize && j>(block*2)){
					if (block3){
						popularSites += "<td>";
						block3=false;
					}					
					popularSites += "<a href=\""+pUrl+"\">"+pPage+"</a><br>";
					if (j==(popSize-1)){
						popularSites += "</td>";
					}
				}
			}
			popularSites += "</font></tr></table></div>";
		}		
		return popularSites;
	}*/
	
	/*public String generateWindowsSets(){	
		String windowsSets = "";		
		Cursor wsC = database.getWindowsSets();	
		if (wsC!=null){			
			int count = wsC.getCount();
			wsC.moveToPosition(0);    	
	    	int num = 0;	    	    	
			while (!wsC.isAfterLast() && num < count){			
	 			String id = wsC.getString(0);
	 			String name = wsC.getString(1);		 			
	 			windowsSets += "<a href=\"padkite.local.windows#"+id+"\">"+name+"</a><br>";	 			
	 			wsC.moveToNext();	 							
			}			
			wsC.close();	    		   	
    	}    
		return windowsSets;
	}*/
	
	private static boolean isNullOrBlank(String s){
	  return (s==null || s.trim().equals("") || s.trim().equals("null"));
	}
	
	public boolean loadRemoteData(int resource, String search){    	
    	String url = null;    	
    	if (resource == 1 || resource == 2 || resource == 3){
    		url = "http://roamtouch.com/padkite/app/json/"+search;    		
    	} //else {
    		//url = SwifteeApplication.getTwitterSearch()+search;
    	//}   		
    	Log.v("URL", url);
        InputStream source = retrieveStream(url);        
        Vector wV = new Vector();
        boolean managerCheck = false;
        if(source == null) {
        	return false;
        }
        Gson gsonName = new Gson();      
        Reader nameReader = new InputStreamReader(source);   
        if (nameReader==null){
        	return false;
        }
        NameResponse responseName = gsonName.fromJson(nameReader, NameResponse.class);         
        List<Result> resultsName = responseName.results;         
        //if (responseName.results.size()<6 && resource == 3){ return false; }   
        int k = 0;
        for (Result result : resultsName) {
        	switch (resource) {
	        	case 1:
	        		String sett = null;
	        		sett = result.r_setting;
	        		String data = null;
	        		data = result.r_data;      	  
	        		setSettings(sett, data);	            	
	        		break;
	        	case 2: 
	        		String pPage = null;
	        		pPage = result.p_Page;
	        		String pUrl = null;
	        		pUrl = result.p_Url;      
	        		String pTooltip = null;
	        		pTooltip = result.p_Tooltip;
	        		String all = pPage+"|"+pUrl+"|"+pTooltip;
	            	//Log.v("ENTRA", "all: "+all);
	            	popSites.add(all);
	        		break;
	        	case 3:
	        		String wSet = result.w_Set;        	
	        		String wTitle = result.w_Title;
	        		String wUrl = result.w_Url;
	        		String wBitmap = result.w_Bitmap;
	        		Object[] wData = new Object[4];
	        		wData[0] = wSet;
	        		wData[1] = wTitle;
	        		wData[2] = wUrl;
	        		wData[3] = wBitmap;
	        		wV.add(wData);
	        		managerCheck = true;
	        		break;        		
	        }        	
        } 
        if (managerCheck){
        	
		    for (int i=0; i<wV.size(); i++){
		    	
				Object[] data = (Object[])wV.get(i);
				String setName = (String) data[0];    
				int checkWindowsSet = database.existWindowsSetByName(setName);				
				String sTitle = (String) data[1];
				String sUrl = (String) data[2];
				String sBitmap = (String) data[3];    
				int setId = 0;
				if (checkWindowsSet < 0 ) {
					setId = database.insertWindowSet(setName, 0);					
				} else {
					setId = checkWindowsSet;
				}
				boolean exist = database.getWindowsManagerListByIdAndTitle(setId, sTitle);
				if (!exist){
					Cursor c = database.insertTabs(setId, sTitle, sUrl, sBitmap);
					/*if (c!=null){			
						int count = c.getCount();
						c.moveToPosition(0);
						int num = 0;			
						while (!c.isAfterLast() && num < count){			
							String id = c.getString(0);
							String title = c.getString(1);
							String ur = c.getString(2);
							num++;						
							c.moveToNext();		
						}		
						c.close();
					}*/
				}
			}
        }
        return true;
    }
	
	public boolean remoteConnections(){		
		boolean as = false;
		boolean pop = false;		
		boolean ws = false;
		
		as = loadRemoteData(1, "urlAssets.json");		
		if (as){ pop = loadRemoteData(2, "popularSites.json"); }
		if (pop){ ws = loadRemoteData(3, "windowSets.json"); }
		
		//if (pop){ tw = loadRemoteData(3, SwifteeApplication.twitter_key); }
		
		if(ws)return ws;
		else return false;
	}   
    
    private static void setSettings(String sett, String data){
    	if (sett.equals("message") && !data.equals("")){ 
    		//DIALOG HERE
    	}    	
    	if (sett.equals("twitter_search")){    	
    		SwifteeApplication.setTwitterSearch(data);
    	} else if (sett.equals("twitter_key")) {
    		SwifteeApplication.setTwitterKey(data);   	
    	} else if (sett.equals("google_search")) {
    		SwifteeApplication.setGoogleSearch(data);    
    		SwifteeApplication.setActiveSearch(SwifteeApplication.WEB_SEARCH);
    	} else if (sett.equals("google_image")) {
    		SwifteeApplication.setImageSearch(data);
    	} else if (sett.equals("youyube_search")) {
    		SwifteeApplication.setYouTubeSearch(data);
    	} else if (sett.equals("google_suggestion")) {
    		SwifteeApplication.setGoogleSuggestion(data);
    	} else if (sett.equals("wiki_suggestion")) {   		
    		String lang = Locale.getDefault().getLanguage();		
    		data = "http://" + lang + data;   		    		
    		SwifteeApplication.setWikipediaSuggestion(data);
    	} else if (sett.equals("youtube_suggestion")) {   		
    		SwifteeApplication.setYouTubeSuggestion(data);
    	} else if (sett.equals("youtube_watch")) {   		
    		SwifteeApplication.setYouTubeWatch(data);
    	} else if (sett.equals("wiki_search")) {   		
    		SwifteeApplication.setWikiSearch(data);
    	} else if (sett.equals("infochimps_wikipedia")) {   		
    		SwifteeApplication.setWikiAbstract(data);
    	}
    }
    
    private InputStream retrieveStream(String url) {  
		  HttpParams params = new BasicHttpParams();
	      HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
	      HttpConnectionParams.setSoTimeout(params, 10 * 1000);
	      HttpConnectionParams.setSocketBufferSize(params, 8192);
	      DefaultHttpClient client = new DefaultHttpClient(params);     	   
        HttpGet getRequest = new HttpGet(url);         
        try {           
           HttpResponse getResponse = client.execute(getRequest);
           final int statusCode = getResponse.getStatusLine().getStatusCode();           
           if (statusCode != HttpStatus.SC_OK) { 
              //Log.w(getClass().getSimpleName(), "Error " + statusCode + " for URL " + url); 
              return null;
           }
           HttpEntity getResponseEntity = getResponse.getEntity();
           return getResponseEntity.getContent();           
        } 
        catch (IOException e) {
           getRequest.abort();
           //Log.w(getClass().getSimpleName(), "Error for URL " + url, e);
        }        
        return null;       
    };	
       
    public static boolean fileExists(Context context, String f) {
        String[] filenames = context.fileList();
        for (String name : filenames) {
          if (name.equals(f)) {
            return true;
          }
        }
        return false;
    };  
    
    public static void setHistory(ArrayList<String> hist){ 
    	history = hist;
    }
    
   
  
}; //End of LandingPage Class


