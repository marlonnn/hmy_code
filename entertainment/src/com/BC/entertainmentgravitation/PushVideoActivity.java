package com.BC.entertainmentgravitation;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.BC.entertainment.cache.ChatRoomCache;
import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.BC.entertainmentgravitation.entity.Member;
import com.BC.entertainmentgravitation.fragment.ExitFragmentListener;
import com.BC.entertainmentgravitation.fragment.PushVideoFragment;
import com.BC.entertainmentgravitation.fragment.ScrollListener;
import com.BC.entertainmentgravitation.fragment.SurfaceFragment;
import com.BC.entertainmentgravitation.fragment.TopSurfaceFragment.SwitchCamera;
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
import com.summer.config.Config;
import com.summer.logger.XLog;
import com.summer.utils.StringUtil;

/**
 * 明星直播
 * @author zhongwen
 *
 */
public class PushVideoActivity extends FragmentActivity implements ExitFragmentListener, SwitchCamera {
	
	private View rootView;
	
	private ScrollListener listener;
	
	private PushVideoFragment fragment;
	
	private ChatRoom chatRoom;
    
    private AbortableFuture<EnterChatRoomResultData> enterRequest;//聊天室
    
    private ChatRoomInfo roomInfo;
    
    private boolean hasPush = false;
    
	public interface TouchListener
	{
		boolean onTouchEvent(MotionEvent event);  
	}
	
	// 保存MyTouchListener接口的列表  
	private ArrayList<TouchListener> myTouchListeners = new ArrayList<TouchListener>();
	
	/** 
	* 提供给Fragment通过PushVideoActivity.this方法来注册自己的触摸事件的方法 
	* @param listener 
	*/  
	public void registerMyTouchListener(TouchListener listener) {  
	     myTouchListeners.add(listener);  
	}  
	      
	/** 
	* 提供给Fragment通过PushVideoActivity.this方法来取消注册自己的触摸事件的方法 
	* @param listener 
	*/  
	public void unRegisterMyTouchListener(TouchListener listener) {  
	    myTouchListeners.remove( listener );  
	}  
	      
	/** 
	* 分发触摸事件给所有注册了MyTouchListener的接口 
	*/  
	@Override  
	public boolean dispatchTouchEvent(MotionEvent ev) {   
	    for (TouchListener listener : myTouchListeners) {  
	        listener.onTouchEvent(ev);  
	    }  
	    return super.dispatchTouchEvent(ev);  
	}  

    //滤镜（涉及硬件编码）的相关操作
    Blacklist[] g_blacklist = { //可扩展，需修改
    		new Blacklist("L39h", 19),
    		new Blacklist("N1", 22)
    };
    
