package com.roamtouch.swiftee;

import com.api.twitter.Twitter;
import com.api.twitter.TwitterException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class TwitterActivity extends Activity {
	
	private ProgressBar progressBar;
	private Thread thread;
	private Handler handler;
	private Runnable runnable;
	private boolean isFinished =false;
	private String result;
	private Boolean success = false;	
	private EditText user,pass,tweet;
	private TextView resultView;  
	private String userName,password;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
    	setContentView(R.layout.twitter);
    	
    	
    	user = (EditText)findViewById(R.id.username);
    	pass = (EditText)findViewById(R.id.password);
    	//tweet = (EditText)findViewById(R.id.tweet);
    	
    	String t = getIntent().getExtras().getString("Tweet");
    	//tweet.setText(t);
    	
    	progressBar = (ProgressBar)findViewById(R.id.progressBar);

    	resultView = (TextView)findViewById(R.id.result);
    	
		handler = new Handler();
		runnable = new Runnable(){

			public void run() {
				if(!isFinished)
					handler.post(this);
				else{
					//Toast.makeText(context, result, 200).show();
					resultView.setText(result);
					if(success) {
						//user.getText().clear();
						//pass.getText().clear();
						tweet.getText().clear();
					}
					isFinished=false;
					progressBar.setVisibility(View.INVISIBLE);
				}
			}

		};


		
    	Button post = (Button)findViewById(R.id.loginButton);
    	post.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				userName = user.getText().toString();
				if(userName.equals("") || userName == null){
					user.setError("Enter twitter username");
					return;
				}
				password = pass.getText().toString();
				if(password.equals("") || password == null){
					pass.setError("Enter twitter password");
					return;
				}
				final String tweetStr = tweet.getText().toString();
				if(tweetStr.equals("") || tweetStr == null){
					tweet.setError("Enter tweet");
					return;
				}
				progressBar.setVisibility(View.VISIBLE);
				handler.post(runnable);
				thread = new Thread(new Runnable(){
					public void run() {						
						result = sendTweet(userName,password,tweetStr);	
						isFinished = true;
					}
				});	
				thread.start();
			}
    		
    	});
    }
    
    /**
     * Upon being resumed we can retrieve the current state.  This allows us
     * to update the state if it was changed at any time while paused.
     */
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getPreferences(0); 
        String restoredText1 = prefs.getString("user", null);
        if (restoredText1 != null) 
        	user.setText(restoredText1);         
        String restoredText2 = prefs.getString("pass", null);
        if (restoredText2 != null) 
        	pass.setText(restoredText2);      
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences.Editor editor = getPreferences(0).edit();
        String u =  user.getText().toString();
        editor.putString("user",u);
        editor.putString("pass", pass.getText().toString());
        editor.commit();
    }
    
    public String sendTweet(String userName,String password,String tweet){
		if(userName.length()==0 || password.length()==0) {
			success = false;
			return "Enter valid username, password.";
		}
		try{
			if (tweet.length() > 140) {
				tweet.substring(0, 140);
			}
			// Make a Twitter object
			Twitter twitter = new Twitter(userName, password);
			// Set my status
			Twitter.Status status = twitter.updateStatus(tweet);
			if (status.getText().equals(tweet)) {
				success = true;
			}
			else {
				success = false;
			}
		}
		catch(TwitterException.E401 e){
			return "Wrong username or password.";
		}
		catch(TwitterException e) {
			return "Exception from twitter.";
		}
		catch(Exception e) {
			return "Unable to connect.";
		}

		return "Tweet posted successfully.";
	}
}
