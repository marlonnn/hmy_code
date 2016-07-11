package com.BC.entertainmentgravitation.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.BC.entertainment.adapter.PrivateRecycleAdapter.OnItemClickListener;
import com.BC.entertainment.adapter.PrivateRecycleAdapter;
import com.BC.entertainmentgravitation.ChatActivity;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Contact;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.summer.config.Config;
import com.summer.fragment.BaseFragment;

/**
 * 私信
 * @author zhongwen
 *
 */
public class PrivateFragment extends BaseFragment implements OnClickListener, OnItemClickListener {

	private View rootView;
	
	private RecyclerView messageList;
	
//	private List<GeTui> geTuis;
	
	private PrivateRecycleAdapter adapter;
	
	private List<Contact> myContacts = new ArrayList<>();
	
	@SuppressLint("InflateParams") @Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_message_private, null);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getRecentContacts();
		ObserChange(true);
	}
	
	private void initView()
	{
		messageList = (RecyclerView) rootView.findViewById(R.id.listViewMessage);
		adapter =  new PrivateRecycleAdapter(getActivity(), myContacts);
        adapter.notifyDataSetChanged();
        adapter.setmOnItemClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        messageList.setVerticalScrollBarEnabled(true);
        messageList.setLayoutManager(linearLayoutManager);
        
        messageList.setItemAnimator(new DefaultItemAnimator());//more的动画效果
		messageList.setAdapter(adapter);
	}
	
	@Override
	public void onItemClick(View view, int position) {
		Contact g = (Contact)view.getTag();
		if (g != null)
		{
			Intent intent = new Intent(getActivity(), ChatActivity.class); 
			Bundle b = new Bundle();
			b.putString("username", g.getId());
			intent.putExtras(b);
			startActivity(intent);
		}
//		if (g != null)
//		{
//			g.setHasRead(true);
//			new GeTuiDao(getActivity()).update(g);
//			adapter.notifyDataSetChanged();
//		}
	}
	
	/**
	 * 忽略未读
	 */	
//	public void ignoreMessage()
//	{
//		new GeTuiDao(getActivity()).update();
//		if (geTuis != null)
//		{
//			for (int i=0; i<geTuis.size(); i++)
//			{
//				geTuis.get(i).setHasRead(true);
//			}
//		}
//		adapter.notifyDataSetChanged();
//	}
	
	private void getRecentContacts()
	{
		 NIMClient.getService(MsgService.class).queryRecentContacts()
	     .setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
	       @Override
	       public void onResult(int code, List<RecentContact> recents, Throwable e) {
	            // recents参数即为最近联系人列表（最近会话列表）
			   List<RecentContact> contacts = recents;
	    	   for (RecentContact r : contacts )
	    	   {
	    		   Contact reContact = new Contact();
	    		   String account = r.getFromAccount();
	    		   reContact.setAccount(account);
	    		   NimUserInfo user = NIMClient.getService(UserService.class).getUserInfo(account);
	    		   reContact.setAvator(formatPortrait(user.getAvatar()));
	    		   reContact.setContent(r.getContent());
	    		   reContact.setId(r.getContactId());
	    		   reContact.setNick(r.getFromNick());
	    		   reContact.setTime(r.getTime());
	    		   myContacts.add(reContact);
	    	   }
	    	   initView();
	       }
	    });
	}
	Observer<List<RecentContact>> messageObserver =
		    new Observer<List<RecentContact>>() {
		        @Override
		        public void onEvent(List<RecentContact> messages) {
					   List<RecentContact> contacts = messages;
			    	   for (RecentContact r : contacts )
			    	   {
			    		   Contact reContact = new Contact();
			    		   String account = r.getFromAccount();
			    		   reContact.setAccount(account);
			    		   NimUserInfo user = NIMClient.getService(UserService.class).getUserInfo(account);
			    		   reContact.setAvator(formatPortrait(user.getAvatar()));
			    		   reContact.setContent(r.getContent());
			    		   reContact.setId(r.getContactId());
			    		   reContact.setNick(r.getFromNick());
			    		   reContact.setTime(r.getTime());
//			    		   myContacts.add(reContact);
			    		   updateContacts(reContact);
			    	   }
			  		 adapter.notifyDataSetChanged();
		        }
		};
	private void updateContacts(Contact contact)
	{
		if (contact != null)
		{
			for (int i=0; i< myContacts.size(); i++ )
			{
				if (myContacts.get(i).getAccount().contains(contact.getAccount()))
				{
					myContacts.get(i).setContent(contact.getContent());
					myContacts.get(i).setTime(contact.getTime());
				}
				else
				{
					myContacts.add(contact);
				}
			}
		}
	}
	
	private void ObserChange(boolean register)
	{

	//  注册/注销观察者
		NIMClient.getService(MsgServiceObserve.class)
		    .observeRecentContact(messageObserver, register);
	}
	
	private String formatPortrait(String portrait)
	{
		String ret = portrait;
		try {
			if (portrait != null)
			{
				String s[] = portrait.split("/");
				
				if (s[2] != null && !s[2].contains("app.haimianyu.cn"))
				{
					ret = "http://app.haimianyu.cn/" + portrait;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	@SuppressWarnings("unused")
	private void getUserInfo()
	{
		List<NimUserInfo> users = NIMClient.getService(UserService.class).getAllUserInfo();
		NimUserInfo user = NIMClient.getService(UserService.class).getUserInfo(Config.User.getUserName());
	}

	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void RequestSuccessful(int status, String jsonString, int taskType) {
		
	}

}
