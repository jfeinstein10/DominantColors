package com.dominantcolors;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class DominantColorsMainActivity extends Activity {

	public static final int PICKER = 1;
	
	private EditText mNumColors;

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
					startActivityForResult(
							Intent.createChooser(i, "Load an Image"), PICKER);
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
				if (pickedUri != null) {
					int numColors = Integer.valueOf(mNumColors.getText().toString());
					Intent intent = DominantColorsResultActivity.newInstance(this, pickedUri, numColors);
					startActivity(intent);
				}
			}
		}
	}

}
