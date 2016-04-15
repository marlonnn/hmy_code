package com.BC.entertainmentgravitation.fragment;

import java.util.List;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioFormat;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.BC.entertainment.view.LiveSurfaceView;
import com.BC.entertainmentgravitation.PushVideoActivity;
import com.BC.entertainmentgravitation.PushVideoActivity.TouchListener;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.netease.LSMediaCapture.lsMediaCapture;
import com.netease.LSMediaCapture.lsMessageHandler;
import com.netease.LSMediaCapture.lsMediaCapture.LSLiveStreamingParaCtx;
import com.netease.LSMediaCapture.lsMediaCapture.Statistics;
import com.netease.LSMediaCapture.lsMediaCapture.LSLiveStreamingParaCtx.HardWareEncEnable;
import com.netease.LSMediaCapture.lsMediaCapture.LSLiveStreamingParaCtx.LSAudioParaCtx;
import com.netease.LSMediaCapture.lsMediaCapture.LSLiveStreamingParaCtx.LSVideoParaCtx;
import com.netease.LSMediaCapture.lsMediaCapture.LSLiveStreamingParaCtx.OutputFormatType;
import com.netease.LSMediaCapture.lsMediaCapture.LSLiveStreamingParaCtx.OutputStreamType;
import com.netease.LSMediaCapture.lsMediaCapture.LSLiveStreamingParaCtx.LSAudioParaCtx.LSAudioCodecType;
import com.netease.LSMediaCapture.lsMediaCapture.LSLiveStreamingParaCtx.LSVideoParaCtx.CameraOrientation;
import com.netease.LSMediaCapture.lsMediaCapture.LSLiveStreamingParaCtx.LSVideoParaCtx.CameraPosition;
import com.netease.LSMediaCapture.lsMediaCapture.LSLiveStreamingParaCtx.LSVideoParaCtx.LSVideoCodecType;
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
	
	private ImageButton switchBtn;
	
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
	
	private float mCurrentDistance;
	
	private float mLastDistance;
	
	private ChatRoom chatRoom;
	
    private ScrollListener listener;
	
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
		
