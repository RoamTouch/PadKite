package com.api.twitter;

import java.util.HashMap;
import java.util.Map;

public class TwitterTrendImage {

	Map<Object, String> mp = new HashMap<Object, String>();

	private String image;
	private String url;

	public String getImage() {
		return image;
	}

	public void setName(String image) {
		this.image = image;
	}	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}