package com.dominantcolors;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dominantcolors.DominantColorsTask.ColorsListener;

public class DominantColorsResultActivity extends Activity implements ColorsListener {

	private ImageView mImageView;
	private LinearLayout mColorHolder;

	public static Intent newInstance(Context context, Uri uri, int numColors) {
		Intent intent = new Intent(context, DominantColorsResultActivity.class);
		intent.putExtra("uri", uri);
		intent.putExtra("numColors", numColors);
		return intent;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.result);
		mImageView = (ImageView) findViewById(R.id.result_image);
		mColorHolder = (LinearLayout) findViewById(R.id.result_color_holder);

		Bundle extras = getIntent().getExtras();		
		if (extras == null)
			finish();

		Uri uri = (Uri) extras.get("uri");
		int numColors = extras.getInt("numColors");
		try {
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
			bitmap = DominantColors.resizeToFitInSquare(bitmap, 500);
			if (mImageView != null)
				mImageView.setImageBitmap(bitmap);
			new DominantColorsTask(this, numColors).execute(bitmap);
		} catch (IOException e) {
			finish();
		}
	}

	@Override
	public void onPreExecute() {
		if (mColorHolder != null)
			mColorHolder.removeAllViews();
	}

	@Override
	public void onPostExecute(int[] colors) {
		if (mColorHolder != null) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
			params.weight = 1;
			for (int color : colors) {
				ImageView iv = new ImageView(DominantColorsResultActivity.this);
				iv.setBackgroundColor(color);
				mColorHolder.addView(iv, params);
			}
		}
	}

}
