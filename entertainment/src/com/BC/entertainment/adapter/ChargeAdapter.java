package com.BC.entertainment.adapter;

import java.util.List;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Yubi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChargeAdapter extends BaseAdapter {

	private Context mContext;
	
	private List<Yubi> mData;
	
	private LayoutInflater mInflater;

	private ViewHolder viewHolder;
	
	public ChargeAdapter(Context context, List<Yubi> mData)
	{
		this.mContext = context;
		this.mData = mData;
		this.mInflater = LayoutInflater.from(mContext);
	}
	
	@Override
	public int getCount() {
		int count = 0;
		
		if(mData != null)
		{
			count = mData.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
		{
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.activity_charge_item, null);

            viewHolder.txtPrice = (TextView) convertView.findViewById(R.id.textViewMoney);
            viewHolder.txtYubi = (TextView) convertView.findViewById(R.id.textViewYubi);

            convertView.setTag(mData.get(position));
        }
		Yubi yubi = mData.get(position);
		
		if (yubi != null)
		{
			viewHolder.txtPrice.setText(yubi.getPrice() + "元");
			viewHolder.txtYubi.setText(yubi.getAmount() + "娱币");
		}
		return convertView;
	}

	private static class ViewHolder
	{
	    TextView txtPrice;
	    TextView txtYubi;
	}
}
