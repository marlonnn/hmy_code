package com.BC.entertainmentgravitation;

import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.BC.entertainment.adapter.RegionAdapter;
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
	private TextView txtViewBankProvince;
	private TextView txtViewBankCity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authenticate_step2);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		findViewById(R.id.textView1).setOnClickListener(this);
		citydbUtil = CitydbUtil.structureCitydbUtil(this);

		initView();
	}
	
	private void initView()
	{
		txtViewBankProvince = (TextView) findViewById(R.id.txtViewBankProvince);
		txtViewBankCity = (TextView) findViewById(R.id.txtViewBankCity);
		txtViewBankProvince.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showSelectBankProvinceDialog();
			}
		});
		txtViewBankCity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showSelectBankCityDialog();
			}
		});
	}
	
	/**
	 * 选择开户省份
	 */
	private void showSelectBankProvinceDialog()
	{
		provinceList = citydbUtil.selectCountry();
		if (provinceList != null && provinceList.size() > 0)
		{
			
			RegionAdapter adapter = new RegionAdapter(this,provinceList);
			builder = new BankDialog.Builder(this, adapter);
			builder.setWheelChangedListener(this);
			builder.setTitle("开户省份");
			builder.setPositiveButton(new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					int location = builder.getProvince().getCurrentItem();
					RegionItem item = provinceList.get(location);
					cityList = citydbUtil.selectCity(item.getPcode());
					txtViewBankProvince.setText(item.getName());
					if (dialog != null)
					{
						dialog.dismiss();
					}
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
			BankDialog dialog = builder.Create(1, null);
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
			builder = new BankDialog.Builder(this, new RegionAdapter(this,cityList));
			builder.setWheelChangedListener(this);
			builder.setTitle("开户城市");

			builder.setPositiveButton(new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					int location = builder.getCity().getCurrentItem();
					RegionItem item = cityList.get(location);
					txtViewBankCity.setText(item.getName());
					if (dialog != null)
					{
						dialog.dismiss();
					}
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
			BankDialog dialog = builder.Create(2, null);
			dialog.show();
		}
		else
		{
			builder = new BankDialog.Builder(this, null);
			builder.setTitle("操作异常");

			builder.setPositiveButton(new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (dialog != null)
					{
						dialog.dismiss();
					}
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
			BankDialog dialog = builder.Create(-1, "请先选择开户银行省份");
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
