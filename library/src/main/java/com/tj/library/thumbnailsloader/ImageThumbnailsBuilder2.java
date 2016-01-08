package com.tj.library.thumbnailsloader;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;

import com.tj.library.model.ImageThumbnails;
import com.tj.library.model.ScreenSizeInfo;
import com.tj.library.utils.FileInfo;

public class ImageThumbnailsBuilder2{
	
	private static ExecutorService threadPool;// 线程池
	
	private volatile static ImageThumbnailsBuilder2 instance;
	
	private final Map<Context, List<ImageThumbnailsBuildTaskHandle>> requestMap;
	
	public static ImageThumbnailsBuilder2 getInstance() {
		if(instance == null){
			synchronized (ImageThumbnailsBuilder2.class) {
				if (instance == null) {
					instance = new ImageThumbnailsBuilder2();
				}
			}
		}
		return instance;
	}
	
	private ImageThumbnailsBuilder2() {
		
		requestMap = new WeakHashMap<Context, List<ImageThumbnailsBuildTaskHandle>>();
		threadPool = Executors.newSingleThreadExecutor();
	}
	
	public void geneateImageSmallThumbnailsFile(ImageThumbnails imgThumbnails, ScreenSizeInfo screenSizeInfo, Context context){
		
		if(FileInfo.isFileExit(imgThumbnails.getSmallImgPath())){
			return;
		}
		
		ImageThumbnailsBuildTask imageThumbnailsBuildTask = new ImageThumbnailsBuildTask(imgThumbnails, screenSizeInfo, true);
		
		submitTask(context, imageThumbnailsBuildTask);
		
	}
	
	public void geneateImageBigThumbnailsFile(ImageThumbnails imgThumbnails, ScreenSizeInfo screenSizeInfo, Context context){
		if(FileInfo.isFileExit(imgThumbnails.getBigImgPath())){
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
                requestList = new LinkedList<ImageThumbnailsBuildTaskHandle>();
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
