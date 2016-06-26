package com.BC.entertainmentgravitation.dialog;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;

import com.BC.entertainment.adapter.BaseInfoRegionAdapter;
import com.BC.entertainmentgravitation.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 权益卡名称
 * @author wen zhong
 *
 */
public class RightsNameDialog extends Dialog {

	public RightsNameDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public RightsNameDialog(Context context, int theme) {
		super(context, theme);
	}

	public RightsNameDialog(Context context) {
		super(context);
	}
	
	public static class Builder {
		
		private Context context;
		private LinearLayout lLayoutWheel;
		private WheelView wheel;
		private View layout;
		private String title;
		/**
		 * 所有权益卡名称
		 */
		private String[] mDatas = new String[] {"戏约卡", "演出卡", "商务卡"};
		
		
		/**
		 * 当名称
		 */
		private String mCurrentName = mDatas[0];
		
		private OnWheelChangedListener wheelChangedListener;
		private DialogInterface.OnClickListener positiveButtonClickListener;
		private DialogInterface.OnClickListener negativeButtonClickListener;

		
		public Builder(Context context) {
			this.context = context;
			wheelChangedListener = new Listener();
		}
		
		/**
		 * Set the Dialog title 
		 * 
		 * @param title
		 * @return
		 */
		public void setTitle(String title) {
			this.title = title;
		}

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @return
		 */
		public void setPositiveButton(DialogInterface.OnClickListener listener) {
			this.positiveButtonClickListener = listener;
		}

		public void setNegativeButton(DialogInterface.OnClickListener listener) {
			this.negativeButtonClickListener = listener;
		}
		
		public void setWheelChangedListener(OnWheelChangedListener wheelChangedListener) {
			this.wheelChangedListener = wheelChangedListener;
		}

		public View findViewById(int id) {
			if (layout == null) {
				return null;
			}
			return layout.findViewById(id);
		}
		
		/**
		 * @param type
		 * @param message
		 * @return
		 */
		@SuppressLint("InflateParams")
		public RightsNameDialog Create()
		{
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final RightsNameDialog dialog = new RightsNameDialog(context,
					R.style.Dialog);
			layout = inflater.inflate(R.layout.dialog_wheel_right, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			((TextView) layout.findViewById(R.id.txtViewTitle)).setText(title);
			lLayoutWheel = (LinearLayout) layout.findViewById(R.id.lLayoutWheel);
			wheel = (WheelView) layout.findViewById(R.id.wheelView);
			lLayoutWheel.setVisibility(View.VISIBLE);
			if (mDatas != null && mDatas.length > 0)
			{
				wheel.setViewAdapter(new BaseInfoRegionAdapter(context, mDatas));
				wheel.setVisibleItems(5);
				wheel.setDrawShadows(false);
				if (wheelChangedListener != null)
				{
					wheel.addChangingListener(wheelChangedListener);
				}
			}
			
			if (positiveButtonClickListener != null) {
				((Button) layout.findViewById(R.id.positiveButton))
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								positiveButtonClickListener.onClick(dialog,
										DialogInterface.BUTTON_POSITIVE);
							}
						});
			}
			else
			{
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			
			if (negativeButtonClickListener != null)
			{
				((Button) layout.findViewById(R.id.negativeButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						negativeButtonClickListener.onClick(dialog,
								DialogInterface.BUTTON_NEGATIVE);
					}
				});
			}
			return dialog;
		}
		
		private  class Listener implements  OnWheelChangedListener{

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				switch(wheel.getId())
				{
				case R.id.wheelView:
					updateDatas();
					break;
				}
			}
			
		}
		  
		/**
		 * 根据当前的省，更新市WheelView的信息
		 */
		private void updateDatas()
		{
			int pCurrent = wheel.getCurrentItem();
			mCurrentName = mDatas[pCurrent];
		}

		public String getmCurrentName() {
			return mCurrentName;
		}

		public void setmCurrentName(String mCurrentName) {
			this.mCurrentName = mCurrentName;
		}

	}
}
