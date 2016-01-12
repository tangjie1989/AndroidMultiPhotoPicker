package com.tj.androidmultiphotopicker;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tj.library.imgdisplayconfig.PhotoPickerImageDisplayConfig;
import com.tj.library.imgloader.ImageLoadTask;
import com.tj.library.imgloadlistener.ImageLoadResultListener;
import com.tj.library.model.ImageGroup;
import com.tj.library.model.ImageThumbnails;
import com.tj.library.model.ScreenSizeInfo;
import com.tj.library.model.StartPhotoPickerNeedParams;
import com.tj.library.thumbnailsloader.ImageThumbnailsBuilder2;
import com.tj.library.utils.FileInfo;
import com.tj.library.utils.TaskUtil;

import java.util.ArrayList;

/**
 * 
 * 相册模块所有界面的选中状态全部根据selectedImgList来进行判断
 * 
 * selectedImgList在所有界面间传递
 *
 */

public class ImageGroupActivity extends TopBarBaseActivity implements
		OnItemClickListener {
	
    private ImageLoadTask mLoadTask = null;
    private ListView imgGroupListView;
    private ImageGroupAdapter imgGroupAdapter;
    
    private static final int LIST_ACTIVITY_REQUEST = 10000;
    private ArrayList<ImageGroup> imgGroupList = new ArrayList<>();
    private ArrayList<ImageThumbnails> selectedImgList;
    
    private LayoutInflater inflater;
    private LinearLayout viewContainer;
    
    private ScreenSizeInfo screenSizeInfo;
    
    private StartPhotoPickerNeedParams startPhotoPickerNeedParams;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		Intent startPickerIntent = getIntent();
		if(startPickerIntent != null){
			startPhotoPickerNeedParams = startPickerIntent.getParcelableExtra(ImageListActivity.START_PHOTO_PICKER_NEED_PARAMS);
			selectedImgList = startPickerIntent.getParcelableArrayListExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS);
			if(selectedImgList == null){
				selectedImgList = new ArrayList<>();
			}
		}else{
			finish();
		}
		
		overrideTopBarBackClick();
		
		inflater = LayoutInflater.from(this);
		viewContainer = (LinearLayout)findViewById(R.id.image_group_container);
		
		Resources re = getResources();
        DisplayMetrics metrics = re.getDisplayMetrics();
        
        screenSizeInfo = new ScreenSizeInfo(metrics.widthPixels, metrics.heightPixels);
		
		loadImages();
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.photo_picker_image_group_list;
	}
	
	@Override
	protected String getViewTitle() {
		return getString(R.string.photo_picker_albunm_selectpic_title);
	}
	
	private void overrideTopBarBackClick(){
		getBackLayout().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent entryIntent = new Intent();
				entryIntent.setClass(getApplicationContext(), PhotoPickerEntryActivity.class);
				entryIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(entryIntent);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK && requestCode == LIST_ACTIVITY_REQUEST){
	        if (data.hasExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS)) {
	        	selectedImgList = data.getParcelableArrayListExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS);
	        }
		}
	}
	
	 /**
     * 加载图片
     */
    private void loadImages() {
    	
//        mLoadingLayout.showLoading(true);
//        if (!SDcardUtil.hasExternalStorage()) {
//            mLoadingLayout.showEmpty(getString(R.string.donot_has_sdcard));
//            return;
//        }

        // 线程正在执行
        if (mLoadTask != null && mLoadTask.getStatus() == Status.RUNNING) {
            return;
        }

        mLoadTask = new ImageLoadTask(this, startPhotoPickerNeedParams.getPickerPhotoCacheSavePath(), new ImageLoadResultListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onResult(boolean success, String error, Object result) {
//                mLoadingLayout.showLoading(false);
                // 如果加载成功
                if (success && result != null && result instanceof ArrayList) {
                    imgGroupList.addAll((ArrayList<ImageGroup>)result);
                    initListView();
                } else {
                    // 加载失败，显示错误提示
//                    mLoadingLayout.showFailed(getString(R.string.loaded_fail));
                }
            }
        });
        TaskUtil.execute(mLoadTask);
    }
    
    private void initListView(){
    	
    	View photoListView = inflater.inflate(R.layout.common_listview, null);
    	imgGroupListView = (ListView)photoListView.findViewById(R.id.common_listview);
    	imgGroupAdapter = new ImageGroupAdapter();
    	
    	imgGroupListView.setOnItemClickListener(this);
    	imgGroupListView.setAdapter(imgGroupAdapter);
    	
    	viewContainer.removeAllViews();
    	viewContainer.addView(photoListView);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
    	
    	if(position < 0){
    		return;
    	}
    	
    	ImageGroup imgGroup = imgGroupList.get(position);
    	
    	ArrayList<ImageThumbnails> childList = imgGroup.getImages();
        Intent mIntent = new Intent(ImageGroupActivity.this, ImageListActivity.class);
        mIntent.putExtra(ImageListActivity.EXTRA_TITLE, imgGroup.getDirName());
        mIntent.putExtra(ImageListActivity.START_PHOTO_PICKER_NEED_PARAMS, startPhotoPickerNeedParams);
        mIntent.putParcelableArrayListExtra(ImageListActivity.EXTRA_IMAGES_DATAS, childList);
        mIntent.putParcelableArrayListExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS, selectedImgList);
        startActivityForResult(mIntent, LIST_ACTIVITY_REQUEST);
    	
    }
    
    private class ImageGroupAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return imgGroupList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(convertView == null){
				convertView = inflater.inflate(R.layout.photo_picker_image_group_list_item, null);
			}
			
			ImageGroup imgGroup = imgGroupList.get(position);
			
			ImageView groupImg = (ImageView)convertView.findViewById(R.id.image_group_first_img);
			TextView groupCount = (TextView)convertView.findViewById(R.id.image_group_count);
			
			groupCount.setText(imgGroup.getDirName() + " (" + imgGroup.getImageCount() + ")");
			
			ImageThumbnails imgThumbnails = imgGroup.getImages().get(0);
			
			ImageThumbnailsBuilder2.getInstance().geneateImageSmallThumbnailsFile(imgThumbnails, screenSizeInfo, getApplicationContext());
			
			if(FileInfo.isFileExit(imgThumbnails.getSmallImgPath())){
				ImageLoader.getInstance().displayImage("file://" + imgThumbnails.getSmallImgPath(), groupImg, PhotoPickerImageDisplayConfig.generateDisplayImageOptionsNoCatchDisk());
			}else{
				ImageLoader.getInstance().displayImage("file://" + imgGroup.getFirstImgPath(), groupImg, PhotoPickerImageDisplayConfig.generateDisplayImageOptionsNoCatch());
			}
			
			return convertView;
		}
    	
    }
    
    /**
     * 	ActivityManager manager = (ActivityManager)getApplication().getSystemService( Activity.ACTIVITY_SERVICE );
		RunningTaskInfo task = manager.getRunningTasks(10).get(0);
		System.out.println("\r\nImageGroupActivity taskId : " + task.id + " numActivities : " + task.numActivities);
     */
    
}
