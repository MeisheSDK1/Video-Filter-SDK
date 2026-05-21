package com.meishe.sdkdemo.edit.music;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.meishe.sdkdemo.utils.dataInfo.MusicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/4/11 下午4:50
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MusicUtils {
    /**
     * 扫描系统里面的音频文件，返回一个list集合
     * Scan the audio files in the system and return a list
     */
    public static List<MusicInfo> getMusicData(final int type, Context context) {
        List<MusicInfo> list = new ArrayList<>();
        String selection = MediaStore.Audio.Media.MIME_TYPE + "=?";
        String[] selectionArgs = new String[]{"audio/mpeg"};
        // 媒体库查询语句
        //Media library query statement
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selection,
                selectionArgs, MediaStore.Audio.Media.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {

                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String author = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

                MusicInfo oneMedia = new MusicInfo();
                oneMedia.setFilePath(path);
                oneMedia.setExoplayerPath(path);
                oneMedia.setDuration(duration * 1000);
                oneMedia.setTitle(title);
                oneMedia.setArtist(author);
                oneMedia.setMimeType(type);
                oneMedia.setTrimIn(0);
                oneMedia.setTrimOut(oneMedia.getDuration());

                oneMedia.setTrimIn(0);
                oneMedia.setTrimOut(oneMedia.getDuration());
                list.add(oneMedia);
            }
            // 释放资源
            //Release resources
            cursor.close();
        }
        return list;
    }
}
