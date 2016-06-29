package com.BC.entertainment.chatroom.module;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;

import com.BC.entertainment.cache.ChatCache;
import com.BC.entertainment.config.PushConfig;
import com.BC.entertainment.inter.ActivityCallback;
import com.BC.entertainment.view.LiveSurfaceView;
import com.netease.LSMediaCapture.lsMediaCapture;
import com.netease.LSMediaCapture.lsMediaCapture.LSLiveStreamingParaCtx;
import com.netease.LSMediaCapture.lsMessageHandler;
import com.summer.logger.XLog;

/**
 * 
 * @author wen zhong
 *
 */
public class LivePlayer implements lsMessageHandler{
	
	private ActivityCallback activityCallback;
	private LiveSurfaceView liveView;
	private lsMediaCapture mLSMediaCapture;//直播实例
	private LSLiveStreamingParaCtx mLSLiveStreamingParaCtx;
    private int mVideoEncodeWidth, mVideoEncodeHeight; // 推流分辨率
    private String mLogPath = null; //直播日志路径
    
    private boolean live = false; // 是否已经开始推流（断网重连用），推流没有暂停
	private boolean m_liveStreamingInitFinished = false;
	private boolean m_liveStreamingInit = false;
	private boolean m_liveStreamingOn = false;
	private boolean m_liveStreamingPause = false;
	private boolean mAlertServiceOn = false;
	private boolean m_tryToStopLiveStreaming = false;
	private Intent mAlertServiceIntent;
    
	public LivePlayer (LiveSurfaceView liveView, ActivityCallback activityCallback)
	{
		this.activityCallback = activityCallback;
		this.liveView = liveView;
		
        // 推流URL和分辨率信息
        mVideoEncodeWidth = 640;
        mVideoEncodeHeight = 360;
	}
	
    public void onActivityResume() {
        if (mLSMediaCapture != null) {
            //关闭推流固定图像
            mLSMediaCapture.stopVideoEncode();

            //关闭推流静音帧
            //mLSMediaCapture.stopAudioEncode();
        }
    }

    public void onActivityPause() {
        if (mLSMediaCapture != null) {
            //关闭视频Preview
            mLSMediaCapture.stopVideoPreview();

            if (m_tryToStopLiveStreaming) {
                m_liveStreamingOn = false;
            } else {
                //继续视频推流，推固定图像
                mLSMediaCapture.resumeVideoEncode();

                //释放音频采集资源
                //mLSMediaCapture.stopAudioRecord();
            }
        }
    }
	
    public boolean startStopLive() {
    	boolean ret = false;
    	
        if (!m_liveStreamingOn ) {
        	if (!m_liveStreamingPause ) {
        		if (!isNullOrEmpty(ChatCache.getInstance().getChatRoom().getPushUrl()))
        		{
        			initLiveParam();
                    // 如果音视频直播或者视频直播，需要等待preview finish之后才能开始直播；如果音频直播，则无需等待preview finish，可以立即开始直播
                    if (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == PushConfig.HAVE_AUDIO) {
                        startAV();
                    }
                    live = true;
                    ret = true;
                    liveView.setVisibility(View.VISIBLE);
        		}
        	}
        }
    	return ret;
    }
    
    //开始直播
    private void startAV() {
        if (mLSMediaCapture != null) {
            mLSMediaCapture.startLiveStreaming();
            m_liveStreamingOn = true;
            m_liveStreamingPause = false;
        }
    }
    
    //切换前后摄像头
    public void switchCamera() {
        if (mLSMediaCapture != null) {
            mLSMediaCapture.switchCamera();
        }
    }
    
    /**
     * 重启直播（例如：断网重连）
     * @return 是否开始重启
     */
    public boolean restartLive() {
        if (live) {
            // 必须是已经开始推流 才需要处理断网重新开始直播
            if (mLSMediaCapture != null) {
                mLSMediaCapture.initLiveStream(ChatCache.getInstance().getChatRoom().getPushUrl(), mLSLiveStreamingParaCtx);
                mLSMediaCapture.startLiveStreaming();
                m_tryToStopLiveStreaming = false;
                return true;
            }
        }
        return false;
    }

