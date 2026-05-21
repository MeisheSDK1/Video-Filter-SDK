package com.meishe.sdkdemo.edit.Caption;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.meicam.sdk.NvsBodyOpacitySpan;
import com.meicam.sdk.NvsCaption;
import com.meicam.sdk.NvsCaptionSpan;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsColorSpan;
import com.meicam.sdk.NvsControlPointPair;
import com.meicam.sdk.NvsFontFamilySpan;
import com.meicam.sdk.NvsFontSizeRatioSpan;
import com.meicam.sdk.NvsItalicSpan;
import com.meicam.sdk.NvsNormalTextSpan;
import com.meicam.sdk.NvsOpacitySpan;
import com.meicam.sdk.NvsOutlineColorSpan;
import com.meicam.sdk.NvsOutlineOpacitySpan;
import com.meicam.sdk.NvsOutlineWidthSpan;
import com.meicam.sdk.NvsRendererIdSpan;
import com.meicam.sdk.NvsShadowOpacitySpan;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsUnderlineSpan;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.base.model.BaseMvpActivity;
import com.meishe.base.utils.BarUtils;
import com.meishe.base.utils.KeyboardUtils;
import com.meishe.base.utils.SizeUtils;
import com.meishe.engine.util.WhiteList;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.download.AssetDownloadActivity;
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
import com.meishe.sdkdemo.edit.view.MSEditText;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.ScreenUtils;
import com.meishe.sdkdemo.utils.SharedPreferencesUtils;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.dataInfo.CaptionInfo;
import com.meishe.sdkdemo.utils.dataInfo.KeyFrameInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.utils.ColorUtil;
import com.meishe.utils.ToastUtil;

