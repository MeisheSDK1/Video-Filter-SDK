package com.meishe.sdkdemo.di;

import com.meishe.sdkdemo.capture.viewmodel.FilterViewModel;
import com.meishe.sdkdemo.capture.viewmodel.CaptureEffectTabViewModel;
import com.meishe.sdkdemo.capture.viewmodel.CaptureViewModel;
import dagger.Subcomponent;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/28 下午4:00
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
@Subcomponent
public interface ViewModelSubComponent {
    @Subcomponent.Builder
    interface Builder {
        ViewModelSubComponent build();
    }

    FilterViewModel createAppListViewModel();
    CaptureEffectTabViewModel createCaptureEffectTabViewModel();
    CaptureViewModel createCaptureViewModel();
}
