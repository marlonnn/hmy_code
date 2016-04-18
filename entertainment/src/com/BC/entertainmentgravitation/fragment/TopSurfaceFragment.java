package com.BC.entertainmentgravitation.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.BC.entertainment.chatroom.helper.ChatRoomMemberCache;
import com.BC.entertainment.chatroom.helper.ChatRoomMemberCache.RoomMemberChangedObserver;
import com.BC.entertainmentgravitation.MainActivity;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.ChatMessage;
import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.common.ui.listview.ListViewUtil.ScrollToPositionListener;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
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
import com.summer.adapter.CommonAdapter;
import com.summer.config.Config;
import com.summer.logger.XLog;
import com.summer.view.CircularImage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class TopSurfaceFragment extends Fragment implements OnClickListener{
	
	private final int MESSAGE_CAPACITY = 500;
	
	private final int LIMIT = 100;
	
	private ChatRoom chatRoom;
	private View view;
	
	private Handler uiHandler;
	
	private Button btnSend;
	
	private EditText edtInput;
	
	private ImageView imageViewChart;
	
	private LinearLayout layoutInput;
	
    // message list view
    private LinkedList<ChatMessage> items;
    
    @SuppressWarnings("rawtypes")
	private CommonAdapter adapter;
    
    private ListView messageListView;
    
    private boolean isNormalEmpty = false; // 固定成员是否拉取完
    
    private long updateTime = 0; // 非游客的updateTime
    private long enterTime = 0; // 游客的enterTime
    
    private CircularImage headPortrait;
    
    private Map<String, ChatRoomMember> memberCache = new ConcurrentHashMap<String, ChatRoomMember>();//聊天室在线人数
    
    private List<RoomMemberChangedObserver> roomMemberChangedObservers = new ArrayList<RoomMemberChangedObserver>();
    
    //roomId(聊天室id),account(用户云信账号)
	private Map<String, Map<String, ChatRoomMember>> cache = new HashMap<String, Map<String, ChatRoomMember>>();
	
	private Map<String, List<SimpleCallback<ChatRoomMember>>> frequencyLimitCache = new HashMap<String, List<SimpleCallback<ChatRoomMember>>>(); // 重复请求处理
	
	public TopSurfaceFragment(ChatRoom chatRoom)
	{
		this.chatRoom = chatRoom;
		this.uiHandler = new Handler();
		items = new LinkedList<ChatMessage>();
		registerObservers(true);
		registerRecMessageObservers(true);
		registerChatMemberObservers(true);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@SuppressLint("InflateParams") @Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_top_surface, null);
		return view;
	}
	
	@SuppressLint("InflateParams") @Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initializeView();
	}
	
	private void initializeView()
	{
		layoutInput = (LinearLayout) view.findViewById(R.id.layout_input);
		
		edtInput = (EditText)view.findViewById(R.id.edtInput);
		
		btnSend = (Button) view.findViewById(R.id.btnSend);
		
		imageViewChart = (ImageView)view.findViewById(R.id.imageView_chart);
		
		messageListView = (ListView)view.findViewById(R.id.messageListView);
		
		headPortrait = (CircularImage) view.findViewById(R.id.portrait);
		
		imageViewChart.setOnClickListener(this);
		
		edtInput.setOnClickListener(this);
		
		btnSend.setOnClickListener(this);
		
		adapter = new CommonAdapter<ChatMessage>(getActivity().getBaseContext(), R.layout.fragment_message_item, 
				items) {

					@Override
					public void convert(
							ViewHolder holder,
							ChatMessage item) {
						holder.setText(R.id.txtName, item.getAccount() + ": ");
						holder.setText(R.id.txtContent, item.getContent());
					}
		};
		messageListView.setAdapter(adapter);
		
		Glide.with(getActivity())
		.load(MainActivity.personalInformation.getHead_portrait())
		.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
		.placeholder(R.drawable.home_image).into(headPortrait);
	}
	
    // 刷新消息列表
    public void refreshMessageList() {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
            }
        });
    }
    
    public void scrollToBottom() {
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                ListViewUtil.scrollToBottom(messageListView);
            }
        }, 200);
    }

    public void saveMessage(final IMMessage message, boolean addFirst) {
        if (message == null) {
            return;
        }

        if (items.size() >= MESSAGE_CAPACITY) {
            items.poll();
        }

        if (addFirst) {
        	
            items.add(0, createChatMessage(message));
        } else {
            items.add(createChatMessage(message));
        }
    }
    
    private ChatMessage createChatMessage(IMMessage message)
    {
    	ChatMessage chatMessage = new ChatMessage();
    	chatMessage.setAccount(message.getFromAccount());
    	chatMessage.setChatRoomId(message.getSessionId());
    	chatMessage.setContent(message.getContent());
    	return chatMessage;
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
		registerObservers(false);
		registerRecMessageObservers(false);
		registerChatMemberObservers(false);
	}
	
    // 发送消息后，更新本地消息列表
    public void onMsgSend(IMMessage message) {
        // add to listView and refresh
        saveMessage(message, false);
        List<IMMessage> addedListItems = new ArrayList<IMMessage>(1);
        addedListItems.add(message);
        
        adapter.notifyDataSetChanged();
        
        scrollToBottom(messageListView);
    }
    
    private void scrollToBottom(ListView listView) {
	    scrollToPosition(listView, listView.getAdapter().getCount() - 1, 0);
	}
    
    private void scrollToPosition(ListView messageListView, int position, int y) {
		scrollToPosition(messageListView, position, y, null);
	}
    
	private void scrollToPosition(final ListView messageListView, final int position, final int y, final ScrollToPositionListener listener) {
		messageListView.post(new Runnable() {
			
			@Override
			public void run() {				
				messageListView.setSelectionFromTop(position, y);
				
				if (listener != null) {
					listener.onScrollEnd();
				}
			}
		});			
	}
	

	@Override
	public void onClick(View v) {
		
		switch(v.getId())
		{
		case R.id.imageView_chart:
			if (layoutInput.isShown())
			{
				layoutInput.setVisibility(View.GONE);
			}
			else
			{
				layoutInput.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.btnSend:
			sendMessage();
			break;
		}
	}
	
	private void sendMessage()
	{
		ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomTextMessage(
				chatRoom.getChatroomid(),
				edtInput.getText().toString());
		sendMessage(message, null);
	}

	public boolean sendMessage(IMMessage msg, String type) {
		
        ChatRoomMessage message = (ChatRoomMessage) msg;

        Map<String, Object> ext = new HashMap<>();
        ChatRoomMember chatRoomMember = ChatRoomMemberCache.getInstance().getChatRoomMember(chatRoom.getChatroomid(), Config.User.getUserName());
        if (chatRoomMember != null && chatRoomMember.getMemberType() != null) {
//            ext.put("type", chatRoomMember.getMemberType().getValue());
            ext.put("nickname", Config.User.getNickName());
            message.setRemoteExtension(ext);
        }
		
//		if (type != null)
//		{
//			Map<String, Object> ext = new HashMap<String, Object>();
//			ext.put("type", type);
//			message.setRemoteExtension(ext);
//		}

		NIMClient.getService(ChatRoomService.class).sendMessage(message, false)
				.setCallback(new RequestCallback<Void>() {
					@Override
					public void onSuccess(Void param) {
						XLog.i("send messsage success");
					}

					@Override
					public void onFailed(int code) {
						if (code == ResponseCode.RES_CHATROOM_MUTED) {
							Toast.makeText(getActivity(), "用户被禁言",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getActivity(),
									"消息发送失败：code:" + code, Toast.LENGTH_SHORT)
									.show();
						}
					}

					@Override
					public void onException(Throwable exception) {
						Toast.makeText(getActivity(), "消息发送失败！",
								Toast.LENGTH_SHORT).show();
					}
				});
		onMsgSend(msg);
		return true;
    }
	
    public Map<String, ChatRoomMember> GetOnLineMember(String chatRoomId)
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
     * 根据聊天室id和用户id获取聊天室成员ChatRoomMember
     * @param roomId 聊天室id
     * @param account 用户云信账号
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
//        frequencyLimitCache.clear();
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
                XLog.i("attachment.getType(): " + attachment.getType());
                if(member != null)
                {
                	XLog.i("member: ");
                	if(member.getAccount() != null)
                	{
                		XLog.i("member get account: " + member.getAccount());
                	}
                }
                XLog.i("attachment.getType(): " + attachment.getType());
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
                XLog.i("ChatRoomMemberIn");
                break;
            case ChatRoomMemberExit:
                for (RoomMemberChangedObserver o : roomMemberChangedObservers) {
                    o.onRoomMemberExit(member);
                }
                XLog.i("ChatRoomMemberExit");
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
     * 从服务器获取聊天室成员资料（去重处理）（异步）
     * @param roomId 聊天室id
     * @param account 用户云信账号
     * @param callback 回调函数
     */
    public void fetchMember(final String roomId, final String account, final SimpleCallback<ChatRoomMember> callback)
    {
        if (TextUtils.isEmpty(roomId) || TextUtils.isEmpty(account)) {
            callback.onResult(false, null);
            return;
        }
        // 频率控制
        if (frequencyLimitCache.containsKey(account)) {
            if (callback != null) {
                frequencyLimitCache.get(account).add(callback);
            }
            return; // 已经在请求中，不要重复请求
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
     * 从服务器获取聊天室成员资料（去重处理）（异步）
     * @param roomId 聊天室ID
     * @param memberQueryType 分页获取成员查询类型
     * @param time 固定成员列表用updateTime, 游客列表用进入enterTime， 填0会使用当前服务器最新时间开始查询，即第一页，单位毫秒
     * @param limit 条数限制
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
     * *************************** 成员操作监听 ****************************
     */
    private void registerChatMemberObservers(boolean register) {
    	registerRoomMemberChangedObserver(roomMemberChangedObserver, register);
    }

    RoomMemberChangedObserver roomMemberChangedObserver = new RoomMemberChangedObserver() {
        @Override
        public void onRoomMemberIn(ChatRoomMember member) {
        	//some one room in
//        	ChatRoomMemberCache.getInstance().saveMyMember(member);
//        	memberCache = ChatRoomMemberCache.getInstance().GetOnLineMember(member.getRoomId());
        	//data notified
        	XLog.i("---on room member in-----" + member.getAccount());
        	XLog.i("---on room member in size-----" + memberCache.size());
        }

        @Override
        public void onRoomMemberExit(ChatRoomMember member) {
        	//some one room out
//        	ChatRoomMemberCache.getInstance().removeMyMember(member);
        	XLog.i("---on room member exit-----" + member.getAccount());
        	XLog.i("---on room member  size-----" + memberCache.size());
        }
    };
    
    private void notifyDataSetChanged(ChatRoomMember member, CommonAdapter adapter)
    {
    	memberCache = ChatRoomMemberCache.getInstance().GetOnLineMember(member.getRoomId());
    	adapter.notifyDataSetChanged();
    	
    }
	
    /**
     * ************************* 观察者 ********************************
     */
    private void registerObservers(boolean register) {
        ChatRoomServiceObserver service = NIMClient.getService(ChatRoomServiceObserver.class);
        service.observeMsgStatus(messageStatusObserver, register);
    }

    /**
     * 消息状态变化观察者
     */
    @SuppressWarnings("serial")
	Observer<ChatRoomMessage> messageStatusObserver = new Observer<ChatRoomMessage>() {
        @Override
        public void onEvent(ChatRoomMessage message) {
        	XLog.i("message status event: " + message.getContent());
        	XLog.i("message status event: " + message.getStatus());
        	
//        	XLog.i("message content: " + message.getContent());
        	XLog.i("message uid: " + message.getUuid());
        	XLog.i("message account: " + message.getFromAccount());
        	XLog.i("messsage session id" + message.getSessionId());
        	XLog.i("messsage msg type" + message.getMsgType());
        	XLog.i("messsage session type" + message.getSessionType());
        }
    };
    
    private void registerRecMessageObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, register);
    }

	Observer<List<ChatRoomMessage>> incomingChatRoomMsg = new Observer<List<ChatRoomMessage>>() {
		@Override
        public void onEvent(List<ChatRoomMessage> messages) {
            
            for (IMMessage message : messages) {
//            	if (message.getContent() == null || message.getContent().equals("") || message.getContent().isEmpty())
//            	{
//            		ChatMessage msg = new ChatMessage();
//            		if(message != null && message.getRemoteExtension() != null && message.getRemoteExtension().get("nickname") != null)
//            		{
//            			XLog.i("nickname----->" + message.getRemoteExtension().get("nickname"));
//            		}
//            		
//            		msg.setAccount(message.getFromAccount());
//            		msg.setContent(String.format("系统消息：欢迎%s进入聊天室", message.getFromAccount()));
//            		items.add(0, msg);
//            	}
//            	else
//            	{
//                	saveMessage(message, false);
//            	}
                if (messages == null || messages.isEmpty()) {
                    return;
                }
                if (message.getMsgType() == MsgTypeEnum.notification) {
                    handleNotification(message);
                }
                else if(message.getMsgType() == MsgTypeEnum.text)
                {
                	saveMessage(message, false);
                }
                

            	adapter.notifyDataSetChanged();
            	XLog.i("message content: " + message.getContent());
            	XLog.i("message uid: " + message.getUuid());
            	XLog.i("message account: " + message.getFromAccount());
            	XLog.i("messsage session id" + message.getSessionId());
            	XLog.i("messsage msg type" + message.getMsgType());
            	XLog.i("messsage session type" + message.getSessionType());
            }
        }
    };
	
    
    /**
     * ************************** 在线用户变化通知 ****************************
     */

    public interface RoomMemberChangedObserver {
        void onRoomMemberIn(ChatRoomMember member);

        void onRoomMemberExit(ChatRoomMember member);
    }
    
    /**
     * 注册在线用户变化通知观察者
     * @param o
     * @param register 注册和反注册标志位
     */
    public void registerRoomMemberChangedObserver(RoomMemberChangedObserver o, boolean register) {
        if (o == null) {
            return;
        }

        if (register) {
            if (!roomMemberChangedObservers.contains(o)) {
                roomMemberChangedObservers.add(o);
                XLog.i("roomMemberChangedObservers.add");
            }
        } else {
            roomMemberChangedObservers.remove(o);
        }
    }
}
