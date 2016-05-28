package com.BC.entertainment.adapter;

import java.util.List;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Contribute;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.summer.view.CircularImage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContributeAdapter extends BaseAdapter{
	
	protected Context mContext;
	protected List<Contribute> mData;
	private LayoutInflater mInflater;
	private ViewHolder viewHolder;
	
	public ContributeAdapter(Context mContext, List<Contribute> mData)
	{
		this.mContext = mContext;
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
            convertView = mInflater.inflate(R.layout.activity_personal_contribution_item, null);
			try {
				viewHolder.cPortrait = (CircularImage) convertView.findViewById(R.id.cImagePortrait);
				viewHolder.Name = (TextView) convertView.findViewById(R.id.txtViewName);
				viewHolder.contribute = (TextView) convertView.findViewById(R.id.txtViewContribute);
				viewHolder.txtViewRange = (TextView) convertView.findViewById(R.id.txtViewRange);
				
				if (mData.get(position) != null)
				{
					viewHolder.txtViewRange.setText(String.valueOf(position + 1));
					viewHolder.Name.setText(isNullOrEmpty(mData.get(position).getNick_name()) ? "未知" : mData.get(position).getNick_name());
					viewHolder.contribute.setText(isNullOrEmpty(mData.get(position).getSumcount()) ? "贡献0娱票" : "贡献" + mData.get(position).getSumcount() + "娱票");
					Glide.with(mContext).load(mData.get(position).getHead())
					.centerCrop()
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.placeholder(R.drawable.home_image).into(viewHolder.cPortrait);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

            convertView.setTag(mData.get(position));
        }

		return convertView;
	}
	
	private boolean isNullOrEmpty(String o)
	{
		if (o != null)
		{
			if (o.length() == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return true;
		}
	}
	
	public void add(List<Contribute> list) {
		if (list == null || list.size() == 0)
			return;
		this.mData.addAll(list);
		notifyDataSetChanged();
	}
	
	public void clearAll() {
		this.mData.clear();
		notifyDataSetChanged();
	}
	
	private static class ViewHolder
	{
		CircularImage cPortrait;
		TextView Name;
		TextView contribute;
		TextView txtViewRange;
	}
}
