package com.dominantcolors.colourlovers;

import com.dominantcolors.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ColourLoversActivity extends FragmentActivity {

	private int mColor;

	public static Intent newInstance(Activity activity, int color) {
		Intent intent = new Intent(activity, ColourLoversActivity.class);
		intent.putExtra("mColor", color);
		return intent;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mColor = getIntent().getExtras().getInt("mColor");

		setContentView(R.layout.frame);

		getSupportFragmentManager()
		.beginTransaction()
		.add(R.id.frame, PatternGridFragment.newInstance(mColor))
		.commit();
	}

	public void onPatternPressed(Pattern pattern) {
		getSupportFragmentManager()
		.beginTransaction()
		.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
		.replace(R.id.frame, PatternFragment.newInstance(pattern))
		.addToBackStack(null)
		.commit();
	}

}
