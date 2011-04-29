
package com.roamtouch.swiftee;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class TrackHelper {
	// Disable it to turn off tracker.
	public static boolean TRACKER_ENABLED = true;
	
	// These are the index
	public static final int EXECUTE_LINK = 0;
	public static final int SELECT_CONTENT = 1;
	public static final int ENTER_ADDERESS = 2;
	public static final int PERFORM_GESTURE = 3;
	public static final int NEW_PAGE = 4;
	
	private static final int MAX_INDEX = NEW_PAGE;

	private int[] track_data;

	private static final String SUMIT_URL = "http://padkite.com/app/statistics.php";
	private static String LOCAL_FILE;

	private static TrackHelper mInstance;
	private Context mContext;
	
	public static synchronized TrackHelper createInstance(Context ctx) {
		if(mInstance == null) {
			mInstance = new TrackHelper(ctx);
		}
		return mInstance;
	}

	private TrackHelper(Context ctx) {
		mContext = ctx;
		track_data = new int[MAX_INDEX + 1];
		LOCAL_FILE = mContext.getCacheDir() + "/udata";
	}

	public static void doTrack(int index, int count) {
		if(!TRACKER_ENABLED || mInstance == null) {
			return;
		}

		if(index > MAX_INDEX) {
			return;
		}
		mInstance.track_data[index] += count;
		Log.e("TrackerHelper", "track data: " + index );
	}

	// Submit current data.
	public void submitNow() {
		if(!TRACKER_ENABLED) {
			return;
		}

		// Do nothing if track data is empty.
		if(isDataEmpty()) {
			return;
		}

        Thread doPost = new Thread(new DoPost(this.toString()));
        doPost.start();
	}

	// Submit saved data.
	public void submitSavedData(int seconds) {
		if(!TRACKER_ENABLED) {
			return;
		}

		// Do nothing if track data is empty.
		String saved = readSavedData();
		if(saved == null) {
			return;
		}

        Thread doPost = new Thread(new DoPost(saved, seconds));
        doPost.start();
	}

	void saveData() {
		if(isDataEmpty()) {
			return;
		}
		try {
			File f = new File(LOCAL_FILE);
			if(!f.exists()) {
				f.createNewFile();
			}
			FileOutputStream os = new FileOutputStream(f);
			os.write(this.toString().getBytes());
			os.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.w("TrackerHelper", "Failed to save data. ", e);
		}
	}

	private String readSavedData() {
		String data = null;
		try {
			File f = new File(LOCAL_FILE);
			if(!f.exists()) {
				return null;
			}
			FileInputStream is = new FileInputStream(f);
			byte[] bytes = new byte[128];
			is.read(bytes);
			data = is.toString();
			is.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.w("TrackerHelper", "Failed to read data. ", e);
		}
		return data;
	}

	public String toString() {
		String string = new String() + track_data[0];
		for(int i = 1; i <= MAX_INDEX; i++) {
			string = string + ";" + track_data[i];
		}
		Log.e("TrackerHelper", "track data: " + string);
		return string;
	}

	private boolean isDataEmpty() {
		boolean empty = true;
		for(int i = 0; i <= MAX_INDEX; i++) {
			if(track_data[i] != 0) {
				empty = false;
				break;
			}
		}
		return empty;
	}

	class DoPost implements Runnable {
		private String mPostData;
		private int mWaitSeconds = 0;

		DoPost(String postData) {
			mPostData = postData;
			mWaitSeconds = 0;
		}

		DoPost(String postData, int wait) {
			mPostData = postData;
			mWaitSeconds = wait;
		}

		public void run() {
			if(mPostData == null) return;

			try {
				Thread.sleep(mWaitSeconds*1000);
				
				URL url = new URL(SUMIT_URL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setConnectTimeout(5000);
				conn.connect();

				// write
				OutputStream outStream = conn.getOutputStream();
				ObjectOutputStream objOutput = new ObjectOutputStream(outStream);
				objOutput.writeObject(mPostData);
				objOutput.flush();

				// read
				//InputStream myInputStream = conn.getInputStream();
			    conn.disconnect();
			    Log.e("TrackerHelper", "track data: done");
			} catch (Exception ee) {
				Log.w("TrackerHelper", "DoPost failed. ", ee);
			} finally {
			}
		}
	}
}