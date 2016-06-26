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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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

import com.BC.entertainment.adapter.PushAdapter;
import com.BC.entertainment.adapter.RecyclerViewAdapter;
import com.BC.entertainment.adapter.RecyclerViewAdapter.OnItemClickListener;
import com.BC.entertainment.cache.ChatCache;
import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.chatroom.extension.BaseEmotion;
import com.BC.entertainment.chatroom.gift.Gift;
import com.BC.entertainment.chatroom.gift.GiftHelper;
import com.BC.entertainment.chatroom.module.Container;
import com.BC.entertainment.chatroom.module.ModuleProxy;
import com.BC.entertainment.chatroom.module.PushModulePanel;
import com.BC.entertainment.inter.MediaCallback;
import com.BC.entertainment.task.ThreadUtil;
import com.BC.entertainmentgravitation.ContributionActivity;
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
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.config.Config;
import com.summer.fragment.BaseFragment;
import com.summer.handler.InfoHandler;
import com.summer.handler.InfoHandler.InfoReceiver;
import com.summer.json.Entity;
import com.summer.logger.XLog;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.view.CircularImage;
import com.summer.view.Pandamate;

public class PushFragment extends BaseFragment implements OnClickListener, ModuleProxy {

	private static final int MESSAGE_CAPACITY = 500;
	private Context context;
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
    public ImageView imageViewAnimation;
	private ImageView btnYuPaoDetail;
    // container
    private Container container;
    
    // message list view
    private ListView messageListView;
    
    private LinkedList<IMMessage> items;//聊天室消息列表
    private PushAdapter adapter;
	private CircularImage headPortrait;
	private RecyclerView recycleView;
	private RecyclerViewAdapter recycleAdapter;
	private TextView onlinePeople;//总的在线人数
	private Gson gson;
	
    //module
	private PushModulePanel modulePanel;
    
	//投资或者撤资
    private ApplauseGiveConcern applauseGiveConcern;
    
    //基本信息弹出窗
	private InfoDialog dialog;
	
	private MediaCallback mediaCallback;
	
	private boolean initApplause = false;
    
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
	                        RequestSuccessful(code, jsonString, taskType);
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

