package com.dominantcolors;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dominantcolors.DominantColorsTask.ColorsListener;


public class DominantColorsLiveActivity extends Activity implements SurfaceHolder.Callback,
Camera.PreviewCallback, ColorsListener {

	private Camera mCamera;
	private LinearLayout mColorHolder;
	private boolean isComputing = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.live);

		mColorHolder = (LinearLayout) findViewById(R.id.live_color_holder);
		SurfaceView surfaceView = (SurfaceView)findViewById(R.id.live_surface);
		surfaceView.getHolder().addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
		if (mCamera != null){
			try {
				mCamera.setPreviewDisplay(holder);
				mCamera.setPreviewCallback(this);
				mCamera.startPreview();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		synchronized (this) {
			if (!isComputing) {
				isComputing = true;
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				new DominantColorsTask(this).execute(bitmap);
			}
		}
	}

	@Override
	public void onPreExecute() {
		// TODO do nothing
	}

	@Override
	public void onPostExecute(DominantColor[] colors) {
		// TODO Auto-generated method stub
		for (final DominantColor color : colors) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
			params.weight = color.percentage;
			ImageView iv = new ImageView(this);
			iv.setBackgroundColor(color.color);
			if (mColorHolder != null)
				mColorHolder.addView(iv, params);
		}
		synchronized (this) {
			isComputing = false;
		}
	}

}
