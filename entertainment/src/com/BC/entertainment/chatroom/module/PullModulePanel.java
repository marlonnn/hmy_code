package com.BC.entertainment.chatroom.module;

import java.util.HashMap;
import java.util.List;
import org.apache.http.NameValuePair;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.BC.entertainment.cache.ChatCache;
import com.BC.entertainment.cache.GiftCache;
import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.chatroom.extension.BaseEmotion;
import com.BC.entertainment.chatroom.extension.BubbleAttachment;
import com.BC.entertainment.chatroom.extension.CustomAttachment;
import com.BC.entertainment.chatroom.extension.CustomAttachmentType;
import com.BC.entertainment.chatroom.extension.EmotionAttachment;
import com.BC.entertainment.chatroom.extension.FontAttachment;
import com.BC.entertainment.chatroom.gift.GiftHelper;
import com.BC.entertainment.task.ThreadUtil;
import com.BC.entertainmentgravitation.FinishActivity;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Member;
import com.BC.entertainmentgravitation.fragment.PullFragment;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.adapter.CommonAdapter.ViewHolder;
import com.summer.config.Config;
import com.summer.handler.InfoHandler;
import com.summer.logger.XLog;
import com.summer.utils.JsonUtil;
import com.summer.view.Pandamate;

public class PullModulePanel {
	
	private final int tipValue = 10;//一条消息10个娱币
	
	private Container container;
	private InfoHandler handler;
	private PullFragment pullFragment;
	
    //module
    public InputPannel inputPanel;
    public DanmakuPanel danmakuPanel;
    public BubbingPanel bubblePanel;
    
	private Bubbling bubbling;//气泡
	public ImageView imageViewAnimation;
	
