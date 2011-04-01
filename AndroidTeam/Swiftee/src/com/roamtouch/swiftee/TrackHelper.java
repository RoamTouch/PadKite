
package com.roamtouch.swiftee;

import android.util.Log;

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

	private static int[] track_data = new int[MAX_INDEX + 1];

	private static final String SUMIT_URL = "http://padkite.com/app/statistics.php";

	public static void doTrack(int index, int count) {
		if(!TRACKER_ENABLED) {
			return;
		}

		if(index > MAX_INDEX) {
			return;
		}
		track_data[index] += count;
		Log.e("TrackerHelper", "track data: " + index );
	}

	public static void submit() {
		if(!TRACKER_ENABLED) {
			return;
		}

		// Do nothing if track data is empty.
		boolean empty = true;
		for(int i = 0; i <= MAX_INDEX; i++) {
			if(track_data[i] != 0) {
				empty = false;
				break;
			}
		}
		if(empty) {
			return;
		}

        Thread doPost = new Thread(new DoPost());
        doPost.start();
}

	static class DoPost implements Runnable {
		public void run() {
			// Build the data string.
			String postString = new String() + track_data[0];
			for(int i = 1; i <= MAX_INDEX; i++) {
				postString = postString + ";" + track_data[i];
			}
			Log.e("TrackerHelper", "track data: " + postString);
			try {
				URL url = new URL(SUMIT_URL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setConnectTimeout(5000);
				conn.connect();

				// write
				OutputStream outStream = conn.getOutputStream();
				ObjectOutputStream objOutput = new ObjectOutputStream(outStream);
				objOutput.writeObject(postString);
				objOutput.flush();

				// read
				InputStream myInputStream = conn.getInputStream();
			    conn.disconnect();
			    Log.e("TrackerHelper", "track data: done");
			} catch (Exception ee) {
				Log.w("TrackerHelper", "DoPost failed. ", ee);
			} finally {
			}
		}
	}
}