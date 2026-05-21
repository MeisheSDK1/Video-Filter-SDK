package com.meishe.base.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.meishe.base.bean.MediaData;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 和媒体相关的工具类
 * Media-related tool classes
 */
public class MediaUtils {
    /**
     * 添加到媒体数据库
     * Add to media database
     */
    public static Uri saveVideo2Album(String videoPath, int videoWidth, int videoHeight,
                                      int videoTime) {
        File file = new File(videoPath);
        if (file.exists()) {
            Uri uri = null;
            long dateTaken = System.currentTimeMillis();
            ContentValues values = new ContentValues(11);
            values.put(MediaStore.Video.Media.DATA, videoPath); // 路径 path
            values.put(MediaStore.Video.Media.TITLE, file.getName()); // 标题 title
            values.put(MediaStore.Video.Media.DURATION, videoTime * 1000); // 时长 time
            values.put(MediaStore.Video.Media.WIDTH, videoWidth); // 视频宽 video width
            values.put(MediaStore.Video.Media.HEIGHT, videoHeight); // 视频高 video height
            values.put(MediaStore.Video.Media.SIZE, file.length()); // 视频大小 video length
            values.put(MediaStore.Video.Media.DATE_TAKEN, dateTaken); // 插入时间 taken time
            values.put(MediaStore.Video.Media.DISPLAY_NAME, file.getName());// 文件名 file name
            values.put(MediaStore.Video.Media.DATE_MODIFIED, dateTaken / 1000);// 修改时间; modify time
            values.put(MediaStore.Video.Media.DATE_ADDED, dateTaken / 1000); // 添加时间; add time
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            ContentResolver resolver = Utils.getApp().getContentResolver();
            if (resolver != null) {
                try {
                    uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                } catch (Exception e) {
                    e.printStackTrace();
                    uri = null;
                }
            }
            if (uri == null) {
                MediaScannerConnection.scanFile(Utils.getApp(), new String[]{videoPath}, new String[]{"video/*"}, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
            }
            return uri;
        }
        return null;
    }

    /**
     * 获取媒体数据列表
     * Gets a list of media data
     *
     * @param type TYPE_VIDEO = 0; 视频 TYPE_PHOTO = 1;图片;YPE_ALL = 2;图片和视频
     */
    public static void getMediaList(final int type, final MediaCallback callback) {
        ThreadUtils.getCachedPool().execute(new Runnable() {
            @Override
            public void run() {
                final List<MediaData> dataList = new ArrayList<>();
                if (type == MediaData.TYPE_ALL) {
                    Cursor cursor = getMediaCursor(MediaData.TYPE_PHOTO);
                    if (cursor != null) {
                        createMediaData(cursor, dataList, false);
                        cursor.close();
                    }
                    cursor = getMediaCursor(MediaData.TYPE_VIDEO);
                    if (cursor != null) {
                        createMediaData(cursor, dataList, true);
                        cursor.close();
                    }
                    Collections.sort(dataList, new Comparator<MediaData>() {
                                @Override
                                public int compare(MediaData item1, MediaData item2) {
                                    if (item2.getDate() < item1.getDate()) {
                                        return -1;
                                    }
                                    return (item2.getDate() == item1.getDate() ? 0 : 1);
                                }
                            }
                    );
                } else {
                    Cursor cursor = getMediaCursor(type);
                    if (cursor != null) {
                        createMediaData(cursor, dataList, type == MediaData.TYPE_VIDEO);
                        cursor.close();
                    }
                }
                if (callback != null) {
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResult(dataList);
                        }
                    });
                }
            }
        });
    }

    @SuppressLint("InlinedApi")
    private static Cursor getMediaCursor(int type) {
        String[] projection = null;
        Uri uri = null;
        String order = null;
        if (type == MediaData.TYPE_VIDEO) {
            projection = new String[]{MediaStore.Video.Thumbnails._ID
                    , MediaStore.Video.Media._ID
                    , MediaStore.Video.Thumbnails.DATA
                    , MediaStore.Video.Media.DURATION
                    , MediaStore.Video.Media.SIZE
                    , MediaStore.Video.Media.DATE_ADDED
                    , MediaStore.Video.Media.DISPLAY_NAME
                    , MediaStore.Video.Media.DATE_MODIFIED};
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            order = MediaStore.Video.Media.DATE_ADDED;
        } else if (type == MediaData.TYPE_PHOTO) {
            projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DATE_ADDED,
                    MediaStore.Images.Thumbnails.DATA,
                    MediaStore.MediaColumns.DISPLAY_NAME
            };
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            order = MediaStore.Images.Media.DATE_ADDED;
        }
        if (projection == null) {
            return null;
        }
        return Utils.getApp().getContentResolver().query(uri,
                projection, null, null, order + " DESC ");
    }

    /**
     * 创建媒体实体类并添加到集合中
     * Create a media entity class and add it to the collection
     */
    @SuppressLint("InlinedApi")
    private static void createMediaData(Cursor cursor, List<MediaData> list, boolean isVideo) {
        if (cursor != null) {
            String mediaId;
            String mediaDate;
            String mediaThumbnails;
            String mediaDisplayName;
            if (isVideo) {
                mediaId = MediaStore.Video.Media._ID;
                mediaDate = MediaStore.Video.Media.DATE_ADDED;
                mediaThumbnails = MediaStore.Video.Thumbnails.DATA;
                mediaDisplayName = MediaStore.Video.Media.DISPLAY_NAME;
            } else {
                mediaId = MediaStore.Images.Media._ID;
                mediaDate = MediaStore.Images.Media.DATE_ADDED;
                mediaThumbnails = MediaStore.Images.Thumbnails.DATA;
                mediaDisplayName = MediaStore.Images.Media.DISPLAY_NAME;
            }
            while (cursor.moveToNext()) {
                int videoId = cursor.getInt(cursor.getColumnIndex(mediaId));
                /*
                 * 没有适配Android Q
                 * Android Q is not available
                 * */
                String path = cursor.getString
                        (cursor.getColumnIndex(mediaThumbnails));
                String displayName = cursor.getString(cursor.getColumnIndex(mediaDisplayName));
                int timeIndex = cursor.getColumnIndex(mediaDate);
                long date = cursor.getLong(timeIndex) * 1000;
                if (FileUtils.isFileExists(path)) {
                    // TODO: 2021/1/28 暂时屏蔽 mpg mkv资源
                    int lastDot = path.lastIndexOf(".");
                    String type = path.substring(lastDot + 1);
                    if (!TextUtils.isEmpty(type)) {
                        if (type.equals("mpg") || type.equals("mkv")) {
                            continue;
                        }
                    }
                    MediaData mediaData = new MediaData()
                            .setId(videoId)
                            .setPath(path)
                            .setDate(date)
                            .setDisplayName(displayName);
                    if (isVideo) {
                        mediaData.setType(MediaData.TYPE_VIDEO)
                                .setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
                    } else {
                        mediaData.setType(MediaData.TYPE_PHOTO);
                    }
                    list.add(mediaData);
                }
            }
        }
    }

    public interface MediaCallback {
        void onResult(List<MediaData> dataList);
    }
}
