package com.BC.entertainment.view;

import com.BC.entertainmentgravitation.R;
import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.BC.entertainmentgravitation.dialog.iWheel;

public class BaseSelcetWheel extends LinearLayout {
	public WheelView content;
	public iWheel wheelInterfasc;
	public List<String> contentList = new ArrayList<String>();

	public BaseSelcetWheel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public BaseSelcetWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public BaseSelcetWheel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public List<String> getContentList() {
		return contentList;
	}

	public void getAll() {
		// TODO Auto-generated method stub
	}

	public void setContentList(List<String> contentList) {
		this.contentList = contentList;
		if (content == null) {
			return;
		}
		content.setViewAdapter(new SelectAdapter(getContext(), contentList));
		content.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (wheelInterfasc != null) {
					wheelInterfasc.selectValue(1,
							BaseSelcetWheel.this.contentList.get(newValue),
							true);
				}
			}
		});
	}

	/**
	 * Adapter for countries
	 */
	private class SelectAdapter extends AbstractWheelTextAdapter {

		private List<String> list;

		// Countries names
		protected SelectAdapter(Context context, List<String> list) {
			super(context, R.layout.wheel_text_item);
			this.list = list;
		}

		@Override
		public int getItemsCount() {
			return list.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return list.get(index);
		}
	}
}

