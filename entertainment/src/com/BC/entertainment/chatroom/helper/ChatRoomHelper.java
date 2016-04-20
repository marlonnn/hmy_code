package com.BC.entertainment.chatroom.helper;

/**
 * Created by hzxuwen on 2016/1/19.
 */
public class ChatRoomHelper {

    public static void init() {
        ChatRoomMemberCache.getInstance().clear();
        ChatRoomMemberCache.getInstance().registerObservers(true);
    }

    public static void logout() {
        ChatRoomMemberCache.getInstance().registerObservers(false);
        ChatRoomMemberCache.getInstance().clear();
    }
}
