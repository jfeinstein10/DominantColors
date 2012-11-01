package com.dominantcolors.colourlovers;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Pattern implements Parcelable {

	private JSONObject mObject;
	
	public Pattern(JSONObject obj) {
		mObject = obj;
	}
	
	public Pattern(Parcel in) throws JSONException {
		mObject = new JSONObject(in.readString());
	}
	
	public String getImageUrl() {
		return getString("imageUrl");
	}
	
	public String getTitle() {
		return getString("title");
	}
	
	private String getString(String key) {
		try {
			return mObject.getString(key);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mObject.toString());
	}
	
	public static final Parcelable.Creator<Pattern> CREATOR = new Parcelable.Creator<Pattern>() {
		public Pattern createFromParcel(Parcel in) {
			try {
				return new Pattern(in);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		public Pattern[] newArray(int size) {
			return new Pattern[size];
		}
	};
}
