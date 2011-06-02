//******************************************************************************** 
//**	Copyright (c) 2011, Roaming Keyboards LLC doing business as RoamTouch®	**	       
//**	All rights reserved.													**
//********************************************************************************
package com.roamtouch.landingpage;

import java.io.FileNotFoundException;

import java.io.BufferedReader;
import java.io.FileOutputStream;
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

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.view.WebPage;
import com.roamtouch.settings.GestureAdapter;

import com.api.twitter.Keys;
import com.api.twitter.TwitterActivity;

import twitter4j.Twitter;
import twitter4j.http.RequestToken;
import twitter4j.http.AccessToken;

/**
 * 
 * @author jose vigil
 * DO NOT INSTANSIATE THIS CLASS IN OTHER THAN
 */
public class LandingPage {

	private static  String landing;
	public static ArrayList<String> twitt = new ArrayList<String>();
	public static ArrayList<String> popSites = new ArrayList<String>();
	public static ArrayList<String> history = new ArrayList<String>();	
	public static ArrayList<String> bookmarks = new ArrayList<String>();
	public static ArrayList<String> bookmarksImages = new ArrayList<String>();
	public static ArrayList<String> starContacts = new ArrayList<String>();
	public static ArrayList<String> callLogs = new ArrayList<String>();
	public static ArrayList<String> notes = new ArrayList<String>();
	
	static boolean hist;
	static boolean book;
	
	SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a");
	
	private static BrowserActivity mParent = null;
	
	private TwitterActivity tw = new TwitterActivity();    		
	private twitter4j.Twitter twitter;
	private List<Status> statuses;
	
	public LandingPage(BrowserActivity parent) 	{		
		mParent = parent;				
	}
	
