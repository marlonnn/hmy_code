package com.BC.entertainmentgravitation;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;

import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.BC.entertainmentgravitation.entity.StarLiveVideoInfo;
import com.BC.entertainmentgravitation.fragment.PushVideoFragment;
import com.BC.entertainmentgravitation.fragment.ScrollListener;
import com.BC.entertainmentgravitation.fragment.SurfaceFragment;
import com.summer.fragment.BaseFragment;

/**
 * 明星直播
 * @author zhongwen
 *
 */
public class PushVideoActivity extends FragmentActivity {
	
	private View rootView;
	
	private ScrollListener listener;
	
	private PushVideoFragment fragment;
	
	private ChatRoom chatRoom;
	
	public interface TouchListener
	{
		boolean onTouchEvent(MotionEvent event);  
	}
	
	// 保存MyTouchListener接口的列表  
	private ArrayList<TouchListener> myTouchListeners = new ArrayList<TouchListener>();
	
	/** 
	* 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法 
	* @param listener 
	*/  
	public void registerMyTouchListener(TouchListener listener) {  
	     myTouchListeners.add(listener);  
	}  
	      
	/** 
	* 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法 
	* @param listener 
	*/  
	public void unRegisterMyTouchListener(TouchListener listener) {  
	    myTouchListeners.remove( listener );  
	}  
	      
	/** 
	* 分发触摸事件给所有注册了MyTouchListener的接口 
	*/  
	@Override  
	public boolean dispatchTouchEvent(MotionEvent ev) {   
	    for (TouchListener listener : myTouchListeners) {  
	        listener.onTouchEvent(ev);  
	    }  
	    return super.dispatchTouchEvent(ev);  
	}  

    //滤镜（涉及硬件编码）的相关操作
    Blacklist[] g_blacklist = { //可扩展，需修改
    		new Blacklist("L39h", 19),
    		new Blacklist("N1", 22)
    };
    
    public class Blacklist {
    	public Blacklist(String model, int api){
    		mModel = model;
    		mApi = api;
    	}
    	public String getModel() {
    		return mModel;
    	}
    	public int getApi() {
    		return mApi;
    	}
    	private String mModel;
    	private int mApi;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        rootView = findViewById(R.id.root_content);
        Intent intent = getIntent();
        if (intent != null)
        {
            String cid = intent.getStringExtra("cid");
        	String pushUrl = intent.getStringExtra("pushUrl");
        	String chatroomid = intent.getStringExtra("chatroomid");
        	chatRoom = new ChatRoom();
        	chatRoom.setCid(cid);
        	chatRoom.setChatroomid(chatroomid);
        	chatRoom.setPushUrl(pushUrl);
        	chatRoom.setFilter(checkVideoResolution());
            fragment = new PushVideoFragment(chatRoom);
        }

        listener = fragment.CreateScrollListener();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.layout_video_play, fragment)
                .commit();
        new SurfaceFragment(listener, chatRoom).show(getSupportFragmentManager(), "push video");
    }
    
    private boolean checkVideoResolution()
    {
    	if (android.os.Build.VERSION.SDK_INT < 19)
    	{
    		return false;
    	}
    	else if(checkCurrentDeviceInBlacklist())
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    
    private boolean checkCurrentDeviceInBlacklist(){ 
    	boolean bInBlacklist = false;
    	String model = Build.MODEL;
    	int api = Build.VERSION.SDK_INT;
    	
    	int listsize = g_blacklist.length;
    	
    	for(int i = 0; i < listsize; i++)
    	{
    		if(model.equals(g_blacklist[i].getModel()) && api == g_blacklist[i].getApi())
    			bInBlacklist = true;
    	}    	
    	
    	return bInBlacklist;
    }
}

