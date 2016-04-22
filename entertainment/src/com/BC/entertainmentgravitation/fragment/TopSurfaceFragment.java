package com.BC.entertainmentgravitation.fragment;

import com.BC.entertainment.chatroom.module.ChatRoomPanel;
import com.BC.entertainment.chatroom.module.GiftCache;
import com.BC.entertainment.chatroom.module.InputPanel;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.netease.nim.uikit.session.module.Container;
import com.netease.nim.uikit.session.module.ModuleProxy;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.logger.XLog;

import android.annotation.SuppressLint;
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
	
	private View view;
	
	private ImageView btnChat;//����
	
	private ImageView btnShare;//����
	
	private ImageView btnFocus;//��ע
	
	private ImageView btnBoos;//�ȵ���
	
	private ImageView btnApplaud;//����
	
	private LinearLayout layoutInput;
	
	private RelativeLayout rootView;
	
	private RelativeLayout functionView;//�ײ����ܼ�������
    
    //module
	private ChatRoomPanel messageListPanel;
    
    private InputPanel inputPanel;
    
	public TopSurfaceFragment(ChatRoom chatRoom)
	{
		this.chatRoom = chatRoom;
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
	
	private void initializeView()
	{
        Container container = new Container(getActivity(), chatRoom.getChatroomid(), SessionTypeEnum.ChatRoom, this);
        if (messageListPanel == null) {
            messageListPanel = new ChatRoomPanel(container, view);
        }
		messageListPanel.registerObservers(true);
		
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
		
		layoutInput.setVisibility(View.GONE);
		
        view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				layoutInput.setVisibility(View.GONE);
				showFunctionView(true);
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
		if (messageListPanel != null)
		{
			messageListPanel.registerObservers(false);
		}

	}

	@Override
	public void onClick(View v) {
		showFunctionView(false);
		switch(v.getId())
		{
		case R.id.imageView_chart:
			layoutInput.setVisibility(View.VISIBLE);
			inputPanel.init(false);
			break;
		case R.id.imageView:
			layoutInput.setVisibility(View.VISIBLE);
			inputPanel.init(true);
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
							Toast.makeText(getActivity(), "�û�������",Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getActivity(),"��Ϣ����ʧ�ܣ�code:" + code, Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onException(Throwable exception) {
						Toast.makeText(getActivity(), "��Ϣ����ʧ�ܣ�",
								Toast.LENGTH_SHORT).show();
					}
				});
		messageListPanel.onMsgSend(msg);
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
}
