package com.tj.library.utils;

/**
 * 手机相册图片本地缩略图文件名生成器
 * 
 */

public class GenerateImageThumbnailsFileNameUtil {
	
	private static final String TAG_BIG = "big";
	private static final String TAG_SMALL = "small";
	
	public static String generateBigThumbnailsFileName(String sourceImgPath){
		if(sourceImgPath.endsWith(".png")){
			return generateImageThumbnailsFileName((sourceImgPath + TAG_BIG), ".png");
		}else{
			return generateImageThumbnailsFileName((sourceImgPath + TAG_BIG), ".jpg");
		}
	}
	
	public static String generateSmallThumbnailsFileName(String sourceImgPath){
		if(sourceImgPath.endsWith(".png")){
			return generateImageThumbnailsFileName((sourceImgPath + TAG_SMALL), ".png");
		}else{
			return generateImageThumbnailsFileName((sourceImgPath + TAG_SMALL), ".jpg");
		}
	}
	
	private static String generateImageThumbnailsFileName(String sourceName, String format){
		return Md5Encode.getMD5(sourceName) + format;
	}

}
