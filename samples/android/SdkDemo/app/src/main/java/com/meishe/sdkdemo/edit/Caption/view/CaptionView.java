package com.meishe.sdkdemo.edit.Caption.view;

import com.meishe.base.model.IBaseView;
import com.meishe.sdkdemo.edit.data.AssetItem;
import com.meishe.sdkdemo.utils.asset.NvAsset;

import java.util.ArrayList;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2024/4/2 14:19
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public interface CaptionView extends IBaseView {
    void getFontsBack(ArrayList<AssetItem> data);

    boolean isActive();

    void onDownloadProgress(int position);

    void onDownloadFinish(int position, NvAsset assetInfo);

    void onDownloadError(int position);
}
