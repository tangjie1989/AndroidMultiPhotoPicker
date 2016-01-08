package com.tj.library.imgdisplayconfig;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class PhotoPickerImageDisplayConfig {

	public static DisplayImageOptions generateDisplayImageOptionsNoCatch() {
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
				.resetViewBeforeLoading(true).cacheInMemory(false)
				.cacheOnDisk(false).considerExifParams(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.build();

		return displayImageOptions;
	}
	
	public static DisplayImageOptions generateDisplayImageOptionsNoCatchDisk() {
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
				.resetViewBeforeLoading(true).cacheInMemory(true)
				.cacheOnDisk(false).considerExifParams(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.build();

		return displayImageOptions;
	}
	
}