    public class Blacklist {
    	public Blacklist(String model, int api){
    		mModel = model;
    		mApi = api;
    	}
    	public String getModel() {
    		return mModel;
    	}
    	public int getApi() {
    		return mApi;
    	}
    	private String mModel;
    	private int mApi;
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
				chatRoom.setPushUrl(intent.getStringExtra("pushUrl"));
				chatRoom.setMaster(intent.getBooleanExtra("isMaster", false));
			} catch (Exception e) {
				Toast.makeText(this, "直播间错误", Toast.LENGTH_LONG).show();
				e.printStackTrace();
				finish();
			}
        }
        enterChatRoom();
        registerObservers(true);
        
    }
    
    @Override
	protected void onPause() {
		super.onPause();
		if (fragment != null)
		{
			fragment.Pause();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onStart() {
		if (hasPush)
		{
			initializePushVideoFragment();
		}
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		fragment.StopVideoEncode();
	}

	private void initializePushVideoFragment()
    {
    	try {
			fragment = new PushVideoFragment(chatRoom);
			XLog.i("room id: " + chatRoom.getChatroomid());
			listener = fragment.CreateScrollListener();
			getSupportFragmentManager()
			        .beginTransaction()
			        .add(R.id.layout_video_play, fragment)
			        .commit();
			new SurfaceFragment(listener, chatRoom, false).show(getSupportFragmentManager(), "push video");
		} catch (Exception e) {
			e.printStackTrace();
			XLog.i("initialize push video fragment exception");
		}
    	
    }
    
    @SuppressWarnings("unchecked")
	private void enterChatRoom()
    {
        EnterChatRoomData data = new EnterChatRoomData(chatRoom.getChatroomid());
        enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoom(data);
        enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>(){

			@Override
			public void onException(Throwable exception) {
				 onLoginDone();
				 XLog.i("enter chat room exception, e=" + exception.getMessage());
	             Toast.makeText(PushVideoActivity.this, 
	            		 StringUtil.getXmlResource(PushVideoActivity.this, R.string.push_video_nim_login_exception) + exception.getMessage(),
	            		 Toast.LENGTH_SHORT).show();
	             finish();
			}

			@Override
			public void onFailed(int code) {
                onLoginDone();
                if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
                    Toast.makeText(PushVideoActivity.this, 
                    		StringUtil.getXmlResource(PushVideoActivity.this, R.string.push_video_nim_black_list), 
                    		Toast.LENGTH_SHORT).show();
                } else {
                	XLog.i("enter chat room failed, code=" + code);
                    Toast.makeText(PushVideoActivity.this, "enter chat room failed, code=" + code, Toast.LENGTH_SHORT).show();
                }
                finish();
			}

			@Override
			public void onSuccess(EnterChatRoomResultData result) {
				onLoginDone();
				roomInfo = result.getRoomInfo();
                ChatRoomMember member = result.getMember();
                member.setRoomId(roomInfo.getRoomId());
                ChatRoomCache.getInstance().ClearOnlinePeople();
                ChatRoomCache.getInstance().saveMemberCache(createMasterMember());
                chatRoom.setChatRoomInfo(roomInfo);
                initializePushVideoFragment();
                hasPush = true;
                XLog.i("enter chat room success" + roomInfo.getRoomId());
			}});
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
    
    private void onLoginDone() {
        enterRequest = null;
        DialogMaker.dismissProgressDialog();
    }
    
    private void logoutChatRoom() {
    	finish();
    }
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		if (fragment != null)
		{
			fragment.Destory();
		}
		logoutChatRoom();
		registerObservers(false);
	}

	private boolean checkVideoResolution()
    {
    	if (android.os.Build.VERSION.SDK_INT < 19)
    	{
    		return false;
    	}
    	else if(checkCurrentDeviceInBlacklist())
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    
    private boolean checkCurrentDeviceInBlacklist(){ 
    	boolean bInBlacklist = false;
    	String model = Build.MODEL;
    	int api = Build.VERSION.SDK_INT;
    	
    	int listsize = g_blacklist.length;
    	
    	for(int i = 0; i < listsize; i++)
    	{
    		if(model.equals(g_blacklist[i].getModel()) && api == g_blacklist[i].getApi())
    			bInBlacklist = true;
    	}    	
    	
    	return bInBlacklist;
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
	
	/************************** 注册 ***************************/
    private void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeOnlineStatus(onlineStatus, register);
        NIMClient.getService(ChatRoomServiceObserver.class).observeKickOutEvent(kickOutObserver, register);
    }
    
    @SuppressWarnings("serial")
	Observer<ChatRoomStatusChangeData> onlineStatus = new Observer<ChatRoomStatusChangeData>() {
        @Override
        public void onEvent(ChatRoomStatusChangeData chatRoomStatusChangeData) {
            if (chatRoomStatusChangeData.status == StatusCode.CONNECTING) {
                DialogMaker.updateLoadingMessage(StringUtil.getXmlResource(PushVideoActivity.this, R.string.push_video_nim_status_connecting));
            } else if (chatRoomStatusChangeData.status == StatusCode.UNLOGIN) {
                Toast.makeText(PushVideoActivity.this, R.string.push_video_nim_status_unlogin, Toast.LENGTH_SHORT).show();
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINING) {
                DialogMaker.updateLoadingMessage(StringUtil.getXmlResource(PushVideoActivity.this, R.string.push_video_nim_status_logining));
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINED) {
            } else if (chatRoomStatusChangeData.status.wontAutoLogin()) {
            } else if (chatRoomStatusChangeData.status == StatusCode.NET_BROKEN) {
                Toast.makeText(PushVideoActivity.this, R.string.push_video_net_broken, Toast.LENGTH_SHORT).show();
            }
            XLog.i("Chat Room Online Status:" + chatRoomStatusChangeData.status.name());
        }
    };

    @SuppressWarnings("serial")
	Observer<ChatRoomKickOutEvent> kickOutObserver = new Observer<ChatRoomKickOutEvent>() {
        @Override
        public void onEvent(ChatRoomKickOutEvent chatRoomKickOutEvent) {
            Toast.makeText(PushVideoActivity.this, 
            		StringUtil.getXmlResource(PushVideoActivity.this, R.string.push_video_kick_out) + chatRoomKickOutEvent.getReason(), 
            		Toast.LENGTH_SHORT).show();
            logoutChatRoom();
        }
    };

	@Override
	public void isExit(boolean exit) {
		if (exit)
		{
			fragment.Destory();
			finish();
		}
	}
	
	@Override
	public void isExit(boolean exit, long totalPeople) {

	}

	@Override
	public void onSwitchCamera() {
		try {
			fragment.SwitchCamera();
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e("switch camera exception!");
		}
	}
}

