package com.meishe.sdkdemo.themeshoot;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meicam.sdk.NvsAudioResolution;
import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsTimelineVideoFx;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.base.utils.Utils;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.edit.data.FilterItem;
import com.meishe.sdkdemo.edit.view.dialog.DownloadDialog;
import com.meishe.sdkdemo.themeshoot.adapter.AssetFilterAdapter;
import com.meishe.sdkdemo.themeshoot.adapter.CaptionAdapter;
import com.meishe.sdkdemo.themeshoot.adapter.CaptionItemDecoration;
import com.meishe.sdkdemo.themeshoot.adapter.ThemePreviewAdapter;
import com.meishe.sdkdemo.themeshoot.adapter.ThemePreviewItemDecoration;
import com.meishe.sdkdemo.themeshoot.bean.CaptionBean;
import com.meishe.sdkdemo.themeshoot.bean.ThemePreviewBean;
import com.meishe.sdkdemo.themeshoot.model.ThemeModel;
import com.meishe.sdkdemo.themeshoot.utlils.ThemeShootUtil;
import com.meishe.sdkdemo.themeshoot.utlils.ThemeTimelineUtils;
import com.meishe.sdkdemo.themeshoot.view.ThemePlayView;
import com.meishe.sdkdemo.themeshoot.view.ThemePreviewLiveWindow;
import com.meishe.sdkdemo.utils.AssetFxUtil;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.NumberUtils;
import com.meishe.sdkdemo.utils.ScreenUtils;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2020/8/5.
 * @Description :主题拍摄-视频编辑预览。ThemePreview-VideoEditPreview
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ThemePreviewActivity extends BaseActivity {

    private int ratioType;
    private View themeBack;
    private View themeCompile;
    private ThemePreviewLiveWindow mLiveWindow;
    private ThemePlayView playView;
    private List<ClipInfo> clipInfoData = new ArrayList<>();
    private NvsTimeline mTimeline;
    private final String TAG = getClass().getName();
    private ThemeModel mThemeModel;
    private RecyclerView themeClipsRecycler;
    private List<ThemePreviewBean> editBeans = new ArrayList<>();
    private ArrayList<FilterItem> mFilterDataArrayList = new ArrayList<>();
    private ThemePreviewAdapter editAdapter;
    /**
     * 编辑主view
     * Edit master view
     */
    private View subMain;
    /**
     * 编辑菜单
     * Edit menu
     */
    private TextView subTittle;
    /**
     * 编辑返回
     * Edit return
     */
    private View subBack;
    /**
     * 字幕编辑recyclerview
     * Subtitles edit recyclerview
     */
    private RecyclerView compoundCaptionRecycler;
    /**
     * 滤镜view
     * Filter view
     */
    private RecyclerView filterRecyclerView;
    private long startDuration = 0;
    private long endDuration = 0;
    private CaptionAdapter captionAdapter;
    private NvsTimelineCompoundCaption currentCompoundCaption;
    private final int CAPTION_RESULT_CODE = 1111;
    private ThemePreviewBean currentPreviewBean;
    private SeekBar filterSeekBar;
    private AssetFilterAdapter assetFilterAdapter;
    private String mCompileVideoPath;
    //    private int videoRotation = 0;

    @Override
    protected int initRootView() {
        return R.layout.activity_theme_capture_preview;
    }

    @Override
    protected void initViews() {
        themeBack = findViewById(R.id.theme_back);
        themeCompile = findViewById(R.id.theme_compile);
        mLiveWindow = findViewById(R.id.theme_preview_live);
        playView = findViewById(R.id.theme_play_view);
        themeClipsRecycler = findViewById(R.id.rl_theme_clips);
        filterSeekBar = findViewById(R.id.theme_filter_seek);
        subMain = findViewById(R.id.cl_theme_sub_main);
        subTittle = findViewById(R.id.theme_tv_sub_tittle);
        subBack = findViewById(R.id.theme_iv_sub_back);
        compoundCaptionRecycler = findViewById(R.id.theme_edit_recycler);
        filterRecyclerView = findViewById(R.id.theme_filter_recycler);
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initData() {
        filterSeekBar.setMax(100);
        filterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (currentPreviewBean != null && currentPreviewBean.getVideoFx() != null) {
                    currentPreviewBean.getVideoFx().setFilterIntensity(i / 100f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        if (getIntent() != null && getIntent().getExtras() != null) {
            mThemeModel = (ThemeModel) getIntent().getExtras().getSerializable("ThemeModel");
        }
        mLiveWindow.setFillMode(NvsLiveWindow.FILLMODE_PRESERVEASPECTFIT);
        initTimeline();
        playView.setProgressMax((int) mTimeline.getDuration());
        themeClipsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        themeClipsRecycler.addItemDecoration(new ThemePreviewItemDecoration(ScreenUtils.dip2px(this, 5)));
        editAdapter = new ThemePreviewAdapter(editBeans, this);
        themeClipsRecycler.setAdapter(editAdapter);
        editAdapter.refreshCurrentPosition(0);
        editAdapter.setOnItemClickListener(new ThemePreviewAdapter.OnThemePreviewOnClickListener() {
            @Override
            public void onItemClick(int position) {
                //去做滤镜显示 Go do the filter display
                if (editBeans != null && position < editBeans.size()) {
                    showSubView(editBeans.get(position));
                }
            }
        });
        compoundCaptionRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        compoundCaptionRecycler.addItemDecoration(new CaptionItemDecoration(ScreenUtils.dip2px(this, 7)));
        captionAdapter = new CaptionAdapter(new ArrayList<CaptionBean>(), this);
        compoundCaptionRecycler.setAdapter(captionAdapter);
        initFilterList();

        mDownloadDialog = new DownloadDialog(this);

        mDownloadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDownloadDialog.setProgress(0);
            }
        });
    }

    private DownloadDialog mDownloadDialog;

    /**
     * 滤镜数据初始化
     * Filter data initialization
     */
    private void initFilterList() {
        NvAssetManager assetManager = NvAssetManager.sharedInstance();
        String bundlePath = "filter";
        assetManager.searchReservedAssets(NvAsset.ASSET_FILTER, bundlePath);
        assetManager.searchLocalAssets(NvAsset.ASSET_FILTER);
        mFilterDataArrayList.clear();
        mFilterDataArrayList = AssetFxUtil.getFilterData(this,
                assetManager.getUsableAssets(NvAsset.ASSET_FILTER, NvAsset.AspectRatio_All, 0),
                null,
                true,
                false);
        for (int i = mFilterDataArrayList.size() - 1; i >= 0; i--) {
            FilterItem filterItem = mFilterDataArrayList.get(i);
            if (filterItem.getFilterMode() == FilterItem.FILTERMODE_PACKAGE) {
                if (TextUtils.isEmpty(filterItem.getImageUrl()) || TextUtils.isEmpty(filterItem.getFilterName())) {
                    mFilterDataArrayList.remove(i);
                }
            }
        }
        assetFilterAdapter = new AssetFilterAdapter(this);
        assetFilterAdapter.setFilterDataList(mFilterDataArrayList);
        filterRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        filterRecyclerView.setAdapter(assetFilterAdapter);
        assetFilterAdapter.setOnItemClickListener(new AssetFilterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FilterItem filterItem = mFilterDataArrayList.get(position);
                if (currentPreviewBean == null) {
                    return;
                }
                if (currentPreviewBean.getVideoFx() != null) {
                    mTimeline.removeTimelineVideoFx(currentPreviewBean.getVideoFx());
                    currentPreviewBean.setVideoFx(null);
                }
                if ("无".equals(filterItem.getFilterName())) {
                    filterSeekBar.setVisibility(View.INVISIBLE);
                    filterSeekBar.setEnabled(false);
                } else {
                    filterSeekBar.setEnabled(true);
                    filterSeekBar.setProgress(80);
                    filterSeekBar.setVisibility(View.VISIBLE);
                    if (filterItem.getFilterMode() == FilterItem.FILTERMODE_BUILTIN) {
                        String buidId = filterItem.getIsCartoon() ? "Cartoon" : filterItem.getFilterName();
                        NvsTimelineVideoFx nvsTimelineVideoFx = mTimeline.addBuiltinTimelineVideoFx(currentPreviewBean.getStartDuration(),
                                currentPreviewBean.getDuration() * 1000, buidId);
                        if (filterItem.getIsCartoon() && nvsTimelineVideoFx != null) {
                            nvsTimelineVideoFx.setBooleanVal("Grayscale", filterItem.getGrayScale());
                            nvsTimelineVideoFx.setBooleanVal("Stroke Only", filterItem.getStrokenOnly());
                            nvsTimelineVideoFx.setFilterIntensity(0.8f);
                        }
                        currentPreviewBean.setVideoFx(nvsTimelineVideoFx);
                    } else if (filterItem.getFilterMode() == FilterItem.FILTERMODE_PACKAGE) {
                        NvsTimelineVideoFx nvsTimelineVideoFx = mTimeline.addPackagedTimelineVideoFx(currentPreviewBean.getStartDuration(),
                                currentPreviewBean.getDuration() * 1000, filterItem.getPackageId());
                        currentPreviewBean.setVideoFx(nvsTimelineVideoFx);
                    }
                }
                mStreamingContext.seekTimeline(mTimeline, startDuration, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_FULLSIZE, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME);
                playTimeline(mTimeline, startDuration, endDuration);
            }
        });
    }

    /**
     * 显示视频片段view
     * Display the video clip view
     */
    private void showClipView() {
        startDuration = 0;
        endDuration = mTimeline.getDuration();
        playTimeline(mTimeline, startDuration, endDuration);
        themeClipsRecycler.setVisibility(View.VISIBLE);
        subMain.setVisibility(View.GONE);
        if (playView != null) {
            playView.setProgressEnable(true);
        }
    }

    /**
     * 显示二级
     * Display level 2
     *
     * @param previewBean
     */
    private void showSubView(ThemePreviewBean previewBean) {
        if (previewBean == null) {
            return;
        }
        if (playView != null) {
            playView.setProgressEnable(false);
        }
        currentPreviewBean = previewBean;
        startDuration = previewBean.getStartDuration();
        endDuration = previewBean.getStartDuration() + previewBean.getDuration() * 1000;
        playTimeline(mTimeline, startDuration, endDuration);

        // 显示滤镜 Display filter
        currentCompoundCaption = previewBean.getCompoundCaption();
        if (currentCompoundCaption != null) {
            refreshCompoundCaptionView(currentCompoundCaption);
            compoundCaptionRecycler.setVisibility(View.VISIBLE);
        } else {
            compoundCaptionRecycler.setVisibility(View.GONE);
        }
        subTittle.setText(previewBean.getName() + (Utils.isZh() ? "-编辑" : "-Edit"));
        themeClipsRecycler.setVisibility(View.GONE);
        assetFilterAdapter.setmSelectPos(0);
        filterSeekBar.setEnabled(false);
        filterSeekBar.setVisibility(View.INVISIBLE);
        subMain.setVisibility(View.VISIBLE);
    }

    /**
     * 刷新字幕编辑view
     * Refresh the subtitle edit view
     *
     * @param compoundCaption
     */
    private void refreshCompoundCaptionView(NvsTimelineCompoundCaption compoundCaption) {
        int captionCount = compoundCaption.getCaptionCount();
        List<CaptionBean> captionBeans = new ArrayList<>();
        for (int i = 0; i < captionCount; i++) {
            CaptionBean captionBean = new CaptionBean();
            captionBean.setCountIndex(i);
            captionBean.setText(compoundCaption.getText(i));
            captionBeans.add(captionBean);
        }
        captionAdapter.setNewDatas(captionBeans);
    }


    private void initTimeline() {
        mStreamingContext.clearCachedResources(false);
        clipInfoData.addAll(TimelineData.instance().getClipInfoData());
        ratioType = TimelineData.instance().getMakeRatio();
//        if (ratioType == NvAsset.AspectRatio_9v16) {
//            videoRotation = NvsVideoClip.ClIP_EXTRAVIDEOROTATION_0;
//        } else if (ratioType == NvAsset.AspectRatio_16v9) {
//            videoRotation = NvsVideoClip.ClIP_EXTRAVIDEOROTATION_0;
//        }
        NvsVideoResolution videoResolution = TimelineData.instance().getVideoResolution();
        if (mStreamingContext == null || clipInfoData == null) {
            return;
        }
        videoResolution.imagePAR = new NvsRational(1, 1);
        NvsRational videoFps = new NvsRational(30, 1);
        NvsAudioResolution audioEditRes = new NvsAudioResolution();
        audioEditRes.sampleRate = 44100;
        audioEditRes.channelCount = 2;
        mTimeline = mStreamingContext.createTimeline(videoResolution, videoFps, audioEditRes);
        if (mTimeline == null) {
            Log.e(TAG, "createTimeline failed!");
            return;
        }
        NvsVideoTrack nvsVideoTrack = mTimeline.appendVideoTrack();
        if (nvsVideoTrack == null) {
            Log.e(TAG, "createTimeline appendVideoTrack failed!");
            return;
        }
        if (mThemeModel == null) {
            Log.e(TAG, "createTimeline failed mThemeModel ==null!");
            return;
        }
        if (clipInfoData == null) {
            Log.e(TAG, "createTimeline failed clipInfoData ==null!");
            return;
        }
        List<ThemeModel.ShotInfo> shotInfos = mThemeModel.getShotInfos();
        if (shotInfos == null || shotInfos.size() == 0) {
            Log.e(TAG, "createTimeline failed shotInfos == null || shotInfos.size() == 0!");
            return;
        }
        int clipIndex = 0;//每次去clipInfoData中去取 Every time I go to clipInfoData to get it
        for (int i = 0; i < shotInfos.size(); i++) {
            ThemePreviewBean previewBean = new ThemePreviewBean();
            ThemeModel.ShotInfo shotInfo = shotInfos.get(i);
            if (shotInfo == null) {
                Log.e(TAG, "shotInfo ==null ！！!|position=" + i);
                continue;
            }
            String shotFilePath = shotInfo.canPlaced() ? shotInfo.getSource() : mThemeModel.getFolderPath() + File.separator + shotInfo.getSource();
            if (shotInfo.canPlaced() && clipIndex < clipInfoData.size()) {
                shotInfo.setSource(clipInfoData.get(clipIndex).getFilePath());
                clipIndex++;
            }
            long itemStartDuration = mTimeline.getDuration();
            previewBean.setStartDuration(itemStartDuration);
            // 如果有变速 添加片段时 选取时长，循环添加
            //If there is a variable speed to add the segment when selected time, add cycle
            List<ThemeModel.ShotInfo.SpeedInfo> speeds = shotInfo.getSpeed();
            if (speeds != null && speeds.size() > 0) {
                long startSpeed = 0;//当前追加位置 Current append position
                for (int j = 0; j < speeds.size(); j++) {
                    ThemeModel.ShotInfo.SpeedInfo speedInfo = speeds.get(j);
                    if (speedInfo != null) {
                        //变速开始位置 Shift starting position
                        long speedStart = NumberUtils.parseString2Long(speedInfo.getStart());
                        long totalSpeedTime = (long) ((NumberUtils.parseString2Long(speedInfo.getEnd()) -
                                NumberUtils.parseString2Long(speedInfo.getStart())) *
                                (speedInfo.getSpeed1()
                                        + speedInfo.getSpeed0()) / 2);
                        if (mTimeline.getDuration() - previewBean.getStartDuration() < speedStart) {
                            //追加原视频 Append original video
                            long addDuration = speedStart - (mTimeline.getDuration() - previewBean.getStartDuration());
                            NvsVideoClip nvsVideoClip = nvsVideoTrack.appendClip(shotFilePath,
                                    startSpeed * Constants.US_TIME_BASE, (startSpeed + addDuration) * Constants.US_TIME_BASE);
                            if (nvsVideoClip == null) {
                                Log.e(TAG, "createTimeline VideoTrack.appendClip changeSpeed failed!");
                                continue;
                            }
                            if (!TextUtils.isEmpty(mThemeModel.getMusic())) {
                                nvsVideoClip.setVolumeGain(0, 0);
                            }
//                            nvsVideoClip.setExtraVideoRotation(videoRotation);
                            startSpeed += addDuration;
                        }
                        //追加变速视频 Additional variable speed video
                        NvsVideoClip nvsVideoClip = nvsVideoTrack.appendClip(shotFilePath,
                                startSpeed * Constants.US_TIME_BASE, (startSpeed + totalSpeedTime) * Constants.US_TIME_BASE);
                        if (nvsVideoClip == null) {
                            Log.e(TAG, "createTimeline VideoTrack.appendClip changeSpeed failed!");
                            continue;
                        }
                        if (!TextUtils.isEmpty(mThemeModel.getMusic())) {
                            nvsVideoClip.setVolumeGain(0, 0);
                        }
//                        nvsVideoClip.setExtraVideoRotation(videoRotation);
                        nvsVideoClip.changeVariableSpeed(speedInfo.getSpeed0()
                                , speedInfo.getSpeed1(), true);
                        startSpeed += totalSpeedTime;
                    }
                }
                if (startSpeed < shotInfo.getNeedDuration()) {
                    //追加原视频 Append original video
                    NvsVideoClip nvsVideoClip = nvsVideoTrack.appendClip(shotFilePath,
                            startSpeed * Constants.US_TIME_BASE, shotInfo.getNeedDuration() * Constants.US_TIME_BASE);
                    if (nvsVideoClip == null) {
                        Log.e(TAG, "createTimeline VideoTrack.appendClip changeSpeed failed!");
                        continue;
                    }
                    if (!TextUtils.isEmpty(mThemeModel.getMusic())) {
                        nvsVideoClip.setVolumeGain(0, 0);
                    }
//                    nvsVideoClip.setExtraVideoRotation(videoRotation);
                }
            } else {
                NvsVideoClip nvsVideoClip = nvsVideoTrack.appendClip(shotFilePath,
                        0, shotInfo.getNeedDuration() * Constants.US_TIME_BASE);
                if (nvsVideoClip == null) {
                    Log.e(TAG, "createTimeline VideoTrack.appendClip failed!");
                    continue;
                }
                if (!TextUtils.isEmpty(mThemeModel.getMusic())) {
                    nvsVideoClip.setVolumeGain(0, 0);
                }
//                nvsVideoClip.setExtraVideoRotation(videoRotation);
            }
            // 组合字幕 Combined captioning
            String compoundCaption = shotInfo.getCompoundCaption();
            if (!TextUtils.isEmpty(compoundCaption)) {
                NvsTimelineCompoundCaption nvsTimelineCompoundCaption = mTimeline.addCompoundCaption(itemStartDuration, mTimeline.getDuration(), compoundCaption);
                previewBean.setCompoundCaption(nvsTimelineCompoundCaption);
            }
            previewBean.setDuration(mTimeline.getDuration() - itemStartDuration);
            previewBean.setBitmap(mStreamingContext.grabImageFromTimeline(mTimeline, itemStartDuration + previewBean.getDuration() / 5, new NvsRational(1, 1)));
            previewBean.setName(Utils.isZh() ? "片段" : "Clip" + (i + 1));
            editBeans.add(previewBean);
        }

        //片头滤镜-字幕 Title Filter - Subtitles
        String titleFilter = mThemeModel.getTitleFilter();
        String titleCaption = mThemeModel.getTitleCaption();
        ThemePreviewBean headBean = new ThemePreviewBean();
        headBean.setName(Utils.isZh() ? "片头" : "Headers");
        //片头封面 Title cover
        if (!TextUtils.isEmpty(mThemeModel.getTitleCover())) {
            headBean.setBgUrl(mThemeModel.getFolderPath() + File.separator + mThemeModel.getTitleCover());
        }
        headBean.setDuration(mThemeModel.getTitleCaptionDuration());
        headBean.setStartDuration(0);
        headBean.setType(ThemePreviewBean.THEME_TYPE_START);
        boolean addHead = false;
        if (!TextUtils.isEmpty(titleFilter)) {
            NvsTimelineVideoFx nvsTimelineVideoFx = mTimeline.addPackagedTimelineVideoFx(0,
                    NumberUtils.parseString2Long(mThemeModel.getTitleFilterDuration()), titleFilter);
            headBean.setVideoFx(nvsTimelineVideoFx);
            headBean.setDuration(NumberUtils.parseString2Long(mThemeModel.getTitleFilterDuration()));
            addHead = true;
        }
        if (!TextUtils.isEmpty(titleCaption)) {
            NvsTimelineCompoundCaption nvsTimelineCompoundCaption = mTimeline.addCompoundCaption(0, mThemeModel.getTitleCaptionDuration() * Constants.US_TIME_BASE, titleCaption);
            headBean.setCompoundCaption(nvsTimelineCompoundCaption);
            headBean.setDuration(mThemeModel.getTitleCaptionDuration());
            addHead = true;
        }
        if (addHead) {
            editBeans.add(0, headBean);
        }
        //片尾滤镜 字幕 End filter subtitles
        String endingFilter = mThemeModel.getEndingFilter();
        String endingCaption = mThemeModel.getEndingCaption();
        ThemePreviewBean endBean = new ThemePreviewBean();
        endBean.setType(ThemePreviewBean.THEME_TYPE_END);
        endBean.setDuration(NumberUtils.parseString2Long(mThemeModel.getEndingFilterLen()));
        long inPoint = mTimeline.getDuration() - endBean.getDuration() * 1000;
        endBean.setStartDuration(inPoint);
        endBean.setName(Utils.isZh() ? "片尾" : "Credits");
        //片尾封面 End cover
        if (!TextUtils.isEmpty(mThemeModel.getEndingCover())) {
            endBean.setBgUrl(mThemeModel.getFolderPath() + File.separator + mThemeModel.getEndingCover());
        }
//        endBean.setBgUrl(mThemeModel.getCover());
        if (!TextUtils.isEmpty(endingFilter)) {
            NvsTimelineVideoFx nvsTimelineVideoFx = mTimeline.addPackagedTimelineVideoFx(inPoint,
                    NumberUtils.parseString2Long(mThemeModel.getEndingFilterLen()) * 1000, endingFilter);
            endBean.setVideoFx(nvsTimelineVideoFx);
            editBeans.add(endBean);
        }
        if (!TextUtils.isEmpty(endingCaption)) {
            NvsTimelineCompoundCaption nvsTimelineCompoundCaption = mTimeline.addCompoundCaption(0, mThemeModel.getEndingCaptionDuration() * Constants.US_TIME_BASE, endingCaption);
            endBean.setCompoundCaption(nvsTimelineCompoundCaption);
            endBean.setDuration(mThemeModel.getEndingCaptionDuration());
        }
        //处理转场 Processing transition
        ThemeTimelineUtils.setVideoClipTrans(nvsVideoTrack, shotInfos);
        //添加音乐 Add music
        ThemeTimelineUtils.addMusic(mTimeline, mThemeModel);
        //添加时间线滤镜 Add a timeline filter
        ThemeTimelineUtils.addTimelineFilter(mTimeline, mThemeModel.getTimelineFilter());
        boolean b = mStreamingContext.connectTimelineWithLiveWindow(mTimeline, mLiveWindow);
        endDuration = mTimeline.getDuration();
        playTimeline(mTimeline, startDuration, endDuration);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mStreamingContext != null && mTimeline != null && playView != null) {
            if (playView.isPlaying()) {
                playTimeline(mTimeline, mStreamingContext.getTimelineCurrentPosition(mTimeline), endDuration);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode > 0) {
            if (requestCode == CAPTION_RESULT_CODE) {
                if (data != null && data.getExtras() != null) {
                    CaptionBean captionBean = (CaptionBean) data.getExtras().getSerializable("CaptionBean");
                    if (captionBean != null && currentCompoundCaption != null) {
                        CaptionBean captionBeanByPosition = captionAdapter.getCaptionBeanByPosition(currentCaption);
                        captionBeanByPosition.setText(captionBean.getText());
                        captionAdapter.notifyItemChanged(currentCaption);
                        currentCompoundCaption.setText(captionBeanByPosition.getCountIndex(), captionBean.getText());
                        mStreamingContext.seekTimeline(mTimeline, startDuration, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_FULLSIZE, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME);
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mStreamingContext != null) {
            mStreamingContext.stop();
        }
    }

    /**
     * 播放
     * play
     *
     * @param mTimeLine
     * @param startTime
     * @param endTime
     */
    public void playTimeline(NvsTimeline mTimeLine, long startTime, long endTime) {
        mStreamingContext.playbackTimeline(mTimeLine, startTime, endTime, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true,
                NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME | NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_LOW_PIPELINE_SIZE);

    }

    @Override
    protected void initListener() {
        playView.setOnPlayClickListener(new ThemePlayView.OnPlayClickListener() {
            @Override
            public void onPlayClick(boolean isPlaying) {
                if (isPlaying) {
                    playTimeline(mTimeline, mStreamingContext.getTimelineCurrentPosition(mTimeline), endDuration);
                } else {
                    mStreamingContext.stop();
                }
            }

            @Override
            public void onProgressChange(boolean fromUser, int progressPosition) {
                if (!fromUser) {
                    mStreamingContext.seekTimeline(mTimeline, progressPosition, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_FULLSIZE, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME);
                }
            }
        });
        themeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStreamingContext.stop();
                mStreamingContext.clearCachedResources(true);
                finish();
            }
        });
        themeCompile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDownloadDialog != null && !mDownloadDialog.isShowing()) {
                    mDownloadDialog.show();
                    mDownloadDialog.setTipsText("正在生成视频");
                }
                mCompileVideoPath = ThemeShootUtil.getCompileVideoPath();
                mStreamingContext.compileTimeline(mTimeline, 0, mTimeline.getDuration(), mCompileVideoPath,
                        NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_1080,
                        NvsStreamingContext.COMPILE_BITRATE_GRADE_MEDIUM, 0);
            }
        });
        subBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 显示视频片段view Display the video clip view
                showClipView();
            }
        });
        mStreamingContext.setStreamingEngineCallback(new NvsStreamingContext.StreamingEngineCallback() {
            @Override
            public void onStreamingEngineStateChanged(int i) {
                playView.updatePlaying(i == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK);
            }

            @Override
            public void onFirstVideoFramePresented(NvsTimeline nvsTimeline) {

            }
        });
        mStreamingContext.setPlaybackCallback(new NvsStreamingContext.PlaybackCallback() {
            @Override
            public void onPlaybackPreloadingCompletion(NvsTimeline nvsTimeline) {

            }

            @Override
            public void onPlaybackStopped(NvsTimeline nvsTimeline) {

            }

            @Override
            public void onPlaybackEOF(NvsTimeline nvsTimeline) {
                playTimeline(mTimeline, startDuration, endDuration);
            }
        });
        mStreamingContext.setPlaybackCallback2(new NvsStreamingContext.PlaybackCallback2() {
            @Override
            public void onPlaybackTimelinePosition(NvsTimeline nvsTimeline, long l) {
                playView.setCurrentProgress((int) l);
            }
        });
        captionAdapter.setOnCaptionItemClick(new CaptionAdapter.OnCaptionItemClick() {


            @Override
            public void onItemEditClick(int position) {
                // 标记点击的字幕 跳转文字编辑
                //Mark clicked subtitles jump text editing
                currentCaption = position;
                Intent intent = new Intent(ThemePreviewActivity.this, ThemeEditCaptionActivity.class);
                intent.putExtra("CaptionBean", captionAdapter.getCaptionBeanByPosition(position));
                startActivityForResult(intent, CAPTION_RESULT_CODE);
            }
        });
        mStreamingContext.setCompileCallback(new NvsStreamingContext.CompileCallback() {
            @Override
            public void onCompileProgress(NvsTimeline nvsTimeline, int i) {
                if (mDownloadDialog != null) {
                    mDownloadDialog.setProgress(i);
                }
            }

            @Override
            public void onCompileFinished(NvsTimeline nvsTimeline) {
                if (mDownloadDialog != null) {
                    mDownloadDialog.dismiss();
                }
                File file = new File(mCompileVideoPath);
                if (file != null && file.exists()) {
                    ToastUtil.showToast(ThemePreviewActivity.this, "视频已保存到相册，视频路径为:" + mCompileVideoPath);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(file));
                    getApplicationContext().sendBroadcast(intent);
                }
                finish();
            }

            @Override
            public void onCompileFailed(NvsTimeline nvsTimeline) {
                if (mDownloadDialog != null) {
                    mDownloadDialog.dismiss();
                }
            }
        });
        playView.setPlaying(true);
    }

    private int currentCaption;

    @Override
    public void onClick(View view) {

    }
}
