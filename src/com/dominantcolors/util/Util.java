package com.dominantcolors.util;

import android.graphics.Color;

public class Util {

	public static int getTextColorForBackground(int background) {
		int avg = (Color.red(background) + Color.green(background) + Color.blue(background))/3;
		return ((avg > 128) ? Color.BLACK : Color.WHITE);
	}
	
}
