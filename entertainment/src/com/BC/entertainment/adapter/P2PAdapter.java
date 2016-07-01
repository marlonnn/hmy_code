package com.BC.entertainment.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.BC.entertainmentgravitation.R;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.config.Config;
import com.summer.logger.XLog;

public class P2PAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<IMMessage> mData;
	private LayoutInflater mInflater;
	private P2PViewHolder pushViewHolder;
	
	public P2PAdapter(Context context, List<IMMessage> mData) {
		this.mContext = context;
		this.mData = mData;
		this.mInflater = LayoutInflater.from(mContext);
	}
	
	@Override
	public int getCount() {
		if (mData != null) {
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

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
		{
			pushViewHolder = new P2PViewHolder();
            convertView = mInflater.inflate(R.layout.fragment_message_item, null);
			pushViewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
			pushViewHolder.txtContent = (TextView) convertView.findViewById(R.id.txtContent);
			convertView.setTag(pushViewHolder);
		}
		else
		{
			pushViewHolder = (P2PViewHolder)convertView.getTag();
		}
		IMMessage item = (IMMessage) getItem(position);
		pushViewHolder.txtName.setTextColor(Color.parseColor("#EEB422"));
		if (item != null)
		{
			if (item.getMsgType() == MsgTypeEnum.text)
			{
				handlerTextMessage(pushViewHolder, item);
			}
		}
		return convertView;
	}
	
    /**
     * 处理文本消息
     * @param holder
     * @param message
     */
    private void handlerTextMessage(P2PViewHolder holder, IMMessage message)
    {
    	try {
			if (message != null)
			{
				pushViewHolder.txtContent.setTextColor( Color.parseColor("#FFFFFF"));
				if (message.getDirect() == MsgDirectionEnum.Out)
				{
					//发出去的消息
					pushViewHolder.txtName.setText(isNullOrEmpty(Config.User.getNickName()) ? "未知用户: " : Config.User.getNickName() + ": ");
					pushViewHolder.txtContent.setText(isNullOrEmpty(message.getContent()) ? "" : message.getContent());
				}
				else if (message.getDirect() == MsgDirectionEnum.In)
				{
					//接受到的消息
					pushViewHolder.txtName.setText(isNullOrEmpty(message.getFromNick()) ? "未知用户: " : message.getFromNick() + ": ");
					pushViewHolder.txtContent.setText(isNullOrEmpty(message.getContent()) ? "" : message.getContent());
				}
			}
		} catch (Exception e) {
			XLog.e(e.getMessage());
			e.printStackTrace();
		}

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
	
	public class P2PViewHolder
	{
		TextView txtName;
		TextView txtContent;
	}
}
