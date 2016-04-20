package com.BC.entertainment.chatroom.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;

import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.constant.MemberType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.logger.XLog;


/**
 * �����ҳ�Ա���ϻ���
 * @author wen zhong 2016/4/18
 *
 */
public class ChatRoomMemberCache {
	
	    //roomId(������id),account(�û������˺�)
	private Map<String, Map<String, ChatRoomMember>> cache = new HashMap<String, Map<String, ChatRoomMember>>();
	
	private List<RoomMemberChangedObserver> roomMemberChangedObservers = new ArrayList<RoomMemberChangedObserver>();
	
	private Map<String, List<SimpleCallback<ChatRoomMember>>> frequencyLimitCache = new HashMap<String, List<SimpleCallback<ChatRoomMember>>>(); // �ظ�������

    static class InstanceHolder {
        final static ChatRoomMemberCache instance = new ChatRoomMemberCache();
    }
    
    public static ChatRoomMemberCache getInstance() {
        return InstanceHolder.instance;
    }
    
    public Map<String, ChatRoomMember> GetChatMemberMap(String chatRoomId)
    {
    	if (cache.containsKey(chatRoomId))
    	{
    		return cache.get(chatRoomId);
    	}
    	else
    	{
    		return null;
    	}
    }
    /**
     * ����������id���û�id��ȡ�����ҳ�ԱChatRoomMember
     * @param roomId ������id
     * @param account �û������˺�
     * @return
     */
    public ChatRoomMember getChatRoomMember(String roomId, String account) {
        if (cache.containsKey(roomId)) {
            return cache.get(roomId).get(account);
        }

        return null;
    }
    
    public void clearRoomCache(String roomId) {
        if (cache.containsKey(roomId)) {
            cache.remove(roomId);
        }
    }
    
    public void clear() {
        cache.clear();
        frequencyLimitCache.clear();
        roomMemberChangedObservers.clear();
    }
    
    public void saveMyMember(ChatRoomMember chatRoomMember) {
        saveMember(chatRoomMember);
    }
    
    public void removeMyMember(ChatRoomMember member)
    {
    	if (member != null && !TextUtils.isEmpty(member.getRoomId()) && !TextUtils.isEmpty(member.getAccount()))
    	{
    		Map<String, ChatRoomMember> members = cache.get(member.getRoomId());
    		if (members != null)
    		{
    			members.remove(member.getAccount());
    		}
    	}
    }
    
    private void saveMembers(List<ChatRoomMember> members) {
        if (members == null || members.isEmpty()) {
            return;
        }

        for (ChatRoomMember m : members) {
            saveMember(m);
        }
    }
    
    private void saveMember(ChatRoomMember member)
    {
    	if (member != null && !TextUtils.isEmpty(member.getRoomId()) && !TextUtils.isEmpty(member.getAccount()))
    	{
    		Map<String, ChatRoomMember> members = cache.get(member.getRoomId());
            if (members == null) {
                members = new HashMap<String, ChatRoomMember>();
                cache.put(member.getRoomId(), members);
            }
            members.put(member.getAccount(), member);
    	}
    }
    
    /**
     * �ӷ�������ȡ�����ҳ�Ա���ϣ�ȥ�ش������첽��
     * @param roomId ������id
     * @param account �û������˺�
     * @param callback �ص�����
     */
    public void fetchMember(final String roomId, final String account, final SimpleCallback<ChatRoomMember> callback)
    {
        if (TextUtils.isEmpty(roomId) || TextUtils.isEmpty(account)) {
            callback.onResult(false, null);
            return;
        }
        // Ƶ�ʿ���
        if (frequencyLimitCache.containsKey(account)) {
            if (callback != null) {
                frequencyLimitCache.get(account).add(callback);
            }
            return; // �Ѿ��������У���Ҫ�ظ�����
        }
        else
        {
        	List<SimpleCallback<ChatRoomMember>> cbs = new ArrayList<SimpleCallback<ChatRoomMember>>();
            if (callback != null) {
                cbs.add(callback);
            }
            frequencyLimitCache.put(account, cbs);
        }
        // fetch
        List<String> accounts = new ArrayList<>(1);
        accounts.add(account);
        NIMClient.getService(ChatRoomService.class).fetchRoomMembersByIds(roomId, accounts).
        	setCallback(new RequestCallbackWrapper<List<ChatRoomMember>>(){

				@Override
				public void onResult(int code, List<ChatRoomMember> members, Throwable exception) {
	                ChatRoomMember member = null;
	                boolean hasCallback = !frequencyLimitCache.get(account).isEmpty();
	                boolean success = code == ResponseCode.RES_SUCCESS && members != null && !members.isEmpty();
	                
	                // cache
	                if (success) {
	                    saveMembers(members);
	                    member = members.get(0);
	                } else {
	                    XLog.i("fetch chat room member failed, code=" + code);
	                }

	                // callback
	                if (hasCallback) {
	                    List<SimpleCallback<ChatRoomMember>> cbs = frequencyLimitCache.get(account);
	                    for (SimpleCallback<ChatRoomMember> cb : cbs) {
	                        cb.onResult(success, member);
	                    }
	                }

	                frequencyLimitCache.remove(account);
				}});
    }
    
