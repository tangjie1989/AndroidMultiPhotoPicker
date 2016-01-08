package com.tj.library.imgloader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;

import com.tj.library.imgloadlistener.ImageLoadResultListener;
import com.tj.library.model.ImageGroup;
import com.tj.library.model.ImageThumbnails;

import java.util.ArrayList;

public class ImageLoadTask extends BaseTask {

    /**
     * 上下文对象
     */
    private Context mContext = null;
    
    /**
     * 缩略图sdcard存储路径
     */
    private String sdcardCacheSavePath;

    /**
     * 存放图片<文件夹,该文件夹下的图片列表>键值对
     */
    private ArrayList<ImageGroup> mGruopList = new ArrayList<ImageGroup>();

    public ImageLoadTask(Context context) {
        super();
        mContext = context;
        result = mGruopList;
    }

    public ImageLoadTask(Context context, String sdcardCacheSavePath, ImageLoadResultListener listener) {
        super();
        mContext = context;
        this.sdcardCacheSavePath = sdcardCacheSavePath;
        result = mGruopList;
        setOnResultListener(listener);
    }
    
    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Boolean doInBackground(Void... params) {
    	
        Uri mImageUri = Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = mContext.getContentResolver();
        Cursor mCursor = null;
        try {
        	
            // 初始化游标
            mCursor = mContentResolver.query(mImageUri, null, null, null, Media.DATE_TAKEN + " DESC");
            // 遍历结果
            while (mCursor.moveToNext()) {
                // 获取图片的路径
                String path = mCursor.getString(mCursor.getColumnIndex(Media.DATA));
                String parentName = mCursor.getString(mCursor.getColumnIndex(Media.BUCKET_DISPLAY_NAME));

//                // 获取该图片的所在文件夹的路径
//                File file = new File(path);
//                String parentName = "";
//                if (file.getParentFile() != null) {
//                    parentName = file.getParentFile().getName();
//                } else {
//                    parentName = file.getName();
//                }
                // 构建一个imageGroup对象
                ImageGroup item = new ImageGroup();
                // 设置imageGroup的文件夹名称
                item.setDirName(parentName);
                
                ImageThumbnails imgThumbnails = new ImageThumbnails(sdcardCacheSavePath, path);

                // 寻找该imageGroup是否是其所在的文件夹中的第一张图片
                int searchIdx = mGruopList.indexOf(item);
                if (searchIdx >= 0) {
                    // 如果是，该组的图片数量+1
                    ImageGroup imageGroup = mGruopList.get(searchIdx);
                    imageGroup.addImage(imgThumbnails);
                } else {
                    // 否则，将该对象加入到groupList中
                    item.addImage(imgThumbnails);
                    mGruopList.add(item);
                }
            }
        } catch (Exception e) {
            // 输出日志
            return false;
        } finally {
            // 关闭游标
            if (mCursor != null && !mCursor.isClosed()) {
                mCursor.close();
            }
        }
        return true;
    }
}
