package com.BC.entertainmentgravitation.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.BC.entertainmentgravitation.R;
import com.summer.fragment.BaseFragment;

public class FoundFragment extends BaseFragment implements OnClickListener{
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_top_surface_empty, container, false);
	}

	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		
	}

}
