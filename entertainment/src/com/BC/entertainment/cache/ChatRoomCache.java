package com.BC.entertainment.cache;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.BC.entertainmentgravitation.entity.Member;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;

import android.text.TextUtils;

public class ChatRoomCache {
	
	private static final int LIMIT = 100;//在线人数listview中最多人数
	
	private long totalPeople;//总的观看人数
	
    /**
     * 聊天室人数缓存
     */
    private Map<String, Member> memberCache = new ConcurrentHashMap<>();
    
    /**
     * 在线人数缓存
     */
    private LinkedList<Member> onlinePeopleitems = new LinkedList<Member>();

    public static ChatRoomCache getInstance() {
        return InstanceHolder.instance;
    }
    
    static class InstanceHolder {
        final static ChatRoomCache instance = new ChatRoomCache();
    }

	public Map<String, Member> getMemberCache() {
		return memberCache;
	}

	public void setMemberCache(Map<String, Member> memberCache) {
		this.memberCache = memberCache;
	}

	public LinkedList<Member> getOnlinePeopleitems() {
		return onlinePeopleitems;
	}

	public void setOnlinePeopleitems(LinkedList<Member> onlinePeopleitems) {
		this.onlinePeopleitems = onlinePeopleitems;
	}

	public long getTotalPeople() {
		return totalPeople;
	}

	public void setTotalPeople(long totalPeople) {
		this.totalPeople = totalPeople;
	}
	
    public void saveMemberCache(List<Member> members)
    {
    	for (Member member : members)
    	{
    		saveMemberCache(member);
    	}
    }
    
    public void saveMemberCache(Member member)
    {
    	if (member != null && !TextUtils.isEmpty(member.getName()))
    	{
    		if (!memberCache.containsKey(member.getName()))
    		{
        		memberCache.put(member.getName(), member);
        		onlinePeopleitems.add(member);
    		}

    	}
    }
    
    public void saveMemberCache(List<ChatRoomMember> chatRoomMembers, boolean flag)
    {
    	for (ChatRoomMember member : chatRoomMembers)
    	{
        	if (member != null && !TextUtils.isEmpty(member.getAccount()))
        	{
        		if (!memberCache.containsKey(member.getAccount()))
        		{
        			Member m = new Member();
        			m.setName(member.getAccount());
        			m.setNick(member.getNick());
        			m.setPortrait(member.getAvatar());
            		memberCache.put(member.getAccount(), m);
            		onlinePeopleitems.add(m);
        		}

        	}
    	}
    }
    
    public void saveOnlinePeople(List<ChatRoomMember> chatRoomMembers)
    {
    	for (ChatRoomMember member : chatRoomMembers)
    	{
        	if (member != null && !TextUtils.isEmpty(member.getAccount()))
        	{
        		if (!memberCache.containsKey(member.getAccount()))
        		{
        			Member m = new Member();
        			m.setName(member.getAccount());
        			m.setNick(member.getNick());
        			m.setPortrait(member.getAvatar());
            		memberCache.put(member.getAccount(), m);
            		onlinePeopleitems.add(m);
        		}

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
    
    public void RemoveMemberCache(Member member)
    {
    	if (member != null)
    	{
    		if (memberCache.containsKey(member.getName()))
    		{
        		memberCache.remove(member.getName());
    		}
    	}
    }
	
    public Member getMember(String account) {
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
