package com.meishe.sdkdemo.capture.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meishe.base.msbus.MSBus;
import com.meishe.sdkdemo.BR;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.capture.adapter.CommonRecyclerViewAdapter;
import com.meishe.sdkdemo.capture.bean.EffectInfo;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/4/21 下午4:05
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class LocalEffectFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private String mTag;
    private ArrayList<Object> mEffectInfos;
    private int mAssetType;
    private int mInstallType;
    private List<EffectInfo> mData = new ArrayList<>();

    public static LocalEffectFragment newInstance(String tag) {
        LocalEffectFragment fragment = new LocalEffectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int initRootView() {
        return R.layout.fragment_capture_filter_item;
    }

    @Override
    protected void initArguments(Bundle arguments) {
        if (arguments != null) {
            mTag = (String) arguments.getSerializable(ARG_PARAM1);
        }
        if (Constants.FRAGMENT_STICKER_TAG.equals(mTag)) {
            mAssetType = NvAsset.ASSET_ANIMATED_STICKER;
            mInstallType = NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ANIMATEDSTICKER;
        } else if (Constants.FRAGMENT_COMPONENT_CAPTION_TAG.equals(mTag)) {
            mAssetType = NvAsset.ASSET_COMPOUND_CAPTION;
            mInstallType = NvsAssetPackageManager.ASSET_PACKAGE_TYPE_COMPOUND_CAPTION;
        } else if (Constants.FRAGMENT_PROP_TAG.equals(mTag)) {
            mAssetType = NvAsset.ASSET_ARSCENE_FACE;
            mInstallType = NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ARSCENE;
        } else if (Constants.FRAGMENT_FILTER_TAG.equals(mTag)) {
            mAssetType = NvAsset.ASSET_FILTER;
            mInstallType = NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX;
        }
    }

    @Override
    protected void initView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        initRecyclerViewGrid(5, R.layout.capture_effect_tab_view, BR.filterInfo);
    }

    @Override
    protected void onLazyLoad() {

    }

    @SuppressLint("CheckResult")
    @Override
    protected void initData() {
        loadLocalEffect();
    }

    @Override
    protected void initListener() {
        mCommonRecyclerViewAdapter.setOnItemClickListener(new CommonRecyclerViewAdapter.OnItemClickListener<EffectInfo>() {
            @Override
            public void onItemClick(View view, int posotion, EffectInfo effectInfo) {
                if (effectInfo != null) {
                    setCaptureFilterByPath(effectInfo.getInfoUrl());
                }
            }
        });
    }

    private void loadLocalEffect() {
        String localPath = null;
        if (Constants.FRAGMENT_STICKER_TAG.equals(mTag)) {
            localPath = PathUtils.getLocalStickerDir();
        } else if (Constants.FRAGMENT_COMPONENT_CAPTION_TAG.equals(mTag)) {
            localPath = PathUtils.getLocalCompDir();
        } else if (Constants.FRAGMENT_PROP_TAG.equals(mTag)) {
            localPath = PathUtils.getLocalPropDir();
        } else if (Constants.FRAGMENT_FILTER_TAG.equals(mTag)) {
            localPath = PathUtils.getLocalFilterDir();
        }
        if (localPath == null) {
            return;
        }
        File file = new File(localPath);
        if (!file.exists()) {
            return;
        }
        String[] list = file.list();
        mEffectInfos = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            EffectInfo effectInfo = new EffectInfo();
            effectInfo.setName(getString(R.string.local_fx_start_name) + i);
            effectInfo.setInfoUrl(localPath + File.separator + list[i]);
            effectInfo.setCoverUrl("https://qasset.meishesdk.com/materialcenter/test/vlog/443DD887-FD90-4DDD-B769-3F8944589203cover.png");
            effectInfo.setDownload(true);
            mEffectInfos.add(effectInfo);
        }
        mCommonRecyclerViewAdapter.setData(mEffectInfos);
    }

    private void setCaptureFilterByPath(String effectPackageFilePath) {
        installEffect(effectPackageFilePath);
    }

    private void installEffect(String effectPackageFilePath) {
//        StringBuilder stringBuilder = new StringBuilder();
        NvAsset asset = NvAssetManager.sharedInstance().installAssetPackage(
                effectPackageFilePath,
                mAssetType, true);
//        NvsAssetPackageManager assetPackageManagerEffect = mNvsEffectSdkContext.getAssetPackageManager();
//        assetPackageManagerEffect.installAssetPackage(effectPackageFilePath, null,
//                mInstallType, false, stringBuilder);
        applyEffect(asset.uuid);
    }

    private void applyEffect(String effectId) {
        if (Constants.FRAGMENT_COMPONENT_CAPTION_TAG.equals(mTag)) {
            MSBus.getInstance().post(Constants.SubscribeType.SUB_APPLY_COMPONENT_CAPTION_TYPE, effectId);
        } else if (Constants.FRAGMENT_STICKER_TAG.equals(mTag)) {
            MSBus.getInstance().post(Constants.SubscribeType.SUB_APPLY_STICKER_TYPE, effectId);
        } else if (Constants.FRAGMENT_PROP_TAG.equals(mTag)) {
            MSBus.getInstance().post(Constants.SubscribeType.SUB_APPLY_PROP_TYPE, effectId);
        } else if (Constants.FRAGMENT_FILTER_TAG.equals(mTag)) {
            MSBus.getInstance().post(Constants.SubscribeType.SUB_APPLY_FILTER_TYPE, effectId);
        }
    }


}
