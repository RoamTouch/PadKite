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

import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class FBFeedActivity extends Activity {
    public static final String LOG = "FBFeedDialog";
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        FBFeedDialog fbDialog = new FBFeedDialog(this, FBSession.getSession(), extras.getString("userMessagePrompt"), extras.getString("attachment"), extras.getString("actionLinks"), extras.getString("targetId"));

        fbDialog.setDelegate(new IDialogDelegate() {
  
            public void dialogDidCancel(FBDialog dialog) {
                setResult(0);
            }
  
            public void dialogDidFailWithError(FBDialog dialog, Throwable error) {
                Log.e(LOG, "Feed activity dialog failed", error);
            }
  
            public void dialogDidSucceed(FBDialog dialog) {
                setResult(1);
            }
  
            public boolean dialogShouldOpenUrlInExternalBrowser(FBDialog dialog, URL url) {
                return false;
            }
          
        });

        setContentView(fbDialog);
        fbDialog.show();
    }

}
