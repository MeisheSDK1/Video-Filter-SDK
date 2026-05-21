package com.meishe.arscene.adapter;

import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishe.arscene.R;
import com.meishe.arscene.bean.BaseFxInfo;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.arscene.inter.OnNodeItemClickListener;
import com.meishe.third.adpater.BaseQuickAdapter;
import com.meishe.third.adpater.BaseViewHolder;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/11/16 20:08
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class BeautyAdapter extends BaseBeautyAdapter {
    private OnNodeItemClickListener mOnNodeItemClickListener;
    protected SparseArray<Object> adapterArray = new SparseArray<>();

    @Override
    protected void convertHolder(BaseViewHolder holder, IFxInfo item) {
        holder.itemView.setAlpha((isEnable && item.canReplace()) ? 1.0f : 0.4f);
        int position = holder.getAdapterPosition();
        List<IFxInfo> nodeFxInfo = item.getFxNodes();
        ImageView mBeautyIcon = holder.getView(R.id.item_beauty_icon);
        TextView mBeautyName = holder.getView(R.id.item_beauty_name);
        mBeautyName.setTextColor(mContext.getResources().getColorStateList(
                (mSkinMode == SKIN_MODE_WHITE)
                        ? R.color.beauty_text_white_selector
                        : R.color.beauty_text_black_selector));
        mBeautyName.setText(item.getName());
        holder.setBackgroundDrawable(R.id.item_beauty_select, mSelectBg);
        if (mSelectPosition == position) {
            if ((null != nodeFxInfo) && !nodeFxInfo.isEmpty()) {
                mBeautyIcon.setImageDrawable(
                        tintColor(item.isExpanded()
                                        ? R.mipmap.icon_fx_back
                                        : item.getResourceId(),
                                mSkinMode));
                mBeautyIcon.setSelected(false);
                mBeautyName.setSelected(false);
            } else {
                holder.setVisible(R.id.item_beauty_select, mSelectStyle != SLELCT_STYLE_SELF);
                mBeautyIcon.setImageResource(item.getResourceId());
                mBeautyIcon.setSelected(true);
                mBeautyName.setSelected(mSelectPosition == position);
            }
        } else {
            holder.setVisible(R.id.item_beauty_select, false);
            if (mSelectStyle == SLELCT_STYLE_SELF) {
                mBeautyIcon.setSelected(false);
                mBeautyIcon.setImageDrawable(tintColor(item.getResourceId(), mSkinMode));
            } else {
                mBeautyIcon.setImageResource(item.getResourceId());
            }
            mBeautyName.setSelected(mSelectPosition == position);
        }
        RecyclerView mItemRvBeauty = holder.getView(R.id.item_rv_beauty);
        if ((null != nodeFxInfo) && !nodeFxInfo.isEmpty() && item.isExpanded()) {
            mItemRvBeauty.setVisibility(View.VISIBLE);
            LinearLayoutManager manager = new LinearLayoutManager(mContext);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mItemRvBeauty.setLayoutManager(manager);
            BeautyNodeAdapter nodeAdapter = (BeautyNodeAdapter) adapterArray.get(position);
            if (null == nodeAdapter) {
                nodeAdapter = new BeautyNodeAdapter();
                adapterArray.put(position, nodeAdapter);
            }
            nodeAdapter.setSkinMode(mSkinMode);
            mItemRvBeauty.setAdapter(nodeAdapter);
            nodeAdapter.setNewData(nodeFxInfo);
            selectNodePosition(nodeAdapter, nodeFxInfo);
            setNodeListener(nodeAdapter, position);
        } else {
            mItemRvBeauty.setVisibility(View.GONE);
        }
    }

    @Override
    protected void convertPlaceHolder(BaseViewHolder holder, IFxInfo item) {

    }

    private void selectNodePosition(BeautyNodeAdapter nodeAdapter, List<IFxInfo> nodeInfos) {
        if ((null == nodeAdapter) || (null == nodeInfos) || nodeInfos.isEmpty()) {
            return;
        }
        for (int i = 0; i < nodeInfos.size(); i++) {
            IFxInfo nodeInfo = nodeInfos.get(i);
            if (null == nodeInfo) {
                continue;
            }
            double strength = nodeInfo.getStrength();
            if (strength != 0 && nodeInfo.isSelected()) {
                nodeAdapter.setSelectPosition(i);
            }
        }
    }

    /**
     * 设置node监听
     * Setting node listening
     *
     * @param nodeAdapter nodeAdapter
     */
    private void setNodeListener(final BeautyNodeAdapter nodeAdapter, final int parentPosition) {
        if (null == nodeAdapter) {
            return;
        }
        nodeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<IFxInfo> data = adapter.getData();
                IFxInfo info = data.get(position);
                if (null == info) {
                    return;
                }
                if (TextUtils.equals(BaseFxInfo.TYPE_PLACE_HOLDER, info.getType())) {
                    return;
                }
                for (IFxInfo fxInfo : data) {
                    fxInfo.setSelected(false);
                }
                info.setSelected(true);
                nodeAdapter.setSelectPosition(position);
                if (null != mOnNodeItemClickListener) {
                    mOnNodeItemClickListener.onItemClick(adapter, parentPosition, position);
                }
            }
        });
    }

    public SparseArray<Object> getAdapterArray() {
        return adapterArray;
    }

    public void setOnNodeItemClickListener(OnNodeItemClickListener onNodeItemClickListener) {
        mOnNodeItemClickListener = onNodeItemClickListener;
    }
}
