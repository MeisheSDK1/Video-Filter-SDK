package com.meishe.sdkdemo.edit.audio.view;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.data.AudioEqualizerItem;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @author :Jml
 * @date :2021/6/25 11:05
 * @des :
 * @Copyright: www.meishesdk.com Inc. All rights reserved
 */
public class AudioEqualizerAdjustItemView extends LinearLayout {
    private TextView mTvCurrent;
    private TextView mTvEqualizerValue;
    private AudioEqualizerVerticalSeekBar mSeekBar;

    private int mMaxValue = 20;
    private int mMinValue = -20;
    private String audioKey;

    private void setAudioKey(String audioKey) {
        this.audioKey = audioKey;
    }

    private AudioEqualizerItem audioEqualizerItem;
    public AudioEqualizerAdjustItemView(Context context) {
        this(context,null);
    }

    public AudioEqualizerAdjustItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AudioEqualizerAdjustItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        View root = LayoutInflater.from(context).inflate(R.layout.activity_audio_equalizer_adjust_item,this);
        mTvCurrent = root.findViewById(R.id.tv_current);
        mTvEqualizerValue = root.findViewById(R.id.audio_equalizer_value);
        mSeekBar = root.findViewById(R.id.seek_bar);
        mSeekBar.setMaxProgress(40);
        mSeekBar.setProgress(20);
        mSeekBar.setOnSlideChangeListener(new AudioEqualizerVerticalSeekBar.SlideChangeListener() {

            @Override
            public void onStart(AudioEqualizerVerticalSeekBar slideView, int progress) {

            }

            @Override
            public void onProgress(AudioEqualizerVerticalSeekBar slideView, int progress) {
                int showPro = progress + mMinValue;
                mTvCurrent.setText(String.valueOf(showPro));
                if (itemListener != null) {
                    itemListener.onItemProgressChange(audioKey, showPro);
                }
            }

            @Override
            public void onStop(AudioEqualizerVerticalSeekBar slideView, int progress) {

            }
        });
    }

    private void setMax(int maxValue){
        this.mMaxValue = maxValue;
    }

    private void setMin(int minValue){
        this.mMinValue = minValue;
    }

    /**
     * 设置当前的值，区间于 min -- max
     * Set the current value in min -- max
     * @param current
     */
    private void setDefaultValue(int current) {
        if (current >= mMinValue && current <= mMaxValue) {
            int progress = current - mMinValue;
            mSeekBar.setProgress(progress);
            mTvCurrent.setText(String.valueOf(current));
        }
    }

    /**
     * 设置当前频段的值
     * Set the value of the current frequency band
     * @param showValue 频段展示的值
     */
    private void setAudioEqualizerValue(String showValue){
        mTvEqualizerValue.setText(showValue);
    }


    public void setAudioEqualizerItem(AudioEqualizerItem audioEqualizerItem){
        this.audioEqualizerItem = audioEqualizerItem;
        if(null != audioEqualizerItem){
            setMax(audioEqualizerItem.getMaxVoice());
            setMin(audioEqualizerItem.getMinVoice());
            setDefaultValue(audioEqualizerItem.getValue());
            setAudioEqualizerValue(audioEqualizerItem.getKey());
            setAudioKey(audioEqualizerItem.getEffectKey());
        }
    }

    public interface onItemProgressChangeListener {
        void onItemProgressChange(String audioKey, int audioValue);
    }

    private onItemProgressChangeListener itemListener;

    public void setOnItemProgressChangeListener(AudioEqualizerAdjustItemView.onItemProgressChangeListener itemListener) {
        this.itemListener = itemListener;
    }
}
