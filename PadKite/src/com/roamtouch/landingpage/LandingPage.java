package com.roamtouch.landingpage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.google.gson.Gson;
import com.roamtouch.swiftee.SwifteeApplication;

public class LandingPage {
		
	private static  String landing;	
	static ArrayList<String> twitter = new ArrayList<String>();
	static ArrayList<String> popSites = new ArrayList<String>();	
		
    public static boolean loadRemoteData(int resource, String search){    	
    	String url = null;    	
    	if (resource == 1 || resource == 2){
    		url = "http://padkite.com/app/json/"+search;    		
    	} else {
    		url = SwifteeApplication.getTwitterSearch()+search;
    	}   		
    	Log.v("URL", url);
        InputStream source = retrieveStream(url);        
        Gson gsonName = new Gson();      
        Reader nameReader = new InputStreamReader(source);        
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
	        	case 3:
	        		String User = result.fromUser;
		        	String ImageUrl = result.profileImageUrl;
		        	String both = User+"|"+ImageUrl;  
		        	//Log.v("BOTH", "both: " + both);
		        	twitter.add(both);
	        		break;        		
	        }    	
        } 
        return true;
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
    
    private static InputStream retrieveStream(String url) {    	
    	DefaultHttpClient client = new DefaultHttpClient();       
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

	public static String getLandingPageString() {	
		
		landing = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">"
			+"<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>"
			+"PadKite Start Page</title><script type=\"text/javascript\">"				
			+"function setFocus(id){"	
			//+"	pBridge.llega();"
			+"	URLType=id;"
			+"	document.forms['urlform'].elements['box'].focus();"
			+"	pBridge.searchType(id);"
			+"}"
			+"function handleKeyPress(e,form){"
			+"	var key=e.keyCode || e.which;"
			+"	if (key==13){"
			+"		form.submit();"
			+"	}"
			+"}"
			+"function getVerticalMargin(){"
			+"var ver = screen.width/6;"
			+"return ver;"
			+ "}"
			+"</script>"
			+"<style>"
			+"body {"
			+"	font-family: Arial, Helvetica, sans-serif;"
			+"}"
			+".logo {"
			+"	display: inline-block;"
			+"	float:right;"
			+"}"
			+".right, .left {"
			+"	width:90px;"
			+"}"
			+".right {"
			+"	margin-right:10px;"
			+"	text-align:right;"
			+"	float:left;"
			+"}"
			+".right a {"
			+"	display:block;"
			+"	color:#abd461;"
			+"	font-size:12px;"
			+"}"
			+".left a {"
			+"	display:block;"
			+"	color:#abd461;"
			+"	font-size:12px;"
			+"}"
			+".middle a {"
			+"	color:#007dc2;"
			+"	margin-right:10px;"
			+"}"
			+".top-links {"
			+"	display:block;"
			+"	margin-top: 5px;"
			+"}"
			+".small-links a {"
			+"	color:#007dc2;"
			+"	font-size:6px;"
			+"}"
			+".footer-links a {"
			+"	color: #999999;"
			+"	font-weight:bold;"
			+"	font-size:12px;"
			+"	text-decoration:none;"
			+"}"
			+".input {"
			+"	position:absolute;"
			+"	margin-left:\"200px\";"
			+"	font-size:15px;"
			+"	top:200px;"
			+"}"
			+".colum-title {"
			+"	color: #999999;"
			+"	font-size:9px;"
			+"}"
			+"</style>"
			+"</head>"
			+"<body>\">"
			+"<br />"
			+"<div>" 
			+"<div class=\"logo\" style=\"text-align:right\">"
			+"<a href=\"http://padkite.com/start\">"
			+"<img src=\"http://padkite.com/app/padkite-brand.gif\" alt=\"PadKite Logo\" width=\"125\" height=\"61\" border=\"0\" /></a>"
			+"	<div class=\"top-links\">"      
			+"		<div class=\"right\">" 
			+"			<a href=\"http://padkite.com\">Home</a>"
			+"			<a href=\"http://padkite.com/user-guides\">User Guides</a>" 
			+"			<a href=\"http://padkite.com/blog\">Blog</a>" 
			+"			<a href=\"http://padkite.com/forum\">Forums</a>"
			+"		</div>"      
			+"		<div class=\"left\" style=\"float:left;text-align:left;\">" 
			+"			<a href=\"http://padkite.com/contact-us\">Contact Us</a>" 
			+"			<a href=\"http://padkite.com/faq\">FAQ</a>" 
			+"			<a href=\"http://padkite.com/beta-program\">Beta Program</a>"
			+"			<a href=\"#\">Press</a>" 
			+"		</div>"    
			+"	</div>"  
			+"	</div>"
			+"	</div>"
			+"	<br clear=\"all\"/>"
			+"	<div class=\"middle\" align=\"center\" style=\"padding-top:15px;\">"
			+"		<div style=\"padding:10px;\">" 
			+"			<a href=\"#\" name=\"URLink\" onclick=\"setFocus(1)\">URL</a>" 
			+"			<a href=\"#\" name=\"SearchLink\" onclick=\"setFocus(2)\">Search</a>" 
			+"			<a href=\"#\" name=\"ImageLink\" onclick=\"setFocus(3)\">Image</a>" 
			+"			<a href=\"#\" name=\"VideoLink\" onclick=\"setFocus(4)\">Video</a><br>"			 
			+"		</div>" 		 
			+"	<form id=\"urlform\" action=\"#\" onSubmit=\"return load();\" onkeypress=\"handleKeyPress(event,this.form)\">"    
			+"		<input name=\"box\" class=\"input\" type=\"text\" size=\"40\"></input><br>" //Isolating the input in order to have the "Go" alone. 			
			+"	</form><br />";
		  for (int i = 0; i < twitter.size(); i++) {
		    	String tw = twitter.get(i);		    	
		    	StringTokenizer tTok = new StringTokenizer(tw, "|");
		    	String name = tTok.nextToken();// this will contain "Fruit"
		    	String url = tTok.nextToken();// this will contain " they taste good"    		    	
		    	landing += "<a href=\""+url+"\"><img src=\""+url+"\" title=\""+name+" width=\"20\" height=\"20\" border=\"0\"></a>";  	
		    }		
			landing += "<div align=\"center\">"  
			+"		<table border=\"0\" height=\"30\" width=\"100%\" class=\"small-links\" cellspacing=\"10\">"
			+"		<tr valign=\"top\">"   
			+"			<td align=\"center\">"
			+"				<div class=\"colum-title\">Bookmarks</div>" 
			+"					<a href=\"padkite.local.contact?id=bien\">Sample_1</a><br>"  
			+"					<a href=\"padkite.local.contact?id=bien\">Sample_2</a><br>"  
			+"					<a href=\"padkite.local.contact?id=bien\">Sample_3</a><br>"  
			+"					<a href=\"padkite.local.contact?id=bien\">Sample_4</a><br>"  
			+"			</td>"      
			+"			<td align=\"center\">"   			
			+"				<div class=\"colum-title\">History</div>" 
			+"					<a href=\"padkite.local.contact?id=bien\">Sample_1</a><br>"
			+"					<a href=\"padkite.local.contact?id=bien\">Sample_2</a><br>"  
			+"					<a href=\"padkite.local.contact?id=bien\">Sample_3</a><br>"  
			+"					<span style=\"text-decoration: none\"><a href=\"padkite.local.contact?id=bien\">Sample_4</a></span><br>"  
			+"			</td>"      			    
			+"			<td align=\"left\">";
			int popSize = popSites.size();
			for (int j = 0; j < popSize/2; j++) {
		    	String pop_1 = popSites.get(j);		    	
		    	StringTokenizer pTok_1 = new StringTokenizer(pop_1, "|");
		    	String pPage_1 = pTok_1.nextToken();
		    	String pUrl_1 = pTok_1.nextToken(); 		    	
		    	//String pTooltip_1 = pTok_1.nextToken();		    			    	
		    	landing += "<a href=\""+pUrl_1+"\">"+pPage_1+"</a><br>";		    		
		    	   
			} 	
			landing += "</td>"      
			+"			<td align=\"left\">";
			for (int k = popSize/2; k < popSize; k++) {
				String pop_2 = popSites.get(k);		    	
		    	StringTokenizer pTok_2 = new StringTokenizer(pop_2, "|");
		    	String pPage_2 = pTok_2.nextToken();
		    	String pUrl_2 = pTok_2.nextToken(); 		    	
		    	//String pTooltip_2 = pTok_2.nextToken();		    			    	
		    	landing += "<a href=\""+pUrl_2+"\">"+pPage_2+"</a><br>";	    				    	   
			}	 
			landing += "</td>"    
			+"		</tr>"  
			+"		</table>"
			+"		<img src=\"http://padkite.com/app/Roamtouch-logo.jpg\" width=\"120\" height=\"42\" align=\"left\">"
			+"		<img src=\"http://padkite.com/app/gesture-kit-bg.jpg\" width=\"80\" height=\"50\" align=\"right\">"
			+"		<div class=\"footer-links\"><a href=\"#\">PadKite Ball Game</a><br></div>"
			+"	</div>"
			+"</body>"
			+"</html>";	
			
			Log.v("VER", landing);
			
		return landing;
	}
    
}