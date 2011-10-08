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
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


import com.google.gson.Gson;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.SwifteeApplication;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;


public class LandingPage {

	private static  String landing = "";
	public static ArrayList<String> popSites = new ArrayList<String>();
	public static ArrayList<String> history = new ArrayList<String>();
	
		
	SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a");
	
	private static BrowserActivity mParent = null;	
	
	
	public LandingPage(BrowserActivity parent) 	{		
		mParent = parent;				
	}
	
	public String generateLandingPageString() {	
		
		String padkite = "PadKite is the first multitouch mobile mouse that allows " +
				"you to quickly identify the content below your finger and fast operate " +
				"with gestures to perform actions. Drag the circle to start operating. Have fun!.";
		
		String mailBoby = "Hi,\n\n" +
				"My feedback is\n\n" +
				"Regards,\n Customer";		
					
		
		landing += "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">"
			+"<html>"
			+"<head>"
			+"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
			+"<title>Landing Page</title>"
			+"<script type=\"text/javascript\">"
			+"var type;"			
			+"function load(){"
			+"	var theUrl = document.forms[\"urlform\"].elements[\"box\"].value;"			
			+"	pBridge.currentType(type);"
			+"	pBridge.currentSearch(theUrl);"			
			+"	document.forms['urlform'].elements['box'].value=\"\";"
			+"	type=\"\";"
			+"	theUrl=\"\";"
			+"	return true;"
			+"}"		
			+"function sendType(t){"
			+"	type = t;"
			+"	document.forms['urlform'].elements['box'].focus();"
			+"}"
			+"</script>"
			+"</head>"
			+"<body>"
			+"<br />"
			+"<div class=\"logo\" style=\"text-align: center;\">"
				+"<a href=\"http://padkite.com/start\">"
					+"<img src=\""
					+Environment.getExternalStorageDirectory()+"/PadKite/Web Assets/PadKite-Logo.png\" alt=\"PadKite Logo\" width=\"150\" />"
				+"</a>"
			+"</div>"
			+"<br />"
			+"<div align=\"center\">"			
			//+"	<form id=\"urlform\" action=\"#\" onSubmit=\"return loadSearch()\">\n"			
			+"<form id=\"urlform\" action=\"#\" onsubmit=\"return load()\">"				
			+"<label>Enter address</label>     <a href=\"#\" name=\"ImageLink\" onclick=\"sendType('image')\">Image</a>    <a href=\"#\" name=\"VideoLink\" onclick=\"sendType('video');\">Video</a>\n"			
			//+"<input type=\"text\" name=\"url\"></input><br>"		
			+"<input name=\"box\" class=\"input\" type=\"text\" size=\"30\" height=\"10\"></input>"
			//+"<input type=\"button\" value=\"Load Webpage\" action=\"#\" onclick=\"return load()\"></input>"			
			+"</form>"			
			//+"<br />"
			//+"<a href=\"http://padkite.com/start\">PadKite Homepage</a><br><br>"
			+"</div>"				
			+"<p align=\"center\"><font size=\"2\">"+padkite+"</font></p>"			
			+"<div class=\"cuadro\" align=\"center\"><h4>Popular Sites</h4><ul class=\"book\"></div>";						
			String popularString = generatePopularSitesString();			
			landing += popularString		
			//+"<br><table><p align=\"center\"><a href=\"mailto:feedback@roamtouch.com?subject='Feedback'&body='hola'>Send Feedback</a></p>" //"+mailBoby+"'
			+"<br><table><p align=\"center\"><A HREF=\"mailto:feedback@roamtouch.com?&subject=PadKite Feddback\">Send feedback</A></p>" //"+mailBoby+"'
			+"<p align=\"center\"><font size=\"1\">423 Broadway #522 Millbrae, California 94030-1905</font><br>"
			+"<font size=\"1\" align=\"center\">Copyright © 2010 by RoamTouch™ All rights reserved.</font></p>"			
			+"</table>"
			+"</body>"
			+"</html>";			
			
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
	}
		
	private static boolean isNullOrBlank(String s){
	  return (s==null || s.trim().equals("") || s.trim().equals("null"));
	}

		
	public boolean loadRemoteData(int resource, String search){    	
    	String url = null;    	
    	if (resource == 1 || resource == 2){
    		url = "http://roamtouch.com/padkite/app/json/"+search;    		
    	} //else {
    		//url = SwifteeApplication.getTwitterSearch()+search;
    	//}   		
    	Log.v("URL", url);
        InputStream source = retrieveStream(url); 
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
	        	/*case 3:
	        		String User = result.fromUser;
		        	String ImageUrl = result.profileImageUrl;
		        	String both = User+"|"+ImageUrl;  
		        	Log.v("BOTH", "both: " + both);
		        	twitt.add(both);
	        		break;*/        		
	        }    	
        } 
        return true;
    }
	
	public boolean remoteConnections(){		
		boolean as = false;
		boolean pop = false;		
		boolean tw = false;
		as = loadRemoteData(1, "urlAssets.json");
		if (as){ pop = loadRemoteData(2, "popularSites.json"); }
		//if (pop){ tw = loadRemoteData(3, SwifteeApplication.twitter_key); }
		if(as)return as;
		else return false;
	}   
    
    private static void setSettings(String sett, String data){
    	if (sett.equals("message") && !data.equals("")){ 
    		//DIALOG HERE
    	}    	
    	if (sett.equals("twitter_search")){
    		//Log.v("RES","twitter_search");
    		SwifteeApplication.setTwitterSearch(data);
    	} else if (sett.equals("twitter_key")) {
    		//Log.v("RES","twitter_key");
    		SwifteeApplication.setTwitterKey(data);   	
    	} else if (sett.equals("google_search")) {
    		//Log.v("RES","google_search");
    		SwifteeApplication.setGoogleSearch(data);    		
    	} else if (sett.equals("google_image")) {
    		//Log.v("RES","google_image");
    		SwifteeApplication.setImageSearch(data);
    	} else if (sett.equals("youyube_search")) {
    		//Log.v("RES","youyube_search");
    		SwifteeApplication.setYouTubeSearch(data);
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


