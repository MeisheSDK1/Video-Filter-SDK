package com.meishe.sdkdemo;

import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.meicam.effect.sdk.NvsEffectSdkContext;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.app.BaseApp;
import com.meishe.base.constants.AndroidOS;
import com.meishe.net.NvsServerClient;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.ParameterSettingValues;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.utils.SpUtil;


/**
 * @author gexinyu on 2018/5/24.
 */

public class MSApplication extends BaseApp {

    public static float mDefaultCaptionSize;
    private static final String TAG = "MSApplication";
    private static String sdkVersion = "";
    private static Context mContext;
    private static MSApplication mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidOS.initConfig(mContext);
        mContext = this;
        mApp = this;
        initMeicamSdk();
//        if (!BuildConfig.DEBUG) {
//            //友盟预初始化 UM preinitialization
//            String channel = AnalyticsConfig.getChannel(this);
//            UMConfigure.preInit(this, UMENG_KEY, channel);
//            Logger.d(TAG, "channel=" + channel);
//        }
        if (SpUtil.getInstance(this).getBoolean(Constants.KEY_AGREE_PRIVACY, false)) {
            init();
        }
    }

    public void init() {
        //Fresco初始化 The Fresco initialization
        Fresco.initialize(this);
        //Okgo配置初始化 Okgo configuration initialization
        NvsServerClient.get().initConfig(this);
//        if (!BuildConfig.DEBUG) {
//            //友盟相关初始化 UM initialization
//            UMConfigure.submitPolicyGrantResult(this, true);
//            UMConfigure.init(this, UMENG_KEY, AnalyticsConfig.getChannel(this), UMConfigure.DEVICE_TYPE_PHONE, "");
//            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL);
//        }
    }

    /**
     * 美摄SDK相关初始化
     * Initialization of SDK related to beauty photography
     */
    private void initMeicamSdk() {
        NvsStreamingContext.setIconSize(1);
        NvsStreamingContext.setMaxReaderCount(10);
        String licensePath = "assets:/meishesdk.lic";
        boolean enableAssetLicense = ParameterSettingValues.instance().isEnableAssetLicense();
        Log.e("Meicam-Application", "是否需要检查授权：" + enableAssetLicense);
        TimelineUtil.initStreamingContext(getContext(), enableAssetLicense ? licensePath : "");
        TimelineUtil.initEffectSdkContext(getContext(), enableAssetLicense ? licensePath : "");
        NvAssetManager.init(getContext());
        NvsStreamingContext instance = NvsStreamingContext.getInstance();
        if (instance != null) {
            NvsStreamingContext.SdkVersion version = instance.getSdkVersion();
            if (version != null) {
                MSApplication.sdkVersion = version.majorVersion + "." + version.minorVersion + "." + version.revisionNumber;
            }
            //HDR亮度调节 增亮 HDR brightness adjustment Brightening
            instance.setColorGainForSDRToHDR(2);
        }
    }


    public static Context getAppContext() {
        return mContext;
    }

    public static MSApplication getInstance() {
        return mApp;
    }

    public static String getSdkVersion() {
        return sdkVersion;
    }

    public static String getAppId() {
        return getContext().getPackageName();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        NvsEffectSdkContext.close();
        NvsStreamingContext.close();
    }

}
