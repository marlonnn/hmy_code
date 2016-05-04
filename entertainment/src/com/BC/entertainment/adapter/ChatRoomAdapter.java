package com.BC.entertainment.adapter;

import java.util.List;

import com.BC.entertainment.cache.ChatRoomCache;
import com.BC.entertainment.chatroom.extension.CustomAttachment;
import com.BC.entertainment.chatroom.extension.CustomAttachmentType;
import com.BC.entertainment.chatroom.extension.EmotionAttachment;
import com.BC.entertainment.chatroom.extension.FontAttachment;
import com.BC.entertainment.chatroom.module.ChatRoomPanel;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Member;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.config.Config;
import com.summer.logger.XLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatRoomAdapter extends BaseAdapter {
	
	private Context mContext;
	
	private List<IMMessage> mData;
	
	private LayoutInflater mInflater;

	private ViewHolder viewHolder;
	
	private ChatRoomPanel chatRoomPanel;
	
	public ChatRoomAdapter(Context context, ChatRoomPanel chatRoomPanel, List<IMMessage> mData)
	{
		this.mContext = context;
		this.chatRoomPanel = chatRoomPanel;
		this.mData = mData;
		this.mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		int count = 0;
		
		if(mData != null)
		{
			count = mData.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null)
		{
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.fragment_message_item, null);

            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            viewHolder.txtContent = (TextView) convertView.findViewById(R.id.txtContent);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
		IMMessage item = mData.get(position);
		if (item != null)
		{
			viewHolder.txtName.setTextColor(Color.parseColor("#EEB422"));
			if (item.getMsgType() == MsgTypeEnum.notification)
			{
				handleNotificationMessage(item);
			}
			else if (item.getMsgType() == MsgTypeEnum.custom)
			{
				handleCustomMessage(item);
			}
			else if (item.getMsgType() == MsgTypeEnum.text)
			{
				handleTextMessage(item);
			}
		}
		return convertView;
	}
	
	private void handleNotificationMessage(IMMessage item)
	{
        if (item != null && item.getAttachment() == null) {
    		ChatRoomNotificationAttachment attachment = (ChatRoomNotificationAttachment) item
    				.getAttachment();
    		viewHolder.txtName.setText("系统消息：");
    		if (attachment.getType() == NotificationType.ChatRoomMemberIn)
    		{
    			viewHolder.txtContent.setText("欢迎"+ attachment.getOperatorNick() + "进入直播间");
    			XLog.i("incoming notification message in");
    		}
    		else if (attachment.getType() == NotificationType.ChatRoomMemberExit)
    		{
    			viewHolder.txtContent.setText((attachment.getOperatorNick() == null ? "" : attachment.getOperatorNick()) + "离开了直播间");
    			XLog.i("incoming notification message exit");
    		}
    		viewHolder.txtContent.setTextColor(Color.parseColor("#8B658B"));
        }
	}
	
	private void handleTextMessage(IMMessage item)
	{
        if (item != null) {
    		try {
    			ChatRoomMessage message  = (ChatRoomMessage)item;
    			if (message.getDirect() == MsgDirectionEnum.Out)
    			{
    				//发出去的消息
    				viewHolder.txtName.setText(Config.User.getNickName() + ":");
    				viewHolder.txtContent.setText( message.getContent());
    				XLog.i("incoming text message out: " + message.getContent());
    			}
    			else if (message.getDirect() == MsgDirectionEnum.In)
    			{
    				//接受到的消息
    				viewHolder.txtName.setText(message.getChatRoomMessageExtension().getSenderNick() + ":");
    				viewHolder.txtContent.setText(message.getContent());
    				XLog.i("incoming text message in: " + message.getContent());
    			}

    			viewHolder.txtContent.setTextColor(Color.parseColor("#FFFFFF"));
    		} catch (Exception e) {
    			e.printStackTrace();
    			XLog.e("may null point exception ");
    		}
        }

	}
	
	private void handleCustomMessage(IMMessage item)
	{
    	if (item != null)
    	{
    		viewHolder.txtName.setTextColor(Color.parseColor("#EEB422"));
    		viewHolder.txtName.setText("系统消息：");
        	CustomAttachment customAttachment = (CustomAttachment)item.getAttachment();
        	Member member = ChatRoomCache.getInstance().getMember(item.getFromAccount());
        	switch(customAttachment.getType())
        	{
        	case CustomAttachmentType.emotion:
        		EmotionAttachment emotionAttachment = (EmotionAttachment)customAttachment;
        		if (emotionAttachment != null)
        		{
        			String emotionName = emotionAttachment.getEmotion().getName();
        			viewHolder.txtContent.setText((member == null ? "" : member.getNick()) + " 送来了 " + emotionAttachment.getEmotion().getName());
        			XLog.i("font gift name: " + emotionName);
        			viewHolder.txtContent.setTextColor(Color.parseColor("#8B658B"));
        		}
        		
        		if (emotionAttachment != null)
        		{
        			chatRoomPanel.showAnimate(emotionAttachment.getEmotion());
        		}
        		break;
        	case CustomAttachmentType.font:
        		FontAttachment fontAttachment = (FontAttachment)customAttachment;
        		if (fontAttachment != null)
        		{
        			String fontName = fontAttachment.getEmotion().getName();
        			viewHolder.txtContent.setText((member == null ? "" : member.getNick()) + " 送来了 " + fontAttachment.getEmotion().getName());
        			XLog.i("font gift name: " + fontName);
        			viewHolder.txtContent.setTextColor(Color.parseColor("#8B658B"));
        		}
        		
        		if (fontAttachment != null)
        		{
        			chatRoomPanel.showAnimate(fontAttachment.getEmotion());
        		}
        		break;
        	}
    	}
	}

	private static class ViewHolder
	{
	    TextView txtName;
	    TextView txtContent;
	}
	
}
