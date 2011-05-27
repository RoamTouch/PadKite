package com.roamtouch.database;

import android.content.Context;  
import android.database.Cursor;  
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

  
public class DBHelper extends SQLiteOpenHelper {  
  
        // Define the version and database file name  
    private static final String DB_NAME = "padkiteDB.db";  
    private static final int DB_VERSION = 1;  
    
    private static final String DATABASE_CREATE1 = "create table user_profiles (_id integer primary key autoincrement, "+
	  "email text not null ," +
	  "username text not null ," +
	  "password text not null);" ;

	private static final String DATABASE_CREATE2 = "create table bookmarks (name text not null ," +
		  "url text not null);" ;
	
	private static final String DATABASE_CREATE3 = "create table padkite_history (_id integer primary key autoincrement, "+
	"timestamp text not null ," +
	"url text not null ," +
	"title text not null ," +
	"type int);" ;
	
	private static final String DATABASE_CREATE4 = "create table padkite_landing_page (_id integer primary key autoincrement, "+
	"timestamp text not null ," +
	"html text not null ," +
	"twitterSearch text not null);";
     
    private SQLiteDatabase db;  
  
    // Constructor to simplify Business logic access to the repository   
    public DBHelper(Context context) {  
  
        super(context, DB_NAME, null, DB_VERSION);  
                // Android will look for the database defined by DB_NAME  
                // And if not found will invoke your onCreate method  
        this.db = this.getWritableDatabase();  
  
    }  
  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
                  
        // Android has created the database identified by DB_NAME  
        // The new database is passed to you vai the db arg  
        // Now it is up to you to create the Schema.  
        // This schema creates a very simple user table, in order  
        // Store user login credentials  
    	
    	db.execSQL(DATABASE_CREATE1);
		db.execSQL(DATABASE_CREATE2);
		db.execSQL(DATABASE_CREATE3);
		db.execSQL(DATABASE_CREATE4);	
    
    	//Log.v("ENTRA", "ver");
       /* db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT)",  
                        UserTable.NAME, UserTable.COL_ID,  
                        UserTable.COL_USERNAME, UserTable.COL_PASSWORD));*/  
  
    }     
   
  
    @Override  
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {  
        // Later when you change the DB_VERSION   
                // This code will be invoked to bring your database  
                // Upto the correct specification  
    }  
}