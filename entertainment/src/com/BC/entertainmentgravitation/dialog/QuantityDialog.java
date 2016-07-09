package com.BC.entertainmentgravitation.dialog;

import com.BC.entertainment.adapter.BaseInfoRegionAdapter;
import com.BC.entertainmentgravitation.R;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
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
 * 数量选择对话框
 * @author wen zhong
 *
 */
public class QuantityDialog extends Dialog {

	public QuantityDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public QuantityDialog(Context context, int theme) {
		super(context, theme);
	}

	public QuantityDialog(Context context) {
		super(context);
	}
	
	public static class Builder {
		
		private Context context;
		private View layout;
		private String title;
		private LinearLayout lLayoutBai;
		private WheelView wheelBai;
		private LinearLayout lLayoutShi;
		private WheelView wheelShi;
		private LinearLayout lLayoutGe;
		private WheelView wheelGe;
		
		private String[] mDatas;
		
		private OnWheelChangedListener wheelChangedListener;
		private DialogInterface.OnClickListener positiveButtonClickListener;
		private DialogInterface.OnClickListener negativeButtonClickListener;
		
		private int quantity;
		
		public Builder(Context context)
		{
			this.context = context;
			mDatas = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
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
		
		public View findViewById(int id) {
			if (layout == null) {
				return null;
			}
			return layout.findViewById(id);
		}
		
		public int GetQuantity()
		{
			return quantity;
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
		
		@SuppressLint("InflateParams")
		public QuantityDialog Create()
		{
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			final QuantityDialog dialog = new QuantityDialog(context,
					R.style.Dialog);
			layout = inflater.inflate(R.layout.dialog_wheel_quantity, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			((TextView) layout.findViewById(R.id.txtViewTitle)).setText(title);
			lLayoutBai = (LinearLayout) layout.findViewById(R.id.rLayoutBai);
			wheelBai = (WheelView) layout.findViewById(R.id.wheelBai);
			lLayoutShi = (LinearLayout) layout.findViewById(R.id.rLayoutShi);
			wheelShi = (WheelView) layout.findViewById(R.id.wheelShi);
			lLayoutGe = (LinearLayout) layout.findViewById(R.id.rLayoutGe);
			wheelGe = (WheelView) layout.findViewById(R.id.wheelGe);
			
			wheelBai.setViewAdapter(new BaseInfoRegionAdapter(context, mDatas));
			wheelBai.setVisibleItems(5);
			wheelBai.setDrawShadows(false);
			wheelShi.setViewAdapter(new BaseInfoRegionAdapter(context, mDatas));
			wheelShi.setVisibleItems(5);
			wheelShi.setDrawShadows(false);
			wheelGe.setViewAdapter(new BaseInfoRegionAdapter(context, mDatas));
			wheelGe.setVisibleItems(5);
			wheelGe.setDrawShadows(false);
			
			if (wheelChangedListener != null)
			{
				wheelBai.addChangingListener(wheelChangedListener);
				wheelShi.addChangingListener(wheelChangedListener);
				wheelGe.addChangingListener(wheelChangedListener);
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

			private int index = 0;
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				switch(wheel.getId())
				{
				case R.id.wheelBai:
					try {
						index = wheelBai.getCurrentItem();
						String bai = mDatas[index];
						quantity = quantity + Integer.parseInt(bai) * 100;
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					break;
				case R.id.wheelShi:
					try {
						index = wheelShi.getCurrentItem();
						String shi = mDatas[index];
						quantity = quantity + Integer.parseInt(shi) * 10;
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					break;
				case R.id.wheelGe:
					try {
						index = wheelGe.getCurrentItem();
						String ge = mDatas[index];
						quantity = quantity + Integer.parseInt(ge);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					break;
				}
			}
			
		}

	}
}
