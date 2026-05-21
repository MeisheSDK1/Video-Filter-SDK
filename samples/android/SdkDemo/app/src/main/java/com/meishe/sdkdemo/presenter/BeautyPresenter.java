package com.meishe.sdkdemo.presenter;

import android.text.TextUtils;

import com.meicam.sdk.NvsARSceneManipulate;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.arscene.BeautyHelper;
import com.meishe.arscene.bean.BeautyFxInfo;
import com.meishe.arscene.bean.FxParams;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.base.model.Presenter;
import com.meishe.sdkdemo.activity.view.BeautyView;
import com.meishe.sdkdemo.utils.Constants;

import java.util.HashSet;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/11/22 11:01
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class BeautyPresenter extends Presenter<BeautyView> {
    /**
     * 设置美肤应用
     * Set up the beauty app
     *
     * @param mTimeline     timeline
     * @param mBeautyHelper BeautyHelper
     * @param info          info
     */
    public void setSkinApply(NvsTimeline mTimeline, BeautyHelper mBeautyHelper, IFxInfo info) {
        applyBeautyFx(FxParams.BEAUTY_SKIN, mTimeline, mBeautyHelper, info);
    }

    /**
     * 应用锐度
     * Application sharpness
     *
     * @param mTimeline     timeline
     * @param mBeautyHelper helper
     * @param isSharpenOpen isOpen
     */
    public void setSharpenApply(NvsTimeline mTimeline, BeautyHelper mBeautyHelper, boolean isSharpenOpen) {
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (null == videoTrack) {
            return;
        }
        int count = videoTrack.getClipCount();
        for (int i = 0; i < count; i++) {
            NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
            if (null == videoClip) {
                continue;
            }
            mBeautyHelper.enableBeauty(videoClip, true);
            NvsVideoFx nvsVideoFx = mBeautyHelper.getBeautyFx(videoClip);
            if (null == nvsVideoFx) {
                continue;
            }
            nvsVideoFx.setBooleanVal(FxParams.DEFAULT_SHARPEN_ENABLE, isSharpenOpen);
        }
        getView().onSeekTimeline();
    }

    /**
     * 是否支持去油光
     * Whether degreasing is supported
     *
     * @param mTimeline     timeline
     * @param mBeautyParams params
     * @return boolean
     */
    public boolean isSupportMatte(NvsTimeline mTimeline, BeautyFxInfo mBeautyParams) {
        if ((null == mTimeline) || (null == mBeautyParams)) {
            return false;
        }
        if (mBeautyParams.isSupportMatte()) {
            return true;
        }
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (null == videoTrack) {
            return false;
        }
        int count = videoTrack.getClipCount();
        for (int i = 0; i < count; i++) {
            NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
            if (null == videoClip) {
                continue;
            }
            NvsVideoFx videoFx = videoClip.insertBuiltinFx(Constants.AR_SCENE, 0);
            if (null == videoFx) {
                return false;
            }
            NvsARSceneManipulate arSceneManipulate = videoFx.getARSceneManipulate();
            if (null == arSceneManipulate) {
                return false;
            }
            videoClip.removeFx(0);
            return arSceneManipulate.isFunctionAvailable(NvsARSceneManipulate.CheckedFunctionType_Matte);
        }
        return false;
    }

    /**
     * 设置美型应用
     * Set up the beauty app
     *
     * @param mTimeline     timeline
     * @param mBeautyHelper BeautyHelper
     * @param info          info
     */
    public void setFaceApply(NvsTimeline mTimeline, BeautyHelper mBeautyHelper, IFxInfo info) {
        applyBeautyFx(FxParams.BEAUTY_FACE, mTimeline, mBeautyHelper, info);
    }

    /**
     * 设置微整形应用
     * Set up the micro-shaping application
     *
     * @param mTimeline     timeline
     * @param mBeautyHelper BeautyHelper
     * @param info          info
     */
    public void setSmallApply(NvsTimeline mTimeline, BeautyHelper mBeautyHelper, IFxInfo info) {
        applyBeautyFx(FxParams.BEAUTY_SMALL, mTimeline, mBeautyHelper, info);
    }

    /**
     * 美颜效果的应用
     * The application of beauty effect
     *
     * @param type          美肤，美型，微整形 Beautiful skin, beautiful shape, micro plastic surgery
     * @param mTimeline     mTimeline
     * @param mBeautyHelper Helper
     * @param info          info
     */
    public void applyBeautyFx(int type, NvsTimeline mTimeline, BeautyHelper mBeautyHelper, IFxInfo info) {
        if (!checkIsNull(mTimeline, mBeautyHelper, info)) {
            return;
        }
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (null == videoTrack) {
            return;
        }
        int count = videoTrack.getClipCount();
        for (int i = 0; i < count; i++) {
            NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
            if (null == videoClip) {
                continue;
            }
            switch (type) {
                case FxParams.BEAUTY_SKIN:
                    mBeautyHelper.enableBeauty(videoClip, true);
                    //”去油光“需要开启高级美颜
                    //"Degreasing" requires turning on advanced beauty
                    if (TextUtils.equals(info.getFxName(), FxParams.ADVANCED_BEAUTY_MATTE_INTENSITY)) {
                        mBeautyHelper.enableAdvancedBeauty(videoClip, true);
                    }
                    break;
                case FxParams.BEAUTY_FACE:
                    mBeautyHelper.enableShape(videoClip, true);
                    mBeautyHelper.enableShapeNew(videoClip, true);
                    break;
                case FxParams.BEAUTY_SMALL:
                    mBeautyHelper.enableShape(videoClip, true);
                    mBeautyHelper.enableShapeNew(videoClip, true);
                    mBeautyHelper.enableAdvancedBeauty(videoClip, true);
                    break;
                default:
                    break;
            }
            mBeautyHelper.applyBeautyFx(videoClip, info);

        }
        getView().onSeekTimeline();
    }

    /**
     * 设置取消默认fx效果
     * 在添加微整形或者美肤中”去油光“时会开启“高级美颜”，此时会默认添加“高级磨皮”效果，且强度为1.
     * 这里如果用户没有在美肤-磨皮中选择设置过“高级磨皮”的话，将默认添加的“高级磨皮”取消，即设置高级美颜强度为0
     * <p>
     * Set to cancel the default fx effect
     * "Advanced Beauty" will be enabled when "Degreasing" is added to micro shaping or skin beauty. At this time, "Advanced Dermabrasion" effect will be added by default, and the intensity is 1.
     * Here, if the user has not selected and set "Advanced Dermabrasion" in Beaut-Dermabrasion, cancel the default addition of "Advanced dermabrasion", that is, set the advanced beauty intensity to 0
     *
     * @param mTimeline     timeline
     * @param mBeautyHelper 美颜helper Beauty helper
     * @param mBeautyParams 添加的美颜效果 Added beauty effect
     */
    public void setCancelDefaultFxValue(NvsTimeline mTimeline, BeautyHelper mBeautyHelper, BeautyFxInfo mBeautyParams) {
        if ((null == mTimeline) || (null == mBeautyParams) || (null == mBeautyHelper)) {
            return;
        }
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (null == videoTrack) {
            return;
        }
        int count = videoTrack.getClipCount();
        HashSet<IFxInfo> data = mBeautyParams.getBeautys();
        if (null == data) {
            for (int i = 0; i < count; i++) {
                NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
                if (null == videoClip) {
                    continue;
                }
                mBeautyHelper.enableAdvancedBeauty(videoClip, false);
            }
            return;
        }
        boolean isAddAdvancedBuffing = false;
        for (IFxInfo info : data) {
            if (null == info) {
                continue;
            }
            if (TextUtils.equals(info.getFxName(), FxParams.ADVANCED_BEAUTY_INTENSITY)) {
                isAddAdvancedBuffing = true;
                break;
            }
        }
        for (int i = 0; i < count; i++) {
            NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
            if (null == videoClip) {
                continue;
            }
            NvsVideoFx videoFx = mBeautyHelper.getBeautyFx(videoClip);
            if (!isAddAdvancedBuffing) {
                videoFx.setFloatVal(FxParams.ADVANCED_BEAUTY_INTENSITY, 0);
            }
        }
    }

    private boolean checkIsNull(NvsTimeline mTimeline, BeautyHelper mBeautyHelper, IFxInfo info) {
        return (null != mTimeline) && (null != mBeautyHelper) && (null != info);
    }
}
