package com.roamtouch.view;

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
        "<font color=\"#F2A31C\"><center ><h1 >PadKite </h1></center>"+
        "<table><tr><td ><img src=\"file:///android_asset/images/history_normal.png\" ></td><td ><h3 >Browser History </h3></td></tr></table>"+
        "<table>";
    	String str = "";
    	
    	if(c!=null){
    		c.moveToFirst();
    		while(!c.isAfterLast()){
    			
    			str+= "<tr>" +
                	   "<td ><h4><font color=\"#000000\" align=\"justify\" >"+c.getString(1)+"</font></h4></td>"+
                	   "<td ><a href=\""+c.getString(2)+"\">"+c.getString(2)+"</a></td>" +
                	   "</tr>";
    			c.moveToNext();
    		}
    	}  
		return 
               data+=str+
               "</table>"+
               "</body></html>";
	}
	public static String getDownLoadHistory(){
		return "";
	}
	public static String getEventsHistory(){
		return "";
	}
}
