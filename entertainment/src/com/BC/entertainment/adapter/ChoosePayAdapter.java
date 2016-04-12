package com.BC.entertainment.adapter;

import java.util.List;

import com.BC.entertainmentgravitation.entity.PayWays;
import com.BC.entertainmentgravitation.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChoosePayAdapter extends BaseAdapter{
	
	private List<PayWays> list;
	private LayoutInflater inflater;
	
	public ChoosePayAdapter(Context context, List<PayWays> list)
	{
		this.inflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" }) @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.dialog_choose_pay_item, null);
		ImageView imagePay = (ImageView)convertView.findViewById(R.id.image_pay);
		TextView textPay = (TextView)convertView.findViewById(R.id.text_pay);
		imagePay.setBackgroundResource(list.get(position).getImageResource());
		textPay.setText(list.get(position).getName());
		convertView.setTag(list.get(position));
		return convertView;
	}

}
