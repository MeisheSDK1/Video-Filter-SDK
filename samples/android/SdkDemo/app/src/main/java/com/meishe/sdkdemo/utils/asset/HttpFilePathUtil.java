package com.meishe.sdkdemo.utils.asset;

import com.meishe.sdkdemo.capture.bean.EffectInfo;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.utils.PathNameUtil;

import java.io.File;

public class HttpFilePathUtil {

    //从新的压缩包路径获取下载的位置
    ///storage/emulated/0/Android/data/com.meishe.ms240sdkdemo/files/NvStreamingSdk/Asset/FCF030E3-9FDA-4BA3-BBAF-4CEE55CAED71.1.zip
    public static String getFilterPathOutOfPathSuffix(EffectInfo effectInfo, int mAssetType) {
        String packageUrl = effectInfo.getZipUrl();
        String mEffectPathDir = PathUtils.getSDCardPathByType(mAssetType);
        String[] mSplit = packageUrl.split("/");
        String effectPath = mEffectPathDir + File.separator + mSplit[mSplit.length - 1];
        String effectUnzipPath = PathNameUtil.getOutOfPathSuffixWithOutPoint(effectPath);
        return effectUnzipPath;
    }

    public static String getFilterPath(EffectInfo effectInfo, int mAssetType) {
        String packageUrl = effectInfo.getZipUrl();
        String mEffectPathDir = PathUtils.getSDCardPathByType(mAssetType);
        String[] mSplit = packageUrl.split("/");
        String effectPath = mEffectPathDir + File.separator + mSplit[mSplit.length - 1];
        return effectPath;
    }

    public static boolean CheckIsDownload(EffectInfo effectInfo, int mAssetType) {
        String effectPath = HttpFilePathUtil.getFilterPathOutOfPathSuffix(effectInfo, mAssetType);
        return new File(effectPath).exists();
    }
}
