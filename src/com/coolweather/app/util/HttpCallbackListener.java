package com.coolweather.app.util;

/**
 * @author: root
 * @ClassName: HttpCallbackListener 
 * @Description: 网络回调接口
 * @date: 2016年2月12日 下午10:21:55
 */

public interface HttpCallbackListener
{
	void onFinish(String response);

	void onError(Exception e);

}
