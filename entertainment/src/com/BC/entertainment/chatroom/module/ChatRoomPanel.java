package com.BC.entertainment.chatroom.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.BC.entertainment.adapter.RecyclerViewAdapter;
import com.BC.entertainment.adapter.RecyclerViewAdapter.OnItemClickListener;
import com.BC.entertainment.cache.ChatRoomCache;
import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.chatroom.extension.BaseEmotion;
import com.BC.entertainment.chatroom.extension.CustomAttachment;
import com.BC.entertainment.chatroom.extension.CustomAttachmentType;
import com.BC.entertainment.chatroom.extension.EmotionAttachment;
import com.BC.entertainment.chatroom.extension.FontAttachment;
import com.BC.entertainment.chatroom.gift.GiftCategory;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.YubiAssets;
import com.BC.entertainmentgravitation.fragment.ExitFragmentListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.common.ui.listview.ListViewUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.adapter.CommonAdapter;
import com.summer.adapter.CommonAdapter.ViewHolder;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.handler.InfoHandler.InfoReceiver;
import com.summer.json.Entity;
import com.summer.logger.XLog;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.summer.view.CircularImage;
import com.summer.view.Pandamate;

/**
 * 聊天室消息收发模块
 * @author wen zhong
 *
 */
public class ChatRoomPanel {

    private static final int MESSAGE_CAPACITY = 500;
    
    private static final int LIMIT = 100;
    
    private final int tipValue = 10;//一条消息10个娱币

    // container
    private Container container;
    private View rootView;
    
    private ImageView imageViewAnimation;
    private Handler uiHandler;
    
//	private Map<String, Map<String, ChatRoomMember>> cache = new HashMap<String, Map<String, ChatRoomMember>>();

    // message list view
    private ListView messageListView;
    private LinkedList<IMMessage> items;//聊天室消息列表
    
//    private LinkedList<ChatRoomMember> ChatRoomCache.getInstance().getOnlinePeopleitems();
    
    private boolean isNormalEmpty = false; // 固定成员是否拉取完
    
    private long updateTime = 0; // 非游客的updateTime
    
    private long enterTime = 0; // 游客的enterTime
    
    private CommonAdapter<IMMessage> adapter;

	private CircularImage headPortrait;
	
	private RecyclerView recycleView;
	
	private RecyclerViewAdapter recycleAdapter;
	
	private TextView onlinePeople;//总的在线人数
	
	private long totalPeople;//总的观看人数
	
	public DanmakuPanel danmakuPanel;
	
	private ExitFragmentListener exitListener;
	
	private Gson gson;
	
	public boolean fetch = false;
	
    public ChatRoomPanel(Container container, View rootView, DanmakuPanel danmakuPanel, ExitFragmentListener exitListener) {
        this.container = container;
        this.rootView = rootView;
        this.danmakuPanel = danmakuPanel;
        this.exitListener = exitListener;
        gson = new Gson();
        init();
    }
    
    private void init() {
    	registerObservers(true);
        updateVideoStatus(false);//更新聊天室状态
        initListView();//初始化和处理聊天室消息列表
        initPortrait();
        this.uiHandler = new Handler();
        initOnlinePortrait();//初始化在线人数头像列表
//        fetchPortrait();
//        fetchOnlinePeople();
//        fetchRoomInfoTask();
    }
    
