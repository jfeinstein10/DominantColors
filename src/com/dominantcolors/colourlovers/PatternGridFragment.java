package com.dominantcolors.colourlovers;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.dominantcolors.R;
import com.dominantcolors.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class PatternGridFragment extends Fragment implements OnItemClickListener {

	private ImageLoader mImageLoader = ImageLoader.getInstance();

	private GridView mGridView;

	private int mColor;
	private PatternAdapter mAdapter;
	private DisplayImageOptions mOptions;

	public static PatternGridFragment newInstance(int color) {
		PatternGridFragment cllf = new PatternGridFragment();
		Bundle args = new Bundle();
		args.putInt("mColor", color);
		cllf.setArguments(args);
		return cllf;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mOptions = new DisplayImageOptions.Builder()
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new RoundedBitmapDisplayer(30))
		.build();
		if (getArguments() != null) {
			mColor = getArguments().getInt("mColor");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.pattern_list, null);
		int numColumns = getResources().getInteger(R.integer.num_cols);
		mGridView = (GridView) v.findViewById(android.R.id.list);
		mGridView.setOnItemClickListener(this);
		mGridView.setNumColumns(numColumns);
		mAdapter = new PatternAdapter(getActivity(), numColumns);
		mGridView.setAdapter(mAdapter);

		v.setBackgroundColor(mColor);
		TextView empty = (TextView) v.findViewById(android.R.id.empty);
		empty.setTextColor(Util.getTextColorForBackground(mColor));

		if (savedInstanceState != null) {
			ArrayList<Pattern> patterns = savedInstanceState.getParcelableArrayList("mAdapter");
			if (patterns == null || patterns.isEmpty()) {
				getPatterns();
			} else {
				setPatterns(patterns);
			}
		} else {
			getPatterns();
		}
		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		ArrayList<Pattern> patterns = new ArrayList<Pattern>();
		for (int i = 0; i < mAdapter.getCount(); i++)
			patterns.add(mAdapter.getItem(i));
		outState.putParcelableArrayList("mAdapter", patterns);
	}

	private void getPatterns() {
		Handler handler = new Handler() {
			@Override
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void handleMessage(Message msg) {
				if (msg.obj instanceof ArrayList) {
					setPatterns((ArrayList)msg.obj);
				}
			}
		};
		new ColourLoversSearchTask(handler).execute(mColor);
	}

	private void setPatterns(ArrayList<Pattern> patterns) {
		if (getView() != null) {
			View empty = getView().findViewById(android.R.id.empty);
			if (patterns == null || patterns.isEmpty()) {
				empty.setVisibility(View.VISIBLE);
			} else {
				empty.setVisibility(View.GONE);
			}
		}
		mAdapter.addAll(patterns);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (getActivity() == null)
			return;
		ColourLoversActivity activity = (ColourLoversActivity) getActivity();
		activity.onPatternPressed(mAdapter.getItem(position));
	}

	private class PatternAdapter extends ArrayAdapter<Pattern> {

		private class ViewHolder {
			public ViewGroup pattern;
			public TextView title;
			public View padding;
			public String currUrl;
		}

		private int mNumCols;
		private int mPadding;

		public PatternAdapter(Context context, int numCols) {
			super(context, -1);
			mNumCols = numCols;
			mPadding = (int) getResources().getDimension(R.dimen.padding);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.pattern_row, null);
				ViewHolder temp = new ViewHolder();
				temp.pattern = (ViewGroup) convertView.findViewById(R.id.pattern_row_image);
				temp.title = (TextView) convertView.findViewById(R.id.patter_row_title);
				temp.padding = convertView.findViewById(R.id.pattern_row_padding);
				convertView.setTag(temp);
			}

			ViewHolder holder = (ViewHolder) convertView.getTag();

			if (position < mNumCols) {
				holder.padding.setPadding(0, mPadding, 0, 0);
			} else if (getCount() - position < mNumCols) {
				holder.padding.setPadding(0, 0, 0, mPadding);
			} else {
				holder.padding.setPadding(0, 0, 0, 0);
			}

			Pattern pattern = getItem(position);
			holder.pattern.setBackgroundColor(Color.TRANSPARENT);
			holder.title.setText(pattern.getTitle());
			holder.currUrl = pattern.getImageUrl();

			mImageLoader.loadImage(getActivity(), pattern.getImageUrl(), mOptions, new SimpleImageListener(convertView, pattern.getImageUrl()));

			return convertView;
		}

		private class SimpleImageListener implements ImageLoadingListener {
			private View view;
			private String url;
			public SimpleImageListener(View view, String url) {
				this.view = view;
				this.url = url;
			}
			@Override
			public void onLoadingCancelled() { }
			@Override
			public void onLoadingComplete(Bitmap arg0) {
				BitmapDrawable d = new BitmapDrawable(getResources(), arg0);
				d.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
				ViewHolder holder = (ViewHolder) view.getTag();
				if (url != null && url.equals(holder.currUrl))
					holder.pattern.setBackgroundDrawable(d);
			}
			@Override
			public void onLoadingFailed(FailReason arg0) { }
			@Override
			public void onLoadingStarted() { }	
		}

	}

}