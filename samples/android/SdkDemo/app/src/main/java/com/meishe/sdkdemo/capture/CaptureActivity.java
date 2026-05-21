package com.meishe.sdkdemo.capture;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.reflect.TypeToken;
import com.meicam.effect.sdk.NvsEffect;
import com.meicam.effect.sdk.NvsEffectRenderCore;
import com.meicam.effect.sdk.NvsVideoEffect;
import com.meicam.effect.sdk.NvsVideoEffectAnimatedSticker;
import com.meicam.effect.sdk.NvsVideoEffectCompoundCaption;
import com.meicam.sdk.NvsARSceneManipulate;
import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsCaption;
import com.meicam.sdk.NvsCaptureAnimatedSticker;
import com.meicam.sdk.NvsCaptureAudioFx;
import com.meicam.sdk.NvsCaptureCompoundCaption;
import com.meicam.sdk.NvsCaptureSceneInfo;
import com.meicam.sdk.NvsCaptureVideoFx;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsExpressionParam;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsVideoFrameInfo;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoStreamInfo;
import com.meishe.arscene.BeautyDataManager;
import com.meishe.arscene.BeautyHelper;
import com.meishe.arscene.bean.CompoundFxInfo;
import com.meishe.arscene.bean.FxParams;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.base.constants.AndroidOS;
import com.meishe.base.msbus.MSBus;
import com.meishe.base.msbus.MSSubscribe;
import com.meishe.base.utils.BarUtils;
import com.meishe.base.utils.KeyboardUtils;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.base.view.MSLiveWindow;
import com.meishe.base.view.MSLiveWindowExt;
import com.meishe.base.view.MagicProgress;
import com.meishe.makeup.MakeupDataManager;
import com.meishe.makeup.MakeupHelper;
import com.meishe.makeup.makeup.BaseParam;
import com.meishe.makeup.makeup.BeautyParam;
import com.meishe.makeup.makeup.CategoryContent;
import com.meishe.makeup.makeup.FilterParam;
import com.meishe.makeup.makeup.Makeup;
import com.meishe.makeup.makeup.MakeupCategory;
import com.meishe.makeup.makeup.MakeupParamContent;
import com.meishe.sdkdemo.BuildConfig;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BasePermissionActivity;
import com.meishe.sdkdemo.bean.AdjustSpecialEffectsInfo;
import com.meishe.sdkdemo.bean.AssetLevelBean;
import com.meishe.sdkdemo.bean.voice.ChangeVoiceData;
import com.meishe.sdkdemo.capture.bean.EffectInfo;
import com.meishe.sdkdemo.capture.bean.TypeAndCategoryInfo;
import com.meishe.sdkdemo.capture.fragment.CaptureEffectFragment;
import com.meishe.sdkdemo.capture.fragment.beauty.BeautyCaptureFragment;
import com.meishe.sdkdemo.capture.fragment.filter.CaptureFilterFragment;
import com.meishe.sdkdemo.capture.viewmodel.CaptureViewModel;
import com.meishe.sdkdemo.dialog.AudioNoisePop;
import com.meishe.sdkdemo.dialog.CaptionEditPop;
import com.meishe.sdkdemo.dialog.TopMoreDialog;
import com.meishe.sdkdemo.dialog.VoicePop;
import com.meishe.sdkdemo.download.AssetDownloadActivity;
import com.meishe.sdkdemo.edit.VideoEditActivity;
import com.meishe.sdkdemo.edit.compoundcaption.FontInfo;
import com.meishe.sdkdemo.edit.data.FilterItem;
import com.meishe.sdkdemo.edit.data.ParseJsonFile;
import com.meishe.sdkdemo.edit.data.Props;
import com.meishe.sdkdemo.edit.music.AudioPlayer;
import com.meishe.sdkdemo.edit.music.SelectMusicActivity;
import com.meishe.sdkdemo.edit.view.DrawRect;
import com.meishe.sdkdemo.edit.view.dialog.TipsDialog;
import com.meishe.sdkdemo.edit.watermark.SingleClickActivity;
import com.meishe.sdkdemo.repository.AppRepository;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.AssetFxUtil;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.Logger;
import com.meishe.sdkdemo.utils.MediaScannerUtil;
import com.meishe.sdkdemo.utils.ParameterSettingValues;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.TimeFormatUtil;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.MusicInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.utils.dataInfo.VideoClipFxInfo;
import com.meishe.sdkdemo.utils.permission.PermissionDialog;
import com.meishe.sdkdemo.view.AdjustSpecialEffectsView;
import com.meishe.sdkdemo.view.FaceAvatarView;
import com.meishe.sdkdemo.view.FilterView;
import com.meishe.sdkdemo.view.MakeUpSingleView;
import com.meishe.utils.ColorUtil;
import com.meishe.utils.ToastUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.meishe.sdkdemo.utils.Constants.BUILD_HUMAN_AI_TYPE_MS;
import static com.meishe.sdkdemo.utils.Constants.FRAGMENT_FILTER_TAG;
import static com.meishe.sdkdemo.utils.Constants.HUMAN_AI_TYPE_MS;
import static com.meishe.sdkdemo.utils.Constants.HUMAN_AI_TYPE_NONE;
import static com.meishe.sdkdemo.utils.MediaConstant.SINGLE_PICTURE_CLICK_CANCEL;
import static com.meishe.sdkdemo.utils.MediaConstant.SINGLE_PICTURE_CLICK_CONFIRM;
import static com.meishe.sdkdemo.utils.MediaConstant.SINGLE_PICTURE_CLICK_TYPE;
import static com.meishe.sdkdemo.utils.MediaConstant.SINGLE_PICTURE_PATH;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zd
 * @CreateDate : 2018-06-05
 * @Description :主题拍摄页。Theme theme shooting page
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CaptureActivity extends BasePermissionActivity implements
        NvsStreamingContext.CaptureDeviceCallback,
        NvsStreamingContext.CaptureRecordingDurationCallback,
        NvsStreamingContext.CaptureRecordingStartedCallback, NvsStreamingContext.CapturedPictureCallback {
    private static final String TAG = CaptureActivity.class.getSimpleName();
    private static final int REQUEST_FILTER_LIST_CODE = 110;
    private static final int REQUEST_CODE_BACKGROUND_SEG = 111;
    private static final int RECORD_DEFAULT = 0;
    private static final int RECORDING = 1;
    private static final int RECORD_FINISH = 2;
    private static final int HANDLER_FRAME = 1001;
    private CaptureHandler mHandler;
    private ConstraintLayout mCaptureRoot;
    private ImageView mIvExit, mIvMore;
    private TextView mTvFrame;
    private View mLlRightContainer;
    private TextView mIvChangeCamera;
    private TextView mSegment;
    private TextView mBackgroundSegment;
    private TextView mAudioNoise;
    private RelativeLayout mCaptureVoice;
    private TextView mTvVoice;
    private ConstraintLayout mFlMiddleParent;
    private ImageView mDelete;
    private View mVideoTimeDot;
    private TextView mRecordTime;
    private ImageView mNext;
    private ConstraintLayout mFlBottomParent;
    private TextView mCaptureMakeup, mCaptureBeauty, mCaptureFilter, mCaptureProp;
    private ImageView mIvTakePhotoBg;
    private TextView mStartText;

    private FrameLayout mFlStartRecord;
    private LinearLayout mRecordTypeLayout;
    private TextView mTvChoosePicture, mTvChooseVideo;
    private Button mPictureCancel, mPictureOk;
    private RelativeLayout mRlPhotosLayout;
    private MagicProgress mFilterMpProgress;
    private AdjustSpecialEffectsView mAdjustSpecialEffectsView;
    private ImageView mImageAutoFocusRect;
    private View mLiveWindow;
    private DrawRect drawRect;

    private int mRecordType = Constants.RECORD_TYPE_PICTURE;
    private ImageView mPictureImage;
    private Bitmap mPictureBitmap;

    /**
     * 录制
     * Record
     */
    private ArrayList<Long> mRecordTimeList = new ArrayList<>();
    private ArrayList<String> mRecordFileList = new ArrayList<>();
    private long mEachRecodingVideoTime = 0, mEachRecodingImageTime = 4000000;
    private long mAllRecordingTime = 0;
    private String mCurRecordVideoPath;
    private NvAssetManager mAssetManager;
    /**
     * 默认前置摄像头
     * Default front-facing camera
     */
    private int mCurrentDeviceIndex = 1;
    private boolean mIsSwitchingCamera;
    NvsStreamingContext.CaptureDeviceCapability mCapability = null;
    private AlphaAnimation mFocusAnimation;

    /**
     * 变焦以及曝光dialog
     * Zoom and exposure dialog
     */
    private boolean m_supportAutoFocus;
    private TopMoreDialog mMoreDialog;

    private NvsCaptureAudioFx mNoiseAudioFx;
    private AudioNoisePop mAudioNoisePop;

    private VoicePop mVoicePop;
    /**
     * 内建的美颜效果
     * Built-in beauty effect
     */
    private NvsCaptureVideoFx mBeautyFx;
    /**
     * 是否初始化完成
     * Whether initialization is complete
     */
    private boolean initArScene;
    /**
     * 滤镜
     * filter view
     */
    private AlertDialog mFilterDialog, mMakeUpDialog;
    private FilterView mFilterBottomView;
    private MakeUpSingleView mMakeUpView;
    /**
     * 滤镜特效
     * fileter fx
     */
    private NvsCaptureVideoFx mCurCaptureVideoFx;
    private ArrayList<FilterItem> mFilterDataArrayList = new ArrayList<>();
    private int mFilterSelPos;
    private EffectInfo mCurrentFilterInfo;
    private VideoClipFxInfo mVideoClipFxInfo = new VideoClipFxInfo();

    /**
     * 道具-默认普通版，不带人脸功能
     * Props-default normal version, without face function
     */
    private int mCanUseARFaceType = HUMAN_AI_TYPE_NONE;
    private List<Props> mPropsList = new ArrayList<>();

    /**
     * 背景抠像特效
     * Background matting fx
     */
    private NvsCaptureVideoFx mBgSegEffect;
    private String mBgSegPackageId;
    /**
     * 美颜 美型 美妆 锐度 道具
     * beauty beautyShape beautyMakeUp Sharp Prop
     */
    private NvsCaptureVideoFx mArSceneFaceEffect;
    /**
     * 校色
     * Adjust color
     */
    private NvsCaptureVideoFx mAdjustColorFx;

    private int drawRectModel;
    private NvsCaptureAnimatedSticker currentAnimatedSticker;
    private long downTime;
    /**
     * 用来保存当前特效层级 贴纸和字幕同理，0为最上层
     * It is used to save the stickers of the current special effect level and the subtitles are the same. 0 is the top layer
     */
    List<AssetLevelBean> stickerVertices = new ArrayList<>();
    List<AssetLevelBean> captionVertices = new ArrayList<>();
    private NvsCaptureCompoundCaption currentCompoundCaption;
    private CaptionEditPop captionEditPop;
    private int selectCaptionIndex;
    private PointF downPointF;
    private long STICK_TIME_DURATION = 10 * 60 * 60 * Constants.NS_TIME_BASE;
    private int narHeight;
    private boolean showNavigation;
    private ParameterSettingValues parameterValues;
    private FragmentManager mFragmentManager;
    private TypeAndCategoryInfo mFilterTypeInfo;

    private TypeAndCategoryInfo mPropTypeInfo;
    /**
     * 组合字幕
     * Compound captionw
     */
    private TypeAndCategoryInfo mComponentTypeInfo;
    /**
     * 贴纸
     * sticker
     */
    private TypeAndCategoryInfo mStickerTypeInfo;

    private NvsEffectRenderCore mEffectRenderCore;
    /**
     * 开始预览时间
     * Start preview time
     */
    private long mStartPreviewTime;
    /**
     * 美颜模板
     * Beauty Template
     */
    private Fragment mBeautyCaptureFragment;

    private CaptureViewModel mCaptureViewModel;
    @Inject
    ViewModelProvider.Factory mViewModelProvider;
    /**
     * 磨皮数据
     * Peeling data
     */
    private List<IFxInfo> mBuffingSkinList;
    /**
     * 美型数据
     * American data
     */
    private List<IFxInfo> mShapeDataList;
    /**
     * 微整型数据
     * Microinteger data
     */
    private List<IFxInfo> mSmallShapeDataList;

    private Makeup preMakeUp;
    private NvsAssetPackageManager mAssetPackageManager;
    private boolean isPropContainShape;

    OrientationEventListener mOrientationListener;
    private int mOrientation;
    /**
     * 是否支持去油光
     * Whether it supports degreasing
     */
    private boolean mIsSupportMatte;
    private int mRecordState = RECORD_DEFAULT;

    private BeautyHelper mBeautyHelper;
    private MakeupHelper mMakeupHelper;
    private LinearLayout mSelectMusicLayout;
    private TextView mSelectMusicName;
    private ImageView cp_musicIcon;
    private MusicInfo mSelectMusicInfo;
    private TipsDialog mTipsDialog;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected int initRootView() {
        BarUtils.transparentStatusBar(this);
        mAssetManager = NvAssetManager.sharedInstance();
        mEffectRenderCore = mNvsEffectSdkContext.createEffectRenderCore();
        mAssetPackageManager = mStreamingContext.getAssetPackageManager();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        getWindow().setAttributes(params);
        mCaptureViewModel = ViewModelProviders.of(this).get(CaptureViewModel.class);
        return R.layout.activity_capture;
    }

    @Override
    protected void initViews() {
        mSelectMusicLayout = findViewById(R.id.cp_selectMusic);
        mSelectMusicName = findViewById(R.id.cp_musicName);
        cp_musicIcon = findViewById(R.id.cp_musicIcon);
        mCaptureRoot = findViewById(R.id.capture_root_layout);
        mIvExit = findViewById(R.id.iv_exit);
        mIvMore = findViewById(R.id.iv_more);
        mTvFrame = findViewById(R.id.tv_frame);

        mLlRightContainer = findViewById(R.id.ll_right_container);
        mIvChangeCamera = findViewById(R.id.iv_rollover);
        TextView mSticker = findViewById(R.id.iv_sticker);
        TextView mComcaption = findViewById(R.id.iv_com_caption);
        mSegment = findViewById(R.id.segment);
        mBackgroundSegment = findViewById(R.id.background_segment);
        mAudioNoise = findViewById(R.id.audio_noise);
        mCaptureVoice = findViewById(R.id.capture_voice);
        mTvVoice = findViewById(R.id.tv_voice);

        mFlMiddleParent = findViewById(R.id.fl_middle_parent);
        mDelete = findViewById(R.id.iv_back_delete);
        mVideoTimeDot = findViewById(R.id.v_timing_dot);
        mRecordTime = findViewById(R.id.tv_timing_num);
        mNext = findViewById(R.id.iv_confirm);


        mFlBottomParent = findViewById(R.id.fl_bottom_parent);
        mCaptureMakeup = findViewById(R.id.capture_makeup);
        mCaptureBeauty = findViewById(R.id.capture_beauty);
        mCaptureFilter = findViewById(R.id.capture_filter);
        mCaptureProp = findViewById(R.id.capture_prop);

        mFlStartRecord = findViewById(R.id.fl_take_photos);
        mIvTakePhotoBg = findViewById(R.id.iv_take_photo);
        mStartText = findViewById(R.id.tv_video_num);

        mRecordTypeLayout = findViewById(R.id.ll_chang_pv);
        mTvChoosePicture = findViewById(R.id.tv_take_photos);
        mTvChooseVideo = findViewById(R.id.tv_take_video);

        mRlPhotosLayout = findViewById(R.id.rl_photos_container);
        mPictureCancel = findViewById(R.id.bt_delete_photos);
        mPictureOk = findViewById(R.id.bt_save_photos);
        mPictureImage = findViewById(R.id.iv_photos);
        mFilterMpProgress = findViewById(R.id.mp_filter);
        mAdjustSpecialEffectsView = findViewById(R.id.adjustSpecialEffectsView);
        mImageAutoFocusRect = findViewById(R.id.iv_focus);
        mTipsDialog = new TipsDialog(this);
        //兼容华为4A的 华为4A则使用liveWindow代替liveWindowExt
        //Huawei 4A compatible with Huawei 4A uses liveWindow instead of liveWindowExt
        if (Util.isHUAWEI4A()) {
            mLiveWindow = findViewById(R.id.lw_window);
        } else {
            mLiveWindow = findViewById(R.id.lw_windowExt);
        }
        mLiveWindow.setVisibility(View.VISIBLE);
        drawRect = findViewById(R.id.capture_draw_rect);

        initTopMoreView();
        initChangeVoiceView();
        mFilterBottomView = new FilterView(mContext);
        initFilterProgress();
        initAudioNoisePop();

        mSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStickerFx();
            }
        });

        mComcaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showComponentCaptionFx();
            }
        });

        mSegment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = mSegment.getText().toString();
                if (getString(R.string.bg_seg_color).equals(content)) {
                    mSegment.setText(getString(R.string.bg_seg_color_cancel));
                    mBackgroundSegment.setVisibility(View.VISIBLE);
                    doBgSeg(true);
                } else {
                    mSegment.setText(getString(R.string.bg_seg_color));
                    mBackgroundSegment.setVisibility(View.GONE);
                    doBgSeg(false);
                }

            }
        });
        mBackgroundSegment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NvAsset asset = mAssetManager.installAssetPackage(Constants.BG_SEGMENT_CAPTURE_SCENE_PATH, NvAsset.ASSET_CAPTURE_SCENE, false);
                if (null != asset) {
                    mBgSegPackageId = asset.uuid;
                }
                gotoSelectPictures();
            }
        });
        mAudioNoise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == mAudioNoisePop) {
                    return;
                }
                changeCaptureDisplay(false);
                mAudioNoisePop.show();
            }
        });
        drawRect.setStickerMuteListenser(new DrawRect.onStickerMuteListenser() {
            @Override
            public void onStickerMute() {
                if (currentAnimatedSticker != null && drawRectModel == Constants.EDIT_MODE_STICKER) {
                    float volumeGain = currentAnimatedSticker.getVolumeGain().leftVolume;
                    volumeGain = volumeGain == 0 ? 1 : 0;
                    currentAnimatedSticker.setVolumeGain(volumeGain, volumeGain);
                    drawRect.setStickerMuteIndex((int) volumeGain);
                }
            }
        });
        drawRect.setOnTouchListener(new DrawRect.OnTouchListener() {
            @Override
            public void onDrag(PointF prePointF, PointF nowPointF) {

                PointF pre = null;
                PointF p = null;
                if (mLiveWindow instanceof MSLiveWindow) {
                    pre = ((MSLiveWindow) mLiveWindow).mapViewToCanonical(prePointF);
                    p = ((MSLiveWindow) mLiveWindow).mapViewToCanonical(nowPointF);
                } else if (mLiveWindow instanceof MSLiveWindowExt) {
                    p = ((MSLiveWindowExt) mLiveWindow).mapViewToCanonical(nowPointF);
                    pre = ((MSLiveWindowExt) mLiveWindow).mapViewToCanonical(prePointF);
                }
                PointF timeLinePointF = new PointF(p.x - pre.x, p.y - pre.y);
                if (drawRectModel == Constants.EDIT_MODE_STICKER) {
                    if (currentAnimatedSticker != null) {
                        currentAnimatedSticker.translateAnimatedSticker(timeLinePointF);
                        updateDrawRectPosition(getAssetViewVerticesList(currentAnimatedSticker.getHorizontalFlip(), currentAnimatedSticker.getBoundingRectangleVertices())
                                , Constants.EDIT_MODE_STICKER, (String) currentAnimatedSticker.getAttachment(Constants.KEY_LEVEL), null);
                    }
                } else if (drawRectModel == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                    if (currentCompoundCaption != null) {
                        currentCompoundCaption.translateCaption(timeLinePointF);
                        List<PointF> assetViewVerticesList = getAssetViewVerticesList(
                                false, currentCompoundCaption.getCompoundBoundingVertices(NvsTimelineCompoundCaption.BOUNDING_TYPE_FRAME));
                        List<List<PointF>> captions = getCaptionList(currentCompoundCaption);
                        updateDrawRectPosition(assetViewVerticesList, Constants.EDIT_MODE_COMPOUND_CAPTION,
                                (String) currentCompoundCaption.getAttachment(Constants.KEY_LEVEL), captions);
                    }
                }
            }

            @Override
            public void onScaleAndRotate(float scaleFactor, PointF anchor, float rotation) {
                PointF assetAnchor = null;
                if (mLiveWindow instanceof MSLiveWindow) {
                    assetAnchor = ((MSLiveWindow) mLiveWindow).mapViewToCanonical(anchor);
                } else if (mLiveWindow instanceof MSLiveWindowExt) {
                    assetAnchor = ((MSLiveWindowExt) mLiveWindow).mapViewToCanonical(anchor);
                }
                //map方法没有计算liveWindow的偏移量
                //The map method does not calculate the offset of liveWindow
                if (drawRectModel == Constants.EDIT_MODE_STICKER) {
                    if (currentAnimatedSticker != null) {
                        currentAnimatedSticker.scaleAnimatedSticker(scaleFactor, assetAnchor);
                        /*
                         * 旋转贴纸
                         * Rotate stickers
                         * */
                        currentAnimatedSticker.rotateAnimatedSticker(rotation);
                        updateDrawRectPosition(getAssetViewVerticesList(currentAnimatedSticker.getHorizontalFlip(), currentAnimatedSticker.getBoundingRectangleVertices())
                                , Constants.EDIT_MODE_STICKER, (String) currentAnimatedSticker.getAttachment(Constants.KEY_LEVEL), null);
                    }
                } else if (drawRectModel == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                    if (currentCompoundCaption != null) {
                        currentCompoundCaption.scaleCaption(scaleFactor, assetAnchor);
                        currentCompoundCaption.rotateCaption(rotation, assetAnchor);
                        List<PointF> assetViewVerticesList = getAssetViewVerticesList(
                                false, currentCompoundCaption.getCompoundBoundingVertices(NvsTimelineCompoundCaption.BOUNDING_TYPE_FRAME));
                        List<List<PointF>> captions = getCaptionList(currentCompoundCaption);
                        updateDrawRectPosition(assetViewVerticesList, Constants.EDIT_MODE_COMPOUND_CAPTION,
                                (String) currentCompoundCaption.getAttachment(Constants.KEY_LEVEL), captions);
                    }
                }
            }

            @Override
            public void onScaleXandY(float xScaleFactor, float yScaleFactor, PointF anchor) {

            }

            @Override
            public void onDel() {
                downPointF = null;
                deleteCaptionOrSticker();
            }

            @Override
            public void onTouchDown(PointF curPoint) {
                downPointF = new PointF(curPoint.x, curPoint.y);
                downTime = System.currentTimeMillis();
            }

            @Override
            public void onAlignClick(boolean isHorizontal) {

            }

            @Override
            public void onOrientationChange(boolean isHorizontal) {
            }

            @Override
            public void onHorizontalFlipClick() {
                downPointF = null;
                changeHorizontalFlip();

            }

            @Override
            public void onBeyondDrawRectClick() {
            }

            @Override
            public void onTouchUp(PointF pointF) {
                long duration = System.currentTimeMillis() - downTime;
                //判断是否是点击事件 排除move drag等影响
                //Judge whether it is a click event to eliminate the impact of move drag
                if (Util.isClickTouch(downPointF, pointF, duration)) {
                    clearSelectState();
                    if (!checkTouchAssetFrame(downPointF)) {
                        if (drawRect.getVisibility() == View.VISIBLE) {
                            drawRect.setVisibility(View.GONE);
                            currentAnimatedSticker = null;
                            currentCompoundCaption = null;
                        }
                    }
                }

            }
        });
        mSelectMusicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAllRecordingTime > 0) {
                    return;
                }
                Bundle musicBundle = new Bundle();
                musicBundle.putInt(Constants.SELECT_MUSIC_FROM, Constants.SELECT_MUSIC_FROM_EDIT);
                musicBundle.putBoolean(Constants.SELECT_MUSIC_HAVE, mSelectMusicInfo != null);
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                        SelectMusicActivity.class,
                        musicBundle,
                        Constants.ACTIVITY_START_CODE_MUSIC_SINGLE);
            }
        });
    }

    @Override
    protected void initTitle() {

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initData() {
        MSBus.getInstance().register(this);
        mFragmentManager = getSupportFragmentManager();
        mHandler = new CaptureHandler(this);
        parameterValues = ParameterSettingValues.instance();
        BarUtils.isNavigationBarExist(this, new BarUtils.OnNavigationStateListener() {
            @Override
            public void onNavigationState(boolean isShowing, int nHeight) {
                showNavigation = isShowing;
                narHeight = nHeight;
            }
        });

        mBuffingSkinList = BeautyDataManager.getBuffingSkin(mContext);
        mShapeDataList = BeautyDataManager.getBeautyShapeList(mContext);
        mSmallShapeDataList = BeautyDataManager.getMicroPlasticList(mContext);
        changeAspectRatio();
        initCaptureData();
        initCapture();
        initBeautyFx();
        searchAssetData();
        initBeautyTemplate();
        initFilterList();
        initFilterDialog();
        initMakeupDialog();
//        initCaptionFontInfoList();
        initViewModelObserver();
        mOrientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (mOrientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                    return;
                }
                /*
                 * 只检测是否有四个角度的改变
                 * Only detect if there are four angle changes
                 */
                if (orientation > 350 || orientation < 10) {
                    mOrientation = 0;
                } else if (orientation > 80 && orientation < 100) {
                    mOrientation = 90;
                } else if (orientation > 170 && orientation < 190) {
                    mOrientation = 180;
                } else if (orientation > 260 && orientation < 280) {
                    mOrientation = 270;
                }
            }
        };

        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        } else {
            mOrientationListener.disable();
        }
    }

    private void initBeautyTemplate() {
        mBeautyCaptureFragment = mFragmentManager.
                findFragmentByTag(Constants.FRAGMENT_BEAUTY_TAG);
        if (mBeautyCaptureFragment == null) {
            mBeautyCaptureFragment = BeautyCaptureFragment.newInstance(mIsSupportMatte, mMakeupHelper, mBeautyHelper);
        }
        if (mBeautyCaptureFragment instanceof BeautyCaptureFragment) {
            ((BeautyCaptureFragment) mBeautyCaptureFragment).initAssetsData(this, false);
        }
    }

    /**
     * 初始化音频降噪弹框
     * Initialize audio noise reduction pop-up
     */
    private void initAudioNoisePop() {
        mAudioNoisePop = AudioNoisePop.create(this, new AudioNoisePop.OnAudioNoisePopListener() {
            @Override
            public void onAudioNoiseLevel(int level) {
                if (level == 0) {
                    mStreamingContext.removeCaptureAudioFx(mNoiseAudioFx.getIndex());
                    mNoiseAudioFx = null;
                    return;
                }
                if (null == mNoiseAudioFx) {
                    mNoiseAudioFx = mStreamingContext.appendBuiltinCaptureAudioFx(Constants.NOISE_SUPPRESSION_KEY);
                }
                mNoiseAudioFx.setIntVal(Constants.NOISE_SUPPRESSION_VALUE_KEY, level);
            }

            @Override
            public void onDismiss() {
                changeCaptureDisplay(true);
            }
        });
    }

    @MSSubscribe(Constants.SubscribeType.SUB_ADD_CUSTOM_STICKER_TYPE)
    public void onAddCustomSticker() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_IMAGE_FROM_CUSTOM_STICKER);
        AppManager.getInstance().jumpActivity(AppManager.getInstance().currentActivity(), SingleClickActivity.class, bundle);
    }


    private void initFilterProgress() {
        mFilterMpProgress.setMax(100);
        mFilterMpProgress.setPointEnable(true);
        mFilterMpProgress.setBreakProgress(0);
        mFilterMpProgress.setShowBreak(false);
        mFilterMpProgress.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {

            @Override
            public void onProgressChange(int progress, boolean fromUser) {
                changeFilterIntensity(progress, fromUser);
            }

            @Override
            public void onProgressChangeFinish(int progress, boolean fromUser) {
                Fragment fragmentByTag = mFragmentManager.findFragmentByTag(FRAGMENT_FILTER_TAG);
                if (fragmentByTag instanceof CaptureFilterFragment) {
                    CaptureFilterFragment captureFilterFragment = (CaptureFilterFragment) fragmentByTag;
                    captureFilterFragment.enableChangedItem(true);
                }
            }

            @Override
            public void onProgressChangeStarted(int progress, boolean fromUser) {
                Fragment fragmentByTag = mFragmentManager.findFragmentByTag(FRAGMENT_FILTER_TAG);
                if (fragmentByTag instanceof CaptureFilterFragment) {
                    CaptureFilterFragment captureFilterFragment = (CaptureFilterFragment) fragmentByTag;
                    captureFilterFragment.enableChangedItem(false);
                }
            }
        });
    }

    /**
     * 删除当前选中的贴纸或者复合字幕
     * Delete the currently selected sticker or composite caption
     */
    private void deleteCaptionOrSticker() {
        if (drawRectModel == Constants.EDIT_MODE_STICKER) {
            if (currentAnimatedSticker != null) {
                String kValue = (String) currentAnimatedSticker.getAttachment(Constants.KEY_LEVEL);
                int count = mStreamingContext.getCaptureAnimatedStickerCount();
                for (int i = 0; i < count; i++) {
                    NvsCaptureAnimatedSticker animatedSticker = mStreamingContext.getCaptureAnimatedStickerByIndex(i);
                    if (!TextUtils.isEmpty(kValue) && animatedSticker != null
                            && TextUtils.equals(kValue, (String) animatedSticker.getAttachment(Constants.KEY_LEVEL))) {
                        for (AssetLevelBean captionVertex : stickerVertices) {
                            if (TextUtils.equals(kValue, captionVertex.getTag())) {
                                stickerVertices.remove(captionVertex);
                                break;
                            }
                        }
                        mStreamingContext.removeCaptureAnimatedSticker(i);
                        drawRect.setVisibility(View.GONE);
                        currentAnimatedSticker = null;
                        break;
                    }
                }
                if (!stickerVertices.isEmpty()) {
                    checkSelectByAttachment(stickerVertices.get(0).getTag());
                }
            }

        } else if (drawRectModel == Constants.EDIT_MODE_COMPOUND_CAPTION) {
            if (currentCompoundCaption != null) {
                String kValue = (String) currentCompoundCaption.getAttachment(Constants.KEY_LEVEL);
                int count = mStreamingContext.getCaptureCompoundCaptionCount();
                for (int i = 0; i < count; i++) {
                    NvsCaptureCompoundCaption compoundCaption = mStreamingContext.getCaptureCompoundCaptionByIndex(i);
                    if (!TextUtils.isEmpty(kValue) && compoundCaption != null
                            && TextUtils.equals(kValue, (String) compoundCaption.getAttachment(Constants.KEY_LEVEL))) {
                        for (AssetLevelBean captionVertex : captionVertices) {
                            if (TextUtils.equals(kValue, captionVertex.getTag())) {
                                captionVertices.remove(captionVertex);
                                break;
                            }
                        }
                        mStreamingContext.removeCaptureCompoundCaption(i);
                        drawRect.setVisibility(View.GONE);
                        currentCompoundCaption = null;
                        break;
                    }
                }
                if (captionVertices.size() > 0) {
                    checkSelectByAttachment(captionVertices.get(0).getTag());
                }
            }
        }
    }

    /**
     * 清除选中信息
     * Clear selected information
     */
    private void clearSelectState() {
        MSBus.getInstance().post(Constants.SubscribeType.SUB_UN_SELECT_ITEM_TYPE);
    }

    /**
     * 点击翻转(目前仅贴纸生效)
     * Click Flip (only stickers take effect at present)
     */
    private void changeHorizontalFlip() {
        if (drawRectModel == Constants.EDIT_MODE_STICKER) {
            if (currentAnimatedSticker != null) {
                currentAnimatedSticker.setHorizontalFlip(!currentAnimatedSticker.getHorizontalFlip());
                updateDrawRectPosition(getAssetViewVerticesList(currentAnimatedSticker.getHorizontalFlip(), currentAnimatedSticker.getBoundingRectangleVertices())
                        , Constants.EDIT_MODE_STICKER, (String) currentAnimatedSticker.getAttachment(Constants.KEY_LEVEL), null);
            }
        }
    }

    /**
     * 点击点检查是否点击到特效上
     * Click to check whether it is clicked on the special effect
     *
     * @param curPoint PointF
     */
    private boolean checkTouchAssetFrame(final PointF curPoint) {
        Point point = new Point((int) curPoint.x, (int) curPoint.y);
        if (drawRectModel == Constants.EDIT_MODE_STICKER) {
            return checkSelectByAttachment(checkTouchAttachment(stickerVertices, point));
        } else if (drawRectModel == Constants.EDIT_MODE_COMPOUND_CAPTION) {
            return checkSelectCaption(captionVertices, point);
        }
        return false;
    }

    /**
     * 选中组合字幕的单个字幕进行修改
     * Select single subtitle of combined subtitles to modify
     *
     * @param vertices
     * @param point
     */
    private boolean checkSelectCaption(List<AssetLevelBean> vertices, Point point) {
        String oldKey = "";
        if (currentCompoundCaption != null) {
            oldKey = (String) currentCompoundCaption.getAttachment(Constants.KEY_LEVEL);
        }
        String attachment = checkTouchAttachment(vertices, point);
        if (TextUtils.isEmpty(attachment)) {
            return false;
        }
        checkSelectByAttachment(attachment);
        boolean showKeyboard = false;
        if (!TextUtils.isEmpty(oldKey) && TextUtils.equals(oldKey, attachment)) {
            showKeyboard = true;
        }
        selectCaptionIndex = -1;
        if (currentCompoundCaption != null) {
            int captionCount = currentCompoundCaption.getCaptionCount();
            for (int i = 0; i < captionCount; i++) {
                List<PointF> captionBoundingVertices = currentCompoundCaption.getCaptionBoundingVertices(i, NvsCaption.BOUNDING_TYPE_TEXT);
                List<PointF> assetViewVerticesList = getAssetViewVerticesList(false, captionBoundingVertices);
                if (checkTouchInPath(assetViewVerticesList, point)) {
                    selectCaptionIndex = i;
                    break;
                }
            }
        }
        if (showKeyboard && selectCaptionIndex >= 0) {
            showCaptionKeyBoard();
        }
        return true;
    }


    /**
     * 抠像背景跳转选择图片
     * jump to select the photos
     */
    private void gotoSelectPictures() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_PICTURE_FROM_BACKGROUND_SEG);
        AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                SingleClickActivity.class, bundle, REQUEST_CODE_BACKGROUND_SEG);
    }


    /**
     * 更新贴纸drawRect位置
     * Update sticker drawRect position
     *
     * @param pointFS
     * @param model
     */
    private void updateDrawRectPosition(List<PointF> pointFS, int model, String attachment, List<List<PointF>> captions) {
        if (model == Constants.EDIT_MODE_STICKER) {
            drawRect.setDrawRect(pointFS, model);
            for (AssetLevelBean stickerVertex : stickerVertices) {
                if (TextUtils.equals(stickerVertex.getTag(), attachment)) {
                    stickerVertex.setData(pointFS);
                    break;
                }
            }
        } else if (model == Constants.EDIT_MODE_COMPOUND_CAPTION) {
            drawRect.setCompoundDrawRect(pointFS, captions, model);
            for (AssetLevelBean captionVertex : captionVertices) {
                if (TextUtils.equals(captionVertex.getTag(), attachment)) {
                    captionVertex.setData(pointFS);
                    break;
                }
            }
        }
        drawRect.setVisibility(View.VISIBLE);
    }

    /**
     * 转换点位
     * Conversion point
     *
     * @param horizontal   //数据翻转 Data flipping
     * @param verticesList
     * @return
     */
    private List<PointF> getAssetViewVerticesList(boolean horizontal, List<PointF> verticesList) {
        List<PointF> newList = new ArrayList<>();
        for (int i = 0; i < verticesList.size(); i++) {
            if (mLiveWindow instanceof MSLiveWindow) {
                PointF pointF = ((MSLiveWindow) mLiveWindow).mapCanonicalToView(verticesList.get(i));
                newList.add(pointF);
            } else if (mLiveWindow instanceof MSLiveWindowExt) {
                PointF pointF = ((MSLiveWindowExt) mLiveWindow).mapCanonicalToView(verticesList.get(i));
                newList.add(pointF);
            }
        }
        if (horizontal) {
            /*
             * 如果已水平翻转，需要对顶点数据进行处理
             * If flipped horizontally, you need to process the vertex data
             * */
            Collections.swap(newList, 0, 3);
            Collections.swap(newList, 1, 2);
        }
        return newList;
    }

    private void showStickerFx() {
        drawRectModel = Constants.EDIT_MODE_STICKER;
        showCommonFragment(mStickerTypeInfo, Constants.FRAGMENT_STICKER_TAG);
        changeCaptureDisplay(false);
        if (stickerVertices.size() > 0) {
            checkSelectByAttachment(stickerVertices.get(0).getTag());
        }
    }

    private void closeStickerFx() {
        if (isEffectFragmentVisible(Constants.FRAGMENT_STICKER_TAG)) {
            hideEffectFragment(Constants.FRAGMENT_STICKER_TAG);
            changeCaptureDisplay(true);
            drawRectModel = -1;
        }
    }

    /**
     * 应用自定义贴纸
     * Add custom stickers
     *
     * @param nvCustomStickerInfo
     */
    @MSSubscribe(Constants.SubscribeType.SUB_APPLY_CUSTOM_STICKER_TYPE)
    private void applyCustomSticker(NvAssetManager.NvCustomStickerInfo nvCustomStickerInfo) {
        String imageSrcFilePath = nvCustomStickerInfo.imagePath;
        int lastPointPos = imageSrcFilePath.lastIndexOf(".");
        String fileSuffixName = imageSrcFilePath.substring(lastPointPos).toLowerCase();
        if (".gif".equals(fileSuffixName)) {//gif
            String targetCafPath = nvCustomStickerInfo.targetImagePath;
            File targetCafFile = new File(targetCafPath);
            if (targetCafFile.exists()) {
                /*
                 * 检测目标caf文件是否存在
                 * Detect the existence of the target caf file
                 * */
                addCustomAnimateSticker(nvCustomStickerInfo, targetCafPath);
            }
        } else {//image
            addCustomAnimateSticker(nvCustomStickerInfo, nvCustomStickerInfo.imagePath);
        }

    }

    private void addCustomAnimateSticker(NvAssetManager.NvCustomStickerInfo nvCustomStickerInfo, String filePath) {
        currentAnimatedSticker =
                mStreamingContext.addCustomCaptureAnimatedSticker(0, STICK_TIME_DURATION,
                        nvCustomStickerInfo.templateUuid, filePath);
        if (currentAnimatedSticker != null) {
//            currentAnimatedSticker.setScale(0.5f);
            List<PointF> assetViewVerticesList = getAssetViewVerticesList(currentAnimatedSticker.getHorizontalFlip(),
                    currentAnimatedSticker.getBoundingRectangleVertices());
            AssetLevelBean levelBean = new AssetLevelBean(assetViewVerticesList);
            stickerVertices.add(0, levelBean);
            currentAnimatedSticker.setAttachment(Constants.KEY_LEVEL, levelBean.getTag());
            //如果贴纸需要打开声音图标 放开此行代码
            //If the sticker needs to open the sound icon, release this line of code
//            drawRect.setMuteVisible(currentAnimatedSticker.hasAudio());
            updateDrawRectPosition(assetViewVerticesList, Constants.EDIT_MODE_STICKER, levelBean.getTag(), null);
        }
    }

    @MSSubscribe(Constants.SubscribeType.SUB_APPLY_STICKER_TYPE)
    private void applySticker(String uuid) {
        if (uuid == null) {
            return;
        }
       /* if (Util.isFastClick()) {
            return;
        }*/
        currentAnimatedSticker = mStreamingContext.appendCaptureAnimatedSticker(0, STICK_TIME_DURATION, uuid);
        if (currentAnimatedSticker != null) {
//            currentAnimatedSticker.setScale(0.5f);
            List<PointF> assetViewVerticesList = getAssetViewVerticesList(currentAnimatedSticker.getHorizontalFlip(), currentAnimatedSticker.getBoundingRectangleVertices());
            AssetLevelBean levelBean = new AssetLevelBean(assetViewVerticesList);
            stickerVertices.add(0, levelBean);
            currentAnimatedSticker.setAttachment(Constants.KEY_LEVEL, levelBean.getTag());
//            drawRect.setMuteVisible(currentAnimatedSticker.hasAudio());
            updateDrawRectPosition(assetViewVerticesList, Constants.EDIT_MODE_STICKER, levelBean.getTag(), null);
        }
    }

    private void showComponentCaptionFx() {
        drawRectModel = Constants.EDIT_MODE_COMPOUND_CAPTION;
        showCommonFragment(mComponentTypeInfo, Constants.FRAGMENT_COMPONENT_CAPTION_TAG);
        if (captionVertices.size() > 0) {
            checkSelectByAttachment(captionVertices.get(0).getTag());
        }
        changeCaptureDisplay(false);
    }

    private void closeCaptionFx() {
        if (isEffectFragmentVisible(Constants.FRAGMENT_COMPONENT_CAPTION_TAG)) {
            drawRectModel = -1;
            hideEffectFragment(Constants.FRAGMENT_COMPONENT_CAPTION_TAG);
            changeCaptureDisplay(true);
        }
    }

    private boolean isEffectFragmentVisible(String tag) {
        if (null != mFragmentManager) {
            Fragment fragmentByTag = mFragmentManager.findFragmentByTag(tag);
            if (null != fragmentByTag) {
                return fragmentByTag.isVisible();
            }
        }
        return false;
    }

    private void showCaptionKeyBoard() {
        if (captionEditPop == null) {
            captionEditPop = CaptionEditPop.create(this);
            captionEditPop.setEventListener(new CaptionEditPop.EventListener() {
                @Override
                public void onConfirm(String text, String textColor, NvAsset fontAsset) {
                    if (currentCompoundCaption != null && selectCaptionIndex >= 0) {
                        if (textColor != null) {
                            currentCompoundCaption.setTextColor(selectCaptionIndex, ColorUtil.colorStringtoNvsColor(textColor));
                        }
                        if (fontAsset != null) {
                            currentCompoundCaption.setFontFamily(selectCaptionIndex, fontAsset.name);
                        }
                        currentCompoundCaption.setText(selectCaptionIndex, text);
                    }
                }
            });
        }
        if (captionEditPop != null && !captionEditPop.isShow()) {
            captionEditPop.resetSign();
            captionEditPop.setNarBar(showNavigation, narHeight);
            if (currentCompoundCaption != null) {
                captionEditPop.setCaptionText(currentCompoundCaption.getText(selectCaptionIndex));
                captionEditPop.setCaptionTextColor(currentCompoundCaption.getTextColor(selectCaptionIndex));
                captionEditPop.setCaptionFont(currentCompoundCaption.getFontFamily(selectCaptionIndex));
            }
            captionEditPop.show();
        }
    }

    @MSSubscribe(Constants.SubscribeType.SUB_APPLY_COMPONENT_CAPTION_TYPE)
    private void applyCaption(String effectId) {
        if (TextUtils.isEmpty(effectId)) {
            return;
        }

        if (Util.isFastClick()) {
            return;
        }
        currentCompoundCaption = mStreamingContext.appendCaptureCompoundCaption(0, STICK_TIME_DURATION, effectId);
        if (currentCompoundCaption != null) {
            currentCompoundCaption.setScaleX(0.5f);
            currentCompoundCaption.setScaleY(0.5f);
            List<PointF> assetViewVerticesList = getAssetViewVerticesList(
                    false, currentCompoundCaption.getCompoundBoundingVertices(NvsTimelineCompoundCaption.BOUNDING_TYPE_FRAME));
            List<List<PointF>> captions = getCaptionList(currentCompoundCaption);
            AssetLevelBean levelBean = new AssetLevelBean(assetViewVerticesList);
            captionVertices.add(0, levelBean);
            currentCompoundCaption.setAttachment(Constants.KEY_LEVEL, levelBean.getTag());
            drawRect.setMuteVisible(false);
            updateDrawRectPosition(assetViewVerticesList, Constants.EDIT_MODE_COMPOUND_CAPTION, levelBean.getTag(), captions);
        }
    }

    private List<List<PointF>> getCaptionList(NvsCaptureCompoundCaption compoundCaption) {
        List<List<PointF>> captions = new ArrayList<>();
        if (compoundCaption != null) {
            int captionCount = compoundCaption.getCaptionCount();
            for (int i = 0; i < captionCount; i++) {
                List<PointF> pointFS = compoundCaption.getCaptionBoundingVertices(i, NvsTimelineCompoundCaption.BOUNDING_TYPE_TEXT);
                captions.add(getAssetViewVerticesList(false, pointFS));
            }
        }
        return captions;
    }


    private void showArSceneFx() {
        if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
            if (initArScene) {
                changeCaptureDisplay(false);
                showCommonFragment(mPropTypeInfo, Constants.FRAGMENT_PROP_TAG);
            } else {
                /*
                 * 授权过期
                 * License expired
                 */
                String[] versionName = getResources().getStringArray(R.array.sdk_expire_tips);
                Util.showDialog(CaptureActivity.this, versionName[0], versionName[1]);
            }
        } else {
            String[] versionName = getResources().getStringArray(R.array.sdk_version_tips);
            Util.showDialog(CaptureActivity.this, versionName[0], versionName[1]);
        }
    }


    /**
     * 初始化变声View
     * init change voice view
     */
    private void initChangeVoiceView() {
        mVoicePop = VoicePop.create(this, mCaptureViewModel.getVoiceDatas(), new VoicePop.OnVoicePopListener() {
            @Override
            public void onDismiss() {
                changeCaptureDisplay(true);
            }

            @Override
            public void onAudioNoiseLevel(ChangeVoiceData voiceData) {
                if (voiceData != null) {
                    if (TextUtils.equals(voiceData.getName(), getResources().getString(R.string.timeline_fx_none))) {
                        mTvVoice.setText(getResources().getString(R.string.change_voice));
                    } else {
                        mTvVoice.setText(voiceData.getName());
                    }
                    if (mStreamingContext != null) {
                        mStreamingContext.removeAllCaptureAudioFx();
                        mStreamingContext.appendBuiltinCaptureAudioFx(voiceData.getVoiceId());
                    }
                }
            }
        });
    }

    private void initTopMoreView() {
        if (mMoreDialog == null) {
            mMoreDialog = TopMoreDialog.create(this, mStreamingContext);
            mMoreDialog.setEventListener(new TopMoreDialog.EventListener() {
                @Override
                public void onDismiss() {

                }

                @Override
                public void onDialogCancel() {
                }

                @Override
                public void onFrameClick(boolean frameFlag) {
                    if (frameFlag) {
                        setCaptureFrame();
                    } else {
                        closeCaptureFrame();
                    }
                }
            });
        }
    }


    @MSSubscribe(value = {Constants.SubscribeType.SUB_APPLY_FILTER_TYPE})
    private void applyFilter(String packageId) {
        removeAllFilterFx();
        if (!TextUtils.isEmpty(packageId)) {
            if (null != mCurCaptureVideoFx) {
                mStreamingContext.removeCaptureVideoFx(mCurCaptureVideoFx.getIndex());
            }
            mCurCaptureVideoFx = mStreamingContext.appendPackagedCaptureVideoFx(packageId);
            float strength = 1.0F;
            if (mCurCaptureVideoFx != null) {
                mCurCaptureVideoFx.setFilterIntensity(strength);
            }
            showFilterSeekViewVisible(packageId, strength);
        }
    }

    @MSSubscribe(value = {Constants.SubscribeType.SUB_APPLY_FILTER_INFO_TYPE})
    private void applyFilter(EffectInfo filterInfo) {
        if (null == filterInfo) {
            return;
        }
        String packageId = filterInfo.getId();
        if (TextUtils.isEmpty(packageId)) {
            return;
        }
        removeAllFilterFx();
        mCurrentFilterInfo = filterInfo;
        if (!TextUtils.isEmpty(packageId)) {
            mCurCaptureVideoFx = mStreamingContext.appendPackagedCaptureVideoFx(packageId);
            if ((filterInfo.getIsAdjusted() == 0) && (filterInfo.getKind() == 8 || filterInfo.getKind() == 9)) {
                mAdjustSpecialEffectsView.setVisibility(View.GONE);
                mFilterMpProgress.setVisibility(View.GONE);
                return;
            }
            float strength = filterInfo.getStrength();
            if (filterInfo.getStrength() == 0) {
                strength = filterInfo.getDefaultStrength();
            }
            if ((mCurCaptureVideoFx != null) && (filterInfo.getIsAdjusted() == 0)) {
                mCurCaptureVideoFx.setFilterIntensity(strength);
            }
            showFilterSeekViewVisible(packageId, strength);
        }
    }

    private void showFilterSeekViewVisible(String packageId, float strength) {
        List<NvsExpressionParam> expValueList = mStreamingContext.getAssetPackageManager().getExpValueList(packageId,
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX);
        Hashtable<String, String> hashtable = mStreamingContext.getAssetPackageManager().getTranslationMap(packageId,
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX);
        if ((null == expValueList) || expValueList.isEmpty() || (null == hashtable)) {
            mAdjustSpecialEffectsView.setVisibility(View.GONE);
            mFilterMpProgress.setVisibility(View.VISIBLE);
            mFilterMpProgress.setProgress((int) (strength * 100));
            return;
        }
        mAdjustSpecialEffectsView.setVisibility(View.VISIBLE);
        mFilterMpProgress.setVisibility(View.GONE);
        List<AdjustSpecialEffectsInfo> dataList = new ArrayList<>();
        AdjustSpecialEffectsInfo adjustSpecialEffectsInfo = null;
        String name = "";
        String key = "";
        for (NvsExpressionParam nvsExpressionParam : expValueList) {
            if (null == nvsExpressionParam) {
                continue;
            }
            int type = nvsExpressionParam.getType();
            adjustSpecialEffectsInfo = new AdjustSpecialEffectsInfo();
            key = nvsExpressionParam.getName();
            name = hashtable.get(key);
            adjustSpecialEffectsInfo.setPackageId(packageId);
            adjustSpecialEffectsInfo.setAdjustmentCategoryName(name);
            adjustSpecialEffectsInfo.setKey(key);
            adjustSpecialEffectsInfo.setType(type);

            switch (type) {
                case NvsExpressionParam.TYPE_COLOR:
                    NvsColor nvsColor = nvsExpressionParam.getColor();
                    if (null != nvsColor) {
                        adjustSpecialEffectsInfo.setColor(nvsColor);
                        if ((mCurCaptureVideoFx != null)) {
                            mCurCaptureVideoFx.setExprObjectVar(key, nvsColor);
                        }
                    }
                    break;
                case NvsExpressionParam.TYPE_FLOAT:
                    NvsExpressionParam.FloatParam floatParam = nvsExpressionParam.getFloatParam();
                    if (null != floatParam) {
                        adjustSpecialEffectsInfo.setDefVal(floatParam.getDefVal());
                        adjustSpecialEffectsInfo.setMaxVal(floatParam.getMaxVal());
                        adjustSpecialEffectsInfo.setMinVal(floatParam.getMinVal());
                        adjustSpecialEffectsInfo.setStrength(floatParam.getDefVal());
                        if ((mCurCaptureVideoFx != null)) {
                            mCurCaptureVideoFx.setExprVar(key, floatParam.getDefVal());
                        }
                    }
                    break;
                case NvsExpressionParam.TYPE_INT:
                    NvsExpressionParam.IntParam intParam = nvsExpressionParam.getIntParam();
                    if (null != intParam) {
                        adjustSpecialEffectsInfo.setDefVal(intParam.getDefVal());
                        adjustSpecialEffectsInfo.setMaxVal(intParam.getMaxVal());
                        adjustSpecialEffectsInfo.setMinVal(intParam.getMinVal());
                        adjustSpecialEffectsInfo.setStrength(intParam.getDefVal());
                        if (mCurCaptureVideoFx != null) {
                            mCurCaptureVideoFx.setExprVar(key, intParam.getDefVal());
                        }
                    }
                    break;
                case NvsExpressionParam.TYPE_BOOLEAN:
                    break;
                default:
                    break;
            }
            dataList.add(adjustSpecialEffectsInfo);
        }
        mAdjustSpecialEffectsView.setData(dataList);
    }

    @MSSubscribe("hideFilterSeekView")
    private void hideFilterSeekView() {
        mFilterMpProgress.setVisibility(View.GONE);
        mAdjustSpecialEffectsView.setVisibility(View.GONE);
    }

    private void changeFilterIntensity(int progress, boolean fromUser) {
        float strength = progress * 1.0f / 100;
        if (null != mCurrentFilterInfo) {
            mCurrentFilterInfo.setStrength(strength);
        }
        if (mCurCaptureVideoFx != null) {
            mCurCaptureVideoFx.setFilterIntensity(strength);
        }
    }

    public static class CaptureHandler extends Handler {
        private WeakReference<CaptureActivity> weakReference;

        public CaptureHandler(CaptureActivity captureActivity) {
            this.weakReference = new WeakReference<>(captureActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (weakReference == null || weakReference.get() == null) {
                return;
            }
            CaptureActivity captureActivity = weakReference.get();
            switch (msg.what) {
                case HANDLER_FRAME:
                    captureActivity.setCaptureFrame();
                    break;
            }
        }
    }

    /**
     * 设置实时帧率
     * Set real-time frame rate
     */
    private void setCaptureFrame() {
        if (mStreamingContext != null) {
            float v = mStreamingContext.detectEngineRenderFramePerSecond();
            if (mTvFrame.getVisibility() != View.VISIBLE) {
                mTvFrame.setVisibility(View.VISIBLE);
            }
            mTvFrame.setText((int) v + "");
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(HANDLER_FRAME, 500);
        }
    }

    /**
     * 关闭实时帧率检测
     * Close real-time frame rate
     */
    private void closeCaptureFrame() {
        if (mHandler != null) {
            mHandler.removeMessages(HANDLER_FRAME);
        }
        mTvFrame.setVisibility(View.INVISIBLE);
    }

    private void searchAssetData() {
        String bundlePath = "filter";
        mAssetManager.searchReservedAssets(NvAsset.ASSET_FILTER, bundlePath);
        mAssetManager.searchLocalAssets(NvAsset.ASSET_FILTER);
        //初始化本地道具 Initialize local props
        if (BuildConfig.HUMAN_AI_TYPE.equals(BUILD_HUMAN_AI_TYPE_MS)) {
            // TODO: 2024/9/24 msarface
//            bundlePath = "msarface";
        } else {
            bundlePath = "arface";
        }
        mAssetManager.searchReservedAssets(NvAsset.ASSET_ARSCENE_FACE, bundlePath);
        mAssetManager.searchLocalAssets(NvAsset.ASSET_ARSCENE_FACE);

    }

    /**
     * 滤镜数据初始化
     * Filter data initialization
     */
    private void initFilterList() {
        mFilterDataArrayList.clear();
        mFilterDataArrayList = AssetFxUtil.getFilterData(this,
                getLocalData(NvAsset.ASSET_FILTER),
                null,
                true,
                false);
    }

    private void initFilterDialog() {
        mFilterDialog = new AlertDialog.Builder(this).create();
        mFilterDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (mFilterSelPos > 0) {
                    mFilterBottomView.setSelectedPos(mFilterSelPos);
                }
                if (mCurCaptureVideoFx != null) {
                    mFilterBottomView.setIntensityLayoutVisible(View.VISIBLE);
                    mFilterBottomView.setIntensitySeekBarProgress((int) (mCurCaptureVideoFx.getFilterIntensity() * 100));
                }
            }
        });
        mFilterDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                changeCaptureDisplay(true);
            }
        });
        mFilterDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                changeCaptureDisplay(true);
                closeCaptureDialogView(mFilterDialog);
            }
        });
        mFilterBottomView = new FilterView(this);
        mFilterBottomView.setBlackTheme(false);
        /*
         * 设置滤镜数据
         * Set filter data
         */
        mFilterBottomView.initFilterRecyclerView(this);
        mFilterBottomView.setFilterArrayList(mFilterDataArrayList);
        mFilterBottomView.setIntensityLayoutVisible(View.INVISIBLE);
        mFilterBottomView.setIntensityTextVisible(View.GONE);
        mFilterBottomView.setFilterListener(new FilterView.OnFilterListener() {
            @Override
            public void onItmeClick(View v, int position) {
                int count = mFilterDataArrayList.size();
                if (position < 0 || position >= count) {
                    return;
                }
                if (mFilterSelPos == position) {
                    return;
                }
                mFilterSelPos = position;
                removeAllFilterFx();
                mFilterBottomView.setIntensitySeekBarMaxValue(100);
                mFilterBottomView.setIntensitySeekBarProgress(100);
                if (position == 0) {
                    mFilterBottomView.setIntensityLayoutVisible(View.INVISIBLE);
                    mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_BUILTIN);
                    mVideoClipFxInfo.setFxId(null);
                    mCurCaptureVideoFx = null;
                    return;
                }
                mFilterBottomView.setIntensityLayoutVisible(View.VISIBLE);
                FilterItem filterItem = mFilterDataArrayList.get(position);
                int filterMode = filterItem.getFilterMode();
                if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
                    String filterName = filterItem.getFilterName();
                    if (!TextUtils.isEmpty(filterName) && filterItem.getIsCartoon()) {
                        mCurCaptureVideoFx = mStreamingContext.appendBuiltinCaptureVideoFx("Cartoon");
                        mCurCaptureVideoFx.setBooleanVal("Stroke Only", filterItem.getStrokenOnly());
                        mCurCaptureVideoFx.setBooleanVal("Grayscale", filterItem.getGrayScale());
                    } else if (!TextUtils.isEmpty(filterName)) {
                        mCurCaptureVideoFx = mStreamingContext.appendBuiltinCaptureVideoFx(filterName);
                    }
                    mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_BUILTIN);
                    mVideoClipFxInfo.setFxId(filterName);
                } else {
                    String filterPackageId = filterItem.getPackageId();
                    if (!TextUtils.isEmpty(filterPackageId)) {
                        mCurCaptureVideoFx = mStreamingContext.appendPackagedCaptureVideoFx(filterPackageId);
                    }
                    mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_PACKAGE);
                    mVideoClipFxInfo.setFxId(filterPackageId);
                }
                if (mCurCaptureVideoFx != null) {
                    mCurCaptureVideoFx.setFilterIntensity(1.0f);
                }
            }

            @Override
            public void onMoreFilter() {
                /*
                 * 拍摄进入下载，不作比例适配
                 * Shoot into download, no proportion adaptation
                 */
                TimelineData.instance().setMakeRatio(NvAsset.AspectRatio_NoFitRatio);
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.moreFilter);
                bundle.putInt("assetType", NvAsset.ASSET_FILTER);
                bundle.putString("from", "capture_filter");
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), AssetDownloadActivity.class, bundle, REQUEST_FILTER_LIST_CODE);
                mFilterBottomView.setMoreFilterClickable(false);
            }

            @Override
            public void onIntensity(int value) {
                if (mCurCaptureVideoFx != null) {
                    float intensity = value / (float) 100;
                    mCurCaptureVideoFx.setFilterIntensity(intensity);
                }
            }
        });
    }

    @MSSubscribe(Constants.SubscribeType.SUB_APPLY_PROP_TYPE)
    private void applyPropEffect(String packageId, EffectInfo currentFilterInfo) {
        //判断点前item的是不是这个呢？不然可能出现UI选中的和实际对不上
        if (currentFilterInfo != null && !packageId.equals(currentFilterInfo.getId())) {
            return;
        }
        if (mArSceneFaceEffect == null) {
            initBeautyFx();
        }

        boolean arSceneAssetPackageContainWarp = mAssetPackageManager.isARSceneAssetPackageContainWarp(packageId);
        boolean arSceneAssetPackageContainFaceMesh = mAssetPackageManager.isARSceneAssetPackageContainFaceMesh(packageId);
        Log.d(TAG, "arSceneAssetPackageContainWarp==" + arSceneAssetPackageContainWarp + " arSceneAssetPackageContainFaceMesh==" + arSceneAssetPackageContainFaceMesh);
        if (arSceneAssetPackageContainWarp || arSceneAssetPackageContainFaceMesh) {
            isPropContainShape = true;
        } else {
            isPropContainShape = false;
        }
        String sceneId = packageId;
        showPropsToast(sceneId);
        //重置默认值
        mArSceneFaceEffect.setFloatVal(Constants.AR_SCENE_FACE_CAMERA_FOVY_ID_KEY, 45);
        NvsAssetPackageManager.ARSceneCameraPreset cameraPreset = mAssetPackageManager.getARSceneAssetPackageCameraPreset(packageId);
        if (null != cameraPreset) {
            float fovy = cameraPreset.fovy;
            mArSceneFaceEffect.setFloatVal(Constants.AR_SCENE_FACE_CAMERA_FOVY_ID_KEY, fovy);
        }
        mArSceneFaceEffect.setStringVal(Constants.AR_SCENE_ID_KEY, sceneId);
    }

    @MSSubscribe(Constants.SubscribeType.SUB_UN_USE_PROP_TYPE)
    private void removeAllProp() {
        if (mArSceneFaceEffect == null) {
            return;
        }
        mArSceneFaceEffect.setStringVal("Scene Id", "");
        isPropContainShape = false;
    }

    private void showPropsToast(String sceneId) {
        NvsAssetPackageManager manager = mStreamingContext.getAssetPackageManager();
        if (manager == null) {
            return;
        }
        String packagePrompt = manager.getARSceneAssetPackagePrompt(sceneId);
        if (!TextUtils.isEmpty(packagePrompt)) {
            ToastUtil.showToastCenter(this, packagePrompt);
        }
    }

    private void initMakeupDialog() {
        mMakeUpDialog = new AlertDialog.Builder(this).create();
        mMakeUpDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                changeCaptureDisplay(true);
            }
        });
        mMakeUpDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                changeCaptureDisplay(true);
                closeCaptureDialogView(mMakeUpDialog);
            }
        });
        mMakeUpView = new MakeUpSingleView(this);
        mMakeUpView.setMakeupCategoryList(MakeupDataManager.getMakeupCategoryList(this, false));
        mMakeUpView.setOnMakeUpEventListener(new MakeUpSingleView.MakeUpEventListener() {
            @Override
            public void onMakeupViewDataChanged(int tabPosition, int position, boolean isClearMakeup) {
                // onMakeupComposeDataChanged(position, isClearMakeup);
                //选择了美妆，美型需要切换到美型1
                //When Beauty Makeup is selected, Beauty needs to switch to Beauty 1
                onMakeupDataChanged(tabPosition, position);
            }

            @Override
            public void onMakeupColorChanged(String makeupId, NvsColor color) {
                mMakeupHelper.setCaptureMakeupColor(makeupId, color);
            }

            @Override
            public void onMakeupIntensityChanged(String makeupId, float intensity) {
                mMakeupHelper.setCaptureMakeupIntensity(makeupId, intensity);
            }

            @Override
            public void onMakeupFilterIntensityChanged(String filterId, float intensity) {
                mMakeupHelper.setCaptureMakeupFilterIntensity(filterId, intensity);
            }

            @Override
            public void removeVideoFxByName(String name) {
                removeFilterFxByName(name);
            }

            @Override
            public void onMakeUpViewDismiss() {
                closeCaptureDialogView(mMakeUpDialog);
            }
        });
    }


    /**
     * 判断选中的美妆项中是否包含不可修改的磨皮
     * Judge whether the selected beauty item contains non-modifiable skin
     *
     * @param beautyParam
     * @return
     */
    private boolean isContainsBeautyStrength(BeautyParam beautyParam) {
        if (null == beautyParam) {
            return false;
        }
        List<BaseParam.Param> params = beautyParam.getParams();
        if (null == params || params.size() == 0) {
            return false;
        }
        boolean isBeautyStrength = false;
        boolean isBeautyType = false;
        for (BaseParam.Param param : params) {
            if (!isBeautyStrength) {
                isBeautyStrength = isBeautyStrength(param);
            }
            if (!isBeautyType) {
                isBeautyType = isBeautyType(param);
            }
            if (isBeautyType && isBeautyStrength) {
                return true;
            }
        }
        return false;
    }


    private boolean isBeautyStrength(BaseParam.Param param) {
        if (null == param) {
            return false;
        }
        if (TextUtils.equals(param.getKey(), FxParams.ADVANCED_BEAUTY_ENABLE)) {

            return true;
        }

        return false;
    }

    private boolean isBeautyType(BaseParam.Param param) {
        if (null == param) {
            return false;
        }
        if (TextUtils.equals(param.getKey(), FxParams.ADVANCED_BEAUTY_TYPE) && TextUtils.equals(param.getType(), "int")) {
            double value = (double) param.getValue();
            return value > 0;
        }
        return false;
    }

    public boolean containsSelectedItem(BaseParam beautyParam, String param) {
        if (null == beautyParam || TextUtils.isEmpty(param)) {
            return false;
        }
        List<BaseParam.Param> params = beautyParam.getParams();
        if (null == params || params.size() == 0) {
            return false;
        }
        for (BaseParam.Param paramItem : params) {
            if (TextUtils.equals(paramItem.getKey(), param)) {

                return true;
            }
        }
        return false;
    }

    /**
     * 初始化美颜特效
     * Initialize the beauty fx
     */
    private void initBeautyFx() {
        mBeautyHelper = new BeautyHelper(mStreamingContext, BuildConfig.FACE_MODEL == 240, parameterValues.isSingleBufferMode());
        if (null == mArSceneFaceEffect) {
            mArSceneFaceEffect = mBeautyHelper.checkBuildCaptureBeautyFx();
        }
        if (null == mBeautyFx) {
            mBeautyFx = mBeautyHelper.checkBuildCaptureBeautyFx();
        }
        if (mArSceneFaceEffect != null) {
            NvsARSceneManipulate arSceneManipulate = mArSceneFaceEffect.getARSceneManipulate();
            if (arSceneManipulate != null) {
                mIsSupportMatte = arSceneManipulate.isFunctionAvailable(NvsARSceneManipulate.CheckedFunctionType_Matte);
            }
        }
        mMakeupHelper = new MakeupHelper(mStreamingContext, BuildConfig.FACE_MODEL == 240, parameterValues.isSingleBufferMode());
        mMakeupHelper.checkBuildCaptureArSceneFx();

        mBeautyHelper.enableCaptureBeauty(true);
        mBeautyHelper.enableCaptureAdvancedBeauty(true);
        mBeautyHelper.enableCaptureShape(true);
        mBeautyHelper.enableCaptureShapeNew(true);
        if (null != mArSceneFaceEffect) {
            mArSceneFaceEffect.setFloatVal(FxParams.ADVANCED_BEAUTY_INTENSITY, 0);
        }
        if (FaceAvatarView.IS_SHOW && (null != mCaptureRoot)) {
            FaceAvatarView faceAvatarView = new FaceAvatarView(this);
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            layoutParams.topMargin = getResources().getDimensionPixelSize(com.meishe.base.R.dimen.dp_px_150);
            layoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.dp_px_30);
            layoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.dp_px_30);
            layoutParams.bottomToTop = R.id.fl_bottom_parent;
            layoutParams.topToTop = R.id.capture_root_layout;
            layoutParams.leftToLeft = R.id.capture_root_layout;
            layoutParams.rightToRight = R.id.capture_root_layout;
            faceAvatarView.setLayoutParams(layoutParams);
            mCaptureRoot.addView(faceAvatarView);
            faceAvatarView.setArSceneEffect(mArSceneFaceEffect);
        }
    }

    private void initViewModelObserver() {
        mCaptureViewModel.getFilterTypeInfo().observe(this, new Observer<TypeAndCategoryInfo>() {
            @Override
            public void onChanged(TypeAndCategoryInfo info) {
                mFilterTypeInfo = info;
                showFilterFragment();
            }
        });
        mCaptureViewModel.getPropTypeInfo().observe(this, new Observer<TypeAndCategoryInfo>() {
            @Override
            public void onChanged(TypeAndCategoryInfo info) {
                mPropTypeInfo = info;
                showCommonFragment(mPropTypeInfo, Constants.FRAGMENT_PROP_TAG);
            }
        });
        mCaptureViewModel.getStickerTypeInfo().observe(this, new Observer<TypeAndCategoryInfo>() {
            @Override
            public void onChanged(TypeAndCategoryInfo info) {
                mStickerTypeInfo = info;
                showCommonFragment(mStickerTypeInfo, Constants.FRAGMENT_STICKER_TAG);
            }
        });
        mCaptureViewModel.getComponentTypeInfo().observe(this, new Observer<TypeAndCategoryInfo>() {
            @Override
            public void onChanged(TypeAndCategoryInfo info) {
                mComponentTypeInfo = info;
                showCommonFragment(mComponentTypeInfo, Constants.FRAGMENT_COMPONENT_CAPTION_TAG);
            }
        });
    }

    /**
     * 设置选中的素材
     * Set selected material
     *
     * @param attachment
     */
    private boolean checkSelectByAttachment(String attachment) {
        if (drawRectModel < 0 || TextUtils.isEmpty(attachment)) {
            return false;
        }
        if (drawRectModel == Constants.EDIT_MODE_STICKER) {
            Fragment fragment = mFragmentManager.findFragmentByTag(Constants.FRAGMENT_STICKER_TAG);
            int count = mStreamingContext.getCaptureAnimatedStickerCount();
            for (int i = 0; i < count; i++) {
                NvsCaptureAnimatedSticker animatedSticker = mStreamingContext.getCaptureAnimatedStickerByIndex(i);
                if (animatedSticker != null && TextUtils.equals(attachment, (String) animatedSticker.getAttachment(Constants.KEY_LEVEL))) {
                    currentAnimatedSticker = animatedSticker;
                    if (fragment instanceof CaptureEffectFragment) {
                        CaptureEffectFragment captureEffectFragment = (CaptureEffectFragment) fragment;
                        captureEffectFragment.selectEffectById(currentAnimatedSticker.getAnimatedStickerPackageId());
                    }
                    updateDrawRectPosition(getAssetViewVerticesList(currentAnimatedSticker.getHorizontalFlip(), currentAnimatedSticker.getBoundingRectangleVertices()), drawRectModel, attachment, null);
                    AssetLevelBean addBean = null;
                    for (int j = 0; j < stickerVertices.size(); j++) {
                        if (TextUtils.equals(stickerVertices.get(j).getTag(), attachment)) {
                            addBean = stickerVertices.get(j);
                            stickerVertices.remove(j);
                            break;
                        }
                    }
                    if (addBean != null) {
                        stickerVertices.add(0, addBean);
                    }
                    return true;
                }
            }
        } else if (drawRectModel == Constants.EDIT_MODE_COMPOUND_CAPTION) {
            int count = mStreamingContext.getCaptureCompoundCaptionCount();
            for (int i = 0; i < count; i++) {
                NvsCaptureCompoundCaption compoundCaption = mStreamingContext.getCaptureCompoundCaptionByIndex(i);
                if (compoundCaption != null && TextUtils.equals(attachment, (String) compoundCaption.getAttachment(Constants.KEY_LEVEL))) {
                    currentCompoundCaption = compoundCaption;
                    List<PointF> vertices = currentCompoundCaption.getCompoundBoundingVertices(NvsTimelineCompoundCaption.BOUNDING_TYPE_FRAME);
                    List<List<PointF>> captions = getCaptionList(currentCompoundCaption);
                    updateDrawRectPosition(getAssetViewVerticesList(false, vertices), drawRectModel, attachment, captions);
                    AssetLevelBean addBean = null;
                    for (int j = 0; j < captionVertices.size(); j++) {
                        if (TextUtils.equals(captionVertices.get(j).getTag(), attachment)) {
                            addBean = captionVertices.get(j);
                            captionVertices.remove(j);
                            break;
                        }
                    }
                    if (addBean != null) {
                        captionVertices.add(0, addBean);
                    }
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 检查选中的Attachment
     * Check the selected Attachment
     *
     * @param vertices
     * @param pointF
     * @return
     */
    private String checkTouchAttachment(List<AssetLevelBean> vertices, Point pointF) {
        if (vertices != null) {
            for (int i = 0; i < vertices.size(); i++) {
                List<PointF> pointFS = vertices.get(i).getData();
                if (checkTouchInPath(pointFS, pointF)) {
                    return vertices.get(i).getTag();
                }
            }
        }
        return "";
    }

    /**
     * 检查点是否在坐标区域内
     * Check whether the point is in the coordinate area
     *
     * @param pointFS
     * @param point
     * @return
     */
    private boolean checkTouchInPath(List<PointF> pointFS, Point point) {
        Path path = new Path();
        if (pointFS != null && pointFS.size() >= 4) {
            path.reset();
            path.moveTo(pointFS.get(0).x, pointFS.get(0).y);
            path.lineTo(pointFS.get(1).x, pointFS.get(1).y);
            path.lineTo(pointFS.get(2).x, pointFS.get(2).y);
            path.lineTo(pointFS.get(3).x, pointFS.get(3).y);
            path.lineTo(pointFS.get(0).x, pointFS.get(0).y);
        }
        if (Util.isPointInPath(point, path)) {
            return true;
        }
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initListener() {
        mLiveWindow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideEffectFragment(Constants.FRAGMENT_BEAUTY_TAG);
                hideEffectFragment(Constants.FRAGMENT_FILTER_TAG);
                hideEffectFragment(Constants.FRAGMENT_PROP_TAG);
                if (KeyboardUtils.isSoftInputVisible(CaptureActivity.this)) {
                    KeyboardUtils.hideSoftInput(CaptureActivity.this);
                }
                if (drawRectModel >= 0) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        checkTouchAssetFrame(new PointF(event.getX(), event.getY()));
                        //clearSelectState();
                        if (currentAnimatedSticker == null && currentCompoundCaption == null) {
                            closeCaptionFx();
                            closeStickerFx();
                        }
                    }
                }
                float rectHalfWidth = mImageAutoFocusRect.getWidth() / 2;
                if (event.getX() - rectHalfWidth >= 0 && event.getX() + rectHalfWidth <= mLiveWindow.getWidth()
                        && event.getY() - rectHalfWidth >= 0 && event.getY() + rectHalfWidth <= mLiveWindow.getHeight()) {
                    mImageAutoFocusRect.setX(event.getX() - rectHalfWidth);
                    mImageAutoFocusRect.setY(event.getY() - rectHalfWidth);
                    RectF rectFrame = new RectF();
                    rectFrame.set(mImageAutoFocusRect.getX(), mImageAutoFocusRect.getY(),
                            mImageAutoFocusRect.getX() + mImageAutoFocusRect.getWidth(),
                            mImageAutoFocusRect.getY() + mImageAutoFocusRect.getHeight());
                    /*
                     * 启动自动聚焦
                     * Start autofocus
                     */
                    mImageAutoFocusRect.startAnimation(mFocusAnimation);
                    if (m_supportAutoFocus) {
                        mStreamingContext.startAutoFocus(new RectF(rectFrame));
                        mStreamingContext.StartContinuousFocus();
                    }
                }

                return false;
            }
        });

        mIvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isFastClick()) {
                    return;
                }
                if (null == mTipsDialog) {
                    return;
                }
                mTipsDialog.show();
                mTipsDialog.setTipsText(R.string.exit_capture_save_confirm);
            }
        });
        mTipsDialog.setOnBtnClickListener(new TipsDialog.OnBtnClickListener() {
            @Override
            public void OnConfirmBtnClicked() {
                mTipsDialog.dismiss();
                stopStreamingContext();
                AppManager.getInstance().finishActivity();
            }

            @Override
            public void OnCancelBtnClicked() {
                mTipsDialog.dismiss();
            }
        });
        /*
         * 切换摄像头开关
         * Toggle camera switch
         */
        mIvChangeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsSwitchingCamera) {
                    return;
                }
                if (mCurrentDeviceIndex == 0) {
                    mCurrentDeviceIndex = 1;
                } else {
                    mCurrentDeviceIndex = 0;
                }
                mIsSwitchingCamera = true;
                startCapturePreview(true);
                checkFlashState(mCurrentDeviceIndex != 1);
            }
        });
        mIvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoreDialog.isShowing()) {
                    mMoreDialog.dismiss();
                } else {
                    mMoreDialog.show();
                }
            }
        });
        /*
         * 美颜
         * Beauty
         */
        mCaptureBeauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCaptureDisplay(false);
                showBeautyFragment();
            }
        });
        //变声  change of voice
        mCaptureVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == mVoicePop) {
                    return;
                }
                changeCaptureDisplay(false);
                mVoicePop.show();
            }
        });
        //美妆  Beauty makeup
        mCaptureMakeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCaptureDisplay(false);
                showCaptureDialogView(mMakeUpDialog, mMakeUpView);
            }
        });

        //滤镜  Filter
        mCaptureFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCaptureDisplay(false);
                showFilterFragment();
            }
        });
        mCaptureProp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showArSceneFx();
            }
        });

        /*
         * 开始录制
         *Start recording
         */
        mFlStartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean startNativeCamera = parameterValues.isStartNativeCamera();
                if (startNativeCamera) {
                    if (mRecordType == Constants.RECORD_TYPE_PICTURE) {
                        takePhoto();
                        changeRecordDisplay(RECORD_DEFAULT, true);
                        return;
                    }
                }

                /*
                 * 当前在录制状态，可停止视频录制
                 * Currently in recording state, you can stop video recording
                 */
                if (getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTURERECORDING) {
                    stopRecording();
                } else {
                    mCurRecordVideoPath = PathUtils.getRecordVideoPath();
                    if (mCurRecordVideoPath == null) {
                        return;
                    }
                    mFlStartRecord.setEnabled(false);
                    if (mRecordType == Constants.RECORD_TYPE_VIDEO) {
                        mEachRecodingVideoTime = 0;
                        /*
                         * 当前未在视频录制状态，则启动视频录制。此处使用带特效的录制方式
                         * If video recording is not currently in progress, start video recording. Use the recording method with special effects here
                         */
                        if (!mStreamingContext.startRecording(mCurRecordVideoPath)) {
                            return;
                        }
                        if (mSelectMusicInfo != null) {
                            if (mAllRecordingTime == 0) {
                                AudioPlayer.getInstance(mContext).setCurrentMusic(mSelectMusicInfo, true);
                            } else {
                                AudioPlayer.getInstance(mContext).seekPosition(mAllRecordingTime + mSelectMusicInfo.getTrimIn());
                            }
                            AudioPlayer.getInstance(mContext).startPlay();
                        }
                        changeSelectMusicState(false);
                        changeRecordDisplay(RECORDING, false);
                        mRecordFileList.add(mCurRecordVideoPath);
                    } else if (mRecordType == Constants.RECORD_TYPE_PICTURE) {
                        /*
                         * 将采集预览输出连接到LiveWindow控件
                         * Connect the capture preview output to the LiveWindow control
                         * */
                        if (mLiveWindow instanceof MSLiveWindow) {
                            mPictureBitmap = ((MSLiveWindow) mLiveWindow).takeScreenshot();
                        } else if (mLiveWindow instanceof MSLiveWindowExt) {
                            mPictureBitmap = ((MSLiveWindowExt) mLiveWindow).takeScreenshot();
                        }
                        if (mPictureBitmap != null) {
                            mPictureImage.setImageBitmap(mPictureBitmap);
                            showPictureLayout(true);
                        } else {
                            changeRecordDisplay(RECORD_DEFAULT, true);
                        }

                    }
                }
            }
        });
        /*
         * 删除视频
         * Delete video
         */
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecordTimeList.size() != 0 && mRecordFileList.size() != 0) {
                    mAllRecordingTime -= mRecordTimeList.get(mRecordTimeList.size() - 1);
                    mRecordTimeList.remove(mRecordTimeList.size() - 1);
                    PathUtils.deleteFile(mRecordFileList.get(mRecordFileList.size() - 1));
                    mRecordFileList.remove(mRecordFileList.size() - 1);
                    mRecordTime.setText(TimeFormatUtil.formatUsToString2(mAllRecordingTime));

                    if (mRecordTimeList.size() == 0) {
                        changeRecordDisplay(RECORD_DEFAULT, mRecordType == Constants.RECORD_TYPE_PICTURE);
                    } else {
                        mStartText.setText(mRecordTimeList.size() + "");
                        mRecordTime.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
        /*
         * 下一步，进入编辑
         * Next, enter edit
         */
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * mRecordFileList，视频存储列表。将拍摄的视频传到下一个页面
                 * mRecordFileList, video storage list. Send the captured video to the next page.
                 */
                ArrayList<ClipInfo> pathList = new ArrayList<>();
                for (int i = 0; i < mRecordFileList.size(); i++) {
                    ClipInfo clipInfo = new ClipInfo();
                    clipInfo.setFilePath(mRecordFileList.get(i));
                    pathList.add(clipInfo);
                }
                if (pathList.size() <= 0) {
                    return;
                }
                NvsAVFileInfo avFileInfo = mStreamingContext.getAVFileInfo(pathList.get(0).getFilePath());
                if (avFileInfo == null) {
                    return;
                }
                /*
                 * 数据清空
                 * Data clear
                 */
                TimelineData.instance().clear();
                NvsSize size = avFileInfo.getVideoStreamDimension(0);
                int rotation = avFileInfo.getVideoStreamRotation(0);
                if (rotation == NvsVideoStreamInfo.VIDEO_ROTATION_90
                        || rotation == NvsVideoStreamInfo.VIDEO_ROTATION_270) {
                    int tmp = size.width;
                    size.width = size.height;
                    size.height = tmp;
                }
                int makeRatio = size.width > size.height ? NvAsset.AspectRatio_16v9 : NvAsset.AspectRatio_9v16;
                TimelineData.instance().setVideoResolution(Util.getVideoEditResolution(makeRatio));
                TimelineData.instance().setMakeRatio(makeRatio);
                TimelineData.instance().setClipInfoData(pathList);
                if (mSelectMusicInfo != null) {
                    List<MusicInfo> musicInfoList = new ArrayList<>();
                    mSelectMusicInfo.setFadeDuration(1000000 * 3);
                    mSelectMusicInfo.setInPoint(0);
                    mSelectMusicInfo.setOutPoint(mSelectMusicInfo.getTrimOut() - mSelectMusicInfo.getTrimIn());
                    mSelectMusicInfo.setOriginalInPoint(mSelectMusicInfo.getInPoint());
                    mSelectMusicInfo.setOriginalOutPoint(mSelectMusicInfo.getOutPoint());
                    mSelectMusicInfo.setOriginalTrimIn(mSelectMusicInfo.getTrimIn());
                    mSelectMusicInfo.setOriginalTrimOut(mSelectMusicInfo.getTrimOut());

                    long cur_music_duration = mSelectMusicInfo.getOriginalOutPoint() - mSelectMusicInfo.getOriginalInPoint();
                    long extra_duration = mAllRecordingTime - mSelectMusicInfo.getOriginalOutPoint();
                    int extra_music = (int) (extra_duration / cur_music_duration);
                    long extra_music_left = extra_duration % cur_music_duration;
                    mSelectMusicInfo.setExtraMusic(extra_music);
                    mSelectMusicInfo.setExtraMusicLeft(extra_music_left);
                    mSelectMusicInfo.setOutPoint(mAllRecordingTime);

                    musicInfoList.add(mSelectMusicInfo);
                    TimelineData.instance().setMusicList(musicInfoList);
                }
                mNext.setClickable(false);

                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.START_ACTIVITY_FROM_CAPTURE, true);
                AppManager.getInstance().jumpActivity(CaptureActivity.this, VideoEditActivity.class, bundle);
            }
        });

        mTvChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRecordType(true);
            }
        });
        mTvChooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRecordType(false);
            }
        });
        mPictureCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideCapturePciture();
                if (mRecordTimeList.isEmpty()) {
                    mDelete.setVisibility(View.INVISIBLE);
                    mNext.setVisibility(View.INVISIBLE);
                    mStartText.setVisibility(View.INVISIBLE);
                }
            }
        });

        mPictureOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRecordType == Constants.RECORD_TYPE_PICTURE) {
                    mAllRecordingTime += mEachRecodingImageTime;
                    mRecordTimeList.add(mEachRecodingImageTime);
                    mRecordTime.setText(TimeFormatUtil.formatUsToString2(mAllRecordingTime));
                    mStartText.setText(String.format("%d", mRecordTimeList.size()));
                    changeRecordDisplay(RECORD_FINISH, true);
                }
                String jpgPath = PathUtils.getTakePhotoDirectory();
                Observable.just(jpgPath).map(new Function<String, Boolean>() {
                    @Override
                    public Boolean apply(String s) throws Exception {
                        Log.d("lpf", "mOrientation=" + mOrientation);
                        Bitmap result = adjustPhotoRotation(mPictureBitmap, mOrientation);
                        return Util.saveBitmapToSD(result, s);
                    }
                }).subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Boolean>() {

                    @Override
                    public void accept(Boolean save_ret) throws Exception {
                        MediaScannerUtil.scanFile(jpgPath, "image/jpg");
                        if (save_ret) {
                            mRecordFileList.add(jpgPath);
                        }

                        hideCapturePciture();
                    }
                }).subscribe();

            }
        });

        mAdjustSpecialEffectsView.setOnItemProgressChangeListener(new AdjustSpecialEffectsView.OnItemProgressChangeListener() {
            @Override
            public void onProgressChange(AdjustSpecialEffectsInfo info, int progress, boolean fromUser) {
                if (!fromUser) {
                    float maxVal = info.getMaxVal();
                    float minVal = info.getMinVal();
                    float diff = maxVal - minVal;
                    float strength = diff * progress / 100f + minVal;
                    info.setStrength(strength);
                    if (mCurCaptureVideoFx != null) {
                        mCurCaptureVideoFx.setExprVar(info.getKey(), info.getStrength());
                    }
                }
            }

            @Override
            public void onProgressChangeFinish(int progress, boolean fromUser) {
                Fragment fragmentByTag = mFragmentManager.findFragmentByTag(FRAGMENT_FILTER_TAG);
                if (fragmentByTag instanceof CaptureFilterFragment) {
                    CaptureFilterFragment captureFilterFragment = (CaptureFilterFragment) fragmentByTag;
                    captureFilterFragment.enableChangedItem(true);
                }
            }

            @Override
            public void onProgressChangeStarted(int progress, boolean fromUser) {
                Fragment fragmentByTag = mFragmentManager.findFragmentByTag(FRAGMENT_FILTER_TAG);
                if (fragmentByTag instanceof CaptureFilterFragment) {
                    CaptureFilterFragment captureFilterFragment = (CaptureFilterFragment) fragmentByTag;
                    captureFilterFragment.enableChangedItem(false);
                }
            }

            @Override
            public void onTouchUp(boolean touchUpFlag) {
                if (mCurCaptureVideoFx != null && touchUpFlag) {
                    mCurCaptureVideoFx.resetStartTime();
                }
            }

            @Override
            public void onColorChange(AdjustSpecialEffectsInfo effectsInfo, int color) {
                float alphaF = (Color.alpha(color) * 1.0f / 255f);
                float red = (Color.red(color) * 1.0f / 255f);
                float green = (Color.green(color) * 1.0f / 255f);
                float blue = (Color.blue(color) * 1.0f / 255f);
                effectsInfo.setColor(new NvsColor(red, green, blue, alphaF));
                if (mCurCaptureVideoFx != null) {
                    mCurCaptureVideoFx.setExprObjectVar(effectsInfo.getKey(), effectsInfo.getColor());
                }

            }
        });
        mFlStartRecord.post(new Runnable() {
            @Override
            public void run() {
                mTvChooseVideo.performClick();
            }
        });

    }

    private void checkFlashState(boolean currentDeviceIndex) {
        if (mMoreDialog != null) {
            mMoreDialog.setFlashEnable(currentDeviceIndex);
        }
    }

    Bitmap adjustPhotoRotation(Bitmap bm, int orientationDegree) {
        if (orientationDegree == 0 || orientationDegree == 180) {
            return bm;
        }
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);

        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            return bm1;
        } catch (OutOfMemoryError ex) {
            ex.printStackTrace();
        }
        return null;
    }


    private void hideCapturePciture() {
        mFlStartRecord.setEnabled(true);
        mIvExit.setVisibility(View.VISIBLE);

        mRlPhotosLayout.clearAnimation();
        mRlPhotosLayout.setVisibility(View.GONE);
        mRlPhotosLayout.setClickable(false);
        mRlPhotosLayout.setFocusable(false);
    }

    private void showBeautyFragment() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        mBeautyCaptureFragment = mFragmentManager.
                findFragmentByTag(Constants.FRAGMENT_BEAUTY_TAG);
        if (mBeautyCaptureFragment == null) {
            mBeautyCaptureFragment = BeautyCaptureFragment.newInstance(mIsSupportMatte, mMakeupHelper, mBeautyHelper);
            fragmentTransaction.add(R.id.bottom_container_high, mBeautyCaptureFragment, Constants.FRAGMENT_BEAUTY_TAG);
        }
        if (mBeautyCaptureFragment instanceof BeautyCaptureFragment) {
            ((BeautyCaptureFragment) mBeautyCaptureFragment).setMakeupTemplateChangedListener(new BeautyCaptureFragment.MakeupTemplateChangedListener() {
                @Override
                public void onMakeupTemplateDeleted(HashMap<Integer, List<IFxInfo>> makeupMap) {
                    Makeup selectItem = mMakeUpView.getSelectItem();
                    if ((null == selectItem)) {
                        return;
                    }
                    int tabPosition = mMakeUpView.getTabPosition();
                    if (tabPosition < 0) {
                        return;
                    }
                    String assetsDirectory = selectItem.getAssetsDirectory();
                    if (TextUtils.isEmpty(assetsDirectory)) {
                        return;
                    }
                    if (tabPosition == 0) {
                        File file = new File(assetsDirectory);
                        mMakeupHelper.applyCaptureMakeupFx(file);
                    } else {
                        Makeup makeup = mMakeUpView.getComposeMakeup();
                        if (null != makeup) {
                            mMakeupHelper.applyCaptureMakeupFx(makeup);
                        }
                        mMakeupHelper.applyCaptureMakeupFx(selectItem);
                    }
                }
            });
        }
        showFragment(mBeautyCaptureFragment, fragmentTransaction, Constants.FRAGMENT_BEAUTY_TAG);
    }

    private void showFilterFragment() {
        boolean canReplace = true;
        if (preMakeUp != null) {
            MakeupParamContent effectContent = preMakeUp.getEffectContent();
            if (effectContent != null) {
                List<FilterParam> filterParamList = effectContent.getFilterParams();
                if (null != filterParamList) {
                    for (FilterParam filterArgs : filterParamList) {
                        if (!filterArgs.canReplace()) {
                            canReplace = false;
                            break;
                        }
                    }
                }
            }
        }

        if (!canReplace) {
            ToastUtil.showToast(mContext, getString(R.string.makeup_not_allow_change_this));
            return;
        }

        if (mFilterTypeInfo == null) {
            mCaptureViewModel.getEffectTypeData(Constants.FRAGMENT_FILTER_TAG);
        } else {
            Fragment fragment = mFragmentManager.
                    findFragmentByTag(Constants.FRAGMENT_FILTER_TAG);
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            if (fragment == null) {
                fragment = CaptureFilterFragment.newInstance(mFilterTypeInfo);
                fragmentTransaction.add(R.id.bottom_container, fragment, Constants.FRAGMENT_FILTER_TAG);
            }
            if (null != mCurrentFilterInfo) {
                if ((mCurrentFilterInfo.getIsAdjusted() == 0) && (mCurrentFilterInfo.getKind() == 8 || mCurrentFilterInfo.getKind() == 9)) {
                    mAdjustSpecialEffectsView.setVisibility(View.GONE);
                    mFilterMpProgress.setVisibility(View.GONE);
                } else {
                    float strength = mCurrentFilterInfo.getStrength();
                    if (mCurrentFilterInfo.getStrength() == 0) {
                        strength = mCurrentFilterInfo.getDefaultStrength();
                    }
                    showFilterSeekViewVisible(mCurrentFilterInfo.getId(), strength);
                }
            }
            showFragment(fragment, fragmentTransaction, Constants.FRAGMENT_FILTER_TAG);
        }

    }


    private void showCommonFragment(TypeAndCategoryInfo info, String tag) {
        if (info == null) {
            mCaptureViewModel.getEffectTypeData(tag);
            return;
        }
        Fragment fragment = mFragmentManager.
                findFragmentByTag(tag);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (fragment == null) {
            fragment = CaptureEffectFragment.newInstance(info, tag);
            fragmentTransaction.add(R.id.bottom_container_high, fragment, tag);
        }
        showFragment(fragment, fragmentTransaction, tag);
    }


    private void hideEffectFragment(String tag) {
        Fragment fragment = mFragmentManager.
                findFragmentByTag(tag);
        if (fragment == null) {
            return;
        }
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (Constants.FRAGMENT_FILTER_TAG.equals(tag)) {
            hideFilterSeekView();
        }
        hideFragment(fragment, fragmentTransaction, tag);
        if (Constants.FRAGMENT_PROP_TAG.equals(tag)
                || Constants.FRAGMENT_FILTER_TAG.equals(tag)
                || Constants.FRAGMENT_BEAUTY_TAG.equals(tag)) {
            changeCaptureDisplay(true);
        }
    }

    private void showFragment(Fragment fragment, FragmentTransaction
            fragmentTransaction, String tag) {
        fragmentTransaction.setCustomAnimations(
                R.anim.slide_bottom_in,
                R.anim.slide_bottom_out
        );
        fragmentTransaction.show(fragment);
//        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    private void hideFragment(Fragment fragment, FragmentTransaction
            fragmentTransaction, String tag) {
        fragmentTransaction.hide(fragment);
//        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }


    private void changeCaptureDisplay(boolean display) {
        if (display) {
            if (!mRecordTimeList.isEmpty()) {
                mFlMiddleParent.setVisibility(View.VISIBLE);
            }
            mIvExit.setVisibility(View.VISIBLE);
            mIvMore.setVisibility(View.VISIBLE);
            mSelectMusicLayout.setVisibility(View.VISIBLE);
            mCaptureVoice.setVisibility(mRecordType == Constants.RECORD_TYPE_PICTURE ? View.GONE : View.VISIBLE);
            //mAudioNoise.setVisibility(mRecordType == Constants.RECORD_TYPE_PICTURE ? View.GONE : View.VISIBLE);
            mIvChangeCamera.setVisibility(View.VISIBLE);
            mLlRightContainer.setVisibility(View.VISIBLE);
            mFlBottomParent.setVisibility(View.VISIBLE);
            drawRect.setVisibility(View.GONE);
        } else {
            mCaptureVoice.setVisibility(View.GONE);
            //mAudioNoise.setVisibility(View.GONE);
            mIvExit.setVisibility(View.INVISIBLE);
            mIvMore.setVisibility(View.INVISIBLE);
            mSelectMusicLayout.setVisibility(View.INVISIBLE);
            mIvChangeCamera.setVisibility(View.INVISIBLE);
            mLlRightContainer.setVisibility(View.INVISIBLE);
            mFlBottomParent.setVisibility(View.INVISIBLE);
            mFlMiddleParent.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 美妆应用
     * 注意：
     * 1.整妆和单妆都是一个makeup包，但是妆容是多个单妆包用json组合起来的整妆
     * 2.整妆和单妆是互斥的，整妆的优先级高于单妆。
     * 即在应用单妆的时候需要将已添加的整妆移除。如果先添加的是单妆，添加整妆时则因为优先级，SDK会将单妆置为无效，只有整妆效果
     * <p>
     * Beauty application
     * be careful:
     * 1. The whole makeup and single makeup are a makeup package, but the makeup is the whole makeup combined by multiple single makeup packages using json
     * 2. Whole makeup and single makeup are mutually exclusive, and the priority of whole makeup is higher than that of single makeup.
     * That is, the added whole makeup needs to be removed when applying single makeup. If the single makeup is added first, the SDK will set the single makeup as invalid because of the priority when adding the whole makeup, only the whole makeup effect
     *
     * @param tabPosition
     * @param position
     */
    private void onMakeupDataChanged(int tabPosition, int position) {
        if (mArSceneFaceEffect == null) {
            initBeautyFx();
        }
        if (mArSceneFaceEffect == null || mMakeupHelper == null) {
            return;
        }
        Makeup selectItem = mMakeUpView.getSelectItem();
        if (tabPosition == 0) {
            clearMicroShape();
            clearShape();
            clearAllCustomMakeup();
            mMakeupHelper.resetCaptureMakeupFx(preMakeUp);
            if (position > 0) {
                File file = new File(selectItem.getAssetsDirectory());
                // 此处也可以使用 mMakeupHelper.applyCaptureMakeupFx(selectItem);
                // 使用file的形式是为了方便客户直接使用本地下载好的妆容。
                //You can also use mMakeupHelper. applyCaptureMakeupFx (selectItem) here;
                //The use of file is to facilitate customers to directly use locally downloaded makeup.
                preMakeUp = mMakeupHelper.applyCaptureMakeupFx(file);
                mMakeUpView.updateSelectMakeup(preMakeUp);
            } else {
                // 妆容且选中为无的情况下
                //When makeup is selected as None
                preMakeUp = null;
                //应用美颜模板
                if (null == mBeautyCaptureFragment) {
                    return;
                }
                if (mBeautyCaptureFragment instanceof BeautyCaptureFragment) {
                    ((BeautyCaptureFragment) mBeautyCaptureFragment).checkBeautyTemplate();
                }
            }
        } else {
            //单状无的情况
            //The situation without single status
            if ((position == 0)) {
                resetCustomMakeup(mMakeUpView.getSelectMakeupId());
            } else {
                mMakeupHelper.applyCaptureMakeupFx(selectItem);
            }
        }
    }

    private void clearShape() {
        if (mShapeDataList != null) {
            for (IFxInfo shapeInfo : mShapeDataList) {
                if (shapeInfo instanceof CompoundFxInfo && !TextUtils.isEmpty(shapeInfo.getPackageId())) {
                    NvsCaptureVideoFx captureVideoFx;
                    if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                        captureVideoFx = mArSceneFaceEffect;
                    } else {
                        captureVideoFx = mBeautyFx;
                    }
                    CompoundFxInfo compoundFxInfo = (CompoundFxInfo) shapeInfo;
                    String fxName = compoundFxInfo.getFxName();
                    if (!TextUtils.isEmpty(fxName)) {
                        captureVideoFx.setFloatVal(fxName, 0);
                    }
                }
            }
        }
    }

    private void clearMicroShape() {
        if (mSmallShapeDataList != null) {
            for (IFxInfo shapeInfo : mSmallShapeDataList) {
                if (shapeInfo instanceof CompoundFxInfo && !TextUtils.isEmpty(shapeInfo.getPackageId())) {
                    NvsCaptureVideoFx captureVideoFx;
                    if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                        captureVideoFx = mArSceneFaceEffect;
                    } else {
                        captureVideoFx = mBeautyFx;
                    }
                    CompoundFxInfo compoundFxInfo = (CompoundFxInfo) shapeInfo;
                    String fxName = compoundFxInfo.getFxName();
                    if (!TextUtils.isEmpty(fxName)) {
                        captureVideoFx.setFloatVal(fxName, 0);
                    }
                }
            }
        }
    }

    //清理所有的单装
    //Clean all single packages
    private void clearAllCustomMakeup() {
        List<MakeupCategory> makeupCategoryList = mMakeUpView.getMakeupCategory();
        for (MakeupCategory category : makeupCategoryList) {
            if (category.getMaterialType() == 21) {
                continue;
            }
            CategoryContent categoryContent = category.getCategoryContent();
            if (null != categoryContent) {
                categoryContent.setSelectedPosition(0);
            }
            resetCustomMakeup(Util.upperCaseName(category.getDisplayName()));
        }
    }

    /**
     * 应用单状
     * Application simplex
     *
     * @param makeupType the makeup type
     */
    public void resetCustomMakeup(String makeupType) {
        mMakeupHelper.setCaptureMakeupIntensity(makeupType, MakeUpSingleView.DEFAULT_MAKEUP_INTENSITY);
        mMakeupHelper.setCaptureMakeupColor(makeupType, new NvsColor(0, 0, 0, 0));
        mMakeupHelper.setCaptureMakeupPackageId(makeupType, "");
    }

    private void stopRecording() {
        stopMusic();
        mStreamingContext.stopRecording();
        if (mRecordType == Constants.RECORD_TYPE_VIDEO) {
            mAllRecordingTime += mEachRecodingVideoTime;
            mRecordTimeList.add(mEachRecodingVideoTime);
            mStartText.setText(mRecordTimeList.size() + "");
            changeRecordDisplay(RECORD_FINISH, false);
        } else {
            changeRecordDisplay(RECORD_FINISH, true);
        }
    }

    private void stopMusic() {
        if (mSelectMusicInfo != null) {
            AudioPlayer.getInstance(this).pause();
        }
    }

    @MSSubscribe(Constants.SubscribeType.SUB_REMO_ALL_FILTER_TYPE)
    private void removeAllFilterFx() {
        List<Integer> remove_list = new ArrayList<>();
        for (int i = 0; i < mStreamingContext.getCaptureVideoFxCount(); i++) {
            NvsCaptureVideoFx fx = mStreamingContext.getCaptureVideoFxByIndex(i);
            if (fx == null) {
                continue;
            }
            if (fx.getAttachment(Constants.BG_SEG_EFFECT_ATTACH_KEY) != null) {
                boolean isBgSet = (boolean) fx.getAttachment(Constants.BG_SEG_EFFECT_ATTACH_KEY);
                if (isBgSet) {
                    continue;
                }
            }
            String name = fx.getBuiltinCaptureVideoFxName();
            String packageId = fx.getCaptureVideoFxPackageId();
            if (!"Beauty".equals(name) && !"Face Effect".equals(name) && !"AR Scene".equals(name)) {
                if (TextUtils.isEmpty(packageId) && !TextUtils.isEmpty(name) && (mCurrentFilterInfo != null && TextUtils.equals(name, mCurrentFilterInfo.getName()))) {
                    remove_list.add(i);
                } else if ((mCurrentFilterInfo != null && TextUtils.equals(packageId, mCurrentFilterInfo.getId()))) {
                    remove_list.add(i);
                } else {
                    boolean remove = false;
                    if (preMakeUp != null) {
                        MakeupParamContent effectContent = preMakeUp.getEffectContent();
                        if (effectContent != null) {
                            List<FilterParam> filterParamList = effectContent.getFilterParams();
                            for (FilterParam filterArgs : filterParamList) {
                                if (TextUtils.equals(packageId, filterArgs.getPackageId())) {
                                    remove = filterArgs.canReplace();
                                    break;
                                }
                            }
                        }
                    }
                    if (remove) {
                        remove_list.add(i);
                    }
                }
            }
        }
        if (!remove_list.isEmpty()) {
            for (int i = remove_list.size() - 1; i >= 0; i--) {
                mStreamingContext.removeCaptureVideoFx(remove_list.get(i));
            }
        }
        mCurrentFilterInfo = null;
    }

    private boolean removeFilterFxByName(String name) {
        for (int i = 0; i < mStreamingContext.getCaptureVideoFxCount(); i++) {
            NvsCaptureVideoFx fx = mStreamingContext.getCaptureVideoFxByIndex(i);
            String name1 = fx.getDescription().getName();
            if (name1.equals(name)) {
                mStreamingContext.removeCaptureVideoFx(i);
                return true;
            }
        }
        return false;
    }

    /**
     * 显示对话框窗口
     * Show dialog window
     */
    private void showCaptureDialogView(Dialog dialog, View view) {
        showCaptureDialogView(dialog, view, false);
    }

    /**
     * 显示对话框窗口
     * Show dialog window
     */
    private void showCaptureDialogView(Dialog dialog, View view, boolean matchParent) {
        TranslateAnimation translate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        translate.setDuration(200);
        translate.setFillAfter(false);
        dialog.show();
        if (view != null) {
            dialog.setContentView(view);
        }
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        if (matchParent) {
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }
        params.dimAmount = 0.0f;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.colorTranslucent));
        dialog.getWindow().setWindowAnimations(R.style.fx_dlg_style);
    }

    /**
     * 关闭对话框窗口
     * Close dialog window
     */
    private void closeCaptureDialogView(Dialog dialog) {
        dialog.dismiss();
        TranslateAnimation translate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        translate.setDuration(300);
        translate.setFillAfter(false);
    }


    /**
     * 改动拍摄屏幕比例
     * Change the capture screen ratio
     **/
    private void changeAspectRatio() {
        int screenHeight = ScreenUtils.getScreenHeight();
        int ratioHeight = ScreenUtils.getScreenWidth() / 9 * 16;
        if (ratioHeight < screenHeight) {
            mLiveWindow.post(new Runnable() {
                @Override
                public void run() {
                    int bottomHeight = mFlBottomParent.getHeight();
                    int largeHeight = screenHeight - ratioHeight;
//                    Log.e("meicam", "screenHeight:" + screenHeight + "  ratioHeight:" + ratioHeight
//                            + "  largeHeight:" + largeHeight + "  bottomHeight:" + bottomHeight);
                    //设置livewindow和DrawRect高度
                    ConstraintLayout.LayoutParams livewindowParams = (ConstraintLayout.LayoutParams) mLiveWindow.getLayoutParams();
                    livewindowParams.height = ratioHeight;
                    mLiveWindow.setLayoutParams(livewindowParams);

                    ConstraintLayout.LayoutParams drawRectParams = (ConstraintLayout.LayoutParams) drawRect.getLayoutParams();
                    drawRectParams.height = ratioHeight;
                    drawRect.setLayoutParams(drawRectParams);
                    if (largeHeight > bottomHeight) {
                        ConstraintLayout.LayoutParams bottomParams = (ConstraintLayout.LayoutParams) mFlBottomParent.getLayoutParams();
                        bottomParams.height = largeHeight;
                        mFlBottomParent.setLayoutParams(bottomParams);
                    }
                }
            });
        } else {
            ConstraintLayout.LayoutParams livewindowParams = (ConstraintLayout.LayoutParams) mLiveWindow.getLayoutParams();
            livewindowParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
            livewindowParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
            mLiveWindow.setLayoutParams(livewindowParams);
//            mFlBottomParent.setBackgroundColor(Color.TRANSPARENT);
        }

    }

    private void initCaptionFontInfoList() {
        String fontJsonPath = "font/info.json";
        String fontJsonText = ParseJsonFile.readAssetJsonFile(this, fontJsonPath);
        if (TextUtils.isEmpty(fontJsonText)) {
            return;
        }
        ArrayList<FontInfo> fontInfoList = ParseJsonFile.fromJson(fontJsonText, new TypeToken<List<FontInfo>>() {
        }.getType());
        if (fontInfoList == null) {
            return;
        }
        int fontCount = fontInfoList.size();
        for (int idx = 0; idx < fontCount; idx++) {
            FontInfo fontInfo = fontInfoList.get(idx);
            if (fontInfo == null) {
                continue;
            }
            String fontAssetPath = "assets:/font/" + fontInfo.getFontFileName();
            mStreamingContext.registerFontByFilePath(fontAssetPath);
        }

        String fontJsonPathSD = Environment.getExternalStorageDirectory() + "/NvStreamingSdk/Asset/Font/info.json";
        if (AndroidOS.USE_SCOPED_STORAGE) {
            fontJsonPathSD = getExternalFilesDir("") + "/NvStreamingSdk/Asset/Font/info.json";
        }
        String fontJsonTextSD = ParseJsonFile.readSDJsonFile(this, fontJsonPathSD);
        if (TextUtils.isEmpty(fontJsonTextSD)) {
            return;
        }
        ArrayList<FontInfo> fontInfoListSD = ParseJsonFile.fromJson(fontJsonTextSD, new TypeToken<List<FontInfo>>() {
        }.getType());
        if (fontInfoListSD == null) {
            return;
        }
        int fontCountSD = fontInfoListSD.size();
        for (int idx = 0; idx < fontCountSD; idx++) {
            FontInfo fontInfo = fontInfoListSD.get(idx);
            if (fontInfo == null) {
                continue;
            }

            String fontAssetPathSD = Environment.getExternalStorageDirectory() + "/NvStreamingSdk/Asset/Font/" + fontInfo.getFontFileName();
            if (AndroidOS.USE_SCOPED_STORAGE) {
                fontAssetPathSD = getExternalFilesDir("") + "/NvStreamingSdk/Asset/Font/" + fontInfo.getFontFileName();
            }
            mStreamingContext.registerFontByFilePath(fontAssetPathSD);
        }
    }

    private void initCaptureData() {
        mStreamingContext.removeAllCaptureVideoFx();
        mFocusAnimation = new AlphaAnimation(1.0f, 0.0f);
        mFocusAnimation.setDuration(1000);
        mFocusAnimation.setFillAfter(true);
        mCanUseARFaceType = NvsStreamingContext.hasARModule();
    }

    private void initCapture() {
        if (null == mStreamingContext) {
            return;
        }
        /*
         *给Streaming Context设置回调接口
         *Set callback interface for Streaming Context
         * */
        setStreamingCallback(false);
        int captureDeviceCount = mStreamingContext.getCaptureDeviceCount();
        if (captureDeviceCount == 0) {
            return;
        }

        /*
         * 将采集预览输出连接到LiveWindow控件
         * Connect the capture preview output to the LiveWindow control
         * */
        if (mLiveWindow instanceof MSLiveWindow) {
            if (!mStreamingContext.connectCapturePreviewWithLiveWindow((MSLiveWindow) mLiveWindow)) {
                Log.e(TAG, "Failed to connect capture preview with livewindow!");
                return;
            }

        } else if (mLiveWindow instanceof MSLiveWindowExt) {
            if (!mStreamingContext.connectCapturePreviewWithLiveWindowExt((MSLiveWindowExt) mLiveWindow)) {
                Log.e(TAG, "Failed to connect capture preview with livewindow!");
                return;
            }
        } else {
            return;
        }

        /*
         * 采集设备数量判定
         * Judging the count of collection equipment
         * */
        if (captureDeviceCount > 1) {
            mCurrentDeviceIndex = 1;
            mIvChangeCamera.setEnabled(true);
        } else {
            mCurrentDeviceIndex = 0;
            mIvChangeCamera.setEnabled(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        } else {
            try {
                startCapturePreview(false);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "startCapturePreviewException: initCapture failed,under 6.0 device may has no access to camera");
                PermissionDialog.noPermissionDialog(CaptureActivity.this);
            }
        }
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                initArScene = bundle.getBoolean("initArScene");
            }
        }
    }

    private boolean startCapturePreview(boolean deviceChanged) {
        mStartPreviewTime = System.currentTimeMillis();
        /*
         * 判断当前引擎状态是否为采集预览状态
         * Determine if the current engine status is the collection preview status
         * */
        int captureResolutionGrade = ParameterSettingValues.instance().getCaptureResolutionGrade();
        if (deviceChanged || getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTUREPREVIEW) {
            m_supportAutoFocus = false;
            if (!mStreamingContext.startCapturePreview(mCurrentDeviceIndex, captureResolutionGrade,
                    NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_DONT_USE_SYSTEM_RECORDER |
                            NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_CAPTURE_BUDDY_HOST_VIDEO_FRAME |
                            NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_ENABLE_TAKE_PICTURE |
                            NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_STRICT_PREVIEW_VIDEO_SIZE, null)) {
                Log.e(TAG, "Failed to start capture preview!");
                return false;
            }
        }
        checkFlashState(mCurrentDeviceIndex != 1);
        return true;
    }

    /**
     * 获取当前引擎状态
     * Get the current engine status
     */
    private int getCurrentEngineState() {
        return mStreamingContext.getStreamingEngineState();
    }

    private void updateSettingsWithCapability(int deviceIndex) {
        /*
         * 获取采集设备能力描述对象，设置自动聚焦，曝光补偿，缩放
         * Get acquisition device capability description object, set auto focus, exposure compensation, zoom
         * */
        mCapability = mStreamingContext.getCaptureDeviceCapability(deviceIndex);
        if (null == mCapability) {
            return;
        }
        m_supportAutoFocus = mCapability.supportAutoFocus;
        if (mMoreDialog == null) {
            initTopMoreView();
        }
        mMoreDialog.checkCapability(mCapability);
    }

    private void changeRecordDisplay(int recordState, boolean isPicture) {
        mRecordState = recordState;
        if (RECORD_DEFAULT == recordState) {
            changeSelectMusicState(true);
            mIvExit.setVisibility(View.VISIBLE);
            mIvChangeCamera.setVisibility(View.VISIBLE);
            mLlRightContainer.setVisibility(View.VISIBLE);

            mIvMore.setVisibility(View.VISIBLE);
            mSelectMusicLayout.setVisibility(View.VISIBLE);
            mCaptureVoice.setVisibility(isPicture ? View.GONE : View.VISIBLE);
            //mAudioNoise.setVisibility(isPicture ? View.GONE : View.VISIBLE);
            if (isPicture) {
                mIvTakePhotoBg.setImageResource(R.mipmap.capture_take_photo);
            } else {
                mIvTakePhotoBg.setImageResource(R.mipmap.capture_take_video);
            }
            mStartText.setVisibility(View.INVISIBLE);

            mCaptureMakeup.setVisibility(View.VISIBLE);
            mCaptureBeauty.setVisibility(View.VISIBLE);
            mCaptureFilter.setVisibility(View.VISIBLE);
            mCaptureProp.setVisibility(View.VISIBLE);

            mDelete.setVisibility(View.INVISIBLE);
            mVideoTimeDot.setVisibility(View.INVISIBLE);
            mNext.setVisibility(View.INVISIBLE);

            mTvChoosePicture.setVisibility(View.VISIBLE);
            mTvChooseVideo.setVisibility(View.VISIBLE);
        } else if (RECORDING == recordState) {
            mIvExit.setVisibility(View.GONE);
            mIvChangeCamera.setVisibility(View.GONE);
            mLlRightContainer.setVisibility(View.GONE);
            mIvMore.setVisibility(View.GONE);
            mSelectMusicLayout.setVisibility(View.GONE);
            mCaptureVoice.setVisibility(View.GONE);
            //mAudioNoise.setVisibility(View.GONE);
            if (isPicture) {
                mVideoTimeDot.setVisibility(View.INVISIBLE);
                mRecordTime.setVisibility(View.INVISIBLE);
            } else {
                mIvTakePhotoBg.setImageResource(R.mipmap.capture_stop_video);
                if (mFlMiddleParent.getVisibility() != View.VISIBLE) {
                    mFlMiddleParent.setVisibility(View.VISIBLE);
                }
                mVideoTimeDot.setVisibility(View.VISIBLE);
                mRecordTime.setVisibility(View.VISIBLE);
            }
            mStartText.setVisibility(View.INVISIBLE);

            mCaptureMakeup.setVisibility(View.INVISIBLE);
            mCaptureBeauty.setVisibility(View.INVISIBLE);
            mCaptureFilter.setVisibility(View.INVISIBLE);
            mCaptureProp.setVisibility(View.INVISIBLE);

            mDelete.setVisibility(View.INVISIBLE);
            mNext.setVisibility(View.INVISIBLE);

            mTvChoosePicture.setVisibility(View.INVISIBLE);
            mTvChooseVideo.setVisibility(View.INVISIBLE);
        } else if (RECORD_FINISH == recordState) {
            changeSelectMusicState(false);
            mIvExit.setVisibility(View.VISIBLE);
            mIvChangeCamera.setVisibility(View.VISIBLE);
            mLlRightContainer.setVisibility(View.VISIBLE);
            mIvMore.setVisibility(View.VISIBLE);
            mSelectMusicLayout.setVisibility(View.VISIBLE);
            //mAudioNoise.setVisibility(isPicture ? View.GONE : View.VISIBLE);
            mCaptureVoice.setVisibility(isPicture ? View.GONE : View.VISIBLE);
            mIvTakePhotoBg.setImageResource(R.mipmap.capture_take_photo);
            mStartText.setVisibility(View.VISIBLE);

            mCaptureMakeup.setVisibility(View.VISIBLE);
            mCaptureBeauty.setVisibility(View.VISIBLE);
            mCaptureFilter.setVisibility(View.VISIBLE);
            mCaptureProp.setVisibility(View.VISIBLE);

            if (mFlMiddleParent.getVisibility() != View.VISIBLE) {
                mFlMiddleParent.setVisibility(View.VISIBLE);
            }
            mDelete.setVisibility(View.VISIBLE);
            mVideoTimeDot.setVisibility(View.INVISIBLE);
            mRecordTime.setVisibility(View.VISIBLE);
            mNext.setVisibility(View.VISIBLE);

            mTvChoosePicture.setVisibility(View.VISIBLE);
            mTvChooseVideo.setVisibility(View.VISIBLE);
        }
        if (mRecordTimeList.isEmpty()) {
            mRecordTime.setVisibility(View.INVISIBLE);
        }
    }


    private ArrayList<NvAsset> getLocalData(int assetType) {
        return mAssetManager.getUsableAssets(assetType, NvAsset.AspectRatio_All, 0);
    }


    @Override
    public void onClick(View view) {
    }

    @Override
    public void onCaptureDeviceCapsReady(int captureDeviceIndex) {
        if (captureDeviceIndex != mCurrentDeviceIndex) {
            return;
        }
        updateSettingsWithCapability(captureDeviceIndex);
    }

    @Override
    public void onCaptureDevicePreviewResolutionReady(int i) {
    }

    @Override
    public void onCaptureDevicePreviewStarted(int i) {
        mIsSwitchingCamera = false;
    }

    @Override
    public void onCaptureDeviceError(int i, int i1) {
        Log.e(TAG, "onCaptureDeviceError: initCapture failed,under 6.0 device may has no access to camera");
        PermissionDialog.noPermissionDialog(CaptureActivity.this);
    }

    @Override
    public void onCaptureDeviceStopped(int i) {

    }

    @Override
    public void onCaptureDeviceAutoFocusComplete(int i, boolean b) {

    }

    @Override
    public void onCaptureRecordingFinished(int i) {
        /*
         *  保存到媒体库
         * Save to media library
         * */
        if (mRecordFileList != null && !mRecordFileList.isEmpty()) {
            for (String path : mRecordFileList) {
                if (path == null) {
                    continue;
                }
                if (path.endsWith(".mp4")) {
                    MediaScannerUtil.scanFile(path, "video/mp4");
                } else if (path.endsWith(".jpg")) {
                    MediaScannerUtil.scanFile(path, "image/jpg");
                }
            }
        }
    }

    @Override
    public void onCaptureRecordingError(int i) {

    }

    @Override
    public void onCaptureRecordingDuration(int i, long l) {
        /*
         * 拍视频or照片
         * Take a video or a photo
         * */
        if (mRecordType == Constants.RECORD_TYPE_VIDEO) {
            if (l >= Constants.MIN_RECORD_DURATION) {
                mFlStartRecord.setEnabled(true);
            }
            if (mFlMiddleParent.getVisibility() != View.VISIBLE) {
                mFlMiddleParent.setVisibility(View.VISIBLE);
            }
            mRecordTime.setVisibility(View.VISIBLE);
            if (mRecordState != RECORDING) {
                return;
            }
            mEachRecodingVideoTime = l;
            String totalTime = TimeFormatUtil.formatUsToString2(mAllRecordingTime + mEachRecodingVideoTime);
            mRecordTime.setText(totalTime);
        } else if (mRecordType == Constants.RECORD_TYPE_PICTURE) {
            stopRecording();
        }
    }

    @Override
    public void onCaptureRecordingStarted(int i) {

    }

    @Override
    public void onCapturedPictureArrived(final ByteBuffer byteBuffer, NvsVideoFrameInfo nvsVideoFrameInfo) {
        runOnUiThread(new Runnable() {
            @SuppressLint("CheckResult")
            @Override
            public void run() {
                NvsVideoResolution mCurrentVideoResolution = new NvsVideoResolution();
                mCurrentVideoResolution.imageWidth = 720;
                mCurrentVideoResolution.imageHeight = 1280;
                mCurrentVideoResolution.imagePAR = new NvsRational(1, 1);

                int makeRatio = NvAsset.AspectRatio_9v16;
                NvsVideoResolution videoEditResolution = Util.getVideoEditResolution(makeRatio);

                int captureVideoFxCount = mStreamingContext.getCaptureVideoFxCount();

                List<NvsEffect> effects = new ArrayList<>();
                NvsRational nvsRational = new NvsRational(9, 16);

                for (int i = 0; i < captureVideoFxCount; i++) {
                    NvsCaptureVideoFx captureVideoFxByIndex = mStreamingContext.getCaptureVideoFxByIndex(i);
                    if (captureVideoFxByIndex.getBuiltinCaptureVideoFxName().equals(Constants.AR_SCENE)) {
                        String stringVal = captureVideoFxByIndex.getStringVal(Constants.AR_SCENE_ID_KEY);
                        NvsVideoEffect videoEffect = mNvsEffectSdkContext.createVideoEffect(Constants.AR_SCENE, nvsRational);

                        if (videoEffect != null) {
                            if (BuildConfig.FACE_MODEL == 240) {
                                videoEffect.setBooleanVal("Use Face Extra Info", true);
                            }
                            NvsARSceneManipulate arSceneManipulate = videoEffect.getARSceneManipulate();

                            //支持的人脸个数，是否需要使用最小的设置
//                            The number of faces supported. Do you need to use the minimum setting
                            videoEffect.setBooleanVal(Constants.MAX_FACES_RESPECT_MIN, true);

                            videoEffect.setBooleanVal("Beauty Effect", true);
                            videoEffect.setBooleanVal("Beauty Shape", true);
                            videoEffect.setBooleanVal("Face Mesh Internal Enabled", true);
                            videoEffect.setBooleanVal("Advanced Beauty Enable", true);

                            if (arSceneManipulate != null) {
                                arSceneManipulate.setDetectionMode(NvsStreamingContext.HUMAN_DETECTION_FEATURE_IMAGE_MODE);
                                arSceneManipulate.setDetectionAutoProbe(true);
                            }

                            if (!TextUtils.isEmpty(stringVal)) {
                                videoEffect.setStringVal(Constants.AR_SCENE_ID_KEY, stringVal);
                            }

                            double advanced_beauty_intensity = captureVideoFxByIndex.getFloatVal("Advanced Beauty Intensity");
                            int advanced_beauty_type = captureVideoFxByIndex.getIntVal("Advanced Beauty Type");
                            double beautyWhitening = captureVideoFxByIndex.getFloatVal("Beauty Whitening");
                            double beautyRedding = captureVideoFxByIndex.getFloatVal("Beauty Reddening");
                            double beautyStrength = captureVideoFxByIndex.getFloatVal("Beauty Strength");

                            videoEffect.setIntVal("Advanced Beauty Type", advanced_beauty_type);
                            videoEffect.setFloatVal("Advanced Beauty Intensity", advanced_beauty_intensity);
                            videoEffect.setFloatVal("Beauty Whitening", beautyWhitening);
                            videoEffect.setFloatVal("Beauty Reddening", beautyRedding);
                            videoEffect.setFloatVal("Beauty Strength", beautyStrength);

                            if (mBuffingSkinList != null && mBuffingSkinList.size() > 0) {
                                for (IFxInfo beautyShapeDataItem : mBuffingSkinList) {
                                    if (beautyShapeDataItem == null) {
                                        continue;
                                    }
                                    String beautyShapeId = beautyShapeDataItem.getFxName();
                                    if (TextUtils.isEmpty(beautyShapeId)) {
                                        continue;
                                    }
                                    double floatVal = captureVideoFxByIndex.getFloatVal(beautyShapeId);
                                    videoEffect.setFloatVal(beautyShapeId, floatVal);
                                }
                            }

                            if (mSmallShapeDataList != null && mSmallShapeDataList.size() > 0) {
                                for (IFxInfo beautyShapeDataItem : mSmallShapeDataList) {
                                    if (beautyShapeDataItem == null) {
                                        continue;
                                    }
                                    String beautyShapeId = beautyShapeDataItem.getFxName();
                                    if (TextUtils.isEmpty(beautyShapeId)) {
                                        continue;
                                    }
                                    double floatVal = captureVideoFxByIndex.getFloatVal(beautyShapeDataItem.getFxName());
                                    videoEffect.setFloatVal(beautyShapeDataItem.getFxName(), floatVal);
                                }
                            }

                            if (mShapeDataList != null && mShapeDataList.size() > 0) {
                                for (IFxInfo beautyShapeDataItem : mShapeDataList) {
                                    if (beautyShapeDataItem == null) {
                                        continue;
                                    }
                                    String beautyShapeId = beautyShapeDataItem.getFxName();
                                    if (TextUtils.isEmpty(beautyShapeId)) {
                                        continue;
                                    }
                                    double floatVal = captureVideoFxByIndex.getFloatVal(beautyShapeId);
                                    videoEffect.setFloatVal(beautyShapeId, floatVal);
                                }
                            }

                            effects.add(0, videoEffect);
                        }

                    }

                    String captureVideoFxPackageId = captureVideoFxByIndex.getCaptureVideoFxPackageId();
                    float filterIntensity = captureVideoFxByIndex.getFilterIntensity();
                    if (!TextUtils.isEmpty(captureVideoFxPackageId)) {
                        NvsVideoEffect videoEffect = mNvsEffectSdkContext.createVideoEffect(captureVideoFxPackageId, nvsRational);
                        videoEffect.setFilterIntensity(filterIntensity);
                        effects.add(videoEffect);
                    }
                }


                int captureCompoundCaptionCount = mStreamingContext.getCaptureCompoundCaptionCount();
                for (int i = 0; i < captureCompoundCaptionCount; i++) {
                    NvsCaptureCompoundCaption captureCompoundCaption = mStreamingContext.getCaptureCompoundCaptionByIndex(i);

                    NvsVideoEffectCompoundCaption compoundCaptionFilter = mNvsEffectSdkContext.
                            createCompoundCaption(0, Long.MAX_VALUE, captureCompoundCaption.
                                    getCaptionStylePackageId(), nvsRational);

                    int captionCount = captureCompoundCaption.getCaptionCount();
                    for (int j = 0; j < captionCount; j++) {
                        String text = captureCompoundCaption.getText(j);
                        String fontFamily = captureCompoundCaption.getFontFamily(j);
                        NvsColor textColor = captureCompoundCaption.getTextColor(j);

                        compoundCaptionFilter.setTextColor(j, textColor);
                        compoundCaptionFilter.setFontFamily(j, fontFamily);
                        compoundCaptionFilter.setText(j, text);
                    }

                    PointF captionTranslation = captureCompoundCaption.getCaptionTranslation();
                    float scaleX = captureCompoundCaption.getScaleX();
                    float scaleY = captureCompoundCaption.getScaleY();
                    float rotationZ = captureCompoundCaption.getRotationZ();
                    float opacity = captureCompoundCaption.getOpacity();

                    compoundCaptionFilter.setScaleX(scaleX);
                    compoundCaptionFilter.setScaleY(scaleY);
                    compoundCaptionFilter.setRotationZ(rotationZ);
                    compoundCaptionFilter.setOpacity(opacity);
                    compoundCaptionFilter.setCaptionTranslation(captionTranslation);

                    if (compoundCaptionFilter != null) {
                        effects.add(compoundCaptionFilter);
                    }
                }

                int captureAnimatedStickerCount = mStreamingContext.getCaptureAnimatedStickerCount();
                for (int i = 0; i < captureAnimatedStickerCount; i++) {
                    NvsCaptureAnimatedSticker captureAnimatedStickerByIndex = mStreamingContext.
                            getCaptureAnimatedStickerByIndex(i);
                    PointF translation = captureAnimatedStickerByIndex.getTranslation();
                    String animatedStickerPackageId = captureAnimatedStickerByIndex.getAnimatedStickerPackageId();
                    if (!TextUtils.isEmpty(animatedStickerPackageId)) {
                        NvsVideoEffectAnimatedSticker stickerFilter = mNvsEffectSdkContext.
                                createAnimatedSticker(0, STICK_TIME_DURATION /*Long.MAX_VALUE*/, false,
                                        animatedStickerPackageId, nvsRational);
                        stickerFilter.setScale(0.5f);
                        double v = translation.x * 0.5;
                        double v1 = translation.y * 0.5;
                        translation.x = (float) v;
                        translation.y = (float) v1;
                        stickerFilter.translateAnimatedSticker(translation);
                        if (stickerFilter != null) {
                            effects.add(stickerFilter);
                        }
                    }
                }

                Observable.just(effects).map(nvsEffects -> {
                    Bitmap pictureBitmap = Bitmap.createBitmap(nvsVideoFrameInfo.frameWidth,
                            nvsVideoFrameInfo.frameHeight, Bitmap.Config.ARGB_8888);
                    if (effects.size() > 0) {
                        mEffectRenderCore.initialize(NvsEffectRenderCore.NV_EFFECT_CORE_FLAGS_SUPPORT_8K |
                                NvsEffectRenderCore.NV_EFFECT_CORE_FLAGS_CREATE_GLCONTEXT_IF_NEED);
                        ByteBuffer byteBufferResult = mEffectRenderCore.renderEffects(effects.toArray(new NvsEffect[effects.size()]),
                                byteBuffer.array(), nvsVideoFrameInfo, 0,
                                NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_RGBA, false,
                                (System.currentTimeMillis() - mStartPreviewTime) * 1000, 0);

                        for (int i = 0; i < effects.size(); i++) {
                            NvsEffect nvsEffect = effects.get(i);
                            mEffectRenderCore.clearEffectResources(nvsEffect);
                            nvsEffect = null;
                        }
                        effects.clear();
                        mEffectRenderCore.clearCacheResources();
                        mEffectRenderCore.cleanUp();
                        mEffectRenderCore.release();
                        if (null != byteBufferResult) {
                            pictureBitmap.copyPixelsFromBuffer(byteBufferResult);
                            byteBufferResult.clear();
                            byteBuffer.clear();
                        } else {
                            pictureBitmap.copyPixelsFromBuffer(byteBuffer);
                        }
                    } else {
                        pictureBitmap.copyPixelsFromBuffer(byteBuffer);
                    }
                    return pictureBitmap;
                }).observeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        mPictureBitmap = bitmap;
                        if (mPictureBitmap != null) {
                            mPictureImage.setImageBitmap(mPictureBitmap);
                            showPictureLayout(true);
                        } else {
                            changeRecordDisplay(RECORD_DEFAULT, true);
                        }
                    }
                }).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Logger.e(throwable.getMessage());
                    }
                }).subscribe();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_FILTER_LIST_CODE:
                    initFilterList();
                    mFilterBottomView.setFilterArrayList(mFilterDataArrayList);
                    mFilterSelPos = AssetFxUtil.getSelectedFilterPos(mFilterDataArrayList, mVideoClipFxInfo);
                    mFilterBottomView.setSelectedPos(mFilterSelPos);
                    mFilterBottomView.notifyDataSetChanged();
                    break;
                case REQUEST_CODE_BACKGROUND_SEG:
                    setBgSeg(data);
                    break;
                case Constants.ACTIVITY_START_CODE_MUSIC_SINGLE:
                    MusicInfo musicInfo = (MusicInfo) data.getSerializableExtra("select_music");
                    mSelectMusicInfo = musicInfo;
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 设置抠像
     * Set matting
     *
     * @param isAddSeg isAddSeg
     */
    private void doBgSeg(boolean isAddSeg) {
        if (!isAddSeg) {
            removeSegEffect();
            mArSceneFaceEffect.setBooleanVal("Single Buffer Mode", parameterValues.isSingleBufferMode());
            return;
        }
        if (mBgSegEffect == null) {
            mBgSegEffect = mStreamingContext.insertBuiltinCaptureVideoFx("Segmentation Background Fill", 0);
            mBgSegEffect.setAttachment(Constants.BG_SEG_EFFECT_ATTACH_KEY, true);
            mBgSegEffect.setMenuVal(Constants.KEY_SEGMENT_TYPE, Constants.SEGMENT_TYPE_BACKGROUND);
            if (!mStreamingContext.isAndroidCameraPreferDualBufferAR()) {
                mArSceneFaceEffect.setBooleanVal("Single Buffer Mode", false);
            }
        }
        if (mMakeupHelper != null) {
            mMakeupHelper.addWhiteListFx("Segmentation Background Fill");
        }
        /*1:铺满（可能会被拉伸）  0：自适应*/
        /*1: Pave (may be stretched) 0: Adaptive*/
        mBgSegEffect.setIntVal(Constants.KEY_SEGMENT_STRETCH_MODE, 1);
        mBgSegEffect.setColorVal(Constants.KEY_SEGMENT_BACKGROUND_COLOR, new NvsColor(0.0f, 0.0f, 0.0f, 0.0f));
    }

    /**
     * 移除抠像
     * Remove matting
     */
    private void removeSegEffect() {
        if (mBgSegEffect != null) {
            int index = mBgSegEffect.getIndex();
            mStreamingContext.removeCaptureVideoFx(index);
            mStreamingContext.removeCurrentCaptureScene();
            mBgSegEffect = null;
        }
    }

    /**
     * 设置抠像背景
     * Set matting background
     *
     * @param intent intent
     */
    private void setBgSeg(Intent intent) {
        if (null == intent) {
            return;
        }
        String clickType = intent.getStringExtra(SINGLE_PICTURE_CLICK_TYPE);
        if (TextUtils.equals(clickType, SINGLE_PICTURE_CLICK_CANCEL)) {
            mStreamingContext.removeCurrentCaptureScene();
            return;
        }
        if (TextUtils.equals(clickType, SINGLE_PICTURE_CLICK_CONFIRM)) {
            String filePath = intent.getStringExtra(SINGLE_PICTURE_PATH);
            NvsCaptureSceneInfo sceneInfo = new NvsCaptureSceneInfo();
            List<NvsCaptureSceneInfo.ClipData> clipDataList = new ArrayList<>();
            NvsCaptureSceneInfo.ClipData clipData = new NvsCaptureSceneInfo.ClipData();
            //视频 Video
            clipData.scan = 1;
            //图片 Image
            clipData.imageFillMode = NvsCaptureSceneInfo.CAPTURESCENE_INFO_IMAGE_FILLMODE_CROP;
            clipData.mediaPath = filePath;
            clipDataList.add(clipData);
            sceneInfo.backgroundClipArray = clipDataList;
            mStreamingContext.applyCaptureScene(mBgSegPackageId, sceneInfo);
        }
    }

    @Override
    protected List<String> initPermissions() {
        return Util.getAllPermissionsList();
    }

    @Override
    protected void hasPermission() {
        startCapturePreview(false);
    }

    @Override
    protected void nonePermission() {
        PermissionDialog.noPermissionDialog(CaptureActivity.this);
    }

    @Override
    protected void noPromptPermission() {
        PermissionDialog.noPermissionDialog(CaptureActivity.this);
    }

    @Override
    protected void onDestroy() {
        closeCaptureFrame();
        AudioPlayer.getInstance(getApplicationContext()).destroyPlayer();
        if (mMoreDialog != null) {
            mMoreDialog.dismiss();
        }
        destroy();
        MSBus.getInstance().unregister(this);
        mOrientationListener.disable();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNext != null) {
            mNext.setClickable(true);
        }
        if (mFilterBottomView != null) {
            mFilterBottomView.setMoreFilterClickable(true);
        }
        startCapturePreview(false);

        //onActivityResult早于Resume，setAECEnabled要等Preview设置才有效果
        if (mSelectMusicInfo == null) {
            mStreamingContext.setAECEnabled(false);
            mSelectMusicName.setText(getResources().getString(R.string.select_music));
        } else {
            mStreamingContext.setAECEnabled(true);
            mSelectMusicName.setText(mSelectMusicInfo.getTitle());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTUREPREVIEW) {
            mStreamingContext.stop();
        }
        stopMusic();
        if (getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTURERECORDING) {
            stopRecording();
        }
        checkFlashState(false);
    }

    private void destroy() {
        mRecordTimeList.clear();
        mRecordFileList.clear();
        mFilterDataArrayList.clear();
        mPropsList.clear();
        AppRepository.AppRepositoryHelper.getInstance().destory();
    }

    /**
     * 停止流媒体上下文
     * 注意：不要在onDestroy方法中调用
     * Stop the streaming media context.
     * Note: Do not call in the onDestroy method.
     */
    private void stopStreamingContext() {
        if (mStreamingContext != null) {
            mStreamingContext.removeCurrentCaptureScene();
            mStreamingContext.removeAllCaptureCompoundCaption();
            mStreamingContext.removeAllCaptureAnimatedSticker();
            mStreamingContext.removeAllCaptureVideoFx();
            mStreamingContext.stop();
            setStreamingCallback(true);
        }
    }

