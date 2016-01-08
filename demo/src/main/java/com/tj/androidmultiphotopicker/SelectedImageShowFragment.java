package com.tj.androidmultiphotopicker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tj.library.imgdisplayconfig.PhotoPickerImageDisplayConfig;
import com.tj.library.model.ImageThumbnails;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class SelectedImageShowFragment extends Fragment {
	
	public static final String EXTRA_SELECTED_IMAGE_THUMBNAILS = "extra_selected_image_thumbnails";
	public static final String EXTRA_SELECTED_IMAGE_POSITION = "extra_selected_image_position";
	
	public static SelectedImageShowFragment newInstance(Bundle args) {
		SelectedImageShowFragment myFragment = new SelectedImageShowFragment();
        myFragment.setArguments(args);
        return myFragment;
    }
	
	public interface SelectedImageShowFragmentDelegate{
		public void operationMenu();
	}
	
	private ImageThumbnails imgThumbnails;
	private PhotoView imageThumbnailsView;
	private ProgressBar loadingBar;
	
	private int position;//可以用来指定view的tag 方便viewpager在activity查找指定view
	private SelectedImageShowFragmentDelegate selectedImageShowFragmentDelegate;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	selectedImageShowFragmentDelegate = (SelectedImageShowFragmentDelegate) activity;
        } catch (ClassCastException e) {
        	e.printStackTrace();
        }
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getArguments();
		
		if(bundle != null){
			imgThumbnails = bundle.getParcelable(EXTRA_SELECTED_IMAGE_THUMBNAILS);
			position = bundle.getInt("position", -1);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		RelativeLayout viewContainer = (RelativeLayout)inflater.inflate(R.layout.photo_picker_selected_image_list_item,null);
		viewContainer.setTag("fragment"+position);
		
		imageThumbnailsView = (PhotoView)viewContainer.findViewById(R.id.selected_image);
		
		imageThumbnailsView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
			@Override
			public void onViewTap(View view, float x, float y) {
				selectedImageShowFragmentDelegate.operationMenu();
			}
		});
		
//		imageThumbnailsView.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				selectedImageShowFragmentDelegate.operationMenu();
//			}
//		});
		
		loadingBar = (ProgressBar)viewContainer.findViewById(R.id.selected_image_loading_bar);
		
		return viewContainer;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ImageLoader.getInstance().displayImage("file://" + imgThumbnails.getBigImgPath(), imageThumbnailsView, PhotoPickerImageDisplayConfig.generateDisplayImageOptionsNoCatch(), new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				loadingBar.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {
			}
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				loadingBar.setVisibility(View.GONE);
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
			}
		});
		
		
	}

}
