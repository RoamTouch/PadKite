package com.roamtouch.view;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;

import com.roamtouch.database.DBConnector;
import com.roamtouch.swiftee.SwifteeApplication;
import com.roamtouch.landingpage.LandingPage;

public class WebPage {

	static final int numPerPage = 50;
	
	public String getBrowserHistory(Context context, String url, int start){
		return getHistory(context, url, start, 1, "history.html");
	}
	
	public String getDownloadHistory(Context context, String url, int start){
		return getHistory(context, url, start, 2, "download.html");
	}
	
	public boolean getLandingHistory(Context context) {
		
		boolean ret = false;
		ArrayList<String> history = new ArrayList<String>();
		SwifteeApplication appState = ((SwifteeApplication)context.getApplicationContext());
    	DBConnector database = appState.getDatabase();
    	Cursor c = database.getFromHistory(1);    	
    	if (c==null){ 
    		return ret; 
    	}    	
    	int count = c.getCount();
    	c.moveToPosition(0);    	
    	int num = 0;
    	String hi = null;    
    	
    	if (count > 5){ count=5; }
    	
		while (!c.isAfterLast() && num < count){
			
			Date date = new Date(Long.parseLong(c.getString(1)));
 			SimpleDateFormat formatter = new SimpleDateFormat("EEE, d, h:mm a");
 			
 			String id = c.getString(1);
 			String link = c.getString(2);
 			String name = c.getString(3);
 			
 			hi = id+"|"+name+"|"+link+"|"+formatter.format(date);
 			history.add(hi);
			
 			//Log.v("VER", "id: "+id+ "  name:  "+name+"  link:  "+link+"  date:  "+date);
			c.moveToNext();
			num++;
			
		}
		if (num==count){ 
			LandingPage.setHistory(history);
			ret=true;			
		}
		return ret;		
	}
	
	public String getHistory(Context context, String url, int start, int type, String file){
		
		SwifteeApplication appState = ((SwifteeApplication)context.getApplicationContext());
    	DBConnector database = appState.getDatabase();
    	Cursor c = database.getFromHistory(type);
    	String data = "Could not open " + file + ". Please contact support.";
    	    	
    	try {
    		InputStream is = appState.getAssets().open("Web Pages/" + file);
    		byte[] bytes=new byte[is.available()];
    		is.read(bytes);
    		data = new String(bytes);
    		bytes = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return data;
		}

    	String str = "<ul>";
    	String end = "Showing " + numPerPage + " results per page.<br>";
    	int num = 0;
    	
    	if(c!=null) {
        	if (start < 0)
        		start = 0;
        	if (start >= c.getCount())
        		start = c.getCount() - 1;

    		c.moveToPosition(start);
    		while (!c.isAfterLast() && num < numPerPage){
    			Date date = new Date(Long.parseLong(c.getString(1)));
     			SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a");
     			
    			/*str+= "<tr>" +
                	   "<td ><font color=\"#000000\" align=\"justify\" size=\"2px\">"+formatter.format(date)+"</font></td>"+
                	   "<td ><font color=\"#F2A31C\" size=\"2px\">"+c.getString(3)+"</font><br><font size=\"2px\"><a href=\""+c.getString(2)+"\">"+c.getString(2)+"</a></font></td>" +
                	   "</tr>";*/
     			
     			if (type == 2) {
     				String filename = c.getString(3);
     				if (filename.equals("")) { // TODO: Remove this later
     					String t[] = c.getString(2).split("/");
     					filename = URLEncoder.encode(t[t.length-1]).replace("%", " %");
     				}
     				String link = c.getString(2);
     				str+="<li><p class=\"title\"><a href=\"file:///sdcard/download/" + filename + "\" >" + filename +"</a></p><p class=\"url\"><a href=\"" + link + "\" >" + link.replace("%"," %") +"</a></p><p class=\"date\">" + formatter.format(date) + "</p></li>";     				
     			}
     			else {
     				String name = c.getString(3);
     				String link = c.getString(2);
     				
     				if (name.equals("null") || name == null)
     					name = "";
     				
     				str+="<li><p class=\"title\"><a href=\"" + link + "\" >" + name + "</a></p><p class=\"url\"><a href=\"" + link + "\" >" + link +"</a></p><p class=\"date\">" + formatter.format(date) + "</p></li>";
     			}

   				c.moveToNext();
    			num++;
    		}

    		if (start >= numPerPage)
    			end+="<a href=\"" + url + "?start=" + (start-numPerPage) + "\">" + (start-numPerPage) + "-" + start +"</a> | ";
			end+="<strong>" + start + "-" + (start+numPerPage) +"</strong> | ";
    		if (!c.isAfterLast())
    			end+="<a href=\"" + url + "?start=" + (start+numPerPage) + "\">" + (start+numPerPage) + "-" + (start + 2*numPerPage) +"</a>";
    	}  
    	str = str + "</ul>" + end;
		return data.replace("<img src=\"../images/loader.gif\" />", str);
	}
	
	public static String getEventsHistory(){
		return "";
	}
}
