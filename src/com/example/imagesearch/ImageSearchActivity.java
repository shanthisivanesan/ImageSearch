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

public class ImageSearchActivity extends Activity {
	EditText etQuery;
	GridView gvResults;
	Button btnSearch;
	ArrayList<ImageResults> imageResults = new ArrayList<ImageResults>();
	ImageResultArrayAdapter imageAdapter;

	int currentPage=1;
	
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
	}
	//Menu
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu,menu);
		return true;
	}
	//Get More Data
	public void moreData(View v)
	{
		currentPage+=1;
		Toast.makeText(this, "Page: "+currentPage,Toast.LENGTH_SHORT).show();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = pref.edit();
		edit.putInt("page", currentPage);
		edit.commit();
		onImageSearch(v);
	}
	
	 public void onSettings(MenuItem mi) {
		// Toast.makeText(this, "Search for: "+mi.toString(),Toast.LENGTH_SHORT).show();
		 Intent intent = new Intent(ImageSearchActivity.this,ImageSettings.class);
		 if(intent!=null)
		 {
			startActivity(intent);
		 }
	  }
	 
	public void setupViews()
	{
		etQuery = (EditText)findViewById(R.id.etQuery);
		btnSearch = (Button)findViewById(R.id.btnSearch);
		gvResults = (GridView)findViewById(R.id.gvResults);
	}
	//Search Button Click- call Google Image search API
	public void onImageSearch(View v)
	{
		String query = etQuery.getText().toString();
		AsyncHttpClient client = new AsyncHttpClient();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String size = settings.getString("size", "small");
		String site = settings.getString("site", "espn.com");
		String type = settings.getString("type", "photo");
		String color = settings.getString("color", "red");
		//Toast.makeText(this, "size:"+size+"site:"+site+"type:"+type+"color:"+color, Toast.LENGTH_LONG).show();
		//API request
		client.get("https://ajax.googleapis.com/ajax/services/search/images?rsz=8&" +
				"start="+ currentPage+"&v=1.0&q="+Uri.encode(query)+"&imgtype="+type+"&as_sitesearch="+site+"&imgsz="+size+"&imgcolor"+color,new JsonHttpResponseHandler(){
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
}
