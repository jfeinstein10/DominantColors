package com.dominantcolors;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class DominantColorsMainActivity extends Activity {

	public static final int PICKER = 1;

	private EditText mNumColors;
	private Uri mPhotoUri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		mNumColors = (EditText) findViewById(R.id.main_numcolors);

		Button loadImage = (Button) findViewById(R.id.main_button);
		loadImage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					int numColors = Integer.valueOf(mNumColors.getText().toString());
					Intent i = new Intent(Intent.ACTION_GET_CONTENT);
					i.setType("image/*");
					//					startActivityForResult(Intent.createChooser(i, "Load an Image"), PICKER);

					Intent pickIntent = new Intent();
					pickIntent.setType("image/*");
					pickIntent.setAction(Intent.ACTION_GET_CONTENT);

					Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
					Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
					chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, 
							new Intent[] {takePhotoIntent});

					ContentValues values = new ContentValues();
					values.put(Media.TITLE, "image");
					mPhotoUri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
					chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);

					startActivityForResult(chooserIntent, PICKER);
				} catch (NumberFormatException e) {
					// number was not valid
				}
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == PICKER) {
				Uri pickedUri = data.getData();
				if (pickedUri == null) {
					pickedUri = mPhotoUri;
				}
				int numColors = Integer.valueOf(mNumColors.getText().toString());
				Intent intent = DominantColorsResultActivity.newInstance(this, pickedUri, numColors);
				startActivity(intent);
			}
		}
	}

}
