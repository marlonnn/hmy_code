package com.BC.entertainmentgravitation.fragment;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.BC.entertainment.cache.GiftCache;
import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.chatroom.gift.Gift;
import com.BC.entertainment.chatroom.module.ChatRoomPanel;
import com.BC.entertainment.chatroom.module.Container;
import com.BC.entertainment.chatroom.module.DanmakuPanel;
import com.BC.entertainment.chatroom.module.InputPanel;
import com.BC.entertainment.chatroom.module.ModuleProxy;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.dialog.ApplauseGiveConcern;
import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.fragment.BaseFragment;
import com.summer.handler.InfoHandler;
import com.summer.logger.XLog;
import com.summer.task.HttpTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TopSurfaceFragment extends BaseFragment implements OnClickListener, ModuleProxy{
	
	private ChatRoom chatRoom;
	
	private boolean isWatchVideo;//否是观看（拉流模式），拉流自己收不到自己的登陆和登出消息
	
	private View view;
	
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
    
    //module
	private ChatRoomPanel chatRoomPanel;
    
    private InputPanel inputPanel;
    
    private DanmakuPanel danmakuPanel;
    
    private SwitchCamera switchCamera;
    
    private HttpTask httpTask;//更新娱票线程
    
    private ExitFragmentListener exitListener;
    
    private ApplauseGiveConcern applauseGiveConcern;//投资或者撤资
    
	public TopSurfaceFragment(ExitFragmentListener exitListener, ChatRoom chatRoom, boolean isWatchVideo)
	{
		this.exitListener = exitListener;
		this.chatRoom = chatRoom;
		
		this.isWatchVideo = isWatchVideo;
		if (exitListener == null)
		{
			XLog.i("---------------------null");
		}
		else
		{
			XLog.i("---------------------not null");
		}
	}
	
	/**
	 * 直播推流时切换摄像头接口
	 * @author zhongwen
	 *
	 */
	public interface SwitchCamera
	{
		void onSwitchCamera();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			switchCamera = (SwitchCamera)activity;
			
//			if (activity instanceof ExitFragmentListener)
//			{
//				this.exitListener = (ExitFragmentListener) getActivity();
//			}
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e("get switch camera exception");
		}
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
	
	@Override
	public void onPause() {
		super.onPause();
		if (danmakuPanel != null)
		{
			danmakuPanel.onPause();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (danmakuPanel != null)
		{
			danmakuPanel.onResume();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	private void initializeView()
	{
        Container container = new Container(getActivity(), chatRoom, SessionTypeEnum.ChatRoom, this);
        
		if (danmakuPanel == null)
		{
			danmakuPanel = new DanmakuPanel(container, view);
		}
		
        if (chatRoomPanel == null) {
            chatRoomPanel = new ChatRoomPanel(container, view, danmakuPanel, exitListener);
            chatRoomPanel.showMessageListView(true);
        }
		
		if (inputPanel == null)
		{
			
			inputPanel = new InputPanel(container, view, GiftCache.getInstance().getListGifts());
		}
        
		layoutInput = (LinearLayout) view.findViewById(R.id.layout_input);
		
		functionView = (RelativeLayout) view.findViewById(R.id.layout_bottom);
		
		btnChat = (ImageView)view.findViewById(R.id.imageView_chart);
		
		totalPiao = (TextView)view.findViewById(R.id.textView_total_value);
		
		btnShare = (ImageView)view.findViewById(R.id.imageView_share);
		
		btnClose = (ImageView)view.findViewById(R.id.imageView_close);
		
		btnSwitch = (ImageView)view.findViewById(R.id.imageView_camera);
		
		btnGift = (ImageView)view.findViewById(R.id.imageView_gift);
		
		btnFocus = (ImageView)view.findViewById(R.id.imageView_focus);
		
		btnInvest = (ImageView)view.findViewById(R.id.imageView_invest);
		
		btnDivest = (ImageView)view.findViewById(R.id.imageView_divest);
		
		if (chatRoom != null && chatRoom.isMaster())
		{
			//主播不需要送礼
			btnGift.setVisibility(View.GONE);
		}
		else
		{
			//观众无法切换摄像头
			btnSwitch.setVisibility(View.GONE);
		}

		
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
		applauseGiveConcern = new ApplauseGiveConcern(getActivity(),
				InfoCache.getInstance().getStartInfo().getStar_ID(), this,
				InfoCache.getInstance().getStartInfo()
						.getThe_current_hooted_thumb_up_prices(),
				InfoCache.getInstance().getStartInfo().getStage_name());
		
        view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				chatRoomPanel.showMessageListView(true);
				showFunctionView(true);
				inputPanel.hideInputMethod();
				inputPanel.hideInputBar();
				inputPanel.hideGiftLayout();
				return false;
			}
		});
		
	}
	
	private void startGetYuPiao()
	{
    	try {
			HashMap<String, String> entity = new HashMap<String, String>();

			
			if (chatRoom.isMaster())
			{
				entity.put("username", Config.User.getUserName());
			}
			else
			{
			    entity.put("username", InfoCache.getInstance().getStartInfo().getUser_name());
			}
			
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
	
	private void stopUpdateYuPiao()
	{
		httpTask.CancelTask();
	}
	
	/**
	 * 显示功能键
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
	public void onDestroy() {
		super.onDestroy();
		if (chatRoomPanel != null)
		{
			chatRoomPanel.registerObservers(false);
		}
		
		if (danmakuPanel != null)
		{
			danmakuPanel.onDestroy();
		}
		
		stopUpdateYuPiao();

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
			chatRoomPanel.showMessageListView(true);
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

			ToastUtil.show(getActivity(), "此功能正在完善中...");
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
			chatRoomPanel.showMessageListView(false);
			layoutInput.setVisibility(View.VISIBLE);
			inputPanel.hideInputBar();
			inputPanel.showGiftLayout();
			break;
		/**
		 * 关闭
		 */
		case R.id.imageView_close:
			if (chatRoomPanel != null)
			{
				chatRoomPanel.registerObservers(false);
			}
			
			if (danmakuPanel != null)
			{
				danmakuPanel.onDestroy();
			}
			
	    	if(exitListener != null)
	    	{
	    		exitListener.isExit(true);
	    	}
	    	
			stopUpdateYuPiao();
			break;
		}
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
							Toast.makeText(getActivity(), "用户被禁言",Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getActivity(),"消息发送失败：code:" + code, Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onException(Throwable exception) {
						Toast.makeText(getActivity(), "消息发送失败！",
								Toast.LENGTH_SHORT).show();
					}
				});
		chatRoomPanel.onMsgSend(msg);
		return true;
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
		//show local animation
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch(taskType)
		{
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
			ToastUtil.show(getActivity(), "提交成功");
			applauseGiveConcern.showAnimationDialog(R.drawable.circle6,
					R.raw.concern);
			break;
		}
	}
}
