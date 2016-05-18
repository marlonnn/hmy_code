package com.BC.entertainmentgravitation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.BC.entertainment.cache.ChatCache;
import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.BC.entertainmentgravitation.fragment.ExitFragmentListener;
import com.BC.entertainmentgravitation.fragment.NEVideoView;
import com.BC.entertainmentgravitation.fragment.PullFragment.IPullMedia;
import com.BC.entertainmentgravitation.fragment.TopPullFragment;
import com.netease.neliveplayer.NEMediaPlayer;
import com.summer.activity.BaseActivity;
import com.umeng.analytics.MobclickAgent;

public class PullActivity extends BaseActivity implements OnClickListener, IPullMedia, ExitFragmentListener{

	private ChatRoom chatRoom;
	private NEVideoView mVideoView;
	NEMediaPlayer mMediaPlayer = new NEMediaPlayer();

	private TopPullFragment topFragment;
	


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pull_video);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
		
		chatRoom = ChatCache.getInstance().getChatRoom();
        mVideoView = (NEVideoView) findViewById(R.id.videoview);
        mVideoView.setBufferStrategy(0); //直播低延时
		mVideoView.setMediaType("livestream");
		mVideoView.setHardwareDecoder(false);
		mVideoView.setPauseInBackground(true);
		mVideoView.setVideoScalingMode(2);
		mVideoView.setVideoPath(chatRoom.getHttpPullUrl());
		mMediaPlayer.setLogLevel(8); //设置log级别
		mVideoView.requestFocus();
		mVideoView.start();
		
        /**
         * 初始化聊天室、输入框、送礼物等
         */
        topFragment = new TopPullFragment(PullActivity.this, chatRoom);
        topFragment.show(getSupportFragmentManager(), "push video");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!mVideoView.isPaused()) {
			mVideoView.start(); //锁屏打开后恢复播放
		}
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		close();
		super.onDestroy();
	}
	@Override
	public void onBackPressed() {
		close();
		super.onBackPressed();
	}
	
	private void close()
	{

		this.finish();
	}

	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		
	}

	@Override
	public void finishPullMedia() {
		if (mVideoView != null)
		{
			mVideoView.release_resource();
		}
		finish();
	}

	@Override
	public void isExit(boolean exit) {
		if (mVideoView != null)
		{
			mVideoView.release_resource();
		}
		finish();
		
	}

	@Override
	public void isExit(boolean exit, long totalPeople) {
		
	}
	
}
