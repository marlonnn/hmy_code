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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
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
import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.chatroom.gift.Gift;
import com.BC.entertainment.chatroom.module.Container;
import com.BC.entertainment.chatroom.module.PullModulePanel;
import com.BC.entertainment.chatroom.module.ModuleProxy;
import com.BC.entertainment.inter.IMedia;
import com.BC.entertainment.inter.SimpleCallback;
import com.BC.entertainmentgravitation.ContributionActivity;
import com.BC.entertainmentgravitation.PersonalHomeActivity;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.dialog.ApplauseGiveConcern;
import com.BC.entertainmentgravitation.dialog.InfoDialog;
import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.BC.entertainmentgravitation.entity.Member;
import com.BC.entertainmentgravitation.entity.YubiAssets;
import com.BC.entertainmentgravitation.util.ListViewUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
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
import com.summer.adapter.CommonAdapter;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.fragment.BaseFragment;
import com.summer.handler.InfoHandler;
import com.summer.handler.InfoHandler.InfoReceiver;
import com.summer.json.Entity;
import com.summer.logger.XLog;
import com.summer.task.HttpTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.summer.view.CircularImage;

public class PullFragment extends BaseFragment implements OnClickListener, ModuleProxy{

	private View rootView;
	private Context context;
	
	private ImageView btnChat;//聊天
	private ImageView btnShare;//分享
	private ImageView btnFocus;//关注
	private ImageView btnClose;//关闭
	private ImageView btnGift;//送礼物
	private ImageView btnInvest;//投资
	private ImageView btnDivest;//撤资
	private LinearLayout layoutInput;
	private TextView totalPiao;//yupiao
	private RelativeLayout functionView;//底部功能键根布局
    private static final int MESSAGE_CAPACITY = 500;
    // container
    private Container container;
    public ImageView imageViewAnimation;
    // message list view
    private ListView messageListView;
    private LinkedList<IMMessage> items;//聊天室消息列表
    private CommonAdapter<IMMessage> adapter;
	private CircularImage headPortrait;
	private RecyclerView recycleView;
	public RecyclerViewAdapter recycleAdapter;
	public TextView onlinePeople;//总的在线人数
	private Gson gson;
	
    //module
    private PullModulePanel modulePanel;
    
    private HttpTask httpTask;//更新娱票线程
    private ApplauseGiveConcern applauseGiveConcern;//投资或者撤资
    
    
    public IMedia iMedia;
    
    private static final int LIMIT = 100;
    private long updateTime = 0; // 非游客的updateTime
    private long enterTime = 0; // 游客的enterTime

    private boolean isNormalEmpty = false; // 固定成员是否拉取完
    
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
	
