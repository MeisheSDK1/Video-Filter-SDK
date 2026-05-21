package com.meishe.base.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The type Media scanner util.
 * 媒体扫描仪工具类
 *
 */
public class MediaScannerUtil {
    private static final String TAG = "MediaScannerUtil";
    private static final MediaScannerClient client = new MediaScannerClient();
    private static MediaScannerConnection mediaScanConn = new MediaScannerConnection(Utils.getApp(), client);
    private static List<MediaScannerCallBack> callBackList = new ArrayList<>();

    private static final Queue<Entity> sPendingScanList = new ConcurrentLinkedQueue<>();

    private static class MediaScannerClient implements MediaScannerConnection.MediaScannerConnectionClient {
        @Override
        public void onMediaScannerConnected() {
            scanOrDisconnect();
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            scanOrDisconnect();
        }
    }

    private static void scanOrDisconnect() {
        Entity entity = sPendingScanList.poll();
        if (entity != null) {
            Log.e(TAG, String.format("scanFile, path =-> %s", entity.path));
            mediaScanConn.scanFile(entity.path, entity.type);
        } else {
            mediaScanConn.disconnect();
            Log.e(TAG, String.format("onScanCompleted and disconnect"));
        }
    }

    /**
     * 扫描文件标签信息
     * <p>
     * Scan file tag information
     *
     * @param filePath The file path
     * @param fileType File type
     */
    public static void scanFile(String filePath, String fileType) {
        if(filePath == null || filePath.isEmpty()) {
            return;
        }
        scan(new Entity(filePath, fileType));
    }

    /**
     * Scan.
     *扫描
     * @param entity the entity
     */
    public static void scan(Entity entity) {
        sPendingScanList.add(entity);
        mediaScanConn.connect();
    }

    /**
     * Scan.
     * 扫描
     * @param path the path
     */
    public static void scan(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        scan(new File(path));
    }

    /**
     * Scan.
     * 扫描
     * @param file the file
     */
    public static void scan(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (null != files && files.length > 0) {
                    for (File f : files) {
                        scan(f);
                    }
                }
            } else {
                scan(new Entity(file.getAbsolutePath()));
            }
        }
    }

    /**
     * Un scan file.
     * 断开扫描文件
     */
    public static void unScanFile() {
        MediaScannerUtil.mediaScanConn.disconnect();
    }

    /**
     * Add call back.
     * 添加回应
     * @param callBack the call back
     */
    public static void addCallBack(MediaScannerCallBack callBack) {
        if (callBack == null) {
            return;
        }

        MediaScannerUtil.callBackList.add(callBack);
    }

    /**
     * Remove call back.
     * 删除回应
     * @param callBack the call back
     */
    public static void removeCallBack(MediaScannerCallBack callBack) {
        if (callBack == null) {
            return;
        }

        MediaScannerUtil.callBackList.remove(callBack);
    }


    /**
     * Scan file async.
     * 扫描文件异步
     * @param ctx      the ctx
     * @param filePath the file path
     */
    public static void scanFileAsync(Context ctx, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        ctx.sendBroadcast(scanIntent);
    }

    /**
     * The type Entity.
     * 实体类型
     */
    public static class Entity {
        /**
         * The Path.
         * 路径
         */
        String path;
        /**
         * The Type.
         * 类型
         */
        String type;

        /**
         * Instantiates a new Entity.
         *
         * @param path the path
         * @param type the type
         */
        public Entity(String path, String type) {
            this.path = path;
            this.type = type;
        }

        /**
         * Instantiates a new Entity.
         *
         * @param path the path
         */
        public Entity(String path) {
            this.path = path;
        }
    }

    /**
     * The type Media scanner call back.
     */
    public static abstract class MediaScannerCallBack {

        /**
         * On scan completed.
         */
        public abstract void onScanCompleted();

    }
}
