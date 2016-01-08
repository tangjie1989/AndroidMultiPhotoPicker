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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tj.library.model.ImageThumbnails;

import java.util.ArrayList;

/**
 *
 * 当前列表顶部的checkbox选中状态根据selectedImgList对应的item.isSelected进行判断
 * 
 * 此界面当用户点击选中和取消选中时，直接对selectedImgList进行对应item.setSelected(true/false)操作。和ImageListActivity不一样
 * 
 * 此界面的数据接受者有两个
 * 1.ImageListActivity
 * 2.PhotoPickerEntryActivity
 * 这两个界面接到数据时，都需要对selectedImgList进行预处理(即根据item的select状态进行真正的remove操作)
 *
 */

public class SelectedImageListActivitiy extends FragmentActivity implements
		SelectedImageShowFragment.SelectedImageShowFragmentDelegate {

	private ViewPager imgListViewPager;
	private PhotoListPagerAdapter imgListPagerAdapter;
	
	private ArrayList<ImageThumbnails> selectedImgList;
	
	private int currViewPagerIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.photo_picker_selected_image_list);
		
		if (getIntent().hasExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS)) {
        	selectedImgList = getIntent().getParcelableArrayListExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS);
        }
		
		currViewPagerIndex = selectedImgList.size() - 1;
		
		initMenu();
		
		initPhotoListViewPager();
		
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
		resultIntent.putParcelableArrayListExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS, selectedImgList);
		setResult(RESULT_OK, resultIntent);
		finish();
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
		
		updateBottomActionBtn();

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
	
	//------------------- menu
	
	private RelativeLayout topMenu;
	private RelativeLayout topMenuBackBtn;
	private TextView topMenuTitle;
	private CheckBox topMenuOperation;
	
	private RelativeLayout bottomMenu;
	private Button bottomMenuConfirmBtn;
	
	private void initMenu(){
		
		topMenu = (RelativeLayout)findViewById(R.id.scan_selected_image_thumbnial_top_menu);
		topMenuBackBtn = (RelativeLayout)findViewById(R.id.scan_selected_image_thumbnial_back_btn);
		topMenuTitle = (TextView)findViewById(R.id.scan_selected_image_thumbnial_title);
		topMenuOperation = (CheckBox)findViewById(R.id.scan_selected_image_thumbnial_operation);
		
		topMenuBackBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				back();
			}
		});
		
		topMenuOperation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageThumbnails imgThumb = getCurrentShowVo();
				boolean isSelect = !imgThumb.isSelected();
				imgThumb.setSelected(isSelect);
				updateTopMenuOperationStatu(isSelect);
				
			}
		});
		
		bottomMenu = (RelativeLayout)findViewById(R.id.scan_selected_image_thumbnial_bottom_menu);
		bottomMenuConfirmBtn = (Button)findViewById(R.id.scan_selected_image_thumbnial_confirm_btn);
		
		bottomMenuConfirmBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent publishOrderIntent = new Intent();
				publishOrderIntent.setClass(getApplicationContext(), PhotoPickerEntryActivity.class);
				publishOrderIntent.putExtra(ImageListActivity.EXTRA_SELECTED_IMAGES_DATAS, selectedImgList);
				publishOrderIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(publishOrderIntent);
			}
		});
		
	}
	
	private void hideOrShowMenu(){
		
		if(topMenu.getVisibility() == View.VISIBLE){
			topMenu.setVisibility(View.GONE);
			bottomMenu.setVisibility(View.GONE);
		}else{
			topMenu.setVisibility(View.VISIBLE);
			bottomMenu.setVisibility(View.VISIBLE);
		}
		
	}
	
	private void updateTopMenuTitle(){
		
		if(selectedImgList.size() == 1){
			topMenuTitle.setText("预览");
		}else{
			topMenuTitle.setText((currViewPagerIndex + 1) + "/" + selectedImgList.size());
		}
		
		ImageThumbnails imgThumb = getCurrentShowVo();
		updateTopMenuOperationStatu(imgThumb.isSelected());
		
	}
	
	private void updateTopMenuOperationStatu(boolean isSelected){
		topMenuOperation.setChecked(isSelected);
		updateBottomActionBtn();
	}
	
	private void updateBottomActionBtn(){
		
		int selectCount = 0;
		
		for(ImageThumbnails imgThumb : selectedImgList){
			if(imgThumb.isSelected()){
				selectCount++;
			}
		}
		
  		if(selectCount > 0){
  			bottomMenuConfirmBtn.setEnabled(true);
  			bottomMenuConfirmBtn.setText("确定 (" + selectCount + ")");
  		}else{
  			bottomMenuConfirmBtn.setEnabled(false);
  			bottomMenuConfirmBtn.setText("确定");
  		}
  		
  	}

	@Override
	public void operationMenu() {
		hideOrShowMenu();
	}
	
}
