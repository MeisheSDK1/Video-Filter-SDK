package com.meishe.cutsame.fragment;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishe.base.model.BaseFragment;
import com.meishe.base.utils.FormatUtils;
import com.meishe.cutsame.R;
import com.meishe.cutsame.bean.ExportTemplateCaption;
import com.meishe.cutsame.fragment.adapter.ExportTemplateCaptionAdapter;
import com.meishe.engine.bean.ClipInfo;
import com.meishe.engine.bean.MeicamCaptionClip;
import com.meishe.engine.bean.MeicamStickerCaptionTrack;
import com.meishe.engine.bean.MeicamVideoClip;
import com.meishe.engine.bean.TimelineData;
import com.meishe.engine.bean.TimelineDataUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2020/12/24 10:55
 * @Description : 导出模板-对字幕进行设置
 * export template caption setting
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class CaptionSettingFragment extends BaseFragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private ExportTemplateCaptionAdapter mExportTemplateCaptionAdapter;
    private List<ExportTemplateCaption> mData;
    private LinearLayout mLlBottomView;

    public CaptionSettingFragment() {

    }

    public static CaptionSettingFragment newInstance() {
        CaptionSettingFragment captionSettingFragment = new CaptionSettingFragment();
        return captionSettingFragment;
    }

    @Override
    protected int bindLayout() {
        return R.layout.frgment_caption_setting;
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void initView(View rootView) {
        mRecyclerView = rootView.findViewById(R.id.recycler);
        mLlBottomView = rootView.findViewById(R.id.ll_bottom_view);
        mLlBottomView.setVisibility(View.GONE);

        rootView.findViewById(R.id.tv_cancel).setOnClickListener(this);
        rootView.findViewById(R.id.tv_confirm).setOnClickListener(this);

        initRecyclerView();

    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mExportTemplateCaptionAdapter = new ExportTemplateCaptionAdapter();
        mRecyclerView.setAdapter(mExportTemplateCaptionAdapter);
        mExportTemplateCaptionAdapter.setOnChildViewClickListener(new ExportTemplateCaptionAdapter.OnChildViewClickListener() {
            @Override
            public void onSelectCaptionClick() {
                boolean isCaptionSelect = false;
                if (mData == null) {
                    return;
                }
                int selectNumber = 0;
                for (int i = 0; i < mData.size(); i++) {
                    ExportTemplateCaption templateCaption = mData.get(i);
                    if (templateCaption == null) {
                        continue;
                    }
                    if (templateCaption.isCaptionSelect()) {
                        selectNumber++;
                        isCaptionSelect = true;
                    }
                }
                updateSelectNumber(selectNumber);
                showBottomPopView(isCaptionSelect);
            }
        });
    }

    private void updateSelectNumber(int selectNumber) {
        TextView tvGroup = getActivity().findViewById(R.id.tv_group);
        if (selectNumber == 0) {
            tvGroup.setText(getString(R.string.activity_cut_export_template_select_all));
        } else {
            tvGroup.setText(String.format(getString(R.string.activity_cut_export_template_select_number), selectNumber));
        }
    }

    private void showBottomPopView(boolean isCaptionSelect) {
        if (isCaptionSelect) {
            mLlBottomView.setVisibility(View.VISIBLE);
        } else {
            mLlBottomView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        initCaptionData();
    }

    /**
     * 初始化字幕数据
     * Initialize subtitle data
     */
    private void initCaptionData() {
        mData = new ArrayList<>();
        List<MeicamStickerCaptionTrack> meicamStickerCaptionTrackList = TimelineData.getInstance().getMeicamStickerCaptionTrackList();
        if (meicamStickerCaptionTrackList != null) {
            for (int i = 0; i < meicamStickerCaptionTrackList.size(); i++) {
                MeicamStickerCaptionTrack meicamStickerCaptionTrack = meicamStickerCaptionTrackList.get(i);
                if (meicamStickerCaptionTrack == null) {
                    continue;
                }
                List<ClipInfo<?>> clipInfoList = meicamStickerCaptionTrack.getClipInfoList();
                if (clipInfoList == null) {
                    continue;
                }
                for (int j = 0; j < clipInfoList.size(); j++) {
                    ClipInfo<?> clipInfo = clipInfoList.get(j);
                    if (clipInfo == null) {
                        continue;
                    }
                    if (clipInfo instanceof MeicamCaptionClip) {
                        ExportTemplateCaption templateCaption = new ExportTemplateCaption();
                        templateCaption.setCaptionContent(((MeicamCaptionClip) clipInfo).getText());
                        long captionDuration = clipInfo.getOutPoint() - clipInfo.getInPoint();
                        templateCaption.setCaptionDuration(FormatUtils.microsecond2Time(captionDuration));
                        templateCaption.setLock(false);
                        ClipInfo clipInfoByCaptionInPoint = TimelineDataUtil.getClipInfoByCaptionInPoint(clipInfo.getInPoint());
                        if (clipInfoByCaptionInPoint instanceof MeicamVideoClip) {
                            String filePath = ((MeicamVideoClip) clipInfoByCaptionInPoint).getFilePath();
                            templateCaption.setImagePath(filePath);
                        }
                        mData.add(templateCaption);
                    }
                }
            }
        }

        mExportTemplateCaptionAdapter.addData(mData);
    }

    /**
     * 选中全部
     * select all
     */
    public void selectAllCaption() {
        if (mData == null) {
            return;
        }
        int selectNumber = 0;
        for (int i = 0; i < mData.size(); i++) {
            ExportTemplateCaption templateCaption = mData.get(i);
            if (templateCaption == null) {
                continue;
            }
            if (templateCaption.isLock()) {
                continue;
            }
            templateCaption.setCaptionSelect(true);
            selectNumber++;
        }
        updateSelectNumber(selectNumber);
        mExportTemplateCaptionAdapter.notifyDataSetChanged();
        showBottomPopView(selectNumber == 0 ? false : true);
    }

    public void setActionState(int state) {
        mExportTemplateCaptionAdapter.setState(state);
    }

    @Override
    public void onClick(View view) {
        TextView tvGroup = getActivity().findViewById(R.id.tv_group);
        tvGroup.setText(getString(R.string.activity_cut_export_template_lock));
        setActionState(ExportTemplateCaptionAdapter.ACTION_STATE_DEFAULT);
        if (mLlBottomView != null) {
            mLlBottomView.setVisibility(View.GONE);
        }
        if (view.getId() == R.id.tv_confirm) {
            if (mData != null) {
                for (int i = 0; i < mData.size(); i++) {
                    ExportTemplateCaption templateCaption = mData.get(i);
                    if (templateCaption == null) {
                        continue;
                    }
                    templateCaption.setLock(true);
                    templateCaption.setCaptionSelect(false);
                }
            }
        } else if (view.getId() == R.id.tv_cancel) {
            if (mData != null) {
                for (int i = 0; i < mData.size(); i++) {
                    ExportTemplateCaption templateCaption = mData.get(i);
                    if (templateCaption == null) {
                        continue;
                    }
                    templateCaption.setCaptionSelect(false);
                }
            }
        }
    }
}
