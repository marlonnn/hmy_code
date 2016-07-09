package com.BC.entertainment.adapter;

import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.content.Context;

import com.BC.entertainmentgravitation.R;

/**
 * 选择数量
 */
public class QuantityAdapter extends AbstractWheelTextAdapter {

	private String[] list;

	public QuantityAdapter(Context context, String[] list) {
		super(context);
		this.list = list;
		this.setItemResource(R.layout.wheel_text_item);
		this.setItemTextResource(R.id.text);

	}

	@Override
	public int getItemsCount() {
		return list.length;
	}

	@Override
	protected CharSequence getItemText(int index) {
		return list[index];
	}
}