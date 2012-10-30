package com.dominantcolors;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DominantColorsMainActivity extends Activity {

	public static final int PICKER = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		Button loadImage = (Button) findViewById(R.id.main_button);
		loadImage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.setType("image/*");
				startActivityForResult(
						Intent.createChooser(i, "Load an Image"), PICKER);
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == PICKER) {
				Uri pickedUri = data.getData();
				if (pickedUri != null) {
					Intent intent = DominantColorsResultActivity.newInstance(this, pickedUri);
					startActivity(intent);
				}
			}
		}
	}

}
