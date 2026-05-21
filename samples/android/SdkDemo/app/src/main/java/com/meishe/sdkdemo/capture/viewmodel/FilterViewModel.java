package com.meishe.sdkdemo.capture.viewmodel;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.meishe.http.bean.BaseBean;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.capture.bean.EffectInfo;
import com.meishe.sdkdemo.capture.bean.KindInfo;
import com.meishe.sdkdemo.repository.AppRepository;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvHttpFilePathUtil;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/24 下午9:19
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class FilterViewModel extends ViewModel {

    private MutableLiveData<KindInfo> triggerLiveData = new MutableLiveData<>();
    AppRepository mAppRepository;

    public FilterViewModel() {
        this.mAppRepository = AppRepository.AppRepositoryHelper.getInstance();
    }

    /**
     * 滤镜数据结构
     * filter data
     */
    private LiveData<BaseBean<EffectInfo>> mFilterLiveData = Transformations.switchMap(triggerLiveData,
            p -> mAppRepository.getFilterData(p));

    public void requestFilterData(KindInfo kindInfo) {
        triggerLiveData.setValue(kindInfo);
    }

    @SuppressLint("CheckResult")
    public void refreshData(List<EffectInfo> datas, String packageUrl) {
        Observable.just(datas).subscribeOn(Schedulers.io()).subscribe(new Consumer<List<EffectInfo>>() {
            @Override
            public void accept(List<EffectInfo> effectInfos) throws Exception {
                for (EffectInfo filterInfo : effectInfos) {
                    if (MSApplication.isZh()) {
                        filterInfo.setName(filterInfo.getDisplayNameZhCn());
                    } else {
                        filterInfo.setName(filterInfo.getDisplayName());
                    }
//                    String effectId = PreferencesManager.get().getString(filterInfo.getId());
//                    if (!TextUtils.isEmpty(effectId)) {
//                        filterInfo.setDownload(true);
//                    }
                    filterInfo.setDownload(NvHttpFilePathUtil.CheckIsDownload(filterInfo, NvAsset.ASSET_FILTER));
                    filterInfo.setSelect(false);
                    if (TextUtils.equals(packageUrl, filterInfo.getZipUrl())) {
                        filterInfo.setSelect(true);
                    }
                }
            }
        });
    }


    public LiveData<BaseBean<EffectInfo>> getFilterLiveData() {
        return mFilterLiveData;
    }

    protected String getFilterPath(EffectInfo filterInfo, int assetType) {
        String packageUrl = filterInfo.getPackageUrl();
        final String pathDir = PathUtils.getSDCardPathByType(assetType);
        if (TextUtils.isEmpty(packageUrl)) {
            return "";
        }
        String[] split = packageUrl.split("/");
        String effectPath = pathDir + File.separator + split[split.length - 1];
        return effectPath;
    }
}
