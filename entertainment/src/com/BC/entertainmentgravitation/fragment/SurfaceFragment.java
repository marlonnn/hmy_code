package com.BC.entertainmentgravitation.fragment;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.BC.entertainmentgravitation.entity.StarLiveVideoInfo;
import com.summer.logger.XLog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class SurfaceFragment extends DialogFragment {
	
	private ViewPager viewPager;
	
	private ScrollListener listener;
	
	private ChatRoom chatRoom;
	
	private ExitFragmentListener exitListener;
	
	public SurfaceFragment(ScrollListener listener, ChatRoom chatRoom)
	{
		this.listener = listener;
		this.chatRoom = chatRoom;
	}
	
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof ExitFragmentListener)
		{
			this.exitListener = (ExitFragmentListener) getActivity();
		}
	}



	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}



	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_surface, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if(position == 0)
                {
                	XLog.i("position == 0");
                	return new SurfaceEmptyFragment();
                }
                else if (position == 1)
                {
                	XLog.i("position == 1");
                	return new TopSurfaceFragment(chatRoom);
                }
                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
        viewPager.setCurrentItem(1);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}
	
	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.SurfaceDialog){
            @Override
            public void onBackPressed() {
                XLog.e("onBackPressed");
                if(exitListener != null)
                {
                    exitListener.isExit(true);
                }
                super.onBackPressed();
            }
        };
        return dialog;
	}
	
}
