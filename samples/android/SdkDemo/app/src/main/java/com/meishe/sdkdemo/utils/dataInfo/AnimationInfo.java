package com.meishe.sdkdemo.utils.dataInfo;

import androidx.annotation.NonNull;

/**
 * @author :Jml
 * @date :2020/8/26 15:27
 * @des : 视频片段上的动画特效信息
 * Information about the animation effects on the video clip
 */
public class AnimationInfo implements Cloneable{
    private String mPackageId;
    private long mAnimationIn;
    private long mAnimationOut;
    private int mAssetType;
    private boolean isPostPackage;

    public boolean isPostPackage() {
        return isPostPackage;
    }

    public void setPostPackage(boolean postPackage) {
        isPostPackage = postPackage;
    }

    public int getmAssetType() {
        return mAssetType;
    }

    public void setmAssetType(int mAssetType) {
        this.mAssetType = mAssetType;
    }

    public void setmPackageId(String mPackageId) {
        this.mPackageId = mPackageId;
    }

    public void setmAnimationIn(long mAnimationIn) {
        this.mAnimationIn = mAnimationIn;
    }

    public void setmAnimationOut(long mAnimationOut) {
        this.mAnimationOut = mAnimationOut;
    }

    public String getmPackageId() {
        return mPackageId;
    }

    public long getmAnimationIn() {
        return mAnimationIn;
    }

    public long getmAnimationOut() {
        return mAnimationOut;
    }

    @NonNull
    @Override
    protected AnimationInfo clone() {
        try {
            return (AnimationInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