//        Bundle bundle = getArguments();
//        
//        if (bundle != null)
//        {
//        	cid = bundle.getString("cid");
//        	mliveStreamingURL = bundle.getString("pushUrl");
//        	chatroomid = bundle.getString("chatroomid");
//        	mHardWareEncEnable = bundle.getBoolean("filter", false);
//        }
        
		TouchListener myTouchListener = new TouchListener() {

			@Override
			public boolean onTouchEvent(MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// Log.i(TAG, "test: down!!!");
					break;
				case MotionEvent.ACTION_MOVE:
					// Log.i(TAG, "test: move!!!");
					/**
					 * 首先判断按下手指的个数是不是大于两个。 如果大于两个则执行以下操作（即图片的缩放操作）。
					 */
					if (event.getPointerCount() >= 2) {

						float offsetX = event.getX(0) - event.getX(1);
						float offsetY = event.getY(0) - event.getY(1);
						/**
						 * 原点和滑动后点的距离差
						 */
						mCurrentDistance = (float) Math.sqrt(offsetX * offsetX
								+ offsetY * offsetY);
						if (mLastDistance < 0) {
							mLastDistance = mCurrentDistance;
						} else {
							/**
							 * 如果当前滑动的距离（currentDistance）比最后一次记录的距离（lastDistance
							 * ）相比大于5英寸（也可以为其他尺寸）， 那么现实图片放大
							 */
							if (mCurrentDistance - mLastDistance > 5) {
								// Log.i(TAG, "test: 放大！！！");
								if (mLSMediaCapture != null) {
									mLSMediaCapture.setCameraZoomPara(true);
								}

								mLastDistance = mCurrentDistance;
								/**
								 * 如果最后的一次记录的距离（lastDistance）与当前的滑动距离（
								 * currentDistance）相比小于5英寸， 那么图片缩小。
								 */
							} else if (mLastDistance - mCurrentDistance > 5) {
								// Log.i(TAG, "test: 缩小！！！");
								if (mLSMediaCapture != null) {
									mLSMediaCapture.setCameraZoomPara(false);
								}

								mLastDistance = mCurrentDistance;
							}
						}
					}
					break;
				case MotionEvent.ACTION_UP:
					// Log.i(TAG, "test: up!!!");
					break;
				default:
					break;
				}
				return true;
			}
		};
		
		// 将myTouchListener注册到分发列表  
		((PushVideoActivity) getActivity()).registerMyTouchListener(myTouchListener);    
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
		initializeLive();
	}
	
	public void initializeView(View view)
	{
		rootView = view.findViewById(R.id.layout_root);
		
        //切换前后摄像头按钮初始化
        switchBtn = (ImageButton)view.findViewById(R.id.switchBtn);	      
        switchBtn.setOnClickListener(new OnClickListener() {
 			@Override
 			public void onClick(View v) {
 				switchCamera();
 			}
 		});
        
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
//        	mLSMediaCapture.setTraceLevel(mLogLevel);
        	
            //初始化直播推流
	        ret = mLSMediaCapture.initLiveStream(mliveStreamingURL, mLSLiveStreamingParaCtx);
	        
	        if(ret) {
	        	m_liveStreamingInit = true;
	        	m_liveStreamingInitFinished = true;
	        }
	        else {
	        	m_liveStreamingInit = true;
	        	m_liveStreamingInitFinished = false;
	        }
        }
	}
	
	//音视频参数设置
	public void paraSet(){	

//        //滤镜模式下不开视频水印
//		if(!mLSLiveStreamingParaCtx.eHaraWareEncType.hardWareEncEnable && mWaterMarkOn) {
//		    waterMark();
//		}
		
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
	private void switchCamera() {
		if(mLSMediaCapture != null) {
			mLSMediaCapture.switchCamera();
		}
	}
		  
	@Override
    public void onPause(){  
//        if(mLSMediaCapture != null) {  		
//    		if(!m_tryToStopLivestreaming)
//    		{  			
//        		//继续视频推流，推固定图像
//        		mLSMediaCapture.resumeVideoEncode();
//        		
//        		//释放音频采集资源
//        		//mLSMediaCapture.stopAudioRecord();
//    		}
//        }
		mLSMediaCapture.resumeVideoEncode();
        super.onPause(); 
    }  
      
//    public void onRestart(){  
//    	super.onRestart(); 
//        if(mLSMediaCapture != null) {
//        	//关闭推流固定图像
//        	mLSMediaCapture.stopVideoEncode();
//            
//            //关闭推流静音帧
//            //mLSMediaCapture.stopAudioEncode();
//        }        
//        
//    }  
      
    @Override
	public void onResume(){   
        super.onResume(); 
    }  
      
    @Override
	public void onStart(){  
        super.onStart();  
    }  
      
    @Override
	public void onStop(){  
        super.onStop();  
    }
    
	
	@Override
	public void onDestroy() {
		if(m_liveStreamingInit) {
			m_liveStreamingInit = false;
		}
		
//		//Demo层网络信息显示操作的销毁
//        if(mNetworkInfoServiceOn) {       	
//            mNetInfoIntent.putExtra("frameRate", 0);  
//        	mNetInfoIntent.putExtra("bitRate", 0);
//      	    mNetInfoIntent.putExtra("resolution", 2);
//         
//            sendBroadcast(mNetInfoIntent);  
//
//            stopService(mNetinfoIntent); 
//            mNetworkInfoServiceOn = false;
//        }
//        
//        //Demo层报警信息操作的销毁
//        if(mAlertServiceOn) {
//        	mAlertServiceIntent = new Intent(MediaPreviewActivity.this, AlertService.class);
//            stopService(mAlertServiceIntent); 
//            mAlertServiceOn = false;
//        }
        
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
	
//	@Override  
//    public void onBackPressed() {  
//        super.onBackPressed();  
//        m_tryToStopLivestreaming = true;      
//    }  
	
	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
	}

	@Override
	public void handleMessage(int arg0, Object arg1) {
		
	}

	@Override
	public void onClick(View v) {
		
	}

}
