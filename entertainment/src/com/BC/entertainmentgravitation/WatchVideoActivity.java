package com.BC.entertainmentgravitation;

import java.util.HashMap;
import java.util.Map;

import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.BC.entertainmentgravitation.fragment.ExitFragmentListener;
import com.BC.entertainmentgravitation.fragment.PushVideoFragment;
import com.BC.entertainmentgravitation.fragment.ScrollListener;
import com.BC.entertainmentgravitation.fragment.SurfaceFragment;
import com.BC.entertainmentgravitation.fragment.TopSurfaceFragment;
import com.BC.entertainmentgravitation.fragment.WatchVideoFragment;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomKickOutEvent;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessageExtension;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomStatusChangeData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.config.Config;
import com.summer.logger.XLog;
import com.summer.utils.StringUtil;

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
public class WatchVideoActivity extends FragmentActivity implements ExitFragmentListener{
	
	public View rootView;
	private ScrollListener listener;

	private ChatRoom chatRoom;
	
    private AbortableFuture<EnterChatRoomResultData> enterRequest;//聊天室

	private ChatRoomInfo roomInfo;
	
	private Handler handler;
	
	private WatchVideoFragment fragment;
	
	private TopSurfaceFragment topsurfaceFragment;
	
	protected final Handler getHandler() {
        if (handler == null) {
            handler = new Handler(getMainLooper()){

				@Override
				public void handleMessage(Message msg) {
//					super.handleMessage(msg);
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
            String cid = intent.getStringExtra("cid");
        	String httpPullUrl = intent.getStringExtra("httpPullUrl");
        	String chatroomid = intent.getStringExtra("chatroomid");
        	chatRoom = new ChatRoom();
        	chatRoom.setCid(cid);
        	chatRoom.setChatroomid(chatroomid);
        	chatRoom.setHttpPullUrl(httpPullUrl);
        }
        enterChatRoom();
        registerObservers(true);
        
        handler = getHandler();

    }
    
    private void initializeWatchVideoFragment(ChatRoomMember member)
    {
        fragment = new WatchVideoFragment(chatRoom);
        fragment.setHandler(handler);
        listener = fragment.CreateScrollListener();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.layout_video_play, fragment)
                .commit();
        new SurfaceFragment(listener, chatRoom, member, true).show(getSupportFragmentManager(), "watch video");
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
						initializeWatchVideoFragment(member);
//						ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomTextMessage(chatRoom.getChatroomid(), "欢迎" + Config.User.getNickName() + "进入聊天室");
////						ChatRoomNotificationAttachment attach = new ChatRoomNotificationAttachment();
////						attach.setType(NotificationType.ChatRoomMemberIn);
//						message.setDirect(MsgDirectionEnum.In);
////						message.setAttachment(attach);
//						NIMClient.getService(ChatRoomService.class).sendMessage(message, false).setCallback(new RequestCallback<Void>() {
//							
//							@Override
//							public void onSuccess(Void arg0) {
//								XLog.i("enter room success" );
//							}
//							
//							@Override
//							public void onFailed(int arg0) {
//								XLog.i("enteroom exception");
//							}
//							
//							@Override
//							public void onException(Throwable arg0) {
//								XLog.i("enteroom exception");
//							}
//						});

					}
				});
	}

	private void onLoginDone() {
		enterRequest = null;
		DialogMaker.dismissProgressDialog();
	}

	private void logoutChatRoom() {
		NIMClient.getService(ChatRoomService.class).exitChatRoom(
				chatRoom.getChatroomid());
		clearChatRoom();
	}

	public void clearChatRoom() {
//		 ChatRoomMemberCache.getInstance().clearRoomCache(chatRoom.getChatroomid());
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
}