    private void fetchRoomInfoTask()
    {
    	fetch = true;
    	new Thread(new Runnable(){

			@Override
			public void run() {
				while (fetch)
				{
					try {
//						fetchData();
//						if (container.chatRoom.isMaster())
//						{
//							getMembers(MemberQueryType.GUEST, enterTime, 0);
//						}
//						else
						{
							getMembers(MemberQueryType.ONLINE_NORMAL, updateTime, 0);
						}
						

						Thread.sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
    		
    	}).start();
    }
    
    /**
     * 显示或者隐藏消息列表
     * @param isShow
     */
    public void showMessageListView(boolean isShow)
    {
    	if (isShow)
    	{
    		messageListView.setVisibility(View.VISIBLE);
    	}
    	else
    	{
    		messageListView.setVisibility(View.GONE);
    	}
    }
    
    /**
     * 初始化和处理聊天室消息列表
     */
    private void initListView(){
    	items = new LinkedList<>();
    	
    	messageListView = (ListView)rootView.findViewById(R.id.messageListView);
    	
    	imageViewAnimation = (ImageView)rootView.findViewById(R.id.imageViewAnimation);
    	
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
								holder.setText(R.id.txtName, "系统消息：");
								if (attachment.getType() == NotificationType.ChatRoomMemberIn)
								{
									holder.setText(R.id.txtContent, "欢迎"+ attachment.getOperatorNick() + "进入直播间");
								}
								else if (attachment.getType() == NotificationType.ChatRoomMemberExit)
								{
									holder.setText(R.id.txtContent, (attachment.getOperatorNick() == null ? "" : attachment.getOperatorNick()) + "离开了直播间");
									XLog.i("incoming notification message in");
								}
								holder.setTextColor(R.id.txtContent, Color.parseColor("#8B658B"));

							} catch (Exception e) {
								e.printStackTrace();
								XLog.i("may null point exception ");
							}
						}
		                else if(item.getMsgType() == MsgTypeEnum.custom)
		                {
							handlerCustomMessage(holder, item);
		                }
						else if (item.getMsgType() == MsgTypeEnum.text)
						{
							try {
								ChatRoomMessage message  = (ChatRoomMessage)item;
								if (message.getDirect() == MsgDirectionEnum.Out)
								{
									//发出去的消息
									holder.setText(R.id.txtName, Config.User.getNickName() + ":");
									holder.setText(R.id.txtContent, message.getContent());
									XLog.i("incoming text message out: " + message.getContent());
								}
								else if (message.getDirect() == MsgDirectionEnum.In)
								{
									//接受到的消息
									holder.setText(R.id.txtName, message.getChatRoomMessageExtension().getSenderNick() + ":");
									holder.setText(R.id.txtContent, message.getContent());
									XLog.i("incoming text message in: " + message.getContent());
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
     * 处理自定义字体或者礼物消息
     * @param holder
     * @param message
     */
    private void handlerCustomMessage(@SuppressWarnings("rawtypes") ViewHolder holder, IMMessage message)
    {
    	if (message != null)
    	{
        	holder.setTextColor(R.id.txtName, Color.parseColor("#EEB422"));
        	holder.setText(R.id.txtName, "系统消息：");
        	CustomAttachment customAttachment = (CustomAttachment)message.getAttachment();
        	ChatRoomMember chatRoomMember = ChatRoomCache.getInstance().getChatRoomMember(message.getFromAccount());
        	switch(customAttachment.getType())
        	{
        	case CustomAttachmentType.emotion:
        		EmotionAttachment emotionAttachment = (EmotionAttachment)customAttachment;
        		if (emotionAttachment != null)
        		{
        			String emotionName = emotionAttachment.getEmotion().getName();
        			holder.setText(R.id.txtContent, (chatRoomMember == null ? "" : chatRoomMember.getNick()) + " 送来了 " + emotionAttachment.getEmotion().getName());
        			XLog.i("font gift name: " + emotionName);
        			holder.setTextColor(R.id.txtContent, Color.parseColor("#8B658B"));
        		}
        		
        		break;
        	case CustomAttachmentType.font:
        		FontAttachment fontAttachment = (FontAttachment)customAttachment;
        		if (fontAttachment != null)
        		{
        			String fontName = fontAttachment.getEmotion().getName();
        			holder.setText(R.id.txtContent, (chatRoomMember == null ? "" : chatRoomMember.getNick()) + " 送来了 " + fontAttachment.getEmotion().getName());
        			XLog.i("font gift name: " + fontName);
        			holder.setTextColor(R.id.txtContent, Color.parseColor("#8B658B"));
        		}
        		break;
        	}
    	}

    }
    
    private void handlerCustomMessage(IMMessage message)
    {
    	if( message != null)
    	{
        	CustomAttachment customAttachment = (CustomAttachment)message.getAttachment();
        	switch(customAttachment.getType())
        	{
        	case CustomAttachmentType.emotion:
        		EmotionAttachment emotionAttachment = (EmotionAttachment)customAttachment;
        		if (emotionAttachment != null)
        		{
        			showAnimate(emotionAttachment.getEmotion());
        		}
        		break;
        	case CustomAttachmentType.font:
        		FontAttachment fontAttachment = (FontAttachment)customAttachment;
        		if (fontAttachment != null)
        		{
        			showAnimate(fontAttachment.getEmotion());
        		}
        		break;
        	}
    	}

    }
    
    private void handlerSendCustomMessage(IMMessage message)
    {
    	if( message != null)
    	{
        	CustomAttachment customAttachment = (CustomAttachment)message.getAttachment();
        	switch(customAttachment.getType())
        	{
        	case CustomAttachmentType.emotion:
        		EmotionAttachment emotionAttachment = (EmotionAttachment)customAttachment;
        		if (emotionAttachment != null)
        		{
        			showAnimate(emotionAttachment.getEmotion());
        		}
        		sendChatRoomGift(emotionAttachment.getEmotion(), customAttachment.getType());
        		break;
        	case CustomAttachmentType.font:
        		FontAttachment fontAttachment = (FontAttachment)customAttachment;
        		if (fontAttachment != null)
        		{
        			showAnimate(fontAttachment.getEmotion());
        		}
        		sendChatRoomGift(fontAttachment.getEmotion(), customAttachment.getType());
        		break;
        	}
    	}

    }
    
    /**
     * 主播头像
     */
    private void initPortrait()
    {
    	headPortrait = (CircularImage) rootView.findViewById(R.id.portrait);
    	if (container.chatRoom.isMaster())
    	{
    		Glide.with(container.activity)
    		.load(Config.User.getImage())
    		.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
    		.placeholder(R.drawable.avatar_def).into(headPortrait);
    		XLog.i("master portrait: " + " http://app.haimianyu.cn/" + Config.User.getImage());
    	}
    	else
    	{
    		Glide.with(container.activity)
    		.load(InfoCache.getInstance().getStartInfo().getHead_portrait())
    		.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
    		.placeholder(R.drawable.avatar_def).into(headPortrait);
    		XLog.i("custom portrait: " + InfoCache.getInstance().getPersonalInfo().getHead_portrait());
    	}

    }
    
    /**
     * 初始化在线人数头像列表
     */
    private void initOnlinePortrait()
    {
    	try {
			onlinePeople = (TextView)rootView.findViewById(R.id.txtViewOnlinePeople);
			onlinePeople.setText(String.valueOf(container.chatRoom.getChatRoomInfo().getOnlineUserCount()));
		} catch (Exception e) {
			e.printStackTrace();
			XLog.i("init on line people exception" + e.getMessage());
		}
//    	ChatRoomCache.getInstance().getOnlinePeopleitems() = ChatRoomCache.getInstance().getOnlinePeopleitems();
    	recycleView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
    	
    	recycleAdapter = new RecyclerViewAdapter(container.activity, ChatRoomCache.getInstance().getOnlinePeopleitems());
    	
    	recycleView.setAdapter(recycleAdapter);
    	
        recycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        recycleView.setItemAnimator(new DefaultItemAnimator());//more的动画效果
        recycleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
    }
    
    public void showAnimate(BaseEmotion baseEmotion)
    {
    	Pandamate.animate(getDrawable(baseEmotion.getCategory()), imageViewAnimation, new Runnable() {
			
			@Override
			public void run() {
				imageViewAnimation.setVisibility(View.VISIBLE);
			}
		}, new Runnable() {
			
			@Override
			public void run() {
				imageViewAnimation.setVisibility(View.GONE);
			}
		});
    }
    
    private int getDrawable(int category)
    {
    	int drawable;
    	switch(category)
    	{
    	case GiftCategory.font_bxjj:
    		drawable = R.drawable.animation_bxjj;
    		break;
    	case GiftCategory.font_cygs:
    		drawable = R.drawable.animation_cygs;
    		break;
    	case GiftCategory.font_djxg:
    		drawable = R.drawable.animation_djxg;
    		break;
    	case GiftCategory.font_fxlp:
    		drawable = R.drawable.animation_fxlp;
    		break;
    	case GiftCategory.font_gjtl:
    		drawable = R.drawable.animation_gjtl;
    		break;
    	case GiftCategory.font_hqmm:
    		drawable = R.drawable.animation_hqmm;
    		break;
    	case GiftCategory.font_hsdd:
    		drawable = R.drawable.animation_hsdd;
    		break;
    	case GiftCategory.font_jmny:
    		drawable = R.drawable.animation_jmny;
    		break;
    	case GiftCategory.font_kxbd:
    		drawable = R.drawable.animation_kxbd;
    		break;
    	case GiftCategory.font_lswz:
    		drawable = R.drawable.animation_lswz;
    		break;
    	case GiftCategory.font_mfsw:
    		drawable = R.drawable.animation_mfsw;
    		break;
    	case GiftCategory.font_mgsr:
    		drawable = R.drawable.animation_mgsr;
    		break;
    	case GiftCategory.font_mkyx:
    		drawable = R.drawable.animation_mkyx;
    		break;
    	case GiftCategory.font_nmes:
    		drawable = R.drawable.animation_nmes;
    		break;
    	case GiftCategory.font_pfdx:
    		drawable = R.drawable.animation_pfdx;
    		break;
    	case GiftCategory.font_swzd:
    		drawable = R.drawable.animation_swzd;
    		break;
    	case GiftCategory.font_xhnf:
    		drawable = R.drawable.animation_xhnf;
    		break;
    	case GiftCategory.font_xrdj:
    		drawable = R.drawable.animation_xrdj;
    		break;
    	case GiftCategory.font_xxrk:
    		drawable = R.drawable.animation_xxrk;
    		break;
    		
    	case GiftCategory.emotion_rose:
    		drawable = R.drawable.animation_rose;
    		break;
    	case GiftCategory.emotion_flowers:
    		drawable = R.drawable.animation_flowers;
    		break;
    	case GiftCategory.emotion_kiss:
    		drawable = R.drawable.animation_kiss;
    		break;
    	case GiftCategory.emotion_touch:
    		drawable = R.drawable.animation_touch;
    		break;
    	case GiftCategory.emotion_shoes:
    		drawable = R.drawable.animation_shoes;
    		break;
    	case GiftCategory.emotion_mic:
    		drawable = R.drawable.animation_mic;
    		break;
    	case GiftCategory.emotion_car:
    		drawable = R.drawable.animation_car;
    		break;
    	case GiftCategory.emotion_plane:
    		drawable = R.drawable.animation_plane;
    		break;
    		default:
    			drawable = R.drawable.animation_bxjj;
    			break;
    	}
    	return drawable;
    }
    
    /**
     * 获取在线人数
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
     * 获取主播头像
     */
//    public void fetchPortrait()
//    {
////        container.activity.runOnUiThread(new Runnable() {
////
////            @Override
////            public void run() {
////            	fetchRoomInfo();
////            }
////        });
//    	fetchRoomInfo();
//    }
    
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
        	
        	 try {
        		 XLog.i("sessioon type: " + message.getSessionType());
        		 XLog.i("message type: " + message.getMsgType());
        		 Log.i("ChatRoomPanel","message type: " + message.getContent());
			} catch (Exception e) {
				e.printStackTrace();
			}
        	
            if (isMyMessage(message)) {
            	//保存消息到聊天室消息列表中
                saveMessage(message, false);
                danmakuPanel.showDanmaku(message);
                if (message.getMsgType() == MsgTypeEnum.notification)
                {
                	handleNotification(message);
                }
                
                else if(message.getMsgType() == MsgTypeEnum.custom)
                {
					handlerCustomMessage(message);
                }
                addedListItems.add(message);
                needRefresh = true;
                XLog.i(message.getMsgType());
            }
        }
        if (needRefresh) {
            refreshMessageList();
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

        String account = message.getFromAccount();
        ChatRoomNotificationAttachment attachment = (ChatRoomNotificationAttachment) message.getAttachment();
        danmakuPanel.showDanmaku(attachment);
        ChatRoomMember member = ChatRoomCache.getInstance().getChatRoomMember(account);
        if (attachment.getType() == NotificationType.ChatRoomMemberIn)
        {
        	totalPeople ++;
        	//进入聊天室
    		addMembers(member, false);
//    		if (member == null)
//    		{
//        		fetchMember(message.getSessionId(), account, new SimpleCallback<ChatRoomMember>() {
//    			
//    							@Override
//    							public void onResult(boolean success,
//    									ChatRoomMember result) {
//    								try {
//    									addMembers(result, false);
//    									XLog.i("fetch room member success: " + result.getNick());
//    								} catch (Exception e) {
//    									e.printStackTrace();
//    								}
//    							}
//    						});
//    		}
//    		fetchMember(roomId, account, new SimpleCallback<ChatRoomMember>() {
//
//				@Override
//				public void onResult(boolean success,
//						ChatRoomMember result) {
//					try {
//						addMembers(result, false);
//						XLog.i("fetch room member success: " + result.getNick());
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			});
    		//更新聊天室人数到后台
    		updateRoomMember();
    		
    		try {
    			XLog.i("some one in chat room, username : " + member.getAccount());
				XLog.i("some one in chat room, avatar: " + member.getAvatar());
			} catch (Exception e) {
				e.printStackTrace();
				XLog.e("exception: " + e.getMessage());
			}
        }
        else if(attachment.getType() == NotificationType.ChatRoomMemberExit)
        {
        	if(!container.chatRoom.isMaster())
        	{
            	if (exitListener != null && member != null && member.getAccount().contains(Config.User.getUserName()))
            	{
            		XLog.i("exit chat room");
            		exitListener.isExit(true, totalPeople);
            	}
        	}

        	 removeMembers(member);	
        	 //更新聊天室人数到后台
        	 updateRoomMember();
        }
    }
    
    public boolean isMyMessage(IMMessage message) {
        return message.getSessionType() == container.sessionType
                && message.getSessionId() != null
                && message.getSessionId().equals(container.chatRoom.getChatroomid());
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
        fetchRoomMembers(container.chatRoom.getChatroomid(), memberQueryType, time, (LIMIT - limit), new SimpleCallback<List<ChatRoomMember>>() {
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
									ChatRoomCache.getInstance().saveMemberCache(result);
//									saveMembers(result);
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
     * 添加到聊天室总人数
     * @param members
     */
    private void addMembers(List<ChatRoomMember> members) {
        for (ChatRoomMember member : members) {
            if (!isNormalEmpty) {
                updateTime = member.getUpdateTime();
            } else {
                enterTime = member.getEnterTime();
            }
            Map<String, ChatRoomMember> memberCache = ChatRoomCache.getInstance().getMemberCache();
//            if (memberCache.containsKey(member.getAccount())) {
//                items.remove(ChatRoomCache.getInstance().getMemberCache().get(member.getAccount()));
//            }
//            ChatRoomCache.getInstance().saveMemberCache(member);
//            ChatRoomCache.getInstance().getOnlinePeopleitems().add(member);
            if (!memberCache.containsKey(member.getAccount())) {
                ChatRoomCache.getInstance().saveMemberCache(member);
                ChatRoomCache.getInstance().getOnlinePeopleitems().add(member);
            }

        }
        Collections.sort(ChatRoomCache.getInstance().getOnlinePeopleitems(), ChatRoomComparator.comp);
//        recycleAdapter.notifyDataSetChanged();
        recycleAdapter.UpdateData();
        if (onlinePeople != null)
        {
        	onlinePeople.setText(String.valueOf(items == null ? 0 : items.size()));
        }
    }
    
    /**
     * 聊天室人离开时减少总人数
     * @param member
     */
    public void removeMembers(ChatRoomMember member)
    {
        if (member == null) {
            return;
        }
        Map<String, ChatRoomMember> memberCache = ChatRoomCache.getInstance().getMemberCache();
        if (ChatRoomCache.getInstance().getOnlinePeopleitems().size() >= 1) {
            if (memberCache.containsKey(member.getAccount())) {
            	ChatRoomCache.getInstance().getOnlinePeopleitems().remove(memberCache.get(member.getAccount()));
            	ChatRoomCache.getInstance().RemoveMemberCache(member.getAccount());
                recycleAdapter.UpdateData();
                if (onlinePeople != null)
                {
                	onlinePeople.setText(String.valueOf(ChatRoomCache.getInstance().getOnlinePeopleitems() == null ? 0 : ChatRoomCache.getInstance().getOnlinePeopleitems().size()));
                }
            }
        }
    }
    
    /**
     * 添加聊天室人员到总人数中
     * @param member
     * @param addFirst
     */
    public void addMembers(ChatRoomMember member, boolean addFirst) {
        if (member == null) {
            return;
        }

        Map<String, ChatRoomMember> memberCache = ChatRoomCache.getInstance().getMemberCache();
        if (ChatRoomCache.getInstance().getOnlinePeopleitems().size() >= LIMIT) {
        	ChatRoomCache.getInstance().getOnlinePeopleitems().poll();
        }
        if (addFirst) {
//            if (memberCache.containsKey(member.getAccount())) {
//            	ChatRoomCache.getInstance().getOnlinePeopleitems().remove(memberCache.get(member.getAccount()));
//            }
//            ChatRoomCache.getInstance().saveMemberCache(member);
//            ChatRoomCache.getInstance().getOnlinePeopleitems().add(0, member);
            if (!memberCache.containsKey(member.getAccount())) {
                ChatRoomCache.getInstance().saveMemberCache(member);
                ChatRoomCache.getInstance().getOnlinePeopleitems().add(0, member);
            }
        } else {
//            if (memberCache.containsKey(member.getAccount())) {
//            	ChatRoomCache.getInstance().getOnlinePeopleitems().remove(memberCache.get(member.getAccount()));
//            }
//            ChatRoomCache.getInstance().saveMemberCache(member);
//            ChatRoomCache.getInstance().getOnlinePeopleitems().add(member);
            if (!memberCache.containsKey(member.getAccount())) {
                ChatRoomCache.getInstance().saveMemberCache(member);
                ChatRoomCache.getInstance().getOnlinePeopleitems().add(member);
            }
        }
        Collections.sort(ChatRoomCache.getInstance().getOnlinePeopleitems(), ChatRoomComparator.comp);
        recycleAdapter.UpdateData();
        XLog.i("have notifiy data set changed");
        XLog.i("amount people: " + ChatRoomCache.getInstance().getOnlinePeopleitems().size());
        if (onlinePeople != null)
        {
        	onlinePeople.setText(String.valueOf(ChatRoomCache.getInstance().getOnlinePeopleitems() == null ? 0 : ChatRoomCache.getInstance().getOnlinePeopleitems().size()));
        }
    }
    
    // 发送消息后，更新本地消息列表
    public void onMsgSend(IMMessage message) {
        // add to listView and refresh
        saveMessage(message, false);
        List<IMMessage> addedListItems = new ArrayList<>(1);
        addedListItems.add(message);

        refreshMessageList();
        danmakuPanel.showDanmaku(message);
        if (message.getMsgType() == MsgTypeEnum.custom)
        {
            handlerSendCustomMessage(message);
        }
        else if (message.getMsgType() == MsgTypeEnum.text)
        {
        	sendChatRoomMessage(message);
        }
        ListViewUtil.scrollToBottom(messageListView);
    }
    
    /**
     * 保存所有聊天室消息
     * @param message
     * @param addFirst
     */
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
    
    /**
     * 获取聊天室主播信息
     */
//    public void fetchRoomInfo(){
//    	NIMClient.getService(ChatRoomService.class).fetchRoomInfo(container.chatRoom.getChatroomid()).setCallback(new RequestCallback<ChatRoomInfo>(){
//
//			@Override
//			public void onException(Throwable code) {
//				XLog.i("fetch room info failed:" + code);
//			}
//
//			@Override
//			public void onFailed(int code) {
//				XLog.i("fetch room info failed:" + code);
//			}
//
//			@Override
//			public void onSuccess(ChatRoomInfo param) {
////				getChatRoomMaster(param);
//				masterId = param.getCreator();
//
//
//			}
//    		
//    	});
//    }

    /**
     * 获取聊天室主播信息
     * @param roomInfo
     */
//    private void getChatRoomMaster(final ChatRoomInfo roomInfo) {
//    	master = getChatRoomMember(roomInfo.getRoomId(), roomInfo.getCreator());
//        if (master != null) {
//            updatePortraitView(roomInfo);
//            XLog.i("get chat room master success");
//        } else {
//            fetchMember(roomInfo.getRoomId(), roomInfo.getCreator(),
//                    new SimpleCallback<ChatRoomMember>() {
//                        @Override
//                        public void onResult(boolean success, ChatRoomMember result) {
//                            if (success) {
//                                master = result;
//                                updatePortraitView(roomInfo);
//                                XLog.i("get chat room master success: " + master.getAccount());
//                            }
//                        }
//                    });
//        }
//    }
    
    /**
     * 主播进入聊天室更新聊天室状态
     * @param master
     * @param isLeave
     */
    private void updateVideoStatus(boolean isLeave)
    {
    	//是主播进入聊天室才发送聊天室状态到后台
    	XLog.i("this is master: " + container.chatRoom.isMaster() );
    	if (container.chatRoom.isMaster())
    	{
        	HashMap<String, String> entity = new HashMap<String, String>();
        	entity.put("username", Config.User.getUserName());
        	if(isLeave){
        		entity.put("status", "1");
        	}
        	else
        	{
            	entity.put("status", "0");
        	}
        	XLog.i("this is master: " + entity.toString());
    		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    		addToThreadPool(Config.update_status, "send update status request", params);
    	}
    }
    
    /**
     * 如果是主播，将聊天室总人数更新到后台
     */
    private void updateRoomMember()
    {
    	//是主播才更新聊天室人数到后台
    	if (container.chatRoom.isMaster())
    	{
        	HashMap<String, String> entity = new HashMap<String, String>();
        	entity.put("username", Config.User.getUserName());
        	entity.put("peoples", String.valueOf(ChatRoomCache.getInstance().getOnlinePeopleitems().size()));
    		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    		addToThreadPool(Config.update_room, "send update room member request", params);
    	}
    }
    
    private void sendChatRoomGift(BaseEmotion baseEmotion, int type)
    {
    	//非主播可以送礼物给主播
    	try {
			if ( !container.chatRoom.isMaster())
			{
				//送礼物之前先检查余额是否充足
				XLog.i("value: " + baseEmotion.getValue());
				XLog.i("my dollar: " + InfoCache.getInstance().getPersonalInfo().getEntertainment_dollar());
				if (baseEmotion.getValue() < Integer.parseInt(InfoCache.getInstance().getPersonalInfo().getEntertainment_dollar()))
				{
			    	HashMap<String, String> entity = new HashMap<String, String>();
			    	entity.put("username", Config.User.getUserName());
			    	entity.put("user_dollar", String.valueOf(baseEmotion.getValue()));
			    	entity.put("type", String.valueOf(type));
			    	entity.put("starid", InfoCache.getInstance().getStartInfo().getUser_name());
					List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
					addToThreadPool(Config.send_gift, "send gift request", params);
					XLog.i("send gift reques");
				}
				else
				{
					//余额不足，需要充值
					ToastUtil.show(container.activity, "余额不足，赶紧充值吧");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e("NumberFormatException");
		}
    }
    
    private void sendChatRoomMessage(IMMessage message)
    {
    	//非主播可以送礼物给主播
    	try {
			if ( !container.chatRoom.isMaster())
			{
				//送礼物之前先检查余额是否充足
				XLog.i("my dollar: " + InfoCache.getInstance().getPersonalInfo().getEntertainment_dollar());
				if (tipValue < Integer.parseInt(InfoCache.getInstance().getPersonalInfo().getEntertainment_dollar()))
				{
			    	HashMap<String, String> entity = new HashMap<String, String>();
			    	entity.put("username", Config.User.getUserName());
			    	entity.put("user_dollar", String.valueOf(tipValue));
			    	entity.put("type", "-1");
			    	entity.put("starid", InfoCache.getInstance().getStartInfo().getUser_name());
					List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
					addToThreadPool(Config.send_gift, "send gift request", params);
					XLog.i("send gift reques");
				}
				else
				{
					//余额不足，需要充值
					ToastUtil.show(container.activity, "余额不足，赶紧充值吧");
				}
			}
		} catch (Exception e) {
			XLog.e("NumberFormatException");
			e.printStackTrace();
		}
    }
    
    private void addToThreadPool(int taskType, String tag, List<NameValuePair> params)
    {
    	XLog.i("add to thread pool: " + tag);
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(new InfoReceiver() {
			
			@Override
			public void onNotifyText(String notify) {
				
			}
			
			@Override
			public void onInfoReceived(int errorCode, HashMap<String, Object> items) {

		        if (errorCode == 0)
		        {
		            XLog.i(errorCode);
		            String jsonString = (String) items.get("content");
		            if (jsonString != null)
		            {
		                JSONObject object;
		                try {
		                    object = new JSONObject(jsonString);
		                    String msg = object.optString("msg");
		                    int code = object.optInt("status", -1);
		                    int taskType = (Integer) items.get("taskType");
		                    if (code == 0)
		                    {
		                        RequestSuccessful(jsonString, taskType);
		                    }
		                    else
		                    {
		                        RequestFailed(code, msg, taskType);
		                    }
		                } catch (JSONException e) {
		                    //parse error
		                    XLog.e(e);
		                    e.printStackTrace();
		                    RequestFailed(-1, "Json Parse Error", -1);
		                }
		            }
		        }
			}
		});
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
    
	private void RequestSuccessful(String jsonString, int taskType) {
		XLog.i("taskType: " + taskType + " json string: " + jsonString);
		switch(taskType)
		{
		case Config.update_status:
			XLog.i("taskType: " + taskType + " json string: " + jsonString);
			break;
		case Config.update_room:
			XLog.i("taskType: " + taskType + " json string: " + jsonString);
			break;
		case Config.send_gift:
			//礼物赠送成功，更新本地账户信息
			
			try {
				Entity<YubiAssets> baseEntity = gson.fromJson(jsonString,
						new TypeToken<Entity<YubiAssets>>() {
						}.getType());
				if(baseEntity != null)
				{
					YubiAssets assets = baseEntity.getData();
					InfoCache.getInstance().getPersonalInfo().setEntertainment_dollar(assets.getUser());
				}
			} catch (JsonSyntaxException e1) {
				e1.printStackTrace();
				XLog.e("JSONException");
			}
			XLog.i("taskType: " + taskType + " json string: " + jsonString);
			break;
		}
	}
	
    private void RequestFailed(int errcode, String message, int taskType)
    {
    	XLog.i("taskType: " + taskType + " error code: " + errcode + "error message: " + message);
		switch(taskType)
		{
		case Config.update_status:
			XLog.i("taskType: " + taskType + " error code: " + errcode + "error message: " + message);
			break;
		case Config.update_room:
			XLog.i("taskType: " + taskType + " error code: " + errcode + "error message: " + message);
			break;
		}
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
        // fetch
        List<String> accounts = new ArrayList<>(1);
        accounts.add(account);
        NIMClient.getService(ChatRoomService.class).fetchRoomMembersByIds(roomId, accounts).
        	setCallback(new RequestCallbackWrapper<List<ChatRoomMember>>(){

				@Override
				public void onResult(int code, List<ChatRoomMember> members, Throwable exception) {
	                boolean success = code == ResponseCode.RES_SUCCESS && members != null && !members.isEmpty();
	                
	                // cache
	                if (success) {
	                	ChatRoomCache.getInstance().saveMemberCache(members);
	                } else {
	                    XLog.i("fetch chat room member failed, code=" + code);
	                }
				}});
    }
    
    /**
     * 更新聊天室主播头像
     * @param chatRoomInfo
     */
//    private void updatePortraitView(ChatRoomInfo chatRoomInfo){
//		Glide.with(container.activity)
//		.load(master.getAvatar())
//		.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
//		.placeholder(R.drawable.avatar_def).into(headPortrait);
//		XLog.i("fetch master portrait seccuss, avator address: " + master.getAvatar());
//    }
    
    /***********************************************************注册相关**********************************************************************/
    public void registerObservers(boolean register) {
    	if(! register){
    		logoutChatRoom();
    	}
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, register);
    }
    
	private void logoutChatRoom() {
		if (container.chatRoom != null && container.chatRoom.isMaster())
		{
			//主播离开需要更新直播间状态
			updateVideoStatus(true);
		}
		fetch  = false;
		NIMClient.getService(ChatRoomService.class).exitChatRoom(
				container.chatRoom.getChatroomid());
	}
    
    @SuppressWarnings("serial")
 	private Observer<List<ChatRoomMessage>> incomingChatRoomMsg = new Observer<List<ChatRoomMessage>>() {
         @Override
         public void onEvent(List<ChatRoomMessage> messages) {
         	XLog.i("incomingChatRoomMsg" + messages.size());
         	Log.i("ChatRoomPanel", "Log incomingChatRoomMsg: " + messages.size());
             if (messages == null || messages.isEmpty()) {
                 return;
             }
             onIncomingMessage(messages);
         }
     };
}
