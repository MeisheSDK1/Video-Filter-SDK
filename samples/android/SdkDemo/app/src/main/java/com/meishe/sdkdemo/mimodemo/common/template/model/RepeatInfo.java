package com.meishe.sdkdemo.mimodemo.common.template.model;

/**
 * 画面反复数据类
 * Screen repetition data class
 */
public class RepeatInfo {
    /**
     * 画面反复起始时间，单位是毫秒
     * Screen iteration start time, in milliseconds
     */
    private float start;
    /**
     * 画面反复终止时间，单位是毫秒
     * Screen time, in milliseconds
     */
    private float end;
    /**
     * 画面反复次数。画面正放倒放再正放反复为1次
     * Number of screen repetitions. The screen is put backwards and then put again for 1 time
     */
    private int count;
    /**
     * 当前片段效果所需要的原始视频长度，单位是毫秒
     * The length of the original video required for the current clip effect, in milliseconds
     */
    private float originDuration;

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getOriginDuration() {
        return originDuration;
    }

    public void setOriginDuration(float originDuration) {
        this.originDuration = originDuration;
    }
}
