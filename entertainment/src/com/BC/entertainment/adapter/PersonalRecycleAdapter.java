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
import com.BC.entertainmentgravitation.entity.Personal;

public class PersonalRecycleAdapter extends RecyclerView.Adapter<PersonalViewHolder>{
	
    private LayoutInflater mInflater;
    private Context mContext;
    protected List<Personal> mDatas;
    
	public PersonalRecycleAdapter(Context context, List<Personal> mData)
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
	public PersonalViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
        View view = mInflater.inflate(R.layout.activity_personal_item, viewGroup, false);
        PersonalViewHolder myViewHolder = new PersonalViewHolder(view);
        return myViewHolder;
	}

    /*
	    绑定ViewHolder的数据
	*/
	@Override
	public void onBindViewHolder(PersonalViewHolder viewGroup, int i) {

		Personal personal = mDatas.get(i);
		if (personal != null)
		{
			viewGroup.imageViewIcon.setBackgroundResource(personal.getResource());
			viewGroup.txtName.setText(personal.getInfo());
			viewGroup.itemView.setTag(personal);
		}
		
		
		setUpitemEvent(viewGroup);

	}
	
    protected void setUpitemEvent(final PersonalViewHolder myViewHolder) {

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

class PersonalViewHolder extends ViewHolder {

	ImageView imageViewIcon;
    TextView txtName;

    public PersonalViewHolder(View v) {
        super(v);
        imageViewIcon = (ImageView) v.findViewById(R.id.imageViewIcon);
        txtName = (TextView) v.findViewById(R.id.txtInfo);
    }
}
