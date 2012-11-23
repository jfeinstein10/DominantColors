package com.dominantcolors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class DominantColorsMainActivity extends Activity {

	public static final int GET_IMAGE = 1;

	private EditText mNumColors;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		mNumColors = (EditText) findViewById(R.id.main_numcolors);

		((Button) findViewById(R.id.live_button)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(DominantColorsMainActivity.this, DominantColorsLiveActivity.class);
				startActivity(intent);
			}
		});
		
		((Button) findViewById(R.id.take_button)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(takePhotoIntent, GET_IMAGE);
			}
		});
		
		((Button) findViewById(R.id.pick_button)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					Intent pickIntent = new Intent();
					pickIntent.setType("image/*");
					pickIntent.setAction(Intent.ACTION_GET_CONTENT);					
					String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
					Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
					startActivityForResult(chooserIntent, GET_IMAGE);
				} catch (NumberFormatException e) {
					// number was not valid
				}
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == GET_IMAGE) {
				int numColors = Integer.valueOf(mNumColors.getText().toString());
				Intent intent = DominantColorsResultActivity.newInstance(this, data, numColors);
				startActivity(intent);
			}
		}
	}

}
