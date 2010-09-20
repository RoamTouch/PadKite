package com.api.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import org.json.JSONObject;

import com.api.facebook.AsyncFacebookRunner;
import com.api.facebook.DialogError;
import com.api.facebook.Facebook;
import com.api.facebook.FacebookError;
import com.api.facebook.Util;
import com.api.facebook.AsyncFacebookRunner.RequestListener;
import com.api.facebook.Facebook.DialogListener;
import com.roamtouch.swiftee.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FacebookActivity extends Activity {
    
    // Your Facebook Application ID must be set before running this example
    // See http://www.facebook.com/developers/createapp.php
    public static final String APP_ID = "153137761374284";
    
    private static final String[] PERMISSIONS =
        new String[] {"publish_stream", "read_stream", "offline_access"};
    
    TextView publicTestsText;
    TextView publicErrorsText;
    Button loginButton;

    Button postButton;
    TextView wallPostText;
    Button logoutButton;
    TextView logoutText;
    
    String tweet;
    
    Facebook authenticatedFacebook = new Facebook();
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent i = getIntent();
        tweet = i.getStringExtra("Post");
        
        setContentView(R.layout.facebook);
        
        publicTestsText = (TextView) findViewById(R.id.publicTests);
        publicErrorsText = (TextView) findViewById(R.id.publicErrors);
        loginButton = (Button) findViewById(R.id.login);

        postButton = (Button) findViewById(R.id.post);
        wallPostText = (TextView) findViewById(R.id.wallPost);
        logoutButton = (Button) findViewById(R.id.logout);
        logoutText = (TextView) findViewById(R.id.logoutTest);
               
        // button to test UI Server login method
        loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                authenticatedFacebook.authorize(FacebookActivity.this, 
                        APP_ID, PERMISSIONS, new TestLoginListener());
            }
        });
        
        // button for testing UI server publish stream dialog
        postButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
              //  authenticatedFacebook.dialog(Tests.this, "stream.publish", 
               //         new TestUiServerListener());
            	Bundle b = new Bundle();
            	b.putString("message", tweet);
                authenticatedFacebook.dialog(FacebookActivity.this, "stream.publish", b,
                        new TestUiServerListener());
                
            }
        });
        
        // enable logout test button
        logoutButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                runTestLogout();
            }
        });
        
