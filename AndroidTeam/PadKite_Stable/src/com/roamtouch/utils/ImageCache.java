package com.roamtouch.utils;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.util.Log;

public final class ImageCache {
	
		//http://code.google.com/p/flickr-viewer-for-honeycomb/source/browse/trunk/src/com/gmail/charleszq/utils/ImageCache.java?r=384
	
		public static final int DEF_CACHE_SIZE = 20;
        public static int CACHE_SIZE = DEF_CACHE_SIZE;

        private static final Map<String, SoftReference<Bitmap>> cache = new LinkedHashMap<String, SoftReference<Bitmap>>() {
                private static final long serialVersionUID = 1L;

                /* (non-Javadoc)
                 * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
                 */
                @Override
                protected boolean removeEldestEntry(
                                Entry<String, SoftReference<Bitmap>> eldest) {
                        return size() > CACHE_SIZE;
                }
                
        };

        public static void dispose() {
                for (SoftReference<Bitmap> bm : cache.values()) {
                        if (bm != null && bm.get() != null) {
                                bm.get().recycle();
                        }
                }
                cache.clear();
        }

        public static void saveToCache(String url, Bitmap bitmap) {
                cache.put(url, new SoftReference<Bitmap>(bitmap));
        }
        
        public static boolean hasImage(String key) {
     	    return cache.containsKey(key);
    	 }
        
        public static void deleteBitmap(String key) {
		    cache.remove(key);
        }

        public static Bitmap getFromCache(String url) {
             if(!cache.containsKey(url)){
            	 return null;
             }
            
            Bitmap bitmap = cache.get(url).get();
            if (bitmap == null || bitmap.isRecycled()) {
            		Log.v("trace",""+url);
                    cache.remove(url);
                    bitmap = null;
            }

            return bitmap;
        }
}












/*import java.lang.ref.SoftReference;
import java.util.HashMap;
import android.graphics.Bitmap;
import android.util.Log;

public class ImageCache {
	
	  private static HashMap<String,SoftReference<Bitmap>> cache = new HashMap<String,SoftReference<Bitmap>>();
	  	   
	  
	  public static Bitmap getImage(String key){
	       if(!cache.containsKey(key)){
	           return null;
	       }        	       
	       SoftReference<Bitmap>ref = cache.get(key);
	       if (ref.get()==null){  
	    	   Log.v("trace",""+key);
	    	   return null;
	       }
	       return ref.get();
	   }  
	 
	  public static void setImage(String key, Bitmap image) {		
		  Log.v("key",""+key);
	    cache.put(key, new SoftReference<Bitmap>(image));
	  }
	 
	  public static boolean hasImage(String key) {
	    return cache.containsKey(key);
	  }
	  
	  public static void deleteBitmap(String key) {
		    cache.remove(key);
	  }
	 
	  public static void clear() {
	    cache.clear();
	  }
	}*/