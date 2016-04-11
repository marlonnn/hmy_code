package com.BC.entertainmentgravitation.dialog;

import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.BC.entertainment.view.BaseSelcetWheel;
import com.BC.entertainmentgravitation.R;

public class SelectWheel4 extends BaseSelcetWheel {
	WheelView content1;
	WheelView content2;
	WheelView content3;
	WheelView content4;

	public iWheel wheelInterfasc1;
	public List<String> contentList1 = new ArrayList<String>();
	public List<String> contentList2 = new ArrayList<String>();
	public List<String> contentList3 = new ArrayList<String>();
	public List<String> contentList4 = new ArrayList<String>();
	String content1String = "";
	String content2String = "";
	String content3String = "";
	String content4String = "";

	int selectItem1 = 0;
	int selectItem2 = 0;
	int selectItem3 = 0;
	int selectItem4 = 0;

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
					content1String = SelectWheel4.this.contentList1
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
		if (contentList2 == null) {
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
					content2String = SelectWheel4.this.contentList2
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
		if (contentList3 == null) {
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
					content3String = SelectWheel4.this.contentList3
							.get(newValue);
					changedUp();
				}
			}
		});
	}

	public void setContentList4(List<String> contentList4) {
		if (contentList4 == null) {
			return;
		}
		this.contentList4 = contentList4;
		if (contentList4.size() > 0) {
			content4String = contentList4.get(0);
		}
		content4.setViewAdapter(new SelectAdapter(getContext(), contentList4));
		content4.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (wheelInterfasc != null) {
					content4String = SelectWheel4.this.contentList4
							.get(newValue);
					changedUp();
				}
			}
		});
	}

	public int getSelectItem1() {
		return selectItem1;
	}

	public void setSelectItem1(int selectItem1) {
		this.selectItem1 = selectItem1;
		content1.setCurrentItem(selectItem1);
	}

	public int getSelectItem2() {
		return selectItem2;
	}

	public void setSelectItem2(int selectItem2) {
		this.selectItem2 = selectItem2;
		content2.setCurrentItem(selectItem2);
	}

	public int getSelectItem3() {
		return selectItem3;
	}

	public void setSelectItem3(int selectItem3) {
		this.selectItem3 = selectItem3;
		content3.setCurrentItem(selectItem3);
	}

	public int getSelectItem4() {
		return selectItem4;
	}

	public void setSelectItem4(int selectItem4) {
		this.selectItem4 = selectItem4;
		content4.setCurrentItem(selectItem4);
	}

	public SelectWheel4(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SelectWheel4(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SelectWheel4(Context context) {
		super(context);
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
			case R.id.content4:
				content4 = (WheelView) child;
				content4.setViewAdapter(new SelectAdapter(getContext(),
						contentList4));
				content4.addChangingListener(new OnWheelChangedListener() {
					@Override
					public void onChanged(WheelView wheel, int oldValue,
							int newValue) {
						content4String = contentList4.get(newValue);
						changedUp();
					}
				});
				content4.setVisibleItems(3);
				content4.setDrawShadows(false);
				break;
			}
		}
		super.addView(child, index, params);
	}

	private void changedUp() {
		if (wheelInterfasc != null) {
			wheelInterfasc.selectValue(1, content1String + content2String
					+ content3String + content4String, true);
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

