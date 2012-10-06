package com.roamtouch.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.text.ClipboardManager;
import android.util.Log;
import com.roamtouch.database.DBHelper;


/**
 * This class is for basic communication between local database and application layer
 * This will be modified once the complete database is designed.
 */
public class DBConnector {

	
		private DBHelper mDbHelper;
		private SQLiteDatabase mDatabase;

		private static final String DATABASE_CREATE1 = "create table user_profiles (" +
	    		"_id integer primary key autoincrement, "+
	    		"email text not null ," +
	    		"username text not null ," +
	    		"password text not null);" ;

		private static final String DATABASE_CREATE2 = "create table bookmarks (" +
				"_id integer primary key autoincrement, "+
				"name text not null ," +
				"url text not null);" ;
		
		private static final String DATABASE_CREATE3 = "create table padkite_history (" +
				"_id integer primary key autoincrement, "+
				"timestamp text not null ," +
				"url text not null ," +
				"title text not null ," +
				"type int);" ;
		
		private static final String DATABASE_CREATE4 = "create table padkite_landing_page (" +
				"_id integer primary key autoincrement, "+
				"timestamp text not null ," +
				"html text not null ," +
				"twitterSearch text not null);";
		
		private static final String DATABASE_CREATE5 = "create table windows_manager (_id integer primary key autoincrement, "+
			"_idWm type int ," +	  
			"title text not null ," +
			"url text not null ," +
			"bitmap text not null);"; 

		private static final String DATABASE_CREATE6 = "create table window_set (_id integer primary key autoincrement, "+		
			"desc text not null ," + 
			"type int);" ; //Optional default or mine.

		
		private static final String DATABASE_CREATE7 = "create table padkite_notes (" +
				"_id integer primary key autoincrement, "+		
				"created text not null ," +
				"last_edited text ," +
				"title text not null ," +
				"note text not null);";

		private static final String DATABASE_CREATE8 = "create table suggestions (" +
			"_id integer primary key autoincrement, "+		
			"desc text not null," +
			"timestamp text not null );";
		
		private static final String DATABASE_CREATE9 = "create table clipboard (" +
				"_id integer primary key autoincrement, "+		
				"desc text not null, " +
				"timestamp text not null );";
		
		private static final String DATABASE_INSERT_COLUMN = "ALTER TABLE clipboard ADD COLUMN timestamp;";
			
		private static final String DATABASE_CREATE10 = "create table recent_inputs (" +
				"_id integer primary key autoincrement, "+		
				"desc text not null," +
				"timestamp text not null );";
	     
		private static final String DATABASE_CREATE11 = "create table voice (" +
				"_id integer primary key autoincrement, "+		
				"desc text not null," +
				"timestamp text not null );";
		
		private static final String DATABASE_CREATE12 = "create table most_visited (" +
				"_id integer primary key autoincrement, "+
				"amount integer not null,"+
				"title text not null ," +
				"url text not null );";
		
		private static final String DATABASE_CREATE13 = "create table site_links (" +
				"_id integer primary key autoincrement, "+				
				"identifier integer not null,"+
				"mainUrl text not null,"+
				"title text not null ,"+
				"url text not null );";
		
		private static final String DATABASE_CREATE14 = "create table my_videos (" +
				"_id integer primary key autoincrement, "+						
				"title text not null,"+
				"videoId text not null,"+
				"url text not null );";
		
		private static final String DATABASE_NAME = "padkiteDB";
		private static final int DATABASE_VERSION = 1;

		private final Context mContext;		

		public DBConnector(Context ctx) {
			this.mContext = ctx;
		}
		
