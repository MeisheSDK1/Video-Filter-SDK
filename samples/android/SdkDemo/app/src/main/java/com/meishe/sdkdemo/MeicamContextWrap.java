package com.meishe.sdkdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.HandlerThread;
import android.util.Log;

import com.meicam.effect.sdk.NvsEffectSdkContext;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.sdkdemo.utils.Logger;

import static com.meishe.sdkdemo.utils.Constants.HUMAN_AI_TYPE_MS;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author :
 * @CreateDate :
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MeicamContextWrap {
    public static final String FACE_106_MODEL = "ms_face106_v4.0.1.next.model";
    public static final String FACE_240_MODEL = "ms_face240_v4.0.1.next.model";
    public static final String HUMAN_SEG_LEVEL_LOW_MODEL = "ms_humansegment_small_v2.0.0.next.model";
    public static final String HUMAN_SEG_MODEL = "ms_humansegment_medium_v2.0.0.next.model";
    public static final String EYE_CONTOUR_MODEL = "ms_eyecontour_v2.0.0.next.model";
    public static final String FAKE_FACE_DAT = "fakeface_v1.0.1.dat";
    public static final String HAND_MODEL = "ms_hand_common_v2.0.0.next.model";
    public static final String AVATAR_MODEL = "ms_avatar_v2.0.0.next.model";
    public static final String FACE_COMMON_DAT = "facecommon_v1.0.1.dat";
    public static final String ADVANCED_BEAUTY_DAT = "advancedbeauty_v1.0.1.dat";
    public static final String SKY_SEG_MODEL = "ms_skysegment_v1.0.5.next.model";
    private static final String TAG = "MeicamContextWrap";
    private Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static MeicamContextWrap mMeicamContextWrap;
    private HandlerThread mHandlerThread;

    public static MeicamContextWrap getInstance() {
        if (null == mMeicamContextWrap) {
            synchronized (MeicamContextWrap.class) {
                if (mMeicamContextWrap == null) {
                    mMeicamContextWrap = new MeicamContextWrap();
                }
            }
        }
        return mMeicamContextWrap;
    }

    /**
     * 获取SDK版本
     * Obtaining the SDK version
     *
     * @return eg:3.6.1
     */
    public static String getSdkVersion() {
        NvsStreamingContext instance = NvsStreamingContext.getInstance();
        if (instance != null) {
            NvsStreamingContext.SdkVersion version = instance.getSdkVersion();
            if (version != null) {
                return version.majorVersion + "." + version.minorVersion + "." + version.revisionNumber;
            }
        }
        return "";
    }

    public void initMeicamModel(Context context, boolean arSceneFinished
            , OnInitModelListener onInitModelListener) {
        //检测SDK包是否有人脸模块 Detects if the SDK package has a face module
        int mCanUseArFaceType = NvsStreamingContext.hasARModule();
        Logger.e(TAG, "mCanUseArFaceType:" + mCanUseArFaceType);
        // 初始化AR Scene，全局只需一次 Initialize the AR Scene, global only once
        if (mCanUseArFaceType == HUMAN_AI_TYPE_MS && !arSceneFinished) {
            boolean initSuccess = initArSceneEffect(context);
            if (null != onInitModelListener) {
                onInitModelListener.onInitModel(initSuccess);
            }
        } else {
            if (null != onInitModelListener) {
                onInitModelListener.onInitModel(false);
            }
            Logger.e(TAG, "initARScene false no face model");
        }

    }

    /**
     * SDK模型等相关初始化
     * SDK model and other related initialization
     *
     * @param context contect
     * @return 人脸模型是否初始化成功 Whether the face model is successfully initialized
     */
    private boolean initArSceneEffect(Context context) {
        //基础人脸模型文件 Basic face model file
        String modelPath = "assets:/facemode/ms/" + FACE_106_MODEL;
        String licensePath = "";
        if (BuildConfig.FACE_MODEL == 240) {
            modelPath = "assets:/facemode/ms/" + FACE_240_MODEL;
        }
        boolean initSuccess = NvsStreamingContext.initHumanDetection(MSApplication.getContext(),
                modelPath, licensePath,
                NvsStreamingContext.HUMAN_DETECTION_FEATURE_FACE_LANDMARK |
                        NvsStreamingContext.HUMAN_DETECTION_FEATURE_FACE_ACTION |
                        NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEMI_IMAGE_MODE);
        /*
         * 对应的effect sdk的部分（如果没用到可以不填加）
         * The part of the corresponding effect sdk (if not used, you can leave it blank)
         */
        NvsEffectSdkContext.initHumanDetection(MSApplication.getContext(),
                modelPath, licensePath,
                NvsStreamingContext.HUMAN_DETECTION_FEATURE_FACE_LANDMARK |
                        NvsStreamingContext.HUMAN_DETECTION_FEATURE_FACE_ACTION |
                        NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEMI_IMAGE_MODE);

//--------------------------------------------------------------------------------------------------
        /*
         * 人脸通用模型初始化
         *The face common model is initialized
         */
        String faceCommonPath = "assets:/facemode/common/" + FACE_COMMON_DAT;
        boolean faceCommonSuccess = NvsStreamingContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_FACE_COMMON, faceCommonPath);
        /*
         * 对应的effect sdk的部分（如果没用到可以不填加）
         * The part of the corresponding effect sdk (if not used, you can leave it blank)
         */
        NvsEffectSdkContext.setupHumanDetectionData(NvsEffectSdkContext.HUMAN_DETECTION_DATA_TYPE_FACE_COMMON, faceCommonPath);
//--------------------------------------------------------------------------------------------------

        /*
         * 美颜，美妆模型初始化
         *The advanced beauty model is initialized
         */
        String advancedBeautyPath = "assets:/facemode/common/" + ADVANCED_BEAUTY_DAT;
        boolean advancedBeautySuccess = NvsStreamingContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_ADVANCED_BEAUTY, advancedBeautyPath);

        /*
         * 对应的effect sdk的部分（如果没用到可以不填加）
         * The part of the corresponding effect sdk (if not used, you can leave it blank)
         */
        NvsEffectSdkContext.setupHumanDetectionData(NvsEffectSdkContext.HUMAN_DETECTION_DATA_TYPE_ADVANCED_BEAUTY, advancedBeautyPath);
