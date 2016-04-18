package com.BC.entertainmentgravitation;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.BC.entertainment.chatroom.helper.ChatRoomMemberCache;
import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.BC.entertainmentgravitation.fragment.ExitFragmentListener;
import com.BC.entertainmentgravitation.fragment.PushVideoFragment;
import com.BC.entertainmentgravitation.fragment.ScrollListener;
import com.BC.entertainmentgravitation.fragment.SurfaceFragment;
import com.netease.LSMediaCapture.lsMessageHandler;
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
import com.summer.logger.XLog;
import com.summer.utils.StringUtil;
import com.summer.utils.ToastUtil;

/**
 * ����ֱ��
 * @author zhongwen
 *
 */
public class PushVideoActivity extends FragmentActivity implements ExitFragmentListener {
	
	private View rootView;
	
	private ScrollListener listener;
	
	private PushVideoFragment fragment;
	
	private ChatRoom chatRoom;
    
    private AbortableFuture<EnterChatRoomResultData> enterRequest;//������
    
    private ChatRoomInfo roomInfo;
    
	private Handler handler;
	
	public interface TouchListener
	{
		boolean onTouchEvent(MotionEvent event);  
	}
	
	// ����MyTouchListener�ӿڵ��б�  
	private ArrayList<TouchListener> myTouchListeners = new ArrayList<TouchListener>();
	
	/** 
	* �ṩ��Fragmentͨ��PushVideoActivity.this������ע���Լ��Ĵ����¼��ķ��� 
	* @param listener 
	*/  
	public void registerMyTouchListener(TouchListener listener) {  
	     myTouchListeners.add(listener);  
	}  
	      
	/** 
	* �ṩ��Fragmentͨ��PushVideoActivity.this������ȡ��ע���Լ��Ĵ����¼��ķ��� 
	* @param listener 
	*/  
	public void unRegisterMyTouchListener(TouchListener listener) {  
	    myTouchListeners.remove( listener );  
	}  
	      
	/** 
	* �ַ������¼�������ע����MyTouchListener�Ľӿ� 
	*/  
	@Override  
	public boolean dispatchTouchEvent(MotionEvent ev) {   
	    for (TouchListener listener : myTouchListeners) {  
	        listener.onTouchEvent(ev);  
	    }  
	    return super.dispatchTouchEvent(ev);  
	}  

    //�˾����漰Ӳ�����룩����ز���
    Blacklist[] g_blacklist = { //����չ�����޸�
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
            String cid = intent.getStringExtra("cid");
        	String pushUrl = intent.getStringExtra("pushUrl");
        	String chatroomid = intent.getStringExtra("chatroomid");
        	chatRoom = new ChatRoom();
        	chatRoom.setCid(cid);
        	chatRoom.setChatroomid(chatroomid);
        	chatRoom.setPushUrl(pushUrl);
        	chatRoom.setFilter(checkVideoResolution());
        }
        enterChatRoom();
        registerObservers(true);
        
