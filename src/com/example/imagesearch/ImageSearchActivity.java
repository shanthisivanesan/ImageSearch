package com.example.imagesearch;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ImageSearchActivity extends Activity {
	EditText etQuery;
	GridView gvResults;
	Button btnSearch;
	ArrayList<ImageResults> imageResults = new ArrayList<ImageResults>();
	ImageResultArrayAdapter imageAdapter;
	AsyncHttpClient client;
	int page=1;
	SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_search);
		setupViews();
		imageAdapter = new ImageResultArrayAdapter(this,imageResults);
		gvResults.setAdapter(imageAdapter);
		gvResults.setOnItemClickListener(new OnItemClickListener(){
			
			public void onItemClick(AdapterView<?> adapter,View parent,int position,long rowId){
				Intent i = new Intent(getApplicationContext(),ImageDisplayActivity.class);
				ImageResults imageRes = imageResults.get(position);
				i.putExtra("result", imageRes);
				startActivity(i);
			}
		});
		
		// Attach the listener to the AdapterView onCreate
		gvResults.setOnScrollListener(new EndlessScrollListener() {
			
			@Override
		    public void onLoadMore(int page, int totalItemsCount) {
	                // Triggered only when new data needs to be appended to the list
	                // Add whatever code is needed to append new items to your AdapterView
				
		        customLoadMoreDataFromApi(page); 
	                // or customLoadMoreDataFromApi(totalItemsCount); 
		    }
        });
	}
	
	//Get the controls
	public void setupViews()
	{
		etQuery = (EditText)findViewById(R.id.etQuery);
		btnSearch = (Button)findViewById(R.id.btnSearch);
		gvResults = (GridView)findViewById(R.id.gvResults);
	}
	// Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
      // This method probably sends out a network request and appends new data items to your adapter. 
      // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
      // Deserialize API response and then construct new objects to append to the adapter
    	
    	client = new AsyncHttpClient();
		//
		 String url ="https://ajax.googleapis.com/ajax/services/search/images";
		 RequestParams params= setParameters(offset);
		 Toast.makeText(this, "url:"+url+",params:"+params, Toast.LENGTH_LONG).show();
		 Log.d("DEBUG","url:"+url+",params:"+params);
		//API request
		 client.get(url, params,new JsonHttpResponseHandler(){
		public void onSuccess(JSONObject response){
			
			JSONArray imageJsonResults = null;
			try
			{
				
				imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
				imageResults.clear();
				imageAdapter.addAll(ImageResults.fromJSONArray(imageJsonResults));
				Log.d("DEBUG",imageResults.toString());
			}
			catch(JSONException e){
				e.printStackTrace();
			}
		}
		});
    }
	//Menu
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu,menu);
		return true;
	}
	
	 public void onSettings(MenuItem mi) {
		// Toast.makeText(this, "Search for: "+mi.toString(),Toast.LENGTH_SHORT).show();
		 Intent intent = new Intent(ImageSearchActivity.this,ImageSettings.class);
		 if(intent!=null)
		 {
			startActivity(intent);
		 }
	  }
	 
	
	
	public RequestParams setParameters(int offset)
	{
		RequestParams params= new RequestParams();
		params.put("rsz","8");
		params.put("start",offset);
		params.put("v","1.0");
		params.put("q",etQuery.getText().toString());
		settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		if(settings!=null)
		{
			params.put("imgsz",settings.getString("size", "small"));
			params.put("as_sitesearch",settings.getString("site", "espn.com"));
			params.put("imgtype",settings.getString("type", "photo"));
			params.put("imgcolor",settings.getString("color", "red"));
    	}		
		return params;
	}

	public abstract class EndlessScrollListener implements OnScrollListener {
		
		// The minimum amount of items to have below your current scroll position
		// before loading more.
		private int visibleThreshold = 5;
		// The current offset index of data you have loaded
		private int currentPage = 0;
		// The total number of items in the dataset after the last load
		private int previousTotalItemCount = 0;
		// True if we are still waiting for the last set of data to load.
		private boolean loading = true;
		// Sets the starting page index
		private int startingPageIndex = 0;
		public EndlessScrollListener() {
		}

		public EndlessScrollListener(int visibleThreshold) {
			this.visibleThreshold = visibleThreshold;
		}

		public EndlessScrollListener(int visibleThreshold, int startPage) {
			this.visibleThreshold = visibleThreshold;
			this.startingPageIndex = startPage;
			this.currentPage = startPage;
		}

		// This happens many times a second during a scroll, so be wary of the code you place here.
		public void onScroll(AbsListView view,int firstVisibleItem,int visibleItemCount,int totalItemCount) 
	        {
			// If the total item count is zero and the previous isn't, assume the
			// list is invalidated and should be reset back to initial state
			if (totalItemCount < previousTotalItemCount) {
				this.currentPage = this.startingPageIndex;
				this.previousTotalItemCount = totalItemCount;
				if (totalItemCount == 0) { this.loading = true; } 
			}

			// If it’s still loading, we check to see if the dataset count has
			// changed, if so we conclude it has finished loading and update the current page
			// number and total item count.
			if (loading && (totalItemCount > previousTotalItemCount)) {
				loading = false;
				previousTotalItemCount = totalItemCount;
				currentPage++;
			}
			
			// If it isn’t currently loading, we check to see if we have breached
			// the visibleThreshold and need to reload more data.
			// If we do need to reload some more data, we execute onLoadMore to fetch the data.
			if (!loading && (totalItemCount - visibleItemCount)<=(firstVisibleItem + visibleThreshold)) {
			    onLoadMore(currentPage + 1, totalItemCount);
			    loading = true;
			}
		}

		// Defines the process for actually loading more data based on page
		public abstract void onLoadMore(int page, int totalItemsCount);

		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// Don't take any action on changed
		}
	}

		
		
		
		//Search Button Click- call Google Image search API
		public void onImageSearch(View v)
		{
			 client = new AsyncHttpClient();
			 Toast.makeText(this, "size:"+page, Toast.LENGTH_LONG).show();
			 String url ="https://ajax.googleapis.com/ajax/services/search/images";
			 RequestParams params= setParameters(page);
			//API request
			 client.get(url, params,new JsonHttpResponseHandler(){
			public void onSuccess(JSONObject response){
				
				JSONArray imageJsonResults = null;
				try
				{
					
					imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
					imageResults.clear();
					imageAdapter.addAll(ImageResults.fromJSONArray(imageJsonResults));
					Log.d("DEBUG",imageResults.toString());
				}
				catch(JSONException e){
					e.printStackTrace();
				}
			}
			});
		}

		//Get More Data
		public void moreData(View v)
		{
			page+=1;
			Toast.makeText(this, "Page: "+page,Toast.LENGTH_SHORT).show();
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			Editor edit = pref.edit();
			edit.putInt("page", page);
			edit.commit();
			customLoadMoreDataFromApi(page);
		}
}
	