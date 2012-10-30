package com.dominantcolors;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class EtsyColorSearchTask extends AsyncTask<Void, Void, Bitmap[]> {

	private static final String API_KEY = "GET YOUR OWN KEY";
	private ColorSearchListener mListener;
	private int mColor;

	public interface ColorSearchListener {
		public void onFinished(Bitmap[] bitmaps);
	}
	
	public EtsyColorSearchTask(ColorSearchListener listener, int color) {
		mListener = listener;
		mColor = color;
	}
	
	@Override
	protected Bitmap[] doInBackground(Void... params) {
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			String url = "http://openapi.etsy.com/v2/listings/active?color=" 
					+ URLEncoder.encode(Integer.toHexString(mColor).substring(2), "utf-8") + "&color_accuracy=" + 2 + "&api_key=" + API_KEY;

			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			String json = reader.readLine();
			JSONObject obj = new JSONObject(json);
			JSONArray results = obj.getJSONArray("results");
			
			Bitmap[] bitmaps = new Bitmap[results.length()];

			for (int i = 0; i < results.length(); i++) {
				String id = results.getJSONObject(i).getString("listing_id");
				url = "http://openapi.etsy.com/v2/listings/" + URLEncoder.encode(id, "utf-8") + "/images?api_key=" + API_KEY;
				request.setURI(new URI(url));
				response = client.execute(request);
				reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				json = reader.readLine();
				obj = new JSONObject(json);
				
				// download image
				url = obj.getJSONArray("results").getJSONObject(0).getString("url_170x135");
				bitmaps[i] = BitmapFactory.decodeStream(new URL(url).openStream());
			}
			return bitmaps;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Bitmap[] bitmaps) {
		if (mListener != null)
			mListener.onFinished(bitmaps);
	}

}