        handler = getHandler();
    }
    
    @Override
	protected void onPause() {
		super.onPause();
		fragment.Pause();
	}

	@Override
	protected void onResume() {
		super.onResume();

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
	protected void onRestart() {
		super.onRestart();
		fragment.StopVideoEncode();
	}

	private void initializePushVideoFragment()
    {
    	fragment = new PushVideoFragment(chatRoom);
    	fragment.setHandler(handler);
        listener = fragment.CreateScrollListener();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.layout_video_play, fragment)
                .commit();
        new SurfaceFragment(listener, chatRoom).show(getSupportFragmentManager(), "push video");
    	
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
				roomInfo = result.getRoomInfo();
                ChatRoomMember member = result.getMember();
                member.setRoomId(roomInfo.getRoomId());
                ChatRoomMemberCache.getInstance().saveMyMember(member);
                initializePushVideoFragment();
                XLog.i("enter chat room success" + roomInfo.getRoomId());
			}});
    }
    
    private void onLoginDone() {
        enterRequest = null;
        DialogMaker.dismissProgressDialog();
    }
    
    private void logoutChatRoom() {
        NIMClient.getService(ChatRoomService.class).exitChatRoom(chatRoom.getChatroomid());
        clearChatRoom();
    }
    
    public void clearChatRoom() {
        ChatRoomMemberCache.getInstance().clearRoomCache(chatRoom.getChatroomid());
        finish();
    }
    
    
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		registerObservers(false);
		logoutChatRoom();
		fragment.Destory();
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
	
	/************************** ע�� ***************************/
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
//                if (fragment != null) {
//                    fragment.updateOnlineStatus(true);
//                }
            } else if (chatRoomStatusChangeData.status.wontAutoLogin()) {
            } else if (chatRoomStatusChangeData.status == StatusCode.NET_BROKEN) {
//                if (fragment != null) {
//                    fragment.updateOnlineStatus(false);
//                }
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
            clearChatRoom();
        }
    };
	
    /**
     * ����ֱ�������е��쳣
     * @return
     */
    protected final Handler getHandler() {
        if (handler == null) {
            handler = new Handler(getMainLooper()){

				@Override
				public void handleMessage(Message msg) {
//					super.handleMessage(msg);
					switch(msg.what)
					{
				      case lsMessageHandler.MSG_INIT_LIVESTREAMING_OUTFILE_ERROR://��ʼ��ֱ������
				      case lsMessageHandler.MSG_INIT_LIVESTREAMING_VIDEO_ERROR:	
				      case lsMessageHandler.MSG_INIT_LIVESTREAMING_AUDIO_ERROR:
				    	  toastAndExit("��ʼ��ֱ������");
				    	  break;
				      case lsMessageHandler.MSG_START_LIVESTREAMING_ERROR://��ʼֱ������
				    	  toastAndExit("��ʼֱ������");
				    	  break;
				      case lsMessageHandler.MSG_STOP_LIVESTREAMING_ERROR://ֱֹͣ������
				    	  toastAndExit("ֱֹͣ������");
				    	  break;
				      case lsMessageHandler.MSG_AUDIO_PROCESS_ERROR://��Ƶ�������
				    	  toastAndExit("��Ƶ�������");
				    	  break;
				      case lsMessageHandler.MSG_VIDEO_PROCESS_ERROR://��Ƶ�������
				    	  toastAndExit("��Ƶ�������");
				    	  break;
				      case lsMessageHandler.MSG_RTMP_URL_ERROR://������Ϣ
				    	  toastAndExit("�����ˣ�������������");
				    	  break;
				      case lsMessageHandler.MSG_URL_NOT_AUTH://ֱ��URL�Ƿ�
				    	  toastAndExit("ֱ����ַURL�Ƿ�������");
				    	  break;
				      case lsMessageHandler.MSG_SEND_STATICS_LOG_ERROR://����ͳ����Ϣ����
				    	  toastAndExit("����ͳ����Ϣ����");
				    	  break;
				      case lsMessageHandler.MSG_SEND_HEARTBEAT_LOG_ERROR://����������Ϣ����
				    	  toastAndExit("����������Ϣ����");
				    	  break;
				      case lsMessageHandler.MSG_AUDIO_SAMPLE_RATE_NOT_SUPPORT_ERROR://��Ƶ�ɼ�������֧��
				    	  toastAndExit("��Ƶ�ɼ�������֧��");
				    	  break;
				      case lsMessageHandler.MSG_AUDIO_PARAMETER_NOT_SUPPORT_BY_HARDWARE_ERROR://��Ƶ������֧��
				    	  toastAndExit("��Ƶ������֧��");
				    	  break;
				      case lsMessageHandler.MSG_NEW_AUDIORECORD_INSTANCE_ERROR://��Ƶʵ����ʼ������
				    	  toastAndExit("��Ƶʵ����ʼ������");
				    	  break;
				      case lsMessageHandler.MSG_AUDIO_START_RECORDING_ERROR://��Ƶ�ɼ�����
				    	  toastAndExit("��Ƶ�ɼ�����");
				    	  break;
				      case lsMessageHandler.MSG_OTHER_AUDIO_PROCESS_ERROR://��Ƶ��������������
				    	  toastAndExit("��Ƶ��������������");
				    	  break;
				      case lsMessageHandler.MSG_QOS_TO_STOP_LIVESTREAMING://����QoS������ʵ��ν������
				    	  break;
				      case lsMessageHandler.MSG_HW_VIDEO_PACKET_ERROR://��ƵӲ���������
				    	  toastAndExit("��ƵӲ���������");
				    	  break;
				      case lsMessageHandler.MSG_CAMERA_PREVIEW_SIZE_NOT_SUPPORT_ERROR://camera�ɼ��ֱ��ʲ�֧��
				    	  toastAndExit("�ɼ��ֱ��ʲ�֧��");
				    	  break;
				      case lsMessageHandler.MSG_START_LIVESTREAMING_FINISHED://��ʼֱ�����
				    	  break;
				      case lsMessageHandler.MSG_STOP_LIVESTREAMING_FINISHED://ֱֹͣ�����
				    	  toastAndExit("ֱֹͣ�����");
			              break;
					}
				}
            	
            };
        }
        return handler;
    }
    
    private void toastAndExit(String message)
    {
  	  ToastUtil.show(PushVideoActivity.this, message);
  	  finish();
    }

	@Override
	public void isExit(boolean exit) {
		if (exit)
		{
			finish();
		}
	}
}

