package com.meishe.sdkdemo.di;

import com.meishe.sdkdemo.MSApplication;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/29 下午8:48
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class AppInjector {
    private AppInjector() {}

    public static void init(MSApplication msApplication){
        DaggerAppComponent.builder().application(msApplication).build().inject(msApplication);
    }
}
