package com.BC.entertainment.adapter;

import java.util.LinkedList;

import com.BC.entertainmentgravitation.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.summer.view.CircularImage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder>{
	
    private LayoutInflater mInflater;
    private Context mContext;
    protected LinkedList<ChatRoomMember> mDatas;
	
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }
    
    private OnItemClickListener mOnItemClickListener;
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    
    //构造方法
    public RecyclerViewAdapter(Context context, LinkedList<ChatRoomMember> datas) {
        //成员变量进行赋值
        this.mContext = context;
        this.mDatas = datas;
        mInflater = LayoutInflater.from(context);
    }
    
    /*
            创建ViewHolder
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = mInflater.inflate(R.layout.fragment_top_surface_portrait_item, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }
    
    /*
            绑定ViewHolder的数据
     */
    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int pos) {
		Glide.with(mContext)
		.load(mDatas.get(pos).getAvatar())
		.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
		.placeholder(R.drawable.avatar_def).into(myViewHolder.circularImage);

        setUpitemEvent(myViewHolder);
    }
    
	@Override
	public int getItemCount() {
		return mDatas.size();
	}
	
    protected void setUpitemEvent(final MyViewHolder myViewHolder) {

        if (mOnItemClickListener != null) {
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int layoutPosition = myViewHolder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(myViewHolder.itemView, layoutPosition);
                }
            });

            //long click
            myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int layoutPosition = myViewHolder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(myViewHolder.itemView, layoutPosition);
                    return false;
                }
            });
        }
    }
    
    public void UpdateData()
    {
    	this.notifyDataSetChanged();
    }

}
class MyViewHolder extends ViewHolder {

	CircularImage circularImage;

    public MyViewHolder(View v) {
        super(v);
        circularImage = (CircularImage) v.findViewById(R.id.imageViewPortrait);
    }
}