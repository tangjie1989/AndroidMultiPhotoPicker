package com.tj.androidmultiphotopicker;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tj.library.imgdisplayconfig.PhotoPickerImageDisplayConfig;
import com.tj.library.model.ImageThumbnails;
import com.tj.library.model.ScreenSizeInfo;
import com.tj.library.model.StartPhotoPickerNeedParams;
import com.tj.library.thumbnailsloader.ImageThumbnailsBuilder2;
import com.tj.library.utils.FileInfo;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 *  当前相册下的图片选中状态根据selectedImgList来判断
 *  
 *  这个界面selectedImgList有两个来源
 *  1.from ImageGroupActivity 如果来自1，则不需要对selectedImgList进行预处理
 *  2.from SelectedImageListActivity 如果来自2，则在onActivityResult需要对selectedImgList进行预处理(即根据item的select状态进行真正的remove操作)
 *  
 *  此界面当用户点击选中和取消选中时，直接对selectedImgList进行add/remove操作。和SelectedImageListActivity不一样
 *
 */

public class ImageListActivity extends Activity implements OnItemClickListener {
	
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_IMAGES_DATAS = "extra_images";
    public static final String EXTRA_SELECTED_IMAGES_DATAS = "extra_selected_images";
    public static final String EXTRA_MAX_SELECTED_COUNT = "extra_max_selected_count";
    public static final String START_PHOTO_PICKER_NEED_PARAMS = "start_photo_need_params";
    
    private static final int SCAN_SELECTED_IMAGE_LIST = 10001;

    private LinearLayout viewContainer;
    private ListView imgListView;
    private ImageAdapter imgAdapter;
    
    private RelativeLayout imgListBackBtn;
    private TextView imgListTitle;
//    private Button imgListCancelBtn;
    
    private Button scanSelectedThumbnailsImageBtn;
    private Button confirmSelectedThumbnailsImageBtn;
    
    private ArrayList<ImageThumbnails> imgList = new ArrayList<ImageThumbnails>();
    private ArrayList<ImageThumbnails> selectedImgList;
    private String curAlbumsName;
    
    private StartPhotoPickerNeedParams startPhotoPickerNeedParams;
    
    private ScreenSizeInfo screenSizeInfo;
    private int USER_PROFILE_IMG_PADDING;
	private int USER_PROFILE_IMG_SIZE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_picker_image_thumbnails_list);
		
		initSizeInfo();
		
		initIntentData();
		
		initView();
		
        updateBottomActionBtn();
		
	}
	
	private void initSizeInfo(){
		Resources re = getResources();
        DisplayMetrics metrics = re.getDisplayMetrics();
        int widths = metrics.widthPixels;
        
        screenSizeInfo = new ScreenSizeInfo(metrics.widthPixels, metrics.heightPixels);
		
		USER_PROFILE_IMG_PADDING = re.getDimensionPixelSize(R.dimen.photo_picker_img_padding);
		USER_PROFILE_IMG_SIZE = (widths - USER_PROFILE_IMG_PADDING*4)/3;
	}
	
	private void initIntentData(){
		
		Intent data = getIntent();
		if(data != null){
			curAlbumsName = data.getStringExtra(EXTRA_TITLE);
	        if (!TextUtils.isEmpty(curAlbumsName)) {
	            setTitle(curAlbumsName);
	        }
	        
	        if (data.hasExtra(EXTRA_IMAGES_DATAS)) {
	        	imgList = data.getParcelableArrayListExtra(EXTRA_IMAGES_DATAS);
	        }
	        
	        if (data.hasExtra(EXTRA_SELECTED_IMAGES_DATAS)) {
	        	selectedImgList = data.getParcelableArrayListExtra(EXTRA_SELECTED_IMAGES_DATAS);
	        	if(selectedImgList == null){
	        		selectedImgList = new ArrayList<ImageThumbnails>();
	        	}
	        }
	        
	        if(data.hasExtra(START_PHOTO_PICKER_NEED_PARAMS)){
	        	startPhotoPickerNeedParams = data.getParcelableExtra(ImageListActivity.START_PHOTO_PICKER_NEED_PARAMS);
	        }
	        
		}
		
	}
	
	private void initView(){
		
		viewContainer = (LinearLayout)findViewById(R.id.image_list_container);
		
		imgListBackBtn = (RelativeLayout)findViewById(R.id.image_list_back_btn);
		imgListTitle = (TextView)findViewById(R.id.image_list_title);
//		imgListCancelBtn = (Button)findViewById(R.id.image_list_cancel_btn);
		
		imgListBackBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				back();
			}
		});
		
		imgListTitle.setText(curAlbumsName);
		
