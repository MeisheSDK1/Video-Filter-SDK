package com.meishe.sdkdemo.capturescene.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsCaptureVideoFx;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.base.msbus.MSBus;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.GsonUtils;
import com.meishe.net.custom.SimpleDownListener;
import com.meishe.net.model.Progress;
import com.meishe.net.temp.TempStringCallBack;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.capturescene.adapter.CaptureSceneAdapter;
import com.meishe.sdkdemo.capturescene.data.CaptureSceneOnlineData;
import com.meishe.sdkdemo.capturescene.data.Constants;
import com.meishe.sdkdemo.capturescene.httputils.NetWorkUtil;
import com.meishe.sdkdemo.capturescene.interfaces.OnItemClickListener;
import com.meishe.sdkdemo.edit.watermark.SingleClickActivity;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.utils.PackageManagerUtil;
import com.meishe.utils.PathNameUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.meishe.net.utils.HttpUtils.runOnUiThread;
import static com.meishe.sdkdemo.capturescene.data.Constants.CAPTURE_SCENE_LOCAL;
import static com.meishe.sdkdemo.capturescene.data.Constants.CAPTURE_SCENE_ONLINE;
import static com.meishe.sdkdemo.capturescene.data.Constants.RESOURCE_NEW_PATH;
import static com.meishe.sdkdemo.utils.Constants.SubscribeType.SUB_CAPTURE_SCENE_ITEM_CLICK_TYPE;
import static com.meishe.sdkdemo.utils.Constants.SubscribeType.SUB_CAPTURE_SCENE_PACKAGE;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2022/1/18 13:09
 * @Description :
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class CaptureSceneEffectView extends LinearLayout {
    private static final String TAG = "CaptureSceneEffectView";
    private final LinkedList<CaptureSceneOnlineData.CaptureSceneDetails> captureSceneDetails = new LinkedList<>();
    private CaptureSceneAdapter captureSceneAdapter;
    private RecyclerView recyclerView;
    private TextView csTextReset;
    private Context mContext;
    private Map<String, String> downloadingURL = new HashMap<>();
    private NvsStreamingContext mStreamingContext;
    private String mUrl;
    private int mType;
    private ImageView mIvAddResource;
    private Activity mActivity;

    public CaptureSceneEffectView(Context context) {
        this(context, null);
    }

    public CaptureSceneEffectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptureSceneEffectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_capture_scene_layout, this, true);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        csTextReset = (TextView) findViewById(R.id.tv_text_reset);
        mIvAddResource = (ImageView) findViewById(R.id.iv_add_resource);
        initListener();
    }

    private void initListener() {
        csTextReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCaptureScene();
            }
        });

        mIvAddResource.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle makeCoverBundle = new Bundle();
                makeCoverBundle.putInt(com.meishe.sdkdemo.utils.Constants.SELECT_MEDIA_FROM, com.meishe.sdkdemo.utils.Constants.SELECT_PICTURE_FROM_BACKGROUND_SEG);
                AppManager.getInstance().jumpActivityForResult(mActivity, SingleClickActivity.class, makeCoverBundle, 100);

            }
        });

    }

    public void setStreamingContext(NvsStreamingContext streamingContext, Activity activity, int type) {
        mType = type;
        mActivity = activity;
        mStreamingContext = streamingContext;
        if (type == Constants.CAPTURE_SCENE_TYPE_IMAGE) {
            mUrl = Constants.CAPTURE_SCENE_PATH_IMAGE;
            mIvAddResource.setVisibility(VISIBLE);
        } else {
            mUrl = Constants.CAPTURE_SCENE_PATH_VIDEO;
            mIvAddResource.setVisibility(GONE);
        }
        initData();
    }

    private void initData() {
        captureSceneAdapter = new CaptureSceneAdapter(mContext,
                captureSceneDetails, new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                CaptureSceneOnlineData.CaptureSceneDetails captureSceneDetails =
                        (CaptureSceneOnlineData.CaptureSceneDetails) view.getTag();

                int captureVideoFxCount = mStreamingContext.getCaptureVideoFxCount();
                for (int i = 0; i < captureVideoFxCount; i++) {
                    NvsCaptureVideoFx captureVideoFxByIndex = mStreamingContext.getCaptureVideoFxByIndex(i);
                    if (captureVideoFxByIndex.getBuiltinCaptureVideoFxName().equals("Storyboard")) {
                        mStreamingContext.removeCaptureVideoFx(i);
                        break;
                    }
                }
                MSBus.getInstance().post(SUB_CAPTURE_SCENE_PACKAGE, captureSceneDetails.getPackageUrl());
                if (mType == Constants.CAPTURE_SCENE_TYPE_IMAGE) {
                    MSBus.getInstance().post(SUB_CAPTURE_SCENE_ITEM_CLICK_TYPE, captureSceneDetails.getPackageUrl());
                    return;
                }

                if (!downloadingURL.containsKey(captureSceneDetails.getPackageUrl()) &&
                        captureSceneDetails.getType() == CAPTURE_SCENE_ONLINE) {
                    CircleBarView circleBarView = (CircleBarView) view.findViewById(R.id.item_cs_download);
                    downloadingURL.put(captureSceneDetails.getPackageUrl(), "");
                    downloadImage(captureSceneDetails.getCoverUrl(), position);
                    downloadPackage(captureSceneDetails.getPackageUrl(), circleBarView, position);
                } else {
                    setCaptureSceneByPath(captureSceneDetails.getId(), captureSceneDetails.getPackageUrl());
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(captureSceneAdapter);

        if (mType == Constants.CAPTURE_SCENE_TYPE_IMAGE) {
            String picSource1 = "/capturescene/7F593D6D-94D2-4B0D-B7A5-D95F1F0B87C3.jpg";
            String picSource2 = "/capturescene/0514EF60-A007-45BD-8B87-7933575769CB.jpg";

            CaptureSceneOnlineData.CaptureSceneDetails captureSceneDetail = new CaptureSceneOnlineData.CaptureSceneDetails();
            captureSceneDetail.setCoverUrl("file:///android_asset" + picSource1);
            captureSceneDetail.setPackageUrl("assets:" + picSource1);
            captureSceneDetails.add(captureSceneDetail);

            captureSceneDetail = new CaptureSceneOnlineData.CaptureSceneDetails();
            captureSceneDetail.setCoverUrl("file:///android_asset" + picSource2);
            captureSceneDetail.setPackageUrl("assets:" + picSource2);
            captureSceneDetails.add(captureSceneDetail);

            captureSceneAdapter.setDataList(captureSceneDetails);
        } else {
            NvAssetManager mAssetManager = NvAssetManager.sharedInstance();
            mAssetManager.searchLocalAssets(NvAsset.ASSET_CAPTURE_SCENE);
            ArrayList<NvAsset> loclList = mAssetManager.getUsableAssets(NvAsset.ASSET_CAPTURE_SCENE, NvAsset.AspectRatio_All, 0);
            if (NetWorkUtil.isNetworkConnected(mContext)) {
                /*
                 * 有权限，则删除本地拍摄的视频文件
                 * Have permission to delete locally captured video files
                 * */
                HttpManager.getOldObjectGet(mUrl, new TempStringCallBack() {
                    @Override
                    public void onResponse(final String stringResponse) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CaptureSceneOnlineData onlineData = GsonUtils.fromJson(stringResponse, CaptureSceneOnlineData.class);
                                if (onlineData == null) {
                                    return;
                                }
                                List<CaptureSceneOnlineData.CaptureSceneDetails> captureSceneDetailsList = onlineData.getData().getElements();
                                int oldSize = captureSceneDetails.size();
                                for (CaptureSceneOnlineData.CaptureSceneDetails sceneDetail : captureSceneDetailsList) {
                                    sceneDetail.setType(CAPTURE_SCENE_ONLINE);
                                    sceneDetail.setCoverUrl(sceneDetail.getCoverUrl().replace(Constants.RESOURCE_OLD_PATH, RESOURCE_NEW_PATH));
                                    String packageUrl = sceneDetail.getPackageUrl();
                                    sceneDetail.setPackageUrl(packageUrl.replaceAll(Constants.RESOURCE_OLD_PATH, RESOURCE_NEW_PATH));
                                    checkIsInstall(sceneDetail);

                                    if (!captureSceneDetails.contains(sceneDetail)) {
                                        for (NvAsset nvAsset : loclList) {
                                            if (Objects.equals(nvAsset.uuid, sceneDetail.getId())) {
                                                sceneDetail.setType(CAPTURE_SCENE_LOCAL);
                                                sceneDetail.setCoverUrl(com.meishe.utils.PathNameUtil.getOutOfPathSuffixWithOutPoint(nvAsset.localDirPath) + ".jpg");
                                                sceneDetail.setPackageUrl(nvAsset.localDirPath);
                                                break;
                                            }
                                        }
                                        captureSceneDetails.add(sceneDetail);
                                    }
                                }
                                if (oldSize != captureSceneDetails.size()) {
                                    captureSceneAdapter.setDataList(captureSceneDetails);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
            }
        }


    }

    private static void checkIsInstall(CaptureSceneOnlineData.CaptureSceneDetails captureSceneDetails) {
        //检查是否已下载
        String path = PathUtils.getCaptureSceneVideoLocalFilePath();
        String packageUrl = captureSceneDetails.getPackageUrl();
        String nameFromUrl = packageUrl.substring(packageUrl.lastIndexOf("/") + 1);
        File file = new File(path);
        boolean hasLocal = false;
        if (file.exists() && file.isDirectory()) {
            String[] strings = file.list();
            if (strings != null && strings.length != 0) {
                for (String string : strings) {
                    if (string.equals(nameFromUrl)) {
                        hasLocal = true;
                    }
                }
            }
        }
        if (hasLocal) {
            captureSceneDetails.setPackageUrl(path + File.separator + nameFromUrl);
            captureSceneDetails.setType(CAPTURE_SCENE_LOCAL);
        }
    }

    private void setCaptureSceneByPath(String sceneId, String scenePackageFilePath) {
        /*
         * 检查改拍摄场景的资源包是否已经安装
         * Check if the resource pack for the shooting scene has been installed
         * */
//        NvsAssetPackageManager assetPackageManager = mStreamingContext.getAssetPackageManager();
//        int packageStatus = assetPackageManager.getAssetPackageStatus(sceneId, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTURESCENE);
//        if (packageStatus == NvsAssetPackageManager.ASSET_PACKAGE_STATUS_NOTINSTALLED) {
//            /*
//             * 该拍摄场景的资源包尚未安装，则安装该资源包，由于拍摄场景的资源包尺寸较大，为了不freeze UI，我们采用异步安装模式
//             * If the resource pack of the shooting scene has not been installed, install the resource pack. Due to the large size of the resource pack of the shooting scene, in order not to freeze the UI, we use an asynchronous installation mode
//             * */
//            assetPackageManager.installAssetPackage(scenePackageFilePath, null, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTURESCENE, false, null);
//        } else {
//            /*
//             * 若拍摄场景的资源包已经安装，应用其效果
//             * If the resource pack for the shooting scene is already installed, apply its effects
//             * */
//            mStreamingContext.applyCaptureScene(sceneId);
//        }

        PackageManagerUtil.installAssetPackage(scenePackageFilePath, null, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTURESCENE);
        mStreamingContext.applyCaptureScene(sceneId);
    }


    private void downloadImage(String coverUrl, final int position) {
        if (!coverUrl.startsWith("http")) {
            return;
        }
        String path = "";
        if (mType == Constants.CAPTURE_SCENE_TYPE_IMAGE) {
            path = PathUtils.getCaptureSceneImageLocalFilePath();
        } else {
            path = PathUtils.getCaptureSceneVideoLocalFilePath();
        }

        String[] split = coverUrl.split("/");
        HttpManager.download(coverUrl, coverUrl, path, split[split.length - 1], new SimpleDownListener(coverUrl) {
            @Override
            public void onFinish(File file, Progress progress) {
                super.onFinish(file, progress);
                CaptureSceneOnlineData.CaptureSceneDetails data = captureSceneDetails.get(position);
                data.setCoverUrl(file.getAbsolutePath());
                captureSceneAdapter.setDataList(position, data, false);
            }
        });
    }

    private void downloadPackage(final String packageUrl, final CircleBarView circleBarView, final int position) {
        if (!packageUrl.startsWith("http")) {
            return;
        }
        final String path = PathUtils.getCaptureSceneVideoLocalFilePath();
        String[] split = packageUrl.split("/");
        HttpManager.download(packageUrl, packageUrl, path, split[split.length - 1], new SimpleDownListener(packageUrl) {
            @Override
            public void onProgress(Progress progress) {
                super.onProgress(progress);
                captureSceneAdapter.setmProgress((int) (progress.fraction * 100), position);

            }

            @Override
            public void onError(Progress progress) {
                super.onError(progress);
                downloadingURL.remove(packageUrl);
                deleteFiles(path);
            }

            @Override
            public void onFinish(File file, Progress progress) {
                super.onFinish(file, progress);
                circleBarView.setVisibility(View.GONE);
                downloadingURL.remove(packageUrl);
                CaptureSceneOnlineData.CaptureSceneDetails data = captureSceneDetails.get(position);
                data.setPackageUrl(file.getAbsolutePath());
                data.setType(CAPTURE_SCENE_LOCAL);
                captureSceneAdapter.setDataList(position, data, true);
                if (captureSceneAdapter.getSelectPosition() == position) {
                    setCaptureSceneByPath(PathNameUtil.getPathNameNoSuffix(file.getAbsolutePath()), file.getAbsolutePath());
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

    public void selectPosition(String url) {
        if (null == captureSceneAdapter || CommonUtils.isEmpty(captureSceneDetails)) {
            return;
        }
        boolean exist = false;
        for (CaptureSceneOnlineData.CaptureSceneDetails details : captureSceneDetails) {
            if (TextUtils.equals(details.getPackageUrl(), url)) {
                exist = true;
                break;
            }
        }
        if (!exist) {
            captureSceneAdapter.setSelectPosition(-1);
        }

    }


    public void clearCaptureScene() {
        mStreamingContext.removeCurrentCaptureScene();
        mStreamingContext.removeAllCaptureVideoFx();
        captureSceneAdapter.setSelectPosition(-1);
    }

    public void addLocalResource(String filePath) {
        if (mType == Constants.CAPTURE_SCENE_TYPE_IMAGE) {
            CaptureSceneOnlineData.CaptureSceneDetails captureSceneDetail = new CaptureSceneOnlineData.CaptureSceneDetails();
            captureSceneDetail.setCoverUrl(filePath);
            captureSceneDetail.setPackageUrl(filePath);
            captureSceneDetails.add(0, captureSceneDetail);
            captureSceneAdapter.setDataList(captureSceneDetails);
            captureSceneAdapter.setSelectPosition(0);
            MSBus.getInstance().post(SUB_CAPTURE_SCENE_ITEM_CLICK_TYPE, captureSceneDetail.getPackageUrl());
        }
    }

    public void onDestroy() {
        for (String url : downloadingURL.keySet()) {
            HttpManager.cancelRequest(url);
        }
        for (String filePath : downloadingURL.values()) {
            deleteFiles(filePath);
            deleteFiles(PathNameUtil.getOutOfPathSuffix(filePath) + "png");
        }
        downloadingURL.clear();
    }
}
