package com.meicam.effectsdkdemo.media;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

import static com.meicam.effectsdkdemo.BuildConfig.DEBUG;

public abstract class MediaEncoder implements Runnable{

    private static final String TAG = "MediaEncoder";

    protected static final int TIMEOUT_USEC = 10000;    // 10[msec]

    protected final Object mSync = new Object();
    /**
     * Flag that indicate this encoder is capturing now.
     * 表示该编码器现在正在捕获的标记。
     */
    protected volatile boolean mIsCapturing;
    /**
     * Flag that indicate the frame data will be available soon.
     * 表示帧数据将很快可用的标记。
     */
    private int mRequestDrain;
    /**
     * Flag to request stop capturing
     * 请求停止捕获的标志
     */
    protected volatile boolean mRequestStop;
    /**
     * Flag the indicate the muxer is running
     * 指示muxer正在运行的标记
     */
    protected boolean mMuxerStarted;
    /**
     * Track Number
     * 轨道序号
     */
    protected int mTrackIndex;
    /**
     * Flag that indicate encoder received EOS(End Of Stream)
     * 表示编码器接收到EOS(流结束)的标记
     *
     */
    protected boolean mIsEOS;
    /**
     * Weak refarence of MediaMuxerWarapper instance
     * MediaMuxerWarapper实例的弱引用
     */
    protected final WeakReference<MediaMuxerWrapper> mWeakMuxer;

    /**
     * BufferInfo instance for dequeuing
     * BufferInfo实例，用于退出队列
     */
    private MediaCodec.BufferInfo mBufferInfo;        // API >= 16(Android4.1.2)

    /**
     * previous presentationTimeUs for writing
     * 以前的presentationTimeUs为写作
     */
    protected long prevOutputPTSUs = 0;

    protected final MediaEncoderListener mListener;

    /**
     * MediaCodec instance for encoding
     * 用于编码的MediaCodec实例
     */
    protected MediaCodec mMediaCodec;

    public MediaEncoder(final MediaMuxerWrapper muxer, final MediaEncoderListener listener) {
        if (listener == null) throw new NullPointerException("MediaEncoderListener is null");
        if (muxer == null) throw new NullPointerException("MediaMuxerWrapper is null");
        mWeakMuxer = new WeakReference<MediaMuxerWrapper>(muxer);
        muxer.addEncoder(this);
        mListener = listener;
        synchronized (mSync) {
            // create BufferInfo here for effectiveness(to reduce GC)
            //在这里创建BufferInfo以提高效率(以减少GC)
            mBufferInfo = new MediaCodec.BufferInfo();
            // wait for starting thread
            // 等待启动线程
            new Thread(this, getClass().getSimpleName()).start();
            try {
                mSync.wait();
            } catch (final InterruptedException e) {
            }
        }
    }

    abstract void prepare() throws IOException;

    /**
     * the method to indicate frame data is soon available or already available
     *  指示帧数据即将可用或已经可用的方法
     *
     *          如果编码器准备装入，则返回true。
     * @return return true if encoder is ready to encod.
     */
    public boolean frameAvailableSoon() {
        if (DEBUG) Log.v(TAG, "frameAvailableSoon");
        synchronized (mSync) {
            if (!mIsCapturing || mRequestStop) {
                return false;
            }
            mRequestDrain++;
            mSync.notifyAll();
        }
        return true;
    }

    @Override
    public void run() {
        synchronized (mSync) {
            mRequestStop = false;
            mRequestDrain = 0;
            mSync.notify();
        }
        final boolean isRunning = true;
        boolean localRequestStop;
        boolean localRequestDrain;
        while (isRunning) {
            synchronized (mSync) {
                localRequestStop = mRequestStop;
                localRequestDrain = (mRequestDrain > 0);
                if (localRequestDrain)
                    mRequestDrain--;
            }
            if (localRequestStop) {
                drain();
                // request stop recording
                // 请求停止录音
                signalEndOfInputStream();
                // process output data again for EOS signale
                // 再次为EOS信号处理输出数据
                drain();
                // release all related objects
                // 释放所有相关对象
                release();
                break;
            }
            if (localRequestDrain) {
                drain();
            } else {
                synchronized (mSync) {
                    try {
                        mSync.wait();
                    } catch (final InterruptedException e) {
                        break;
                    }
                }
            }
        }
        if (DEBUG) Log.d(TAG, "Encoder thread exiting");
        synchronized (mSync) {
            mRequestStop = true;
            mIsCapturing = false;
        }
    }

