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

package roamtouch.webkit;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

public class WebHitTestResult {

    static final String LOGTAG = "webview";
	
	/**
	 * Default HitTestResult, where the target is unknown
	 */
	public static final int UNKNOWN_TYPE = 0;
	/**
	 * HitTestResult for hitting a HTML::a tag
	 */
	public static final int ANCHOR_TYPE = 1;
	/**
	 * HitTestResult for hitting a phone number
	 */
	public static final int PHONE_TYPE = 2;
	/**
	 * HitTestResult for hitting a map address
	 */
	public static final int GEO_TYPE = 3;
	/**
	 * HitTestResult for hitting an email address
	 */
	public static final int EMAIL_TYPE = 4;
	/**
	 * HitTestResult for hitting an HTML::img tag
	 */
	public static final int IMAGE_TYPE = 5;
	/**
	 * HitTestResult for hitting a HTML::a tag which contains HTML::img
	 */
	public static final int IMAGE_ANCHOR_TYPE = 6;
	/**
	 * HitTestResult for hitting a HTML::a tag with src=http
	 */
	public static final int SRC_ANCHOR_TYPE = 7;
	/**
	 * HitTestResult for hitting a HTML::a tag with src=http + HTML::img
	 */
	public static final int SRC_IMAGE_ANCHOR_TYPE = 8;
	/**
	 * HitTestResult for hitting an edit text area
	 */
	public static final int EDIT_TEXT_TYPE = 9;
	/**
	 * HitTestResult for hitting an HTML5 video tag
	 */
	public static final int VIDEO_TYPE = 10;
	/**
	 * HitTestResult for hitting text a text node
	 */
	public static final int TEXT_TYPE = 11;

	
	
	private int mType;
	private String mExtra;
	private Rect mRect;
	private int mIdentifier;

	WebHitTestResult() {
		mType = UNKNOWN_TYPE;
		mIdentifier = 0;
	}

	public void setType(int type) {
		mType = type;
	}

	public void setExtra(String extra) {
		mExtra = extra;
	}

	public void setRect(Rect rect) {
		mRect = rect;
	}

	public void setIdentifier(int identifier) {
		mIdentifier = identifier;
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

	public int getIdentifier() {
		return mIdentifier;
	}

	public void dump() {
		Log.v(LOGTAG, "WebHitTestResult mType=" + mType
				+ ", mExtra=" + mExtra
				+ ", mIdentifier=" + mIdentifier
				+ ", mRect=" + mRect.toString()) ;
		
	}
		
}

