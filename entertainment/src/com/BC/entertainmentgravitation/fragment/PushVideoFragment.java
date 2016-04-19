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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

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
 * ����ֱ���䣬������Ƶ
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
	
	private boolean mHardWareEncEnable = false;//�Ƿ�֧���˾�ģʽ
	
	private CameraSurfaceView mCameraSurfaceView;
	
    private LiveSurfaceView mVideoView;
	
    //��ѯ����ͷ֧�ֵĲɼ��ֱ�����Ϣ��ر���
    private Thread mCameraThread;
    
    private Looper mCameraLooper;
    
    private int mCameraID = CAMERA_POSITION_FRONT;//Ĭ�ϲ�ѯ���Ǻ�������ͷ
    
    private Camera mCamera;
    
	//SDKͳ����ر���
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
	
	private final int LS_LOG_QUIET       = 0x00;            //!< log���ģʽ�������
    private final int LS_LOG_ERROR       = 1 << 0;          //!< log���ģʽ���������
    private final int LS_LOG_WARNING     = 1 << 1;          //!< log���ģʽ���������
    private final int LS_LOG_INFO        = 1 << 2;          //!< log���ģʽ�������Ϣ
    private final int LS_LOG_DEBUG       = 1 << 3;          //!< log���ģʽ�����������Ϣ
    private final int LS_LOG_DETAIL      = 1 << 4;          //!< log���ģʽ�������ϸ
    private final int LS_LOG_RESV        = 1 << 5;          //!< log���ģʽ������
    private final int LS_LOG_LEVEL_COUNT = 6;
    private final int LS_LOG_DEFAULT     = LS_LOG_WARNING;	//!< log���ģʽ��Ĭ���������
	
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
		
		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //Ӧ������ʱ��������Ļ������������
		
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
		
//        //�л�ǰ������ͷ��ť��ʼ��
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
    	
	    //����ֱ��ʵ��
        mLSMediaCapture = new lsMediaCapture(this, getActivity(), mVideoPreviewWidth, mVideoPreviewHeight);

		if(mHardWareEncEnable)
		{
		    mCameraSurfaceView.setPreviewSize(mVideoPreviewWidth, mVideoPreviewHeight);
        }
		else
		{
		    mVideoView.setPreviewSize(mVideoPreviewWidth, mVideoPreviewHeight);
		}
		
        //��������ʵ��
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
        
        //����ͳ�����ݵ�������Ϣ����
