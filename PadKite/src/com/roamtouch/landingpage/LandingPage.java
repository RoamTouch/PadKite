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

import android.util.Log;

import com.google.gson.Gson;
import com.roamtouch.swiftee.SwifteeApplication;

public class LandingPage {
		
	private static  String landing;	
	static ArrayList<String> twitter = new ArrayList<String>();
	static ArrayList<String> popSites = new ArrayList<String>();
	
    public static void loadRemoteData(int resource, String search){
    	String url = null;    	
    	switch (resource){
    		case 1:
    			url = "http://padkite.com/app/json/"+search;
			break;	
    		case 2:
    			url = "http://padkite.com/app/json/"+search;
    			break;					
    		case 3:
				url = SwifteeApplication.getTwitterSearch()+search;
				break;			
		}   	
    	Log.v("URL", url);
        InputStream source = retrieveStream(url);        
        Gson gsonName = new Gson();      
        Reader nameReader = new InputStreamReader(source);        
        NameResponse responseName = gsonName.fromJson(nameReader, NameResponse.class);         
        List<Result> resultsName = responseName.results;        
        for (Result result : resultsName) {   	
        	if (resource == 1 || resource == 2){
        		String pPage = null;
        		pPage = result.p_Page;
        		String pUrl = null;
            	pUrl = result.p_Url;
            	String pTooltip = null;
            	pTooltip = result.p_Tooltip;  
        		//setURls(pUrl, pTooltip);
            	Log.v("ACA", "aca: "+pPage+"|"+pUrl+"|"+pTooltip);
	            if (resource == 2){	            
	            	String all = pPage+"|"+pUrl+"|"+pTooltip;
	            	Log.v("ENTRA", "all: "+all);
	            	popSites.add(all);
	            }
	        } else {
	        	String User = result.fromUser;
	        	String ImageUrl = result.profileImageUrl;
	        	String both = User+"|"+ImageUrl;       
	        	twitter.add(both);
	        }    	
        }   	
    }
    
    private static void setURls(String pUrl, String pTooltip){
    	Log.v("URL",pTooltip);
    	if (pTooltip=="searchTwitter"){
    		Log.v("URL","searchTwitter");
    		SwifteeApplication.setTwitterSearch(pUrl);
    	} else if (pTooltip=="searchGoogle") {
    		Log.v("URL","searchKeyword");
    		SwifteeApplication.setGoogleSearch(pUrl);
    	} else if  (pTooltip=="searchImage"){
    		Log.v("URL","searchImage");
    		SwifteeApplication.setImageSearch(pUrl);
    	} else if (pTooltip=="searchYouTube"){
    		Log.v("URL","searchYouTube");
    		SwifteeApplication.setYouTubeSearch(pUrl);
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
     }

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
		    	StringTokenizer tokens = new StringTokenizer(tw, "|");
		    	String name = tokens.nextToken();// this will contain "Fruit"
		    	String url = tokens.nextToken();// this will contain " they taste good"    		    	
		    	landing += "<a href=\""+url+"\"><img src=\""+url+"\" title=\""+name+" width=\"20\" height=\"20\" border=\"0\"></a>";  	
		    }		
			landing += "<div align=\"center\">"  
			+"		<table border=\"0\" height=\"30\" width=\"360\" class=\"small-links\" cellspacing=\"10\">"
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
			+"			<td align=\"center\">" 
			+"				<a href=\"padkite.local.contact?id=bien\"> Facebook</a><br>"        
			+"				<a href=\"http://twitter.com/\">Twitter</a><br>" 
			+"				<a href=\"http://www.linkedin.com\"> LinkedIn</a><br>"        
			+"				<a href=\"http://www.flickr.com\">Flickr</a><br>"        
			+"				<a href=\"http://www.apple.com\"> Apple</a><br>"             
			+"				<a href=\"http://m.espn.go.com/wireless/index?w=1ajv8&i=COM\"> ESPN</a><br>"        
			+"				<a href=\"http://www.nytimes.com/\">NY Times</a><br>"        
			+"				<a href=\"http://hp.mobileweb.ebay.com/home\">eBay</a><br>" 
			+"				<a href=\"http://google.com/\">Google</a><br>"       
			+"				<a href=\"http://picasaweb.google.com/m/viewer?source=androidclient\"> Picassa</a><br>" 
			+"				<span style=\"text-decoration: none\"><a href=\"http://www.m.amazon.com/\">Amazon</a></span><br>"      
			+"			</td>"      
			+"			<td align=\"center\">"        
			+"				<a href=\"http://mw.weather.com/\">Weather</a><br>"         
			+"				<a href=\"http://www.wikipedia.org/\">Wikipedia</a><br>"        
			+"				<a href=\"http://www.bing.com/\">Bing</a><br>"  
			+"				<a href=\"http://m.yahoo.com/\">Yahoo</a><br>"        
			+"				<a href=\"http://extreme.mobile.msn.com/\">MSN</a><br>"      
			+"				<a href=\"http://www.craigslist.org\"> Craigslist</a><br>"          
			+"				<a href=\"http://www.wordpress.com\"> Wordpress</a><br>"        
			+"				<a href=\"http://www.ask.com\">Ask</a><br>"  
			+"				<a href=\"http://www.blogspot.com\"> BlogSpot</a><br>"        
			+"				<a href=\"http://www.qq.com\">Qq</a><br>"  
			+"				<a href=\"http://www.cnnmobile.com/\">CNN</a><br>"     
			+"			</td>"    
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