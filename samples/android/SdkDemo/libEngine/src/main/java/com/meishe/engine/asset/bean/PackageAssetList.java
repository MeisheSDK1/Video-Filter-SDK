package com.meishe.engine.asset.bean;

import java.util.List;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2021/2/24 14:53
 * @Description :包内资源包裹实体类 An in-package resource wraps an entity class
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class PackageAssetList {
    private List<AssetInfo> assetList;

    public List<AssetInfo> getAssetList() {
        return assetList;
    }

    public void setAssetList(List<AssetInfo> assetList) {
        this.assetList = assetList;
    }
}
