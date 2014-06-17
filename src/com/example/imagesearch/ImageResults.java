package com.example.imagesearch;

import java.io.Serializable;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ImageResults implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3276913018732848044L;
	private String fullUrl;
	private String thumbUrl;
	
	public ImageResults (JSONObject json){
		try{
			this.fullUrl = json.getString("url");
			this.thumbUrl = json.getString("tbUrl");
		}
		catch(JSONException e){
			this.fullUrl = null;
			this.thumbUrl = null;
		}
	}
	
	public String getFullUrl() {
		return fullUrl;
	}
	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}
	public String getThumbUrl() {
		return thumbUrl;
	}
	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}

	public static ArrayList<ImageResults> fromJSONArray(
			JSONArray array) {
		ArrayList<ImageResults> results = new ArrayList<ImageResults>();
		for(int x=0;x<array.length();x++){
			try{
				results.add(new ImageResults(array.getJSONObject(x)));
			}
			catch(JSONException e){
				e.printStackTrace();
			}
		}
		return results;
	}
}
