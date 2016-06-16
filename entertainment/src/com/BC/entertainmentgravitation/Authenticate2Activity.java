package com.BC.entertainmentgravitation;

import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.BC.entertainmentgravitation.dialog.BankDialog;
import com.BC.entertainmentgravitation.entity.RegionItem;
import com.BC.entertainmentgravitation.util.CitydbUtil;
import com.summer.activity.BaseActivity;
import com.umeng.analytics.MobclickAgent;

public class Authenticate2Activity extends BaseActivity implements OnClickListener, OnWheelChangedListener{

	private List<RegionItem> provinceList;
	private List<RegionItem> cityList;
	private CitydbUtil citydbUtil;
	private BankDialog.Builder builder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authenticate_step2);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		findViewById(R.id.textView1).setOnClickListener(this);
		citydbUtil = CitydbUtil.structureCitydbUtil(this);
		provinceList = citydbUtil.selectCountry();
		builder = new BankDialog.Builder(this);
		builder.setWheelChangedListener(this);
	}
	
	/**
	 * 选择开户省份
	 */
	private void showSelectBankProvinceDialog()
	{
		provinceList = citydbUtil.selectCountry();
		if (provinceList != null && provinceList.size() > 0)
		{
			builder.setProvinceList(provinceList);
			BankDialog dialog = builder.Create(1, null);
			builder.setTitle("开户省份");
			builder.setPositiveButton(new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					int location = builder.getProvince().getCurrentItem();
					RegionItem item = builder.getProvinceList().get(location);
					cityList = citydbUtil.selectCity(item.getPcode());
				}});
			
			builder.setNegativeButton(new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (dialog != null)
					{
						dialog.dismiss();
					}
				}
			});
			
			dialog.show();
		}
	}
	
	/**
	 * 选择开户城市
	 */
	private void showSelectBankCityDialog()
	{
		if (cityList != null && cityList.size() > 0)
		{
			builder.setCityList(cityList);
			BankDialog dialog = builder.Create(2, null);
			builder.setTitle("开户城市");
			builder.setPositiveButton(new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					int location = builder.getCity().getCurrentItem();
					RegionItem item = builder.getCityList().get(location);
				}});
			
			builder.setNegativeButton(new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (dialog != null)
					{
						dialog.dismiss();
					}
				}
			});
			
			dialog.show();
		}
		else
		{
			BankDialog dialog = builder.Create(-1, "请先选择开户银行省份");
			builder.setPositiveButton(new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					int location = builder.getProvince().getCurrentItem();
					RegionItem item = builder.getProvinceList().get(location);
					cityList = citydbUtil.selectCity(item.getPcode());
				}});
			
			builder.setNegativeButton(new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (dialog != null)
					{
						dialog.dismiss();
					}
				}
			});
			
			dialog.show();
		}
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    
	@Override
	protected void onResume() {
		super.onResume();
        MobclickAgent.onResume(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textView1:
			Intent it = new Intent(this, Authenticate3Activity.class);
			startActivity(it);
			break;
		/**
		 * 返回键
		 */
		case R.id.imageViewBack:
			finish();
			break;
		}
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		switch(wheel.getId())
		{
		case R.id.wheelViewPovince:
//			int currentItem = builder.getProvince().getCurrentItem();
			
			break;
		case R.id.wheelViewCity:
			break;
		}
	}
	
//	private void updateCity(int location)
//	{
//		RegionItem item = builder.getProvinceList().get(location);
//		cityList = citydbUtil.selectCity(item.getPcode());
//	}

}
