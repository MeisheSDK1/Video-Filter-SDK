package com.meishe.sdkdemo.edit.clipEdit.adjust;

import android.graphics.Point;

import com.meicam.sdk.NvsTimeline;
import com.meishe.engine.bean.CutData;

import java.util.Map;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yangtailin
 * @CreateDate :2021/11/22 17:10
 * @Description :区域裁剪fragment接口定义 Region clipping fragment interface definition.
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public interface ICutRegionFragment {
    /**
     * Rotate video.
     *
     * @param degree the degree
     */
    void rotateVideo(float degree);

    /**
     * Reset.
     */
    void reset();

    /**
     * Seek timeline.
     *
     * @param timestamp    the timestamp
     * @param seekShowMode the seek show mode
     */
    void seekTimeline(long timestamp, int seekShowMode);

    /**
     * Sets cut data.
     *
     * @param cutData the cut data
     */
    void setCutData(CutData cutData);

    /**
     * Sets time line.
     *
     * @param timeline the timeline
     */
    void setTimeLine(NvsTimeline timeline);

    /**
     * Init data.
     */
    void initData();

    /**
     * Gets trans from data.
     *
     * @param originalTimelineWidth  the original timeline width
     * @param originalTimelineHeight the original timeline height
     * @return the trans from data
     */
    Map<String, Float> getTransFromData(int originalTimelineWidth, int originalTimelineHeight);

    /**
     * Get rect view size int [ ].
     *
     * @return the int [ ]
     */
    int[] getRectViewSize();

    /**
     * Get region data float [ ].
     *
     * @param size the size
     * @return the float [ ]
     */
    float[] getRegionData(float[] size);

    /**
     * Gets ratio.
     *
     * @return the ratio
     */
    int getRatio();

    /**
     * Gets ratio value.
     *
     * @return the ratio value
     */
    float getRatioValue();

    /**
     * Change cut rect view.
     *
     * @param ratio the ratio
     */
    void changeCutRectView(int ratio);

    /**
     * Sets on cut rect change listener.
     *
     * @param listener the listener
     */
    void setOnCutRectChangeListener(CutVideoFragment.OnCutRectChangedListener listener);

    /**
     * 旋转clip
     * @param degree 角度
     */
    void rotateClip(float degree);


    /**
     * The interface On cut rect changed listener.
     */
    interface OnCutRectChangedListener {
        /**
         * On scale and rotate.
         *
         * @param scale  the scale
         * @param degree the degree
         */
        void onScaleAndRotate(float scale, float degree);

        /**
         * On size changed.
         *
         * @param size the size
         */
        void onSizeChanged(Point size);
    }

}
