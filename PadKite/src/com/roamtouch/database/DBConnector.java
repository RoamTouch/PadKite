package com.roamtouch.database;

import java.sql.Timestamp;
import java.util.ArrayList;

import com.roamtouch.landingpage.LandingPage;
import com.roamtouch.swiftee.SwifteeHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * This class is for basic communication between local database and application layer
 * This will be modified once the complete database is designed.
 */
public class DBConnector {

		
		private DatabaseHelper mDbHelper;
		private SQLiteDatabase mDatabase;

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
		
		private static final String DATABASE_NAME = "padkiteDB";
		private static final int DATABASE_VERSION = 1;

		private final Context mContext;

		private static class DatabaseHelper extends SQLiteOpenHelper {

			DatabaseHelper(Context context) {
				super(context, DATABASE_NAME, null, DATABASE_VERSION);
			}

			@Override
			public void onCreate(SQLiteDatabase db) {

				db.execSQL(DATABASE_CREATE1);
				db.execSQL(DATABASE_CREATE2);
				db.execSQL(DATABASE_CREATE3);
				db.execSQL(DATABASE_CREATE4);				
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			}
		}

		public DBConnector(Context ctx) {
			this.mContext = ctx;
		}

		public DBConnector open() {
			mDbHelper = new DatabaseHelper(mContext);
			mDatabase = mDbHelper.getWritableDatabase();
			return this;
		}

