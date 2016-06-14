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

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Found;

public class FoundRecycleAdapter extends RecyclerView.Adapter<FoundViewHolder>{
	
    private LayoutInflater mInflater;
    private Context mContext;
    protected List<Found> mDatas;
    
	public FoundRecycleAdapter(Context context, List<Found> mData)
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
	public FoundViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
        View view = mInflater.inflate(R.layout.activity_personal_item, viewGroup, false);
        FoundViewHolder myViewHolder = new FoundViewHolder(view);
        return myViewHolder;
	}

    /*
	    绑定ViewHolder的数据
	*/
	@Override
	public void onBindViewHolder(FoundViewHolder viewGroup, int i) {

		Found personal = mDatas.get(i);
		if (personal != null)
		{
			viewGroup.imageViewIcon.setBackgroundResource(personal.getResource());
			viewGroup.txtName.setText(personal.getInfo());
			viewGroup.itemView.setTag(personal);
		}
		
		
		setUpitemEvent(viewGroup);

	}
	
    protected void setUpitemEvent(final FoundViewHolder myViewHolder) {

        if (mOnItemClickListener != null) {
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int layoutPosition = myViewHolder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(myViewHolder.itemView, layoutPosition);
                }
            });
            
            myViewHolder.imageViewIcon.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
                    int layoutPosition = myViewHolder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(myViewHolder.itemView, layoutPosition);
				}
			});

        }
    }
}

class FoundViewHolder extends ViewHolder {

	ImageView imageViewIcon;
    TextView txtName;

    public FoundViewHolder(View v) {
        super(v);
        imageViewIcon = (ImageView) v.findViewById(R.id.imageViewIcon);
        txtName = (TextView) v.findViewById(R.id.txtInfo);
    }
}
