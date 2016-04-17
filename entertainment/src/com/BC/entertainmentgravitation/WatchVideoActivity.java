package com.BC.entertainmentgravitation;

import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.BC.entertainmentgravitation.fragment.PushVideoFragment;
import com.BC.entertainmentgravitation.fragment.ScrollListener;
import com.BC.entertainmentgravitation.fragment.SurfaceFragment;
import com.BC.entertainmentgravitation.fragment.WatchVideoFragment;
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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

/**
 * 看明星直播
 * @author zhongwen
 *
 */
public class WatchVideoActivity extends FragmentActivity {
	
	public View rootView;
	private ScrollListener listener;

	private ChatRoom chatRoom;
	
    private AbortableFuture<EnterChatRoomResultData> enterRequest;//聊天室

	private ChatRoomInfo roomInfo;

	private void registerObservers(boolean register) {
		NIMClient.getService(ChatRoomServiceObserver.class)
				.observeOnlineStatus(onlineStatus, register);
		NIMClient.getService(ChatRoomServiceObserver.class)
				.observeKickOutEvent(kickOutObserver, register);
	}

	@SuppressWarnings("serial")
	Observer<ChatRoomStatusChangeData> onlineStatus = new Observer<ChatRoomStatusChangeData>() {
		@Override
		public void onEvent(ChatRoomStatusChangeData chatRoomStatusChangeData) {
			if (chatRoomStatusChangeData.status == StatusCode.CONNECTING) {
				// DialogMaker.updateLoadingMessage("连接中...");
			} else if (chatRoomStatusChangeData.status == StatusCode.UNLOGIN) {
				// Toast.makeText(ChatRoomActivity.this,
				// R.string.nim_status_unlogin, Toast.LENGTH_SHORT).show();
			} else if (chatRoomStatusChangeData.status == StatusCode.LOGINING) {
				// DialogMaker.updateLoadingMessage("登录中...");
			} else if (chatRoomStatusChangeData.status == StatusCode.LOGINED) {
				// if (fragment != null) {
				// fragment.updateOnlineStatus(true);
				// }
			} else if (chatRoomStatusChangeData.status.wontAutoLogin()) {
			} else if (chatRoomStatusChangeData.status == StatusCode.NET_BROKEN) {
				// if (fragment != null) {
				// fragment.updateOnlineStatus(false);
				// }
				// Toast.makeText(ChatRoomActivity.this, R.string.net_broken,
				// Toast.LENGTH_SHORT).show();
			}
			XLog.i("Chat Room Online Status:"
					+ chatRoomStatusChangeData.status.name());
			// LogUtil.i(TAG, "Chat Room Online Status:" +
			// chatRoomStatusChangeData.status.name());
		}
	};

	@SuppressWarnings("serial")
	Observer<ChatRoomKickOutEvent> kickOutObserver = new Observer<ChatRoomKickOutEvent>() {
		@Override
		public void onEvent(ChatRoomKickOutEvent chatRoomKickOutEvent) {
			Toast.makeText(WatchVideoActivity.this,
					"被踢出聊天室，原因:" + chatRoomKickOutEvent.getReason(),
					Toast.LENGTH_SHORT).show();
			clearChatRoom();
		}
	};

	@SuppressWarnings("unchecked")
	private void enterChatRoom() {
		EnterChatRoomData data = new EnterChatRoomData(chatRoom.getChatroomid());
		enterRequest = NIMClient.getService(ChatRoomService.class)
				.enterChatRoom(data);
		enterRequest
				.setCallback(new RequestCallback<EnterChatRoomResultData>() {


					@Override
					public void onException(Throwable exception) {
						onLoginDone();
						XLog.i("enter chat room exception, e="
								+ exception.getMessage());
						Toast.makeText(
								WatchVideoActivity.this,
								"enter chat room exception, e="
										+ exception.getMessage(),
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onFailed(int code) {
						onLoginDone();
						if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
							Toast.makeText(WatchVideoActivity.this,
									"你已被拉入黑名单，不能再进入", Toast.LENGTH_SHORT)
									.show();
						} else {
							XLog.i("enter chat room failed, code=" + code);
							Toast.makeText(WatchVideoActivity.this,
									"enter chat room failed, code=" + code,
									Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onSuccess(EnterChatRoomResultData result) {
						roomInfo = result.getRoomInfo();
						ChatRoomMember member = result.getMember();
						member.setRoomId(roomInfo.getRoomId());
						XLog.i("enter chat room success" + roomInfo.getRoomId());
					}
				});
	}

	    private void onLoginDone() {
	        enterRequest = null;
	    }
	    
	    private void logoutChatRoom() {
	        NIMClient.getService(ChatRoomService.class).exitChatRoom(chatRoom.getChatroomid());
	        clearChatRoom();
	    }
	    
	    public void clearChatRoom() {
//	        ChatRoomMemberCache.getInstance().clearRoomCache(roomId);
//	        finish();
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

        WatchVideoFragment fragment = new WatchVideoFragment(chatRoom);
        listener = fragment.CreateScrollListener();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.layout_video_play, fragment)
                .commit();
        new SurfaceFragment(listener, chatRoom).show(getSupportFragmentManager(), "watch video");
    }
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		registerObservers(false);
		logoutChatRoom();
	}
}
