package com.coolweather.app.util;

/**
 * @author: root
 * @ClassName: HttpCallbackListener 
 * @Description: 
 * @date: 2016��2��12�� ����10:21:55
 */

public interface HttpCallbackListener
{
	void onFinish(String response);

	void onError(Exception e);

}
