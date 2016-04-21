package com.BC.entertainment.chatroom.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.BC.entertainment.adapter.RecyclerViewAdapter;
import com.BC.entertainment.adapter.RecyclerViewAdapter.OnItemClickListener;
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
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.constant.MemberType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.adapter.CommonAdapter;
import com.summer.config.Config;
import com.summer.logger.XLog;
import com.summer.view.CircularImage;

/**
 * ��������Ϣ�շ�ģ��
 * @author wen zhong
 *
 */
public class ChatRoomPanel {

    private static final int MESSAGE_CAPACITY = 500;
    
    private static final int LIMIT = 100;

    // container
    private Container container;
    private View rootView;
    private Handler uiHandler;
    
	private Map<String, Map<String, ChatRoomMember>> cache = new HashMap<String, Map<String, ChatRoomMember>>();

    // message list view
    private ListView messageListView;
    private LinkedList<IMMessage> items;
    
    private LinkedList<ChatRoomMember> onlinePeopleitems;
    
    //��������������
    private Map<String, ChatRoomMember> memberCache = new ConcurrentHashMap<>();
    
    private boolean isNormalEmpty = false; // �̶���Ա�Ƿ���ȡ��
    
    private long updateTime = 0; // ���ο͵�updateTime
    
    private long enterTime = 0; // �ο͵�enterTime
    
    private CommonAdapter<IMMessage> adapter;

	private CircularImage headPortrait;
	
	private ChatRoomMember master;//����Ա�����Ϣ
	
	private RecyclerView recycleView;
	
	private RecyclerViewAdapter recycleAdapter;
	
	private TextView onlinePeople;//�ܵ���������
	
	private Map<String, List<SimpleCallback<ChatRoomMember>>> frequencyLimitCache = new HashMap<String, List<SimpleCallback<ChatRoomMember>>>(); // �ظ�������
	
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
    
    public ChatRoomPanel(Container container, View rootView) {
        this.container = container;
        this.rootView = rootView;
        init();
    }
    
    private void init() {
        initListView();
        initPortrait();
        this.uiHandler = new Handler();
        initOnlinePortrait();
        initOnlinePeople();
        fetchPortrait();
        fetchOnlinePeople();
    }
    
    /**
     * ��������Ϣ�б�
     */
    private void initListView(){
    	items = new LinkedList<>();
    	
    	messageListView = (ListView)rootView.findViewById(R.id.messageListView);
    	
    	adapter = new CommonAdapter<IMMessage>(container.activity, R.layout.fragment_message_item, 
				items){
					@Override
					public void convert(
							ViewHolder holder,
							IMMessage item) {
						holder.setTextColor(R.id.txtName, Color.parseColor("#EEB422"));
						if (item.getMsgType() == MsgTypeEnum.notification)
						{
					 		try {
								ChatRoomNotificationAttachment attachment = (ChatRoomNotificationAttachment) item
										.getAttachment();
								holder.setText(R.id.txtName, "ϵͳ��Ϣ��");
								if( item.getDirect() == MsgDirectionEnum.In )
								{
									holder.setText(R.id.txtContent, "��ӭ"+ attachment.getOperatorNick() + "����ֱ����");
								}
								else if( item.getDirect() == MsgDirectionEnum.Out )
								{
									holder.setText(R.id.txtContent, (attachment.getOperatorNick() == null ? "" : attachment.getOperatorNick()) + "�뿪��ֱ����");
								}
								holder.setTextColor(R.id.txtContent, Color.parseColor("#8B658B"));

							} catch (Exception e) {
								e.printStackTrace();
								XLog.i("may null point exception ");
							}
						}
						else if (item.getMsgType() == MsgTypeEnum.text)
						{
							try {
								ChatRoomMessage message  = (ChatRoomMessage)item;
								if (message.getDirect() == MsgDirectionEnum.Out)
								{
									//����ȥ����Ϣ
									holder.setText(R.id.txtName, Config.User.getNickName() + ":");
									holder.setText(R.id.txtContent, message.getContent());
								}
								else if (message.getDirect() == MsgDirectionEnum.In)
								{
									//���ܵ�����Ϣ
									holder.setText(R.id.txtName, message.getChatRoomMessageExtension().getSenderNick() + ":");
									holder.setText(R.id.txtContent, message.getContent());
								}

								holder.setTextColor(R.id.txtContent, Color.parseColor("#FFFFFF"));
							} catch (Exception e) {
								e.printStackTrace();
								XLog.e("may null point exception ");
							}
						}

					}};
		messageListView.setAdapter(adapter);
    }
    
    /**
     * ����ͷ��
     */
    private void initPortrait()
    {
    	headPortrait = (CircularImage) rootView.findViewById(R.id.portrait);
		Glide.with(container.activity)
		.load(MainActivity.personalInformation.getHead_portrait())
		.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
		.placeholder(R.drawable.avatar_def).into(headPortrait);
    }
    
    /**
     * ��ʼ����������ͷ���б�
     */
    private void initOnlinePortrait()
    {
    	onlinePeopleitems = new LinkedList<ChatRoomMember>();
    	recycleView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
    	
    	recycleAdapter = new RecyclerViewAdapter(container.activity, onlinePeopleitems);
    	
    	recycleView.setAdapter(recycleAdapter);
    	
        recycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        recycleView.setItemAnimator(new DefaultItemAnimator());//more�Ķ���Ч��
        recycleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
    }
    
    
    /**
     * ��ʼ����������
     */
    private void initOnlinePeople()
    {
    	onlinePeople = (TextView)rootView.findViewById(R.id.txtViewOnlinePeople);
    }
    