    /**
     * 停止直播（例如：断网了）
     */
    public void stopLive() {
    	m_tryToStopLiveStreaming = true;
        if (mLSMediaCapture != null) {
            mLSMediaCapture.stopLiveStreaming();
        }
    }
    
    public void tryStop() {
        m_tryToStopLiveStreaming  = true;
    }
    
    public void resetLive() {
        if (mLSMediaCapture != null && m_liveStreamingInitFinished) {

            mLSMediaCapture.stopLiveStreaming();
            mLSMediaCapture.stopVideoPreview();
            mLSMediaCapture.destroyVideoPreview();
            //反初始化推流实例
            mLSMediaCapture.uninitLsMediaCapture(false);
            mLSMediaCapture = null;

            m_liveStreamingInitFinished = false;
            m_liveStreamingOn = false;
            m_liveStreamingPause = false;
            m_tryToStopLiveStreaming = false;
        } else if (mLSMediaCapture !=null && !m_liveStreamingInitFinished) {
            //反初始化推流实例
            mLSMediaCapture.uninitLsMediaCapture(true);
        }

        if (m_liveStreamingInit) {
            m_liveStreamingInit = false;
        }

        if (mAlertServiceOn) {
            mAlertServiceIntent = new Intent(getActivity(), AlertService.class);
            getActivity().stopService(mAlertServiceIntent);
            mAlertServiceOn = false;
        }
    }

    // 设置推流参数
    private void initLiveParam() {
    	getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
        //创建直播实例
        mLSMediaCapture = new lsMediaCapture(this, getActivity(), mVideoEncodeWidth, mVideoEncodeHeight);
        liveView.setPreviewSize(mVideoEncodeWidth, mVideoEncodeHeight);
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
        
        //配置音视频和camera参数
        configLiveStream();
    }
    
    private void configLiveStream() {
        //输出格式：视频、音频和音视频
        mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType = PushConfig.HAVE_AV;
        //输出封装格式
        mLSLiveStreamingParaCtx.eOutFormatType.outputFormatType = PushConfig.RTMP;
        //摄像头参数配置
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition.cameraPosition = PushConfig.CAMERA_POSITION_FRONT;//后置摄像头
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.interfaceOrientation.interfaceOrientation = PushConfig.CAMERA_ORIENTATION_PORTRAIT;//竖屏
        //音频编码参数配置
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.samplerate = 44100;
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.bitrate = 64000;
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.frameSize = 2048;
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.channelConfig = AudioFormat.CHANNEL_IN_MONO;
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.codec.audioCODECType = PushConfig.LS_AUDIO_CODEC_AAC;
        
    	//硬件编码参数设置
        mLSLiveStreamingParaCtx.eHaraWareEncType.hardWareEncEnable = false;	
        
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.fps = 20;
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.bitrate = 600000;
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec.videoCODECType = PushConfig.LS_VIDEO_CODEC_AVC;
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.width = 640;
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.height = 360;
        
        //设置日志级别和日志文件路径
        getLogPath();
        
        mLSMediaCapture.startVideoPreview(liveView, mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition.cameraPosition);
        
        //初始化直播推流
        m_liveStreamingInitFinished = mLSMediaCapture.initLiveStream(ChatCache.getInstance().getChatRoom().getPushUrl(),
        		mLSLiveStreamingParaCtx);
        m_liveStreamingInit = true;
    }
    
