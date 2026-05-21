package com.meishe.cutsame.fragment.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.meishe.base.utils.ImageLoader;
import com.meishe.base.utils.ToastUtils;
import com.meishe.cutsame.R;
import com.meishe.cutsame.bean.ExportTemplateClip;
import com.meishe.cutsame.bean.ExportTemplateSection;
import com.meishe.third.adpater.BaseSectionQuickAdapter;
import com.meishe.third.adpater.BaseViewHolder;

import androidx.annotation.NonNull;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2020/12/24 15:55
 * @Description : 片段适配器
 * clip adapter
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class ExportTemplateClipAdapter extends BaseSectionQuickAdapter<ExportTemplateSection, BaseViewHolder> {

    public static final int ACTION_STATE_DEFAULT = 0;
    public static final int ACTION_STATE_CREATE_GROUP = 1;


    private OnItemChildClickListener mOnItemChildClickListener;
    private ImageLoader.Options mRoundCornerOptions;
    private int mState;
    private int mSelectPosition = -1;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     * 与QuickAdapter#QuickAdapter(Context,int)相同，但与
     * 一些初始化数据
     */
    public ExportTemplateClipAdapter() {
        super(R.layout.item_export_template_clip, R.layout.item_export_template_clip_header, null);
        mRoundCornerOptions = new ImageLoader.Options().centerCrop().roundedCorners(4);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, ExportTemplateSection item) {
        helper.setText(R.id.tv_track_name, item.header);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseViewHolder holder = super.onCreateViewHolder(parent, viewType);
        return holder;
    }

    @Override
    protected void convert(@NonNull final BaseViewHolder helper, final ExportTemplateSection item) {
        final ExportTemplateClip templateClip = item.t;
        int currentPosition = helper.getAdapterPosition();
        if (mSelectPosition == currentPosition) {
            helper.getView(R.id.iv_bg).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.iv_bg).setVisibility(View.GONE);
        }
        if (templateClip == null) {
            return;
        }
        ImageView clipView = helper.getView(R.id.iv_image);
        String path = templateClip.getCoverPath();
        if (TextUtils.isEmpty(path)) {
            path = templateClip.getImagePath();
        }
        ImageLoader.loadUrl(mContext, path, clipView);
        helper.setText(R.id.tv_clip_name, templateClip.getClipName());
        helper.setText(R.id.tv_groups, templateClip.getFootageGroupsId() == 0 ? mContext.
                getString(R.string.activity_cut_export_template_no_groups) :
                String.format(mContext.getString(R.string.activity_cut_export_template_group_param),
                        templateClip.getFootageGroupsId()));
        helper.setText(R.id.tv_clip_duration, templateClip.getClipDuration());
        helper.setText(R.id.tv_footage_type, getFootageContent(templateClip.getFootageType()));
        View ivGroupDown = helper.getView(R.id.iv_group_down);
        final View ivGroupContainer = helper.getView(R.id.rl_footage_container);

        View ivLock = helper.getView(R.id.iv_lock);
        View ivLockLayer = helper.getView(R.id.iv_mont_layer);

        if (templateClip.isLock()) {
            clipView.setAlpha(0.5f);
            ivLock.setVisibility(View.VISIBLE);
            ivLockLayer.setVisibility(View.VISIBLE);
        } else {
            clipView.setAlpha(1f);
            ivLock.setVisibility(View.GONE);
            ivLockLayer.setVisibility(View.GONE);
        }

        View lockView = helper.getView(R.id.frame_is_lock);
        View ivGroupView = helper.getView(R.id.frame_group);

        if (mState == ACTION_STATE_DEFAULT) {
            lockView.setVisibility(View.VISIBLE);
            ivGroupView.setVisibility(View.INVISIBLE);
        } else {
            lockView.setVisibility(View.INVISIBLE);
            ivGroupView.setVisibility(View.VISIBLE);
        }

        if (templateClip.getFootageGroupsId() != 0 || templateClip.isLock() ||
                templateClip.getFootageType().equals(ExportTemplateClip.TYPE_FOOTAGE_IMAGE) ||
                templateClip.getFootageType().equals(ExportTemplateClip.TYPE_FOOTAGE_VIDEO)) {
            helper.setBackgroundRes(R.id.iv_group, R.mipmap.ic_cut_same_export_template_group_forbid_selected);
            ivGroupDown.setVisibility(View.VISIBLE);
        } else {
            ivGroupDown.setVisibility(View.GONE);
            if (templateClip.isSelectFootageGroups()) {
                helper.setBackgroundRes(R.id.iv_group, R.mipmap.ic_cut_same_export_template_group_selected);
            } else {
                helper.setBackgroundRes(R.id.iv_group, R.mipmap.ic_cut_same_export_template_group_unchecked);
            }
        }

        clipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lastSelectPosition = mSelectPosition;
                mSelectPosition = helper.getAdapterPosition();
                notifyItemChanged(lastSelectPosition);
                notifyItemChanged(mSelectPosition);
                if (mOnItemChildClickListener != null) {
                    mOnItemChildClickListener.onSelectPosition(mSelectPosition);
                }
            }
        });

        ivGroupContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (templateClip.getFootageGroupsId() == 0) {
                    return;
                }

                if (mOnItemChildClickListener != null) {
                    mOnItemChildClickListener.onCancelGroupClick(ivGroupContainer, templateClip);
                }
            }
        });

        ivGroupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (templateClip.isLock()) {
                    ToastUtils.showShort(R.string.activity_cut_export_template_lock_source_no_group);
                    return;
                }
                if (templateClip.getFootageType().equals(ExportTemplateClip.TYPE_FOOTAGE_IMAGE)) {
                    ToastUtils.showShort(R.string.activity_cut_export_template_only_image_no_group);
                    return;
                }
                if (templateClip.getFootageType().equals(ExportTemplateClip.TYPE_FOOTAGE_VIDEO)) {
                    ToastUtils.showShort(R.string.activity_cut_export_template_only_video_no_group);
                    return;
                }
                if (templateClip.getFootageGroupsId() > 0) {
                    return;
                }
                boolean selectFootageGroups = templateClip.isSelectFootageGroups();
                templateClip.setSelectFootageGroups(!selectFootageGroups);
                int adapterPosition = helper.getAdapterPosition();
                notifyItemChanged(adapterPosition);
                if (mOnItemChildClickListener != null) {
                    mOnItemChildClickListener.onGroupViewClick();
                }
            }
        });

        if (templateClip.getFootageGroupsId() > 0) {
            helper.setBackgroundRes(R.id.iv_is_lock, R.mipmap.ic_cut_same_export_template_group_lock);
        } else {
            helper.setBackgroundRes(R.id.iv_is_lock, templateClip.isLock() ? R.mipmap.ic_cut_same_export_template_locked_red
                    : R.mipmap.ic_cut_same_export_template_unlock);
        }

        lockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //有群组了 不能再锁定 There is a group, it can no longer be locked
                if (templateClip.getFootageGroupsId() > 0) {
                    return;
                }
                if (mOnItemChildClickListener != null) {
                    mOnItemChildClickListener.clickLockView(helper.getAdapterPosition(), templateClip);
                }
            }
        });

        View footageType = helper.getView(R.id.tv_footage_type);
        footageType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mState == ACTION_STATE_CREATE_GROUP) {
                    return;
                }
                if (templateClip.getFootageGroupsId() > 0) {
                    ToastUtils.showShort(R.string.activity_cut_export_template_have_group_limit_change_footage_type);
                    return;
                }
                if (mOnItemChildClickListener != null) {
                    mOnItemChildClickListener.onFootageTypeViewClick(view, helper.getAdapterPosition(), templateClip);
                }
            }
        });
    }

    private String getFootageContent(String footageType) {
        if (footageType.equals(ExportTemplateClip.TYPE_FOOTAGE_IMAGE)) {
            return mContext.getString(R.string.activity_cut_export_template_only_picture);
        } else if (footageType.equals(ExportTemplateClip.TYPE_FOOTAGE_VIDEO)) {
            return mContext.getString(R.string.activity_cut_export_template_only_video);
        } else {
            return mContext.getString(R.string.activity_cut_export_template_unlimited);
        }
    }


    public void setSelectPosition(int mSelectPosition) {
        notifyItemChanged(this.mSelectPosition);
        this.mSelectPosition = mSelectPosition;
        notifyItemChanged(mSelectPosition);
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        this.mState = state;
        notifyDataSetChanged();
    }

    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        this.mOnItemChildClickListener = onItemChildClickListener;
    }

    public interface OnItemChildClickListener {

        /**
         * 点击镜头类型视图
         * click footage view
         *
         * @param view
         * @param position
         * @param templateClip
         */
        void onFootageTypeViewClick(View view, int position, ExportTemplateClip templateClip);

        /**
         * 点击参与群组
         * clip group
         */
        void onGroupViewClick();

        /**
         * 点击取消群组
         * click cancel group
         *
         * @param view
         * @param templateClip
         */
        void onCancelGroupClick(View view, ExportTemplateClip templateClip);

        /**
         * 选中item
         * select item
         *
         * @param position
         */
        void onSelectPosition(int position);

        /**
         * 点击锁定按钮
         * click lock view
         *
         * @param adapterPosition
         * @param templateClip
         */
        void clickLockView(int adapterPosition, ExportTemplateClip templateClip);
    }
}
