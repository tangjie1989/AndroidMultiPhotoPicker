package com.tj.library.thumbnailsloader;

import java.lang.ref.WeakReference;

public class ImageThumbnailsBuildTaskHandle {
	
	private final WeakReference<ImageThumbnailsBuildTask> request;

    public ImageThumbnailsBuildTaskHandle(ImageThumbnailsBuildTask request) {
        this.request = new WeakReference(request);
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        ImageThumbnailsBuildTask _request = request.get();
        return _request == null || _request.cancel(mayInterruptIfRunning);
    }

    public boolean isFinished() {
        ImageThumbnailsBuildTask _request = request.get();
        return _request == null || _request.isDone();
    }

    public boolean isCancelled() {
        ImageThumbnailsBuildTask _request = request.get();
        return _request == null || _request.isCancelled();
    }

    public boolean shouldBeGarbageCollected() {
        boolean should = isCancelled() || isFinished();
        if (should)
            request.clear();
        return should;
    }
}
