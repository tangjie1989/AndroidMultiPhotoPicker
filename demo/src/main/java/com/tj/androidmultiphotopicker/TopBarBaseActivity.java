package com.tj.androidmultiphotopicker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 统一处理顶部导航栏
 * 
 * @author tangjie
 * 
 */
public class TopBarBaseActivity extends Activity {
	
	private ImageButton backBtn;
	
	protected TextView title;
	
	protected boolean isInitTopbar = true;//有些界面不带topbar

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());
		
		if(isInitTopbar){
			initTopBar();
		}
	}
	
	protected int getLayoutId(){
		return 0;
	}
	
	private void initTopBar() {
		
		backBtn = (ImageButton)findViewById(R.id.imageView_left);
		backBtn.setImageResource(R.drawable.back_default);
		
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		title = (TextView)findViewById(R.id.title);
		title.setText(getViewTitle());
	}
	
	protected String getViewTitle(){
		return "";
	}
	
	protected void NoTopBar(){
		isInitTopbar = false;
	}
	
	protected ImageButton getBackLayout() {
		return backBtn;
	}
}
