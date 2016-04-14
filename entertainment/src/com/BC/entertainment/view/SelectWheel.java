package com.BC.entertainment.view;

import java.util.ArrayList;

import kankan.wheel.widget.WheelView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.BC.entertainmentgravitation.R;

public class SelectWheel extends BaseSelcetWheel {

	public SelectWheel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SelectWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public SelectWheel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

	}

	public void setCurrentItem(int item) {
		// TODO Auto-generated method stub
		content.setCurrentItem(item);
	}

	@Override
	public void getAll() {
		// TODO Auto-generated method stub
		super.getAll();
		if (wheelInterfasc != null) {
			if (contentList != null && contentList.size() > 0) {
				wheelInterfasc.selectValue(1, contentList.get(0), true);
			}
		}
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		// TODO Auto-generated method stub
		if (child instanceof WheelView) {
			switch (child.getId()) {
			case R.id.content:
				content = (WheelView) child;
				content.setVisibleItems(3);
				content.setDrawShadows(false);
				setContentList(new ArrayList<String>());
				break;
			}
		}
		super.addView(child, index, params);
	}

}
