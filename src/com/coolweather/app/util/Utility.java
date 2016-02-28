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
 * @Description: �����洢ʡ�������ݺ���������
 * @date: 2016��2��12�� ����11:13:17
 */

public class Utility
{
	// ʡ�������ݸ�ʽ�� ����|����,����|����
	// �������򣺾����Ȱ����ŷָ����ٰ������߷ָ�
	// ���Ž������������������õ�ʵ�����У�������CoolWeatherDB�е�����save()���������ݴ洢����Ӧ�ı��С�

	/**
	 * �����ʹ洢���������ص�ʡ������
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
					// ���������������ݴ洢��Province��
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * �����ʹ洢���������ص��м�����
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
					// ���������������ݴ洢��City��
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * �����ʹ洢���������ص��ؼ�����
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
					// ���������������ݴ洢��County��
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}

	// �������ݸ�ʽ
	// {"weatherinfo":{"city":"��ɽ","cityid":"101190404","temp1":"21��","temp2":"9��","weather":"����תС��","img1":"d1.gif","img2":"n7.gif","ptime":"11:00"}}
	// ����cityid���û�����֪���ģ�img1��img2��׼��ʹ�ã����ֻ��Ҫ��ʾ���������¶ȷ�Χ��������Ϣ����������ʱ���⼸��

	/**
	 * �������������ص�JSON��ʽ���������ݲ��洢������
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
	 * ������������Ϣ�洢��SharedPreferences�ļ���
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��", Locale.CHINA);

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
