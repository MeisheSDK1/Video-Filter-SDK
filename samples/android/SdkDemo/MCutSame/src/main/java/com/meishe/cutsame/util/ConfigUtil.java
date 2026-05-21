package com.meishe.cutsame.util;


import com.meishe.cutsame.BuildConfig;
import com.meishe.cutsame.R;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yangtailin
 * @CreateDate :2021/3/22 10:17
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ConfigUtil {
    public static final String BUILD_TYPE_TOB = "2B";
    public static final String BUILD_TYPE_TOC = "2C";

    public static boolean isToC() {
        return BUILD_TYPE_TOC.equals(BuildConfig.TO_TYPE);
    }

    public static int[] colors = {R.color.color_point_group_1, R.color.color_point_group_2, R.color.color_point_group_3, R.color.color_point_group_4, R.color.color_point_group_5
            , R.color.color_point_group_6, R.color.color_point_group_7, R.color.color_point_group_8, R.color.color_point_group_9, R.color.color_point_group_10
            , R.color.color_point_group_11, R.color.color_point_group_12, R.color.color_point_group_13};
}
