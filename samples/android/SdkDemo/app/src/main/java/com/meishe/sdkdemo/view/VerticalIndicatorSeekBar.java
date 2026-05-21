package com.meishe.sdkdemo.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishe.sdkdemo.edit.view.VerticalSeekBar;

import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.utils.ScreenUtils;

public class VerticalIndicatorSeekBar extends RelativeLayout {
    private Context mContext;
    private View mTextViewLayout;
    private OnSeekBarChangedListener mOnSeekBarChangedListener;
    private VerticalSeekBar mSeekBar;
    private TextView mTextView;
    private int mSeekBarWidth = 0;

    public void setOnSeekBarChangedListener(OnSeekBarChangedListener listener) {
        mOnSeekBarChangedListener = listener;
    }

    public void setProgress(final int progress) {
        mSeekBar.setProgress(progress);
        mTextView.setText(String.valueOf(progress));
        if (mSeekBarWidth == 0) {
            mSeekBar.post(new Runnable() {
                @Override
                public void run() {
                    setTextLocation(mSeekBar, progress);
                }
            });
        } else {
            setTextLocation(mSeekBar, progress);
        }
        //mSeekBar.onSizeChanged(mSeekBar.getWidth(), mSeekBar.getHeight(), 0, 0);
    }

    public void setMaxProgress(int progress) {
        mSeekBar.setMaxProgress(progress);
        mTextView.setText(String.valueOf(progress));
        if (mSeekBarWidth == 0) {
            mSeekBar.post(new Runnable() {
                @Override
                public void run() {
                    setTextLocation(mSeekBar, progress);
                }
            });
        } else {
            setTextLocation(mSeekBar, progress);
        }
    }

    public VerticalIndicatorSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.view_indicator_vertical_seek_bar, this);
        mTextViewLayout = rootView.findViewById(R.id.seek_text_layout);
        mTextView = rootView.findViewById(R.id.seek_text);
        mSeekBar = rootView.findViewById(R.id.seekBar);
        mSeekBar.setThumb(R.mipmap.round_white);
        mSeekBar.setThumbSizeDp(18, 18);
        mSeekBar.setSelectColor(Color.parseColor("#ffffffff"));
        mSeekBar.setmInnerProgressWidthDp(3);
        mSeekBar.setOnSlideChangeListener(new VerticalSeekBar.SlideChangeListener() {
            @Override
            public void onStart(VerticalSeekBar slideView, int progress) {
                if (mOnSeekBarChangedListener != null) {
                    mOnSeekBarChangedListener.onStartTrackingTouch(slideView);
                }
            }

            @Override
            public void onProgress(VerticalSeekBar slideView, int progress) {
                int text = progress;
                //设置文本显示 Set text display
                mTextView.setText(String.valueOf(text));
                setTextLocation(slideView, progress);

                if (mOnSeekBarChangedListener != null) {
                    mOnSeekBarChangedListener.onProgressChanged(slideView, progress, true);
                }
            }

            @Override
            public void onStop(VerticalSeekBar slideView, int progress) {
                if (mOnSeekBarChangedListener != null) {
                    mOnSeekBarChangedListener.onStopTrackingTouch(slideView);
                }
            }
        });
    }

    private void setTextLocation(VerticalSeekBar seekBar, int progress) {
        float textHeight = mTextView.getHeight();
        float bottom = seekBar.getBottom();
        float max = Math.abs(seekBar.getMaxProgress());
        float thumb = ScreenUtils.dip2px(mContext, 15);
        float average = (((float) seekBar.getHeight()) - 2 * thumb) / max;
        float currentProgress = progress;
        float poy = bottom - textHeight / 2 - thumb - average * currentProgress;
        mTextViewLayout.setY(poy);
    }

    public interface OnSeekBarChangedListener {
        void onProgressChanged(VerticalSeekBar seekBar, int progress, boolean fromUser);

        void onStartTrackingTouch(VerticalSeekBar seekBar);

        void onStopTrackingTouch(VerticalSeekBar seekBar);
    }
}