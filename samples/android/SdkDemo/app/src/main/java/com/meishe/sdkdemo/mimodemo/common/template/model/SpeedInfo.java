package com.meishe.sdkdemo.mimodemo.common.template.model;


import com.meishe.sdkdemo.mimodemo.common.Constants;

public class SpeedInfo {
    /**
     * 变速片段起始时间，单位是毫秒，当前值代表变速后的时长
     * The start time of the shift segment, in milliseconds. The current value represents the duration after the shift
     */
    private float start;
    /**
     * 变速片段终止时间，单位是毫秒，当前值代表变速后的时长
     * Variable segment termination time, in milliseconds. The current value represents the duration after the change
     */
    private float end;
    /**
     * 起始速度值
     * Initial velocity value
     */
    private float speed0 = Constants.DEFAULT_SPEED_VALUE;
    /**
     * 终止速度值
     * Termination velocity value
     */
    private float speed1 = Constants.DEFAULT_SPEED_VALUE;

    public SpeedInfo() {
    }

    public SpeedInfo(float start, float end, float speed0, float speed1) {
        this.start = start;
        this.end = end;
        this.speed0 = speed0;
        this.speed1 = speed1;
    }

    long needDuration;

    public long getNeedDuration() {
        return needDuration;
    }

    public void setNeedDuration(long needDuration) {
        this.needDuration = needDuration;
    }

    public float getStart() {
        return start;
    }

    public void setStart(float start) {
        this.start = start;
    }

    public float getEnd() {
        return end;
    }

    public void setEnd(float end) {
        this.end = end;
    }

    public float getSpeed0() {
        return speed0;
    }

    public void setSpeed0(float speed0) {
        this.speed0 = speed0;
    }

    public float getSpeed1() {
        return speed1;
    }

    public void setSpeed1(float speed1) {
        this.speed1 = speed1;
    }
}
