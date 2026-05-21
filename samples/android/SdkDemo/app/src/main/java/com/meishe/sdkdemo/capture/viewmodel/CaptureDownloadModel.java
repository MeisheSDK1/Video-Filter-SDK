package com.meishe.sdkdemo.capture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.meishe.sdkdemo.capture.bean.EffectInfo;
import com.meishe.sdkdemo.repository.AppRepository;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/4/23 下午8:30
 * @Description : 负责特效下载
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CaptureDownloadModel extends ViewModel {

    private AppRepository mAppRepository;
    private MutableLiveData<String> mFilePath=new MutableLiveData<>();

    public CaptureDownloadModel() {
        this.mAppRepository = AppRepository.AppRepositoryHelper.getInstance();
    }

    public void downloadPackage(EffectInfo filterInfo) {
        mAppRepository.downloadPackage(mFilePath,filterInfo);
    }

    public LiveData<String> getFilePath() {
        return mFilePath;
    }

}
