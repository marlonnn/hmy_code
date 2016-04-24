package com.BC.entertainmentgravitation.fragment;

import java.util.List;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.BC.entertainment.view.LiveSurfaceView;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.netease.LSMediaCapture.lsMediaCapture;
import com.netease.LSMediaCapture.lsMediaCapture.LSLiveStreamingParaCtx;
import com.netease.LSMediaCapture.lsMediaCapture.Statistics;
import com.netease.LSMediaCapture.lsMessageHandler;
import com.netease.livestreamingFilter.view.CameraSurfaceView;
import com.summer.fragment.BaseFragment;
import com.summer.logger.XLog;

/**
 * 明星直播间，推送视频
 * @author wen zhong
 *
 */
public class PushVideoFragment extends BaseFragment implements View.OnClickListener, lsMessageHandler{

	private View rootView;
	
	private lsMediaCapture mLSMediaCapture = null;
	
	private boolean m_liveStreamingOn = false;
	private boolean m_liveStreamingPause = false;
	private boolean m_liveStreamingInit = false;
	private boolean m_liveStreamingInitFinished = false;
	
	public final int CAMERA_POSITION_BACK = 0;
	
	public final int CAMERA_POSITION_FRONT = 1;
	
	private String cid;
	
	private String mliveStreamingURL;
	
	private String chatroomid;
	
	private boolean mHardWareEncEnable = false;//是否支持滤镜模式
	
	private CameraSurfaceView mCameraSurfaceView;
	
    private LiveSurfaceView mVideoView;
	
    //查询摄像头支持的采集分辨率信息相关变量
    private Thread mCameraThread;
    
    private Looper mCameraLooper;
    
    private int mCameraID = CAMERA_POSITION_BACK;//默认查询的是后置摄像头
    
    private Camera mCamera;
    
	//SDK统计相关变量
	private LSLiveStreamingParaCtx mLSLiveStreamingParaCtx = null;
    private Statistics mStatistics = null;
    
    private final int FLV = 0;
    private final int RTMP = 1;

    private final int HAVE_AUDIO = 0;
	private final int HAVE_VIDEO = 1;
	private final int HAVE_AV = 2;
	private final int CAMERA_ORIENTATION_PORTRAIT = 0;
	private final int CAMERA_ORIENTATION_LANDSCAPE = 1;
	
	private final int LS_VIDEO_CODEC_AVC = 0;
	private final int LS_VIDEO_CODEC_VP9 = 1;
	private final int LS_VIDEO_CODEC_H265 = 2;
	
	private final int LS_AUDIO_STREAMING_LOW_QUALITY = 0;
	private final int LS_AUDIO_STREAMING_HIGH_QUALITY = 1;
	
	private final int LS_AUDIO_CODEC_AAC = 0;
	private final int LS_AUDIO_CODEC_SPEEX = 1;
	private final int LS_AUDIO_CODEC_MP3 = 2;
	private final int LS_AUDIO_CODEC_G711A = 3;
	private final int LS_AUDIO_CODEC_G711U = 4;
	
	private final int LS_LOG_QUIET       = 0x00;            //!< log输出模式：不输出
    private final int LS_LOG_ERROR       = 1 << 0;          //!< log输出模式：输出错误
    private final int LS_LOG_WARNING     = 1 << 1;          //!< log输出模式：输出警告
    private final int LS_LOG_INFO        = 1 << 2;          //!< log输出模式：输出信息
    private final int LS_LOG_DEBUG       = 1 << 3;          //!< log输出模式：输出调试信息
    private final int LS_LOG_DETAIL      = 1 << 4;          //!< log输出模式：输出详细
    private final int LS_LOG_RESV        = 1 << 5;          //!< log输出模式：保留
    private final int LS_LOG_LEVEL_COUNT = 6;
    private final int LS_LOG_DEFAULT     = LS_LOG_WARNING;	//!< log输出模式：默认输出警告
	
	private float mCurrentDistance;
	
