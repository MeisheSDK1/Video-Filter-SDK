package com.meishe.sdkdemo.themeshoot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.meishe.http.AssetType;
import com.meishe.http.bean.BaseBean;
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
import com.meishe.sdkdemo.download.SquareDecoration;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.edit.view.dialog.DownloadDialog;
import com.meishe.sdkdemo.themeshoot.model.ThemeModel;
import com.meishe.sdkdemo.themeshoot.utlils.ThemeShootUtil;
import com.meishe.sdkdemo.themeshoot.view.ThemeCapturePreviewView;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.FileUtils;
import com.meishe.sdkdemo.utils.Logger;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.utils.PathNameUtil;
import com.meishe.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2020/7/31.
 * @Description :主题拍摄选择页。Theme shooting selection page
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ThemeSelectActivity extends BaseActivity {
    private final String TAG = getClass().getName();
    private CustomTitleBar mEditCustomTitleBar;
    private RecyclerView mRecyclerView;
    //    private AssetDownloadListAdapter mAssetListAdapter;
    private ThemeListAdapt mThemeListAdapt;
    private ArrayList<NvAsset> mAssetDatalist = new ArrayList<>();
    private AlertDialog mAlertDialog;
    private ThemeCapturePreviewView mThemeCapturePreviewView;
    private List<ThemeModel> mThemeModelList;
    //    private final String URL = "https://vsapi.meishesdk.com/app/index.php?command=listVlogMaterial&page=0&pageSize=1000";
    private String mDownloadPath;
    private DownloadDialog mDownloadDialog;
    private ThemeModel mThemeData;

    @Override
    protected int initRootView() {
        return R.layout.activity_theme_preview;
    }

    @Override
    protected void initViews() {
        mEditCustomTitleBar = (CustomTitleBar) findViewById(R.id.title);
        mRecyclerView = findViewById(R.id.theme_template_list);
        mThemeListAdapt = new ThemeListAdapt(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.addItemDecoration(new SquareDecoration());
        mRecyclerView.setAdapter(mThemeListAdapt);
        mThemeListAdapt.setOnItemClickListener(new ThemeListAdapt.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mThemeData = mThemeModelList.get(position);
                if (mThemeData.isLocal()) {
                    showPreviewSetPage(mThemeData);
                } else {
                    downloadPackage(mThemeData.getDownLoadPackageUrl());
                }
            }
        });
        initPreviewDialog();
        getLocalData();
        getHttpData();
        registerFont();
    }

    private void registerFont(){
        mStreamingContext.registerFontByFilePath("assets:/font/theme/Muyao-Softbrush-2.ttf");
        mStreamingContext.registerFontByFilePath("assets:/font/theme/庞门正道粗书体6.0.ttf");
    }

    private void showPreviewSetPage(ThemeModel themeModel) {
        mThemeCapturePreviewView = new ThemeCapturePreviewView(this);
        mThemeCapturePreviewView.setOnThemePreviewOperationListener(new ThemeCapturePreviewView.OnThemePreviewOperationListener() {
            @Override
            public void onPreviewClosed() {
                closeDialogView(mAlertDialog);
            }

            @Override
            public void onEnterButtonPressed(int ratioType) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("ThemeModel", mThemeData);
                bundle.putSerializable("ratioType", ratioType);
                AppManager.getInstance().jumpActivity(AppManager.getInstance().currentActivity(), ThemeCaptureActivity.class, bundle);
            }
        });
        mThemeCapturePreviewView.updateThemeModelView(themeModel);
        showPreviewPage(mAlertDialog, mThemeCapturePreviewView);
    }

    /**
     * 本地获取资源
     */
    private void getLocalData() {
        mThemeModelList = ThemeShootUtil.getThemeModelListFromSdCard(this);
        if (mThemeModelList == null) {
            mThemeModelList = new ArrayList<>();
        }
    }

    /**
     * 获取网络资源
     */
    private void getHttpData() {
        if (!NetWorkUtil.isNetworkConnected(this)) {
            ToastUtil.showToast(this, R.string.network_not_available);
            return;
        }
        HttpManager.getMaterialList(null, AssetType.CAPTURE_TEMPLATE_ALL, -1, NvAsset.AspectRatio_All, "", MSApplication.getSdkVersion()
                , 1, 1000, new RequestCallback<BaseBean<BaseDataBean<ThemeModel>>>() {
                    @Override
                    public void onSuccess(BaseResponse<BaseBean<BaseDataBean<ThemeModel>>> response) {
                        Log.e(this.getClass().getName(), "getHttpData result= ok");
                        // 网络资源尚未转义，转义完刷新数据 以服务器为准
                        //The network resource has not been escaped,
                        // the data will be refreshed after escaping, the server shall prevail
                        if (response != null && response.getData() != null) {
                            List<BaseDataBean<ThemeModel>> onlineDetails = response.getData().getElements();
                            // 以服务器为准
                            // based on the server data
                            compareLocalAndOnlineData(onlineDetails);
                        }
                        refreshData();
                    }

                    @Override
                    public void onError(BaseResponse<BaseBean<BaseDataBean<ThemeModel>>> response) {
                        Log.e(this.getClass().getName() + "getHttpData", "onError: get http theme data error!");
                    }
                });
    }

    /**
     * 比较服务器与本地数据，以服务器数据为准，显示出来
     * Compare server and local data, and display it based on server data
     *
     * @param onlineDetails
     */
    private void compareLocalAndOnlineData(List<BaseDataBean<ThemeModel>> onlineDetails) {
        if (onlineDetails == null) {
            return;
        }
        for (int i = 0; i < onlineDetails.size(); i++) {
            BaseDataBean<ThemeModel> detail = onlineDetails.get(i);
            if (!isLocalHas(detail.getId())) {
                ThemeModel themeModel = detail.getInfoJson();
                if (themeModel == null) {
                    themeModel = new ThemeModel();
                }
                themeModel.setPreview(detail.getPreviewVideoUrl());
                themeModel.setCover(detail.getCoverUrl());
                themeModel.setLocal(false);
                themeModel.setDownLoadPackageUrl(detail.getPackageUrl());
                themeModel.setName(detail.getDisplayName());
                mThemeModelList.add(themeModel);
            }
        }
    }

    /**
     * 通过json获取themeModel
     * Get themeModel through json
     *
     * @param infoJson
     * @return
     */
    private ThemeModel getOnlineJsonModel(String infoJson) {
        if (TextUtils.isEmpty(infoJson)) {
            return null;
        }
        try {
            ThemeModel jsonModel = new Gson().fromJson(infoJson, ThemeModel.class);
            return jsonModel;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断本地是否有线上item
     * Determine whether the item is wired locally
     *
     * @param sign
     * @return
     */
    private boolean isLocalHas(String sign) {
        if (mThemeModelList == null || TextUtils.isEmpty(sign)) {
            return false;
        }
        for (ThemeModel themeModel : mThemeModelList) {
            if (sign.equals(themeModel.getId())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 记录下载地址的数组，维护下载的完整性。
     * An array of download addresses to maintain download integrity.
     */
    private Map<String, String> downloadingURL = new HashMap<>();

    /**
     * 下载网络包数据
     * Download network packet data
     *
     * @param packageUrl
     */
    private void downloadPackage(final String packageUrl) {
        mDownloadDialog.show();
        downloadingURL.put(packageUrl, mDownloadPath + PathNameUtil.getPathNameWithSuffix(packageUrl));
        OkHttpClientManager.downloadAsyn(packageUrl, mDownloadPath, new DownLoadResultCallBack<String>() {
            @Override
            public void onProgress(long now, long total, int progress) {
                mDownloadDialog.setProgress(progress);
            }

            @Override
            public void onError(Request request, Exception e) {
                super.onError(request, e);
                Logger.e(TAG, "downloadPackageOnError: " + e.toString());
                mDownloadDialog.dismiss();
                mDownloadDialog.setProgress(0);
                downloadingURL.remove(packageUrl);
                deleteFiles(mDownloadPath);
            }

            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                mDownloadDialog.dismiss();
                mDownloadDialog.setProgress(0);
                downloadingURL.remove(packageUrl);
                PathUtils.unZipFile(response, mDownloadPath);
                deleteFiles(response);
                ThemeShootUtil.refreshThemeData(mThemeData, ThemeShootUtil.getLocalThemeModelByPath(FileUtils.getFileNameNoEx(response)));
                refreshData();
//                showPreviewSetPage(mThemeData);
            }
        });
    }

    private void deleteFiles(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    private void refreshData() {
        mThemeListAdapt.setAssetDatalist(mThemeModelList);
        mThemeListAdapt.notifyDataSetChanged();
    }

    @Override
    protected void initTitle() {
        mEditCustomTitleBar.setTextCenter(getResources().getString(R.string.theme_shoot));
    }

    @Override
    protected void initData() {
        mDownloadPath = ThemeShootUtil.getThemeSDPath(this) + File.separator;
        if (mThemeModelList != null) {
            Log.d("TAG", "initData: =================themeModelList=" + mThemeModelList.size());
            mThemeListAdapt.setAssetDatalist(mThemeModelList);
            mThemeListAdapt.notifyDataSetChanged();
        }
        mDownloadDialog = new DownloadDialog(this);

        mDownloadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDownloadDialog.setProgress(0);
                cancelDownloads();
            }
        });
    }

    private void cancelDownloads() {
        for (String url : downloadingURL.keySet()) {
            OkHttpClientManager.cancelTag(url);
        }
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onClick(View v) {

    }

    private void initPreviewDialog() {
        mAlertDialog = new AlertDialog.Builder(this).create();
        mAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                closeDialogView(mAlertDialog);
            }
        });
    }

    private void showPreviewPage(AlertDialog dialog, View view) {
        if (view == null || view == null) {
            return;
        }
        dialog.show();
        dialog.setContentView(view);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (mThemeCapturePreviewView != null) {
                    mThemeCapturePreviewView.clear();
                    mThemeCapturePreviewView = null;
                }
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        params.dimAmount = 0.0f;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.colorTranslucent));
        dialog.getWindow().setWindowAnimations(R.style.fx_dlg_fading_style);
//        dialog.getWindow().setWindowAnimations(R.style.fx_dlg_style);
    }

    private void closeDialogView(AlertDialog dialog) {
        if (dialog == null) {
            return;
        }
        dialog.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mStreamingContext != null) {
            mStreamingContext.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mThemeCapturePreviewView != null) {
            mThemeCapturePreviewView.onResume();
        }
    }
}
