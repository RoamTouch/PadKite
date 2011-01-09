package com.roamtouch.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;

import com.roamtouch.database.DBConnector;
import com.roamtouch.swiftee.SwifteeApplication;

public class WebPage {

	static final int numPerPage = 50;
	
	public String getBrowserHistory(Context context, String url, int start){
		SwifteeApplication appState = ((SwifteeApplication)context.getApplicationContext());
    	DBConnector database = appState.getDatabase();
    	Cursor c = database.getFromHistory(1);
    	String data = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"></head><body>" +
        "<div class=\"logo\" style=\"text-align: center;\"><a href=\"http://padkite.com/start\">"+
		"<img src=\"PadKite-Logo.png\" alt=\"PadKite Logo\" width=\"150\" /></a></div>" +
        "<table><tr><td style=\"width:50px\" height:\"50px\"><img src=\"history_normal.png\" width=\"75\" height=\"75\"></td><td ><font color=\"#F2A31C\"><h3 >Browser History </h3></font></td></tr></table>"+
        "<table style=\"width:100%\">";
    	String str = "";
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
     			
    			str+= "<tr>" +
                	   "<td ><font color=\"#000000\" align=\"justify\" size=\"2px\">"+formatter.format(date)+"</font></td>"+
                	   "<td ><font color=\"#F2A31C\" size=\"2px\">"+c.getString(3)+"</font><br><font size=\"2px\"><a href=\""+c.getString(2)+"\">"+c.getString(2)+"</a></font></td>" +
                	   "</tr>";
    			c.moveToNext();
    			num++;
    		}

    		if (start >= numPerPage)
    			end+="<a href=\"" + url + "?start=" + (start-numPerPage) + "\">" + (start-numPerPage) + "-" + start +"</a> | ";
			end+="<strong>" + start + "-" + (start+numPerPage) +"</strong> | ";
    		if (!c.isAfterLast())
    			end+="<a href=\"" + url + "?start=" + (start+numPerPage) + "\">" + (start+numPerPage) + "-" + (start + 2*numPerPage) +"</a>";
    	}  
		return 
               data+=str+
               "</table>"+end+
               "</body></html>";
	}
	public String getDownloadHistory(Context context, String url, int start){
		SwifteeApplication appState = ((SwifteeApplication)context.getApplicationContext());
    	DBConnector database = appState.getDatabase();
    	Cursor c = database.getFromHistory(2);
    	String data = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"></head><body>" +
        "<div class=\"logo\" style=\"text-align: center;\"><a href=\"http://padkite.com/start\">"+
		"<img src=\"PadKite-Logo.png\" alt=\"PadKite Logo\" width=\"150\" /></a></div>" +
        "<table><tr><td style=\"width:50px\" height:\"50px\"><img src=\"download_normal.png\" width=\"75\" height=\"75\"></td><td ><font color=\"#F2A31C\"><h3 >Download History </h3></font></td></tr></table>"+
        "<table style=\"width:100%\">";
    	String str = "";
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
     			
    			str+= "<tr>" +
                	   "<td ><font color=\"#000000\" align=\"justify\" size=\"2px\">"+formatter.format(date)+"</font></td>"+
                	   "<td ><font color=\"#F2A31C\" size=\"2px\">"+c.getString(3)+"</font><br><font size=\"2px\"><a href=\""+c.getString(2)+"\">"+c.getString(2)+"</a></font></td>" +
                	   "</tr>";
    			c.moveToNext();
    			num++;
    		}

    		if (start >= numPerPage)
    			end+="<a href=\"" + url + "?start=" + (start-numPerPage) + "\">" + (start-numPerPage) + "-" + start +"</a> | ";
			end+="<strong>" + start + "-" + (start+numPerPage) +"</strong> | ";
    		if (!c.isAfterLast())
    			end+="<a href=\"" + url + "?start=" + (start+numPerPage) + "\">" + (start+numPerPage) + "-" + (start + 2*numPerPage) +"</a>";
    	}  
		return 
               data+=str+
               "</table>"+end+
               "</body></html>";
	}
	public static String getEventsHistory(){
		return "";
	}
}
