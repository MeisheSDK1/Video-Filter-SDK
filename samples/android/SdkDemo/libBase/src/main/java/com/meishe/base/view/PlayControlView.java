package com.meishe.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meishe.base.R;

/**
 * Created by CaoZhiChao on 2020/11/5 10:09
 * 播放控制视图类
 * Play Control View class
 */
public class PlayControlView extends RelativeLayout {
    private static final String TAG = "PlayControlView";
    private OnSeekBarListener mListener;
    private Context mContext;
    public SeekBar mSeekBar;
    private TextView mStartText;
    private TextView mCurrentText;
    private ImageView mBasePlayImg;
    private OnPlayClickListener mOnPlayClickListener;

    public PlayControlView(Context context) {
        super(context);
        initView(context);
    }


    public PlayControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PlayControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        View rootView = LayoutInflater.from(context).inflate(R.layout.base_play_seek_bar, this);
        mSeekBar = rootView.findViewById(R.id.base_seek_bar);
        mStartText = rootView.findViewById(R.id.base_tv_start_text);
        mCurrentText = rootView.findViewById(R.id.base_tv_current_text);
        mBasePlayImg = rootView.findViewById(R.id.base_play_img);
        initListener();
    }

    /**
     * Gets max progress.
     * 最大进度值
     *
     * @return the max progress
     */
    public float getMaxProgress() {
        return mSeekBar.getMax();
    }

    /**
     * Gets min progress.
     * 最小进度值
     *
     * @return the min progress
     */
    public float getMinProgress() {
        return 0;
    }

    /**
     * Sets max.
     * 设置最大值
     *
     * @param max the max
     */
    public void setMax(int max) {
        if (mSeekBar != null) {
            Log.e(TAG, "setMax: " + max);
            mSeekBar.setMax(max);
        }
    }

    /**
     * Sets progress.
     * 设置进度条
     *
     * @param progress the progress
     */
    public void setProgress(int progress) {
        if (mSeekBar != null) {
            mSeekBar.setProgress(progress);
        }
    }

    /**
     * Sets progress.
     * 获取进度条
     */
    public int getProgress() {
        if (mSeekBar != null) {
            return mSeekBar.getProgress();
        }
        return 0;
    }


    private void initListener() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mListener != null) {
                    mListener.onProgressChanged(seekBar, progress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mListener != null) {
                    mListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mListener != null) {
                    mListener.onStopTrackingTouch(seekBar);
                }
            }
        });
        mBasePlayImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPlayClickListener != null) {
                    mOnPlayClickListener.onPlayClick();
                }
            }
        });
    }

    /**
     * Sets listener.
     * 设置监听
     *
     * @param listener the listener
     */
    public void setListener(OnSeekBarListener listener) {
        this.mListener = listener;
    }

    /**
     * The interface On seek bar listener.
     * 拖动条监听接口
     */
    public interface OnSeekBarListener {
        /**
         * On start tracking touch.
         * 开始跟踪触摸
         *
         * @param seekBar the seek bar 拖动条
         */
        void onStartTrackingTouch(SeekBar seekBar);

        /**
         * On stop tracking touch.
         * 停止跟踪触摸
         *
         * @param seekBar the seek bar 拖动条
         */
        void onStopTrackingTouch(SeekBar seekBar);

        /**
         * On progress changed.
         * 进度改变
         *
         * @param seekBar  the seek bar 拖动条
         * @param progress the progress 进度
         * @param fromUser the from user 来自用户
         */
        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);
    }

    /**
     * The interface On play click listener.
     * 播放界面单击“监听器”的接口
     */
    public interface OnPlayClickListener {
        /**
         * On play click.
         * 播放监听
         */
        void onPlayClick();
    }

    /**
     * Sets on play click listener.
     * 设置播放点击的监听
     *
     * @param onPlayClickListener the on play click listener
     */
    public void setOnPlayClickListener(OnPlayClickListener onPlayClickListener) {
        mOnPlayClickListener = onPlayClickListener;
    }

    /**
     * Sets start text visible.
     * 设置开始文本可见
     *
     * @param visible the visible
     */
    public void setStartTextVisible(boolean visible) {
        mStartText.setVisibility(visible ? VISIBLE : GONE);
    }

    /**
     * Sets end text visible.
     * 设置结束文本可见
     *
     * @param visible the visible
     */
    public void setEndTextVisible(boolean visible) {
        mCurrentText.setVisibility(visible ? VISIBLE : GONE);
    }

    /**
     * Sets current text.
     * 设置当前文本
     *
     * @param currentValue the current value 当前文本
     */
    public void setCurrentText(String currentValue) {
        if (mCurrentText != null) {
            mCurrentText.setText(currentValue);
        }
    }

    /**
     * Sets start text.
     * 设置开始文本
     *
     * @param startValue the start value 开始值
     */
    public void setStartText(String startValue) {
        if (mStartText != null) {
            mStartText.setText(startValue);
        }
    }

    /**
     * Chang play state.
     * 播放状态
     *
     * @param isPlaying the is playing 正在播放
     */
    public void changPlayState(boolean isPlaying) {
        if (isPlaying) {
            mBasePlayImg.setBackgroundResource(R.mipmap.base_pause);
        } else {
            mBasePlayImg.setBackgroundResource(R.mipmap.base_play);
        }
    }
}

