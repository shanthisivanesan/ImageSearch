package com.example.imagesearch;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;

public class ImageDisplayActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);
		ImageResults result = (ImageResults) getIntent().getSerializableExtra("result");
		SmartImageView ivImage = (SmartImageView) findViewById(R.id.ivResult);
		ivImage.setImageUrl(result.getFullUrl());
		//Toast.makeText(this, result.getThumbUrl(), Toast.LENGTH_SHORT).show();
	}
}
