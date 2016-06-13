package com.BC.entertainment.adapter;

import java.util.List;

import com.BC.entertainment.cache.ChatCache;
import com.BC.entertainment.chatroom.extension.BubbleAttachment;
import com.BC.entertainment.chatroom.extension.CustomAttachment;
import com.BC.entertainment.chatroom.extension.CustomAttachmentType;
import com.BC.entertainment.chatroom.extension.EmotionAttachment;
import com.BC.entertainment.chatroom.extension.FontAttachment;
import com.BC.entertainment.chatroom.module.Container;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Member;
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

public class PushAdapter extends BaseAdapter{
	
	private Container container;
	private Context mContext;
	private List<IMMessage> mData;
	private LayoutInflater mInflater;
	private PushViewHolder pushViewHolder;

	public PushAdapter(Container container, Context context, List<IMMessage> mData) {
		this.container = container;
		this.mContext = context;
		this.mData = mData;
		this.mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		if (mData != null) {
			return mData.size();
		}
		else
		{
			return 0;
		}
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
			pushViewHolder = new PushViewHolder();
            convertView = mInflater.inflate(R.layout.fragment_message_item, null);
			pushViewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
			pushViewHolder.txtContent = (TextView) convertView.findViewById(R.id.txtContent);
			convertView.setTag(pushViewHolder);
		}
		else
		{
			pushViewHolder = (PushViewHolder)convertView.getTag();
		}
		IMMessage item = (IMMessage) getItem(position);
		pushViewHolder.txtName.setTextColor(Color.parseColor("#EEB422"));
		if (item != null)
		{
			if (item.getMsgType() == MsgTypeEnum.notification)
			{
				handlerNotificationMessage(pushViewHolder, item);
			}
			else if(item.getMsgType() == MsgTypeEnum.custom)
			{
				handlerCustomMessage(pushViewHolder, item);
			}
			else if (item.getMsgType() == MsgTypeEnum.text)
			{
				handlerTextMessage(pushViewHolder, item);
			}
		}
		return convertView;
	}
	
    // 刷新消息列表
    public void RefreshMessageList() {
    	if (container != null)
    	{
            container.activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
    	}
    }
	
	/**
	 * 处理通知消息
	 * @param holder
	 * @param item
	 */
	private void handlerNotificationMessage(PushViewHolder holder, IMMessage item)
	{
		try {
			if (item != null)
			{
				ChatRoomNotificationAttachment attachment = (ChatRoomNotificationAttachment) item
						.getAttachment();
				pushViewHolder.txtName.setTextColor(Color.parseColor("#EEB422"));
				pushViewHolder.txtName.setText("系统消息：");
				if (attachment.getType() == NotificationType.ChatRoomMemberIn)
				{
					pushViewHolder.txtContent.setText("欢迎"+ attachment.getOperatorNick() + "进入直播间");
				}
				else if (attachment.getType() == NotificationType.ChatRoomMemberExit)
				{
					pushViewHolder.txtContent.setText((isNullOrEmpty(attachment.getOperatorNick()) ? "未知用户" : attachment.getOperatorNick()) + "离开了直播间");
				}
				pushViewHolder.txtContent.setTextColor( Color.parseColor("#8B658B"));
			}

		} catch (Exception e) {
			XLog.e(e.getMessage());
			e.printStackTrace();
		}
	}
	
    /**
     * 处理自定义字体或者礼物消息
     * @param holder
     * @param message
     */
    private void handlerCustomMessage(PushViewHolder holder, IMMessage message)
    {
    	try {
			if (message != null)
			{
				CustomAttachment customAttachment = (CustomAttachment)message.getAttachment();
				Member member = ChatCache.getInstance().getMember(message.getFromAccount());
				pushViewHolder.txtContent.setTextColor( Color.parseColor("#8B658B"));
				switch(customAttachment.getType())
				{
				case CustomAttachmentType.emotion:
					EmotionAttachment emotionAttachment = (EmotionAttachment)customAttachment;
					if (emotionAttachment != null)
					{
						pushViewHolder.txtName.setText("系统消息：");
						pushViewHolder.txtContent.setText((isNullOrEmpty(member.getNick()) ? "未知用户" : member.getNick()) + " 送来了 " + emotionAttachment.getEmotion().getName());
					}
					break;
				case CustomAttachmentType.font:
					FontAttachment fontAttachment = (FontAttachment)customAttachment;
					if (fontAttachment != null)
					{
						pushViewHolder.txtName.setText("系统消息：");
						pushViewHolder.txtContent.setText((isNullOrEmpty(member.getNick()) ? "未知用户" : member.getNick()) + " 送来了 " + fontAttachment.getEmotion().getName());
					}
					break;
			   	case CustomAttachmentType.bubble:
			   		BubbleAttachment bubbleAttachment = (BubbleAttachment)customAttachment;
			   		if (bubbleAttachment != null && bubbleAttachment.getBubble() != null && bubbleAttachment.getBubble().isFirstSend())
			   		{
						pushViewHolder.txtName.setText("系统消息：");
						pushViewHolder.txtContent.setText((isNullOrEmpty(member.getNick()) ? "未知用户" : member.getNick()) + " 我点亮了");
			   		}
			   		break;
				}
			}
		} catch (Exception e) {
			XLog.e(e.getMessage());
			e.printStackTrace();
		}
    }
    
    /**
     * 处理文本消息
     * @param holder
     * @param message
     */
    private void handlerTextMessage(PushViewHolder holder, IMMessage message)
    {
    	try {
			if (message != null)
			{
				pushViewHolder.txtContent.setTextColor( Color.parseColor("#FFFFFF"));
				if (message.getDirect() == MsgDirectionEnum.Out)
				{
					//发出去的消息
					pushViewHolder.txtName.setText(isNullOrEmpty(Config.User.getNickName()) ? "未知用户: " : Config.User.getNickName() + ": ");
					pushViewHolder.txtContent.setText(isNullOrEmpty(message.getContent()) ? "" : message.getContent());
				}
				else if (message.getDirect() == MsgDirectionEnum.In)
				{
					//接受到的消息
					ChatRoomMessage chatMessage  = (ChatRoomMessage)message;
					pushViewHolder.txtName.setText(isNullOrEmpty(chatMessage.getChatRoomMessageExtension().getSenderNick()) ? "未知用户: " : chatMessage.getChatRoomMessageExtension().getSenderNick() + ": ");
					pushViewHolder.txtContent.setText(isNullOrEmpty(message.getContent()) ? "" : message.getContent());
				}
			}
		} catch (Exception e) {
			XLog.e(e.getMessage());
			e.printStackTrace();
		}

    }
	
	private boolean isNullOrEmpty(String o)
	{
		if (o != null)
		{
			if (o.length() == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return true;
		}
	}
	
	public class PushViewHolder
	{
		TextView txtName;
		TextView txtContent;
	}

}
