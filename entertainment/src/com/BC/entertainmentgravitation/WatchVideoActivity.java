package com.BC.entertainmentgravitation;


import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.BC.entertainmentgravitation.entity.EditPersonal;
import com.BC.entertainmentgravitation.entity.VideoStatus;
import com.BC.entertainmentgravitation.fragment.ExitFragmentListener;
import com.BC.entertainmentgravitation.fragment.ScrollListener;
import com.BC.entertainmentgravitation.fragment.SurfaceFragment;
import com.BC.entertainmentgravitation.fragment.TopSurfaceFragment;
import com.BC.entertainmentgravitation.fragment.WatchVideoFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomKickOutEvent;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomStatusChangeData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.logger.XLog;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.StringUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

/**
 * 看明星直播
 * @author zhongwen
 *
 */
public class WatchVideoActivity extends BaseActivity implements ExitFragmentListener{
	
	public View rootView;
	private ScrollListener listener;

	private ChatRoom chatRoom;
	
    private AbortableFuture<EnterChatRoomResultData> enterRequest;//聊天室

	private ChatRoomInfo roomInfo;
	
	private Handler handler;
	
	private WatchVideoFragment fragment;
	
	protected final Handler getHandler() {
        if (handler == null) {
            handler = new Handler(getMainLooper()){

				@Override
				public void handleMessage(Message msg) {
				}
            	
            };
        }
        return handler;
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_back);
        
        rootView = findViewById(R.id.root_content);
        
        Intent intent = getIntent();
        if (intent != null)
        {
        	chatRoom = new ChatRoom();
        	try {
				chatRoom.setCid(intent.getStringExtra("cid"));
				chatRoom.setChatroomid(intent.getStringExtra("chatroomid"));
				chatRoom.setHttpPullUrl(intent.getStringExtra("httpPullUrl"));
				chatRoom.setMaster(intent.getBooleanExtra("isMaster", false));
			} catch (Exception e) {
				Toast.makeText(this, "直播间错误", Toast.LENGTH_LONG).show();
				e.printStackTrace();
				finish();
			}
        }
//        queryVideoStatus();
        
        handler = getHandler();
        
