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

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.roamtouch.landingpage.Result;
import com.roamtouch.landingpage.NameResponse;
import com.roamtouch.view.WebPage;

public class LandingPage {
		
	private static  String landing;
	static ArrayList<String> twitter = new ArrayList<String>();
	static ArrayList<String> popUrl = new ArrayList<String>();
	static ArrayList<String> history = new ArrayList<String>();
	
    public static void loadTwitterSearch(String search){
    	String url = "http://search.twitter.com/search.json?q="+search;
    	//String url = "http://search.twitter.com/search.json?q=josevigil";
        InputStream source = retrieveStream(url);        
        Gson gsonName = new Gson();        
        Reader nameReader = new InputStreamReader(source);        
        NameResponse responseName = gsonName.fromJson(nameReader, NameResponse.class);         
        List<Result> resultsName = responseName.results;
        for (Result result : resultsName) {        	
        	String User = result.fromUser;
        	String ImageUrl = result.profileImageUrl;
        	String both = User+"|"+ImageUrl;       
        	twitter.add(both);
        };   	   
    }       
    
    public static void loadLandingPageHistroy(Context context){
    	String history = WebPage.getLandingPageHistory(context);
    	Log.v("HISTORY", "HT: "+history);
    	
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
     }

	public static String getLandingPageString() {	
		
		landing = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">"
			+"<html>"
			+"<head>"
			+"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
			+"<title>PadKite Start Page</title>"
			+"<script type=\"text/javascript\">"
			+"function load(){"
			+"    var theUrl = document.getElementById('urlform').url.value;"
			+"    if (theUrl != \"\"){"
			+"            //location.href = theUrl;"
			+"            //window.navigate(theUrl);"
			+"            var i = theUrl.search(\"http://\")"
			+"            if(i == -1)"
			+"	    {"
			+"            	var i = theUrl.search(\"\\.\");"
			+"		if (i != -1)"
			+"	            	window.document.location.href=\"http://\"+theUrl;"
			+"		else"
			+"	            	window.document.location.href=\"http://www.google.com/m/search?q=\"+theUrl;"
			+"            }"
			+"            else"
			+"            	window.document.location.href=theUrl;"
			+"    }"
			+"	return false;"
			+"}"			
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
			+"</style>"
			+"</head>"
			+"<body onLoad=\"pBridge.landingIsLoaded(true)\">"
			+"<br />"
			+"<div>"
			+"  <div class=\"logo\" style=\"text-align:right\"><a href=\"http://padkite.com/start\"> <img src=\"http://padkite.com/app/padkite-brand.gif\" alt=\"PadKite Logo\" width=\"125\" height=\"61\" border=\"0\" /></a>"
			+"    <div class=\"top-links\">"
			+"      <div class=\"right\"> <a href=\"http://padkite.com\">Home</a> <a href=\"http://padkite.com/user-guides\">User Guides</a> <a href=\"http://padkite.com/blog\">Blog</a> <a href=\"http://padkite.com/forum\">Forums</a></div>"
			+"      <div class=\"left\" style=\"float:left;text-align:left;\"> <a href=\"http://padkite.com/contact-us\">Contact Us</a> <a href=\"http://padkite.com/faq\">FAQ</a> <a href=\"http://padkite.com/beta-program\">Beta Program</a> <a href=\"#\">Press</a> </div>"
			+"    </div>"
			+"  </div>"
			+"</div>"
			+"<br clear=\"all\"/>"
			+"<div class=\"middle\" align=\"center\" style=\"padding-top:15px;\">"
			+"  <form id=\"urlform\" action=\"#\" onSubmit=\"return load();\">"
			+"    <div style=\"padding:10px;\"> <a href=\"#\">URL</a> <a href=\"#\">Search</a> <a href=\"#\">Image</a> <a href=\"#\">Video</a><br>"
			+"    </div>"
			+"    <input name=\"url\" type=\"text\" size=\"40\">"
			+"    </input>"
			+"    <br>"
			+"  </form>"
			+"  <br />";
		    for (int i = 0; i < twitter.size(); i++) {
		    	String tw = twitter.get(i);		    	
		    	StringTokenizer tokens = new StringTokenizer(tw, "|");
		    	String name = tokens.nextToken();// this will contain "Fruit"
		    	String url = tokens.nextToken();// this will contain " they taste good"    		    	
		    	landing += "<a href=\""+url+"\"><img src=\""+url+"\" title=\""+name+" width=\"20\" height=\"20\" border=\"0\"></a>";  	
		    }		
			landing += "</div>"                                                          
			+"<div align=\"center\">"                                            
			+"  <table border=\"0\" height=\"30\" width=\"360\" class=\"small-links\" cellspacing=\"10\">"
			+"    <tr valign=\"top\">"                                           
			+"      <td align=\"center\">"                                       
			+"        <a href=\"padkite.local.contact?id=bien\"> Facebook</a><br>"
			+"        <a href=\"http://twitter.com/\">Twitter</a> <a href=\"http://www.linkedin.com\"> LinkedIn</a><br>"
			+"        <a href=\"http://www.flickr.com\">Flickr</a><br>"          
			+"        <a href=\"http://www.apple.com\"> Apple</a><br>"           
			+"      </td>"                                                     
			+"      <td align=\"center\">"                                       
			+"        <a href=\"http://www.craigslist.org\"> Craigslist</a>"     
			+"        <a href=\"http://www.wordpress.com\"> Wordpress</a><br>"   
			+"        <a href=\"http://www.ask.com\">Ask</a> <a href=\"http://www.blogspot.com\"> BlogSpot</a><br>"
			+"        <a href=\"http://www.qq.com\">Qq</a> <a href=\"http://www.cnnmobile.com/\">CNN</a><br>"
			+"      </td>"                                                     
			+"      <td align=\"center\">"                                       
			+"        <a href=\"http://m.espn.go.com/wireless/index?w=1ajv8&i=COM\"> ESPN</a>"
			+"        <a href=\"http://www.nytimes.com/\">NY Times</a><br>"      
			+"        <a href=\"http://hp.mobileweb.ebay.com/home\">eBay</a> <a href=\"http://google.com/\">Google</a><br>"
			+"        <a href=\"http://picasaweb.google.com/m/viewer?source=androidclient\"> Picassa</a> <span style=\"text-decoration: none\"> <a href=\"http://www.m.amazon.com/\">Amazon</a></span><br>"
			+"      </td>"                                                     
			+"      <td align=\"center\">"                                       
			+"        <a href=\"http://mw.weather.com/\">Weather Channel</a>"    
			+"        <a href=\"http://www.wikipedia.org/\">Wikipedia</a><br>"   
			+"        <a href=\"http://www.bing.com/\">Bing</a> <a href=\"http://m.yahoo.com/\">Yahoo</a><br>"
			+"        <a href=\"http://extreme.mobile.msn.com/\">MSN</a>"        
			+"      </td>"                                                     
			+"    </tr>"                                                       
			+"  </table>"                                                      
			+"<img src=\"http://padkite.com/app/Roamtouch-logo.jpg\" width=\"120\" height=\"42\" align=\"left\"><img src=\"http://padkite.com/app/gesture-kit-bg.jpg\" width=\"80\" height=\"50\" align=\"right\"><div class=\"footer-links\"><a href=\"#\">Practice Ball Game</a><br>"
			+"  <a href=\"#\">Contribute playing with ads</a></div>"             
			+"</div>"                                                          
			+"</body>"                                                         
			+"</html>";	
			
			Log.v("VER", landing);
			
		return landing;
	}
    
}