//    private void takePhoto(long time) {
//        if (mCurRecordVideoPath != null) {
//            NvsVideoFrameRetriever videoFrameRetriever = mStreamingContext.createVideoFrameRetriever(mCurRecordVideoPath);
//            if (videoFrameRetriever != null) {
//                int screenHeight = ScreenUtils.getScreenHeight(this);
//                screenHeight = (int) (screenHeight / 16) * 16;
//                //videoFrameRetriever.getFrameAtTimeWithCustomVideoFrameHeight 需要传入被16整除的数字
//                mPictureBitmap = videoFrameRetriever.getFrameAtTimeWithCustomVideoFrameHeight(time, screenHeight);
//                Log.d("takePhoto", " 被16整除的height" + screenHeight + "  screen: " + ScreenUtils.getScreenWidth(this) + " " + ScreenUtils.getScreenHeight(this) + "**bitmap=" + mPictureBitmap);
//                if (mPictureBitmap != null) {
//                    mPictureImage.setImageBitmap(mPictureBitmap);
//                    showPictureLayout(true);
//                } else {
//                    changeRecordDisplay(RECORD_DEFAULT, true);
//                }
//                videoFrameRetriever.release();
//            }
//        }
//    }

    private void setStreamingCallback(boolean isDestroyCallback) {
        mStreamingContext.setCaptureDeviceCallback(isDestroyCallback ? null : this);
        mStreamingContext.setCaptureRecordingDurationCallback(isDestroyCallback ? null : this);
        mStreamingContext.setCaptureRecordingStartedCallback(isDestroyCallback ? null : this);
        mStreamingContext.setCapturedPictureCallback(isDestroyCallback ? null : this);
    }

    private void takePhoto() {
        mStreamingContext.takePicture(0);
    }

    private void selectRecordType(boolean ivPicture) {
        int[] location = new int[2];
        mFlStartRecord.getLocationInWindow(location); //获取在当前窗口内的绝对坐标
        float middleX = location[0] + mFlStartRecord.getWidth() / 2f;
        float targetX;
        if (ivPicture) {
            if (mRecordType == Constants.RECORD_TYPE_PICTURE) {
                return;
            }
            targetX = middleX;
            mTvChoosePicture.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            mTvChoosePicture.setTextColor(ContextCompat.getColor(this, R.color.color_black));
            mTvChooseVideo.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mTvChooseVideo.setTextColor(ContextCompat.getColor(this, com.meishe.base.R.color.color_888888));
            mIvTakePhotoBg.setImageResource(R.mipmap.capture_take_photo);
            mRecordType = Constants.RECORD_TYPE_PICTURE;
            mCaptureVoice.setVisibility(View.GONE);
           // mAudioNoise.setVisibility(View.GONE);
        } else {
            mCaptureVoice.setVisibility(View.VISIBLE);
            //mAudioNoise.setVisibility(View.VISIBLE);
            mTvChooseVideo.getLocationInWindow(location);
            targetX = location[0] + mTvChooseVideo.getWidth() / 2f;
            mTvChooseVideo.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            mTvChooseVideo.setTextColor(ContextCompat.getColor(this, R.color.color_black));
            mTvChoosePicture.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mTvChoosePicture.setTextColor(ContextCompat.getColor(this, com.meishe.base.R.color.color_888888));
            mIvTakePhotoBg.setImageResource(R.mipmap.capture_take_video);
            mRecordType = Constants.RECORD_TYPE_VIDEO;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(mRecordTypeLayout, "translationX", middleX - targetX);
        animator.setDuration(300);
        animator.start();
    }

    private void showPictureLayout(boolean show) {
        TranslateAnimation topTranslate;
        if (show) {
            mRlPhotosLayout.setVisibility(View.INVISIBLE);
            topTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            topTranslate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mRlPhotosLayout.clearAnimation();
                    mIvExit.setVisibility(View.GONE);
                    mRlPhotosLayout.setVisibility(View.VISIBLE);
                    mRlPhotosLayout.setClickable(true);
                    mRlPhotosLayout.setFocusable(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } else {
            mFlStartRecord.setEnabled(true);
            mIvExit.setVisibility(View.VISIBLE);
            topTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);

            topTranslate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mRlPhotosLayout.clearAnimation();
                    mRlPhotosLayout.setVisibility(View.GONE);
                    mRlPhotosLayout.setClickable(false);
                    mRlPhotosLayout.setFocusable(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
        topTranslate.setDuration(300);
        topTranslate.setFillAfter(true);
        mRlPhotosLayout.setAnimation(topTranslate);
    }

    @Override
    public void onBackPressed() {
        if (isEffectFragmentVisible(Constants.FRAGMENT_BEAUTY_TAG)) {
            hideEffectFragment(Constants.FRAGMENT_BEAUTY_TAG);
            return;
        }
        if (isEffectFragmentVisible(Constants.FRAGMENT_FILTER_TAG)) {
            hideEffectFragment(Constants.FRAGMENT_FILTER_TAG);
            return;
        }
        if (isEffectFragmentVisible(Constants.FRAGMENT_PROP_TAG)) {
            hideEffectFragment(Constants.FRAGMENT_PROP_TAG);
            return;
        }
        stopStreamingContext();
        AppManager.getInstance().finishActivity();
    }

    private void changeSelectMusicState(boolean canSelect) {
        cp_musicIcon.setVisibility(canSelect ? View.VISIBLE : View.GONE);
        mSelectMusicName.setVisibility(canSelect ? View.VISIBLE : View.GONE);
    }
}
