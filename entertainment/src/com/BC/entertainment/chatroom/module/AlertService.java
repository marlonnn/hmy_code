package com.BC.entertainment.chatroom.module;

import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.TextView;

public class AlertService extends Service {
	/**
	 * 定义浮动窗口布局
	 */
	Dialog mDialog;
	/**
	 * 悬浮窗的布局
	 */
	WindowManager.LayoutParams wmParams;
	LayoutInflater inflater;

    public static Boolean isShown = false;

    private TextView mAlertTV;
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO 自动生成的方法存根
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO 自动生成的方法存根
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle bundle = intent.getExtras();
		String alertString = bundle.getString("alert");
		
		initWindow(alertString);
		return START_NOT_STICKY;//super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mDialog != null){
			mDialog.dismiss();
		}
	}

	
	/**
	 * 初始化
	 */
	private void initWindow(String alertString) {
		if(alertString.equals("MSG_INIT_LIVESTREAMING_ERROR"))
		{
//			mAlertTV.setText("直播初始化出错，请检查后重新开始直播");
		}
		/*
		else if(alertString.equals("MSG_STOP_LIVESTREAMING_ERROR"))
		{
			mAlertTV.setText("停止直播出错，请退出程序后重新开始直播");
			bShowAlert = true;
		}
		else if(alertString.equals("MSG_AUDIO_PROCESS_ERROR"))
		{
			mAlertTV.setText("音频编码打包发送线程出错，请检查音频相关配置参数后重新开始直播");
			bShowAlert = true;
		}
		else if(alertString.equals("MSG_VIDEO_PROCESS_ERROR"))
		{
			mAlertTV.setText("视频编码打包发送线程出错，请检查视频相关配置参数后重新开始直播");
			bShowAlert = true;
		}
		else if(alertString.equals("MSG_RTMP_URL_ERROR"))
		{
			mAlertTV.setText("当前RTMP服务器断开，请退出直播后重新开始直播");
			bShowAlert = true;
		}
		*/
		else if(alertString.equals("MSG_URL_NOT_AUTH"))
		{
			mAlertTV.setText("直播URL非法，请输入正确的URL后重新开始直播");
		}
		/*
		else if(alertString.equals("MSG_QOS_TO_STOP_LIVESTREAMING"))
		{
			mAlertTV.setText("当前网络质量较差，请退出直播后重新开始直播");
			bShowAlert = true;
		}
		*/
		else if(alertString.equals("MSG_HW_VIDEO_PACKET_ERROR"))
		{
			mAlertTV.setText("视频硬件编码出错，请退出直播后重新开始直播，或者切换到视频软件编码");
		}
		/*
		else if(alertString.equals("MSG_SEND_BITRATE_ERROR"))
		{
			mAlertTV.setText("视频发送码率为0kbps，请检查网络连接状况后重新开始直播");
		}
		*/
	}

}
