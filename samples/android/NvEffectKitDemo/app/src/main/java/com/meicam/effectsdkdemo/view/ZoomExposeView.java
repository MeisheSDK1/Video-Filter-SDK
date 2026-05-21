package com.meicam.effectsdkdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.meicam.effectsdkdemo.R;

import androidx.annotation.Nullable;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/5/24 19:52
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class ZoomExposeView extends LinearLayout {
    public static final int TYPE_ZOOM = 0x11;
    public static final int TYPE_EXPOSE = 0x12;
    private final SeekBar mSeekBar;
    private OnIntensityListener mOnIntensityListener;

    public ZoomExposeView(Context context) {
        this(context, null);
    }

    public ZoomExposeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomExposeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View rootView = LayoutInflater.from(context).inflate(R.layout.zoom_and_expose_view, this);
        mSeekBar = rootView.findViewById(R.id.zoom_expose_seekbar);
        initListener();
    }

    private void initListener() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && (null != mOnIntensityListener)) {
                    mOnIntensityListener.onIntensity(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setMax(int maxValue) {
        if (null == mSeekBar) {
            return;
        }
        mSeekBar.setMax(maxValue);
    }

    public void setProgress(int progress) {
        if (null == mSeekBar) {
            return;
        }
        mSeekBar.setProgress(progress);
    }

    public void setOnIntensityListener(OnIntensityListener onIntensityListener) {
        mOnIntensityListener = onIntensityListener;
    }

    public interface OnIntensityListener {
        void onIntensity(int progress);
    }
}