		enterChatRoom();
		registerObservers(true);
    }
    
    private void queryVideoStatus()
    {
    	if (chatRoom == null || chatRoom.getCid() == null)
    	{
			ToastUtil.show(this, "直播间不在直播中，请稍后重试");
			return;
    	}
    	
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("cid", chatRoom.getCid());
		ShowProgressDialog("查询直播中...");
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.query_video_status, "send search request", params);
    }
    
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
    
    private void initializeWatchVideoFragment()
    {
        fragment = new WatchVideoFragment(chatRoom);
        fragment.setHandler(handler);
        listener = fragment.CreateScrollListener();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.layout_video_play, fragment)
                .commit();
        new SurfaceFragment(listener, chatRoom, true).show(getSupportFragmentManager(), "watch video");
    }
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		logoutChatRoom();
		registerObservers(false);
	}
    
	private void registerObservers(boolean register) {
		NIMClient.getService(ChatRoomServiceObserver.class).observeOnlineStatus(onlineStatus, register);
		NIMClient.getService(ChatRoomServiceObserver.class).observeKickOutEvent(kickOutObserver, register);
	}

	@SuppressWarnings("serial")
	Observer<ChatRoomStatusChangeData> onlineStatus = new Observer<ChatRoomStatusChangeData>() {
		@Override
		public void onEvent(ChatRoomStatusChangeData chatRoomStatusChangeData) {
			
            if (chatRoomStatusChangeData.status == StatusCode.CONNECTING) {
                DialogMaker.updateLoadingMessage(StringUtil.getXmlResource(WatchVideoActivity.this, R.string.push_video_nim_status_connecting));
            } else if (chatRoomStatusChangeData.status == StatusCode.UNLOGIN) {
                Toast.makeText(WatchVideoActivity.this, R.string.push_video_nim_status_unlogin, Toast.LENGTH_SHORT).show();
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINING) {
                DialogMaker.updateLoadingMessage(StringUtil.getXmlResource(WatchVideoActivity.this, R.string.push_video_nim_status_logining));
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINED) {
//                if (fragment != null) {
//                    fragment.updateOnlineStatus(true);
//                }
            } else if (chatRoomStatusChangeData.status.wontAutoLogin()) {
            } else if (chatRoomStatusChangeData.status == StatusCode.NET_BROKEN) {
//                if (fragment != null) {
//                    fragment.updateOnlineStatus(false);
//                }
                Toast.makeText(WatchVideoActivity.this, R.string.push_video_net_broken, Toast.LENGTH_SHORT).show();
            }
			XLog.i("Chat Room Online Status:"+ chatRoomStatusChangeData.status.name());
		}
	};

	@SuppressWarnings("serial")
	Observer<ChatRoomKickOutEvent> kickOutObserver = new Observer<ChatRoomKickOutEvent>() {
		@Override
		public void onEvent(ChatRoomKickOutEvent chatRoomKickOutEvent) {
            Toast.makeText(WatchVideoActivity.this, 
            		StringUtil.getXmlResource(WatchVideoActivity.this, R.string.push_video_kick_out) + chatRoomKickOutEvent.getReason(), 
            		Toast.LENGTH_SHORT).show();
			clearChatRoom();
		}
	};

	@SuppressWarnings("unchecked")
	private void enterChatRoom() {
		EnterChatRoomData data = new EnterChatRoomData(chatRoom.getChatroomid());
		enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoom(data);
		enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>() {

					@Override
					public void onException(Throwable exception) {
						onLoginDone();
						XLog.i("enter chat room exception, e="+ exception.getMessage());
						Toast.makeText(WatchVideoActivity.this,
								"enter chat room exception, e="+ exception.getMessage(),Toast.LENGTH_SHORT).show();
						finish();
					}

					@Override
					public void onFailed(int code) {
						onLoginDone();
						if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
							Toast.makeText(WatchVideoActivity.this,
									"你已被拉入黑名单，不能再进入", Toast.LENGTH_SHORT).show();
						} else {
							XLog.i("enter chat room failed, code=" + code);
							Toast.makeText(WatchVideoActivity.this,
									"enter chat room failed, code=" + code,Toast.LENGTH_SHORT).show();
						}
						finish();
					}

					@Override
					public void onSuccess(EnterChatRoomResultData result) {
						roomInfo = result.getRoomInfo();
						final ChatRoomMember member = result.getMember();
						member.setExtension(roomInfo.getExtension());
						XLog.i("extension: " + roomInfo.getExtension());
						member.setRoomId(roomInfo.getRoomId());
						XLog.i("enter chat room success" + roomInfo.getRoomId());
						initializeWatchVideoFragment();

					}
				});
	}

	private void onLoginDone() {
		enterRequest = null;
		DialogMaker.dismissProgressDialog();
	}

	private void logoutChatRoom() {
		clearChatRoom();
	}

	public void clearChatRoom() {
		 finish();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
	
	@Override
	public void isExit(boolean exit) {
		if (exit)
		{
			finish();
		}
		
	}

	@SuppressWarnings("unused")
	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		Gson gson = new Gson();
		switch(taskType)
		{

		case Config.query_video_status:
			
			if (jsonString != null)
			{
				try {
					JSONObject jsonObj = new JSONObject(jsonString);
					String data =  jsonObj.getString("data");
					int status =   jsonObj.getInt("status");
					if (status == 0 && data != null)
					{
						JSONObject ret = jsonObj.getJSONObject("data").getJSONObject("ret"); 
						if (ret != null)
						{
							if( ret.getInt("status") == 0)
							{
								enterChatRoom();
								registerObservers(true);
							}
							else
							{
								Toast.makeText(WatchVideoActivity.this, "主播不在直播间，请稍后再试", Toast.LENGTH_SHORT).show();
								finish();
							}
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
					XLog.e("JSONException");
					finish();
				} 

			}
			break;
		}
	}
}
