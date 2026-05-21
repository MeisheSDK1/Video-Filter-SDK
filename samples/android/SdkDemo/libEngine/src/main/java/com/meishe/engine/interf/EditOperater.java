package com.meishe.engine.interf;

import com.meicam.sdk.NvsTimeline;
import com.meishe.engine.bean.MeicamVideoClip;

/**
 * 编辑Timeline对象的方法
 * A method to edit the Timeline object
 */
public interface EditOperater {


    /**
     * 获取当前timeline
     * Get current timeline
     * @return
     */
    NvsTimeline getCurrentTimeline();

    /**
     * 获取编辑的VideoClip
     * Get the edited VideoClip
     * @param timeStamp 当前timeline的时间戳
     * @return
     */
    MeicamVideoClip getEditVideoClip(long timeStamp, int trackIndex);

    /**
     * 获取当前timeline的时间戳
     * Gets the timestamp of the current timeline
     * * @return
     */
    long getCurrentTimelinePosition();

}
