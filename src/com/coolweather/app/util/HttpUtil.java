package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author: root
 * @ClassName: HttpUtil 
 * @Description: ������ʹ���
 * @date: 2016��2��28�� ����1:26:43
 */

public class HttpUtil
{
	/**
	 * ����һ�����̷߳�����������
	 * @param address
	 * @param listener
	 */
	public static void sendHttpRequest(final String address, final HttpCallbackListener listener)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				HttpURLConnection connection = null;
				try
				{
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);

					// InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null)
					{
						response.append(line);
					}
					if (listener != null)
					{
						// �ص�onFinish()����
						listener.onFinish(response.toString());
					}
				}
				catch (Exception e)
				{
					if (listener != null)
					{
						// �ص�onError()����
						listener.onError(e);
					}
				}
				finally
				{
					if (connection != null)
					{
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}
