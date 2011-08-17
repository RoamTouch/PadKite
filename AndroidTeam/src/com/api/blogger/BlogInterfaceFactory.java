package com.api.blogger;

import com.api.blogger.BlogConfigBLOGGER.BlogInterfaceType;

//import android.util.Log;


public class BlogInterfaceFactory {

	// private static final String TAG = "BlogInterfaceFactory";

	static BlogInterface instance;

	public static BlogInterface getInstance(BlogInterfaceType type) {
		if (type == BlogConfigBLOGGER.BlogInterfaceType.BLOGGER) {
			if (instance == null || !(instance instanceof BloggerAPI)) {
				instance = new BloggerAPI();
			}
			return instance;
		} else {
			// Log.e(TAG,"Tried to instantiate an unsupported BlogInterface type!");
			return null;
		}
	}

}