package com.BC.entertainment.view;

import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.dialog.iWheel;

public class SelectWheel3 extends BaseSelcetWheel {
	WheelView content1;
	WheelView content2;
	WheelView content3;

	public iWheel wheelInterfasc1;
	public List<String> contentList1 = new ArrayList<String>();
	public List<String> contentList2 = new ArrayList<String>();
	public List<String> contentList3 = new ArrayList<String>();
	String content1String = "";
	String content2String = "";
	String content3String = "";

	public List<String> getContentList1() {
		return contentList1;
	}

	public void setContentList1(List<String> contentList1) {
		if (contentList1 == null) {
			return;
		}
		this.contentList1 = contentList1;
		if (contentList1.size() > 0) {
			content1String = contentList1.get(0);
		}
		content1.setViewAdapter(new SelectAdapter(getContext(), contentList1));
		content1.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (wheelInterfasc != null) {
					content1String = SelectWheel3.this.contentList1
							.get(newValue);
					changedUp();
				}
			}
		});
	}

	public List<String> getContentList2() {
		return contentList2;
	}

	public void setContentList2(List<String> contentList2) {
		if (contentList1 == null) {
			return;
		}
		this.contentList2 = contentList2;
		if (contentList2.size() > 0) {
			content2String = contentList2.get(0);
		}
		content2.setViewAdapter(new SelectAdapter(getContext(), contentList2));
		content2.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (wheelInterfasc != null) {
					content2String = SelectWheel3.this.contentList2
							.get(newValue);
					changedUp();
				}
			}
		});
	}

	public List<String> getContentList3() {
		return contentList3;
	}

	public void setContentList3(List<String> contentList3) {
		if (contentList1 == null) {
			return;
		}
		this.contentList3 = contentList3;
		if (contentList3.size() > 0) {
			content3String = contentList3.get(0);
		}
		content3.setViewAdapter(new SelectAdapter(getContext(), contentList3));
		content3.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (wheelInterfasc != null) {
					content3String = SelectWheel3.this.contentList3
							.get(newValue);
					changedUp();
				}
			}
		});
	}

	public SelectWheel3(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SelectWheel3(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public SelectWheel3(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		// TODO Auto-generated method stub
		if (child instanceof WheelView) {
			switch (child.getId()) {
			case R.id.content:
				content1 = (WheelView) child;

				content1.setViewAdapter(new SelectAdapter(getContext(),
						contentList1));
				content1.addChangingListener(new OnWheelChangedListener() {
					@Override
					public void onChanged(WheelView wheel, int oldValue,
							int newValue) {
						content1String = contentList1.get(newValue);
						changedUp();
					}
				});
				content1.setVisibleItems(3);
				content1.setDrawShadows(false);
				break;
			case R.id.content2:
				content2 = (WheelView) child;
				content2.setViewAdapter(new SelectAdapter(getContext(),
						contentList2));
				content2.addChangingListener(new OnWheelChangedListener() {
					@Override
					public void onChanged(WheelView wheel, int oldValue,
							int newValue) {
						content2String = contentList2.get(newValue);
						changedUp();
					}
				});
				content2.setVisibleItems(3);
				content2.setDrawShadows(false);

				break;
			case R.id.content3:
				content3 = (WheelView) child;
				content3.setViewAdapter(new SelectAdapter(getContext(),
						contentList3));
				content3.addChangingListener(new OnWheelChangedListener() {
					@Override
					public void onChanged(WheelView wheel, int oldValue,
							int newValue) {
						content3String = contentList3.get(newValue);
						changedUp();
					}
				});
				content3.setVisibleItems(3);
				content3.setDrawShadows(false);
				break;
			}
		}
		super.addView(child, index, params);
	}

	private void changedUp() {
		if (wheelInterfasc != null) {
			wheelInterfasc.selectValue(1, content1String + content2String
					+ content3String, true);
		}
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
