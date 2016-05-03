package com.BC.entertainment.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;

public class ChatRoomCache {
	
    //聊天室人数缓存
    private Map<String, ChatRoomMember> memberCache = new ConcurrentHashMap<>();

    public static ChatRoomCache getInstance() {
        return InstanceHolder.instance;
    }
    
    static class InstanceHolder {
        final static ChatRoomCache instance = new ChatRoomCache();
    }

	public Map<String, ChatRoomMember> getMemberCache() {
		return memberCache;
	}

	public void setMemberCache(Map<String, ChatRoomMember> memberCache) {
		this.memberCache = memberCache;
	}
    
}