//		imgListCancelBtn.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent publishOrderIntent = new Intent();
//				publishOrderIntent.setClass(getApplicationContext(), PhotoPickerEntryActivity.class);
//				publishOrderIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(publishOrderIntent);
//			}
//		});
		
		scanSelectedThumbnailsImageBtn = (Button)findViewById(R.id.scan_selected_thumbnails_image);
		confirmSelectedThumbnailsImageBtn = (Button)findViewById(R.id.confirm_selected_thumbnails_image);
		
		scanSelectedThumbnailsImageBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent scanIntent = new Intent(getApplicationContext(), SelectedImageListActivitiy.class);
				scanIntent.putParcelableArrayListExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS, selectedImgList);
				startActivityForResult(scanIntent, SCAN_SELECTED_IMAGE_LIST);
			}
		});
		
		confirmSelectedThumbnailsImageBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent publishOrderIntent = new Intent();
				publishOrderIntent.setClass(getApplicationContext(), PhotoPickerEntryActivity.class);
				publishOrderIntent.putExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS, selectedImgList);
				publishOrderIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(publishOrderIntent);
			}
		});
		
		initListView();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK && requestCode == SCAN_SELECTED_IMAGE_LIST){
			
	        if (data.hasExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS)) {
	        	
	        	selectedImgList = data.getParcelableArrayListExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS);
	        	
	        	//SelectedImageListActivitiy 删除和选中只是状态值变化，但是此列表是根据add和remove判断显示，所以要预处理下selectedImgList
	        	Iterator<ImageThumbnails> iterator = selectedImgList.iterator();
	        	while(iterator.hasNext()){
	        		ImageThumbnails imgThumb = iterator.next();
	        		if(!imgThumb.isSelected()){
	        			iterator.remove();
	        		}
	        	}
	        	
	        	updateBottomActionBtn();
	        	
	        	imgAdapter.notifyDataSetChanged();
	        	
	        }
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			back();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void back(){
		Intent resultIntent = new Intent();
		resultIntent.putExtra(ImageListActivity.EXTRA_TITLE, curAlbumsName);
		resultIntent.putParcelableArrayListExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS, selectedImgList);
		setResult(RESULT_OK, resultIntent);
		finish();
	}
	
    private void initListView(){
    	
    	imgListView = new ListView(this);
    	imgAdapter = new ImageAdapter();
    	
    	imgListView.setDividerHeight(0);
    	
    	imgListView.setOnItemClickListener(this);
    	imgListView.setAdapter(imgAdapter);
    	
    	LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    	viewContainer.removeAllViews();
    	viewContainer.addView(imgListView, lp);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
    	
    }
    
    private int column = 3;//每行显示图片数
    
    private class ImageAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if(imgList == null || imgList.size() == 0){
				return 0;
			}
			return imgList.size() % column == 0 ? imgList.size() / 3 : (imgList.size() /3 + 1);
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
		public boolean isEnabled(int position) {
			return false;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(convertView == null){
				LinearLayout userPhotoLayout = new LinearLayout(getApplicationContext());
				userPhotoLayout.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));

				for (int i = 0; i < column; i++) {
					userPhotoLayout.addView(createImageCell());
				}
				
				convertView = userPhotoLayout;
			}
			
			LinearLayout userPhotoLayout = (LinearLayout) convertView;
			
			for (int i = 0; i < column; i++) {
				userPhotoLayout.getChildAt(i).setVisibility(View.GONE);
			}

			int cstart = position*column;
			int cend = (position+1) * column;
			
			cend = cend > imgList.size() ? imgList.size() : cend;
			
			for (int i = cstart; i < cend; i++) {
				ImageThumbnails imgThumbnails = imgList.get(i);
				setImageCell(imgThumbnails, (FrameLayout)userPhotoLayout.getChildAt((i % column)), i);
			}
			
			return convertView;
		}
    	
    }
    
  	private FrameLayout createImageCell() {
  		
  		FrameLayout imgThumbnailsContainer = new FrameLayout(this);
//  		imgThumbnailsContainer.setOrientation(LinearLayout.VERTICAL);
  		
  		ImageView imageView = new ImageView(this);
  		imageView.setScaleType(ScaleType.CENTER_CROP);
  		
  		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(USER_PROFILE_IMG_SIZE,USER_PROFILE_IMG_SIZE);
  		lp.leftMargin = USER_PROFILE_IMG_PADDING;
  		lp.topMargin = USER_PROFILE_IMG_PADDING;
  		imgThumbnailsContainer.addView(imageView,lp);
  		
  		ImageView imageViewCover = new ImageView(this);
  		imageViewCover.setScaleType(ScaleType.CENTER_INSIDE);
  		imageViewCover.setImageResource(R.drawable.photo_picker_image_thumbnails_cover);
  		imageViewCover.setVisibility(View.GONE);
  		imgThumbnailsContainer.addView(imageViewCover,lp);
  		
  		LayoutParams clp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
  		imgThumbnailsContainer.setLayoutParams(clp);
  		
  		return imgThumbnailsContainer;
  		
  	}
  	
  	private void setImageCell(final ImageThumbnails imgThumbnails, FrameLayout cell,final int i) {

  		final ImageView imageView = (ImageView)cell.getChildAt(0);
  		
  		ImageThumbnailsBuilder2.getInstance().geneateImageSmallThumbnailsFile(imgThumbnails, screenSizeInfo, getApplicationContext());
		
		if(FileInfo.isFileExit(imgThumbnails.getSmallImgPath())){
			ImageLoader.getInstance().displayImage("file://" + imgThumbnails.getSmallImgPath(), imageView, PhotoPickerImageDisplayConfig.generateDisplayImageOptionsNoCatchDisk());
		}else{
			ImageLoader.getInstance().displayImage("file://" + imgThumbnails.getSourceImgPath(), imageView, PhotoPickerImageDisplayConfig.generateDisplayImageOptionsNoCatch());
		}
		
		final ImageView coverImgView = (ImageView)cell.getChildAt(1);
		hideOrShowImageCoverView(imgThumbnails, coverImgView);
  		
  		imageView.setOnClickListener(new View.OnClickListener() {
  			@Override
  			public void onClick(View v) {
  				
  				if(getSelectedImageCount() == startPhotoPickerNeedParams.getMaxSelectCount() && !imgThumbnails.isSelected()){
  					Toast.makeText(getApplicationContext(), "最多选择" + startPhotoPickerNeedParams.getMaxSelectCount() + "张", Toast.LENGTH_SHORT).show();
  					return;
  				}
  				
  				updateSelectedImgList(imgThumbnails);
  				
  				hideOrShowImageCoverView(imgThumbnails, coverImgView);
  				
  				ImageThumbnailsBuilder2.getInstance().geneateImageBigThumbnailsFile(imgThumbnails, screenSizeInfo, getApplicationContext());
  				
  			}
  		});
  		cell.setVisibility(View.VISIBLE);
  	}
  	
  	private void hideOrShowImageCoverView(ImageThumbnails imgThumbnails, ImageView coverImgView){
  		
  		if(selectedImgList.contains(imgThumbnails)){
  			imgThumbnails.setSelected(true);
  			coverImgView.setVisibility(View.VISIBLE);
		}else{
			imgThumbnails.setSelected(false);
			coverImgView.setVisibility(View.GONE);
		}
  	}
    
  	private void updateSelectedImgList(ImageThumbnails imgThumbnails){
  		if(selectedImgList.contains(imgThumbnails)){
  			selectedImgList.remove(imgThumbnails);
		}else{
			selectedImgList.add(imgThumbnails);
		}
  		updateBottomActionBtn();
  	}
  	
  	private void updateBottomActionBtn(){
  		if(selectedImgList != null && getSelectedImageCount() > 0){
  			scanSelectedThumbnailsImageBtn.setEnabled(true);
  			confirmSelectedThumbnailsImageBtn.setEnabled(true);
  			confirmSelectedThumbnailsImageBtn.setText("确定 (" + getSelectedImageCount() + ")");
  		}else{
  			scanSelectedThumbnailsImageBtn.setEnabled(false);
  			confirmSelectedThumbnailsImageBtn.setEnabled(false);
  			confirmSelectedThumbnailsImageBtn.setText("确定");
  		}
  		
  	}
  	
  	private int getSelectedImageCount(){
  		return selectedImgList.size();
  	}
    
}
