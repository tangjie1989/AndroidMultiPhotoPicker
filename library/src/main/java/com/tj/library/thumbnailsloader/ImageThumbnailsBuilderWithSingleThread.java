package com.tj.library.thumbnailsloader;

import android.content.Context;

import com.tj.library.model.ImageThumbnails;
import com.tj.library.model.ScreenSizeInfo;
import com.tj.library.utils.FileInfoUtil;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageThumbnailsBuilderWithSingleThread {
	
	private static ExecutorService threadPool;// 线程池
	
	private volatile static ImageThumbnailsBuilderWithSingleThread instance;
	
	private final Map<Context, List<ImageThumbnailsBuildTaskHandle>> requestMap;
	
	public static ImageThumbnailsBuilderWithSingleThread getInstance() {
		if(instance == null){
			synchronized (ImageThumbnailsBuilderWithSingleThread.class) {
				if (instance == null) {
					instance = new ImageThumbnailsBuilderWithSingleThread();
				}
			}
		}
		return instance;
	}
	
	private ImageThumbnailsBuilderWithSingleThread() {
		
		requestMap = new WeakHashMap<>();
		threadPool = Executors.newSingleThreadExecutor();
	}
	
	public void geneateImageSmallThumbnailsFile(ImageThumbnails imgThumbnails, ScreenSizeInfo screenSizeInfo, Context context){
		
		if(FileInfoUtil.isFileExit(imgThumbnails.getSmallImgPath())){
			return;
		}
		
		ImageThumbnailsBuildTask imageThumbnailsBuildTask = new ImageThumbnailsBuildTask(imgThumbnails, screenSizeInfo, true);
		
		submitTask(context, imageThumbnailsBuildTask);
		
	}
	
	public void geneateImageBigThumbnailsFile(ImageThumbnails imgThumbnails, ScreenSizeInfo screenSizeInfo, Context context){
		if(FileInfoUtil.isFileExit(imgThumbnails.getBigImgPath())){
			return;
		}
		
		ImageThumbnailsBuildTask imageThumbnailsBuildTask = new ImageThumbnailsBuildTask(imgThumbnails, screenSizeInfo, false);
		
		submitTask(context, imageThumbnailsBuildTask);
	}
	
	private void submitTask(Context context, ImageThumbnailsBuildTask imageThumbnailsBuildTask){
		
		threadPool.submit(imageThumbnailsBuildTask);
		
        ImageThumbnailsBuildTaskHandle requestHandle = new ImageThumbnailsBuildTaskHandle(imageThumbnailsBuildTask);

        if (context != null) {
            // Add request to request map
            List<ImageThumbnailsBuildTaskHandle> requestList = requestMap.get(context);
            if (requestList == null) {
                requestList = new LinkedList<>();
                requestMap.put(context, requestList);
            }

            requestList.add(requestHandle);

            Iterator<ImageThumbnailsBuildTaskHandle> iterator = requestList.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().shouldBeGarbageCollected()) {
                    iterator.remove();
                }
            }
        }
		
	}
	
}