		public void checkCreatedTables(SQLiteDatabase db){
			
			if (db==null)	
				db = mDatabase;
			
			boolean up = checkTableExist("user_profiles");
			if (!up)
				db.execSQL(DATABASE_CREATE1);
			
			boolean bo = checkTableExist("bookmarks");
			if (!bo)
				db.execSQL(DATABASE_CREATE2);
			
			boolean ph = checkTableExist("padkite_history");
			if (!ph)
				db.execSQL(DATABASE_CREATE3);
			
			boolean plp = checkTableExist("padkite_landing_page");
			if (!plp)
				db.execSQL(DATABASE_CREATE4);
			
			boolean wm = checkTableExist("windows_manager");
			if (!wm)
				db.execSQL(DATABASE_CREATE5);
			
			boolean ws = checkTableExist("window_set");
			if (!ws){
				try {
					db.execSQL(DATABASE_CREATE6);
				} catch(Exception e){
						e.printStackTrace();
				}
			}
			
			boolean pn = checkTableExist("padkite_notes");
			if (!pn)
				db.execSQL(DATABASE_CREATE7);
			
			boolean su = checkTableExist("suggestions");
			if (!su)
				db.execSQL(DATABASE_CREATE8);
			
			boolean cb = checkTableExist("clipboard");
			if (!cb)
				db.execSQL(DATABASE_CREATE9);
			
			//mDatabase.execSQL(DATABASE_INSERT_COLUMN);
			
			insertClipBoard("hola");
			
			boolean ri = checkTableExist("recent_inputs");
			if (!ri)
				db.execSQL(DATABASE_CREATE10);
			
			boolean vo = checkTableExist("voice");
			if (!vo)
				db.execSQL(DATABASE_CREATE11);
			
			boolean mv = checkTableExist("most_visited");
			if (!mv)
				db.execSQL(DATABASE_CREATE12);					
			
			
			boolean sl = checkTableExist("site_links");
			if (!sl)
				db.execSQL(DATABASE_CREATE13);	
			
			boolean mV = checkTableExist("my_videos");
			if (!mV)
				db.execSQL(DATABASE_CREATE14);	
			
		}	
		
		public DBConnector open() {
			mDbHelper = new DBHelper(mContext, this);
			mDatabase = mDbHelper.getWritableDatabase();
			return this;
		}

		public void close() {
			mDbHelper.close();
		}
		
