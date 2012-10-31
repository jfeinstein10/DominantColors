package com.dominantcolors;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dominantcolors.DominantColorsTask.ColorsListener;
import com.dominantcolors.EtsyColorSearchTask.ColorSearchListener;

public class DominantColorsResultActivity extends Activity implements ColorsListener {

	private ImageView mImageView;
	private TextView mCurrentColor;
	private LinearLayout mColorHolder;

	private DominantColor[] mColors;

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
		mCurrentColor = (TextView) findViewById(R.id.result_color);
		mColorHolder = (LinearLayout) findViewById(R.id.result_color_holder);

		if (savedInstanceState != null) {
			mColors = (DominantColor[]) savedInstanceState.getParcelableArray("mColors");
			onPostExecute(mColors);
		}

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
			if (mColors == null)
				new DominantColorsTask(this, numColors).execute(bitmap);
		} catch (IOException e) {
			finish();
		}
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
					public void onClick(View v) {
						getWindow().setBackgroundDrawable(new ColorDrawable(color.color));
						mCurrentColor.setText(Integer.toHexString(color.color).substring(2));
						int avg = (Color.red(color.color) + Color.green(color.color) + Color.blue(color.color))/3;
						mCurrentColor.setTextColor((avg > 128) ? Color.BLACK : Color.WHITE);
						Handler handler = new Handler() {
							@Override
							public void handleMessage(Message msg) {
								if (msg.obj instanceof Bitmap) {
									BitmapDrawable d = new BitmapDrawable(getResources(), (Bitmap)msg.obj);
									d.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
									getWindow().setBackgroundDrawable(d);
								}
							}
						};
						new ColourLoversSearchTask(handler).execute(color.color);
					}
				});
				mColorHolder.addView(iv, params);
			}
		}
	}

}
