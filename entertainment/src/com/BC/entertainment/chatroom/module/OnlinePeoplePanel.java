package com.BC.entertainment.chatroom.module;

import java.util.LinkedList;

import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.BC.entertainment.adapter.RecyclerViewAdapter;
import com.BC.entertainment.adapter.RecyclerViewAdapter.OnItemClickListener;
import com.BC.entertainment.chatroom.helper.OnlinePeopleCache;
import com.BC.entertainmentgravitation.R;
import com.netease.nim.uikit.session.module.Container;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;

public class OnlinePeoplePanel {
	
    private static final int MESSAGE_CAPACITY = 20;

    // container
    private Container container;
    private View rootView;
    private Handler uiHandler;

	private RecyclerView recycleView;
	
	private RecyclerViewAdapter adapter;
	
	private LinkedList<ChatRoomMember> items;
	
	private OnlinePeopleCache onlinePeopleCache;

	private TextView onlinePeople;

    public OnlinePeoplePanel(Container container, View rootView) {
        this.container = container;
        this.rootView = rootView;
        init();
    }
    
    private void init() {
    	initRecycleView();
        this.uiHandler = new Handler();
    }
    
    public void registerObservers(boolean register)
    {
    	if (onlinePeopleCache != null)
    	{
    		onlinePeopleCache.registerObservers(register);
    	}
    }
    
    private void postInitRercycleView()
    {
        container.activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
            	initRecycleView();
            }
        });
    }
    
    private void initRecycleView(){
    	
    	if(onlinePeopleCache == null)
    	{
    		onlinePeopleCache = OnlinePeopleCache.getInstance();
    	}
    	
    	items = onlinePeopleCache.getItems();
    	
    	onlinePeople = (TextView)rootView.findViewById(R.id.txtViewOnlinePeople);
    	
    	recycleView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
    	
    	adapter = new RecyclerViewAdapter(container.activity, items);
    	
    	onlinePeopleCache.setOnlinePeople(onlinePeople);
    	onlinePeopleCache.setAdapter(adapter);
    	recycleView.setAdapter(adapter);
    	
        //设置RecyclerView的布局管理
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.activity, LinearLayoutManager.VERTICAL, false);
//        recycleView.setLayoutManager(linearLayoutManager);
        recycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        recycleView.setItemAnimator(new DefaultItemAnimator());//more的动画效果
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
    }
    
    
    // 刷新消息列表
    public void refreshMessageList() {
        container.activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
