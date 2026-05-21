package com.meishe.sdkdemo.capture.bean;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/25 下午4:25
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public abstract class BaseEffectInfo {

    /**
     * 特效类别
     * Effect type
     */
    public int type;

    /**
     * 特效类型
     * Effect type
     */
    public int captureVideoFxType;

    /**
     * 包类型安装的id
     * Fx package id
     */
    public String uuid;

    /**
     *内建特效名字
     * Buildin fx name
     */
    public String fxName;

    /**
     * 调节特效的key
     * adjust fx key
     */
    public String fxParam;
    /**
     * 特效强度
     * fx strength
     */
    public float strength;

    public static class CaptureVideoFxType{
        static final int TYPE_BUILTIN = 0;
        static final int TYPE_PACKAGE = 1;
        static final int TYPE_CUSTOM = 2;
    }

}
