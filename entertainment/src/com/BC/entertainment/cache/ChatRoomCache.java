package com.BC.entertainment.cache;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.text.TextUtils;

import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;

public class ChatRoomCache {
	
	private static final int LIMIT = 100;//在线人数listview中最多人数
	
	private long totalPeople;//总的观看人数
	
    /**
     * 聊天室人数缓存
     */
    private Map<String, ChatRoomMember> memberCache = new ConcurrentHashMap<>();
    
    /**
     * 在线人数缓存
     */
    private LinkedList<ChatRoomMember> onlinePeopleitems = new LinkedList<ChatRoomMember>();

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

	public LinkedList<ChatRoomMember> getOnlinePeopleitems() {
		return onlinePeopleitems;
	}

	public void setOnlinePeopleitems(LinkedList<ChatRoomMember> onlinePeopleitems) {
		this.onlinePeopleitems = onlinePeopleitems;
	}

	public long getTotalPeople() {
		return totalPeople;
	}

	public void setTotalPeople(long totalPeople) {
		this.totalPeople = totalPeople;
	}
	
    public void saveMemberCache(List<ChatRoomMember> members)
    {
    	for (ChatRoomMember member : members)
    	{
    		saveMemberCache(member);
    	}
    }
    
    public void saveMemberCache(ChatRoomMember member)
    {
    	if (member != null && !TextUtils.isEmpty(member.getRoomId()) && !TextUtils.isEmpty(member.getAccount()))
    	{
    		if (!memberCache.containsKey(member.getAccount()))
    		{
        		memberCache.put(member.getAccount(), member);
    		}

    	}
    }
    
    public void RemoveMemberCache(String account)
    {
		if (memberCache.containsKey(account))
		{
    		memberCache.remove(account);
		}
    }
    
    public void RemoveMemberCache(ChatRoomMember member)
    {
    	if (member != null)
    	{
    		if (memberCache.containsKey(member.getAccount()))
    		{
        		memberCache.remove(member.getAccount());
    		}
    	}
    }
	
    public ChatRoomMember getChatRoomMember(String account) {
        if (memberCache.containsKey(account)) {
            return memberCache.get(account);
        }

        return null;
    }
    
    public void clearMemberCache(String roomId) {
    	memberCache.clear();
    }
    
	public void ClearOnlinePeople()
	{
		onlinePeopleitems.clear();
		memberCache.clear();
	}
}
