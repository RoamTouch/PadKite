package com.roamtouch.swiftee;

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
}
