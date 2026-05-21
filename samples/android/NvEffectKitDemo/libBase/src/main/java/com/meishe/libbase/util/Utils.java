package com.meishe.libbase.util;

import android.text.TextUtils;

import com.meicam.sdk.NvsAssetPackageManager;


/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/6/9 19:52
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class Utils {
    /**
     * 获取SDK中的素材类型表示方式
     * Get the representation of the material type in the SDK
     *
     * @param suffixName the suffix name 后缀名
     * @return the package type
     */
    public static int getPackageType(String suffixName) {
        if (TextUtils.isEmpty(suffixName)) {
            return -1;
        }
        switch (suffixName) {
            case "videofx":
                return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX;
            case "facemesh":
                return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_FACE_MESH;
            case "warp":
                return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_WARP;
            case "makeup":
                return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_MAKEUP;
            default:
                return -1;
        }
    }
}
