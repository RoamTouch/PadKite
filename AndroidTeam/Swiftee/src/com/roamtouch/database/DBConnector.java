package com.roamtouch.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;


/**
 * This class is for basic communication between local database and application layer
 * This will be modified once the complete database is designed.
 */
public class DBConnector {

		
		private DatabaseHelper mDbHelper;
		private SQLiteDatabase mDatabase;

		private static final String DATABASE_CREATE = "create table user_profiles (_id integer primary key autoincrement, "+
													  "email text not null ," +
													  "username text not null ," +
													  "password text not null);";

		private static final String DATABASE_NAME = "swifteeDB";
		private static final int DATABASE_VERSION = 1;

		private final Context mContext;

		private static class DatabaseHelper extends SQLiteOpenHelper {

			DatabaseHelper(Context context) {
				super(context, DATABASE_NAME, null, DATABASE_VERSION);
			}

			@Override
			public void onCreate(SQLiteDatabase db) {

				db.execSQL(DATABASE_CREATE);
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
				mDatabase.execSQL("INSERT INTO user_profiles VALUES('"+name+"','"+username+"','"+password+"')");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
    	}
}
