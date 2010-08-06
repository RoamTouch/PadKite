/*
 * Copyright 2009 Codecarpet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.api.facebook;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.api.facebook.FBLoginButton.FBLoginButtonStyle;
import com.api.facebook.FBRequest.FBRequestDelegate;
import com.api.facebook.FBSession.FBSessionDelegate;
import com.roamtouch.swiftee.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FacebookActivity extends Activity {
    private static final String LOG = "FBConnectSample";
    private String textToPost;
    // /////////////////////////////////////////////////////////////////////////////////////////////////
    // This application will not work until you enter your Facebook application's API key here:
    Runnable syncRunnable,runnable;
	boolean uploadFinished=false;
    private static final String API_KEY = "807397dfee15c3684a64d10a2b22fd1d";

    // Enter either your API secret or a callback URL (as described in documentation):
    private static final String API_SECRET = "e72b72af175491ad454188ed5ae34c40";
    private static final String GET_SESSION_PROXY = null; // "<YOUR SESSION CALLBACK)>";
    private static final int PERMISSIONREQUESTCODE = 1;
    //private static final int MESSAGEPUBLISHED = 2;
    // /////////////////////////////////////////////////////////////////////////////////////////////////

    private FBSession mSession;
    private FBLoginButton mLoginButton;
    private TextView mLabel;
    private Button mPermissionButton;
    private Button mFeedButton;
    private Handler mHandler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        Bundle b = this.getIntent().getExtras();
        textToPost = b.getString("Post"); 
        
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN); 
    	
    	
        mHandler = new Handler();

        if (GET_SESSION_PROXY != null) {
            mSession = FBSession.getSessionForApplication_getSessionProxy(API_KEY, GET_SESSION_PROXY, new FBSessionDelegateImpl());
        } else {
            mSession = FBSession.getSessionForApplication_secret(API_KEY, API_SECRET, new FBSessionDelegateImpl());
        }

        setContentView(R.layout.facebook);

        mLabel = (TextView) findViewById(R.id.label);
        mPermissionButton = (Button) findViewById(R.id.permissionButton);
        mPermissionButton.setOnClickListener(new OnClickListener() {

            
            public void onClick(View v) {
                askPermission();
            }
        });
        mFeedButton = (Button) findViewById(R.id.feedButton);
        mFeedButton.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                publishFeed();
            	
            }
        });

        mLoginButton = (FBLoginButton) findViewById(R.id.login);
        mLoginButton.setStyle(FBLoginButtonStyle.FBLoginButtonStyleWide);
        mLoginButton.setSession(mSession);

        mSession.resume(this);

    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////

    private void askPermission() {
        Intent intent = new Intent(this, FBPermissionActivity.class);
        intent.putExtra("permissions", new String[]{"publish_stream"});
        this.startActivityForResult(intent, PERMISSIONREQUESTCODE );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
        case PERMISSIONREQUESTCODE:
            if (resultCode == 1) 
                mPermissionButton.setVisibility(View.INVISIBLE);
        default:
            return;
        }

    }

    private void publishFeed() {

        Log.d("facebook-uploaded","sadawd...........................$$$$$$$$$$$$$$");
  
         Map<String, String> args = new HashMap<String, String>(); 
        args.put("message", textToPost);
       
         FBRequest uploadPhotoRequest = FBRequest.requestWithSession(mSession,new FBRequestDelegateImpl());
      
        uploadPhotoRequest.call("stream.publish",args);
        
    
       
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////

  /*  private class FBDialogDelegateImpl extends FBDialogDelegate {

        @Override
        public void dialogDidFailWithError(FBDialog dialog, Throwable error) {
            mLabel.setText(error.toString());
        }

    }*/

    private void checkPermission() {
        String fql = "select publish_stream from permissions where uid == " + String.valueOf(mSession.getUid());
        Map<String, String> params = Collections.singletonMap("query", fql);
        FBRequest.requestWithDelegate(new FBHasPermissionRD()).call("facebook.fql.query", params);
    }    

    private class FBSessionDelegateImpl extends FBSessionDelegate {

        @Override
        public void sessionDidLogin(FBSession session, Long uid) {
            // we check if the user already has the permissions before displaying permission button
            checkPermission();

            mHandler.post(new Runnable() {
                public void run() {
                    mFeedButton.setVisibility(View.VISIBLE);
                }
            });

            String fql = "select uid,name from user where uid == " + session.getUid();

            Map<String, String> params = Collections.singletonMap("query", fql);
            FBRequest.requestWithDelegate(new FBRequestDelegateImpl()).call("facebook.fql.query", params);
        }



        @Override
        public void sessionDidLogout(FBSession session) {
            mHandler.post(new Runnable() {
                public void run() {
                   // mLabel.setText("");
                    mPermissionButton.setVisibility(View.INVISIBLE);
                    mFeedButton.setVisibility(View.INVISIBLE);
                }
            });
        }

    }

    private class FBRequestDelegateImpl extends FBRequestDelegate {

        @Override
        public void requestDidLoad(FBRequest request, Object result) {

            String name = null;

            if (result instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) result;
                try {
                    JSONObject jo = jsonArray.getJSONObject(0);
                    name = jo.getString("name");
                } catch (JSONException e) {
                    Log.e(LOG, "Login response error", e);
                }
            }
            mLabel.setText("Logged in as " + name);
        }

        @Override
        public void requestDidFailWithError(FBRequest request, Throwable error) {
           // mLabel.setText(error.toString());
        }
    }

    private class FBHasPermissionRD extends FBRequestDelegate {

        @Override
        public void requestDidFailWithError(FBRequest request, Throwable error) {
            super.requestDidFailWithError(request, error);
        }

        @Override
        public void requestDidLoad(FBRequest request, Object result) {
            int hasPermission = 0;

            if (result instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) result;
                try {
                    JSONObject jo = jsonArray.getJSONObject(0);
                    hasPermission = jo.getInt("publish_stream");
                    if (hasPermission == 0) {
                        mHandler.post(new Runnable() {
                            public void run() {
                                mPermissionButton.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                } catch (JSONException e) {
                    Log.e(LOG, "Permission response error", e);
                }
            }
        }
    }
}