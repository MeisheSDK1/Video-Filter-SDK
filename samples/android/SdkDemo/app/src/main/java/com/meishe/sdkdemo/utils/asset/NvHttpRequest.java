package com.meishe.sdkdemo.utils.asset;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.meishe.base.utils.ThreadUtils;
import com.meishe.base.utils.ZipUtils;
import com.meishe.net.custom.BaseResponse;
import com.meishe.net.custom.RequestCallback;
import com.meishe.net.custom.SimpleDownListener;
import com.meishe.net.model.Progress;
import com.meishe.http.bean.BaseBean;
import com.meishe.http.bean.BaseDataBean;
import com.meishe.http.AssetType;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.utils.PackageManagerUtil;
import com.meishe.utils.PathNameUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * Created by shizhouhu on 2018/6/14.
 */

public class NvHttpRequest {
    private static final String TAG = "NvHttpRequest ";
    public static final int NONETWORK = 0;
    /**
     * wifi连接
     * wifi connection
     */
    public static final int WIFI = 1;
    /**
     * 移动网络
     * mobile network
     */
    public static final int NOWIFI = 2;

    /**
     * 检验网络连接类型，判断是否是wifi连接
     * Check the network connection type to determine whether it is a wifi connection
     *
     * @param context
     * @return the network connection type
     */
    public static int checkNetWorkType(Context context) {

        if (!checkNetWork(context)) {
            return NvHttpRequest.NONETWORK;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            return NvHttpRequest.WIFI;
        else
            return NvHttpRequest.NOWIFI;
    }

    /**
     * 检测网络是否连接
     * <p>
     * Check if the network is connected
     *
     * @param context
     * @return
     */
    public static boolean checkNetWork(Context context) {
        /*
         * 获得连接设备管理器
         * Get Connected Device Manager
         * */
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return (ni != null && ni.isAvailable());
    }

    private static final Gson m_gson = new Gson();
    private static NvHttpRequest m_instance = null;

    public static NvHttpRequest sharedInstance() {
        if (m_instance == null)
            m_instance = new NvHttpRequest();
        return m_instance;
    }

    private NvHttpRequest() {
    }

    public void getAssetList(final String searchKey, final int assetType,int ratioFlag, int aspectRatio,
                             int categoryId, int page, int pageSize, String sdkVersion,
                             final NvHttpRequestListener requestListener) {
        final AssetType finalType = NvAssetUtils.getAssetType(assetType, categoryId);
        if(finalType==null)return;
        final int total = page * pageSize;
        HttpManager.getMaterialList(null, finalType, ratioFlag, aspectRatio, searchKey,
                sdkVersion, page, pageSize, new RequestCallback<BaseBean<BaseDataBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<BaseBean<BaseDataBean>> response) {
                        if (response != null && response.getCode() == 1 && response.getData() != null) {
                            BaseBean<BaseDataBean> data = response.getData();
                            if (requestListener != null) {
                                requestListener.onGetAssetListSuccess(data.getElements(), assetType,
                                        total < data.getTotal(), searchKey);
                            }
                        }else {
                            if (requestListener != null) {
                                requestListener.onGetAssetListFailed(null, assetType);
                            }
                        }
                    }

                    @Override
                    public void onError(BaseResponse<BaseBean<BaseDataBean>> response) {
                        if (requestListener != null) {
                            requestListener.onGetAssetListFailed(new Throwable(response.getMessage()),
                                    assetType);
                        }
                    }
                });
    }

    public void getFontDataList(int page, int pageSize, String sdkVersion,
                                final NvHttpRequestListener requestListener) {
        final int total = page * pageSize;
        String searchKey = "";
        int assetType = 0;
        HttpManager.getFontDataList("getFontList",sdkVersion, page, pageSize, new RequestCallback<BaseBean<BaseDataBean>>() {
            @Override
            public void onSuccess(BaseResponse<BaseBean<BaseDataBean>> response) {
                if (response != null && response.getCode() == 1 && response.getData() != null) {
                    BaseBean<BaseDataBean> data = response.getData();
                    if (requestListener != null) {
                        requestListener.onGetAssetListSuccess(data.getElements(), assetType,
                                total < data.getTotal(), searchKey);
                    }
                }else {
                    if (requestListener != null) {
                        requestListener.onGetAssetListFailed(null, assetType);
                    }
                }
            }

            @Override
            public void onError(BaseResponse<BaseBean<BaseDataBean>> response) {
                if (requestListener != null) {
                    requestListener.onGetAssetListFailed(new Throwable(response.getMessage()),
                            assetType);
                }
            }
        });
    }

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    public void downloadAsset(String srcFileUrl, final String assetDownloadDir, String desFilePath,
                              final String assetSuffixName, NvHttpRequestListener downloadListener,
                              int assetType, String downloadId) {
        final NvHttpRequestListener localDownloadListener = downloadListener;
        final String desFileUrl = desFilePath;
        final int localAssetType = assetType;
        final String localDownloadId = downloadId;
        String[] split = srcFileUrl.split("/");
        Log.e(TAG, "downloadAsset srcFileUrl: "+ srcFileUrl+" assetDownloadDir: "+assetDownloadDir
                +" desFileUrl: "+desFileUrl);
        HttpManager.download(srcFileUrl, srcFileUrl, assetDownloadDir, split[split.length-1], new SimpleDownListener(srcFileUrl) {
            @Override
            public void onProgress(Progress progress) {
                super.onProgress(progress);
                if (localDownloadListener != null) {
                    localDownloadListener.onDonwloadAssetProgress((int) (progress.fraction * 100), localAssetType, localDownloadId);
                }
            }

            @Override
            public void onFinish(File newFile, Progress progress) {
                super.onFinish(newFile, progress);
                ThreadUtils.getIoPool().execute(() -> {
                    String packagePath = newFile.getAbsolutePath();
                    if (ZipUtils.isZipFile(packagePath)) {
                        String parentPath = PathNameUtil.getOutOfPathSuffixWithOutPoint(packagePath);
                        Log.e(TAG, "onFinish parentPath: " + parentPath);
                        try {
                            ZipUtils.unzipFile(packagePath, parentPath);
                            StringBuilder lic = new StringBuilder();
                            StringBuilder packageB = new StringBuilder();
                            PackageManagerUtil.getPackAndLicPathFromFileDirectory(parentPath, lic, packageB);
                            packagePath = packageB.toString();
                        } catch (IOException e) {
                            Log.e(TAG, "onFinish unzipFile error! IOException: " + e);
                        }
                    }
                    String finalPackagePath = packagePath;
                    ThreadUtils.runOnUiThread(() -> {
                        if (localDownloadListener != null) {
                            localDownloadListener.onDonwloadAssetSuccess(true, finalPackagePath, localAssetType, localDownloadId);
                        }
                    });
                });
            }

            @Override
            public void onError(Progress progress) {
                super.onError(progress);
                File localFile = new File(desFileUrl);
                if (localFile.exists()) {
                    localFile.delete();
                }
                if (localDownloadListener != null) {
                    localDownloadListener.onDonwloadAssetFailed(progress.exception, localAssetType, localDownloadId);
                }
            }
        });
    }

    public interface NvHttpRequestListener {
        void onGetAssetListSuccess(List responseArrayList, int assetType, boolean hasNext, String searchKey);

        void onGetAssetListFailed(Throwable e, int assetType);

        void onDonwloadAssetProgress(int progress, int assetType, String downloadId);

        void onDonwloadAssetSuccess(boolean success, String downloadPath, int assetType, String downloadId);

        void onDonwloadAssetFailed(Throwable e, int assetType, String downloadId);
    }
}
