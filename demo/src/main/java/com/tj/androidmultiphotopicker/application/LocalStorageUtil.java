package com.tj.androidmultiphotopicker.application;

import android.content.Context;
import android.os.Environment;

import com.tj.library.utils.DeviceInfoUtil;

import java.io.File;

public class LocalStorageUtil {

	// SD卡文件根目录
	private static String BASE_DIR = "MultiPhotoPicker";
	
	// 缓存目录
	private static final String IMAGE_CACHE_DIR = "caches";

	// sd卡根目录
	private String sdcardCacheBaseAbsolutePath;

	private String imageCacheAbsoluteDir;// 图片缓存目录

	public String getSdcardCacheBaseAbsolutePath() {
		return sdcardCacheBaseAbsolutePath;
	}

	public void setSdcardCacheBaseAbsolutePath(String sdcardCacheBaseAbsolutePath) {
		this.sdcardCacheBaseAbsolutePath = sdcardCacheBaseAbsolutePath;
	}

	public String getImageCacheAbsoluteDir() {
		return imageCacheAbsoluteDir;
	}

	public void setImageCacheAbsoluteDir(String imageCacheAbsoluteDir) {
		this.imageCacheAbsoluteDir = imageCacheAbsoluteDir;
	}

	public String getAppDir(){
		return Environment.getExternalStorageDirectory() + File.separator + BASE_DIR;
	}
	
	public String getImageCacheDir(){
		return Environment.getExternalStorageDirectory() + File.separator + BASE_DIR + File.separator + IMAGE_CACHE_DIR;
	}

	public void initLocalDir(Context context) {

		long availableSDCardSpace = DeviceInfoUtil.getExternalStorageSpace();// 获取SD卡可用空间
		
		String sdcardBasePath;
		
		if (availableSDCardSpace != -1L) {// 如果存在SD卡
			sdcardBasePath = Environment.getExternalStorageDirectory() + File.separator + BASE_DIR;
		} else if (DeviceInfoUtil.getInternalStorageSpace() != -1L) {
			sdcardBasePath = context.getFilesDir().getPath() + File.separator + BASE_DIR;
		} else {// sd卡不存在
			// 没有可写入位置
			return;
		}
		
		setSdcardCacheBaseAbsolutePath(sdcardBasePath);
		
		// 图片缓存目录
		setImageCacheAbsoluteDir(getSdcardCacheBaseAbsolutePath() + File.separator + IMAGE_CACHE_DIR);

		// 初始化根目录
		File basePath = new File(getSdcardCacheBaseAbsolutePath());
		if (!basePath.exists()) {
			basePath.mkdir();
		}

	}
	
}
