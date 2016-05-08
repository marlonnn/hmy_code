package com.BC.entertainmentgravitation.fragment;

import java.util.List;
import java.util.concurrent.Semaphore;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioFormat;
import android.os.Bundle;
import android.os.Looper;
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
import com.netease.LSMediaCapture.lsMessageHandler;
import com.netease.livestreamingFilter.view.CameraSurfaceView;
import com.summer.fragment.BaseFragment;
import com.summer.logger.XLog;
import com.summer.utils.ToastUtil;

/**
 * 明星直播间，推送视频
 * @author wen zhong
 *
 */
public class PushVideoFragment extends BaseFragment implements View.OnClickListener, lsMessageHandler{

	private View rootView;
	
	private lsMediaCapture mLSMediaCapture = null;
	
	private boolean m_liveStreamingOn = false;
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
    
    private final int RTMP = 1;

	private final int HAVE_AV = 2;
	private final int CAMERA_ORIENTATION_PORTRAIT = 0;
	private final int CAMERA_ORIENTATION_LANDSCAPE = 1;
	
	private final int LS_VIDEO_CODEC_AVC = 0;
	
	private final int LS_AUDIO_CODEC_AAC = 0;
	
	private final int LS_LOG_QUIET       = 0x00;            //!< log输出模式：不输出
    private final int LS_LOG_ERROR       = 1 << 0;          //!< log输出模式：输出错误
    private final int LS_LOG_WARNING     = 1 << 1;          //!< log输出模式：输出警告
    private final int LS_LOG_INFO        = 1 << 2;          //!< log输出模式：输出信息
    private final int LS_LOG_DEBUG       = 1 << 3;          //!< log输出模式：输出调试信息
    private final int LS_LOG_DETAIL      = 1 << 4;          //!< log输出模式：输出详细
    private final int LS_LOG_RESV        = 1 << 5;          //!< log输出模式：保留
    private final int LS_LOG_LEVEL_COUNT = 6;
    private final int LS_LOG_DEFAULT     = LS_LOG_WARNING;	//!< log输出模式：默认输出警告
	
	private ChatRoom chatRoom;
	
    private ScrollListener listener;
    
    private ExitFragmentListener exitListener;
    
	public PushVideoFragment(ChatRoom chatRoom)
	{
		this.chatRoom = chatRoom;
	}
	
	public static PushVideoFragment newInstance(ChatRoom chatRoom)
	{
		PushVideoFragment f = new PushVideoFragment(chatRoom);
		return f;
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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof ExitFragmentListener)
		{
			this.exitListener = (ExitFragmentListener) getActivity();
		}
	}
    
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
		
    	cid = chatRoom.getCid();
    	mliveStreamingURL = chatRoom.getPushUrl();
    	chatroomid = chatRoom.getChatroomid();
    	mHardWareEncEnable = chatRoom.isFilter();
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
    	final int mVideoPreviewWidth = 640;
    	final int mVideoPreviewHeight = 480;
    	
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
        	mLSMediaCapture.setTraceLevel(LS_LOG_INFO);
        	
            //初始化直播推流
	        ret = mLSMediaCapture.initLiveStream(mliveStreamingURL, mLSLiveStreamingParaCtx);
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
	    	  toastAndExit("视频处理出错");
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
    	if(message != null && getActivity() != null)
    	{
        	ToastUtil.show(getActivity(), message);
    	}
    	if(exitListener != null)
    	{
    		exitListener.isExit(true);
    	}
    }
}
