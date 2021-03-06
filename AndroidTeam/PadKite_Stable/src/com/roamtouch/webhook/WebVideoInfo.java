/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.roamtouch.webhook;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

public class WebVideoInfo {
    public String mPoster;
    public String mType;
    public int mVideoWidth;
    public int mVideoHeight;

    WebVideoInfo() {
    }
    public void setPoster(String poster){
        mPoster = poster;
    }
    public void setContentType (String type){
        mType = type;
    }
    public void setVideoSize(int width, int height){
        mVideoWidth = width;
        mVideoHeight = height;
    }
}


