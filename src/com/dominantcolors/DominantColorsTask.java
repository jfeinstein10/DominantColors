package com.dominantcolors;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class DominantColorsTask extends AsyncTask<Bitmap, Void, int[]> {
	
	private ColorsListener mListener;
	
	public interface ColorsListener {
		public void onPreExecute();
		public void onPostExecute(int[] colors);
	}
	
	public class SimpleColorsListener implements ColorsListener {
		public void onPreExecute() { }
		public void onPostExecute(int[] colors) { }
	}
	
	public DominantColorsTask(ColorsListener listener) {
		mListener = listener;
	}
	
	@Override
	protected void onPreExecute() {
		if (mListener != null)
			mListener.onPreExecute();
	}
	
	@Override
	protected int[] doInBackground(Bitmap... bitmap) {
		return DominantColor.getDominantColors(bitmap[0], 5);
	}

	@Override
	protected void onPostExecute(int[] colors) {
		if (mListener != null)
			mListener.onPostExecute(colors);
	}
	
}
