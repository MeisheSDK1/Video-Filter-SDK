package com.meishe.sdkdemo.edit.clipEdit.correctionColor;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.base.view.MagicProgress;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.edit.clipEdit.SingleClipFragment;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.interfaces.OnTitleBarClickListener;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.edit.view.DenoiseAdjustView;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.CorrectionColorInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;

import java.util.ArrayList;
import java.util.List;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yyj
 * @CreateDate : 2019/6/28.
 * @Description :视频编辑-编辑-校色页面-Activity
 * @Description :VideoEdit-edit-Calibration-Activity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CorrectionColorActivity extends BaseActivity {
    private CustomTitleBar mTitleBar;
    private RelativeLayout mBottomLayout;
    private MagicProgress mColorSeekBar;
    private RecyclerView mColorTypeRv;
    private RelativeLayout ll_seek;
    private ColorTypeAdapter mColorTypeAdapter;
    private ImageView mCorrectionColorFinish;
    private SingleClipFragment mClipFragment;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private NvsVideoFx mColorVideoFx, mVignetteVideoFx, mSharpenVideoFx, mTintVideoFx, mDenoiseVideoFx, mCurrentVideoFx;
    private TextView mColorResetImageView;
    private ArrayList<ClipInfo> mClipArrayList;
    private int mCurClipIndex = 0;
    private String mCurrentColorType;
    private ColorTypeItem mCurrentAdjustItem;
    private static final float floatZero = 0.000001f;
    private String[] nameFxArray;
    private DenoiseAdjustView mDenoiseAdjustView;
    //调节噪点选择的模式 初始化为单色
    //Adjust the mode of noise selection Initialize to monochrome
    private boolean denoiseSingleColor = true;
    private CorrectionColorInfo correctionColorInfo;

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_correction_color;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottomLayout);
        mColorSeekBar = (MagicProgress) findViewById(R.id.colorSeekBar);
        mColorResetImageView = (TextView) findViewById(R.id.colorResetImageView);
        mCorrectionColorFinish = (ImageView) findViewById(R.id.correctionColorFinish);
        mColorTypeRv = (RecyclerView) findViewById(R.id.colorTypeRv);
        mDenoiseAdjustView = findViewById(R.id.denoise_adjust);
        ll_seek = findViewById(R.id.ll_seek);
        mColorSeekBar.setMax(200);
        mColorSeekBar.setPointEnable(true);
        mColorSeekBar.setBreakProgress(100);
        mColorSeekBar.setShowBreak(false);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.correctionColor);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        mClipArrayList = BackupData.instance().cloneClipInfoData();
        mCurClipIndex = BackupData.instance().getClipIndex();
        if (mCurClipIndex < 0 || mCurClipIndex >= mClipArrayList.size()) {
            return;
        }
        ClipInfo clipInfo = mClipArrayList.get(mCurClipIndex);
        if (clipInfo == null) {
            return;
        }
        correctionColorInfo = clipInfo.getCorrectionColorInfo();
        if (null == correctionColorInfo) {
            correctionColorInfo = new CorrectionColorInfo();
        }
        clipInfo.setCorrectionColorInfo(correctionColorInfo);
        mTimeline = TimelineUtil.createSingleClipTimeline(clipInfo, true);
        if (mTimeline == null) {
            return;
        }
        initColorAdjustVideoFx();
        initVideoFragment();
        initColorTypeRv();
        initColorSeekBar();
        int brightnessVal = getProgressByValue(correctionColorInfo.getBrightnessVal(), Constants.ADJUST_BRIGHTNESS);
        mColorSeekBar.setProgress(brightnessVal);

    }

    private void initColorTypeRv() {
        nameFxArray = new String[]{Constants.ADJUST_BRIGHTNESS, Constants.ADJUST_CONTRAST, Constants.ADJUST_SATURATION, Constants.ADJUST_HIGHTLIGHT, Constants.ADJUST_SHADOW,
                Constants.ADJUST_TEMPERATURE, Constants.ADJUST_TINT, Constants.ADJUST_FADE, Constants.ADJUST_DEGREE, Constants.ADJUST_AMOUNT, Constants.ADJUST_DENOISE};
        List<ColorTypeItem> colorTypeItems = new ArrayList<>();

        TypedArray typedArray = mContext.getResources().obtainTypedArray(R.array.sub_adjust_menu_icon);
        TypedArray typedArraySelected = mContext.getResources().obtainTypedArray(R.array.sub_adjust_menu_icon_selected);
        String[] nameArray = mContext.getResources().getStringArray(R.array.adjust_menu_array);
        for (int index = 0; index < nameArray.length; index++) {
            ColorTypeItem menuInfo = new ColorTypeItem();
            menuInfo.setColorAtrubuteText(nameArray[index]);
            menuInfo.setColorTypeName(nameFxArray[index]);
            menuInfo.setIcon(typedArray.getResourceId(index, -1));
            menuInfo.setSelectedIcon(typedArraySelected.getResourceId(index, -1));
            if (index < 5 || index == 7) {
                menuInfo.setFxName(Constants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
            } else if (index == 9) {
                menuInfo.setFxName(Constants.ADJUST_TYPE_SHARPEN);

            } else if (index == 5 || index == 6) {
                menuInfo.setFxName(Constants.ADJUST_TYPE_TINT);

            } else if (index == 8) {
                menuInfo.setFxName(Constants.ADJUST_TYPE_VIGETTE);
            } else {
                menuInfo.setFxName(Constants.ADJUST_TYPE_DENOISE);
            }

            colorTypeItems.add(menuInfo);
        }
        typedArray.recycle();
        typedArraySelected.recycle();
        colorTypeItems.get(0).setSelected(true);
        mCurrentVideoFx = mColorVideoFx;
        mCurrentColorType = Constants.ADJUST_BRIGHTNESS;
        mCurrentAdjustItem = colorTypeItems.get(0);
        ClipInfo clipInfo = mClipArrayList.get(mCurClipIndex);
        CorrectionColorInfo correctionColorInfo = clipInfo.getCorrectionColorInfo();
        //获取亮度 对比度 饱和度 高光 阴影 褪色
        //Get brightness, contrast, saturation, highlight, shadow, fade
        float brightVal = (float) correctionColorInfo.getBrightnessVal();
        float contrastVal = (float) correctionColorInfo.getContrastVal();
        float saturationVal = (float) correctionColorInfo.getSaturationVal();
        float highLight = (float) correctionColorInfo.getmHighLight();
        float shadow = (float) correctionColorInfo.getmShadow();
        float fade = (float) correctionColorInfo.getFade();
        //暗角 Dark Angle
        float vignette = (float) correctionColorInfo.getVignetteVal();
        //获取锐度 Sharpen
        float sharpen = (float) correctionColorInfo.getSharpenVal();
        //获取色温 色调 Temperature
        float temperature = (float) correctionColorInfo.getTemperature();
        float tint = (float) correctionColorInfo.getTint();
        //噪点 Density
        float intensity = (float) correctionColorInfo.getDensity();
        //噪点密度 DenoiseDensity
        float density = (float) correctionColorInfo.getDenoiseDensity();
        //设置默认值（此值为进入页面带有的值 重置的原点）
        //Set the default value (this value is the value that enters the page with the reset origin)
        //特殊标注 这里 的顺序是按照显示的功能排列
        //Special labels The order here is arranged according to the displayed function
        colorTypeItems.get(0).setDefaultValue(brightVal);
        colorTypeItems.get(1).setDefaultValue(contrastVal);
        colorTypeItems.get(2).setDefaultValue(saturationVal);
        colorTypeItems.get(3).setDefaultValue(highLight);
        colorTypeItems.get(4).setDefaultValue(shadow);
        colorTypeItems.get(5).setDefaultValue(temperature);
        colorTypeItems.get(6).setDefaultValue(tint);
        colorTypeItems.get(7).setDefaultValue(fade);
        colorTypeItems.get(8).setDefaultValue(vignette);
        colorTypeItems.get(9).setDefaultValue(sharpen);
        //设置噪点的程度和密度 Set the level and density of noise
        colorTypeItems.get(10).setDefaultValue(intensity);
        colorTypeItems.get(10).setExtra(density);
        colorTypeItems.get(10).setNoiseGrayscale(correctionColorInfo.isNoiseGrayscale());
        denoiseSingleColor = correctionColorInfo.isNoiseGrayscale();

        mColorTypeAdapter = new ColorTypeAdapter(this, colorTypeItems);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mColorTypeRv.setLayoutManager(linearLayoutManager);
        mColorTypeRv.setAdapter(mColorTypeAdapter);

        mColorTypeAdapter.setOnItemClickListener(new ColorTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ColorTypeItem colorTypeItem) {
                if (colorTypeItem == null || colorTypeItem.getColorTypeName() == null || colorTypeItem.getFxName() == null) {
                    return;
                }
                mCurrentAdjustItem = colorTypeItem;
                if (mCurrentAdjustItem.getFxName().equals(Constants.ADJUST_TYPE_DENOISE)) {
                    mDenoiseAdjustView.setVisibility(View.VISIBLE);
                    ll_seek.setVisibility(View.GONE);
                } else {
                    ll_seek.setVisibility(View.VISIBLE);
                    mDenoiseAdjustView.setVisibility(View.GONE);
                }
                /*
                 * 校色参数
                 * Calibration parameters
                 * 设置当前的特效对象
                 * */
                //当选择的是噪点选项时，默认选择的单色模式,类型设置的是噪点强度调节，需要在调节滑竿是在赋值
                //When the noise option is selected, the monochromatic mode is selected by default, and the type set is noise intensity adjustment, which is required when the adjustment rod is assigned
                mCurrentColorType = colorTypeItem.getColorTypeName();

                if (colorTypeItem.getFxName().equals(Constants.ADJUST_TYPE_BASIC_IMAGE_ADJUST)) {
                    mCurrentVideoFx = mColorVideoFx;
                } else if (colorTypeItem.getFxName().equals(Constants.ADJUST_TYPE_VIGETTE)) {
                    mCurrentVideoFx = mVignetteVideoFx;
                } else if (colorTypeItem.getFxName().equals(Constants.ADJUST_TYPE_SHARPEN)) {
                    mCurrentVideoFx = mSharpenVideoFx;
                } else if (colorTypeItem.getFxName().equals(Constants.ADJUST_TYPE_TINT)) {
                    mCurrentVideoFx = mTintVideoFx;
                } else if (colorTypeItem.getFxName().equals(Constants.ADJUST_TYPE_DENOISE)) {
                    mCurrentVideoFx = mDenoiseVideoFx;
                    mCurrentVideoFx.setBooleanVal(Constants.ADJUST_DENOISE_COLOR_MODE, denoiseSingleColor);
                    correctionColorInfo.setNoiseGrayscale(denoiseSingleColor);
                }
                if (colorTypeItem.getFxName().equals(Constants.ADJUST_TYPE_DENOISE)) {
                    mDenoiseAdjustView.setDenoiseProgress(getProgressByValue((float) mCurrentVideoFx.getFloatVal(Constants.ADJUST_DENOISE), Constants.ADJUST_DENOISE));
                    mDenoiseAdjustView.setDenoiseDensityProgress(getProgressByValue((float) mCurrentVideoFx.getFloatVal(Constants.ADJUST_DENOISE_DENSITY), Constants.ADJUST_DENOISE_DENSITY));
                    mDenoiseAdjustView.setNoiseGrayscale(denoiseSingleColor);
                } else {
                    initColorSeekBar();
                    mColorSeekBar.setProgress(getProgressByValue((float) mCurrentVideoFx.getFloatVal(mCurrentColorType), mCurrentColorType));
                }
            }
        });
    }


    private void initColorSeekBar() {
        if (mCurrentColorType.equals(Constants.ADJUST_DEGREE)
                || mCurrentColorType.equals(Constants.ADJUST_AMOUNT)) {
            mColorSeekBar.setMax(100);
            mColorSeekBar.setBreakProgress(0);
            return;
        }
        mColorSeekBar.setMax(200);
        mColorSeekBar.setBreakProgress(100);
    }

    /**
     * 获取当前设置的特效和值
     * Get the currently set special effect and value
     */
    private void initColorAdjustVideoFx() {
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return;
        }
        NvsVideoClip videoClip = videoTrack.getClipByIndex(0);
        if (videoClip == null) {
            return;
        }
        int fxCount = videoClip.getFxCount();
        for (int index = 0; index < fxCount; ++index) {
            NvsVideoFx videoFx = videoClip.getFxByIndex(index);
            if (videoFx == null) {
                continue;
            }

            String fxName = videoFx.getBuiltinVideoFxName();
            if (fxName == null || TextUtils.isEmpty(fxName)) {
                continue;
            }
            if (fxName.equals(Constants.ADJUST_TYPE_BASIC_IMAGE_ADJUST)) {
                mColorVideoFx = videoFx;
            } else if (fxName.equals(Constants.ADJUST_TYPE_VIGETTE)) {
                mVignetteVideoFx = videoFx;
            } else if (fxName.equals(Constants.ADJUST_TYPE_SHARPEN)) {
                mSharpenVideoFx = videoFx;
            } else if (fxName.equals(Constants.ADJUST_TYPE_TINT)) {
                mTintVideoFx = videoFx;
            } else if (fxName.equals(Constants.ADJUST_TYPE_DENOISE)) {
                mDenoiseVideoFx = videoFx;
            }
        }

        if (mColorVideoFx == null) {
            mColorVideoFx = videoClip.appendBuiltinFx(Constants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
            mColorVideoFx.setAttachment(Constants.ADJUST_TYPE_BASIC_IMAGE_ADJUST, Constants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
        }

        if (mVignetteVideoFx == null) {
            mVignetteVideoFx = videoClip.appendBuiltinFx(Constants.ADJUST_TYPE_VIGETTE);
            mVignetteVideoFx.setAttachment(Constants.ADJUST_TYPE_VIGETTE, Constants.ADJUST_TYPE_VIGETTE);
        }

        if (mSharpenVideoFx == null) {
            mSharpenVideoFx = videoClip.appendBuiltinFx(Constants.ADJUST_TYPE_SHARPEN);
            mSharpenVideoFx.setAttachment(Constants.ADJUST_TYPE_SHARPEN, Constants.ADJUST_TYPE_SHARPEN);
        }

        if (mTintVideoFx == null) {
            mTintVideoFx = videoClip.appendBuiltinFx(Constants.ADJUST_TYPE_TINT);
            mTintVideoFx.setAttachment(Constants.ADJUST_TYPE_TINT, Constants.ADJUST_TYPE_TINT);
        }


        if (null == mDenoiseVideoFx) {
            mDenoiseVideoFx = videoClip.appendBuiltinFx(Constants.ADJUST_TYPE_DENOISE);
            mDenoiseVideoFx.setAttachment(Constants.ADJUST_TYPE_DENOISE, Constants.ADJUST_TYPE_DENOISE);
        }

    }

    @Override
    protected void initListener() {
        mTitleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {
                removeTimeline();
            }

            @Override
            public void OnCenterTextClick() {
            }

            @Override
            public void OnRightTextClick() {
            }
        });
        mCorrectionColorFinish.setOnClickListener(this);

        mColorSeekBar.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int progress, boolean fromUser) {
                onAdjustDataChanged(mCurrentColorType, progress);
            }
        });

        /*
         * 重置校色参数 重置到进入页面时的值
         * Reset color calibration parameters
         * */
        mColorResetImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mCurrentAdjustItem) {

                    resetAdjustData(mCurrentColorType, 0);
                }
            }
        });
        mDenoiseAdjustView.setOnFunctionListener(new DenoiseAdjustView.OnFunctionListener() {
            @Override
            public void onSelectMode(boolean single) {
                denoiseSingleColor = single;
                mCurrentVideoFx.setBooleanVal(Constants.ADJUST_DENOISE_COLOR_MODE, denoiseSingleColor);
                correctionColorInfo.setNoiseGrayscale(denoiseSingleColor);
                if (mClipFragment.getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    mClipFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                }
            }

            @Override
            public void onIntensityChanged(int progress) {
                mCurrentColorType = Constants.ADJUST_DENOISE;
                onAdjustDataChanged(mCurrentColorType, progress);
            }

            @Override
            public void onDensityChanged(int progress) {
                mCurrentColorType = Constants.ADJUST_DENOISE_DENSITY;
                onAdjustDataChanged(mCurrentColorType, progress);
            }

            @Override
            public void onReset() {
                if (null != mCurrentAdjustItem) {
                    mCurrentAdjustItem.setDefaultValue(0);
                    mCurrentAdjustItem.setExtra(0);
                    resetAdjustData(Constants.ADJUST_DENOISE, mCurrentAdjustItem.getDefaultValue());
                    resetAdjustData(Constants.ADJUST_DENOISE_DENSITY, mCurrentAdjustItem.getExtra());
                    resetAdjustData(Constants.ADJUST_DENOISE_COLOR_MODE, mCurrentAdjustItem.isNoiseGrayscale() ? 0 : 1);
                }
            }
        });
    }

    /**
     * 调节seekbar 调节的值变化
     * Adjust the value of seekbar adjustment
     *
     * @param progress
     */
    private void onAdjustDataChanged(String mCurrentColorType, int progress) {
        if (mCurrentVideoFx != null && mCurrentColorType != null) {
            float colorVal = getFloatColorVal(progress, mCurrentColorType);
            mCurrentVideoFx.setFloatVal(mCurrentColorType, colorVal);
            setClipInfoAdjustValue(colorVal, mCurrentColorType);
            if (mClipFragment.getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                mClipFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
            }
        }
    }


    /**
     * 通过当前的进度 获取对应设置特效的值
     * Obtain the value of the corresponding special effect through the current progress
     *
     * @param progress
     * @param mCurrentColorType
     * @return
     */
    private float getFloatColorVal(int progress, String mCurrentColorType) {
        if (!TextUtils.isEmpty(mCurrentColorType)) {
            //暗角 噪点取值 0 -1 Dark Angle noise value
            if (mCurrentColorType.equals(Constants.ADJUST_DEGREE) || mCurrentColorType.equals(Constants.ADJUST_DENOISE) || mCurrentColorType.equals(Constants.ADJUST_DENOISE_DENSITY)) {
                return progress / 100f;
            }
            //锐度 0 - 5 sharpness
            else if (mCurrentColorType.equals(Constants.ADJUST_AMOUNT)) {
                return progress / 100f * 5;
            } else {
                //其他 -1 ~ 1 other
                return (progress - 100) / 100F;

            }
        }
        return (progress - 100) / 100F;
    }

    /**
     * 通过特效的值获取对应的进度是多少
     * Get the corresponding progress through the value of the special effect
     *
     * @param value
     * @param mCurrentColorType
     * @return
     */
    private int getProgressByValue(float value, String mCurrentColorType) {
        if (!TextUtils.isEmpty(mCurrentColorType)) {
            //暗角 取值 0 -1 Dark Angle noise value
            if (mCurrentColorType.equals(Constants.ADJUST_DEGREE) || mCurrentColorType.equals(Constants.ADJUST_DENOISE) || mCurrentColorType.equals(Constants.ADJUST_DENOISE_DENSITY)) {
                return (int) (value * 100);
            }
            //锐度取值 0 - 5 sharpness
            else if (mCurrentColorType.equals(Constants.ADJUST_AMOUNT)) {
                return (int) (value * 100 / 5);
            } else {
                return (int) ((value * 100) + 100);
            }
        }
        return (int) ((value * 100) + 100);
    }

    /**
     * 重置当前调节项的值，恢复到进入页面时的值
     * Reset the value of the current adjustment item and return to the value when entering the page
     *
     * @param mCurrentColorType 当前调节的项
     * @param defaultValue      原始值
     */
    private void resetAdjustData(String mCurrentColorType, float defaultValue) {
        if (mCurrentVideoFx != null && mCurrentColorType != null && null != mCurrentAdjustItem) {
            //获取到当前这个调节的默认值
            //Get the current adjusted default value
            mCurrentVideoFx.setFloatVal(mCurrentColorType, defaultValue);
            int progress = getProgressByValue(defaultValue, mCurrentColorType);
            //调节杆不是同一个 分别设置
            //The adjustment levers are not the same, set separately
            if (mCurrentColorType.equals(Constants.ADJUST_DENOISE)) {
                mDenoiseAdjustView.setDenoiseProgress(progress);
            } else if (mCurrentColorType.equals(Constants.ADJUST_DENOISE_DENSITY)) {
                mDenoiseAdjustView.setDenoiseDensityProgress(progress);
            } else if (mCurrentColorType.equals(Constants.ADJUST_DENOISE_COLOR_MODE)) {
                mCurrentVideoFx.setBooleanVal(mCurrentColorType, defaultValue == 0);
                mDenoiseAdjustView.setNoiseGrayscale(defaultValue == 0);
            } else {
                mColorSeekBar.setProgress(progress);
            }
            setClipInfoAdjustValue(defaultValue, mCurrentColorType);
            if (mClipFragment.getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                mClipFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
            }
        }
    }

    private void setClipInfoAdjustValue(float colorVal, String mCurrentColorType) {
        ClipInfo clipInfo = mClipArrayList.get(mCurClipIndex);
        //亮度 brightness
        if (mCurrentColorType.equals(Constants.ADJUST_BRIGHTNESS)) {
            correctionColorInfo.setBrightnessVal(colorVal);
        }
        //对比度 Contrast ratio
        else if (mCurrentColorType.equals(Constants.ADJUST_CONTRAST)) {
            correctionColorInfo.setContrastVal(colorVal);
        }
        //饱和度 saturation
        else if (mCurrentColorType.equals(Constants.ADJUST_SATURATION)) {
            correctionColorInfo.setSaturationVal(colorVal);
        }
        //高光 highlight
        else if (mCurrentColorType.equals(Constants.ADJUST_HIGHTLIGHT)) {
            correctionColorInfo.setmHighLight(colorVal);
        }

        //阴影 shadow
        else if (mCurrentColorType.equals(Constants.ADJUST_SHADOW)) {
            correctionColorInfo.setmShadow(colorVal);
        }
        //色温 Color temperature
        else if (mCurrentColorType.equals(Constants.ADJUST_TEMPERATURE)) {
            correctionColorInfo.setTemperature(colorVal);
        }
        //色调 hue
        else if (mCurrentColorType.equals(Constants.ADJUST_TINT)) {
            correctionColorInfo.setTint(colorVal);
        }
        //褪色 fade
        else if (mCurrentColorType.equals(Constants.ADJUST_FADE)) {
            correctionColorInfo.setFade(colorVal);
        }
        //暗角 Dark Angle
        else if (mCurrentColorType.equals(Constants.ADJUST_DEGREE)) {
            correctionColorInfo.setVignetteVal(colorVal);
        }
        //锐度 sharpness
        else if (mCurrentColorType.equals(Constants.ADJUST_AMOUNT)) {
            correctionColorInfo.setSharpenVal(colorVal);
        }
        //噪点  程度 密度 Noise degree density
        else if (mCurrentColorType.equals(Constants.ADJUST_DENOISE)) {
            correctionColorInfo.setDensity(colorVal);
        } else if (mCurrentColorType.equals(Constants.ADJUST_DENOISE_DENSITY)) {
            correctionColorInfo.setDenoiseDensity(colorVal);
        } else if (mCurrentColorType.equals(Constants.ADJUST_DENOISE_COLOR_MODE)) {
            correctionColorInfo.setNoiseGrayscale(colorVal == 0);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.correctionColorFinish) {
            BackupData.instance().setClipInfoData(mClipArrayList);
            removeTimeline();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            AppManager.getInstance().finishActivity();
        }
    }

    @Override
    public void onBackPressed() {
        removeTimeline();
        AppManager.getInstance().finishActivity();
        super.onBackPressed();
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }

    private void initVideoFragment() {
        mClipFragment = new SingleClipFragment();
        mClipFragment.setFragmentLoadFinisedListener(new SingleClipFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mClipFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
            }
        });
        mClipFragment.setTimeline(mTimeline);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        mClipFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.spaceLayout, mClipFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mClipFragment);
    }
}
