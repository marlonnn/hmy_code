package com.BC.entertainment.cache;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.text.TextUtils;

import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.BC.entertainmentgravitation.entity.Member;
import com.BC.entertainmentgravitation.entity.StarInformation;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.summer.config.Config;

public class ChatCache {
	
	private ChatRoom chatRoom = new ChatRoom();

    /**
     * 聊天室人数缓存
     */
    private Map<String, Member> memberCache = new ConcurrentHashMap<>();
    
    /**
     * 在线人数缓存
     */
    private LinkedList<Member> onlinePeopleitems = new LinkedList<Member>();
    
    public static ChatCache getInstance() {
        return InstanceHolder.instance;
    }
    
    static class InstanceHolder {
        final static ChatCache instance = new ChatCache();
    }
    
    public Member getMember(String account) {
        if (memberCache.containsKey(account)) {
            return memberCache.get(account);
        }
        return null;
    }
    
    public void AddMember(Member member)
    {
    	if (member != null && !TextUtils.isEmpty(member.getName()))
    	{
    		//  && !member.getName().contains(InfoCache.getInstance().getLiveStar().getUser_name())
    		if (!memberCache.containsKey(member.getName()) && !member.getName().contains(InfoCache.getInstance().getLiveStar().getUser_name()))
    		{
    			try {
    				if (member.getPortrait() != null)
    				{
    					String s[] = member.getPortrait().split("/");
    					
    					if (s[2] != null && !s[2].contains("app.haimianyu.cn"))
    					{
    						member.setPortrait("http://app.haimianyu.cn/" + member.getPortrait());
    					}
    				}

				} catch (Exception e) {
					e.printStackTrace();
				}
        		memberCache.put(member.getName(), member);
        		onlinePeopleitems.add(member);
    		}
    	}
    }
    
    public void AddMember(List<ChatRoomMember> chatRoomMembers)
    {
    	for (ChatRoomMember member : chatRoomMembers)
    	{
        	if (member != null && !TextUtils.isEmpty(member.getAccount()))
        	{
        		if (!memberCache.containsKey(member.getAccount()) && !member.getAccount().contains(Config.User.getUserName()))
        		{
        			Member m = new Member();
        			m.setName(member.getAccount());
        			m.setNick(member.getNick());
        			if (member.getAvatar() != null)
        			{
            			try {
							String s[] = member.getAvatar().split("/");
							if (s[2] != null && !s[2].contains("app.haimianyu.cn"))
							{
								m.setPortrait("http://app.haimianyu.cn/" + member.getAvatar());
							}
							else
							{
								m.setPortrait(member.getAvatar());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}	
        			}
            		memberCache.put(member.getAccount(), m);
            		onlinePeopleitems.add(m);
        		}

        	}
    	}
    }
    
    public void AddMember(List<ChatRoomMember> chatRoomMembers, StarInformation information)
    {
    	for (ChatRoomMember member : chatRoomMembers)
    	{
        	if (member != null && !TextUtils.isEmpty(member.getAccount()))
        	{
        		if (!memberCache.containsKey(member.getAccount()) && !member.getAccount().contains(information.getUser_name()))
        		{
        			Member m = new Member();
        			m.setName(member.getAccount());
        			m.setNick(member.getNick());
        			if (member.getAvatar() != null)
        			{
            			try {
							String s[] = member.getAvatar().split("/");
							if (s[2] != null && !s[2].contains("app.haimianyu.cn"))
							{
								m.setPortrait("http://app.haimianyu.cn/" + member.getAvatar());
							}
							else
							{
								m.setPortrait(member.getAvatar());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}	
        			}
            		memberCache.put(member.getAccount(), m);
            		onlinePeopleitems.add(m);
        		}

        	}
    	}
    }
    
    public void RemoveMember(Member member)
    {
    	if (member != null)
    	{
    		if (memberCache.containsKey(member.getName()))
    		{
        		memberCache.remove(member.getName());
        		for (int i=0; i<onlinePeopleitems.size(); i++)
        		{
        			if (onlinePeopleitems.get(i).getName().contains(member.getName()))
        			{
                		onlinePeopleitems.remove(i);
        			}
        		}
    		}
    	}
    }
    
    public void RemoveMember(String account)
    {
    	if (account != null)
    	{
    		if (memberCache.containsKey(account))
    		{
        		memberCache.remove(account);
        		for (int i=0; i<onlinePeopleitems.size(); i++)
        		{
        			if (onlinePeopleitems.get(i).getName().contains(account))
        			{
                		onlinePeopleitems.remove(i);
        			}
        		}
    		}
    	}
    }
    
    public void ClearMember()
    {
		onlinePeopleitems.clear();
		memberCache.clear();
    }
    
	public ChatRoom getChatRoom() {
		return chatRoom;
	}

	public void setChatRoom(ChatRoom chatRoom) {
		this.chatRoom = chatRoom;
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
}