		public boolean checkTableExist(String tableName){
			
			try{
				Cursor c = mDatabase.rawQuery("SELECT COUNT() FROM sqlite_master WHERE type='table' AND name ='"+tableName+"';", null);
				if(c!=null){
					c.moveToFirst();
					int count = c.getInt(0);
					if(count>0){
						c.close();
						return true;
					} else {
						return false;
					}
						
				}
				c.close();
				return false;
			}
			catch(Exception e){
				e.printStackTrace();
				return false;
			}
						
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
		
		public void deleteAllBookmarks() {
			mDatabase.execSQL("DELETE FROM bookmarks");
		}
		
		public boolean checkIfBookmarkAdded(){
			
			boolean check = false;
			Cursor cursor = null;
			try{
				cursor = mDatabase.rawQuery("SELECT count(*) FROM user_profiles", null);
				if(cursor!=null){
					cursor.moveToFirst();
					int count = cursor.getInt(0);
					if(count>0){
						check = true;
					} else {
						check = false;
					}
				}
				
			}	
			finally { if (cursor != null) {
					cursor.close();
				}	
		    }			
		    return check;
		}

		public void addBookmark(){
			try
			{
				mDatabase.execSQL("INSERT INTO bookmarks(name,url) VALUES ('Cancel','Gesture cancelled')");
				mDatabase.execSQL("INSERT INTO bookmarks(name,url) VALUES ('Padkite','http://padkite.com')");
				mDatabase.execSQL("INSERT INTO bookmarks(name,url) VALUES ('PadKite','http://padkite.com')");
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
		
		public Cursor getBookmarks(){		
			Cursor c = null;
			try
			{
				c = mDatabase.rawQuery("SELECT * FROM bookmarks", null);
				if(c!=null){					
					if(c.getCount()>0){						
						return c;
					} else {
						return null;
					}
				} else {
					return null;
				}
			
			} catch(Exception e) {
				Log.v("error", "error: " + e);
				e.printStackTrace();
				return null;
			}	
			
		}
		
		
		public Cursor getMostVisited(){		
			Cursor c = null;
			try
			{
				c = mDatabase.rawQuery("SELECT title, url, amount FROM most_visited ORDER BY amount", null);
				if(c!=null){					
					if(c.getCount()>0){
						int ver = c.getCount();						
						return c;
					} else {
						c.close();
						return null;
					}
				} else {
					c.close();
					return null;
				}
			
			} catch(Exception e) {
				e.printStackTrace();
				return null;
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
				Cursor c = mDatabase.rawQuery("SELECT * FROM padkite_history WHERE type="+type, null);
				if(c!=null)
					if(c.getCount()>0)
						return c;
				
				return null;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
		public Cursor getFromHistoryIntoArray(int type){
			try
			{
				Cursor c = mDatabase.rawQuery("SELECT * FROM padkite_history WHERE type="+type+" ORDER BY timestamp ASC LIMIT 10", null);
				if(c!=null)
					if(c.getCount()>0)
						return c;
				
				return null;
			}
			catch(Exception e)
			{
				Log.v("fuck", "e: "+e);
				e.printStackTrace();
				return null;
			}
		}
		
		public int countFromHistory(String title){
			int amount = 0;
			try
			{
				Cursor c = mDatabase.rawQuery("SELECT COUNT(title) FROM padkite_history WHERE title='"+title+"' AND type=1", null);			
				if(c!=null){
					if(c.getCount()>0){
						String n = c.getString(0);
						amount = Integer.parseInt(n);		 
						return amount;
					}					
				}				
				return amount;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return amount;
			}
		}
		
		public void insertMostVisited(int amount, String title, String url){
			
			try {
				mDatabase.execSQL("INSERT INTO most_visited(amount,title,url) VALUES('"+amount+"','"+title+"','"+url+"')");
			
			} catch(Exception e) {
				e.printStackTrace();
			}			
    	}
		
		public void deleteMostVisited() {
			mDatabase.execSQL("DELETE FROM most_visited");
		}
		
		public Cursor getSiteLinks(int identifier){		
			Cursor c = null;
			try
			{
				c = mDatabase.rawQuery("SELECT title, url FROM site_links WHERE identifier= '"+identifier+"' ORDER BY title ASC", null);
				if(c!=null){					
					if(c.getCount()>0){
						int ver = c.getCount();
						return c;
					} else {
						return null;
					}
				} else {
					return null;
				}
			
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}	
			
		}
		
		public void insertSiteLinks(String mainUrl, int identifier, String title, String url){
			
			try {
				mDatabase.execSQL("INSERT INTO site_links(mainUrl, identifier,title,url) VALUES('"+mainUrl+"','"+identifier+"','"+title+"','"+url+"')");
			
			} catch(Exception e) {
				e.printStackTrace();
			}			
    	}
		
		public void deleteSiteLinks() {
			mDatabase.execSQL("DELETE FROM site_links");
		}
		
		public Cursor insertTabs(int id, String title,String url,String bitmap){
			
			try {
				mDatabase.execSQL("INSERT INTO windows_manager(_idWm,title,url,bitmap) VALUES('"+id+"','"+title+"','"+url+"','"+bitmap+"')");			
			} catch(Exception e) {
				e.printStackTrace();
			}			
			
			Cursor c = getWindowsManagerListById(id);
			return c;
			
    	}
		
		public int insertWindowSet(String name, int type){		
		
			int id=0;
			
			try {
				mDatabase.execSQL("INSERT INTO window_set(desc, type) VALUES('"+name+"',"+type+");"); 
			} catch(Exception e) {
				e.printStackTrace();
			}				
			
			
			try	{
				Cursor c = mDatabase.rawQuery("SELECT _id FROM window_set WHERE desc='"+name+"' AND type='"+type+"'", null);
				if(c!=null){
					c.moveToFirst();
					id = c.getInt(0);
					c.close();
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}		
			
			return id;
			
    	}
		
		public int existVideo(String videoId){
			
			int id=-1;
			
			try	{
				Cursor c = mDatabase.rawQuery("SELECT _id FROM my_videos WHERE videoId='" + videoId + "'", null);
				if(c!=null){
					if (c.moveToFirst()){
						id = c.getInt(0);
					}
					c.close();					
					return id;
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}		
			
			return id;
		}	
	
		public int insertVideo(String title, String videoId, String url){		
			
			int id=0;
			
			try {
				mDatabase.execSQL("INSERT INTO my_videos(title, videoId, url) VALUES('"+title+"','"+videoId+"','"+url+"');"); 
			} catch(Exception e) {
				e.printStackTrace();
			}	
			
			return id;
			
    	}
		
		
		public int existWindowsSetByName(String name){
			
			int id=-1;
			
			try	{
				Cursor c = mDatabase.rawQuery("SELECT _id FROM window_set WHERE desc='"+name+"'", null);
				if(c!=null){
					if (c.moveToFirst()){
						id = c.getInt(0);
					}
					c.close();					
					return id;
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}		
			
			return id;
		}		
		
		
		
		public Cursor getWindowsSets(int type){
			
			Cursor c = null;
			try
			{
				c = mDatabase.rawQuery("SELECT _id, desc FROM window_set WHERE type='"+type+"'", null);
				if(c!=null){					
					if(c.getCount()>0){
						int ver = c.getCount();
						return c;
					} else {
						return null;
					}
				} else {
					return null;
				}
			
			} catch(Exception e) {
				Log.v("error", "e: "+e);
				e.printStackTrace();
				return null;
			}
			
			//finally { if (c != null) {
			//	c.close();
			//}    			
		}
		
		public Cursor getMyVideos(){
			
			Cursor c = null;
			try
			{				
				c = mDatabase.rawQuery("SELECT * FROM my_videos", null); 
				if(c!=null){					
					if(c.getCount()>0){
						int ver = c.getCount();
						return c;
					} else {
						return null;
					}
				} else {
					return null;
				}
			
			} catch(Exception e) {
				Log.v("error", "e: "+e);
				e.printStackTrace();
				return null;
			}
			
			//finally { if (c != null) {
			//	c.close();
			//}    			
		}	
		
		
		/*public boolean existWindowsSet(String name){
			try
			{
				Cursor c = mDatabase.rawQuery("SELECT "+name+" count(*) FROM window_set group by '"+name+"'", null);
				if(c!=null)
					if(c.getCount()>0){
						return true;
					} else {
						return false;
					}		
			} catch(Exception e) {
				e.printStackTrace();
				return false;
			}
			return false;
		}*/	
		
		
		public Cursor getWindowsManagerListById(int id){
			try
			{
				Cursor c = mDatabase.rawQuery("SELECT title,url,bitmap FROM windows_manager WHERE _idWm='"+id+"'", null); // WHERE type="+type, null);
				if(c!=null){					
					if(c.getCount()>0){
						int ver = c.getCount();
						return c;						
					} else {
						c.close();
						return null;
					}				
				} else {
					c.close();
					return null;
				}
			
			} catch(Exception e) {
				Log.v("dale", "error: "+e);
				//e.printStackTrace();
				return null;
			}			
		}		
		
		public boolean getWindowsManagerListByIdAndTitle(int id, String title){

			try {
				
				Cursor c = mDatabase.rawQuery("SELECT title,url,bitmap FROM windows_manager WHERE _idWm='"+id+"' AND title='"+title+"'", null); // WHERE type="+type, null);
				if(c!=null){					
					if(c.getCount()>0){						
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			
			} catch(Exception e) {
				Log.v("dale", "error: "+e);
				//e.printStackTrace();
				return false;
			}			
		}	
		
		public int clearAllTabs(){			
			int ret = 0;
			try {
				ret = mDatabase.delete("window_set", null, null);				
			} catch(Exception e) {
				Log.v("error", "e: "+e);
				e.printStackTrace();
			}	
			
			try {
				ret = mDatabase.delete("windows_manager", null, null);				
			} catch(Exception e) {
				Log.v("error", "e: "+e);
				e.printStackTrace();
			}	
			
			return ret;
		}
		
		public void dropTable(String tableName){			
			try {
				mDatabase.execSQL("DROP TABLE IF EXISTS "+tableName);			
			} catch(Exception e) {
				Log.v("error", "e: "+e);
				e.printStackTrace();
			}
		}
		
		public boolean deleteWindowSetById(int id){
			
			try {
				mDatabase.delete("window_set", "_id"+"="+id, null);			
				
			} catch(Exception e) {				
				Log.v("error", "e: "+e);
				e.printStackTrace();
				return false;				
			}
			
			try {
				mDatabase.delete("windows_manager", "_id"+"="+id, null);
			} catch(Exception e) {				
				Log.v("error", "e: "+e);
				e.printStackTrace();
				return false;
			}	
			return true;
			
		}
		
		public void deleteWindowSetAndList(){
			
			try {							
				mDatabase.execSQL("DELETE FROM window_set");
			} catch(Exception e) {				
				Log.v("error", "e: "+e);
				e.printStackTrace();				
			}
			
			try {
				mDatabase.execSQL("DELETE FROM windows_manager");
			} catch(Exception e) {				
				Log.v("error", "e: "+e);
				e.printStackTrace();
			}
			
		}
		
		public Cursor getSuggestion(String like){	
			try
			{
				Cursor c = mDatabase.rawQuery("SELECT desc FROM suggestions WHERE desc like '"+like+"' ORDER BY desc ASC LIMIT 10", null); 
				if(c!=null){
					if(c.getCount()>0){
						return c;
					} else {
						c.close();
						return null;
					}
				} else {
					c.close();
					return null;
				}			
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}						
    	}
		
		public void insertSuggestion(String suggestion){
			
			String timestamp = System.currentTimeMillis()+"";
			
			try {
				mDatabase.execSQL("INSERT INTO suggestions(desc, timestamp) VALUES('"+suggestion+"','"+timestamp+"')");
			
			} catch(Exception e) {
				e.printStackTrace();
			}			
    	}
		public void deleteAllSuggestions() {
			mDatabase.execSQL("DELETE FROM suggestions");
		}
		
		public Cursor getClipBoard(){	
			try
			{
				//Cursor c = mDatabase.rawQuery("SELECT desc FROM clipboard ORDER BY timestamp ASC LIMIT 20", null);
				Cursor c = mDatabase.rawQuery("SELECT desc FROM clipboard", null);
				if(c!=null)
					if(c.getCount()>0)
						return c;
				
				return null;
			
			} catch(Exception e) {
				Log.v("error",""+e);
				e.printStackTrace();
				return null;
			}						
    	}
		
		public boolean existClipBoard(String clipboard){	
			try
			{
				Cursor c = mDatabase.rawQuery("SELECT desc FROM clipboard WHERE desc='"+clipboard+"'", null);
				if(c!=null)
					if(c.getCount()>0)
						return true;
				
				return false;
			
			} catch(Exception e) {
				e.printStackTrace();
				return false;
			}						
    	}
		
		public void insertClipBoard(String clipboard){
			
			String timestamp = System.currentTimeMillis()+"";
			try {
				mDatabase.execSQL("INSERT INTO clipboard(desc, timestamp) VALUES('"+clipboard+"','"+timestamp+"')");
			
			} catch(Exception e) {
				e.printStackTrace();
			}			
    	}
		
		public void deleteAllClipBoards() {
			mDatabase.execSQL("DELETE FROM clipboard");
		}
		
		public Cursor getRecent(){	
			try
			{
				Cursor c = mDatabase.rawQuery("SELECT desc FROM recent_inputs ORDER BY timestamp ASC LIMIT 20", null); // WHERE type="+type, null);
				if(c!=null)
					if(c.getCount()>0)
						return c;
				
				return null;
			
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}						
    	}
		
		public void insertRecent(String recent){
			
			String timestamp = System.currentTimeMillis()+"";
			
			try {
				mDatabase.execSQL("INSERT INTO recent_inputs(desc, timestamp) VALUES('"+recent+"','"+timestamp+"')");
			
			} catch(Exception e) {
				e.printStackTrace();
			}			
    	}
		
		public Cursor getVoice(){	
			try
			{
				Cursor c = mDatabase.rawQuery("SELECT desc FROM voice ORDER BY timestamp ASC LIMIT 20", null);
				if(c!=null)
					if(c.getCount()>0)
						return c;
				
				return null;
			
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}						
    	}
		
		public void insertVoice(String voice){
			
			String timestamp = System.currentTimeMillis()+"";
			
			try {
				mDatabase.execSQL("INSERT INTO recent_inputs(desc, timestamp) VALUES('"+voice+"','"+timestamp+"')");
			
			} catch(Exception e) {
				e.printStackTrace();
			}			
    	}

		
		/*public void customCall(){
			
			try {
				mDatabase.execSQL(DATABASE_CREATE4);
			} catch(Exception e) {
				Log.v("error", "e: "+e);
				e.printStackTrace();
			}		
				
			try {	
				mDatabase.execSQL(DATABASE_CREATE5);
			} catch(Exception e) {
				Log.v("error", "e: "+e);
				e.printStackTrace();
			}	
		}*/

		
		
		
		
		
		
}
