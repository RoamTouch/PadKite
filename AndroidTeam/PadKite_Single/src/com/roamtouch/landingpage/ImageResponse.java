package com.roamtouch.landingpage;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ImageResponse {
	
	public List<Result> results;
	
	@SerializedName("image")
	public long image;
	
	public String query;
	
}
