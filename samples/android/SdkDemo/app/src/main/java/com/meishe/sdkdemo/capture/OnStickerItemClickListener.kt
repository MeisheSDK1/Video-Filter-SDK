package com.meishe.sdkdemo.capture

import com.meishe.sdkdemo.utils.asset.NvAsset
import com.meishe.sdkdemo.utils.asset.NvAssetManager

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2022/1/10 17:17
 * @Description :
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public interface OnStickerItemClickListener {
    fun onItemClick(nvAsset: NvAsset?){}
    fun onCustomItemClick(nvAsset: NvAssetManager.NvCustomStickerInfo?){}
    fun loadMore(){}
    fun onAddCustomSticker(){}
}