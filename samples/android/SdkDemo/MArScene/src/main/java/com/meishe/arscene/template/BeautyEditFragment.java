package com.meishe.arscene.template;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meishe.arscene.Constants;
import com.meishe.arscene.R;
import com.meishe.arscene.adapter.BaseBeautyAdapter;
import com.meishe.arscene.adapter.BeautyAdapter;
import com.meishe.arscene.adapter.BeautyNodeAdapter;
import com.meishe.arscene.bean.CompoundFxInfo;
import com.meishe.arscene.bean.FxParams;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.arscene.iview.BeautyEditView;
import com.meishe.arscene.presenter.BeautyEditPresenter;
import com.meishe.base.model.BaseMvpFragment;

import java.util.ArrayList;
import java.util.List;

import static com.meishe.arscene.bean.BaseFxInfo.TYPE_PLACE_HOLDER;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/2/9 17:59
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class BeautyEditFragment extends BaseMvpFragment<BeautyEditPresenter> implements BeautyEditView {
    /**
     * 美肤，美型，微整形，调节数据
     * Beauty skin, beauty shape, micro plastic data
     */
    private List<IFxInfo> mBeautyData = new ArrayList<>();
    private int mBeautyType = FxParams.BEAUTY_SKIN;
    /**
     * 是否支持去油光
     * Whether it supports degreasing
     */
    private boolean mIsSupportMatte;
    private RecyclerView mRvBeauty;
    private BeautyAdapter mBeautyAdapter;

    private OnBeautyEditListener mBeautyEditListener;

    public static BeautyEditFragment newInstance(int type, boolean mIsSupportMatte) {
        BeautyEditFragment fragment = new BeautyEditFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.BEAUTYTYPE, type);
        args.putBoolean(Constants.SUPPORT_MATTE, mIsSupportMatte);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_beauty;
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void initView(View rootView) {
        if (null != getArguments()) {
            mBeautyType = getArguments().getInt(Constants.BEAUTYTYPE);
            mIsSupportMatte = getArguments().getBoolean(Constants.SUPPORT_MATTE);
        }
        mRvBeauty = rootView.findViewById(R.id.rv_beauty);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvBeauty.setLayoutManager(manager);
        mBeautyAdapter = new BeautyAdapter();
        mBeautyAdapter.setSkinMode(BaseBeautyAdapter.SKIN_MODE_BLACK);
        if (mBeautyType == FxParams.BEAUTY_CONTOURING) {
            mBeautyAdapter.setSelectStyle(BaseBeautyAdapter.SLELCT_STYLE_DEFAULT);
        }
        mBeautyAdapter.setEnable(true);
        mRvBeauty.setAdapter(mBeautyAdapter);
        initListener();
    }

    @Override
    protected void initData() {
        //mPresenter.getBeautyData(getContext(), mBeautyType, mBeautyData, mIsSupportMatte);
        mBeautyAdapter.addData(mBeautyData);
    }

    public void refreshViewData(List<IFxInfo> iFxInfos) {
        if (null == iFxInfos) {
            return;
        }
        if (null == mBeautyAdapter) {
            mBeautyData = iFxInfos;
            return;
        }
        mBeautyAdapter.setAllExpanded(false);
        mBeautyAdapter.setNewData(iFxInfos);
        setSelectPosition(-1, -1);
        mRvBeauty.scrollToPosition(0);
    }

    private void initListener() {
        mBeautyAdapter.setOnItemClickListener((adapter, view, position) -> {
            IFxInfo info = (IFxInfo) adapter.getData().get(position);
            if (null == info) {
                return;
            }
            if (TextUtils.equals(info.getType(), TYPE_PLACE_HOLDER)) {
                return;
            }
            mBeautyAdapter.setSelectPosition(position, false);
            boolean isHasChild = false;
            List<IFxInfo> nodeFxInfo = info.getFxNodes();
            if ((null != nodeFxInfo) && !nodeFxInfo.isEmpty()) {
                isHasChild = true;
                mBeautyAdapter.setExpandedByFxInfo(info);
            }
            mBeautyAdapter.setAllExpandedExceptSelect(false);
            if (null != mBeautyEditListener) {
                mBeautyEditListener.onBeautyEdit(mBeautyType, (isHasChild) ? null : info);
            }
        });
        mBeautyAdapter.setOnNodeItemClickListener((adapter, parentPosition, position) -> {
            IFxInfo info = (IFxInfo) adapter.getData().get(position);
            if (null == info) {
                return;
            }
            if (null != mBeautyEditListener) {
                mBeautyEditListener.onBeautyEdit(mBeautyType, info);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void resetFx() {
        if (null == mBeautyAdapter) {
            return;
        }
        List<IFxInfo> data = mBeautyAdapter.getData();
        if (data.isEmpty()) {
            return;
        }
        if (null == mBeautyEditListener) {
            return;
        }
        for (IFxInfo info : data) {
            List<IFxInfo> nodeFxInfos = info.getFxNodes();
            if ((null != nodeFxInfos) && !nodeFxInfos.isEmpty()) {
                for (IFxInfo nodeInfo : nodeFxInfos) {
                    if (null == nodeInfo) {
                        continue;
                    }
                    if (nodeInfo.getStrength() != nodeInfo.getDefaultStrength() || nodeInfo.isDefaultFx()) {
                        info.setIsExpanded(false);
                        nodeInfo.setStrength(nodeInfo.getDefaultStrength());
                        mBeautyEditListener.onResetFx(mBeautyType, nodeInfo);
                    }
                }
                continue;
            }
            if (info.getStrength() != info.getDefaultStrength()) {
                info.setStrength(info.getDefaultStrength());
                mBeautyEditListener.onResetFx(mBeautyType, info);
            }
        }
        mBeautyAdapter.notifyDataSetChanged();
        setSelectPosition(-1, -1);

    }

    /**
     * 设置选中位置
     * Set selected location
     *
     * @param selectPosition           position
     * @param selectNodeSelectPosition nodePosition
     */
    public void setSelectPosition(int selectPosition, int selectNodeSelectPosition) {
        if (null == mBeautyAdapter) {
            return;
        }
        List<IFxInfo> iFxInfos = mBeautyAdapter.getData();
        for (IFxInfo iFxInfo : iFxInfos) {
            if (null == iFxInfo) {
                continue;
            }
            List<IFxInfo> nodeInfo = iFxInfo.getFxNodes();
            if ((null == nodeInfo) || nodeInfo.isEmpty()) {
                continue;
            }
            iFxInfo.setIsExpanded(false);
        }
        int position = mBeautyAdapter.getSelectPosition();
        SparseArray<Object> mAdapters = mBeautyAdapter.getAdapterArray();
        if (mAdapters.size() > 0) {
            for (int i = 0; i < mAdapters.size(); i++) {
                BeautyNodeAdapter nodeAdapter = (BeautyNodeAdapter) mAdapters.get(i);
                if (null == nodeAdapter) {
                    continue;
                }
                nodeAdapter.setSelectPosition(-1);
            }
            BeautyNodeAdapter nodeAdapter = (BeautyNodeAdapter) mAdapters.get(position);
            if (null != nodeAdapter) {
                nodeAdapter.setSelectPosition(selectNodeSelectPosition);
            }
        }
        mBeautyAdapter.setSelectPosition(selectPosition);
    }

    /**
     * 修改强度
     * Modified strength
     *
     * @param strength strength
     */
    public void changeStrength(double strength) {
        if (null == mBeautyAdapter) {
            return;
        }
        int position = mBeautyAdapter.getSelectPosition();
        IFxInfo info = mBeautyAdapter.getData().get(position);
        if (null == info) {
            return;
        }
        info.setStrength(strength);
    }

    /**
     * 修改参数值
     * Modify parameter value
     *
     * @param key   key
     * @param value value
     */
    public void changeParamValue(String key, Object value) {
        if (null == mBeautyAdapter) {
            return;
        }
        int position = mBeautyAdapter.getSelectPosition();
        IFxInfo info = mBeautyAdapter.getData().get(position);
        if (null == info) {
            return;
        }
        if (info instanceof CompoundFxInfo) {
            FxParams param = ((CompoundFxInfo) info).findParam(key);
            if (null == param) {
                return;
            }
            param.value = value;
        }
    }

    /**
     * 修改node强度
     * 注：node里面是互斥的，即只能选择一个。如磨皮有磨皮1，2，3，4，但是最终只能选择一个
     * Modifying node strength
     * Note: node is mutually exclusive, that is, only one can be selected. For example, there are peeling 1,2,3,4, but only one can be selected
     *
     * @param strength strength
     */
    public void changeNodeStrengthExclusive(double strength) {
        if (null == mBeautyAdapter) {
            return;
        }
        int position = mBeautyAdapter.getSelectPosition();
        SparseArray<Object> mAdapters = mBeautyAdapter.getAdapterArray();
        if (mAdapters.size() == 0) {
            return;
        }
        BeautyNodeAdapter nodeAdapter = (BeautyNodeAdapter) mAdapters.get(position);
        int nodePosition = nodeAdapter.getSelectPosition();
        List<IFxInfo> iFxInfos = nodeAdapter.getData();
        for (int i = 0; i < iFxInfos.size(); i++) {
            IFxInfo info = iFxInfos.get(i);
            if (null == info) {
                continue;
            }
            info.setStrength((i == nodePosition) ? strength : 0);
        }
    }

    /**
     * 修改node强度
     * Modifying node strength
     *
     * @param strength strength
     */
    public void changeNodeStrength(double strength) {
        if (null == mBeautyAdapter) {
            return;
        }
        int position = mBeautyAdapter.getSelectPosition();
        SparseArray<Object> mAdapters = mBeautyAdapter.getAdapterArray();
        if (mAdapters.size() == 0) {
            return;
        }
        BeautyNodeAdapter nodeAdapter = (BeautyNodeAdapter) mAdapters.get(position);
        if (null == nodeAdapter) {
            return;
        }
        List<IFxInfo> iFxInfos = nodeAdapter.getData();
        if (iFxInfos.isEmpty()) {
            return;
        }
        int nodePosition = nodeAdapter.getSelectPosition();
        if ((nodePosition == -1) || (nodePosition > iFxInfos.size())) {
            return;
        }
        IFxInfo info = iFxInfos.get(nodePosition);
        if (null == info) {
            return;
        }
        info.setStrength(strength);
    }

    public void setBeautyEditListener(OnBeautyEditListener beautyEditListener) {
        mBeautyEditListener = beautyEditListener;
    }

    public interface OnBeautyEditListener {
        /**
         * 美颜编辑应用
         * Beauty editing application
         *
         * @param beautyType type
         * @param info       info
         */
        void onBeautyEdit(int beautyType, IFxInfo info);

        /**
         * 重置
         * reset
         *
         * @param beautyType type
         * @param info       info
         */
        void onResetFx(int beautyType, IFxInfo info);
    }
}
