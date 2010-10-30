package com.roamtouch.swiftee;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.api.blogger.BloggerActivity;
import com.api.facebook.FacebookActivity;
import com.api.twitter.TwitterActivity;
import com.roamtouch.floatingcursor.FloatingCursor;
import com.roamtouch.settings.GestureRecorder;

import roamtouch.webkit.WebView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class GestureActions {

	private BrowserActivity mParent = null;
	private PackageManager mPm = null;  
	
	protected String mSelection = "";
	protected String mTitle = null;
	
	protected void createTitle()
 	{
 		// Stub: Create title from mSelection
 	}
	
	public GestureActions(BrowserActivity parent, String selection)
	{
		mSelection = selection;
		mParent = parent;
		mPm = mParent.getPackageManager();
		
		// Check selection for http
		
    	if (mSelection.startsWith("http://"))
    		mSelection = mParent.getShortLink(mSelection);
    	else if(mSelection.contains("http://")){
    		int start = mSelection.indexOf("http://");
    		int end;
    		int spaceIndex = mSelection.indexOf(' ', start);
    		int lineIndex = mSelection.indexOf('\n', start);
    		if(spaceIndex != -1 && lineIndex !=-1){
    			if(spaceIndex < lineIndex)end = spaceIndex; 
    			else end = lineIndex;
    		}   			
    		else if(spaceIndex == -1 && lineIndex!=-1){
    			end = lineIndex;
    		}
    		else if(spaceIndex != -1 && lineIndex==-1){
    			end = spaceIndex;
    		}
    		else 
    			return;
    		String longUrl = mSelection.substring(start, end);
    		String shortUrl = mParent.getShortLink(longUrl);
    		mSelection = mSelection.replace(longUrl, shortUrl);
//    		Toast.makeText(mParent, mSelection, Toast.LENGTH_LONG).show();
    	}
	}
	
	public void search(WebView webView)
	{
		webView.loadUrl("http://www.google.com/search?q=" + mSelection);
	}

	public void email()
	{
		// Here we need to fire the intent to write an email with the content just pasted
		   
		//  This will not work in the emulator, because the emulator does not have gmail. 
		Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        if (mTitle != null)
        	intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
        intent.putExtra(Intent.EXTRA_TEXT, mSelection);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mParent.startActivity(intent);
	}

	public void calendar()
	{
    	Intent intent = new Intent(Intent.ACTION_EDIT);
    	intent.setType("vnd.android.cursor.item/event");

    	if (mTitle != null)
        	intent.putExtra("title", mTitle);
    	intent.putExtra("description", mSelection);
    	mParent.startActivity(intent);
	}

	public void oldFacebook(String accessToken, long accessExpires)
	{
    	Intent intent = new Intent(mParent,FacebookActivity.class);  	
   		intent.putExtra("Post", mSelection);
   		intent.putExtra("accessToken", accessToken);
   		intent.putExtra("accessExpires", accessExpires);
   		
   		//Log.v("onOldFacebook", "accessToken = " + accessToken);
    	
    	mParent.startActivityForResult(intent, BrowserActivity.FacebookRequestCode);
	}
	
	protected Intent getIntent(Intent intent, String classname)
	{
		ResolveInfo riIntent = null;
		
		List<ResolveInfo> rList = mPm.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY | PackageManager.GET_RESOLVED_FILTER );
        
        for (int i=0; i<rList.size(); i++) 
        {
                ResolveInfo ri = rList.get(i);
                
                if (ri.activityInfo.name.startsWith(classname))
                	riIntent = ri;
        }

        if (riIntent == null)
        	return null;

        Intent intentX = new Intent(intent);

        intentX.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT
                |Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);

        ActivityInfo ai = riIntent.activityInfo;
        intentX.setComponent(new ComponentName(
                ai.applicationInfo.packageName, ai.name));

        return intentX;
	}
	
	public void facebook(String accessToken, long accessExpires)
	{
		        
		        final Intent intent = new Intent(Intent.ACTION_SEND);
		        intent.setType("text/plain");
		        intent.putExtra(Intent.EXTRA_TEXT, mSelection);
		        
		    	// Start Facebook intent directly
		        Intent intentX = getIntent(intent, "com.facebook.katana");

		        intentX = null; // FIXME: FB broken at the moment; revert to old sharing code
 
		        if (intentX != null)
			        mParent.startActivity(intentX);       	
		        else
		        	oldFacebook(accessToken, accessExpires);
	}

	public void oldTwitter()
	{
    	Intent intent = new Intent(mParent,TwitterActivity.class);  	
   		intent.putExtra("Post", mSelection);
    	
		mParent.startActivity(intent);
	}

	public void twitter()
	{	      
	        final Intent intent = new Intent(Intent.ACTION_SEND);
	        intent.setType("text/plain");
	        intent.putExtra(Intent.EXTRA_TEXT, mSelection);
		        
	    	// Start Twitter intent directly
	        Intent intentX = getIntent(intent, "com.twitter.android");
		        
	        if (intentX != null) {
		        mParent.startActivity(intentX);       	
	        }
	        else {
	        	oldTwitter();
	        }
	}
	
	public void blog()
	{
    	Intent intent = new Intent(mParent, BloggerActivity.class);  	
   		intent.putExtra("Post", mSelection);
    	
		mParent.startActivity(intent);
	}
	
	public String translate(String languageTo)
	{	
    	return Translater.text(mSelection, "ENGLISH", languageTo);
	}

	public void wikipedia(WebView webView)
	{	
    	webView.loadUrl("http://en.wikipedia.org/wiki/"+mSelection);
	}

	public void addLink(String url)
	{
    	Intent i = new Intent(mParent,GestureRecorder.class);
		i.putExtra("Gesture_Name", "");
		i.putExtra("isNewBookmark", true);
		i.putExtra("url", url);
		i.putExtra("Gesture_Type", SwifteeApplication.BOOKMARK_GESTURE);
		mParent.startActivity(i);
	}
	
	public void openLink(FloatingCursor floatingCursor)
	{
    	floatingCursor.addNewWindow();
	}
	
	public void send()
	{
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        if (mTitle != null)
        	intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
        intent.putExtra(Intent.EXTRA_TEXT, mSelection);
        mParent.startActivity(Intent.createChooser(intent, "Share ..."));		
	}

	public void download()
	{
		try {
			mParent.new DownloadFilesTask().execute(new URL(mSelection), null, null);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
