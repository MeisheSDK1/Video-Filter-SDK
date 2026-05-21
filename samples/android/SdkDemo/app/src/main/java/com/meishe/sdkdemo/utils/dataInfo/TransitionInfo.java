package com.meishe.sdkdemo.utils.dataInfo;

import com.meicam.sdk.NvsVideoTransition;
import com.meishe.sdkdemo.R;

public class TransitionInfo {
    public static int TRANSITIONMODE_BUILTIN = 0;
    public static int TRANSITIONMODE_PACKAGE = 1;

    private int m_transitionMode;
    private String m_transitionId;

    private int m_imageId;
    private String m_imageUrl;

    private long mTransitionInterval = 2000000L;

    private NvsVideoTransition mVideoTransition;
    /**
     * 因为目前设置转场和调节专场时间走的是同一个方法，所以这里添加一个参数 分开设置
     * 单独设置转场不设置时间
     * 当前是否是在调节专场时间
     * Because the current method for setting the transition and adjusting the special time is the same, we will add a separate parameter here
     * Set the transition separately without setting the time
     * Is the special session time being adjusted
     */
    private boolean mChangeTransitionDuration;

    public long getTransitionInterval() {
        return mTransitionInterval;
    }

    public void setTransitionInterval(long transitionInterval) {
        this.mTransitionInterval = transitionInterval;
    }

    public NvsVideoTransition getVideoTransition() {
        return mVideoTransition;
    }

    public void setVideoTransition(NvsVideoTransition videoTransition) {
        this.mVideoTransition = videoTransition;
    }

    public int getM_imageId() {
        return m_imageId;
    }

    public void setM_imageId(int m_imageId) {
        this.m_imageId = m_imageId;
    }

    public String getM_imageUrl() {
        return m_imageUrl;
    }

    public void setM_imageUrl(String m_imageUrl) {
        this.m_imageUrl = m_imageUrl;
    }

    public TransitionInfo() {
        m_transitionId = "Fade";
        m_transitionMode = TRANSITIONMODE_BUILTIN;
        m_imageId = R.mipmap.fade;
        m_imageUrl = "";
    }

    public void setTransitionMode(int mode) {
        m_transitionMode = mode;
    }

    public int getTransitionMode() {
        return m_transitionMode;
    }

    public void setTransitionId(String fxId) {
        m_transitionId = fxId;
    }

    public String getTransitionId() {
        return m_transitionId;
    }

    public TransitionInfo copySelf() {
        TransitionInfo transitionInfo = new TransitionInfo();
        transitionInfo.setVideoTransition(mVideoTransition);
        transitionInfo.setTransitionInterval(mTransitionInterval);
        transitionInfo.setTransitionMode(m_transitionMode);
        transitionInfo.setTransitionId(m_transitionId);
        transitionInfo.setM_imageId(m_imageId);
        transitionInfo.setM_imageUrl(m_imageUrl);
        return transitionInfo;
    }

    public void setChangeDuration(boolean changeTransitionDuration) {
        this.mChangeTransitionDuration = changeTransitionDuration;
    }

    public boolean ismChangeTransitionDuration() {
        return mChangeTransitionDuration;
    }
}
