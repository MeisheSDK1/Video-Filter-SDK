package com.meishe.cutsame.util;

import com.meishe.cutsame.bean.RatioInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2021/1/13 16:16
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class RatioUtil {
    public static String COLON_TAG = ":";
    public static String V_TAG = "v";
    private static int[] ASPECTRATIOS = {1, 2, 4, 8, 16, 32, 64, 512, 1024};
    private static String[] ASPECTRATIOSTR = {"16v9", "1v1", "9v16", "4v3", "3v4", "18v9", "9v18", "21v9", "9v21"};

    public static String getAspectRatioStr(int rawValue) {
        String str = "16:9";
        if (rawValue == 1) {
            str = "16:9";
        } else if (rawValue == 2) {
            str = "1:1";
        } else if (rawValue == 4) {
            str = "9:16";
        } else if (rawValue == 8) {
            str = "4:3";
        } else if (rawValue == 16) {
            str = "3:4";
        } else if (rawValue == 32) {
            str = "18:9";
        } else if (rawValue == 64) {
            str = "9:18";
        } else if (rawValue == 512) {
            str = "21:9";
        } else if (rawValue == 1024) {
            str = "9:21";
        }
        return str;
    }

    public static int getAspectRatio(String ratios) {
        int index = Arrays.asList(ASPECTRATIOSTR).indexOf(ratios);
        return (index < 0) ? 1 : ASPECTRATIOS[index];
    }


    public static List<RatioInfo> getSupportedAspectRatios(int defaultRatio, int supportedAspectRatio) {
        List<RatioInfo> ratios = new ArrayList<>();
        for (int i = 0; i < ASPECTRATIOS.length; i++) {
            int indexRatio = ASPECTRATIOS[i];
            if ((indexRatio & supportedAspectRatio) != 0) {
                RatioInfo ratioInfo = new RatioInfo();
                String ratioStr = getAspectRatioStr(indexRatio);
                ratioInfo.setTag(indexRatio);
                ratioInfo.setName((defaultRatio == indexRatio) ? ratioStr + "(默认)" : ratioStr);
                ratios.add(ratioInfo);
            }
        }
        return ratios;
    }

}