	public String generateLandingPageString() {	
		
		landing = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">"
			+"<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>PadKite Start Page</title>"
			+"<link href=\"http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css\" rel=\"stylesheet\" type=\"text/css\"/>"
			+"<script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js\"></script>"
			+"<script src=\"http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js\"></script>"
			+"<script type=\"text/javascript\">";
			//JAVASCRIPT
			String javascript = getJavaScript();
			landing += javascript;
			//STYLES
			String styles = getCSSStyles();
			landing += styles
			+"</style>"
			+"</head>"
			+"<body>"			
			+"<div id=\"dialog\" title=\"Basic dialog\">"		
			+"	<form>"
			+"	<p>This is an animated dialog which is useful for displaying information. The dialog window can be moved, resized and closed with the 'x' icon.</p>"
	        +"	<label>Label for the text area:</label>" 
	        +"		<textarea></textarea>" 
	        +"	</form>"
			+"</div>"
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
			+"			<a href=\"#\" name=\"URLink\" onclick=\"setFocus('url')\">URL</a>" 
			+"			<a href=\"#\" name=\"SearchLink\" onclick=\"setFocus('search')\">Search</a>" 
			+"			<a href=\"#\" name=\"ImageLink\" onclick=\"setFocus('image')\">Image</a>" 
			+"			<a href=\"#\" name=\"VideoLink\" onclick=\"setFocus('video')\">Video</a>"			 
			+"			<a href=\"#\" name=\"Twitter\" onclick=\"setFocus('twitter')\">Twitter</a><br>"
			+"		</div>" 		 
			+"	<form id=\"urlform\" action=\"#\" onSubmit=\"return loadSearch()\">" //handleKeyPress(event,this.form)\">"    
			+"		<input name=\"box\" class=\"input\" type=\"text\" size=\"40\"></input><br>" //Isolating the input in order to have the "Go" alone.
			+"	</form><br>"
			+"<button id=\"opener\">Open Dialog</button>"			
			+"<!--twitter--><div id='twConteiner'><div id='tw'>";			
			String twitString = generateTwitterString();			
			landing += twitString; 
			landing +="</div></div><!--twitter-->"			
			+"	<br><div align=\"center\">"  
			+"		<table border=\"0\" width=\"100%\" class=\"small-links\" cellspacing=\"5\">"
			+"		<tr valign=\"top\" width=\"30%\">"   
			+"			<td align=\"left\">"
			+"				<div class=\"column-title\">Bookmarks</div>" 
			+"<!--bookmarks-->";			
			String booksString = generateBookmarksString();			
			landing += booksString
			+="<!--bookmarks-->"
			+"			</td>"  
			+"			<td align=\"left\" width=\"30%\">"   			
			+"				<div class=\"column-title\">Recent History</div>"
			+"<!--history-->";		
			String histString = generateHistoryString();
			landing += histString
			+"<!--history-->"			
			+"    			</div><br>" 
			+"				<div class=\"column-title\">Call Log</div>"
			+"<!--callLog-->";		
			String callString = generateCallLogString();			
			landing += callString
			+"<!--callLog-->"			
			+"			</td>" 
			+"			<td align=\"left\" width=\"20%\">"
			+"				<div class=\"column-title\">Starred Contacts</div>" 
			+"<!--starredContacts-->";		
			String starredString = generateStarredContactsString();			
			landing += starredString
			+"<!--starredContacts-->"
			+"     			</div><br>"		
			+"			</td>"   
			+"			<td align=\"left\" width=\"20%\">"
			+"				<div class=\"column-title\">PadKite Notes</div>"		
			+"<!--padkiteNotes-->";			
			String notesString = generateNotesString();		
			landing += notesString
			+"<!--padkiteNotes-->"
			+"		 	   </div>" 
			+"				<div class=\"column-title\">Popular Sites</div>"		
			+"<!--popularSites-->";			
			String popString = generatePopularSitesString();		
			landing += popString
			+"<!--popularSites-->"
			+"		 	   </div>" 
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
	
	private String getJavaScript(){
		String javascript = "var type;"
			+"function loadSearch(){"		
			+"	var url = document.forms['urlform'].elements['box'].value;"
			+"	var theUrl;"
			+"	switch(type){"
			+"		case 'url':"	
			
			+"			theUrl = 'http://'+url;"
			+"			window.document.location.href=theUrl;"
			+"		break;"
			+"		case 'search':" 
			+"			theUrl = '"+SwifteeApplication.getGoogleSearch()+"\"'+url;"
			+"			window.document.location.href=theUrl;"
			+"		break;"
			+"		case 'image':" 
			+"			theUrl = '"+SwifteeApplication.getImageSearch()+"\"'+url;"
			+"			window.document.location.href=theUrl;"
			+"		break;"
			+"		case \"video\":"
			+"			theUrl = '"+SwifteeApplication.getYouTubeSearch()+"\"'+url;"
			+"			window.document.location.href=theUrl;"
			+"		break;"
			+"		case \"twitter\":"
			+"			pBridge.searchTwitter(url);"	
			+"			scroll(0,0);"	
			+"			break;"
			+"		default:"
			+"			theUrl = '"+SwifteeApplication.getGoogleSearch()+"\"'+url;"
			+"			window.document.location.href=theUrl;"
			+"			break;"
			+"	};"
			+"	document.forms['urlform'].elements['box'].value=\"\";"
			+"	type=\"\";"
			+"	url=\"\";"
			+"	return false;"
			+"}"					
			+"function setFocus(id){"			
			+"	type=id;"
			+"	document.forms['urlform'].elements['box'].focus();"			
			+"}"
			+"$.fx.speeds._default = 1000;"
			+"$(function() {"
			+"	$( \"#dialog\" ).dialog({"
			+"		autoOpen: false,"
			+"		show: \"blind\","			
			+"	});"
			+"$( \"#opener\" ).click(function() {"
			+"		$( \"#dialog\" ).dialog( \"open\" );"
			+"		return false;"
			+"	});"
			+"});"
			+"</script>";
		return javascript;
	}
	
	private String getCSSStyles(){
		String styles = "<style>"
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
			+".column-title {"
			+"	color: #999999;"
			+"	font-weight:bold;"
			+"	font-size:11px;"
			+"}"
			+".small-date {"
			+"	color: #999999;"
			+"	font-size:4px;"
			+"}"		
			+".overlay"
			+"{"
			+"   background-color: #000;"
			+"   opacity: .7;"
			+"   filter: alpha(opacity=70);"
			+"   position: absolute; top: 0; left: 0;"
			+"   width: 70%; height: 80%;"
			+"   z-index: 10;"
			+"}";
		return styles;
	}
	
	public String getLandingString(){
		return landing;
	}
	
	public void setLandingString(String lp){
		landing = lp;
	}
	
	public String generateTwitterString(){
		String twitterString = "";
		if (twitt.size()>0){
		  	for (int i = 0; i < twitt.size(); i++) {
		    	String tw = twitt.get(i);		    	
		    	StringTokenizer tTok = new StringTokenizer(tw, "|");
		    	String name = tTok.nextToken();
		    	String url = tTok.nextToken();
		    	twitterString += "<a href=\""+url+"\"><img src=\""+url+"\" title=\""+name+" width=\"20\" height=\"20\" border=\"0\"></a>";		    	    	  	
		    }
		}		
		return twitterString;
	}
	
	public String generateBookmarksString(){
		String bookString = "";
		if (bookmarksImages.size()>0){
			for (int t = 0; t < bookmarksImages.size(); t++) {
		    	String bo = bookmarksImages.get(t);		    	
		    	StringTokenizer hTok = new StringTokenizer(bo, "|");
		    	String name = hTok.nextToken();
		    	String url = hTok.nextToken();		    	
		    	String image = hTok.nextToken();
		    	bookString += "<img src=\""+image+"\" width=\"15\" height=\"15\" border=\"0\"><a href=\""+url+"\">"+name+"</a><br>";		    			    
		    }
		}		
		return bookString;
	}
	
	public String generateHistoryString(){
		String histString = "";
		if (hist==true){
			for (int l = 0; l < history.size(); l++) {
		    	String hi = history.get(l);		    	
		    	StringTokenizer hTok = new StringTokenizer(hi, "|");
		    	String id = hTok.nextToken();
		    	String name = hTok.nextToken();    		    	
		    	String link = hTok.nextToken();
		    	String date = hTok.nextToken();
		    	if (link.contains("http://")){
		    		link = link.replaceAll("http://", "");
		    		if (link.contains("www.")){
		    			link = link.replaceAll("www.", "");
		    		}
		    	}
		    	if (name.equals("null")){ 
		    		if (link.length()>15){
		    			link = link.subSequence(0, 12)+"...";
		    		}
		    		name = link; 
		    	}
		    	histString += "<a href=\""+link+"\" id=\""+id+"\">"+name+"</a><br>";
		    	histString += "<div class=\"small-date\">"+date+"</div>";		    
		    }
			String more = "file:///android_asset/Web Pages/history.html";
			histString += "<a href=\""+more+"\" id=\"more\">More...</a><br>";
		}		
		return histString;
	}
	
	public String generateCallLogString(){
		String callString = "";
		int callSize = callLogs.size();			 
		if ( callSize > 0){				
			for (int s = 0; s < callSize; s++) {
				String call = callLogs.get(s);		    	
		    	StringTokenizer cTok = new StringTokenizer(call, "|");
		    	String number = cTok.nextToken();
		    	String name = cTok.nextToken();
		    	String date = cTok.nextToken();			    	
		    	callString += "<a href=\""+name+"\">"+name+"</a><br>";			    	
		    	callString += "<div class=\"small-date\">"+date+"</div>";
		    }		
		}		
		return callString;
	}
	
	public String generateStarredContactsString(){		
		String starredString = "";
		int starSize = starContacts.size();
		if (starSize>0){						
			for (int s = 0; s < starSize; s++) {
				String star = starContacts.get(s);		    	
		    	StringTokenizer sTok = new StringTokenizer(star, "|");
		    	String id = sTok.nextToken();
		    	String name = sTok.nextToken();			    		    	
		    	String image = sTok.nextToken();
		    	Log.v("B", "image: " +image);
		    	if (image!=null || !image.equals("null")){
		    		starredString += "<img src=\""+image+"\" width=\"15\" height=\"15\" border=\"0\"><a href=local.contact."+id+"> "+name+"</a><br>";
		    	} else {
		    		starredString += "<a href=local.contact."+id+"> "+name+"</a><br>";
		    	}
			}		
		}		
		return starredString;
	}
	
	public String generatePopularSitesString(){	
		String popularSites = "";
		int popSize = popSites.size();
		if (popSize>0){				
			for (int j = 0; j < popSize/3; j++) {
		    	String pop = popSites.get(j);		    	
		    	StringTokenizer pTok = new StringTokenizer(pop, "|");
		    	String pPage = pTok.nextToken();
		    	String pUrl = pTok.nextToken();			    	    			    	
		    	popularSites += "<a href=\""+pUrl+"\">"+pPage+"</a><br>";	    					    	   
			}							
		}			
		return popularSites;
	}
	
	public String generateNotesString(){	
		String notesResult = "";
		int popSize = notes.size();
		if (popSize>0){				
			for (int j = 0; j < popSize; j++) {
		    	String not = notes.get(j);		    	
		    	StringTokenizer nTok = new StringTokenizer(not, "|");
		    	String nId = nTok.nextToken();
		    	String nCreated = nTok.nextToken(); 	
		    	String nLast_edited = nTok.nextToken();
		    	String nTitle = nTok.nextToken();
		    	String nNote = nTok.nextToken();
		    	String note = "id="+nId+"&created="+nCreated+"&last_edited="+nLast_edited+"&title="+nTitle+"&note="+nNote;
		    	notesResult += "<a href=\"padkite.local.note#"+note+"\">"+nTitle+"</a><br>";	    					    	   
			}		
		}			
		return notesResult;
	}

	
	private static boolean isNullOrBlank(String s){
	  return (s==null || s.trim().equals("") || s.trim().equals("null"));
	}

		
	public boolean loadRemoteData(int resource, String search){   
		
    	String url = null;    	
    	if (resource == 1 || resource == 2){
    		url = "http://roamtouch.com/padkite/app/json/"+search;    		
    	} else {
    		url = SwifteeApplication.getTwitterSearch()+search;
    	}   		
    	Log.v("URL", url);
        InputStream source = retrieveStream(url);        
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
	        	case 3:
	        		String User = result.fromUser;
		        	String ImageUrl = result.profileImageUrl;
		        	String both = User+"|"+ImageUrl;  
		        	//Log.v("BOTH", "both: " + both);
		        	twitt.add(both);
	        		break;        		
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
		if (pop){ tw = loadRemoteData(3, SwifteeApplication.twitter_key); }
		if(tw)return tw;
		else return false;
	}
    
    public boolean getLandingPageHistory(){    	
    	WebPage page = new WebPage();
    	hist = page.getLandingHistory(mParent);  
    	return hist;
    }
    
    public static void setHistory(ArrayList<String> hist){ 
    	history = hist;
    }
   
    public void setBookmarksImages(Context cont){
    	View view = new View(cont); 
    	ArrayList<String> imagesArray =	GestureAdapter.getBookMarkImages(cont, view);
    	for (int i = 0; i < imagesArray.size(); i++) {
	    	String image = imagesArray.get(i);	
	    	String book = bookmarks.get(i);
	    	String all = book+"|"+image;	    		   
	    	bookmarksImages.add(all);
	    }    	
    }
            
    public static void setBookmark(ArrayList<String> book){ 
    	bookmarks = book; 
    }
    
    public static void setNotes(ArrayList<String> note){ 
    	notes = note; 
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
	     
     
	public boolean getStarredContacts(){
		
		ContentResolver cr = mParent.getContentResolver();
		
		Cursor curStar = cr.query(
				ContactsContract.Contacts.CONTENT_URI,  
                null, 
                ContactsContract.Contacts.STARRED + " != 0", 
                null, 
                null);
				
		if (curStar == null) { return false; }		
		if (curStar.getCount() > 0) {  

			String star = null;
			while (curStar.moveToNext()) { 					
  
	            String id = curStar.getString(curStar.
	            		getColumnIndex(ContactsContract.Contacts._ID));  
	            String name = curStar.getString(curStar.
	            		getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));  
	                 
	            
	            String image = getContactPicture(id);           
	            
	            star = id+"|"+name+"|"+image;
	            starContacts.add(star);	            
			}	
		}		
		
		String[] strFields = {
				CallLog.Calls.NUMBER, 
				CallLog.Calls.TYPE,
				CallLog.Calls.CACHED_NAME,
				CallLog.Calls.DATE,
				CallLog.Calls._ID
        };
		
		String strOrder = CallLog.Calls.DATE + " DESC"; 
 
		Cursor callCursor = cr.query(
				CallLog.Calls.CONTENT_URI,
				strFields,
				null,
				null,
				strOrder
        );	
		
		if (callCursor == null) { return false; }		
		
		if (callCursor.getCount() > 0) {  
			int top = 0;
			String call = null;
			while (callCursor.moveToNext() && top < 5 ) { 					
				
	            String number = callCursor.getString(callCursor.
	            		getColumnIndex(CallLog.Calls.NUMBER));  
	            String cacheName = callCursor.getString(callCursor.
	            		getColumnIndex(CallLog.Calls.CACHED_NAME));
	           	                  	            
	            Long longTime = callCursor.getLong(callCursor.
	            		getColumnIndex(CallLog.Calls.CACHED_NAME));
	            
	            String id = callCursor.getString(callCursor.
	            		getColumnIndex(CallLog.Calls._ID));
	            
	            String image = getContactPicture(id);      
	           
	            //Date date = new Date(longTime);
	            SimpleDateFormat formatter = new SimpleDateFormat("EEE, d, h:mm a");
	            String callTime = formatter.format(longTime);
	            Log.v("A","longTime: "+longTime+" callTime :"+callTime);
	            call = number+"|"+cacheName+"|"+image+"|"+callTime;	            
	            
	            callLogs.add(call);	            
	            
	            top++;
			}	
		}
		return true;
		
	};	
	
	public String getContactPicture(String contactID) {
		String ret = null;
    	Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactID));
    	InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(mParent.getContentResolver(), uri);
    	Bitmap contactPhoto = BitmapFactory.decodeStream(input);
    	
    	if (contactPhoto!=null){
	    	String path = null;	
	    	String home_path = Environment.getExternalStorageDirectory()+"/PadKite/Web Assets/";
	    	path = home_path + "Contacts/Images/" + contactID + ".png";
	    	boolean exist = fileExists(mParent, path);
			if (exist==false){ 
				try {
					contactPhoto.compress(CompressFormat.PNG, 95, new FileOutputStream(path));
					ret = path;
				} catch (FileNotFoundException e) {	
					e.printStackTrace();
				}	 
			}
		 } else {
			 ret =  null;
		 }    	
		return ret;
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
    
      
        
      
    
  
}; //End of LandingPage Class


