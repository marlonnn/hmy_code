package com.BC.entertainment.chatroom.helper;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.widget.TextView;

import com.BC.entertainment.adapter.RecyclerViewAdapter;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.constant.MemberType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.summer.logger.XLog;

/**
 * 聊天室在线人数缓存
 * @author zhongwen
 *
 */
public class OnlinePeopleCache {
	
	private static final int LIMIT = 100;
	
	private LinkedList<ChatRoomMember> items = new LinkedList<>();
	
	private Map<String, ChatRoomMember> memberCache = new ConcurrentHashMap<>();
	
    private long updateTime = 0; // 非游客的updateTime
    private long enterTime = 0; // 游客的enterTime
    
	private boolean isNormalEmpty = false; // 固定成员是否拉取完
	
	private String roomId;
	
	private RecyclerViewAdapter adapter;
	
	private TextView onlinePeople;//总的在线人数
	
    public TextView getOnlinePeople() {
		return onlinePeople;
	}

	public void setOnlinePeople(TextView onlinePeople) {
		this.onlinePeople = onlinePeople;
	}

	public LinkedList<ChatRoomMember> getItems() {
		return items;
	}

	public void setItems(LinkedList<ChatRoomMember> items) {
		this.items = items;
	}

	public RecyclerViewAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(RecyclerViewAdapter adapter) {
		this.adapter = adapter;
		fetchData();
	}

	private  static Map<MemberType, Integer> compMap = new HashMap<>();

    static {
        compMap.put(MemberType.CREATOR, 0);
        compMap.put(MemberType.ADMIN, 1);
        compMap.put(MemberType.NORMAL, 2);
        compMap.put(MemberType.LIMITED, 3);
        compMap.put(MemberType.GUEST, 4);
    }

    private static Comparator<ChatRoomMember> comp = new Comparator<ChatRoomMember>() {
        @Override
        public int compare(ChatRoomMember lhs, ChatRoomMember rhs) {
            if (lhs == null) {
                return 1;
            }

            if (rhs == null) {
                return -1;
            }

            return compMap.get(lhs.getMemberType()) - compMap.get(rhs.getMemberType());
        }
    };
	
	
    static class InstanceHolder {
        final static OnlinePeopleCache instance = new OnlinePeopleCache();
    }
    
    public static OnlinePeopleCache getInstance() {
        return InstanceHolder.instance;
    }
	
    private void fetchData() {
        if (!isNormalEmpty) {
            // 拉取固定在线成员
            getMembers(MemberQueryType.ONLINE_NORMAL, updateTime, 0);
        } else {
            // 拉取非固定成员
            getMembers(MemberQueryType.GUEST, enterTime, 0);
        }
    }
    
    /**
     * 获取成员列表
     */
    private void getMembers(final MemberQueryType memberQueryType, final long time, int limit) {
        ChatRoomMemberCache.getInstance().fetchRoomMembers(roomId, memberQueryType, time, (LIMIT - limit), new SimpleCallback<List<ChatRoomMember>>() {
            @Override
            public void onResult(boolean success, List<ChatRoomMember> result) {
                if (success) {

                    addMembers(result);

                    if (memberQueryType == MemberQueryType.ONLINE_NORMAL && result.size() < LIMIT) {
                        isNormalEmpty = true; // 固定成员已经拉完
                        getMembers(MemberQueryType.GUEST, enterTime, result.size());
                    }
                }
            }
        });
    }
    
    private void addMembers(List<ChatRoomMember> members) {
        for (ChatRoomMember member : members) {
            if (!isNormalEmpty) {
                updateTime = member.getUpdateTime();
            } else {
                enterTime = member.getEnterTime();
            }

            if (memberCache.containsKey(member.getAccount())) {
                items.remove(memberCache.get(member.getAccount()));
            }
            memberCache.put(member.getAccount(), member);

            items.add(member);
        }
        Collections.sort(items, comp);
        adapter.notifyDataSetChanged();
        if (onlinePeople != null)
        {
        	onlinePeople.setText(String.valueOf(items == null ? 0 : items.size()));
        }
    }
    
    public void removeMembers(ChatRoomMember member)
    {
        if (member == null) {
            return;
        }
        if (items.size() >= 1) {
            if (memberCache.containsKey(member.getAccount())) {
                items.remove(memberCache.get(member.getAccount()));
                memberCache.remove(member.getAccount());
                if (onlinePeople != null)
                {
                	onlinePeople.setText(String.valueOf(items == null ? 0 : items.size()));
                }
            }
        }
    }
    
    public void addMembers(ChatRoomMember member, boolean addFirst) {
        if (member == null) {
            return;
        }

        if (items.size() >= LIMIT) {
            items.poll();
        }

        if (addFirst) {
            if (memberCache.containsKey(member.getAccount())) {
                items.remove(memberCache.get(member.getAccount()));
            }
            memberCache.put(member.getAccount(), member);
            items.add(0, member);
        } else {
            if (memberCache.containsKey(member.getAccount())) {
                items.remove(memberCache.get(member.getAccount()));
            }
            memberCache.put(member.getAccount(), member);
            items.add(member);
        }
        if (onlinePeople != null)
        {
        	onlinePeople.setText(String.valueOf(items == null ? 0 : items.size()));
        }
    }

    /**
     * *************************** 成员操作监听 ****************************
     */
    public void registerObservers(boolean register) {
        ChatRoomMemberCache.getInstance().registerRoomMemberChangedObserver(roomMemberChangedObserver, register);
    }

    ChatRoomMemberCache.RoomMemberChangedObserver roomMemberChangedObserver = new ChatRoomMemberCache.RoomMemberChangedObserver() {
        @Override
        public void onRoomMemberIn(ChatRoomMember member) {
        	addMembers(member, false);
        	XLog.i("Room Member Changed Observer in, online people is: " + items.size());
        }

        @Override
        public void onRoomMemberExit(ChatRoomMember member) {
        	removeMembers(member);
        	XLog.i("Room Member Changed Observer exit, online people is: " + items.size());
        }
    };
}
