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

import com.roamtouch.swiftee.SwifteeApplication;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Point;
import android.util.Log;

public class WebHitTestResult {

    static final String LOGTAG = "webview";   
       
    public static final int TYPE_NO_TYPE	             	= -1;
    public static final int TYPE_UNKNOWN_TYPE              	= 0;
    public static final int TYPE_ANCHOR_TYPE              	= 1;
   	public static final int TYPE_EMAIL_TYPE          		= 4;
   	public static final int TYPE_GEO_TYPE           	    = 3;
   	public static final int TYPE_IMAGE_ANCHOR_TYPE          = 6;
   	public static final int TYPE_IMAGE_TYPE                 = 5;
   	public static final int TYPE_PHONE_TYPE 			    = 2;
   	public static final int TYPE_SRC_ANCHOR_TYPE            = 7;
   	public static final int TYPE_SRC_IMAGE_ANCHOR_TYPE      = 8;    
   	public static final int TYPE_EDIT_TEXT_TYPE		        = 9;    
   	public static final int TYPE_VIDEO_TYPE 				= 10;
   	public static final int TYPE_TEXT_TYPE 					= 11;    
   	public static final int TYPE_INPUT_TYPE 				= 12;
   	public static final int TYPE_SELECT_TYPE 				= 13;
       
   	public static final int TYPE_PADKITE_WINDOWS_MANAGER	= 20;
   	public static final int TYPE_PADKITE_TAB				= 21;
   	public static final int TYPE_PADKITE_ROW				= 22;
   	public static final int TYPE_PADKITE_BUTTON				= 23;
   	public static final int TYPE_PADKITE_BACKGROUND			= 24;
   	public static final int TYPE_PADKITE_PANEL				= 25;
   	public static final int TYPE_PADKITE_INPUT				= 26;
   	public static final int TYPE_PADKITE_TIP_BUTTON			= 27;
   	public static final int TYPE_PADKITE_MORE_LINKS			= 28;
   	public static final int TYPE_PADKITE_SERVER				= 30;
   	public static final int TYPE_PADKITE_BACKGROUND_ROW		= 31;
   	public static final int TYPE_PADKITE_WEB_VIDEO_LINK		= 32;
   	public static final int TYPE_PADKITE_VIDEO_LINK			= 33;
   	
   	public static final int CALLBACK_MOVEHIT 		= 80;
   	public static final int CALLBACK_SINGLE_TOUCH 	= 81;
   	public static final int CALLBACK_REFRESH_MENU 	= 82;

    private int mType;
    private String mExtra;
    private String mToolTip;
    private Rect mRect;
    private Rect panelRect;
	private int mIdentifier;
    private WebVideoInfo mVideoInfo = null;
    private Point mPoint;
    private int mCallBack;
    private String mNodeName;
	
	WebHitTestResult() {
        mType = TYPE_UNKNOWN_TYPE;
        mIdentifier = 0;
    }

    public void setType(int type) {
    	SwifteeApplication.setCType(type);
        mType = type;
    }

    public void setExtra(String extra) {
        mExtra = extra;
    }
    public void setToolTip(String toolTip) {
        mToolTip = toolTip;
    }

    public void setRect(Rect rect) {
        mRect = rect;
    }

    public void setPoint(int x, int y) {
        mPoint = new Point(x, y);
    }

    public void setIdentifier(int identifier) {
        mIdentifier = identifier;
    }

    public void setVideoInfo(WebVideoInfo info) {
        mVideoInfo = info;
    }

    public int getType() {
        return mType;
    }

    public String getExtra() {
        return mExtra;
    }

    public Rect getRect() {
        return mRect;
    }

    public Point getPoint() {
        return mPoint;
    }

    public int getIdentifier() {
        return mIdentifier;
    }

    public String getToolTip() {
        return mToolTip;
    }

    public WebVideoInfo getVideoInfo() {
        return mVideoInfo;
    }

    @Override
    public String toString() {
        return "WebHitTestResult mType=" + mType
            + ", mExtra=" + mExtra
            + ", mToolTip=" + mToolTip
            + ", mIdentifier=" + mIdentifier
            + ", mPoint=" + mPoint
            + ", mRect=" + mRect;
    }

    public void dump() {
        Log.v(LOGTAG, "WebHitTestResult mType=" + mType
            + ", mExtra=" + mExtra
            + ", mToolTip=" + mToolTip
            + ", mIdentifier=" + mIdentifier
            + ", mPoint=" + mPoint
            + ", mRect=" + mRect.toString()) ;
    }

	public String getHref() {
		return mExtra;
	}

	public void setHref(String url) {
		mExtra = url;		
	}

    public Rect getPanelRect() {
		return panelRect;
	}

	public void setPanelRect(Rect panelRect) {
		this.panelRect = panelRect;
	}
	
	public int getCallBack() {
		return mCallBack;
	}

	public void setCallBack(int callBack) {
		this.mCallBack = callBack;
	}
	
	public String getNodeName() {
		return mNodeName;
	}

	public void setNodeName(String nodeName) {
		this.mNodeName = nodeName;
	}

}

