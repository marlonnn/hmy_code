package com.BC.entertainment.adapter;

import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.content.Context;

import com.BC.entertainmentgravitation.R;

/**
 * Adapter for countries
 */
public class BankAdapter extends AbstractWheelTextAdapter {

	private String[] list;

	public BankAdapter(Context context, String[] list) {
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