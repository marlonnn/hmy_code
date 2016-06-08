package com.BC.entertainment.chatroom.helper;

import com.BC.entertainment.config.Cache;

/**
 * 注销帮助类
 * Created by huangjun on 2015/10/8.
 */
public class LogoutHelper {
    public static void logout() {
        // 清理缓存&注销监听&清除状态
        Cache.clear();
    }
}
