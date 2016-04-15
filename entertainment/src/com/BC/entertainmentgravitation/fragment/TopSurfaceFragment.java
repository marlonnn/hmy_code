package com.BC.entertainmentgravitation.fragment;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.BC.entertainmentgravitation.entity.StarLiveVideoInfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TopSurfaceFragment extends Fragment{
	
	private ChatRoom chatRoom;
	
	public TopSurfaceFragment(ChatRoom chatRoom)
	{
		this.chatRoom = chatRoom;
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_top_surface, container, false);
	}

	
}
