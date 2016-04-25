package com.BC.entertainmentgravitation.fragment;

import com.BC.entertainment.chatroom.gift.Gift;
import com.BC.entertainment.chatroom.module.ChatRoomPanel;
import com.BC.entertainment.chatroom.module.Container;
import com.BC.entertainment.chatroom.module.DanmakuPanel;
import com.BC.entertainment.chatroom.module.GiftCache;
import com.BC.entertainment.chatroom.module.InputPanel;
import com.BC.entertainment.chatroom.module.ModuleProxy;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.config.Config;
import com.summer.logger.XLog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class TopSurfaceFragment extends Fragment implements OnClickListener, ModuleProxy{
	
	private ChatRoom chatRoom;
	
	private ChatRoomMember chatRoomMember;
	
	private boolean isWatchVideo;//否是观看（拉流模式），拉流自己收不到自己的登陆和登出消息
	
	private View view;
	
	private ImageView btnChat;//聊天
	
	private ImageView btnShare;//分享
	
	private ImageView btnFocus;//关注
	
	private ImageView btnBoos;//喝倒彩
	
	private ImageView btnApplaud;//鼓掌
	
	private LinearLayout layoutInput;
	
	private RelativeLayout rootView;
	
	private RelativeLayout functionView;//底部功能键根布局
    
    //module
	private ChatRoomPanel chatRoomPanel;
    
    private InputPanel inputPanel;
    
    private DanmakuPanel danmakuPanel;
    
    private SwitchCamera switchCamera;
    
	public TopSurfaceFragment(ChatRoom chatRoom, ChatRoomMember chatRoomMember, boolean isWatchVideo)
	{
		this.chatRoom = chatRoom;
		
		this.chatRoomMember = chatRoomMember;
		
		this.isWatchVideo = isWatchVideo;
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
        Container container = new Container(getActivity(), chatRoom.getChatroomid(), SessionTypeEnum.ChatRoom, this);
        
		if (danmakuPanel == null)
		{
			danmakuPanel = new DanmakuPanel(container, view);
		}
		
        if (chatRoomPanel == null) {
            chatRoomPanel = new ChatRoomPanel(container, view, danmakuPanel);
//            if (isWatchVideo)
//            {
//            	chatRoomPanel.addMembers(chatRoomMember, false);
//            }
            chatRoomPanel.showMessageListView(true);
        }
		chatRoomPanel.registerObservers(true);
//		chatRoomPanel.registerCustomMsgObservers();
		
		if (inputPanel == null)
		{
			
			inputPanel = new InputPanel(container, view, GiftCache.getInstance().getListGifts());
		}
        
		layoutInput = (LinearLayout) view.findViewById(R.id.layout_input);
		
		functionView = (RelativeLayout) view.findViewById(R.id.layout_bottom);
		
		btnChat = (ImageView)view.findViewById(R.id.imageView_chart);
		
		btnShare = (ImageView)view.findViewById(R.id.imageView_share);
		btnFocus = (ImageView)view.findViewById(R.id.imageView_focus);
		btnBoos = (ImageView)view.findViewById(R.id.imageView_boos);
		btnApplaud = (ImageView)view.findViewById(R.id.imageView);
		
		btnChat.setOnClickListener(this);
		btnShare.setOnClickListener(this);
		btnFocus.setOnClickListener(this);
		btnBoos.setOnClickListener(this);
		btnApplaud.setOnClickListener(this);
		
//		layoutInput.setVisibility(View.INVISIBLE);
		
        view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
//				layoutInput.setVisibility(View.INVISIBLE);
				chatRoomPanel.showMessageListView(true);
				showFunctionView(true);
				inputPanel.hideInputMethod();
				inputPanel.hideInputBar();
				inputPanel.hideGiftLayout();
				return false;
			}
		});
		
	}
	
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

	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.imageView_chart:
			showFunctionView(false);
			chatRoomPanel.showMessageListView(true);
			layoutInput.setVisibility(View.VISIBLE);
			inputPanel.showInputBar();
			inputPanel.hideGiftLayout();
			break;
		case R.id.imageView:
			//暂时用鼓掌来测试礼物
			showFunctionView(false);
			chatRoomPanel.showMessageListView(false);
			layoutInput.setVisibility(View.VISIBLE);
			inputPanel.hideInputBar();
			inputPanel.showGiftLayout();
			break;
			
		case R.id.imageView_share:
			//暂时用分享按钮来切换摄像头
			if( switchCamera != null)
			{
				switchCamera.onSwitchCamera();
			}
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
//		danmakuPanel.AddDanmaku(false, (Config.User.getNickName() == null ? "" : Config.User.getNickName()) + ": " + message.getContent());
//		danmakuPanel.AddDanmaKuShowTextAndImage(false);
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
	public boolean sendCustomMessage(final IMMessage msg) {
		
		NIMClient.getService(MsgService.class).sendMessage(msg, false).setCallback(new RequestCallback<Void>() {

			@Override
			public void onException(Throwable exception) {
				Toast.makeText(getActivity(), "自定义消息发送失败！",
						Toast.LENGTH_SHORT).show();
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
			public void onSuccess(Void param) {
				XLog.i("send custom messsage success");
		        chatRoomPanel.onMsgSend(msg);
			}
		});
		
		return true;
	}
}
