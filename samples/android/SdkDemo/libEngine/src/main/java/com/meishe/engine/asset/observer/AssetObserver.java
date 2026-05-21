package com.meishe.engine.asset.observer;

import com.meishe.engine.interf.IBaseInfo;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2021/2/23 13:22
 * @Description :资源变动观察者 The asset changed observer
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public abstract class AssetObserver {
    /**
     * 添加资源
     * Add asset
     *
     * @param assetInfo the asset info
     */
    public void onAssetAdd(IBaseInfo assetInfo) {
    }

    /**
     * 删除资源
     * Delete asset
     *
     * @param assetInfo the asset info
     */
    public void onAssetRemove(IBaseInfo assetInfo) {
    }
}