    public PullModulePanel(PullFragment pullFragment, Container container, View rootView, InfoHandler handler)
    {
    	this.container = container;
    	this.handler = handler;
    	this.pullFragment = pullFragment;
    	this.imageViewAnimation = pullFragment.imageViewAnimation;
    	
    	bubblePanel = new BubbingPanel(container);
		bubbling = (Bubbling) rootView.findViewById(R.id.pullBubbling);
		inputPanel = new InputPannel(container, rootView, GiftCache.getInstance().getListGifts(), bubbling);
		inputPanel.setAmountMoney(InfoCache.getInstance().getPersonalInfo().getEntertainment_dollar());
		
		bubbling.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				bubbling.start();
				bubblePanel.sendBubbling(false, bubbling.getmIndex());
			}
		});
		
		danmakuPanel = new DanmakuPanel(container, rootView);
    }
    
	/**
	 * 判断用户是否可以发送礼物 娱币余额
	 * @param message
	 * @return
	 */
    public boolean CanSendCustomMessage(IMMessage message)
    {
    	boolean can = false;
    	
    	if (message != null)
    	{
    		try {
				
				if (message.getMsgType() == MsgTypeEnum.text)
				{
					if (tipValue < Integer.parseInt(InfoCache.getInstance().getPersonalInfo().getEntertainment_dollar()))
					{
						can = true;
					}
				}
				else if (message.getMsgType() == MsgTypeEnum.custom)
				{
					CustomAttachment customAttachment = (CustomAttachment)message.getAttachment();
					switch(customAttachment.getType())
					{
					/**
					 * 表情
					 */
					case CustomAttachmentType.emotion:
						EmotionAttachment emotionAttachment = (EmotionAttachment)customAttachment;
						if (emotionAttachment != null)
						{
							if (emotionAttachment.getEmotion().getValue() < Integer.parseInt(InfoCache.getInstance().getPersonalInfo().getEntertainment_dollar()))
							{
								can = true;
							}
						}
						break;
					/**
					 * 字体
					 */
					case CustomAttachmentType.font:
						FontAttachment fontAttachment = (FontAttachment)customAttachment;
						if (fontAttachment != null)
						{
							if (fontAttachment.getEmotion().getValue() < Integer.parseInt(InfoCache.getInstance().getPersonalInfo().getEntertainment_dollar()))
							{
								can = true;
							}
						}
						break;
					/**
					 * 气泡
					 */
					case CustomAttachmentType.bubble:
						//气泡暂时不需要娱币
						can = true;
						break;
					}
				}

			} catch (Exception e) {
				can = false;
				e.printStackTrace();
			}
    	}
    	return can;
    }
    
    /**
     * 发送消息 需要请求服务器
     * @param baseEmotion
     * @param type
     * @param handler
     */
    public void SendMessageRequest(BaseEmotion baseEmotion, int type, InfoHandler handler)
    {
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("username", Config.User.getUserName());
    	if (baseEmotion != null)
    	{
			entity.put("user_dollar", String.valueOf(baseEmotion.getValue()));
			entity.put("type", String.valueOf(type));
    	}
    	else
    	{
        	entity.put("user_dollar", String.valueOf(tipValue));
        	entity.put("type", "-1");
    	}
    	entity.put("starid", InfoCache.getInstance().getLiveStar().getUser_name());
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		ThreadUtil.AddToThreadPool(Config.send_gift, "send message request", params, handler);
    }
    
    /**
     * 处理自定义字体或者礼物消息
     * @param holder
     * @param message
     */
    public void HandlerCustomMessage(@SuppressWarnings("rawtypes") ViewHolder holder, IMMessage message)
    {
    	if (message != null)
    	{
        	CustomAttachment customAttachment = (CustomAttachment)message.getAttachment();
        	Member member = ChatCache.getInstance().getMember(message.getFromAccount());
        	switch(customAttachment.getType())
        	{
        	case CustomAttachmentType.emotion:
        		EmotionAttachment emotionAttachment = (EmotionAttachment)customAttachment;
        		if (emotionAttachment != null)
        		{
       			    holder.setImageResource(R.id.imageViewMessage, R.drawable.fragment_message_icon);
               	    holder.setTextColor(R.id.txtName, Color.parseColor("#EEB422"));
               	    holder.setText(R.id.txtName, "系统消息：");
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
	       			holder.setImageResource(R.id.imageViewMessage, R.drawable.fragment_message_icon);
	               	holder.setTextColor(R.id.txtName, Color.parseColor("#EEB422"));
	               	holder.setText(R.id.txtName, "系统消息：");
        			String fontName = fontAttachment.getEmotion().getName();
        			holder.setText(R.id.txtContent, (member == null ? "" : member.getNick()) + " 送来了 " + fontAttachment.getEmotion().getName());
        			XLog.i("font gift name: " + fontName);
        			holder.setTextColor(R.id.txtContent, Color.parseColor("#8B658B"));
        		}
        		break;
       	case CustomAttachmentType.bubble:
       		BubbleAttachment bubbleAttachment = (BubbleAttachment)customAttachment;
       		if (bubbleAttachment != null && bubbleAttachment.getBubble() != null && bubbleAttachment.getBubble().isFirstSend())
       		{
       			holder.setImageResource(R.id.imageViewMessage, R.drawable.fragment_message_icon);
               	holder.setTextColor(R.id.txtName, Color.parseColor("#EEB422"));
               	holder.setText(R.id.txtName, "系统消息：");
       			holder.setText(R.id.txtContent, (member == null ? "" : member.getNick()) + " 我点亮了");
       		}

       		break;
        	}
    	}
    }
    
    /**
     * 处理发送的礼物消息
     * @param message
     */
    public void SendCustomMessageRequest(IMMessage message)
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
        			SendMessageRequest(emotionAttachment.getEmotion(), customAttachment.getType(), handler);
        			showAnimate(emotionAttachment.getEmotion());
        		}
        		break;
        	case CustomAttachmentType.font:
        		FontAttachment fontAttachment = (FontAttachment)customAttachment;
        		if (fontAttachment != null)
        		{
        			SendMessageRequest(fontAttachment.getEmotion(), customAttachment.getType(), handler);
        			showAnimate(fontAttachment.getEmotion());
        		}
        		break;
        	case CustomAttachmentType.bubble:
        		BubbleAttachment bubbleAttachment = (BubbleAttachment)customAttachment;
        		if (bubbleAttachment != null)
        		{
        			bubbling.startAnimation(bubbleAttachment.getBubble().getCategory());
        			if (bubbleAttachment.getBubble().isFirstSend())
        			{
        				//添加到消息列表中，显示 用户名：我点亮了
        				pullFragment.saveMessage(message, false);
        			}
        		}
        		break;
        	}
    	}
    }

    public void HandlerCustomMessage(IMMessage message)
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
	             	//保存消息到聊天室消息列表中
        			pullFragment.saveMessage(message, false);
        		}
        		break;
        	case CustomAttachmentType.font:
        		FontAttachment fontAttachment = (FontAttachment)customAttachment;
        		if (fontAttachment != null)
        		{
        			showAnimate(fontAttachment.getEmotion());
	             	//保存消息到聊天室消息列表中
        			pullFragment.saveMessage(message, false);
        		}
        		break;
       	case CustomAttachmentType.bubble:
       		BubbleAttachment bubbleAttachment = (BubbleAttachment)customAttachment;
       		if (bubbleAttachment != null)
       		{
       			bubbling.startAnimation();
       			if (bubbleAttachment.getBubble().isFirstSend())
       			{
       				if (message.getFromAccount() != null && !message.getFromAccount().contains(Config.User.getUserName()))
       				{
        	            //保存消息到聊天室消息列表中
       					//接收到的是别人的消息，同时是第一次点亮，则添加到列表中显示
       					pullFragment.saveMessage(message, false);
       				}
       			}
       		}
       		break;
        	}
    	}
    }
    
    public void HandleNotification(IMMessage message) {
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

        	if (account != null && account.contains(InfoCache.getInstance().getLiveStar().getUser_name()))
        	{
	   			Intent intent = new Intent(container.activity, FinishActivity.class);
	   			intent.putExtra("totalPeople", ChatCache.getInstance().getOnlinePeopleitems().size());
	   			container.activity.startActivity(intent);
	   			if (pullFragment.mediaCallback != null)
	   			{
	   				pullFragment.Destroy();
	   				pullFragment.mediaCallback.finishPullMedia();
	   			}
        	}
        	else
        	{
            	Member member = ChatCache.getInstance().getMember(account);
            	pullFragment.removeMembers(member);
        	}
//        	if (member != null && member.getName().contains(InfoCache.getInstance().getLiveStar().getUser_name()))
//        	{
//	   			Intent intent = new Intent(container.activity, FinishActivity.class);
//	   			intent.putExtra("totalPeople", ChatCache.getInstance().getOnlinePeopleitems().size());
//	   			container.activity.startActivity(intent);
//	   			if (pullFragment.mediaCallback != null)
//	   			{
//	   				pullFragment.Destroy();
//	   				pullFragment.mediaCallback.finishPullMedia();
//	   			}
//        	}

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
     * 游客进入聊天室，发送获取头像信息请求
     * @param username
     */
    private void sendMemberRequest(String username)
    {
    	HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("username", username);
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	ThreadUtil.AddToThreadPool(Config.member_in, "get start info", params, handler);
    }
    
}