//--------------------------------------------------------------------------------------------------

        /*
         * 假脸模型初始化，类似面具效果等，特效只跟随脸部动
         * Fake face model initialization, similar to mask effects, etc., the special effects only follow the movement of the face
         */
        String fakeFacePath = "assets:/facemode/common/" + FAKE_FACE_DAT;
        boolean fakefaceSuccess = NvsStreamingContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_FAKE_FACE, fakeFacePath);

        /*
         * 对应的effect sdk的部分（如果没用到可以不填加）
         * The part of the corresponding effect sdk (if not used, you can leave it blank)
         */
        NvsEffectSdkContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_FAKE_FACE, fakeFacePath);
//--------------------------------------------------------------------------------------------------
        /*
         * 人像背景分割模型
         * Portrait Background Segmentation Model
         */
        String segPath = "assets:/facemode/common/" + HUMAN_SEG_MODEL;
        if (null != NvsStreamingContext.getInstance()) {
            int level = NvsStreamingContext.getInstance().getDeviceCpuLevel();
            Log.e(TAG, "MS CPU level:-->" + level);
            if (level == NvsStreamingContext.DEVICE_POWER_LEVEL_LOW) {
                segPath = "assets:/facemode/common/" + HUMAN_SEG_LEVEL_LOW_MODEL;
            }
        }
        boolean segSuccess = NvsStreamingContext.initHumanDetectionExt(MSApplication.getContext(),
                segPath, null, NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEGMENTATION_BACKGROUND);

        /*
         * 对应的effect sdk的部分（如果没用到可以不填加）
         * The part of the corresponding effect sdk (if not used, you can leave it blank)
         */
        NvsEffectSdkContext.initHumanDetectionExt(MSApplication.getContext(),
                segPath, null, NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEGMENTATION_BACKGROUND);
