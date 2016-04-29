package com.BC.entertainment.adapter;

import java.util.List;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Yubi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ChargeRecycleAdapter extends RecyclerView.Adapter<ListViewHolder>{
	
    private LayoutInflater mInflater;
    private Context mContext;
    protected List<Yubi> mDatas;
    
	public ChargeRecycleAdapter(Context context, List<Yubi> mData)
	{
		this.mContext = context;
		this.mDatas = mData;
		this.mInflater = LayoutInflater.from(mContext);
	}
	
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
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
	public ListViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
        View view = mInflater.inflate(R.layout.activity_charge_item, viewGroup, false);
        ListViewHolder myViewHolder = new ListViewHolder(view);
        return myViewHolder;
	}

    /*
	    绑定ViewHolder的数据
	*/
	@Override
	public void onBindViewHolder(ListViewHolder viewGroup, int i) {

		Yubi yubi = mDatas.get(i);
		if (yubi != null)
		{
			viewGroup.txtPrice.setText(yubi.getPrice() + "元");
			viewGroup.txtYubi.setText(yubi.getAmount() + "娱币");
			viewGroup.itemView.setTag(yubi);
		}
		
		
		setUpitemEvent(viewGroup);

	}
	
    protected void setUpitemEvent(final ListViewHolder myViewHolder) {

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
}

class ListViewHolder extends ViewHolder {

    TextView txtPrice;
    TextView txtYubi;

    public ListViewHolder(View v) {
        super(v);
        txtPrice = (TextView) v.findViewById(R.id.textViewMoney);
        txtYubi = (TextView) v.findViewById(R.id.textViewYubi);
    }
}
