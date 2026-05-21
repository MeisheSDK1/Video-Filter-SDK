package com.meicam.effectsdkdemo.media;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.opengl.EGLContext;
import android.opengl.Matrix;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;

import static com.meicam.effectsdkdemo.BuildConfig.DEBUG;

/**
 * @author Administrator
 */
public class MediaVideoEncoder extends MediaEncoder {

    private static final String TAG = "MediaVideoEncoder";

    private static final int FRAME_RATE = 25;
    private static final float BPP = 0.25f;

    private final int mWidth;
    private final int mHeight;
    private final int mRotation;

    private RenderHandler mRenderHandler;

    private Surface mSurface;

    private static final String MIME_TYPE = "video/avc";

    private float[] modelMatrix = new float[16];  //获得一个model矩阵


    public MediaVideoEncoder(final MediaMuxerWrapper muxer, final MediaEncoderListener listener, final int width, final int height, int rotation) {
        super(muxer, listener);
        if (DEBUG) Log.i(TAG, "MediaVideoEncoder: ");
        mRotation = rotation;
        if (rotation == 90 || rotation == 270) {
            mWidth = height;
            mHeight = width;
        } else {
            mWidth = width;
            mHeight = height;
        }
        mRenderHandler = RenderHandler.createHandler(TAG);
    }

    public void setEglContext(final EGLContext shared_context) {
        mRenderHandler.setEglContext(shared_context, mSurface, true);
    }


    @Override
    void prepare() throws IOException {
        final MediaCodecInfo videoCodecInfo = selectVideoCodec(MIME_TYPE);
        if (videoCodecInfo == null) {
            Log.e(TAG, "Unable to find an appropriate codec for " + MIME_TYPE);
            return;
        }
        if (DEBUG) Log.i(TAG, "selected codec: " + videoCodecInfo.getName( ));

        final MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);    // API >= 18
        format.setInteger(MediaFormat.KEY_BIT_RATE, calcBitRate( ));
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 10);

        mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
        mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        // get Surface for encoder input
        // 获取编码器输入的Surface
        // this method only can call between #configure and #start
        // 该方法只能在#configure和#start之间调用
        mSurface = mMediaCodec.createInputSurface( );    // API >= 18
        mMediaCodec.start( );
        if (DEBUG) Log.i(TAG, "prepare finishing");
        if (mListener != null) {
            try {
                mListener.onPrepared(this);
            } catch (final Exception e) {
                Log.e(TAG, "prepare:", e);
            }
        }

        Matrix.setRotateM(modelMatrix, 0, mRotation, 0,0,1);
    }

    public boolean frameAvailableSoon(int texId, long streamTime) {
        boolean result;
        if (result = super.frameAvailableSoon( )) {
            mRenderHandler.draw(texId,null, modelMatrix, streamTime);
        }
        return result;
    }

    private int calcBitRate() {
        final int bitrate = (int) (BPP * FRAME_RATE * mWidth * mHeight);
        Log.i(TAG, String.format("bitrate=%5.2f[Mbps]", bitrate / 1024f / 1024f));
        return bitrate;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void signalEndOfInputStream() {
        if (DEBUG) {
            Log.d(TAG, "sending EOS to encoder");
        }
        mMediaCodec.signalEndOfInputStream( );    // API >= 18
        mIsEOS = true;
    }

    /**
     * select the first codec that match a specific MIME type
     *选择匹配特定MIME类型的第一个编解码器
     * @param mimeType
     * @return null if no codec matched 没匹配到返回空
     */
    protected static final MediaCodecInfo selectVideoCodec(final String mimeType) {
        if (DEBUG) {
            Log.v(TAG, "selectVideoCodec:");
        }

        // get the list of available codecs
        // 获取可用的编解码器列表
        final int numCodecs = MediaCodecList.getCodecCount( );
        for (int i = 0; i < numCodecs; i++) {
            final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder( )) {    // skipp decoder
                continue;
            }
            // select first codec that match a specific MIME type and color format
            // 选择匹配特定MIME类型和颜色格式的第一个编解码器
            final String[] types = codecInfo.getSupportedTypes( );
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    if (DEBUG) Log.i(TAG, "codec:" + codecInfo.getName( ) + ",MIME=" + types[j]);
                    final int format = selectColorFormat(codecInfo, mimeType);
                    if (format > 0) {
                        return codecInfo;
                    }
                }
            }
        }
        return null;
    }

    /**
     * select color format available on specific codec and we can use.
     *选择颜色格式可用的特定编解码器和我们可以使用。
     * @return 0 if no colorFormat is matched 如果没有colorFormat匹配返回0
     */
    protected static final int selectColorFormat(final MediaCodecInfo codecInfo, final String mimeType) {
        if (DEBUG) Log.i(TAG, "selectColorFormat: ");
        int result = 0;
        final MediaCodecInfo.CodecCapabilities caps;
        try {
            Thread.currentThread( ).setPriority(Thread.MAX_PRIORITY);
            caps = codecInfo.getCapabilitiesForType(mimeType);
        } finally {
            Thread.currentThread( ).setPriority(Thread.NORM_PRIORITY);
        }
        int colorFormat;
        for (int i = 0; i < caps.colorFormats.length; i++) {
            colorFormat = caps.colorFormats[i];
            if (isRecognizedViewoFormat(colorFormat)) {
                if (result == 0)
                    result = colorFormat;
                break;
            }
        }
        if (result == 0)
            Log.e(TAG, "couldn't find a good color format for " + codecInfo.getName( ) + " / " + mimeType);
        return result;
    }

    /**
     * 我们可以在这个类别使用的颜色格式
     * color formats that we can use in this class
     */
    protected static int[] recognizedFormats;

    static {
        recognizedFormats = new int[]{
//        	MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar,
//        	MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar,
//        	MediaCodecInfo.CodecCapabilities.COLOR_QCOM_FormatYUV420SemiPlanar,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface,
        };
    }

    private static final boolean isRecognizedViewoFormat(final int colorFormat) {
        if (DEBUG) Log.i(TAG, "isRecognizedViewoFormat:colorFormat=" + colorFormat);
        final int n = recognizedFormats != null ? recognizedFormats.length : 0;
        for (int i = 0; i < n; i++) {
            if (recognizedFormats[i] == colorFormat) {
                return true;
            }
        }
        return false;
    }
}