import java.io.File;
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
import static com.meishe.utils.ColorUtil.nvsColorToHexString;
import static com.meishe.utils.PathUtils.LOCAL_EFFECT_CAPTION_STYLE;
import static com.meishe.utils.PathUtils.LOCAL_FONT;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yyj
 * @CreateDate : 2019/6/22.
 * @Description :视频编辑-字幕-样式-Fragment
 * @Description :VideoEdit-Caption-Style-Fragment
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CaptionStyleActivity extends BaseMvpActivity<CaptionPresenter>
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

    //设置子间距 相对于Timeline 长边的比例
    //Sets the scale of the sub spacing relative to the long side of the timeline
    private static final double CAPTION_PERCENT_SMALL_SPACING = -0.005;
    private static final double CAPTION_PERCENT_STANDARD_SPACING = 0;
    private static final double CAPTION_PERCENT_MORE_LARGE_SPACING = 0.02;
    private static final double CAPTION_PERCENT_LARGE_SPACING = 0.04;

    //字幕的行间距 行间距目前支持设置绝对值
    //Line spacing of subtitles Line spacing currently supports setting absolute values
    private static final float CAPTION_SMALL_LINE_SPACING = -10;
    private static final float CAPTION_STANDARD_LINE_SPACING = 0;
    private static final float CAPTION_MORE_LARGE_LINE_SPACING = 20;
    private static final float CAPTION_LARGE_LINE_SPACEING = 40;

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
    //花字  Rich word
    private ArrayList<AssetItem> mRichWordList;
    //组合动画  Composite animation
    private ArrayList<AssetItem> mAnimationList;
    //入场动画 In animation
    private ArrayList<AssetItem> mMarchInAniList;
    //出场动画 Out animation
    private ArrayList<AssetItem> mMarchOutAniList;
    //气泡 Bubble
    private ArrayList<AssetItem> mBubbleList;
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
    private int mSelectedShadowPos = 0;
    private int mSelectedFontPos = 0;
    NvsTimelineCaption mCurCaption = null;
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
    private CaptionFontSizeRatioFragment mCaptionFontSizeRatioFragment;
    private CaptionLetterSpacingFragment mCaptionLetterSpacingFragment;
    private ArrayList<CaptionColorInfo> mCaptionColorList;
    private ArrayList<CaptionColorInfo> mCaptionOutlineColorList;
    private ArrayList<CaptionColorInfo> mCaptionBackgroundList;
    private ArrayList<CaptionColorInfo> mCaptionShadowList;
    private ArrayList<AssetItem> mCaptionFontList;
    private ArrayList<AssetItem> mServerCaptionFontList;
    private int mCaptionColorOpacityValue = 100;
    private int mCaptionBackgroundOpacityValue = 100;
    private float mCaptionBackgroundCornerValue = 0;
    private float mCaptionBackgroundPeddingValue = 0;
    private float mCaptionOutlineWidthValue = 100f;
    private int mCaptionOutlineOpacityValue = 100;
    //    private int mCaptionSizeValue = 72;
    ArrayList<CaptionInfo> mCaptionDataListClone;
    private CaptionInfo mTempCaptionInfo;
    private int mCurCaptionZVal = 0;

    private boolean bIsStyleUuidApplyToAll = false;
    private boolean bIsCaptionColorApplyToAll = false;
    private boolean bIsOutlineApplyToAll = false;
    private boolean bIsCaptionBackgroundApplyToAll = false;
    private boolean bIsFontApplyToAll = false;
    private boolean bIsSizeApplyToAll = false;
    private boolean bIsPositionApplyToAll = false;
    private boolean bIsFontSizeRatioApplyToAll = false;
    private boolean bIsCaptionShadowToAll = false;
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
    private MSEditText mEtCaptionInput;
    private boolean isAddCaption;
    private int mKeyboardHeight;
    private View mDecorView;
    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;
    private boolean mIsVisibleForLast;
    private int mDefaultBottomViewHeight;
    private InputMethodManager mInputMethodManager;
    private long mCurTime;
    private int mSelectionStart;
    private int mSelectionEnd;
    private int mTopContainHeight = 1050;
    private int mBeforeSelect;
    private int mAfterSelect;

    private boolean isShowSoftInput = false;
    private float mFontDefaultSize;
    private CaptionShadowFragment mCaptionShadowFragment;
    private boolean bIsStyleUuidApplyToAllChange;
    private boolean bIsCaptionColorApplyToAllCheck;
    private boolean bIsFontSizeRatioApplyToAllCheck;
    private boolean bIsCaptionShadowToAllCheck;
    private boolean bIsOutlineApplyToAllCheck;
    private boolean bIsFontApplyToAllCheck;
    private boolean bIsLetterSpacingApplyToAllCheck;
    private boolean bIsCaptionBackgroundApplyToAllCheck;
    private Context mContext;

    @Override
    protected int bindLayout() {
        mContext = this;
        AppManager.getInstance().addActivity(this);
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_caption_style;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

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
        WeakReference<CaptionStyleActivity> mWeakReference;

        public CaptionStyleHandler(CaptionStyleActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final CaptionStyleActivity activity = mWeakReference.get();
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
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        //addOnSoftKeyBoardVisibleListener();
        addKeyBoardListener();
    }

    @Override
    protected void initView() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mCaptionStyleTab = (TabLayout) findViewById(R.id.captionStyleTab);
        mViewPager = (CustomViewPager) findViewById(R.id.viewPager);
        mViewPager.setPagingEnabled(false);
        mCaptionAssetFinish = (ImageView) findViewById(R.id.captionAssetFinish);
        mEtCaptionInput = findViewById(R.id.et_caption_input);

        mBottomLayout.post(new Runnable() {
            @Override
            public void run() {
                int height = mBottomLayout.getLayoutParams().height;
            }
        });

        mEtCaptionInput.setOnKeyBoardHideListener(new MSEditText.OnKeyBoardHideListener() {
            @Override
            public void onKeyHide(int keyCode, KeyEvent event) {

            }
        });
        initTitle();
    }

    protected void initTitle() {
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void requestData() {
        mDefaultBottomViewHeight = SizeUtils.dp2px(320);
        mInputMethodManager =
                (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        initAssetData();
        initVideoFragment();
        initTabLayout();

        NvsVideoTrack videoTrackByIndex = mTimeline.getVideoTrackByIndex(0);
        videoTrackByIndex.setVolumeGain(0, 0);
        mBottomLayout.post(new Runnable() {
            @Override
            public void run() {
                mVideoFragment.playVideo(mCurTime, mCurTime + 40000);
            }
        });
        mBottomLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                videoTrackByIndex.setVolumeGain(1, 1);
                if (isAddCaption) {
                    addCaption(getString(R.string.text_hold_caption), isTraditionCaption);
                    setEditText(getString(R.string.text_hold_caption));
                }
                seekTimeline(mCurTime + 40000);
            }
        }, 600);
        mPresenter.getFonts();
        initListener();
    }

    private void showSoftKeyboard(long delay, int height) {
        if (height > 0) {
            changeViewHeight(height);
            mEtCaptionInput.postDelayed(new Runnable() {
                @Override
                public void run() {
                    KeyboardUtils.showSoftInput(mEtCaptionInput, InputMethodManager.SHOW_IMPLICIT);
                }
            }, delay);
        } else {
            mEtCaptionInput.postDelayed(new Runnable() {
                @Override
                public void run() {

                    KeyboardUtils.showSoftInput(mEtCaptionInput, InputMethodManager.SHOW_IMPLICIT);

                    changeViewHeight(height);
                }
            }, delay);
        }
    }

    private void changeViewHeight(int need_height) {
        View viewById = findViewById(R.id.spaceLayout);
        ViewGroup.LayoutParams layoutParams = viewById.getLayoutParams();
        if (need_height > 0) {
            changeBottomViewParams(need_height);
            int screenHeight = ScreenUtils.getScreenHeight(mContext);
            mTopContainHeight = screenHeight - need_height;
            //            Log.d("lpf", "  mTopContainHeight=" + mTopContainHeight + "  screenHeight=" + screenHeight + " need_height=" + need_height);
            layoutParams.height = mTopContainHeight;
        } else {
            changeBottomViewParams(mDefaultBottomViewHeight);
            int screenHeight = ScreenUtils.getScreenHeight(mContext);
            mTopContainHeight = screenHeight - mDefaultBottomViewHeight;
            layoutParams.height = mTopContainHeight;
            //            Log.d("lpf", "  mTopContainHeight=" + mTopContainHeight + "  screenHeight=" + screenHeight + " need_height" + need_height);
        }
        viewById.setLayoutParams(layoutParams);
    }

    private void changeBottomViewParams(int height) {
        ViewGroup.LayoutParams layoutParams = mBottomLayout.getLayoutParams();
        layoutParams.height = height;
        mBottomLayout.setLayoutParams(layoutParams);

        mEtCaptionInput.setFocusable(true);
        mEtCaptionInput.setFocusableInTouchMode(true);
        mEtCaptionInput.requestFocus();
    }

    protected void initListener() {
        mCaptionAssetFinish.setOnClickListener(this);
        if (mVideoFragment != null) {
            mVideoFragment.setCaptionTextEditListener(new VideoFragment.VideoCaptionTextEditListener() {
                @Override
                public void onCaptionTextEdit() {
                    //                    /*
                    //                     * 字幕编辑
                    //                     * Caption editing
                    //                     * */
                    //                    InputDialog inputDialog = new InputDialog(CaptionStyleActivity.this, R.style.dialog, new InputDialog.OnCloseListener() {
                    //                        @Override
                    //                        public void onClick(Dialog dialog, boolean ok) {
                    //                            if (ok) {
                    //                                InputDialog d = (InputDialog) dialog;
                    //                                String userInputText = d.getUserInputText();
                    //                                mCurCaption.setText(userInputText);
                    //                                updateCaption();
                    //                                int index = getCaptionIndex(mCurCaptionZVal);
                    //                                if (index >= 0) {
                    //                                    mCaptionDataListClone.get(index).setText(userInputText);
                    //                                }
                    //                            }
                    //                        }
                    //                    });
                    //
                    //                    if (mCurCaption != null) {
                    //                        inputDialog.setUserInputText(mCurCaption.getText());
                    //                    }
                    //                    inputDialog.show();
                }
            });
            mVideoFragment.setAssetEditListener(new VideoFragment.AssetEditListener() {
                @Override
                public void onAssetDelete() {
                    mTimeline.removeCaption(mCurCaption);
                    mCurCaption = null;
                    mVideoFragment.setCurCaption(mCurCaption);
                    mVideoFragment.changeCaptionRectVisible();
                    int index = getCaptionIndex(mCurCaptionZVal);
                    if (index >= 0) {
                        mCaptionDataListClone.remove(index);
                        BackupData.instance().setCaptionData(mCaptionDataListClone);
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
                    if (mCurCaption == null) {
                        return;
                    }
                    //Log.e(TAG,"captionTranslation.x = " + captionTranslation.x + "pointF.y =" + captionTranslation.y);
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index)
                                .setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        mCaptionDataListClone.get(index).setTranslation(mCurCaption.getCaptionTranslation());
                    }
                }

                @Override
                public void onAssetScale() {
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index)
                                .setUsedScaleRotationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        mCaptionDataListClone.get(index).setScaleFactorX(mCurCaption.getScaleX());
                        mCaptionDataListClone.get(index).setScaleFactorY(mCurCaption.getScaleY());
                        mCaptionDataListClone.get(index).setAnchor(mCurCaption.getAnchorPoint());
                        mCaptionDataListClone.get(index).setRotation(mCurCaption.getRotationZ());
                        //                        mCaptionDataListClone.get(index).setCaptionSize(mCurAddCaption.getFontSize());
                        mCaptionDataListClone.get(index).setTranslation(mCurCaption.getCaptionTranslation());
                    }
                }

                @Override
                public void onAssetAlign(int alignVal) {
                    int zVal = (int) mCurCaption.getZValue();
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
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index)
                                .setOrientationType(
                                        isHorizontal ? CaptionInfo.O_HORIZONTAL : CaptionInfo.O_VERTICAL);
                    }
                }
            });

            mVideoFragment.setOnRectScaleListener(new VideoFragment.OnDoubleFlingerScaleListener() {
                @Override
                public void onScale(View view, float onScale) {
                    if (mCurCaption == null) {
                        return;
                    }
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index)
                                .setUsedScaleRotationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        mCaptionDataListClone.get(index).setScaleFactorX(onScale);
                        mCaptionDataListClone.get(index).setScaleFactorY(onScale);
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
                        if (mCurCaption == null) {
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
                        if (mCurCaption == null) {
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

        //        mEtCaptionInput.setOnLongClickListener(new View.OnLongClickListener() {
        //            @Override
        //            public boolean onLongClick(View v) {
        ////                PopupMenu menu = new PopupMenu(mContext,v);
        ////                MenuInflater inflater=menu.getMenuInflater();
        ////                inflater.inflate(R.menu.selection_action_menu, menu.getMenu());
        ////                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
        ////                    @Override
        ////                    public boolean onMenuItemClick(MenuItem item) {
        ////                        return false;
        ////                    }
        ////                });
        ////                menu.show();
        //                return false;
        //            }
        //        });

        //        ViewTreeObserver viewTreeObserver = mEtCaptionInput.getViewTreeObserver();
        //        viewTreeObserver.addOnTouchModeChangeListener(new ViewTreeObserver.OnTouchModeChangeListener() {
        //            @Override
        //            public void onTouchModeChanged(boolean isInTouchMode) {
        //                Log.e("lpf","-------------onTouchModeChanged-------------------------------");
        //            }
        //        });

        //        Drawable mSelectHandleLeft = mEtCaptionInput.getTextSelectHandleLeft();
        //        Drawable mSelectHandleRight = mEtCaptionInput.getTextSelectHandleRight();
        //        mSelectHandleLeft.setCallback(new Drawable.Callback() {
        //            @Override
        //            public void invalidateDrawable(@NonNull Drawable who) {
        //                Log.e("lpf","-------------invalidateDrawable-------------------------------");
        //            }
        //
        //            @Override
        //            public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
        //                Log.e("lpf","-------------scheduleDrawable-------------------------------");
        //            }
        //
        //            @Override
        //            public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
        //                Log.e("lpf","-------------unscheduleDrawable-------------------------------");
        //            }
        //        });

        mEtCaptionInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    InputMethodManager inputMethodManager =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    mEtCaptionInput.clearFocus();
                    return true;
                }
                return false;
            }
        });

        mEtCaptionInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                refreshStartEnd();

                mBeforeSelect = mEtCaptionInput.getSelectionStart();
                //                Log.e("lpf", "mBeforeSelect=" + mBeforeSelect);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshStartEnd();
            }

            @Override
            public void afterTextChanged(Editable s) {
                refreshStartEnd();
                String content = s.toString();
                mAfterSelect = mEtCaptionInput.getSelectionStart();
                //                Log.e("lpf", "mAfterSelect=" + mAfterSelect);

                if (mCurCaption != null) {
                    List<NvsCaptionSpan> textSpanList = mCurCaption.getTextSpanList();
                    if (mBeforeSelect < mAfterSelect) {
                        for (int i = textSpanList.size() - 1; i >= 0; i--) {
                            NvsCaptionSpan nvsCaptionSpan = textSpanList.get(i);
                            int start = nvsCaptionSpan.getStart();
                            int end = nvsCaptionSpan.getEnd();
                            int offset = mAfterSelect - mBeforeSelect;
                            if (mBeforeSelect == end) {
                                nvsCaptionSpan.setEnd(mAfterSelect);
                                continue;
                            } else if (start < mBeforeSelect && end > mBeforeSelect) {
                                nvsCaptionSpan.setEnd(nvsCaptionSpan.getEnd() + offset);
                            } else if (start >= mBeforeSelect) {
                                nvsCaptionSpan.setStart(nvsCaptionSpan.getStart() + offset);
                                nvsCaptionSpan.setEnd(nvsCaptionSpan.getEnd() + offset);
                            }
                        }
                    } else {
                        for (int i = textSpanList.size() - 1; i >= 0; i--) {
                            NvsCaptionSpan nvsCaptionSpan = textSpanList.get(i);
                            int start = nvsCaptionSpan.getStart();
                            int end = nvsCaptionSpan.getEnd();
                            int offset = mAfterSelect - mBeforeSelect;
                            if (mAfterSelect <= start) {
                                textSpanList.remove(nvsCaptionSpan);
                                continue;
                            }
                            if (mBeforeSelect > start && mAfterSelect < end) {
                                nvsCaptionSpan.setEnd(nvsCaptionSpan.getEnd() + offset);
                                if (start >= end) {
                                    textSpanList.remove(nvsCaptionSpan);
                                }
                                continue;
                            } else if (start >= mBeforeSelect) {
                                nvsCaptionSpan.setEnd(nvsCaptionSpan.getEnd() + offset);
                                nvsCaptionSpan.setStart(nvsCaptionSpan.getStart() + offset);
                            }
                        }
                    }
                    mCurCaption.setTextSpanList(textSpanList);
                }

                if (TextUtils.isEmpty(content)) {
                    CaptionInfo currentCaptionInfo = getCurrentCaptionInfo();
                    currentCaptionInfo.setText("");
                    mTempCaptionInfo = currentCaptionInfo.clone();
                    mTimeline.removeCaption(mCurCaption);
                    mCurCaption = null;
                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                    mVideoFragment.setDrawRectVisible(View.GONE);
                    mCaptionDataListClone.remove(currentCaptionInfo);
                    isAddCaption = true;
                } else {
                    if (isAddCaption && mCurCaption == null) {
                        addCaption(content, isTraditionCaption);
                    } else {
                        updateCaption(content);
                    }
                }
            }
        });

        mEtCaptionInput.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                refreshStartEnd();
                //                Log.e("lpf", "onCreateActionMode-------------" +
                //                        "mSelectionStart=" + mSelectionStart + "mSelectionEnd=" + mSelectionEnd);
                //                MenuInflater menuInflater = actionMode.getMenuInflater()
                //                menu.clear();
                //                menuInflater.inflate(R.menu.selection_action_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                refreshStartEnd();
                //                Log.e("lpf", "onPrepareActionMode-------------" +
                //                        "mSelectionStart=" + mSelectionStart + "mSelectionEnd=" + mSelectionEnd);
                MenuInflater menuInflater = actionMode.getMenuInflater();
                menu.clear();
                menuInflater.inflate(R.menu.selection_action_menu, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.edit_style) {
                    refreshStartEnd();
                    hideSoftInput(mEtCaptionInput);
                } else if (itemId == R.id.edit_cut) {
                    refreshStartEnd();
                    String content = mEtCaptionInput.getText().toString();
                    String substring = content.substring(mSelectionStart, mSelectionEnd);
                    //                        ToastUtil.showToast(mContext, "selectionStart="+selectionStart+
                    //                                " selectionEnd="+selectionEnd +" substring+"+substring);
                    mEtCaptionInput.getText().replace(mSelectionStart, mSelectionEnd, "");
                    cut(substring);
                    ToastUtil.showToast(mContext, getString(R.string.toast_caption_style_text_cut));
                    actionMode.finish();
                } else if (itemId == R.id.edit_copy) {
                    String substring;
                    String content;
                    refreshStartEnd();
                    content = mEtCaptionInput.getText().toString();
                    substring = content.substring(mSelectionStart, mSelectionEnd);
                    copyFromEditText(substring);
                    ToastUtil.showToast(mContext, getString(R.string.toast_caption_style_text_copy));
                    actionMode.finish();
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

                refreshStartEnd();
            }
        });
        //mVideoFragment.setIsLimitPlay(true);
    }

    final int Menu_1 = Menu.FIRST;
    final int Menu_2 = Menu.FIRST + 1;
    final int Menu_3 = Menu.FIRST + 2;
    private ClipboardManager mClipboard = null;

    public void onCreateContextMenu(ContextMenu m, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(m, v, menuInfo);

        //在上下文菜单选项中添加选项内容
        //        Add option content to context menu options
        //add方法的参数：add(分组id,itemid, 排序, 菜单文字)
        //Parameters of the add method: add (group id, itemid, sort, menu text)
        m.add(0, Menu_1, 0, getString(R.string.caption_style_activity_text_copy));
        m.add(0, Menu_2, 1, getString(R.string.caption_style_activity_text_paste));
        m.add(0, Menu_3, 2, getString(R.string.caption_style_activity_text_select_all));
    }

    private void copyFromEditText(String content) {

        // Gets a handle to the clipboard service.
        if (null == mClipboard) {
            mClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        }

        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("simple text", content);

        // Set the clipboard's primary clip.
        mClipboard.setPrimaryClip(clip);
    }

    private void pasteToResult() {
        // Gets a handle to the clipboard service.
        if (null == mClipboard) {
            mClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        }

        String resultString = "";
        // 检查剪贴板是否有内容
        //Check the clipboard for content
        if (!mClipboard.hasPrimaryClip()) {
            Toast.makeText(mContext,
                    "Clipboard is empty", Toast.LENGTH_SHORT).show();
        } else {
            ClipData clipData = mClipboard.getPrimaryClip();
            int count = clipData.getItemCount();

            for (int i = 0; i < count; ++i) {

                ClipData.Item item = clipData.getItemAt(i);
                CharSequence str = item
                        .coerceToText(mContext);
                Log.i("mengdd", "item : " + i + ": " + str);

                resultString += str;
            }
        }
        mEtCaptionInput.setText(resultString);
    }

    //ContextMenu菜单选项的选项选择的回调事件
    //Callback event of the option selection of the ContextMenu menu option
    public boolean onContextItemSelected(MenuItem item) {
        //参数为用户选择的菜单选项对象
        //Parameter is the menu option object selected by the user
        //根据菜单选项的id来执行相应的功能
        //Execute corresponding functions according to the id of the menu option
        switch (item.getItemId()) {
            case 1:
                Toast.makeText(this, getString(R.string.caption_style_activity_text_copy),
                        Toast.LENGTH_SHORT).show();
                //                copyFromEditText();
                break;
            case 2:
                Toast.makeText(this, getString(R.string.caption_style_activity_text_paste),
                        Toast.LENGTH_SHORT).show();
                pasteToResult();
                break;
            case 3:
                Toast.makeText(this, getString(R.string.caption_style_activity_text_select_all),
                        Toast.LENGTH_SHORT).show();
                mEtCaptionInput.selectAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void cut(String content) {
        //获取剪贴版
        //Get clip board
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        //创建ClipData对象
        //Create a ClipData object
        //第一个参数只是一个标记，随便传入。
        //The first parameter is just a tag, which is passed in casually
        //第二个参数是要复制到剪贴版的内容
        //The second parameter is the content to be copied to the clip board
        ClipData clip = ClipData.newPlainText("simple text", content);
        //传入clipData对象.
        //Incoming clipData object
        clipboard.setPrimaryClip(clip);
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
        if (mCurCaption == null) {
            return null;
        }
        int zValue = (int) mCurCaption.getZValue();
        int captionIndex = getCaptionIndex(zValue);
        CaptionInfo captionInfo = mCaptionDataListClone.get(captionIndex);
        return captionInfo;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.captionAssetFinish) {
            applyToAllCaption();
            BackupData.instance().setCaptionData(mCaptionDataListClone);
            removeTimeline();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            intent.putExtra("isSelectCurCaption", true);
            intent.putExtra("isSelectCurCaptionZValue", mCurCaptionZVal);
            AppManager.getInstance().finishActivity();
        }
    }

    @Override
    public void onBackPressed() {
        removeTimeline();
        AppManager.getInstance().finishActivity();
        super.onBackPressed();
    }

    //
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTempCaptionInfo = null;
        mStreamingContext.clearCachedResources(true);
    }

    private void playVideoFormStart() {
        if (mCurCaption != null && mVideoFragment != null) {
            mVideoFragment.stopEngine();
            long startTime = mCurCaption.getInPoint();
            long endTime = mCurCaption.getOutPoint();
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
        if (mCaptionDataListClone == null) {
            return;
        }
        int count = mCaptionDataListClone.size();
        CaptionInfo curCaptionInfo = mCaptionDataListClone.get(index);
        for (int i = 0; i < count; ++i) {
            if (i == index) {
                continue;
            }
            CaptionInfo captionInfo = mCaptionDataListClone.get(i);
            NvsTimelineCaption captionByIndex = null;
            if (captionInfo != null) {
                captionByIndex = getCaptionByIndex(captionInfo.getCaptionZVal());
            }

            if (bIsStyleUuidApplyToAll) {
                captionInfo.setCaptionStyleUuid(curCaptionInfo.getCaptionStyleUuid());
                if (captionByIndex != null) {
                    captionByIndex.applyCaptionStyle(curCaptionInfo.getCaptionStyleUuid());
                }
            } else {
                if (bIsStyleUuidApplyToAllChange) {
                    captionInfo.setCaptionStyleUuid("");
                    if (captionByIndex != null) {
                        captionByIndex.applyCaptionStyle("");
                    }
                }
            }

            if (bIsCaptionColorApplyToAll) {
                captionInfo.setCaptionColor(curCaptionInfo.getCaptionColor());
                captionInfo.setCaptionColorAlpha(curCaptionInfo.getCaptionColorAlpha());
                captionInfo.setUsedColorFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);

                if (captionByIndex != null) {
                    NvsColor color = ColorUtil.colorStringtoNvsColor(captionInfo.getCaptionColor());
                    color.a = captionInfo.getCaptionColorAlpha() / 100f;
                    captionByIndex.setTextColor(color);
                }
            } else {
                if (bIsCaptionColorApplyToAllCheck) {
                    if (captionByIndex != null) {
                        NvsColor captionColor = captionByIndex.getTextColor();
                        captionInfo.setCaptionColor(ColorUtil.nvsColorToHexString(captionColor));
                        captionInfo.setCaptionColorAlpha(captionInfo.getCaptionColorAlpha());
                        captionInfo.setUsedColorFlag(CaptionInfo.ATTRIBUTE_UNUSED_FLAG);
                        captionByIndex.setTextColor(captionColor);
                    }
                }
            }

            if (bIsFontSizeRatioApplyToAll) {
                float captionSize = curCaptionInfo.getCaptionSize();
                captionInfo.setCaptionSize(captionSize);
                if (captionByIndex != null) {
                    captionByIndex.setFontSize(captionSize);
                }
            } else {
                if (bIsFontSizeRatioApplyToAllCheck) {
                    captionInfo.setCaptionSize(mFontDefaultSize);
                    if (captionByIndex != null) {
                        captionByIndex.setFontSize(mFontDefaultSize);
                    }
                }
            }
            if (bIsCaptionShadowToAll) {
                String captionShadowColor = curCaptionInfo.getShadowColor();
                int usedShadowFlag = curCaptionInfo.getUsedShadowFlag();
                captionInfo.setShadowColor(captionShadowColor);
                captionInfo.setUsedShadowFlag(usedShadowFlag);
                captionInfo.setShadow(curCaptionInfo.isShadow());
                if (captionByIndex != null
                        && !TextUtils.isEmpty(captionShadowColor)
                        && curCaptionInfo.isShadow()) {
                    NvsColor nvsColor = ColorUtil.colorStringtoNvsColor(captionShadowColor);
                    captionByIndex.setDrawShadow(true);
                    captionByIndex.setShadowColor(nvsColor);
                }
            } else {
                if (bIsCaptionShadowToAllCheck) {
                    captionInfo.setShadowColor("");
                    captionInfo.setUsedShadowFlag(CaptionInfo.ATTRIBUTE_UNUSED_FLAG);
                    captionInfo.setShadow(false);
                    if (captionByIndex != null) {
                        NvsColor nvsColor = new NvsColor(0, 0, 0, 0);
                        captionByIndex.setDrawShadow(false);
                        captionByIndex.setShadowColor(nvsColor);
                    }
                }
            }

            if (bIsOutlineApplyToAll) {
                boolean hasOutline = curCaptionInfo.isHasOutline();
                captionInfo.setHasOutline(hasOutline);
                captionInfo.setOutlineColor(curCaptionInfo.getOutlineColor());
                int outlineColorAlpha = curCaptionInfo.getOutlineColorAlpha();
                captionInfo.setOutlineColorAlpha(outlineColorAlpha);
                captionInfo.setOutlineWidth(curCaptionInfo.getOutlineWidth());
                captionInfo.setUsedOutlineFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                if (captionByIndex != null) {
                    if (hasOutline) {
                        captionByIndex.setDrawOutline(true);
                        captionByIndex.setOutlineWidth(curCaptionInfo.getOutlineWidth());
                        NvsColor color = ColorUtil.colorStringtoNvsColor(curCaptionInfo.getOutlineColor());
                        color.a = outlineColorAlpha / 100f;
                        captionByIndex.setOutlineColor(color);
                    }
                }
            } else {
                if (bIsOutlineApplyToAllCheck) {
                    captionInfo.setHasOutline(false);
                    captionInfo.setOutlineColor("");
                    captionInfo.setOutlineColorAlpha(0);
                    captionInfo.setOutlineWidth(curCaptionInfo.getOutlineWidth());
                    captionInfo.setUsedOutlineFlag(CaptionInfo.ATTRIBUTE_UNUSED_FLAG);
                    if (captionByIndex != null) {
                        captionByIndex.setDrawOutline(false);
                        captionByIndex.setOutlineWidth(0);
                        captionByIndex.setOutlineColor(null);
                    }
                }
            }
            if (bIsFontApplyToAll) {
                captionInfo.setCaptionFont(curCaptionInfo.getCaptionFont());
                captionInfo.setBold(curCaptionInfo.isBold());
                captionInfo.setItalic(curCaptionInfo.isItalic());
                captionInfo.setUnderline(curCaptionInfo.isUnderline());
                captionInfo.setUsedUnderlineFlag(curCaptionInfo.getUsedUnderlineFlag());
                captionInfo.setUsedIsBoldFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                captionInfo.setUsedIsItalicFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                if (captionByIndex != null) {
                    captionByIndex.setFontByFilePath(curCaptionInfo.getCaptionFont());
                    captionByIndex.setBold(curCaptionInfo.isBold());
                    captionByIndex.setItalic(curCaptionInfo.isShadow());
                    captionByIndex.setDrawShadow(curCaptionInfo.isShadow());
                    captionByIndex.setUnderline(curCaptionInfo.isUnderline());
                }
            } else {
                if (bIsFontApplyToAllCheck) {
                    captionInfo.setCaptionFont("");
                    captionInfo.setBold(false);
                    captionInfo.setItalic(false);
                    captionInfo.setUnderline(false);
                    captionInfo.setUsedUnderlineFlag(CaptionInfo.ATTRIBUTE_UNUSED_FLAG);
                    captionInfo.setUsedIsBoldFlag(CaptionInfo.ATTRIBUTE_UNUSED_FLAG);
                    captionInfo.setUsedIsItalicFlag(CaptionInfo.ATTRIBUTE_UNUSED_FLAG);
                    if (captionByIndex != null) {
                        captionByIndex.setFontByFilePath("");
                        captionByIndex.setBold(false);
                        captionByIndex.setItalic(false);
                        captionByIndex.setUnderline(false);
                    }
                }
            }

            if (bIsLetterSpacingApplyToAll) {
                captionInfo.setUsedLetterSpacingFlag(curCaptionInfo.getUsedLetterSpacingFlag());
                captionInfo.setLetterSpacing(curCaptionInfo.getLetterSpacing());
                captionInfo.setUsedLetterSpacingFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                captionInfo.setLineSpacing(curCaptionInfo.getLineSpacing());

                if (captionByIndex != null) {
                    captionByIndex.setLetterSpacingType(NvsCaption.LETTER_SPACING_TYPE_ABSOLUTE);
                    captionByIndex.setLetterSpacing(curCaptionInfo.getLetterSpacing());
                    captionByIndex.setLineSpacing(curCaptionInfo.getLineSpacing());
                }
            } else {
                if (bIsLetterSpacingApplyToAllCheck) {
                    captionInfo.setUsedLetterSpacingFlag(CaptionInfo.ATTRIBUTE_UNUSED_FLAG);
                    captionInfo.setLetterSpacing(curCaptionInfo.getLetterSpacing());
                    captionInfo.setUsedLetterSpacingFlag(CaptionInfo.ATTRIBUTE_UNUSED_FLAG);
                    captionInfo.setLineSpacing(curCaptionInfo.getLineSpacing());

                    if (captionByIndex != null) {
                        captionByIndex.setLetterSpacingType(NvsCaption.LETTER_SPACING_TYPE_ABSOLUTE);
                        float letterSpace = getLetterSpacing(CAPTION_PERCENT_STANDARD_SPACING);
                        captionByIndex.setLetterSpacing(letterSpace);
                        captionByIndex.setLineSpacing(0);
                    }
                }
            }
            if (bIsCaptionBackgroundApplyToAll) {
                captionInfo.setCaptionBackground(curCaptionInfo.getCaptionBackground());
                captionInfo.setCaptionBackgroundAlpha(curCaptionInfo.getCaptionBackgroundAlpha());
                captionInfo.setCaptionBackgroundRadius(curCaptionInfo.getCaptionBackgroundRadius());
                captionInfo.setCaptionBackgroundPadding(curCaptionInfo.getCaptionBackgroundPadding());
                captionInfo.setUsedBackgroundFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                captionInfo.setUsedBackgroundRadiusFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                captionInfo.setUsedBackgroundPaddingFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                if (captionByIndex != null) {
                    String captionBackground = curCaptionInfo.getCaptionBackground();
                    NvsColor color = ColorUtil.colorStringtoNvsColor(captionBackground);
                    color.a = curCaptionInfo.getCaptionBackgroundAlpha() / 100f;
                    captionByIndex.setBackgroundColor(color);
                    captionByIndex.setBackgroundRadius(curCaptionInfo.getCaptionBackgroundRadius());
                    captionByIndex.setBoundaryPaddingRatio(curCaptionInfo.getCaptionBackgroundPadding());
                }
            } else {
                if (bIsCaptionBackgroundApplyToAllCheck) {
                    captionInfo.setCaptionBackground("");
                    captionInfo.setCaptionBackgroundAlpha(0);
                    captionInfo.setCaptionBackgroundRadius(0);
                    captionInfo.setCaptionBackgroundPadding(0);
                    captionInfo.setUsedBackgroundFlag(CaptionInfo.ATTRIBUTE_UNUSED_FLAG);
                    captionInfo.setUsedBackgroundRadiusFlag(CaptionInfo.ATTRIBUTE_UNUSED_FLAG);
                    captionInfo.setUsedBackgroundPaddingFlag(CaptionInfo.ATTRIBUTE_UNUSED_FLAG);

                    if (captionByIndex != null) {
                        NvsColor nvsColor = new NvsColor(0, 0, 0, 0);
                        captionByIndex.setBackgroundColor(nvsColor);
                        captionByIndex.setBackgroundRadius(0);
                        captionByIndex.setBoundaryPaddingRatio(0);
                    }
                }
            }
        }
        if (bIsPositionApplyToAll) {
            updateCaptionPosition();
        }
        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
    }

    public NvsTimelineCaption getCaptionByIndex(int index) {
        if (mTimeline == null) {
            return null;
        }
        NvsTimelineCaption firstCaption = mTimeline.getFirstCaption();

        while (firstCaption != null) {
            if (firstCaption.getZValue() == index) {
                return firstCaption;
            }
            firstCaption = mTimeline.getNextCaption(firstCaption);
        }
        return null;
    }

    private void updateCaptionPosition() {
        NvsTimelineCaption caption = mTimeline.getFirstCaption();
        while (caption != null) {
            if (caption.getCategory() == NvsTimelineCaption.THEME_CATEGORY
                    && caption.getRoleInTheme() != NvsTimelineCaption.ROLE_IN_THEME_GENERAL) {//主题字幕不作处理
                caption = mTimeline.getNextCaption(caption);
                continue;
            }
            int zVal = (int) caption.getZValue();
            if (mCurCaptionZVal == zVal) {
                caption = mTimeline.getNextCaption(caption);
                continue;
            }
            List<PointF> list = caption.getBoundingRectangleVertices();
            if (list == null || list.size() < 4) {
                caption = mTimeline.getNextCaption(caption);
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
            caption = mTimeline.getNextCaption(caption);
        }
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mCurCaption = null;
        mTimeline = null;
        m_handler.removeCallbacksAndMessages(null);
        stopProgressTimer();
    }

    private void initTabLayout() {
        String[] assetName;
        if (isTraditionCaption) {
            //传统字幕 Traditional subtitles
            assetName = getResources().getStringArray(R.array.captionEdit);
            mCaptionStyleTab.setSelectedTabIndicatorColor(getResources().getColor(R.color.blue_4a));
        } else {
            //拼装字幕 Assemble subtitles
            assetName = getResources().getStringArray(R.array.pieced_caption);
            mCaptionStyleTab.setSelectedTabIndicatorColor(getResources().getColor(R.color.red_ff64));
        }
        for (int i = 0; i < assetName.length; i++) {
            TextView textView = new TextView(this);
            textView.setText(assetName[i]);
            textView.setTextSize(12);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(getResources().getColor(R.color.gray_90));
            TabLayout.Tab tab = mCaptionStyleTab.newTab().setCustomView(textView);
            tab.getCustomView().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    tab.select();
                    return true;
                }
            });
            mCaptionStyleTab.addTab(tab);
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
                //hideSoftInput(mEtCaptionInput);
                //强制切换键盘的弹起状态
                //Force the keyboard to switch its pop-up state
                if (mIsVisibleForLast) {
                    mInputMethodManager.toggleSoftInput(1, 2);
                }
                //mEtCaptionInput.clearFocus();
                /*
                 * 当前选中的tab的位置，切换到相应的fragment
                 * Position of the currently selected tab, switch to the corresponding fragment
                 * */
                int nowPosition = tab.getPosition();
                if (nowPosition == 1 && mCurCaption != null) {
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    public void hideSoftInput(@NonNull final View view) {
        if (mInputMethodManager == null) {
            return;
        }
        mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
            if (!TextUtils.isEmpty(mCurCaption.getModularCaptionAnimationPackageId())) {
                displayAnimationProgress(true, NvAsset.ASSET_CAPTION_ANIMATION);
            } else {
                if (!TextUtils.isEmpty(mCurCaption.getModularCaptionOutAnimationPackageId()) &&
                        !TextUtils.isEmpty(mCurCaption.getModularCaptionInAnimationPackageId())) {
                    int maxDuration = (int) ((mCurCaption.getOutPoint() - mCurCaption.getInPoint()) / 1000);
                    if (maxDuration - mCurCaption.getModularCaptionInAnimationDuration()
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
                if (!TextUtils.isEmpty(mCurCaption.getModularCaptionOutAnimationPackageId())) {
                    displayAnimationProgress(true, NvAsset.ASSET_CAPTION_OUT_ANIMATION);
                }
                if (!TextUtils.isEmpty(mCurCaption.getModularCaptionInAnimationPackageId())) {
                    displayAnimationProgress(true, NvAsset.ASSET_CAPTION_IN_ANIMATION);
                }
            }
            animationFragmentFirstSelect = false;
        } else {
            //动画tab被选中 Animation tab is selected
            displaySeekBar(!TextUtils.isEmpty(mCurCaption.getModularCaptionAnimationPackageId()) ||
                    !TextUtils.isEmpty(mCurCaption.getModularCaptionOutAnimationPackageId())
                    || !TextUtils.isEmpty(mCurCaption.getModularCaptionOutAnimationPackageId()));
        }
    }

    private void initCaptionTabFragment() {
        if (isTraditionCaption) {
            mCaptionStyleFragment = initCaptionStyleFragment();
            mAssetFragmentsArray.add(mCaptionStyleFragment);
        } else {
            mAssetFragmentsArray.add(initRichWordFragment());
            mAssetFragmentsArray.add(initAnimationFragment());
            mAssetFragmentsArray.add(initBubbleFragment());
        }
        mCaptionColorFragment = initCaptionColorFragment();
        mAssetFragmentsArray.add(mCaptionColorFragment);
        mCaptionOutlineFragment = initCaptionOutlineFragment();
        mAssetFragmentsArray.add(mCaptionOutlineFragment);
        mCaptionShadowFragment = initCaptionShadowFragment();
        mAssetFragmentsArray.add(mCaptionShadowFragment);
        mCaptionBackgroundFragment = initCaptionBackgroundFragment();
        mAssetFragmentsArray.add(mCaptionBackgroundFragment);

        mCaptionFontFragment = initCaptionFontFragment();
        mAssetFragmentsArray.add(mCaptionFontFragment);
        mCaptionSizeFragment = initCaptionSizeFragment();
        mCaptionLetterSpacingFragment = initCaptionLetterSpacingFragment();
        mAssetFragmentsArray.add(mCaptionLetterSpacingFragment);
        mCaptionPositionFragment = initCaptionPositionFragment();
        mAssetFragmentsArray.add(mCaptionPositionFragment);

        mCaptionFontSizeRatioFragment = initCaptionFontSizeRatioFragment();
        mAssetFragmentsArray.add(mCaptionFontSizeRatioFragment);
    }

    private void initAssetData() {
        Intent intent = getIntent();
        if (intent != null) {
            isTraditionCaption = intent.getBooleanExtra("tradition_caption", true);
            isAddCaption = intent.getBooleanExtra("isAdd", false);
            mCurTime = BackupData.instance().getCurrentTime();
        }
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null) {
            return;
        }

        mStreamingContext.seekTimeline(mTimeline, mCurTime,
                NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE,
                NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME);

        mEtCaptionInput.setFocusable(true);
        mEtCaptionInput.setFocusableInTouchMode(true);
        //        mEtCaptionInput.requestFocus();

        int need_height = (int) SharedPreferencesUtils.getParam(mContext, "need_height", 0);
        //TODO  适配google手机第一次安装 键盘输入无效
        //Keyboard input is invalid after the first installation of Google mobile phone
        if (WhiteList.isGooglePixel3()) {
            need_height = 1461;
        }
        if (WhiteList.isXiaoMi9()) {
            need_height = 1035;
        }

        if (isAddCaption) {
            mEtCaptionInput.clearFocus();
            mCaptionDataListClone = BackupData.instance().cloneCaptionData();
            if (mCaptionDataListClone != null) {
                TimelineUtil.setCaption(mTimeline, mCaptionDataListClone);
            }

            //            if (need_height > 0) {
            //                showSoftKeyboard(600, need_height);
            //            } else {
            //                showSoftKeyboard(1200, need_height);
            //            }

        } else {
            //            changeBottomViewParams(mDefaultBottomViewHeight);
            mCurCaptionZVal = BackupData.instance().getCaptionZVal();
            mCaptionDataListClone = BackupData.instance().cloneCaptionData();
            if (mCaptionDataListClone != null) {
                TimelineUtil.setCaption(mTimeline, mCaptionDataListClone);
            }
            selectCaption();
            if (mCurCaption != null) {
                setEditText(mCurCaption.getText());
                mFontDefaultSize = MSApplication.mDefaultCaptionSize;
            }
        }
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
        mCaptionShadowList = new ArrayList<>();
        mAssetFragmentsArray = new ArrayList<>();
        mCaptionColorList = new ArrayList<>();
        mCaptionOutlineColorList = new ArrayList<>();
        mCaptionFontList = new ArrayList<>();
        mServerCaptionFontList = new ArrayList<>();

        assetDataRequest();
        initCaptionBackgroundList();
        initCaptionColorList();
        initCaptionOutlineColorList();
        initCaptionShadowList();
    }

    private void checkInit() {
        if (mSeekBar == null) {
            return;
        }
        if (mCurCaption != null) {
            long duration = mCurCaption.getOutPoint() - mCurCaption.getInPoint();
            mSeekBar.setMaxProgress((int) (duration / 1000));
            mMaxDuration = duration;
            mSelectedRichPos =
                    getTargetPosition(mRichWordList, mCurCaption.getModularCaptionRendererPackageId());
            mSelectedBubblePos =
                    getTargetPosition(mBubbleList, mCurCaption.getModularCaptionContextPackageId());
            mSelectedAnimationPos =
                    getTargetPosition(mAnimationList, mCurCaption.getModularCaptionAnimationPackageId());
            mSelectedInAnimationPos =
                    getTargetPosition(mMarchInAniList, mCurCaption.getModularCaptionInAnimationPackageId());
            mSelectedOutAnimationPos =
                    getTargetPosition(mMarchOutAniList, mCurCaption.getModularCaptionOutAnimationPackageId());
        } else {
            //Log.d("lhz", "mCurAddCaption is null,mCurCaptionZVal=" + mCurCaptionZVal);
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
                        asset.name = Util.isZh(mContext) ? jsonFileInfo.getName_Zh() : jsonFileInfo.getName();
                        asset.aspectRatio = Integer.parseInt(jsonFileInfo.getFitRatio());
                        String coverPath = com.meishe.utils.PathUtils.getAssetFileBySuffixPic(
                                "captionstyle/" + jsonFileInfo.getFxPackageId());
                        if (coverPath.endsWith(".webp")) {
                            asset.coverUrl = "asset://android_asset/" + coverPath;
                        } else {
                            asset.coverUrl = "file:///android_asset/" + coverPath;
                        }
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

        PathUtils.getLocalResource(LOCAL_EFFECT_CAPTION_STYLE, mTotalCaptionStyleList);

        AssetItem assetItem = new AssetItem();
        NvAsset asset = new NvAsset();
        asset.name = getString(R.string.makeup_null);
        assetItem.setImageRes(R.mipmap.captionstyle_no);
        assetItem.setAssetMode(AssetItem.ASSET_NONE);
        assetItem.setAsset(asset);
        mTotalCaptionStyleList.add(0, assetItem);

        for (AssetItem item : mTotalCaptionStyleList) {
            if (!TextUtils.isEmpty(item.getAsset().localDirPath)) {
                NvsStreamingContext.getInstance().registerFontByFilePath(item.getAsset().localDirPath);
            } else if (!TextUtils.isEmpty(item.getAsset().bundledLocalDirPath)) {
                NvsStreamingContext.getInstance()
                        .registerFontByFilePath(item.getAsset().bundledLocalDirPath);
            }
        }
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
                        asset.name = isZh(this) ? jsonFileInfo.getName_Zh() : jsonFileInfo.getName();
                        if (!TextUtils.isEmpty(jsonFileInfo.getFitRatio())) {
                            try {
                                asset.aspectRatio = Integer.parseInt(jsonFileInfo.getFitRatio());
                            } catch (Exception e) {
                                Log.e(TAG, "Exception=" + e);
                            }
                        }

                        String coverPath = com.meishe.utils.PathUtils.getAssetFileBySuffixPic(
                                assetPath + File.separator + jsonFileInfo.getFxPackageId());
                        if (isAnimation) {
                            asset.coverUrl = "asset://android_asset/" + coverPath;
                        } else {
                            asset.coverUrl = "file:///android_asset/" + coverPath;
                        }
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
        mVideoFragment.setLayoutType(VideoFragment.TYPE_CAPTION);
        mVideoFragment.setFragmentLoadFinisedListener(
                new VideoFragment.OnFragmentLoadFinisedListener() {
                    @Override
                    public void onLoadFinished() {
                        seekTimeline(BackupData.instance().getCurSeekTimelinePos());
                        if (mCurCaption == null) {
                            selectCaption();
                        }

                        if (mCurCaption != null) {
                            int captionIndex = getCaptionIndex((int) mCurCaption.getZValue());
                            reloadKeyFrame(captionIndex);
                        }

                        mVideoFragment.setLiveWindowLayout();

                        mCaptionAssetFinish.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (mCurCaption != null) {
                                    int alignVal = mCurCaption.getTextAlignment();
                                    mVideoFragment.setAlignIndex(alignVal);

                                    long duration = mStreamingContext.getTimelineCurrentPosition(mTimeline)
                                            - mCurCaption.getInPoint();
                                    mCurCaption.setCurrentKeyFrameTime(duration);

                                    mVideoFragment.setCurCaption(mCurCaption);
                                    mVideoFragment.updateCaptionCoordinate(mCurCaption);
                                    mVideoFragment.changeCaptionRectVisible();
                                    //
                                    mCurCaption.removeKeyframeAtTime(TRANS_X, duration);
                                    mCurCaption.removeKeyframeAtTime(TRANS_Y, duration);
                                    mCurCaption.removeKeyframeAtTime(SCALE_X, duration);
                                    mCurCaption.removeKeyframeAtTime(SCALE_Y, duration);
                                    mCurCaption.removeKeyframeAtTime(ROTATION_Z, duration);
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
        if (mCaptionDataListClone == null) {
            return;
        }
        if (mCurCaption != null) {
            mCurCaption.setFontByFilePath(fontPath);
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

    private void initCaptionShadowList() {
        for (int index = 0; index < CaptionColors.length; ++index) {
            mCaptionShadowList.add(new CaptionColorInfo(CaptionColors[index], false));
        }
        mCaptionShadowList.add(0, new CaptionColorInfo("", false));
    }

    private void initCaptionFontList() {
        mCaptionFontList.clear();
        PathUtils.getLocalResource(LOCAL_FONT, mCaptionFontList);
        AssetItem noneFontInfo = new AssetItem();
        noneFontInfo.setAsset(new NvAsset());
        noneFontInfo.setImageRes(R.mipmap.captionstyle_no);
        noneFontInfo.setAssetMode(AssetItem.ASSET_NONE);
        mCaptionFontList.add(0, noneFontInfo);
        mCaptionFontList.addAll(mServerCaptionFontList);

//        String jsonBundlePath = "font/info_caption.json";
//        ArrayList<ParseJsonFile.FxJsonFileInfo.JsonFileInfo> infoLists = ParseJsonFile.readBundleFxJsonFile(this, jsonBundlePath);
//
//
//        if (infoLists != null) {
//            for (ParseJsonFile.FxJsonFileInfo.JsonFileInfo fileInfo : infoLists) {
//                localFontInfo = new AssetItem();
//                item = new NvAsset();
//                StringBuilder coverPath = new StringBuilder("file:///android_asset/font/");
//                coverPath.append(fileInfo.getImageName());
//                item.coverUrl = coverPath.toString();
//                item.fxFileName = fileInfo.getFxFileName();
//                item.downloadStatus = NvAsset.DownloadStatusFinished;
//                item.bundledLocalDirPath = "assets:/font/" + fileInfo.getFxFileName();
//
//                localFontInfo.setAsset(item);
//                localFontInfo.setAssetMode(AssetItem.ASSET_BUILTIN);
//                mCaptionFontList.add(localFontInfo);
//            }
//        }
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
        if (mCurCaption != null) {
            mSelectedRichPos =
                    getTargetPosition(mRichWordList, mCurCaption.getModularCaptionRendererPackageId());
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
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index < 0) {
                    return;
                }
                if (checkPosition()) {
                    if (pos == 0) {
                        CaptionInfo captionInfo = mCaptionDataListClone.get(index);
                        //                        List<NvsCaptionSpan> textSpanList = mCurCaption.getTextSpanList();
                        //                        NvsNormalTextSpan nvsNormalTextSpan = new NvsNormalTextSpan(mSelectionStart, mSelectionEnd);
                        //                        setSpanList(nvsNormalTextSpan, 0f, textSpanList, captionInfo);
                        changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_RENDER_ID, captionInfo);
                    } else {

                        mCurCaption.resetTextColorState();

                        CaptionInfo captionInfo = mCaptionDataListClone.get(index);

                        changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_NORMAL_TEXT, captionInfo);
                        changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_OUTLINE_WIDTH, captionInfo);
                        changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_OUTLINE_COLOR, captionInfo);
                        changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_COLOR, captionInfo);

                        List<NvsCaptionSpan> textSpanList = mCurCaption.getTextSpanList();
                        NvsRendererIdSpan nvsRendererIdSpan =
                                new NvsRendererIdSpan(mSelectionStart, mSelectionEnd);
                        setSpanList(nvsRendererIdSpan, mRichWordList.get(pos).getAsset().uuid, textSpanList,
                                captionInfo);
                    }
                    return;
                }

                changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_RENDER_ID,
                        mCaptionDataListClone.get(index));
                applyAssemblyCaption(pos, NvAsset.ASSET_CAPTION_RICH_WORD,
                        mRichWordList.get(pos).getAsset());
            }
        });
        return mRichWordFragment;
    }

    private CaptionBubbleFragment initBubbleFragment() {
        mBubbleFragment = new CaptionBubbleFragment();
        mBubbleFragment.setAssetInfoList(mBubbleList);
        if (mCurCaption != null) {
            mSelectedBubblePos =
                    getTargetPosition(mBubbleList, mCurCaption.getModularCaptionContextPackageId());
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
        if (mCurCaption != null) {
            mSelectedAnimationPos =
                    getTargetPosition(mAnimationList, mCurCaption.getModularCaptionAnimationPackageId());
            mSelectedInAnimationPos =
                    getTargetPosition(mMarchInAniList, mCurCaption.getModularCaptionInAnimationPackageId());
            mSelectedOutAnimationPos =
                    getTargetPosition(mMarchOutAniList, mCurCaption.getModularCaptionOutAnimationPackageId());
        }
        mAnimationFragment.setCaptionStateListener(
                new CaptionAnimationFragment.OnCaptionStateListener() {
                    @Override
                    public void onFragmentLoadFinished() {
                        mAnimationFragment.setSelectedPos(mSelectedAnimationPos, mSelectedInAnimationPos,
                                mSelectedOutAnimationPos);
                        mAnimationFragment.checkSelectedTab();
                        mSeekBar = mAnimationFragment.getSeekBar();
                        checkInit();
                        mSeekBar.setOnRangeListener(new HorizontalSeekBar.onRangeListener() {
                            @Override
                            public void onRange(float left, float right) {
                                if (mCurCaption == null) {
                                    return;
                                }
                                int index = getCaptionIndex(mCurCaptionZVal);
                                CaptionInfo captionInfo = mCaptionDataListClone.get(index);
                                int leftValue =
                                        (int) (Float.parseFloat(String.format(getString(R.string.format_1f), left))
                                                * 1000);
                                int rightValue =
                                        (int) (Float.parseFloat(String.format(getString(R.string.format_1f), right))
                                                * 1000);
                                //组合动画与出入动画互斥(出入动画不互斥)。前者默认时长0.5s后者0.6s
                                //Combination animation and in-out animation are mutually exclusive (in-out animation is not mutually exclusive)
                                //,The former has a default duration of 0.5s and the latter 0.6s
                                if (!TextUtils.isEmpty(mCurCaption.getModularCaptionAnimationPackageId())) {
                                    if (leftValue <= 100) {
                                        leftValue = 100;
                                        mSeekBar.setLeftProgress(leftValue);
                                        //组合动画最小值可设置成100ms
                                        //The minimum value of the combined animation can be set to 100ms
                                    }
                                    mCurCaption.setModularCaptionAnimationPeroid(leftValue);
                                    if (captionInfo != null) {
                                        captionInfo.setCombinationAnimationDuration(leftValue);
                                    }
                                } else {
                                    if (!TextUtils.isEmpty(mCurCaption.getModularCaptionInAnimationPackageId())) {
                                        if (mSeekBar.getMaxProgress() - leftValue < IN_OUT_ANIMATION_DEFAULT_DURATION
                                                && TextUtils.isEmpty(
                                                mCurCaption.getModularCaptionOutAnimationPackageId())) {
                                            //如果设置入动画后，剩余的时间小于默认时间500毫秒（出入动画默认时长500ms，不论设置不设置出动画），且此时没有出动画，则把出动画时长设置成0
                                            //If the remaining time after setting the animation is less than the default time of 500 milliseconds
                                            // (the default time of the animation is 500ms, regardless of whether the animation is set or not),
                                            // and there is no animation at this time, set the animation duration to 0
                                            mCurCaption.setModularCaptionOutAnimationDuration(0);
                                        }
                                        if (captionInfo != null) {
                                            captionInfo.setMarchInAnimationDuration(leftValue);
                                        }
                                        mCurCaption.setModularCaptionInAnimationDuration(leftValue);
                                    }
                                    if (!TextUtils.isEmpty(mCurCaption.getModularCaptionOutAnimationPackageId())) {
                                        //如果设置出动画后，剩余的时间小于默认时间500毫秒（出入动画默认时长500ms，不论设置不设置入动画），且此时没有入动画，则把入动画时长设置成0
                                        //If after setting the animation, the remaining time is less than the default time of 500 milliseconds
                                        // (the default time of the in and out animation is 500ms, regardless of whether it is set or not),
                                        // and there is no animation at this time, then set the time of the in animation to 0
                                        if (mSeekBar.getMaxProgress() - right < IN_OUT_ANIMATION_DEFAULT_DURATION
                                                && TextUtils.isEmpty(mCurCaption.getModularCaptionInAnimationPackageId())) {
                                            mCurCaption.setModularCaptionInAnimationDuration(0);
                                        }
                                        if (captionInfo != null) {
                                            captionInfo.setMarchOutAnimationDuration(rightValue);
                                        }
                                        mCurCaption.setModularCaptionOutAnimationDuration(rightValue);
                                    }
                                }
                                if (mVideoFragment != null) {
                                    mVideoFragment.stopEngine();
                                }
                                m_handler.removeMessages(PLAY_VIDEO_FORM_START);
                                //                m_handler.sendEmptyMessageDelayed(PLAY_VIDEO_FORM_START, 500);
                                //Log.d("lhz", "leftValue=" + leftValue + "**rightValue=" + rightValue+"**maxValue="+mSeekBar.getMaxProgress());
                            }

                            @Override
                            public void onTouchUpLeft(boolean leftFlag) {
                                if (mCurCaption == null) return;
                                long playTimeStart = mCurCaption.getInPoint();
                                if (leftFlag) {
                                    //组合动画或者入动画
                                    //Combine animation or enter animation
                                    playTimeStart = mCurCaption.getInPoint();
                                } else {
                                    //出动画
                                    //Out animation
                                    if (!TextUtils.isEmpty(mCurCaption.getModularCaptionOutAnimationPackageId())) {
                                        playTimeStart = mCurCaption.getOutPoint()
                                                - mCurCaption.getModularCaptionOutAnimationDuration() * 1000;
                                    }
                                }
                                mVideoFragment.playVideo(playTimeStart, mCurCaption.getOutPoint());
                            }
                        });
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
        if (mCurCaption == null || asset == null) {
            return;
        }
        if (mCaptionDataListClone == null) {
            return;
        }
        isCaptionStyleItemClick = true;
        long startTime = mCurCaption.getInPoint();
        long endTime = mCurCaption.getOutPoint();
        mVideoFragment.setDrawRectVisible(View.GONE);
        int index = getCaptionIndex(mCurCaptionZVal);
        CaptionInfo captionInfo = mCaptionDataListClone.get(index);
        mSelectedType = type;
        //Log.d("lhz", "type=" + type + "**asset.uuid=" + asset.uuid + "**pos=" + pos);
        switch (type) {
            case NvAsset.ASSET_CAPTION_RICH_WORD:
                /*if (mSelectedRichPos == pos) {
                    mVideoFragment.playVideo(startTime, endTime);
                    return;
                }*/
                mSelectedRichPos = pos;
                mSelectedAnimationPos = 0;
                mCurCaption.applyModularCaptionRenderer(asset.uuid);
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
                mCurCaption.applyModularCaptionContext(asset.uuid);
                if (captionInfo != null) {
                    captionInfo.setBubbleUuid(asset.uuid);
                }
                break;
            case ASSET_CAPTION_ANIMATION:
                mSelectedAnimationPos = pos;
                mSelectedInAnimationPos = 0;
                mSelectedOutAnimationPos = 0;
                if (!TextUtils.isEmpty(mCurCaption.getModularCaptionOutAnimationPackageId())) {
                    mCurCaption.applyModularCaptionOutAnimation("");
                    //恢复默认值 Restore Defaults
                    mCurCaption.setModularCaptionOutAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
                }
                if (!TextUtils.isEmpty(mCurCaption.getModularCaptionInAnimationPackageId())) {
                    mCurCaption.applyModularCaptionInAnimation("");
                    mCurCaption.setModularCaptionInAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
                }
                if (mCurCaption.getModularCaptionAnimationPeroid() == 0) {
                    //如果切换的时候，设置的动画时长是0，则恢复默认，产品需求。
                    //If the animation duration is set to 0 when switching, the default and product requirements will be restored.
                    mCurCaption.setModularCaptionAnimationPeroid(ANIMATION_DEFAULT_DURATION);
                }
                mCurCaption.applyModularCaptionAnimation(asset.uuid);
                if (captionInfo != null) {
                    captionInfo.setCombinationAnimationUuid(asset.uuid);
                    captionInfo.setCombinationAnimationDuration(
                            mCurCaption.getModularCaptionAnimationPeroid());
                    captionInfo.setMarchOutAnimationUuid("");
                    captionInfo.setMarchInAnimationUuid("");
                }
                break;
            case NvAsset.ASSET_CAPTION_IN_ANIMATION:
                mSelectedInAnimationPos = pos;
                if (!TextUtils.isEmpty(mCurCaption.getModularCaptionAnimationPackageId())) {
                    mCurCaption.applyModularCaptionAnimation("");
                    mCurCaption.setModularCaptionAnimationPeroid(ANIMATION_DEFAULT_DURATION);
                }
                if (mCurCaption.getModularCaptionInAnimationDuration() == 0) {
                    //如果切换的时候，设置的动画时长是0，则恢复默认,产品需求。
                    //If the animation duration is set to 0 when switching, the default and product requirements will be restored.
                    mCurCaption.setModularCaptionInAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
                }
                mCurCaption.applyModularCaptionInAnimation(asset.uuid);
                if (captionInfo != null) {
                    captionInfo.setCombinationAnimationUuid("");
                    captionInfo.setCombinationAnimationDuration(0);
                    captionInfo.setMarchInAnimationUuid(asset.uuid);
                    captionInfo.setMarchInAnimationDuration(
                            mCurCaption.getModularCaptionInAnimationDuration());
                    if (TextUtils.isEmpty(asset.uuid)) {
                        captionInfo.setMarchInAnimationDuration(0);
                    }
                }
                break;
            case ASSET_CAPTION_OUT_ANIMATION:
                mSelectedOutAnimationPos = pos;
                if (!TextUtils.isEmpty(mCurCaption.getModularCaptionAnimationPackageId())) {
                    mCurCaption.applyModularCaptionAnimation("");
                    mCurCaption.setModularCaptionAnimationPeroid(ANIMATION_DEFAULT_DURATION);
                }
                if (mCurCaption.getModularCaptionOutAnimationDuration() == 0) {
                    //如果切换的时候，设置的动画时长是0，则恢复默认，产品需求。
                    //If the animation duration is set to 0 when switching, the default and product requirements will be restored.
                    mCurCaption.setModularCaptionOutAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
                }
                mCurCaption.applyModularCaptionOutAnimation(asset.uuid);
                //为了看效果，增加了50毫秒的播放时间
                //In order to see the effect, add 50 milliseconds of playback time
                int outDuration = mCurCaption.getModularCaptionOutAnimationDuration() + 50;
                startTime = endTime > outDuration * 1000 ? (endTime - outDuration * 1000) : startTime;

                if (captionInfo != null) {
                    captionInfo.setCombinationAnimationUuid("");
                    captionInfo.setCombinationAnimationDuration(0);
                    captionInfo.setMarchOutAnimationUuid(asset.uuid);
                    captionInfo.setMarchOutAnimationDuration(
                            mCurCaption.getModularCaptionOutAnimationDuration());
                    if (TextUtils.isEmpty(asset.uuid)) {
                        captionInfo.setMarchOutAnimationDuration(0);
                    }
                }
                break;
            default:
                break;
        }

        if (pos == 0) {
            //            mCurCaption.resetTextColorState();
            NvsColor textColor = mCurCaption.getTextColor();
            if (captionInfo != null) {
                captionInfo.setCaptionColor(nvsColorToHexString(textColor));
            }
        } else {
            mCurCaption.resetTextColorState();
        }
        mVideoFragment.playVideo(startTime, endTime);
        float captionSize = mCurCaption.getFontSize();
        float scaleX = mCurCaption.getScaleX();
        float scaleY = mCurCaption.getScaleY();
        PointF pointF = mCurCaption.getCaptionTranslation();
        float rotateAngle = mCurCaption.getRotationZ();
        if (captionInfo != null) {
            captionInfo.setTranslation(pointF);
            captionInfo.setCaptionSize(captionSize);
            captionInfo.setScaleFactorX(scaleX);
            captionInfo.setScaleFactorY(scaleY);
            captionInfo.setRotation(rotateAngle);
        }
    }

    private void displaySeekBar(boolean visible) {
        if (mSeekBar == null) {
            return;
        }
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
        if (mCurCaption == null) {
            return;
        }
        if (mSeekBar == null) {
            return;
        }
        if (visible) {
            if (mSeekBar.getVisibility() != View.VISIBLE) {
                mSeekBar.setVisibility(View.VISIBLE);
            }
        } else {
            if (TextUtils.isEmpty(mCurCaption.getModularCaptionInAnimationPackageId()) &&
                    TextUtils.isEmpty(mCurCaption.getModularCaptionOutAnimationPackageId())) {
                mSeekBar.setVisibility(View.INVISIBLE);
                return;
            }
        }

        if (type == NvAsset.ASSET_CAPTION_IN_ANIMATION) {
            if (mCurCaption != null) {
                if (TextUtils.isEmpty(mCurCaption.getModularCaptionInAnimationPackageId())) {
                    mSeekBar.setLeftMoveIcon(0);
                } else {
                    if (hasCombineAnimation) {
                        mSeekBar.reset();
                        mSeekBar.setMoveIconSize(20, 35);
                    }
                    if (TextUtils.isEmpty(mCurCaption.getModularCaptionOutAnimationPackageId())) {
                        mSeekBar.setRightMoveIcon(0);
                    }
                    int duration = mCurCaption.getModularCaptionInAnimationDuration();
                    //Log.d("lhz", "duration=" + duration + "**type=" + type);
                    mSeekBar.setMoveIconLowPadding(10);
                    mSeekBar.setLeftMoveIcon(R.mipmap.bar_left);
                    mSeekBar.setLeftProgress(duration);
                    hasCombineAnimation = false;
                }
            }
        } else if (type == NvAsset.ASSET_CAPTION_OUT_ANIMATION) {
            if (TextUtils.isEmpty(mCurCaption.getModularCaptionOutAnimationPackageId())) {
                mSeekBar.setRightMoveIcon(0);
            } else {
                if (hasCombineAnimation) {
                    mSeekBar.reset();
                    mSeekBar.setMoveIconSize(20, 35);
                }
                if (TextUtils.isEmpty(mCurCaption.getModularCaptionInAnimationPackageId())) {
                    mSeekBar.setLeftMoveIcon(0);
                }
                int duration = mCurCaption.getModularCaptionOutAnimationDuration();
                mSeekBar.setMoveIconLowPadding(10);
                mSeekBar.setRightMoveIcon(R.mipmap.bar_right);
                mSeekBar.setRightProgress(duration);
                hasCombineAnimation = false;
                //  Log.d("lhz", "duration=" + duration + "**type=" + type);
            }
        } else {
            if (TextUtils.isEmpty(mCurCaption.getModularCaptionAnimationPackageId())) {
                mSeekBar.reset();
                mSeekBar.setVisibility(View.INVISIBLE);
                return;
            }
            int duration = mCurCaption.getModularCaptionAnimationPeroid();
            mSeekBar.reset();
            mSeekBar.setMoveIconSize(20, 20);
            mSeekBar.setLeftMoveIcon(R.mipmap.round_white);
            mSeekBar.setLeftProgress(duration);
            hasCombineAnimation = true;
            //Log.d("lhz", "duration=" + duration + "**type=" + type);
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
                mEtCaptionInput.clearFocus();
                if (pos < 0 || pos >= mTotalCaptionStyleList.size()) {
                    return;
                }
                if (mCaptionDataListClone == null) {
                    return;
                }
                if (mCurCaption == null) {
                    return;
                }

                isCaptionStyleItemClick = true;
                long startTime = mCurCaption.getInPoint();
                long endTime = mCurCaption.getOutPoint();
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
                int index = getCaptionIndex(mCurCaptionZVal);
                CaptionInfo captionInfo = null;
                if (index >= 0) {
                    captionInfo = mCaptionDataListClone.get(index);
                }
                /*
                 * 应用字幕样式
                 * Apply caption style
                 * */
                mCurCaption.applyCaptionStyle(asset.uuid);
                //                }

                //                long duration = mStreamingContext.getTimelineCurrentPosition(mTimeline) - mCurAddCaption.getInPoint();
                //                mCurAddCaption.setCurrentKeyFrameTime(duration);

                float captionSize = mCurCaption.getFontSize();
                float scaleX = mCurCaption.getScaleX();
                float scaleY = mCurCaption.getScaleY();
                PointF pointF = mCurCaption.getCaptionTranslation();
                float rotateAngle = mCurCaption.getRotationZ();

                if (captionInfo != null) {
                    changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_RENDER_ID,
                            mCaptionDataListClone.get(index));
                    captionInfo.setCaptionStyleUuid(asset.uuid);
                    captionInfo.setTranslation(pointF);
                    captionInfo.setCaptionSize(captionSize);
                    captionInfo.setScaleFactorX(scaleX);
                    captionInfo.setScaleFactorY(scaleY);
                    captionInfo.setRotation(rotateAngle);
                    //添加样式会影响加粗 Adding style will affect bold
                    captionInfo.setBold(mCurCaption.getBold());
                    if (captionInfo.getUsedColorFlag() == CaptionInfo.ATTRIBUTE_USED_FLAG) {
                        NvsColor textColor = ColorUtil.colorStringtoNvsColor(captionInfo.getCaptionColor());
                        if (textColor != null) {
                            textColor.a = captionInfo.getCaptionColorAlpha() / 100.0f;
                            mCurCaption.setTextColor(textColor);
                        }
                    }
                }
                if (index >= 0) {
                    reloadKeyFrame(index);
                }
                mVideoFragment.playVideo(startTime, endTime);
            }

            @Override
            public void onIsApplyToAll(boolean isApplyToAll) {
                bIsStyleUuidApplyToAll = isApplyToAll;
                bIsStyleUuidApplyToAllChange = true;
                applyToAllCaption();
            }
        });
        return captionStyleFragment;
    }

    private void reloadKeyFrame(int index) {
        if (mCaptionDataListClone == null) {
            return;
        }
        Map<Long, KeyFrameInfo> keyFrameInfoMap = mCaptionDataListClone.get(index).getKeyFrameInfo();
        Set<Long> keySet = keyFrameInfoMap.keySet();
        for (long currentTime : keySet) {
            KeyFrameInfo keyFrameInfo = keyFrameInfoMap.get(currentTime);
            long duration = currentTime - mCurCaption.getInPoint();
            mCurCaption.removeKeyframeAtTime(TRANS_X, duration);
            mCurCaption.removeKeyframeAtTime(TRANS_Y, duration);
            mCurCaption.removeKeyframeAtTime(SCALE_X, duration);
            mCurCaption.removeKeyframeAtTime(SCALE_Y, duration);
            mCurCaption.removeKeyframeAtTime(ROTATION_Z, duration);

            mCurCaption.setCurrentKeyFrameTime(duration);
            mCurCaption.setScaleX(keyFrameInfo.getScaleX());
            mCurCaption.setScaleY(keyFrameInfo.getScaleY());
            mCurCaption.setCaptionTranslation(keyFrameInfo.getTranslation());
            mCurCaption.setRotationZ(keyFrameInfo.getRotationZ());
        }
        //set caption bezier adjust function
        for (long currentTime : keySet) {
            KeyFrameInfo keyFrameInfo = keyFrameInfoMap.get(currentTime);
            double forwardControlPointX = keyFrameInfo.getForwardControlPointX();
            double backwardControlPointX = keyFrameInfo.getBackwardControlPointX();
            if (forwardControlPointX == -1 && backwardControlPointX == -1) {
                continue;
            }
            long duration = currentTime - mCurCaption.getInPoint();
            mCurCaption.setCurrentKeyFrameTime(duration);
            NvsControlPointPair pairX = mCurCaption.getControlPoint(TRANS_X);
            NvsControlPointPair pairY = mCurCaption.getControlPoint(TRANS_Y);
            if (pairX == null || pairY == null) {
                continue;
            }
            if (backwardControlPointX != -1) {
                pairX.backwardControlPoint.x = backwardControlPointX - mCurCaption.getInPoint();
                pairX.backwardControlPoint.y = keyFrameInfo.getBackwardControlPointYForTransX();
                pairY.backwardControlPoint.x = backwardControlPointX - mCurCaption.getInPoint();
                pairY.backwardControlPoint.y = keyFrameInfo.getBackwardControlPointYForTransY();
            }
            if (forwardControlPointX != -1) {
                pairX.forwardControlPoint.x = forwardControlPointX - mCurCaption.getInPoint();
                pairX.forwardControlPoint.y = keyFrameInfo.getForwardControlPointYForTransX();
                pairY.forwardControlPoint.x = forwardControlPointX - mCurCaption.getInPoint();
                pairY.forwardControlPoint.y = keyFrameInfo.getForwardControlPointYForTransY();
            }
            mCurCaption.setControlPoint(TRANS_X, pairX);
            mCurCaption.setControlPoint(TRANS_Y, pairY);
        }
    }

    private CaptionColorFragment initCaptionColorFragment() {
        CaptionColorFragment captionColorFragment = new CaptionColorFragment();
        captionColorFragment.setCaptionColorInfolist(mCaptionColorList);
        captionColorFragment.setCaptionColorListener(new CaptionColorFragment.OnCaptionColorListener() {
            @Override
            public void onFragmentLoadFinished() {
                if (mCaptionDataListClone == null) {
                    return;
                }
                mCaptionColorFragment.applyToAllCaption(bIsCaptionColorApplyToAll);
                mSelectedColorPos = getCaptionColorSelectedIndex();
                if (mSelectedColorPos >= 0) {
                    mCaptionColorList.get(mSelectedColorPos).mSelected = true;
                    mCaptionColorFragment.setCaptionColorInfolist(mCaptionColorList);
                    mCaptionColorFragment.notifyDataSetChanged();
                }
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0 && mCaptionDataListClone != null) {
                    mCaptionColorOpacityValue = mCaptionDataListClone.get(index).getCaptionColorAlpha();
                    mCaptionColorFragment.updateCaptionOpacityValue(mCaptionColorOpacityValue);
                }
            }

            @Override
            public void onCaptionColor(int pos) {
                if (pos < 0 || pos > mCaptionColorList.size()) {
                    return;
                }
                if (mCurCaption == null) {
                    return;
                }
                if (mSelectedColorPos == pos) {
                    return;
                }
                if (mSelectedColorPos >= 0) {
                    mCaptionColorList.get(mSelectedColorPos).mSelected = false;
                }
                if (mCaptionDataListClone == null) {
                    return;
                }

                mCaptionColorList.get(pos).mSelected = true;
                mCaptionColorFragment.notifyDataSetChanged();
                mSelectedColorPos = pos;
                mCaptionColorOpacityValue = 100;
                mCaptionColorFragment.updateCaptionOpacityValue(mCaptionColorOpacityValue);
                NvsColor color = ColorUtil.colorStringtoNvsColor(mCaptionColorList.get(pos).mColorValue);
                int index = getCaptionIndex(mCurCaptionZVal);
                CaptionInfo captionInfo = null;
                if (index >= 0) {
                    captionInfo = mCaptionDataListClone.get(index);
                }

                if (checkPosition()) {
                    /*部分字幕编辑 设置字体颜色*/
                    //                    List<NvsCaptionSpan> textSpanList = mCurCaption.getTextSpanList();
                    //                    NvsColorSpan nvsColorSpan = null;
                    //                    for (NvsCaptionSpan span : textSpanList) {
                    //                        if (span instanceof NvsColorSpan) {
                    //                            nvsColorSpan = (NvsColorSpan) span;
                    //                            break;
                    //                        }
                    //                    }

                    List<NvsCaptionSpan> textSpanList = mCurCaption.getTextSpanList();
                    NvsColorSpan nvsColorSpan = new NvsColorSpan(mSelectionStart, mSelectionEnd);
                    setSpanList(nvsColorSpan, color, textSpanList, captionInfo);
                } else {

                    changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_COLOR,
                            mCaptionDataListClone.get(index));
                    /*
                     * 设置字体颜色
                     * Set font color
                     * */
                    mCurCaption.setTextColor(color);
                    if (captionInfo != null) {
                        captionInfo.setUsedColorFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        captionInfo.setCaptionColor(mCaptionColorList.get(pos).mColorValue);
                    }
                }
                updateCaption();
            }

            @Override
            public void onCaptionOpacity(int progress) {
                if (mCurCaption == null) {
                    return;
                }
                if (mCaptionDataListClone == null) {
                    return;
                }
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index < 0) {
                    return;
                }
                if (checkPosition()) {
                    List<NvsCaptionSpan> textSpanList = mCurCaption.getTextSpanList();
                    //                    NvsOpacitySpan nvsOpacitySpan = new NvsOpacitySpan(mSelectionStart, mSelectionEnd);
                    NvsBodyOpacitySpan nvsOpacitySpan =
                            new NvsBodyOpacitySpan(mSelectionStart, mSelectionEnd);
                    setSpanList(nvsOpacitySpan, progress / 100.0f, textSpanList,
                            mCaptionDataListClone.get(index));
                    return;
                }
                changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_OPACITY,
                        mCaptionDataListClone.get(index));
                /*
                 * 设置字体的不透明度
                 * Set the opacity of the font
                 * */
                NvsColor curColor = mCurCaption.getTextColor();
                curColor.a = progress / 100.0f;
                String strColor = nvsColorToHexString(curColor);
                mCurCaption.setTextColor(curColor);
                mCaptionColorOpacityValue = progress;

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
                bIsCaptionColorApplyToAllCheck = true;
                applyToAllCaption();
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
                        if (mCaptionDataListClone == null) {
                            return;
                        }
                        mCaptionOutlineFragment.applyToAllCaption(bIsOutlineApplyToAll);
                        mSelectedOutlinePos = getOutlineColorSelectedIndex();
                        mCaptionOutlineColorList.get(mSelectedOutlinePos).mSelected = true;
                        mCaptionOutlineFragment.setCaptionOutlineInfolist(mCaptionOutlineColorList);
                        mCaptionOutlineFragment.notifyDataSetChanged(mSelectedOutlinePos);
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            boolean isDrawOutline = mCaptionDataListClone.get(index).isHasOutline();
                            if (isDrawOutline) {
                                mCaptionOutlineWidthValue = mCaptionDataListClone.get(index).getOutlineWidth();
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
                        if (mCurCaption == null) {
                            return;
                        }
                        if (mSelectedOutlinePos == pos) {
                            return;
                        }
                        if (mCaptionDataListClone == null) {
                            return;
                        }
                        mCaptionOutlineColorList.get(mSelectedOutlinePos).mSelected = false;
                        mCaptionOutlineColorList.get(pos).mSelected = true;
                        mCaptionOutlineFragment.notifyDataSetChanged(pos);
                        mSelectedOutlinePos = pos;

                        mCaptionOutlineOpacityValue = 100;
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (pos == 0) {
                            if (checkPosition()) {
                                if (index >= 0) {
                                    removeOutLineSpans(index);
                                    updateCaption();
                                    return;
                                }
                            }
                            removeOutLineSpans(index);
                            mCurCaption.setDrawOutline(false);
                            mCaptionOutlineWidthValue = 0;
                            if (index >= 0) {
                                mCaptionDataListClone.get(index)
                                        .setUsedOutlineFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                                mCaptionDataListClone.get(index).setHasOutline(false);
                                mCaptionDataListClone.get(index).setOutlineColor("");
                            }
                        } else {
                            /*
                             * 设置描边颜色
                             * Set outline color
                             * */
                            NvsColor color =
                                    ColorUtil.colorStringtoNvsColor(mCaptionOutlineColorList.get(pos).mColorValue);
                            mCaptionOutlineWidthValue = 8f;
                            if (checkPosition()) {
                                if (index >= 0) {

                                    /*
                                     * 设置字幕描边标识
                                     * Set caption stroke flag
                                     * */
                                    //                            mCurCaption.setDrawOutline(true);

                                    List<NvsCaptionSpan> textSpanList = mCurCaption.getTextSpanList();
                                    NvsNormalTextSpan nvsNormalTextSpan =
                                            new NvsNormalTextSpan(mSelectionStart, mSelectionEnd);
                                    setSpanList(nvsNormalTextSpan, mCaptionOutlineWidthValue, textSpanList,
                                            mCaptionDataListClone.get(index));

                                    NvsOutlineWidthSpan nvsOutlineWidthSpan =
                                            new NvsOutlineWidthSpan(mSelectionStart, mSelectionEnd);
                                    setSpanList(nvsOutlineWidthSpan, mCaptionOutlineWidthValue, textSpanList,
                                            mCaptionDataListClone.get(index));

                                    NvsOutlineColorSpan nvsOutlineColorSpan =
                                            new NvsOutlineColorSpan(mSelectionStart, mSelectionEnd);
                                    setSpanList(nvsOutlineColorSpan, color, textSpanList,
                                            mCaptionDataListClone.get(index));
                                    mCaptionOutlineFragment.updateCaptionOutlineWidthValue(mCaptionOutlineWidthValue);
                                    mCaptionOutlineFragment.updateCaptionOutlineOpacityValue(
                                            mCaptionOutlineOpacityValue);
                                    return;
                                }
                            }

                            removeOutLineSpans(index);
                            /*
                             * 设置字幕描边标识
                             * Set caption stroke flag
                             * */
                            mCurCaption.setDrawOutline(true);

                            mCurCaption.setOutlineColor(color);
                            /*
                             * 字幕描边宽度
                             * Caption stroke width
                             * */
                            mCurCaption.setOutlineWidth(mCaptionOutlineWidthValue);
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
                        if (mCurCaption == null) {
                            return;
                        }
                        if (mSelectedOutlinePos == 0) {
                            return;
                        }
                        if (mCaptionDataListClone == null) {
                            return;
                        }
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index < 0) {
                            return;
                        }
                        if (checkPosition()) {
                            List<NvsCaptionSpan> textSpanList = mCurCaption.getTextSpanList();
                            NvsOutlineWidthSpan nvsOutlineWidthSpan =
                                    new NvsOutlineWidthSpan(mSelectionStart, mSelectionEnd);
                            NvsNormalTextSpan nvsNormalTextSpan =
                                    new NvsNormalTextSpan(mSelectionStart, mSelectionEnd);
                            setSpanList(nvsNormalTextSpan, width * 1.0f, textSpanList,
                                    mCaptionDataListClone.get(index));
                            setSpanList(nvsOutlineWidthSpan, width * 1.0f, textSpanList,
                                    mCaptionDataListClone.get(index));
                            return;
                        }
                        changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_OUTLINE_WIDTH,
                                mCaptionDataListClone.get(index));
                        changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_NORMAL_TEXT,
                                mCaptionDataListClone.get(index));

                        mCurCaption.setDrawOutline(true);
                        /*
                         * 字幕描边宽度
                         * Caption stroke width
                         * */
                        mCurCaption.setOutlineWidth(width * 1.0f);
                        mCaptionOutlineWidthValue = width * 1.0f;
                        if (index >= 0) {
                            mCaptionDataListClone.get(index).setOutlineWidth(mCaptionOutlineWidthValue);
                        }

                        updateCaption();
                    }

                    @Override
                    public void onCaptionOutlineOpacity(int opacity) {
                        if (mCurCaption == null) {
                            return;
                        }
                        if (mSelectedOutlinePos == 0) {
                            return;
                        }
                        if (mCaptionDataListClone == null) {
                            return;
                        }

                        int index = getCaptionIndex(mCurCaptionZVal);
                        CaptionInfo captionInfo = mCaptionDataListClone.get(index);
                        if (captionInfo == null) {
                            return;
                        }

                        if (checkPosition()) {
                            List<NvsCaptionSpan> textSpanList = mCurCaption.getTextSpanList();
                            NvsOutlineOpacitySpan nvsOutlineOpacitySpan =
                                    new NvsOutlineOpacitySpan(mSelectionStart, mSelectionEnd);
                            setSpanList(nvsOutlineOpacitySpan, opacity / 100.0f, textSpanList,
                                    mCaptionDataListClone.get(index));
                        } else {
                            /*
                             * 设置字幕描边的不透明度
                             * Set the opacity of the caption stroke
                             * */
                            NvsColor curColor = mCurCaption.getOutlineColor();
                            curColor.a = opacity / 100.0f;
                            mCurCaption.setOutlineColor(curColor);
                            mCaptionOutlineOpacityValue = opacity;
                            if (index >= 0) {
                                captionInfo.setOutlineColorAlpha(opacity);
                            }
                        }

                        updateCaption();
                    }

                    @Override
                    public void onIsApplyToAll(boolean isApplyToAll) {
                        bIsOutlineApplyToAll = isApplyToAll;
                        bIsOutlineApplyToAllCheck = true;
                        applyToAllCaption();
                    }
                });
        return captionOutlineFragment;
    }

    private void removeOutLineSpans(int index) {
        changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_OUTLINE_COLOR,
                mCaptionDataListClone.get(index));
        changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_NORMAL_TEXT,
                mCaptionDataListClone.get(index));
        changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_OUTLINE_WIDTH,
                mCaptionDataListClone.get(index));
    }

    private CaptionBackgroundFragment initCaptionBackgroundFragment() {
        CaptionBackgroundFragment captionBackgroundFragment = new CaptionBackgroundFragment();
        captionBackgroundFragment.setCaptionBackgroundInfolist(mCaptionBackgroundList);
        captionBackgroundFragment.setCaptionBackgroundListener(
                new CaptionBackgroundFragment.OnCaptionBackgroundListener() {
                    @Override
                    public void onFragmentLoadFinished() {
                        if (mCaptionDataListClone == null) {
                            return;
                        }
                        if (mCurCaption != null) {
                            mCaptionBackgroundFragment.applyToAllCaption(bIsCaptionBackgroundApplyToAll);
                            int index = getCaptionIndex(mCurCaptionZVal);
                            if (index >= 0) {
                                mCaptionBackgroundOpacityValue =
                                        mCaptionDataListClone.get(index).getCaptionBackgroundAlpha();
                                mCaptionBackgroundFragment.updateCaptionOpacityValue(
                                        mCaptionBackgroundOpacityValue);
                            }
                            mSelectedBackgroundPos = getBackgroundSelectedIndex();
                            if (mSelectedBackgroundPos >= 0) {
                                mCaptionBackgroundList.get(mSelectedBackgroundPos).mSelected = true;
                                NvsColor curColor = mCurCaption.getBackgroundColor();
                                String strColor = nvsColorToHexString(curColor);
                                mCaptionBackgroundList.get(mSelectedBackgroundPos).mColorValue = strColor;
                                mCaptionBackgroundFragment.setCaptionBackgroundInfolist(mCaptionBackgroundList);
                                mCaptionBackgroundFragment.notifyDataSetChanged(mSelectedBackgroundPos);
                            }
                            //设置背景圆角的最大值和当前的值
                            //Set the maximum and current value of the background rounded corners
                            RectF rectF = mCurCaption.getTextBoundingRect();
                            float height = Math.abs(rectF.top - rectF.bottom);
                            float width = Math.abs(rectF.right - rectF.left);
                            float maxRadius = height >= width ? width / 2 : height / 2;
                            //设置圆角的值
                            //Set the value of the rounded corners
                            mCaptionBackgroundFragment.initCaptionMaxCorner((int) (maxRadius));
                            if (mCurCaption != null) {
                                mCaptionBackgroundCornerValue = mCurCaption.getBackgroundRadius();
                                mCaptionBackgroundFragment.updateCaptionCornerValue(
                                        (int) mCaptionBackgroundCornerValue);
                            }

                            if (mCurCaption != null) {
                                mCaptionBackgroundPeddingValue = mCurCaption.getBoundaryPaddingRatio();
                                mCaptionBackgroundFragment.updateCaptionPaddingValue(
                                        Math.round(mCaptionBackgroundPeddingValue * 100));
                            }
                        }
                    }

                    @Override
                    public void onCaptionColor(int pos) {
                        if (pos < 0 || pos > mCaptionBackgroundList.size()) {
                            return;
                        }
                        if (mCurCaption == null) {
                            return;
                        }
                        if (mSelectedBackgroundPos == pos) {
                            return;
                        }
                        if (mCaptionDataListClone == null) {
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
                            mCurCaption.setBackgroundColor(color);
                            if (index >= 0) {
                                mCaptionDataListClone.get(index)
                                        .setUsedBackgroundFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                                mCaptionDataListClone.get(index).setCaptionBackground(noColor);
                            }
                        } else {
                            //背景色透明度 background Opacity Value
                            mCaptionBackgroundOpacityValue = 100;
                            /*
                             * 设置背景色
                             * Set font color
                             * */
                            NvsColor color =
                                    ColorUtil.colorStringtoNvsColor(mCaptionBackgroundList.get(pos).mColorValue);
                            mCurCaption.setBackgroundColor(color);
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
                        mCaptionBackgroundCornerValue = mCurCaption.getBackgroundRadius();
                        mCaptionBackgroundFragment.updateCaptionCornerValue(
                                (int) mCaptionBackgroundCornerValue);
                        updateCaption();
                    }

                    @Override
                    public void onCaptionOpacity(int progress) {
                        if (mCurCaption == null) {
                            return;
                        }
                        if (mCaptionDataListClone == null) {
                            return;
                        }
                        /*
                         * 设置背景的不透明度
                         * Set the opacity of the font
                         * */
                        NvsColor curColor = mCurCaption.getBackgroundColor();
                        curColor.a = progress / 100.0f;
                        String strColor = nvsColorToHexString(curColor);
                        int index = getCaptionIndex(mCurCaptionZVal);
                        CaptionInfo captionInfo = null;
                        if (index >= 0) {
                            captionInfo = mCaptionDataListClone.get(index);
                        }
                        mCurCaption.setBackgroundColor(curColor);
                        mCaptionBackgroundOpacityValue = progress;
                        if (captionInfo != null) {
                            captionInfo.setUsedBackgroundFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            captionInfo.setCaptionBackground(strColor);
                            captionInfo.setCaptionBackgroundAlpha(progress);
                        }

                        updateCaption();
                    }

                    @Override
                    public void onCaptionCorner(int progress) {
                        if (mCurCaption == null) {
                            return;
                        }
                        if (mCaptionDataListClone == null) {
                            return;
                        }
                        /*
                         * 设置背景圆角 Set background rounded corners
                         * */
                        //设置背景圆角的最大值和当前的值
                        //Set the maximum and current value of the background rounded corners
                        RectF rectF = mCurCaption.getTextBoundingRect();
                        float height = Math.abs(rectF.top - rectF.bottom);
                        float width = Math.abs(rectF.right - rectF.left);
                        float maxRadius = height >= width ? width / 2 : height / 2;
                        //设置圆角的最大值 Set the maximum value of rounded corners
                        //mCaptionBackgroundFragment.initCaptionMaxCorner((int) (maxRadius));
                        mCaptionBackgroundCornerValue = progress;
                        mCurCaption.setBackgroundRadius(mCaptionBackgroundCornerValue);
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
                        if (mCurCaption == null) {
                            return;
                        }
                        if (mCaptionDataListClone == null) {
                            return;
                        }
                        //设置边距的最大值 Set the maximum value of rounded corners
                        //mCaptionBackgroundFragment.initCaptionMaxPadding((int) (maxRadius));
                        mCaptionBackgroundPeddingValue = progress / (float) 100;
                        mCurCaption.setBoundaryPaddingRatio(mCaptionBackgroundPeddingValue);
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
                        bIsCaptionBackgroundApplyToAllCheck = true;
                        applyToAllCaption();
                    }
                });
        return captionBackgroundFragment;
    }

    private CaptionFontFragment initCaptionFontFragment() {
        CaptionFontFragment captionFontFragment = new CaptionFontFragment();
        captionFontFragment.setCaptionFontListener(new CaptionFontFragment.OnCaptionFontListener() {
            @Override
            public void onFragmentLoadFinished() {
                if (mCaptionDataListClone == null) {
                    return;
                }
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
                if (mCurCaption == null) {
                    return;
                }
                mSelectedFontPos = pos;
                NvAsset asset = mCaptionFontList.get(pos).getAsset();
                if (asset == null) {
                    return;
                }
                //                if (pos != 0&&TextUtils.isEmpty(asset.localDirPath)){
                //                    onFontDownload(pos);
                //                    return;
                //                }
                int index = getCaptionIndex(mCurCaptionZVal);
                CaptionInfo captionInfo = null;
                if (index >= 0) {
                    captionInfo = mCaptionDataListClone.get(index);
                }
                String text = mCurCaption.getText();
                List<NvsCaptionSpan> textSpanList = mCurCaption.getTextSpanList();
                NvsFontFamilySpan nvsFontFamilySpan = new NvsFontFamilySpan(mSelectionStart, mSelectionEnd);

                if (!TextUtils.isEmpty(asset.localDirPath)) {
                    if (checkPosition()) {
                        String fontFamily = mStreamingContext.registerFontByFilePath(asset.localDirPath);
                        //                        setSpanList(NvsCaptionSpan.SPAN_TYPE_FONT_FAMILY, fontFamily, captionInfo);
                        setSpanList(nvsFontFamilySpan, fontFamily, textSpanList, captionInfo);
                    } else {
                        changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_FONT_FAMILY,
                                mCaptionDataListClone.get(index));
                        applyCaptionFont(asset.localDirPath);
                    }
                } else if (!TextUtils.isEmpty(asset.bundledLocalDirPath)) {
                    if (checkPosition()) {
                        String fontFamily = mStreamingContext.registerFontByFilePath(asset.bundledLocalDirPath);
                        //                        setSpanList(NvsCaptionSpan.SPAN_TYPE_FONT_FAMILY, fontFamily, captionInfo);
                        setSpanList(nvsFontFamilySpan, fontFamily, textSpanList, captionInfo);
                    } else {
                        changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_FONT_FAMILY,
                                mCaptionDataListClone.get(index));
                        applyCaptionFont(asset.bundledLocalDirPath);
                    }
                } else if (pos == 0) {
                    if (checkPosition()) {
                        changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_FONT_FAMILY,
                                mCaptionDataListClone.get(index));
                    } else {
                        applyCaptionFont("");
                    }
                }
            }

            @Override
            public void onBold(boolean mIsBold) {
                if (mCurCaption == null) {
                    return;
                }
                if (mCaptionDataListClone == null) {
                    return;
                }
                //                boolean isBold = mCurAddCaption.getBold();
                //                isBold = !isBold;
                /*
                 * 字幕加粗
                 * Caption bold
                 * */
                mCurCaption.setBold(mIsBold);
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedIsBoldFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setBold(mIsBold);
                }
                updateCaption();
            }

            @Override
            public void onItalic(boolean mIsItalic) {
                if (mCurCaption == null) {
                    return;
                }
                if (mCaptionDataListClone == null) {
                    return;
                }
                boolean isItalic = mCurCaption.getItalic();
                isItalic = !isItalic;
                int index = getCaptionIndex(mCurCaptionZVal);
                CaptionInfo captionInfo = null;
                if (index >= 0) {
                    captionInfo = mCaptionDataListClone.get(index);
                }
                if (checkPosition()) {
                    List<NvsCaptionSpan> textSpanList = mCurCaption.getTextSpanList();
                    NvsItalicSpan nvsItalicSpan = new NvsItalicSpan(mSelectionStart, mSelectionEnd);
                    setSpanList(nvsItalicSpan, mIsItalic, textSpanList, captionInfo);
                } else {
                    changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_ITALIC,
                            mCaptionDataListClone.get(index));
                    /*
                     * 字幕斜体
                     * Caption italics
                     * */
                    mCurCaption.setItalic(isItalic);
                    if (captionInfo != null) {
                        captionInfo.setUsedIsItalicFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        captionInfo.setItalic(isItalic);
                    }
                }
                updateCaption();
            }

            @Override
            public void onShadow() {
                if (mCurCaption == null) {
                    return;
                }
                if (mCaptionDataListClone == null) {
                    return;
                }
                boolean isShadow = mCurCaption.getDrawShadow();
                isShadow = !isShadow;
                if (isShadow) {
                    //                    captionFontFragment.showSeekContainerVisible(View.VISIBLE);
                    PointF point = new PointF(7, -7);
                    NvsColor shadowColor = new NvsColor(0, 0, 0, 1.0f);
                    /*
                     * 设置字幕阴影偏移量
                     * Set the caption shadow offset
                     * */
                    mCurCaption.setShadowOffset(point);
                    /*
                     * 设置字幕阴影颜色
                     * Set the caption shadow color
                     * */
                    mCurCaption.setShadowColor(shadowColor);
                } else {
                    //                    captionFontFragment.showSeekContainerVisible(View.GONE);
                }
                mCurCaption.setDrawShadow(isShadow);

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
                if (mCurCaption == null) {
                    return;
                }
                if (mCaptionDataListClone == null) {
                    return;
                }
                boolean isUnderline = mCurCaption.getUnderline();
                isUnderline = !isUnderline;
                CaptionInfo captionInfo = null;
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    captionInfo = mCaptionDataListClone.get(index);
                }
                if (checkPosition()) {
                    List<NvsCaptionSpan> textSpanList = mCurCaption.getTextSpanList();
                    NvsUnderlineSpan nvsUnderlineSpan = new NvsUnderlineSpan(mSelectionStart, mSelectionEnd);
                    setSpanList(nvsUnderlineSpan, underline, textSpanList, captionInfo);
                } else {
                    changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_UNDERLINE,
                            mCaptionDataListClone.get(index));
                    mCurCaption.setUnderline(isUnderline);

                    mCaptionDataListClone.get(index).setUsedUnderlineFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setUnderline(isUnderline);
                }

                updateCaption();
            }

            @Override
            public void onIsApplyToAll(boolean isApplyToAll) {
                bIsFontApplyToAll = isApplyToAll;
                bIsFontApplyToAllCheck = true;
                applyToAllCaption();
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
                if (mCurCaption == null) {
                    return;
                }
                if (mCaptionDataListClone == null) {
                    return;
                }
                CaptionInfo captionInfo = null;
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    captionInfo = mCaptionDataListClone.get(index);
                }
                if (captionInfo == null) {
                    return;
                }

                if (checkPosition()) {
                    List<NvsCaptionSpan> textSpanList = mCurCaption.getTextSpanList();
                    NvsShadowOpacitySpan nvsShadowOpacitySpan =
                            new NvsShadowOpacitySpan(mSelectionStart, mSelectionEnd);
                    setSpanList(nvsShadowOpacitySpan, progress / 100f, textSpanList, captionInfo);
                } else {
                    changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_SHADOW_OPACITY,
                            mCaptionDataListClone.get(index));
                    NvsColor shadowColor = mCurCaption.getShadowColor();
                    shadowColor.a = progress / 100f;
                    mCurCaption.setShadowColor(shadowColor);
                    captionInfo.setShadowOpacity(progress / 100f);
                }

                updateCaption();
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
                if (mCurCaption == null) {
                    return;
                }
                mCurCaption.setFontSize(size);
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
                        if (mCurCaption == null) {
                            selectCaption();
                        }

                        if (mCurCaption != null) {
                            //设置字间距 Set word spacing
                            float letterSpaceing = mCurCaption.getLetterSpacing();
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
                            float lineSpacing = mCurCaption.getLineSpacing();
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
                        if (mCurCaption == null) {
                            return;
                        }
                        if (mCaptionDataListClone == null) {
                            return;
                        }
                        if (spacingMode == CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER) {
                            float letterSpace = getLetterSpacing(CAPTION_PERCENT_SMALL_SPACING);
                            mCurCaption.setLetterSpacingType(NvsCaption.LETTER_SPACING_TYPE_ABSOLUTE);
                            mCurCaption.setLetterSpacing(letterSpace);

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
                            mCurCaption.setLineSpacing(CAPTION_SMALL_LINE_SPACING);
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
                        if (mCurCaption == null) {
                            return;
                        }
                        if (mCaptionDataListClone == null) {
                            return;
                        }
                        if (spacingMode == CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER) {
                            float letterSpace = getLetterSpacing(CAPTION_PERCENT_STANDARD_SPACING);
                            mCurCaption.setLetterSpacingType(NvsCaption.LETTER_SPACING_TYPE_ABSOLUTE);
                            mCurCaption.setLetterSpacing(letterSpace);
                            //                    mCurAddCaption.setLetterSpacing(CAPTION_STANDARD_SPACING);

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
                            mCurCaption.setLineSpacing(CAPTION_STANDARD_LINE_SPACING);
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
                        if (mCurCaption == null) {
                            return;
                        }
                        if (mCaptionDataListClone == null) {
                            return;
                        }
                        if (spacingMode == CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER) {
                            float letterSpace = getLetterSpacing(CAPTION_PERCENT_MORE_LARGE_SPACING);
                            mCurCaption.setLetterSpacingType(NvsCaption.LETTER_SPACING_TYPE_ABSOLUTE);
                            mCurCaption.setLetterSpacing(letterSpace);
                            //                    mCurAddCaption.setLetterSpacing(CAPTION_MORE_LARGE_SPACING);
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
                            mCurCaption.setLineSpacing(CAPTION_MORE_LARGE_LINE_SPACING);
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
                        if (mCurCaption == null) {
                            return;
                        }

                        if (mCaptionDataListClone == null) {
                            return;
                        }

                        if (spacingMode == CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER) {
                            float letterSpace = getLetterSpacing(CAPTION_PERCENT_LARGE_SPACING);
                            mCurCaption.setLetterSpacingType(NvsCaption.LETTER_SPACING_TYPE_ABSOLUTE);
                            mCurCaption.setLetterSpacing(letterSpace);
                            //                    mCurAddCaption.setLetterSpacing(CAPTION_LARGE_SPACEING);
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
                            mCurCaption.setLineSpacing(CAPTION_LARGE_LINE_SPACEING);
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
                        bIsLetterSpacingApplyToAllCheck = true;
                        applyToAllCaption();
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
                        if (mCurCaption == null || !ifCouldEditCaption()) {
                            return;
                        }
                        if (mCaptionDataListClone == null) {
                            return;
                        }
                        List<PointF> list = mCurCaption.getBoundingRectangleVertices();
                        if (list == null || list.size() < 4) {
                            return;
                        }
                        Collections.sort(list, new Util.PointXComparator());

                        float xOffset = -(mTimeline.getVideoRes().imageWidth / 2 + list.get(0).x);
                        mCurCaption.translateCaption(new PointF(xOffset, 0));

                        mAlignType = CAPTION_ALIGNLEFT;
                        updateCaption();

                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index)
                                    .setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            mCaptionDataListClone.get(index).setTranslation(mCurCaption.getCaptionTranslation());
                        }
                    }

                    @Override
                    public void OnAlignCenterHorizontal() {
                        if (mCurCaption == null || !ifCouldEditCaption()) {
                            return;
                        }
                        if (mCaptionDataListClone == null) {
                            return;
                        }
                        List<PointF> list = mCurCaption.getBoundingRectangleVertices();
                        if (list == null || list.size() < 4) {
                            return;
                        }
                        Collections.sort(list, new Util.PointXComparator());

                        float xOffset = -((list.get(3).x - list.get(0).x) / 2 + list.get(0).x);
                        mCurCaption.translateCaption(new PointF(xOffset, 0));
                        updateCaption();
                        mAlignType = CAPTION_ALIGNHORIZCENTER;
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index)
                                    .setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            mCaptionDataListClone.get(index).setTranslation(mCurCaption.getCaptionTranslation());
                        }
                    }

                    @Override
                    public void OnAlignRight() {
                        if (mCurCaption == null || !ifCouldEditCaption()) {
                            return;
                        }
                        if (mCaptionDataListClone == null) {
                            return;
                        }
                        List<PointF> list = mCurCaption.getBoundingRectangleVertices();
                        if (list == null || list.size() < 4) {
                            return;
                        }
                        Collections.sort(list, new Util.PointXComparator());

                        float xOffset = mTimeline.getVideoRes().imageWidth / 2 - list.get(3).x;

                        mCurCaption.translateCaption(new PointF(xOffset, 0));

                        updateCaption();

                        mAlignType = CAPTION_ALIGNRIGHT;
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index)
                                    .setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            mCaptionDataListClone.get(index).setTranslation(mCurCaption.getCaptionTranslation());
                        }
                    }

                    @Override
                    public void OnAlignTop() {
                        if (mCurCaption == null || !ifCouldEditCaption()) {
                            return;
                        }
                        if (mCaptionDataListClone == null) {
                            return;
                        }
                        List<PointF> list = mCurCaption.getBoundingRectangleVertices();
                        if (list == null || list.size() < 4) {
                            return;
                        }
                        Collections.sort(list, new Util.PointYComparator());
                        float y_dis = list.get(3).y - list.get(0).y;

                        float yOffset = mTimeline.getVideoRes().imageHeight / 2 - list.get(0).y - y_dis;

                        mCurCaption.translateCaption(new PointF(0, yOffset));

                        updateCaption();

                        mAlignType = CAPTION_ALIGNTOP;
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index)
                                    .setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            mCaptionDataListClone.get(index).setTranslation(mCurCaption.getCaptionTranslation());
                        }
                    }

                    @Override
                    public void OnAlignCenterVertical() {
                        if (mCurCaption == null || !ifCouldEditCaption()) {
                            return;
                        }
                        if (mCaptionDataListClone == null) {
                            return;
                        }
                        List<PointF> list = mCurCaption.getBoundingRectangleVertices();
                        if (list == null || list.size() < 4) {
                            return;
                        }
                        Collections.sort(list, new Util.PointYComparator());

                        float yOffset = -((list.get(3).y - list.get(0).y) / 2 + list.get(0).y);
                        mCurCaption.translateCaption(new PointF(0, yOffset));

                        updateCaption();

                        mAlignType = CAPTION_ALIGNVERTCENTER;
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index)
                                    .setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            mCaptionDataListClone.get(index).setTranslation(mCurCaption.getCaptionTranslation());
                        }
                    }

                    @Override
                    public void OnAlignBottom() {
                        if (mCurCaption == null || !ifCouldEditCaption()) {
                            return;
                        }

                        if (mCaptionDataListClone == null) {
                            return;
                        }

                        List<PointF> list = mCurCaption.getBoundingRectangleVertices();
                        if (list == null || list.size() < 4) {
                            return;
                        }
                        Collections.sort(list, new Util.PointYComparator());
                        float y_dis = list.get(3).y - list.get(0).y;

                        float yOffset = -(mTimeline.getVideoRes().imageHeight / 2 + list.get(3).y - y_dis);
                        mCurCaption.translateCaption(new PointF(0, yOffset));

                        updateCaption();

                        mAlignType = CAPTION_ALIGNBOTTOM;
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            mCaptionDataListClone.get(index)
                                    .setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            mCaptionDataListClone.get(index).setTranslation(mCurCaption.getCaptionTranslation());
                        }
                    }

                    @Override
                    public void onIsApplyToAll(boolean isApplyToAll) {
                        bIsPositionApplyToAll = isApplyToAll;
                        applyToAllCaption();
                    }
                });
        return captionPositionFragment;
    }

    public CaptionFontSizeRatioFragment initCaptionFontSizeRatioFragment() {
        CaptionFontSizeRatioFragment captionFontSizeRatioFragment = new CaptionFontSizeRatioFragment();
        captionFontSizeRatioFragment.setOnCaptionFontSizeRationListener(
                new CaptionFontSizeRatioFragment.OnCaptionFontSizeRationListener() {
                    @Override
                    public void onFragmentLoadFinished() {
                        if (checkPosition()) {
                            mCaptionFontSizeRatioFragment.setSeekEnable(true);
                        } else {
                            boolean frameCaption = mCurCaption.isFrameCaption();
                            mCaptionFontSizeRatioFragment.setSeekEnable(!frameCaption);
                        }

                        captionFontSizeRatioFragment.applyToAllCaption(false);
                        if (mCaptionDataListClone == null) {
                            return;
                        }

                        int index = getCaptionIndex(mCurCaptionZVal);
                        CaptionInfo captionInfo = null;
                        if (index >= 0) {
                            captionInfo = mCaptionDataListClone.get(index);
                        }
                        if (captionInfo == null) {
                            return;
                        }
                        float captionSize = captionInfo.getCaptionSize();
                        if (captionSize > 0) {
                            int progress = (int) Math.round((captionSize / mFontDefaultSize - 0.25) / 7.75 * 100);
                            captionFontSizeRatioFragment.updateCaptionOpacityValue(progress);
                        }
                    }

                    @Override
                    public void onChangeFontSizeRation(int progress) {
                        if (mCurCaption == null) {
                            return;
                        }

                        if (mCaptionDataListClone == null) {
                            return;
                        }

                        int index = getCaptionIndex(mCurCaptionZVal);
                        CaptionInfo captionInfo = null;
                        if (index >= 0) {
                            captionInfo = mCaptionDataListClone.get(index);
                        }
                        if (captionInfo == null) {
                            return;
                        }

                        if (checkPosition()) {
                            float fontSizeRatio = 0.25f + progress / 100f * 7.75f;
                            List<NvsCaptionSpan> textSpanList = mCurCaption.getTextSpanList();
                            NvsFontSizeRatioSpan nvsFontSizeRatioSpan =
                                    new NvsFontSizeRatioSpan(mSelectionStart, mSelectionEnd);
                            setSpanList(nvsFontSizeRatioSpan, fontSizeRatio, textSpanList, captionInfo);
                        } else {
                            float fontSize = (0.25f + progress / 100f * 7.75f) * mFontDefaultSize;
                            mCurCaption.setFontSize(fontSize);
                            captionInfo.setCaptionSize(fontSize);
                        }
                        updateCaption();
                    }

                    @Override
                    public void onIsApplyToAll(boolean isApplyToAll) {
                        bIsFontSizeRatioApplyToAll = isApplyToAll;
                        bIsFontSizeRatioApplyToAllCheck = true;
                        applyToAllCaption();
                    }
                });

        return captionFontSizeRatioFragment;
    }

    public CaptionShadowFragment initCaptionShadowFragment() {
        CaptionShadowFragment captionShadowFragment = new CaptionShadowFragment();
        captionShadowFragment.setCaptionBackgroundInfolist(mCaptionShadowList);
        captionShadowFragment.setOnCaptionFontSizeRationListener(
                new CaptionShadowFragment.OnCaptionShadowListener() {
                    @Override
                    public void onFragmentLoadFinished() {
                        captionShadowFragment.applyToAllCaption(false);
                        mSelectedShadowPos = getShadowSelectedIndex();
                        if (mSelectedShadowPos == 0) {
                            mCaptionShadowList.get(mSelectedShadowPos).mSelected = false;
                            mCaptionShadowList.get(0).mSelected = true;
                            mCaptionShadowFragment.notifyDataSetChanged();
                            mSelectedShadowPos = 0;
                            captionShadowFragment.showSeekContainerVisible(View.GONE);
                        } else if (mSelectedShadowPos > 0) {
                            mCaptionShadowList.get(mSelectedShadowPos).mSelected = true;
                            mCaptionShadowFragment.notifyDataSetChanged();
                            captionShadowFragment.showSeekContainerVisible(View.VISIBLE);
                            int captionIndex = getCaptionIndex(mCurCaptionZVal);
                            if (captionIndex >= 0
                                    && mCaptionDataListClone != null
                                    && mCaptionDataListClone.size() > 0) {
                                CaptionInfo captionInfo = mCaptionDataListClone.get(captionIndex);
                                if (captionInfo != null) {
                                    float shadowOpacity = captionInfo.getShadowOpacity();
                                    captionShadowFragment.updateCaptionOpacityValue((int) (shadowOpacity * 100));
                                }
                            }
                        }
                    }

                    @Override
                    public void onChangeShadow(int progress) {
                        if (mCurCaption == null) {
                            return;
                        }
                        if (mCaptionDataListClone == null) {
                            return;
                        }
                        CaptionInfo captionInfo = null;
                        int index = getCaptionIndex(mCurCaptionZVal);
                        if (index >= 0) {
                            captionInfo = mCaptionDataListClone.get(index);
                        }
                        if (captionInfo == null) {
                            return;
                        }

                        //                if (checkPosition()) {
                        //                    List<NvsCaptionSpan> textSpanList = mCurCaption.getTextSpanList();
                        //                    NvsShadowOpacitySpan nvsShadowOpacitySpan=new NvsShadowOpacitySpan(mSelectionStart,mSelectionEnd);
                        //                    setSpanList(nvsShadowOpacitySpan, progress/100f, textSpanList, captionInfo);
                        //                } else {
                        //                    changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_SHADOW_OPACITY, mCaptionDataListClone.get(index));
                        //                    NvsColor shadowColor = mCurCaption.getShadowColor();
                        //                    shadowColor.a=progress/100f;
                        //                    mCurCaption.setShadowColor(shadowColor);
                        //                    captionInfo.setMisShadowOpacity(progress/100f);
                        //                }

                        changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_SHADOW_OPACITY,
                                mCaptionDataListClone.get(index));
                        NvsColor shadowColor = mCurCaption.getShadowColor();
                        shadowColor.a = progress / 100f;
                        mCurCaption.setShadowColor(shadowColor);
                        //String colorToHexString = nvsColorToHexString(shadowColor);
                        //captionInfo.setShadowColor(colorToHexString);
                        captionInfo.setShadowOpacity(progress / 100f);
                        updateCaption();
                    }

                    @Override
                    public void onIsApplyToAll(boolean isApplyToAll) {
                        bIsCaptionShadowToAll = isApplyToAll;
                        bIsCaptionShadowToAllCheck = true;
                        applyToAllCaption();
                    }

                    @Override
                    public void onCaptionShadowColor(int pos) {
                        if (pos < 0 || pos > mCaptionShadowList.size()) {
                            return;
                        }
                        if (mCurCaption == null) {
                            return;
                        }
                        if (mSelectedShadowPos == pos) {
                            return;
                        }
                        if (mCaptionDataListClone == null) {
                            return;
                        }

                        int index = getCaptionIndex(mCurCaptionZVal);
                        CaptionInfo captionInfo = null;
                        if (index >= 0) {
                            captionInfo = mCaptionDataListClone.get(index);
                        }

                        if (captionInfo == null) {
                            return;
                        }

                        //刷新页面 refresh page
                        mCaptionShadowList.get(mSelectedShadowPos).mSelected = false;
                        mCaptionShadowList.get(pos).mSelected = true;
                        mCaptionShadowFragment.notifyDataSetChanged();
                        mSelectedShadowPos = pos;

                        if (pos == 0) {
                            //                    if(checkPosition()){
                            //                        changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_SHADOW_OPACITY, mCaptionDataListClone.get(index));
                            //                    }else{
                            //                        mCurCaption.setDrawShadow(false);
                            //                        captionShadowFragment.showSeekContainerVisible(View.GONE);
                            //                        captionInfo.setCaptionShadowColor("");
                            //                    }

                            mCurCaption.setDrawShadow(false);
                            captionShadowFragment.showSeekContainerVisible(View.GONE);
                            captionInfo.setShadowColor("");
                            captionInfo.setShadowOpacity(0.0f);
                            captionInfo.setShadow(false);
                            captionInfo.setUsedShadowFlag(CaptionInfo.ATTRIBUTE_UNUSED_FLAG);
                        } else {
                            //                    if (checkPosition()){
                            //                        List<NvsCaptionSpan> textSpanList = mCurCaption.getTextSpanList();
                            //                        NvsShadowOpacitySpan nvsShadowOpacitySpan=new NvsShadowOpacitySpan(mSelectionStart,mSelectionEnd);
                            //                        setSpanList(nvsShadowOpacitySpan, 1.0f, textSpanList, captionInfo);
                            //                    }else{
                            //                        changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_SHADOW_OPACITY, mCaptionDataListClone.get(index));
                            //                        mCurCaption.setDrawShadow(true);
                            //                        captionShadowFragment.showSeekContainerVisible(View.VISIBLE);
                            //                        captionShadowFragment.updateCaptionOpacityValue(100);
                            //                        /*
                            //                         * 设置背景色
                            //                         * Set font color
                            //                         * */
                            //                        NvsColor color = ColorUtil.colorStringtoNvsColor(mCaptionShadowList.get(pos).mColorValue);
                            //
                            //                        PointF point = new PointF(7, -7);
                            //                        /*
                            //                         * 设置字幕阴影偏移量
                            //                         * Set the caption shadow offset
                            //                         * */
                            //                        mCurCaption.setShadowOffset(point);
                            //                        /*
                            //                         * 设置字幕阴影颜色
                            //                         * Set the caption shadow color
                            //                         * */
                            //                        mCurCaption.setShadowColor(color);
                            //                        captionInfo.setCaptionShadowColor(mCaptionShadowList.get(pos).mColorValue);
                            //                    }
                            //                }
                            changeOrRemoveSpanByType(NvsCaptionSpan.SPAN_TYPE_SHADOW_OPACITY,
                                    mCaptionDataListClone.get(index));
                            mCurCaption.setDrawShadow(true);
                            captionShadowFragment.showSeekContainerVisible(View.VISIBLE);
                            captionShadowFragment.updateCaptionOpacityValue(100);
                            /*
                             * 设置背景色
                             * Set font color
                             * */
                            NvsColor color =
                                    ColorUtil.colorStringtoNvsColor(mCaptionShadowList.get(pos).mColorValue);

                            PointF point = new PointF(7, -7);
                            /*
                             * 设置字幕阴影偏移量
                             * Set the caption shadow offset
                             * */
                            mCurCaption.setShadowOffset(point);
                            /*
                             * 设置字幕阴影颜色
                             * Set the caption shadow color
                             * */
                            mCurCaption.setShadowColor(color);
                            captionInfo.setShadowColor(mCaptionShadowList.get(pos).mColorValue);
                            captionInfo.setShadowOpacity(1.0f);
                            captionInfo.setShadow(true);
                            captionInfo.setUsedShadowFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        }
                        updateCaption();
                    }
                });

        return captionShadowFragment;
    }

    private int getCaptionStyleSelectedIndex() {
        int selectIndex = 0;
        if (mCurCaption != null) {
            String uuid = mCurCaption.getCaptionStylePackageId();
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
        if (mCurCaption != null) {
            switch (type) {
                case NvAsset.ASSET_CAPTION_RICH_WORD:
                    String uuid = mCurCaption.getModularCaptionRendererPackageId();
                    mSelectedRichPos = getTargetPosition(mRichWordList, uuid);
                    break;
                case NvAsset.ASSET_CAPTION_BUBBLE:
                    mSelectedBubblePos =
                            getTargetPosition(mBubbleList, mCurCaption.getModularCaptionContextPackageId());
                    break;
                case ASSET_CAPTION_ANIMATION:
                    mSelectedAnimationPos =
                            getTargetPosition(mAnimationList, mCurCaption.getModularCaptionAnimationPackageId());
                    mSelectedInAnimationPos = 0;
                    mSelectedOutAnimationPos = 0;
                    break;
                case NvAsset.ASSET_CAPTION_IN_ANIMATION:
                    mSelectedInAnimationPos = getTargetPosition(mMarchInAniList,
                            mCurCaption.getModularCaptionInAnimationPackageId());
                    mSelectedAnimationPos = 0;
                    break;
                case ASSET_CAPTION_OUT_ANIMATION:
                    mSelectedOutAnimationPos = getTargetPosition(mMarchOutAniList,
                            mCurCaption.getModularCaptionOutAnimationPackageId());
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
        if (mCaptionDataListClone == null) {
            return -1;
        }
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
        if (mCaptionDataListClone == null) {
            return -1;
        }
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

    private int getShadowSelectedIndex() {
        if (mCaptionDataListClone == null) {
            return -1;
        }
        int selectedPos = 0;
        int captionIndex = getCaptionIndex(mCurCaptionZVal);
        if (captionIndex >= 0) {
            String shadowColor = mCaptionDataListClone.get(captionIndex).getShadowColor();
            for (int i = 0; i < mCaptionShadowList.size(); ++i) {
                if (mCaptionShadowList.get(i).mColorValue.compareTo(shadowColor) == 0) {
                    selectedPos = i;
                    break;
                }
            }
        }
        return selectedPos;
    }

    private int getBackgroundSelectedIndex() {
        if (mCaptionDataListClone == null) {
            return -1;
        }
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
        if (mCaptionDataListClone == null) {
            return -1;
        }
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

    private void updateCaption(String content) {
        if (mCurCaption != null) {
            mCurCaption.setText(content);
            CaptionInfo currentCaptionInfo = getCurrentCaptionInfo();
            if (currentCaptionInfo != null) {
                currentCaptionInfo.setText(content);
            }
            seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
            updateDrawRect();
        }
    }

    private void updateCaption() {
        //        seekTimeline(BackupData.instance().getCurSeekTimelinePos());
        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
        updateDrawRect();
    }

    private void updateDrawRect() {
        if (mCurCaption != null) {
            int alignVal = mCurCaption.getTextAlignment();
            mVideoFragment.setAlignIndex(alignVal);
            int captionIndex = getCaptionIndex((int) mCurCaption.getZValue());
            CaptionInfo captionInfo = mCaptionDataListClone.get(captionIndex);
            if (captionInfo != null) {
                Map<Long, KeyFrameInfo> keyFrameInfo = captionInfo.getKeyFrameInfo();
                if (keyFrameInfo.isEmpty()) {
                    mVideoFragment.setCurCaption(mCurCaption);
                    mVideoFragment.updateCaptionCoordinate(mCurCaption);
                    mVideoFragment.changeCaptionRectVisible();
                } else {
                    long duration =
                            mStreamingContext.getTimelineCurrentPosition(mTimeline) - mCurCaption.getInPoint();
                    mCurCaption.setCurrentKeyFrameTime(duration);

                    mVideoFragment.setCurCaption(mCurCaption);
                    mVideoFragment.updateCaptionCoordinate(mCurCaption);
                    mVideoFragment.changeCaptionRectVisible();

                    mCurCaption.removeKeyframeAtTime(TRANS_X, duration);
                    mCurCaption.removeKeyframeAtTime(TRANS_Y, duration);
                    mCurCaption.removeKeyframeAtTime(SCALE_X, duration);
                    mCurCaption.removeKeyframeAtTime(SCALE_Y, duration);
                    mCurCaption.removeKeyframeAtTime(ROTATION_Z, duration);
                }
            }
        }
    }

    private void seekTimeline(long timestamp) {
        mVideoFragment.seekTimeline(timestamp,
                NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
    }

    private int getCaptionIndex(int curZValue) {
        if (mCaptionDataListClone == null) {
            return 0;
        }
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
        //        long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        long curPos = mCurTime;
        List<NvsTimelineCaption> captionList = mTimeline.getCaptionsByTimelinePosition(curPos);
        int captionCount = captionList.size();
        for (int index = 0; index < captionCount; index++) {
            int tmpZVal = (int) captionList.get(index).getZValue();
            if (mCurCaptionZVal == tmpZVal) {
                mCurCaption = captionList.get(index);
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

    public void setEditText(String text) {
        isAddCaption = false;
        mEtCaptionInput.setText(text);
        mEtCaptionInput.setSelection(mEtCaptionInput.getText().length());
    }

    public void showSoftInput(@NonNull final View view, final int flags) {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        imm.showSoftInput(view, flags);
    }

    /*
     * 添加字幕
     * Add captions
     * */
    private void addCaption(String caption, boolean traditional) {
        long inPoint = mCurTime;
        long captionDuration = 4 * Constants.NS_TIME_BASE;
        long outPoint = inPoint + captionDuration;
        long duration = mTimeline.getDuration();

        if (outPoint > duration) {
            captionDuration = duration - inPoint;
            if (captionDuration <= Constants.NS_TIME_BASE) {
                captionDuration = Constants.NS_TIME_BASE;
                inPoint = duration - captionDuration;
                if (duration <= Constants.NS_TIME_BASE) {
                    captionDuration = duration;
                    inPoint = 0;
                }
            }
            outPoint = duration;
        }
        if (traditional) {
            mCurCaption = mTimeline.addCaption(caption, inPoint, captionDuration, null);
        } else {
            mCurCaption = mTimeline.addModularCaption(caption, inPoint, captionDuration);
        }
        if (mCurCaption == null) {
            Log.e(TAG, "addCaption: " + " 添加字幕失败！");
            return;
        }
        float zVal = getCurCaptionZVal();
        mCurCaption.setZValue(zVal);
        mCurCaptionZVal = (int) zVal;

        mFontDefaultSize = mCurCaption.getFontSize();
        MSApplication.mDefaultCaptionSize = mCurCaption.getFontSize();
        CaptionInfo captionInfo = Util.saveCaptionData(mCurCaption);
        if (null != mTempCaptionInfo) {
            mTempCaptionInfo.setText(mCurCaption.getText());
            TimelineUtil.updateCaptionAttribute(mCurCaption, mTempCaptionInfo, false);
            captionInfo = mTempCaptionInfo.clone();
        }
        if (mCaptionDataListClone == null) {
            mCaptionDataListClone = new ArrayList<>();
        }
        if (captionInfo != null) {
            captionInfo.setTraditionCaption(traditional);
            mCaptionDataListClone.add(captionInfo);
        }
        seekTimeline(mCurTime + 40000);
        //        showOrHideCaptionAlpha();
        //        if (mCurCaption != null) {
        //            alphaSeek.setProgress((int) (mCurCaption.getOpacity() * alphaSeek.getMax()));
        //        }
        //        updateDrawRect();
    }

    /**
     * 获取当前字幕Z值
     * Gets the current caption Z value
     */
    private float getCurCaptionZVal() {
        float zVal = 0.0f;
        NvsTimelineCaption caption = mTimeline.getFirstCaption();
        while (caption != null) {
            float tmpZVal = caption.getZValue();
            if (tmpZVal > zVal) {
                zVal = tmpZVal;
            }
            caption = mTimeline.getNextCaption(caption);
        }
        zVal += 1.0;
        return zVal;
    }

    private void addOnSoftKeyBoardVisibleListener() {
        if (mKeyboardHeight > 0) {
            return;
        }
        mDecorView = getWindow().getDecorView();
        mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Util.isFastClick()) {
                    return;
                }
                Rect rect = new Rect();
                mDecorView.getWindowVisibleDisplayFrame(rect);
                //计算出可见屏幕的高度 Calculate the height of the visible screen
                int displayHeight = rect.bottom - rect.top;
                //获得屏幕整体的高度 Get the overall height of the screen
                int hight = mDecorView.getHeight();
                boolean visible = (double) displayHeight / hight < 0.8;
                if (visible && !mIsVisibleForLast) {
                    mKeyboardHeight = hight - displayHeight - BarUtils.getStatusBarHeight() - BarUtils.
                            getNavBarHeight(CaptionStyleActivity.this);
                }
                mIsVisibleForLast = visible;
                Log.d("ddd", "--------------onGlobalLayout-------------------displayHeight="
                        + displayHeight
                        + "  mIsVisibleForLast="
                        + mIsVisibleForLast);
                //                mEtCaptionInput.clearFocus();
                if (mKeyboardHeight > 0) {
                    if (visible) {
                        //mEtCaptionInput.clearFocus();
                        int height = mKeyboardHeight + SizeUtils.dp2px(95);
                        int need_height = (int) SharedPreferencesUtils.getParam(mContext, "need_height", 0);
                        if (height > need_height) {
                            changeViewHeight(height);
                            SharedPreferencesUtils.setParam(mContext, "need_height", height);
                        } else {
                            changeViewHeight(need_height);
                        }
                    }
                }
            }
        };
        mDecorView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    private void addKeyBoardListener() {
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                mIsVisibleForLast = true;
                Log.d("keyBoardShow", "  mIsVisibleForLast=" + mIsVisibleForLast);
                mKeyboardHeight = height;

                if (mKeyboardHeight > 0) {
                    //mEtCaptionInput.clearFocus();
                    int heightt = mKeyboardHeight + SizeUtils.dp2px(95);
                    int need_height = (int) SharedPreferencesUtils.getParam(mContext, "need_height", 0);
                    if (heightt > need_height) {
                        changeViewHeight(heightt);
                        SharedPreferencesUtils.setParam(mContext, "need_height", heightt);
                    } else {
                        changeViewHeight(need_height);
                    }
                }
            }

            @Override
            public void keyBoardHide(int height) {
                Log.d("keyBoardHide", "  mIsVisibleForLast=" + height);
                mIsVisibleForLast = false;
                if (mCaptionFontSizeRatioFragment != null) {
                    if (checkPosition()) {
                        mCaptionFontSizeRatioFragment.setSeekEnable(true);
                    } else {
                        if (null == mCurCaption) {
                            return;
                        }
                        boolean frameCaption = mCurCaption.isFrameCaption();
                        mCaptionFontSizeRatioFragment.setSeekEnable(!frameCaption);
                    }
                }
            }
        });
    }

    public void setSpanList(NvsCaptionSpan nvsCaptionSpan, Object object,
                            List<NvsCaptionSpan> spanList, CaptionInfo captionInfo) {

        setSpanValue(nvsCaptionSpan, object);
        spanList.add(nvsCaptionSpan);
        mCurCaption.setTextSpanList(spanList);
        captionInfo.setList(spanList);
        updateCaption();
    }

    public void setSpanValue(NvsCaptionSpan nvsCaptionSpan, Object object) {
        if (nvsCaptionSpan instanceof NvsColorSpan && object instanceof NvsColor) {
            ((NvsColorSpan) nvsCaptionSpan).setR(((NvsColor) object).r);
            ((NvsColorSpan) nvsCaptionSpan).setB(((NvsColor) object).b);
            ((NvsColorSpan) nvsCaptionSpan).setG(((NvsColor) object).g);
        } else if (nvsCaptionSpan instanceof NvsFontFamilySpan) {
            ((NvsFontFamilySpan) nvsCaptionSpan).setFontFamily((String) object);
        } else if (nvsCaptionSpan instanceof NvsOpacitySpan) {
            ((NvsOpacitySpan) nvsCaptionSpan).setOpacity((Float) object);
        } else if (nvsCaptionSpan instanceof NvsItalicSpan) {
            ((NvsItalicSpan) nvsCaptionSpan).setItalic((Boolean) object);
        } else if (nvsCaptionSpan instanceof NvsUnderlineSpan) {
            ((NvsUnderlineSpan) nvsCaptionSpan).setUnderline((Boolean) object);
        } else if (nvsCaptionSpan instanceof NvsRendererIdSpan) {
            ((NvsRendererIdSpan) nvsCaptionSpan).setRendererId((String) object);
        } else if (nvsCaptionSpan instanceof NvsOutlineColorSpan) {
            if (object == null) {
                ((NvsOutlineColorSpan) nvsCaptionSpan).setOutlineColor(null);
            } else {
                ((NvsOutlineColorSpan) nvsCaptionSpan).setOutlineColor((NvsColor) object);
            }
        } else if (nvsCaptionSpan instanceof NvsOutlineWidthSpan) {
            ((NvsOutlineWidthSpan) nvsCaptionSpan).setOutlineWidth((Float) object);
        } else if (nvsCaptionSpan instanceof NvsNormalTextSpan) {
            ((NvsNormalTextSpan) nvsCaptionSpan).setOutlineWidth((Float) object);
        } else if (nvsCaptionSpan instanceof NvsFontSizeRatioSpan && object instanceof Float) {
            ((NvsFontSizeRatioSpan) nvsCaptionSpan).setFontSizeRatio((Float) object);
        } else if (nvsCaptionSpan instanceof NvsBodyOpacitySpan && object instanceof Float) {
            ((NvsBodyOpacitySpan) nvsCaptionSpan).setOpacity((Float) object);
        } else if (nvsCaptionSpan instanceof NvsOutlineOpacitySpan && object instanceof Float) {
            ((NvsOutlineOpacitySpan) nvsCaptionSpan).setOpacity((Float) object);
        } else if (nvsCaptionSpan instanceof NvsShadowOpacitySpan && object instanceof Float) {
            ((NvsShadowOpacitySpan) nvsCaptionSpan).setOpacity((Float) object);
        }
    }

    private void changeOrRemoveSpanByType(String type, CaptionInfo captionInfo) {
        refreshStartEnd();
        List<NvsCaptionSpan> textSpanList = mCurCaption.getTextSpanList();
        for (int i = textSpanList.size() - 1; i >= 0; i--) {
            NvsCaptionSpan nvsCaptionSpan = textSpanList.get(i);
            int start = nvsCaptionSpan.getStart();
            int end = nvsCaptionSpan.getEnd();
            String spanType = nvsCaptionSpan.getType();
            if (spanType.equals(type) && mSelectionStart == mSelectionEnd) {
                textSpanList.remove(nvsCaptionSpan);
                continue;
            }
            if (spanType.equals(type) && mSelectionStart <= start && mSelectionEnd >= end) {
                //span在内部 直接删除
                //Span Delete directly internally
                textSpanList.remove(nvsCaptionSpan);
            } else if (spanType.equals(type)
                    && start < mSelectionStart
                    && end > mSelectionStart
                    && end <= mSelectionEnd) {
                //span的尾部在内部 更改尾部位置
                //The tail of span changes the tail position internally
                nvsCaptionSpan.setEnd(mSelectionStart);
            } else if (spanType.equals(type)
                    && start < mSelectionEnd
                    && end > mSelectionEnd
                    && start >= mSelectionStart) {
                //span的头部在内部 更改头部
                //The head of span changes the head internally
                nvsCaptionSpan.setStart(mSelectionEnd);
            } else if (spanType.equals(type) && start < mSelectionStart && end > mSelectionEnd) {
                //span比选择区域大，需要裂变
                //Span is larger than the selection area and requires fission
                NvsCaptionSpan spanByType = createSpanByType(type, mSelectionEnd, nvsCaptionSpan.getEnd());
                Object objet = getValueBySpan(nvsCaptionSpan);
                setSpanValue(spanByType, objet);
                nvsCaptionSpan.setEnd(mSelectionStart);
                textSpanList.add(i, spanByType);
            }
        }
        mCurCaption.setTextSpanList(textSpanList);
        captionInfo.setList(textSpanList);
        updateCaption();
    }

    private Object getValueBySpan(NvsCaptionSpan nvsCaptionSpan) {
        if (nvsCaptionSpan instanceof NvsColorSpan) {
            float r = ((NvsColorSpan) nvsCaptionSpan).getR();
            float g = ((NvsColorSpan) nvsCaptionSpan).getG();
            float b = ((NvsColorSpan) nvsCaptionSpan).getB();
            NvsColor nvsColor = new NvsColor(r, g, b, 1);
            return nvsColor;
        } else if (nvsCaptionSpan instanceof NvsFontFamilySpan) {
            return ((NvsFontFamilySpan) nvsCaptionSpan).getFontFamily();
        } else if (nvsCaptionSpan instanceof NvsOpacitySpan) {
            return ((NvsOpacitySpan) nvsCaptionSpan).getOpacity();
        } else if (nvsCaptionSpan instanceof NvsItalicSpan) {
            return ((NvsItalicSpan) nvsCaptionSpan).isItalic();
        } else if (nvsCaptionSpan instanceof NvsUnderlineSpan) {
            return ((NvsUnderlineSpan) nvsCaptionSpan).isUnderline();
        } else if (nvsCaptionSpan instanceof NvsRendererIdSpan) {
            return ((NvsRendererIdSpan) nvsCaptionSpan).getRendererId();
        } else if (nvsCaptionSpan instanceof NvsOutlineColorSpan) {
            return ((NvsOutlineColorSpan) nvsCaptionSpan).getOutlineColor();
        } else if (nvsCaptionSpan instanceof NvsOutlineWidthSpan) {
            return ((NvsOutlineWidthSpan) nvsCaptionSpan).getOutlineWidth();
        } else if (nvsCaptionSpan instanceof NvsNormalTextSpan) {
            return ((NvsNormalTextSpan) nvsCaptionSpan).getOutlineWidth();
        } else if (nvsCaptionSpan instanceof NvsFontSizeRatioSpan) {
            return ((NvsFontSizeRatioSpan) nvsCaptionSpan).getFontSizeRatio();
        } else if (nvsCaptionSpan instanceof NvsBodyOpacitySpan) {
            return ((NvsBodyOpacitySpan) nvsCaptionSpan).getOpacity();
        } else if (nvsCaptionSpan instanceof NvsOutlineOpacitySpan) {
            return ((NvsOutlineOpacitySpan) nvsCaptionSpan).getOpacity();
        } else if (nvsCaptionSpan instanceof NvsShadowOpacitySpan) {
            return ((NvsShadowOpacitySpan) nvsCaptionSpan).getOpacity();
        }
        return null;
    }

    private NvsCaptionSpan createSpanByType(String type, int start, int end) {
        NvsCaptionSpan nvsCaptionSpan = null;
        if (NvsCaptionSpan.SPAN_TYPE_OUTLINE_WIDTH.equals(type)) {
            nvsCaptionSpan = new NvsOutlineWidthSpan(start, end);
        } else if (NvsCaptionSpan.SPAN_TYPE_COLOR.equals(type)) {
            nvsCaptionSpan = new NvsColorSpan(start, end);
        } else if (NvsCaptionSpan.SPAN_TYPE_FONT_FAMILY.equals(type)) {
            nvsCaptionSpan = new NvsFontFamilySpan(start, end);
        } else if (NvsCaptionSpan.SPAN_TYPE_ITALIC.equals(type)) {
            nvsCaptionSpan = new NvsItalicSpan(start, end);
        } else if (NvsCaptionSpan.SPAN_TYPE_UNDERLINE.equals(type)) {
            nvsCaptionSpan = new NvsUnderlineSpan(start, end);
        } else if (NvsCaptionSpan.SPAN_TYPE_OPACITY.equals(type)) {
            nvsCaptionSpan = new NvsBodyOpacitySpan(start, end);
        } else if (NvsCaptionSpan.SPAN_TYPE_RENDER_ID.equals(type)) {
            nvsCaptionSpan = new NvsRendererIdSpan(start, end);
        } else if (NvsCaptionSpan.SPAN_TYPE_OUTLINE_COLOR.equals(type)) {
            nvsCaptionSpan = new NvsOutlineColorSpan(start, end);
        } else if (NvsCaptionSpan.SPAN_TYPE_NORMAL_TEXT.equals(type)) {
            nvsCaptionSpan = new NvsNormalTextSpan(start, end);
        } else if (NvsCaptionSpan.SPAN_TYPE_FONT_SIZE_RATIO.equals(type)) {
            nvsCaptionSpan = new NvsFontSizeRatioSpan(start, end);
        } else if (NvsCaptionSpan.SPAN_TYPE_BODY_OPACITY.equals(type)) {
            nvsCaptionSpan = new NvsBodyOpacitySpan(start, end);
        } else if (NvsCaptionSpan.SPAN_TYPE_OUTLINE_OPACITY.equals(type)) {
            nvsCaptionSpan = new NvsOutlineOpacitySpan(start, end);
        } else if (NvsCaptionSpan.SPAN_TYPE_SHADOW_OPACITY.equals(type)) {
            nvsCaptionSpan = new NvsShadowOpacitySpan(start, end);
        }
        return nvsCaptionSpan;
    }

    private boolean checkPosition() {
        refreshStartEnd();
        if (mSelectionStart < mSelectionEnd) {
            return true;
        }
        return false;
    }

    private void refreshStartEnd() {
        mSelectionStart = mEtCaptionInput.getSelectionStart();
        mSelectionEnd = mEtCaptionInput.getSelectionEnd();
    }
}
