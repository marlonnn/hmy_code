package com.BC.entertainment.service;

import java.util.List;

import com.BC.entertainmentgravitation.ChatActivity;
import com.BC.entertainmentgravitation.R;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.config.Config;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class MessageService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	  
    @Override  
    public void onCreate() {  
        super.onCreate(); 
        registerObservers(true);
    }  
  
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        return super.onStartCommand(intent, flags, startId);  
    }  
      
    @Override  
    public void onDestroy() {  
        super.onDestroy();  
        registerObservers(false);
    } 
    
    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeReceiveMessage(incomingMessageObserver, register);
    }

    /**
     * 消息接收观察者
     */
    @SuppressWarnings("serial")
	Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            if (messages == null || messages.isEmpty()) {
                return;
            }
            for (IMMessage message : messages) {
                if (message == null) {
                    return;
                }

                sendNotification(message);
            }
        }
    };
    
	private void sendNotification(IMMessage message)
	{   
		if (message != null)
		{
//			if (message.getFromAccount().contains(Config.User.getUserName()))
			{
				String ns = Context.NOTIFICATION_SERVICE;   
				NotificationManager nm =    
				    (NotificationManager) getSystemService(ns);   
				   
				//Create Notification Object   
				int icon = R.drawable.app_logo; //通知图标  
				long when = System.currentTimeMillis();   
				   
				Notification notification =    
				    new Notification(icon, "通知消息", when);   
				notification.defaults |= Notification.DEFAULT_SOUND; 
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				//Set ContentView using setLatestEvenInfo   
				Intent notificationIntent = new Intent(this, ChatActivity.class); 
				Bundle b = new Bundle();
				b.putString("username", message.getSessionId());
				notificationIntent.putExtras(b);
				PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);  
				notification.setLatestEventInfo(this, message.getFromNick() + "发来消息", message.getContent(), contentIntent); 
//				startForeground(1, notification); 
				//Send notification   
				//The first argument is a unique id for this notification.   
				//This id allows you to cancel the notification later   
				//This id also allows you to update your notification   
				//by creating a new notification and resending it against that id   
				//This id is unique with in this application   
					        nm.notify(1, notification);   
			}
		}

	 }   
}
