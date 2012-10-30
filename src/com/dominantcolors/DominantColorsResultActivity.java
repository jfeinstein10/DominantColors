package com.dominantcolors;

import java.net.URI;

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
	
	public static Intent newInstance(Context context, Uri uri) {
		Intent intent = new Intent(context, DominantColorsResultActivity.class);
		intent.putExtra("uri", uri);
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
		try {
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
			bitmap = Bitmap.createScaledBitmap(bitmap, 480, 320, false);
			mImageView.setImageBitmap(bitmap);
			new DominantColorsTask(this).execute(bitmap);
		} catch (Exception e) {	}
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