	public PushFragment(Activity activity, ChatRoom chatRoom)
	{
		container = new Container(activity, chatRoom, SessionTypeEnum.ChatRoom, this);
		context = activity;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mediaCallback = (MediaCallback)activity;
//			modulePanel.SetMediaCallback(mediaCallback);
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e("get switch camera exception");
		}
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		gson = new Gson();
		sendMemberRequest();
		super.onCreate(savedInstanceState);
	}
	
    /**
     * 游客进入聊天室，发送获取头像信息请求
     * @param username
     */
    private void sendMemberRequest()
    {
    	initApplause = true;
    	HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("username", Config.User.getUserName());
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	ThreadUtil.AddToThreadPool(Config.member_in, "get start info", params, handler);
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
	
	@Override
	public void onDestroy() {
		modulePanel.RegisterObservers(false);
		if (modulePanel.danmakuPanel != null)
		{
			modulePanel.danmakuPanel.onDestroy();
		}
		if (modulePanel != null)
		{
			modulePanel.stopUpdateYuPiao();	
		}
    	ChatCache.getInstance().ClearMember();
    	super.onDestroy();
	}
	
	public void Destroy()
	{
		modulePanel.RegisterObservers(false);
		if (modulePanel != null && modulePanel.danmakuPanel != null)
		{
			modulePanel.danmakuPanel.onDestroy();
		}
		if (modulePanel != null)
		{
			modulePanel.stopUpdateYuPiao();	
		}
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
        
        /**
         * 初始化聊天室和输入框控件
         */
        initializeView();
        modulePanel = new PushModulePanel(this, container, rootView, handler);
		modulePanel.RegisterObservers(true);
        modulePanel.updateVideoStatus(false);//更新聊天室状态
		modulePanel.startGetYuPiao();//开始更新娱票
	}
	
	private void initChatView()
	{
		initListView();
		initPortrait();
		initOnlinePortrait();
	}
	
    /**
     * 初始化和处理聊天室消息列表
     */
    private void initListView(){
    	items = new LinkedList<>();
    	
    	messageListView = (ListView)rootView.findViewById(R.id.messageListView);
    	
    	imageViewAnimation = (ImageView)rootView.findViewById(R.id.imageViewAnimation);
    	adapter = new PushAdapter(container, context, items);
		messageListView.setAdapter(adapter);
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
		btnInvest.setVisibility(View.GONE);
		btnDivest.setVisibility(View.GONE);
		btnInvest.setOnClickListener(this);
		btnDivest.setOnClickListener(this);
		
//		/**
//		 * 初始化投资和撤资弹出对话框
//		 */
//		applauseGiveConcern = new ApplauseGiveConcern( container.activity,
//				InfoCache.getInstance().getStartInfo().getStar_ID(), this,
//				InfoCache.getInstance().getStartInfo()
//						.getThe_current_hooted_thumb_up_prices(),
//				InfoCache.getInstance().getStartInfo().getStage_name());
		
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
	
	private void initApplauseConcern(Member member)
	{
		/**
		 * 初始化投资和撤资弹出对话框
		 */
		applauseGiveConcern = new ApplauseGiveConcern( container.activity,
				member.getId(), this, Integer.parseInt(member.getBid()), member.getNick());
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
					if (applauseGiveConcern != null)
					{
						applauseGiveConcern.sendFocusRequest(member.getId());
					}
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
			if (modulePanel != null)
			{
				modulePanel.ShowShare();
			}
			break;
		/**
		 * 切换摄像头
		 */
		case R.id.imageView_camera:
			if(mediaCallback!= null)
			{
				mediaCallback.onSwitchCamera();
			}
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
			modulePanel.RegisterObservers(false);
			if(mediaCallback != null)
			{
				mediaCallback.finishPushMedia();
			}

			if (modulePanel.danmakuPanel != null)
			{
				modulePanel.danmakuPanel.onDestroy();
			}
			modulePanel.stopUpdateYuPiao();
			ChatCache.getInstance().ClearMember();
			container.activity.finish();
			break;
		/**
		 * 查看直播间娱票详情	
		 */
		case R.id.imageView3:
//			ToastUtil.show(container.activity, "此功能正在努力开发中，敬请期待...");
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
		m.setId(Config.User.getClientID());
		m.setNick(Config.User.getNickName());
		m.setName(Config.User.getUserName());
		m.setPortrait(InfoCache.getInstance().getPersonalInfo().getHead_portrait());
		m.setGender(InfoCache.getInstance().getPersonalInfo().getGender());
		m.setRegion(InfoCache.getInstance().getPersonalInfo().getRegion());
		m.setConstellation(InfoCache.getInstance().getPersonalInfo().getThe_constellation());
		m.setNationality(InfoCache.getInstance().getPersonalInfo().getNationality());
		m.setMood(InfoCache.getInstance().getPersonalInfo().getIn_the_mood());
		m.setPiao(InfoCache.getInstance().getPersonalInfo().getPiao());
		return m;
	}
	
	@Override
	public boolean sendMessage(IMMessage msg) {
		modulePanel.SendMessage(msg);
		return true;
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

 	@Override
 	public void RequestSuccessful(int statu, String jsonString, int taskType) {
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
 					if (initApplause)
 					{
 						initApplauseConcern(member);
 						initApplause = false;
 					}
 					else
 					{
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
 	 					modulePanel.updateRoomMember();
 	 					
 	 					XLog.i("some one in chat room, username : " + member.getName());
 	 					XLog.i("some one in chat room, portrait: " + member.getPortrait());
 					}

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
		
	}

	public PushAdapter GetAdapter()
	{
		return this.adapter;
	}
	
	public ListView GetMessageListView()
	{
		return this.messageListView;
	}
}
