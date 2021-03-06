package com.tj.androidmultiphotopicker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tj.androidmultiphotopicker.application.PhotoPickerApplication;
import com.tj.library.imgdisplayconfig.PhotoPickerImageDisplayConfig;
import com.tj.library.model.ImageThumbnails;
import com.tj.library.model.ScreenSizeInfo;
import com.tj.library.model.StartPhotoPickerNeedParams;
import com.tj.library.thumbnailsloader.ImageThumbnailsBuilderWithSingleThread;
import com.tj.library.utils.DeviceInfoUtil;
import com.tj.library.utils.FileInfoUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

/**
 *  PhotoPickerEntry
 *
 */
public class PhotoPickerEntryActivity extends TopBarBaseActivity {
	
	private static final int START_CHOOSE_IMG_FROM_CAMERA = 1000;
	
	private LinearLayout imgPickerContainer;
	private int lineImgCount = 3;
	private ArrayList<ImageThumbnails> selectedImgList = new ArrayList<>();
	
	private int screenWidth;
	private int pickerImgPadding;
	private int pickerImgHeight;
	private int imgPickerContainerHeight;
	
	private DisplayImageOptions pickerImgDisplayOption = PhotoPickerImageDisplayConfig.generateDisplayImageOptionsNoCatchDisk();
	
	//专为拍照返回图片显示用
	private DisplayImageOptions pickerImgDisplayNoCatchOption = PhotoPickerImageDisplayConfig.generateDisplayImageOptionsNoCatch();
	
	private StartPhotoPickerNeedParams startPhotoPickerNeedParams;
	
	private String takePhotoFileName;
	private ScreenSizeInfo screenSizeInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		screenSizeInfo = new ScreenSizeInfo(DeviceInfoUtil.getScreenWidth(getApplicationContext()), DeviceInfoUtil.getScreenHeight(getApplicationContext()));