//--------------------------------------------------------------------------------------------------

        /*
         * 手势点位模型，比心等效果会使用到这个模型
         * Gesture point model, heart and other effects will use this model
         */
        String handPath = "assets:/facemode/common/" + HAND_MODEL;
        boolean handSuccess = NvsStreamingContext.initHumanDetectionExt(MSApplication.getContext(), handPath, null,
                NvsStreamingContext.HUMAN_DETECTION_FEATURE_HAND_LANDMARK
                        | NvsStreamingContext.HUMAN_DETECTION_FEATURE_HAND_ACTION);

        /*
         * 对应的effect sdk的部分（如果没用到可以不填加）
         * The part of the corresponding effect sdk (if not used, you can leave it blank)
         */
        NvsEffectSdkContext.initHumanDetectionExt(MSApplication.getContext(), handPath, null,
                NvsStreamingContext.HUMAN_DETECTION_FEATURE_HAND_LANDMARK
                        | NvsStreamingContext.HUMAN_DETECTION_FEATURE_HAND_ACTION);
//--------------------------------------------------------------------------------------------------
        /*
         * 眼球模型初始化 , 作用在美妆的 美瞳选项 ,不用美妆美瞳 可以不做初始化
         */
        String eyeBallModelPath = "assets:/facemode/common/" + EYE_CONTOUR_MODEL;
        boolean eyeBallSuccess = NvsStreamingContext.initHumanDetectionExt(MSApplication.getContext(), eyeBallModelPath, null
                , NvsStreamingContext.HUMAN_DETECTION_FEATURE_EYEBALL_LANDMARK | NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEMI_IMAGE_MODE);
        /*
         * 对应的effect sdk的部分（如果没用到可以不填加）
         * The part of the corresponding effect sdk (if not used, you can leave it blank)
         */
        NvsEffectSdkContext.initHumanDetectionExt(MSApplication.getContext(),
                eyeBallModelPath, null, NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_EYEBALL_LANDMARK);
//--------------------------------------------------------------------------------------------------
        /*
         * avatar模型初始化，小狐狸效果等，根据表情动的特效果类别会使用这个模型
         * avatar model initialization, little fox effect, etc.,
         * this model will be used according to the special effect category of facial expressions
         */
        modelPath = "assets:/facemode/common/" + AVATAR_MODEL;
        boolean avatarSuccess = NvsStreamingContext.initHumanDetectionExt(MSApplication.getContext(),
                modelPath,
                null,
                NvsStreamingContext.HUMAN_DETECTION_FEATURE_AVATAR_EXPRESSION);
        /*
         * 对应的effect sdk的部分（如果没用到可以不填加）
         * The part of the corresponding effect sdk (if not used, you can leave it blank)
         */
        NvsEffectSdkContext.initHumanDetectionExt(MSApplication.getContext(),
                modelPath,
                null,
                NvsStreamingContext.HUMAN_DETECTION_FEATURE_AVATAR_EXPRESSION);
//--------------------------------------------------------------------------------------------------
        modelPath = "assets:/facemode/common/" + SKY_SEG_MODEL;
        boolean skySeguccess = NvsStreamingContext.initHumanDetectionExt(MSApplication.getContext(),
                modelPath,
                null,
                NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEGMENTATION_SKY);
        StringBuilder sb = new StringBuilder();
        sb.append("******************************************").append("\n")
                .append("Face :").append(initSuccess).append("\n")
                .append("FaceCommon:").append(faceCommonSuccess).append("\n")
                .append("AdvancedBeauty:").append(advancedBeautySuccess).append("\n")
                .append("Fake Face:").append(fakefaceSuccess).append("\n")
                .append("Segment:").append(segSuccess).append("\n")
                .append("Hand:").append(handSuccess).append("\n")
                .append("Eye Ball:").append(eyeBallSuccess).append("\n")
                .append("Avatar:").append(avatarSuccess).append("\n")
                .append("Sky Segment:").append(skySeguccess).append("\n")
                .append("******************************************");
        Log.e(TAG, sb.toString());
        return initSuccess;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public interface OnInitModelListener {
        void onInitModel(boolean initSuccess);
    }

    public void destory() {
        if (null != mHandlerThread) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
        if (null != mMeicamContextWrap) {
            mMeicamContextWrap = null;
        }
    }
}
