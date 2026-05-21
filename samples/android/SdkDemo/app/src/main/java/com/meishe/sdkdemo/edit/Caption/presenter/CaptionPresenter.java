package com.meishe.sdkdemo.edit.Caption.presenter;

import android.text.TextUtils;

import com.meicam.sdk.NvsStreamingContext;
import com.meishe.base.model.Presenter;
import com.meishe.base.utils.LogUtils;
import com.meishe.http.bean.BaseDataBean;
import com.meishe.net.custom.SimpleDownListener;
import com.meishe.net.model.Progress;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.edit.Caption.view.CaptionListener;
import com.meishe.sdkdemo.edit.Caption.view.CaptionView;
import com.meishe.sdkdemo.edit.data.AssetItem;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.asset.NvHttpRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2024/4/2 14:19
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class CaptionPresenter extends Presenter<CaptionView> {

    private Set<String> mDownloadSet = new HashSet<>();
    private String assetDownloadDir = PathUtils.getSDCardPathByType(NvAsset.ASSET_FONT);

    /**
     * 获取字体本地地址
     */
    private String getFontLocalDirPath(String remotePackageUrl) {
        String[] split = remotePackageUrl.split("/");
        File file = new File(assetDownloadDir + File.separator + split[split.length - 1]);
        if (file.exists()) {
            return file.getAbsolutePath();
        } else {
            return "";
        }
    }

    /**
     * 获取字体数据
     */
    public void getFonts() {
        getFonts(null);
    }

    /**
     * 获取字体数据
     */
    public void getFonts(CaptionListener captionListener) {
        NvAssetManager.sharedInstance().getFontDataList(1, 100, new NvHttpRequest.NvHttpRequestListener() {
            @Override
            public void onGetAssetListSuccess(List responseArrayList, int assetType, boolean hasNext, String searchKey) {
                if (responseArrayList != null) {
                    ArrayList<AssetItem> data = new ArrayList<>();
                    convertFontData(new ArrayList<>(responseArrayList),data);
                    if (captionListener != null) {
                        captionListener.getFontsBack(data);
                    }else {
                        getView().getFontsBack(data);
                    }
                }
            }

            @Override
            public void onGetAssetListFailed(Throwable e, int assetType) {

            }

            @Override
            public void onDonwloadAssetProgress(int progress, int assetType, String downloadId) {

            }

            @Override
            public void onDonwloadAssetSuccess(boolean success, String downloadPath, int assetType, String downloadId) {

            }

            @Override
            public void onDonwloadAssetFailed(Throwable e, int assetType, String downloadId) {

            }
        });
    }

    /**
     * 转换服务器字体数据
     */
    private void convertFontData(ArrayList<BaseDataBean> mServerList, ArrayList<AssetItem> mFontList) {
        if (mServerList != null && !mServerList.isEmpty()) {
            NvAsset item;
            AssetItem localFontInfo;
            for (BaseDataBean fileInfo : mServerList) {
                localFontInfo = new AssetItem();
                item = new NvAsset();
                item.coverUrl = fileInfo.getCoverUrl();
                item.fxFileName = fileInfo.getDisplayName();
                item.downloadStatus = NvAsset.DownloadStatusNone;
                item.uuid = fileInfo.getUuid();
                item.remotePackageUrl = fileInfo.getRemotePackageUrl();
                item.localDirPath = getFontLocalDirPath(fileInfo.getRemotePackageUrl());
                if (!TextUtils.isEmpty(item.localDirPath)) {
                    item.name = NvsStreamingContext.getInstance().registerFontByFilePath(item.localDirPath);
                }

                localFontInfo.setAsset(item);
                localFontInfo.setAssetMode(AssetItem.ASSET_LOCAL);
                mFontList.add(localFontInfo);
            }
        }
    }

    /**
     * 下载字体资源
     * Download the font resource pack
     *
     * @param assetInfo asset info
     * @param position  the index of asset info int the list
     */
    public void downloadAsset(final NvAsset assetInfo, final int position) {
        downloadAsset(assetInfo, position,null);
    }

    /**
     * 下载字体资源
     * Download the font resource pack
     *
     * @param assetInfo asset info
     * @param position  the index of asset info int the list
     */
    public void downloadAsset(final NvAsset assetInfo, final int position,CaptionListener captionListener) {
        if (assetInfo == null) {
            return;
        }
        String[] split = assetInfo.remotePackageUrl.split("/");
        File file = new File(assetDownloadDir + File.separator + split[split.length - 1]);
        if (file.exists()) {
            assetInfo.downloadStatus = NvAsset.DownloadStatusFinished;
            assetInfo.localDirPath = file.getAbsolutePath();
            if (captionListener != null) {
                captionListener.onDownloadFinish(position, assetInfo);
            }else {
                getView().onDownloadFinish(position, assetInfo);
            }
            return;
        }
        String packageId = String.valueOf(assetInfo.remotePackageUrl.hashCode());
        /*
         * 如果请求过，则返回
         * If the request has been made , return
         */
        if (mDownloadSet.contains(packageId)) {
            LogUtils.d("You can not request now!");
            return;
        }
        mDownloadSet.add(packageId);

        HttpManager.download(assetInfo.remotePackageUrl, assetInfo.remotePackageUrl, assetDownloadDir, split[split.length - 1], new SimpleDownListener(assetInfo.remotePackageUrl) {
            @Override
            public void onProgress(Progress progress) {
                assetInfo.downloadStatus = NvAsset.DownloadStatusInProgress;
                assetInfo.downloadProgress = (int) (progress.fraction * 100);
                if (captionListener != null) {
                    captionListener.onDownloadProgress(position);
                }else {
                    if (!isViewActive()) {
                        return;
                    }
                    getView().onDownloadProgress(position);
                }
            }

            @Override
            public void onFinish(File file, Progress progress) {
                /*
                 * 删除请求记录
                 * Delete request record
                 */
                mDownloadSet.remove(packageId);
                assetInfo.downloadStatus = NvAsset.DownloadStatusFinished;
                assetInfo.localDirPath = file.getAbsolutePath();
                if (captionListener != null) {
                    captionListener.onDownloadFinish(position, assetInfo);
                }else {
                    if (!isViewActive()) {
                        return;
                    }
                    getView().onDownloadFinish(position, assetInfo);
                }
            }

            @Override
            public void onError(Progress progress) {
                /*
                 * 删除请求记录
                 * Delete request record
                 */
                mDownloadSet.remove(packageId);
                if (file.exists()) {
                    file.delete();
                }
                assetInfo.downloadStatus = NvAsset.DownloadStatusNone;
                if (captionListener != null) {
                    captionListener.onDownloadError(position);
                }else {
                    if (!isViewActive()) {
                        return;
                    }
                    getView().onDownloadError(position);
                }
            }
        });
    }

    private boolean isViewActive() {
        return getView().isActive();
    }
}
