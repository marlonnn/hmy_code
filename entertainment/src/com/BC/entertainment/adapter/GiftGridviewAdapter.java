package com.BC.entertainment.adapter;

import java.util.List;

import com.BC.entertainment.chatroom.gift.BaseGift;
import com.BC.entertainmentgravitation.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GiftGridviewAdapter extends BaseAdapter{
	
	private Context context;

	private List<BaseGift> baseGifts;

	public GiftGridviewAdapter(Context context, List<BaseGift> baseGifts) {
		this.context = context;
		this.baseGifts = baseGifts;
	}

	@Override
	public int getCount() {
		return baseGifts.size();
	}

	@Override
	public Object getItem(int position) {
		return baseGifts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemlayout;
		if (convertView == null) {
			itemlayout = LayoutInflater.from(context).inflate(R.layout.fragment_top_surface_gift_item, null);
		} else {
			itemlayout = convertView;
		}
		
		BaseGift viewHolder = baseGifts.get(position);
		
		if(viewHolder != null)
		{
			((ImageView) itemlayout.findViewById(R.id.imageView_gift_icon)).setBackgroundResource(viewHolder.getIconResId());
			((TextView) itemlayout.findViewById(R.id.textView_gift_value)).setText(viewHolder.getValue() + "");
			((TextView) itemlayout.findViewById(R.id.textView_gift_experient_points)).setText("+" + viewHolder.getExPoints() + "点经验值");
		}

		return itemlayout;
	}

}
