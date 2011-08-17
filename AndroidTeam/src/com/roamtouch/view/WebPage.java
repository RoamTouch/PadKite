package com.roamtouch.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;

import com.roamtouch.database.DBConnector;
import com.roamtouch.swiftee.SwifteeApplication;

public class WebPage {

		
	public String getBrowserHistory(Context context){
		SwifteeApplication appState = ((SwifteeApplication)context.getApplicationContext());
    	DBConnector database = appState.getDatabase();
    	Cursor c = database.getFromHistory(1);
    	String data = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"></head><body>" +
        "<div class=\"logo\" style=\"text-align: center;\"><a href=\"http://padkite.com/start\">"+
		"<img src=\"PadKite-Logo.png\" alt=\"PadKite Logo\" width=\"150\" /></a></div>" +
        "<table><tr><td style=\"width:50px\" height:\"50px\"><img src=\"history_normal.png\" width=\"75\" height=\"75\"></td><td ><font color=\"#F2A31C\"><h3 >Browser History </h3></font></td></tr></table>"+
        "<table style=\"width:100%\">";
    	String str = "";
    	
    	if(c!=null){
    		c.moveToFirst();
    		while(!c.isAfterLast()){
    			Date date = new Date(Long.parseLong(c.getString(1)));
     			SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a");
     			
    			str+= "<tr>" +
                	   "<td ><font color=\"#000000\" align=\"justify\" size=\"2px\">"+formatter.format(date)+"</font></td>"+
                	   "<td ><font color=\"#F2A31C\" size=\"2px\">"+c.getString(3)+"</font><br><font size=\"2px\"><a href=\""+c.getString(2)+"\">"+c.getString(2)+"</a></font></td>" +
                	   "</tr>";
    			c.moveToNext();
    		}
    	}  
		return 
               data+=str+
               "</table>"+
               "</body></html>";
	}
	public String getDownLoadHistory(Context context){
		SwifteeApplication appState = ((SwifteeApplication)context.getApplicationContext());
    	DBConnector database = appState.getDatabase();
    	Cursor c = database.getFromHistory(2);
    	String data = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"></head><body>" +
        "<div class=\"logo\" style=\"text-align: center;\"><a href=\"http://padkite.com/start\">"+
		"<img src=\"PadKite-Logo.png\" alt=\"PadKite Logo\" width=\"150\" /></a></div>" +
        "<table><tr><td style=\"width:50px\" height:\"50px\"><img src=\"download_normal.png\" width=\"75\" height=\"75\"></td><td ><font color=\"#F2A31C\"><h3 >Download History </h3></font></td></tr></table>"+
        "<table style=\"width:100%\">";
    	String str = "";
    	
    	if(c!=null){
    		c.moveToFirst();
    		while(!c.isAfterLast()){
    			Date date = new Date(Long.parseLong(c.getString(1)));
     			SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a");
     			
    			str+= "<tr>" +
                	   "<td ><font color=\"#000000\" align=\"justify\" size=\"2px\">"+formatter.format(date)+"</font></td>"+
                	   "<td ><font color=\"#F2A31C\" size=\"2px\">"+c.getString(3)+"</font><br><font size=\"2px\"><a href=\""+c.getString(2)+"\">"+c.getString(2)+"</a></font></td>" +
                	   "</tr>";
    			c.moveToNext();
    		}
    	}  
		return 
               data+=str+
               "</table>"+
               "</body></html>";
	}
	public static String getEventsHistory(){
		return "";
	}
}
