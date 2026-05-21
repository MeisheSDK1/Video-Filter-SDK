package com.meishe.sdkdemo.urledit.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.meishe.base.model.BaseMvpFragment;
import com.meishe.base.utils.ClipboardUtils;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.cutsame.fragment.presenter.TemplatePresenter;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.urledit.adapter.UrlMaterialAdapter;
import com.meishe.sdkdemo.urledit.adapter.UrlMaterialMusicAdapter;
import com.meishe.sdkdemo.urledit.adapter.UrlMaterialVideoAdapter;
import com.meishe.sdkdemo.urledit.bean.UrlMaterialInfo;
import com.meishe.sdkdemo.urledit.inter.OnUrlChangeListener;
import com.meishe.sdkdemo.urledit.iview.UrlMaterialPresenter;
import com.meishe.sdkdemo.urledit.iview.UrlMaterialView;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.ToastUtil;
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
public class UrlMaterialFragment extends BaseMvpFragment<UrlMaterialPresenter> implements UrlMaterialView {
    private SwipeRefreshLayout mSrlRefresh;
    private TextView mUrlSelect;
    private RecyclerView mRvUrlList;
    private View mTvNoDataView;
    private UrlMaterialAdapter mUrlMaterialAdapter;
    private OnUrlChangeListener mOnUrlChangeListener;
    private int mUrlType = Constants.URL_MATERIAL_VIDEO;
    private boolean mIsMulti = true;

