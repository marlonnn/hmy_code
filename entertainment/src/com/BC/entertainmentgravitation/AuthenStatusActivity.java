package com.BC.entertainmentgravitation;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.BC.entertainment.cache.InfoCache;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.summer.view.CircularImage;
import com.umeng.analytics.MobclickAgent;

public class AuthenStatusActivity extends BaseActivity implements OnClickListener{

	private CircularImage cImagePortrait;
	private TextView textViewStatus;
	private ImageView imgViewAuthenticated;
	private String message = "";
	private int status;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authenticate_status);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		message = getIntent().getStringExtra("authenStatus");
		status = getIntent().getIntExtra("status", -1);
		cImagePortrait = (CircularImage) findViewById(R.id.cViewPortrait);
		textViewStatus = (TextView) findViewById(R.id.textViewStatus);
		imgViewAuthenticated = (ImageView) findViewById(R.id.imgViewAuthenticated);
		Glide.with(this)
		.load(InfoCache.getInstance().getPersonalInfo().getHead_portrait())
		.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
		.placeholder(R.drawable.avatar_def).into(cImagePortrait);
		switch(status)
		{
		case 0:
			imgViewAuthenticated.setVisibility(View.VISIBLE);
			break;
		case 602:
			imgViewAuthenticated.setVisibility(View.GONE);
			break;
		default:
			imgViewAuthenticated.setVisibility(View.GONE);
			break;
		}
		textViewStatus.setText("认证状态 : " + message);
		
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
