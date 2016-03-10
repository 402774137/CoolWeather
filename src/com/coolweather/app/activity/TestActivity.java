package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.Window;

import android.widget.TextView;

public class TestActivity extends Activity {

	private TextView showtext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);

		initLayout();

		loaddata();
	}

	private void initLayout() {
		// TODO 自动生成的方法存根
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.test);

		showtext = (TextView) findViewById(R.id.showtext);

	}

private void loaddata() {
	// TODO 自动生成的方法存根
	String countyname = getIntent().getStringExtra("county_name");
	
	if (!TextUtils.isEmpty(countyname))
	{
		
		queryFromServer(countyname);
	}
}

private void queryFromServer(String countyname)
{
//	http://wthrcdn.etouch.cn/weather_mini?citykey=101010100
//	http://wthrcdn.etouch.cn/weather_mini?city=北京
	
	String address = "http://wthrcdn.etouch.cn/weather_mini?city=" + countyname ;
	
	HttpUtil.sendHttpRequest(address, new HttpCallbackListener()
	{
		@Override
		public void onFinish(final String response)
		{
			
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					showtext.setText(response);
				}
			});
		}

		@Override
		public void onError(Exception e)
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					showtext.setText("读取失败");
				}
			});
		}
	});
}


}
