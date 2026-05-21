package com.cdv.customvideofx;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.meicam.sdk.NvsColor;

import java.nio.ByteBuffer;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2021/6/22 15:49
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class EditUtil {
    public static int alignedData(int data, int num) {
        return data - data % num;
    }

    public static NvsColor parseNvsColor(String color) {
        String[] split = color.split(",");
        if (split.length < 4) {
            return null;
        }
        return new NvsColor(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]));
    }

    /**
     * 格式化时间
     *
     * @param us
     * @return
     */
    public static String formatTimeStrWithUs(long us) {
        long second = us / 1000000;
        long hh = second / 3600;
        long mm = second % 3600 / 60;
        long ss = second % 60;
        String timeStr;
        if (us == 0) {
            timeStr = "00:00";
        }
        if (hh > 0) {
            timeStr = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            timeStr = String.format("%02d:%02d", mm, ss);
        }
        return timeStr;
    }

    public static Uri getVideoContentUri(Context context, String filePath) {
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Video.Media._ID},
                    MediaStore.Video.Media.DATA + "=?",
                    new String[]{filePath},
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                return Uri.withAppendedPath(baseUri, "" + id);
            } else {
                return null;
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }


    /**
     * content路径转绝对路径
     *
     * @param contentResolver
     * @param path
     * @return
     */
    public static String contentPath2AbsPath(ContentResolver contentResolver, String path) {
        Uri uri = Uri.parse(path);
        return getFilePathFromContentUri(uri, contentResolver);
    }

    public static String getFilePathFromContentUri(Uri selectedVideoUri,
                                                   ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;

    }


    public static Uri getImageContentUri(Context context, String filePath) {
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media._ID},
                    MediaStore.Images.Media.DATA + "=?",
                    new String[]{filePath},
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                return Uri.withAppendedPath(baseUri, "" + id);
            } else {
                return null;
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public static Uri getAudioContentUri(Context context, String filePath) {
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Media._ID},
                    MediaStore.Audio.Media.DATA + "=?",
                    new String[]{filePath},
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                return Uri.withAppendedPath(baseUri, "" + id);
            } else {
                return null;
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public static String getFilePathFromContentUri(Context context, String contentUri) {
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(
                    Uri.parse(contentUri),
                    new String[]{BaseColumns._ID},
                    null,
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst())
                return cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));

            return null;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public static Uri insertVideoUri(Context context) {
        ContentValues values = new ContentValues();

        values.put(MediaStore.Video.Media.DISPLAY_NAME, "Meishe Video");
        values.put(MediaStore.Video.Media.TITLE, "Meishe Video");
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
//        values.put(MediaStore.Video.Media.IS_PENDING, 1);
        values.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Video.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000);
//        values.put(MediaStore.Video.Media.RELATIVE_PATH, "DCIM/Soloop");
        Uri outVideoUri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        return outVideoUri;
    }


    public static ByteBuffer flipI420Vertical(ByteBuffer src, int width, int height) {
        int ySize = width * height;
        int uSize = ySize / 4;
        int vSize = ySize / 4;

        byte[] in = new byte[src.remaining()];
        src.get(in);

        byte[] out = new byte[in.length];

        int yStart = 0;
        int uStart = yStart + ySize;
        int vStart = uStart + uSize;

        int halfH = height;
        int halfHChroma = height / 2;
        int halfWChroma = width / 2;

        // -------- Flip Y Planar --------
        for (int y = 0; y < height; y++) {
            int srcPos = y * width;
            int dstPos = (height - 1 - y) * width;
            System.arraycopy(in, srcPos, out, dstPos, width);
        }

        // -------- Flip U Planar --------
        for (int y = 0; y < halfHChroma; y++) {
            int srcPos = uStart + y * halfWChroma;
            int dstPos = uStart + (halfHChroma - 1 - y) * halfWChroma;
            System.arraycopy(in, srcPos, out, dstPos, halfWChroma);
        }

        // -------- Flip V Planar --------
        for (int y = 0; y < halfHChroma; y++) {
            int srcPos = vStart + y * halfWChroma;
            int dstPos = vStart + (halfHChroma - 1 - y) * halfWChroma;
            System.arraycopy(in, srcPos, out, dstPos, halfWChroma);
        }

        return ByteBuffer.wrap(out);
    }

}