    protected void signalEndOfInputStream() {
        if (DEBUG) Log.d(TAG, "sending EOS to encoder");
        // signalEndOfInputStream is only avairable for video encoding with surface
        // signalEndOfInputStream仅用于surface的视频编码
        // and equivalent sending a empty buffer with BUFFER_FLAG_END_OF_STREAM flag.
        //等效地发送一个带有BUFFER_FLAG_END_OF_STREAM标志的空缓冲区。
        encode(null, 0, prevOutputPTSUs);
    }

    /**
     * Method to set byte array to the MediaCodec encoder
     *方法将字节数组设置为MediaCodec编码器
     * @param buffer
     * @param length             　length of byte array, zero means EOS. 字节数组的长度，0表示EOS
     * @param presentationTimeUs
     */
    protected void encode(final ByteBuffer buffer, final int length, final long presentationTimeUs) {
        if (!mIsCapturing) return;
        final ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
        while (mIsCapturing) {
            final int inputBufferIndex = mMediaCodec.dequeueInputBuffer(TIMEOUT_USEC);
            Log.d(TAG, "inputBufferIndex: " + inputBufferIndex);
            if (inputBufferIndex >= 0) {
                final ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                if (buffer != null) {
                    inputBuffer.put(buffer);
                }
                if (DEBUG) Log.v(TAG, "encode:queueInputBuffer");
                if (length <= 0) {
                    mIsEOS = true;
                    if (DEBUG) Log.i(TAG, "send BUFFER_FLAG_END_OF_STREAM");
                    mMediaCodec.queueInputBuffer(inputBufferIndex, 0, 0,
                            presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    break;
                } else {
                    mMediaCodec.queueInputBuffer(inputBufferIndex, 0, length,
                            presentationTimeUs, 0);
                }
                break;
            } else if (inputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // wait for MediaCodec encoder is ready to encode
                // 等待MediaCodec编码器已准备好编码
                // nothing to do here because MediaCodec#dequeueInputBuffer(TIMEOUT_USEC)
                // 因为MediaCodec#dequeueInputBuffer(TIMEOUT_USEC)
                // will wait for maximum TIMEOUT_USEC(10msec) on each call
                // 每次调用将等待最大TIMEOUT_USEC(10msec)
            }
        }
    }

    /**
     * Release all releated objects
     * 释放所有相关的对象
     */
    protected void release() {
        if (DEBUG) Log.d(TAG, "release:");
        try {
            mListener.onStopped(this);
        } catch (final Exception e) {
            Log.e(TAG, "failed onStopped", e);
        }
        mIsCapturing = false;
        if (mMediaCodec != null) {
            try {
                mMediaCodec.stop();
                mMediaCodec.release();
                mMediaCodec = null;
            } catch (final Exception e) {
                Log.e(TAG, "failed releasing MediaCodec", e);
            }
        }
        if (mMuxerStarted) {
            final MediaMuxerWrapper muxer = mWeakMuxer != null ? mWeakMuxer.get() : null;
            if (muxer != null) {
                try {
                    muxer.stop();
                } catch (final Exception e) {
                    Log.e(TAG, "failed stopping muxer", e);
                }
            }
        }
        mBufferInfo = null;
    }

    /**
     * drain encoded data and write them to muxer
     * 抽取编码数据并将其写入muxer
     */
    protected void drain() {
        if (mMediaCodec == null) return;
        ByteBuffer[] encoderOutputBuffers = mMediaCodec.getOutputBuffers();
        Log.d(TAG, "encoderOutputBuffers: " + encoderOutputBuffers.length);
        int encoderStatus, count = 0;
        final MediaMuxerWrapper muxer = mWeakMuxer.get();
        if (muxer == null) {
        //throw new NullPointerException("muxer is unexpectedly null"); 抛出空指针异常
            Log.w(TAG, "muxer is unexpectedly null");
            return;
        }
        Log.d(TAG, "mIsCapturing: " + mIsCapturing);
        LOOP:
        while (mIsCapturing) {
            // get encoded data with maximum timeout duration of TIMEOUT_USEC(=10[msec])
            // 获取最大超时时间为TIMEOUT_USEC(=10[msec])的编码数据
            encoderStatus = mMediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            //Log.e(TAG, "encoderStatus: "+encoderStatus );
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // wait 5 counts(=TIMEOUT_USEC x 5 = 50msec) until data/EOS come
                // 等待5个计数(=TIMEOUT_USEC x 5 = 50msec)，直到数据/EOS到来
                if (!mIsEOS) {
                    if (++count > 5)
                        break LOOP;        // out of while
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                if (DEBUG) Log.v(TAG, "INFO_OUTPUT_BUFFERS_CHANGED");
                // this shoud not come when encoding
                // 编码时不应该出现这种情况
                encoderOutputBuffers = mMediaCodec.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (DEBUG) Log.v(TAG, "INFO_OUTPUT_FORMAT_CHANGED");
                // this status indicate the output format of codec is changed
                // 此状态表示编解码器的输出格式发生了变化
                // this should come only once before actual encoded data
                // 这应该只在实际编码数据之前出现一次
                // but this status never come on Android4.3 or less
                // 但在Android4.3或更低版本上不会出现这种状态
                // and in that case, you should treat when MediaCodec.BUFFER_FLAG_CODEC_CONFIG come.
                // 在这种情况下，你应该治疗MediaCodec。BUFFER_FLAG_CODEC_CONFIG来。
                if (mMuxerStarted) {    // second time request is error
                    throw new RuntimeException("format changed twice");
                }
                // get output format from codec and pass them to muxer
                // 从编解码器获得输出格式，并将它们传递给muxer
                // getOutputFormat should be called after INFO_OUTPUT_FORMAT_CHANGED otherwise crash.
                // getOutputFormat应该在INFO_OUTPUT_FORMAT_CHANGED之后调用，否则会崩溃
                final MediaFormat format = mMediaCodec.getOutputFormat(); // API >= 16
                mTrackIndex = muxer.addTrack(format);
                mMuxerStarted = true;
                if (!muxer.start()) {
                    // we should wait until muxer is ready
                    // 我们应该等到muxer准备好
                    synchronized (muxer) {
                        while (!muxer.isStarted())
                            try {
                                muxer.wait(100);
                            } catch (final InterruptedException e) {
                                break LOOP;
                            }
                    }
                }
            } else if (encoderStatus < 0) {
                if (DEBUG)
                    Log.w(TAG, "drain:unexpected result from encoder#dequeueOutputBuffer: " + encoderStatus);
            } else {
                final ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    // this never should come...may be a MediaCodec internal error
                    // 这永远不应该发生……可能是MediaCodec内部错误
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                }
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // You should set output format to muxer here when you target Android4.3 or less
                    // 当你的目标是Android4.3或更低版本时，你应该将输出格式设置为muxer
                    // but MediaCodec#getOutputFormat can not call here(because INFO_OUTPUT_FORMAT_CHANGED don't come yet)
                    // 但是MediaCodec#getOutputFormat不能在这里调用(因为INFO_OUTPUT_FORMAT_CHANGED还没有到来)
                    // therefor we should expand and prepare output format from buffer data.
                    // 为此，我们需要从缓冲区数据中展开并准备输出格式。
                    // This sample is for API>=18(>=Android 4.3), just ignore this flag here
                    // 这个例子是API>=18(>=Android 4.3)，忽略这个标志在这里
                    if (DEBUG) Log.d(TAG, "drain:BUFFER_FLAG_CODEC_CONFIG");
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size != 0) {
                    // encoded data is ready, clear waiting counter
                    // 编码数据已准备好，清除等待计数器
                    count = 0;
                    if (!mMuxerStarted) {
                        // muxer is not ready...this will prrograming failure.
                        // muxer 还没准备好……这将导致编程失败。
                        throw new RuntimeException("drain:muxer hasn't started");
                    }
                    // write encoded data to muxer(need to adjust presentationTimeUs.
                    // 写入编码数据到muxer(需要调整presentationTimeUs。
                    muxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                    prevOutputPTSUs = mBufferInfo.presentationTimeUs;
                }
                // return buffer to encoder
                // 将缓冲区返回编码器
                mMediaCodec.releaseOutputBuffer(encoderStatus, false);
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    // when EOS come.
                    // EOS到来的时候。
                    mIsCapturing = false;
                    break;      // out of while 跳出判断
                }
            }
        }
    }

    /*package*/ void startRecording() {
        if (DEBUG) Log.v(TAG, "startRecording");
        synchronized (mSync) {
            mIsCapturing = true;
            mRequestStop = false;
            mSync.notifyAll();
        }
    }

    /**
     * the method to request stop encoding
     */
    /*package*/ void stopRecording() {
        if (DEBUG) Log.v(TAG, "stopRecording");
        synchronized (mSync) {
            if (!mIsCapturing || mRequestStop) {
                return;
            }
            // for rejecting newer frame
            // 用于拒绝较新的帧
            mRequestStop = true;
            mSync.notifyAll();
            // We can not know when the encoding and writing finish.
            // 我们不知道编码和写入何时结束。
            // so we return immediately after request to avoid delay of caller thread
            // 因此，我们在请求后立即返回，以避免调用线程的延迟
        }
    }

    public interface MediaEncoderListener {
        public void onPrepared(MediaEncoder encoder);

        public void onStopped(MediaEncoder encoder);
    }
}
