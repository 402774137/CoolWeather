package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

/**
 * @author: root
 * @ClassName: ChooseAreaActivity 
 * @Description: ѡ�����
 * @date: 2016��2��28�� ����2:12:07
 */

public class ChooseAreaActivity extends Activity
{
	public static final int			LEVEL_PROVINCE	= 0;
	public static final int			LEVEL_CITY		= 1;
	public static final int			LEVEL_COUNTY	= 2;

	private ProgressDialog			progressDialog;
	private TextView				titleText;
	private ListView				listView;
	private ArrayAdapter<String>	adapter;
	private CoolWeatherDB			coolWeatherDB;

	private List<String>			dataList		= new ArrayList<String>();

	/** ʡ�б� */
	private List<Province>			provinceList;
	/** ���б� */
	private List<City>				cityList;
	/** ���б� */
	private List<County>			countyList;

	/** ѡ�е�ʡ�� */
	private Province				selectedProvince;
	/** ѡ�еĳ��� */
	private City					selectedCity;
	/** ��ǰѡ�еļ��� */
	private int						currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		choose();
		initLayout();
		queryProvinces();
	}

	/**
	 * �ȴ�SharedPreferences�ļ��ж�ȡcity_selected��־λ��
	 * ���Ϊtrue��˵����ǰ�Ѿ�ѡ��������ˣ�ֱ����ת��WeatherActivity
	 */
	private void choose()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("city_selected", false))
		{
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
	}

	/**
	 * ��ʼ������
	 */
	private void initLayout()
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.choose_area);

		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);

		coolWeatherDB = CoolWeatherDB.getInstance(this);

		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3)
			{
				// ���ز���ʱ��queryProvinces()δִ�У�currentLevelδ����
				if (currentLevel == LEVEL_PROVINCE)
				{
					selectedProvince = provinceList.get(index);
					queryCities();
				}
				else if (currentLevel == LEVEL_CITY)
				{
					selectedCity = cityList.get(index);
					queryCounties();
				}

				else if (currentLevel == LEVEL_COUNTY)
				{
//					String countyCode = countyList.get(index).getCountyCode();
//					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
//					intent.putExtra("county_code", countyCode);
//					startActivity(intent);
//					finish();
					
//					�������ҳ��
					String CountyName = countyList.get(index).getCountyName();
					Intent intent = new Intent(ChooseAreaActivity.this, TestActivity.class);
					intent.putExtra("county_name", CountyName);
					startActivity(intent);
					finish();
				}
			}
		});
	}

	/**
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryProvinces()
	{
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0)
		{
			dataList.clear();
			for (Province province : provinceList)
			{
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);

			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		}
		else
		{
			queryFromServer(null, "province");
		}
	}

	/**
	 * ��ѯѡ��ʡ�����е��У����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryCities()
	{
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());

		if (cityList.size() > 0)
		{
			dataList.clear();
			for (City city : cityList)
			{
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);

			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}
		else
		{
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	/**
	 * ��ѯѡ���������е��أ����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryCounties()
	{
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());

		if (countyList.size() > 0)
		{
			dataList.clear();
			for (County county : countyList)
			{
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);

			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		}
		else
		{
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}

	/**
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ�������ݲ������洢�����ݿ⣬����ڴ����ݿ��ж�ȡ
	 * @param code
	 * @param type
	 */
	private void queryFromServer(final String code, final String type)
	{
		String address;
		if (!TextUtils.isEmpty(code))
		{
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}
		else
		{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}

		showProgressDialog();

		HttpUtil.sendHttpRequest(address, new HttpCallbackListener()
		{
			// �ṩ�����Թ��ص������·������������߳��У�
			@Override
			public void onFinish(String response)
			{
				boolean result = false;

				// �����ʹ洢λ������
				if ("province".equals(type))
				{
					result = Utility.handleProvincesResponse(coolWeatherDB, response);
				}
				else if ("city".equals(type))
				{
					result = Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
				}
				else if ("county".equals(type))
				{
					result = Utility.handleCountiesResponse(coolWeatherDB, response, selectedCity.getId());
				}

				if (result)
				{
					// ͨ��runOnUiThread()�����ص����̴߳����߼�
					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							closeProgressDialog();

							// �����ݿ��ж�ȡ
							if ("province".equals(type))
							{
								queryProvinces();
							}
							else if ("city".equals(type))
							{
								queryCities();
							}
							else if ("county".equals(type))
							{
								queryCounties();
							}
						}
					});
				}
			}

			@Override
			public void onError(Exception e)
			{
				// ͨ��runOnUiThread()�����ص����̴߳����߼�
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog()
	{
		if (progressDialog == null)
		{
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/**
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog()
	{
		if (progressDialog != null)
		{
			progressDialog.dismiss();
		}
	}

	/* 
	 *�����Ƿ�����@see android.app.Activity#onBackPressed()
	 * ����Back���������ݵ�ǰ�ļ������жϣ���ʱӦ�÷������б�ʡ�б�����ֱ���˳���
	 */
	@Override
	public void onBackPressed()
	{
		if (currentLevel == LEVEL_COUNTY)
		{
			queryCities();
		}
		else if (currentLevel == LEVEL_CITY)
		{
			queryProvinces();
		}
		else
		{
			finish();
		}
	}
}
