package com.meishe.sdkdemo.edit.clipEdit.caption;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.meicam.sdk.NvsCaption;
import com.meicam.sdk.NvsClipCaption;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsControlPointPair;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.base.model.BaseMvpActivity;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.download.AssetDownloadActivity;
import com.meishe.sdkdemo.edit.Caption.CaptionAnimationFragment;
import com.meishe.sdkdemo.edit.Caption.CaptionBackgroundFragment;
import com.meishe.sdkdemo.edit.Caption.CaptionBubbleFragment;
import com.meishe.sdkdemo.edit.Caption.CaptionColorFragment;
import com.meishe.sdkdemo.edit.Caption.CaptionFontFragment;
import com.meishe.sdkdemo.edit.Caption.CaptionLetterSpacingFragment;
import com.meishe.sdkdemo.edit.Caption.CaptionOutlineFragment;
import com.meishe.sdkdemo.edit.Caption.CaptionPositionFragment;
import com.meishe.sdkdemo.edit.Caption.CaptionRichWordFragment;
import com.meishe.sdkdemo.edit.Caption.CaptionSizeFragment;
import com.meishe.sdkdemo.edit.Caption.CaptionStyleFragment;
import com.meishe.sdkdemo.edit.Caption.presenter.CaptionPresenter;
import com.meishe.sdkdemo.edit.Caption.view.CaptionView;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.data.AssetItem;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.data.CaptionColorInfo;
import com.meishe.sdkdemo.edit.data.ParseJsonFile;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.edit.view.CustomViewPager;
import com.meishe.sdkdemo.edit.view.HorizontalSeekBar;
import com.meishe.sdkdemo.edit.view.InputDialog;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.dataInfo.CaptionInfo;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.KeyFrameInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.utils.ColorUtil;
import com.meishe.utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static com.meishe.sdkdemo.utils.Constants.ASSET_DOWNLOAD_FAILED;
import static com.meishe.sdkdemo.utils.Constants.ASSET_DOWNLOAD_INPROGRESS;
import static com.meishe.sdkdemo.utils.Constants.ASSET_DOWNLOAD_START_TIMER;
import static com.meishe.sdkdemo.utils.Constants.ASSET_DOWNLOAD_SUCCESS;
import static com.meishe.sdkdemo.utils.Constants.ASSET_LIST_REQUEST_FAILED;
import static com.meishe.sdkdemo.utils.Constants.ASSET_LIST_REQUEST_SUCCESS;
import static com.meishe.sdkdemo.utils.Constants.CaptionColors;
import static com.meishe.sdkdemo.utils.Constants.ROTATION_Z;
import static com.meishe.sdkdemo.utils.Constants.SCALE_X;
import static com.meishe.sdkdemo.utils.Constants.SCALE_Y;
import static com.meishe.sdkdemo.utils.Constants.TRANS_X;
import static com.meishe.sdkdemo.utils.Constants.TRANS_Y;
import static com.meishe.sdkdemo.utils.asset.NvAsset.ASSET_CAPTION_ANIMATION;
import static com.meishe.sdkdemo.utils.asset.NvAsset.ASSET_CAPTION_IN_ANIMATION;
import static com.meishe.sdkdemo.utils.asset.NvAsset.ASSET_CAPTION_OUT_ANIMATION;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yyj
 * @CreateDate : 2019/6/22.
 * @Description :视频编辑-字幕-样式-Fragment
 * @Description :VideoEdit-Caption-Style-Fragment
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ClipCaptionStyleActivity extends BaseMvpActivity<CaptionPresenter>
        implements View.OnClickListener, CaptionView {
    private static final String TAG = "CaptionStyleActivity";
    private int CATEGORY_RICH_WORD = 5;
    private int CATEGORY_BUBBLE = 6;
    private int CATEGORY_IN_ANIMATION = 7;
    private int CATEGORY_OUT_ANIMATION = 8;
    private int CATEGORY_ANIMATION = 9;
    private final String PATH_RICH_WORD = "captionrichword";
    private final String PATH_BUBBLE = "captionbubble";
    private final String PATH_ANIMATION = "captionanimation/combination";
    private final String PATH_IN_ANIMATION = "captionanimation/in";
    private final String PATH_OUT_ANIMATION = "captionanimation/out";

    private static final int CAPTIONSTYLEREQUESTLIST = 103;
    private static final int VIDEOPLAYTOEOF = 105;
    private static final int CAPTION_RICH_WORD = 106;
    private static final int CAPTION_ANIMATION = 107;
    private static final int CAPTION_IN_ANIMATION = 108;
    private static final int CAPTION_OUT_ANIMATION = 109;
    private static final int CAPTION_BUBBLE = 110;
    private static final int VIDEO_PLAY_STOP = 112;

    private static final int CAPTION_ALIGNLEFT = 0;
    private static final int CAPTION_ALIGNHORIZCENTER = 1;
    private static final int CAPTION_ALIGNRIGHT = 2;
    private static final int CAPTION_ALIGNTOP = 3;
    private static final int CAPTION_ALIGNVERTCENTER = 4;
    private static final int CAPTION_ALIGNBOTTOM = 5;

    private static final int PLAY_VIDEO_FORM_START = 1130;
    //字幕的行间距 行间距目前支持设置绝对值
    //Line spacing of subtitles Line spacing currently supports setting absolute values
    private static final float CAPTION_SMALL_LINE_SPACING = -10;
    private static final float CAPTION_STANDARD_LINE_SPACING = 0;
    private static final float CAPTION_MORE_LARGE_LINE_SPACING = 20;
    private static final float CAPTION_LARGE_LINE_SPACEING = 40;

    //设置子间距 相对于Timeline 长边的比例
    //Sets the scale of the sub spacing relative to the long side of the timeline
    private static final double CAPTION_PERCENT_SMALL_SPACING = -0.005;
    private static final double CAPTION_PERCENT_STANDARD_SPACING = 0;
    private static final double CAPTION_PERCENT_MORE_LARGE_SPACING = 0.02;
    private static final double CAPTION_PERCENT_LARGE_SPACING = 0.04;

    private CustomTitleBar mTitleBar;
    private RelativeLayout mBottomLayout;

    private TabLayout mCaptionStyleTab;
    private CustomViewPager mViewPager;
    private ImageView mCaptionAssetFinish;
    private HorizontalSeekBar mSeekBar;
    private VideoFragment mVideoFragment;
    /*
     * 总的字幕样式列表
     * List of total caption styles
     * */
    private ArrayList<AssetItem> mTotalCaptionStyleList;
    private ArrayList<AssetItem> mRichWordList;//花字 Flowery character
    private ArrayList<AssetItem> mAnimationList;//组合动画 Composite animation
    private ArrayList<AssetItem> mMarchInAniList;//入场动画 Entrance animation
    private ArrayList<AssetItem> mMarchOutAniList;//出场动画 Appearance animation
    private ArrayList<AssetItem> mBubbleList;//气泡 bubble
    private ArrayList<Fragment> mAssetFragmentsArray;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;

    private NvAssetManager mAssetManager;
    private int mCaptionStyleType = NvAsset.ASSET_CAPTION_STYLE;
    private int mFontType = NvAsset.ASSET_FONT;

    private long mMaxDuration;
    private int mSelectedStylePos = 0;
    private int mSelectedRichPos = 0;
    private int mSelectedBubblePos = 0;
    private int mSelectedAnimationPos = 0;
    private int mSelectedInAnimationPos = 0;
    private int mSelectedOutAnimationPos = 0;
    private int mSelectedType = -1;
    private int mSelectedColorPos = -1;
    private int mSelectedOutlinePos = 0;
    private int mSelectedBackgroundPos = 0;
    private int mSelectedFontPos = 0;
    NvsClipCaption mCurAddCaption = null;
    private int mAlignType = -1;
    private CaptionRichWordFragment mRichWordFragment;
    private CaptionBubbleFragment mBubbleFragment;
    private CaptionAnimationFragment mAnimationFragment;
    private CaptionStyleFragment mCaptionStyleFragment;
    private CaptionColorFragment mCaptionColorFragment;
    private CaptionOutlineFragment mCaptionOutlineFragment;
    private CaptionBackgroundFragment mCaptionBackgroundFragment;
    private CaptionFontFragment mCaptionFontFragment;
    private CaptionSizeFragment mCaptionSizeFragment;
    private CaptionPositionFragment mCaptionPositionFragment;
    private CaptionLetterSpacingFragment mCaptionLetterSpacingFragment;
    private ArrayList<CaptionColorInfo> mCaptionColorList;
    private ArrayList<CaptionColorInfo> mCaptionOutlineColorList;
    private ArrayList<CaptionColorInfo> mCaptionBackgroundList;
    private ArrayList<AssetItem> mCaptionFontList;
    private ArrayList<AssetItem> mServerCaptionFontList;
    private int mCaptionColorOpacityValue = 100;
    private int mCaptionBackgroundOpacityValue = 100;
    private float mCaptionBackgroundCornerValue = 0;
    private float mCaptionBackgroundPeddingValue = 0;
    private int mCaptionOutlineWidthValue = 100;
    private int mCaptionOutlineOpacityValue = 100;
    //    private int mCaptionSizeValue = 72;
    List<CaptionInfo> mCaptionDataListClone;
    private int mCurCaptionZVal = 0;

    private boolean bIsStyleUuidApplyToAll = false;
    private boolean bIsCaptionColorApplyToAll = false;
    private boolean bIsOutlineApplyToAll = false;
    private boolean bIsCaptionBackgroundApplyToAll = false;
    private boolean bIsFontApplyToAll = false;
    private boolean bIsSizeApplyToAll = false;
    private boolean bIsPositionApplyToAll = false;
    private boolean bIsLetterSpacingApplyToAll = false;

    private boolean isCaptionStyleItemClick = false;
    boolean m_waitFlag = false;

    private Timer mTimer;
    private TimerTask mTimerTask;
    private int mFontCurClickPos = 0;
    private boolean isTraditionCaption = true;
    private boolean hasCombineAnimation;
    private int IN_OUT_ANIMATION_DEFAULT_DURATION = 500;
    private int ANIMATION_DEFAULT_DURATION = 600;
    private ArrayList<ClipInfo> mClipArrayList;
    private int mCurClipIndex;
    private ClipInfo mClipInfo;
    private NvsVideoClip curVideoClip;
    private Context mContext;

    @Override
    public int bindLayout() {
        mContext = this;
        AppManager.getInstance().addActivity(this);
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_clip_caption_style;
    }

    private CaptionStyleHandler m_handler = new CaptionStyleHandler(this);

    @Override
    public void getFontsBack(ArrayList<AssetItem> data) {
        mServerCaptionFontList.clear();
        mServerCaptionFontList.addAll(data);
        updateFontList();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void onDownloadProgress(int position) {
        updateFontItem();
    }

    @Override
    public void onDownloadFinish(int position, NvAsset assetInfo) {
        applyLastSelFont(assetInfo.uuid);
    }

    @Override
    public void onDownloadError(int position) {

    }

    static class CaptionStyleHandler extends Handler {
        WeakReference<ClipCaptionStyleActivity> mWeakReference;

        public CaptionStyleHandler(ClipCaptionStyleActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final ClipCaptionStyleActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case VIDEOPLAYTOEOF:
                    case VIDEO_PLAY_STOP:
                        activity.updateCaption();
                        break;
                    case ASSET_LIST_REQUEST_SUCCESS:
                        activity.updateFontList();
                        break;
                    case ASSET_LIST_REQUEST_FAILED:
                        activity.fontListRequestFail();
                        break;
                    case ASSET_DOWNLOAD_START_TIMER:
                        activity.startProgressTimer();
                        String progressUuid = (String) msg.obj;
                        activity.fontItemCopy(progressUuid);
                        break;
                    case ASSET_DOWNLOAD_SUCCESS:
                        String successUuid = (String) msg.obj;
                        activity.fontItemCopy(successUuid);
                        activity.applyLastSelFont(successUuid);
                        activity.updateFontItem();
                        break;
                    case ASSET_DOWNLOAD_FAILED:
                        activity.fontDownloadFail();
                        activity.updateFontItem();
                        break;
                    case ASSET_DOWNLOAD_INPROGRESS:
                        activity.updateFontDownloadProgress();
                        break;
                    case PLAY_VIDEO_FORM_START:
                        activity.playVideoFormStart();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void initView() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mCaptionStyleTab = (TabLayout) findViewById(R.id.captionStyleTab);
        mViewPager = (CustomViewPager) findViewById(R.id.viewPager);
        mViewPager.setPagingEnabled(false);
        mCaptionAssetFinish = (ImageView) findViewById(R.id.captionAssetFinish);
        mSeekBar = findViewById(R.id.seek_bar);
        initTitle();
    }

    protected void initTitle() {
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void requestData() {
        initAssetData();
        initVideoFragment();
        initTabLayout();
        initListener();
        mPresenter.getFonts();
    }

    protected void initListener() {
        mCaptionAssetFinish.setOnClickListener(this);
        mSeekBar.setOnRangeListener(new HorizontalSeekBar.onRangeListener() {
            @Override
            public void onRange(float left, float right) {
                if (mCurAddCaption == null) {
                    return;
                }
                int index = getCaptionIndex(mCurCaptionZVal);
                CaptionInfo captionInfo = mCaptionDataListClone.get(index);
                int leftValue =
                        (int) (Float.parseFloat(String.format(getString(R.string.format_1f), left)) * 1000);
                int rightValue =
                        (int) (Float.parseFloat(String.format(getString(R.string.format_1f), right)) * 1000);
                //组合动画与出入动画互斥(出入动画不互斥)。前者默认时长0.5s后者0.6s
                //Combination animation and in-out animation are mutually exclusive (in-out animation is not mutually exclusive)
                //,The former has a default duration of 0.5s and the latter 0.6s
                if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionAnimationPackageId())) {
                    if (leftValue <= 100) {
                        leftValue = 100;
                        mSeekBar.setLeftProgress(leftValue);
                        //组合动画最小值可设置成100ms
                        //The minimum value of the combined animation can be set to 100ms
                    }
                    mCurAddCaption.setModularCaptionAnimationPeroid(leftValue);
                    if (captionInfo != null) {
                        captionInfo.setCombinationAnimationDuration(leftValue);
                    }
                } else {
                    if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionInAnimationPackageId())) {
                        if (mSeekBar.getMaxProgress() - leftValue < IN_OUT_ANIMATION_DEFAULT_DURATION
                                && TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId())) {
                            //如果设置入动画后，剩余的时间小于默认时间500毫秒（出入动画默认时长500ms，不论设置不设置出动画），且此时没有出动画，则把出动画时长设置成0
                            //If the remaining time after setting the animation is less than the default time of 500 milliseconds
                            // (the default time of the animation is 500ms, regardless of whether the animation is set or not),
                            // and there is no animation at this time, set the animation duration to 0
                            mCurAddCaption.setModularCaptionOutAnimationDuration(0);
                        }
                        if (captionInfo != null) {
                            captionInfo.setMarchInAnimationDuration(leftValue);
                        }
                        mCurAddCaption.setModularCaptionInAnimationDuration(leftValue);
                    }
                    if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId())) {
                        //如果设置出动画后，剩余的时间小于默认时间500毫秒（出入动画默认时长500ms，不论设置不设置入动画），且此时没有入动画，则把入动画时长设置成0
                        //If after setting the animation, the remaining time is less than the default time of 500 milliseconds
                        // (the default time of the in and out animation is 500ms, regardless of whether it is set or not),
                        // and there is no animation at this time, then set the time of the in animation to 0
                        if (mSeekBar.getMaxProgress() - right < IN_OUT_ANIMATION_DEFAULT_DURATION
                                && TextUtils.isEmpty(mCurAddCaption.getModularCaptionInAnimationPackageId())) {
                            mCurAddCaption.setModularCaptionInAnimationDuration(0);
                        }
                        if (captionInfo != null) {
                            captionInfo.setMarchOutAnimationDuration(rightValue);
                        }
                        mCurAddCaption.setModularCaptionOutAnimationDuration(rightValue);
                    }
                }
                if (mVideoFragment != null) {
                    mVideoFragment.stopEngine();
                }
                m_handler.removeMessages(PLAY_VIDEO_FORM_START);
                //                m_handler.sendEmptyMessageDelayed(PLAY_VIDEO_FORM_START, 500);
            }

            @Override
            public void onTouchUpLeft(boolean leftFlag) {
                if (mCurAddCaption == null) return;
                long playTimeStart = mCurAddCaption.getInPoint();
                if (leftFlag) {
                    //组合动画或者入动画
                    //Combine animation or enter animation
                    playTimeStart = mCurAddCaption.getInPoint();
                } else {
                    //出动画
                    //Out animation
                    if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId())) {
                        playTimeStart = mCurAddCaption.getOutPoint()
                                - mCurAddCaption.getModularCaptionOutAnimationDuration() * 1000;
                    }
                }
                mVideoFragment.playVideo(playTimeStart, mCurAddCaption.getOutPoint());
            }
        });
        if (mVideoFragment != null) {
            mVideoFragment.setCaptionTextEditListener(new VideoFragment.VideoCaptionTextEditListener() {
                @Override
                public void onCaptionTextEdit() {
                    /*
                     * 字幕编辑
                     * Caption editing
                     * */
                    InputDialog inputDialog = new InputDialog(ClipCaptionStyleActivity.this, R.style.dialog,
                            new InputDialog.OnCloseListener() {
                                @Override
                                public void onClick(Dialog dialog, boolean ok) {
                                    if (ok) {
                                        InputDialog d = (InputDialog) dialog;
                                        String userInputText = d.getUserInputText();
                                        mCurAddCaption.setText(userInputText);
                                        updateCaption();
                                        int index = getCaptionIndex(mCurCaptionZVal);
                                        if (index >= 0) {
                                            mCaptionDataListClone.get(index).setText(userInputText);
                                        }
                                    }
                                }
                            });

                    if (mCurAddCaption != null) {
                        inputDialog.setUserInputText(mCurAddCaption.getText());
                    }
                    inputDialog.show();
                }
            });
            mVideoFragment.setAssetEditListener(new VideoFragment.AssetEditListener() {
                @Override
                public void onAssetDelete() {
                    curVideoClip.removeCaption(mCurAddCaption);
                    mCurAddCaption = null;
                    mVideoFragment.setCurCaption(mCurAddCaption);
                    mVideoFragment.changeCaptionRectVisible();
                    int index = getCaptionIndex(mCurCaptionZVal);
                    if (index >= 0) {
                        mCaptionDataListClone.remove(index);
                        BackupData.instance().setClipCaptionList(mCaptionDataListClone);
                        removeTimeline();
                        Intent intent = new Intent();
                        intent.putExtra("isSelectCurCaption", false);
                        setResult(RESULT_OK, intent);
                        AppManager.getInstance().finishActivity();
                    }
                }

                @Override
                public void onAssetSelected(PointF curPoint) {
                }

                @Override
                public void onAssetTranslation() {
                    if (mCurAddCaption == null) {
                        return;
                    }
                    int zVal = (int) mCurAddCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index)
                                .setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        mCaptionDataListClone.get(index).setTranslation(mCurAddCaption.getCaptionTranslation());
                    }
                }

                @Override
                public void onAssetScale() {
                    int zVal = (int) mCurAddCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index)
                                .setUsedScaleRotationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        mCaptionDataListClone.get(index).setScaleFactorX(mCurAddCaption.getScaleX());
                        mCaptionDataListClone.get(index).setScaleFactorY(mCurAddCaption.getScaleY());
                        mCaptionDataListClone.get(index).setAnchor(mCurAddCaption.getAnchorPoint());
                        mCaptionDataListClone.get(index).setRotation(mCurAddCaption.getRotationZ());
                        //                        mCaptionDataListClone.get(index).setCaptionSize(mCurAddCaption.getFontSize());
                        mCaptionDataListClone.get(index).setTranslation(mCurAddCaption.getCaptionTranslation());
                    }
                }

                @Override
                public void onAssetAlign(int alignVal) {
                    int zVal = (int) mCurAddCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setAlignVal(alignVal);
                    }
                }

                @Override
                public void onAssetHorizFlip(boolean isHorizFlip) {

                }

                @Override
                public void onOrientationChange(boolean isHorizontal) {
                    int zVal = (int) mCurAddCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index)
                                .setOrientationType(
                                        isHorizontal ? CaptionInfo.O_HORIZONTAL : CaptionInfo.O_VERTICAL);
                    }
                }
            });
            mVideoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
                @Override
                public void playBackEOF(NvsTimeline timeline) {
                    m_handler.sendEmptyMessage(VIDEOPLAYTOEOF);
                }

                @Override
                public void playStopped(NvsTimeline timeline) {
                    if (isCaptionStyleItemClick) {
                        return;
                    }
                    m_handler.sendEmptyMessage(VIDEO_PLAY_STOP);
                }

                @Override
                public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
                    mVideoFragment.setDrawRectVisible(View.GONE);
                }

                @Override
                public void streamingEngineStateChanged(int state) {

                }
            });
            mVideoFragment.setLiveWindowClickListener(new VideoFragment.OnLiveWindowClickListener() {
                @Override
                public void onLiveWindowClick() {
                    isCaptionStyleItemClick = false;
                }
            });
        }

        mVideoFragment.setBeforeAnimateStickerEditListener(
                new VideoFragment.IBeforeAnimateStickerEditListener() {
                    @Override
                    public boolean beforeTransitionCouldDo() {
                        if (mCurAddCaption == null) {
                            return false;
                        }
                        boolean b = ifCouldEditCaption();
                        if (!b) {
                            return false;
                        } else {
                            return true;
                        }
                    }

                    @Override
                    public boolean beforeScaleCouldDo() {
                        if (mCurAddCaption == null) {
                            return false;
                        }
                        boolean b = ifCouldEditCaption();
                        if (!b) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                });
    }

    private boolean ifCouldEditCaption() {
        CaptionInfo captionInfo = getCurrentCaptionInfo();
        if (captionInfo != null) {
            Map<Long, KeyFrameInfo> keyFrameInfoHashMap = captionInfo.getKeyFrameInfo();
            if (!keyFrameInfoHashMap.isEmpty()) {
                // give tips
                ToastUtil.showToastCenter(getApplicationContext(),
                        getResources().getString(R.string.tips_when_move_caption));
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private CaptionInfo getCurrentCaptionInfo() {
        if (mCurAddCaption == null) {
            return null;
        }
        int zValue = (int) mCurAddCaption.getZValue();
        int captionIndex = getCaptionIndex(zValue);
        CaptionInfo captionInfo = mCaptionDataListClone.get(captionIndex);
        return captionInfo;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.captionAssetFinish) {
            applyToAllCaption();
            BackupData.instance().setClipCaptionList(mCaptionDataListClone);
            removeTimeline();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            intent.putExtra("isSelectCurCaption", true);
            AppManager.getInstance().finishActivity();
        }
    }

    @Override
    public void onBackPressed() {
        removeTimeline();
        AppManager.getInstance().finishActivity();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CAPTIONSTYLEREQUESTLIST:
                if (isTraditionCaption) {
                    initCaptionStyleList();
                    mCaptionStyleFragment.setAssetInfolist(mTotalCaptionStyleList);
                    mSelectedStylePos = getCaptionStyleSelectedIndex();
                    mCaptionStyleFragment.setSelectedPos(mSelectedStylePos);
                    mCaptionStyleFragment.notifyDataSetChanged();
                    updateCaption();
                }
                break;
            case CAPTION_RICH_WORD:
                mRichWordList.clear();
                //花字 Flower word
                dealAssetData(NvAsset.ASSET_CAPTION_RICH_WORD, PATH_RICH_WORD, mRichWordList);
                changeAssemblyCaption(NvAsset.ASSET_CAPTION_RICH_WORD);
                updateCaption();
                break;
            case CAPTION_BUBBLE:
                mBubbleList.clear();
                //气泡 BUBBLE
                dealAssetData(NvAsset.ASSET_CAPTION_BUBBLE, PATH_BUBBLE, mBubbleList);
                changeAssemblyCaption(NvAsset.ASSET_CAPTION_BUBBLE);
                updateCaption();
                break;
            case CAPTION_IN_ANIMATION:
                mMarchInAniList.clear();
                //入场动画 in Animation
                dealAssetData(NvAsset.ASSET_CAPTION_IN_ANIMATION, PATH_IN_ANIMATION, mMarchInAniList);
                changeAssemblyCaption(NvAsset.ASSET_CAPTION_IN_ANIMATION);
                updateCaption();
                break;
            case CAPTION_OUT_ANIMATION:
                mMarchOutAniList.clear();
                //出场动画 out Animation
                dealAssetData(ASSET_CAPTION_OUT_ANIMATION, PATH_OUT_ANIMATION, mMarchOutAniList);
                changeAssemblyCaption(NvAsset.ASSET_CAPTION_OUT_ANIMATION);
                updateCaption();
                break;
            case CAPTION_ANIMATION:
                mAnimationList.clear();
                //组合动画 comb Animation
                dealAssetData(ASSET_CAPTION_ANIMATION, PATH_ANIMATION, mAnimationList);
                changeAssemblyCaption(ASSET_CAPTION_ANIMATION);
                updateCaption();
                break;

            default:
                break;
        }
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
        /*
         * 存储素材数据线程
         * Store material data thread
         * */
        new Thread(new Runnable() {
            @Override
            public void run() {
                NvAssetManager.sharedInstance().setAssetInfoToSharedPreferences(mFontType);
            }
        }).start();
    }

    private void playVideoFormStart() {
        if (mCurAddCaption != null && mVideoFragment != null) {
            mVideoFragment.stopEngine();
            long startTime = mCurAddCaption.getInPoint();
            long endTime = mCurAddCaption.getOutPoint();
            mVideoFragment.setDrawRectVisible(View.GONE);
            mVideoFragment.playVideo(startTime, endTime);
        }
    }

    /**
     * 应用字体
     * apply font
     */
    private void applyLastSelFont(String uuid) {
        String curClickUuid = mCaptionFontList.get(mFontCurClickPos).getAsset().uuid;
        if (!TextUtils.isEmpty(curClickUuid) && curClickUuid.equals(uuid)) {
            String fontPath = mCaptionFontList.get(mFontCurClickPos).getAsset().localDirPath;
            applyCaptionFont(fontPath);
            mCaptionFontFragment.setSelectedPos(mFontCurClickPos);
            mSelectedFontPos = mFontCurClickPos;
        }
    }

    private void startProgressTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    m_handler.sendEmptyMessage(ASSET_DOWNLOAD_INPROGRESS);
                }
            };
            mTimer.schedule(mTimerTask, 0, 50);
        }
    }

    private void stopProgressTimer() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void updateFontList() {
        initCaptionFontList();
        mSelectedFontPos = getCaptionFontSelectedIndex();
        mCaptionFontFragment.setFontInfolist(mCaptionFontList);
        mCaptionFontFragment.setSelectedPos(mSelectedFontPos);
        mCaptionFontFragment.notifyDataSetChanged();
    }

    private void updateFontDownloadProgress() {
        boolean isDownloadState = false;
        for (int i = 0; i < mCaptionFontList.size(); ++i) {
            NvAsset asset = mCaptionFontList.get(i).getAsset();
            if (asset == null) {
                continue;
            }
            if (asset.downloadStatus == NvAsset.DownloadStatusInProgress
                    || asset.downloadStatus == NvAsset.DownloadStatusPending) {
                isDownloadState = true;
            }
        }
        if (isDownloadState) {
            /*
             * 下载状态，通知更新数据
             * Download status, notify update data
             * */
            updateFontItem();
        }
    }

    private void updateFontItem() {
        mCaptionFontFragment.notifyDataSetChanged();
    }

    private void fontItemCopy(String uuid) {
        NvAsset curAsset = null;
        ArrayList<NvAsset> usableAsset = getFontAssetsDataList();
        for (int index = 0; index < usableAsset.size(); ++index) {
            NvAsset asset = usableAsset.get(index);
            if (asset == null) {
                continue;
            }
            if (!TextUtils.isEmpty(asset.uuid) && uuid.equals(asset.uuid)) {
                curAsset = asset;
                break;
            }
        }

        for (int i = 0; i < mCaptionFontList.size(); ++i) {
            NvAsset asset = mCaptionFontList.get(i).getAsset();
            if (asset == null) {
                continue;
            }
            if (curAsset != null && !TextUtils.isEmpty(asset.uuid) && asset.uuid.equals(uuid)) {
                mCaptionFontList.get(i).getAsset().copyAsset(curAsset);
            }
        }
    }

    private void fontListRequestFail() {
        ToastUtil.showToast(this, this.getResources().getString(R.string.check_network));
    }

    private void fontDownloadFail() {
        ToastUtil.showToast(this, this.getResources().getString(R.string.download_failed));
    }

    private void applyToAllCaption() {
        int index = getCaptionIndex(mCurCaptionZVal);
        if (index < 0) {
            return;
        }
        int count = mCaptionDataListClone.size();
        CaptionInfo curCaptionInfo = mCaptionDataListClone.get(index);
        for (int i = 0; i < count; ++i) {
            if (i == index) {
                continue;
            }
            if (bIsStyleUuidApplyToAll) {
                mCaptionDataListClone.get(i).setCaptionStyleUuid(curCaptionInfo.getCaptionStyleUuid());
            }
            if (bIsCaptionColorApplyToAll) {
                mCaptionDataListClone.get(i).setCaptionColor(curCaptionInfo.getCaptionColor());
                mCaptionDataListClone.get(i).setCaptionColorAlpha(curCaptionInfo.getCaptionColorAlpha());
                mCaptionDataListClone.get(i).setUsedColorFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
            }
            if (bIsOutlineApplyToAll) {
                mCaptionDataListClone.get(i).setHasOutline(curCaptionInfo.isHasOutline());
                mCaptionDataListClone.get(i).setOutlineColor(curCaptionInfo.getOutlineColor());
                mCaptionDataListClone.get(i).setOutlineColorAlpha(curCaptionInfo.getOutlineColorAlpha());
                mCaptionDataListClone.get(i).setOutlineWidth(curCaptionInfo.getOutlineWidth());
                mCaptionDataListClone.get(i).setUsedOutlineFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
            }
            if (bIsFontApplyToAll) {
                mCaptionDataListClone.get(i).setCaptionFont(curCaptionInfo.getCaptionFont());
                mCaptionDataListClone.get(i).setBold(curCaptionInfo.isBold());
                mCaptionDataListClone.get(i).setItalic(curCaptionInfo.isItalic());
                mCaptionDataListClone.get(i).setShadow(curCaptionInfo.isShadow());
                mCaptionDataListClone.get(i).setUnderline(curCaptionInfo.isUnderline());
                mCaptionDataListClone.get(i).setUsedUnderlineFlag(curCaptionInfo.getUsedUnderlineFlag());
                mCaptionDataListClone.get(i).setUsedIsBoldFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                mCaptionDataListClone.get(i).setUsedIsItalicFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                mCaptionDataListClone.get(i).setUsedShadowFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
            }
            //            if (bIsSizeApplyToAll) {
            //                mCaptionDataListClone.get(i).setCaptionSize(curCaptionInfo.getCaptionSize());
            //            }

            if (bIsLetterSpacingApplyToAll) {
                mCaptionDataListClone.get(i)
                        .setUsedLetterSpacingFlag(curCaptionInfo.getUsedLetterSpacingFlag());
                mCaptionDataListClone.get(i).setLetterSpacing(curCaptionInfo.getLetterSpacing());
                mCaptionDataListClone.get(i).setUsedLetterSpacingFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                //同时设置行间距 Also set the line spacing
                mCaptionDataListClone.get(i).setLineSpacing(curCaptionInfo.getLineSpacing());
            }
            if (bIsCaptionBackgroundApplyToAll) {
                mCaptionDataListClone.get(i).setCaptionBackground(curCaptionInfo.getCaptionBackground());
                mCaptionDataListClone.get(i)
                        .setCaptionBackgroundAlpha(curCaptionInfo.getCaptionBackgroundAlpha());
                mCaptionDataListClone.get(i)
                        .setCaptionBackgroundRadius(curCaptionInfo.getCaptionBackgroundRadius());
                mCaptionDataListClone.get(i).setUsedBackgroundFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                mCaptionDataListClone.get(i).setUsedBackgroundRadiusFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
            }
        }
        if (bIsPositionApplyToAll) {
            updateCaptionPosition();
        }
    }

    private void updateCaptionPosition() {
        NvsClipCaption caption = curVideoClip.getFirstCaption();
        while (caption != null) {
            if (caption.getCategory() == NvsClipCaption.THEME_CATEGORY
                    && caption.getRoleInTheme()
                    != NvsClipCaption.ROLE_IN_THEME_GENERAL) {//主题字幕不作处理 Subject subtitles are not processed
                caption = curVideoClip.getNextCaption(caption);
                continue;
            }
            int zVal = (int) caption.getZValue();
            if (mCurCaptionZVal == zVal) {
                caption = curVideoClip.getNextCaption(caption);
                continue;
            }
            List<PointF> list = caption.getBoundingRectangleVertices();
            if (list == null || list.size() < 4) {
                caption = curVideoClip.getNextCaption(caption);
                continue;
            }

            /*
             * 字幕对齐方式，包括左对齐，右对齐，水平居中，上对齐，底部对齐，垂直居中
             * Caption alignment, including left, right, centered horizontally，top, bottom, centered vertically
             * */
            int index = getCaptionIndex(zVal);
            switch (mAlignType) {
                case CAPTION_ALIGNLEFT:
                    Collections.sort(list, new Util.PointXComparator());
                    float xLeftOffset = -(mTimeline.getVideoRes().imageWidth / 2 + list.get(0).x);
                    caption.translateCaption(new PointF(xLeftOffset, 0));
                    break;
                case CAPTION_ALIGNHORIZCENTER:
                    Collections.sort(list, new Util.PointXComparator());
                    float xHorizCenterOffset = -((list.get(3).x - list.get(0).x) / 2 + list.get(0).x);
                    caption.translateCaption(new PointF(xHorizCenterOffset, 0));
                    break;
                case CAPTION_ALIGNRIGHT:
                    Collections.sort(list, new Util.PointXComparator());
                    float xRightOffset = mTimeline.getVideoRes().imageWidth / 2 - list.get(3).x;
                    caption.translateCaption(new PointF(xRightOffset, 0));
                    break;
                case CAPTION_ALIGNTOP:
                    Collections.sort(list, new Util.PointYComparator());
                    float yTopdis = list.get(3).y - list.get(0).y;
                    float yTopOffset = mTimeline.getVideoRes().imageHeight / 2 - list.get(0).y - yTopdis;
                    caption.translateCaption(new PointF(0, yTopOffset));
                    break;
                case CAPTION_ALIGNVERTCENTER:
                    Collections.sort(list, new Util.PointYComparator());
                    float yVertCenterOffset = -((list.get(3).y - list.get(0).y) / 2 + list.get(0).y);
                    caption.translateCaption(new PointF(0, yVertCenterOffset));
                    break;
                case CAPTION_ALIGNBOTTOM:
                    Collections.sort(list, new Util.PointYComparator());
                    float yBottomdis = list.get(3).y - list.get(0).y;
                    float yBottomOffset =
                            -(mTimeline.getVideoRes().imageHeight / 2 + list.get(3).y - yBottomdis);
                    caption.translateCaption(new PointF(0, yBottomOffset));
                    break;
                default:
                    break;
            }
            if (index >= 0) {

                mCaptionDataListClone.get(index).setTranslation(caption.getCaptionTranslation());
                mCaptionDataListClone.get(index).setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
            }
            caption = curVideoClip.getNextCaption(caption);
        }
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mCurAddCaption = null;
        mTimeline = null;
        m_handler.removeCallbacksAndMessages(null);
        stopProgressTimer();
    }

    private void initTabLayout() {
        String[] assetName;
        if (isTraditionCaption) {
            //传统字幕 Traditional subtitles
            assetName = getResources().getStringArray(R.array.clip_captionEdit);
            mCaptionStyleTab.setSelectedTabIndicatorColor(getResources().getColor(R.color.blue_4a));
        } else {
            //拼装字幕 Assemble subtitles
            assetName = getResources().getStringArray(R.array.clip_pieced_caption);
            mCaptionStyleTab.setSelectedTabIndicatorColor(getResources().getColor(R.color.red_ff64));
        }
        for (int i = 0; i < assetName.length; i++) {
            TextView textView = new TextView(this);
            textView.setText(assetName[i]);
            textView.setTextSize(12);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(getResources().getColor(R.color.gray_90));
            mCaptionStyleTab.addTab(mCaptionStyleTab.newTab().setCustomView(textView));
        }
        initCaptionTabFragment();
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mAssetFragmentsArray.get(position);
            }

            @Override
            public int getCount() {
                return mAssetFragmentsArray.size();
            }
        });
        /*
         * 添加tab切换的监听事件
         * Add a tab switch to listen for events
         * */
        mCaptionStyleTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                /*
                 * 当前选中的tab的位置，切换到相应的fragment
                 * Position of the currently selected tab, switch to the corresponding fragment
                 * */
                int nowPosition = tab.getPosition();
                if (nowPosition == 1 && mCurAddCaption != null) {
                    animationFragmentSelect();
                } else {
                    displaySeekBar(false);
                }
                TextView textView = (TextView) tab.getCustomView();
                if (isTraditionCaption && textView != null) {
                    textView.setTextColor(getResources().getColor(R.color.blue_4a));
                } else if (textView != null) {
                    textView.setTextColor(getResources().getColor(R.color.red_ff64));
                }
                mViewPager.setCurrentItem(nowPosition);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView textView = (TextView) tab.getCustomView();
                if (textView != null) {
                    textView.setTextColor(getResources().getColor(R.color.gray_90));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private boolean animationFragmentFirstSelect = true;

    /**
     * 动画fragment被选中的处理逻辑。
     * The processing logic of the animation fragment being selected
     */
    private void animationFragmentSelect() {
        if (animationFragmentFirstSelect) {
            //如果是第一次进入且被选中动画的fragment
            //If it is the first time to enter and the fragment of the animation is selected
            if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionAnimationPackageId())) {
                displayAnimationProgress(true, NvAsset.ASSET_CAPTION_ANIMATION);
            } else {
                if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId()) &&
                        !TextUtils.isEmpty(mCurAddCaption.getModularCaptionInAnimationPackageId())) {
                    int maxDuration =
                            (int) ((mCurAddCaption.getOutPoint() - mCurAddCaption.getInPoint()) / 1000);
                    if (maxDuration - mCurAddCaption.getModularCaptionInAnimationDuration()
                            < IN_OUT_ANIMATION_DEFAULT_DURATION) {
                        //如果设置入动画后，剩余的时间小于默认时间500毫秒（出入动画默认时长500ms，不论设置不设置出动画），且此时没有出动画，则把出动画时长设置成0
                        //If the remaining time after setting the animation is less than the default time of 500 milliseconds
                        // (the default time of the animation is 500ms, regardless of whether the animation is set or not)
                        // , and there is no animation at this time, set the animation duration to 0
                        displayAnimationProgress(true, NvAsset.ASSET_CAPTION_OUT_ANIMATION);
                        displayAnimationProgress(true, NvAsset.ASSET_CAPTION_IN_ANIMATION);
                    } else {
                        displayAnimationProgress(true, NvAsset.ASSET_CAPTION_IN_ANIMATION);
                        displayAnimationProgress(true, NvAsset.ASSET_CAPTION_OUT_ANIMATION);
                    }
                    //第一次SeekBar设置左右进度的时候，如果存在遮盖，则后设置的会被显示也已被拖动。
                    //When SeekBar sets the left and right progress for the first time, if there is a cover, the later set will be displayed and dragged.
                    return;
                }
                if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId())) {
                    displayAnimationProgress(true, NvAsset.ASSET_CAPTION_OUT_ANIMATION);
                }
                if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionInAnimationPackageId())) {
                    displayAnimationProgress(true, NvAsset.ASSET_CAPTION_IN_ANIMATION);
                }
            }
            animationFragmentFirstSelect = false;
        } else {
            //动画tab被选中 Animation tab is selected
            displaySeekBar(!TextUtils.isEmpty(mCurAddCaption.getModularCaptionAnimationPackageId()) ||
                    !TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId())
                    || !TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId()));
        }
    }

    private void initCaptionTabFragment() {
        if (isTraditionCaption) {
            mCaptionStyleFragment = initCaptionStyleFragment();//字幕样式 Subtitle style
            mAssetFragmentsArray.add(mCaptionStyleFragment);
        } else {
            mAssetFragmentsArray.add(initRichWordFragment());//花字 Flowery character
            mAssetFragmentsArray.add(initAnimationFragment());//动画 animation
            mAssetFragmentsArray.add(initBubbleFragment());//气泡 bubble
        }
        mCaptionColorFragment = initCaptionColorFragment();//填充 fill
        mAssetFragmentsArray.add(mCaptionColorFragment);
        mCaptionOutlineFragment = initCaptionOutlineFragment();//描边 stroke
        mAssetFragmentsArray.add(mCaptionOutlineFragment);
        mCaptionBackgroundFragment = initCaptionBackgroundFragment();//背景 background
        mAssetFragmentsArray.add(mCaptionBackgroundFragment);

        mCaptionFontFragment = initCaptionFontFragment();//字体 font
        mAssetFragmentsArray.add(mCaptionFontFragment);
        mCaptionSizeFragment = initCaptionSizeFragment();
        mCaptionLetterSpacingFragment = initCaptionLetterSpacingFragment();//间距 spacing
        mAssetFragmentsArray.add(mCaptionLetterSpacingFragment);
        mCaptionPositionFragment = initCaptionPositionFragment();//位置 position
        mAssetFragmentsArray.add(mCaptionPositionFragment);
    }

    private void initAssetData() {
        Intent intent = getIntent();
        if (intent != null) {
            isTraditionCaption = intent.getBooleanExtra("tradition_caption", true);
        }
        mClipArrayList = BackupData.instance().cloneClipInfoData();
        mCurClipIndex = BackupData.instance().getClipIndex();
        if (mCurClipIndex < 0 || mCurClipIndex >= mClipArrayList.size()) {
            return;
        }
        mClipInfo = mClipArrayList.get(mCurClipIndex);
        mTimeline = TimelineUtil.createSingleClipTimeline(mClipInfo, true);
        if (mTimeline == null) {
            return;
        }
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack != null) {
            curVideoClip = videoTrack.getClipByIndex(0);
        }
        if (curVideoClip == null) {
            return;
        }

        if (mTimeline == null) {
            return;
        }
        mCurCaptionZVal = BackupData.instance().getCaptionZVal();
        mCaptionDataListClone = BackupData.instance().cloneClipCaptionData();
        TimelineUtil.applyClipCaption(curVideoClip, mCaptionDataListClone, 0);
        selectCaption();
        mAssetManager = NvAssetManager.sharedInstance();
        if (isTraditionCaption) {
            mTitleBar.setTextCenter(R.string.traditional_caption);
            mTotalCaptionStyleList = new ArrayList<>();
            mAssetManager.searchLocalAssets(mCaptionStyleType);
            String bundlePath = "captionstyle";
            mAssetManager.searchReservedAssets(mCaptionStyleType, bundlePath);
            initCaptionStyleList();
        } else {
            mTitleBar.setTextCenter(R.string.pieced_together_caption);
            initPiecedTogetherCaption();
        }
        mCaptionBackgroundList = new ArrayList<>();
        mAssetFragmentsArray = new ArrayList<>();
        mCaptionColorList = new ArrayList<>();
        mCaptionOutlineColorList = new ArrayList<>();
        mCaptionFontList = new ArrayList<>();
        mServerCaptionFontList = new ArrayList<>();

        mAssetManager.searchLocalAssets(mFontType);//查找字体文件 Find font file
        mAssetManager.searchReservedAssets(mFontType, "font");
        assetDataRequest();
        initCaptionBackgroundList();
        initCaptionColorList();
        initCaptionOutlineColorList();
    }

    private void checkInit() {
        if (mCurAddCaption != null) {
            long duration = mCurAddCaption.getOutPoint() - mCurAddCaption.getInPoint();
            mSeekBar.setMaxProgress((int) (duration / 1000));
            mMaxDuration = duration;
            mSelectedRichPos =
                    getTargetPosition(mRichWordList, mCurAddCaption.getModularCaptionRendererPackageId());
            mSelectedBubblePos =
                    getTargetPosition(mBubbleList, mCurAddCaption.getModularCaptionContextPackageId());
            mSelectedAnimationPos =
                    getTargetPosition(mAnimationList, mCurAddCaption.getModularCaptionAnimationPackageId());
            mSelectedInAnimationPos = getTargetPosition(mMarchInAniList,
                    mCurAddCaption.getModularCaptionInAnimationPackageId());
            mSelectedOutAnimationPos = getTargetPosition(mMarchOutAniList,
                    mCurAddCaption.getModularCaptionOutAnimationPackageId());
        }
        mSeekBar.setTransformText(1000, 1);
    }

    private void initCaptionStyleList() {
        mTotalCaptionStyleList.clear();
        ArrayList<NvAsset> usableAsset = getAssetsDataList(mCaptionStyleType);
        String jsonBundlePath = "captionstyle/info.json";
        ArrayList<ParseJsonFile.FxJsonFileInfo.JsonFileInfo> infoLists =
                ParseJsonFile.readBundleFxJsonFile(this, jsonBundlePath);
        if (infoLists != null) {
            for (ParseJsonFile.FxJsonFileInfo.JsonFileInfo jsonFileInfo : infoLists) {
                for (NvAsset asset : usableAsset) {
                    if (asset == null) {
                        continue;
                    }
                    if (TextUtils.isEmpty(asset.uuid)) {
                        continue;
                    }

                    /*
                     * assets路径下的字幕样式包
                     * Caption style package under assets path
                     * */
                    if (asset.isReserved && asset.uuid.equals(jsonFileInfo.getFxPackageId())) {
                        if (Util.isZh(mContext)) {
                            asset.name = jsonFileInfo.getName_Zh();
                        } else {
                            asset.name = jsonFileInfo.getName();
                        }

                        asset.aspectRatio = Integer.parseInt(jsonFileInfo.getFitRatio());
                        StringBuilder coverPath = new StringBuilder("file:///android_asset/captionstyle/");
                        coverPath.append(jsonFileInfo.getImageName());
                        asset.coverUrl = coverPath.toString();
                    }
                }
            }
        }

        int ratio = TimelineData.instance().getMakeRatio();
        for (NvAsset asset : usableAsset) {
            if (asset == null) {
                continue;
            }
            if (TextUtils.isEmpty(asset.uuid)) {
                continue;
            }
            // TODO: 2024/9/11 非通用模版采取判断画幅比例问题
            if (asset.ratioFlag == 0 && (ratio & asset.aspectRatio) == 0) {
                /*
                 * 制作比例不适配，不加载
                 * Production proportions do not fit, do not load
                 * */
                continue;
            }
            AssetItem assetItem = new AssetItem();
            assetItem.setAsset(asset);
            assetItem.setAssetMode(AssetItem.ASSET_LOCAL);
            mTotalCaptionStyleList.add(assetItem);
        }
        AssetItem assetItem = new AssetItem();
        NvAsset asset = new NvAsset();
        asset.name = getString(R.string.makeup_null);
        assetItem.setImageRes(R.mipmap.captionstyle_no);
        assetItem.setAssetMode(AssetItem.ASSET_NONE);
        assetItem.setAsset(asset);
        mTotalCaptionStyleList.add(0, assetItem);
    }

    private void initPiecedTogetherCaption() {
        if (mRichWordList == null) {
            mRichWordList = new ArrayList<>();
        }
        mRichWordList.clear();
        //花字 richWord
        dealAssetData(NvAsset.ASSET_CAPTION_RICH_WORD, PATH_RICH_WORD, mRichWordList);
        if (mBubbleList == null) {
            mBubbleList = new ArrayList<>();
        }
        mBubbleList.clear();
        //气泡 bubble
        dealAssetData(NvAsset.ASSET_CAPTION_BUBBLE, PATH_BUBBLE, mBubbleList);
        if (mAnimationList == null) {
            mAnimationList = new ArrayList<>();
        }
        mAnimationList.clear();
        //组合动画 comb animation
        dealAssetData(ASSET_CAPTION_ANIMATION, PATH_ANIMATION, mAnimationList);
        if (mMarchInAniList == null) {
            mMarchInAniList = new ArrayList<>();
        }
        mMarchInAniList.clear();
        //入场动画 in animation
        dealAssetData(NvAsset.ASSET_CAPTION_IN_ANIMATION, PATH_IN_ANIMATION, mMarchInAniList);
        if (mMarchOutAniList == null) {
            mMarchOutAniList = new ArrayList<>();
        }
        mMarchOutAniList.clear();
        //出场动画 out animation
        dealAssetData(ASSET_CAPTION_OUT_ANIMATION, PATH_OUT_ANIMATION, mMarchOutAniList);
    }

    /**
     * 处理asset资源数据
     * handle assets
     *
     * @param assetType int 资源类型
     * @param assetPath String 资源路径
     * @param assetList List<AssetItem> 资源列表
     */
    private void dealAssetData(int assetType, String assetPath, List<AssetItem> assetList) {
        mAssetManager.searchLocalAssets(assetType);
        mAssetManager.searchReservedAssets(assetType, assetPath);
        ArrayList<NvAsset> usableAsset = getAssetsDataList(assetType);
        String jsonBundlePath = assetPath + "/info.json";
        boolean isAnimation = false;
        if (assetType == ASSET_CAPTION_OUT_ANIMATION || assetType == ASSET_CAPTION_IN_ANIMATION
                || assetType == ASSET_CAPTION_ANIMATION) {
            isAnimation = true;
        }
        ArrayList<ParseJsonFile.FxJsonFileInfo.JsonFileInfo> infoLists =
                ParseJsonFile.readBundleFxJsonFile(this, jsonBundlePath);
        if (infoLists != null) {
            for (ParseJsonFile.FxJsonFileInfo.JsonFileInfo jsonFileInfo : infoLists) {
                for (NvAsset asset : usableAsset) {
                    if (asset == null) {
                        continue;
                    }
                    if (TextUtils.isEmpty(asset.uuid)) {
                        continue;
                    }
                    if (asset.isReserved && asset.uuid.equals(jsonFileInfo.getFxPackageId())) {
                        if (isZh(this)) {
                            asset.name = jsonFileInfo.getName_Zh();
                        } else {
                            asset.name = jsonFileInfo.getName();
                        }
                        if (!TextUtils.isEmpty(jsonFileInfo.getFitRatio())) {
                            try {
                                asset.aspectRatio = Integer.parseInt(jsonFileInfo.getFitRatio());
                            } catch (Exception e) {
                                Log.e(TAG, "Exception=" + e);
                            }
                        }
                        StringBuilder coverPath = new StringBuilder();
                        if (isAnimation) {
                            coverPath.append("asset://android_asset/");
                        } else {
                            coverPath.append("file:///android_asset/");
                        }
                        coverPath.append(assetPath);
                        coverPath.append("/");
                        coverPath.append(jsonFileInfo.getImageName());
                        asset.coverUrl = coverPath.toString();
                    }
                }
            }
        }
        if (assetList == null) {
            return;
        }
        AssetItem firstItem = new AssetItem();
        NvAsset firstAsset = new NvAsset();
        firstAsset.name = getString(R.string.timeline_fx_none);
        if (isAnimation) {
            firstItem.setImageRes(R.mipmap.square_clear);
        } else {
            firstItem.setImageRes(R.mipmap.captionstyle_no);
        }

        firstItem.setAssetMode(AssetItem.ASSET_NONE);
        firstItem.setAsset(firstAsset);
        assetList.add(firstItem);
        int ratio = TimelineData.instance().getMakeRatio();
        for (NvAsset asset : usableAsset) {
            if (asset == null) {
                continue;
            }
            if (TextUtils.isEmpty(asset.uuid)) {
                continue;
            }
            if ((ratio & asset.aspectRatio) == 0) {
                /*
                 * 制作比例不适配，不加载
                 * Production proportions do not fit, do not load
                 * */
                continue;
            }
            AssetItem assetItem = new AssetItem();
            assetItem.setAsset(asset);
            assetItem.setAssetMode(AssetItem.ASSET_LOCAL);
            assetList.add(assetItem);
        }
    }

    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setFragmentLoadFinisedListener(
                new VideoFragment.OnFragmentLoadFinisedListener() {
                    @Override
                    public void onLoadFinished() {
                        seekTimeline(BackupData.instance().getCurSeekTimelinePos());
                        if (mCurAddCaption == null) {
                            selectCaption();
                        }

                        int captionIndex = getCaptionIndex((int) mCurAddCaption.getZValue());
                        reloadKeyFrame(captionIndex);

                        mCaptionAssetFinish.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (mCurAddCaption != null) {
                                    int alignVal = mCurAddCaption.getTextAlignment();
                                    mVideoFragment.setAlignIndex(alignVal);

                                    long duration = mStreamingContext.getTimelineCurrentPosition(mTimeline)
                                            - mCurAddCaption.getInPoint();
                                    mCurAddCaption.setCurrentKeyFrameTime(duration);

                                    mVideoFragment.setCurCaption(mCurAddCaption);
                                    mVideoFragment.updateCaptionCoordinate(mCurAddCaption);
                                    mVideoFragment.changeCaptionRectVisible();
                                    mCurAddCaption.removeKeyframeAtTime(TRANS_X, duration);
                                    mCurAddCaption.removeKeyframeAtTime(TRANS_Y, duration);
                                    mCurAddCaption.removeKeyframeAtTime(SCALE_X, duration);
                                    mCurAddCaption.removeKeyframeAtTime(SCALE_Y, duration);
                                    mCurAddCaption.removeKeyframeAtTime(ROTATION_Z, duration);
                                }
                            }
                        }, 100);
                    }
                });
        mVideoFragment.setTimeline(mTimeline);
        /*
         * 设置字幕模式
         * Set caption mode
         * */
        mVideoFragment.setEditMode(Constants.EDIT_MODE_CAPTION);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        bundle.putBoolean("playBarVisible", false);
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.spaceLayout, mVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);
    }

    /*
     * 获取下载到手机路径下的素材，包括assets路径下自带的素材
     * Get the material downloaded to the mobile phone path,
     * including the material that comes with the assets path
     * */
    private ArrayList<NvAsset> getAssetsDataList(int assetType) {
        return mAssetManager.getUsableAssets(assetType, NvAsset.AspectRatio_All, 0);
    }

    /*
     * 获取字体数据列表
     * Get font data list
     * */
    private ArrayList<NvAsset> getFontAssetsDataList() {
        return mAssetManager.getRemoteAssetsWithPage(mFontType, NvAsset.AspectRatio_All, 0, 1, 10);
    }

    private void assetDataRequest() {
        mAssetManager.downloadRemoteAssetsInfo(mFontType, 1, NvAsset.AspectRatio_All, 0, 1, 10);
        mAssetManager.setManagerlistener(new NvAssetManager.NvAssetManagerListener() {
            @Override
            public void onRemoteAssetsChanged(boolean hasNext) {
                m_handler.sendEmptyMessage(ASSET_LIST_REQUEST_SUCCESS);
            }

            @Override
            public void onRemoteAssetsChanged(List<NvAsset> assetDataList, boolean hasNext) {

            }

            @Override
            public void onGetRemoteAssetsFailed() {
                m_handler.sendEmptyMessage(ASSET_LIST_REQUEST_FAILED);
            }

            @Override
            public void onDownloadAssetProgress(String uuid, int progress) {
                sendHandleMsg(uuid, ASSET_DOWNLOAD_START_TIMER);
            }

            @Override
            public void onDonwloadAssetFailed(String uuid) {
                sendHandleMsg(uuid, ASSET_DOWNLOAD_FAILED);
            }

            @Override
            public void onDonwloadAssetSuccess(String uuid) {
                sendHandleMsg(uuid, ASSET_DOWNLOAD_SUCCESS);
            }

            @Override
            public void onFinishAssetPackageInstallation(String uuid) {

            }

            @Override
            public void onFinishAssetPackageUpgrading(String uuid) {

            }
        });
    }

    private void sendHandleMsg(String uuid, int what) {
        Message sendMsg = m_handler.obtainMessage();
        if (sendMsg == null) {
            sendMsg = new Message();
        }
        sendMsg.what = what;
        sendMsg.obj = uuid;
        m_handler.sendMessage(sendMsg);
    }

    private void applyCaptionFont(String fontPath) {
        if (mCurAddCaption != null) {
            mCurAddCaption.setFontByFilePath(fontPath);
            int index = getCaptionIndex(mCurCaptionZVal);
            if (index >= 0) {
                mCaptionDataListClone.get(index).setCaptionFont(fontPath);
            }
            updateCaption();
        }
    }

    private void initCaptionColorList() {
        for (int index = 0; index < CaptionColors.length; ++index) {
            mCaptionColorList.add(new CaptionColorInfo(CaptionColors[index], false));
        }
    }

    private void initCaptionOutlineColorList() {
        for (int index = 0; index < CaptionColors.length; ++index) {
            mCaptionOutlineColorList.add(new CaptionColorInfo(CaptionColors[index], false));
        }
        mCaptionOutlineColorList.add(0, new CaptionColorInfo("", false));
    }

    private void initCaptionBackgroundList() {
        for (int index = 0; index < CaptionColors.length; ++index) {
            mCaptionBackgroundList.add(new CaptionColorInfo(CaptionColors[index], false));
        }
        mCaptionBackgroundList.add(0, new CaptionColorInfo("", false));
    }

    private void initCaptionFontList() {
        mCaptionFontList.clear();
        AssetItem noneFontInfo = new AssetItem();
        noneFontInfo.setAsset(new NvAsset());
        noneFontInfo.setImageRes(R.mipmap.captionstyle_no);
        noneFontInfo.setAssetMode(AssetItem.ASSET_NONE);
        mCaptionFontList.add(noneFontInfo);
        mCaptionFontList.addAll(mServerCaptionFontList);
        //        ArrayList<NvAsset> usableAsset = getFontAssetsDataList();
        //        NvAsset item;
        //        for (int index = 0; index < usableAsset.size(); ++index) {
        //            item = usableAsset.get(index);
        //            AssetItem localFontInfo = new AssetItem();
        //            localFontInfo.setAsset(item);
        //            localFontInfo.setAssetMode(AssetItem.ASSET_LOCAL);
        //            mCaptionFontList.add(localFontInfo);
        //        }
        //        usableAsset = getAssetsDataList(mFontType);
        //        for (int index = 0; index < usableAsset.size(); ++index) {
        //            item = usableAsset.get(index);
        //            if (!hasAddFont(item.uuid)) {
        //                item.coverUrl = "file:///android_asset/font/" + item.uuid + ".png";
        //                AssetItem localFontInfo = new AssetItem();
        //                localFontInfo.setAsset(item);
        //                localFontInfo.setAssetMode(AssetItem.ASSET_BUILTIN);
        //                mCaptionFontList.add(localFontInfo);
        //            }
        //        }

        for (AssetItem assetItem : mCaptionFontList) {
            if (!TextUtils.isEmpty(assetItem.getAsset().localDirPath)) {
                NvsStreamingContext.getInstance().registerFontByFilePath(assetItem.getAsset().localDirPath);
            } else if (!TextUtils.isEmpty(assetItem.getAsset().bundledLocalDirPath)) {
                NvsStreamingContext.getInstance()
                        .registerFontByFilePath(assetItem.getAsset().bundledLocalDirPath);
            }
        }
    }

    private boolean hasAddFont(String fontUuid) {
        if (TextUtils.isEmpty(fontUuid)) {
            return true;
        }
        if (mCaptionFontList != null) {
            for (AssetItem item : mCaptionFontList) {
                if (fontUuid.equals(item.getAsset().uuid)) {
                    return true;
                }
            }
        }
        return false;
    }

    private CaptionRichWordFragment initRichWordFragment() {
        mRichWordFragment = new CaptionRichWordFragment();

        mRichWordFragment.setAssetInfoList(mRichWordList);
        if (mCurAddCaption != null) {
            mSelectedRichPos =
                    getTargetPosition(mRichWordList, mCurAddCaption.getModularCaptionRendererPackageId());
        }
        mRichWordFragment.setCaptionStateListener(new CaptionRichWordFragment.OnCaptionStateListener() {
            @Override
            public void onFragmentLoadFinished() {
                mRichWordFragment.setSelectedPos(mSelectedRichPos);
            }

            @Override
            public void onLoadMore() {
                if (m_waitFlag) {
                    return;
                }
                mVideoFragment.stopEngine();
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.pieced_together_caption);
                bundle.putInt("assetType", NvAsset.ASSET_CAPTION_RICH_WORD);
                bundle.putInt("categoryId", CATEGORY_RICH_WORD);
                AppManager.getInstance()
                        .jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                AssetDownloadActivity.class, bundle, CAPTION_RICH_WORD);
                m_waitFlag = true;
            }

            @Override
            public void onItemClick(int pos) {
                if (pos < 0 || pos >= mRichWordList.size()) {
                    return;
                }
                applyAssemblyCaption(pos, NvAsset.ASSET_CAPTION_RICH_WORD,
                        mRichWordList.get(pos).getAsset());
            }
        });
        return mRichWordFragment;
    }

    private CaptionBubbleFragment initBubbleFragment() {
        mBubbleFragment = new CaptionBubbleFragment();
        mBubbleFragment.setAssetInfoList(mBubbleList);
        if (mCurAddCaption != null) {
            mSelectedBubblePos =
                    getTargetPosition(mBubbleList, mCurAddCaption.getModularCaptionContextPackageId());
        }
        mBubbleFragment.setCaptionStateListener(new CaptionBubbleFragment.OnCaptionStateListener() {
            @Override
            public void onFragmentLoadFinished() {
                mBubbleFragment.setSelectedPos(mSelectedBubblePos);
            }

            @Override
            public void onLoadMore() {
                if (m_waitFlag) {
                    return;
                }
                mVideoFragment.stopEngine();
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.more_caption_bubble);
                bundle.putInt("assetType", NvAsset.ASSET_CAPTION_BUBBLE);
                bundle.putInt("categoryId", CATEGORY_BUBBLE);
                AppManager.getInstance()
                        .jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                AssetDownloadActivity.class, bundle, CAPTION_BUBBLE);
                m_waitFlag = true;
            }

            @Override
            public void onItemClick(int pos) {
                if (pos < 0 || pos >= mBubbleList.size()) {
                    return;
                }
                applyAssemblyCaption(pos, NvAsset.ASSET_CAPTION_BUBBLE, mBubbleList.get(pos).getAsset());
            }
        });
        return mBubbleFragment;
    }

    private CaptionAnimationFragment initAnimationFragment() {
        mAnimationFragment = new CaptionAnimationFragment();
        mAnimationFragment.setAssetList(mAnimationList, mMarchInAniList, mMarchOutAniList);
        if (mCurAddCaption != null) {
            mSelectedAnimationPos =
                    getTargetPosition(mAnimationList, mCurAddCaption.getModularCaptionAnimationPackageId());
            mSelectedInAnimationPos = getTargetPosition(mMarchInAniList,
                    mCurAddCaption.getModularCaptionInAnimationPackageId());
            mSelectedOutAnimationPos = getTargetPosition(mMarchOutAniList,
                    mCurAddCaption.getModularCaptionOutAnimationPackageId());
        }
        mAnimationFragment.setCaptionStateListener(
                new CaptionAnimationFragment.OnCaptionStateListener() {
                    @Override
                    public void onFragmentLoadFinished() {
                        mAnimationFragment.setSelectedPos(mSelectedAnimationPos, mSelectedInAnimationPos,
                                mSelectedOutAnimationPos);
                        mAnimationFragment.checkSelectedTab();
                    }

                    @Override
                    public void onLoadMore(int typ) {
                        if (m_waitFlag) {
                            return;
                        }
                        mVideoFragment.stopEngine();
                        Bundle bundle = new Bundle();
                        bundle.putInt("titleResId", R.string.moreCaptionStyle);
                        bundle.putInt("assetType", typ);
                        int requestCode;
                        if (typ == ASSET_CAPTION_IN_ANIMATION) {
                            bundle.putInt("categoryId", CATEGORY_IN_ANIMATION);
                            requestCode = CAPTION_IN_ANIMATION;
                        } else if (typ == ASSET_CAPTION_OUT_ANIMATION) {
                            bundle.putInt("categoryId", CATEGORY_OUT_ANIMATION);
                            requestCode = CAPTION_OUT_ANIMATION;
                        } else {
                            bundle.putInt("categoryId", CATEGORY_ANIMATION);
                            requestCode = CAPTION_ANIMATION;
                        }
                        AppManager.getInstance()
                                .jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                        AssetDownloadActivity.class, bundle, requestCode);
                        m_waitFlag = true;
                    }

                    @Override
                    public void onItemClick(int pos, int type) {
                        if (pos < 0) {
                            return;
                        }
                        if (type == ASSET_CAPTION_ANIMATION && pos < mAnimationList.size()) {
                            applyAssemblyCaption(pos, ASSET_CAPTION_ANIMATION,
                                    mAnimationList.get(pos).getAsset());
                        } else if (type == ASSET_CAPTION_OUT_ANIMATION && pos < mMarchOutAniList.size()) {
                            applyAssemblyCaption(pos, ASSET_CAPTION_OUT_ANIMATION,
                                    mMarchOutAniList.get(pos).getAsset());
                        } else if (pos < mMarchInAniList.size()) {
                            applyAssemblyCaption(pos, NvAsset.ASSET_CAPTION_IN_ANIMATION,
                                    mMarchInAniList.get(pos).getAsset());
                        }
                        // displaySeekBar(pos > 0);
                        displayAnimationProgress(pos > 0, type);
                    }
                });
        return mAnimationFragment;
    }

    /**
     * 应用拼装字幕
     * apply assembly caption
     */
    private void applyAssemblyCaption(int pos, int type, NvAsset asset) {
        if (mCurAddCaption == null || asset == null) {
            return;
        }
        isCaptionStyleItemClick = true;
        long startTime = mCurAddCaption.getInPoint();
        long endTime = mCurAddCaption.getOutPoint();
        mVideoFragment.setDrawRectVisible(View.GONE);
        int index = getCaptionIndex(mCurCaptionZVal);
        CaptionInfo captionInfo = mCaptionDataListClone.get(index);
        mSelectedType = type;
        switch (type) {
            case NvAsset.ASSET_CAPTION_RICH_WORD:
                /*if (mSelectedRichPos == pos) {
                    mVideoFragment.playVideo(startTime, endTime);
                    return;
                }*/
                mSelectedRichPos = pos;
                mSelectedAnimationPos = 0;
                mCurAddCaption.applyModularCaptionRenderer(asset.uuid);
                if (captionInfo != null) {
                    captionInfo.setRichWordUuid(asset.uuid);
                }
                break;
            case NvAsset.ASSET_CAPTION_BUBBLE:
               /* if (mSelectedBubblePos == pos) {
                    mVideoFragment.playVideo(startTime, endTime);
                    return;
                }*/
                mSelectedBubblePos = pos;
                mSelectedAnimationPos = 0;
                mCurAddCaption.applyModularCaptionContext(asset.uuid);
                if (captionInfo != null) {
                    captionInfo.setBubbleUuid(asset.uuid);
                }
                break;
            case ASSET_CAPTION_ANIMATION:
                mSelectedAnimationPos = pos;
                mSelectedInAnimationPos = 0;
                mSelectedOutAnimationPos = 0;
                if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId())) {
                    mCurAddCaption.applyModularCaptionOutAnimation("");
                    //恢复默认值 Restore Defaults
                    mCurAddCaption.setModularCaptionOutAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
                }
                if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionInAnimationPackageId())) {
                    mCurAddCaption.applyModularCaptionInAnimation("");
                    mCurAddCaption.setModularCaptionInAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
                }
                if (mCurAddCaption.getModularCaptionAnimationPeroid() == 0) {
                    //如果切换的时候，设置的动画时长是0，则恢复默认，产品需求。
                    //If the animation duration is set to 0 when switching, the default and product requirements will be restored.
                    mCurAddCaption.setModularCaptionAnimationPeroid(ANIMATION_DEFAULT_DURATION);
                }
                mCurAddCaption.applyModularCaptionAnimation(asset.uuid);
                if (captionInfo != null) {
                    captionInfo.setCombinationAnimationUuid(asset.uuid);
                    captionInfo.setCombinationAnimationDuration(
                            mCurAddCaption.getModularCaptionAnimationPeroid());
                    captionInfo.setMarchOutAnimationUuid("");
                    captionInfo.setMarchInAnimationUuid("");
                }
                break;
            case NvAsset.ASSET_CAPTION_IN_ANIMATION:
                mSelectedInAnimationPos = pos;
                if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionAnimationPackageId())) {
                    mCurAddCaption.applyModularCaptionAnimation("");
                    mCurAddCaption.setModularCaptionAnimationPeroid(ANIMATION_DEFAULT_DURATION);
                }
                if (mCurAddCaption.getModularCaptionInAnimationDuration() == 0) {
                    //如果切换的时候，设置的动画时长是0，则恢复默认,产品需求。
                    //If the animation duration is set to 0 when switching, the default and product requirements will be restored.
                    mCurAddCaption.setModularCaptionInAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
                }
                mCurAddCaption.applyModularCaptionInAnimation(asset.uuid);
                if (captionInfo != null) {
                    captionInfo.setCombinationAnimationUuid("");
                    captionInfo.setCombinationAnimationDuration(0);
                    captionInfo.setMarchInAnimationUuid(asset.uuid);
                    captionInfo.setMarchInAnimationDuration(
                            mCurAddCaption.getModularCaptionInAnimationDuration());
                    if (TextUtils.isEmpty(asset.uuid)) {
                        captionInfo.setMarchInAnimationDuration(0);
                    }
                }
                break;
            case ASSET_CAPTION_OUT_ANIMATION:
                mSelectedOutAnimationPos = pos;
                if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionAnimationPackageId())) {
                    mCurAddCaption.applyModularCaptionAnimation("");
                    mCurAddCaption.setModularCaptionAnimationPeroid(ANIMATION_DEFAULT_DURATION);
                }
                if (mCurAddCaption.getModularCaptionOutAnimationDuration() == 0) {
                    //如果切换的时候，设置的动画时长是0，则恢复默认，产品需求。
                    //If the animation duration is set to 0 when switching, the default and product requirements will be restored.
                    mCurAddCaption.setModularCaptionOutAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
                }
                mCurAddCaption.applyModularCaptionOutAnimation(asset.uuid);
                //为了看效果，增加了50毫秒的播放时间
                //In order to see the effect, add 50 milliseconds of playback time
                int outDuration = mCurAddCaption.getModularCaptionOutAnimationDuration() + 50;
                startTime = endTime > outDuration * 1000 ? (endTime - outDuration * 1000) : startTime;

                if (captionInfo != null) {
                    captionInfo.setCombinationAnimationUuid("");
                    captionInfo.setCombinationAnimationDuration(0);
                    captionInfo.setMarchOutAnimationUuid(asset.uuid);
                    captionInfo.setMarchOutAnimationDuration(
                            mCurAddCaption.getModularCaptionOutAnimationDuration());
                    if (TextUtils.isEmpty(asset.uuid)) {
                        captionInfo.setMarchOutAnimationDuration(0);
                    }
                }
                break;
            default:
                break;
        }
        mVideoFragment.playVideo(startTime, endTime);
        float captionSize = mCurAddCaption.getFontSize();
        float scaleX = mCurAddCaption.getScaleX();
        float scaleY = mCurAddCaption.getScaleY();
        PointF pointF = mCurAddCaption.getCaptionTranslation();
        float rotateAngle = mCurAddCaption.getRotationZ();
        if (captionInfo != null) {
            captionInfo.setTranslation(pointF);
            captionInfo.setCaptionSize(captionSize);
            captionInfo.setScaleFactorX(scaleX);
            captionInfo.setScaleFactorY(scaleY);
            captionInfo.setRotation(rotateAngle);
        }
    }

    private void displaySeekBar(boolean visible) {
        if (visible) {
            if (mSeekBar.getVisibility() != View.VISIBLE) {
                mSeekBar.setVisibility(View.VISIBLE);
            }
        } else {
            if (mSeekBar.getVisibility() == View.VISIBLE) {
                mSeekBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 展示动画的进度
     * Show the progress of the animation
     * 注意:出入动画和组合动画互斥
     * Note: Access animation and combination animation are mutually exclusive
     */
    private void displayAnimationProgress(boolean visible, int type) {
        if (visible) {
            if (mSeekBar.getVisibility() != View.VISIBLE) {
                mSeekBar.setVisibility(View.VISIBLE);
            }
        } else {
            if (TextUtils.isEmpty(mCurAddCaption.getModularCaptionInAnimationPackageId()) &&
                    TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId())) {
                mSeekBar.setVisibility(View.INVISIBLE);
                return;
            }
        }

        if (type == NvAsset.ASSET_CAPTION_IN_ANIMATION) {
            if (TextUtils.isEmpty(mCurAddCaption.getModularCaptionInAnimationPackageId())) {
                mSeekBar.setLeftMoveIcon(0);
            } else {
                if (hasCombineAnimation) {
                    mSeekBar.reset();
                    mSeekBar.setMoveIconSize(20, 35);
                }
                if (TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId())) {
                    mSeekBar.setRightMoveIcon(0);
                }
                int duration = mCurAddCaption.getModularCaptionInAnimationDuration();
                mSeekBar.setMoveIconLowPadding(10);
                mSeekBar.setLeftMoveIcon(R.mipmap.bar_left);
                mSeekBar.setLeftProgress(duration);
                hasCombineAnimation = false;
            }
        } else if (type == NvAsset.ASSET_CAPTION_OUT_ANIMATION) {
            if (TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId())) {
                mSeekBar.setRightMoveIcon(0);
            } else {
                if (hasCombineAnimation) {
                    mSeekBar.reset();
                    mSeekBar.setMoveIconSize(20, 35);
                }
                if (TextUtils.isEmpty(mCurAddCaption.getModularCaptionInAnimationPackageId())) {
                    mSeekBar.setLeftMoveIcon(0);
                }
                int duration = mCurAddCaption.getModularCaptionOutAnimationDuration();
                mSeekBar.setMoveIconLowPadding(10);
                mSeekBar.setRightMoveIcon(R.mipmap.bar_right);
                mSeekBar.setRightProgress(duration);
                hasCombineAnimation = false;
            }
        } else {
            if (TextUtils.isEmpty(mCurAddCaption.getModularCaptionAnimationPackageId())) {
                mSeekBar.reset();
                mSeekBar.setVisibility(View.INVISIBLE);
                return;
            }
            int duration = mCurAddCaption.getModularCaptionAnimationPeroid();
            mSeekBar.reset();
            mSeekBar.setMoveIconSize(20, 20);
            mSeekBar.setLeftMoveIcon(R.mipmap.round_white);
            mSeekBar.setLeftProgress(duration);
            hasCombineAnimation = true;
        }
    }

    private CaptionStyleFragment initCaptionStyleFragment() {
        CaptionStyleFragment captionStyleFragment = new CaptionStyleFragment();
        captionStyleFragment.setAssetInfolist(mTotalCaptionStyleList);
        captionStyleFragment.setCaptionStyleListener(new CaptionStyleFragment.OnCaptionStyleListener() {
            @Override
            public void onFragmentLoadFinished() {
                mCaptionStyleFragment.applyToAllCaption(bIsStyleUuidApplyToAll);
                mSelectedStylePos = getCaptionStyleSelectedIndex();
                mCaptionStyleFragment.setSelectedPos(mSelectedStylePos);
                mCaptionStyleFragment.notifyDataSetChanged();
            }

            @Override
            public void OnDownloadCaptionStyle() {
                if (m_waitFlag) {
                    return;
                }
                mVideoFragment.stopEngine();
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.moreCaptionStyle);
                bundle.putInt("assetType", NvAsset.ASSET_CAPTION_STYLE);
                AppManager.getInstance()
                        .jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                AssetDownloadActivity.class, bundle, CAPTIONSTYLEREQUESTLIST);
                m_waitFlag = true;
            }

            @Override
            public void onItemClick(int pos) {
                if (pos < 0 || pos >= mTotalCaptionStyleList.size()) {
                    return;
                }
                if (mCurAddCaption == null) {
                    return;
                }
                isCaptionStyleItemClick = true;
                long startTime = mCurAddCaption.getInPoint();
                long endTime = mCurAddCaption.getOutPoint();
                mVideoFragment.setDrawRectVisible(View.GONE);
                if (mSelectedStylePos == pos) {
                    mVideoFragment.playVideo(startTime, endTime);
                    return;
                }
                NvAsset asset = mTotalCaptionStyleList.get(pos).getAsset();
                if (asset == null) {
                    return;
                }
                mSelectedStylePos = pos;
                /*
                 * 应用字幕样式
                 * Apply caption style
                 * */
                mCurAddCaption.applyCaptionStyle(asset.uuid);

                //                long duration = mStreamingContext.getTimelineCurrentPosition(mTimeline) - mCurAddCaption.getInPoint();
                //                mCurAddCaption.setCurrentKeyFrameTime(duration);

                float captionSize = mCurAddCaption.getFontSize();
                float scaleX = mCurAddCaption.getScaleX();
                float scaleY = mCurAddCaption.getScaleY();
                PointF pointF = mCurAddCaption.getCaptionTranslation();
                float rotateAngle = mCurAddCaption.getRotationZ();
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    CaptionInfo captionInfo = mCaptionDataListClone.get(index);
                    if (captionInfo != null) {
                        captionInfo.setCaptionStyleUuid(asset.uuid);
                        captionInfo.setTranslation(pointF);
                        captionInfo.setCaptionSize(captionSize);
                        captionInfo.setScaleFactorX(scaleX);
                        captionInfo.setScaleFactorY(scaleY);
                        captionInfo.setRotation(rotateAngle);
                        //添加样式会影响加粗 Adding style will affect bold
                        captionInfo.setBold(mCurAddCaption.getBold());
                        if (captionInfo.getUsedColorFlag() == CaptionInfo.ATTRIBUTE_USED_FLAG) {
                            NvsColor textColor = ColorUtil.colorStringtoNvsColor(captionInfo.getCaptionColor());
                            if (textColor != null) {
                                textColor.a = captionInfo.getCaptionColorAlpha() / 100.0f;
                                mCurAddCaption.setTextColor(textColor);
                            }
                        }
                    }
                }
                reloadKeyFrame(index);

                mVideoFragment.playVideo(startTime, endTime);
            }

            @Override
            public void onIsApplyToAll(boolean isApplyToAll) {
                bIsStyleUuidApplyToAll = isApplyToAll;
            }
        });
        return captionStyleFragment;
    }

    private void reloadKeyFrame(int index) {
        Map<Long, KeyFrameInfo> keyFrameInfoMap = mCaptionDataListClone.get(index).getKeyFrameInfo();
        Set<Long> keySet = keyFrameInfoMap.keySet();
        for (long currentTime : keySet) {
            KeyFrameInfo keyFrameInfo = keyFrameInfoMap.get(currentTime);
            long duration = currentTime - mCurAddCaption.getInPoint();
            mCurAddCaption.removeKeyframeAtTime(TRANS_X, duration);
            mCurAddCaption.removeKeyframeAtTime(TRANS_Y, duration);
            mCurAddCaption.removeKeyframeAtTime(SCALE_X, duration);
            mCurAddCaption.removeKeyframeAtTime(SCALE_Y, duration);
            mCurAddCaption.removeKeyframeAtTime(ROTATION_Z, duration);

            mCurAddCaption.setCurrentKeyFrameTime(duration);
            mCurAddCaption.setScaleX(keyFrameInfo.getScaleX());
            mCurAddCaption.setScaleY(keyFrameInfo.getScaleY());
            mCurAddCaption.setCaptionTranslation(keyFrameInfo.getTranslation());
            mCurAddCaption.setRotationZ(keyFrameInfo.getRotationZ());
        }
        //set caption bezier adjust function
        for (long currentTime : keySet) {
            KeyFrameInfo keyFrameInfo = keyFrameInfoMap.get(currentTime);
            double forwardControlPointX = keyFrameInfo.getForwardControlPointX();
            double backwardControlPointX = keyFrameInfo.getBackwardControlPointX();
            if (forwardControlPointX == -1 && backwardControlPointX == -1) {
                continue;
            }
            long duration = currentTime - mCurAddCaption.getInPoint();
            mCurAddCaption.setCurrentKeyFrameTime(duration);
            NvsControlPointPair pairX = mCurAddCaption.getControlPoint(TRANS_X);
            NvsControlPointPair pairY = mCurAddCaption.getControlPoint(TRANS_Y);
            if (pairX == null || pairY == null) {
                continue;
            }
            if (backwardControlPointX != -1) {
                pairX.backwardControlPoint.x = backwardControlPointX - mCurAddCaption.getInPoint();
                pairX.backwardControlPoint.y = keyFrameInfo.getBackwardControlPointYForTransX();
                pairY.backwardControlPoint.x = backwardControlPointX - mCurAddCaption.getInPoint();
                pairY.backwardControlPoint.y = keyFrameInfo.getBackwardControlPointYForTransY();
            }
            if (forwardControlPointX != -1) {
                pairX.forwardControlPoint.x = forwardControlPointX - mCurAddCaption.getInPoint();
                pairX.forwardControlPoint.y = keyFrameInfo.getForwardControlPointYForTransX();
                pairY.forwardControlPoint.x = forwardControlPointX - mCurAddCaption.getInPoint();
                pairY.forwardControlPoint.y = keyFrameInfo.getForwardControlPointYForTransY();
            }
            mCurAddCaption.setControlPoint(TRANS_X, pairX);
            mCurAddCaption.setControlPoint(TRANS_Y, pairY);
        }
    }

    private CaptionColorFragment
    initCaptionColorFragment() {
        CaptionColorFragment captionColorFragment = new CaptionColorFragment();
        captionColorFragment.setCaptionColorInfolist(mCaptionColorList);
        captionColorFragment.setCaptionColorListener(new CaptionColorFragment.OnCaptionColorListener() {
            @Override
            public void onFragmentLoadFinished() {
                mCaptionColorFragment.applyToAllCaption(bIsCaptionColorApplyToAll);
                mSelectedColorPos = getCaptionColorSelectedIndex();
                if (mSelectedColorPos >= 0) {
                    mCaptionColorList.get(mSelectedColorPos).mSelected = true;
                    mCaptionColorFragment.setCaptionColorInfolist(mCaptionColorList);
                    mCaptionColorFragment.notifyDataSetChanged();
                }
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionColorOpacityValue = mCaptionDataListClone.get(index).getCaptionColorAlpha();
                    mCaptionColorFragment.updateCaptionOpacityValue(mCaptionColorOpacityValue);
                }
            }

            @Override
            public void onCaptionColor(int pos) {
                if (pos < 0 || pos > mCaptionColorList.size()) {
                    return;
                }
                if (mCurAddCaption == null) {
                    return;
                }
                if (mSelectedColorPos == pos) {
                    return;
                }
                if (mSelectedColorPos >= 0) {
                    mCaptionColorList.get(mSelectedColorPos).mSelected = false;
                }
                mCaptionColorList.get(pos).mSelected = true;
                mCaptionColorFragment.notifyDataSetChanged();
                mSelectedColorPos = pos;
                mCaptionColorOpacityValue = 100;
                mCaptionColorFragment.updateCaptionOpacityValue(mCaptionColorOpacityValue);
                /*
                 * 设置字体颜色
                 * Set font color
                 * */
                NvsColor color = ColorUtil.colorStringtoNvsColor(mCaptionColorList.get(pos).mColorValue);
                mCurAddCaption.setTextColor(color);
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedColorFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setCaptionColor(mCaptionColorList.get(pos).mColorValue);
                }
                updateCaption();
            }

            @Override
            public void onCaptionOpacity(int progress) {
                if (mCurAddCaption == null) {
                    return;
                }
                /*
                 * 设置字体的不透明度
                 * Set the opacity of the font
                 * */
                NvsColor curColor = mCurAddCaption.getTextColor();
                curColor.a = progress / 100.0f;
                String strColor = ColorUtil.nvsColorToHexString(curColor);
                mCurAddCaption.setTextColor(curColor);
                mCaptionColorOpacityValue = progress;
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedColorFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setCaptionColor(strColor);
                    mCaptionDataListClone.get(index).setCaptionColorAlpha(progress);
                }

                updateCaption();
            }

            @Override
            public void onIsApplyToAll(boolean isApplyToAll) {
                bIsCaptionColorApplyToAll = isApplyToAll;
            }
        });
        return captionColorFragment;
    }

    private CaptionOutlineFragment initCaptionOutlineFragment() {
        CaptionOutlineFragment captionOutlineFragment = new CaptionOutlineFragment();
        captionOutlineFragment.setCaptionOutlineInfolist(mCaptionOutlineColorList);
        captionOutlineFragment.setCaptionOutlineListener(
                new CaptionOutlineFragment.OnCaptionOutlineListener() {
                    @Override
                    public void onFragmentLoadFinished() {
                        mCaptionOutlineFragment.applyToAllCaption(bIsOutlineApplyToAll);
                        mSelectedOutlinePos = getOutlineColorSelectedIndex();
                        mCaptionOutlineColorList.get(mSelectedOutlinePos).mSelected = true;
                        mCaptionOutlineFragment.setCaptionOutlineInfolist(mCaptionOutlineColorList);
                        mCaptionOutlineFragment.notifyDataSetChanged(mSelectedOutlinePos);
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            boolean isDrawOutline = mCaptionDataListClone.get(index).isHasOutline();
                            if (isDrawOutline) {
                                mCaptionOutlineWidthValue =
                                        (int) mCaptionDataListClone.get(index).getOutlineWidth();
                                mCaptionOutlineOpacityValue =
                                        mCaptionDataListClone.get(index).getOutlineColorAlpha();
                            }
                            mCaptionOutlineFragment.updateCaptionOutlineWidthValue(mCaptionOutlineWidthValue);
                            mCaptionOutlineFragment.updateCaptionOutlineOpacityValue(mCaptionOutlineOpacityValue);
                        }
                    }

                    @Override
                    public void onCaptionOutlineColor(int pos) {
                        if (pos < 0 || pos > mCaptionOutlineColorList.size()) {
                            return;
                        }
                        if (mCurAddCaption == null) {
                            return;
                        }
                        if (mSelectedOutlinePos == pos) {
                            return;
                        }
                        mCaptionOutlineColorList.get(mSelectedOutlinePos).mSelected = false;
                        mCaptionOutlineColorList.get(pos).mSelected = true;
                        mCaptionOutlineFragment.notifyDataSetChanged(pos);
                        mSelectedOutlinePos = pos;

                        mCaptionOutlineOpacityValue = 100;
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (pos == 0) {
                            mCurAddCaption.setDrawOutline(false);
                            mCaptionOutlineWidthValue = 0;
                            if (index >= 0) {
                                mCaptionDataListClone.get(index)
                                        .setUsedOutlineFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                                mCaptionDataListClone.get(index).setHasOutline(false);
                                mCaptionDataListClone.get(index).setOutlineColor("");
                            }
                        } else {
                            mCaptionOutlineWidthValue = 8;
                            /*
                             * 设置字幕描边标识
                             * Set caption stroke flag
                             * */
                            mCurAddCaption.setDrawOutline(true);
                            /*
                             * 设置描边颜色
                             * Set outline color
                             * */
                            NvsColor color =
                                    ColorUtil.colorStringtoNvsColor(mCaptionOutlineColorList.get(pos).mColorValue);
                            mCurAddCaption.setOutlineColor(color);
                            /*
                             * 字幕描边宽度
                             * Caption stroke width
                             * */
                            mCurAddCaption.setOutlineWidth(mCaptionOutlineWidthValue);
                            if (index >= 0) {
                                mCaptionDataListClone.get(index)
                                        .setUsedOutlineFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                                mCaptionDataListClone.get(index).setHasOutline(true);
                                mCaptionDataListClone.get(index)
                                        .setOutlineColor(mCaptionOutlineColorList.get(pos).mColorValue);
                                mCaptionDataListClone.get(index).setOutlineWidth(mCaptionOutlineWidthValue);
                                mCaptionDataListClone.get(index).setOutlineColorAlpha(mCaptionOutlineOpacityValue);
                            }
                        }
                        mCaptionOutlineFragment.updateCaptionOutlineWidthValue(mCaptionOutlineWidthValue);
                        mCaptionOutlineFragment.updateCaptionOutlineOpacityValue(mCaptionOutlineOpacityValue);
                        updateCaption();
                    }

                    @Override
                    public void onCaptionOutlineWidth(int width) {
                        if (mCurAddCaption == null) {
                            return;
                        }
                        if (mSelectedOutlinePos == 0) {
                            return;
                        }
                        /*
                         * 字幕描边宽度
                         * Caption stroke width
                         * */
                        mCurAddCaption.setOutlineWidth(width);
                        mCaptionOutlineWidthValue = width;
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index).setOutlineWidth(mCaptionOutlineWidthValue);
                        }
                        updateCaption();
                    }

                    @Override
                    public void onCaptionOutlineOpacity(int opacity) {
                        if (mCurAddCaption == null) {
                            return;
                        }
                        if (mSelectedOutlinePos == 0) {
                            return;
                        }
                        /*
                         * 设置字幕描边的不透明度
                         * Set the opacity of the caption stroke
                         * */
                        NvsColor curColor = mCurAddCaption.getOutlineColor();
                        curColor.a = opacity / 100.0f;
                        mCurAddCaption.setOutlineColor(curColor);
                        mCaptionOutlineOpacityValue = opacity;
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index).setOutlineColorAlpha(opacity);
                        }
                        updateCaption();
                    }

                    @Override
                    public void onIsApplyToAll(boolean isApplyToAll) {
                        bIsOutlineApplyToAll = isApplyToAll;
                    }
                });
        return captionOutlineFragment;
    }

    private CaptionBackgroundFragment initCaptionBackgroundFragment() {
        CaptionBackgroundFragment captionBackgroundFragment = new CaptionBackgroundFragment();
        captionBackgroundFragment.setIsClipEdit(true);
        captionBackgroundFragment.setCaptionBackgroundInfolist(mCaptionBackgroundList);
        captionBackgroundFragment.setCaptionBackgroundListener(
                new CaptionBackgroundFragment.OnCaptionBackgroundListener() {
                    @Override
                    public void onFragmentLoadFinished() {

                        mCaptionBackgroundFragment.applyToAllCaption(bIsCaptionBackgroundApplyToAll);
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            mCaptionBackgroundOpacityValue =
                                    mCaptionDataListClone.get(index).getCaptionBackgroundAlpha();
                            mCaptionBackgroundFragment.updateCaptionOpacityValue(mCaptionBackgroundOpacityValue);
                        }
                        mSelectedBackgroundPos = getBackgroundSelectedIndex();
                        if (mSelectedBackgroundPos >= 0) {
                            mCaptionBackgroundList.get(mSelectedBackgroundPos).mSelected = true;
                            NvsColor curColor = mCurAddCaption.getBackgroundColor();
                            String strColor = ColorUtil.nvsColorToHexString(curColor);
                            mCaptionBackgroundList.get(mSelectedBackgroundPos).mColorValue = strColor;
                            mCaptionBackgroundFragment.setCaptionBackgroundInfolist(mCaptionBackgroundList);
                            mCaptionBackgroundFragment.notifyDataSetChanged(mSelectedBackgroundPos);
                        }
                        //设置背景圆角的最大值和当前的值
                        //Set the maximum and current value of the background rounded corners
                        RectF rectF = mCurAddCaption.getTextBoundingRect();
                        float height = Math.abs(rectF.top - rectF.bottom);
                        float width = Math.abs(rectF.right - rectF.left);
                        float maxRadius = height >= width ? width / 2 : height / 2;
                        //设置圆角的值
                        //Set the value of the rounded corners
                        mCaptionBackgroundFragment.initCaptionMaxCorner((int) (maxRadius));
                        if (mCurAddCaption != null) {
                            mCaptionBackgroundCornerValue = mCurAddCaption.getBackgroundRadius();
                            mCaptionBackgroundFragment.updateCaptionCornerValue(
                                    (int) mCaptionBackgroundCornerValue);
                        }
                        if (mCurAddCaption != null) {
                            mCaptionBackgroundPeddingValue = mCurAddCaption.getBoundaryPaddingRatio();
                            mCaptionBackgroundFragment.updateCaptionPaddingValue(
                                    Math.round(mCaptionBackgroundPeddingValue * 100));
                        }
                    }

                    @Override
                    public void onCaptionColor(int pos) {
                        if (pos < 0 || pos > mCaptionBackgroundList.size()) {
                            return;
                        }
                        if (mCurAddCaption == null) {
                            return;
                        }
                        if (mSelectedBackgroundPos == pos) {
                            return;
                        }
                        //刷新页面 refresh page
                        mCaptionBackgroundList.get(mSelectedBackgroundPos).mSelected = false;
                        mCaptionBackgroundList.get(pos).mSelected = true;
                        mCaptionBackgroundFragment.notifyDataSetChanged(pos);
                        mSelectedBackgroundPos = pos;
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (pos == 0) {
                            //设置无背景色 Set no background color
                            // 黑色透明#00000000
                            String noColor = "#00000000";
                            NvsColor color = ColorUtil.colorStringtoNvsColor(noColor);
                            mCurAddCaption.setBackgroundColor(color);
                            if (index >= 0) {
                                mCaptionDataListClone.get(index)
                                        .setUsedBackgroundFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                                mCaptionDataListClone.get(index).setCaptionBackground(noColor);
                            }
                        } else {
                            //背景色透明度 Background color transparency
                            mCaptionBackgroundOpacityValue = 100;
                            /*
                             * 设置背景色
                             * Set font color
                             * */
                            NvsColor color =
                                    ColorUtil.colorStringtoNvsColor(mCaptionBackgroundList.get(pos).mColorValue);
                            mCurAddCaption.setBackgroundColor(color);
                            if (index >= 0) {
                                mCaptionDataListClone.get(index)
                                        .setUsedBackgroundFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                                mCaptionDataListClone.get(index)
                                        .setCaptionBackground(mCaptionBackgroundList.get(pos).mColorValue);
                            }
                        }
                        if (index >= 0) {
                            mCaptionDataListClone.get(index)
                                    .setUsedBackgroundFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            mCaptionDataListClone.get(index)
                                    .setCaptionBackground(mCaptionBackgroundList.get(pos).mColorValue);
                        }
                        mCaptionBackgroundFragment.updateCaptionOpacityValue(mCaptionBackgroundOpacityValue);
                        mCaptionBackgroundCornerValue = mCurAddCaption.getBackgroundRadius();
                        mCaptionBackgroundFragment.updateCaptionCornerValue(
                                (int) mCaptionBackgroundCornerValue);
                        updateCaption();
                    }

                    @Override
                    public void onCaptionOpacity(int progress) {
                        if (mCurAddCaption == null) {
                            return;
                        }
                        /*
                         * 设置背景的不透明度
                         * Set the opacity of the font
                         * */
                        NvsColor curColor = mCurAddCaption.getBackgroundColor();
                        curColor.a = progress / 100.0f;
                        String strColor = ColorUtil.nvsColorToHexString(curColor);
                        mCurAddCaption.setBackgroundColor(curColor);
                        mCaptionBackgroundOpacityValue = progress;
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index)
                                    .setUsedBackgroundFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            mCaptionDataListClone.get(index).setCaptionBackground(strColor);
                            mCaptionDataListClone.get(index).setCaptionBackgroundAlpha(progress);
                        }

                        updateCaption();
                    }

                    @Override
                    public void onCaptionCorner(int progress) {
                        if (mCurAddCaption == null) {
                            return;
                        }
                        /*
                         * 设置背景圆角 Set background rounded corners
                         * */
                        //设置背景圆角的最大值和当前的值
                        //Set the maximum and current value of the background rounded corners
                        RectF rectF = mCurAddCaption.getTextBoundingRect();
                        float height = Math.abs(rectF.top - rectF.bottom);
                        float width = Math.abs(rectF.right - rectF.left);
                        float maxRadius = height >= width ? width / 2 : height / 2;
                        //设置圆角的最大值 Set the maximum value of rounded corners
                        //mCaptionBackgroundFragment.initCaptionMaxCorner((int) (maxRadius));
                        mCaptionBackgroundCornerValue = progress;
                        mCurAddCaption.setBackgroundRadius(mCaptionBackgroundCornerValue);
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index)
                                    .setCaptionBackgroundRadius(mCaptionBackgroundCornerValue);
                            mCaptionDataListClone.get(index)
                                    .setUsedBackgroundRadiusFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        }

                        updateCaption();
                    }

                    @Override
                    public void onCaptionPadding(int progress) {
                        if (mCurAddCaption == null) {
                            return;
                        }
                        if (mCaptionDataListClone == null) {
                            return;
                        }
                        //设置边距的最大值 Set the maximum value of rounded corners
                        //mCaptionBackgroundFragment.initCaptionMaxPadding((int) (maxRadius));
                        mCaptionBackgroundPeddingValue = progress / (float) 100;
                        mCurAddCaption.setBoundaryPaddingRatio(mCaptionBackgroundPeddingValue);
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index)
                                    .setCaptionBackgroundPadding(mCaptionBackgroundPeddingValue);
                            mCaptionDataListClone.get(index)
                                    .setUsedBackgroundPaddingFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        }

                        updateCaption();
                    }

                    @Override
                    public void onIsApplyToAll(boolean isApplyToAll) {
                        bIsCaptionBackgroundApplyToAll = isApplyToAll;
                    }
                });
        return captionBackgroundFragment;
    }

    private CaptionFontFragment initCaptionFontFragment() {
        CaptionFontFragment captionFontFragment = new CaptionFontFragment();
        captionFontFragment.setCaptionFontListener(new CaptionFontFragment.OnCaptionFontListener() {
            @Override
            public void onFragmentLoadFinished() {
                updateFontList();
                mCaptionFontFragment.applyToAllCaption(bIsFontApplyToAll);
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionFontFragment.updateUnderlineButton(
                            mCaptionDataListClone.get(index).isUnderline());
                    mCaptionFontFragment.updateBoldButton(mCaptionDataListClone.get(index).isBold());
                    mCaptionFontFragment.updateItalicButton(mCaptionDataListClone.get(index).isItalic());
                    mCaptionFontFragment.updateShadowButton(mCaptionDataListClone.get(index).isShadow());
                }
            }

            @Override
            public void onItemClick(int pos) {
                if (pos < 0 || pos >= mCaptionFontList.size()) {
                    return;
                }
                mSelectedFontPos = pos;
                NvAsset asset = mCaptionFontList.get(pos).getAsset();
                if (asset == null) {
                    return;
                }
                if (!TextUtils.isEmpty(asset.localDirPath)) {
                    applyCaptionFont(asset.localDirPath);
                } else if (!TextUtils.isEmpty(asset.bundledLocalDirPath)) {
                    applyCaptionFont(asset.bundledLocalDirPath);
                } else if (pos == 0) {
                    applyCaptionFont("");
                }
            }

            @Override
            public void onBold(boolean mIsBold) {
                if (mCurAddCaption == null) {
                    return;
                }
                //                boolean isBold = mCurAddCaption.getBold();
                //                isBold = !isBold;
                /*
                 * 字幕加粗
                 * Caption bold
                 * */
                mCurAddCaption.setBold(mIsBold);
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedIsBoldFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setBold(mIsBold);
                }
                updateCaption();
            }

            @Override
            public void onItalic(boolean mIsItalic) {
                if (mCurAddCaption == null) {
                    return;
                }
                boolean isItalic = mCurAddCaption.getItalic();
                isItalic = !isItalic;
                /*
                 * 字幕斜体
                 * Caption italics
                 * */
                mCurAddCaption.setItalic(isItalic);
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedIsItalicFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setItalic(isItalic);
                }
                updateCaption();
            }

            @Override
            public void onShadow() {
                if (mCurAddCaption == null) {
                    return;
                }
                boolean isShadow = mCurAddCaption.getDrawShadow();
                isShadow = !isShadow;
                if (isShadow) {
                    PointF point = new PointF(7, -7);
                    NvsColor shadowColor = new NvsColor(0, 0, 0, 0.5f);
                    /*
                     * 设置字幕阴影偏移量
                     * Set the caption shadow offset
                     * */
                    mCurAddCaption.setShadowOffset(point);
                    /*
                     * 设置字幕阴影颜色
                     * Set the caption shadow color
                     * */
                    mCurAddCaption.setShadowColor(shadowColor);
                }
                mCurAddCaption.setDrawShadow(isShadow);

                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedShadowFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setShadow(isShadow);
                }
                updateCaption();
            }

            @Override
            public void onUnderline(boolean underline) {
                // 设置下划线 Set underline
                if (mCurAddCaption == null) {
                    return;
                }
                boolean isUnderline = mCurAddCaption.getUnderline();
                isUnderline = !isUnderline;
                mCurAddCaption.setUnderline(isUnderline);
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedUnderlineFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setUnderline(isUnderline);
                }
                updateCaption();
            }

            @Override
            public void onIsApplyToAll(boolean isApplyToAll) {
                bIsFontApplyToAll = isApplyToAll;
            }

            @Override
            public void onFontDownload(int pos) {
                int count = mCaptionFontList.size();
                if (pos <= 0 || pos >= count) {
                    return;
                }
                if (mFontCurClickPos == pos) {
                    /*
                     * 重复点击，不作处理；防止素材多次下载
                     * Double click without processing; prevent material from downloading multiple times
                     * */
                    return;
                }
                mFontCurClickPos = pos;
                //                mAssetManager.downloadAsset(mFontType, mCaptionFontList.get(pos).getAsset().uuid);
                mPresenter.downloadAsset(mCaptionFontList.get(pos).getAsset(), mFontCurClickPos);
            }

            @Override
            public void onChangeOpacity(int progress) {

            }
        });
        return captionFontFragment;
    }

    private CaptionSizeFragment initCaptionSizeFragment() {
        CaptionSizeFragment captionSizeFragment = new CaptionSizeFragment();
        captionSizeFragment.setCaptionSizeListener(new CaptionSizeFragment.OnCaptionSizeListener() {
            @Override
            public void onFragmentLoadFinished() {
                mCaptionSizeFragment.applyToAllCaption(bIsSizeApplyToAll);
                int index = getCaptionIndex(mCurCaptionZVal);
                //                if (index >= 0) {
                //                    int captionSizeVal = (int) mCaptionDataListClone.get(index).getCaptionSize();
                //                    if (captionSizeVal >= 0)
                //                        mCaptionSizeValue = captionSizeVal;
                //                    mCaptionSizeFragment.updateCaptionSizeValue(mCaptionSizeValue);
                //                }
            }

            @Override
            public void OnCaptionSize(int size) {
                if (mCurAddCaption == null) {
                    return;
                }
                mCurAddCaption.setFontSize(size);
                //                mCaptionSizeValue = size;
                //                int index = getCaptionIndex(mCurCaptionZVal);
                //                if (index >= 0)
                //                    mCaptionDataListClone.get(index).setCaptionSize(size);
                updateCaption();
            }

            @Override
            public void onIsApplyToAll(boolean isApplyToAll) {
                bIsSizeApplyToAll = isApplyToAll;
            }
        });
        return captionSizeFragment;
    }

    private CaptionLetterSpacingFragment initCaptionLetterSpacingFragment() {
        CaptionLetterSpacingFragment captionLetterSpacingFragment = new CaptionLetterSpacingFragment();
        captionLetterSpacingFragment.setCaptionLetterSpacingListener(
                new CaptionLetterSpacingFragment.OnCaptionLetterSpacingListener() {
                    @Override
                    public void onFragmentLoadFinished() {
                        mCaptionLetterSpacingFragment.applyToAllCaption(bIsLetterSpacingApplyToAll);
                        if (mCurAddCaption == null) {
                            selectCaption();
                        }

                        if (mCurAddCaption != null) {

                            //设置字间距 Set word spacing
                            float letterSpaceing = mCurAddCaption.getLetterSpacing();
                            if (letterSpaceing == getLetterSpacing(CAPTION_PERCENT_STANDARD_SPACING)) {
                                mCaptionLetterSpacingFragment.setSelectedStandard(
                                        CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER, true);
                            } else if (letterSpaceing == getLetterSpacing(CAPTION_PERCENT_MORE_LARGE_SPACING)) {
                                mCaptionLetterSpacingFragment.setSelectedMore(
                                        CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER, true);
                            } else if (letterSpaceing == getLetterSpacing(CAPTION_PERCENT_LARGE_SPACING)) {
                                mCaptionLetterSpacingFragment.setSelectedLarge(
                                        CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER, true);
                            } else if (letterSpaceing == getLetterSpacing(CAPTION_PERCENT_SMALL_SPACING)) {
                                mCaptionLetterSpacingFragment.setSelectedSmall(
                                        CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER, true);
                            } else {
                                mCaptionLetterSpacingFragment.setSelectedStandard(
                                        CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER, true);
                            }
                            //设置行间距 Set line spacing
                            float lineSpacing = mCurAddCaption.getLineSpacing();
                            if (lineSpacing == CAPTION_SMALL_LINE_SPACING) {
                                mCaptionLetterSpacingFragment.setSelectedSmall(
                                        CaptionLetterSpacingFragment.CAPTION_SPACING_LINE, true);
                            } else if (lineSpacing == CAPTION_STANDARD_LINE_SPACING) {
                                mCaptionLetterSpacingFragment.setSelectedStandard(
                                        CaptionLetterSpacingFragment.CAPTION_SPACING_LINE, true);
                            } else if (lineSpacing == CAPTION_MORE_LARGE_LINE_SPACING) {
                                mCaptionLetterSpacingFragment.setSelectedMore(
                                        CaptionLetterSpacingFragment.CAPTION_SPACING_LINE, true);
                            } else if (lineSpacing == CAPTION_LARGE_LINE_SPACEING) {
                                mCaptionLetterSpacingFragment.setSelectedLarge(
                                        CaptionLetterSpacingFragment.CAPTION_SPACING_LINE, true);
                            } else {
                                mCaptionLetterSpacingFragment.setSelectedStandard(
                                        CaptionLetterSpacingFragment.CAPTION_SPACING_LINE, true);
                            }
                        }
                    }

                    @Override
                    public void onSmallBtnClicked(int spacingMode) {
                        if (mCurAddCaption == null) {
                            return;
                        }
                        if (spacingMode == CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER) {
                            float letterSpace = getLetterSpacing(CAPTION_PERCENT_SMALL_SPACING);
                            mCurAddCaption.setLetterSpacingType(NvsCaption.LETTER_SPACING_TYPE_ABSOLUTE);
                            mCurAddCaption.setLetterSpacing(letterSpace);

                            updateCaption();
                            int index = getCaptionIndex(mCurCaptionZVal);
                            if (index >= 0) {
                                CaptionInfo curCaptionInfo = mCaptionDataListClone.get(index);
                                if (curCaptionInfo != null) {
                                    curCaptionInfo.setUsedLetterSpacingFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                                    curCaptionInfo.setLetterSpacing(letterSpace);
                                }
                            }
                        } else {
                            //设置行间距 Set line spacing
                            mCurAddCaption.setLineSpacing(CAPTION_SMALL_LINE_SPACING);
                            updateCaption();
                            int index = getCaptionIndex(mCurCaptionZVal);
                            if (index >= 0) {
                                CaptionInfo curCaptionInfo = mCaptionDataListClone.get(index);
                                if (curCaptionInfo != null) {
                                    curCaptionInfo.setLineSpacing(CAPTION_SMALL_LINE_SPACING);
                                }
                            }
                        }
                    }

                    @Override
                    public void onStandardBtnClicked(int spacingMode) {
                        if (mCurAddCaption == null) {
                            return;
                        }
                        if (spacingMode == CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER) {

                            float letterSpace = getLetterSpacing(CAPTION_PERCENT_STANDARD_SPACING);
                            mCurAddCaption.setLetterSpacingType(NvsCaption.LETTER_SPACING_TYPE_ABSOLUTE);
                            mCurAddCaption.setLetterSpacing(letterSpace);

                            updateCaption();
                            int index = getCaptionIndex(mCurCaptionZVal);
                            if (index >= 0) {
                                CaptionInfo curCaptionInfo = mCaptionDataListClone.get(index);
                                if (curCaptionInfo != null) {
                                    curCaptionInfo.setUsedLetterSpacingFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                                    curCaptionInfo.setLetterSpacing(letterSpace);
                                }
                            }
                        } else {
                            //设置行间距 Set line spacing
                            mCurAddCaption.setLineSpacing(CAPTION_STANDARD_LINE_SPACING);
                            updateCaption();
                            int index = getCaptionIndex(mCurCaptionZVal);
                            if (index >= 0) {
                                CaptionInfo curCaptionInfo = mCaptionDataListClone.get(index);
                                if (curCaptionInfo != null) {
                                    curCaptionInfo.setLineSpacing(CAPTION_STANDARD_LINE_SPACING);
                                }
                            }
                        }
                    }

                    @Override
                    public void onMoreBtnClicked(int spacingMode) {
                        if (mCurAddCaption == null) {
                            return;
                        }
                        if (spacingMode == CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER) {

                            float letterSpace = getLetterSpacing(CAPTION_PERCENT_MORE_LARGE_SPACING);
                            mCurAddCaption.setLetterSpacingType(NvsCaption.LETTER_SPACING_TYPE_ABSOLUTE);
                            mCurAddCaption.setLetterSpacing(letterSpace);

                            updateCaption();
                            int index = getCaptionIndex(mCurCaptionZVal);
                            if (index >= 0) {
                                CaptionInfo curCaptionInfo = mCaptionDataListClone.get(index);
                                if (curCaptionInfo != null) {
                                    curCaptionInfo.setUsedLetterSpacingFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                                    curCaptionInfo.setLetterSpacing(letterSpace);
                                }
                            }
                        } else {
                            //设置行间距 Set line spacing
                            mCurAddCaption.setLineSpacing(CAPTION_MORE_LARGE_LINE_SPACING);
                            updateCaption();
                            int index = getCaptionIndex(mCurCaptionZVal);
                            if (index >= 0) {
                                CaptionInfo curCaptionInfo = mCaptionDataListClone.get(index);
                                if (curCaptionInfo != null) {
                                    curCaptionInfo.setLineSpacing(CAPTION_MORE_LARGE_LINE_SPACING);
                                }
                            }
                        }
                    }

                    @Override
                    public void onLargeBtnClicked(int spacingMode) {
                        if (mCurAddCaption == null) {
                            return;
                        }
                        if (spacingMode == CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER) {

                            float letterSpace = getLetterSpacing(CAPTION_PERCENT_LARGE_SPACING);
                            mCurAddCaption.setLetterSpacingType(NvsCaption.LETTER_SPACING_TYPE_ABSOLUTE);
                            mCurAddCaption.setLetterSpacing(letterSpace);

                            updateCaption();
                            int index = getCaptionIndex(mCurCaptionZVal);
                            if (index >= 0) {
                                CaptionInfo curCaptionInfo = mCaptionDataListClone.get(index);
                                if (curCaptionInfo != null) {
                                    curCaptionInfo.setUsedLetterSpacingFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                                    curCaptionInfo.setLetterSpacing(letterSpace);
                                }
                            }
                        } else {
                            //设置行间距 Set line spacing
                            mCurAddCaption.setLineSpacing(CAPTION_LARGE_LINE_SPACEING);
                            updateCaption();
                            int index = getCaptionIndex(mCurCaptionZVal);
                            if (index >= 0) {
                                CaptionInfo curCaptionInfo = mCaptionDataListClone.get(index);
                                if (curCaptionInfo != null) {
                                    curCaptionInfo.setLineSpacing(CAPTION_LARGE_LINE_SPACEING);
                                }
                            }
                        }
                    }

                    @Override
                    public void onIsApplyToAll(boolean isApplyToAll) {
                        bIsLetterSpacingApplyToAll = isApplyToAll;
                    }
                });

        return captionLetterSpacingFragment;
    }

    private float getLetterSpacing(double v) {
        NvsVideoResolution videoRes = mTimeline.getVideoRes();
        int imageWidth = videoRes.imageWidth;
        int imageHeight = videoRes.imageHeight;
        int longer = 0;
        if (imageWidth > imageHeight) {
            longer = imageWidth;
        } else {
            longer = imageHeight;
        }
        return (float) (longer * v);
    }

    private CaptionPositionFragment initCaptionPositionFragment() {
        CaptionPositionFragment captionPositionFragment = new CaptionPositionFragment();
        captionPositionFragment.setCaptionPostionListener(
                new CaptionPositionFragment.OnCaptionPositionListener() {
                    @Override
                    public void onFragmentLoadFinished() {
                        mCaptionPositionFragment.applyToAllCaption(bIsPositionApplyToAll);
                    }

                    @Override
                    public void OnAlignLeft() {
                        if (mCurAddCaption == null || !ifCouldEditCaption()) {
                            return;
                        }

                        List<PointF> list = mCurAddCaption.getBoundingRectangleVertices();
                        if (list == null || list.size() < 4) {
                            return;
                        }
                        Collections.sort(list, new Util.PointXComparator());

                        float xOffset = -(mTimeline.getVideoRes().imageWidth / 2 + list.get(0).x);
                        mCurAddCaption.translateCaption(new PointF(xOffset, 0));

                        mAlignType = CAPTION_ALIGNLEFT;
                        updateCaption();

                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index)
                                    .setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            mCaptionDataListClone.get(index)
                                    .setTranslation(mCurAddCaption.getCaptionTranslation());
                        }
                    }

                    @Override
                    public void OnAlignCenterHorizontal() {
                        if (mCurAddCaption == null || !ifCouldEditCaption()) {
                            return;
                        }

                        List<PointF> list = mCurAddCaption.getBoundingRectangleVertices();
                        if (list == null || list.size() < 4) {
                            return;
                        }
                        Collections.sort(list, new Util.PointXComparator());

                        float xOffset = -((list.get(3).x - list.get(0).x) / 2 + list.get(0).x);
                        mCurAddCaption.translateCaption(new PointF(xOffset, 0));
                        updateCaption();
                        mAlignType = CAPTION_ALIGNHORIZCENTER;
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index)
                                    .setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            mCaptionDataListClone.get(index)
                                    .setTranslation(mCurAddCaption.getCaptionTranslation());
                        }
                    }

                    @Override
                    public void OnAlignRight() {
                        if (mCurAddCaption == null || !ifCouldEditCaption()) {
                            return;
                        }

                        List<PointF> list = mCurAddCaption.getBoundingRectangleVertices();
                        if (list == null || list.size() < 4) {
                            return;
                        }
                        Collections.sort(list, new Util.PointXComparator());

                        float xOffset = mTimeline.getVideoRes().imageWidth / 2 - list.get(3).x;

                        mCurAddCaption.translateCaption(new PointF(xOffset, 0));

                        updateCaption();

                        mAlignType = CAPTION_ALIGNRIGHT;
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index)
                                    .setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            mCaptionDataListClone.get(index)
                                    .setTranslation(mCurAddCaption.getCaptionTranslation());
                        }
                    }

                    @Override
                    public void OnAlignTop() {
                        if (mCurAddCaption == null || !ifCouldEditCaption()) {
                            return;
                        }

                        List<PointF> list = mCurAddCaption.getBoundingRectangleVertices();
                        if (list == null || list.size() < 4) {
                            return;
                        }
                        Collections.sort(list, new Util.PointYComparator());
                        float y_dis = list.get(3).y - list.get(0).y;

                        float yOffset = mTimeline.getVideoRes().imageHeight / 2 - list.get(0).y - y_dis;

                        mCurAddCaption.translateCaption(new PointF(0, yOffset));

                        updateCaption();

                        mAlignType = CAPTION_ALIGNTOP;
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index)
                                    .setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            mCaptionDataListClone.get(index)
                                    .setTranslation(mCurAddCaption.getCaptionTranslation());
                        }
                    }

                    @Override
                    public void OnAlignCenterVertical() {
                        if (mCurAddCaption == null || !ifCouldEditCaption()) {
                            return;
                        }
                        List<PointF> list = mCurAddCaption.getBoundingRectangleVertices();
                        if (list == null || list.size() < 4) {
                            return;
                        }
                        Collections.sort(list, new Util.PointYComparator());

                        float yOffset = -((list.get(3).y - list.get(0).y) / 2 + list.get(0).y);
                        mCurAddCaption.translateCaption(new PointF(0, yOffset));

                        updateCaption();

                        mAlignType = CAPTION_ALIGNVERTCENTER;
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index)
                                    .setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            mCaptionDataListClone.get(index)
                                    .setTranslation(mCurAddCaption.getCaptionTranslation());
                        }
                    }

                    @Override
                    public void OnAlignBottom() {
                        if (mCurAddCaption == null || !ifCouldEditCaption()) {
                            return;
                        }

                        List<PointF> list = mCurAddCaption.getBoundingRectangleVertices();
                        if (list == null || list.size() < 4) {
                            return;
                        }
                        Collections.sort(list, new Util.PointYComparator());
                        float y_dis = list.get(3).y - list.get(0).y;

                        float yOffset = -(mTimeline.getVideoRes().imageHeight / 2 + list.get(3).y - y_dis);
                        mCurAddCaption.translateCaption(new PointF(0, yOffset));

                        updateCaption();

                        mAlignType = CAPTION_ALIGNBOTTOM;
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index)
                                    .setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            mCaptionDataListClone.get(index)
                                    .setTranslation(mCurAddCaption.getCaptionTranslation());
                        }
                    }

                    @Override
                    public void onIsApplyToAll(boolean isApplyToAll) {
                        bIsPositionApplyToAll = isApplyToAll;
                    }
                });
        return captionPositionFragment;
    }

    private int getCaptionStyleSelectedIndex() {
        int selectIndex = 0;
        if (mCurAddCaption != null) {
            String uuid = mCurAddCaption.getCaptionStylePackageId();
            for (int index = 0; index < mTotalCaptionStyleList.size(); ++index) {
                NvAsset asset = mTotalCaptionStyleList.get(index).getAsset();
                if (asset == null) {
                    continue;
                }
                if (asset.uuid.compareTo(uuid) == 0) {
                    selectIndex = index;
                    break;
                }
            }
        }

        return selectIndex;
    }

    /**
     * 根据不同类型获取改变拼装字幕相关
     * Acquire and change subtitles related to different types
     */
    private void changeAssemblyCaption(int type) {
        if (mCurAddCaption != null) {
            switch (type) {
                case NvAsset.ASSET_CAPTION_RICH_WORD:
                    String uuid = mCurAddCaption.getModularCaptionRendererPackageId();
                    mSelectedRichPos = getTargetPosition(mRichWordList, uuid);
                    break;
                case NvAsset.ASSET_CAPTION_BUBBLE:
                    mSelectedBubblePos =
                            getTargetPosition(mBubbleList, mCurAddCaption.getModularCaptionContextPackageId());
                    break;
                case ASSET_CAPTION_ANIMATION:
                    mSelectedAnimationPos = getTargetPosition(mAnimationList,
                            mCurAddCaption.getModularCaptionAnimationPackageId());
                    mSelectedInAnimationPos = 0;
                    mSelectedOutAnimationPos = 0;
                    break;
                case NvAsset.ASSET_CAPTION_IN_ANIMATION:
                    mSelectedInAnimationPos = getTargetPosition(mMarchInAniList,
                            mCurAddCaption.getModularCaptionInAnimationPackageId());
                    mSelectedAnimationPos = 0;
                    break;
                case ASSET_CAPTION_OUT_ANIMATION:
                    mSelectedOutAnimationPos = getTargetPosition(mMarchOutAniList,
                            mCurAddCaption.getModularCaptionOutAnimationPackageId());
                    mSelectedAnimationPos = 0;
                    break;
                default:
                    break;
            }
        }
        int position = mCaptionStyleTab.getSelectedTabPosition();
        if (position == 0) {
            mRichWordFragment.setAssetInfoList(mRichWordList);
            mRichWordFragment.setSelectedPos(mSelectedRichPos);
        } else if (position == 1) {
            mAnimationFragment.setAssetList(mAnimationList, mMarchInAniList, mMarchOutAniList);
            mAnimationFragment.setSelectedPos(mSelectedAnimationPos, mSelectedInAnimationPos,
                    mSelectedOutAnimationPos);
        } else if (position == 2) {
            mBubbleFragment.setAssetInfoList(mBubbleList);
            mBubbleFragment.setSelectedPos(mSelectedBubblePos);
        }
    }

    /**
     * 获取所给集合中某一个条目的索引
     * Get the index of an item in the given set
     */
    private int getTargetPosition(List<AssetItem> targetList, String uuid) {
        int index = 0;
        if (targetList != null && !TextUtils.isEmpty(uuid)) {
            for (int i = 0; i < targetList.size(); i++) {
                AssetItem assetItem = targetList.get(i);
                if (assetItem == null || assetItem.getAsset() == null) {
                    continue;
                }
                if (uuid.equals(assetItem.getAsset().uuid)) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    private int getCaptionColorSelectedIndex() {
        int selectedPos = -1;
        //获取的颜色是#ffffffff，比较颜色忽略透明度，只保留后面色值
        //        The obtained color is #ffffffff, compare the colors to ignore the transparency, and only keep the following color values
        int subIndex = 3;
        int captionIndex = getCaptionIndex(mCurCaptionZVal);
        if (captionIndex >= 0) {
            String captionColor = mCaptionDataListClone.get(captionIndex).getCaptionColor();
            if (captionColor != null && captionColor.length() > subIndex) {
                captionColor = captionColor.substring(subIndex);
            }
            for (int i = 0; i < mCaptionColorList.size(); ++i) {
                String colorIndex = mCaptionColorList.get(i).mColorValue;
                if (colorIndex != null && colorIndex.length() > subIndex) {
                    colorIndex = colorIndex.substring(subIndex);
                }
                if (colorIndex != null && colorIndex.equals(captionColor)) {
                    selectedPos = i;
                    break;
                }
            }
        }
        return selectedPos;
    }

    private int getOutlineColorSelectedIndex() {
        int selectedPos = 0;
        int captionIndex = getCaptionIndex(mCurCaptionZVal);
        if (captionIndex >= 0) {
            String outlineColor = mCaptionDataListClone.get(captionIndex).getOutlineColor();
            for (int i = 0; i < mCaptionOutlineColorList.size(); ++i) {
                if (mCaptionOutlineColorList.get(i).mColorValue.compareTo(outlineColor) == 0) {
                    selectedPos = i;
                    break;
                }
            }
        }
        return selectedPos;
    }

    private int getBackgroundSelectedIndex() {
        int selectedPos = 0;
        int captionIndex = getCaptionIndex(mCurCaptionZVal);
        if (captionIndex >= 0) {
            String backgroundColor = mCaptionDataListClone.get(captionIndex).getCaptionBackground();
            for (int i = 0; i < mCaptionBackgroundList.size(); ++i) {
                //去掉透明度比较 Remove the transparency comparison
                String initColor = mCaptionBackgroundList.get(i).mColorValue;
                //判断要改一下，这个里不比较透明度 change it, it's not more transparent here
                if (!TextUtils.isEmpty(backgroundColor) && backgroundColor.length() == 9) {

                    if (!TextUtils.isEmpty(initColor)) {
                        String initColorWithoutAlpha = initColor.substring(3);
                        String backgroundColorWithoutAlpha = backgroundColor.substring(3);
                        if (initColorWithoutAlpha.compareTo(backgroundColorWithoutAlpha) == 0) {
                            selectedPos = i;
                            break;
                        }
                    }
                }
            }
        }
        return selectedPos;
    }

    private int getCaptionFontSelectedIndex() {
        int selectedPos = 0;
        int captionIndex = getCaptionIndex(mCurCaptionZVal);
        if (captionIndex >= 0) {
            String captionFont = mCaptionDataListClone.get(captionIndex).getCaptionFont();
            if (TextUtils.isEmpty(captionFont)) {
                return 0;
            }
            for (int i = 0; i < mCaptionFontList.size(); ++i) {
                AssetItem assetItem = mCaptionFontList.get(i);
                NvAsset asset = assetItem.getAsset();
                if (asset == null) {
                    continue;
                }
                int assetMode = assetItem.getAssetMode();
                String comparePath = asset.bundledLocalDirPath;
                if (AssetItem.ASSET_LOCAL == assetMode) {
                    comparePath = asset.localDirPath;
                }
                //sd卡中的字体包也放到了ASSET_BUILTIN下，原资源获取有问题，此处判断多加一个local
                //The font package in the sd card is also placed under ASSET_BUILTIN,
                // there is a problem with the original resource acquisition, here it is judged to add a local
                //修改开始 modifyStart
                if (AssetItem.ASSET_BUILTIN == assetMode) {
                    if (TextUtils.isEmpty(comparePath)) {
                        comparePath = asset.localDirPath;
                    }
                }
                //修改结束 modifyEnd
                if (TextUtils.isEmpty(comparePath)) {
                    continue;
                }
                if (comparePath.compareTo(captionFont) == 0) {
                    selectedPos = i;
                    break;
                }
            }
        }
        return selectedPos;
    }

    private void updateCaption() {
        seekTimeline(BackupData.instance().getCurSeekTimelinePos());
        updateDrawRect();
    }

    private void updateDrawRect() {
        if (mCurAddCaption != null) {
            int alignVal = mCurAddCaption.getTextAlignment();
            mVideoFragment.setAlignIndex(alignVal);
            int captionIndex = getCaptionIndex((int) mCurAddCaption.getZValue());
            CaptionInfo captionInfo = mCaptionDataListClone.get(captionIndex);
            if (captionInfo != null) {
                Map<Long, KeyFrameInfo> keyFrameInfo = captionInfo.getKeyFrameInfo();
                if (keyFrameInfo.isEmpty()) {
                    mVideoFragment.setCurCaption(mCurAddCaption);
                    mVideoFragment.updateCaptionCoordinate(mCurAddCaption);
                    mVideoFragment.changeCaptionRectVisible();
                } else {
                    long duration =
                            mStreamingContext.getTimelineCurrentPosition(mTimeline) - mCurAddCaption.getInPoint();
                    mCurAddCaption.setCurrentKeyFrameTime(duration);

                    mVideoFragment.setCurCaption(mCurAddCaption);
                    mVideoFragment.updateCaptionCoordinate(mCurAddCaption);
                    mVideoFragment.changeCaptionRectVisible();

                    mCurAddCaption.removeKeyframeAtTime(TRANS_X, duration);
                    mCurAddCaption.removeKeyframeAtTime(TRANS_Y, duration);
                    mCurAddCaption.removeKeyframeAtTime(SCALE_X, duration);
                    mCurAddCaption.removeKeyframeAtTime(SCALE_Y, duration);
                    mCurAddCaption.removeKeyframeAtTime(ROTATION_Z, duration);
                }
            }
        }
    }

    private void seekTimeline(long timestamp) {
        mVideoFragment.seekTimeline(timestamp,
                NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
    }

    private int getCaptionIndex(int curZValue) {
        int index = -1;
        int count = mCaptionDataListClone.size();
        for (int i = 0; i < count; ++i) {
            int zVal = mCaptionDataListClone.get(i).getCaptionZVal();
            if (curZValue == zVal) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void selectCaption() {
        long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        List<NvsClipCaption> captionList = curVideoClip.getCaptionsByClipTimePosition(curPos);
        int captionCount = captionList.size();
        for (int index = 0; index < captionCount; index++) {
            int tmpZVal = (int) captionList.get(index).getZValue();
            if (mCurCaptionZVal == tmpZVal) {
                mCurAddCaption = captionList.get(index);
                checkInit();
                break;
            }
        }
    }

    public boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_waitFlag = false;
    }
}
