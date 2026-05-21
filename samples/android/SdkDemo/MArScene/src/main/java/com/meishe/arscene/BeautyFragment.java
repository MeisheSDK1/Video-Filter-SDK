package com.meishe.arscene;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;

import com.meishe.arscene.adapter.BeautyAdapter;
import com.meishe.arscene.adapter.BeautyNodeAdapter;
import com.meishe.arscene.bean.BaseFxInfo;
import com.meishe.arscene.bean.CompoundFxInfo;
import com.meishe.arscene.bean.FxParams;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.arscene.inter.OnBeautyApplyListener;
import com.meishe.arscene.inter.OnNodeItemClickListener;
import com.meishe.arscene.iview.BeautyFragmentView;
import com.meishe.arscene.presenter.BeautyFragmentPresenter;
import com.meishe.base.model.BaseMvpFragment;
import com.meishe.third.adpater.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiFei
 * @CreateDate : 2022-11-15 下午15:20
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BeautyFragment extends BaseMvpFragment<BeautyFragmentPresenter> implements BeautyFragmentView {
    /**
     * 美肤，美型，微整形数据
     * Beauty skin, beauty shape, micro plastic data
     */
    private final List<IFxInfo> mBeautyData = new ArrayList<>();
    private BeautyAdapter mBeautyAdapter;
    private int mBeautyType = FxParams.BEAUTY_SKIN;
    private OnBeautyApplyListener mBeautyApplyListener;


    private BeautyFragment() {

    }

    public static BeautyFragment create(int type) {
        BeautyFragment fragment = new BeautyFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.BEAUTYTYPE, type);
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
        }
        RecyclerView rvBeauty = rootView.findViewById(R.id.rv_beauty);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvBeauty.setLayoutManager(manager);
        mBeautyAdapter = new BeautyAdapter();
        rvBeauty.setAdapter(mBeautyAdapter);
        initListener();
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void initData() {
        mPresenter.getSkinData(getContext(), mBeautyType, mBeautyData, true);
        if (mBeautyType == FxParams.BEAUTY_SKIN) {
            //初始化美肤磨皮数据 Initialize the data for the beauty dermabrasion
            if (!mBeautyData.isEmpty()) {
                IFxInfo mBuffingSkinData = mBeautyData.get(0);
                if (null != mBuffingSkinData) {
                    mBuffingSkinData.setFxNodes(mPresenter.getBuffingSkinData(getContext()));
                }
            }
        }
        mBeautyAdapter.addData(mBeautyData);
    }

    private void initListener() {
        mBeautyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!mBeautyAdapter.isEnable()) {
                    return;
                }
                IFxInfo info = (IFxInfo) adapter.getData().get(position);
                if (null == info) {
                    return;
                }
                if (TextUtils.equals(BaseFxInfo.TYPE_PLACE_HOLDER, info.getType()) || !info.canReplace()) {
                    return;
                }
                List<IFxInfo> nodeFxInfo = info.getFxNodes();
                if ((null != nodeFxInfo) && !nodeFxInfo.isEmpty()) {
                    info.setIsExpanded(!info.isExpanded());
                } else {
                    mBeautyAdapter.setAllExpanded(false);
                }
                mBeautyAdapter.setSelectPosition(position);
                if (null != mBeautyApplyListener) {
                    mBeautyApplyListener.onBeautyApply(info);
                }

            }
        });
        mBeautyAdapter.setOnNodeItemClickListener(new OnNodeItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, int parentPosition, int position) {
                IFxInfo info = (IFxInfo) adapter.getData().get(position);
                if (null == info) {
                    return;
                }
                if (null != mBeautyApplyListener) {
                    mBeautyApplyListener.onBeautyApply(info);
                }
            }
        });
    }

    /**
     * 设置是否可点击
     * Set whether to be clickable
     *
     * @param isEnable isEnable
     */
    public void setIsEnable(boolean isEnable) {
        if (null == mBeautyAdapter) {
            return;
        }
        mBeautyAdapter.setEnable(isEnable);
        if (!isEnable) {
            setSelectPosition(-1, -1);
        }
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
     * 修改已选中的特效名称，如美白A，美白B
     * Modify the selected effects name, such as Whitening A, Whitening B
     *
     * @param name name
     */
    public void changeFxName(String name) {
        if (null == mBeautyAdapter) {
            return;
        }
        int position = mBeautyAdapter.getSelectPosition();
        changeFxName(position, name);
    }

    /**
     * 修改某项特效名称
     * Change the name of a special effect
     *
     * @param position position
     * @param name     name
     */
    public void changeFxName(int position, String name) {
        if (null == mBeautyAdapter) {
            return;
        }
        if ((position < 0) || (position > mBeautyAdapter.getData().size())) {
            return;
        }
        IFxInfo info = mBeautyAdapter.getData().get(position);
        if (null == info) {
            return;
        }
        info.setName(name);
        mBeautyAdapter.notifyItemChanged(position);
    }

    /**
     * 更新已选中的某一项IFxInfo
     * Updates one of the selected IFxInfo items
     *
     * @param info info
     */
    public void updateFxInfo(IFxInfo info) {
        if (null == info) {
            return;
        }
        if (null == mBeautyAdapter) {
            return;
        }
        int position = mBeautyAdapter.getSelectPosition();
        if ((position < 0) || (position > mBeautyAdapter.getData().size())) {
            return;
        }
        IFxInfo iFxInfo = mBeautyAdapter.getData().get(position);
        iFxInfo.copy(info);
        mBeautyAdapter.notifyItemChanged(position);
    }

    /**
     * 替换更新某一项IFxInfo
     * Replace updates an IFxInfo item
     *
     * @param position position
     * @param info     info
     */
    public void updateFxInfo(int position, IFxInfo info) {
        if ((null == mBeautyAdapter) || (null == info)) {
            return;
        }
        if ((position < 0) || (position > mBeautyAdapter.getData().size())) {
            return;
        }
        List<IFxInfo> iFxInfos = mBeautyAdapter.getData();
        if (iFxInfos.isEmpty()) {
            return;
        }
        iFxInfos.set(position, info);
        mBeautyAdapter.notifyItemChanged(position);
    }

    /**
     * 获取对应美颜列表数据
     * Obtain the corresponding beauty list data
     *
     * @return data
     */
    public List<IFxInfo> getBeautyData() {
        if (null == mBeautyAdapter) {
            return null;
        }
        return mBeautyAdapter.getData();
    }


    public void setBeautyApplyListener(OnBeautyApplyListener beautyApplyListener) {
        mBeautyApplyListener = beautyApplyListener;
    }

}