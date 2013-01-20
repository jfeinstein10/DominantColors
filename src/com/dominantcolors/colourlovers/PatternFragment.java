package com.dominantcolors.colourlovers;

import android.graphics.Bitmap;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dominantcolors.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class PatternFragment extends Fragment {

	private ImageLoader mImageCache = ImageLoader.getInstance();
	private Pattern mPattern;
	
	private ViewGroup mBackground;
	private TextView mTextView;
	private TextView mTitle;
	private LinearLayout mColorHolder;
	
	public static PatternFragment newInstance(Pattern pattern) {
		PatternFragment pf = new PatternFragment();
		Bundle args = new Bundle();
		args.putParcelable("mPattern", pattern);
		pf.setArguments(args);
		return pf;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mPattern = getArguments().getParcelable("mPattern");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mBackground = (ViewGroup) inflater.inflate(R.layout.pattern, null);
		mTextView = (TextView) mBackground.findViewById(R.id.pattern_text);
		mTitle = (TextView) mBackground.findViewById(R.id.pattern_title);
		mColorHolder = (LinearLayout) mBackground.findViewById(R.id.pattern_color_holder);
		if (mPattern != null) {
			setPattern(mPattern);
		} else {
			mTextView.setVisibility(View.VISIBLE);
		}
		return mBackground;
	}
	
	public void setPattern(Pattern pattern) {
		mPattern = pattern;
		// remove the placeholder text
		mTextView.setVisibility(View.GONE);
		// set the background
		mImageCache.loadImage(getActivity(), pattern.getImageUrl(),
				new ImageLoadingListener() {
					@Override
					public void onLoadingComplete(Bitmap arg0) {
						BitmapDrawable d = new BitmapDrawable(getResources(), arg0);
						d.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
						mBackground.setBackgroundDrawable(d);
					}
					@Override
					public void onLoadingCancelled() { }
					@Override
					public void onLoadingFailed(FailReason arg0) { }
					@Override
					public void onLoadingStarted() { }			
		});
		// set the title
		mTitle.setText(mPattern.getTitle());
		// add the pattern colors
		int[] colors = mPattern.getColors();
		for (int c : colors) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
			params.weight = 1;			
			ImageView iv = new ImageView(getActivity());
			iv.setBackgroundColor(c);
			mColorHolder.addView(iv, params);
		}
	}
	
}
