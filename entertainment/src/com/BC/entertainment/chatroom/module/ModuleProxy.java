package com.BC.entertainment.chatroom.module;

import com.BC.entertainment.chatroom.gift.Gift;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * 会话窗口提供给子模块的代理接口。
 */
public interface ModuleProxy {

    // 发送消息
    boolean sendMessage(IMMessage msg);
    
    //send custom message
    boolean sendCustomMessage(IMMessage msg);

    // 消息输入区展开时候的处理
    void onInputPanelExpand();

    // 应当收起输入区
    void shouldCollapseInputPanel();

    // 是否正在录音
    boolean isLongClickEnabled();
    
    //show gift animation
    void showAnimation(Gift gift);
}
