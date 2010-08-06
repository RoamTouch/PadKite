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

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;


public class FBLoginActivity extends Activity {
	  private FBLoginDialog fbDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        
        fbDialog = new FBLoginDialog(this, FBSession.getSession());
        setContentView(fbDialog);
        fbDialog.show();
    }

  	@Override
  	protected void onDestroy() {
    		super.onDestroy();
    		try {
    			  fbDialog._webView.destroy();
    		} catch (Exception e) {
    			  e.printStackTrace();
    		}
  	}

}
