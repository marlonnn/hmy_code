package com.BC.entertainmentgravitation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
import com.BC.entertainment.chatroom.extension.CustomAttachment;
import com.BC.entertainment.chatroom.extension.CustomAttachmentType;
import com.BC.entertainment.chatroom.extension.EmotionAttachment;
import com.BC.entertainment.chatroom.extension.FontAttachment;
import com.BC.entertainment.chatroom.gift.Gift;
import com.BC.entertainment.chatroom.gift.GiftHelper;
import com.BC.entertainment.chatroom.module.Container;
import com.BC.entertainment.chatroom.module.DanmakuPanel;
import com.BC.entertainment.chatroom.module.InputPannel;
import com.BC.entertainment.chatroom.module.ModuleProxy;
import com.BC.entertainmentgravitation.dialog.ApplauseGiveConcern;
import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.BC.entertainmentgravitation.entity.Member;
import com.BC.entertainmentgravitation.entity.YubiAssets;
import com.BC.entertainmentgravitation.fragment.NEVideoView;
import com.BC.entertainmentgravitation.fragment.TopSurfaceFragment.SwitchCamera;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.netease.neliveplayer.NEMediaPlayer;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.common.ui.listview.ListViewUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
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
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.activity.BaseActivity;
import com.summer.adapter.CommonAdapter;
import com.summer.adapter.CommonAdapter.ViewHolder;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
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

public class PullActivity extends BaseActivity implements OnClickListener, ModuleProxy{

	private View view;
	private Context mContext;
	
	private ChatRoom chatRoom;
	private NEVideoView mVideoView;
	NEMediaPlayer mMediaPlayer = new NEMediaPlayer();
	
