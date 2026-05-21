package com.meishe.sdkdemo.di;

import android.app.Application;

import com.meishe.sdkdemo.MSApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/28 下午4:08
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance Builder application(Application application);
        AppComponent build();
    }
    void inject(MSApplication mvvmApplication);
}