//        staticsHandle();
    	
        if(mLSMediaCapture != null) {  
        	boolean ret = false;
        	
        	//��������ͷ��Ϣ������ʼ������ƵԤ��
        	mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition.cameraPosition = CAMERA_POSITION_BACK;//Ĭ�Ϻ�������ͷ���û����Ը�����Ҫ����
            if(mHardWareEncEnable)
			{
			    mLSMediaCapture.startVideoPreviewOpenGL(mCameraSurfaceView, mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition.cameraPosition);
            }
			else
			{
			    mLSMediaCapture.startVideoPreview(mVideoView, mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition.cameraPosition);
			}

            //��������Ƶ��camera����
            paraSet();
            
            //������־����
        	mLSMediaCapture.setTraceLevel(LS_LOG_ERROR);
        	
            //��ʼ��ֱ������
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
	
	//����Ƶ��������
	public void paraSet(){
		
		//�����ʽ����Ƶ����Ƶ������Ƶ
		mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType = HAVE_AV;

		//�����װ��ʽ
		mLSLiveStreamingParaCtx.eOutFormatType.outputFormatType = RTMP;
		
        //����ͷ��������
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.interfaceOrientation.interfaceOrientation = CAMERA_ORIENTATION_PORTRAIT;//����
        
        //��Ƶ�����������
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.samplerate = 44100;
    	mLSLiveStreamingParaCtx.sLSAudioParaCtx.bitrate = 64000;
    	mLSLiveStreamingParaCtx.sLSAudioParaCtx.frameSize = 2048;
    	mLSLiveStreamingParaCtx.sLSAudioParaCtx.audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    	mLSLiveStreamingParaCtx.sLSAudioParaCtx.channelConfig = AudioFormat.CHANNEL_IN_MONO;	
    	mLSLiveStreamingParaCtx.sLSAudioParaCtx.codec.audioCODECType = LS_AUDIO_CODEC_AAC;

    	//Ӳ�������������
        mLSLiveStreamingParaCtx.eHaraWareEncType.hardWareEncEnable = mHardWareEncEnable;	
        
        //��Ƶ����������ã���Ƶ���ʿ������û��������ã���Ƶ�ֱ��ʰ������±������
        //�ɼ��ֱ���     ����ֱ���     ��������
        //1280x720     1280x720     1500kbps
        //1280x720     960x540      800kbps
        //960x720      960x720      1000kbps
        //960x720      960x540      800kbps
        //960x540      960x540      800kbps
        //640x480      640x480      600kbps
        //640x480      640x360      500kbps
        //320x240      320x240      250kbps
        //320x240      320x180      200kbps
        //�����Ǳ���ֱ��ʵ���Ϣ������
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
	
	//��ʼֱ��
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
	
	//��ѯAndroid����ͷ֧�ֵĲ����ֱ�����ط�����4��
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
	
    //��ѯAndroid����ͷ֧�ֵĲ����ֱ�����ط�����1��
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
	
	//��ѯAndroid����ͷ֧�ֵĲ����ֱ�����ط�����2��
	public void lockCamera() {
		try {
			mCamera.reconnect();
		} catch (Exception e) {
		}
	}
	
	//��ѯAndroid����ͷ֧�ֵĲ����ֱ�����ط�����3��
	public void releaseCamera() {
		if (mCamera != null) {
			lockCamera();
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}		
	}
	
    //�л�ǰ������ͷ
	private void switchCamera() {
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
        
        //ֱֹͣ���������API�ӿ�
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
	
	//�����л�ǰ̨��ָ���Ƶ����  ��Ҫ��activity onRestart()��������
	public void StopVideoEncode()
	{
        if(mLSMediaCapture != null) {
        	//�ر������̶�ͼ��
        	mLSMediaCapture.stopVideoEncode();
        }  
	}
	
	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
	}

	@Override
	public void onClick(View v) {
		
	}

	   //����SDK���������쳣���¼����û���Ҫ���������������Ϣ��������Ӧ�Ĵ���
    //�������������Ϣ���û����ݶ�����Ϣ����ֱ������
	@Override
	public void handleMessage(int msg, Object object) {
		  switch (msg) {
		      case MSG_INIT_LIVESTREAMING_OUTFILE_ERROR://��ʼ��ֱ������
		      case MSG_INIT_LIVESTREAMING_VIDEO_ERROR:	
		      case MSG_INIT_LIVESTREAMING_AUDIO_ERROR:
		    	  if(m_liveStreamingInit)
		    	  {
		    		  sendMessage(MSG_INIT_LIVESTREAMING_OUTFILE_ERROR);
	      		      XLog.i("MSG_INIT_LIVESTREAMING_AUDIO_ERROR");
		    	  }
		    	  break;
		      case MSG_START_LIVESTREAMING_ERROR://��ʼֱ������
		    	  sendMessage(MSG_START_LIVESTREAMING_ERROR);
		    	  XLog.i("MSG_START_LIVESTREAMING_ERROR");
		    	  break;
		      case MSG_STOP_LIVESTREAMING_ERROR://ֱֹͣ������
		    	  if(m_liveStreamingOn)
		    	  {
		    		  sendMessage(MSG_STOP_LIVESTREAMING_ERROR);
	      		    XLog.i("MSG_STOP_LIVESTREAMING_ERROR");
		    	  }
		    	  break;
		      case MSG_AUDIO_PROCESS_ERROR://��Ƶ�������
		    	  if(m_liveStreamingOn && System.currentTimeMillis() - mLastAudioProcessErrorAlertTime  >= 10000)
		    	  {
	      		      mLastAudioProcessErrorAlertTime = System.currentTimeMillis();
	      		      sendMessage(MSG_AUDIO_PROCESS_ERROR);
		    	  }

		    	  XLog.i("MSG_AUDIO_PROCESS_ERROR");
		    	  
		    	  break;
		      case MSG_VIDEO_PROCESS_ERROR://��Ƶ�������
		      {
		    	  if(m_liveStreamingOn && System.currentTimeMillis() - mLastVideoProcessErrorAlertTime  >= 10000)
		    	  {
	      		      mLastVideoProcessErrorAlertTime = System.currentTimeMillis();
	      		      sendMessage(MSG_VIDEO_PROCESS_ERROR);
		    	  }
		    	  XLog.i("MSG_VIDEO_PROCESS_ERROR");
		    	  break;
		      }
		      case MSG_RTMP_URL_ERROR://������Ϣ
		    	  if (mLSMediaCapture != null)
		    	  {
			    	  mLSMediaCapture.stopLiveStreaming();
			    	  sendMessage(MSG_RTMP_URL_ERROR);
		    	  }
		    	  XLog.i("MSG_RTMP_URL_ERROR");
		    	  break;
		      case MSG_URL_NOT_AUTH://ֱ��URL�Ƿ�
		    	  if(m_liveStreamingInit)
		    	  {
			    	  sendMessage(MSG_URL_NOT_AUTH);
		    	  }
		    	  XLog.i("MSG_URL_NOT_AUTH");
		    	  break;
		      case MSG_SEND_STATICS_LOG_ERROR://����ͳ����Ϣ����
		    	  sendMessage(MSG_SEND_STATICS_LOG_ERROR);
		    	  XLog.i("MSG_SEND_STATICS_LOG_ERROR");
		    	  break;
		      case MSG_SEND_HEARTBEAT_LOG_ERROR://����������Ϣ����
		    	  sendMessage(MSG_SEND_HEARTBEAT_LOG_ERROR);
		    	  XLog.i("MSG_SEND_HEARTBEAT_LOG_ERROR");
		    	  break;
		      case MSG_AUDIO_SAMPLE_RATE_NOT_SUPPORT_ERROR://��Ƶ�ɼ�������֧��
		    	  sendMessage(MSG_AUDIO_SAMPLE_RATE_NOT_SUPPORT_ERROR);
		    	  //Log.i(TAG, "test: in handleMessage, MSG_AUDIO_SAMPLE_RATE_NOT_SUPPORT_ERROR");
		    	  XLog.i("MSG_AUDIO_SAMPLE_RATE_NOT_SUPPORT_ERROR");
		    	  break;
		      case MSG_AUDIO_PARAMETER_NOT_SUPPORT_BY_HARDWARE_ERROR://��Ƶ������֧��
		    	  sendMessage(MSG_AUDIO_PARAMETER_NOT_SUPPORT_BY_HARDWARE_ERROR);
		    	  XLog.i("MSG_AUDIO_PARAMETER_NOT_SUPPORT_BY_HARDWARE_ERROR");
		    	  break;
		      case MSG_NEW_AUDIORECORD_INSTANCE_ERROR://��Ƶʵ����ʼ������
		    	  sendMessage(MSG_NEW_AUDIORECORD_INSTANCE_ERROR);
		    	  XLog.i("MSG_NEW_AUDIORECORD_INSTANCE_ERROR");
		    	  break;
		      case MSG_AUDIO_START_RECORDING_ERROR://��Ƶ�ɼ�����
		    	  sendMessage(MSG_AUDIO_START_RECORDING_ERROR);
		    	  XLog.i("MSG_AUDIO_START_RECORDING_ERROR");
		    	  break;
		      case MSG_OTHER_AUDIO_PROCESS_ERROR://��Ƶ��������������
		    	  sendMessage(MSG_OTHER_AUDIO_PROCESS_ERROR);
		    	  XLog.i("MSG_OTHER_AUDIO_PROCESS_ERROR");
		    	  break;
		      case MSG_QOS_TO_STOP_LIVESTREAMING://����QoS������ʵ��ν������
		    	  XLog.i("MSG_QOS_TO_STOP_LIVESTREAMING");
//		    	  m_tryToStopLivestreaming = true;
//		    	  m_QoSToStopLivestreaming = true;
//		  		  mLSMediaCapture.stopLiveStreaming();
		    	  break;
		      case MSG_HW_VIDEO_PACKET_ERROR://��ƵӲ���������
		    	  if(m_liveStreamingOn)
		    	  {
		    		  sendMessage(MSG_HW_VIDEO_PACKET_ERROR);
		    		  XLog.i("MSG_HW_VIDEO_PACKET_ERROR");
		    	  }
		    	  break;
		      case MSG_CAMERA_PREVIEW_SIZE_NOT_SUPPORT_ERROR://camera�ɼ��ֱ��ʲ�֧��
		    	  sendMessage(MSG_CAMERA_PREVIEW_SIZE_NOT_SUPPORT_ERROR);
		    	  XLog.i("MSG_CAMERA_PREVIEW_SIZE_NOT_SUPPORT_ERROR");
		    	  break;
		      case MSG_START_PREVIEW_FINISHED://camera�ɼ�Ԥ�����
		    	  XLog.i("MSG_START_PREVIEW_FINISHED");
		    	  startAV();
		    	  break;
		      case MSG_START_LIVESTREAMING_FINISHED://��ʼֱ�����
		    	  XLog.i("MSG_START_LIVESTREAMING_FINISHED");
		    	  break;
		      case MSG_STOP_LIVESTREAMING_FINISHED://ֱֹͣ�����
		    	  XLog.i("MSG_STOP_LIVESTREAMING_FINISHED");
		    	  sendMessage(MSG_STOP_LIVESTREAMING_FINISHED);
	              break;
		      case MSG_STOP_VIDEO_CAPTURE_FINISHED:
		    	  XLog.i("MSG_STOP_VIDEO_CAPTURE_FINISHED");
		    	  if(mLSMediaCapture != null)
		    	  {
		    	      //������Ƶ�����������һ֡ͼ��
		    	      mLSMediaCapture.resumeVideoEncode();
		    	  }
		    	  break;
		      case MSG_STOP_RESUME_VIDEO_CAPTURE_FINISHED:
		    	  XLog.i("MSG_STOP_RESUME_VIDEO_CAPTURE_FINISHED");
		    	  //������Ƶpreview
		    	  if(mLSMediaCapture != null)
		    	  {
		              mLSMediaCapture.resumeVideoPreview();
		              m_liveStreamingOn = true;
		    	      //������Ƶ������������֡
		              mLSMediaCapture.startVideoLiveStream();
		    	  }
		    	  break;
		      case MSG_STOP_AUDIO_CAPTURE_FINISHED:
		    	  XLog.i("MSG_STOP_AUDIO_CAPTURE_FINISHED");
		    	  if( mLSMediaCapture != null)
		    	  {
		    	      //������Ƶ�������ƾ���֡
		    	      mLSMediaCapture.resumeAudioEncode();
		    	  }
		    	  break;
		      case MSG_STOP_RESUME_AUDIO_CAPTURE_FINISHED:
		    	  XLog.i("MSG_STOP_RESUME_AUDIO_CAPTURE_FINISHED");
		    	  //������Ƶ������������֡
		          mLSMediaCapture.startAudioLiveStream();
		    	  break;
		      case MSG_SWITCH_CAMERA_FINISHED://�л�����ͷ���
		    	  XLog.i("MSG_SWITCH_CAMERA_FINISHED");
		    	  int cameraId = (Integer) object;//�л�֮���camera id
		    	  break;
		      case MSG_SEND_STATICS_LOG_FINISHED://����ͳ����Ϣ���
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
