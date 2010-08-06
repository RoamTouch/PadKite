package com.api.facebook;

import java.net.URL;

public interface IDialogDelegate {
  
    /**
     * Called when the dialog succeeds and is about to be dismissed.
     */
    public void dialogDidSucceed(FBDialog dialog);

    /**
     * Called when the dialog is cancelled and is about to be dismissed.
     */
    public void dialogDidCancel(FBDialog dialog);

    /**
     * Called when dialog failed to load due to an error.
     */
    public void dialogDidFailWithError(FBDialog dialog, Throwable error);

    /**
     * Asks if a link touched by a user should be opened in an external
     * browser.
     * 
     * If a user touches a link, the default behavior is to open the link in
     * the Safari browser, which will cause your app to quit. You may want
     * to prevent this from happening, open the link in your own internal
     * browser, or perhaps warn the user that they are about to leave your
     * app. If so, implement this method on your delegate and return NO. If
     * you warn the user, you should hold onto the URL and once you have
     * received their acknowledgement open the URL yourself using
     * [[UIApplication sharedApplication] openURL:].
     */
    public boolean dialogShouldOpenUrlInExternalBrowser(FBDialog dialog, URL url);
}
