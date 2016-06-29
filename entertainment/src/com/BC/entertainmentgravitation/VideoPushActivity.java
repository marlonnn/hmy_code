package com.BC.entertainmentgravitation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.BC.entertainment.adapter.PushAdapter;
import com.BC.entertainment.adapter.RecyclerViewAdapter;
import com.BC.entertainment.cache.ChatCache;
import com.BC.entertainment.chatroom.gift.Gift;
import com.BC.entertainment.chatroom.module.ChatRoom;
import com.BC.entertainment.chatroom.module.InputControl;
import com.BC.entertainment.chatroom.module.LivePlayer;
import com.BC.entertainment.chatroom.module.PushControl;
import com.BC.entertainment.inter.ActivityCallback;
import com.BC.entertainment.task.ThreadUtil;
import com.BC.entertainment.view.LiveSurfaceView;
import com.BC.entertainmentgravitation.entity.StarLiveVideoInfo;
import com.google.gson.Gson;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.summer.handler.InfoHandler;
import com.summer.handler.InfoHandler.InfoReceiver;
import com.summer.logger.XLog;
import com.summer.utils.JsonUtil;
import com.summer.view.CircularImage;
import com.umeng.analytics.MobclickAgent;

public class VideoPushActivity extends BaseActivity implements OnClickListener, ActivityCallback {

	private StarLiveVideoInfo startLiveVideoInfo;
	private LiveSurfaceView mVideoView;
	private RelativeLayout rlayoutLoading;
	private RelativeLayout rootView;
	private LivePlayer livePlayer;
	private ChatRoom chatRoom;
	
	private PushControl pushControl;
	
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
    // message list view
    private ListView messageListView;
    private LinkedList<IMMessage> items;//聊天室消息列表
    private PushAdapter adapter;
	private CircularImage headPortrait;
	private RecyclerView recycleView;
	private RecyclerViewAdapter recycleAdapter;
	private TextView onlinePeople;//总的在线人数
	private Gson gson;
	private boolean initApplause;
	
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
		setContentView(R.layout.activity_push_video);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		registerObservers(true);
		gson = new Gson();
		mVideoView = (LiveSurfaceView) findViewById(R.id.videoview);
		rootView = (RelativeLayout) findViewById(R.id.layout_root);
        initListView();
        initializeView();

		rlayoutLoading = (RelativeLayout) findViewById(R.id.rLayoutPushLoading);
        Intent intent = this.getIntent();
        startLiveVideoInfo = (StarLiveVideoInfo)intent.getSerializableExtra("liveInfo");
        chatRoom = new ChatRoom(this);
        chatRoom.EnterChatRoom(startLiveVideoInfo);


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
				getInputControl().hideInputMethod();
				getInputControl().hideInputBar();
				getInputControl().hideGiftLayout();
				return false;
			}
		});
	}
	
    /**
     * 初始化和处理聊天室消息列表
     */
    private void initListView(){
    	items = new LinkedList<>();
    	
    	messageListView = (ListView)rootView.findViewById(R.id.messageListView);
    	
//    	imageViewAnimation = (ImageView)rootView.findViewById(R.id.imageViewAnimation);
//    	adapter = new PushAdapter(container, context, items);
//		messageListView.setAdapter(adapter);
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
    
    private void initLivePlayer()
    {
    	livePlayer = new LivePlayer(mVideoView, this);
    	livePlayer.startStopLive();
    }
	
	/**
	 * 明星直播
	 * @param startLiveVideoInfo
	 */
	private void startLiveVideo(StarLiveVideoInfo startLiveVideoInfo)
	{
		ChatCache.getInstance().getChatRoom().setChatroomid(startLiveVideoInfo.getChatroomid());
		ChatCache.getInstance().getChatRoom().setCid(startLiveVideoInfo.getCid());
		ChatCache.getInstance().getChatRoom().setPushUrl(startLiveVideoInfo.getPushUrl());
		ChatCache.getInstance().getChatRoom().setMaster(true);
        initLivePlayer();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
        MobclickAgent.onPause(this);
        // 暂停直播
        if (livePlayer != null) {
            livePlayer.onActivityPause();
        }
	}


	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		
        // 恢复直播
        if (livePlayer != null) {
            livePlayer.onActivityResume();
        }
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
        resetLivePlayer();
        super.onDestroy();
        registerObservers(false);
        if (pushControl != null)
        {
        	pushControl.stopUpdateYuPiao();
        }
        ChatCache.getInstance().ClearMember();
    }
    
    private void resetLivePlayer() {
        // 释放资源
        if (livePlayer != null) {
            livePlayer.tryStop();
            livePlayer.resetLive();
        }
    }
    
    private InputControl getInputControl()
    {
    	return pushControl.getInputControl();
    }

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		
	}

	@Override
	public Activity getActivity() {
		return VideoPushActivity.this;
	}

	@Override
	public void onLoginSuccess() {
    	pushControl = new PushControl(this, handler, rootView);
    	pushControl.UpdateVideoStatus(false);//更新聊天室状态
		//登录成功再开始直播
		startLiveVideo(startLiveVideoInfo);	
	}

	@Override
	public void onLoginFailed() {
		finish();
	}
	
	@Override
	public void onLiveStart() {
		rlayoutLoading.setVisibility(View.GONE);
		sendMemberRequest();
	}

	@Override
	public void onInitFailed() {
		finish();
	}

	@Override
	public void onNetWorkBroken() {
        Toast.makeText(VideoPushActivity.this, "网络连接已断开", Toast.LENGTH_SHORT).show();
        resetLivePlayer();
	}

	@Override
	public void onFinished() {
		finish();
	}

	@Override
	public boolean sendMessage(IMMessage msg) {
		return false;
	}

	@Override
	public void showAnimation(Gift gift) {
		
	}
	
    /***********************************************************注册相关**********************************************************************/
    private void registerObservers(boolean register) {
    	if(! register){
    		logoutChatRoom();
    	}
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, register);
    }
    
	private void logoutChatRoom() {
		if (pushControl != null )
		{
			//主播离开需要更新直播间状态
			pushControl.UpdateVideoStatus(true);
		}
		NIMClient.getService(ChatRoomService.class).exitChatRoom(ChatCache.getInstance().getChatRoom().getChatroomid());
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
	
    //接受到云信消息
	public void onIncomingMessage(List<ChatRoomMessage> messages) {
		
	}

	@Override
	public void onClick(View v) {
		
	}
}
