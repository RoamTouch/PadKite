package com.roamtouch.database;

import com.roamtouch.swiftee.BrowserActivity;
import com.roamtouch.swiftee.SwifteeHelper;

import android.content.Context;  
import android.database.Cursor;  
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.util.Log;

  
public class DBHelper extends SQLiteOpenHelper {  
  
    // Define the version and database file name  
    private static final String DB_NAME = "padkiteDB.db";  
    private static final int DB_VERSION = 7; 
    
    DBConnector dbConnector;
    
    private SQLiteDatabase db;  
    
    Handler mHandler = new Handler();
    
    // Constructor to simplify Business logic access to the repository   
    public DBHelper(Context context, DBConnector db) {  
  
        super(context, DB_NAME, null, DB_VERSION);  
                // Android will look for the database defined by DB_NAME  
                // And if not found will invoke your onCreate method  
        //this.db = this.getWritableDatabase();  
        dbConnector = db;
    }  
  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
                  
        // Android has created the database identified by DB_NAME  
        // The new database is passed to you vai the db arg  
        // Now it is up to you to create the Schema.  
        // This schema creates a very simple user table, in order  
        // Store user login credentials  
    	
    	dbConnector.checkCreatedTables(db);
    
     	
       /* db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT)",  
                        UserTable.NAME, UserTable.COL_ID,  
                        UserTable.COL_USERNAME, UserTable.COL_PASSWORD));*/
		
		/*java.sql.Timestamp currentTimestamp = BrowserActivity.getCurrentTimeStamp();
		String title = "Sample PadKite note";
		String note = "In order to crate a note of a given content you select, simply draw the \"e\" gesture, edit the note and save it. The not will instantly appear on your landing page as a link note like this one. Enjoy!.";
		db.execSQL("INSERT INTO padkite_notes(created,last_edited,title,note) VALUES ('"+currentTimestamp+"','"+currentTimestamp+"','"+title+"','"+note+"')");*/	
	
		
    }   
    
   
   
  
    @Override  
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {  
        
    	// Later when you change the DB_VERSION   
        // This code will be invoked to bring your database  
        // Upto the correct specification  
    	 
    	mHandler.postDelayed(new Runnable(){
    		 
         	public void run(){
         		
         		dbConnector.checkCreatedTables(null);	
         		
         	}
         }, 500);    	 
    	
    	
    	
    }  
}