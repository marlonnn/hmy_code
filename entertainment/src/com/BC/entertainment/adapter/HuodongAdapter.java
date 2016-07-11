package com.BC.entertainment.adapter;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Huodong;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class HuodongAdapter extends RecyclerView.Adapter<HuodongViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    protected List<Huodong> mDatas;
    
	public HuodongAdapter(Context context, List<Huodong> mData)
	{
		this.mContext = context;
		this.mDatas = mData;
		this.mInflater = LayoutInflater.from(mContext);
	}
	
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    
    private OnItemClickListener mOnItemClickListener;
    
	public OnItemClickListener getmOnItemClickListener() {
		return mOnItemClickListener;
	}

	public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
		this.mOnItemClickListener = mOnItemClickListener;
	}

	@Override
	public int getItemCount() {
		return mDatas.size();
	}

	    /*
	    创建ViewHolder
	*/
	@Override
	public HuodongViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
	    View view = mInflater.inflate(R.layout.activity_huodong_item, viewGroup, false);
	    HuodongViewHolder myViewHolder = new HuodongViewHolder(view);
	    return myViewHolder;
	}

	/*
	    绑定ViewHolder的数据
	*/
	@Override
	public void onBindViewHolder(HuodongViewHolder viewGroup, int i) {
	
		try {
			Huodong huodong = mDatas.get(i);
			if (huodong != null)
			{
				viewGroup.title.setText(huodong.getName());
				viewGroup.time.setText(huodong.getAdd_time());
				Glide.with(mContext)
				.load(InfoCache.getInstance().getPersonalInfo().getHead_portrait())
				.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
				.placeholder(R.drawable.avatar_def).into(viewGroup.logo);
				viewGroup.itemView.setTag(huodong);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setUpitemEvent(viewGroup);
	
	}

	protected void setUpitemEvent(final HuodongViewHolder myViewHolder) {
	
	    if (mOnItemClickListener != null) {
	        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                int layoutPosition = myViewHolder.getLayoutPosition();
	                mOnItemClickListener.onItemClick(myViewHolder.itemView, layoutPosition);
	            }
	        });
	
	    }
	}
}

class HuodongViewHolder extends ViewHolder {

    TextView title;
    TextView time;
    ImageView logo;
    public HuodongViewHolder(View v) {
        super(v);
        title = (TextView) v.findViewById(R.id.txtTitle);
        time = (TextView) v.findViewById(R.id.txtTime);
        logo = (ImageView) v.findViewById(R.id.imgLogo);
    }
}
