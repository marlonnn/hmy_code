package com.BC.entertainmentgravitation.fragment;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainment.adapter.AuthenPictureAdapter;
import com.summer.fragment.BaseDialogFragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

/**
 * 查看 认证照片
 * @author zhongwen
 *
 */
public class AuthenPictureFragment extends BaseDialogFragment{

	private View contentView;// editConnect
	private OnPageChangeListener changeListener;
	private AuthenPictureAdapter adapter;
	private ViewPager pager;
	private int currentItem = 0;
	
	public AuthenPictureFragment(int currentItem)
	{
		this.currentItem = currentItem;
	}

	public OnPageChangeListener getChangeListener() {
		return changeListener;
	}

	public void setChangeListener(OnPageChangeListener changeListener) {
		this.changeListener = changeListener;
	}

	public AuthenPictureAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(AuthenPictureAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.fragment_picture, container,
				false);
		initializeView();
		return contentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		super.onViewCreated(view, savedInstanceState);
	}

	@SuppressWarnings("deprecation")
	private void initializeView() {
		contentView.findViewById(R.id.negativeButton).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						dismiss();
					}
				});
		pager = (ViewPager) contentView.findViewById(R.id.picture);
		if (adapter != null) {
			pager.setAdapter(adapter);
			pager.setCurrentItem(currentItem);
		}
		if (changeListener != null) {
			pager.setOnPageChangeListener(changeListener);
		}
	}
	
	public void setPage(int item)
	{
		if (pager != null)
		{
			pager.setCurrentItem(item);
		}
	}
	
	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		
	}

}
