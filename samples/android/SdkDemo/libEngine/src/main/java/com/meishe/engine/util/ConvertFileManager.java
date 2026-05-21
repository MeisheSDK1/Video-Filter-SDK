package com.meishe.engine.util;

import android.text.TextUtils;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsMediaFileConvertor;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.base.utils.FileUtils;
import com.meishe.base.utils.LogUtils;

import java.io.File;
import java.util.Hashtable;

/**
 * The type Convert file manager.
 * 此类为 转换文件管理器
 */
public class ConvertFileManager implements NvsMediaFileConvertor.MeidaFileConvertorCallback {

    private NvsMediaFileConvertor mMediaFileConvertor;
    private static ConvertFileManager mInstance;
    private long mCurrentTaskId;
    private String mDestFilePath;
    private OnConvertListener mOnConvertListener;

    private ConvertFileManager() {
    }

    /**
     * Gets instance.
     * 获得实例
     *
     * @return the instance
     */
    public static ConvertFileManager getInstance() {
        if (mInstance == null) {
            mInstance = new ConvertFileManager();
        }
        return mInstance;
    }

    @Override
    public void onProgress(long taskId, float progress) {
        if (mCurrentTaskId != taskId) {
            return;
        }
        if (mOnConvertListener != null) {
            mOnConvertListener.onConvertProgress(progress * 100);
        }
    }

    @Override
    public void onFinish(long taskId, String srcFile, String dstFile, int errorCode) {
        if (mCurrentTaskId != taskId) {
            return;
        }

        videoClipConvertComplete(dstFile,
                errorCode == NvsMediaFileConvertor.CONVERTOR_ERROR_CODE_NO_ERROR);
    }

    /**
     * @param dstFile   技术文件
     * @param isSuccess 字段名
     *                  视频剪辑转换完成 video Clip Convert Complete
     */
    private void videoClipConvertComplete(String dstFile, boolean isSuccess) {
        finishConvert();
        if (mOnConvertListener != null) {
            mOnConvertListener.onConvertFinish(dstFile, isSuccess);
        }
    }

    @Override
    public void notifyAudioMuteRage(long l, long l1, long l2) {

    }

    /**
     * Convert file.
     * 转换文件
     * <p>
     * meicam的视频剪辑
     */
    public void convertFile(String path) {
        if (!FileUtils.isFileExists(path)) {
            LogUtils.e("path===" + path);
            return;
        }
        if (mMediaFileConvertor == null) {
            mMediaFileConvertor = new NvsMediaFileConvertor();
        }
        String fileName = PathUtils.getFileName(path);
        mDestFilePath = PathUtils.getVideoConvertDir() + File.separator + "MY_" + System.currentTimeMillis() + "_" + fileName;
        mMediaFileConvertor.setMeidaFileConvertorCallback(this, null);


        NvsAVFileInfo avFileInfo = NvsStreamingContext.getInstance().getAVFileInfo(path);
        long duration = 0;
        if (avFileInfo != null) {
            duration = avFileInfo.getDuration();
        }
        if (WhiteList.isCovert4KFileWhiteList(path)) {
            Hashtable<String, Object> hashtable = new Hashtable<>();
            hashtable.put(NvsMediaFileConvertor.CONVERTOR_CUSTOM_VIDEO_HEIGHT, 1080);
            mCurrentTaskId = mMediaFileConvertor.convertMeidaFile(path, mDestFilePath, true, 0, duration, hashtable);
        } else {
            mCurrentTaskId = mMediaFileConvertor.convertMeidaFile(path, mDestFilePath, true, 0, duration, null);
        }
    }


    /**
     * Cancel convert.
     * 取消转换
     */
    public void cancelConvert() {
        if (mMediaFileConvertor == null) {
            return;
        }
        mMediaFileConvertor.cancelTask(mCurrentTaskId);
        mMediaFileConvertor = null;
        mCurrentTaskId = -1;
        if (!TextUtils.isEmpty(mDestFilePath)) {
            File file = new File(mDestFilePath);
            if (file.exists()) {
                file.delete();
            }
        }
        if (mOnConvertListener != null) {
            mOnConvertListener.onConvertCancel();
        }
    }

    private void finishConvert() {
        if (mMediaFileConvertor != null) {
            mMediaFileConvertor.release();
        }
        mMediaFileConvertor = null;
    }

    /**
     * Sets on convert listener.
     * 设置转换监听器
     *
     * @param onConvertListener the on convert listener
     */
    public void setOnConvertListener(OnConvertListener onConvertListener) {
        this.mOnConvertListener = onConvertListener;
    }

    /**
     * The interface On convert listener.
     * 转换监听器上的接口
     */
    public interface OnConvertListener {

        /**
         * On convert progress.
         * 转换进展
         *
         * @param progress the progress
         */
        void onConvertProgress(float progress);

        /**
         * On convert finish.
         * 转换结束
         *
         * @param convertSucess the convert sucess
         */

        void onConvertFinish(String path, boolean convertSucess);

        /**
         * On convert cancel.
         * 转换取消
         */
        void onConvertCancel();

    }
}
