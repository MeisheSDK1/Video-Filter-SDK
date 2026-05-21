package com.meishe.sdkdemo.utils.dataInfo;

import androidx.annotation.NonNull;

public class CorrectionColorInfo {
    /**
     * 校色数据
     * Calibration data
     */
    private float m_brightnessVal;
    private float m_contrastVal;
    private float m_saturationVal;
    /**
     * 暗角
     * Vignetting
     */
    private float m_vignetteVal;
    /**
     * 锐度
     * Sharpness
     */
    private float m_sharpenVal;
    /**
     * 高光
     * highlight
     */
    private float mHighLight;
    /**
     * 阴影
     * shadow
     */
    private float mShadow;
    private float fade;
    /**
     * 色温 色调
     * temperature
     */
    private float temperature;
    private float tint;

    /**
     * 噪点程度
     * Degree of noise
     */
    private float density;
    /**
     * 噪点密度
     * Noise density
     */
    private float denoiseDensity;
    /**
     * 是否是灰度噪声
     */
    private boolean noiseGrayscale;


    public CorrectionColorInfo() {
        density = 0.0f;
        m_brightnessVal = 0.0f;
        m_contrastVal = 0.0f;
        m_saturationVal = 0.0f;
        m_sharpenVal = 0;
        m_vignetteVal = 0;
        noiseGrayscale = true;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public void setDenoiseDensity(float denoiseDensity) {
        this.denoiseDensity = denoiseDensity;
    }

    public float getDenoiseDensity() {
        return denoiseDensity;
    }

    public float getmHighLight() {
        return mHighLight;
    }

    public void setmHighLight(float mHighLight) {
        this.mHighLight = mHighLight;
    }

    public float getmShadow() {
        return mShadow;
    }

    public void setmShadow(float mShadow) {
        this.mShadow = mShadow;
    }

    public float getFade() {
        return fade;
    }

    public void setFade(float fade) {
        this.fade = fade;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getTint() {
        return tint;
    }

    public void setTint(float tint) {
        this.tint = tint;
    }

    public float getSaturationVal() {
        return m_saturationVal;
    }

    public void setSaturationVal(float saturationVal) {
        this.m_saturationVal = saturationVal;
    }

    public float getVignetteVal() {
        return m_vignetteVal;
    }

    public void setVignetteVal(float vignetteVal) {
        this.m_vignetteVal = vignetteVal;
    }

    public float getSharpenVal() {
        return m_sharpenVal;
    }

    public void setSharpenVal(float sharpenVal) {
        this.m_sharpenVal = sharpenVal;
    }

    public float getBrightnessVal() {
        return m_brightnessVal;
    }

    public void setBrightnessVal(float brightnessVal) {
        this.m_brightnessVal = brightnessVal;
    }

    public float getContrastVal() {
        return m_contrastVal;
    }

    public void setContrastVal(float contrastVal) {
        this.m_contrastVal = contrastVal;
    }

    public boolean isNoiseGrayscale() {
        return noiseGrayscale;
    }

    public void setNoiseGrayscale(boolean noiseGrayscale) {
        this.noiseGrayscale = noiseGrayscale;
    }

    @NonNull
    @Override
    protected CorrectionColorInfo clone() {
        try {
            CorrectionColorInfo correctionColorInfo = new CorrectionColorInfo();
            correctionColorInfo.setBrightnessVal(this.getBrightnessVal());
            correctionColorInfo.setSaturationVal(this.getSaturationVal());
            correctionColorInfo.setContrastVal(this.getContrastVal());
            correctionColorInfo.setVignetteVal(this.getVignetteVal());
            correctionColorInfo.setSharpenVal(this.getSharpenVal());
            correctionColorInfo.setmHighLight(this.getmHighLight());
            correctionColorInfo.setmShadow(this.getmShadow());
            correctionColorInfo.setTemperature(this.getTemperature());
            correctionColorInfo.setTint(this.getTint());
            correctionColorInfo.setFade(this.getFade());
            correctionColorInfo.setDensity(this.getDensity());
            correctionColorInfo.setDenoiseDensity(this.getDenoiseDensity());
            correctionColorInfo.setNoiseGrayscale(this.isNoiseGrayscale());
            return correctionColorInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
