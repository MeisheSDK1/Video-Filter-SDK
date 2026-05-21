package com.meishe.sdkdemo.presenter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.meicam.sdk.NvsStreamingContext;
import com.meishe.base.model.Presenter;
import com.meishe.base.utils.GsonUtils;
import com.meishe.http.HttpConstants;
import com.meishe.net.temp.TempStringCallBack;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.activity.view.MainView;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.capturescene.httputils.NetWorkUtil;
import com.meishe.sdkdemo.dialog.PrivacyPolicyDialog;
import com.meishe.sdkdemo.main.MainViewPagerFragment;
import com.meishe.sdkdemo.main.MainViewPagerFragmentData;
import com.meishe.sdkdemo.main.bean.AdBeansFormUrl;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.FileUtils;
import com.meishe.sdkdemo.utils.ParameterSettingValues;
import com.meishe.sdkdemo.utils.PathUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/11/4 16:32
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class MainPresenter extends Presenter<MainView> {
    private static final String TAG = "MainActivity";
    private static final int SPANCOUNT = 8;

    /**
     * 不允许开启开发者选项中的不保留活动，SDK中有些类是单例，这个功能会影响正常使用。
     * The unreserved activity in the developer option is not allowed to be enabled. Some classes in the SDK are singletons. This feature can interfere with normal use.
     *
     * @param activity activity
     */
    public void setDeveloperOptions(Activity activity) {
        if (null == activity) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            int alwaysFinish = Settings.Global.getInt(activity.getContentResolver(), Settings.Global.ALWAYS_FINISH_ACTIVITIES, 0);
            if (alwaysFinish == 1) {
                Dialog dialog = null;
                dialog = new AlertDialog.Builder(activity)
                        .setMessage(R.string.no_back_activity_message)
                        .setNegativeButton(R.string.no_back_activity_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                activity.finish();
                            }
                        }).setPositiveButton(R.string.no_back_activity_setting, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                                activity.startActivity(intent);
                            }
                        }).create();
                dialog.show();
            }
        }
    }

    /**
     * 获取轮播图数据
     * Rotating map data initialization
     */
    public void getSpannerViewData(Context context) {
        if (NetWorkUtil.isNetworkConnected(context)) {
            HttpManager.getOldObjectGet(HttpConstants.AD_SPANNER_URL, new TempStringCallBack() {
                @Override
                public void onResponse(String stringResponse) {
                    if (stringResponse != null) {
                        final AdBeansFormUrl adBean = GsonUtils.fromJson(stringResponse, AdBeansFormUrl.class);
                        if ((null == adBean) || adBean.getCode() != 1) {
                            return;
                        }
                        List<AdBeansFormUrl.AdInfo> mAdInfos = adBean.getData();
                        if (mAdInfos.isEmpty()) {
                            return;
                        }
                        getView().getSpannerViewData(mAdInfos);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                }
            });
        }
    }

    /**
     * 组装首页功能视图碎片
     * Assemble the home page functional view fragment
     *
     * @param context context
     */
    public void packageFragmentAndView(Context context) {
        Map<Integer, List<String>> map = subListByItemCount(context);
        List<Fragment> mFragmentList = new ArrayList<>();
        for (int i = 0; i < map.size(); i++) {
            List<String> nameList = map.get(i);
            MainViewPagerFragment mediaFragment = new MainViewPagerFragment();
            Bundle bundle = new Bundle();
            ArrayList<MainViewPagerFragmentData> list = initFragmentDataById(context, nameList, i);
            bundle.putParcelableArrayList("list", list);
            mediaFragment.setArguments(bundle);
            mFragmentList.add(mediaFragment);
        }
        getView().onPackageFragmentView(mFragmentList, map);
    }

    /**
     * 初始化默认配置参数
     * 注：androidCameraPreferDualBufferAR true 需要开启双buffer  返回false 需要开启单
     * 因为底层的方法是设置的是否是 单buffer模式 所以需要取反
     * <p>
     * Initializes the default configuration parameters
     * Note: androidCameraPreferDualBufferAR true need open double buffer returns false need open list
     * Because the underlying method is set to single buffer mode, it needs to be inverted
     */
    public void initParameterSetting() {
        ParameterSettingValues parameterValues = ParameterSettingValues.instance();
        boolean androidCameraPreferDualBufferAR = NvsStreamingContext.getInstance().isAndroidCameraPreferDualBufferAR();
        Log.d(TAG, "androidCameraPreferDualBufferAR=" + androidCameraPreferDualBufferAR);
        parameterValues.setSingleBufferMode(!androidCameraPreferDualBufferAR);
    }

    /**
     * 生成每页显示的功能标题集合
     * <p>
     * Generate a collection of feature titles displayed per page
     *
     * @return 索引和对应索引页的标题集合；Index and title set of corresponding index pages
     */
    private Map<Integer, List<String>> subListByItemCount(Context context) {
        String[] fragmentItems = context.getResources().getStringArray(R.array.main_fragment_item);
        Map<Integer, List<String>> map = new HashMap<>();
        List<String> list = Arrays.asList(fragmentItems);
        int count = (list.size() - 1) / SPANCOUNT + 1;
        for (int i = 0; i < count; i++) {
            int endTime = Math.min(list.size(), (i + 1) * SPANCOUNT);
            int startTime = i == 0 ? i : i * SPANCOUNT;
            List<String> childList = list.subList(startTime, endTime);
            map.put(i, childList);
        }
        return map;
    }

    /**
     * @param names         names
     * @param fragmentCount 当前功能页索引；Index of current feature page
     * @return ArrayList
     */
    private ArrayList<MainViewPagerFragmentData> initFragmentDataById(Context context, List<String> names, int fragmentCount) {
        /*
         * 当前页功能模块背景
         * Function page background of current page
         * */
        String[] fragmentItemsBackGround = context.getResources().getStringArray(R.array.main_fragment_background);
        List<String> listBackground = Arrays.asList(fragmentItemsBackGround);

        /*
         * 当前页功能模块图标
         * */
        String[] fragmentItemsImage = context.getResources().getStringArray(R.array.main_fragment_image);
        List<String> listImage = Arrays.asList(fragmentItemsImage);

        /*
         * 生成当前页面功能模块，实体类集合
         * Generate current page function module, entity class collection
         * */
        ArrayList<MainViewPagerFragmentData> list1 = new ArrayList<>();
        for (int i = 0, size = names.size(); i < size; i++) {
            int backGroundId = context.getResources().getIdentifier(listBackground.get(fragmentCount * 8 + i), "drawable", context.getPackageName());
            int imageId = context.getResources().getIdentifier(listImage.get(fragmentCount * 8 + i), "drawable", context.getPackageName());
            if (backGroundId != 0 && imageId != 0) {
                list1.add(new MainViewPagerFragmentData(backGroundId, names.get(i), imageId));
            }
        }
        return list1;
    }

    /**
     * 拷贝资源到sdcard
     * Copy resources to sdcard
     *
     * @param context context
     */
    public void copyResourceToSdCard(Context context) {
        Observable.just(1).map((Function<Integer, Object>) integer -> {
            copyAssetCaptureSceneToSdCard(context);
            return integer;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                Log.d(TAG, "copyAssetCaptureSceneToSdCard success");
            }
        }).subscribe();
    }

    /**
     * 将Asset资源拷贝到sd卡
     * Copy the Asset resource to the sd card
     *
     * @param context context
     */
    private void copyAssetCaptureSceneToSdCard(Context context) {
        if (null == context) {
            return;
        }
        AssetManager assets = context.getAssets();
        String localFilterDir = PathUtils.getCaptureSceneImageLocalFilePath();
        try {
            String[] filter_adjusts = assets.list("capture_scene");
            for (String fileName : filter_adjusts) {
                FileUtils.copyAssetFile(context, fileName,
                        "capture_scene", localFilterDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐私协议提示
     * Privacy Protocol Tips
     */
    public void showPrivacyDialog(Activity context) {
        PrivacyPolicyDialog privacyPolicyDialog = new PrivacyPolicyDialog(context, R.style.dialog);
        privacyPolicyDialog.setOnButtonClickListener(new PrivacyPolicyDialog.OnPrivacyClickListener() {
            @Override
            public void onButtonClick(boolean isAgree) {
                getView().onPrivacyPolicyAgree(isAgree);
            }

            @Override
            public void pageJumpToWeb(String clickTextContent) {
                String serviceAgreement = context.getString(R.string.service_agreement);
                String privacyPolicy = context.getString(R.string.privacy_policy);
                String visitUrl = "";
                if (clickTextContent.contains(serviceAgreement)) {
                    visitUrl = Constants.SERVICE_AGREEMENT_URL;
                } else if (clickTextContent.contains(privacyPolicy)) {
                    visitUrl = Constants.PRIVACY_POLICY_URL;
                }
                if (TextUtils.isEmpty(visitUrl)) {
                    return;
                }
                getView().onPrivacyPolicyWeb(visitUrl);
            }
        });
        privacyPolicyDialog.show();
    }

    /**
     * 检查授权的方法，不同类型使用不同的授权。
     * 商汤的授权使用同一个授权文件，不区分普通或者高级
     * Method of checking authorization. Different types of authorization are used.
     * Sensetime's license uses the same license document and does not distinguish between ordinary and advanced
     */
    public void checkAuthorization(Context context) {
        getView().onInitArSceneEffect();
    }

}