    /**
     * �ӷ�������ȡ�����ҳ�Ա���ϣ�ȥ�ش������첽��
     * @param roomId ������ID
     * @param memberQueryType ��ҳ��ȡ��Ա��ѯ����
     * @param time �̶���Ա�б���updateTime, �ο��б��ý���enterTime�� ��0��ʹ�õ�ǰ����������ʱ�俪ʼ��ѯ������һҳ����λ����
     * @param limit ��������
     * @param callback
     */
	public void fetchRoomMembers(String roomId, MemberQueryType memberQueryType, long time, int limit, 
			final SimpleCallback<List<ChatRoomMember>> callback) {
		if (TextUtils.isEmpty(roomId)) {
			callback.onResult(false, null);
			return;
		}

		NIMClient.getService(ChatRoomService.class).fetchRoomMembers(roomId, memberQueryType, time, limit)
				.setCallback(
						new RequestCallbackWrapper<List<ChatRoomMember>>() {
							@Override
							public void onResult(int code,
									List<ChatRoomMember> result,
									Throwable exception) {
								boolean success = code == ResponseCode.RES_SUCCESS;

								if (success) {
									saveMembers(result);
								} else {
									XLog.i("fetch members by page failed, code:"+ code);
								}

								if (callback != null) {
									callback.onResult(success, result);
								}
							}
						});
	}
	
    /**
     * ********************************** ���� ********************************
     */

    public void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, register);
    }

    @SuppressWarnings("serial")
	private Observer<List<ChatRoomMessage>> incomingChatRoomMsg = new Observer<List<ChatRoomMessage>>() {
        @Override
        public void onEvent(List<ChatRoomMessage> messages) {
        	XLog.i("incomingChatRoomMsg");
            if (messages == null || messages.isEmpty()) {
                return;
            }

            for (IMMessage message : messages) {
                if (message == null) {
                    XLog.i("receive chat room message null");
                    continue;
                }

                if (message.getMsgType() == MsgTypeEnum.notification) {
                    handleNotification(message);
                }
            	XLog.i("message content: " + message.getContent());
            	XLog.i("message uid: " + message.getUuid());
            	XLog.i("message account: " + message.getFromAccount());
            	XLog.i("messsage session id" + message.getSessionId());
            	XLog.i("messsage msg type" + message.getMsgType());
            	XLog.i("messsage session type" + message.getSessionType());
            }
        }
    };
    
    private void handleNotification(IMMessage message) {
        if (message.getAttachment() == null) {
            return;
        }

        String roomId = message.getSessionId();
        ChatRoomNotificationAttachment attachment = (ChatRoomNotificationAttachment) message.getAttachment();
        List<String> targets = attachment.getTargets();
        if (targets != null) {
            for (String target : targets) {
                ChatRoomMember member = getChatRoomMember(roomId, target);
                handleMemberChanged(attachment.getType(), member);
            }
        }
    }
    
    private void handleMemberChanged(NotificationType type, ChatRoomMember member) {
        if (member == null) {
            return;
        }

        switch (type) {
            case ChatRoomMemberIn:
                for (RoomMemberChangedObserver o : roomMemberChangedObservers) {
                    o.onRoomMemberIn(member);
                }
                break;
            case ChatRoomMemberExit:
                for (RoomMemberChangedObserver o : roomMemberChangedObservers) {
                    o.onRoomMemberExit(member);
                }
                break;
            case ChatRoomManagerAdd:
                member.setMemberType(MemberType.ADMIN);
                break;
            case ChatRoomManagerRemove:
                member.setMemberType(MemberType.NORMAL);
                break;
            case ChatRoomMemberBlackAdd:
                member.setInBlackList(true);
                break;
            case ChatRoomMemberBlackRemove:
                member.setInBlackList(false);
                break;
            case ChatRoomMemberMuteAdd:
                member.setMuted(true);
                break;
            case ChatRoomMemberMuteRemove:
                member.setMuted(false);
                member.setMemberType(MemberType.GUEST);
                break;
            case ChatRoomCommonAdd:
                member.setMemberType(MemberType.NORMAL);
                break;
            case ChatRoomCommonRemove:
                member.setMemberType(MemberType.GUEST);
                break;
            default:
                break;
        }

        saveMember(member);
    }
    
    /**
     * ************************** �����û��仯֪ͨ ****************************
     */

    public interface RoomMemberChangedObserver {
        void onRoomMemberIn(ChatRoomMember member);

        void onRoomMemberExit(ChatRoomMember member);
    }
    
    /**
     * ע�������û��仯֪ͨ�۲���
     * @param o
     * @param register ע��ͷ�ע���־λ
     */
    public void registerRoomMemberChangedObserver(RoomMemberChangedObserver o, boolean register) {
        if (o == null) {
            return;
        }

        if (register) {
            if (!roomMemberChangedObservers.contains(o)) {
                roomMemberChangedObservers.add(o);
            }
        } else {
            roomMemberChangedObservers.remove(o);
        }
    }
}
