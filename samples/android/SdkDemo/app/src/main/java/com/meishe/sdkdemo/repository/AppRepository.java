package com.meishe.sdkdemo.repository;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.meicam.sdk.NvsStreamingContext;
import com.meishe.http.bean.BaseBean;
import com.meishe.net.custom.BaseResponse;
import com.meishe.net.custom.RequestCallback;
import com.meishe.net.custom.SimpleDownListener;
import com.meishe.net.model.Progress;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.capture.bean.CategoryInfo;
import com.meishe.sdkdemo.capture.bean.EffectInfo;
import com.meishe.sdkdemo.capture.bean.KindInfo;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.asset.HttpFilePathUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/29 下午7:17
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class AppRepository {

    private static AppRepository mInstance = new AppRepository();
    private Map<String, BaseBean<EffectInfo>> cacheMap;

    public static class AppRepositoryHelper {
        public static AppRepository getInstance() {
            return mInstance;
        }

    }

    private AppRepository() {
        cacheMap = new HashMap<>();
    }


    /**
     * 获取滤镜数据
     * Get filter data
     *
     * @param kindInfo
     * @return
     */
    public MutableLiveData<BaseBean<EffectInfo>> getFilterData(KindInfo kindInfo) {
        MutableLiveData<BaseBean<EffectInfo>> liveData = new MutableLiveData<>();
        if (kindInfo == null) {
            return null;
        }
        String cacheKey = getCacheKey(kindInfo);
        BaseBean<EffectInfo> filterInfos = cacheMap.get(cacheKey);
        if (filterInfos != null) {
            liveData.setValue(filterInfos);
            return liveData;
        }

        final int category = kindInfo.getCategory();
        final int kindInfoId = kindInfo.getId();
        HttpManager.getMaterialList(null, String.valueOf(kindInfo.getMaterialType()),
                String.valueOf(category),
                String.valueOf(kindInfoId), "", MSApplication.getSdkVersion(),
                kindInfo.getPageNumber(), Constants.PAGE_SIZE, new RequestCallback<BaseBean<EffectInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<BaseBean<EffectInfo>> response) {
                        if (response.getCode() == 1) {
                            List<EffectInfo> elements = response.getData().getElements();
                            if (elements != null) {
                                for (EffectInfo filterInfo : elements) {
                                    filterInfo.setName(filterInfo.getDisplayName());
                                    if (category == 2 && (kindInfoId == 8 || kindInfoId == 9)) {
                                        //分屏和边框滤镜默认值是1
                                        // The default value for split screen and border filters is 1.
                                        filterInfo.setDefaultStrength(1);
                                    } else {
                                        filterInfo.setDefaultStrength(0.8F);
                                    }
                                    String effectPath = HttpFilePathUtil.getFilterPathOutOfPathSuffix(filterInfo, kindInfo.getMaterialType());
                                    boolean isDownload = false;
                                    if (new File(effectPath).exists()) {
                                        NvsStreamingContext streamingContext = NvsStreamingContext.getInstance();
                                        if ((null != streamingContext) && (null != streamingContext.getAssetPackageManager())) {
                                            String fxPath = HttpFilePathUtil.getFilterPath(filterInfo, kindInfo.getMaterialType());
                                            int localVersion = streamingContext.getAssetPackageManager().getAssetPackageVersionFromAssetPackageFilePath(fxPath);
                                            int netVersion = filterInfo.getVersion();
                                            isDownload = localVersion >= netVersion;
                                        } else {
                                            isDownload = true;
                                        }
                                    }
                                    filterInfo.setDownload(isDownload);
                                }

                            }
                            String cacheKey = getCacheKey(kindInfo);
                            cacheMap.put(cacheKey, response.getData());
                            liveData.setValue(response.getData());
                        }

                    }

                    @Override
                    public void onError(BaseResponse<BaseBean<EffectInfo>> response) {
                        liveData.setValue(null);
                    }
                });
        return liveData;
    }


    /**
     * 下载特效包
     * Download special Effects Pack
     *
     * @param filterInfo
     */
    public MutableLiveData<String> downloadPackage(MutableLiveData<String> filePathLiveDate, EffectInfo filterInfo) {
        if (null == filterInfo) {
            return null;
        }
        String packageUrl = filterInfo.getZipUrl();
        if (TextUtils.isEmpty(packageUrl)) {
            return null;
        }

        String effectPathDir = PathUtils.getCaptureSDCardPathByType(filterInfo.getAssetType());
        String[] split = packageUrl.split("/");
        HttpManager.download(packageUrl, packageUrl, effectPathDir, split[split.length - 1], new SimpleDownListener(packageUrl) {
            @Override
            public void onProgress(Progress progress) {
                super.onProgress(progress);
                filterInfo.setProgress((int) (progress.fraction * 360));
            }

            @Override
            public void onError(Progress progress) {
                super.onError(progress);
            }

            @Override
            public void onFinish(File file, Progress progress) {
                super.onFinish(file, progress);
                Log.e("TAG", "onFinish: " + file, new Exception(""));
                filterInfo.setDownload(true);
                if (null != file) {
                    String absolutePath = file.getAbsolutePath();
                    filePathLiveDate.setValue(absolutePath);
                }
            }
        });
        return filePathLiveDate;
    }


    private String getCacheKey(KindInfo kindInfo) {
        StringBuilder cacheKeyBuilder = new StringBuilder();
        cacheKeyBuilder.append(kindInfo.getMaterialType())
                .append(kindInfo.getCategory())
                .append(kindInfo.getId())
                .append(kindInfo.getPageNumber());
        return cacheKeyBuilder.toString();
    }

    private String getCacheKey(CategoryInfo categoryInfo) {
        StringBuilder cacheKeyBuilder = new StringBuilder();
        cacheKeyBuilder.append(categoryInfo.getType());
        cacheKeyBuilder.append(categoryInfo.getId());
        cacheKeyBuilder.append(categoryInfo.getPageNumber());
        return cacheKeyBuilder.toString();
    }

    protected String getFilterPath(EffectInfo filterInfo, int assetType) {
        String packageUrl = filterInfo.getPackageUrl();
        final String pathDir = PathUtils.getSDCardPathByType(assetType);
        String[] split = packageUrl.split("/");
        String effectPath = pathDir + File.separator + split[split.length - 1];
        return effectPath;
    }

    public void destory() {
        if ((null != cacheMap) && !cacheMap.isEmpty()) {
            cacheMap.clear();
        }
    }
}
