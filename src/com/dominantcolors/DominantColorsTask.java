package com.dominantcolors;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class DominantColorsTask extends AsyncTask<Bitmap, Void, DominantColor[]> {

	private ColorsListener mListener;
	private int mNumColors;

	public interface ColorsListener {
		public void onPreExecute();
		public void onPostExecute(DominantColor[] colors);
	}

	public class SimpleColorsListener implements ColorsListener {
		public void onPreExecute() { }
		public void onPostExecute(DominantColor[] colors) { }
	}
	
	public DominantColorsTask(ColorsListener listener) {
		this(listener, -1);
	}

	public DominantColorsTask(ColorsListener listener, int numColors) {
		mListener = listener;
		mNumColors = numColors;
	}

	@Override
	protected void onPreExecute() {
		if (mListener != null)
			mListener.onPreExecute();
	}

	@Override
	protected DominantColor[] doInBackground(Bitmap... bitmap) {
//		return DominantColors.getMeanShift(bitmap[0], 40.0f);
		if (mNumColors > 0)
			return DominantColors.getDominantColors(bitmap[0], mNumColors);
		else 
			return DominantColors.getDominantColors(bitmap[0]);
	}

	@Override
	protected void onPostExecute(DominantColor[] colors) {
		if (mListener != null)
			mListener.onPostExecute(colors);
	}

}
