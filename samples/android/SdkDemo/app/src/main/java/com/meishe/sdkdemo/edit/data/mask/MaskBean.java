package com.meishe.sdkdemo.edit.data.mask;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/3/5.
 * @Description :蒙版Adapter实体Bean。Mask Adapter bean class
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MaskBean {
    /**
     * 名称
     * name
     */
    private String name;
    /**
     * 背景资源
     * cover
     */
    private int coverId;
    /**
     * 蒙版类型区分标记
     * Mask type differentiating tag
     */
    private int maskType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaskType() {
        return maskType;
    }

    public void setMaskType(int maskType) {
        this.maskType = maskType;
    }

    public int getCoverId() {
        return coverId;
    }

    public void setCoverId(int coverId) {
        this.coverId = coverId;
    }

    public class MaskType {
        /**
         * The constant NONE.
         * 常数没有
         */
        public static final int NONE = 0;
        /**
         * The constant LINE.
         * 不断线
         */
        public static final int LINE = 1;
        /**
         * The constant MIRROR.
         * 不断的镜子
         */
        public static final int MIRROR = 2;
        /**
         * The constant CIRCLE.
         * 不断循环
         */
        public static final int CIRCLE = 3;
        /**
         * The constant RECT.
         * 矩形
         */
        public static final int RECT = 4;
        /**
         * The constant HEART.
         * 心形
         */
        public static final int HEART = 5;
        /**
         * The constant STAR.
         * 星形
         */
        public static final int STAR = 6;
    }
}
