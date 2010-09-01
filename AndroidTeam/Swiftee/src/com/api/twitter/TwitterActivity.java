package com.api.twitter;


import com.roamtouch.swiftee.R;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TwitterActivity extends Activity {

	
	private WebView wv;
	private EditText pinText;
	private Button okBut;
	private RequestToken requestToken;
	private AccessToken accessToken;
	private Twitter twitter;
	private String tweet;
	private TextView errorText;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        tweet = getIntent().getStringExtra("Tweet");
        
        setContentView(R.layout.twitter);
        
        wv = (WebView) findViewById(R.id.webview);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setBuiltInZoomControls(true);
        
        pinText = (EditText) findViewById(R.id.pin);
        okBut = (Button) findViewById(R.id.ok);
        
        okBut.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(pinText.getWindowToken(), 0);
				try{
				String pin = pinText.getText().toString();
				 try{
		             if(pin.length() > 0){
		               accessToken = twitter.getOAuthAccessToken(requestToken, pin);
		             }else{
		               accessToken = twitter.getOAuthAccessToken();
		             }
		             
		             Status status = twitter.updateStatus(tweet);
		             errorText.setText("Successfully updated the status to [" + status.getText() + "].");
		             
		          } catch (TwitterException te) {
		            if(401 == te.getStatusCode()){
		            	errorText.setText("Unable to get the access token.");
		            }
		           
		            else{
		            	errorText.setText("Sorry! There was some error in tweeting.Try again later");
		              te.printStackTrace();
		            }
		          }
				}
				catch(Exception e){
					e.printStackTrace();
				}
				}
        });
        
        errorText = (TextView) findViewById(R.id.errorText);
        
        try{
     // The factory instance is re-useable and thread safe.
        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(getResources().getString(R.string.consumer_key), getResources().getString(R.string.consumer_secret));
         requestToken = twitter.getOAuthRequestToken();
         accessToken = null;
        
        wv.loadUrl(requestToken.getAuthorizationURL());
        
        
        }
        catch(Exception e){
        	e.printStackTrace();
        }
      
    }
}
