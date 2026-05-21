package com.meishe.sdkdemo.utils;

import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsVideoResolution;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.activity.SettingActivity;
import com.meishe.utils.SpUtil;

import java.io.Serializable;

/**
 * Created by admin on 2018-5-28.
 */

public class ParameterSettingValues implements Serializable {
    private static ParameterSettingValues parameterValues;
    private int m_captureResolutionGrade;
    private boolean mSingleBufferMode;
    private int m_compileVideoRes;
    private double m_compileBitrate;
    private boolean m_disableDeviceEncorder;
    private boolean m_isUseBackgroudBlur;
    private int liveWindowModel;
    private int bitDepth;
    private String exportConfig;
    private boolean supportHEVC;
    private boolean quickPack;
    /**
     * 是否原生相机拍摄
     * Whether to shoot with a native camera
     */
    private boolean startNativeCamera;
    /**
     * sdr-hdr 颜色增益
     * sdr-hdr color gain
     */
    private float colorGain = 2.0f;

    private boolean isTestMaterial;
    private boolean enableAssetLicense = true;

    public int getCompileVideoRes() {
        return m_compileVideoRes;
    }

    public void setCompileVideoRes(int compileVideoRes) {
        this.m_compileVideoRes = compileVideoRes;
    }

    public ParameterSettingValues() {
        m_captureResolutionGrade = NvsStreamingContext.VIDEO_CAPTURE_RESOLUTION_GRADE_SUPER_HIGH;
        mSingleBufferMode = true;
        m_compileVideoRes = SettingActivity.CompileVideoRes_1080;
        m_compileBitrate = 0;
        m_disableDeviceEncorder = false;
        m_isUseBackgroudBlur = false;
        quickPack = false;
        startNativeCamera = false;
        liveWindowModel = NvsLiveWindow.HDR_DISPLAY_MODE_SDR;
        bitDepth = NvsVideoResolution.VIDEO_RESOLUTION_BIT_DEPTH_8_BIT;
        exportConfig = "none";
        supportHEVC = false;
        colorGain = 1.0f;
        isTestMaterial = false;
        enableAssetLicense = true;
    }

    public static ParameterSettingValues instance() {
        if (parameterValues == null) {
            parameterValues = init();
        }
        return getParameterValues();
    }

    private static ParameterSettingValues init() {
        if (parameterValues == null) {
            synchronized (ParameterSettingValues.class) {
                parameterValues = (ParameterSettingValues) SpUtil.getObjectFromShare(MSApplication.getContext(), Constants.KEY_PARAMTER);
                if (parameterValues == null) {
                    parameterValues = new ParameterSettingValues();
                }
            }
        }
        return parameterValues;
    }

    public static void setParameterValues(ParameterSettingValues values) {
        parameterValues = values;
    }

    public static ParameterSettingValues getParameterValues() {
        return parameterValues;
    }

    public int getCaptureResolutionGrade() {
        return m_captureResolutionGrade;
    }

    public void setCaptureResolutionGrade(int captureRatio) {
        this.m_captureResolutionGrade = captureRatio;
    }

    public boolean isSingleBufferMode() {
        return mSingleBufferMode;
    }

    public void setSingleBufferMode(boolean singleBufferMode) {
        this.mSingleBufferMode = singleBufferMode;
    }

    public double getCompileBitrate() {
        return m_compileBitrate;
    }

    public void setCompileBitrate(double bitrate) {
        this.m_compileBitrate = bitrate;
    }

    public boolean disableDeviceEncorder() {
        return m_disableDeviceEncorder;
    }

    public void setDisableDeviceEncorder(boolean useDeviceEncorder) {
        m_disableDeviceEncorder = useDeviceEncorder;
    }

    public boolean isUseBackgroudBlur() {
        return m_isUseBackgroudBlur;
    }

    public void setUseBackgroudBlur(boolean useBackgroudBlur) {
        m_isUseBackgroudBlur = useBackgroudBlur;
    }

    public int getLiveWindowModel() {
        return liveWindowModel;
    }

    public void setLiveWindowModel(int liveWindowModel) {
        this.liveWindowModel = liveWindowModel;
    }

    public int getBitDepth() {
        return bitDepth;
    }

    public void setBitDepth(int bitDepth) {
        this.bitDepth = bitDepth;
    }

    public String getExportConfig() {
        return exportConfig;
    }

    public void setExportConfig(String exportConfig) {
        this.exportConfig = exportConfig;
    }

    public boolean isSupportHEVC() {
        return supportHEVC;
    }

    public void setSupportHEVC(boolean supportHEVC) {
        this.supportHEVC = supportHEVC;
    }

    public float getColorGain() {
        return colorGain;
    }

    public void setColorGain(float colorGain) {
        this.colorGain = colorGain;
    }

    public boolean isQuickPack() {
        return quickPack;
    }

    public void setStartNativeCamera(boolean startNativeCamera) {
        this.startNativeCamera = startNativeCamera;
    }

    public boolean isStartNativeCamera() {
        return startNativeCamera;
    }

    public void setQuickPack(boolean quickPack) {
        this.quickPack = quickPack;
    }

    public boolean isTestMaterial() {
        return isTestMaterial;
    }

    public void setTestMaterial(boolean testMaterial) {
        isTestMaterial = testMaterial;
    }

    public boolean isEnableAssetLicense() {
        return enableAssetLicense;
    }

    public void setEnableAssetLicense(boolean enableAssetLicense) {
        this.enableAssetLicense = enableAssetLicense;
    }

    public void setParameterSettingValues() {
        ParameterSettingValues values = ParameterSettingValues.instance();
        SpUtil sp = SpUtil.getInstance(MSApplication.getContext());
        sp.setObjectToShare(MSApplication.getContext(), values, Constants.KEY_PARAMTER);
    }
}
