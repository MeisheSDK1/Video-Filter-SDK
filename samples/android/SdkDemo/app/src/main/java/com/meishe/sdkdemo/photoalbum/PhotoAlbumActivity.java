package com.meishe.sdkdemo.photoalbum;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.ui.PlayerView;
import com.google.gson.Gson;
import com.meishe.base.utils.FileIOUtils;
import com.meishe.base.utils.GsonUtils;
import com.meishe.http.AssetType;
import com.meishe.http.bean.BaseBean;
import com.meishe.http.bean.BaseDataBean;
import com.meishe.net.custom.BaseResponse;
import com.meishe.net.custom.RequestCallback;
import com.meishe.net.custom.SimpleDownListener;
import com.meishe.net.model.Progress;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BasePermissionActivity;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.capturescene.httputils.NetWorkUtil;
import com.meishe.sdkdemo.capturescene.httputils.OkHttpClientManager;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.edit.view.dialog.DownloadDialog;
import com.meishe.sdkdemo.musicLyrics.MultiVideoSelectActivity;
import com.meishe.sdkdemo.photoalbum.grallyRecyclerView.CardScaleHelper;
import com.meishe.sdkdemo.photoalbum.grallyRecyclerView.GalleryAdapter;
import com.meishe.sdkdemo.photoalbum.grallyRecyclerView.GalleryRecyclerView;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.FileUtils;
import com.meishe.sdkdemo.utils.Logger;
import com.meishe.sdkdemo.utils.MediaConstant;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.utils.PathNameUtil;
import com.meishe.utils.SystemUtils;
import com.meishe.utils.ToastUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yyj
 * @CreateDate : 2019/11/7.
 * @Description :影集Activity。PhotoAlbumActivity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class PhotoAlbumActivity extends BasePermissionActivity {
    private final String CONFIG = "config.json";
    private final String TAG = "PhotoAlbumActivity";
    private CustomTitleBar mTitleBar;
    private GalleryRecyclerView mAlbumRv;
    private GalleryAdapter mGalleryAdapter;
    private CardScaleHelper mCardScaleHelper;
    private TextView mAlbumNameText, mAlbumTipsText, mAlbumIndexText;
    private SimpleExoPlayerWrapper playerWrapper;
    private Button mAlbumUseBtn;
    private String mDownloadPath;
    private ArrayList<PhotoAlbumData> mDataListLocal = new ArrayList<>();
    private PhotoAlbumData mCurrentData;
    private final Gson mGson = new Gson();
    private DownloadDialog mDownloadDialog;
    /**
     * 记录下载地址的数组，维护下载的完整性。
     * An array of download addresses to maintain download integrity.
     */
    private Map<String, String> downloadingURL = new HashMap<>();

    private Context mContext = MSApplication.getContext();

    @Override
    protected List<String> initPermissions() {
        return Util.getAllPermissionsList();
    }

    @Override
    protected void hasPermission() {

    }

    @Override
    protected void nonePermission() {

    }

    @Override
    protected void noPromptPermission() {

    }

    @Override
    protected int initRootView() {
        return R.layout.activity_photoalbum;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mAlbumRv = (GalleryRecyclerView) findViewById(R.id.albumRv);
        mAlbumNameText = (TextView) findViewById(R.id.albumNameText);
        mAlbumTipsText = (TextView) findViewById(R.id.albumTipsText);
        mAlbumUseBtn = (Button) findViewById(R.id.albumUseBtn);
        mAlbumIndexText = (TextView) findViewById(R.id.albumIndexText);
        mDownloadDialog = new DownloadDialog(this);

        mDownloadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDownloadDialog.setProgress(0);
                cancelDownloads();
            }
        });
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.photosAlbum);
    }

    @Override
    protected void initData() {
        playerWrapper = new SimpleExoPlayerWrapper(this);

        mDownloadPath = PathUtils.getSDCardPathByType(NvAsset.ASSET_PHOTO_ALBUM) + File.separator;

        getLoadData();
        getHttpData();

        initRecyclerView();
    }

    @Override
    protected void initListener() {
        mAlbumUseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetWorkUtil.isNetworkConnected(PhotoAlbumActivity.this)) {
                    ToastUtil.showToast(PhotoAlbumActivity.this, R.string.network_not_available);
                    return;
                }
                if (mCurrentData != null && !mCurrentData.isLocal) {
                    mDownloadDialog.show();
                    downloadPackage(mCurrentData.uuid, mCurrentData.packageUrl);
                } else {
                    gotoSelectPictures();
                }
            }
        });

        playerWrapper.setPlayerEventListener(new SimpleExoPlayerWrapper.PlayerEventListener() {
            @Override
            public void onRenderedFirstFrame() {
                if (mGalleryAdapter != null) {
                    mGalleryAdapter.updatePlayItem(mCurrentData);
                }
            }

            @Override
            public void onPlayCountChanged(int playCount) {

            }

            @Override
            public void onStateBuffering() {

            }

            @Override
            public void onStateReady() {

            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        clearDownloads();

        PhotoAlbumConstants.albumData = null;

        if (playerWrapper != null) {
            playerWrapper.destroyPlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mCurrentData && playerWrapper != null) {
            playerWrapper.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (playerWrapper != null) {
            playerWrapper.pause();
        }
    }

    /**
     * 跳转选择图片
     * jump to select the photos
     */
    private void gotoSelectPictures() {
        PhotoAlbumConstants.albumData = mCurrentData;
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_VIDEO_FROM_PHOTO_ALBUM);
        if (mCurrentData != null && !TextUtils.isEmpty(mCurrentData.photosAlbumReplaceMax) && !TextUtils.isEmpty(mCurrentData.photosAlbumReplaceMin)) {
            int iMax = Integer.valueOf(mCurrentData.photosAlbumReplaceMax);
            int iMin = Integer.valueOf(mCurrentData.photosAlbumReplaceMin);
            bundle.putInt(MediaConstant.LIMIT_COUNT_MAX, iMax);
            bundle.putInt(MediaConstant.LIMIT_COUNT_MIN, iMin);
        } else {
            bundle.putInt(MediaConstant.LIMIT_COUNT_MAX, 10);
            bundle.putInt(MediaConstant.LIMIT_COUNT_MIN, 1);
        }
        bundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.IMAGE);
        AppManager.getInstance().jumpActivity(PhotoAlbumActivity.this, MultiVideoSelectActivity.class, bundle);
    }

    /**
     * 下载指定的影集包
     * Download the specified album package
     *
     * @param uuid
     * @param packageUrl
     */
    private void downloadPackage(String uuid, final String packageUrl) {
        if (TextUtils.isEmpty(packageUrl)) {
            return;
        }
        downloadingURL.put(packageUrl, mDownloadPath + PathNameUtil.getPathNameWithSuffix(packageUrl));
        String[] split = packageUrl.split("/");
        HttpManager.download(packageUrl, packageUrl, mDownloadPath, split[split.length - 1], new SimpleDownListener(packageUrl) {
            @Override
            public void onError(Progress progress) {
                super.onError(progress);
                Logger.e(TAG, "downloadPackageOnError: " + progress.exception.toString());
                mDownloadDialog.dismiss();
                mDownloadDialog.setProgress(0);
                downloadingURL.remove(packageUrl);
                deleteFiles(mDownloadPath);
            }

            @Override
            public void onProgress(Progress progress) {
                super.onProgress(progress);
                mDownloadDialog.setProgress((int) (progress.fraction * 100));
            }

            @Override
            public void onFinish(File file, Progress progress) {
                super.onFinish(file, progress);
                mDownloadDialog.dismiss();
                mDownloadDialog.setProgress(0);
                downloadingURL.remove(packageUrl);
                PathUtils.unZipFile(file.getAbsolutePath(), mDownloadPath);
                String onePackageDir = FileUtils.getFileNameNoEx(file.getAbsolutePath());
                File onefile = new File(onePackageDir);
                mCurrentData.sourceDir = onefile.getAbsolutePath();
                mCurrentData.isLocal = true;
                mCurrentData.uuid = onefile.getName();
                getEachFileLocal(mCurrentData, onefile.listFiles());
                //这里在对应影集模板下保存个配置文件
                String configJson = GsonUtils.toJson(mCurrentData);
                FileIOUtils.writeFileFromString(onePackageDir + File.separator + CONFIG, configJson);
                deleteFiles(file.getAbsolutePath());
                if (mGalleryAdapter != null) {
                    mGalleryAdapter.updateItemData(mCurrentData);
                }
                gotoSelectPictures();
            }
        });
    }

    /**
     * 获取已下载的影集包数据
     * Get the downloaded album data
     */
    private void getLoadData() {
        if (mDownloadPath == null || mDownloadPath.isEmpty()) {
            return;
        }
        File file = new File(mDownloadPath);
        if (!file.exists()) {
            return;
        }
        File[] subFile = file.listFiles();
        if (subFile == null) {
            return;
        }

        mDataListLocal.clear();
        for (int i = 0; i < subFile.length; ++i) {
            File dir = subFile[i];
            if (!dir.isDirectory()) {
                continue;
            }
            if (dir.getName().contains("MACOSX")) {
                continue;
            }
            PhotoAlbumData oneItem = new PhotoAlbumData();
            oneItem.id = mDataListLocal.size();
            oneItem.sourceDir = dir.getAbsolutePath();
            oneItem.isLocal = true;
            oneItem.uuid = dir.getName();
            getEachFileLocal(oneItem, dir.listFiles());

            String configJson = FileIOUtils.readFile2String(dir.getAbsolutePath() + File.separator + CONFIG);
            if (!TextUtils.isEmpty(configJson)) {
                PhotoAlbumData tempData = GsonUtils.fromJson(configJson, PhotoAlbumData.class);
                if (null != tempData) {
                    oneItem.coverImageUrl = tempData.coverImageUrl;
                    oneItem.coverVideoUrl = tempData.coverVideoUrl;
                }
            }
            if (oneItem.isExist()) {
                mDataListLocal.add(oneItem);
            }
        }
        updateAdapter();
    }

    private void updateAdapter() {
        if (mGalleryAdapter != null) {
            mGalleryAdapter.setData(mDataListLocal);
            if (mGalleryAdapter.getCount() > 0) {
                mAlbumUseBtn.setVisibility(View.VISIBLE);
                initCardScale();
            }
        }
    }

    private void getEachFileLocal(PhotoAlbumData oneItem, File[] oneZip) {
        if (oneItem == null || oneZip == null) {
            return;
        }
        for (int k = 0; k < oneZip.length; ++k) {
            File hitFile = oneZip[k];
            String filePath = hitFile.getPath();
            String fileName = hitFile.getName();
            if (fileName.toLowerCase(Locale.US).endsWith("msphotoalbum")) {
                oneItem.filePath = filePath;
            } else if (fileName.toLowerCase(Locale.US).endsWith("lic")) {
                oneItem.licPath = filePath;
            }
            if (fileName.equals("cover.jpg")) {
                oneItem.coverImageUrl = filePath;
            } else if (fileName.equals("cover.mp4")) {
                oneItem.coverVideoUrl = filePath;
            } else if (fileName.equals("info.json")) {
                try {
                    String read_json = Util.loadFromSDFile(filePath);
                    JSONObject jsonObject = new JSONObject(read_json);
                    if (jsonObject != null) {
                        String sName = jsonObject.getString("photosAlbumName");
                        String sTips = jsonObject.getString("photosAlbumTips");
                        String sMax = jsonObject.getString("photosAlbumReplaceMax");
                        String sMin = jsonObject.getString("photosAlbumReplaceMin");
                        oneItem.photosAlbumName = parseStringContent(sName);
                        oneItem.photosAlbumTips = parseStringContent(sTips);
                        oneItem.photosAlbumReplaceMax = sMax;
                        oneItem.photosAlbumReplaceMin = sMin;
                    }
                } catch (Exception e) {
                    Log.d(TAG, "phase info.json exception!");
                }
            }
        }
    }

    private String parseStringContent(String strContent) {
        if (TextUtils.isEmpty(strContent)) {
            return "";
        }
        String[] parseStrArray = strContent.split("\\|");
        if (parseStrArray == null || parseStrArray.length <= 1) {
            return strContent;
        }
        boolean isChinese = SystemUtils.isZh(mContext);
        return isChinese ? parseStrArray[0] : parseStrArray[1];
    }

    private void getHttpData() {
        if (!NetWorkUtil.isNetworkConnected(this)) {
            return;
        }
        HttpManager.getMaterialList(null, AssetType.PHOTO_ALBUM_ALL, -1, NvAsset.AspectRatio_All, "",
                MSApplication.getSdkVersion(), 1, 1000, new RequestCallback<BaseBean<BaseDataBean<PhotoAlbumData>>>() {
                    @Override
                    public void onSuccess(BaseResponse<BaseBean<BaseDataBean<PhotoAlbumData>>> response) {
                        if (response != null && response.getData() != null) {
                            List<BaseDataBean<PhotoAlbumData>> elements = response.getData().getElements();
                            if (elements != null) {
                                for (BaseDataBean<PhotoAlbumData> element : elements) {
                                    if (element == null) {
                                        continue;
                                    }
                                    if (!isExitInLocalData(element)) {
                                        PhotoAlbumData oneItem = element.getInfoJson();
                                        if (oneItem != null) {
                                            oneItem.id = mDataListLocal.size();
                                            oneItem.uuid = element.getId();
                                            oneItem.photosAlbumName = MSApplication.isZh() ? element.getDisplayNamezhCN() : element.getDisplayName();
                                            oneItem.coverImageUrl = element.getCoverUrl();
                                            String albumTips = oneItem.photosAlbumTips;
                                            oneItem.photosAlbumTips = parseStringContent(albumTips);
                                            oneItem.coverVideoUrl = element.getPreviewVideoUrl();
                                            oneItem.packageUrl = element.getPackageUrl();
                                            oneItem.isLocal = false;
                                            oneItem.description = element.getDescription();
                                            mDataListLocal.add(oneItem);
                                        }
                                    }
                                    updateAdapter();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(BaseResponse<BaseBean<BaseDataBean<PhotoAlbumData>>> response) {
                        Log.e(TAG, "onError: get http photoalbum data error!");
                    }
                });
    }

    private boolean isExitInLocalData(BaseDataBean<PhotoAlbumData> dataBean) {
        String uuid = dataBean.getId();
        if (mDataListLocal == null || mDataListLocal.isEmpty() || TextUtils.isEmpty(uuid)) {
            return false;
        }
        for (PhotoAlbumData photoAlbumData : mDataListLocal) {
            if (photoAlbumData == null) {
                continue;
            }
            if (TextUtils.equals(photoAlbumData.getUuid(), uuid)) {
                photoAlbumData.description = dataBean.getDescription();
                return true;
            }
        }
        return false;
    }

    //本地是否存在
    //isExistInLocal
    private boolean isExistInLocal(String id) {
        for (PhotoAlbumData photoAlbumData : mDataListLocal) {
            if (photoAlbumData == null) {
                continue;
            }
            if (photoAlbumData.sourceDir != null && photoAlbumData.sourceDir.endsWith(id)) {
                return true;
            }
        }
        return false;
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mAlbumRv.setLayoutManager(linearLayoutManager);
        mGalleryAdapter = new GalleryAdapter(this, mAlbumRv);
        mAlbumRv.setAdapter(mGalleryAdapter);

        mGalleryAdapter.setOnGrallyItemSelectListener(new GalleryAdapter.OnGrallyItemSelectListener() {
            @Override
            public void onItemSelect(int pos, PhotoAlbumData itemData, PlayerView view) {
                if (itemData == null || view == null) {
                    return;
                }
                Log.e(TAG, "onItemSelect: " + pos + " isLocal: " + itemData.isLocal);
                mCurrentData = itemData;
                view.setPlayer(playerWrapper.getPlayer());
                playerWrapper.resetPlayer(itemData.coverVideoUrl);
//                playerWrapper.seekTo(0);
                playerWrapper.start();

                mAlbumNameText.setText(itemData.photosAlbumName);
                mAlbumTipsText.setText(itemData.description);
                String sIndex = (pos + 1) + "/" + mDataListLocal.size();
                mAlbumIndexText.setText(sIndex);
            }
        });
    }

    private void initCardScale() {
        /*
         * mRecyclerView绑定scale效果
         * mRecyclerView bound scale effect
         * */
        mCardScaleHelper = new CardScaleHelper();

        mCardScaleHelper.setCurrentItemPos(0);

        mCardScaleHelper.setOnGrallyItemSelectListener(new CardScaleHelper.OnGrallyItemSelectListener() {
            @Override
            public void onItemSelect(int pos) {

            }

            @Override
            public void onScrolling() {

            }
        });
        mCardScaleHelper.attachToRecyclerView(mAlbumRv);
    }

    private void deleteFiles(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    private void clearDownloads() {
        cancelDownloads();
        for (String filePath : downloadingURL.values()) {
            deleteFiles(filePath);
        }
        downloadingURL.clear();
    }

    private void cancelDownloads() {
        for (String url : downloadingURL.keySet()) {
            OkHttpClientManager.cancelTag(url);
        }
    }
}
