package com.tj.androidmultiphotopicker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tj.library.model.ImageThumbnails;

import java.util.ArrayList;

/**
 *
 * 当前列表顶部的checkbox选中状态根据selectedImgList对应的item.isSelected进行判断
 * 
 * 此界面当用户点击选中和取消选中时，直接对selectedImgList进行对应item.setSelected(true/false)操作。和ImageListActivity不一样
 * 
 * 此界面的数据接收者
 * 1.PhotoPickerEntryActivity
 * 这两个界面接到数据时，都需要对selectedImgList进行预处理(即根据item的select状态进行真正的remove操作)
 *
 */

public class SelectedImageListConfirmActivitiy extends FragmentActivity implements
		SelectedImageShowFragment.SelectedImageShowFragmentDelegate {
	
	public static final String EXTRA_SHOW_IMG_INDEX = "extra_show_img_index";

	private ViewPager imgListViewPager;
	private PhotoListPagerAdapter imgListPagerAdapter;
	
	private ArrayList<ImageThumbnails> selectedImgList;
	
	private int currViewPagerIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.photo_picker_selected_image_list_confirm);
		
		initIntentData();
		
		initMenu();
		
		initPhotoListViewPager();
		
	}
	
	private void initIntentData(){
		
		Intent data = getIntent();
		if(data != null){
			if (data.hasExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS)) {
	        	selectedImgList = data.getParcelableArrayListExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS);
	        }
			if (data.hasExtra(EXTRA_SHOW_IMG_INDEX)) {
				currViewPagerIndex = data.getIntExtra(EXTRA_SHOW_IMG_INDEX, 0);
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
		
		Intent publishOrderIntent = new Intent();
		publishOrderIntent.setClass(getApplicationContext(), PhotoPickerEntryActivity.class);
		publishOrderIntent.putExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS, selectedImgList);
		publishOrderIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(publishOrderIntent);
		
	}
	
	//初始化横向滑动viewpager
	private void initPhotoListViewPager() {

		imgListPagerAdapter = new PhotoListPagerAdapter(getSupportFragmentManager());
		
		imgListViewPager = (ViewPager) findViewById(R.id.pager);
		imgListViewPager.setAdapter(imgListPagerAdapter);
		
		imgListViewPager.setCurrentItem(currViewPagerIndex);
		
		imgListViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				int index = imgListViewPager.getCurrentItem();
				if(arg0 == 0 && currViewPagerIndex != index){ //如果滑动停止并且当前页不是上一页
					currViewPagerIndex = index;
					updateTopMenuTitle();
				}
			}
		});
		
		updateTopMenuTitle();

	}
	
	private class PhotoListPagerAdapter extends FragmentStatePagerAdapter {
		
		public PhotoListPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
	    public int getItemPosition(Object object){
	        return PagerAdapter.POSITION_NONE;
	    }
		
		@Override
		public Fragment getItem(int position) {
			Bundle args = new Bundle();
			args.putParcelable(SelectedImageShowFragment.EXTRA_SELECTED_IMAGE_THUMBNAILS, selectedImgList.get(position));
			args.putInt(SelectedImageShowFragment.EXTRA_SELECTED_IMAGE_POSITION, position);
			return SelectedImageShowFragment.newInstance(args);
		}

		@Override
		public int getCount() {
			return selectedImgList.size();
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

	}
	
	public ImageThumbnails getCurrentShowVo(){
		int index = imgListViewPager.getCurrentItem();
		return selectedImgList.get(index);
	}
	
	public int getCurrentShowPosition(){
		return imgListViewPager.getCurrentItem();
	}
	
	
	private TextView viewPagerScrollIndex;
	private CheckBox imgThumbnailsOperation;
	
	private void initMenu(){
		
		viewPagerScrollIndex = (TextView)findViewById(R.id.confirm_selected_image_thumbnial_title);
		imgThumbnailsOperation = (CheckBox)findViewById(R.id.confirm_selected_image_thumbnial_operation);
		
		imgThumbnailsOperation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageThumbnails imgThumb = getCurrentShowVo();
				boolean isSelect = !imgThumb.isSelected();
				imgThumb.setSelected(isSelect);
				updateTopMenuOperationStatu(isSelect);
				
			}
		});
		
	}
	
	private void updateTopMenuTitle(){
		
		if(selectedImgList.size() == 1){
			viewPagerScrollIndex.setText("预览");
		}else{
			viewPagerScrollIndex.setText((currViewPagerIndex + 1) + "/" + selectedImgList.size());
		}
		
		ImageThumbnails imgThumb = getCurrentShowVo();
		updateTopMenuOperationStatu(imgThumb.isSelected());
		
	}
	
	private void updateTopMenuOperationStatu(boolean isSelected){
		imgThumbnailsOperation.setChecked(isSelected);
	}
	
	@Override
	public void operationMenu() {
		back();
	}
	
}
