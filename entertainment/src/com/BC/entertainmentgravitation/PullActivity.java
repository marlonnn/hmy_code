package com.BC.entertainmentgravitation;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.BC.entertainment.cache.ChatCache;
import com.BC.entertainment.inter.IMedia;
import com.BC.entertainmentgravitation.entity.StarLiveVideoInfo;
import com.BC.entertainmentgravitation.fragment.ExitFragmentListener;
import com.BC.entertainmentgravitation.fragment.NEVideoView;
import com.BC.entertainmentgravitation.fragment.TopPullFragment;
import com.netease.neliveplayer.NEMediaPlayer;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.summer.logger.XLog;
import com.summer.utils.StringUtil;
import com.summer.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

public class PullActivity extends BaseActivity implements OnClickListener, IMedia, ExitFragmentListener{

	private NEVideoView mVideoView;
	private NEMediaPlayer mMediaPlayer = new NEMediaPlayer();

	private TopPullFragment topFragment;
	
	private AbortableFuture<EnterChatRoomResultData> enterRequest;//聊天室

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pull_video);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
		
		Intent intent = this.getIntent();
		StarLiveVideoInfo startLiveVideoInfo = (StarLiveVideoInfo)intent.getSerializableExtra("liveInfo");
        enterChatRoom(startLiveVideoInfo, false);
		
	}
	
	private void startVideo()
	{
        mVideoView = (NEVideoView) findViewById(R.id.videoview);
        mVideoView.setBufferStrategy(0); //直播低延时
		mVideoView.setMediaType("livestream");
		mVideoView.setHardwareDecoder(false);
		mVideoView.setPauseInBackground(true);
		mVideoView.setVideoScalingMode(2);
		mVideoView.setVideoPath(ChatCache.getInstance().getChatRoom().getHttpPullUrl());
		mMediaPlayer.setLogLevel(8); //设置log级别
		mVideoView.requestFocus();
		mVideoView.start();
        /**
         * 初始化聊天室、输入框、送礼物等
         */
        topFragment = new TopPullFragment(PullActivity.this, ChatCache.getInstance().getChatRoom());
        topFragment.show(getSupportFragmentManager(), "push video");
	}
	
    @SuppressWarnings("unchecked")
	private void enterChatRoom(final StarLiveVideoInfo startLiveVideoInfo, final boolean isPush)
    {
        EnterChatRoomData data = new EnterChatRoomData(startLiveVideoInfo.getChatroomid());
        Map<String, Object> extention = new HashMap<>();
        extention.put("nick", Config.User.getNickName());
        extention.put("avatar", Config.User.getImage());
        data.setNick(Config.User.getNickName());
        data.setAvatar(Config.User.getImage());
        data.setNotifyExtension(extention);
//        data.setExtension(extention);
        enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoom(data);
        enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>(){

			@Override
			public void onException(Throwable exception) {
				 onLoginDone();
				 XLog.e("enter chat room exception, e=" + exception.getMessage());
	             Toast.makeText(PullActivity.this, 
	            		 StringUtil.getXmlResource(PullActivity.this, R.string.push_video_nim_login_exception) + exception.getMessage(),
	            		 Toast.LENGTH_SHORT).show();
	             finish();
			}

			@Override
			public void onFailed(int code) {
                if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
                    Toast.makeText(PullActivity.this, 
                    		StringUtil.getXmlResource(PullActivity.this, R.string.push_video_nim_black_list), 
                    		Toast.LENGTH_SHORT).show();
                } else {
                	XLog.e("enter chat room failed, code=" + code);
                    Toast.makeText(PullActivity.this, "enter chat room failed, code=" + code, Toast.LENGTH_SHORT).show();
                }
                finish();
			}

			@Override
			public void onSuccess(EnterChatRoomResultData result) {
                startWatchVideo(startLiveVideoInfo);
			}});
    }
    
    private void startWatchVideo(final StarLiveVideoInfo startLiveVideoInfo)
    {
		if(startLiveVideoInfo != null && startLiveVideoInfo.getHttpPullUrl() != null && !startLiveVideoInfo.getHttpPullUrl().isEmpty())
		{
			ChatCache.getInstance().getChatRoom().setChatroomid(startLiveVideoInfo.getChatroomid());
			ChatCache.getInstance().getChatRoom().setCid(startLiveVideoInfo.getCid());
			ChatCache.getInstance().getChatRoom().setHttpPullUrl(startLiveVideoInfo.getHttpPullUrl());
			ChatCache.getInstance().getChatRoom().setMaster(true);
			startVideo();
		}
		else
		{
			ToastUtil.show(this, "该直播暂未开始！");
		}
    }
    
    private void onLoginDone() {
        enterRequest = null;
    }
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mVideoView != null)
		{
			if (!mVideoView.isPaused()) {
				mVideoView.start(); //锁屏打开后恢复播放
			}
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

	@Override
	public void finishPushMedia() {
		
	}

	@Override
	public void onSwitchCamera() {
		
	}
	
}
