package com.dominantcolors;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dominantcolors.DominantColorsTask.ColorsListener;
import com.dominantcolors.colourlovers.ColourLoversActivity;
import com.dominantcolors.util.Util;

public class DominantColorsResultActivity extends FragmentActivity implements ColorsListener {

	private ImageView mImageView;
	private TextView mCurrentColor;
	private LinearLayout mColorHolder;

	private DominantColor[] mColors;

	public static Intent newInstance(Context context, Intent data, int numColors) {
		data.setClass(context, DominantColorsResultActivity.class);
		data.putExtra("numColors", numColors);
		return data;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.result);
		mImageView = (ImageView) findViewById(R.id.result_image);
		mCurrentColor = (TextView) findViewById(R.id.result_color);
		mColorHolder = (LinearLayout) findViewById(R.id.result_color_holder);

		if (savedInstanceState != null) {
			mColors = (DominantColor[]) savedInstanceState.getParcelableArray("mColors");
			onPostExecute(mColors);
		}

		Bundle extras = getIntent().getExtras();		
		if (extras == null)
			finish();

		Uri uri = getIntent().getData();
		Bitmap bitmap = (Bitmap) getIntent().getExtras().get("data");
		int numColors = extras.getInt("numColors");
		if (bitmap == null) {
			try {
				bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
			} catch (IOException e) {
				finish();
				return;
			}
		}
		bitmap = DominantColors.resizeToFitInSquare(bitmap, 500);
		if (mImageView != null)
			mImageView.setImageBitmap(bitmap);
		if (mColors == null)
			new DominantColorsTask(this, numColors).execute(bitmap);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArray("mColors", mColors);
	}

	@Override
	public void onPreExecute() {
		if (mColorHolder != null)
			mColorHolder.removeAllViews();
	}

	@Override
	public void onPostExecute(DominantColor[] colors) {
		mColors = colors;
		if (mColorHolder != null) {
			for (final DominantColor color : colors) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
				params.weight = color.percentage;

				ImageView iv = new ImageView(DominantColorsResultActivity.this);
				iv.setBackgroundColor(color.color);
				iv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						getWindow().setBackgroundDrawable(new ColorDrawable(color.color));
						mCurrentColor.setText(Integer.toHexString(color.color).substring(2));
						mCurrentColor.setTextColor(Util.getTextColorForBackground(color.color));
					}
				});
				iv.setLongClickable(true);
				iv.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						final Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
						vibrator.vibrate(50);
						startActivity(ColourLoversActivity.newInstance(DominantColorsResultActivity.this, color.color));
						return true;
					}
				});
				mColorHolder.addView(iv, params);
			}
		}
	}

}
