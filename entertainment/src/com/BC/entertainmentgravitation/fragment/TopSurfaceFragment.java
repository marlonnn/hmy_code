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
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.ChatMessage;
import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.common.ui.listview.ListViewUtil.ScrollToPositionListener;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.constant.MemberType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.adapter.CommonAdapter;
import com.summer.config.Config;
import com.summer.logger.XLog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    
    private Map<String, ChatRoomMember> memberCache = new ConcurrentHashMap<String, ChatRoomMember>();//聊天室在线人数
    
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
	
	
    /**
     * *************************** 成员操作监听 ****************************
     */
    private void registerChatMemberObservers(boolean register) {
        ChatRoomMemberCache.getInstance().registerRoomMemberChangedObserver(roomMemberChangedObserver, register);
//        ChatRoomMemberCache.getInstance().registerObservers(register);
    }

    ChatRoomMemberCache.RoomMemberChangedObserver roomMemberChangedObserver = new ChatRoomMemberCache.RoomMemberChangedObserver() {
        @Override
        public void onRoomMemberIn(ChatRoomMember member) {
        	//some one room in
        	ChatRoomMemberCache.getInstance().saveMyMember(member);
        	memberCache = ChatRoomMemberCache.getInstance().GetOnLineMember(member.getRoomId());
        	//data notified
        }

        @Override
        public void onRoomMemberExit(ChatRoomMember member) {
        	//some one room out
        	ChatRoomMemberCache.getInstance().removeMyMember(member);
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
            	if (message.getContent() == null || message.getContent().equals("") || message.getContent().isEmpty())
            	{
            		ChatMessage msg = new ChatMessage();
            		XLog.i("nickname----->" + message.getRemoteExtension().get("nickname"));
            		msg.setAccount(message.getFromAccount());
            		msg.setContent(String.format("系统消息：欢迎%s进入聊天室", message.getFromAccount()));
            		items.add(0, msg);
            	}
            	else
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
	
}