		initImgPickerContainer();
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_photo_picker_entry;
	}
	
	@Override
	protected String getViewTitle() {
		return getString(R.string.photo_picker_albunm_title);
	}
	
	private void initImgPickerContainer(){
		
		screenWidth = DeviceInfoUtil.getScreenWidth(getApplicationContext());
		pickerImgPadding = getResources().getDimensionPixelSize(R.dimen.entry_picker_img_padding);
		pickerImgHeight = (screenWidth - 6*pickerImgPadding)/lineImgCount;
		imgPickerContainerHeight = pickerImgHeight + 2*pickerImgPadding;// imgheight + top&bottom padding
		
		imgPickerContainer = (LinearLayout)findViewById(R.id.entry_img_picker_container);
		
		RelativeLayout.LayoutParams pickerContainerLp = (RelativeLayout.LayoutParams)imgPickerContainer.getLayoutParams();
		if(pickerContainerLp != null){
			pickerContainerLp.height = imgPickerContainerHeight;
			pickerContainerLp.width = screenWidth;
			imgPickerContainer.setLayoutParams(pickerContainerLp);
		}else{
			pickerContainerLp = new RelativeLayout.LayoutParams(screenWidth,imgPickerContainerHeight);
			pickerContainerLp.addRule(RelativeLayout.BELOW, R.id.top_bar);
			pickerContainerLp.topMargin = pickerImgPadding;
			imgPickerContainer.setLayoutParams(pickerContainerLp);
		}
		
		imgPickerContainer.setBackgroundResource(R.drawable.photo_picker_add_img_bg);
		
		updateImgPickerContainer();
	}
	
	private void updateImgPickerContainer(){
		
		imgPickerContainer.removeAllViews();
		
		int pickedCount = selectedImgList.size();
		if(pickedCount > lineImgCount){
			pickedCount = lineImgCount;
		}
		
		for (int i = 0; i < pickedCount; i++) {
			imgPickerContainer.addView(createPickerImageView(i));
		}
		
		if(pickedCount < lineImgCount){
			imgPickerContainer.addView(createAddIndicatorImageView(pickedCount));
		}
	}
	
	private ImageView createPickerImageView(final int index){
		
		ImageView pickerImgView = createCommonImageView(index);
		
		pickerImgView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				ImageThumbnails imgThumbnails = selectedImgList.get(index);
				ImageThumbnailsBuilderWithSingleThread.getInstance().geneateImageSmallThumbnailsFile(imgThumbnails, screenSizeInfo, getApplicationContext());

				Intent entryIntent = new Intent();
				entryIntent.setClass(getApplicationContext(), SelectedImageListConfirmActivitiy.class);
				entryIntent.putExtra(SelectedImageListConfirmActivitiy.EXTRA_SHOW_IMG_INDEX, index);
				entryIntent.putExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS, selectedImgList);
				startActivity(entryIntent);
			}
		});
		
		ImageThumbnails imgThumbnails = selectedImgList.get(index);
		
		if(FileInfoUtil.isFileExit(imgThumbnails.getSmallImgPath())){
			ImageLoader.getInstance().displayImage("file://" + imgThumbnails.getSmallImgPath(), pickerImgView, pickerImgDisplayOption);
		}else{
			ImageLoader.getInstance().displayImage("file://" + imgThumbnails.getSourceImgPath(), pickerImgView, pickerImgDisplayNoCatchOption);
		}
		return pickerImgView;
	}
	
	private ImageView createAddIndicatorImageView(int index){
		
		ImageView addView = createCommonImageView(index);
		addView.setImageResource(R.drawable.photo_picker_publish_show_order_imgs);
		
		addView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showChooseImgFrom();
			}
		});
		
		return addView;
	}
	
	private ImageView createCommonImageView(int index){
		
		ImageView imageView = new ImageView(getApplicationContext());
		
		LinearLayout.LayoutParams imgViewLayout = new LinearLayout.LayoutParams(pickerImgHeight,pickerImgHeight);
		if(index > 0){
			imgViewLayout.leftMargin = pickerImgPadding;
		}
		
		imageView.setScaleType(ScaleType.CENTER_CROP);
		imageView.setLayoutParams(imgViewLayout);
		return imageView;
	}
	
	private void initStartPhotoPickerParams(){
		
		startPhotoPickerNeedParams = new StartPhotoPickerNeedParams();
		startPhotoPickerNeedParams.setMaxSelectCount(lineImgCount);
		
		String pickerPhotoCacheSavePath = PhotoPickerApplication.getInstance().getLocalStorageUtil().getImageCacheAbsoluteDir();
		
		if(!pickerPhotoCacheSavePath.endsWith("/")){
			pickerPhotoCacheSavePath += File.separator;
		}
		
		startPhotoPickerNeedParams.setPickerPhotoCacheSavePath(pickerPhotoCacheSavePath);
	}

	//接收来自相册的选中图片list
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		updateDataFromIntent(intent);
	}
	
	private void updateDataFromIntent(Intent data){
		
		if(data != null){
			
	        if (data.hasExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS)) {
	        	ArrayList<ImageThumbnails> intentSelectedImgList = data.getParcelableArrayListExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS);
	        	
	        	Iterator<ImageThumbnails> iterator = intentSelectedImgList.iterator();
	        	while(iterator.hasNext()){
	        		ImageThumbnails imgThumb = iterator.next();
	        		if(!imgThumb.isSelected()){
	        			iterator.remove();
	        		}
	        	}
	        	
	        	if(intentSelectedImgList != null){
	        		selectedImgList.clear();
	        		selectedImgList.addAll(intentSelectedImgList);
	        		
	        		updateImgPickerContainer();
	        	}
	        }
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode == Activity.RESULT_OK){
			
			if(requestCode == START_CHOOSE_IMG_FROM_CAMERA){
				
				if(startPhotoPickerNeedParams != null){
					
					String cachePath = startPhotoPickerNeedParams.getPickerPhotoCacheSavePath();
					
					if(!TextUtils.isEmpty(cachePath) && !TextUtils.isEmpty(takePhotoFileName)){
						
						ImageThumbnails imgThumbnails = new ImageThumbnails(cachePath, cachePath + File.separator + takePhotoFileName);
						imgThumbnails.setSelected(true);
						selectedImgList.add(imgThumbnails);
						
						if(!FileInfoUtil.isFileExit(imgThumbnails.getBigImgPath())){
							ImageThumbnailsBuilderWithSingleThread.getInstance().geneateImageBigThumbnailsFile(imgThumbnails, screenSizeInfo, getApplicationContext());
						}
						
		        		updateImgPickerContainer();
					}
				}else{
					Toast.makeText(getApplicationContext(), R.string.photo_picker_take_photo_from_camera_failure, Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
	private void showChooseImgFrom(){
		
		final Dialog selectDialog = new Dialog(PhotoPickerEntryActivity.this);
		selectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		selectDialog.setContentView(R.layout.photo_picker_select_dialog);
		
		View fromLib = selectDialog.findViewById(R.id.from_library);
		fromLib.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startChooseImgFromAlbums();
				selectDialog.dismiss();
			}
		});
		
		View takePhoto = selectDialog.findViewById(R.id.take_photo);
		takePhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startChooseImgFromCamera();
				selectDialog.dismiss();
			}
		});
		
		selectDialog.show();
		
	}
	
	private void startChooseImgFromAlbums(){
		
		if(startPhotoPickerNeedParams == null){
			initStartPhotoPickerParams();
		}
		
		Intent entryIntent = new Intent();
		entryIntent.setClass(getApplicationContext(), ImageGroupActivity.class);
		
		entryIntent.putExtra(ImageListActivity.START_PHOTO_PICKER_NEED_PARAMS, startPhotoPickerNeedParams);
		entryIntent.putExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS, selectedImgList);
		startActivity(entryIntent);
	}
	
	private void startChooseImgFromCamera(){
		
		if(startPhotoPickerNeedParams == null){
			initStartPhotoPickerParams();
		}
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
		takePhotoFileName = new DateFormat().format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA)) + ".jpg";	
		intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(new File(startPhotoPickerNeedParams.getPickerPhotoCacheSavePath()+takePhotoFileName)));
        startActivityForResult(intent, START_CHOOSE_IMG_FROM_CAMERA);  
	}
	
}
