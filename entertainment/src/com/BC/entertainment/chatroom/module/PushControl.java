package com.BC.entertainment.chatroom.module;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.view.View;

import com.BC.entertainment.inter.ActivityCallback;
import com.BC.entertainment.task.ThreadUtil;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.logger.XLog;
import com.summer.task.HttpTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.UrlUtil;

public class PushControl {

    private HttpTask httpTask;//更新娱票线程
    
	private ActivityCallback activityCallback;
	private View rootView;
	private InfoHandler handler;
	private Container_new container;
	
	//module control
	private InputControl inputControl;
	
	public PushControl (ActivityCallback activityCallback, InfoHandler handler, View rootView)
	{
		this.activityCallback = activityCallback;
		this.handler = handler;
		this.rootView = rootView;
		container = new Container_new(activityCallback, SessionTypeEnum.ChatRoom);
		
		inputControl = new InputControl (container, rootView);
//		inputControl.setAmountMoney(money)
	}
	
	/**
	 * 开始获取娱票线程
	 */
	public void startGetYuPiao()
	{
    	try {
			HashMap<String, String> entity = new HashMap<String, String>();
			entity.put("username", Config.User.getUserName());
			List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
			httpTask = new HttpTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, "get yu piao info", params, UrlUtil.GetUrl(Config.query_piao));
			httpTask.setTaskType(Config.query_piao);
			httpTask.setInfoHandler(handler);
			ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e("start get yu piao exception");
		}
	}
	
	/**
	 * 停止获取娱票线程
	 */
	public void stopUpdateYuPiao()
	{
		httpTask.CancelTask();
	}

    /**
     * 主播进入聊天室更新聊天室状态
     * @param master
     * @param isLeave
     */
    public void UpdateVideoStatus(boolean isLeave)
    {
    	//是主播进入聊天室才发送聊天室状态到后台
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("username", Config.User.getUserName());
    	if(isLeave){
    		entity.put("status", "1");
    	}
    	else
    	{
        	entity.put("status", "0");
    	}
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		ThreadUtil.AddToThreadPool(Config.update_status, "send update status request", params, handler);
    }
    
	public InputControl getInputControl() {
		return inputControl;
	}

}
