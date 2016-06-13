package com.BC.entertainment.chatroom.module;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

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
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Member;
import com.BC.entertainmentgravitation.fragment.PushFragment;
import com.BC.entertainmentgravitation.util.ListViewUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.adapter.CommonAdapter.ViewHolder;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.logger.XLog;
import com.summer.task.HttpTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.UrlUtil;
import com.summer.view.Pandamate;

public class PushModulePanel {
	private final int tipValue = 10;//一条消息10个娱币
	
	private Container container;
	private InfoHandler handler;
	private PushFragment pushFragment;
	
    //module
    public InputPannel inputPanel;
    public DanmakuPanel danmakuPanel;
    public BubbingPanel bubblePanel;
    
	private Bubbling bubbling;//气泡
	public ImageView imageViewAnimation;
	
    private HttpTask httpTask;//更新娱票线程
    
    public PushModulePanel(PushFragment pushFragment, Container container, View rootView, InfoHandler handler)
    {
    	this.handler = handler;
    	this.container = container;
    	this.pushFragment = pushFragment;
    	this.imageViewAnimation = pushFragment.imageViewAnimation;
    	
    	bubblePanel = new BubbingPanel(container);
		bubbling = (Bubbling) rootView.findViewById(R.id.bubbling);
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
        				pushFragment.saveMessage(message, false);
        			}
        		}
        		break;
        	}
    	}
    }
    
    /**
     * 处理自定义消息
     * @param message
     */
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
        			pushFragment.saveMessage(message, false);
        		}
        		break;
        	case CustomAttachmentType.font:
        		FontAttachment fontAttachment = (FontAttachment)customAttachment;
        		if (fontAttachment != null)
        		{
        			showAnimate(fontAttachment.getEmotion());
	             	//保存消息到聊天室消息列表中
        			pushFragment.saveMessage(message, false);
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
       					pushFragment.saveMessage(message, false);
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
        	Member member = ChatCache.getInstance().getMember(account);
        	pushFragment.removeMembers(member);
        	updateRoomMember();
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
     * 主播进入聊天室更新聊天室状态
     * @param master
     * @param isLeave
     */
    public void updateVideoStatus(boolean isLeave)
    {
    	//是主播进入聊天室才发送聊天室状态到后台
    	XLog.i("this is master: " + container.chatRoom.isMaster() );
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("username", Config.User.getUserName());
    	if(isLeave){
    		entity.put("status", "1");
    	}
    	else
    	{
        	entity.put("status", "0");
    	}
    	XLog.i("this is master: " + entity.toString());
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		ThreadUtil.AddToThreadPool(Config.update_status, "send update status request", params, handler);
    }
    
    /**
     * 如果是主播，将聊天室总人数更新到后台
     */
    public void updateRoomMember()
    {
    	//是主播才更新聊天室人数到后台
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("username", Config.User.getUserName());
    	entity.put("peoples", String.valueOf(ChatCache.getInstance().getOnlinePeopleitems().size()));
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		ThreadUtil.AddToThreadPool(Config.update_room, "send update room member request", params, handler);
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
    
	/**
	 * 开始获取娱票线程
	 */
	public void startGetYuPiao()
	{
    	try {
			HashMap<String, String> entity = new HashMap<String, String>();
			entity.put("username", Config.User.getUserName());
			List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
			httpTask = new HttpTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, "get yu piao info", params, UrlUtil.GetUrl(Config.query_piao));
			httpTask.setTaskType(Config.query_piao);
			InfoHandler handler = new InfoHandler(pushFragment);
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
	public void stopUpdateYuPiao()
	{
		httpTask.CancelTask();
	}
	
    /***********************************************************注册相关**********************************************************************/
    public void RegisterObservers(boolean register) {
    	if(! register){
    		logoutChatRoom();
    	}
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, register);
    }
    
	private void logoutChatRoom() {
		if (container.chatRoom != null )
		{
			//主播离开需要更新直播间状态
			updateVideoStatus(true);
		}
		NIMClient.getService(ChatRoomService.class).exitChatRoom(container.chatRoom.getChatroomid());
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
         boolean needScrollToBottom = ListViewUtil.isLastMessageVisible(pushFragment.GetMessageListView());
         boolean needRefresh = false;
         for (IMMessage message : messages) {
         	
             if (isMyMessage(message)) {
             	 danmakuPanel.showDanmaku(message);
                 if (message.getMsgType() == MsgTypeEnum.notification)
                 {
                 	 HandleNotification(message);
                 	//保存消息到聊天室消息列表中
                 	pushFragment.saveMessage(message, false);
                 }
                 else if(message.getMsgType() == MsgTypeEnum.custom)
                 {
                 	HandlerCustomMessage(message);
 	            	//保存消息到聊天室消息列表中
                 }
                 else if(message.getMsgType() == MsgTypeEnum.text)
                 {
 	            	//保存消息到聊天室消息列表中
                	 pushFragment.saveMessage(message, false);
                 }
                 needRefresh = true;
                 XLog.i(message.getMsgType());
             }
         }
         if (needRefresh) {
        	 pushFragment.GetAdapter().RefreshMessageList();
         }

         // incoming messages tip
         IMMessage lastMsg = messages.get(messages.size() - 1);
         if (isMyMessage(lastMsg) && needScrollToBottom) {
             ListViewUtil.scrollToBottom(pushFragment.GetMessageListView());
         }
     }
     
     public void SendMessage(IMMessage msg)
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
 		OnMsgSend(msg);
     }
     
     /**
      *  发送消息后，更新本地消息列表
      * @param message
      */
     public void OnMsgSend(IMMessage message) {
         // add to listView and refresh
    	 pushFragment.saveMessage(message, false);
    	 pushFragment.GetAdapter().RefreshMessageList();
         danmakuPanel.showDanmaku(message);
         ListViewUtil.scrollToBottom(pushFragment.GetMessageListView());
     }
     
 	public void ShowShare() {
		String name = Config.User.getNickName();
		ShareSDK.initSDK(container.activity, "10ee118b8af16");

		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字
		oks.setTitle("演员在直播！导演你快来......");
		oks.setText("看演员，去海绵娱直播APP!" + "(" + name
				+ "正在直播中)");
		oks.setSite(container.activity.getString(R.string.app_name));
		// 分享链接地址
		oks.setUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.BC.entertainmentgravitation");
		// logo地址
		oks.setImageUrl("http://app.haimianyu.cn/DOWNLOAD/app_logo.png");
		oks.show(container.activity);
	}
     
     public boolean isMyMessage(IMMessage message) {
         return message.getSessionType() == container.sessionType
                 && message.getSessionId() != null
                 && message.getSessionId().equals(container.chatRoom.getChatroomid());
     }
}
