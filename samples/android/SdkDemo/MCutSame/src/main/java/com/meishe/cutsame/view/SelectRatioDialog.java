package com.meishe.cutsame.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meishe.cutsame.R;
import com.meishe.cutsame.adapter.RatioAdapter;
import com.meishe.cutsame.bean.RatioInfo;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.view.decoration.ItemDecoration;
import com.meishe.third.pop.core.CenterPopupView;

import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2020/12/28 15:29
 * @Description : 选择画幅比例视图
 * select resolution view
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class SelectRatioDialog extends CenterPopupView implements View.OnClickListener {
    static final int ASSET_PACKAGE_ASPECT_RATIO_16V9 = 1;
    static final int ASSET_PACKAGE_ASPECT_RATIO_1V1 = 2;
    static final int ASSET_PACKAGE_ASPECT_RATIO_9V16 = 4;
    static final int ASSET_PACKAGE_ASPECT_RATIO_4V3 = 8;
    static final int ASSET_PACKAGE_ASPECT_RATIO_3V4 = 16;
    static final int ASSET_PACKAGE_ASPECT_RATIO_18V9 = 32;
    static final int ASSET_PACKAGE_ASPECT_RATIO_9V18 = 64;

    private Context mContext;
    private ImageView mIvClose;
    private OnSelectRatioListener mOnSelectRatioListener;
    private RelativeLayout mRlCutDialogRatio;
    private RecyclerView mRvRatio;
    private RatioAdapter mRatioAdapter;
    private int mRatio = -1;
    private List<RatioInfo> mRatioInfos;


    public SelectRatioDialog(@NonNull Context context, int ration, List<RatioInfo> ratioInfos, OnSelectRatioListener mOnSelectRatioListener) {
        super(context);
        mContext = context;
        mRatio = ration;
        mRatioInfos = ratioInfos;
        this.mOnSelectRatioListener = mOnSelectRatioListener;
    }

    @Override
    protected int getImplLayoutId() {
        return bindLayoutId != 0 ? bindLayoutId : R.layout.cut_dialog_ratio;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        initView();
        initListener();
    }

    private void initListener() {
        mIvClose.setOnClickListener(this);
    }

    private void initView() {
        mRlCutDialogRatio = findViewById(R.id.rl_cut_dialog_ratio_parent);
        Drawable radiusDrawable = CommonUtils.getRadiusDrawable((int) getResources().getDimension(R.dimen.dp_px_15), getResources().getColor(R.color.white_fc));
        mRlCutDialogRatio.setBackground(radiusDrawable);
        mIvClose = findViewById(R.id.iv_close);
        mRvRatio = findViewById(R.id.rvRatio);
        GridLayoutManager manager = new GridLayoutManager(mContext, 2);
        mRvRatio.setLayoutManager(manager);
        ItemDecoration decoration = new ItemDecoration(0
                , mContext.getResources().getDimensionPixelSize(R.dimen.dp_px_21), 0, 0);
        mRvRatio.addItemDecoration(decoration);
        mRatioAdapter = new RatioAdapter(mContext, mRatio, mRatioInfos);
        mRvRatio.setAdapter(mRatioAdapter);
        mRatioAdapter.setOnItemClickListener(new RatioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (mOnSelectRatioListener != null) {
                    mOnSelectRatioListener.onSelectRatio(mRatioInfos.get(position).getTag());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_close) {
            SelectRatioDialog.this.dismiss();
        }
    }

    public interface OnSelectRatioListener {
        void onSelectRatio(int tag);
    }
}
