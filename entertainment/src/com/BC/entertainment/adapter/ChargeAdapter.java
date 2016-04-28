package com.BC.entertainment.adapter;

import java.util.List;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Yubi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChargeAdapter extends BaseAdapter {

	private Context mContext;
	
	private List<Yubi> mData;
	
	private LayoutInflater mInflater;

	private ViewHolder viewHolder;
	
	public ChargeAdapter(Context context, List<Yubi> mData)
	{
		this.mContext = context;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
		{
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.fragment_message_item, null);

            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            viewHolder.txtContent = (TextView) convertView.findViewById(R.id.txtContent);

            convertView.setTag(mData.get(position));
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
		Yubi yubi = mData.get(position);
		
		if (yubi != null)
		{
				
		}
		return null;
	}

	private static class ViewHolder
	{
	    TextView txtName;
	    TextView txtContent;
	}
}