	private ImageView btnChat;//聊天
	private ImageView btnShare;//分享
	private ImageView btnFocus;//关注
	private ImageView btnClose;//关闭
	private ImageView btnSwitch;//切换摄像头
	private ImageView btnGift;//送礼物
	private ImageView btnInvest;//投资
	private ImageView btnDivest;//撤资
	private LinearLayout layoutInput;
	private RelativeLayout rootView;
	private TextView totalPiao;//yupiao
	private RelativeLayout functionView;//底部功能键根布局
    private static final int MESSAGE_CAPACITY = 500;
	private int tipValue = 10;//一条消息10个娱币
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
//	private ChatRoomPanel chatRoomPanel;
    private InputPannel inputPanel;
    private DanmakuPanel danmakuPanel;
    private SwitchCamera switchCamera;
    private HttpTask httpTask;//更新娱票线程
    private ApplauseGiveConcern applauseGiveConcern;//投资或者撤资
    
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

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pull_video);
		view = findViewById(R.id.layout_root);
		registerObservers(true);
		mContext = this;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
		
        Intent intent = getIntent();
        if (intent != null)
        {
        	chatRoom = new ChatRoom();
        	try {
				chatRoom.setCid(intent.getStringExtra("cid"));
				chatRoom.setChatroomid(intent.getStringExtra("chatroomid"));
				chatRoom.setHttpPullUrl(intent.getStringExtra("httpPullUrl"));
				chatRoom.setMaster(intent.getBooleanExtra("isMaster", false));
			} catch (Exception e) {
				Toast.makeText(this, "直播间错误", Toast.LENGTH_LONG).show();
				e.printStackTrace();
				finish();
			}
        }
        mVideoView = (NEVideoView) findViewById(R.id.video_view);
        mVideoView.setBufferStrategy(0); //直播低延时
		mVideoView.setMediaType("livestream");
		mVideoView.setHardwareDecoder(false);
		mVideoView.setPauseInBackground(true);
		mVideoView.setVideoPath(chatRoom.getHttpPullUrl());
		mMediaPlayer.setLogLevel(8); //设置log级别
		mVideoView.requestFocus();
		mVideoView.start();
		
        initChatView();
        /**
         * 初始化聊天室和输入框控件
         */
        initializeView();
        
	}
	
	private void initChatView()
	{
		initListView();
		initPortrait();
		fetchOnlinePeople();
		initOnlinePortrait();
	}
	
    private void fetchOnlinePeople()
    {
    	fetchRoomMembers(container.chatRoom.getChatroomid(), MemberQueryType.ONLINE_NORMAL, 0, 100, null);
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
									XLog.i("fetch members success : " + result.size());
									ChatCache.getInstance().AddMember(result);
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
     * 主播头像
     */
    private void initPortrait()
    {
    	headPortrait = (CircularImage) rootView.findViewById(R.id.portrait);
		Glide.with(container.activity)
		.load(InfoCache.getInstance().getStartInfo().getHead_portrait())
		.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
		.placeholder(R.drawable.avatar_def).into(headPortrait);
		XLog.i("custom portrait: " + InfoCache.getInstance().getPersonalInfo().getHead_portrait());
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
            }
            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
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
    
	private void initializeView()
	{
		Container container = new Container(this, chatRoom, SessionTypeEnum.ChatRoom, this);
		if (danmakuPanel == null)
		{
			danmakuPanel = new DanmakuPanel(container, view);
		}
		
		showMessageListView(true);
		
		if (inputPanel == null)
		{
			inputPanel = new InputPannel(container, view, GiftCache.getInstance().getListGifts());
		}
		layoutInput = (LinearLayout) findViewById(R.id.layout_input);
		functionView = (RelativeLayout) findViewById(R.id.layout_bottom);
		btnChat = (ImageView) findViewById(R.id.imageView_chart);
		totalPiao = (TextView) findViewById(R.id.textView_total_value);
		btnShare = (ImageView) findViewById(R.id.imageView_share);
		btnClose = (ImageView) findViewById(R.id.imageView_close);
		btnSwitch = (ImageView) findViewById(R.id.imageView_camera);
		btnGift = (ImageView) findViewById(R.id.imageView_gift);
		btnFocus = (ImageView) findViewById(R.id.imageView_focus);
		btnInvest = (ImageView) findViewById(R.id.imageView_invest);
		btnDivest = (ImageView) findViewById(R.id.imageView_divest);
		btnSwitch.setVisibility(View.GONE);
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
		applauseGiveConcern = new ApplauseGiveConcern( this,
				InfoCache.getInstance().getStartInfo().getStar_ID(), this,
				InfoCache.getInstance().getStartInfo()
						.getThe_current_hooted_thumb_up_prices(),
				InfoCache.getInstance().getStartInfo().getStage_name());
		
        view.setOnTouchListener(new OnTouchListener() {
			
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
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (!mVideoView.isPaused()) {
			mVideoView.start(); //锁屏打开后恢复播放
		}
		super.onResume();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		close();
		super.onDestroy();
	}
	@Override
	public void onBackPressed() {
		close();
		super.onBackPressed();
	}
	
	private void close()
	{
		registerObservers(false);
		if (danmakuPanel != null)
		{
			danmakuPanel.onDestroy();
		}
		stopUpdateYuPiao();
		ChatCache.getInstance().ClearMember();
		this.finish();
	}

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

    /***********************************************************注册相关**********************************************************************/
    public void registerObservers(boolean register) {
    	if(! register){
    		logoutChatRoom();
    	}
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, register);
    }
    
	private void logoutChatRoom() {
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
     
     public void onIncomingMessage(List<ChatRoomMessage> messages) {
         boolean needScrollToBottom = ListViewUtil.isLastMessageVisible(messageListView);
         boolean needRefresh = false;
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
     
     public boolean isMyMessage(IMMessage message) {
         return message.getSessionType() == container.sessionType
                 && message.getSessionId() != null
                 && message.getSessionId().equals(container.chatRoom.getChatroomid());
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
     
     /**
      * 显示表情动漫效果
      * @param baseEmotion
      */
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
         	}
     	}

     }
     
     /**
      *  刷新消息列表
      */
     public void refreshMessageList() {
         this.runOnUiThread(new Runnable() {

             @Override
             public void run() {
                 adapter.notifyDataSetChanged();
             }
         });
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
         	if (member != null && member.getName().contains(InfoCache.getInstance().getStartInfo().getUser_name()))
         	{
         		XLog.i("exit chat room");
         		XLog.i("exit chat room, member.getName(): " + member.getName());
         		XLog.i("exit chat room, ---getUser_name(): " + InfoCache.getInstance().getStartInfo().getUser_name());
    			Intent intent = new Intent(this, FinishActivity.class);
    			intent.putExtra("totalPeople", ChatCache.getInstance().getOnlinePeopleitems().size());
    			startActivity(intent);
    			mVideoView.release_resource();
    			this.finish();
         	}
        	removeMembers(member);
         }
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
	 * 停止获取娱票线程
	 */
	private void stopUpdateYuPiao()
	{
		httpTask.CancelTask();
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
			if( switchCamera != null)
			{
				switchCamera.onSwitchCamera();
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
			close();
			break;
		}
	}
	
	private void showShare() {
		String name;
		if (chatRoom.isMaster())
		{
			name = Config.User.getNickName();
		}
		else
		{
			name = InfoCache.getInstance().getStartInfo().getUser_name();
		}
		ShareSDK.initSDK(this, "10ee118b8af16");

		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字
		oks.setTitle("看演员，去海绵娱直播APP!");
		oks.setText("看演员，去海绵娱直播APP!" + "(" + name
				+ "正在直播中)");
		oks.setSite(getString(R.string.app_name));
		// 分享链接地址
		oks.setUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.BC.entertainmentgravitation");
		// logo地址
		oks.setImageUrl("http://app.haimianyu.cn/DOWNLOAD/app_logo.png");
		oks.show(this);
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
							Toast.makeText(mContext, "用户被禁言",Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(mContext,"消息发送失败：code:" + code, Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onException(Throwable exception) {
						Toast.makeText(mContext, "消息发送失败！",
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
     * 处理发送的礼物消息
     * @param message
     */
    private void handlerSendCustomMessage(IMMessage message)
    {
    	if( message != null)
    	{
        	CustomAttachment customAttachment = (CustomAttachment)message.getAttachment();
        	switch(customAttachment.getType())
        	{
        	case CustomAttachmentType.emotion:
        		EmotionAttachment emotionAttachment = (EmotionAttachment)customAttachment;
        		if (emotionAttachment != null && sendChatRoomGift(emotionAttachment.getEmotion(), customAttachment.getType()))
        		{
        			showAnimate(emotionAttachment.getEmotion());
        		}
        		break;
        	case CustomAttachmentType.font:
        		FontAttachment fontAttachment = (FontAttachment)customAttachment;
        		if (fontAttachment != null && sendChatRoomGift(fontAttachment.getEmotion(), customAttachment.getType()))
        		{
        			showAnimate(fontAttachment.getEmotion());
        		}
        		break;
        	}
    	}
    }
    
    private boolean sendChatRoomGift(BaseEmotion baseEmotion, int type)
    {
    	//非主播可以送礼物给主播
    	try {
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
				XLog.i("---type-----" + String.valueOf(type));
				return true;
			}
			else
			{
				//余额不足，需要充值
				ToastUtil.show(container.activity, "余额不足，赶紧充值吧");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e("NumberFormatException");
			return false;
		}
    }
    private void sendChatRoomMessage(IMMessage message)
    {
    	//非主播可以送礼物给主播
    	try {
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
		} catch (Exception e) {
			XLog.e("NumberFormatException");
			e.printStackTrace();
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
}
