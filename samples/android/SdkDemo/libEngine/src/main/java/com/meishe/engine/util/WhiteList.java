package com.meishe.engine.util;

import android.os.Build;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.base.utils.MediaTypeUtils;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: ChuChenGuang
 * @CreateDate: 2021/2/22 16:39
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class WhiteList {
    /**
     * 设置做大 icon Reader
     * need set max icon reader white list boolean.
     *
     * @return the boolean
     */
    public static boolean isNeedSetMaxIconReaderWhiteList() {
        String currOs = Build.MODEL;
        if ("MIX 2S".equals(currOs)) {
            return true;
        }
        return false;
    }

    /**
     * 转码4k素材的白名单
     * Is covert 4 k file white list boolean.
     *
     * @param path the path
     * @return the boolean
     */
    public static boolean isCovert4KFileWhiteList(String path) {
        String currOs = Build.MODEL;
        if ("5061K_EEA".equals(currOs) && checkVideoAssetIs4K(path)) {
            return true;
        }
        return false;
    }

    /**
     * 判断素材是否是4k
     * Check video asset is 4 k boolean.
     *
     * @param path the path
     * @return the boolean
     */
    public static boolean checkVideoAssetIs4K(String path) {
        NvsAVFileInfo avFileInfo = NvsStreamingContext.getInstance().getAVFileInfo(path);
        if (avFileInfo == null) {
            return false;
        }
        if (avFileInfo.getAVFileType() == NvsAVFileInfo.AV_FILE_TYPE_AUDIOVIDEO) {
            NvsSize nvsSize = avFileInfo.getVideoStreamDimension(0);
            if (nvsSize == null) {
                return false;
            }
            return nvsSize.height >= nvsSize.width ? (nvsSize.width >= 2160) : (nvsSize.height >= 2160);
        }

        return false;
    }

    /**
     * flv视频分割
     * Is flv asset split boolean.
     *
     * @param path the path
     * @return the boolean
     */
    public static boolean isFLVAssetSplit(String path) {
        String currOs = Build.MODEL;
        if ("PDPM00".equals(currOs) && "FLV".equals(MediaTypeUtils.getFileTypeBySub(path))) {
            return true;
        }
        return false;
    }

    public static boolean isGooglePixel3(){
        String currOs = Build.MODEL;
        if(currOs.equalsIgnoreCase("Pixel 3 XL")){
            return true;
        }
        return false;
    }

    public static boolean isXiaoMi9(){
        String currOs = Build.MODEL;
        if(currOs.equalsIgnoreCase("MI 9")){
            return true;
        }
        return false;
    }
}
