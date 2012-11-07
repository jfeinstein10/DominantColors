package com.dominantcolors.colourlovers;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ColourLoversSearchTask extends AsyncTask<Integer, Bitmap, JSONArray> {

	public static final String PATTERNS = "http://www.colourlovers.com/api/patterns";
	private Handler mHandler;

	public ColourLoversSearchTask() { }

	public ColourLoversSearchTask(Handler handler) {
		mHandler = handler;
	}
	
	@Override
	protected JSONArray doInBackground(Integer... color) {
		if (mHandler == null) return null;
		try {
			HttpClient client = new DefaultHttpClient();			
			HttpGet get = new HttpGet(PATTERNS + "?format=json&hex=" + Integer.toHexString(color[0]).substring(2));
			HttpResponse response = client.execute(get);
			String s = EntityUtils.toString(response.getEntity());
			return new JSONArray(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(JSONArray array) {
		Message msg = new Message();
		ArrayList<Pattern> patterns = new ArrayList<Pattern>();
		try {
			for (int i = 0; i < array.length(); i++)
				patterns.add(new Pattern(array.getJSONObject(i)));
		} catch (JSONException e) { }
		msg.obj = patterns;
		mHandler.sendMessage(msg);
	}

}