	private float mLastDistance;
	
	private ChatRoom chatRoom;
	
    private ScrollListener listener;
    
    private Handler handler;

	private long mLastAudioProcessErrorAlertTime = 0;

	private long mLastVideoProcessErrorAlertTime = 0;
    
	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public PushVideoFragment(ChatRoom chatRoom)
	{
		this.chatRoom = chatRoom;
	}
	
    public ScrollListener CreateScrollListener() {
        listener = new ScrollListener() {
            @Override
            public void onScroll(final float transY, final boolean goUp) {
                XLog.i("transY " + transY);
                if (PushVideoFragment.this.isVisible() && null != rootView) {
                    if (goUp) 
                    {
                        rootView.setTranslationY(-transY);
                    } 
                    else 
                    {
                        rootView.setTranslationY(getDeviceHeight(getActivity()) - transY);
                    }

                    if(transY == 0)
                    {
                        rootView.setTranslationY(0);
                    }
                }
            }
        };

        return listener;
    }
    
    private int getDeviceHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.heightPixels;
    }
    
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
		
    	cid = chatRoom.getCid();
    	mliveStreamingURL = chatRoom.getPushUrl();
    	chatroomid = chatRoom.getChatroomid();
    	mHardWareEncEnable = chatRoom.isFilter();
    	XLog.i("cid: " + cid);
    	XLog.i("mliveStreamingURL: " + mliveStreamingURL);
    	XLog.i("chatroomid: " + chatroomid);
    	XLog.i("mHardWareEncEnable: " + mHardWareEncEnable);
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (mHardWareEncEnable)
		{
			return inflater.inflate(R.layout.fragment_push_video_opengl, container, false);
		}
		else
		{
			return inflater.inflate(R.layout.fragment_push_video, container, false);
		}

	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initializeView(view);
	}
	
	public void initializeView(View view)
	{
		rootView = view.findViewById(R.id.layout_root);
		
//        //切换前后摄像头按钮初始化
//        switchBtn = (ImageButton)view.findViewById(R.id.switchBtn);	      
//        switchBtn.setOnClickListener(new OnClickListener() {
// 			@Override
// 			public void onClick(View v) {
// 				switchCamera();
// 			}
// 		});
        
		if(mHardWareEncEnable)
		{
		    mCameraSurfaceView = (CameraSurfaceView) view.findViewById(R.id.camerasurfaceview);
		}
		else		
		{
            mVideoView = (LiveSurfaceView) view.findViewById(R.id.videoview);
        }
	}
	
	private void initializeLive()
	{
    	int mVideoPreviewWidth = 640;
    	int mVideoPreviewHeight = 480;
    	
	    //创建直播实例
        mLSMediaCapture = new lsMediaCapture(this, getActivity(), mVideoPreviewWidth, mVideoPreviewHeight);

		if(mHardWareEncEnable)
		{
		    mCameraSurfaceView.setPreviewSize(mVideoPreviewWidth, mVideoPreviewHeight);
        }
		else
		{
		    mVideoView.setPreviewSize(mVideoPreviewWidth, mVideoPreviewHeight);
		}
		
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
        
        //发送统计数据到网络信息界面
//        staticsHandle();
    	
        if(mLSMediaCapture != null) {  
        	boolean ret = false;
        	
        	//设置摄像头信息，并开始本地视频预览
        	mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition.cameraPosition = CAMERA_POSITION_BACK;//默认后置摄像头，用户可以根据需要调整
            if(mHardWareEncEnable)
			{
			    mLSMediaCapture.startVideoPreviewOpenGL(mCameraSurfaceView, mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition.cameraPosition);
            }
			else
			{
			    mLSMediaCapture.startVideoPreview(mVideoView, mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition.cameraPosition);
			}

            //配置音视频和camera参数
            paraSet();
            
            //设置日志级别
        	mLSMediaCapture.setTraceLevel(LS_LOG_ERROR);
        	
            //初始化直播推流
	        ret = mLSMediaCapture.initLiveStream(mliveStreamingURL, mLSLiveStreamingParaCtx);
	        
	        if(ret) {
	        	m_liveStreamingInit = true;
	        	m_liveStreamingInitFinished = true;
	        	XLog.i("-----------liveStreamingInitFinished------------");
	        }
	        else {
	        	m_liveStreamingInit = true;
	        	m_liveStreamingInitFinished = false;
	        }
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
        
        //视频编码参数配置，视频码率可以由用户任意设置，视频分辨率按照如下表格设置
        //采集分辨率     编码分辨率     建议码率
        //1280x720     1280x720     1500kbps
        //1280x720     960x540      800kbps
        //960x720      960x720      1000kbps
        //960x720      960x540      800kbps
        //960x540      960x540      800kbps
        //640x480      640x480      600kbps
        //640x480      640x360      500kbps
        //320x240      320x240      250kbps
        //320x240      320x180      200kbps
        //如下是编码分辨率等信息的设置
//        if(mVideoResolution.equals("HD")) {
//        	mLSLiveStreamingParaCtx.sLSVideoParaCtx.fps = 20;
//        	mLSLiveStreamingParaCtx.sLSVideoParaCtx.bitrate = 800000;
//        	mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec.videoCODECType = LS_VIDEO_CODEC_AVC;
//        	mLSLiveStreamingParaCtx.sLSVideoParaCtx.width = 960;
//        	mLSLiveStreamingParaCtx.sLSVideoParaCtx.height = 540;
//        }
//        else if(mVideoResolution.equals("SD")) {
//        	mLSLiveStreamingParaCtx.sLSVideoParaCtx.fps = 20;
//        	mLSLiveStreamingParaCtx.sLSVideoParaCtx.bitrate = 600000;
//        	mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec.videoCODECType = LS_VIDEO_CODEC_AVC;
//        	mLSLiveStreamingParaCtx.sLSVideoParaCtx.width = 640;
//        	mLSLiveStreamingParaCtx.sLSVideoParaCtx.height = 480;
//        }
//        else {
//        	mLSLiveStreamingParaCtx.sLSVideoParaCtx.fps = 15;
//        	mLSLiveStreamingParaCtx.sLSVideoParaCtx.bitrate = 250000;
//        	mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec.videoCODECType = LS_VIDEO_CODEC_AVC;
//        	mLSLiveStreamingParaCtx.sLSVideoParaCtx.width = 320;
//        	mLSLiveStreamingParaCtx.sLSVideoParaCtx.height = 240;
//        }
        
    	mLSLiveStreamingParaCtx.sLSVideoParaCtx.fps = 20;
    	mLSLiveStreamingParaCtx.sLSVideoParaCtx.bitrate = 600000;
    	mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec.videoCODECType = LS_VIDEO_CODEC_AVC;
    	mLSLiveStreamingParaCtx.sLSVideoParaCtx.width = 640;
    	mLSLiveStreamingParaCtx.sLSVideoParaCtx.height = 480;
        
        startAV();
	}
	
	//开始直播
	private void startAV(){
		if(mLSMediaCapture != null && m_liveStreamingInitFinished) {
		    mLSMediaCapture.startLiveStreaming();
		    XLog.i("========================startAV=======================");
		    m_liveStreamingOn = true;
		}
	}
	
	private void getSupportCamera()
	{
		List<Camera.Size> cameraSupportSize = getCameraSupportSize();
	}
	
	//查询Android摄像头支持的采样分辨率相关方法（4）
	public List<Camera.Size> getCameraSupportSize() {	
		openCamera();		
		if(mCamera != null) {
			Parameters param = mCamera.getParameters();		
			List<Camera.Size> previewSizes = param.getSupportedPreviewSizes();	
			releaseCamera();		
			return previewSizes;
		}	
		return null;
	}
	
    //查询Android摄像头支持的采样分辨率相关方法（1）
	public void openCamera() {
		final Semaphore lock = new Semaphore(0);
		final RuntimeException[] exception = new RuntimeException[1];
		mCameraThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				mCameraLooper = Looper.myLooper();
				try {
					mCamera = Camera.open(mCameraID);
				} catch (RuntimeException e) {
					exception[0] = e;
				} finally {
					lock.release();
					Looper.loop();
				}
			}
		});
		mCameraThread.start();
		lock.acquireUninterruptibly();
	}
	
	//查询Android摄像头支持的采样分辨率相关方法（2）
	public void lockCamera() {
		try {
			mCamera.reconnect();
		} catch (Exception e) {
		}
	}
	
	//查询Android摄像头支持的采样分辨率相关方法（3）
	public void releaseCamera() {
		if (mCamera != null) {
			lockCamera();
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}		
	}
	
    //切换前后摄像头
	public void SwitchCamera() {
		if(mLSMediaCapture != null) {
			mLSMediaCapture.switchCamera();
		}
	}
		  
	@Override
    public void onPause(){  

        super.onPause(); 
    }
	
	public void Pause()
	{
        if(mLSMediaCapture != null) {
        	mLSMediaCapture.resumeVideoEncode();
        }
	}
      
    @Override
	public void onResume(){   
        super.onResume(); 
    }  
      
    @Override
	public void onStart(){  
        super.onStart();  
		initializeLive();
    }  
      
    @Override
	public void onStop(){  
        super.onStop();
    }
    
	
	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        

	}
	
	public void Destory()
	{
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
	
	//程序切回前台后恢复视频编码  需要在activity onRestart()方法调用
	public void StopVideoEncode()
	{
        if(mLSMediaCapture != null) {
        	//关闭推流固定图像
        	mLSMediaCapture.stopVideoEncode();
        }  
	}
	
	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
	}

	@Override
	public void onClick(View v) {
		
	}
	
	//处理SDK抛上来的异常和事件，用户需要在这里监听各种消息，进行相应的处理。
    //例如监听断网消息，用户根据断网消息进行直播重连
	@Override
	public void handleMessage(int msg, Object object) {
		  switch (msg) {
		      case MSG_INIT_LIVESTREAMING_OUTFILE_ERROR://初始化直播出错
		      case MSG_INIT_LIVESTREAMING_VIDEO_ERROR:	
		      case MSG_INIT_LIVESTREAMING_AUDIO_ERROR:
		    	  if(m_liveStreamingInit)
		    	  {
		    		  sendMessage(MSG_INIT_LIVESTREAMING_OUTFILE_ERROR);
	      		      XLog.i("MSG_INIT_LIVESTREAMING_AUDIO_ERROR");
		    	  }
		    	  break;
		      case MSG_START_LIVESTREAMING_ERROR://开始直播出错
		    	  sendMessage(MSG_START_LIVESTREAMING_ERROR);
		    	  XLog.i("MSG_START_LIVESTREAMING_ERROR");
		    	  break;
		      case MSG_STOP_LIVESTREAMING_ERROR://停止直播出错
		    	  if(m_liveStreamingOn)
		    	  {
		    		  sendMessage(MSG_STOP_LIVESTREAMING_ERROR);
	      		    XLog.i("MSG_STOP_LIVESTREAMING_ERROR");
		    	  }
		    	  break;
		      case MSG_AUDIO_PROCESS_ERROR://音频处理出错
		    	  if(m_liveStreamingOn && System.currentTimeMillis() - mLastAudioProcessErrorAlertTime  >= 10000)
		    	  {
	      		      mLastAudioProcessErrorAlertTime = System.currentTimeMillis();
	      		      sendMessage(MSG_AUDIO_PROCESS_ERROR);
		    	  }

		    	  XLog.i("MSG_AUDIO_PROCESS_ERROR");
		    	  
		    	  break;
		      case MSG_VIDEO_PROCESS_ERROR://视频处理出错
		      {
		    	  if(m_liveStreamingOn && System.currentTimeMillis() - mLastVideoProcessErrorAlertTime  >= 10000)
		    	  {
	      		      mLastVideoProcessErrorAlertTime = System.currentTimeMillis();
	      		      sendMessage(MSG_VIDEO_PROCESS_ERROR);
		    	  }
		    	  XLog.i("MSG_VIDEO_PROCESS_ERROR");
		    	  break;
		      }
		      case MSG_RTMP_URL_ERROR://断网消息
		    	  if (mLSMediaCapture != null)
		    	  {
			    	  mLSMediaCapture.stopLiveStreaming();
			    	  sendMessage(MSG_RTMP_URL_ERROR);
		    	  }
		    	  XLog.i("MSG_RTMP_URL_ERROR");
		    	  break;
		      case MSG_URL_NOT_AUTH://直播URL非法
		    	  if(m_liveStreamingInit)
		    	  {
			    	  sendMessage(MSG_URL_NOT_AUTH);
		    	  }
		    	  XLog.i("MSG_URL_NOT_AUTH");
		    	  break;
		      case MSG_SEND_STATICS_LOG_ERROR://发送统计信息出错
		    	  sendMessage(MSG_SEND_STATICS_LOG_ERROR);
		    	  XLog.i("MSG_SEND_STATICS_LOG_ERROR");
		    	  break;
		      case MSG_SEND_HEARTBEAT_LOG_ERROR://发送心跳信息出错
		    	  sendMessage(MSG_SEND_HEARTBEAT_LOG_ERROR);
		    	  XLog.i("MSG_SEND_HEARTBEAT_LOG_ERROR");
		    	  break;
		      case MSG_AUDIO_SAMPLE_RATE_NOT_SUPPORT_ERROR://音频采集参数不支持
		    	  sendMessage(MSG_AUDIO_SAMPLE_RATE_NOT_SUPPORT_ERROR);
		    	  //Log.i(TAG, "test: in handleMessage, MSG_AUDIO_SAMPLE_RATE_NOT_SUPPORT_ERROR");
		    	  XLog.i("MSG_AUDIO_SAMPLE_RATE_NOT_SUPPORT_ERROR");
		    	  break;
		      case MSG_AUDIO_PARAMETER_NOT_SUPPORT_BY_HARDWARE_ERROR://音频参数不支持
		    	  sendMessage(MSG_AUDIO_PARAMETER_NOT_SUPPORT_BY_HARDWARE_ERROR);
		    	  XLog.i("MSG_AUDIO_PARAMETER_NOT_SUPPORT_BY_HARDWARE_ERROR");
		    	  break;
		      case MSG_NEW_AUDIORECORD_INSTANCE_ERROR://音频实例初始化出错
		    	  sendMessage(MSG_NEW_AUDIORECORD_INSTANCE_ERROR);
		    	  XLog.i("MSG_NEW_AUDIORECORD_INSTANCE_ERROR");
		    	  break;
		      case MSG_AUDIO_START_RECORDING_ERROR://音频采集出错
		    	  sendMessage(MSG_AUDIO_START_RECORDING_ERROR);
		    	  XLog.i("MSG_AUDIO_START_RECORDING_ERROR");
		    	  break;
		      case MSG_OTHER_AUDIO_PROCESS_ERROR://音频操作的其他错误
		    	  sendMessage(MSG_OTHER_AUDIO_PROCESS_ERROR);
		    	  XLog.i("MSG_OTHER_AUDIO_PROCESS_ERROR");
		    	  break;
		      case MSG_QOS_TO_STOP_LIVESTREAMING://网络QoS极差，码率档次降到最低
		    	  XLog.i("MSG_QOS_TO_STOP_LIVESTREAMING");
//		    	  m_tryToStopLivestreaming = true;
//		    	  m_QoSToStopLivestreaming = true;
//		  		  mLSMediaCapture.stopLiveStreaming();
		    	  break;
		      case MSG_HW_VIDEO_PACKET_ERROR://视频硬件编码出错
		    	  if(m_liveStreamingOn)
		    	  {
		    		  sendMessage(MSG_HW_VIDEO_PACKET_ERROR);
		    		  XLog.i("MSG_HW_VIDEO_PACKET_ERROR");
		    	  }
		    	  break;
		      case MSG_CAMERA_PREVIEW_SIZE_NOT_SUPPORT_ERROR://camera采集分辨率不支持
		    	  sendMessage(MSG_CAMERA_PREVIEW_SIZE_NOT_SUPPORT_ERROR);
		    	  XLog.i("MSG_CAMERA_PREVIEW_SIZE_NOT_SUPPORT_ERROR");
		    	  break;
		      case MSG_START_PREVIEW_FINISHED://camera采集预览完成
		    	  XLog.i("MSG_START_PREVIEW_FINISHED");
		    	  startAV();
		    	  break;
		      case MSG_START_LIVESTREAMING_FINISHED://开始直播完成
		    	  XLog.i("MSG_START_LIVESTREAMING_FINISHED");
		    	  break;
		      case MSG_STOP_LIVESTREAMING_FINISHED://停止直播完成
		    	  XLog.i("MSG_STOP_LIVESTREAMING_FINISHED");
		    	  sendMessage(MSG_STOP_LIVESTREAMING_FINISHED);
	              break;
		      case MSG_STOP_VIDEO_CAPTURE_FINISHED:
		    	  XLog.i("MSG_STOP_VIDEO_CAPTURE_FINISHED");
		    	  if(mLSMediaCapture != null)
		    	  {
		    	      //继续视频推流，推最后一帧图像
		    	      mLSMediaCapture.resumeVideoEncode();
		    	  }
		    	  break;
		      case MSG_STOP_RESUME_VIDEO_CAPTURE_FINISHED:
		    	  XLog.i("MSG_STOP_RESUME_VIDEO_CAPTURE_FINISHED");
		    	  //开启视频preview
		    	  if(mLSMediaCapture != null)
		    	  {
		              mLSMediaCapture.resumeVideoPreview();
		              m_liveStreamingOn = true;
		    	      //开启视频推流，推正常帧
		              mLSMediaCapture.startVideoLiveStream();
		    	  }
		    	  break;
		      case MSG_STOP_AUDIO_CAPTURE_FINISHED:
		    	  XLog.i("MSG_STOP_AUDIO_CAPTURE_FINISHED");
		    	  if( mLSMediaCapture != null)
		    	  {
		    	      //继续音频推流，推静音帧
		    	      mLSMediaCapture.resumeAudioEncode();
		    	  }
		    	  break;
		      case MSG_STOP_RESUME_AUDIO_CAPTURE_FINISHED:
		    	  XLog.i("MSG_STOP_RESUME_AUDIO_CAPTURE_FINISHED");
		    	  //开启音频推流，推正常帧
		          mLSMediaCapture.startAudioLiveStream();
		    	  break;
		      case MSG_SWITCH_CAMERA_FINISHED://切换摄像头完成
		    	  XLog.i("MSG_SWITCH_CAMERA_FINISHED");
		    	  int cameraId = (Integer) object;//切换之后的camera id
		    	  break;
		      case MSG_SEND_STATICS_LOG_FINISHED://发送统计信息完成
		    	  XLog.i("MSG_SEND_STATICS_LOG_FINISHED");
		    	  break;
		      case MSG_SERVER_COMMAND_STOP_LIVESTREAMING:
		    	  XLog.i("MSG_SERVER_COMMAND_STOP_LIVESTREAMING");
		    	  break;
		      case MSG_GET_STATICS_INFO:
		    	  XLog.i("MSG_GET_STATICS_INFO");
		  }
	}
	
	private void sendMessage(int what)
	{
		Message msg = new Message();
		msg.what = what;
		handler.sendMessageDelayed(msg, 50);
	}
}