//        runTestPublicApi();
    }
    

    public class TestLoginListener implements DialogListener {

        public void onComplete(Bundle values) {
            if (testAuthenticatedApi()) {
            	publicTestsText.setText(
                        "Log in successfull");
            	logoutButton.setVisibility(View.VISIBLE);
            	loginButton.setVisibility(View.INVISIBLE);
            	publicTestsText.setTextColor(Color.GREEN);
            } else {
            	publicTestsText.setText(
                        "Log in  failed");
                publicTestsText.setTextColor(Color.RED);
            }
           
        }

        public void onCancel() {
        }

        public void onError(DialogError e) {
            e.printStackTrace();
        }

        public void onFacebookError(FacebookError e) {
            e.printStackTrace();
        }
    }
    
    public boolean testAuthenticatedApi() {
        if (!authenticatedFacebook.isSessionValid()) return false;
        try {
            Log.d("Tests", "Testing request for 'me'");
            String response = authenticatedFacebook.request("me");
            JSONObject obj = Util.parseJson(response);
            if (obj.getString("name") == null || 
                    obj.getString("name").equals("")) {
                return false;
            }
            
            Log.d("Tests", "Testing graph API wall post");
            Bundle parameters = new Bundle();
            parameters.putString("message", URLEncoder.encode("hello world"));
            parameters.putString("description", 
                    URLEncoder.encode("test test test"));
            response = authenticatedFacebook.request("me/feed", parameters, 
                    "POST");
            Log.d("Tests", "got response: " + response);
            if (response == null || response.equals("") || 
                    response.equals("false")) {
                return false;
            }
            
            Log.d("Tests", "Testing graph API delete");
            response = response.replaceAll("\\{\"id\":\"", "");
            response = response.replaceAll("\"\\}", "");
            response = authenticatedFacebook.request(response, new Bundle(), 
                    "DELETE");
            if (!response.equals("true")) return false;
            
            Log.d("Tests", "Testing old API wall post");
            parameters = new Bundle();
            parameters.putString("method", "stream.publish");
            String attachments = 
                URLEncoder.encode("{\"name\":\"Name=Title\"," +
                		"\"href\":\"http://www.google.fr/\",\"" +
                		"caption\":\"Caption\",\"description\":\"Description" +
                		"\",\"media\":[{\"type\":\"image\",\"src\":" +
                		"\"http://www.kratiroff.com/logo-facebook.jpg\"," +
                		"\"href\":\"http://developers.facebook.com/\"}]," +
                		"\"properties\":{\"another link\":{\"text\":\"" +
                		"Facebook homepage\",\"href\":\"http://www.facebook." +
                		"com\"}}}");
            parameters.putString("attachment", attachments);
            response = authenticatedFacebook.request(parameters);
            Log.d("Tests", "got response: " + response);
            if (response == null || response.equals("") || 
                    response.equals("false")) {
                return false;
            }
            
           
            
            Log.d("Tests", "All Authenticated Tests Passed");
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean testAuthenticatedErrors() {
        if (!authenticatedFacebook.isSessionValid()) return false;
        
        Log.d("Tests", "Testing that request for 'me/invalid' is rejected");
        try {
            Util.parseJson(authenticatedFacebook.request("me/invalid"));
            return false;
        } catch (Throwable e) {
            Log.d("Tests", "*" + e.getMessage() + "*");
            if (!e.getMessage().equals("Unknown path components: /invalid")) {
                return false;
            }
        }
        
        Log.d("Tests", "Testing that old API call with invalid method fails");
        Bundle params = new Bundle();
        params.putString("method", "something_invalid");
        try {
            Util.parseJson(authenticatedFacebook.request(params));
            return false;
        } catch (Throwable e) {
            Log.d("Tests", "*" + e.getMessage() + "*");
            if (!e.getMessage().equals("Unknown method") ) {
                return false;
            }
        }
        
        Log.d("Tests", "All Authenticated Error Tests Passed");
        return true;
    }
    
    public class TestUiServerListener implements DialogListener {

        public void onComplete(Bundle values) {
            final String postId = values.getString("post_id");
            if (postId != null) {
                Log.d("Facebook-Example", "Dialog Success! post_id=" + postId);
                new AsyncFacebookRunner(authenticatedFacebook).request(postId, 
                        new TestPostRequestListener());
            } else {
                FacebookActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        wallPostText.setText("Wall Post Failure");
                        wallPostText.setTextColor(Color.RED);
                    }
                });
            }
        }

        public void onCancel() { }

        public void onError(DialogError e) {
            e.printStackTrace();
        }

        public void onFacebookError(FacebookError e) {
            e.printStackTrace();
        }
    }
    
    public class TestPostRequestListener implements RequestListener {
        
        public void onComplete(final String response) {
            Log.d("Tests", "Got response: " + response);
            try {
                JSONObject json = Util.parseJson(response);
                //final String message = json.getString("message");
                String postId = json.getString("id");
                FacebookActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        wallPostText.setText("Wall Post Success");
                        wallPostText.setTextColor(Color.GREEN);
                    }
                });
                
               
            } catch (Throwable e) {
                e.printStackTrace();
                FacebookActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        wallPostText.setText("Wall Post Failure");
                        wallPostText.setTextColor(Color.RED);
                    }
                });
            }
        }

        public void onFacebookError(FacebookError e) {
            e.printStackTrace();
        }

        public void onFileNotFoundException(FileNotFoundException e) {
            e.printStackTrace();
        }

        public void onIOException(IOException e) {
            e.printStackTrace();
        }

        public void onMalformedURLException(MalformedURLException e) {
            e.printStackTrace();
        }
    }
   
    public void runTestLogout() {
        if (testLogout()) {
            logoutText.setText("Logout successfull");
            logoutText.setTextColor(Color.GREEN);
            logoutButton.setVisibility(View.INVISIBLE);
        	loginButton.setVisibility(View.VISIBLE);
        	publicTestsText.setText("");
        	publicErrorsText.setText("");
        	wallPostText.setText("");
        } else {
            logoutText.setText("Logout  Failed");
            logoutText.setTextColor(Color.RED);
        }
    }
    
    public boolean testLogout() {
        try {
            String oldAccessToken = authenticatedFacebook.getAccessToken();
            
            Log.d("Tests", "Testing logout");
            String response = authenticatedFacebook.logout(this);
            Log.d("Tests", "Got logout response: *" + response + "*");
            if (!response.equals("true")) {
                return false;
            }
            
            Log.d("Tests", "Testing logout on logged out facebook session");
            try {
                Util.parseJson(authenticatedFacebook.logout(this));
                return false;
            } catch (FacebookError e) {
                if (e.getErrorCode() != 101 || 
                        !e.getMessage().equals("Invalid API key") ) {
                    return false;
                }
            }
            
            Log.d("Tests", "Testing logout on unauthenticated object");
            try {
                Util.parseJson(new Facebook().logout(this));
                return false;
            } catch (FacebookError e) {
                if (e.getErrorCode() != 101 || 
                        !e.getMessage().equals("Invalid API key") ) {
                    return false;
                }
            }
            
            Log.d("Tests", "Testing that old access token no longer works");
            Facebook invalidFb = new Facebook();
            invalidFb.setAccessToken(oldAccessToken);
            try {
                Util.parseJson(invalidFb.request("me"));
                return false;
            } catch (FacebookError e) {
                if (!e.getMessage().equals("Error processing access token.")) {
                    return false;
                }
            }
            
            Log.d("Tests", "All Logout Tests Passed");
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // test bad UI server method?
    
    // test invalid permission? <-- UI server test
}