    //获取日志文件路径
    public void getLogPath()
    {
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                mLogPath = Environment.getExternalStorageDirectory() + "/log/";
            }
            if(mLSMediaCapture != null) {
                mLSMediaCapture.setTraceLevel(PushConfig.LS_LOG_ERROR, mLogPath);
            }
        } catch (Exception e) {
            XLog.e(e.getMessage());
        }
    }
    
	private boolean isNullOrEmpty(String o)
	{
		if (o != null)
		{
			if (o.length() == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return true;
		}
	}
    
	@Override
	public void handleMessage(int msg, Object arg1) {
		  switch (msg) {
	      case lsMessageHandler.MSG_INIT_LIVESTREAMING_OUTFILE_ERROR://初始化直播出错
	      case lsMessageHandler.MSG_INIT_LIVESTREAMING_VIDEO_ERROR:	
	      case lsMessageHandler.MSG_INIT_LIVESTREAMING_AUDIO_ERROR:
//	    	  toastAndExit("初始化直播出错");
	    	  break;
	      case lsMessageHandler.MSG_START_LIVESTREAMING_ERROR://开始直播出错
//	    	  toastAndExit("开始直播出错");
	    	  break;
	      case lsMessageHandler.MSG_STOP_LIVESTREAMING_ERROR://停止直播出错
//	    	  toastAndExit("停止直播出错");
	    	  break;
	      case lsMessageHandler.MSG_AUDIO_PROCESS_ERROR://音频处理出错
//	    	  toastAndExit("音频处理出错");
	    	  break;
	      case lsMessageHandler.MSG_VIDEO_PROCESS_ERROR://视频处理出错
//	    	  if (m_liveStreamingOn && System.currentTimeMillis() - mLastVideoProcessErrorAlertTime >= 10000)
//	    	  {
//		    	  toastAndExit("视频处理出错");
//		    	  mLastVideoProcessErrorAlertTime = System.currentTimeMillis();
//	    	  }
	    	  break;
	      case lsMessageHandler.MSG_RTMP_URL_ERROR://断网消息
//	    	  toastAndExit("断网了，请检查网络连接");
	    	  break;
	      case lsMessageHandler.MSG_URL_NOT_AUTH://直播URL非法
//	    	  toastAndExit("直播地址URL非法，请检查");
	    	  break;
	      case lsMessageHandler.MSG_SEND_STATICS_LOG_ERROR://发送统计信息出错
//	    	  toastAndExit("发送统计信息出错");
	    	  break;
	      case lsMessageHandler.MSG_SEND_HEARTBEAT_LOG_ERROR://发送心跳信息出错
//	    	  toastAndExit("发送心跳信息出错");
	    	  break;
	      case lsMessageHandler.MSG_AUDIO_SAMPLE_RATE_NOT_SUPPORT_ERROR://音频采集参数不支持
//	    	  toastAndExit("音频采集参数不支持");
	    	  break;
	      case lsMessageHandler.MSG_AUDIO_PARAMETER_NOT_SUPPORT_BY_HARDWARE_ERROR://音频参数不支持
//	    	  toastAndExit("音频参数不支持");
	    	  break;
	      case lsMessageHandler.MSG_NEW_AUDIORECORD_INSTANCE_ERROR://音频实例初始化出错
//	    	  toastAndExit("音频实例初始化出错");
	    	  break;
	      case lsMessageHandler.MSG_AUDIO_START_RECORDING_ERROR://音频采集出错
//	    	  toastAndExit("音频采集出错");
	    	  break;
	      case lsMessageHandler.MSG_OTHER_AUDIO_PROCESS_ERROR://音频操作的其他错误
//	    	  toastAndExit("音频操作的其他错误");
	    	  break;
	      case lsMessageHandler.MSG_QOS_TO_STOP_LIVESTREAMING://网络QoS极差，码率档次降到最低
	    	  break;
	      case lsMessageHandler.MSG_HW_VIDEO_PACKET_ERROR://视频硬件编码出错
//	    	  toastAndExit("视频硬件编码出错");
	    	  break;
	      case lsMessageHandler.MSG_CAMERA_PREVIEW_SIZE_NOT_SUPPORT_ERROR://camera采集分辨率不支持
//	    	  toastAndExit("采集分辨率不支持");
	    	  break;
	      case lsMessageHandler.MSG_START_LIVESTREAMING_FINISHED://开始直播完成
	    	  activityCallback.onLiveStart();
	    	  break;
	      case lsMessageHandler.MSG_STOP_LIVESTREAMING_FINISHED://停止直播完成
//	    	  toastAndExit("停止直播完成");
              break;
		  }
	}

    private Activity getActivity() {
    	
        return activityCallback.getActivity();
    }
}
