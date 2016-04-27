package com.BC.entertainment.chatroom.module;

import android.app.Activity;

import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

/**
 * 
 * @author wen zhong 2016/4/25
 *
 */
public class Container {
	
    public final Activity activity;
    public final ChatRoom chatRoom;
    public final SessionTypeEnum sessionType;
    public final ModuleProxy proxy;

    public Container(Activity activity, ChatRoom chatRoom, SessionTypeEnum sessionType, ModuleProxy proxy) {
        this.activity = activity;
        this.chatRoom = chatRoom;
        this.sessionType = sessionType;
        this.proxy = proxy;
    }

}
