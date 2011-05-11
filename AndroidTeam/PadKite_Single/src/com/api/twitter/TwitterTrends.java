package com.api.twitter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
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
    
    public String getAs_of() {
        return as_of;
    }
    public void setAs_of(String asOf) {
        as_of = asOf;
    }
    public List<TwitterTrendName> getTrends() {
        return trends;
    }
    public void setTrends(List<TwitterTrendName> trends) {
        this.trends = trends;
    } 
    
    public void runJSONParser(){
        try{
	        Log.i("MY INFO", "Json Parser started..");
	        Gson gson = new Gson();
	        Reader r = new InputStreamReader(getJSONData("http://api.twitter.com/1/trends.json"));
	        
	        /**
	         * TODO http://api.twitter.com/1/trends/"+location+".json (44418)
	         * While using this trend with location parameter the result is an object 
	         * MyObject[] jsonObjects = gson.fromJson(jsonText, MyObject[].class); or
	         * Map<String, Object>[] result = gson.fromJson(jsonText, HashMap[].class);
	         **/
	        
	        Log.i("MY INFO", ""+r.toString());
	        TwitterTrends objs = gson.fromJson(r, TwitterTrends.class);
	        Log.i("MY INFO", "CACA: "+objs.getTrends().size());
	        for(TwitterTrendName tr : objs.getTrends()){
	            Log.i("TRENDS", tr.getName() + " - " + tr.getUrl());
	        }
	        }catch(Exception ex){
	        	Log.i("MY INFO", "ex: "+ex);
	            ex.printStackTrace();
        }
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