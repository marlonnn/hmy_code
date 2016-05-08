package com.BC.entertainmentgravitation;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.BC.entertainment.view.LiveSurfaceView;
import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.netease.LSMediaCapture.lsMediaCapture;
import com.netease.LSMediaCapture.lsMessageHandler;
import com.netease.LSMediaCapture.lsMediaCapture.LSLiveStreamingParaCtx;
import com.summer.activity.BaseActivity;
import com.summer.logger.XLog;
import com.summer.utils.ToastUtil;

public class PushActivity extends BaseActivity implements View.OnClickListener, lsMessageHandler{
	
	private Context mContext;
	private View rootView;
	private ChatRoom chatRoom;
	private LiveSurfaceView mVideoView;
	private lsMediaCapture mLSMediaCapture;
	private boolean m_liveStreamingInit = false;
	private boolean m_liveStreamingInitFinished = false;
	private boolean mHardWareEncEnable = false;
	private boolean m_liveStreamingOn = false;
	
	private final int LS_VIDEO_CODEC_AVC = 0;
	
	//SDK统计相关变量
	private LSLiveStreamingParaCtx mLSLiveStreamingParaCtx = null;
	public static final int CAMERA_POSITION_BACK = 0;
	public static final int CAMERA_POSITION_FRONT = 1;
	public static final int CAMERA_ORIENTATION_PORTRAIT = 0;
	public static final int CAMERA_ORIENTATION_LANDSCAPE = 1;
	public static final int LS_AUDIO_STREAMING_LOW_QUALITY = 0;
	public static final int LS_AUDIO_STREAMING_HIGH_QUALITY = 1;

	public static final int LS_AUDIO_CODEC_AAC = 0;
	public static final int LS_AUDIO_CODEC_SPEEX = 1;
	public static final int LS_AUDIO_CODEC_MP3 = 2;
	public static final int LS_AUDIO_CODEC_G711A = 3;
	public static final int LS_AUDIO_CODEC_G711U = 4;
    
	public static final int FLV = 0;
	public static final int RTMP = 1;

	public static final int HAVE_AUDIO = 0;
	public static final int HAVE_VIDEO = 1;
	public static final int HAVE_AV = 2;
	
	public static final int LS_LOG_QUIET       = 0x00;            //!< log输出模式：不输出
    public static final int LS_LOG_ERROR       = 1 << 0;          //!< log输出模式：输出错误
    public static final int LS_LOG_WARNING     = 1 << 1;          //!< log输出模式：输出警告
    public static final int LS_LOG_INFO        = 1 << 2;          //!< log输出模式：输出信息
    public static final int LS_LOG_DEBUG       = 1 << 3;          //!< log输出模式：输出调试信息
    public static final int LS_LOG_DETAIL      = 1 << 4;          //!< log输出模式：输出详细
    public static final int LS_LOG_RESV        = 1 << 5;          //!< log输出模式：保留
    public static final int LS_LOG_LEVEL_COUNT = 6;
    public static final int LS_LOG_DEFAULT     = LS_LOG_WARNING;	//!< log输出模式：默认输出警告
    private long mLastVideoProcessErrorAlertTime = 0;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_video);
		rootView = findViewById(R.id.root_content);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
		
        Intent intent = getIntent();
        if (intent != null)
        {
        	chatRoom = new ChatRoom();
        	try {
				chatRoom.setCid(intent.getStringExtra("cid"));
				chatRoom.setChatroomid(intent.getStringExtra("chatroomid"));
				chatRoom.setPushUrl(intent.getStringExtra("pushUrl"));
				chatRoom.setMaster(intent.getBooleanExtra("isMaster", false));
			} catch (Exception e) {
				Toast.makeText(this, "直播间错误", Toast.LENGTH_LONG).show();
				e.printStackTrace();
				finish();
			}
        }
        
        mVideoView = (LiveSurfaceView) findViewById(R.id.videoview);
        
        initializeLive();
	}
	
	@Override
	protected void onPause() {
        if(mLSMediaCapture != null) {
        	mLSMediaCapture.resumeVideoEncode();
        }
		super.onPause();
	}


	@Override
	protected void onResume() {
		super.onResume();
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
        	mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition.cameraPosition = CAMERA_POSITION_BACK;//默认后置摄像头，用户可以根据需要调整
		    mLSMediaCapture.startVideoPreview(mVideoView, mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition.cameraPosition);
        
            //配置音视频和camera参数
            paraSet();
            
            //设置日志级别
        	mLSMediaCapture.setTraceLevel(LS_LOG_INFO);
        	
            //初始化直播推流
	        ret = mLSMediaCapture.initLiveStream("rtmp://p112.live.126.net/live/77934cf008bf4972abbc2af27eaee751?wsSecret=d5352b9f5b36a232286df0321432696c&wsTime=1462724959",
	        		mLSLiveStreamingParaCtx);
	        XLog.i("------ret---------" + ret);
	        if(ret) {
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
		mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType = HAVE_AV;
		//输出封装格式
		mLSLiveStreamingParaCtx.eOutFormatType.outputFormatType = RTMP;
        //摄像头参数配置
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.interfaceOrientation.interfaceOrientation = CAMERA_ORIENTATION_PORTRAIT;//竖屏
        //音频编码参数配置
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.samplerate = 44100;
    	mLSLiveStreamingParaCtx.sLSAudioParaCtx.bitrate = 64000;
    	mLSLiveStreamingParaCtx.sLSAudioParaCtx.frameSize = 2048;
    	mLSLiveStreamingParaCtx.sLSAudioParaCtx.audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    	mLSLiveStreamingParaCtx.sLSAudioParaCtx.channelConfig = AudioFormat.CHANNEL_IN_MONO;	
    	mLSLiveStreamingParaCtx.sLSAudioParaCtx.codec.audioCODECType = LS_AUDIO_CODEC_AAC;
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
		    XLog.i("========================startAV=======================");
		}
	}
	
	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		
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
        	finish();
    	}
    }

	@Override
	public void onClick(View v) {
	}

	@Override
	public void onBackPressed() {
		this.finish();
		super.onBackPressed();
	}

	
}
