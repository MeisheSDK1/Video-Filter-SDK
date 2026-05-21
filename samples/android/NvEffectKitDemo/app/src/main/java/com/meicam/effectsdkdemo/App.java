package com.meicam.effectsdkdemo;

import android.app.Application;
import android.content.Context;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/5/23 15:51
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getAppContext() {
        return mContext;
    }
}
