package com.tj.library.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.tj.library.imgdisplayconfig.PhotoPickerImageDisplayConfig;
import com.tj.library.model.ImageThumbnails;
import com.tj.library.model.ScreenSizeInfo;

public class GenerateImageThumbnailsFileUtil {

	private ImageThumbnails imgThumbnails;
	private ScreenSizeInfo screenSizeInfo;
	
	private boolean isCancelOperation = false;
	
	public void cancelOperation(){
		isCancelOperation = true;
	}
	
	private boolean isOperationCancelled(){
		return isCancelOperation;
	}
	
	public GenerateImageThumbnailsFileUtil(ImageThumbnails imgThumbnails, ScreenSizeInfo screenSizeInfo) {
		super();
		this.imgThumbnails = imgThumbnails;
		this.screenSizeInfo = screenSizeInfo;
	}

	/**
	 * 生成系统相册图片的缩略图大图并保存至sdcard
	 */
	public void generateDevicePhotoBigThumbnailsToLocalSDCard(){
		
		if(FileInfo.isFileExit(imgThumbnails.getBigImgPath())){
			return;
		}
		
		saveScaleImageToLocal(imgThumbnails.getBigImgPath(), false);
	}
	
	/**
	 * 生成系统相册图片的缩略图小图并保存至sdcard
	 */
	public void generateDevicePhotoSmallThumbnailsToLocalSDCard(){
		
		if(FileInfo.isFileExit(imgThumbnails.getSmallImgPath())){
			return;
		}
		
		saveScaleImageToLocal(imgThumbnails.getSmallImgPath(), true);
	}
	
	private void saveScaleImageToLocal(String savePath, boolean isSmall){
		
		ImageSize targetSize;
		if(isSmall){
			targetSize = new ImageSize(screenSizeInfo.getWidth()/4, screenSizeInfo.getHeight()/4);
		}else{
			targetSize = new ImageSize((int)(screenSizeInfo.getWidth()/2), (int)(screenSizeInfo.getHeight()/2));
		}
		
		if(isOperationCancelled()){
			return;
		}
		
		Bitmap showImg = ImageLoader.getInstance().loadImageSync("file://"+ imgThumbnails.getSourceImgPath(), targetSize, PhotoPickerImageDisplayConfig.generateDisplayImageOptionsNoCatch());
		
		if(showImg != null && !showImg.isRecycled()){
			
			//生成压缩后的小图
			saveBitmapToLocalFile(savePath, showImg, 90, true);
		}
	}
	
	//保存本地
	private void saveBitmapToLocalFile(String filePath , Bitmap sourceBitmap , int compressDegree , boolean isSourceBitmapRecycle){
		
		FileOutputStream stream = null;
		
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
		
		try {
			stream = new FileOutputStream(file);
			
			if(isOperationCancelled()){
				return;
			}
			
			if(sourceBitmap != null && !sourceBitmap.isRecycled()){
				
				if(filePath.endsWith(".png")){
					sourceBitmap.compress(CompressFormat.PNG, compressDegree, stream);//大小压缩
				}else{
					sourceBitmap.compress(CompressFormat.JPEG, compressDegree, stream);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stream != null) {
					stream.flush();
					stream.close();
				}
				
				if(isSourceBitmapRecycle && sourceBitmap != null && !sourceBitmap.isRecycled()){
					sourceBitmap.recycle();
					sourceBitmap = null;
				}
			} catch (IOException e2) {
			}
		}
		
	}
	
}
