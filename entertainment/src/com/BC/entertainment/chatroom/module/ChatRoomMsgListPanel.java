package com.BC.entertainment.chatroom.module;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.os.Handler;
import android.view.View;
import android.widget.ListView;

import com.BC.entertainment.chatroom.helper.ChatRoomMemberCache;
import com.BC.entertainmentgravitation.MainActivity;
import com.BC.entertainmentgravitation.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.common.ui.listview.ListViewUtil;
import com.netease.nim.uikit.session.module.Container;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.adapter.CommonAdapter;
import com.summer.logger.XLog;
import com.summer.view.CircularImage;

/**
 * 聊天室消息收发模块
 * @author wen zhong
 *
 */
public class ChatRoomMsgListPanel {

    private static final int MESSAGE_CAPACITY = 500;

    // container
    private Container container;
    private View rootView;
    private Handler uiHandler;

    // message list view
    private ListView messageListView;
    private LinkedList<IMMessage> items;
    
    private CommonAdapter<IMMessage> adapter;

	private CircularImage headPortrait;
	
	private ChatRoomMember master;//管理员相关信息
    
    public ChatRoomMsgListPanel(Container container, View rootView) {
        this.container = container;
        this.rootView = rootView;
        init();
    }
    
    private void init() {
        initListView();
        initPortrait();
        this.uiHandler = new Handler();
        fetchPortrait();
    }
    
    private void initListView(){
    	items = new LinkedList<>();
    	
    	messageListView = (ListView)rootView.findViewById(R.id.messageListView);
    	
    	adapter = new CommonAdapter<IMMessage>(container.activity, R.layout.fragment_message_item, 
				items){
					@Override
					public void convert(
							ViewHolder holder,
							IMMessage item) {
						holder.setText(R.id.txtName, item.getFromAccount() + ": ");
						holder.setText(R.id.txtContent, item.getContent());
					}};
		messageListView.setAdapter(adapter);
    }
    
    private void initPortrait()
    {
    	headPortrait = (CircularImage) rootView.findViewById(R.id.portrait);
		Glide.with(container.activity)
		.load(MainActivity.personalInformation.getHead_portrait())
		.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
		.placeholder(R.drawable.avatar_def).into(headPortrait);
    }
    
    //加载主播的头像
    public void fetchPortrait()
    {
        container.activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
            	fetchRoomInfo();
            }
        });
    }
    
    // 刷新消息列表
    public void refreshMessageList() {
        container.activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
    
    public void scrollToBottom() {
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ListViewUtil.scrollToBottom(messageListView);
            }
        }, 200);
    }
    
    public void onIncomingMessage(List<ChatRoomMessage> messages) {
        boolean needScrollToBottom = ListViewUtil.isLastMessageVisible(messageListView);
        boolean needRefresh = false;
        List<IMMessage> addedListItems = new ArrayList<>(messages.size());
        for (IMMessage message : messages) {
            if (isMyMessage(message)) {
                saveMessage(message, false);
                addedListItems.add(message);
                needRefresh = true;
            }
        }
        if (needRefresh) {
            adapter.notifyDataSetChanged();
        }

        // incoming messages tip
        IMMessage lastMsg = messages.get(messages.size() - 1);
        if (isMyMessage(lastMsg) && needScrollToBottom) {
            ListViewUtil.scrollToBottom(messageListView);
        }
    }
    
    public boolean isMyMessage(IMMessage message) {
        return message.getSessionType() == container.sessionType
                && message.getSessionId() != null
                && message.getSessionId().equals(container.account);
    }
    
    // 发送消息后，更新本地消息列表
    public void onMsgSend(IMMessage message) {
        // add to listView and refresh
        saveMessage(message, false);
        List<IMMessage> addedListItems = new ArrayList<>(1);
        addedListItems.add(message);

        adapter.notifyDataSetChanged();
        ListViewUtil.scrollToBottom(messageListView);
    }

    public void saveMessage(final IMMessage message, boolean addFirst) {
        if (message == null) {
            return;
        }

        if (items.size() >= MESSAGE_CAPACITY) {
            items.poll();
        }

        if (addFirst) {
            items.add(0, message);
        } else {
            items.add(message);
        }
    }
    
    public void fetchRoomInfo(){
    	NIMClient.getService(ChatRoomService.class).fetchRoomInfo(container.account).setCallback(new RequestCallback<ChatRoomInfo>(){

			@Override
			public void onException(Throwable code) {
				XLog.i("fetch room info failed:" + code);
			}

			@Override
			public void onFailed(int code) {
				XLog.i("fetch room info failed:" + code);
			}

			@Override
			public void onSuccess(ChatRoomInfo param) {
				getChatRoomMaster(param);
			}
    		
    	});
    }

    private void getChatRoomMaster(final ChatRoomInfo roomInfo) {
    	master = ChatRoomMemberCache.getInstance().getChatRoomMember(roomInfo.getRoomId(), roomInfo.getCreator());
        if (master != null) {
            updateView(roomInfo);
        } else {
            ChatRoomMemberCache.getInstance().fetchMember(roomInfo.getRoomId(), roomInfo.getCreator(),
                    new SimpleCallback<ChatRoomMember>() {
                        @Override
                        public void onResult(boolean success, ChatRoomMember result) {
                            if (success) {
                                master = result;
                                updateView(roomInfo);
                            }
                        }
                    });
        }
    }
    
    private void updateView(ChatRoomInfo chatRoomInfo){
		Glide.with(container.activity)
		.load(master.getAvatar())
		.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
		.placeholder(R.drawable.avatar_def).into(headPortrait);
    }
    
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
                 else if(message.getMsgType() == MsgTypeEnum.text)
                 {
                 }
             	XLog.i("message content: " + message.getContent());
             	XLog.i("message uid: " + message.getUuid());
             	XLog.i("message account: " + message.getFromAccount());
             	XLog.i("messsage session id: " + message.getSessionId());
             	XLog.i("messsage msg type: " + message.getMsgType());
             	XLog.i("messsage session type: " + message.getSessionType());
             }
             onIncomingMessage(messages);
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
 					XLog.i(message.getRemoteExtension().get("nickname"));
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
 					break;
 				case ChatRoomMemberExit:
 					XLog.i("---on room member exit-----");
 					break;
 	            default:
 	                break;
 				}
 			}
 		}
 	}
}
