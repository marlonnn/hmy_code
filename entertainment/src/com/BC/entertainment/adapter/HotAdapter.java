package com.BC.entertainment.adapter;

import java.util.List;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.FHNEntity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.summer.view.CircularImage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HotAdapter extends BaseAdapter{
	
	protected Context mContext;
	protected List<FHNEntity> mData;
	private LayoutInflater mInflater;
	private ViewHolder viewHolder;
	
	public HotAdapter(Context mContext, List<FHNEntity> mData)
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
            convertView = mInflater.inflate(R.layout.fragment_hot_item, null);
            
            viewHolder.cPortrait = (CircularImage) convertView.findViewById(R.id.cImagePortrait);
            viewHolder.Name = (TextView) convertView.findViewById(R.id.txtViewName);
            viewHolder.Location = (TextView) convertView.findViewById(R.id.txtViewLocation);
            viewHolder.People = (TextView) convertView.findViewById(R.id.txtViewPeople);
            viewHolder.Status = (ImageView) convertView.findViewById(R.id.imgViewStatus);
            viewHolder.imgPortrait = (ImageView) convertView.findViewById(R.id.imgViewPortrait);
			if (mData.get(position) != null)
			{
				viewHolder.Name.setText(mData.get(position).getStar_names());
				viewHolder.Location.setText(mData.get(position).getRegion());
				if (mData.get(position).getPeoples() != null && !mData.get(position).getPeoples().isEmpty())
				{
					viewHolder.People.setText(mData.get(position).getPeoples());
				}
				if (mData.get(position).getVstatus() != null && !mData.get(position).getVstatus().isEmpty() && mData.get(position).getVstatus().contains("0"))
				{
					viewHolder.Status.setVisibility(View.VISIBLE);
				}
				else
				{
					viewHolder.Status.setVisibility(View.GONE);
				}
				Glide.with(mContext).load(mData.get(position).getPortrait())
				.centerCrop()
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.placeholder(R.drawable.home_image).into(viewHolder.imgPortrait);
				
				viewHolder.imgPortrait.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
//						try {
//							FHNEntity entity = (FHNEntity)v.getTag();
//							if (entity != null)
//							{
//								Intent i = new Intent(getActivity(), DetailsActivity.class);
//								i.putExtra("userID", entity.getStar_ID());
//								startActivity(i);
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						Intent i = new Intent(getActivity(), DetailsActivity.class);
//						i.putExtra("userID", item.getStar_ID());
//						startActivity(i);
					}
				});
				Glide.with(mContext).load(mData.get(position).getHead_portrait())
				.centerCrop()
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.placeholder(R.drawable.avatar_def).into(viewHolder.cPortrait);
				
				viewHolder.cPortrait.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
					}
				});
			}
            convertView.setTag(mData.get(position));
        }

		return convertView;
	}
	
	public void add(List<FHNEntity> list) {
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
		TextView Location;
		TextView People;
		ImageView Status;
		ImageView imgPortrait;
	}
}
