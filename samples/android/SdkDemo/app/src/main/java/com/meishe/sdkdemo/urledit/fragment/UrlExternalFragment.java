package com.meishe.sdkdemo.urledit.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meishe.base.model.BaseFragment;
import com.meishe.base.utils.KeyboardUtils;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.urledit.adapter.UrlExternalAdapter;
import com.meishe.sdkdemo.urledit.bean.UrlMaterialInfo;
import com.meishe.sdkdemo.urledit.inter.OnUrlChangeListener;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.third.adpater.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2024/12/2 13:45
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class UrlExternalFragment extends BaseFragment {
    private int mUrlType = Constants.URL_MATERIAL_VIDEO;
    private LinearLayout mUrlHandle;
    private TextView mUrlAdd;
    private TextView mUrlDelete;
    private UrlExternalAdapter mUrlExternalAdapter;

    private OnUrlChangeListener mOnUrlChangeListener;
    private boolean mIsMulti = true;
    private int mClickPosition = -1;

    public static UrlExternalFragment create(int urlType) {
        UrlExternalFragment externalFragment = new UrlExternalFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.URL_MATERIAL_TYPE, urlType);
        externalFragment.setArguments(bundle);
        return externalFragment;
    }

    public void setOnUrlChangeListener(OnUrlChangeListener mOnUrlChangeListener) {
        this.mOnUrlChangeListener = mOnUrlChangeListener;
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_url_external;
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void initView(View rootView) {
        Bundle bundle = getArguments();
        if (null != bundle) {
            mUrlType = bundle.getInt(Constants.URL_MATERIAL_TYPE);
        }
        List<UrlMaterialInfo> mUrlExternalInfos = new ArrayList<>();
        UrlMaterialInfo urlExternalInfo = new UrlMaterialInfo();
        mUrlExternalInfos.add(urlExternalInfo);
        RecyclerView mRvUrl = rootView.findViewById(R.id.rv_url);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRvUrl.setLayoutManager(linearLayoutManager);
        mUrlExternalAdapter = new UrlExternalAdapter(getContext());
        mUrlExternalAdapter.setUrlType(mUrlType);
        mRvUrl.setAdapter(mUrlExternalAdapter);
        mUrlExternalAdapter.setNewData(mUrlExternalInfos);
        mUrlHandle = rootView.findViewById(R.id.external_url_handle);
        mUrlAdd = rootView.findViewById(R.id.external_url_add);
        mUrlDelete = rootView.findViewById(R.id.external_url_delete);
        initListener();
    }

    @Override
    protected void initData() {

    }

    public void setSelectMulti(boolean isMulti) {
        mIsMulti = isMulti;
    }

    private void initListener() {
        mUrlExternalAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                UrlMaterialInfo urlExternalInfo = (UrlMaterialInfo) adapter.getData().get(position);
                if (view.getId() == R.id.url_input_clear) {
                    if (null == urlExternalInfo) {
                        return;
                    }
                    urlExternalInfo.setUrl("");
                    urlExternalInfo.setCoverUrl("");
                    adapter.notifyItemChanged(position);
                    if (null == mOnUrlChangeListener) {
                        return;
                    }
                    mOnUrlChangeListener.onUrlChange();
                    if (mUrlType == Constants.URL_MATERIAL_MUSIC) {
                        mOnUrlChangeListener.onUrlInfo(null);
                    }
                    return;
                }
                KeyboardUtils.hideSoftInput(getActivity());
                mOnUrlChangeListener.onEditVisibility(false);
                if (mUrlType == Constants.URL_MATERIAL_VIDEO) {
                    if (mClickPosition == position) {
                        mClickPosition = -1;
                        mUrlHandle.setVisibility(View.GONE);
                        mOnUrlChangeListener.onEditVisibility(true);
                        return;
                    }
                    if (mUrlHandle.getVisibility() != View.VISIBLE) {
                        mUrlHandle.setVisibility(View.VISIBLE);
                    }
                    mClickPosition = position;
                    mUrlDelete.setAlpha(position == 0 ? 0.5F : 1F);
                    mUrlDelete.setClickable(position != 0);
                    return;
                }
                mOnUrlChangeListener.onUrlInfo(urlExternalInfo);

            }
        });
        mUrlExternalAdapter.setOnUrlTextChangedListener(new UrlExternalAdapter.OnUrlTextChangedListener() {
            @Override
            public void onUrlTextChange() {
                if (null == mOnUrlChangeListener) {
                    return;
                }
                mOnUrlChangeListener.onUrlChange();
                mUrlHandle.setVisibility(View.GONE);
                if (mUrlType == Constants.URL_MATERIAL_MUSIC) {
                    mOnUrlChangeListener.onUrlInfo(null);
                }
            }
        });
        mUrlAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUrlHandle.setVisibility(View.GONE);
                UrlMaterialInfo urlExternalInfo = new UrlMaterialInfo();
                mUrlExternalAdapter.addData(urlExternalInfo);
                mOnUrlChangeListener.onEditVisibility(true);
            }
        });
        mUrlDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUrlHandle.setVisibility(View.GONE);
                mUrlExternalAdapter.remove(mClickPosition);
                if (null == mOnUrlChangeListener) {
                    return;
                }
                mOnUrlChangeListener.onUrlChange();
                mOnUrlChangeListener.onEditVisibility(true);
            }
        });
    }

    public void resetDataBySelected() {
        if (mIsMulti) {
            return;
        }
        List<UrlMaterialInfo> urlMaterialInfos = mUrlExternalAdapter.getData();
        if (urlMaterialInfos.isEmpty()) {
            return;
        }
        for (int i = 0; i < urlMaterialInfos.size(); i++) {
            UrlMaterialInfo materialInfo = urlMaterialInfos.get(i);
            if (null == materialInfo) {
                continue;
            }
            String contentUrl = materialInfo.getUrl();
            if (!TextUtils.isEmpty(contentUrl)) {
                materialInfo.setUrl("");
                mUrlExternalAdapter.notifyItemChanged(i);
            }
        }
    }

    public List<UrlMaterialInfo> getUrlData() {
        List<UrlMaterialInfo> urlData = new ArrayList<>();
        List<UrlMaterialInfo> infos = mUrlExternalAdapter.getData();
        if (infos.isEmpty()) {
            return urlData;
        }
        for (UrlMaterialInfo urlExternalInfo : infos) {
            String url = urlExternalInfo.getUrl();
            if (TextUtils.isEmpty(url)) {
                continue;
            }
            urlData.add(urlExternalInfo);
        }
        return urlData;
    }

}
