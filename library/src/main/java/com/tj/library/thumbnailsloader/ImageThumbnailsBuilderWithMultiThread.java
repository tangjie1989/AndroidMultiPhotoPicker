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
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Deprecated
public class ImageThumbnailsBuilderWithMultiThread {
	
	private static ExecutorService threadPool;// 线程池
	
	private static ImageThumbnailsBuilderWithMultiThread instance;
	
	private final Map<Context, List<ImageThumbnailsBuildTaskHandle>> requestMap;
	
	public static ImageThumbnailsBuilderWithMultiThread getInstance() {
		if (instance == null) {
			instance = new ImageThumbnailsBuilderWithMultiThread();
		}
		return instance;
	}
	
	private ImageThumbnailsBuilderWithMultiThread() {
		
		requestMap = new WeakHashMap<>();
//		threadPool = Executors.newFixedThreadPool(3, createThreadFactory(Thread.NORM_PRIORITY, "uil-pool-d-"));
//		threadPool = Executors.newCachedThreadPool(createThreadFactory(Thread.MIN_PRIORITY, "uil-pool-d-"));
		threadPool = Executors.newCachedThreadPool();
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
                requestList = new LinkedList();
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
	
	private static ThreadFactory createThreadFactory(int threadPriority, String threadNamePrefix) {
		return new DefaultThreadFactory(threadPriority, threadNamePrefix);
	}
	
	private static class DefaultThreadFactory implements ThreadFactory {

		private static final AtomicInteger poolNumber = new AtomicInteger(1);

		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;
		private final int threadPriority;

		DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
			this.threadPriority = threadPriority;
			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
			if (t.isDaemon()) t.setDaemon(false);
			t.setPriority(threadPriority);
			return t;
		}
	}
}
