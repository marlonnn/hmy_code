package com.BC.entertainmentgravitation;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.BC.entertainment.cache.InfoCache;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.summer.activity.BaseActivity;
import com.umeng.analytics.MobclickAgent;

public class PesonalPortraitActivity extends BaseActivity implements OnClickListener{

	private ImageView imageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_portrait);
		imageView = (ImageView)findViewById(R.id.imageView);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		setPortrait();
	}
	
	private void setPortrait()
	{
		Glide.with(this)
		.load(InfoCache.getInstance().getPersonalInfo().getHead_portrait())
		.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
		.placeholder(R.drawable.avatar_def).into(imageView);
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    
	@Override
	protected void onResume() {
		super.onResume();
        MobclickAgent.onResume(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/**
		 * 返回键
		 */
		case R.id.imageViewBack:
			finish();
			break;
		}
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		
	}

}
