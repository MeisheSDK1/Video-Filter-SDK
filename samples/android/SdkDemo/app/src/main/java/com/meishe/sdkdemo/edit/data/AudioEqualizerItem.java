package com.meishe.sdkdemo.edit.data;

import com.meishe.sdkdemo.bean.EqualizerType;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @author :Jml
 * @date :2021/6/25 13:46
 * @des : 音频均衡器调节的数据
 * Audio equalizer adjusts data
 * @Copyright: www.meishesdk.com Inc. All rights reserved
 */
public class AudioEqualizerItem extends EqualizerType.EqualizerTypeItemValue{

    /**
     * 频段声音的最大值
     * The maximum value of a band sound
     */
    private int maxVoice = 20;
    /**
     * 频段声音的最小值
     * The minimum value of frequency band sound
     */
    private int minVoice = -20;

    public AudioEqualizerItem(String equalizerKey, String showAudioEqualizerValue, int voiceValue, int maxVoice, int minVoice) {
        this.effectKey = equalizerKey;
        this.key = showAudioEqualizerValue;
        this.value = voiceValue;
        this.maxVoice = maxVoice;
        this.minVoice = minVoice;
    }

    public int getMaxVoice() {
        return maxVoice;
    }

    public void setMaxVoice(int maxVoice) {
        this.maxVoice = maxVoice;
    }

    public int getMinVoice() {
        return minVoice;
    }

    public void setMinVoice(int minVoice) {
        this.minVoice = minVoice;
    }
}
