package com.BC.entertainmentgravitation.fragment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.BC.entertainment.adapter.RecyclerViewAdapter;
import com.BC.entertainment.adapter.RecyclerViewAdapter.OnItemClickListener;
import com.BC.entertainment.cache.ChatCache;
import com.BC.entertainment.cache.GiftCache;
import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.chatroom.extension.BaseEmotion;
import com.BC.entertainment.chatroom.extension.BubbleAttachment;
import com.BC.entertainment.chatroom.extension.CustomAttachment;
import com.BC.entertainment.chatroom.extension.CustomAttachmentType;
import com.BC.entertainment.chatroom.extension.EmotionAttachment;
import com.BC.entertainment.chatroom.extension.FontAttachment;
import com.BC.entertainment.chatroom.gift.Gift;
import com.BC.entertainment.chatroom.gift.GiftHelper;
import com.BC.entertainment.chatroom.module.BubbingPanel;
import com.BC.entertainment.chatroom.module.Bubbling;
import com.BC.entertainment.chatroom.module.Container;
import com.BC.entertainment.chatroom.module.DanmakuPanel;
import com.BC.entertainment.chatroom.module.InputPannel;
import com.BC.entertainment.chatroom.module.ModuleProxy;
import com.BC.entertainmentgravitation.PersonalHomeActivity;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.dialog.ApplauseGiveConcern;
import com.BC.entertainmentgravitation.dialog.InfoDialog;
import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.BC.entertainmentgravitation.entity.Member;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.common.ui.listview.ListViewUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.adapter.CommonAdapter;
import com.summer.adapter.CommonAdapter.ViewHolder;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.fragment.BaseFragment;
import com.summer.handler.InfoHandler;
import com.summer.handler.InfoHandler.InfoReceiver;
import com.summer.json.Entity;
import com.summer.logger.XLog;
import com.summer.task.HttpBaseTask;
import com.summer.task.HttpTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.summer.view.CircularImage;
import com.summer.view.Pandamate;

public class PushFragment extends BaseFragment implements OnClickListener, ModuleProxy{

	private Context context;
	private static final int MESSAGE_CAPACITY = 500;
	
	private View rootView;
	
	private ImageView btnChat;//聊天
	private ImageView btnShare;//分享
	private ImageView btnFocus;//关注
	private ImageView btnClose;//关闭
	private ImageView btnSwitch;//切换摄像头
	private ImageView btnGift;//送礼物
	private ImageView btnInvest;//投资
	private ImageView btnDivest;//撤资
	private LinearLayout layoutInput;
	private TextView totalPiao;//yupiao
	private RelativeLayout functionView;//底部功能键根布局
	
	private Bubbling bubbling;//气泡
	private boolean isFirst = true;
    // container
    private Container container;
    private ImageView imageViewAnimation;
    
    // message list view
    private ListView messageListView;
    private LinkedList<IMMessage> items;//聊天室消息列表
    private CommonAdapter<IMMessage> adapter;
	private CircularImage headPortrait;
	private RecyclerView recycleView;
	private RecyclerViewAdapter recycleAdapter;
	private TextView onlinePeople;//总的在线人数
	private Gson gson;
	
    //module
    private InputPannel inputPanel;
    private DanmakuPanel danmakuPanel;
    private BubbingPanel bubblePanel;
    
    private HttpTask httpTask;//更新娱票线程
    private ApplauseGiveConcern applauseGiveConcern;//投资或者撤资
    
	private IPushMedia iPushMedia;
	private InfoDialog dialog;
    
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

	private ImageView btnYuPaoDetail;

	public PushFragment(Activity activity, ChatRoom chatRoom)
	{
		container = new Container(activity, chatRoom, SessionTypeEnum.ChatRoom, this);
		context = activity;
	}

	/**
	 * 直播推流时切换摄像头接口
	 * @author zhongwen
	 *
	 */
	public interface IPushMedia
	{
		void onSwitchCamera();
		void finishPushMedia();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			iPushMedia = (IPushMedia)activity;
			
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e("get switch camera exception");
		}
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		registerObservers(true);
		gson = new Gson();
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onPause() {
		if (danmakuPanel != null)
		{
			danmakuPanel.onPause();
		}
		super.onPause();
	}
	
