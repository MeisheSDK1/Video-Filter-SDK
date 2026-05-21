package com.meishe.arscene.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.meishe.arscene.R;
import com.meishe.base.view.MagicProgress;

import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/2/14 14:20
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class SeekBarView extends ConstraintLayout {
    private TextView mSeekTitle;
    private MagicProgress mSeekProgress;

    public SeekBarView(Context context) {
        this(context, null);

    }

    public SeekBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.view_seek_bar, this);
        mSeekTitle = view.findViewById(R.id.seek_bar_title);
        mSeekProgress = view.findViewById(R.id.seek_bar_progress);
    }

    public void setSeekTitle(String title) {
        if (null == mSeekTitle) {
            return;
        }
        mSeekTitle.setText(title);
    }

    public void setSeekProgress(int progress) {
        if ((null == mSeekProgress) || (progress < 0)) {
            return;
        }
        mSeekProgress.setProgress(progress);
    }


    public void setMax(int max) {
        mSeekProgress.setMax(max);
    }

    public void setPointEnable(boolean pointEnable) {
        mSeekProgress.setPointEnable(pointEnable);
    }

    public void setBreakProgress(int breakProgress) {
        mSeekProgress.setBreakProgress(breakProgress);
    }

    public void setPointProgress(int pointProgress) {
        mSeekProgress.setPointProgress(pointProgress);
    }

    public void setProgress(int progress) {
        mSeekProgress.setProgress(progress);
    }

    public void setOnProgressChangeListener(MagicProgress.OnProgressChangeListener listener) {
        mSeekProgress.setOnProgressChangeListener(listener);
    }
}
