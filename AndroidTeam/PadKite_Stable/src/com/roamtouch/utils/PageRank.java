package com.roamtouch.utils;

import java.net.URL;  
import java.net.URLConnection;  

import org.acra.ErrorReporter;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.temesoft.google.pr.JenkinsHash;  
  
/** 
 * PageRankService provides simple API to Google PageRank Technology 
 */  
public class PageRank {        
  
    /** 
     * List of available google datacenter IPs and addresses 
     */  
    static final public String GOOGLE_PR_DATACENTER_IP = "toolbarqueries.google.com";   
    
    private static final int HTTP_TIMEOUT_MS = 1000;
	private static final String HTTP_TIMEOUT = "http.connection-manager.timeout";
    
	private DefaultHttpClient httpClient;
   
    public PageRank() {
		super();
		httpClient = new DefaultHttpClient();
		httpClient.getParams().setLongParameter(HTTP_TIMEOUT, HTTP_TIMEOUT_MS);
		// TODO Auto-generated constructor stub
	}  
    
    /** 
     * Must receive a domain in form of: "http://www.domain.com" 
     *  
     * @param domain  
     * @return PR rating (int) or -1 if unavailable or internal error happened. 
     */  
    public int getPageRank(String domain) {  
          
        JenkinsHash jHash = new JenkinsHash();  
        long hash = jHash.hash(("info:" + domain).getBytes());  
  
        String url = "http://" + GOOGLE_PR_DATACENTER_IP  
                + "/search?client=navclient-auto&hl=en&" + "ch=6" + hash  
                + "&ie=UTF-8&oe=UTF-8&features=Rank&q=info:" + domain;  
        
        HttpGet method = new HttpGet(url);
		HttpResponse response;
		try {		
			response = httpClient.execute(method);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				JSONArray array = new JSONArray(EntityUtils.toString(response.getEntity())).getJSONArray(1); 
				//return array;
				
				try {
					for (int i = 0; i < array.length(); i++) {
						String rank =  array.getString(i);
						Log.v("rank","rank: "+rank);
					}
				} catch (JSONException e) {
					Log.v("error", "error:" +e);			    
				}
				
			}
		} catch(Exception e) {
			Log.v("error", "error:" +e);			
		}    	
    
        
        /*try {  
            URLConnection conn = new URL(url).openConnection();  
            String pageRankResponse = IOUtils.toString(conn.getInputStream());  
              
            if (StringUtils.isNotBlank(pageRankResponse)) {  
                return NumberUtils.toInt(pageRankResponse.split(":")[2].trim());  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }*/  
          
        return -1;  
    }  
  
    /** 
     * @param args 
     */  
    public static void main(String[] args) {  
        PageRank prService = new PageRank();  
        System.out.println("PageRank: " + prService.getPageRank("http://www.iteye.com"));  
    }
  
}  