package com.BC.entertainment.adapter;

import java.text.SimpleDateFormat;
import java.util.List;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Contact;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.summer.view.CircularImage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PrivateRecycleAdapter extends RecyclerView.Adapter<PrivateViewHolder>{

    private LayoutInflater mInflater;
    private Context mContext;
    protected List<Contact> mDatas;
    
	public PrivateRecycleAdapter(Context context, List<Contact> mData)
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
	public PrivateViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
	    View view = mInflater.inflate(R.layout.activity_private_item, viewGroup, false);
	    PrivateViewHolder myViewHolder = new PrivateViewHolder(view);
	    return myViewHolder;
	}

	/*
	    绑定ViewHolder的数据
	*/
	@Override
	public void onBindViewHolder(PrivateViewHolder viewGroup, int i) {
	
		try {
			Contact contact = mDatas.get(i);
			if (contact != null)
			{
				viewGroup.name.setText(contact.getNick());
				viewGroup.content.setText(contact.getContent());
				viewGroup.time.setText(getStrTime(contact.getTime()));
				Glide.with(mContext)
				.load(contact.getAvator())
				.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
				.placeholder(R.drawable.avatar_def).into(viewGroup.avator);
//				if (geTui.isHasRead())
//				{
//					viewGroup.title.setTextColor(Color.parseColor("#ff999999"));
//					viewGroup.content.setTextColor(Color.parseColor("#ff999999"));
//				}
//				else
//				{
//					viewGroup.title.setTextColor(Color.parseColor("#FFFFFF"));
//					viewGroup.content.setTextColor(Color.parseColor("#FFFFFF"));
//				}
				viewGroup.itemView.setTag(contact);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		setUpitemEvent(viewGroup);
	}
	
	private String getStrTime(long cc_time) { 
		 String re_StrTime = null; 
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		 Long lcc_time = Long.valueOf(cc_time); 
		 re_StrTime = sdf.format(lcc_time);  
		 return re_StrTime; 
	}

	protected void setUpitemEvent(final PrivateViewHolder myViewHolder) {
	
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

class PrivateViewHolder extends ViewHolder {

    TextView name;
    TextView content;
    TextView time;
    CircularImage avator;
    
    public PrivateViewHolder(View v) {
        super(v);
        name = (TextView) v.findViewById(R.id.txtName);
        content = (TextView) v.findViewById(R.id.txtContent);
        time = (TextView) v.findViewById(R.id.txtTime);
        avator = (CircularImage) v.findViewById(R.id.imgLogo);
    }
}
