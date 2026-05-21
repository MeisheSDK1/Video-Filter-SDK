package com.meicam.effectsdkdemo.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meicam.effectsdkdemo.R;
import com.meishe.libbase.util.ScreenUtils;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/5/25 13:44
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class RecordView extends RelativeLayout implements View.OnClickListener {
    public static final int RECORD_STUTUS_RECORDING = 0x111;
    public static final int RECORD_STUTUS_STOP = 0x112;
    private static final int RECORD_TYPE_PICTURE = 0x11;
    private static final int RECORD_TYPE_VIDEO = 0x22;
    private LinearLayout mLayoutRecordType;
    private Button mTakePicture;
    private Button mTakeVideo;
    private Button mStartRecord;
    private int mRecordType = RECORD_TYPE_VIDEO;
    private int mRecordStatus = RECORD_STUTUS_STOP;
    private OnRecordListener mOnRecordListener;

    public RecordView(Context context) {
        this(context, null);
    }

    public RecordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_record, this);
        initView(rootView);
    }

    private void initView(View rootView) {
        mLayoutRecordType = rootView.findViewById(R.id.layout_record_type);
        mTakePicture = rootView.findViewById(R.id.take_picture);
        mTakeVideo = rootView.findViewById(R.id.take_video);
        mStartRecord = rootView.findViewById(R.id.start_record);
        mTakePicture.setOnClickListener(this);
        mTakeVideo.setOnClickListener(this);
        mStartRecord.setOnClickListener(this);
        if (mRecordType == RECORD_TYPE_VIDEO) {
            mTakePicture.setVisibility(GONE);
            mStartRecord.setText(R.string.start_record);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mTakeVideo.getLayoutParams();
            params.leftMargin = 0;
            mTakeVideo.setLayoutParams(params);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_picture:
                selectRecordType(true);
                break;
            case R.id.take_video:
                selectRecordType(false);
                break;
            case R.id.start_record:
                if (null == mOnRecordListener) {
                    return;
                }
                if (mRecordStatus == RECORD_STUTUS_STOP) {
                    mStartRecord.setText("");
                    mStartRecord.setBackgroundResource(R.mipmap.particle_capture_recording);
                    mOnRecordListener.onStartRecord(mRecordType);
                    mRecordStatus = RECORD_STUTUS_RECORDING;
                    return;
                }
                if (mRecordStatus == RECORD_STUTUS_RECORDING) {
                    mStartRecord.setText((mRecordType == RECORD_TYPE_PICTURE) ? R.string.start_photo : R.string.start_record);
                    mStartRecord.setBackgroundResource(R.drawable.record_button_list);
                    mOnRecordListener.onStopRecord(mRecordType);
                    mRecordStatus = RECORD_STUTUS_STOP;
                    return;
                }
                break;
            default:
                break;
        }
    }

    private void selectRecordType(boolean ivPicture) {
        mStartRecord.setBackgroundResource(R.drawable.record_button_list);
        int[] location = new int[2];
        mStartRecord.getLocationInWindow(location);
        float middleX = location[0] + mStartRecord.getWidth() / 2f;
        float targetX;
        if (ivPicture) {
            if (mRecordType == RECORD_TYPE_PICTURE) {
                return;
            }
            targetX = middleX;
            mTakePicture.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            mTakeVideo.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mStartRecord.setText(R.string.start_photo);
            mRecordType = RECORD_TYPE_PICTURE;
        } else {
            if (mRecordType == RECORD_TYPE_VIDEO) {
                return;
            }
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mTakeVideo.getLayoutParams();
            targetX = location[0] - params.leftMargin + mTakeVideo.getWidth() / 2f - ScreenUtils.dip2px(getContext(), 8);
            mTakeVideo.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            mTakePicture.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mStartRecord.setText(R.string.start_record);
            mRecordType = RECORD_TYPE_VIDEO;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(mLayoutRecordType, "translationX", targetX - middleX);
        animator.setDuration(300);
        animator.start();
    }

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        mOnRecordListener = onRecordListener;
    }

    public interface OnRecordListener {
        /**
         * 开始录制
         *
         * @param type type
         */
        void onStartRecord(int type);

        /**
         * 停止录制
         *
         * @param type type
         */
        void onStopRecord(int type);
    }
}
