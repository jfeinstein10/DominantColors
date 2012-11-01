package com.dominantcolors.colourlovers;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
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

public class ColourLoversGridFragment extends Fragment implements OnItemClickListener {

	private ImageLoader mImageLoader = ImageLoader.getInstance();

	private GridView mGridView;

	private int mColor;
	private PatternAdapter mAdapter;
	private DisplayImageOptions mOptions;

	public static ColourLoversGridFragment newInstance(int color) {
		ColourLoversGridFragment cllf = new ColourLoversGridFragment();
		Bundle args = new Bundle();
		args.putInt("mColor", color);
		cllf.setArguments(args);
		return cllf;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mColor = getArguments().getInt("mColor");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.pattern_list, null);
		mGridView = (GridView) v.findViewById(android.R.id.list);
		v.setBackgroundColor(mColor);
		TextView empty = (TextView) v.findViewById(android.R.id.empty);
		empty.setTextColor(Util.getTextColorForBackground(mColor));
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mOptions = new DisplayImageOptions.Builder()
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new RoundedBitmapDisplayer(30))
		.build();

		mAdapter = new PatternAdapter(getActivity());
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);

		// do the actual search
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
		// TODO Auto-generated method stub

	}

	private class PatternAdapter extends ArrayAdapter<Pattern> {

		private class ViewHolder {
			public ViewGroup pattern;
			public TextView title;
			public String currUrl;
		}

		public PatternAdapter(Context context) {
			super(context, -1);
		}

		@TargetApi(11)
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.pattern_row, null);
				ViewHolder temp = new ViewHolder();
				temp.pattern = (ViewGroup) convertView.findViewById(R.id.pattern_row_image);
				temp.title = (TextView) convertView.findViewById(R.id.patter_row_title);
				convertView.setTag(temp);
			}
			
			ViewHolder holder = (ViewHolder) convertView.getTag();

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