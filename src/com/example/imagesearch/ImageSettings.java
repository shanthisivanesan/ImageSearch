package com.example.imagesearch;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class ImageSettings extends Activity {

	public String size;
	public Spinner spColor;
	public Spinner spType;
	public EditText etSite;
	public RadioGroup rg;
	Object color,type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_settings);
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String _size = settings.getString("size", "small");
		String _site = settings.getString("site", "espn.com");
		String _type = settings.getString("type", "photo");
		String _color = settings.getString("color", "red");
		
		spColor = (Spinner)findViewById(R.id.spColor);
		//set color
		spColor.setSelection(getIndex(spColor, _color));
		spColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        color = parent.getItemAtPosition(pos);
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});
		spType= (Spinner)findViewById(R.id.spType);
		//set type
		spType.setSelection(getIndex(spType, _type));
		spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        type = parent.getItemAtPosition(pos);
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});
		//set site
		etSite = (EditText)findViewById(R.id.etFilter);
		etSite.setText(_site);
		
		//set radiobutton
		if(_size.equals("small"))
		{
			RadioButton sm=(RadioButton)findViewById(R.id.rdsmall);
			sm.setChecked(true);
		}
	    else if(_size.equals("medium"))
	    {
	    	RadioButton md=(RadioButton)findViewById(R.id.rdmedium);
			md.setChecked(true);
	    }
	    else if(_size.equals("large"))
	    {
	    	RadioButton lg=(RadioButton)findViewById(R.id.rdlarge);
			lg.setChecked(true);
	    }
	    else if(_size.equals("extra large"))
	    {
	    	RadioButton el=(RadioButton)findViewById(R.id.rdextralarge);
			el.setChecked(true);
	    }
		//rg.check(((RadioButton)rg.getChildAt(INDEX)).getId());
	}
	
	//private method to get spinner index
	 private int getIndex(Spinner spinner, String myString)
	 {
	  int index = 0;

	  for (int i=0;i<spinner.getCount();i++){
	   if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
	    index = i;
	    i=spinner.getCount();//will stop the loop, kind of break, by making condition false
	   }
	  }
	  return index;
	 } 
	//radiobutton clicked
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    //Toast.makeText(this, view.getId(), Toast.LENGTH_SHORT).show();
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.rdsmall:
	            if (checked)
	            	size= "small";
	            break;
	        case R.id.rdmedium:
	            if (checked)
	            	size= "medium";
	            break;
	        case R.id.rdlarge:
	            if (checked)
	            	size= "large"; 
	            break;
	        case R.id.rdextralarge:
	            if (checked)
	            	size= "extra large";
	            break;
	    }
	}
	
	//save user preferences
	
	public void onSave(View v)
	{
		//Toast.makeText(this, 
		//		"size:"+size+",type:"+type.toString()+",color:"+color.toString()+",site:"+etSite.getText().toString(), 
		//		Toast.LENGTH_LONG).show();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = pref.edit();
		edit.putString("size", size);
		edit.putString("site", etSite.getText().toString());
		edit.putString("color", color.toString());
		edit.putString("type", type.toString());
		edit.commit();
		// Activity finished ok, return the data
		// setResult(RESULT_OK, data); // set result code and bundle data for response
		  finish(); // closes the activity, pass data to parent
	}
}