	public void onResume() {
		if (danmakuPanel != null)
		{
			danmakuPanel.onResume();
		}
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		registerObservers(false);
		if (danmakuPanel != null)
		{
			danmakuPanel.onDestroy();
		}
		stopUpdateYuPiao();
    	ChatCache.getInstance().ClearMember();
    	super.onDestroy();
	}
	
	public void Destroy()
	{
		registerObservers(false);
		if (danmakuPanel != null)
		{
			danmakuPanel.onDestroy();
		}
		stopUpdateYuPiao();
    	ChatCache.getInstance().ClearMember();
	}

	@SuppressLint("InflateParams") @Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_push, null);
		return rootView;
	}
	
	@SuppressLint("InflateParams") @Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
        initChatView();
        
        initBubbleView();
        /**
         * 初始化聊天室和输入框控件
         */
        initializeView();
        
        updateVideoStatus(false);//更新聊天室状态
//        container.activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}
	
	private void initChatView()
	{
		initListView();
		initPortrait();
		initOnlinePortrait();
	}
	
	private void initBubbleView()
	{
		if (bubblePanel == null)
		{
			bubblePanel = new BubbingPanel(container);
		}
		bubbling = (Bubbling) rootView.findViewById(R.id.bubbling);
		bubbling.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				bubbling.start();
				bubblePanel.sendBubbling(isFirst, bubbling.getmIndex());
				if (isFirst)
				{
					isFirst = false;
				}
			}
		});
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
							IMMessage item, int position) {
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
    
	private void initializeView()
	{
		if (danmakuPanel == null)
		{
			danmakuPanel = new DanmakuPanel(container, rootView);
		}
		
		showMessageListView(true);
		
		if (inputPanel == null)
		{
			inputPanel = new InputPannel(container, rootView, GiftCache.getInstance().getListGifts(), bubbling);
		}
		layoutInput = (LinearLayout) rootView.findViewById(R.id.layout_input);
		functionView = (RelativeLayout) rootView.findViewById(R.id.layout_bottom);
		btnChat = (ImageView) rootView.findViewById(R.id.imageView_chart);
		totalPiao = (TextView) rootView.findViewById(R.id.textView_total_value);
		btnShare = (ImageView) rootView.findViewById(R.id.imageView_share);
		btnClose = (ImageView) rootView.findViewById(R.id.imageView_close);
		btnSwitch = (ImageView) rootView.findViewById(R.id.imageView_camera);
		btnGift = (ImageView) rootView.findViewById(R.id.imageView_gift);
		btnFocus = (ImageView) rootView.findViewById(R.id.imageView_focus);
		btnInvest = (ImageView) rootView.findViewById(R.id.imageView_invest);
		btnDivest = (ImageView) rootView.findViewById(R.id.imageView_divest);
		btnYuPaoDetail = (ImageView) rootView.findViewById(R.id.imageView3);
		btnYuPaoDetail.setOnClickListener(this);
		btnGift.setVisibility(View.GONE);//主播不需要送礼
		btnChat.setOnClickListener(this);
		btnShare.setOnClickListener(this);
		btnFocus.setOnClickListener(this);
		btnSwitch.setOnClickListener(this);
		btnClose.setOnClickListener(this);
		btnGift.setOnClickListener(this);
		btnInvest.setOnClickListener(this);
		btnDivest.setOnClickListener(this);
		
		startGetYuPiao();
		
		/**
		 * 初始化投资和撤资弹出对话框
		 */
		applauseGiveConcern = new ApplauseGiveConcern( container.activity,
				InfoCache.getInstance().getStartInfo().getStar_ID(), this,
				InfoCache.getInstance().getStartInfo()
						.getThe_current_hooted_thumb_up_prices(),
				InfoCache.getInstance().getStartInfo().getStage_name());
		
		rootView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showMessageListView(true);
				showFunctionView(true);
				inputPanel.hideInputMethod();
				inputPanel.hideInputBar();
				inputPanel.hideGiftLayout();
				return false;
			}
		});
	}
	
    /**
     * 主播头像
     */
    private void initPortrait()
    {
    	String image = "";
    	try {
			headPortrait = (CircularImage) rootView.findViewById(R.id.portrait);
			headPortrait.setOnClickListener(this);
			
			try {
				if (Config.User.getImage() != null)
				{
					String s[] = Config.User.getImage().split("/");
					
					if (s[2] != null && !s[2].contains("app.haimianyu.cn"))
					{
						image = "http://app.haimianyu.cn/" + Config.User.getImage();
					}
					else
					{
						image = Config.User.getImage();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Glide.with(this)
			.load(image)
			.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
			.placeholder(R.drawable.avatar_def).into(headPortrait);
			XLog.i("master portrait: " + " http://app.haimianyu.cn/" + Config.User.getImage());
		} catch (Exception e) {
			e.printStackTrace();
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
    	recycleView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
    	
    	recycleAdapter = new RecyclerViewAdapter(container.activity, ChatCache.getInstance().getOnlinePeopleitems());
    	
    	recycleView.setAdapter(recycleAdapter);
    	
        recycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        recycleView.setItemAnimator(new DefaultItemAnimator());//more的动画效果
        recycleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            	showInfoDialog(ChatCache.getInstance().getOnlinePeopleitems().get(position));
//            	ToastUtil.show(container.activity, "此功能正在努力开发中，敬请期待...");
            }
            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
    }
    
    private void showInfoDialog(final Member member)
    {
    	if (member != null)
    	{
        	final InfoDialog.Builder builder = new InfoDialog.Builder(context);
        	builder.setMember(member);
        	/**
        	 * 设置聊天室成员
        	 */
        	builder.setManagerListerner(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					
				}
        		
        	});
        	/**
        	 * 关闭
        	 */
        	builder.setCloseListerner(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (dialog != null)
					{
						dialog.dismiss();
					}
				}
			});

        	/**
        	 * 跳转到个人主页
        	 */
        	builder.setPositiveClickListener(new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent();
					intent.setClass(container.activity, PersonalHomeActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("member", member);
					intent.putExtras(bundle);
					startActivity(intent);
				}
        		
        	});

        	/**
        	 * 关注 聊天室相关成员
        	 */
        	builder.setNegativeClickListener(new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
        	
        	dialog = builder.create();

    		dialog.show();
    	}
    	
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
        	Member member = ChatCache.getInstance().getMember(message.getFromAccount());
        	switch(customAttachment.getType())
        	{
        	case CustomAttachmentType.emotion:
        		EmotionAttachment emotionAttachment = (EmotionAttachment)customAttachment;
        		if (emotionAttachment != null)
        		{
        			String emotionName = emotionAttachment.getEmotion().getName();
        			holder.setText(R.id.txtContent, (member == null ? "" : member.getNick()) + " 送来了 " + emotionAttachment.getEmotion().getName());
        			XLog.i("font gift name: " + emotionName);
        			holder.setTextColor(R.id.txtContent, Color.parseColor("#8B658B"));
        		}
        		
        		break;
        	case CustomAttachmentType.font:
        		FontAttachment fontAttachment = (FontAttachment)customAttachment;
        		if (fontAttachment != null)
        		{
        			String fontName = fontAttachment.getEmotion().getName();
        			holder.setText(R.id.txtContent, (member == null ? "" : member.getNick()) + " 送来了 " + fontAttachment.getEmotion().getName());
        			XLog.i("font gift name: " + fontName);
        			holder.setTextColor(R.id.txtContent, Color.parseColor("#8B658B"));
        		}
        		break;
        	case CustomAttachmentType.bubble:
        		
        		BubbleAttachment bubbleAttachment = (BubbleAttachment)customAttachment;
        		if (bubbleAttachment != null && bubbleAttachment.getBubble() != null)
        		{
        			if (bubbleAttachment.getBubble().isFirstSend())
        			{
            			holder.setText(R.id.txtContent, (member == null ? "" : member.getNick()) + " 我点亮了");
            			holder.setTextColor(R.id.txtContent, Color.parseColor("#8B658B"));
        			}

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
	            	//保存消息到聊天室消息列表中
	                saveMessage(message, false);
        		}
        		break;
        	case CustomAttachmentType.font:
        		FontAttachment fontAttachment = (FontAttachment)customAttachment;
        		if (fontAttachment != null)
        		{
        			showAnimate(fontAttachment.getEmotion());
	            	//保存消息到聊天室消息列表中
	                saveMessage(message, false);
        		}
        		break;
        	case CustomAttachmentType.bubble:
        		BubbleAttachment bubbleAttachment = (BubbleAttachment)customAttachment;
        		if (bubbleAttachment != null)
        		{
        			bubbling.startAnimation();
        			if (bubbleAttachment.getBubble().isFirstSend())
        			{
        				if (message.getFromAccount() != null)
        				{
        					if (!message.getFromAccount().contains(Config.User.getUserName()))
        					{
        						//接收到的是别人的消息，同时是第一次点亮，则添加到列表中显示
        		            	//保存消息到聊天室消息列表中
        		                saveMessage(message, false);
        					}
        				}
        			}
        		}
        		break;
        	}
    	}
    }
    
    public void showAnimate(BaseEmotion baseEmotion)
    {
    	Pandamate.animate(GiftHelper.getDrawable(baseEmotion.getCategory()), imageViewAnimation, new Runnable() {
			
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
    
    // 刷新消息列表
    public void refreshMessageList() {
        container.activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
    
    public void onIncomingMessage(List<ChatRoomMessage> messages) {
        boolean needScrollToBottom = ListViewUtil.isLastMessageVisible(messageListView);
        boolean needRefresh = false;
//        List<IMMessage> addedListItems = new ArrayList<>(messages.size());
        for (IMMessage message : messages) {
        	
//        	 try {
//        		 XLog.i("sessioon type: " + message.getSessionType());
//        		 XLog.i("message type: " + message.getMsgType());
//        		 Log.i("ChatRoomPanel","message type: " + message.getContent());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
        	
            if (isMyMessage(message)) {
                danmakuPanel.showDanmaku(message);
                if (message.getMsgType() == MsgTypeEnum.notification)
                {
                	handleNotification(message);
                	//保存消息到聊天室消息列表中
                    saveMessage(message, false);
                }
                else if(message.getMsgType() == MsgTypeEnum.custom)
                {
					handlerCustomMessage(message);
	            	//保存消息到聊天室消息列表中
                }
                else if(message.getMsgType() == MsgTypeEnum.text)
                {
	            	//保存消息到聊天室消息列表中
	                saveMessage(message, false);
                }
//                addedListItems.add(message);
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
        if (attachment.getType() == NotificationType.ChatRoomMemberIn)
        {
        	sendMemberRequest(account);
        }
        else if(attachment.getType() == NotificationType.ChatRoomMemberExit)
        {
        	Member member = ChatCache.getInstance().getMember(account);
        	removeMembers(member);
        	//更新聊天室人数到后台
        	updateRoomMember();
        }
    }
    
    /**
     * 如果是主播，将聊天室总人数更新到后台
     */
    private void updateRoomMember()
    {
    	//是主播才更新聊天室人数到后台
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("username", Config.User.getUserName());
    	entity.put("peoples", String.valueOf(ChatCache.getInstance().getOnlinePeopleitems().size()));
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.update_room, "send update room member request", params);
    }
    
    /**
     * 聊天室人离开时减少总人数
     * @param member
     */
    public void removeMembers(Member member)
    {
        if (member == null) {
            return;
        }
        ChatCache.getInstance().RemoveMember(member);
        recycleAdapter.UpdateData();
        if (onlinePeople != null)
        {
        	onlinePeople.setText(String.valueOf(ChatCache.getInstance().getOnlinePeopleitems() == null ? 0 : ChatCache.getInstance().getOnlinePeopleitems().size()));
        }
    }
    
    public boolean isMyMessage(IMMessage message) {
        return message.getSessionType() == container.sessionType
                && message.getSessionId() != null
                && message.getSessionId().equals(container.chatRoom.getChatroomid());
    }
    
    /**
     * 游客进入聊天室，发送获取头像信息请求
     * @param username
     */
    private void sendMemberRequest(String username)
    {
    	HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("username", username);
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	addToThreadPool(Config.member_in, "get start info", params);
    }
	
	/**
	 * 开始获取娱票线程
	 */
	private void startGetYuPiao()
	{
    	try {
			HashMap<String, String> entity = new HashMap<String, String>();
			entity.put("username", Config.User.getUserName());
			List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
			httpTask = new HttpTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, "get yu piao info", params, UrlUtil.GetUrl(Config.query_piao));
			httpTask.setTaskType(Config.query_piao);
			InfoHandler handler = new InfoHandler(this);
			httpTask.setInfoHandler(handler);
			ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e("start get yu piao exception");
		}
	}
	
    /**
     * 主播进入聊天室更新聊天室状态
     * @param master
     * @param isLeave
     */
    private void updateVideoStatus(boolean isLeave)
    {
    	//是主播进入聊天室才发送聊天室状态到后台
    	XLog.i("this is master: " + container.chatRoom.isMaster() );
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
    
    private void addToThreadPool(int taskType, String tag, List<NameValuePair> params)
    {
    	XLog.i("add to thread pool: " + tag);
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
	
	/**
	 * 停止获取娱票线程
	 */
	private void stopUpdateYuPiao()
	{
		httpTask.CancelTask();
	}
	
	/**
	 * 显示底部功能键
	 * @param isShow
	 */
	private void showFunctionView(boolean isShow)
	{
		if (isShow)
		{
			functionView.setVisibility(View.VISIBLE);
		}
		else
		{
			functionView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		/**
		 * 聊天
		 */
		case R.id.imageView_chart:
			showFunctionView(false);
			showMessageListView(true);
			layoutInput.setVisibility(View.VISIBLE);
			inputPanel.showInputBar();
			inputPanel.hideGiftLayout();
			break;
		/**
		 * 投资
		 */
		case R.id.imageView_invest:
			
			if (applauseGiveConcern != null)
			{
				applauseGiveConcern.showApplaudDialog(1);
			}

			break;
		/**
		 * 撤资	
		 */
		case R.id.imageView_divest:
			if (applauseGiveConcern != null)
			{
				applauseGiveConcern.showApplaudDialog(2);
			}
			break;
		/**
		 * 关注
		 */
		case R.id.imageView_focus:
			if (applauseGiveConcern != null)
			{
				applauseGiveConcern.sendFocusRequest();
			}
			break;
		/**
		 * 分享
		 */
		case R.id.imageView_share:

//			ToastUtil.show(getActivity(), "此功能正在完善中...");
			showShare();
			break;
		/**
		 * 切换摄像头
		 */
		case R.id.imageView_camera:
			if(iPushMedia != null)
			{
				iPushMedia.onSwitchCamera();
			}
			break;
		/**
		 * 送礼物
		 */
		case R.id.imageView_gift:
			showFunctionView(false);
			showMessageListView(false);
			layoutInput.setVisibility(View.VISIBLE);
			inputPanel.hideInputBar();
			inputPanel.showGiftLayout();
			break;
		/**
		 * 关闭
		 */
		case R.id.imageView_close:
			registerObservers(false);
			if(iPushMedia != null)
			{
				iPushMedia.finishPushMedia();
			}

			if (danmakuPanel != null)
			{
				danmakuPanel.onDestroy();
			}
			stopUpdateYuPiao();
			ChatCache.getInstance().ClearMember();
			container.activity.finish();
			break;
		/**
		 * 查看直播间娱票详情	
		 */
		case R.id.imageView3:
			ToastUtil.show(container.activity, "此功能正在努力开发中，敬请期待...");
			break;
		/**
		 * 点击主播头像
		 */
		case R.id.portrait:
			showInfoDialog(CreateMember());
			break;
		}
	}
	
	private Member CreateMember()
	{
		Member m = new Member();
		m.setId(Config.User.getClientID());
		m.setNick(Config.User.getNickName());
		m.setName(Config.User.getUserName());
		m.setPortrait(InfoCache.getInstance().getPersonalInfo().getHead_portrait());
		m.setGender(InfoCache.getInstance().getPersonalInfo().getGender());
		m.setRegion(InfoCache.getInstance().getPersonalInfo().getRegion());
		m.setConstellation(InfoCache.getInstance().getPersonalInfo().getThe_constellation());
		m.setNationality(InfoCache.getInstance().getPersonalInfo().getNationality());
		m.setMood(InfoCache.getInstance().getPersonalInfo().getIn_the_mood());
		return m;
	}
	
	private void showShare() {
		String name = Config.User.getNickName();
		ShareSDK.initSDK(container.activity, "10ee118b8af16");

		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字
		oks.setTitle("演员在直播！导演你快来......");
		oks.setText("看演员，去海绵娱直播APP!" + "(" + name
				+ "正在直播中)");
		oks.setSite(getString(R.string.app_name));
		// 分享链接地址
		oks.setUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.BC.entertainmentgravitation");
		// logo地址
		oks.setImageUrl("http://app.haimianyu.cn/DOWNLOAD/app_logo.png");
		oks.show(container.activity);
	}
	
	@Override
	public boolean sendMessage(IMMessage msg) {
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
							Toast.makeText(container.activity.getBaseContext(), "用户被禁言",Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(container.activity.getBaseContext(),"消息发送失败：code:" + code, Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onException(Throwable exception) {
						Toast.makeText(container.activity.getBaseContext(), "消息发送失败！",
								Toast.LENGTH_SHORT).show();
					}
				});
		onMsgSend(msg);
		return true;
	}
	
    /**
     *  发送消息后，更新本地消息列表
     * @param message
     */
    public void onMsgSend(IMMessage message) {
        // add to listView and refresh
        saveMessage(message, false);
        refreshMessageList();
        danmakuPanel.showDanmaku(message);
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

	@Override
	public void onInputPanelExpand() {
	}

	@Override
	public void shouldCollapseInputPanel() {
	}

	@Override
	public boolean isLongClickEnabled() {
		return false;
	}

	@Override
	public void showAnimation(Gift gift) {
		
	}

    /***********************************************************注册相关**********************************************************************/
    public void registerObservers(boolean register) {
    	if(! register){
    		logoutChatRoom();
    	}
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, register);
    }
    
	private void logoutChatRoom() {
		if (container.chatRoom != null )
		{
			//主播离开需要更新直播间状态
			updateVideoStatus(true);
		}
		NIMClient.getService(ChatRoomService.class).exitChatRoom(container.chatRoom.getChatroomid());
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

 	@Override
 	public void RequestSuccessful(String jsonString, int taskType) {
 		switch(taskType)
 		{
 		case Config.update_status:
 			XLog.i("taskType: " + taskType + " json string: " + jsonString);
 			break;
 		case Config.update_room:
 			XLog.i("taskType: " + taskType + " json string: " + jsonString);
 			break;
 		case Config.query_piao:
 			
 			try {
 				JSONObject jsonObj = new JSONObject(jsonString);
 				String data =  jsonObj.getString("data");
 				int status =   jsonObj.getInt("status");
 				if (status == 0 && data != null)
 				{
 					totalPiao.setText(data);
 				}
 			} catch (JSONException e) {
 				e.printStackTrace();
 			}
 			break;
 		case Config.give_applause_booed:
 			switch (applauseGiveConcern.getType()) {
 			case 1:
 				applauseGiveConcern.showAnimationDialog(R.drawable.circle4,
 						R.raw.applaud);
 				break;
 			case 2:
 				applauseGiveConcern.showAnimationDialog(R.drawable.circle5,
 						R.raw.give_back);
 				break;
 			default:
 				applauseGiveConcern.showAnimationDialog(R.drawable.circle4,
 						R.raw.applaud);
 				break;
 			}

 			break;
 		case Config.and_attention:
 			ToastUtil.show(container.activity.getApplicationContext(), "提交成功");
 			applauseGiveConcern.showAnimationDialog(R.drawable.circle6,
 					R.raw.concern);
 			break;
 		case Config.member_in:
 			try {
 				Entity<Member> memberEntity = gson.fromJson(jsonString,
 						new TypeToken<Entity<Member>>() {
 						}.getType());
 				
 				if (memberEntity != null && memberEntity.getData() != null)
 				{
 					Member member = memberEntity.getData();
 					//进入聊天室
 					ChatCache.getInstance().AddMember(member);
 			        recycleAdapter.UpdateData();
 			        XLog.i("have notifiy data set changed");
 			        XLog.i("amount people: " + ChatCache.getInstance().getOnlinePeopleitems().size());
 			        if (onlinePeople != null)
 			        {
 			        	onlinePeople.setText(String.valueOf(ChatCache.getInstance().getOnlinePeopleitems() == null ? 0 : ChatCache.getInstance().getOnlinePeopleitems().size()));
 			        }
 					//更新聊天室人数到后台
 					updateRoomMember();
 					
 					XLog.i("some one in chat room, username : " + member.getName());
 					XLog.i("some one in chat room, portrait: " + member.getPortrait());
 				}
 			} catch (JsonSyntaxException e) {
 				e.printStackTrace();
 				XLog.e("exception: " + e.getMessage());
 			}
 			break;
 		}
 	}

	@Override
	public void sendMessage(IMMessage msg, boolean isFirst) {
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
							Toast.makeText(container.activity.getBaseContext(), "用户被禁言",Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(container.activity.getBaseContext(),"消息发送失败：code:" + code, Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onException(Throwable exception) {
						Toast.makeText(container.activity.getBaseContext(), "消息发送失败！",
								Toast.LENGTH_SHORT).show();
					}
				});
//		if (isFirst)
//		{
//			onMsgSend(msg);
//		}
		
	}

}
