package com.meishe.sdkdemo.capture.viewmodel;

import android.text.TextUtils;

import com.meishe.base.utils.LogUtils;
import com.meishe.base.utils.Utils;
import com.meishe.http.AssetType;
import com.meishe.net.custom.BaseResponse;
import com.meishe.net.custom.RequestCallback;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.bean.voice.ChangeVoiceData;
import com.meishe.sdkdemo.capture.bean.CategoryInfo;
import com.meishe.sdkdemo.capture.bean.KindInfo;
import com.meishe.sdkdemo.capture.bean.TypeAndCategoryInfo;
import com.meishe.sdkdemo.repository.AppRepository;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.asset.NvAsset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/25 下午3:53
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CaptureViewModel extends ViewModel {
    private final MutableLiveData<TypeAndCategoryInfo> mFilterTypeInfo = new MutableLiveData<>();
    private final MutableLiveData<TypeAndCategoryInfo> mPropTypeInfo = new MutableLiveData<>();
    private final MutableLiveData<TypeAndCategoryInfo> mComponentTypeInfo = new MutableLiveData<>();
    private final MutableLiveData<TypeAndCategoryInfo> mStickerTypeInfo = new MutableLiveData<>();
    private final AppRepository mAppRepository;

    public CaptureViewModel() {
        this.mAppRepository = AppRepository.AppRepositoryHelper.getInstance();
    }

    /**
     * 得到类别信息
     * get effect type data
     *
     * @param tag
     */
    public void getEffectTypeData(@NonNull String tag) {
        String type = "";
        if (tag.equals(Constants.FRAGMENT_FILTER_TAG)) {
            type = AssetType.FILTER_ALL.getType();
        } else if (tag.equals(Constants.FRAGMENT_PROP_TAG)) {
            type = AssetType.AR_SCENE_PRE_ALL.getType();
        } else if (tag.equals(Constants.FRAGMENT_COMPONENT_CAPTION_TAG)) {
            type = AssetType.COM_CAPTION_ALL.getType();
        } else if (tag.equals(Constants.FRAGMENT_STICKER_TAG)) {
            type = AssetType.STICKER_ALL.getType();
        }
        String finalType = type;
        HttpManager.getMaterialTypeAndCategory(null, type, "", "",
                new RequestCallback<List<TypeAndCategoryInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<TypeAndCategoryInfo>> response) {
                        if (response.getCode() == 1) {
                            if (null != response.getData()) {
                                TypeAndCategoryInfo typeAndCategoryInfo =
                                        response.getData().get(0);
                                CategoryInfo categoryInfo = typeAndCategoryInfo.getCategories().get(0);

                                if (finalType.equals(AssetType.FILTER_ALL.getType())) {
                                    mFilterTypeInfo.setValue(typeAndCategoryInfo);
                                    KindInfo kindInfo = categoryInfo.getKinds().get(0);
                                    mAppRepository.getFilterData(kindInfo);
                                } else if (finalType.equals(AssetType.AR_SCENE_PRE_ALL.getType())) {
                                    mPropTypeInfo.setValue(typeAndCategoryInfo);
                                    categoryInfo.setAssetType(NvAsset.ASSET_ARSCENE_FACE);
                                } else if (finalType.equals(AssetType.COM_CAPTION_ALL.getType())) {
                                    mComponentTypeInfo.setValue(typeAndCategoryInfo);
                                    categoryInfo.setAssetType(NvAsset.ASSET_COMPOUND_CAPTION);
                                } else if (finalType.equals(AssetType.STICKER_ALL.getType())) {
                                    mStickerTypeInfo.setValue(typeAndCategoryInfo);
                                    categoryInfo.setAssetType(NvAsset.ASSET_ANIMATED_STICKER);
                                }

                            }

                        }
                    }

                    @Override
                    public void onError(BaseResponse<List<TypeAndCategoryInfo>> response) {
                        LogUtils.e(response.getMessage());
                    }
                });
    }

    public List<ChangeVoiceData> getVoiceDatas() {
        List<ChangeVoiceData> dataList = new ArrayList<>();
        dataList.add(ChangeVoiceData.noneData());
        dataList.addAll(getAssetsChangeVoices());
        return dataList;
    }

    private List<ChangeVoiceData> getAssetsChangeVoices() {
        List<ChangeVoiceData> assList = new ArrayList<>();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(MSApplication.getContext().getAssets().open(Utils.isZh() ? "voice/info_Zh.txt" : "voice/info.txt"));
            if (inputStreamReader != null) {
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    //todo readLine 每一行读取背景图等信息 处理assList
                    //Read the background image and other information for each line to process assList
                    if (!TextUtils.isEmpty(line) && !line.startsWith("#")) {
                        String[] split = line.split(",");
                        String voiceId = split.length > 0 ? split[0] : "";
                        String name = split.length > 1 ? split[1] : "";
                        String bgColor = split.length > 2 ? split[2] : "";
                        String imgUrl = split.length > 3 ? split[3] : "";
                        ChangeVoiceData changeVoiceData = new ChangeVoiceData();
                        changeVoiceData.setName(name);
                        changeVoiceData.setVoiceId(voiceId);
                        changeVoiceData.setBgUrl(imgUrl);
                        changeVoiceData.setBgColor(bgColor);
                        assList.add(changeVoiceData);
                    }
                }
                bufferedReader.close();
                inputStreamReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return assList;
        }

        return assList;
    }


    public MutableLiveData<TypeAndCategoryInfo> getFilterTypeInfo() {
        return mFilterTypeInfo;
    }

    public MutableLiveData<TypeAndCategoryInfo> getPropTypeInfo() {
        return mPropTypeInfo;
    }

    public MutableLiveData<TypeAndCategoryInfo> getComponentTypeInfo() {
        return mComponentTypeInfo;
    }

    public MutableLiveData<TypeAndCategoryInfo> getStickerTypeInfo() {
        return mStickerTypeInfo;
    }
}
