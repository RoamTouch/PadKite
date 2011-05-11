package com.api.twitter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import android.util.Log;
import com.api.twitter.TwitterTrendName;

public class TwitterTrends {
	
    private String as_of;
    private List<TwitterTrendName> trends;
    private List<TwitterTrendImage> images;
    
    public String getAs_of() {
        return as_of;
    }
    public void setAs_of(String asOf) {
        as_of = asOf;
    }
    
    //NAME
    public List<TwitterTrendName> getTrends() {
        return trends;
    }
    public void setTrends(List<TwitterTrendName> trends) {
        this.trends = trends;
    } 
    
    //IMAGE
    public List<TwitterTrendImage> getTrendsImages() {
        return images;
    }
    public void setTrendsImages(List<TwitterTrendImage> images) {
        this.images = images;
    }
    
    /**
     * TODO http://api.twitter.com/1/trends/"+location+".json (44418)
     * While using this trend with location parameter the result is an object 
     * MyObject[] jsonObjects = gson.fromJson(jsonText, MyObject[].class); or
     * Map<String, Object>[] result = gson.fromJson(jsonText, HashMap[].class);
     **/
    
    public void runJSONParser(){
    	ArrayList<String> NAMES = new ArrayList<String>();
        try{
	        Log.i("MY INFO", "Json Parser started..");
	        Gson gsonName = new Gson();  
	        Reader rName = new InputStreamReader(getJSONData("http://api.twitter.com/1/trends.json"));                
	        TwitterTrends objsNames = gsonName.fromJson(rName, TwitterTrends.class);
	        for(TwitterTrendName trName : objsNames.getTrends()){	            
	            String name;
	            if (trName.getName().contains("#")){
	            	String[] namePer = trName.getName().split("#");
	            	name = namePer[1];
	            } else {
	            	name = trName.getName();
	            }   
	            NAMES.add("http://api.twitter.com/1/users/profile_image/"+name+".json?size=mini");
	            Log.i("TRENDS", "http://api.twitter.com/1/users/profile_image/"+name+".json?size=mini");
	        }       
        }
        catch(Exception exName){
        	Log.i("MY INFO", "exName: "+exName);
        	exName.printStackTrace();
        }
        
        /*try {
        	Gson gsonImage = new Gson();       	
        	Object ia[] = NAMES.toArray();       	 
        	for(int i=0; i<ia.length; i++){ 
        		String imageUrl = (String) ia[i];
        		Log.i("TRENDS", "imageSgon: "+imageUrl);
        		Reader rImage = new InputStreamReader(getJSONData(imageUrl)); 
        		TwitterTrends objsImages = gsonImage.fromJson(rImage, TwitterTrends.class);
                for(TwitterTrendImage trImage : objsImages.getTrendsImages()){
                 	Log.i("TRENDS", trImage.getImage());
                 }
        	}         
        }
        catch(Exception exImage){
        	Log.i("MY INFO", "exName: "+exImage);
        	exImage.printStackTrace();
        }*/
    }
    
    public InputStream getJSONData(String url){
        DefaultHttpClient httpClient = new DefaultHttpClient();
        URI uri;
        InputStream data = null;
        try {
            uri = new URI(url);
            HttpGet method = new HttpGet(uri);
            HttpResponse response = httpClient.execute(method);
            data = response.getEntity().getContent();
        } catch (Exception e) {
        	Log.i("MY INFO", "exeeee: "+e);
            e.printStackTrace();
        }        
        return data;
    }
    
}