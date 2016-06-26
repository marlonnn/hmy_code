package com.BC.entertainment.cache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * 身份认证相关
 * @author zhongwen
 *
 */
/**
 * @author zhongwen
 *
 */
public class AuthenCache {
	
	private Context context;
	
	/**
	 * 把全国的省市区的信息以json的格式保存，解析完成后赋值为null
	 */
//	private JSONObject mJsonObj;
	private JSONArray mjsonArray;
	
	/**
	 * 所有省
	 */
	public String[] mProvinceDatas;
	
	/**
	 * key - 省 value - 市s
	 */
	public Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	/**
	 * key - 市 values - 区s
	 */
	public Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();
	
	/**
	 * 当前省的名称
	 */
	public String mCurrentProviceName;
	/**
	 * 当前市的名称
	 */
	public String mCurrentCityName;
	/**
	 * 当前区的名称
	 */
	public String mCurrentAreaName ="";
	
	/**
	 * 开户银行
	 */
	public static final String[] banks = new String[] {
		"中国银行","北京银行","建设银行","中国工商银行",
		"中国农业银行","中信银行","民生银行","中国交通银行",
		"中国邮政储蓄银行","招商银行","中国光大银行","兴业银行","其他"};

	/**
	 * 专业类型
	 */
	public static final String[] professional = new String[] {
		"表演","舞蹈","声乐","模特","导演","民间艺人"
	};
	
	/***
	 * 身份证类型
	 */
	public static final String[] IDType = new String[] {
		"身份证", "港澳居民来往内地通行证","香港、澳门身份证", "台胞证", "护照"
	};
	
	public AuthenCache(Context context)
	{
		this.context = context;
		if (mProvinceDatas == null)
		{
			AreaTread area = new AreaTread();
			area.start();
		}
	}
	
	/**
	 * 解析整个Json对象，完成后释放Json对象的内存
	 */
	public void initDatas()
	{
		try
		{
			mProvinceDatas = new String[mjsonArray.length()];
			for (int i = 0; i < mjsonArray.length(); i++)
			{
				JSONObject jsonP = mjsonArray.getJSONObject(i);// 每个省的json对象
				String province = jsonP.getString("name");// 省名字

				mProvinceDatas[i] = province;

				JSONArray jsonCs = null;
				try
				{
					/**
					 * Throws JSONException if the mapping doesn't exist or is
					 * not a JSONArray.
					 */
					jsonCs = jsonP.getJSONArray("city");
				} catch (Exception e1)
				{
					continue;
				}
				String[] mCitiesDatas = new String[jsonCs.length()];
				for (int j = 0; j < jsonCs.length(); j++)
				{
					JSONObject jsonCity = jsonCs.getJSONObject(j);
					String city = jsonCity.getString("name");// 市名字
					mCitiesDatas[j] = city;
					JSONArray jsonAreas = null;
					try
					{
						/**
						 * Throws JSONException if the mapping doesn't exist or
						 * is not a JSONArray.
						 */
						jsonAreas = jsonCity.getJSONArray("area");
					} catch (Exception e)
					{
						continue;
					}

					String[] mAreasDatas = new String[jsonAreas.length()];// 当前市的所有区
					for (int k = 0; k < jsonAreas.length(); k++)
					{
						String area = jsonAreas.getString(k);// 区域的名称
						mAreasDatas[k] = area;
					}
					mAreaDatasMap.put(city, mAreasDatas);
				}

				mCitisDatasMap.put(province, mCitiesDatas);
			}

		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		mjsonArray = null;
	}
	
	/**
	 * 从assert文件夹中读取省市区的json文件，然后转化为json对象
	 */
	public void initJsonData()
	{
		try
		{
			InputStream is = context.getAssets().open("districtData.json");
			InputStreamReader inputStreamReader = new InputStreamReader(is, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	        String temp = "";
	        String jsonSource = "";
	            //一行一行的读取
	            while ((temp = bufferedReader.readLine()) != null)
	            {
	                jsonSource += temp;
	            }
	        //关闭
	        bufferedReader.close();
	        is.close();
			mjsonArray = new JSONArray(jsonSource.toString());
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	class AreaTread extends Thread{

		@Override
		public void run() {
			initJsonData();
			initDatas();
			super.run();
		}
		
	}
}
