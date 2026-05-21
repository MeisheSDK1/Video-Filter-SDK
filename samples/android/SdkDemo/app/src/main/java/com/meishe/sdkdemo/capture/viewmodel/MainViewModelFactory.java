package com.meishe.sdkdemo.capture.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.meishe.sdkdemo.capture.bean.CategoryInfo;

import org.jetbrains.annotations.NotNull;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/4/23 上午11:30
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MainViewModelFactory implements ViewModelProvider.Factory {

    private CategoryInfo categoryInfo;

    public MainViewModelFactory(CategoryInfo categoryInfo) {
        this.categoryInfo = categoryInfo;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        return (T) new CaptureEffectTabViewModel(categoryInfo);
    }
}
