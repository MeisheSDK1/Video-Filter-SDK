package com.meishe.sdkdemo.capture.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.base.msbus.MSBus;
import com.meishe.base.msbus.MSSubscribe;
import com.meishe.base.utils.LogUtils;
import com.meishe.http.AssetType;
import com.meishe.http.bean.BaseBean;
import com.meishe.net.custom.BaseResponse;
import com.meishe.net.custom.RequestCallback;
import com.meishe.net.custom.SimpleDownListener;
import com.meishe.net.model.Progress;
import com.meishe.sdkdemo.BR;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.capture.adapter.CommonRecyclerViewAdapter;
import com.meishe.sdkdemo.capture.bean.EffectInfo;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.KeyBoardUtil;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvHttpFilePathUtil;
import com.meishe.utils.PackageManagerUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchEffectTabFragment extends BaseFragment {

    private static final String PARAM_REQUEST_TYPE = "param_request_type";
    private EditText mEtSearch;
    private TextView mTvCancel;
    private TextView mSearchEmpty;
    private boolean searchContentEmpty;
    private boolean isSearching;

    private String mRequestType;

    private int mEffectInstallType;

    private int mAssetType;
    private String mTag;
    private String mEffectPathDir;
    private String[] mSplit;
    private String mPackageUrl;
    private List<EffectInfo> mData = new ArrayList<>();

    public SearchEffectTabFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public static SearchEffectTabFragment newInstance(String requestType) {
        SearchEffectTabFragment fragment = new SearchEffectTabFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_REQUEST_TYPE, requestType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (null != arguments) {
            mTag = arguments.getString(PARAM_REQUEST_TYPE);
        }

        if (Constants.FRAGMENT_STICKER_TAG.equals(mTag)) {
            mRequestType = AssetType.STICKER_ALL.getType();
            mEffectInstallType = NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ANIMATEDSTICKER;
            mAssetType = NvAsset.ASSET_ANIMATED_STICKER;
        } else if (Constants.FRAGMENT_COMPONENT_CAPTION_TAG.equals(mTag)) {
            mRequestType = AssetType.COM_CAPTION_ALL.getType();
            mEffectInstallType = NvsAssetPackageManager.ASSET_PACKAGE_TYPE_COMPOUND_CAPTION;
            mAssetType = NvAsset.ASSET_COMPOUND_CAPTION;
        } else if (Constants.FRAGMENT_PROP_TAG.equals(mTag)) {
            mRequestType = AssetType.AR_SCENE_ALL.getType();
            mEffectInstallType = NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ARSCENE;
            mAssetType = NvAsset.ASSET_ARSCENE_FACE;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mEtSearch = view.findViewById(R.id.et_search);
        mTvCancel = view.findViewById(R.id.tv_cancel);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mSearchEmpty = view.findViewById(R.id.search_empty);
        initRecyclerViewGrid(5, R.layout.capture_search_effect_tab_view, BR.searchEffectInfo);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStreamingContext = NvsStreamingContext.getInstance();
    }

    @Override
    protected int initRootView() {
        return 0;
    }

    @Override
    protected void initArguments(Bundle arguments) {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void initListener() {

        mCommonRecyclerViewAdapter.setOnItemClickListener(new CommonRecyclerViewAdapter.
                OnItemClickListener<EffectInfo>() {
            @Override
            public void onItemClick(View view, int posotion, EffectInfo filterInfo) {
//                 if (Constants.FRAGMENT_PROP_TAG.equals(mTag)){
//                     for (FilterInfo info : mData) {
//                         info.setSelect(false);
//                     }
//                     filterInfo.setSelect(true);
//                }
                for (EffectInfo info : mData) {
                    info.setSelect(false);
                }
                filterInfo.setSelect(true);
                ///storage/emulated/0/Android/data/com.meishe.ms106sdkdemo/files/NvStreamingSdk/Asset/Filter/26073681-6655-4C77-9A49-3C00565C05AA.1.videofx
//                mPackageUrl = filterInfo.getPackageUrl();
//                mEffectPathDir = PathUtils.getSDCardPathByType(mAssetType);
//                mSplit = mPackageUrl.split("/");
//                String effectPath = mEffectPathDir + File.separator + mSplit[mSplit.length - 1];
//
//                String effectId = PreferencesManager.get().getString(filterInfo.getId());
//                if (TextUtils.isEmpty(effectId)) {
                mPackageUrl = filterInfo.getZipUrl();
                String effectPath = getFilterPath(filterInfo, mAssetType);
                if (!new File(effectPath).exists()) {
                    downloadPackage(filterInfo);
                } else {
                    setCaptureFilterByPath(filterInfo.getId(), effectPath);
                }
            }
        });

        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //关闭软键盘
                    KeyBoardUtil.hideSoftKeyBroad(mEtSearch, mEtSearch.getContext());
                    //do something
                    //doSearch();
                    String searchContent = mEtSearch.getText().toString().trim();
                    if (!TextUtils.isEmpty(searchContent)) {
                        searchDataByContent(searchContent);
                        isSearching = true;
                    }
                    return true;
                }
                return false;
            }
        });

        mEtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mTvCancel.setVisibility(View.VISIBLE);
                } else {
                    mTvCancel.setVisibility(View.GONE);
                }
            }
        });

        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchContent = s.toString().trim();
                if (TextUtils.isEmpty(searchContent)) {
                    mEtSearch.setCompoundDrawables(null, null, null, null);
                    searchContentEmpty = true;
                    isSearching = false;
                } else {
                    if (searchContentEmpty) {
                        Drawable drawable = getResources().getDrawable(
                                R.mipmap.filter_search_close);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                                drawable.getMinimumHeight());
                        mEtSearch.setCompoundDrawables(null, null, drawable, null);
                    }
                    mTvCancel.setVisibility(View.VISIBLE);
                    searchContentEmpty = false;

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEtSearch.setCursorVisible(true);
                Drawable drawable = mEtSearch.getCompoundDrawables()[2];
                if (null == drawable) {
                    return false;
                }
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }

                if (event.getX() > mEtSearch.getWidth() - mEtSearch.getPaddingRight() - drawable.getIntrinsicWidth()) {
                    mEtSearch.setText("");
                }
                return false;
            }
        });
    }


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
                    installEffect(absolutePath);
                }
            }
        });
    }

    private void installEffect(String effectPackageFilePath) {
//        NvsAssetPackageManager assetPackageManager = mStreamingContext.getAssetPackageManager();
//        StringBuilder stringBuilder = new StringBuilder();
//        int i = assetPackageManager.installAssetPackage(effectPackageFilePath, null,
//                mEffectInstallType, true, stringBuilder);
//        LogUtils.d("installEffect code=" + i);
//        PreferencesManager.get().putString(stringBuilder.toString(), effectPackageFilePath);
//        applyEffect(stringBuilder.toString());
        String packageId = PackageManagerUtil.installAssetPackage(effectPackageFilePath, null, mEffectInstallType);
        LogUtils.d("installEffect packageId=" + packageId);
        applyEffect(packageId);
    }

    private void refreshOtherFragment(String packageUrl) {
        if (TextUtils.isEmpty(packageUrl)) {
            return;
        }
        MSBus.getInstance().post(Constants.SubscribeType.SUB_REFRESH_DATA_TYPE, packageUrl);
    }


    private void applyEffect(String effectId) {
        refreshOtherFragment(mPackageUrl);
        if (Constants.FRAGMENT_COMPONENT_CAPTION_TAG.equals(mTag)) {
            MSBus.getInstance().post(Constants.SubscribeType.SUB_APPLY_COMPONENT_CAPTION_TYPE, effectId);
        } else if (Constants.FRAGMENT_STICKER_TAG.equals(mTag)) {
            MSBus.getInstance().post(Constants.SubscribeType.SUB_APPLY_STICKER_TYPE, effectId);
        } else if (Constants.FRAGMENT_PROP_TAG.equals(mTag)) {
            MSBus.getInstance().post(Constants.SubscribeType.SUB_APPLY_PROP_TYPE, effectId);
        }
    }

    private void setCaptureFilterByPath(String filterId, String effectPackageFilePath) {
        NvsAssetPackageManager assetPackageManager = mStreamingContext.getAssetPackageManager();
        int packageStatus = assetPackageManager.getAssetPackageStatus(filterId,
                mEffectInstallType);
        if (packageStatus == NvsAssetPackageManager.ASSET_PACKAGE_STATUS_NOTINSTALLED) {
            installEffect(effectPackageFilePath);
        } else {
            applyEffect(filterId);
        }
    }

    private void searchDataByContent(String searchContent) {
        getFilterData(searchContent);
    }

    private void getFilterData(String searchContent) {
        HttpManager.getMaterialList(null, mRequestType, searchContent, MSApplication.getSdkVersion(),
                1, Constants.PAGE_SIZE, new RequestCallback<BaseBean<EffectInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<BaseBean<EffectInfo>> response) {
                        if (response.getCode() == 1) {
                            mData.clear();
                            List<EffectInfo> elements = response.getData().getElements();
                            if (elements != null) {
                                for (EffectInfo filterInfo : elements) {
                                    if (MSApplication.isZh()) {
                                        filterInfo.setName(filterInfo.getDisplayNameZhCn());
                                    } else {
                                        filterInfo.setName(filterInfo.getDisplayName());
                                    }
//                                    String effectId = PreferencesManager.get().getString(filterInfo.getId());
//                                    boolean isDownload = false;
//                                    if (!TextUtils.isEmpty(effectId)) {
//                                        File file = new File(effectId);
//                                        if (file.exists()) {
//                                            isDownload = true;
//                                        }
//                                    }
//                                    filterInfo.setDownload(isDownload);
                                    filterInfo.setDownload(NvHttpFilePathUtil.CheckIsDownload(filterInfo, mAssetType));
                                }
                                mData.addAll(elements);
                                mSearchEmpty.setVisibility(mData.isEmpty() ? View.VISIBLE : View.GONE);
                                mSearchEmpty.setText(mData.isEmpty() ?
                                        String.format(getContext().getString(R.string.asset_search_empty), searchContent) : "");
                                mCommonRecyclerViewAdapter.setData(mData);
                            }
                        }

                    }

                    @Override
                    public void onError(BaseResponse<BaseBean<EffectInfo>> response) {
                    }
                });
    }

    @MSSubscribe(Constants.SubscribeType.SUB_UN_SELECT_ITEM_TYPE)
    private void unSelectAll() {
        for (EffectInfo info : mData) {
            info.setSelect(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MSBus.getInstance().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MSBus.getInstance().register(this);
    }
}