package com.BC.entertainment.inter;

import com.BC.entertainment.chatroom.gift.Gift;
import com.BC.entertainmentgravitation.entity.StarLiveVideoInfo;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import android.app.Activity;

public interface ActivityCallback {
	
    Activity getActivity();
    
    void onLoginSuccess();
    
    void onLoginFailed();
    
    void onLiveStart();
    
    void onInitFailed();
    
    void onNetWorkBroken();
    
    void onFinished();
    
    // 发送消息
    boolean sendMessage(IMMessage msg);
    
    //show gift animation
    void showAnimation(Gift gift);
}
