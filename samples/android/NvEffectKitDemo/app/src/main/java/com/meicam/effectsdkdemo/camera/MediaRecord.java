package com.meicam.effectsdkdemo.camera;

import com.meicam.effectsdkdemo.media.MediaAudioEncoder;
import com.meicam.effectsdkdemo.media.MediaEncoder;
import com.meicam.effectsdkdemo.media.MediaMuxerWrapper;
import com.meicam.effectsdkdemo.media.MediaVideoEncoder;

import java.io.IOException;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/5/25 16:23
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class MediaRecord {
    private static final String TAG = MediaRecord.class.getSimpleName();
    private MediaMuxerWrapper mMuxer;
    private OnMediaRecordListener mOnMediaRecordListener;

    public void startRecord(int width, int height, OnMediaRecordListener mOnMediaRecordListener) {
        this.mOnMediaRecordListener = mOnMediaRecordListener;
        try {
            mMuxer = new MediaMuxerWrapper(".mp4");
            new MediaVideoEncoder(mMuxer, mMediaEncoderListener, width, height, 0);
            new MediaAudioEncoder(mMuxer, mMediaEncoderListener);
            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public String stopRecord() {
        String outPath = "";
        if (mMuxer != null) {
            mMuxer.stopRecording();
            outPath = mMuxer.getOutputPath();
        }
        mMuxer = null;
        return outPath;
    }

    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (null == mOnMediaRecordListener) {
                return;
            }
            if (encoder instanceof MediaVideoEncoder) {
                mOnMediaRecordListener.onPrepared((MediaVideoEncoder) encoder);
            }
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (null == mOnMediaRecordListener) {
                return;
            }
            if (encoder instanceof MediaVideoEncoder) {
                mOnMediaRecordListener.onStopped(null);
            }
        }
    };

    public interface OnMediaRecordListener {
        void onPrepared(MediaVideoEncoder encoder);

        void onStopped(MediaVideoEncoder encoder);
    }
}
