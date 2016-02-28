package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

/**
 * @author: root
 * @ClassName: Utility 
 * @Description: 解析存储省市县数据和天气数据
 * @date: 2016年2月12日 下午11:13:17
 */

public class Utility
{
	// 省市县数据格式： 代号|城市,代号|城市
	// 解析规则：就是先按逗号分隔，再按单竖线分隔
	// 接着将解析出来的数据设置到实体类中，最后调用CoolWeatherDB中的三个save()方法将数据存储到相应的表中。

	/**
	 * 解析和存储服务器返回的省级数据
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response)
	{
		if (!TextUtils.isEmpty(response))
		{
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0)
			{
				for (String p : allProvinces)
				{
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					// 将解析出来的数据存储到Province表
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和存储服务器返回的市级数据
	 * @param coolWeatherDB
	 * @param response
	 * @param provinceId
	 * @return
	 */
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId)
	{
		if (!TextUtils.isEmpty(response))
		{
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0)
			{
				for (String c : allCities)
				{
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					// 将解析出来的数据存储到City表
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和存储服务器返回的县级数据
	 * @param coolWeatherDB
	 * @param response
	 * @param cityId
	 * @return
	 */
	public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId)
	{
		if (!TextUtils.isEmpty(response))
		{
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0)
			{
				for (String c : allCounties)
				{
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					// 将解析出来的数据存储到County表
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}

	// 天气数据格式
	// {"weatherinfo":{"city":"昆山","cityid":"101190404","temp1":"21℃","temp2":"9℃","weather":"多云转小雨","img1":"d1.gif","img2":"n7.gif","ptime":"11:00"}}
	// 其中cityid是用户无需知晓的，img1和img2不准备使用，因此只需要显示城市名、温度范围、天气信息描述、发布时间这几项

	/**
	 * 解析服务器返回的JSON格式的天气数据并存储到本地
	 * @param context
	 * @param response
	 */
	public static void handleWeatherResponse(Context context, String response)
	{
		try
		{
			JSONObject jsonObject = new JSONObject(response);

			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");

			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 将所有天气信息存储到SharedPreferences文件中
	 * @param context
	 * @param cityName
	 * @param weatherCode
	 * @param temp1
	 * @param temp2
	 * @param weatherDesp
	 * @param publishTime
	 */
	public static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);

		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}

}
