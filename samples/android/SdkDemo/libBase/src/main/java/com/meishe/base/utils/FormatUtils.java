package com.meishe.base.utils;

import java.text.DecimalFormat;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate : 2020/11/20
 * @Description :关于格式化的工具类。 A tool class about formatting
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class FormatUtils {
    private static final int HOUR = 3600;
    private static final int MINUTE = 60;
    private static final int HUNDRED = 100;
    private static final int MILLISECOND = 1000;
    private static final int MICROSECOND = 1000000;

    /**
     * 将时间转化成00:00:00的格式
     * Convert the time to a 00:00:00 format
     *
     * @param time long 需要转化的微秒数 The microsecond need to convert
     * @return the string
     */
    public static String microsecond2Time(long time) {
        return sec2Time((int) (time / MICROSECOND));
    }

    /**
     * 将时间转化成00:00:00的格式
     * Convert the time to a 00:00:00 format
     *
     * @param time long 需要转化的毫秒数 The millisecond need to convert
     * @return the string
     */
    public static String millisecond2Time(long time) {
        return sec2Time((int) (time / MILLISECOND));
    }

    /**
     * 将时间转化成00:00:00的格式
     * Convert the time to a 00:00:00 format
     *
     * @param time int 需要转化的秒数 The second need to convert
     * @return the string
     */
    public static String sec2Time(int time) {
        String timeStr = "00:00";
        if (time > 0) {
            int hour = time / HOUR;
            int minute = time / MINUTE;
            int second = time % MINUTE;

            timeStr = getTime(minute) + ":" + getTime(second);

            if (hour < HUNDRED && hour > 0) {
                timeStr = getTime(hour) + ":" + timeStr;
            } else if (hour >= HUNDRED) {
                timeStr = "99:59:59";
            }
        }
        return timeStr;
    }

    private static String getTime(int i) {
        return i >= 0 && i < 10 ? "0" + i : Integer.toString(i);
    }

    /**
     * 将时间转化成00:00:x.xs的格式
     * Convert the time to a 00:00:x.xs format
     *
     * @param duration long 需要转化的微秒时长 The microsecond need to convert
     * @return the string
     */
    public static String duration2Text(long duration) {
        DecimalFormat format = new DecimalFormat("0.0");
        float time = duration * 1f / MICROSECOND;
        String timeStr;
        if (time > MINUTE) {
            int hour = (int) (time / HOUR);
            int minute = (int) (time / MINUTE);
            float second = time % MINUTE;
            timeStr = getTime(minute) + ":" + (second < 10 ? "0" + format.format(second) + "s" : format.format(second) + "s");
            if (hour < HUNDRED && hour > 0) {
                timeStr = getTime(hour) + ":" + timeStr;
            } else if (hour >= HUNDRED) {
                timeStr = "99:59:59s";
            }
        } else {
            timeStr = format.format(time) + "s";
        }
        return timeStr;
    }
}
