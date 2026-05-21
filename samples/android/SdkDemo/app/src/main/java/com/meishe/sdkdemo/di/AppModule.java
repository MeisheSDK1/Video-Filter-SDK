package com.meishe.sdkdemo.di;

import androidx.lifecycle.ViewModelProvider;

import com.meishe.sdkdemo.capture.viewmodel.AppViewModelFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/28 下午4:05
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
@Module(subcomponents = ViewModelSubComponent.class)
public class AppModule {


    @Singleton
    @Provides
    ViewModelProvider.Factory provideViewModelFactory(ViewModelSubComponent.Builder viewModelSubComponent){
        return new AppViewModelFactory(viewModelSubComponent.build());
    }


}
