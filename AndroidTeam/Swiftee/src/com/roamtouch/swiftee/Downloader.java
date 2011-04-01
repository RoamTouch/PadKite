package com.roamtouch.swiftee;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;

public class Downloader {

	
	
	public static void downloadFile(URL url){
		
			try {
				InputStream myInputStream =null;
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("GET");
				conn.connect();

				myInputStream = conn.getInputStream();
				
				String s = url.toString();
				String arr[] = s.split("/");
				int count = arr.length;
				
				FileOutputStream f = new FileOutputStream(new File(Environment.getExternalStorageDirectory()+"/download",arr[count-1]));
				System.out.println(Environment.getExternalStorageDirectory()+"/download"+arr[count-1]);
			    byte[] buffer = new byte[1024];
			    while (myInputStream.read(buffer) > 0 ) {
			         f.write(buffer);
			    }
			    f.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			} 
	}
	
	public static void updateRemoteContentIfNeeded() {
		new Thread(new Runnable() {

			public void run() {
				try {
					// Update the remote content after 3 seconds.
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				downloadRemoteContent();				
			}
			
		})
		.start();
		;
	}

	private static String REMOTE_CONTENT_URL= "http://padkite.com/app/homepage.html";
	private static String REMOTE_CONTENT_LOCAL_NAME = "content.html";
	private static void downloadRemoteContent() {
		try {
			URL url = new URL(REMOTE_CONTENT_URL);
			InputStream myInputStream = null;
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.connect();

			myInputStream = conn.getInputStream();
			
			String fileName = Environment.getExternalStorageDirectory() + "/Padkite/" + REMOTE_CONTENT_LOCAL_NAME;
			FileOutputStream f = new FileOutputStream(new File(fileName));
			Log.w("Downloader", fileName);
		    byte[] buffer = new byte[1024];
		    while (myInputStream.read(buffer) > 0 ) {
		         f.write(buffer);
		    }
		    f.close();
		    conn.disconnect();
		    Log.w("Downloader", "Home updated.");
		} catch (Exception e) {
			Log.w("Downloader", "Update failed: " + e.getMessage());
		} 
	}
}
