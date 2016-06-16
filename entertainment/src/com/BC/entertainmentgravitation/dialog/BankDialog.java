package com.BC.entertainmentgravitation.dialog;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;

import com.BC.entertainment.adapter.RegionAdapter;
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
 * 
 * @author wen zhong
 *
 */
public class BankDialog extends Dialog {

	public BankDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public BankDialog(Context context, int theme) {
		super(context, theme);
	}

	public BankDialog(Context context) {
		super(context);
	}
	
	public static class Builder {
		
		private Context context;
		private LinearLayout lLayoutWheel;
		private WheelView province;
		private WheelView city;
		private View layout;
		private String title;
		private TextView txtViewMessage;
		
		private RegionAdapter adapter;
		
		private OnWheelChangedListener wheelChangedListener;
		private DialogInterface.OnClickListener positiveButtonClickListener;
		private DialogInterface.OnClickListener negativeButtonClickListener;
		
		public Builder(Context context, RegionAdapter adapter) {
			this.context = context;
			this.adapter = adapter;
		}
		
		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */

		public void setTitle(String title) {
			this.title = title;
		}
		
		public WheelView getProvince() {
			return province;
		}

		public void setProvince(WheelView province) {
			this.province = province;
		}

		public WheelView getCity() {
			return city;
		}

		public void setCity(WheelView city) {
			this.city = city;
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
		
		@SuppressLint("InflateParams")
		public BankDialog Create(int type, String message)
		{
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final BankDialog dialog = new BankDialog(context,
					R.style.Dialog);
			layout = inflater.inflate(R.layout.dialog_wheel_bank, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			((TextView) layout.findViewById(R.id.txtViewTitle)).setText(title);
			txtViewMessage = ((TextView) layout.findViewById(R.id.txtViewMessage));
			lLayoutWheel = (LinearLayout) layout.findViewById(R.id.lLayoutWheel);
			province = (WheelView) layout.findViewById(R.id.wheelViewPovince);
			
			city = (WheelView) layout.findViewById(R.id.wheelViewCity);
			switch (type)
			{
			case -1:
				lLayoutWheel.setVisibility(View.GONE);
				txtViewMessage.setVisibility(View.VISIBLE);
				if (message != null)
				{
					txtViewMessage.setText(message);
				}
				break;
			/**
			 * 省
			 */
			case 1:
				lLayoutWheel.setVisibility(View.VISIBLE);
				txtViewMessage.setVisibility(View.GONE);
				if (adapter != null)
				{
					province.setViewAdapter(adapter);
					province.setVisibleItems(5);
					province.setDrawShadows(false);
					if (wheelChangedListener != null)
					{
						province.addChangingListener(wheelChangedListener);
					}
				}

				break;
			/**
			 * 市
			 */
			case 2:
				lLayoutWheel.setVisibility(View.VISIBLE);
				txtViewMessage.setVisibility(View.GONE);
				if (adapter != null)
				{
					city.setViewAdapter(adapter);
					city.setVisibleItems(5);
					city.setDrawShadows(false);
					if (wheelChangedListener != null)
					{
						city.addChangingListener(wheelChangedListener);
					}
				}

				break;
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
			else
			{
				layout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
			}
			
			return dialog;
		}
	}
	
}