    public static UrlMaterialFragment create(int urlType) {
        UrlMaterialFragment materialFragment = new UrlMaterialFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.URL_MATERIAL_TYPE, urlType);
        materialFragment.setArguments(bundle);
        return materialFragment;
    }

    public void setOnUrlChangeListener(OnUrlChangeListener mOnUrlChangeListener) {
        this.mOnUrlChangeListener = mOnUrlChangeListener;
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_url_material;
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
        mSrlRefresh = rootView.findViewById(R.id.srl_url_refresh);
        mUrlSelect = rootView.findViewById(R.id.url_select_material);
        updateSelectMaterialView(false);
        mRvUrlList = rootView.findViewById(R.id.rv_url_list);
        mTvNoDataView = rootView.findViewById(R.id.tv_url_no_date);
        if (mUrlType == Constants.URL_MATERIAL_VIDEO) {
            mUrlSelect.setVisibility(View.VISIBLE);
            final int decoration = (int) getResources().getDimension(com.meishe.cutsame.R.dimen.dp_px_30);
            int screenWidth = ScreenUtils.getScreenWidth();
            if (mRvUrlList.getLayoutParams() != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mRvUrlList.getLayoutParams();
                layoutParams.width = screenWidth - 2 * decoration;
                layoutParams.leftMargin = (int) (decoration / 2f);
                mRvUrlList.setLayoutParams(layoutParams);
            }
            mRvUrlList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            mUrlMaterialAdapter = new UrlMaterialVideoAdapter((ScreenUtils.getScreenWidth() - 4 * decoration) / 2);
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mRvUrlList.setLayoutManager(linearLayoutManager);
            mUrlMaterialAdapter = new UrlMaterialMusicAdapter(0);

        }
        mUrlMaterialAdapter.setHasStableIds(true);
        mRvUrlList.setAdapter(mUrlMaterialAdapter);
        initListener();
    }

    public void setSelectMulti(boolean isMulti) {
        mIsMulti = isMulti;
    }

    private void initListener() {
        mSrlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mPresenter != null) {
                    initData();
                }
            }
        });
        mUrlMaterialAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mUrlType == Constants.URL_MATERIAL_VIDEO && !mUrlMaterialAdapter.isShowSelected()) {
                    return;
                }
                List<UrlMaterialInfo> urlMaterialInfos = mUrlMaterialAdapter.getData();
                if (urlMaterialInfos.isEmpty()) {
                    return;
                }
                UrlMaterialInfo urlMaterialInfo = urlMaterialInfos.get(position);
                if (null == urlMaterialInfo) {
                    return;
                }
                if (mUrlType == Constants.URL_MATERIAL_VIDEO) {
                    resetDataBySelected(position);
                    urlMaterialInfo.setSelected(!urlMaterialInfo.isSelected());
                    mUrlMaterialAdapter.notifyItemChanged(position);
                }
                if (null != mOnUrlChangeListener) {
                    mOnUrlChangeListener.onUrlChange();
                    mOnUrlChangeListener.onUrlInfo(urlMaterialInfo);
                }
            }
        });
        mUrlMaterialAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                List<UrlMaterialInfo> urlMaterialInfos = mUrlMaterialAdapter.getData();
                if (urlMaterialInfos.isEmpty()) {
                    return;
                }
                UrlMaterialInfo urlMaterialInfo = urlMaterialInfos.get(position);
                if (null == urlMaterialInfo) {
                    return;
                }
                String url = urlMaterialInfo.getUrl();
                if (TextUtils.isEmpty(url)) {
                    return;
                }
                ClipboardUtils.copyText(url);
                ToastUtil.showToast(getContext(), R.string.url_copy_tips);
            }
        });
        mUrlMaterialAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (mPresenter != null && !mPresenter.getMoreUrlMaterial(getContext(), getMaterialType())) {
                    mUrlMaterialAdapter.loadMoreEnd();
                }
            }
        }, mRvUrlList);
        mUrlSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSelected = (boolean) mUrlSelect.getTag();
                updateSelectMaterialView(isSelected);
                mUrlMaterialAdapter.setShowSelected(isSelected);
            }
        });
    }

    @Override
    protected void initData() {
        mPresenter.getUrlMaterialList(getContext(), getMaterialType(), 1);
    }

    public void resetDataBySelected() {
        if (mIsMulti) {
            return;
        }
        List<UrlMaterialInfo> urlMaterialInfos = mUrlMaterialAdapter.getData();
        if (urlMaterialInfos.isEmpty()) {
            return;
        }
        for (int i = 0; i < urlMaterialInfos.size(); i++) {
            UrlMaterialInfo materialInfo = urlMaterialInfos.get(i);
            if (null == materialInfo) {
                continue;
            }
            if (materialInfo.isSelected()) {
                materialInfo.setSelected(false);
                mUrlMaterialAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    private void resetDataBySelected(int position) {
        if (mIsMulti) {
            return;
        }
        List<UrlMaterialInfo> urlMaterialInfos = mUrlMaterialAdapter.getData();
        if (urlMaterialInfos.isEmpty()) {
            return;
        }
        for (int i = 0; i < urlMaterialInfos.size(); i++) {
            UrlMaterialInfo materialInfo = urlMaterialInfos.get(i);
            if (null == materialInfo) {
                continue;
            }
            if (materialInfo.isSelected() && (i != position)) {
                materialInfo.setSelected(false);
                mUrlMaterialAdapter.notifyItemChanged(i);
            }
        }
    }

    @Override
    public void onUrlMaterialListBack(List<UrlMaterialInfo> urlMaterialList) {
        if (mSrlRefresh.isRefreshing()) {
            mSrlRefresh.setRefreshing(false);
        }
        mTvNoDataView.setVisibility(View.INVISIBLE);
        if (urlMaterialList != null && !urlMaterialList.isEmpty()) {
            mUrlMaterialAdapter.setNewData(urlMaterialList);
            if (urlMaterialList.size() < TemplatePresenter.PAGE_NUM) {
                mUrlMaterialAdapter.loadMoreEnd(true);
            }
        } else {
            setEmptyView();
        }
        if (null != mOnUrlChangeListener) {
            mOnUrlChangeListener.onUrlChange();
        }
    }

    @Override
    public void onMoreUrlMaterialBack(List<UrlMaterialInfo> urlMaterialList) {
        if (urlMaterialList == null || urlMaterialList.isEmpty()) {
            mUrlMaterialAdapter.loadMoreEnd();
            setEmptyView();
        } else {
            mUrlMaterialAdapter.loadMoreComplete();
            mUrlMaterialAdapter.addData(urlMaterialList);
        }
    }

    public List<UrlMaterialInfo> getUrlData() {
        List<UrlMaterialInfo> urlData = new ArrayList<>();
        List<UrlMaterialInfo> infos = mUrlMaterialAdapter.getData();
        if (infos.isEmpty()) {
            return urlData;
        }
        for (UrlMaterialInfo urlExternalInfo : infos) {
            if (!urlExternalInfo.isSelected()) {
                continue;
            }
            String url = urlExternalInfo.getUrl();
            if (TextUtils.isEmpty(url)) {
                continue;
            }
            urlData.add(urlExternalInfo);
            if (mUrlType == Constants.URL_MATERIAL_MUSIC) {
//                MusicInfo musicInfo = mTrimMusicView.getMusicInfo();
//                if (null == musicInfo || !TextUtils.equals(musicInfo.getFilePath(), url)) {
//                    continue;
//                }
//                urlExternalInfo.setTrimIn(musicInfo.getTrimIn());
//                urlExternalInfo.setTrimOut(musicInfo.getTrimOut());
            }

        }
        return urlData;
    }

    private int getMaterialType() {
        return (mUrlType == Constants.URL_MATERIAL_VIDEO) ? 1 : 2;
    }

    private void setEmptyView() {
        if ((mUrlMaterialAdapter == null) || (mUrlMaterialAdapter.getData().isEmpty())) {
            if (mTvNoDataView != null) {
                mTvNoDataView.setVisibility(View.VISIBLE);
            }
        } else {
            if (mTvNoDataView != null) {
                mTvNoDataView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private void updateSelectMaterialView(boolean isSelected) {
        if (null == mUrlSelect) {
            return;
        }
        if (!isSelected) {
            mUrlSelect.setTextColor(getResources().getColor(R.color.white));
            mUrlSelect.setText(R.string.url_select_material);
            mUrlSelect.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.icon_url_select, 0, 0, 0);
            mUrlSelect.setTag(true);
        } else {
            mUrlSelect.setTextColor(getResources().getColor(R.color.color_63ABFF));
            mUrlSelect.setText(R.string.cancel);
            mUrlSelect.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            mUrlSelect.setTag(false);
        }
    }
}
