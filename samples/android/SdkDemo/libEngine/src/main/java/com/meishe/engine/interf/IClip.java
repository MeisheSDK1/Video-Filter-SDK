package com.meishe.engine.interf;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yangtailin
 * @CreateDate :2020/12/18 14:48
 * @Description :Clip接口规范 Interface of clip
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public interface IClip {
    /**
     * Gets nvs object.
     *
     * @return the nvs object
     */
    Object getNvsObject();

    /**
     * Sets nvs object.
     *
     * @param nvsObject the nvs object
     */
    void setNvsObject(Object nvsObject);

    /**
     * Gets audio type.
     *
     * @return the audio type
     */
    int getAudioType();

    /**
     * Sets audio type.
     *
     * @param mAudioType the m audio type
     */
    void setAudioType(int mAudioType);

    /**
     * Gets clip index in track.
     *
     * @return the clip index in track
     */
    int getClipIndexInTrack();

    /**
     * Sets clip index in track.
     *
     * @param clipIndexInTrack the clip index in track
     */
    void setClipIndexInTrack(int clipIndexInTrack);

    /**
     * Gets track index.
     *
     * @return the track index
     */
    int getTrackIndex();

    /**
     * Sets track index.
     *
     * @param trackIndex the track index
     */
    void setTrackIndex(int trackIndex);

    /**
     * Gets in point.
     *
     * @return the in point
     */
    long getInPoint();

    /**
     * Sets in point.
     *
     * @param inPoint the in point
     */
    void setInPoint(long inPoint);

    /**
     * Gets speed.
     *
     * @return the speed
     */
    double getSpeed();

    /**
     * Sets speed.
     *
     * @param speed the speed
     */
    void setSpeed(double speed);

    /**
     * Gets type.
     *
     * @return the type
     */
    String getType();

    /**
     * Sets type.
     *
     * @param type the type
     */
    void setType(String type);

    /**
     * Gets icon file path.
     *
     * @return the icon file path
     */
    String getIconFilePath();

    /**
     * Sets icon file path.
     *
     * @param iconFilePath the icon file path
     */
    void setIconFilePath(String iconFilePath);

    /**
     * Gets file path.
     *
     * @return the file path
     */
    String getFilePath();

    /**
     * Sets file path.
     *
     * @param filePath the file path
     */
    void setFilePath(String filePath);

    /**
     * Gets text.
     *
     * @return the text
     */
    String getText();

    /**
     * Sets text.
     *
     * @param text the text
     */
    void setText(String text);

    /**
     * Gets trim in.
     *
     * @return the trim in
     */
    long getTrimIn();

    /**
     * Sets trim in.
     *
     * @param trimIn the trim in
     */
    void setTrimIn(long trimIn);

    /**
     * Gets trim out.
     *
     * @return the trim out
     */
    long getTrimOut();

    /**
     * Sets trim out.
     *
     * @param trimOut the trim out
     */
    void setTrimOut(long trimOut);

    /**
     * Get record array float [ ].
     *
     * @return the float [ ]
     */
    float[] getRecordArray();

    /**
     * Sets record array.
     *
     * @param recordArray the record array
     */
    void setRecordArray(float[] recordArray);

    /**
     * Sets record length.
     *
     * @param recordLength the record length
     */
    void setRecordLength(int recordLength);

    /**
     * Gets record length.
     *
     * @return the record length
     */
    int getRecordLength();

    /**
     * Gets audio fade in.
     *
     * @return the audio fade in
     */
    long getAudioFadeIn();

    /**
     * Sets audio fade in.
     *
     * @param mAudioFadeIn the m audio fade in
     */
    void setAudioFadeIn(long mAudioFadeIn);

    /**
     * Gets audio fade out.
     *
     * @return the audio fade out
     */
    long getAudioFadeOut();

    /**
     * Sets audio fade out.
     *
     * @param mAudioFadeOut the m audio fade out
     */
    void setAudioFadeOut(long mAudioFadeOut);

    /**
     * Gets curve speed name.
     *
     * @return the curve speed name
     */
    String getCurveSpeedName();

    /**
     * Sets curve speed name.
     *
     * @param curveSpeedName the curve speed name
     */
    void setCurveSpeedName(String curveSpeedName);

}
