package com.meishe.net.temp;

import okhttp3.Callback;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/6/21.
 * @Description :中文
 * @Description :English
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public interface TempStringCallBack {

    void onResponse(String stringResponse);

    void onError(Throwable throwable);
}
