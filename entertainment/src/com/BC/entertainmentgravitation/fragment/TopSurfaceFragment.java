package com.BC.entertainmentgravitation.fragment;

import java.util.HashMap;
import java.util.Map;

import com.BC.entertainment.chatroom.helper.ChatRoomMemberCache;
import com.BC.entertainment.chatroom.module.ChatRoomMsgListPanel;
import com.BC.entertainment.config.Cache;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.netease.nim.uikit.session.module.Container;
import com.netease.nim.uikit.session.module.ModuleProxy;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.config.Config;
import com.summer.logger.XLog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TopSurfaceFragment extends Fragment implements OnClickListener, ModuleProxy{
	
	private ChatRoom chatRoom;
	
	private View view;
	
	private Button btnSend;
	
	private EditText edtInput;
	
	private ImageView imageViewChart;
	
	private LinearLayout layoutInput;
    
    //module
    protected ChatRoomMsgListPanel messageListPanel;
	
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
            messageListPanel = new ChatRoomMsgListPanel(container, view);
        }
        
		messageListPanel.registerObservers(true);
        
		layoutInput = (LinearLayout) view.findViewById(R.id.layout_input);
		
		edtInput = (EditText)view.findViewById(R.id.edtInput);
		
		btnSend = (Button) view.findViewById(R.id.btnSend);
		
		imageViewChart = (ImageView)view.findViewById(R.id.imageView_chart);
		
		imageViewChart.setOnClickListener(this);
		
		edtInput.setOnClickListener(this);
		
		btnSend.setOnClickListener(this);
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		messageListPanel.registerObservers(false);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.imageView_chart:
			if (layoutInput.isShown())
			{
				layoutInput.setVisibility(View.GONE);
			}
			else
			{
				layoutInput.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.btnSend:
			sendMessage();
			break;
		}
	}
	
	private void sendMessage()
	{
		ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomTextMessage(
				chatRoom.getChatroomid(),
				edtInput.getText().toString());
		sendMessage(message, null);
	}

	public boolean sendMessage(IMMessage msg, String type) {
		
        ChatRoomMessage message = (ChatRoomMessage) msg;
        
        Map<String, Object> ext = new HashMap<>();
        ChatRoomMember chatRoomMember = ChatRoomMemberCache.getInstance().getChatRoomMember(chatRoom.getChatroomid(), Cache.getAccount());
        if (chatRoomMember != null && chatRoomMember.getMemberType() != null) {
            ext.put("type", chatRoomMember.getMemberType().getValue());
            ext.put("nickname", Config.User.getNickName() == null ? "" : Config.User.getNickName());
            message.setRemoteExtension(ext);
        }

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
		messageListPanel.onMsgSend(msg);
		return true;
    }

	@Override
	public boolean sendMessage(IMMessage msg) {
		return false;
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
