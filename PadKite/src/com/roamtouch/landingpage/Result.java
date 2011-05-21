package com.roamtouch.landingpage;

import com.google.gson.annotations.SerializedName;

public class Result {
	
	@SerializedName("profile_image_url")
	public String profileImageUrl;
	
	@SerializedName("created_at")
	public String createdAt;
	
	@SerializedName("from_user")
	public String fromUser;	
	
	public String text;	
	
	/////////////////////////////
	
	@SerializedName("pPage")
	public String p_Page;
	
	@SerializedName("pUrl")
	public String p_Url;
	
	@SerializedName("pTooltip")
	public String p_Tooltip;
	
}
