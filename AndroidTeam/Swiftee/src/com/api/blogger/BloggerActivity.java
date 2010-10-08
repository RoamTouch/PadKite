package com.api.blogger;


import java.io.IOException;

import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.roamtouch.swiftee.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;


public class BloggerActivity extends Activity {
    
	
	private ProgressBar progressBar;
	private Thread thread;
	private Handler handler;
	private Runnable runnable;
	private boolean isFinished =false;
	private String result;
	private Boolean success = false;	
	private EditText email,pass,postContent;
	private TextView resultView;  
	
	/** Called when the activity is first created. */
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
        setContentView(R.layout.blogger_layout);	
        
        email = (EditText)findViewById(R.id.email);
    	pass = (EditText)findViewById(R.id.password);
    	postContent = (EditText)findViewById(R.id.postContent);
    	
    	String t = getIntent().getExtras().getString("PostContent");
    	postContent.setText(t);
    	
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
						postContent.getText().clear();
						
					}
					isFinished=false;
					progressBar.setVisibility(View.INVISIBLE);
					BloggerActivity.this.finish();
				}
			}

		};


		
    	Button post = (Button)findViewById(R.id.loginButton);
    	post.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				final String emailStr = email.getText().toString();
				if(emailStr.equals("") || emailStr == null){
					email.setError("Enter valid email");
					return;
				}
				final String password = pass.getText().toString();
				if(password.equals("") || password == null){
					pass.setError("Enter gmail password");
					return;
				}
				final String postStr = postContent.getText().toString();
				if(postStr.equals("") || postStr == null){
					postContent.setError("Enter post");
					return;
				}
				progressBar.setVisibility(View.VISIBLE);
				handler.post(runnable);
				thread = new Thread(new Runnable(){
					public void run() {						
						result = postToBlog(emailStr,password,postStr);	
						isFinished = true;
					}
				});	
				thread.start();
			}
    		
    	});
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getPreferences(0); 
        String restoredText1 = prefs.getString("user", null);
        if (restoredText1 != null) 
        	email.setText(restoredText1);         
        String restoredText2 = prefs.getString("pass", null);
        if (restoredText2 != null) 
        	pass.setText(restoredText2);      
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences.Editor editor = getPreferences(0).edit();
        String u =  email.getText().toString();
        editor.putString("user",u);
        editor.putString("pass", pass.getText().toString());
        editor.commit();
    }
    
    public String postToBlog(String email,String pass,String post){
    	BlogConfigBLOGGER.BlogInterfaceType typeEnum = BlogConfigBLOGGER
		.getInterfaceTypeByNumber(1);
        BlogInterface blogapi = null;
		blogapi = BlogInterfaceFactory.getInstance(typeEnum);
		// Log.d(TAG, "Using interface type: " + typeEnum);
		blogapi.setInstanceConfig("");
		
		try {
			String auth_id = blogapi.getAuthId(email,pass);
			Log.d("reply...............", auth_id);
			String postUri = blogapi.getPostUrl();
			boolean publishOk = blogapi.createPost(
					this,
					auth_id,
					postUri,
					null,
					"Blog title",
					null,
					post,
					"Padkite",
					email,
					false);
			if(publishOk)
				return "Post published successfully";
			return "Post was not published successfully";
			//Log.d("reply publish ok...............",""+publishOk);
		} 
		catch (AuthenticationException e) {
			e.printStackTrace();
			return e.toString();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.toString();
		}
    }
}