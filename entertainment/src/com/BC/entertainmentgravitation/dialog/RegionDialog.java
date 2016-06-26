package com.BC.entertainmentgravitation.dialog;

import java.util.HashMap;
import java.util.Map;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;

import com.BC.entertainment.adapter.BaseInfoRegionAdapter;
import com.BC.entertainment.cache.AuthenCache;
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
 * 地区选择
 * @author wen zhong
 *
 */
public class RegionDialog extends Dialog {

	public RegionDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public RegionDialog(Context context, int theme) {
		super(context, theme);
	}

	public RegionDialog(Context context) {
		super(context);
	}
	
	public static class Builder {
		
		private Context context;
		private LinearLayout lLayoutWheel;
		private WheelView province;
		private WheelView city;
		private WheelView area;
		private View layout;
		private String title;
		/**
		 * 所有省
		 */
		private String[] mProvinceDatas;
		
		/**
		 * key - 省 value - 市s
		 */
		private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
		/**
		 * key - 市 values - 区s
		 */
		private Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();
		
		/**
		 * 当前省的名称
		 */
		private String mCurrentProviceName = "";
		/**
		 * 当前市的名称
		 */
		private String mCurrentCityName = "";
		/**
		 * 当前区的名称
		 */
		private String mCurrentAreaName ="";
		
		private OnWheelChangedListener wheelChangedListener;
		private DialogInterface.OnClickListener positiveButtonClickListener;
		private DialogInterface.OnClickListener negativeButtonClickListener;

		
		public Builder(Context context, AuthenCache authenCache) {
			this.context = context;
			wheelChangedListener = new Listener();
			
			mProvinceDatas = authenCache.mProvinceDatas;
			mCitisDatasMap = authenCache.mCitisDatasMap;
			mAreaDatasMap = authenCache.mAreaDatasMap;
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
		
		/**
		 * @param type
		 * @param message
		 * @return
		 */
		@SuppressLint("InflateParams")
		public RegionDialog Create()
		{
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final RegionDialog dialog = new RegionDialog(context,
					R.style.Dialog);
			layout = inflater.inflate(R.layout.dialog_wheel_region, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			((TextView) layout.findViewById(R.id.txtViewTitle)).setText(title);
			lLayoutWheel = (LinearLayout) layout.findViewById(R.id.lLayoutWheel);
			province = (WheelView) layout.findViewById(R.id.wheelViewPovince);
			city = (WheelView) layout.findViewById(R.id.wheelViewCity);
			area = (WheelView) layout.findViewById(R.id.wheelViewArea);
			lLayoutWheel.setVisibility(View.VISIBLE);
			if (mProvinceDatas != null && mProvinceDatas.length > 0)
			{
				province.setViewAdapter(new BaseInfoRegionAdapter(context, mProvinceDatas));
				province.setVisibleItems(5);
				province.setDrawShadows(false);
				if (wheelChangedListener != null)
				{
					province.addChangingListener(wheelChangedListener);
					city.addChangingListener(wheelChangedListener);
					area.addChangingListener(wheelChangedListener);
				}
				city.setVisibleItems(5);
				area.setVisibleItems(5);
				city.setDrawShadows(false);
				area.setDrawShadows(false);
				updateCities();
				updateAreas();
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
				case R.id.wheelViewPovince:
					updateCities();
					break;
				case R.id.wheelViewCity:
					updateAreas();
					break;
				case R.id.wheelViewArea:
					mCurrentAreaName = mAreaDatasMap.get(mCurrentCityName)[newValue];
					break;
				}
			}
			
		}
		  
		/**
		 * 根据当前的省，更新市WheelView的信息
		 */
		private void updateCities()
		{
			int pCurrent = province.getCurrentItem();
			mCurrentProviceName = mProvinceDatas[pCurrent];
			String[] cities = mCitisDatasMap.get(mCurrentProviceName);
			if (cities == null)
			{
				cities = new String[] { "" };
			}
			city.setViewAdapter(new BaseInfoRegionAdapter(context, cities));
			city.setCurrentItem(0);
			updateAreas();
		}
		
		/**
		 * 根据当前的市，更新区WheelView的信息
		 */
		private void updateAreas()
		{
			int pCurrent = city.getCurrentItem();
			mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
			String[] areas = mAreaDatasMap.get(mCurrentCityName);

			if (areas == null)
			{
				areas = new String[] { "" };
			}
			area.setViewAdapter(new BaseInfoRegionAdapter(context, areas));
			area.setCurrentItem(0);
			mCurrentAreaName = mAreaDatasMap.get(mCurrentCityName)[0];
		}

		public String[] getmProvinceDatas() {
			return mProvinceDatas;
		}

		public void setmProvinceDatas(String[] mProvinceDatas) {
			this.mProvinceDatas = mProvinceDatas;
		}

		public Map<String, String[]> getmCitisDatasMap() {
			return mCitisDatasMap;
		}

		public void setmCitisDatasMap(Map<String, String[]> mCitisDatasMap) {
			this.mCitisDatasMap = mCitisDatasMap;
		}

		public Map<String, String[]> getmAreaDatasMap() {
			return mAreaDatasMap;
		}

		public void setmAreaDatasMap(Map<String, String[]> mAreaDatasMap) {
			this.mAreaDatasMap = mAreaDatasMap;
		}

		public String getmCurrentProviceName() {
			return mCurrentProviceName;
		}

		public void setmCurrentProviceName(String mCurrentProviceName) {
			this.mCurrentProviceName = mCurrentProviceName;
		}

		public String getmCurrentCityName() {
			return mCurrentCityName;
		}

		public void setmCurrentCityName(String mCurrentCityName) {
			this.mCurrentCityName = mCurrentCityName;
		}

		public String getmCurrentAreaName() {
			return mCurrentAreaName;
		}

		public void setmCurrentAreaName(String mCurrentAreaName) {
			this.mCurrentAreaName = mCurrentAreaName;
		}
	}
}
