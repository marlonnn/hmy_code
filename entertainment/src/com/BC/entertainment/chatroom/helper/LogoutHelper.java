package com.BC.entertainment.chatroom.helper;

import com.BC.entertainment.config.Cache;
import com.netease.nim.uikit.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.NimUIKit;

/**
 * ע��������
 * Created by huangjun on 2015/10/8.
 */
public class LogoutHelper {
    public static void logout() {
        // ������&ע������&���״̬
        NimUIKit.clearCache();
//        ChatRoomHelper.logout();
        Cache.clear();
//        LoginSyncDataStatusObserver.getInstance().reset();
    }
}
