package com.meishe.sdkdemo.edit.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.meishe.sdkdemo.R;

/**
 * @author :Jml
 * @date :2020/11/26 15:16
 * @des : 校色中噪点调节使用
 * Adjust the noise point in color calibration
 */
public class DenoiseAdjustView extends LinearLayout {
    private TextView tv_single, tv_multi;
    private SeekBar seek_intensity, seek_density;
    private TextView tv_reset;
    private OnFunctionListener onFunctionListener;

    public DenoiseAdjustView(Context context) {
        this(context, null);
    }

    public DenoiseAdjustView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DenoiseAdjustView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        setListener();
    }


    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_denoise_adjust, this);
        tv_single = view.findViewById(R.id.tv_single);
        tv_multi = view.findViewById(R.id.tv_multi);
        tv_reset = view.findViewById(R.id.tv_reset);
        seek_intensity = view.findViewById(R.id.seek_intensity);
        seek_density = view.findViewById(R.id.seek_density);
    }

    private void setListener() {
        tv_single.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onFunctionListener) {
                    onFunctionListener.onSelectMode(true);
                }
                setSelectedModeBottomLine(tv_single, tv_multi);
            }
        });
        tv_multi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onFunctionListener) {
                    onFunctionListener.onSelectMode(false);
                }
                setSelectedModeBottomLine(tv_multi, tv_single);
            }
        });
        tv_reset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onFunctionListener) {
                    onFunctionListener.onReset();
                }
            }
        });
        seek_intensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && null != onFunctionListener) {
                    onFunctionListener.onIntensityChanged(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seek_density.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && null != onFunctionListener) {
                    onFunctionListener.onDensityChanged(progress);
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

    public void setNoiseGrayscale(boolean noiseGrayscale) {
        setSelectedModeBottomLine(noiseGrayscale ? tv_single : tv_multi, noiseGrayscale ? tv_multi : tv_single);
    }

    private void setSelectedModeBottomLine(TextView tv_selected, TextView tv_unselected) {
        Drawable drawable = getResources().getDrawable(
                R.mipmap.adjust_denoise_bottom_line);
        // 这一步必须要做,否则不会显示. This step must be done, otherwise it will not be displayed.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());
        tv_selected.setCompoundDrawables(null, null, null, drawable);
        tv_unselected.setCompoundDrawables(null, null, null, null);
        tv_selected.setTextColor(getResources().getColor(R.color.white));
        tv_unselected.setTextColor(getResources().getColor(R.color.ccffffff));
    }

    public void setOnFunctionListener(OnFunctionListener onFunctionListener) {
        this.onFunctionListener = onFunctionListener;
    }

    /**
     * 设置噪点密度进度
     * Set the noise density progress
     *
     * @param denoiseDensityProgress
     */
    public void setDenoiseDensityProgress(int denoiseDensityProgress) {
        seek_density.setProgress(denoiseDensityProgress);
    }

    /**
     * 设置噪点程度进度
     * Set the noise level progress
     *
     * @param progress
     */
    public void setDenoiseProgress(int progress) {
        seek_intensity.setProgress(progress);
    }

    public interface OnFunctionListener {
        void onSelectMode(boolean single);

        void onIntensityChanged(int progress);

        void onDensityChanged(int progress);

        void onReset();
    }
}
