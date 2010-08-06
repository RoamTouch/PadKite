package com.api.facebook;

public interface ISessionDelegate {
    /**
     * Called when a user has successfully logged in and begun a session.
     */
    public void sessionDidLogin(FBSession session, Long uid);

    /**
     * Called when a session is about to log out.
     */
    public void sessionWillLogout(FBSession session, Long uid);

    /**
     * Called when a session has logged out.
     */
    public void sessionDidLogout(FBSession session);
}
