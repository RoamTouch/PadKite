package com.roamtouch.view;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;

import com.roamtouch.database.DBConnector;
import com.roamtouch.swiftee.SwifteeApplication;

public class WebPage {

	static final int numPerPage = 50;
	
	public String getBrowserHistory(Context context, String url, int start){
		return getHistory(context, url, start, 1, "history.html");
	}
	
	public String getDownloadHistory(Context context, String url, int start){
		return getHistory(context, url, start, 2, "download.html");
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
    	
    	if(c!=null){
        	if (start < 0)
        		start = 0;
        	if (start >= c.getCount())
        		start = c.getCount() - 1;

    		c.moveToPosition(start);
    		while(!c.isAfterLast() && num < numPerPage){
    			Date date = new Date(Long.parseLong(c.getString(1)));
     			SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a");
     			
    			/*str+= "<tr>" +
                	   "<td ><font color=\"#000000\" align=\"justify\" size=\"2px\">"+formatter.format(date)+"</font></td>"+
                	   "<td ><font color=\"#F2A31C\" size=\"2px\">"+c.getString(3)+"</font><br><font size=\"2px\"><a href=\""+c.getString(2)+"\">"+c.getString(2)+"</a></font></td>" +
                	   "</tr>";*/
     			if (type == 2) {
     				String t[] = c.getString(2).split("/");
     				String filename = t[t.length-1];
     				str+="<li><p class=\"title\"><a href=\"file:///sdcard/download/" + filename + "\" >" + filename +"</a></p><p class=\"url\"><a href=\"" + c.getString(2) + "\" >" + c.getString(2) +"</a></p><p class=\"date\">" + formatter.format(date) + "</p></li>";     				
     			}
     			else
     				str+="<li><p class=\"title\"><a href=\"" + c.getString(2) + "\" >" + c.getString(3) +"</a></p><p class=\"url\"><a href=\"" + c.getString(2) + "\" >" + c.getString(2) +"</a></p><p class=\"date\">" + formatter.format(date) + "</p></li>";

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
