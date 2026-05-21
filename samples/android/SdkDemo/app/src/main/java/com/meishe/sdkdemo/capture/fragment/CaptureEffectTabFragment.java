package com.meishe.sdkdemo.capture.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meishe.base.msbus.MSBus;
import com.meishe.base.msbus.MSSubscribe;
import com.meishe.base.utils.CommonUtils;
import com.meishe.mvvm.BaseListMvvmModel;
import com.meishe.mvvm.IBaseModelListener;
import com.meishe.mvvm.PagingResult;
import com.meishe.sdkdemo.BR;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.capture.adapter.CommonRecyclerViewAdapter;
import com.meishe.sdkdemo.capture.bean.CategoryInfo;
import com.meishe.sdkdemo.capture.bean.EffectInfo;
import com.meishe.sdkdemo.capture.viewmodel.CaptureDownloadModel;
import com.meishe.sdkdemo.capture.viewmodel.CaptureEffectTabViewModel;
import com.meishe.sdkdemo.capture.viewmodel.MainViewModelFactory;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.asset.NvHttpFilePathUtil;
import com.meishe.utils.PathNameUtil;
import com.meishe.utils.ToastUtil;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class CaptureEffectTabFragment extends BaseFragment implements IBaseModelListener<List<EffectInfo>> {


    public static final String TAG = "CaptureEffectTabFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private CategoryInfo mCategoryInfo;
    private String mEffectPathDir;
    private String[] mSplit;
    private int mAssetType;
    private int mInstallType;
    private String mTag;
    private CaptureEffectTabViewModel mCaptureEffectTabViewModel;
    private CaptureDownloadModel mCaptureDownloadModel;
    private SmartRefreshLayout mRefreshLayout;

    /**
     * 是否还有下一页
     * is has next page
     */
    private boolean mHasNext;
    private String mTitle;
    private List<EffectInfo> mData = new ArrayList<>();
    private boolean isFromUser = false;

    @Inject
    ViewModelProvider.Factory mViewModelProvider;

    private Observer<String> mObserver;
    private EffectInfo mCurrentEffectInfo;

    public CaptureEffectTabFragment() {
        // Required empty public constructor
    }

    public static CaptureEffectTabFragment newInstance(CategoryInfo categoryInfo, String tag, String title) {
        CaptureEffectTabFragment fragment = new CaptureEffectTabFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, categoryInfo);
        args.putString(ARG_PARAM2, tag);
        args.putString(ARG_PARAM3, title);
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
        MSBus.getInstance().register(this);
        if (getArguments() != null) {
            mCategoryInfo = (CategoryInfo) getArguments().getSerializable(ARG_PARAM1);
            mTag = (String) getArguments().getSerializable(ARG_PARAM2);
            mTitle = (String) getArguments().getSerializable(ARG_PARAM3);
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
        }

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        mRecyclerView = findViewById(R.id.recyclerView);
        mRefreshLayout = findViewById(R.id.refreshLayout);
//        if (Constants.FRAGMENT_PROP_TAG.equals(mTag)) {
//            initRecyclerViewGrid(4, R.layout.capture_effect_tab_view, BR.filterInfo);
//        }else{
//            initRecyclerViewGrid(5, R.layout.capture_effect_tab_view, BR.filterInfo);
//        }
        initRecyclerViewGrid(5, R.layout.capture_effect_tab_view, BR.filterInfo);
        initViewModel();
    }

    @Override
    protected void onLazyLoad() {
    }


    @Override
    protected void initData() {

    }

    private void initViewModel() {
        mCategoryInfo.setAssetType(mAssetType);
        mCaptureDownloadModel = ViewModelProviders.of(this, mViewModelProvider).get(CaptureDownloadModel.class);
        mCaptureEffectTabViewModel = new ViewModelProvider(this, new MainViewModelFactory(mCategoryInfo)).
                get(CaptureEffectTabViewModel.class);
        mCaptureEffectTabViewModel.register(this);
        mCaptureEffectTabViewModel.refresh();
        mBinding.setVariable(BR.isLoading, true);
        mObserver = new Observer<String>() {
            @Override
            public void onChanged(String path) {
                if (isFromUser) {
                    applyEffect(installEffect(path));
                }
            }
        };
        mCaptureDownloadModel.getFilePath().observeForever(mObserver);
    }

    private TextView mSelectTextView;
    private TextView mPreTextView;

    @Override
    public void initListener() {
        mCommonRecyclerViewAdapter.setOnItemClickListener(new CommonRecyclerViewAdapter.
                OnItemClickListener<EffectInfo>() {
            @Override
            public void onItemClick(View view, int posotion, EffectInfo effectInfo) {
                MSBus.getInstance().post(Constants.SubscribeType.SUB_UN_SELECT_ITEM_TYPE);
                effectInfo.setSelect(true);
                mCurrentEffectInfo = effectInfo;
                String packageUrl = effectInfo.getZipUrl();
                mEffectPathDir = PathUtils.getSDCardPathByType(mAssetType);
                mSplit = packageUrl.split("/");
                String effectPath = mEffectPathDir + File.separator + mSplit[mSplit.length - 1];
//                String effectId = PreferencesManager.get().getString(effectInfo.getId());
//                if (TextUtils.isEmpty(effectId)) {
                String effectUnzipPath = PathNameUtil.getOutOfPathSuffixWithOutPoint(effectPath);
                if (!new File(effectUnzipPath).exists()) {
                    effectInfo.setAssetType(mAssetType);
                    mCaptureDownloadModel.downloadPackage(effectInfo);
                } else {
                    applyEffect(installEffect(effectPath));
                }
                if (mPreTextView != null) {
                    mPreTextView.setSelected(false);
                }
                mSelectTextView = view.findViewById(R.id.tv_title);
                mSelectTextView.setFocusable(true);
                mSelectTextView.setFocusableInTouchMode(true);
                mSelectTextView.setSelected(true);

                mPreTextView = mSelectTextView;


            }
        });

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mBinding.setVariable(BR.isLoading, false);
                mCaptureEffectTabViewModel.refresh();
            }
        });

        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mHasNext) {
                    mCaptureEffectTabViewModel.loadNextPage();
                } else {
                    // 将不会再次触发加载更多事件
//                    Load more events will not be triggered again
                    refreshLayout.finishLoadMoreWithNoMoreData();
                }

            }
        });

    }

    private void applyEffect(String effectId) {
        if (Constants.FRAGMENT_COMPONENT_CAPTION_TAG.equals(mTag)) {
            MSBus.getInstance().post(Constants.SubscribeType.SUB_APPLY_COMPONENT_CAPTION_TYPE, effectId);
        } else if (Constants.FRAGMENT_STICKER_TAG.equals(mTag)) {
            MSBus.getInstance().post(Constants.SubscribeType.SUB_APPLY_STICKER_TYPE, effectId);
        } else if (Constants.FRAGMENT_PROP_TAG.equals(mTag)) {
            MSBus.getInstance().post(Constants.SubscribeType.SUB_APPLY_PROP_TYPE, effectId, mCurrentEffectInfo);
        }
    }

    /**
     * 安装特效包会进行美摄授权检测，一个特效包会对应一个授权lic文件，美摄sdk demo 使用的是全量授权，所以安装特效包的时候，传递特效包授权路径的参数缺省了
     * The installation of special effects package will be subject to the authorization detection of American photography.
     * A special effects package will correspond to an authorized LIC file. The American Photography SDK demo uses full authorization. Therefore, when installing special effects package,
     * the parameters of passing the authorization path of special effects package are default
     *
     * @param effectPackageFilePath
     */
    private String installEffect(String effectPackageFilePath) {
        NvAsset asset = NvAssetManager.sharedInstance().installAssetPackage(
                effectPackageFilePath,
                mAssetType, true);
//        StringBuilder stringBuilder = new StringBuilder();
//        NvsAssetPackageManager assetPackageManagerEffect = mNvsEffectSdkContext.getAssetPackageManager();
//        assetPackageManagerEffect.installAssetPackage(effectPackageFilePath, null,
//                mInstallType, false, stringBuilder);
//        PreferencesManager.get().putString(stringBuilder.toString(), effectPackageFilePath);
        return asset.uuid;
    }


    @MSSubscribe(Constants.SubscribeType.SUB_UN_SELECT_ITEM_TYPE)
    private void unSelectAll() {
        mCurrentEffectInfo = null;
        for (EffectInfo info : mData) {
            info.setSelect(false);
        }

    }

    @Override
    protected void selectEffectById(String id) {
        super.selectEffectById(id);
        if (TextUtils.isEmpty(id)) {
            return;
        }
        if (CommonUtils.isEmpty(mData)) {
            return;
        }
        for (EffectInfo info : mData) {
            info.setSelect(TextUtils.equals(id, info.getId()));
        }

    }

    @MSSubscribe(Constants.SubscribeType.SUB_REFRESH_DATA_TYPE)
    private void refreshData(String packageUrl) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NotNull ObservableEmitter<Object> emitter) throws Exception {
                if (mData != null) {
                    for (EffectInfo filterInfo : mData) {
                        filterInfo.setName(filterInfo.getDisplayName());
//                        String effectId = PreferencesManager.get().getString(filterInfo.getId());
//                        if (!TextUtils.isEmpty(effectId)) {
//                            filterInfo.setDownload(true);
//                        }
                        filterInfo.setDownload(NvHttpFilePathUtil.CheckIsDownload(filterInfo, mAssetType));
                        filterInfo.setSelect(false);
                        if (filterInfo.getZipUrl().equals(packageUrl)) {
                            filterInfo.setSelect(true);
                        }
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        isFromUser = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isFromUser = false;
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                isFromUser = true;
            }
        }, 500);
    }


    @Override
    public void onLoadSuccess(BaseListMvvmModel model, List<EffectInfo> data, PagingResult... result) {
        if (data != null) {
            if (result != null && result.length > 0 && result[0].isFirstPage) {
                mData.clear();
            }
            mHasNext = result[0].hasNextPage;
            if (!checkoutIsContainer(mData, data)) {
                mData.addAll(data);
            }
            mBinding.setVariable(BR.isLoading, false);
            mCommonRecyclerViewAdapter.setData(mData);
        }
        mRefreshLayout.setEnableLoadMore(true);
        mRefreshLayout.finishLoadMore();
        mRefreshLayout.finishRefresh();
    }

    private boolean checkoutIsContainer(List<EffectInfo> dataOrigin, List<EffectInfo> data) {
        for (EffectInfo datum : data) {
            for (EffectInfo effectInfo : dataOrigin) {
                if (effectInfo.getPackageUrl().equals(datum.getPackageUrl())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onLoadFail(BaseListMvvmModel model, String message, PagingResult... result) {
        ToastUtil.showToast(mContext, message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MSBus.getInstance().unregister(this);
    }
}