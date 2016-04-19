package com.BC.entertainmentgravitation.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.BC.entertainment.chatroom.helper.ChatRoomMemberCache;
import com.BC.entertainment.chatroom.helper.ChatRoomNotificationHelper;
import com.BC.entertainment.chatroom.helper.MessageType;
import com.BC.entertainment.chatroom.helper.ChatRoomMemberCache.RoomMemberChangedObserver;
import com.BC.entertainmentgravitation.MainActivity;
import com.BC.entertainmentgravitation.NotifyDataSetChanged;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.ChatMessage;
import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.common.ui.listview.ListViewUtil.ScrollToPositionListener;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
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
    
    private CircularImage headPortrait;
	
	public TopSurfaceFragment(ChatRoom chatRoom)
	{
		this.chatRoom = chatRoom;
		this.uiHandler = new Handler();
		items = new LinkedList<ChatMessage>();
	}
	
	private void addMessage(ChatMessage chatMessage, boolean addFirst)
	{
		if (chatMessage != null)
		{
			if (items.size() >= MESSAGE_CAPACITY)
			{
				items.poll();
			}
	        if (addFirst) {
	        	items.add(0, chatMessage);
	        } else {
	        	items.add(chatMessage);
	        }
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		register(true);
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
//						holder.setImageResource(R.id.imageViewMessage, drawableId)
						if (item.getType() == MessageType.notificatijon)
						{
							holder.setText(R.id.txtName, "ϵͳ��Ϣ��");
							holder.setText(R.id.txtContent, item.getContent());
						}
						else
						{
							holder.setText(R.id.txtName, item.getNickName() + ": ");
							holder.setText(R.id.txtContent, item.getContent());
						}

					}
		};
		messageListView.setAdapter(adapter);
		
		Glide.with(getActivity())
		.load(MainActivity.personalInformation.getHead_portrait())
		.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
		.placeholder(R.drawable.home_image).into(headPortrait);
	}
	
    // ˢ����Ϣ�б�
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		register(false);
	}
	
    // ������Ϣ�󣬸��±�����Ϣ�б�
    public void onMsgSend(IMMessage message) {
        // add to listView and refresh
//        saveMessage(message, false);
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

		NIMClient.getService(ChatRoomService.class).sendMessage(message, false)
				.setCallback(new RequestCallback<Void>() {
					@Override
					public void onSuccess(Void param) {
						XLog.i("send messsage success");
					}

					@Override
					public void onFailed(int code) {
						if (code == ResponseCode.RES_CHATROOM_MUTED) {
							Toast.makeText(getActivity(), "�û�������",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getActivity(),
									"��Ϣ����ʧ�ܣ�code:" + code, Toast.LENGTH_SHORT)
									.show();
						}
					}

					@Override
					public void onException(Throwable exception) {
						Toast.makeText(getActivity(), "��Ϣ����ʧ�ܣ�",
								Toast.LENGTH_SHORT).show();
					}
				});
		onMsgSend(msg);
		return true;
    }
    
    /**
     * *************************** ��Ա�������� ****************************
     */
    private void register(boolean register) {
    	//��ȥ��������ȡ���е������ҳ�Ա��Ϣ
    	ChatRoomMemberCache.getInstance().fetchMember(chatRoom.getChatroomid(), Config.User.getUserName(), new SimpleCallback<ChatRoomMember>(){

			@Override
			public void onResult(boolean success, ChatRoomMember result) {
				if (result != null && result.getNick() != null)
				    XLog.i("fetch member success: " + result.getNick());
			}});
    	registerRecObservers(register);
    	registerObservers(register);
    }

	private void createChatMessage(IMMessage message, String nickname)
	{
		XLog.i("createChatMessage text" );
		if (message == null || nickname == null)
		{
			return;
		}
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setType(MessageType.text);
		chatMessage.setAccount(message.getFromAccount());
		chatMessage.setChatRoomId(message.getSessionId());
		chatMessage.setContent(message.getContent());
		chatMessage.setNickName(nickname);
		
    	addMessage(chatMessage, false);
		adapter.notifyDataSetChanged();
	}
	
	private void createChatMessage(ChatRoomMember chatMember, String content)
	{
		XLog.i("createChatMessage notification" );
		if (chatMember == null || content == null)
		{
			return;
		}
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setType(MessageType.notificatijon);
		chatMessage.setAccount(chatMember.getAccount());
		chatMessage.setChatRoomId(chatMember.getRoomId());
		chatMessage.setContent(content);
		chatMessage.setNickName(chatMember.getNick());
    	addMessage(chatMessage, false);
		adapter.notifyDataSetChanged();
	}
    
    public void registerRecObservers(boolean register) {
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
                else if(message.getMsgType() == MsgTypeEnum.text)
                {
                	createChatMessage(message, NimUserInfoCache.getInstance().getUserDisplayName(message.getFromAccount()));
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
		ChatRoomNotificationAttachment attachment = (ChatRoomNotificationAttachment) message
				.getAttachment();
		List<String> targets = attachment.getTargets();

		if (targets != null) {
			for (String target : targets) {
				ChatRoomMember member = ChatRoomMemberCache.getInstance().getChatRoomMember(roomId, target);
				if (member != null) {
					if (member.getAccount() != null) {
						XLog.i("member get account: " + member.getAccount());
						XLog.i("member get nickname: " + member.getNick());
					}
				}
				XLog.i("attachment.getType(): " + attachment.getType());
				switch (attachment.getType()) 
				{
				case ChatRoomMemberIn:
					XLog.i("---on room member in-----" );
		        	String notificatioinIn = ChatRoomNotificationHelper.buildText("��ӭ", member.getNick(), "����ֱ����");
		            createChatMessage(member, notificatioinIn);
					break;
				case ChatRoomMemberExit:
					XLog.i("---on room member exit-----");
		        	String notificatioinExit = ChatRoomNotificationHelper.buildText("", member.getNick(), "�뿪��ֱ����");
		        	createChatMessage(member, notificatioinExit);
					break;

	            default:
	                break;
				}
			}
		}
	}
	
    /**
     * ************************* �۲��� ********************************
     */
    private void registerObservers(boolean register) {
        ChatRoomServiceObserver service = NIMClient.getService(ChatRoomServiceObserver.class);
        service.observeMsgStatus(messageStatusObserver, register);
    }

    /**
     * ��Ϣ״̬�仯�۲���
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

}
