package com.meishe.arscene.bean;

import com.meishe.arscene.inter.IFxInfo;

import java.io.Serializable;
import java.util.HashSet;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/11/22 10:14
 * @Description: 外界需传的美颜参数
 * Beauty parameters to be transmitted from the outside world
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class BeautyFxInfo implements Serializable {
    /**
     * 是否是单buffer模式
     * Whether the buffer mode is single
     */
    private boolean isSingleBufferMode;

    /**
     * 美白类型：美白A，美白B
     * 默认美白B,且数据列表也选择的是美白B数据
     * Whitening type: Whitening A, whitening B
     * The default is whitening B, and the data list also selects whitening B data
     */
    private String beautyType = FxParams.BEAUTY_B;

    /**
     * 是否支持“去油光”
     * Whether to support "degreasing"
     */
    private boolean isSupportMatte;

    /**
     * 是否开启“校色”
     * Whether to enable "Color Check"
     */
    private boolean isOpenAdjustColor;

    /**
     * 是否开启锐度
     * Whether to enable sharpness
     */
    private boolean isOpenSharpen;

    /**
     * 是否开启美肤
     * Whether to turn on the beauty
     */
    private boolean isOpenSkin;

    /**
     * 是否开启美型
     * Whether to open the shape type
     */
    private boolean isOpenFace;

    /**
     * 是否开启微整形
     * Whether to open the micro shape type
     */
    private boolean isOpenSmall;

    private HashSet<IFxInfo> beautys;

    public boolean isSingleBufferMode() {
        return isSingleBufferMode;
    }

    public void setSingleBufferMode(boolean singleBufferMode) {
        isSingleBufferMode = singleBufferMode;
    }

    public String getBeautyType() {
        return beautyType;
    }

    public void setBeautyType(String beautyType) {
        this.beautyType = beautyType;
    }

    public boolean isSupportMatte() {
        return isSupportMatte;
    }

    public void setSupportMatte(boolean supportMatte) {
        isSupportMatte = supportMatte;
    }

    public boolean isOpenAdjustColor() {
        return isOpenAdjustColor;
    }

    public void setOpenAdjustColor(boolean openAdjustColor) {
        isOpenAdjustColor = openAdjustColor;
    }

    public boolean isOpenSharpen() {
        return isOpenSharpen;
    }

    public void setOpenSharpen(boolean openSharpen) {
        isOpenSharpen = openSharpen;
    }

    public boolean isOpenSkin() {
        return isOpenSkin;
    }

    public void setOpenSkin(boolean openSkin) {
        isOpenSkin = openSkin;
    }

    public boolean isOpenFace() {
        return isOpenFace;
    }

    public void setOpenFace(boolean openFace) {
        isOpenFace = openFace;
    }

    public boolean isOpenSmall() {
        return isOpenSmall;
    }

    public void setOpenSmall(boolean openSmall) {
        isOpenSmall = openSmall;
    }

    public HashSet<IFxInfo> getBeautys() {
        return beautys;
    }

    public void setBeautys(HashSet<IFxInfo> beautys) {
        this.beautys = beautys;
    }
}
