package com.tj.library.thumbnailsloader;

import com.tj.library.model.ImageThumbnails;
import com.tj.library.model.ScreenSizeInfo;
import com.tj.library.utils.GenerateImageThumbnailsFileUtil;

public class ImageThumbnailsBuildTask implements Runnable{

	private GenerateImageThumbnailsFileUtil ilsUtil;
	private ImageThumbnails ImageThumbnails;
	private ScreenSizeInfo screenSizeInfo;
	private boolean isSmall;
	private boolean isCancelled = false;
	private boolean isFinished = false;
	
	public ImageThumbnailsBuildTask(ImageThumbnails ImageThumbnails,
			ScreenSizeInfo screenSizeInfo, boolean isSmall) {
		this.ImageThumbnails = ImageThumbnails;
		this.screenSizeInfo = screenSizeInfo;
		this.isSmall = isSmall;
	}
	
	
	@Override
	public void run() {
		
//		long start = System.currentTimeMillis();
		
		ilsUtil = new GenerateImageThumbnailsFileUtil(ImageThumbnails, screenSizeInfo);
		
		if(isSmall){
			ilsUtil.generateDevicePhotoSmallThumbnailsToLocalSDCard();
		}else{
			ilsUtil.generateDevicePhotoBigThumbnailsToLocalSDCard();
		}
		
		isFinished = true;
		
//		long end = System.currentTimeMillis();
		
//		System.out.println("\r\n cha : " + (end - start));
		
	}
	
	public boolean isCancelled() {
		return isCancelled;
	}

	public boolean isDone() {
		return isCancelled() || isFinished;
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		isCancelled = true;
		ilsUtil.cancelOperation();
		return isCancelled();
	}

}
