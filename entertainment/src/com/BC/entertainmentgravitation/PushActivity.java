package com.BC.entertainmentgravitation;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.BC.entertainment.cache.ChatCache;
import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.config.PushConfig;
import com.BC.entertainment.view.LiveSurfaceView;
import com.BC.entertainmentgravitation.entity.Member;
import com.BC.entertainmentgravitation.entity.StarLiveVideoInfo;
import com.BC.entertainmentgravitation.fragment.ExitFragmentListener;
import com.BC.entertainmentgravitation.fragment.PushFragment.IPushMedia;
import com.BC.entertainmentgravitation.fragment.TopPushFragment;
import com.netease.LSMediaCapture.lsMediaCapture;
import com.netease.LSMediaCapture.lsMediaCapture.LSLiveStreamingParaCtx;
import com.netease.LSMediaCapture.lsMessageHandler;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.summer.logger.XLog;
import com.summer.utils.StringUtil;
import com.summer.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

public class PushActivity extends BaseActivity implements lsMessageHandler, IPushMedia, ExitFragmentListener{
	
	private Context mContext;
//	private ChatRoom chatRoom;
	private LiveSurfaceView mVideoView;
	private lsMediaCapture mLSMediaCapture;
	private boolean m_liveStreamingInit = false;
	private boolean m_liveStreamingInitFinished = false;
	private boolean mHardWareEncEnable = false;
	private boolean m_liveStreamingOn = false;
	private final int LS_VIDEO_CODEC_AVC = 0;
	private LSLiveStreamingParaCtx mLSLiveStreamingParaCtx = null;
    private long mLastVideoProcessErrorAlertTime = 0;
    
//    private PushFragment pushFragment;

    private TopPushFragment topFragment;
	private RelativeLayout rlayoutLoading;
	private AbortableFuture<EnterChatRoomResultData> enterRequest;//聊天室
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push);
//		scrollView = (ScrollView)findViewById(R.id.scrollView);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
		getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//		chatRoom = ChatCache.getInstance().getChatRoom();
        mVideoView = (LiveSurfaceView) findViewById(R.id.videoview);
        rlayoutLoading = (RelativeLayout) findViewById(R.id.rLayoutPushLoading);
        Intent intent = this.getIntent();
        StarLiveVideoInfo startLiveVideoInfo = (StarLiveVideoInfo)intent.getSerializableExtra("liveInfo");
        enterChatRoom(startLiveVideoInfo, true);
