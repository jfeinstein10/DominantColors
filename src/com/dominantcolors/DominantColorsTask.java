package com.dominantcolors;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class DominantColorsTask extends AsyncTask<Bitmap, Void, int[]> {

	private ColorsListener mListener;
	private int mNumColors;

	public interface ColorsListener {
		public void onPreExecute();
		public void onPostExecute(int[] colors);
	}

	public class SimpleColorsListener implements ColorsListener {
		public void onPreExecute() { }
		public void onPostExecute(int[] colors) { }
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
	protected int[] doInBackground(Bitmap... bitmap) {
		if (mNumColors > 0)
			return DominantColors.getDominantColors(bitmap[0], mNumColors);
		else 
			return DominantColors.getDominantColors(bitmap[0]);
	}

	@Override
	protected void onPostExecute(int[] colors) {
		if (mListener != null)
			mListener.onPostExecute(colors);
	}

}
