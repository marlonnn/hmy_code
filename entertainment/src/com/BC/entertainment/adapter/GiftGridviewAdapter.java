package com.BC.entertainment.adapter;

import java.util.List;

import com.BC.entertainment.chatroom.gift.BaseGift;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GiftGridviewAdapter extends BaseAdapter{
	
	private Context context;

	private List<BaseGift> baseActions;

	public GiftGridviewAdapter(Context context, List<BaseGift> baseActions) {
		this.context = context;
		this.baseActions = baseActions;
	}

	@Override
	public int getCount() {
		return baseActions.size();
	}

	@Override
	public Object getItem(int position) {
		return baseActions.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemlayout;
//		if (convertView == null) {
//			itemlayout = LayoutInflater.from(context).inflate(R.layout.nim_actions_item_layout, null);
//		} else {
//			itemlayout = convertView;
//		}
//
//		BaseAction viewHolder = baseActions.get(position);
//		((ImageView) itemlayout.findViewById(R.id.imageView)).setBackgroundResource(viewHolder.getIconResId());
//		((TextView) itemlayout.findViewById(R.id.textView)).setText(context.getString(viewHolder.getTitleId()));
		return null;
	}

}
