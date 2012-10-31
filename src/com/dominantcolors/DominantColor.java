package com.dominantcolors;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class DominantColor implements Parcelable {

	public int color;
	public float percentage;

	public DominantColor(int color, float percentage) {
		this.color = color;
		this.percentage = percentage;
	}

	public DominantColor(Bundle bndl) {
		color = bndl.getInt("color");
		percentage = bndl.getFloat("percentage");
	}

	public static final Parcelable.Creator<DominantColor> CREATOR = new Parcelable.Creator<DominantColor>() {
		public DominantColor createFromParcel(Parcel in) {
			return new DominantColor(in.readBundle());
		}
		public DominantColor[] newArray(int size) {
			return new DominantColor[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Bundle bndl = new Bundle();
		bndl.putInt("color", color);
		bndl.putFloat("percentage", percentage);
		dest.writeBundle(bndl);
	}

}
