package com.tj.androidmultiphotopicker.application;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;

public class PhotoPickerApplication extends Application {
	
	private static PhotoPickerApplication instance;
	
	private LocalStorageUtil photoPickerLocalStorageUtil;
	
	public LocalStorageUtil getLocalStorageUtil() {
		return photoPickerLocalStorageUtil;
	}

	public static PhotoPickerApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		
		super.onCreate();
		
		instance = this;
		
		photoPickerLocalStorageUtil = new LocalStorageUtil();
		photoPickerLocalStorageUtil.initLocalDir(this);

		initImageLoader(getApplicationContext());
	}
	
	public void initImageLoader(Context context) {
		
		File cacheDir = new File(photoPickerLocalStorageUtil.getImageCacheAbsoluteDir());
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.diskCache(new UnlimitedDiskCache(cacheDir))
//				.writeDebugLogs() // Remove for release app
				.build();
		ImageLoader.getInstance().init(config);
	}
	

}
