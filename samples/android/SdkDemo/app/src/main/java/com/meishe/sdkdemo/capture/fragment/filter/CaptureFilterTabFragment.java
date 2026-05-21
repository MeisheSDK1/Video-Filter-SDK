package com.meishe.sdkdemo.capture.fragment.filter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meishe.base.msbus.MSBus;
import com.meishe.base.msbus.MSSubscribe;
import com.meishe.base.utils.LogUtils;
import com.meishe.http.bean.BaseBean;
import com.meishe.net.custom.SimpleDownListener;
import com.meishe.net.model.Progress;
import com.meishe.sdkdemo.BR;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.capture.adapter.CommonLoadMoreAndRefreshAdapter;
import com.meishe.sdkdemo.capture.bean.EffectInfo;
import com.meishe.sdkdemo.capture.bean.KindInfo;
import com.meishe.sdkdemo.capture.fragment.BaseHorizontalLoadMoreAndRefreshFragment;
import com.meishe.sdkdemo.capture.viewmodel.FilterViewModel;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.asset.NvHttpFilePathUtil;
import com.meishe.third.adpater.BaseQuickAdapter;
import com.meishe.utils.PackageManagerUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

public class CaptureFilterTabFragment extends BaseHorizontalLoadMoreAndRefreshFragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    /**
     * 特效类型数据结构
     * Special effect type data structure
     */
    private KindInfo mKindInfo;
    private String mEffectPathDir;
    private String[] mSplit;
    private FilterViewModel mFilterViewModel;
    private List<EffectInfo> mData = new ArrayList<>();

    @Inject
    ViewModelProvider.Factory mViewModelProvider;
    private EffectInfo mDefaultInfo;
    private boolean isFirst;
    private boolean mCanLoadMore = true;
    private static String mSelectPackageUrl;
    private HashMap<String, String> assetsDownloadInfo = new HashMap<>();
    private boolean mEnableChangeItem = true;

    public CaptureFilterTabFragment() {
        // Required empty public constructor
    }

    public static CaptureFilterTabFragment newInstance(KindInfo mKindInfo) {
        CaptureFilterTabFragment fragment = new CaptureFilterTabFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, mKindInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mKindInfo = (KindInfo) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mEnableChangeItem = true;
        MSBus.getInstance().register(this);
    }


    @Override
    protected int initRootView() {
        return R.layout.fragment_capture_filter_item;
    }

    @Override
    protected void initArguments(Bundle arguments) {

    }

    @Override
    protected void initView() {
        mBinding.setVariable(BR.isLoading, true);
        mRecyclerView = findViewById(R.id.recyclerView);
        initRecyclerView(LinearLayoutManager.HORIZONTAL, R.layout.capture_filter_item_view, BR.filterInfo);
    }

    @Override
    protected void onLazyLoad() {
        initViewModel();
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initListener() {
        mCommonRecyclerViewAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (mCanLoadMore) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            mKindInfo.setPageNumber(mKindInfo.getPageNumber() + 1);
                            mFilterViewModel.requestFilterData(mKindInfo);
                        }
                    });
                }
            }
        });
        mCommonRecyclerViewAdapter.setOnItemClickListener(new CommonLoadMoreAndRefreshAdapter.OnItemClickListener<EffectInfo>() {
            @Override
            public void onItemClick(View view, int posotion, EffectInfo filterInfo) {
                if (!mEnableChangeItem) {
                    return;
                }
                if (filterInfo.isSelect()) {
                    return;
                }
                for (EffectInfo info : mData) {
                    info.setSelect(false);
                }
                filterInfo.setSelect(true);
                ///storage/emulated/0/Android/data/com.meishe.ms106sdkdemo/files/NvStreamingSdk/Asset/Filter/26073681-6655-4C77-9A49-3C00565C05AA.1.videofx
//                if (mDefaultInfo != null && filterInfo.getId().equals(mDefaultInfo.getId())) {
//                    applyEffect(filterInfo);
//                    return;
//                }
//                String packageUrl = filterInfo.getPackageUrl();
//                mEffectPathDir = PathUtils.getCaptureSDCardPathByType(NvAsset.ASSET_FILTER);
//                mSplit = packageUrl.split("/");
//                String effectPath = mEffectPathDir + File.separator + mSplit[mSplit.length - 1];
//                String effectId = PreferencesManager.get().getString(filterInfo.getId());
//                if (TextUtils.isEmpty(effectId)) {
                String effectPath = NvHttpFilePathUtil.getFilterPathOutOfPathSuffix(filterInfo, NvAsset.ASSET_FILTER);
                if (!new File(effectPath).exists()) {
                    downloadPackage(filterInfo);
                } else {
                    setCaptureFilterByPath(filterInfo, effectPath);
                }
            }
        });
    }

    private void initViewModel() {
        mFilterViewModel = ViewModelProviders.of(this, mViewModelProvider).get(FilterViewModel.class);
        mKindInfo.setPageNumber(1);
        mFilterViewModel.requestFilterData(mKindInfo);
        mFilterViewModel.getFilterLiveData().observeForever(new Observer<BaseBean<EffectInfo>>() {
            @Override
            public void onChanged(BaseBean<EffectInfo> filterInfos) {
                mBinding.setVariable(BR.isLoading, false);
                if (null == filterInfos) {
                    return;
                }
                updateFilterInfo(filterInfos.getElements());
            }
        });
    }

    public void setDefaultFilterInfo(EffectInfo effectInfo) {
        if (effectInfo == null) {
            return;
        }
        isFirst = true;
        mDefaultInfo = effectInfo;
        String id = effectInfo.getId();
        if (mData.size() == 0) {
            mData.add(effectInfo);
        } else {
            EffectInfo effectInfo1 = mData.get(0);
            if (!effectInfo1.getId().equals(id)) {
                mData.add(0, mDefaultInfo);
            }
        }
    }

    private void updateFilterInfo(List<EffectInfo> filterInfos) {
        boolean isLoadMore = mKindInfo.getPageNumber() > 1;
        boolean checkoutIsContainer = checkoutIsContainer(mData, filterInfos);
        if (!checkoutIsContainer) {
            mData.addAll(mData.size(), filterInfos);
        }
//        if (mDefaultInfo != null) {
//            for (int i = 1; i < mData.size(); i++) {
//                EffectInfo effectInfo = mData.get(i);
//                if (effectInfo.getId().equals(mDefaultInfo.getId())) {
//                    mData.remove(effectInfo);
//                }
//            }
//        }
        for (EffectInfo mDatum : mData) {
            mDatum.setSelect(TextUtils.equals(mSelectPackageUrl, mDatum.getPackageUrl()));
        }
        if (isLoadMore) {
            if (filterInfos.isEmpty() || filterInfos.size() < 20) {
                mCanLoadMore = false;
                mCommonRecyclerViewAdapter.loadMoreEnd(true);
            } else {
                mCanLoadMore = true;
                mCommonRecyclerViewAdapter.loadMoreComplete();
            }
            if (!checkoutIsContainer) {
                mCommonRecyclerViewAdapter.addData(new ArrayList<>(filterInfos));
            }
        } else {
            mCommonRecyclerViewAdapter.setNewData(new ArrayList<>(mData));
        }
        if (isFirst) {
            isFirst = false;
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    RecyclerView.ViewHolder viewHolderForAdapterPosition = mRecyclerView.findViewHolderForAdapterPosition(0);
                    if (null != viewHolderForAdapterPosition) {
                        viewHolderForAdapterPosition.itemView.performClick();
                    }
                }
            });

        }
    }


    private boolean checkoutIsContainer(List<EffectInfo> dataOrigin, List<EffectInfo> data) {
        for (EffectInfo datum : data) {
            for (EffectInfo effectInfo : dataOrigin) {
                if (TextUtils.isEmpty(effectInfo.getPackageUrl())) {
                    continue;
                }
                if (effectInfo.getPackageUrl().equals(datum.getPackageUrl())) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 下载特效包
     * download fx package
     *
     * @param filterInfo
     */
    private void downloadPackage(EffectInfo filterInfo) {
        if (null == filterInfo) {
            return;
        }
        String packageUrl = filterInfo.getZipUrl();
        if (TextUtils.isEmpty(packageUrl)) {
            return;
        }

        String effectPathDir = PathUtils.getCaptureSDCardPathByType(NvAsset.ASSET_FILTER);
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
                filterInfo.setDownload(true);
                if (null != file) {
                    String absolutePath = file.getAbsolutePath();
                    saveEffectInfo(filterInfo);
                    installEffect(filterInfo, absolutePath);
                }
            }
        });
    }

    private void saveEffectInfo(EffectInfo filterInfo) {
        String separator = ";";
        String info = "name:" + filterInfo.getDisplayNameZhCn() + separator +
                "coverUrl:" + filterInfo.getCoverUrl() + separator +
                "aspectRatio:" + String.valueOf(filterInfo.getSupportedAspectRatio()) + separator +
                "assetType:" + String.valueOf(filterInfo.getAssetType()) + separator +
                "isAdjusted:" + String.valueOf(filterInfo.getIsAdjusted());
        assetsDownloadInfo.put(filterInfo.getId(), info);
        NvAssetManager.sharedInstance().setAssetInfoToSharedPreferences(2, assetsDownloadInfo);
    }

    private void setCaptureFilterByPath(EffectInfo filterInfo, String effectPackageFilePath) {
        String filterId = filterInfo.getId();
        NvsAssetPackageManager assetPackageManager = mStreamingContext.getAssetPackageManager();
        int packageStatus = assetPackageManager.getAssetPackageStatus(filterId,
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX);
        if (packageStatus == NvsAssetPackageManager.ASSET_PACKAGE_STATUS_NOTINSTALLED) {
            installEffect(filterInfo, effectPackageFilePath);
        } else {
            applyEffect(filterInfo);
        }
    }

    private void applyEffect(EffectInfo filterInfo) {
        if (null == filterInfo) {
            return;
        }
        String effectId = filterInfo.getId();
        if (TextUtils.isEmpty(effectId)) {
            return;
        }
        MSBus.getInstance().post(Constants.SubscribeType.SUB_APPLY_FILTER_INFO_TYPE, filterInfo);
        mSelectPackageUrl = filterInfo.getPackageUrl();
    }

    /**
     * 安装特效包会进行美摄授权检测，一个特效包会对应一个授权lic文件，美摄sdk demo 使用的是全量授权，所以安装特效包的时候，传递特效包授权路径的参数缺省了
     * The installation of special effects package will be subject to the authorization detection of American photography.
     * A special effects package will correspond to an authorized LIC file. The American Photography SDK demo uses full authorization. Therefore, when installing special effects package,
     * the parameters of passing the authorization path of special effects package are default
     *
     * @param effectPackageFilePath
     */
    private void installEffect(EffectInfo filterInfo, String effectPackageFilePath) {
//        NvsAssetPackageManager assetPackageManager = mStreamingContext.getAssetPackageManager();
//        StringBuilder stringBuilder = new StringBuilder();
//        int i = assetPackageManager.installAssetPackage(effectPackageFilePath, null,
//                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX, true, stringBuilder);
//
//        NvsAssetPackageManager assetPackageManagerEffect = mNvsEffectSdkContext.getAssetPackageManager();
//        assetPackageManagerEffect.installAssetPackage(effectPackageFilePath, null,
//                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX, false, null);
//
//        LogUtils.d("installEffect code=" + i);
//        PreferencesManager.get().putString(stringBuilder.toString(), effectPackageFilePath);
        String packageId = PackageManagerUtil.installAssetPackage(effectPackageFilePath, null, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX);
        LogUtils.d("installEffect packageId=" + packageId);
        applyEffect(filterInfo);
    }

    @MSSubscribe(Constants.SubscribeType.SUB_UN_USE_FILTER_TYPE)
    private void unUseFilter() {
        for (EffectInfo info : mData) {
            info.setSelect(false);
        }
        MSBus.getInstance().post("hideFilterSeekView");
        MSBus.getInstance().post(Constants.SubscribeType.SUB_REMO_ALL_FILTER_TYPE);
    }

    @MSSubscribe(Constants.SubscribeType.SUB_REFRESH_DATA_TYPE)
    private void refreshData(String packageUrl) {
        mFilterViewModel.refreshData(mData, packageUrl);
    }

    @Override
    public void onPause() {
        super.onPause();
        MSBus.getInstance().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSelectPackageUrl = null;
    }

    public void enableChangedItem(boolean enable) {
        mEnableChangeItem = enable;
    }
}