    /**
     * ��ȡ��������
     */
    private void fetchOnlinePeople()
    {
        container.activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
            	fetchData();
            }
        });
    }

    /**
     * ��ȡ����ͷ��
     */
    public void fetchPortrait()
    {
        container.activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
            	fetchRoomInfo();
            }
        });
    }
    
    // ˢ����Ϣ�б�
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
                if (message.getMsgType() == MsgTypeEnum.notification)
                {
                	handleNotification(message);
                }
                addedListItems.add(message);
                needRefresh = true;
                XLog.i(message.getMsgType());
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
            	if (message.getDirect() == MsgDirectionEnum.In)
            	{
            		addMembers(member, false);
            	}
            	else if(message.getDirect() == MsgDirectionEnum.Out)
            	{
            	    removeMembers(member);	
            	}
            }
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
    }
    
    public void saveMyMember(ChatRoomMember chatRoomMember) {
        saveMember(chatRoomMember);
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
    
    public boolean isMyMessage(IMMessage message) {
        return message.getSessionType() == container.sessionType
                && message.getSessionId() != null
                && message.getSessionId().equals(container.account);
    }
    
    private void fetchData() {
        if (!isNormalEmpty) {
            // ��ȡ�̶����߳�Ա
            getMembers(MemberQueryType.ONLINE_NORMAL, updateTime, 0);
        } else {
            // ��ȡ�ǹ̶���Ա
            getMembers(MemberQueryType.GUEST, enterTime, 0);
        }
    }
    
    /**
     * ��ȡ��Ա�б�
     */
    private void getMembers(final MemberQueryType memberQueryType, final long time, int limit) {
        fetchRoomMembers(container.account, memberQueryType, time, (LIMIT - limit), new SimpleCallback<List<ChatRoomMember>>() {
            @Override
            public void onResult(boolean success, List<ChatRoomMember> result) {
                if (success) {

                    addMembers(result);

                    if (memberQueryType == MemberQueryType.ONLINE_NORMAL && result.size() < LIMIT) {
                        isNormalEmpty = true; // �̶���Ա�Ѿ�����
                        getMembers(MemberQueryType.GUEST, enterTime, result.size());
                    }
                }
            }
        });
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
     * ��ӵ�������������
     * @param members
     */
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

            onlinePeopleitems.add(member);
        }
        Collections.sort(onlinePeopleitems, comp);
        recycleAdapter.notifyDataSetChanged();
        if (onlinePeople != null)
        {
        	onlinePeople.setText(String.valueOf(items == null ? 0 : items.size()));
        }
    }
    
    /**
     * ���������뿪ʱ����������
     * @param member
     */
    public void removeMembers(ChatRoomMember member)
    {
        if (member == null) {
            return;
        }
        if (onlinePeopleitems.size() >= 1) {
            if (memberCache.containsKey(member.getAccount())) {
            	onlinePeopleitems.remove(memberCache.get(member.getAccount()));
                memberCache.remove(member.getAccount());
                if (onlinePeople != null)
                {
                	onlinePeople.setText(String.valueOf(onlinePeopleitems == null ? 0 : onlinePeopleitems.size()));
                }
            }
        }
    }
    
    /**
     * �����������Ա����������
     * @param member
     * @param addFirst
     */
    public void addMembers(ChatRoomMember member, boolean addFirst) {
        if (member == null) {
            return;
        }

        if (onlinePeopleitems.size() >= LIMIT) {
        	onlinePeopleitems.poll();
        }

        if (addFirst) {
            if (memberCache.containsKey(member.getAccount())) {
            	onlinePeopleitems.remove(memberCache.get(member.getAccount()));
            }
            memberCache.put(member.getAccount(), member);
            onlinePeopleitems.add(0, member);
        } else {
            if (memberCache.containsKey(member.getAccount())) {
            	onlinePeopleitems.remove(memberCache.get(member.getAccount()));
            }
            memberCache.put(member.getAccount(), member);
            onlinePeopleitems.add(member);
        }
        Collections.sort(onlinePeopleitems, comp);
        recycleAdapter.notifyDataSetChanged();
        if (onlinePeople != null)
        {
        	onlinePeople.setText(String.valueOf(onlinePeopleitems == null ? 0 : onlinePeopleitems.size()));
        }
    }
    
    // ������Ϣ�󣬸��±�����Ϣ�б�
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
    	master = getChatRoomMember(roomInfo.getRoomId(), roomInfo.getCreator());
        if (master != null) {
            updatePortraitView(roomInfo);
        } else {
            fetchMember(roomInfo.getRoomId(), roomInfo.getCreator(),
                    new SimpleCallback<ChatRoomMember>() {
                        @Override
                        public void onResult(boolean success, ChatRoomMember result) {
                            if (success) {
                                master = result;
                                updatePortraitView(roomInfo);
                            }
                        }
                    });
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
    
    private void updatePortraitView(ChatRoomInfo chatRoomInfo){
		Glide.with(container.activity)
		.load(master.getAvatar())
		.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
		.placeholder(R.drawable.avatar_def).into(headPortrait);
		XLog.i("fetch master portrait seccuss");
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
             onIncomingMessage(messages);
         }
     };
     
}
