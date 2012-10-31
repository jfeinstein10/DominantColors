package com.dominantcolors;

import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ColourLoversSearchTask extends AsyncTask<Integer, Bitmap, Void> {

	public static final String PATTERNS = "http://www.colourlovers.com/api/patterns";
	private Handler mHandler;

	public ColourLoversSearchTask(Handler handler) {
		mHandler = handler;
	}

	@Override
	protected Void doInBackground(Integer... color) {
		if (mHandler == null) return null;
		try {
			HttpClient client = new DefaultHttpClient();			
			HttpGet get = new HttpGet(PATTERNS + "?format=json&hex=" + Integer.toHexString(color[0]).substring(2));
			HttpResponse response = client.execute(get);
			String s = EntityUtils.toString(response.getEntity());
			Log.v(PATTERNS, s);
			JSONArray patterns = new JSONArray(s);
			for (int i = 0; i < patterns.length(); i++) {
				JSONObject pattern = patterns.getJSONObject(i);
				String imageUrl = pattern.getString("imageUrl");
				Bitmap b = BitmapFactory.decodeStream(new URL(imageUrl).openStream());
				publishProgress(b);
//				url, imageUrl, badgeUrl, apiUrl

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Bitmap... bitmap) {
		Message msg = new Message();
		msg.obj = bitmap[0];
		mHandler.sendMessage(msg);
	}

}
