package com.meishe.sdkdemo.capture.viewmodel;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.meishe.sdkdemo.di.ViewModelSubComponent;

import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/24 下午9:21
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
@Singleton
public class AppViewModelFactory implements ViewModelProvider.Factory {

    private final ArrayMap<Class, Callable<? extends ViewModel>> mCreators;

    @Inject
    public AppViewModelFactory(ViewModelSubComponent viewModelSubComponent) {
        mCreators =new ArrayMap<>();
        mCreators.put(FilterViewModel.class,() ->viewModelSubComponent.createAppListViewModel());
        mCreators.put(CaptureEffectTabViewModel.class,() ->viewModelSubComponent.createCaptureEffectTabViewModel());
        mCreators.put(CaptureEffectTabViewModel.class,() ->viewModelSubComponent.createCaptureViewModel());
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Callable<? extends ViewModel> model = mCreators.get(modelClass);
        if (model == null) {
            for (Map.Entry<Class, Callable<? extends ViewModel>> entry : mCreators.entrySet()) {
                if (modelClass.isAssignableFrom(entry.getKey())) {
                    model = entry.getValue();
                    break;
                }
            }
        }
        if (model == null) {
            throw new IllegalArgumentException("Unknown model class " + modelClass);
        }
        try {
            return (T) model.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
