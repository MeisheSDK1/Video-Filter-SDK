package com.meishe.sdkdemo.capture.fragment.beauty;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meishe.arscene.CustomBeautyHelper;
import com.meishe.base.constants.BaseConstants;
import com.meishe.base.model.BaseMvpFragment;
import com.meishe.base.utils.LogUtils;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.capture.bean.BeautyTemplateInfo;
import com.meishe.utils.DrawableUitls;
import com.scwang.smart.refresh.horizontal.SmartRefreshHorizontal;

import java.util.ArrayList;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/2/9 15:05
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class BeautyTemplateFragment extends BaseMvpFragment<BeautyTemplatePresenter> implements BeautyTemplateView {
    private final List<BeautyTemplateInfo> mBeautyTemplates = new ArrayList<>();
    private Context mContext;
    private SmartRefreshHorizontal mSrTemplate;
    private LinearLayout mNoneLayout;
    private ImageView mTemplateCustom, mTemplateSelectCustom;
    private View mTemplateCustomPoint;
    private BeautyTemplateInfo mCustomTemplateInfo;
    private BeautyTemplateAdapter mTemplateAdapter;
    private OnBeautyTemplateListener mTemplateListener;
    private int mApplyPosition = -1;
    private int pageNum = 1;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public static BeautyTemplateFragment newInstance() {
        return new BeautyTemplateFragment();
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_beauty_template;
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void initView(View rootView) {
        initHeaderView();
        mSrTemplate = rootView.findViewById(R.id.sr_template);
        mSrTemplate.setEnableLoadMore(false);
        mSrTemplate.setEnableAutoLoadMore(true);
        mSrTemplate.setEnableLoadMoreWhenContentNotFull(false);
        RecyclerView mRvTemplate = rootView.findViewById(R.id.rv_beauty_template);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvTemplate.setLayoutManager(manager);
        mTemplateAdapter = new BeautyTemplateAdapter(mContext);
        mRvTemplate.setAdapter(mTemplateAdapter);
        mTemplateAdapter.addHeaderView(mNoneLayout, 0, LinearLayout.HORIZONTAL);

        mTemplateCustom = rootView.findViewById(R.id.beauty_template_custom);
        mTemplateSelectCustom = rootView.findViewById(R.id.beauty_template_select_custom);
        int roundRadius = mContext.getResources().getDimensionPixelSize(com.meishe.base.R.dimen.dp_px_24);
        Drawable customDrawable = DrawableUitls.getRadiusDrawable(roundRadius
                , mContext.getResources().getColor(com.meishe.base.R.color.color_D9A5CFFF));
        mTemplateSelectCustom.setBackground(customDrawable);
        Drawable customPointDrawable = DrawableUitls.getRadiusDrawable(roundRadius
                , mContext.getResources().getColor(com.meishe.base.R.color.color_4A90E2));
        mTemplateCustomPoint = rootView.findViewById(R.id.beauty_template_custom_point);
        mTemplateCustomPoint.setBackground(customPointDrawable);
        initListener();
    }

    public void initHeaderView() {
        mNoneLayout = new LinearLayout(mContext);
        mNoneLayout.setOrientation(LinearLayout.VERTICAL);
        int size = mContext.getResources().getDimensionPixelSize(com.meishe.base.R.dimen.dp_px_150);
        int padding = mContext.getResources().getDimensionPixelSize(com.meishe.base.R.dimen.dp_px_45);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        ImageView mNoneView = new ImageView(mContext);
        Drawable drawable = DrawableUitls.getRadiusDrawable(mContext.getResources().getDimensionPixelSize(com.meishe.base.R.dimen.dp_px_24)
                , mContext.getResources().getColor(com.meishe.base.R.color.color_E3E3E3));
        mNoneView.setBackground(drawable);
        mNoneView.setImageResource(R.mipmap.icon_none);
        mNoneView.setPadding(padding, padding, padding, padding);
        mNoneView.setLayoutParams(params);

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.topMargin = mContext.getResources().getDimensionPixelSize(com.meishe.base.R.dimen.dp_px_15);
        textParams.gravity = Gravity.CENTER;
        TextView noneText = new TextView(mContext);
        noneText.setText(mContext.getResources().getString(R.string.no));
        noneText.setTextColor(mContext.getResources().getColor(R.color.black));
        noneText.setTextSize(9);
        noneText.setLayoutParams(textParams);
        mNoneLayout.addView(mNoneView);
        mNoneLayout.addView(noneText);
    }

    private void initListener() {
        mSrTemplate.setOnLoadMoreListener(refreshLayout -> mPresenter.getBeautyTemplateFromNet(pageNum, mTemplateAdapter.getData()));
        mTemplateCustom.setOnClickListener(v -> {
            mTemplateSelectCustom.setVisibility(View.VISIBLE);
            mTemplateAdapter.setSelectPosition(-1);
            if (null == mCustomTemplateInfo) {
                LogUtils.e("The custom beauty template info is null!");
                return;
            }
            if (null != mTemplateListener) {
                mTemplateListener.onApplyTemplate(mCustomTemplateInfo);
            }
        });
        mTemplateSelectCustom.setOnClickListener(v -> {
            if (null != mTemplateListener) {
                CustomBeautyHelper.get().setCustomMode(true);
                mTemplateListener.onEditTemplate();
            }
        });
        mNoneLayout.setOnClickListener(v -> {
            if ((mTemplateAdapter.getSelectPosition() == -1) && (mTemplateSelectCustom.getVisibility() == View.GONE)) {
                return;
            }
            if (mTemplateSelectCustom.getVisibility() == View.VISIBLE) {
                mTemplateSelectCustom.setVisibility(View.GONE);
            }
            mApplyPosition = -1;
            mTemplateAdapter.setSelectPosition(-1);
            if (null != mTemplateListener) {
                mTemplateListener.onApplyTemplate(null);
            }
        });
        mTemplateAdapter.setOnItemClickListener((adapter, view, position) -> {
            CustomBeautyHelper.get().setCustomMode(false);
            if (null == mTemplateAdapter) {
                return;
            }
            List<BeautyTemplateInfo> templateInfos = mTemplateAdapter.getData();
            if (templateInfos.isEmpty()) {
                return;
            }
            BeautyTemplateInfo templateInfo = templateInfos.get(position);
            if (null == templateInfo) {
                return;
            }
            if (mTemplateSelectCustom.getVisibility() == View.VISIBLE) {
                mTemplateSelectCustom.setVisibility(View.GONE);
            }
            int selectPosition = position + 1;
            if (templateInfo.getDownloadStatus() != BaseConstants.RES_STATUS_DOWNLOAD_ALREADY) {
                if (mTemplateAdapter.getSelectPosition() == selectPosition) {
                    return;
                }
                mApplyPosition = selectPosition;
                mPresenter.downloadPackage(selectPosition, templateInfo);
                return;
            }
            if ((mTemplateAdapter.getSelectPosition() == selectPosition) && (null != mTemplateListener)) {
                mTemplateListener.onEditTemplate();
                return;
            }
            mTemplateAdapter.setSelectPosition(selectPosition);
            if (null != mTemplateListener) {
                mTemplateListener.onApplyTemplate(templateInfo);
            }
        });
    }

    @Override
    protected void initData() {
        mCustomTemplateInfo = new BeautyTemplateInfo();
        mPresenter.getCustomBeautyTemplateData(mCustomTemplateInfo);
        mPresenter.getBeautyTemplateData(mBeautyTemplates);
    }

    public void setAssetsData(List<BeautyTemplateInfo> assetsBeautyTemplates) {
        if ((null == assetsBeautyTemplates) || assetsBeautyTemplates.isEmpty()) {
            return;
        }
        mBeautyTemplates.clear();
        mBeautyTemplates.addAll(0, assetsBeautyTemplates);
    }

    public boolean isApplyCustomTemplate() {
        return (null != mTemplateSelectCustom) && (mTemplateSelectCustom.getVisibility() == View.VISIBLE)
                && (null != mTemplateAdapter) && (mTemplateAdapter.getSelectPosition() == -1);
    }

    public void updateCustomTemplatePointView(boolean isVisibility) {
        if (null == mTemplateCustomPoint) {
            return;
        }
        mTemplateCustomPoint.setVisibility(isVisibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBeautyTemplateData(List<BeautyTemplateInfo> mData, boolean isMoreData) {
        mTemplateAdapter.setNewData(mData);
//        if (!mData.isEmpty() && ParameterSettingValues.instance().isDefaultArScene()) {
//            BeautyTemplateInfo templateInfo = mData.get(0);
//            if (templateInfo.isAssets()) {
//                mTemplateAdapter.setSelectPosition(1);
//            }
//        }
        if (!isMoreData) {
            mSrTemplate.finishLoadMoreWithNoMoreData();
        }
    }

    @Override
    public void onDownloadPackageProgress(int position, BeautyTemplateInfo templateInfo, int progress) {
        if (null == templateInfo) {
            return;
        }
        templateInfo.setDownloadStatus(BaseConstants.RES_STATUS_DOWNLOADING);
        mTemplateAdapter.notifyItemChanged(position);
    }

    @Override
    public void onDownloadPackageError(int position, BeautyTemplateInfo templateInfo) {
        if (null == templateInfo) {
            return;
        }
        templateInfo.setDownloadStatus(BaseConstants.RES_STATUS_DOWNLOAD_ERROR);
        mTemplateAdapter.notifyItemChanged(position);
    }

    @Override
    public void onDownloadPackageSuccess(int position, BeautyTemplateInfo templateInfo) {
        if (null == templateInfo) {
            return;
        }
        pageNum++;
        templateInfo.setDownloadStatus(BaseConstants.RES_STATUS_DOWNLOAD_ALREADY);
        mTemplateAdapter.notifyItemChanged(position);
        if (mApplyPosition == position) {
            mTemplateAdapter.setSelectPosition(position);
            if (null != mTemplateListener) {
                mTemplateListener.onApplyTemplate(templateInfo);
            }
        }
    }

    public boolean checkIsApplyTemplate() {
        if (null == mTemplateAdapter) {
            return false;
        }
        int position = mTemplateAdapter.getSelectPosition() - 1;
        if (position < 0) {
            return false;
        }
        BeautyTemplateInfo templateInfo = mTemplateAdapter.getData().get(position);
        if (null == templateInfo) {
            return false;
        }
        mTemplateListener.onApplyTemplate(templateInfo);
        return true;
    }

    public void setTemplateListener(OnBeautyTemplateListener templateListener) {
        mTemplateListener = templateListener;
    }

    public interface OnBeautyTemplateListener {
        /**
         * Edit the beauty template
         * 编辑美颜模板
         * Edit the beauty template
         */
        void onEditTemplate();

        /**
         * Apply a beauty template
         * 应用美颜模板
         * Apply a beauty template
         *
         * @param templateInfo info
         */
        void onApplyTemplate(BeautyTemplateInfo templateInfo);
    }
}
