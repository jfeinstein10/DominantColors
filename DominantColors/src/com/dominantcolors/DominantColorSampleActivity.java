package com.dominantcolors;

import com.dominantcolor.dominantcolor.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * http://mobile.tutsplus.com/tutorials/android/android-sdk-displaying-images-with-an-enhanced-gallery/
 * 
 * @author jeremy
 *
 */
public class DominantColorSampleActivity extends Activity {

	public static final int PICKER = 1;

	private ImageView mImageView;
	private LinearLayout mColorHolder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		mImageView = (ImageView) findViewById(R.id.main_image);
		mColorHolder = (LinearLayout) findViewById(R.id.main_color_holder);

		Button loadImage = (Button) findViewById(R.id.main_button);
		loadImage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.setType("image/*");
				startActivityForResult(Intent.createChooser(i, "Load an Image"), PICKER);
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == PICKER) {
				Uri pickedUri = data.getData();
				if (pickedUri != null) {
					try {
						Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pickedUri);
						bitmap = Bitmap.createScaledBitmap(bitmap, 480, 320, false);
						if (mImageView != null)
							mImageView.setImageBitmap(bitmap);
						new DominantColorTask().execute(bitmap);
					} catch (Exception e) {	}
				}
			}
		}
	}

	private class DominantColorTask extends AsyncTask<Bitmap, Void, int[]> {

		@Override
		protected void onPreExecute() {
			if (mColorHolder != null)
				mColorHolder.removeAllViews();
		}
		
		@Override
		protected int[] doInBackground(Bitmap... bitmap) {
			return DominantColor.getDominantColors(bitmap[0], 5);
		}

		@Override
		protected void onPostExecute(int[] colors) {
			if (mColorHolder != null) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
				params.weight = 1;
				for (int color : colors) {
					ImageView iv = new ImageView(DominantColorSampleActivity.this);
					iv.setBackgroundColor(color);
					mColorHolder.addView(iv, params);
				}
			}
		}
	}


}
