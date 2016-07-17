package com.BC.entertainmentgravitation.getui;

import org.json.JSONObject;

import com.BC.entertainmentgravitation.MessageCenterActivity;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.GeTui;
import com.BC.entertainmentgravitation.entity.GeTuiDao;
import com.google.gson.Gson;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.summer.logger.XLog;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PushReceiver extends BroadcastReceiver{
	
	private Gson gson = new Gson();
	
//	private NotificationManager messageNotificatioManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	@Override
	public void onReceive(Context context, Intent intent) {
		 Bundle bundle = intent.getExtras();
		 XLog.i("onReceive() action=" + bundle.getInt("action"));
		 switch (bundle.getInt(PushConsts.CMD_ACTION)) 
		 {
         case PushConsts.GET_MSG_DATA:
             // 获取透传数据
             // String appid = bundle.getString("appid");
             byte[] payload = bundle.getByteArray("payload");

             String taskid = bundle.getString("taskid");
             String messageid = bundle.getString("messageid");

             // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
             boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
             System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));

             if (payload != null) {
                 String data = new String(payload);

                 XLog.i("receiver payload : " + data);
                 
         		try {
         			JSONObject jsonObject=new JSONObject(data);
         			GeTui g = new GeTui();
         			g.setMessagecontent(jsonObject.getString("messagecontent"));
         			g.setMessagetitle(jsonObject.getString("messagetitle"));
         			g.setMessagetype(jsonObject.getString("messagetype"));
         			if (g.getMessagetype() != null && g.getMessagetype().contains("3"))
         			{
             			g.setMessageid(jsonObject.getString("messageid"));
         			}
         			g.setTime(String.valueOf(System.currentTimeMillis()));//毫秒
         			new GeTuiDao(context).add(g);
         			sendNotification(context, g);

				} catch (Exception e) {
					e.printStackTrace();
				}
             }
             break;
         case PushConsts.GET_CLIENTID:
             // 获取ClientID(CID)
             // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
             String cid = bundle.getString("clientid");
//             if (GetuiSdkDemoActivity.tView != null) {
//                 GetuiSdkDemoActivity.tView.setText(cid);
//             }
             break;
             
         case PushConsts.THIRDPART_FEEDBACK:
             /*
              * String appid = bundle.getString("appid"); String taskid =
              * bundle.getString("taskid"); String actionid = bundle.getString("actionid");
              * String result = bundle.getString("result"); long timestamp =
              * bundle.getLong("timestamp");
              * 
              * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " +
              * taskid); Log.d("GetuiSdkDemo", "actionid = " + actionid); Log.d("GetuiSdkDemo",
              * "result = " + result); Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
              */
             break;

         default:
             break;
		 }
	}
	
	  private void sendNotification(Context ctx, GeTui geTui)   
	    {   
	        String ns = Context.NOTIFICATION_SERVICE;   
	        NotificationManager nm =    
	            (NotificationManager)ctx.getSystemService(ns);   
	           
	        //Create Notification Object   
	        int icon = R.drawable.app_logo; //通知图标  
	        long when = System.currentTimeMillis();   
	           
	        Notification notification =    
	            new Notification(icon, geTui.getMessagetitle(), when);   
	        notification.defaults |= Notification.DEFAULT_SOUND; 
	        notification.flags = Notification.FLAG_AUTO_CANCEL;
	        //Set ContentView using setLatestEvenInfo   
	        Intent notificationIntent = new Intent(ctx, MessageCenterActivity.class); 
	        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);  
	        notification.setLatestEventInfo(ctx, geTui.getMessagetitle(), geTui.getMessagecontent(), contentIntent);           
	        //Send notification   
	        //The first argument is a unique id for this notification.   
	        //This id allows you to cancel the notification later   
	        //This id also allows you to update your notification   
	        //by creating a new notification and resending it against that id   
	        //This id is unique with in this application   
	        nm.notify(1, notification);   
	    }   

}