		public void close() {
			mDbHelper.close();
		}
		
		
		public void registerUser(String name,String username,String password){
			try
			{
				mDatabase.execSQL("INSERT INTO user_profiles(email,username,password) VALUES('"+name+"','"+username+"','"+password+"')");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
    	}
		
		public void writeLandingPage(Timestamp currentTimestamp,String html,String twitterSearch){
			try
			{
				mDatabase.execSQL("INSERT INTO padkite_landing_page(timestamp,html,twitterSearch) VALUES('"+currentTimestamp+"','"+html+"','"+twitterSearch+"')");			
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
    	}
		
		public String checkLandingPage(){
			String land = null;
			try{
				Cursor c = mDatabase.rawQuery("SELECT timestamp,twitterSearch FROM padkite_landing_page", null);				
				if(c!=null){
					c.moveToFirst();
					String timestamp = c.getString(0);
					String twitterSearch = c.getString(1);
					land = timestamp+""+twitterSearch;
				}				
			}
			catch(Exception e){
				e.printStackTrace();
				land = null;
			}
			return land;
		}
		
		public boolean checkUserRegistered(){
			try{
				Cursor c = mDatabase.rawQuery("SELECT count(*) FROM user_profiles", null);
				if(c!=null){
					c.moveToFirst();
					int count = c.getInt(0);
					if(count>0)
						return true;
				}
				return false;
			}
			catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		
		public void deleteAllBookmarks() {
			mDatabase.execSQL("DELETE FROM bookmarks");
		}
		public void restoreDefaults(){
			deleteAllBookmarks();
			mDatabase.execSQL("DELETE FROM padkite_history");
		}
		public boolean checkIfBookmarkAdded(){
			try{
				Cursor c = mDatabase.rawQuery("SELECT count(*) FROM bookmarks", null);
				if(c!=null){
					c.moveToFirst();
					int count = c.getInt(0);
					if(count<2)
						return true;
				}
				return false;
			}
			catch(Exception e){
				e.printStackTrace();
				return false;
			}		}

		public void addBookmark(){
			try
			{
				mDatabase.execSQL("INSERT INTO bookmarks(name,url) VALUES ('Cancel','Gesture cancelled')");
				mDatabase.execSQL("INSERT INTO bookmarks(name,url) VALUES ('Padkite','http://padkite.com')");				
				mDatabase.execSQL("INSERT INTO bookmarks(name,url) VALUES ('Google','http://www.google.com')");
				mDatabase.execSQL("INSERT INTO bookmarks(name,url) VALUES ('Calendar','http://calendar.google.com')");
				mDatabase.execSQL("INSERT INTO bookmarks(name,url) VALUES ('Facebook','http://www.facebook.com')");
				mDatabase.execSQL("INSERT INTO bookmarks(name,url) VALUES ('Twitter','http://twitter.com')");
				mDatabase.execSQL("INSERT INTO bookmarks(name,url) VALUES ('Wikipedia','http://www.wikipedia.com')");
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		public void addBookmark(String name,String url){
			try
			{
//				System.out.println("-------------I am here in for adding gesture "+name+"and" + url +" in database------------");
				mDatabase.execSQL("INSERT INTO bookmarks(name,url) VALUES('"+name+"','"+url+"')");
				
			}
			catch(Exception e)
			{
//				System.out.println("-------------exception while adding bookmarks ------------");
				e.printStackTrace();
			}
		}
		public void deleteBookmark(String name){
			try
			{
//				System.out.println("-------------I am here in for deleting gesture "+name+" from database------------");				
				mDatabase.execSQL("DELETE from bookmarks WHERE name = '"+name+"'");
			}
			catch(Exception e)
			{
//				System.out.println("-------------exception while deleting bookmarks ------------");
				e.printStackTrace();
			}
		}
		public void updateBookmark(String name,String url){
			try
			{
//				System.out.println("-------------I am here in for deleting gesture "+name+" from database------------");
				deleteBookmark(name);
				addBookmark(name,url);
			}
			catch(Exception e)
			{
//				System.out.println("-------------exception while deleting bookmarks ------------");
				e.printStackTrace();
			}
		}
		
		public String getBookmark(String name){
			try{
				Cursor c = mDatabase.rawQuery("SELECT url FROM bookmarks WHERE name='"+name+"'", null);
				if(c!=null){
					if (c.moveToFirst())
					{
//						System.out.println("-------------inside dbconector "+c.getCount()+" get bookmark ------------");
						String url = c.getString(c.getColumnIndex("url"));
						return url;
					}
					else
						Log.e("getBookmark", "Error: Could not get bookmark for '" + name + "'");
				}
				return null;
			}
			catch(Exception e){
//				System.out.println("-------------exception "+  e  +" while getting bookmarks ------------");
				e.printStackTrace();
				return null;
			}
		}
		
		public boolean getBookmarks(){
			try{
				boolean ret = false;
				ArrayList<String> books = new ArrayList<String>();				
				Cursor cursor = mDatabase.rawQuery("SELECT name, url FROM bookmarks", null);				
				if (cursor==null){ 
		    		return ret; 
		    	}  
				int count = cursor.getCount();
				//Log.v("VER", "count: "+count);
		    	int num = 1;
		    	String bo = null;	
		    	cursor.moveToPosition(1); //Skipping Cancel Gesture
				if(cursor!=null){
					while ( !cursor.isAfterLast() && num < count ) {					
						String name = cursor.getString(cursor.getColumnIndex("name"));						
						String url = cursor.getString(cursor.getColumnIndex("url"));
						bo = name+"|"+url;
						//Log.v("VER", "bo: "+bo);
						books.add(bo);
						num++;
						cursor.moveToNext();
					}
					cursor.close();
				}
				if (num==count){ 
					LandingPage.setBookmark(books);
					ret=true;			
				}
				return ret;
			}
			catch(Exception e){
//				System.out.println("-------------exception "+  e  +" while getting bookmarks ------------");
				Log.v("VER", "e: "+e);
				e.printStackTrace();
				return false;
			}
		}
		
		/**
		 * @param timestamp
		 * @param url
		 * @param title
		 * @param type 0 for browser history, 1 for down-load history and 2 for event history
		 */
		public void addToHistory(String timestamp,String url,String title,int type){					
			try
			{
				mDatabase.execSQL("INSERT INTO padkite_history(timestamp,url,title,type) VALUES('"+timestamp+"','"+url+"','"+title+"',"+type+")");			
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}			
		}
		
		public Cursor getFromHistory(int type){
			try
			{
				Cursor c = mDatabase.rawQuery("SELECT * FROM padkite_history WHERE type="+type+ " ORDER BY timestamp DESC", null);
				if(c!=null){
					int count = c.getCount(); 
					if(count>0){
						return c;
					}
				}
				return null;
			}
			catch(Exception e)
			{				
				e.printStackTrace();
				return null;
			}
		}
}
