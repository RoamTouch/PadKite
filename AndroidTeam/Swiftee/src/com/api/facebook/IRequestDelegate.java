package com.api.facebook;

public interface IRequestDelegate {
    /**
     * Called just before the request is sent to the server.
     */
    public void requestLoading(FBRequest request);

    /**
     * Called when an error prevents the request from completing successfully.
     */
    public void requestDidFailWithError(FBRequest request, Throwable error);

    /**
     * Called when a request returns and its response has been parsed.
     * 
     * The resulting object will be a JSONArray or JSONObject,
     * depending on the response type.
     */
    public void requestDidLoad(FBRequest request, Object result);

    /**
     * Called when the request was cancelled.
     */
    public void requestWasCancelled(FBRequest request);
}
