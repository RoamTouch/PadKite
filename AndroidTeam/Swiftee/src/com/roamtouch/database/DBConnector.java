package com.roamtouch.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


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
		
	

		private static final String DATABASE_NAME = "swifteeDB";
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
				db.execSQL("INSERT INTO bookmarks(name,url) VALUES ('Google','http://www.google.com')");
				db.execSQL("INSERT INTO bookmarks(name,url) VALUES ('Yahoo','http://www.yahoo.com')");
				db.execSQL("INSERT INTO bookmarks(name,url) VALUES ('Wikipedia','http://www.wikipedia.com')");
				db.execSQL("INSERT INTO bookmarks(name,url) VALUES ('Picasa','http://www.picasa.google.com')");
				db.execSQL("INSERT INTO bookmarks(name,url) VALUES ('Cancel','Gesture cancelled')");
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

		public void addBookmark(String name,String url){
			try
			{
//				mDatabase.execSQL("INSERT INTO user_profiles(email,username,password) VALUES('"+name+"','"+username+"','"+password+"')");
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		public String getBookmark(String name){
			try{
				Cursor c = mDatabase.rawQuery("SELECT * FROM bookmarks WHERE name='"+name+"'", null);
				if(c!=null){
					c.moveToFirst();
					String url = c.getColumnName(1);
					return url;
				}
				return null;
			}
			catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
}
