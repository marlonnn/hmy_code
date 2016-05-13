package com.BC.entertainment.adapter;

import java.util.LinkedList;

import com.BC.entertainment.adapter.RecyclerViewAdapter.OnItemClickListener;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Member;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.summer.view.CircularImage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 明星列表 热门
 * @author wen zhong
 *
 */
public class HotRecycleAdapter extends RecyclerView.Adapter<HotViewHolder>{

    private LayoutInflater mInflater;
    private Context mContext;
    
    protected LinkedList<Member> mDatas;
	
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }
    
    private OnItemClickListener mOnItemClickListener;
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    
    //构造方法
    public HotRecycleAdapter(Context context, LinkedList<Member> datas) {
        //成员变量进行赋值
        this.mContext = context;
        this.mDatas = datas;
        mInflater = LayoutInflater.from(context);
    }
    
    /*
            创建ViewHolder
     */
    @Override
    public HotViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = mInflater.inflate(R.layout.fragment_hot_item, viewGroup, false);
        HotViewHolder myViewHolder = new HotViewHolder(view);
        return myViewHolder;
    }
    
    /*
            绑定ViewHolder的数据
     */
    @Override
    public void onBindViewHolder(final HotViewHolder myViewHolder, final int pos) {
		Glide.with(mContext)
		.load(mDatas.get(pos).getPortrait())
		.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
		.placeholder(R.drawable.avatar_def).into(myViewHolder.cPortrait);

        setUpitemEvent(myViewHolder);
    }
    
	@Override
	public int getItemCount() {
		return mDatas.size();
	}
	
    protected void setUpitemEvent(final HotViewHolder myViewHolder) {

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
class HotViewHolder extends ViewHolder{
	
	CircularImage cPortrait;
	TextView Name;
	TextView Location;
	TextView People;
	ImageView imgPortrait;
	TextView Status;

    public HotViewHolder(View v) {
        super(v);
        cPortrait = (CircularImage) v.findViewById(R.id.cImagePortrait);
        Name = (TextView) v.findViewById(R.id.txtViewName);
        Location = (TextView) v.findViewById(R.id.txtViewLocation);
        People = (TextView) v.findViewById(R.id.txtViewPeople);
        Status = (TextView) v.findViewById(R.id.txtViewStatus);
        imgPortrait = (ImageView) v.findViewById(R.id.imgViewPortrait);
    }
	
}