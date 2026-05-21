package com.meishe.engine.asset.observer;

import android.database.Observable;

import com.meishe.base.utils.ThreadUtils;
import com.meishe.engine.asset.bean.AssetInfo;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2021/2/23 13:25
 * @Description :资源变动被观察者 the asset changed observable
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class AssetObservable extends Observable<AssetObserver> {

    /**
     * 添加资源
     * Add asset
     *
     * @param assetInfo the asset info
     */
    public void addAsset(final AssetInfo assetInfo) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = mObservers.size() - 1; i >= 0; i--) {
                    mObservers.get(i).onAssetAdd(assetInfo);
                }
            }
        });
    }

    /**
     * 删除资源
     * Delete asset
     *
     * @param assetInfo the asset info
     */
    public void deleteAsset(final AssetInfo assetInfo) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = mObservers.size() - 1; i >= 0; i--) {
                    mObservers.get(i).onAssetRemove(assetInfo);
                }
            }
        });
    }
}
