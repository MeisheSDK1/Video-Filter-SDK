package com.meishe.sdkdemo.edit.watermark;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineVideoFx;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseFragmentPagerAdapter;
import com.meishe.sdkdemo.edit.EditBaseActivity;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.clipEdit.EditActivity;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.utils.dataInfo.VideoFx;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import static com.meishe.sdkdemo.edit.watermark.WaterMarkConstant.ASSETS_WATERMARK_PICTURE_PATH;
import static com.meishe.sdkdemo.edit.watermark.WaterMarkConstant.DEFAULT_WATERMARK_PICTURE;
import static com.meishe.sdkdemo.edit.watermark.WaterMarkConstant.WATERMARKTYPE_DYNAMICS;
import static com.meishe.sdkdemo.edit.watermark.WaterMarkConstant.WATERMARKTYPE_EFFECK;
import static com.meishe.sdkdemo.edit.watermark.WaterMarkConstant.WATERMARKTYPE_STATIC;
import static com.meishe.sdkdemo.utils.Constants.EDIT_MODE_EFFECT;
import static com.meishe.sdkdemo.utils.Constants.EDIT_MODE_WATERMARK;
import static com.meishe.sdkdemo.utils.MediaConstant.SINGLE_PICTURE_PATH;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yyj
 * @CreateDate : 2019/6/28.
 * @Description :视频编辑-水印-Activity
 * @Description :VideoEdit-WaterMark-Activity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class WaterMarkActivity extends EditBaseActivity implements OnItemClickListener {
    public static final String WATER_CLEAN = "waterClean";
    public static final String EFFECT_CLEAN = "EffectClean";
    public static final int WATER_MARK_TYPE = 0;
    private final String TAG = "WaterMarkActivity";

    private CustomTitleBar m_titleBar;
    private Point liveWindowPoint;
    private String mPicturePath;
    private int pictureType;
    private int waterMarkType;

    private String mWaterMarkPicture;
    private TabLayout tlSelectWater;
    private ViewPager vpSelectWater;
    private List<Fragment> fragmentLists = new ArrayList<>();
    private List<String> fragmentTabTitles = new ArrayList<>();
    private NvsTimelineVideoFx story;
    private float mTransX = 0;
    private float mTransY = 0;
    private float mScale = 1;
    private int mPictureW;
    private int mPictureH;
    private boolean backFromSelect = false;
    private EffectFragment mEffectFragment;
    private LinearLayout mBottomLayout;

    @Override
    protected int initRootView() {
        return R.layout.activity_water_mark;
    }

    @Override
    protected void initViews() {
        m_titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mBottomLayout = findViewById(R.id.water_setlayout);
        findViewById(R.id.water_btn_ok).setOnClickListener(this);
        tlSelectWater = (TabLayout) findViewById(R.id.tl_select_water);
        vpSelectWater = (ViewPager) findViewById(R.id.vp_select_water);
    }

    @Override
    protected void initTitle() {
        m_titleBar.setTextCenter(R.string.watermark);
        m_titleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        initVideoFragment(R.id.water_videoLayout);

        String[] tabList = getResources().getStringArray(R.array.select_water);
        for (int i = 0; i < tabList.length; i++) {
            WaterMarkSelectFragment mediaFragment = new WaterMarkSelectFragment(this, this);
            Bundle bundle = new Bundle();
            bundle.putInt(WaterMarkConstant.WATERMARKTYPE_TYPE, WaterMarkConstant.WATERMARKTYPES[i]);
            mediaFragment.setArguments(bundle);
            fragmentLists.add(mediaFragment);
            fragmentTabTitles.add(tabList[i]);
        }

        mEffectFragment = new EffectFragment(this, this);
        fragmentLists.add(mEffectFragment);
        fragmentTabTitles.add(getResources().getString(R.string.effect_name_effect));

        BaseFragmentPagerAdapter fragmentPagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), fragmentLists, fragmentTabTitles);
        vpSelectWater.setAdapter(fragmentPagerAdapter);
        tlSelectWater.setupWithViewPager(vpSelectWater);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();

    }

    public void initVideoFragment(int layoutId) {
        if (nvsTimeline == null)
            return;
        videoFragment = new VideoFragment();
        videoFragment.setAutoPlay(true);
        videoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                videoFragment.seekTimeline(nvsStreamingContext.getTimelineCurrentPosition(nvsTimeline), 0);
            }
        });
        videoFragment.setTimeline(nvsTimeline);
        Bundle m_bundle = new Bundle();
        m_bundle.putInt("titleHeight", m_titleBar.getLayoutParams().height);
        m_bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        m_bundle.putBoolean("playBarVisible", true);
        m_bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        videoFragment.setArguments(m_bundle);
        getFragmentManager().beginTransaction()
                .add(layoutId, videoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(videoFragment);
    }

    int playState;
    //是否有特效
    //Whether there are special effects
    boolean hasEffect = false;

    @Override
    protected void initListener() {
        videoFragment.setAssetEditListener(new VideoFragment.AssetEditListener() {
            @Override
            public void onAssetDelete() {
                if (videoFragment.getWaterType() == WATER_MARK_TYPE) {
                    clearActivityData();
                    if (waterMarkType == WATERMARKTYPE_DYNAMICS) {
                        TimelineUtil.checkAndDeleteExitFX(nvsTimeline);
                    }
                    refreshLiveWindowFrame();
                    videoFragment.setPicturePath(null);
                    videoFragment.setDrawRectVisible(View.GONE);
                } else {
                    hasEffect = false;
                    videoFragment.removeWaterToTimeline();
                    videoFragment.setDrawRectVisible(View.GONE);
                }
                for (Fragment fragment : fragmentLists) {
                    if (fragment != null && fragment instanceof WaterMarkSelectFragment) {
                        if (waterMarkType == ((WaterMarkSelectFragment) fragment).getWaterType()) {
                            ((WaterMarkSelectFragment) fragment).refreshNowPosition();
                        }
                    }
                    if (waterMarkType == WATERMARKTYPE_EFFECK && fragment != null && fragment instanceof EffectFragment) {
                        ((EffectFragment) fragment).refreshSelectPosition();
                    }
                }
                waterMarkType = -1;
            }

            @Override
            public void onAssetSelected(PointF curPoint) {

            }

            @Override
            public void onAssetTranslation() {

            }

            @Override
            public void onAssetScale() {

            }

            @Override
            public void onAssetAlign(int alignVal) {

            }

            @Override
            public void onAssetHorizFlip(boolean isHorizFlip) {

            }
        });
        videoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
            @Override
            public void playBackEOF(NvsTimeline timeline) {

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void playStopped(NvsTimeline timeline) {
            }

            @Override
            public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
            }

            @Override
            public void streamingEngineStateChanged(int state) {
                playState = state;
                if (waterMarkType == WaterMarkConstant.WATERMARKTYPE_DYNAMICS || waterMarkType == WATERMARKTYPE_EFFECK) {
                    if (state == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                        videoFragment.setDrawRectVisible(View.GONE);
                    } else {
                        videoFragment.setDrawRectVisible(View.VISIBLE);
                    }
                }
            }
        });
        videoFragment.setWaterMarkChangeListener(new VideoFragment.WaterMarkChangeListener() {
            @Override
            public void onDrag(List<PointF> newPointList) {
                if (story != null && waterMarkType == WATERMARKTYPE_DYNAMICS) {
                    float centerX = newPointList.get(3).x - (newPointList.get(3).x - newPointList.get(0).x) / 2;
                    float centerY = newPointList.get(1).y - (newPointList.get(1).y - newPointList.get(0).y) / 2;
                    int sceneWidth = videoFragment.getLiveWindowSize().x;
                    int sceneHeight = videoFragment.getLiveWindowSize().y;
                    float transX = centerX - sceneWidth / 2;
                    float transY = centerY - sceneHeight / 2;
                    story.setFloatVal("Sticker TransX", transX);
                    story.setFloatVal("Sticker TransY", -transY);
                    mTransX = transX;
                    mTransY = transY;
                    refreshLiveWindowFrame();
                }
            }

            @Override
            public void onScaleAndRotate(List<PointF> nowPointToRect) {
                if (story != null && waterMarkType == WATERMARKTYPE_DYNAMICS) {
                    int normalWidth = (int) Math.abs(nowPointToRect.get(0).x - nowPointToRect.get(3).x);
                    int normalHeight = (int) Math.abs(nowPointToRect.get(0).y - nowPointToRect.get(1).y);
                    float scale1 = (float) normalWidth / mPictureH;
                    float scale2 = (float) normalHeight / mPictureH;
                    float scale = (scale1 + scale2) / 2;
                    mScale = scale;
                    story.setFloatVal("Sticker Scale", scale);
                    refreshLiveWindowFrame();
                }
            }
        });

        mEffectFragment.setOnEffectSeekBarListener(new EffectFragment.OnEffectSeekBarListener() {
            @Override
            public void onViewLevelChange(float value) {
                videoFragment.upDateMosicFxLevel(value);
            }

            @Override
            public void onViewNumberChange(float value) {
                videoFragment.upDateMosicFxNum(value);
            }

            @Override
            public void onViewBlurChange(float value) {
                videoFragment.upDateBlurFxLevel(value);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            WaterMarkSelectFragment waterMarkSelectFragment = (WaterMarkSelectFragment) fragmentLists.get(0);
            Gson gson = new Gson();
            WaterMarkCacheData waterMarkCacheData = new WaterMarkCacheData(waterMarkSelectFragment.getCacheList());
            String str = gson.toJson(waterMarkCacheData);
            FileUtil.writeWaterMarkCacheFile(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !backFromSelect) {

            VideoFx videoFx = TimelineData.instance().getVideoFx();
            if (videoFx != null) {
                videoFragment.setEffectByData(videoFx);
                videoFragment.refreshLiveWindowFrame();
                hasEffect = true;
            }
            backFromSelect = false;
            liveWindowPoint = videoFragment.getLiveWindowSize();
            WaterMarkData waterMarkData = TimelineData.instance().getWaterMarkData();
            if (waterMarkData != null) {
                setActivityData(waterMarkData.getWaterMarkItemData().getItemWaterMarkType(), waterMarkData.getPicPath(), waterMarkData.getWaterMarkItemData().getWaterMarkpath());
                liveWindowPoint = waterMarkData.getPointOfLiveWindow();
                if (waterMarkType == WATERMARKTYPE_STATIC) {
                    setWaterMarkOnTheFirst(mWaterMarkPicture);
                } else if (waterMarkType == WATERMARKTYPE_DYNAMICS) {
                    mTransX = waterMarkData.getTransX();
                    mTransY = waterMarkData.getTransY();
                    mScale = waterMarkData.getScale();
                    setDynamicWaterMarkOnTheFirst(mPicturePath, mWaterMarkPicture, true);
                }
                videoFragment.setWaterType(WATER_MARK_TYPE);
                videoFragment.setDrawRect(waterMarkData.getPointFInLiveWindow());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.water_btn_ok) {
            if (mPicturePath != null) {
                List<PointF> nowPointToRect = videoFragment.getDrawRect();
                List<PointF> newPointToRect = viewPointToCanonical(nowPointToRect);
                List<PointF> pointAtFirst = videoFragment.getPointFListToFirstAddWaterMark();
                List<PointF> newPointAtFirst = viewPointToCanonical(pointAtFirst);
                int excursionX = (int) (newPointAtFirst.get(3).x - newPointToRect.get(3).x);
                int excursionY = (int) (newPointAtFirst.get(3).y - newPointToRect.get(3).y);
                int picWidthOfCanonical = (int) Math.abs(newPointToRect.get(0).x - newPointToRect.get(3).x);
                int picHeightOfCanonical = (int) Math.abs(newPointToRect.get(0).y - newPointToRect.get(1).y);
                if (waterMarkType == WATERMARKTYPE_DYNAMICS) {
                    picWidthOfCanonical = (int) Math.abs(nowPointToRect.get(0).x - nowPointToRect.get(3).x);
                    picHeightOfCanonical = (int) Math.abs(nowPointToRect.get(0).y - nowPointToRect.get(1).y);
                }

                WaterMarkData waterMarkData = new WaterMarkData(nowPointToRect, excursionX, excursionY, picWidthOfCanonical,
                        picHeightOfCanonical, mPicturePath, liveWindowPoint, new WaterMarkItemData(pictureType, waterMarkType, mWaterMarkPicture, mPicturePath));
                waterMarkData.setScale(mScale);
                waterMarkData.setTransX(mTransX);
                waterMarkData.setTransY(mTransY);
                TimelineData.instance().setWaterMarkData(waterMarkData);
                if (!hasEffect) {
                    TimelineData.instance().clearTimelineVideoFx();
                }
                Intent intent = new Intent();
                intent.putExtra(WATER_CLEAN, false);
                intent.putExtra(EFFECT_CLEAN, hasEffect);
                setResult(RESULT_OK, intent);
            } else {
                TimelineData.instance().cleanWaterMarkData();
                if (!hasEffect) {
                    TimelineData.instance().clearTimelineVideoFx();
                }
                Intent intent = new Intent();
                intent.putExtra(WATER_CLEAN, true);
                intent.putExtra(EFFECT_CLEAN, hasEffect);
                setResult(RESULT_OK, intent);
            }
            finishActivity();
        }
    }

    /**
     * view点位坐标映射为timeline坐标
     * The view point coordinates are mapped to timeline coordinates
     *
     * @param nowPointToRect
     * @return
     */
    private List<PointF> viewPointToCanonical(List<PointF> nowPointToRect) {
        List<PointF> newPointFS = new ArrayList<>();
        for (PointF pointF : nowPointToRect) {
            PointF newPointToCanonical = videoFragment.getLiveWindow().mapViewToCanonical(pointF);
            newPointFS.add(newPointToCanonical);
        }
        return newPointFS;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            clearAllData(WaterMarkConstant.WATERMARKTYPE_STATIC);
            setActivityData(WaterMarkConstant.WATERMARKTYPE_STATIC, data.getStringExtra(SINGLE_PICTURE_PATH), data.getStringExtra(SINGLE_PICTURE_PATH));
            setWaterMarkOnTheFirst(mWaterMarkPicture);
            refreshLiveWindowFrame();
            WaterMarkSelectFragment waterMarkSelectFragment = (WaterMarkSelectFragment) fragmentLists.get(0);
            waterMarkSelectFragment.addDataAndRefresh(mPicturePath);
        }
    }

    private void setWaterMarkOnTheFirst(String picturePath) {
        videoFragment.setEditMode(EDIT_MODE_WATERMARK);
        videoFragment.setPicturePath(picturePath);
        videoFragment.firstSetWaterMarkPosition(liveWindowPoint.x, liveWindowPoint.y, picturePath);
        videoFragment.setDrawRectVisible(View.VISIBLE);
    }

    private void setDynamicWaterMarkOnTheFirst(String mPicturePath, String waterMarkPicture, boolean isFromWindowFocusChanged) {
        setWaterMarkOnTheFirst(null);
        videoFragment.setDrawRectVisible(View.GONE);
        videoFragment.playVideo(0, -1);
        String dir = ASSETS_WATERMARK_PICTURE_PATH;
        if (mPicturePath.contains(DEFAULT_WATERMARK_PICTURE)) {
            dir = PathUtils.getWatermarkCafDirectoryDir();
        }
        int sceneWidth = videoFragment.getLiveWindowSize().x;
        int sceneHeight = videoFragment.getLiveWindowSize().y;

        List<PointF> nowPointToRect = videoFragment.getDrawRect();
        float normalX = nowPointToRect.get(0).x;
        float normalY = nowPointToRect.get(0).y;
        float transX = normalX - sceneWidth / 2 + mPictureW / 2;
        float transY = normalY - sceneHeight / 2 + mPictureH / 2;
        if (isFromWindowFocusChanged) {
            story = WaterMarkUtil.setDynamicWaterMark(nvsTimeline, sceneWidth, sceneHeight, waterMarkPicture, mPictureW, mPictureH, dir, mTransX, mTransY, mScale);
        } else {
            mTransX = transX;
            mTransY = transY;
            story = WaterMarkUtil.setDynamicWaterMark(nvsTimeline, sceneWidth, sceneHeight, waterMarkPicture, mPictureW, mPictureH, dir, transX, transY, 1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onItemClick(RecyclerView.ViewHolder holder, int position, int pictureType, String picturePath, int waterMarkType, String waterMarkPicture) {
        videoFragment.setWaterType(WATER_MARK_TYPE);
        if (position == 0 && waterMarkType == WaterMarkConstant.WATERMARKTYPE_STATIC) {
            backFromSelect = true;
            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), SingleClickActivity.class, new Bundle(), EditActivity.CLIPTRIM_REQUESTCODE);
        } else {
            clearAllData(waterMarkType);
            this.pictureType = pictureType;
            setActivityData(waterMarkType, picturePath, waterMarkPicture);
            if (waterMarkType == WaterMarkConstant.WATERMARKTYPE_STATIC) {
                setWaterMarkOnTheFirst(waterMarkPicture);
                WaterMarkSelectFragment selectFragment = (WaterMarkSelectFragment) fragmentLists.get(1);
                if (selectFragment != null && selectFragment.getNowPosition() != -1) {
                    selectFragment.refreshNowPosition();
                }
            } else {
                WaterMarkSelectFragment selectFragment = (WaterMarkSelectFragment) fragmentLists.get(0);
                if (selectFragment != null && selectFragment.getNowPosition() != -1) {
                    selectFragment.refreshNowPosition();
                }
                setDynamicWaterMarkOnTheFirst(picturePath, waterMarkPicture, false);
            }
        }
        refreshLiveWindowFrame();
    }

    private void clearAllData(int waterMarkType) {
        clearActivityData();
        TimelineUtil.checkAndDeleteExitFX(nvsTimeline);
        if (videoFragment != null) {
            videoFragment.removeWaterToTimeline();
        }
        if (fragmentLists != null && fragmentLists.size() > 2) {
            if (waterMarkType == WaterMarkConstant.WATERMARKTYPE_STATIC) {
                WaterMarkSelectFragment selectFragment = (WaterMarkSelectFragment) fragmentLists.get(1);
                if (selectFragment != null && selectFragment.getNowPosition() != -1) {
                    selectFragment.refreshNowPosition();
                }
                if (mEffectFragment != null) {
                    mEffectFragment.refreshSelectPosition();
                }
            } else if (waterMarkType == WATERMARKTYPE_DYNAMICS) {
                WaterMarkSelectFragment selectFragment = (WaterMarkSelectFragment) fragmentLists.get(0);
                if (selectFragment != null && selectFragment.getNowPosition() != -1) {
                    selectFragment.refreshNowPosition();
                }
                if (mEffectFragment != null) {
                    mEffectFragment.refreshSelectPosition();
                }
            } else {
                WaterMarkSelectFragment selectFragment = (WaterMarkSelectFragment) fragmentLists.get(1);
                if (selectFragment != null && selectFragment.getNowPosition() != -1) {
                    selectFragment.refreshNowPosition();
                }
                selectFragment = (WaterMarkSelectFragment) fragmentLists.get(0);
                if (selectFragment != null && selectFragment.getNowPosition() != -1) {
                    selectFragment.refreshNowPosition();
                }

            }
        }
    }

    @Override
    public void onEffectClick(RecyclerView.ViewHolder holder, int position, EffectItemData effectItemData) {
        waterMarkType = WATERMARKTYPE_EFFECK;
        clearAllData(waterMarkType);
        if (effectItemData != null) {
            hasEffect = true;
            int type = effectItemData.getType();
            videoFragment.setEditMode(EDIT_MODE_EFFECT);
            videoFragment.setEffectByPoint(type);
            videoFragment.setDrawRectVisible(View.VISIBLE);
            videoFragment.refreshLiveWindowFrame();
        }
    }


    private void refreshLiveWindowFrame() {
        if (playState != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
            videoFragment.seekTimeline(nvsStreamingContext.getTimelineCurrentPosition(nvsTimeline), 0);
        }
    }

    private void clearActivityData() {
        mWaterMarkPicture = null;
        mPicturePath = null;
    }


    private void setActivityData(int type, String picturePath, String mWaterMarkPath) {
        mPicturePath = picturePath;
        mWaterMarkPicture = mWaterMarkPath;
        waterMarkType = type;
        Point point;
        if (waterMarkType == WaterMarkConstant.WATERMARKTYPE_STATIC) {
            point = videoFragment.getPictureSize(picturePath);
        } else {
            point = videoFragment.getPictureSize(mWaterMarkPath);
        }
        mPictureW = point.x;
        mPictureH = point.y;
    }
}