	public PullFragment(Activity activity, ChatRoom chatRoom)
	{
		container = new Container(activity, chatRoom, SessionTypeEnum.ChatRoom, this);
		context = activity;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		registerObservers(true);
		gson = new Gson();
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			iMedia = (IMedia)activity;
			
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e("get switch camera exception");
		}
	}

	@Override
	public void onDestroy() {
		registerObservers(false);
		if (modulePanel.danmakuPanel != null)
		{
			modulePanel.danmakuPanel.onDestroy();
		}
		stopUpdateYuPiao();
		ChatCache.getInstance().ClearMember();
		super.onDestroy();
	}
	
	public void Destroy()
	{
		registerObservers(false);
		if (modulePanel != null && modulePanel.danmakuPanel != null)
		{
			modulePanel.danmakuPanel.onDestroy();
		}
		stopUpdateYuPiao();
		ChatCache.getInstance().ClearMember();
	}
	
	@Override
	public void onPause() {
		if (modulePanel != null &&  modulePanel.danmakuPanel != null)
		{
			modulePanel.danmakuPanel.onPause();
		}
		super.onPause();
	}
	
	public void onResume() {
		if (modulePanel != null &&  modulePanel.danmakuPanel != null)
		{
			modulePanel.danmakuPanel.onResume();
		}
		super.onResume();
	}

	@SuppressLint("InflateParams") @Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_pull, null);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
        initChatView();
        
        /**
         * 初始化聊天室和输入框控件
         */
        initializeView();
        
        modulePanel = new PullModulePanel(this, container, rootView, handler);
	}
	
	private void initChatView()
	{
		initListView();
		initPortrait();
		fetchData();
		initOnlinePortrait();
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

                    ChatCache.getInstance().AddMember(result);
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
							IMMessage item, int position) {
//						holder.setTextColor(R.id.txtName, Color.parseColor("#EEB422"));
						if (item.getMsgType() == MsgTypeEnum.notification)
						{
					 		try {
								ChatRoomNotificationAttachment attachment = (ChatRoomNotificationAttachment) item
										.getAttachment();
//								holder.setText(R.id.txtName, "系统消息：");
			        			holder.setImageResource(R.id.imageViewMessage, R.drawable.fragment_message_icon);
			                	holder.setTextColor(R.id.txtName, Color.parseColor("#EEB422"));
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
		                	modulePanel.HandlerCustomMessage(holder, item);
		                }
						else if (item.getMsgType() == MsgTypeEnum.text)
						{
							try {
								ChatRoomMessage message  = (ChatRoomMessage)item;
								if (message.getDirect() == MsgDirectionEnum.Out)
								{
									//发出去的消息
				        			holder.setImageResource(R.id.imageViewMessage, R.drawable.fragment_message_icon);
				                	holder.setTextColor(R.id.txtName, Color.parseColor("#EEB422"));
				                	
									holder.setText(R.id.txtName, Config.User.getNickName() + ":");
									holder.setText(R.id.txtContent, message.getContent());
									XLog.i("incoming text message out: " + message.getContent());
								}
								else if (message.getDirect() == MsgDirectionEnum.In)
								{
									//接受到的消息
				        			holder.setImageResource(R.id.imageViewMessage, R.drawable.fragment_message_icon);
				                	holder.setTextColor(R.id.txtName, Color.parseColor("#EEB422"));
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
    	headPortrait.setOnClickListener(this);
		Glide.with(this)
		.load(InfoCache.getInstance().getLiveStar().getHead_portrait())
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
            	showInfoDialog(ChatCache.getInstance().getOnlinePeopleitems().get(position));
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
    
	private void initializeView()
	{
		showMessageListView(true);
		layoutInput = (LinearLayout) rootView.findViewById(R.id.layout_input);
		functionView = (RelativeLayout) rootView.findViewById(R.id.layout_bottom);
		btnChat = (ImageView) rootView.findViewById(R.id.imageView_chart);
		totalPiao = (TextView) rootView.findViewById(R.id.textView_total_value);
		btnShare = (ImageView) rootView.findViewById(R.id.imageView_share);
		btnClose = (ImageView) rootView.findViewById(R.id.imageView_close);
		btnGift = (ImageView) rootView.findViewById(R.id.imageView_gift);
		btnFocus = (ImageView) rootView.findViewById(R.id.imageView_focus);
		btnInvest = (ImageView) rootView.findViewById(R.id.imageView_invest);
		btnDivest = (ImageView) rootView.findViewById(R.id.imageView_divest);
		btnYuPaoDetail = (ImageView) rootView.findViewById(R.id.imgViewMoneyDetail);
		btnYuPaoDetail.setOnClickListener(this);
		btnChat.setOnClickListener(this);
		btnShare.setOnClickListener(this);
		btnFocus.setOnClickListener(this);
		btnClose.setOnClickListener(this);
		btnGift.setOnClickListener(this);
		btnInvest.setOnClickListener(this);
		btnDivest.setOnClickListener(this);
		
		startGetYuPiao();
		
		/**
		 * 初始化投资和撤资弹出对话框
		 */
		applauseGiveConcern = new ApplauseGiveConcern( container.activity,
				InfoCache.getInstance().getLiveStar().getStar_ID(), this,
				InfoCache.getInstance().getLiveStar()
						.getThe_current_hooted_thumb_up_prices(),
				InfoCache.getInstance().getLiveStar().getStage_name());
		
		rootView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showMessageListView(true);
				showFunctionView(true);
				modulePanel.inputPanel.hideInputMethod();
				modulePanel.inputPanel.hideInputBar();
				modulePanel.inputPanel.hideGiftLayout();
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
         for (ChatRoomMessage message : messages) {
             if (isMyMessage(message)) {

            	 modulePanel.danmakuPanel.showDanmaku(message);
                 if (message.getMsgType() == MsgTypeEnum.notification)
                 {
                	 modulePanel.HandleNotification(message);
                 	//保存消息到聊天室消息列表中
                    saveMessage(message, false);
                 }
                 else if(message.getMsgType() == MsgTypeEnum.custom)
                 {
 					modulePanel.HandlerCustomMessage(message);
 	             	//保存消息到聊天室消息列表中
                 }
                 else if(message.getMsgType() == MsgTypeEnum.text)
                 {
                  	//保存消息到聊天室消息列表中
                     saveMessage(message, false);
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
     
     
     /**
      *  刷新消息列表
      */
     public void refreshMessageList() {
    	 container.activity.runOnUiThread(new Runnable() {

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
     
 	/**
 	 * 开始获取娱票线程
 	 */
 	private void startGetYuPiao()
 	{
     	try {
 			HashMap<String, String> entity = new HashMap<String, String>();
 			entity.put("username", InfoCache.getInstance().getLiveStar().getUser_name());
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
			modulePanel.inputPanel.showInputBar();
			modulePanel.inputPanel.hideGiftLayout();
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

			showShare();
			break;
		/**
		 * 送礼物
		 */
		case R.id.imageView_gift:
			showFunctionView(false);
			showMessageListView(false);
			layoutInput.setVisibility(View.VISIBLE);
			modulePanel.inputPanel.hideInputBar();
			modulePanel.inputPanel.showGiftLayout();
			break;
		/**
		 * 关闭
		 */
		case R.id.imageView_close:
			if (iMedia != null)
			{
				Destroy();
				iMedia.finishPullMedia();
			}
			break;
		/**
		 * 查看直播间娱票详情	
		 */
		case R.id.imgViewMoneyDetail:
			Intent intent = new Intent(getActivity(), ContributionActivity.class);
			Bundle b = new Bundle();
			b.putSerializable("member", CreateMember());
			intent.putExtras(b);
			startActivity(intent);
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
		m.setId(InfoCache.getInstance().getPersonalInfo().getClientID());
		m.setNick(InfoCache.getInstance().getPersonalInfo().getNickname());
		m.setPortrait(InfoCache.getInstance().getPersonalInfo().getHead_portrait());
		m.setGender(InfoCache.getInstance().getPersonalInfo().getGender());
		m.setRegion(InfoCache.getInstance().getPersonalInfo().getRegion());
		m.setConstellation(InfoCache.getInstance().getPersonalInfo().getThe_constellation());
		m.setNationality(InfoCache.getInstance().getPersonalInfo().getNationality());
		m.setId(InfoCache.getInstance().getPersonalInfo().getClientID());
		m.setPiao(InfoCache.getInstance().getPersonalInfo().getPiao());
//		m.setMood(InfoCache.getInstance().getPersonalInfo().get)
		return m;
	}
	
	private void showShare() {
		String name = InfoCache.getInstance().getLiveStar().getStage_name();

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
		if (modulePanel.CanSendCustomMessage(msg))
		{
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
		else
		{
			ToastUtil.show(getActivity(), "娛币不足，请赶紧充值吧。");
			return false;
		}

	}
	
	 /**
     *  发送消息后，更新本地消息列表
     * @param message
     */
    public void onMsgSend(IMMessage message) {
        // add to listView and refresh
        saveMessage(message, false);
        refreshMessageList();
        modulePanel.danmakuPanel.showDanmaku(message);
        if (message.getMsgType() == MsgTypeEnum.custom)
        {
        	modulePanel.SendCustomMessageRequest(message);
        }
        else if (message.getMsgType() == MsgTypeEnum.text)
        {
        	modulePanel.SendMessageRequest(null, -1, handler);
        }
        
        ListViewUtil.scrollToBottom(messageListView);
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
	}

}
