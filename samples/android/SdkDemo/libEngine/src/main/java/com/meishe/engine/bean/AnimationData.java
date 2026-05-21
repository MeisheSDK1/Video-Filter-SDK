package com.meishe.engine.bean;

/**
 * author：yangtailin on 2020/9/2 10:41
 * 此类为动画数据类
 * This class is an animated data class
 */
public class AnimationData {
    private long mInPoint;
    private long mOutPoint;
    private String mPackageFxPath;
    private String mType;

    /**
     * Gets in point.
     * 获取点
     *
     * @return the in point
     */
    public long getInPoint() {
        return mInPoint;
    }

    /**
     * Sets in point.
     * 设置点
     * @param inPoint the in point
     */
    public void setInPoint(long inPoint) {
        this.mInPoint = inPoint;
    }

    /**
     * Gets out point.
     * 获取结束点
     * @return the out point
     */
    public long getOutPoint() {
        return mOutPoint;
    }

    /**
     * Sets out point.
     * 设置结束点
     * @param outPoint the out point
     */
    public void setOutPoint(long outPoint) {
        this.mOutPoint = outPoint;
    }

    /**
     * Gets package fx path.
     * 获取包外汇路径
     * @return the package fx path
     */
    public String getPackageFxPath() {
        return mPackageFxPath;
    }

    /**
     * Sets package fx path.
     * 设置包外汇路径
     * @param packageFxPath the package fx path
     */
    public void setPackageFxPath(String packageFxPath) {
        this.mPackageFxPath = packageFxPath;
    }



    @Override
    public String toString() {
        return "AnimationData{" +
                "mInPoint=" + mInPoint +
                ", mOutPoint=" + mOutPoint +
                ", mPackageFxPath='" + mPackageFxPath + '\'' +
                '}';
    }
}
