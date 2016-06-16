package com.BC.entertainment.adapter;

import java.util.List;

import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.content.Context;

import com.BC.entertainmentgravitation.entity.RegionItem;

/**
 * Adapter for countries
 */
public class RegionAdapter extends AbstractWheelTextAdapter {

	private List<RegionItem> list;

	public RegionAdapter(Context context, List<RegionItem> list) {
		super(context);
		this.list = list;

	}

	@Override
	public int getItemsCount() {
		return list.size();
	}

	@Override
	protected CharSequence getItemText(int index) {
		return list.get(index).getName();
	}
}