//        delayLiveVideo();
//        handler = new Handler();
//        handler.postDelayed(runnable, 10);

        mVideoView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				topFragment.pushFragment.showShare();
				return false;
			}
		});
	}
	
    @SuppressWarnings("unchecked")
	private void enterChatRoom(final StarLiveVideoInfo startLiveVideoInfo, final boolean isPush)
    {
        EnterChatRoomData data = new EnterChatRoomData(startLiveVideoInfo.getChatroomid());
        enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoom(data);
        enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>(){

			@Override
			public void onException(Throwable exception) {
				 onLoginDone();
				 XLog.e("enter chat room exception, e=" + exception.getMessage());
	             Toast.makeText(PushActivity.this, 
	            		 StringUtil.getXmlResource(PushActivity.this, R.string.push_video_nim_login_exception) + exception.getMessage(),
	            		 Toast.LENGTH_SHORT).show();
	             finish();
			}

			@Override
			public void onFailed(int code) {
                if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
                    Toast.makeText(PushActivity.this, 
                    		StringUtil.getXmlResource(PushActivity.this, R.string.push_video_nim_black_list), 
                    		Toast.LENGTH_SHORT).show();
                } else {
                	XLog.e("enter chat room failed, code=" + code);
                    Toast.makeText(PushActivity.this, "enter chat room failed, code=" + code, Toast.LENGTH_SHORT).show();
                }
                finish();
			}

			@Override
			public void onSuccess(EnterChatRoomResultData result) {
				ChatRoomInfo roomInfo = result.getRoomInfo();
                ChatRoomMember member = result.getMember();
                member.setRoomId(roomInfo.getRoomId());
                ChatCache.getInstance().ClearMember();
                ChatCache.getInstance().AddMember(createMasterMember());
                ChatCache.getInstance().getChatRoom().setChatRoomInfo(roomInfo);
                if (isPush)
                {
                    startLiveVideo(startLiveVideoInfo);	
                }
                else
                {
//                	startWatchVideo(startLiveVideoInfo);
                }
                XLog.i("enter chat room success" + roomInfo.getRoomId());
			}});
    }
    
	/**
	 * 明星直播
	 * @param startLiveVideoInfo
	 */
	private void startLiveVideo(StarLiveVideoInfo startLiveVideoInfo)
	{
		ChatCache.getInstance().getChatRoom().setChatroomid(startLiveVideoInfo.getChatroomid());
		ChatCache.getInstance().getChatRoom().setCid(startLiveVideoInfo.getCid());
		ChatCache.getInstance().getChatRoom().setPushUrl(startLiveVideoInfo.getPushUrl());
		ChatCache.getInstance().getChatRoom().setMaster(true);
        /**
         * 初始化推流
         */
        initializeLive();
        /**
         * 初始化聊天室、输入框、送礼物等
         */
        topFragment = new TopPushFragment(PushActivity.this, ChatCache.getInstance().getChatRoom());
        topFragment.show(getSupportFragmentManager(), "push video");
	}
	
	
	public void showHideFragment(){
	
	    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//	    ft.setCustomAnimations(android.R.animator.fade_in,
//	                    android.R.animator.fade_out);
	
	    if (topFragment.isHidden()) {
	        ft.show(topFragment);
	        XLog.d("===============Show==========");
	    } else {
	        ft.hide(topFragment);
	        XLog.d("===============Hide============");                        
	    }
	
	    ft.commit();
	}
	
    private void onLoginDone() {
        enterRequest = null;
        DialogMaker.dismissProgressDialog();
    }
    
    private Member createMasterMember()
    {
    	Member m = new Member();
    	m.setId(Config.User.getClientID());
    	m.setName(Config.User.getUserName());
        m.setPortrait(InfoCache.getInstance().getPersonalInfo().getHead_portrait());
        m.setAge(InfoCache.getInstance().getPersonalInfo().getAge());
        m.setNick(InfoCache.getInstance().getPersonalInfo().getNickname());
        m.setDollar(InfoCache.getInstance().getPersonalInfo().getEntertainment_dollar());
        m.setPiao(InfoCache.getInstance().getPersonalInfo().getPiao());
    	return m;
    }
	
	private Runnable runnable = new Runnable(){

		@Override
		public void run() {
	        /**
	         * 初始化推流
	         */
	        initializeLive();
		}
		
	};
	
	private void delayLiveVideo()
	{
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
		        /**
		         * 初始化推流
		         */
		        initializeLive();
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
        if(mLSMediaCapture != null) {
        	mLSMediaCapture.resumeVideoEncode();
        }
        MobclickAgent.onPause(this);
	}


	@Override
	protected void onResume() {
		super.onResume();
		if (topFragment != null)
		{
			topFragment.onResume();
		}

		MobclickAgent.onResume(this);
	}


	@Override
	protected void onRestart() {
        if(mLSMediaCapture != null) {
        	//关闭推流固定图像
        	mLSMediaCapture.stopVideoEncode();
        }   
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

		if(m_liveStreamingInit) {
			m_liveStreamingInit = false;
		}
        //停止直播调用相关API接口
		if(mLSMediaCapture != null && m_liveStreamingInitFinished && m_liveStreamingOn) {		
			mLSMediaCapture.stopLiveStreaming();		
			mLSMediaCapture.stopVideoPreview();
			mLSMediaCapture.destroyVideoPreview();	
			mLSMediaCapture = null;
		}
		else if(mLSMediaCapture != null && m_liveStreamingInitFinished)
		{		
			mLSMediaCapture.stopVideoPreview();
			mLSMediaCapture.destroyVideoPreview();	
			mLSMediaCapture = null;
			
		}
		if(m_liveStreamingOn) {
		    m_liveStreamingOn = false;
		}

		super.onDestroy();
	}

	private void initializeLive()
	{
    	final int mVideoPreviewWidth = 640;
    	final int mVideoPreviewHeight = 480;
    	mContext = this;
	    //创建直播实例
        mLSMediaCapture = new lsMediaCapture(this, mContext, mVideoPreviewWidth, mVideoPreviewHeight);
        mVideoView.setPreviewSize(mVideoPreviewWidth, mVideoPreviewHeight);
        //创建参数实例
        mLSLiveStreamingParaCtx = mLSMediaCapture.new LSLiveStreamingParaCtx();
        mLSLiveStreamingParaCtx.eHaraWareEncType = mLSLiveStreamingParaCtx.new HardWareEncEnable();
        mLSLiveStreamingParaCtx.eOutFormatType = mLSLiveStreamingParaCtx.new OutputFormatType();
        mLSLiveStreamingParaCtx.eOutStreamType = mLSLiveStreamingParaCtx.new OutputStreamType();
        mLSLiveStreamingParaCtx.sLSAudioParaCtx = mLSLiveStreamingParaCtx.new LSAudioParaCtx();
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.codec = mLSLiveStreamingParaCtx.sLSAudioParaCtx.new LSAudioCodecType();
        mLSLiveStreamingParaCtx.sLSVideoParaCtx = mLSLiveStreamingParaCtx.new LSVideoParaCtx();
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec = mLSLiveStreamingParaCtx.sLSVideoParaCtx.new LSVideoCodecType();
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition = mLSLiveStreamingParaCtx.sLSVideoParaCtx.new CameraPosition();
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.interfaceOrientation = mLSLiveStreamingParaCtx.sLSVideoParaCtx.new CameraOrientation();
        if(mLSMediaCapture != null) {  
        	boolean ret = false;
        	//设置摄像头信息，并开始本地视频预览
        	mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition.cameraPosition = PushConfig.CAMERA_POSITION_BACK;//默认后置摄像头，用户可以根据需要调整
		    mLSMediaCapture.startVideoPreview(mVideoView, mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition.cameraPosition);
            //配置音视频和camera参数
            paraSet();
            //设置日志级别
        	mLSMediaCapture.setTraceLevel(PushConfig.LS_LOG_INFO);
            //初始化直播推流
	        ret = mLSMediaCapture.initLiveStream(ChatCache.getInstance().getChatRoom().getPushUrl(),
	        		mLSLiveStreamingParaCtx);
	        XLog.i("------ret---------" + ret);
	        if(ret) 
	        {
	        	m_liveStreamingInit = true;
	        	m_liveStreamingInitFinished = true;
	        	XLog.i("------ret---------" + "success");
	        }
	        else {
	        	m_liveStreamingInit = true;
	        	m_liveStreamingInitFinished = false;
	        	XLog.i("------ret---------" + "fail");
	        }
	        startAV();
        }
	}
	
	//音视频参数设置
	public void paraSet(){
		//输出格式：视频、音频和音视频
		mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType = PushConfig.HAVE_AV;
		//输出封装格式
		mLSLiveStreamingParaCtx.eOutFormatType.outputFormatType = PushConfig.RTMP;
        //摄像头参数配置
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.interfaceOrientation.interfaceOrientation = PushConfig.CAMERA_ORIENTATION_PORTRAIT;//竖屏
        //音频编码参数配置
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.samplerate = 44100;
    	mLSLiveStreamingParaCtx.sLSAudioParaCtx.bitrate = 64000;
    	mLSLiveStreamingParaCtx.sLSAudioParaCtx.frameSize = 2048;
    	mLSLiveStreamingParaCtx.sLSAudioParaCtx.audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    	mLSLiveStreamingParaCtx.sLSAudioParaCtx.channelConfig = AudioFormat.CHANNEL_IN_MONO;	
    	mLSLiveStreamingParaCtx.sLSAudioParaCtx.codec.audioCODECType = PushConfig.LS_AUDIO_CODEC_AAC;
    	//硬件编码参数设置
        mLSLiveStreamingParaCtx.eHaraWareEncType.hardWareEncEnable = mHardWareEncEnable;	
    	mLSLiveStreamingParaCtx.sLSVideoParaCtx.fps = 20;
    	mLSLiveStreamingParaCtx.sLSVideoParaCtx.bitrate = 600000;
    	mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec.videoCODECType = LS_VIDEO_CODEC_AVC;
    	mLSLiveStreamingParaCtx.sLSVideoParaCtx.width = 640;
    	mLSLiveStreamingParaCtx.sLSVideoParaCtx.height = 480;
        
	}
	
	//开始直播
	private void startAV(){
		XLog.i("start av----");
		if(mLSMediaCapture != null && m_liveStreamingInitFinished) {
		    mLSMediaCapture.startLiveStreaming();
		    m_liveStreamingOn = true;
		    rlayoutLoading.setVisibility(View.GONE);
		    mVideoView.setVisibility(View.VISIBLE);
		    XLog.i("========================startAV=======================");
		}
	}

	@Override
	public void handleMessage(int msg, Object arg1) {
		  switch (msg) {
	      case lsMessageHandler.MSG_INIT_LIVESTREAMING_OUTFILE_ERROR://初始化直播出错
	      case lsMessageHandler.MSG_INIT_LIVESTREAMING_VIDEO_ERROR:	
	      case lsMessageHandler.MSG_INIT_LIVESTREAMING_AUDIO_ERROR:
	    	  toastAndExit("初始化直播出错");
	    	  break;
	      case lsMessageHandler.MSG_START_LIVESTREAMING_ERROR://开始直播出错
	    	  toastAndExit("开始直播出错");
	    	  break;
	      case lsMessageHandler.MSG_STOP_LIVESTREAMING_ERROR://停止直播出错
	    	  toastAndExit("停止直播出错");
	    	  break;
	      case lsMessageHandler.MSG_AUDIO_PROCESS_ERROR://音频处理出错
	    	  toastAndExit("音频处理出错");
	    	  break;
	      case lsMessageHandler.MSG_VIDEO_PROCESS_ERROR://视频处理出错
	    	  if (m_liveStreamingOn && System.currentTimeMillis() - mLastVideoProcessErrorAlertTime >= 10000)
	    	  {
		    	  toastAndExit("视频处理出错");
		    	  mLastVideoProcessErrorAlertTime = System.currentTimeMillis();
	    	  }
	    	  break;
	      case lsMessageHandler.MSG_RTMP_URL_ERROR://断网消息
	    	  toastAndExit("断网了，请检查网络连接");
	    	  break;
	      case lsMessageHandler.MSG_URL_NOT_AUTH://直播URL非法
	    	  toastAndExit("直播地址URL非法，请检查");
	    	  break;
	      case lsMessageHandler.MSG_SEND_STATICS_LOG_ERROR://发送统计信息出错
	    	  toastAndExit("发送统计信息出错");
	    	  break;
	      case lsMessageHandler.MSG_SEND_HEARTBEAT_LOG_ERROR://发送心跳信息出错
	    	  toastAndExit("发送心跳信息出错");
	    	  break;
	      case lsMessageHandler.MSG_AUDIO_SAMPLE_RATE_NOT_SUPPORT_ERROR://音频采集参数不支持
	    	  toastAndExit("音频采集参数不支持");
	    	  break;
	      case lsMessageHandler.MSG_AUDIO_PARAMETER_NOT_SUPPORT_BY_HARDWARE_ERROR://音频参数不支持
	    	  toastAndExit("音频参数不支持");
	    	  break;
	      case lsMessageHandler.MSG_NEW_AUDIORECORD_INSTANCE_ERROR://音频实例初始化出错
	    	  toastAndExit("音频实例初始化出错");
	    	  break;
	      case lsMessageHandler.MSG_AUDIO_START_RECORDING_ERROR://音频采集出错
	    	  toastAndExit("音频采集出错");
	    	  break;
	      case lsMessageHandler.MSG_OTHER_AUDIO_PROCESS_ERROR://音频操作的其他错误
	    	  toastAndExit("音频操作的其他错误");
	    	  break;
	      case lsMessageHandler.MSG_QOS_TO_STOP_LIVESTREAMING://网络QoS极差，码率档次降到最低
	    	  break;
	      case lsMessageHandler.MSG_HW_VIDEO_PACKET_ERROR://视频硬件编码出错
	    	  toastAndExit("视频硬件编码出错");
	    	  break;
	      case lsMessageHandler.MSG_CAMERA_PREVIEW_SIZE_NOT_SUPPORT_ERROR://camera采集分辨率不支持
	    	  toastAndExit("采集分辨率不支持");
	    	  break;
	      case lsMessageHandler.MSG_START_LIVESTREAMING_FINISHED://开始直播完成
	    	  break;
	      case lsMessageHandler.MSG_STOP_LIVESTREAMING_FINISHED://停止直播完成
	    	  toastAndExit("停止直播完成");
              break;
		  }
	}
	
    private void toastAndExit(String message)
    {
    	if(message != null)
    	{
        	ToastUtil.show(this, message);
        	topFragment.Destroy();
			if(m_liveStreamingInit) {
				m_liveStreamingInit = false;
			}
	        //停止直播调用相关API接口
			if(mLSMediaCapture != null && m_liveStreamingInitFinished && m_liveStreamingOn) {		
				mLSMediaCapture.stopLiveStreaming();		
				mLSMediaCapture.stopVideoPreview();
				mLSMediaCapture.destroyVideoPreview();	
				mLSMediaCapture = null;
			}
			else if(mLSMediaCapture != null && m_liveStreamingInitFinished)
			{		
				mLSMediaCapture.stopVideoPreview();
				mLSMediaCapture.destroyVideoPreview();	
				mLSMediaCapture = null;
				
			}
			if(m_liveStreamingOn) {
			    m_liveStreamingOn = false;
			}

        	finish();
    	}
    }

	@Override
	public void onBackPressed() {
		topFragment.Destroy();
		if(m_liveStreamingInit) {
			m_liveStreamingInit = false;
		}
        //停止直播调用相关API接口
		if(mLSMediaCapture != null && m_liveStreamingInitFinished && m_liveStreamingOn) {		
			mLSMediaCapture.stopLiveStreaming();		
			mLSMediaCapture.stopVideoPreview();
			mLSMediaCapture.destroyVideoPreview();	
			mLSMediaCapture = null;
		}
		else if(mLSMediaCapture != null && m_liveStreamingInitFinished)
		{		
			mLSMediaCapture.stopVideoPreview();
			mLSMediaCapture.destroyVideoPreview();	
			mLSMediaCapture = null;
			
		}
		if(m_liveStreamingOn) {
		    m_liveStreamingOn = false;
		}
		this.finish();
		super.onBackPressed();
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		
	}

	@Override
	public void onSwitchCamera() {
		if( mLSMediaCapture != null)
		{
			mLSMediaCapture.switchCamera();
		}
	}

	@Override
	public void finishPushMedia() {
		if(m_liveStreamingInit) {
			m_liveStreamingInit = false;
		}
        //停止直播调用相关API接口
		if(mLSMediaCapture != null && m_liveStreamingInitFinished && m_liveStreamingOn) {		
			mLSMediaCapture.stopLiveStreaming();		
			mLSMediaCapture.stopVideoPreview();
			mLSMediaCapture.destroyVideoPreview();	
			mLSMediaCapture = null;
		}
		else if(mLSMediaCapture != null && m_liveStreamingInitFinished)
		{		
			mLSMediaCapture.stopVideoPreview();
			mLSMediaCapture.destroyVideoPreview();	
			mLSMediaCapture = null;
			
		}
		if(m_liveStreamingOn) {
		    m_liveStreamingOn = false;
		}
	}

	@Override
	public void isExit(boolean exit) {
		if (exit)
		{
			topFragment.Destroy();
			if(m_liveStreamingInit) {
				m_liveStreamingInit = false;
			}
	        //停止直播调用相关API接口
			if(mLSMediaCapture != null && m_liveStreamingInitFinished && m_liveStreamingOn) {		
				mLSMediaCapture.stopLiveStreaming();		
				mLSMediaCapture.stopVideoPreview();
				mLSMediaCapture.destroyVideoPreview();	
				mLSMediaCapture = null;
			}
			else if(mLSMediaCapture != null && m_liveStreamingInitFinished)
			{		
				mLSMediaCapture.stopVideoPreview();
				mLSMediaCapture.destroyVideoPreview();	
				mLSMediaCapture = null;
				
			}
			if(m_liveStreamingOn) {
			    m_liveStreamingOn = false;
			}
			finish();
		}
	}

	@Override
	public void isExit(boolean exit, long totalPeople) {
		
	}

}
