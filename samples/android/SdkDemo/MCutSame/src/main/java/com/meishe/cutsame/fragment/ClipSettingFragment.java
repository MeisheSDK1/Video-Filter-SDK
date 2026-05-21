package com.meishe.cutsame.fragment;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meishe.cutsame.ExportTemplateSettingActivity;
import com.meishe.cutsame.R;
import com.meishe.cutsame.bean.ExportTemplateClip;
import com.meishe.cutsame.bean.ExportTemplateSection;
import com.meishe.cutsame.bean.ExportTemplateTrackMenu;
import com.meishe.cutsame.fragment.adapter.ExportTemplateClipAdapter;
import com.meishe.cutsame.fragment.adapter.ExportTemplateTrackMenuAdapter;
import com.meishe.cutsame.pop.PopFootageAttachView;
import com.meishe.cutsame.pop.PopFootageGroupAttachView;
import com.meicam.sdk.NvsIconGenerator;
import com.meishe.base.model.BaseFragment;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.FileUtils;
import com.meishe.base.utils.FormatUtils;
import com.meishe.base.utils.ImageUtils;
import com.meishe.base.utils.MediaTypeUtils;
import com.meishe.base.utils.ToastUtils;
import com.meishe.base.play.PlayerFragment;
import com.meishe.engine.bean.ClipInfo;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.MeicamVideoClip;
import com.meishe.engine.bean.MeicamVideoTrack;
import com.meishe.engine.bean.TimelineData;
import com.meishe.engine.util.PathUtils;
import com.meishe.third.adpater.BaseQuickAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2020/12/24 10:55
 * @Description : 导出模板-对clip进行设置
 * export template clip setting
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class ClipSettingFragment extends BaseFragment implements View.OnClickListener, NvsIconGenerator.IconCallback {

    private RecyclerView mRecyclerViewLeft;
    private RecyclerView mRecyclerViewRight;
    private ExportTemplateTrackMenuAdapter mTrackMenuAdapter;
    private ExportTemplateClipAdapter mExportTemplateClipAdapter;
    private List<ExportTemplateSection> mData;
    private List<ExportTemplateTrackMenu> mMenuData;
    private LinearLayout mLlBottomView;
    private int mFootageGroupId;
    private TextView mTvCancel;
    private TextView mTvConfirm;
    private NvsIconGenerator mNvsIconGenerator;

    public ClipSettingFragment() {

    }

    public static ClipSettingFragment newInstance() {
        return new ClipSettingFragment();
    }


    @Override
    protected int bindLayout() {
        return R.layout.frgment_clip_setting;
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void initView(View rootView) {
        mRecyclerViewLeft = rootView.findViewById(R.id.recycler_left);
        mRecyclerViewRight = rootView.findViewById(R.id.recycler_right);

        mLlBottomView = rootView.findViewById(R.id.ll_bottom_view);
        mLlBottomView.setVisibility(View.GONE);
        mTvCancel = rootView.findViewById(R.id.tv_cancel);
        mTvConfirm = rootView.findViewById(R.id.tv_confirm);

        initLeftRecyclerView();
        initRightRecyclerView();

        initListener();
    }

    private void initListener() {
        mTvCancel.setOnClickListener(this);
        mTvConfirm.setOnClickListener(this);
    }

    private void initRightRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerViewRight.setLayoutManager(linearLayoutManager);
        mExportTemplateClipAdapter = new ExportTemplateClipAdapter();
        mRecyclerViewRight.setAdapter(mExportTemplateClipAdapter);
        mExportTemplateClipAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mExportTemplateClipAdapter.setSelectPosition(0);
            }
        });
        mExportTemplateClipAdapter.setOnItemChildClickListener(new ExportTemplateClipAdapter.OnItemChildClickListener() {
            @Override
            public void onFootageTypeViewClick(View view, final int position, final ExportTemplateClip templateClip) {
                if (templateClip == null) {
                    return;
                }
                PopFootageAttachView popFootageAttachView = PopFootageAttachView.create(getContext(), view, new PopFootageAttachView.OnAttachListener() {
                    @Override
                    public void onApplyToAll(int selectViewId) {
                        if (selectViewId == 0) {
                            return;
                        }
                        if (mData == null) {
                            return;
                        }
                        String footageType = null;
                        if (selectViewId == R.id.rb_footage_unlimited) {
                            footageType = ExportTemplateClip.TYPE_FOOTAGE_IMAGE_AND_VIDEO;
                        } else if (selectViewId == R.id.rb_footage_image) {
                            footageType = ExportTemplateClip.TYPE_FOOTAGE_IMAGE;
                        } else if (selectViewId == R.id.rb_footage_video) {
                            footageType = ExportTemplateClip.TYPE_FOOTAGE_VIDEO;
                        }
                        for (int i = 0; i < mData.size(); i++) {
                            ExportTemplateSection exportTemplateSection = mData.get(i);
                            if (exportTemplateSection == null) {
                                continue;
                            }
                            ExportTemplateClip exportTemplateClip = exportTemplateSection.t;
                            if (exportTemplateClip == null) {
                                continue;
                            }
                            if (exportTemplateClip.getTrackIndex() != templateClip.getTrackIndex()) {
                                continue;
                            }
                            int footageGroupsId = exportTemplateClip.getFootageGroupsId();
                            if (footageGroupsId > 0) {
                                continue;
                            }
                            exportTemplateClip.setFootageType(footageType);
                        }
                        mExportTemplateClipAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFootageType(int selectViewId) {
                        if (selectViewId == R.id.rb_footage_unlimited) {
                            templateClip.setFootageType(ExportTemplateClip.TYPE_FOOTAGE_IMAGE_AND_VIDEO);
                        } else if (selectViewId == R.id.rb_footage_image) {
                            templateClip.setFootageType(ExportTemplateClip.TYPE_FOOTAGE_IMAGE);
                        } else if (selectViewId == R.id.rb_footage_video) {
                            templateClip.setFootageType(ExportTemplateClip.TYPE_FOOTAGE_VIDEO);
                        }
                        mExportTemplateClipAdapter.notifyItemChanged(position);
                    }
                }, templateClip.getFootageType());
                popFootageAttachView.show();
            }

            @Override
            public void onGroupViewClick() {
                if (mData == null) {
                    return;
                }
                int selectNumber = 0;
                for (int i = 0; i < mData.size(); i++) {
                    ExportTemplateSection exportTemplateSection = mData.get(i);
                    if (exportTemplateSection == null) {
                        continue;
                    }
                    ExportTemplateClip exportTemplateClip = exportTemplateSection.t;
                    if (exportTemplateClip == null) {
                        continue;
                    }
                    boolean selectFootageGroups = exportTemplateClip.isSelectFootageGroups();
                    if (selectFootageGroups) {
                        selectNumber++;
                    }
                }
                TextView tvGroup = getActivity().findViewById(R.id.tv_group);
                if (selectNumber == 0) {
                    tvGroup.setText(getString(R.string.activity_cut_export_template_cancel));
                } else {
                    tvGroup.setText(String.format(getString(R.string.activity_cut_export_template_select_number), selectNumber));
                }
                showBottomPopView(selectNumber);
            }

            @Override
            public void onCancelGroupClick(View view, final ExportTemplateClip templateClip) {
                final PopFootageGroupAttachView popFootageGroupAttachView = PopFootageGroupAttachView.create(getContext(), view, new PopFootageGroupAttachView.OnCancelCroupClickListener() {
                    @Override
                    public void onCancelClick() {
                        int footageGroupsId = templateClip.getFootageGroupsId();
                        if (mData != null) {
                            for (ExportTemplateSection exportTemplateSection : mData) {
                                if (exportTemplateSection == null) {
                                    continue;
                                }
                                ExportTemplateClip exportTemplateClip = exportTemplateSection.t;
                                if (exportTemplateClip == null) {
                                    continue;
                                }
                                if (exportTemplateClip.getFootageGroupsId() == footageGroupsId) {
                                    exportTemplateClip.setFootageGroupsId(0);
                                }
                            }
                        }
                        mExportTemplateClipAdapter.notifyDataSetChanged();
                        boolean hasGroup = false;
                        if (mData != null) {
                            for (ExportTemplateSection exportTemplateSection : mData) {
                                if (exportTemplateSection == null) {
                                    continue;
                                }
                                ExportTemplateClip exportTemplateClip = exportTemplateSection.t;
                                if (exportTemplateClip == null) {
                                    continue;
                                }
                                if (exportTemplateClip.getFootageGroupsId() != 0) {
                                    hasGroup = true;
                                    break;
                                }
                            }
                            if (!hasGroup) {
                                mFootageGroupId = 0;
                            }
                        }

                    }
                });
                popFootageGroupAttachView.show();
            }

            @Override
            public void onSelectPosition(int position) {
                playCurrentClip(position);
            }

            @Override
            public void clickLockView(int position, ExportTemplateClip templateClip) {
                if (CommonUtils.isEmpty(mData)) {
                    return;
                }
                boolean isLock = templateClip.isLock();
                if (isLock) {
                    templateClip.setLock(false);
                    mExportTemplateClipAdapter.notifyItemChanged(position);
                    return;
                }
                boolean isHasNotLock = false;
                for (int i = 0; i < mData.size(); i++) {
                    ExportTemplateSection exportTemplateSection = mData.get(i);
                    if (exportTemplateSection == null) {
                        continue;
                    }
                    if (exportTemplateSection.isHeader) {
                        continue;
                    }
                    ExportTemplateClip exportTemplateClip = exportTemplateSection.t;
                    if (exportTemplateClip == null) {
                        continue;
                    }
                    if (templateClip == exportTemplateClip) {
                        continue;
                    }
                    boolean lock = exportTemplateClip.isLock();
                    if (!lock) {
                        isHasNotLock = true;
                        break;
                    }

                }

                if (isHasNotLock) {
                    templateClip.setLock(true);
                    mExportTemplateClipAdapter.notifyItemChanged(position);
                } else {
                    ToastUtils.showLong(getString(R.string.activity_cut_export_not_allow_lock_all_clip));
                }
            }
        });


    }

    /**
     * 播放当前片段
     * play current clip
     *
     * @param position
     */
    private void playCurrentClip(int position) {
        if (mData == null) {
            return;
        }
        ExportTemplateSection exportTemplateSection = mData.get(position);
        if (exportTemplateSection == null) {
            return;
        }
        ExportTemplateClip templateClip = exportTemplateSection.t;
        if (templateClip == null) {
            return;
        }

        long inPoint = templateClip.getInPoint();
        if (position > 0) {
            FragmentActivity activity = getActivity();
            if (activity instanceof ExportTemplateSettingActivity) {
                PlayerFragment playerFragment = ((ExportTemplateSettingActivity) activity).getVideoFragment();
                if (playerFragment != null) {
                    playerFragment.seekTimeline(inPoint, 0);
                    ((ExportTemplateSettingActivity) activity).setPlayControlViewProgress(inPoint);
                }
            }
        }
    }

    private void showBottomPopView(int isGroupSelect) {
        if (isGroupSelect > 0) {
            mLlBottomView.setVisibility(View.VISIBLE);
            if (isGroupSelect == 1) {
                mTvConfirm.setTextColor(getResources().getColor(R.color.ffc1c1c1));
            } else {
                mTvConfirm.setTextColor(getResources().getColor(R.color.red_fc2b55));
            }
        } else {
            mLlBottomView.setVisibility(View.GONE);
        }
    }

    private void initLeftRecyclerView() {
        mTrackMenuAdapter = new ExportTemplateTrackMenuAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerViewLeft.setLayoutManager(layoutManager);
        mRecyclerViewLeft.setAdapter(mTrackMenuAdapter);
        mTrackMenuAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ExportTemplateTrackMenu trackMenu = mMenuData.get(position);
                if (trackMenu == null) {
                    return;
                }
                for (int i = 0; i < mData.size(); i++) {
                    if ((i + 1) > mData.size()) {
                        return;
                    }
                    ExportTemplateSection section = mData.get(i);
                    if (section == null) {
                        continue;
                    }
                    if (trackMenu.getTrackIndex() == section.getTrackSectionIndex()) {
                        mRecyclerViewRight.scrollToPosition(i + 1);
                        LinearLayoutManager mLayoutManager =
                                (LinearLayoutManager) mRecyclerViewRight.getLayoutManager();
                        mLayoutManager.scrollToPositionWithOffset(i + 1, 0);
                        return;
                    }
                }
            }
        });

    }

    @Override
    protected void initData() {
        mNvsIconGenerator = new NvsIconGenerator();
        mNvsIconGenerator.setIconCallback(this);
        initRecyclerViewData();
    }

    private void initRecyclerViewData() {
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        mMenuData = new ArrayList<>();
        mData = new ArrayList<>();
        int footageId = 0;
        if (meicamVideoTrackList != null) {
            for (int i = 0; i < meicamVideoTrackList.size(); i++) {
                MeicamVideoTrack meicamVideoTrack = meicamVideoTrackList.get(i);
                List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
                if ((clipInfoList == null) || clipInfoList.isEmpty()) {
                    continue;
                }
                ExportTemplateTrackMenu exportTemplateTrackMenu = new ExportTemplateTrackMenu();
                if (i == 0) {
                    exportTemplateTrackMenu.setTrackName(getString(R.string.activity_cut_export_template_main_track));
                } else {
                    exportTemplateTrackMenu.setTrackName(String.format(getString(R.string.activity_cut_export_template_pip), i));
                }
                exportTemplateTrackMenu.setTrackIndex(i);
                mMenuData.add(exportTemplateTrackMenu);
                ExportTemplateSection exportTemplateSection = new ExportTemplateSection(null);
                exportTemplateSection.isHeader = true;
                if (i == 0) {
                    exportTemplateSection.header = getString(R.string.activity_cut_export_template_main_track);
                } else {
                    exportTemplateSection.header = String.format(getString(R.string.activity_cut_export_template_pip), i);
                }
                exportTemplateSection.setTrackSectionIndex(i);
                mData.add(exportTemplateSection);
                for (int j = 0; j < clipInfoList.size(); j++) {
                    ClipInfo<?> clipInfo = clipInfoList.get(j);
                    if (clipInfo == null) {
                        continue;
                    }
                    if (clipInfo instanceof MeicamVideoClip) {
                        MeicamVideoClip meicamVideoClip = (MeicamVideoClip) clipInfo;
                        if (meicamVideoClip.getVideoType().equals(CommonData.CLIP_HOLDER)) {
                            continue;
                        }
                        ExportTemplateClip exportTemplateClip = new ExportTemplateClip();
                        exportTemplateClip.setClipName(String.format(getString(R.string.activity_cut_export_template_clip_param), j + 1));
                        long clipDuration = clipInfo.getOutPoint() - clipInfo.getInPoint();
                        exportTemplateClip.setClipDuration(FormatUtils.microsecond2Time(clipDuration));
                        exportTemplateClip.setImagePath(meicamVideoClip.getFilePath());
                        exportTemplateClip.setFileType(meicamVideoClip.getVideoType());
                        exportTemplateClip.setFootageType(ExportTemplateClip.TYPE_FOOTAGE_IMAGE_AND_VIDEO);
                        exportTemplateClip.setFootageGroupsId(0);
                        exportTemplateClip.setFootageId(footageId);
                        exportTemplateClip.setVideoReverse(meicamVideoClip.getVideoReverse());
                        exportTemplateClip.setReversePath(meicamVideoClip.getReverseFilePath());
                        exportTemplateClip.setLock(false);
                        exportTemplateClip.setInPoint(clipInfo.getInPoint());
                        exportTemplateClip.setOutPoint(clipInfo.getOutPoint());
                        exportTemplateClip.setTrimIn(meicamVideoClip.getTrimIn());
                        exportTemplateClip.setTrimOut(meicamVideoClip.getTrimOut());
                        exportTemplateClip.setTrackIndex(i);
                        ExportTemplateSection exportTemplateSectionContent = new ExportTemplateSection(exportTemplateClip);
                        mData.add(exportTemplateSectionContent);
                        getCoverPath(exportTemplateClip);

                        footageId++;
                    }
                }
            }
            mTrackMenuAdapter.addData(mMenuData);
            mExportTemplateClipAdapter.addData(mData);
        }
    }

    private void getCoverPath(ExportTemplateClip exportTemplateClip) {
        String filePath = exportTemplateClip.getImagePath();
        if (filePath == null) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        if (MediaTypeUtils.isImageFileType(filePath)) {
            exportTemplateClip.setCoverPath(filePath);
        } else {
            Bitmap bitmap;
            if (exportTemplateClip.isVideoReverse()) {
                bitmap = mNvsIconGenerator.getIconFromCache(exportTemplateClip.getReversePath(), exportTemplateClip.getTrimIn(), 0);
            } else {
                bitmap = mNvsIconGenerator.getIconFromCache(filePath, exportTemplateClip.getTrimIn(), 0);
            }
            if (bitmap == null) {
                long taskId;
                if (exportTemplateClip.isVideoReverse()) {
                    taskId = mNvsIconGenerator.getIcon(exportTemplateClip.getReversePath(), exportTemplateClip.getTrimIn(), 0);
                } else {
                    taskId = mNvsIconGenerator.getIcon(filePath, exportTemplateClip.getTrimIn(), 0);
                }
                exportTemplateClip.setTaskId(taskId);
            } else {
                saveCover(exportTemplateClip, bitmap);
            }
        }
    }

    public void setActionState(int state) {
        if (mExportTemplateClipAdapter != null) {
            mExportTemplateClipAdapter.setState(state);
        }
    }

    public void setSelectPosition(int position) {
        if (mExportTemplateClipAdapter != null) {
            mExportTemplateClipAdapter.setSelectPosition(position);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_confirm) {
            int selectNumber = 0;
            if (mData == null) {
                return;
            }

            for (int i = 0; i < mData.size(); i++) {
                ExportTemplateSection exportTemplateSection = mData.get(i);
                if (exportTemplateSection == null) {
                    continue;
                }
                ExportTemplateClip exportTemplateClip = exportTemplateSection.t;
                if (exportTemplateClip == null) {
                    continue;
                }
                boolean selectFootageGroups = exportTemplateClip.isSelectFootageGroups();
                if (selectFootageGroups) {
                    selectNumber++;
                }
                if (selectNumber >= 2) {
                    break;
                }
            }

            if (selectNumber < 2) {
                return;
            }
            if (mLlBottomView != null) {
                mLlBottomView.setVisibility(View.GONE);
            }
            mFootageGroupId++;
            for (int i = 0; i < mData.size(); i++) {
                ExportTemplateSection exportTemplateSection = mData.get(i);
                if (exportTemplateSection == null) {
                    continue;
                }
                ExportTemplateClip exportTemplateClip = exportTemplateSection.t;
                if (exportTemplateClip == null) {
                    continue;
                }
                boolean selectFootageGroups = exportTemplateClip.isSelectFootageGroups();
                if (selectFootageGroups) {
                    exportTemplateClip.setFootageGroupsId(mFootageGroupId);
                }
                exportTemplateClip.setSelectFootageGroups(false);
            }
        } else if (view.getId() == R.id.tv_cancel) {
            for (int i = 0; i < mData.size(); i++) {
                ExportTemplateSection exportTemplateSection = mData.get(i);
                if (exportTemplateSection == null) {
                    continue;
                }
                ExportTemplateClip exportTemplateClip = exportTemplateSection.t;
                if (exportTemplateClip == null) {
                    continue;
                }
                exportTemplateClip.setSelectFootageGroups(false);
            }
            if (mLlBottomView != null) {
                mLlBottomView.setVisibility(View.GONE);
            }
        }

        TextView tvGroup = getActivity().findViewById(R.id.tv_group);
        tvGroup.setText(getString(R.string.activity_cut_export_template_groups));
        setActionState(ExportTemplateClipAdapter.ACTION_STATE_DEFAULT);
        mExportTemplateClipAdapter.notifyDataSetChanged();
    }

    public List<ExportTemplateSection> getData() {
        return mData;
    }

    @Override
    public void onIconReady(Bitmap bitmap, long timestamp, long taskId) {
        if (mData == null) {
            return;
        }
        for (ExportTemplateSection exportTemplateSection : mData) {
            ExportTemplateClip templateClip = exportTemplateSection.t;
            if (templateClip == null) {
                continue;
            }
            if (taskId == templateClip.getTaskId()) {
                saveCover(templateClip, bitmap);
            }
        }
    }

    private void saveCover(ExportTemplateClip templateClip, Bitmap bitmap) {
        String path = PathUtils.getTemplateCoverFileFolder() + File.separator + System.currentTimeMillis() + ".png";
        templateClip.setCoverPath(path);
        FileUtils.createOrExistsDir(path);
        ImageUtils.save(bitmap, path, Bitmap.CompressFormat.PNG);
        mExportTemplateClipAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        if (mNvsIconGenerator != null) {
            mNvsIconGenerator.cancelTask(0);
            mNvsIconGenerator.release();
            mNvsIconGenerator = null;
        }
        super.onDestroy();

    }
}
