package com.BC.entertainment.chatroom.module;

import android.app.Activity;
import android.widget.Toast;

import com.BC.entertainment.inter.ActivityCallback;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.StarLiveVideoInfo;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.summer.logger.XLog;
import com.summer.utils.StringUtil;

public class ChatRoom {
	
	private ActivityCallback activityCallback;
	private AbortableFuture<EnterChatRoomResultData> enterRequest;
	
	public ChatRoom(ActivityCallback activityCallback)
	{
		this.activityCallback = activityCallback;
	}
	
    @SuppressWarnings("unchecked")
	public void EnterChatRoom(StarLiveVideoInfo startLiveVideoInfo)
    {
        EnterChatRoomData data = new EnterChatRoomData(startLiveVideoInfo.getChatroomid());
        enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoom(data);
        enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>(){

			@Override
			public void onException(Throwable exception) {
				 enterRequest = null;
				 XLog.e("enter chat room exception, e=" + exception.getMessage());
	             Toast.makeText(getActivity(), StringUtil.getXmlResource(getActivity(), R.string.push_video_nim_login_exception) + exception.getMessage(),
	            		 Toast.LENGTH_SHORT).show();
	             activityCallback.onLoginFailed();
			}

			@Override
			public void onFailed(int code) {
                if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
                    Toast.makeText(getActivity(), StringUtil.getXmlResource(getActivity(), R.string.push_video_nim_black_list), 
                    		Toast.LENGTH_SHORT).show();
                } else {
                	XLog.e("enter chat room failed, code=" + code);
                    Toast.makeText(getActivity(), "enter chat room failed, code=" + code, Toast.LENGTH_SHORT).show();
                }
                activityCallback.onLoginFailed();
			}
			
			@Override
			public void onSuccess(EnterChatRoomResultData result) {
				activityCallback.onLoginSuccess();
			}});
    }

    private Activity getActivity() {
    	
        return activityCallback.getActivity();
    }
}
