package com.meishe.sdkdemo.capture.fragment.beauty;

import android.text.TextUtils;
import android.util.Log;

import com.meishe.base.constants.BaseConstants;
import com.meishe.base.model.Presenter;
import com.meishe.base.utils.ToastUtils;
import com.meishe.base.utils.ZipUtils;
import com.meishe.http.AssetType;
import com.meishe.http.bean.BaseBean;
import com.meishe.makeup.MakeupDataManager;
import com.meishe.makeup.makeup.Makeup;
import com.meishe.makeup.makeup.MakeupParamContent;
import com.meishe.makeup.utils.ParseJsonFile;
import com.meishe.net.custom.BaseResponse;
import com.meishe.net.custom.RequestCallback;
import com.meishe.net.custom.SimpleDownListener;
import com.meishe.net.model.Progress;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.capture.bean.BeautyTemplateInfo;
import com.meishe.sdkdemo.utils.FileUtils;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.utils.NvAsset;
import com.meishe.utils.PathUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/2/9 15:08
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class BeautyTemplatePresenter extends Presenter<BeautyTemplateView> {
    private static final String TAG = "BeautyTemplatePresenter";

    /**
     * 获取自定义美颜模板数据
     */
    public void getCustomBeautyTemplateData(BeautyTemplateInfo templateInfo) {
        Makeup makeup = new Makeup();
        MakeupParamContent paramContent = new MakeupParamContent();
        paramContent.setBeautyParams(new ArrayList<>());
        paramContent.setShapeParams(new ArrayList<>());
        paramContent.setAdjustParams(new ArrayList<>());
        paramContent.setMicroShapeParams(new ArrayList<>());
        makeup.setEffectContent(paramContent);
        makeup.setAssetsDirectory("");
        templateInfo.setTemplateType(BeautyTemplateInfo.BeautyTemplateType.BeautyTemplate_Custom);
        templateInfo.setMakeup(makeup);
    }

    /**
     * 获取美颜模板数据
     * Obtain beauty template data
     */
    public void getBeautyTemplateData(List<BeautyTemplateInfo> mBeautyTemplates) {
        getBeautyTemplateFromLocal(mBeautyTemplates);
        getBeautyTemplateFromNet(1, mBeautyTemplates);
    }

    /**
     * 从本地获取模板数据
     * Obtain template data locally
     *
     * @param beautyTemplates beautyTemplates
     */
    private void getBeautyTemplateFromLocal(List<BeautyTemplateInfo> beautyTemplates) {
        String rootDirPath = PathUtils.getFolderDirPath(PathUtils.LOCAL_EFFECT_BEAUTY_TEMPLATE);
        if (TextUtils.isEmpty(rootDirPath) || (null == rootDirPath)) {
            return;
        }
        File rootFile = new File(rootDirPath);
        if (!rootFile.exists()) {
            return;
        }
        File[] files = rootFile.listFiles();
        if ((null == files) || (files.length == 0)) {
            return;
        }
        for (File file : files) {
            BeautyTemplateInfo templateInfo = new BeautyTemplateInfo();
            templateInfo.setDisplayName("Test");
            templateInfo.setDisplayNameZhCn("测试");
            templateInfo.setDownloadStatus(BaseConstants.RES_STATUS_DOWNLOAD_ALREADY);
            parseBeautyTemplateInfo(file.getAbsolutePath(), templateInfo);
            beautyTemplates.add(templateInfo);
        }
    }

    /**
     * 从网络获取模板数据
     * Obtain template data from the network
     *
     * @param pageNum         pageNum
     * @param beautyTemplates beautyTemplates
     */
    public void getBeautyTemplateFromNet(int pageNum, List<BeautyTemplateInfo> beautyTemplates) {
        AssetType assetType = AssetType.BEAUTY_TEMPLATE;
        assetType.setSorted(0);
        HttpManager.getMaterialList(null,
                assetType
                , null
                , 1
                , NvAsset.AspectRatio_All
                , ""
                , MSApplication.getSdkVersion()
                , pageNum, 20
                , new RequestCallback<BaseBean<BeautyTemplateInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<BaseBean<BeautyTemplateInfo>> response) {
                        if (null == response) {
                            return;
                        }
                        int code = response.getCode();
                        if (code != 1) {
                            ToastUtils.showShort(response.getMsg());
                            return;
                        }
                        BaseBean<BeautyTemplateInfo> baseBean = response.getData();
                        if (null == baseBean) {
                            return;
                        }
                        boolean isMoreData = (baseBean.getTotal() == 20);
                        List<BeautyTemplateInfo> data = baseBean.getElements();
                        if ((null == data) || data.isEmpty()) {
                            getView().onBeautyTemplateData(beautyTemplates, isMoreData);
                            return;
                        }
                        beautyTemplates.addAll(data);
                        checkFileExist(beautyTemplates);
                        getView().onBeautyTemplateData(beautyTemplates, isMoreData);
                    }

                    @Override
                    public void onError(BaseResponse<BaseBean<BeautyTemplateInfo>> response) {
                        getView().onBeautyTemplateData(beautyTemplates, false);
                    }
                });
    }

    /**
     * 检查文件是否存在
     * Check whether the file exists
     *
     * @param beautyTemplates Beauty Templates
     */
    private void checkFileExist(List<BeautyTemplateInfo> beautyTemplates) {
        if ((null == beautyTemplates) || beautyTemplates.isEmpty()) {
            return;
        }
        String rootDirPath = PathUtils.getAssetDownloadPath(NvAsset.ASSET_BEAUTY_TEMPLATE);
        if (TextUtils.isEmpty(rootDirPath) || (null == rootDirPath)) {
            return;
        }
        File rootFile = new File(rootDirPath);
        if (!rootFile.exists()) {
            return;
        }
        File[] files = rootFile.listFiles();
        if ((null == files) || (files.length == 0)) {
            return;
        }
        for (BeautyTemplateInfo templateInfo : beautyTemplates) {
            if (null == templateInfo) {
                continue;
            }
            int downloadStatus = templateInfo.getDownloadStatus();
            if (downloadStatus == BaseConstants.RES_STATUS_DOWNLOAD_ALREADY) {
                continue;
            }

            for (File file : files) {
                if (!file.isDirectory()) {
                    continue;
                }
                String directoryName = file.getName();
                if (TextUtils.equals(directoryName, templateInfo.getId())) {
                    if (parseBeautyTemplateInfo(file.getAbsolutePath(), templateInfo)) {
                        templateInfo.setDownloadStatus(BaseConstants.RES_STATUS_DOWNLOAD_ALREADY);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 下载特效包
     * download fx package
     *
     * @param templateInfo templateInfo
     */
    public void downloadPackage(int position, BeautyTemplateInfo templateInfo) {
        if (null == templateInfo) {
            return;
        }
        String packageUrl = templateInfo.getPackageUrl();
        if (TextUtils.isEmpty(packageUrl)) {
            return;
        }
        String[] zipName = packageUrl.split("/");
        String rootDirPath = PathUtils.getAssetDownloadPath(NvAsset.ASSET_BEAUTY_TEMPLATE);
        HttpManager.download(packageUrl, packageUrl, rootDirPath, zipName[zipName.length - 1], new SimpleDownListener(packageUrl) {
            @Override
            public void onProgress(Progress progress) {
                super.onProgress(progress);
                int currentProgress = (int) (progress.fraction * 360);
                getView().onDownloadPackageProgress(position, templateInfo, currentProgress);
            }

            @Override
            public void onError(Progress progress) {
                super.onError(progress);
                getView().onDownloadPackageError(position, templateInfo);
            }

            @Override
            public void onFinish(File file, Progress progress) {
                super.onFinish(file, progress);
                try {
                    if (null == progress) {
                        return;
                    }
                    String zipFilePath = progress.filePath;
                    if (TextUtils.isEmpty(zipFilePath)) {
                        return;
                    }
                    String destDirPath = rootDirPath + File.separator + templateInfo.getId();
                    List<File> files = ZipUtils.unzipFile(zipFilePath, destDirPath);
                    if ((null == files) || files.isEmpty()) {
                        return;
                    }
                    FileUtils.deleteFile(file);
                    NvAssetManager.sharedInstance().installAssetPackage(destDirPath);
                    if (parseBeautyTemplateInfo(destDirPath, templateInfo)) {
                        getView().onDownloadPackageSuccess(position, templateInfo);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    getView().onDownloadPackageError(position, templateInfo);
                }
            }
        });
    }

    /**
     * 解析美颜模板信息
     * Parse beauty template information
     *
     * @param assetsDirectory root path
     * @param templateInfo    template info
     */
    private boolean parseBeautyTemplateInfo(String assetsDirectory, BeautyTemplateInfo templateInfo) {
        if (TextUtils.isEmpty(assetsDirectory) || (null == templateInfo)) {
            return false;
        }
        String fileJson = assetsDirectory + File.separator + MakeupDataManager.INFO_NEW_JSON;
        File jsonFile = new File(fileJson);
        if (!jsonFile.exists()) {
            Log.e(TAG, "parseBeautyTemplateInfo jsonFile is null: " + fileJson);
            return false;
        }
        String readInfo = ParseJsonFile.readSdCardJsonFile(fileJson);
        if (TextUtils.isEmpty(readInfo)) {
            Log.e(TAG, "parseBeautyTemplateInfo readInfo is null: " + readInfo);
            return false;
        }
        templateInfo.setDownloadStatus(BaseConstants.RES_STATUS_DOWNLOAD_ALREADY);
        Makeup makeup = ParseJsonFile.fromJson(readInfo, Makeup.class);
        makeup.setAssetsDirectory(assetsDirectory);
        templateInfo.setMakeup(makeup);
        return true;
    }
}
