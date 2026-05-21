package com.meicam.effectsdkdemo.media;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.meicam.effectsdkdemo.utils.FileUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.meicam.effectsdkdemo.BuildConfig.DEBUG;


public class MediaMuxerWrapper {

    private static final String TAG = "MediaMuxerWrapper";

    private final MediaMuxer mMediaMuxer;
    private MediaEncoder mVideoEncoder, mAudioEncoder;

    private int mEncoderCount, mStatredCount;
    private String mOutputPath;
    private boolean mIsStarted;
    private long index0StartTime;
    private long index1StartTime;
    private boolean hasGetIndex0StartTime = false;
    private boolean hasGetIndex1StartTime = false;

    /**
     * Constructor
     * 构造函数
     * @param ext extension of output file 文件拓展名
     * @throws IOException
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public MediaMuxerWrapper(String ext) throws IOException {
        if (TextUtils.isEmpty(ext)) ext = ".mp4";
        try {
            mOutputPath = FileUtils.getCaptureFilePath(ext);
        } catch (final NullPointerException e) {
            throw new RuntimeException("This app has no permission of writing external storage");
        }
        mMediaMuxer = new MediaMuxer(mOutputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
//        mEncoderCount = mStatredCount = 0;
//        mIsStarted = false;
    }

    public String getOutputPath() {
        return mOutputPath;
    }

    /**
     * assign encoder to this calss. this is called from encoder.
     *将编码器分配给这个类。这是由encoder调用的。
     * @param encoder instance of MediaVideoEncoder or MediaAudioEncoder MediaVideoEncoder的实例对象
     */
    public void addEncoder(final MediaEncoder encoder) {
        if (encoder instanceof MediaVideoEncoder) {
            if (mVideoEncoder != null)
                throw new IllegalArgumentException("Video encoder already added.");
            mVideoEncoder = encoder;
        } else if (encoder instanceof MediaAudioEncoder) {
            if (mAudioEncoder != null)
                throw new IllegalArgumentException("Video encoder already added.");
            mAudioEncoder = encoder;
        } else
            throw new IllegalArgumentException("unsupported encoder");
        mEncoderCount = (mVideoEncoder != null ? 1 : 0) + (mAudioEncoder != null ? 1 : 0);
    }

    public void prepare() throws IOException {
        if (mVideoEncoder != null)
            mVideoEncoder.prepare();
        if (mAudioEncoder != null)
            mAudioEncoder.prepare();
    }

    public void startRecording() {
        hasGetIndex0StartTime = false;
        hasGetIndex1StartTime = false;
        if (mVideoEncoder != null)
            mVideoEncoder.startRecording();
        if (mAudioEncoder != null)
            mAudioEncoder.startRecording();
    }

    public void stopRecording() {
        if (mVideoEncoder != null)
            mVideoEncoder.stopRecording();
        mVideoEncoder = null;
        if (mAudioEncoder != null)
            mAudioEncoder.stopRecording();
        mAudioEncoder = null;
    }

    /**
     * request start recording from encoder
     *请求从编码器开始录音
     * @return true when muxer is ready to write
     *          当muxer准备写入时为True
     */
    /*package*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    synchronized boolean start() {
        if (DEBUG) Log.v(TAG, "start:");
        mStatredCount++;
        if ((mEncoderCount > 0) && (mStatredCount == mEncoderCount)) {
            mMediaMuxer.start();
            mIsStarted = true;
            notifyAll();
            if (DEBUG) Log.v(TAG, "MediaMuxer started:");
        }
        return mIsStarted;
    }

    /**
     * request stop recording from encoder when encoder received EOS
     * 当编码器收到EOS时请求停止从编码器录制
     */
    /*package*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    synchronized void stop() {
        if (DEBUG) Log.v(TAG, "stop:mStatredCount=" + mStatredCount);
        mStatredCount--;
        if ((mEncoderCount > 0) && (mStatredCount <= 0)) {
            mMediaMuxer.stop();
            mMediaMuxer.release();
            mIsStarted = false;
            if (DEBUG) Log.v(TAG, "MediaMuxer stopped:");
        }
    }

    public synchronized boolean isStarted() {
        return mIsStarted;
    }

    /**
     * assign encoder to muxer
     *将编码器分配给muxer
     * @param format
     * @return minus value indicate error 负值表示错误
     */
    /*package*/
    synchronized int addTrack(final MediaFormat format) {
        if (mIsStarted)
            throw new IllegalStateException("muxer already started");
        final int trackIx = mMediaMuxer.addTrack(format);
        if (DEBUG)
            Log.i(TAG, "addTrack:trackNum=" + mEncoderCount + ",trackIx=" + trackIx + ",format=" + format);
        return trackIx;
    }

    /**
     * write encoded data to muxer
     * 将编码数据写入muxer
     */
    /*package*/
    synchronized void writeSampleData(final int trackIndex, final ByteBuffer byteBuf, final MediaCodec.BufferInfo bufferInfo) {
        if (trackIndex == 0) {
            if (!hasGetIndex0StartTime) {
                index0StartTime = bufferInfo.presentationTimeUs;
                hasGetIndex0StartTime = true;
            }
            bufferInfo.presentationTimeUs -= index0StartTime;
        }

        if (trackIndex == 1) {
            if (!hasGetIndex1StartTime) {
                index1StartTime = bufferInfo.presentationTimeUs;
                hasGetIndex1StartTime = true;
            }
            bufferInfo.presentationTimeUs -= index1StartTime;
        }

        if (mStatredCount > 0)
            mMediaMuxer.writeSampleData(trackIndex, byteBuf, bufferInfo);
    }

}
