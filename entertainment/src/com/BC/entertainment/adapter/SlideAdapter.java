package com.BC.entertainment.adapter;

import java.util.List;

import com.BC.entertainment.inter.SlideCallback;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.FHNEntity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.summer.slidelistview.SlideBaseAdapter;
import com.summer.view.CircularImage;

public class SlideAdapter extends SlideBaseAdapter {
	
	private List<FHNEntity> mData;
	private SlideCallback slideCallback;
	
	public SlideAdapter(Context context, List<FHNEntity> mData, SlideCallback slideCallback) {
		super(context);
		this.mContext = context;
		this.mData = mData;
		this.slideCallback = slideCallback;
	}

	@Override
	public int getCount() {
		if (mData != null && mData.size() > 0)
		{
			return mData.size();
		}
		else
		{
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = createConvertView(position);
			holder = new ViewHolder();
			holder.lLayout = (LinearLayout) convertView.findViewById(R.id.lLayout);
			holder.portrait = (CircularImage) convertView.findViewById(R.id.cImagePortrait);
			holder.txtViewName = (TextView) convertView.findViewById(R.id.txtViewName);
			holder.txtViewMood = (TextView) convertView.findViewById(R.id.txtViewMood);
			holder.btnCancelFocus = (Button) convertView.findViewById(R.id.btnCancelFocus);
			holder.btnCacelListen = (Button) convertView.findViewById(R.id.btnCacelListen);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Glide.with(mContext).load(mData.get(position).getHead_portrait())
		    .centerCrop()
		    .diskCacheStrategy(DiskCacheStrategy.ALL)
		    .placeholder(R.drawable.home_image).into(holder.portrait);
		holder.txtViewName.setText(isNullOrEmpty(mData.get(position).getStar_names()) ? "" : mData.get(position).getStar_names());
		if (holder.lLayout != null)
		{
			holder.lLayout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (slideCallback != null)
					{
						slideCallback.itemClick(mData.get(position));
					}
				}
			});
		}
		if (holder.btnCancelFocus != null) {
			//取消关注
			holder.btnCancelFocus.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mData.remove(position);
					notifyDataSetChanged();
					if (slideCallback != null)
					{
						slideCallback.unFocus(mData.get(position).getStar_ID());
					}
				}
			});
		}

		if (holder.btnCacelListen != null) {
			//取消收听
			holder.btnCacelListen.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (slideCallback != null)
					{
						slideCallback.unListen(mData.get(position).getStar_ID());
					}
				}
			});
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

	@Override
	public int getFrontViewId(int position) {
		return R.layout.activity_focus_item;
	}

	@Override
	public int getLeftBackViewId(int position) {
		return 0;
	}

	@Override
	public int getRightBackViewId(int position) {
		return R.layout.activity_focus_right_back_view;
	}
	
	class ViewHolder {
		LinearLayout lLayout;
		CircularImage  portrait;
		TextView txtViewName;
		TextView txtViewMood;
		/**
		 * 取消关注
		 */
		Button btnCancelFocus;
		/**
		 * 取消收听
		 */
		Button btnCacelListen;
	}
}
