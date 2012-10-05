package com.roamtouch.utils;

import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AsyncImageLoader extends Thread {	
	
	private int type;
	
    public interface AsyncImageCallback {
        /** @brief Called by an AsyncImageLoader upon request completion.
          * @param url Same URL as the one passed to the AsyncImageLoader constructor.
          * @param bm A Bitmap object, or null on failure.
          */
    	
    	void onImageReceived(String url, Bitmap bm);
    	void onVideoReceived(String url, Bitmap bm); 
    }
    /** @brief Start an asynchronous image fetch operation.
      * @param url The URL of the remote picture.
      * @param cb The AsyncImageCallback object you want to be notified the operation completes.
      */
    public AsyncImageLoader(String url, AsyncImageCallback cb, int type) {
        super();
        this.type = type;
        mURL=url;
        mCallback=cb;       
        start();
    }
    public void run() {
        try {
            HttpURLConnection conn = (HttpURLConnection)(new URL(mURL)).openConnection();
            conn.setDoInput(true);
            conn.connect();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inTempStorage = new byte[16*1024];
            if (type==0){
            	mCallback.onImageReceived(mURL,BitmapFactory.decodeStream(conn.getInputStream()));
            } else {
            	mCallback.onVideoReceived(mURL,BitmapFactory.decodeStream(conn.getInputStream()));
            }
        } catch (IOException e) {
        	if (type==0){
        		mCallback.onImageReceived(mURL,null);
        	} else {
        		mCallback.onVideoReceived(mURL,null);
        	}
        }
    }
    private String mURL;
    private AsyncImageCallback mCallback;
}