package com.dominantcolors;

import java.io.File;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.URLConnectionImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class DominantColorsApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.bitmapConfig(Bitmap.Config.RGB_565)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.resetViewBeforeLoading()
		.cacheInMemory()
		.cacheOnDisc()
		.build();
		
		File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
		int mem = ((ActivityManager)getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
		.memoryCacheExtraOptions(400, 400)
		.threadPriority(Thread.NORM_PRIORITY - 1)
		.threadPoolSize(4)
		// 6 Mb
		.memoryCache(new WeakMemoryCache())
		// 30 days
		.discCache(new LimitedAgeDiscCache(cacheDir, 30 * 24 * 60 * 60))
		.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
		// 5s connection timeout, 10s read timeout
		.imageDownloader(new URLConnectionImageDownloader(5 * 1000, 10 * 1000)) 
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.denyCacheImageMultipleSizesInMemory()
		.defaultDisplayImageOptions(options)
		.build();
				
		// init and reset on Application start
		ImageLoader.getInstance().init(config);
	}

}
