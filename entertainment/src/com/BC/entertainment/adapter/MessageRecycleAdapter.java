package com.BC.entertainment.adapter;

import java.util.List;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.GeTui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MessageRecycleAdapter extends RecyclerView.Adapter<MessageViewHolder>{

    private LayoutInflater mInflater;
    private Context mContext;
    protected List<GeTui> mDatas;
    
	public MessageRecycleAdapter(Context context, List<GeTui> mData)
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
	public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
	    View view = mInflater.inflate(R.layout.activity_message_item, viewGroup, false);
	    MessageViewHolder myViewHolder = new MessageViewHolder(view);
	    return myViewHolder;
	}

	/*
	    绑定ViewHolder的数据
	*/
	@Override
	public void onBindViewHolder(MessageViewHolder viewGroup, int i) {
	
		try {
			GeTui geTui = mDatas.get(i);
			if (geTui != null)
			{
				viewGroup.title.setText(geTui.getMessagetitle());
				viewGroup.content.setText(geTui.getMessagecontent());
				if (geTui.isHasRead())
				{
					viewGroup.title.setTextColor(Color.parseColor("#ff999999"));
					viewGroup.content.setTextColor(Color.parseColor("#ff999999"));
				}
				else
				{
					viewGroup.title.setTextColor(Color.parseColor("#FFFFFF"));
					viewGroup.content.setTextColor(Color.parseColor("#FFFFFF"));
				}
				viewGroup.itemView.setTag(geTui);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setUpitemEvent(viewGroup);
	
	}

	protected void setUpitemEvent(final MessageViewHolder myViewHolder) {
	
	    if (mOnItemClickListener != null) {
	        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                int layoutPosition = myViewHolder.getLayoutPosition();
	                mOnItemClickListener.onItemClick(myViewHolder.itemView, layoutPosition);
	            }
	        });
//	        
//	        myViewHolder.imageViewIcon.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//	                int layoutPosition = myViewHolder.getLayoutPosition();
//	                mOnItemClickListener.onItemClick(myViewHolder.itemView, layoutPosition);
//				}
//			});
	
	    }
	}
}

class MessageViewHolder extends ViewHolder {

    TextView title;
    TextView content;

    public MessageViewHolder(View v) {
        super(v);
        title = (TextView) v.findViewById(R.id.txtTitle);
        content = (TextView) v.findViewById(R.id.txtContent);
    }
}
