package com.meishe.sdkdemo.mimodemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.meicam.sdk.NvsTimeline;
import com.meishe.http.AssetType;
import com.meishe.http.bean.BaseDataBean;
import com.meishe.net.custom.BaseResponse;
import com.meishe.net.custom.RequestCallback;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.capturescene.httputils.NetWorkUtil;
import com.meishe.sdkdemo.capturescene.httputils.OkHttpClientManager;
import com.meishe.sdkdemo.capturescene.httputils.download.DownLoadResultCallBack;
import com.meishe.sdkdemo.edit.interfaces.OnTitleBarClickListener;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.edit.view.dialog.DownloadDialog;
import com.meishe.sdkdemo.mimodemo.bean.MiMoLocalData;
import com.meishe.sdkdemo.mimodemo.bean.MimoOnlineData;
import com.meishe.sdkdemo.mimodemo.common.template.model.MiMoInfo;
import com.meishe.sdkdemo.mimodemo.common.template.model.TempJsonInfo;
import com.meishe.sdkdemo.mimodemo.common.template.utils.NvMiMoContext;
import com.meishe.sdkdemo.mimodemo.common.utils.MimoTimelineUtil;
import com.meishe.sdkdemo.mimodemo.common.utils.ParseJsonFile;
import com.meishe.sdkdemo.mimodemo.common.utils.asset.NvAsset;
import com.meishe.sdkdemo.mimodemo.common.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.mimodemo.interf.OnMiMoSelectListener;
import com.meishe.sdkdemo.mimodemo.mediapaker.SelectMediaActivity;
import com.meishe.sdkdemo.utils.FileUtils;
import com.meishe.sdkdemo.utils.Logger;
import com.meishe.sdkdemo.utils.MimoFileDataUtil;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.utils.ToastUtil;
import com.meishe.sdkdemo.utils.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.FragmentManager;
import okhttp3.Request;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : Guijun
 * @CreateDate : 2020/8/5.
 * @Description :ThemePreviewListAdapt。ThemePreviewListAdapt
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MimoActivity extends BaseActivity {
    private String TAG = "MimoActivity";
    private static final int DOWN_LOAD_TYPE_ZIP = 201;
    private static final int DOWN_LOAD_TYPE_VIDEO = 202;
    private MimoVideoFragment mMimoVideoFragment;
    private CustomTitleBar customTitleBar;
//    private final String URL = "https://vsapi.meishesdk.com/app/index.php?command=listMimoMaterial&page=0&pageSize=1000";
    private String mDownloadPath;
    private List<BaseDataBean<MiMoLocalData>> onlineDataDetails;
    private MiMoListFragment templateListFragment;
    private DownloadDialog mDownloadDialog;
    private MiMoLocalData mCurrentData;
    private RelativeLayout mLoadingView;
    private Map<String, MiMoLocalData> downloadingMp4 = new HashMap<>();
    private List<MiMoLocalData> mDataListLocals = new ArrayList<>();

    /**
     * 重置数据，清除用户选择数据，保留模板数据
     * Reset data, clear user selection data, retain template data
     */
    private void resetTemplateData() {
        MiMoLocalData selectTemplate = NvMiMoContext.getInstance().getSelectedMimoData();
        if (selectTemplate == null) {
            return;
        }
        selectTemplate.resetTemplateVideoInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        rebuildTimeLineForPlayer();
        resetTemplateData();
    }

    @Override
    protected int initRootView() {
        return R.layout.activity_mimo;
    }

    @Override
    protected void initViews() {
        NvAssetManager.init(this);
        customTitleBar = findViewById(R.id.title);
        mLoadingView = findViewById(R.id.loading_layout);
        customTitleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {

            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {

            }
        });

    }

    private void initBottomFragment(List<MiMoLocalData> templateInfos) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        templateListFragment = new MiMoListFragment(templateInfos);
        fragmentManager.beginTransaction().add(R.id.bottom_layout, templateListFragment).commit();
        fragmentManager.beginTransaction().show(templateListFragment);
        templateListFragment.setOnTemplateSelectListener(new OnMiMoSelectListener() {
            @Override
            public void onTemplateSelected(int position) {
                rebuildTimeLineForPlayer();
            }

            @Override
            public void onTemplateConfirm() {
                mCurrentData = templateListFragment.getCurrentData();
                if (mCurrentData == null) {
                    Logger.e(TAG, "MIMO template data is null");
                    return;
                }
                if (mCurrentData.isLocal()) {
                    NvMiMoContext.getInstance().setSelectedMimoData(mCurrentData);
                    installPackageSource(mCurrentData);
                    goSelectMedia();
                } else {
                    downloadPackage(mCurrentData.getPackageUrl(), DOWN_LOAD_TYPE_ZIP);
                }
            }
        });
        if (mDataListLocals != null && mDataListLocals.size() > 0) {
            mCurrentData = mDataListLocals.get(0);
        }
    }

    /**
     * 安装mimo特效包
     * install mimo packageFx
     *
     * @param moLocalData
     */
    private void installPackageSource(MiMoLocalData moLocalData) {
        if (moLocalData == null) {
            return;
        }
        String rootPath = moLocalData.getSourceDir();
        if (TextUtils.isEmpty(rootPath)) {
            return;
        }
        File file = new File(rootPath);
        if (!file.exists()) {
            return;
        }

        File[] subFile = file.listFiles();
        if (subFile == null) {
            return;
        }
        for (int i = 0; i < subFile.length; i++) {
            String packagePath = subFile[i].getAbsolutePath().trim();
            if (TextUtils.isEmpty(packagePath)) {
                continue;
            }
            if (packagePath.endsWith(NvAsset.SUFFIX_ANIMATED_STICKER)) {
                NvAssetManager.sharedInstance().installAssetPackage(packagePath, NvAsset.ASSET_ANIMATED_STICKER, false);
            } else if (packagePath.endsWith(NvAsset.SUFFIX_VIDEO_FX)) {
                NvAssetManager.sharedInstance().installAssetPackage(packagePath, NvAsset.ASSET_FILTER, false);
            } else if (packagePath.endsWith(NvAsset.SUFFIX_VIDEOTRANSITION)) {
                NvAssetManager.sharedInstance().installAssetPackage(packagePath, NvAsset.ASSET_VIDEO_TRANSITION, false);
            } else if (packagePath.endsWith(NvAsset.SUFFIX_COMPOUNDCAPTION)) {
                NvAssetManager.sharedInstance().installAssetPackage(packagePath, NvAsset.ASSET_COMPOUND_CAPTION, false);
            }
        }
    }

    /**
     * 跳转素材选择界面
     * Jump material selection interface
     */
    private void goSelectMedia() {
        Intent intent = new Intent(MimoActivity.this, SelectMediaActivity.class);
        startActivity(intent);
    }

    private void rebuildTimeLineForPlayer() {
        mMimoVideoFragment.stopEngine();
        boolean isSucesss = resetEngine();
        if (!isSucesss) {
            mMimoVideoFragment.removeTimeLine();
            return;
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mMimoVideoFragment.playVideoFromStartPosition();
            }
        });
    }

    private void initVideoFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mMimoVideoFragment = MimoVideoFragment.newInstance(0L);
        fragmentManager.beginTransaction().add(R.id.videoLayout, mMimoVideoFragment).commit();
        fragmentManager.beginTransaction().show(mMimoVideoFragment);
    }

    /**
     * 重置引擎相关类数据
     * reset MiMoEngine
     *
     * @return
     */
    private boolean resetEngine() {
        mCurrentData = templateListFragment.getCurrentData();
        if (mCurrentData == null) {
            return false;
        }
        NvsTimeline timeline;
        String videoPath = mCurrentData.getVideoPath();
        if (TextUtils.isEmpty(videoPath)) {
            String videoUrl = mCurrentData.getVideoUrl();
            if (videoUrl == null || videoUrl.isEmpty()) {
                return false;
            }
            String cachePath = PathUtils.getMimoPreviewVideoPath(MimoActivity.this, videoUrl);
            File cacheVideoFile = new File(cachePath);
            if (cacheVideoFile.exists()) {
                mCurrentData.setVideoPath(cachePath);
                videoPath = cachePath;
            } else {
                downloadingMp4.put(cachePath, mCurrentData);
                downloadPackage(videoUrl, DOWN_LOAD_TYPE_VIDEO);
                return false;
            }
        }
        if (mMimoVideoFragment.getTimeLine() == null) {
            timeline = MimoTimelineUtil.createTimeline(videoPath);
        } else {
            timeline = MimoTimelineUtil.reBuildSingleVideoTrack(mMimoVideoFragment.getTimeLine(), videoPath);
        }

        mMimoVideoFragment.setTimeLine(timeline);
        mMimoVideoFragment.initData();
        return true;
    }


    private void installFont() {
        String fontJsonPath = "font/info_mimo.json";
        String fontJsonText = ParseJsonFile.readAssetJsonFile(this, fontJsonPath);
        if (TextUtils.isEmpty(fontJsonText)) {
            return;
        }
        TempJsonInfo fontJsonInfo = ParseJsonFile.fromJson(fontJsonText, TempJsonInfo.class);
        if (fontJsonInfo == null) {
            return;
        }
        List<TempJsonInfo.JsonInfo> fontList = fontJsonInfo.getJsonList();
        if (fontList == null || fontList.isEmpty()) {
            return;
        }
        int fontCount = fontList.size();
        for (int idx = 0; idx < fontCount; idx++) {
            TempJsonInfo.JsonInfo fontInfo = fontList.get(idx);
            if (fontInfo == null) {
                continue;
            }
            String fontAssetPath = "assets:/font/" + fontInfo.jsonPath;
            String fontName = mStreamingContext.registerFontByFilePath(fontAssetPath);
            Log.d(TAG, "fontName = " + fontName);
        }
    }

    @Override
    protected void initTitle() {
    }

    /**
     * 取消下载
     * cancel download
     */
    private void cancelDownloads() {
        for (String url : downloadingURL.keySet()) {
            OkHttpClientManager.cancelTag(url);
        }
    }

    @Override
    protected void initData() {
        mDownloadDialog = new DownloadDialog(this);

        mDownloadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDownloadDialog.setProgress(0);
                cancelDownloads();
            }
        });
        /*
         * 模板下载路径
         * The Mimo template download path
         */
        mDownloadPath = PathUtils.getSDCardPathByType(com.meishe.sdkdemo.utils.asset.NvAsset.ASSET_MIMO) + File.separator;
        installFont();
        NvMiMoContext.init(this.getApplicationContext());
        initVideoFragment();
        getLocalData();
        initBottomFragment(mDataListLocals);
        getHttpData();
    }

    /**
     * 获取本地模板数据
     * Get local template data
     */
    private void getLocalData() {
        String rootDirPath = mDownloadPath;
        if (TextUtils.isEmpty(rootDirPath) || (null == rootDirPath)) {
            return;
        }
        File rootFile = new File(rootDirPath);
        if (!rootFile.exists()) {
            return;
        }
        File[] subFile = rootFile.listFiles();
        if (subFile == null) {
            return;
        }
        mDataListLocals.clear();
        for (int i = 0; i < subFile.length; ++i) {
            File dir = subFile[i];
            if (!dir.isDirectory()) {
                continue;
            }
            String dirName = dir.getName();
            MiMoLocalData localData = getMimoLolalDataFromInfo(dir.getAbsolutePath());
            if (localData != null) {
                localData.setId(dirName);
                localData.setSourceDir(dir.getAbsolutePath());
                localData.setLocal(true);
                MimoFileDataUtil.updateShotClipInfos(localData);
                localData.updateTotalShotVideoInfos();
                mDataListLocals.add(localData);
            }
        }
    }

    /**
     * 从指定路径中读取本地MiMo数据
     * Read local MiMo data from the specified path
     *
     * @param dirPath
     * @return
     */
    private MiMoLocalData getMimoLolalDataFromInfo(String dirPath) {
        MiMoLocalData miMoLocalData = null;
        File infoFile = new File(dirPath, "info.json");
        if (!infoFile.exists()) {
            return miMoLocalData;
        }
        try {
            String read_json = Util.loadFromSDFile(infoFile.getPath());
            miMoLocalData = mGson.fromJson(read_json, MiMoLocalData.class);
            if (miMoLocalData != null) {
                miMoLocalData.setCoverUrl(dirPath + File.separator + miMoLocalData.getCover());
                miMoLocalData.setVideoPath(dirPath + File.separator + miMoLocalData.getPreview());
                miMoLocalData.setMusicFilePath(dirPath + File.separator + miMoLocalData.getMusic());
            }
        } catch (Exception e) {
            Log.e(TAG, "phase info.json exception!");
        }
        return miMoLocalData;
    }

    /**
     * 获取网络数据
     * get data from http
     */
    private void getHttpData() {
        if (!NetWorkUtil.isNetworkConnected(this)) {
            initCacheData();
            mLoadingView.setVisibility(View.GONE);
            if (mDataListLocals.size() == 0) {
                ToastUtil.showToast(this, R.string.check_network);
            }
            return;
        }
        HttpManager.getMaterialList(null, AssetType.MIMO_ALL, -1, NvAsset.AspectRatio_All, "", MSApplication.getSdkVersion()
                , 1, 1000, new RequestCallback<MimoOnlineData<MiMoLocalData>>() {
                    @Override
                    public void onSuccess(BaseResponse<MimoOnlineData<MiMoLocalData>> response) {
                        if (response != null && response.getData() != null) {
                            String path = PathUtils.getMimoCacheFolderPath(MimoActivity.this);
                            File file = new File(path, "info.json");
                            if (!file.exists()) {
                                ParseJsonFile.saveObjectByJson(file, response.getData());
                            }
                            onlineDataDetails = response.getData().getElements();
                            initOnlineData();
                        }
                        mLoadingView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(BaseResponse<MimoOnlineData<MiMoLocalData>> response) {
                        mLoadingView.setVisibility(View.GONE);
                        ToastUtil.showToast(MimoActivity.this, R.string.network_strayed_try);
                    }
                });
    }

    private Gson mGson = new Gson();

    private void initCacheData() {
        try {
            String path = PathUtils.getMimoCacheFolderPath(MimoActivity.this);
            File file = new File(path, "info.json");
            if (!file.exists()) {
                return;
            }
            MimoOnlineData onlineData = (MimoOnlineData) ParseJsonFile.getObjectByJson(file, MimoOnlineData.class);
            if (onlineData != null) {
                onlineDataDetails = onlineData.getElements();
            }
            initOnlineData();
        } catch (Exception e) {
            Log.e(TAG, "Exception:" + e.toString());
        }
    }

    /**
     * 同步线上与本地数据
     * Synchronize online and local data
     */
    private void initOnlineData() {
        if (onlineDataDetails == null || onlineDataDetails.isEmpty()) {
            return;
        }
        List<MiMoInfo> templateData = new ArrayList<>();
        for (int i = 0; i < onlineDataDetails.size(); i++) {
            BaseDataBean<MiMoLocalData> dataDetail = onlineDataDetails.get(i);
            if (!isExistInLocal(dataDetail.getId())) {
                try {
                    MiMoLocalData miMoLocalData = dataDetail.getInfoJson();
                    if (miMoLocalData == null) {
                        continue;
                    }
                    miMoLocalData.setPackageUrl(dataDetail.getPackageUrl());
                    miMoLocalData.setUuid(dataDetail.getId());
                    miMoLocalData.setVideoUrl(dataDetail.getPreviewVideoUrl());
                    miMoLocalData.setCoverUrl(dataDetail.getCoverUrl());
                    miMoLocalData.setName(dataDetail.getDisplayName());
                    miMoLocalData.setLocal(false);
                    mDataListLocals.add(miMoLocalData);
                } catch (Exception e) {
                    continue;
                }
            }
        }
        refreshData();
        if (mCurrentData == null) {
            rebuildTimeLineForPlayer();
        }
    }

    /**
     * 刷新数据
     * refresh data
     */
    private void refreshData() {
        templateListFragment.setNewDatas(mDataListLocals);
    }

    /**
     * 指定id是否在本地数据中存在
     * Specifies whether the id exists in the local data
     */
    private boolean isExistInLocal(String id) {
        if (!TextUtils.isEmpty(id)) {
            for (MiMoLocalData localData : mDataListLocals) {
                if (localData == null) {
                    continue;
                }
                if (id.equals(localData.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 记录下载地址的数组，维护下载的完整性。
     * An array of download addresses to maintain download integrity.
     */
    private Map<String, String> downloadingURL = new HashMap<>();

    private void downloadPackage(final String packageUrl, final int loadType) {
        String downloadPath = mDownloadPath;
        if (loadType == DOWN_LOAD_TYPE_VIDEO) {
            mLoadingView.setVisibility(View.VISIBLE);
            downloadPath = PathUtils.getMimoCacheFolderPath(MimoActivity.this);
        } else {
            if (mDownloadDialog != null) {
                mDownloadDialog.show();
            }
            downloadingURL.put(packageUrl, mDownloadPath + mCurrentData.getId());
        }
        OkHttpClientManager.downloadAsyn(packageUrl, downloadPath, new DownLoadResultCallBack<String>() {
            @Override
            public void onProgress(long now, long total, int progress) {
                if (loadType == DOWN_LOAD_TYPE_ZIP) {
                    mDownloadDialog.setProgress(progress);
                }
            }

            @Override
            public void onError(Request request, Exception e) {
                super.onError(request, e);
                Logger.e(TAG, "downloadPackageOnError: " + e.toString());
                if (loadType == DOWN_LOAD_TYPE_VIDEO) {
                    mLoadingView.setVisibility(View.GONE);
                } else {
                    mDownloadDialog.dismiss();
                    mDownloadDialog.setProgress(0);
                    deleteFiles(downloadingURL.get(packageUrl));
                    downloadingURL.remove(packageUrl);
                }
                ToastUtil.showToast(MimoActivity.this, R.string.check_network);
            }

            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                Logger.e(TAG, "download  onResponse: " + response + "  loadType " + loadType);
                if (loadType == DOWN_LOAD_TYPE_VIDEO) {
                    MiMoLocalData miMoLocalData = downloadingMp4.get(response);
                    if (miMoLocalData != null) {
                        downloadingMp4.remove(response);
                        miMoLocalData.setVideoPath(response);
                        if (miMoLocalData == mCurrentData) {
                            rebuildTimeLineForPlayer();
                        }
                    }
                    mLoadingView.setVisibility(View.GONE);
                } else {
                    mDownloadDialog.dismiss();
                    mDownloadDialog.setProgress(0);
                    Logger.e(TAG, "onResponse: " + response);
                    downloadingURL.remove(packageUrl);

                    PathUtils.unZipFile(response, mDownloadPath);

                    String onePackageDir = FileUtils.getFileNameNoEx(response);
                    File file = new File(onePackageDir);
                    MiMoLocalData localData = getMimoLolalDataFromInfo(onePackageDir);
                    if (localData != null) {
                        localData.setId(file.getName());
                        localData.setSourceDir(onePackageDir);
                        localData.setLocal(true);
                        MimoFileDataUtil.updateShotClipInfos(localData);
                        localData.updateTotalShotVideoInfos();
                        NvMiMoContext.getInstance().setSelectedMimoData(localData);
                        for (int j = 0; j < mDataListLocals.size(); j++) {
                            MiMoLocalData tempData = mDataListLocals.get(j);
                            if (TextUtils.equals(tempData.getId(), file.getName())) {
                                mDataListLocals.remove(j);
                                mDataListLocals.add(j, localData);
                                refreshData();
                                break;
                            }
                        }
                    }
                    installPackageSource(localData);
                    deleteFiles(response);
                    goSelectMedia();
                }
            }
        });
    }

    private void deleteFiles(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    protected void initListener() {
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMimoVideoFragment != null) {
            mMimoVideoFragment.stopEngine();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NvMiMoContext.getInstance().setSelectListIndex(0);
    }
}
