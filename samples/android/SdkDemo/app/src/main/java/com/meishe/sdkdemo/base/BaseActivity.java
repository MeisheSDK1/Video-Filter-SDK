package com.meishe.sdkdemo.base;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.meicam.effect.sdk.NvsEffectSdkContext;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;

/**
 * @author Newnet
 * @date 2017/1/6
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "BaseActivity";
    public NvAssetManager mAssetManager;
    public NvsStreamingContext mStreamingContext;
    public NvsEffectSdkContext mNvsEffectSdkContext;
    public Context mContext;
    public boolean useUmeng = true;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        Log.d(TAG, this.getClass().getSimpleName());
        mContext = this;
        mStreamingContext = getStreamingContext();
        mStreamingContext.stop();
        mNvsEffectSdkContext = getEffectContext();
//        mAssetManager = getNvAssetManager();
        //把当前初始化的activity加入栈中
        //Add the currently initialized activity to the stack
        AppManager.getInstance().addActivity(this);
        setContentView(initRootView());
        if (Build.VERSION.SDK_INT > 34) {
            View contentRoot = findViewById(android.R.id.content);
            contentRoot.setBackgroundResource(R.color.ff242728);
            if (contentRoot instanceof ViewGroup && ((ViewGroup) contentRoot).getChildCount() > 0) {
                ((ViewGroup) contentRoot).getChildAt(0).setFitsSystemWindows(true);
            }
        }
        initViews();
        initData();
        initTitle();
        initListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        boolean isBackgroud = Util.isBackground(this);
        if (isBackgroud) {
            if (mStreamingContext.getStreamingEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_COMPILE) {
                mStreamingContext.stop();
            }
        }
    }

    public NvsStreamingContext getStreamingContext() {
        if (mStreamingContext == null) {
            synchronized (NvsStreamingContext.class) {
                if (mStreamingContext == null) {
                    mStreamingContext = NvsStreamingContext.getInstance();
                    if (mStreamingContext == null) {
                        String licensePath = "assets:/meishesdk.lic";
                        mStreamingContext = TimelineUtil.initStreamingContext(getApplicationContext(), licensePath);
                    }
                }
            }
        }
        return mStreamingContext;
    }

    public NvsEffectSdkContext getEffectContext() {
        if (mNvsEffectSdkContext == null) {
            synchronized (NvsEffectSdkContext.class) {
                if (mNvsEffectSdkContext == null) {
                    mNvsEffectSdkContext = NvsEffectSdkContext.getInstance();
                    if (mNvsEffectSdkContext == null) {
                        String licensePath = "assets:/meishesdk.lic";
                        mNvsEffectSdkContext = TimelineUtil.initEffectSdkContext(getApplicationContext(), licensePath);
                    }
                }
            }
        }
        return mNvsEffectSdkContext;
    }

    public NvAssetManager getNvAssetManager() {
        synchronized (NvAssetManager.class) {
            if (mAssetManager == null) {
                mAssetManager = NvAssetManager.sharedInstance();
                if (mAssetManager == null) {
                    mAssetManager = NvAssetManager.init(this);
                }
            }
        }
        return mAssetManager;
    }

    /*
     * 初始化页面布局Id
     * Initialize activity layout Id.
     */
    protected abstract int initRootView();

    /*
     * 初始化视图组件
     * Initialize the view component.
     */
    protected abstract void initViews();

    /*
     * 初始化头布局
     * Initialize the Title .
     */
    protected abstract void initTitle();

    /*
     * 数据处理
     * Data processing.
     */
    protected abstract void initData();

    /*
     * 视图监听事件处理
     * View listen event handling.
     */
    protected abstract void initListener();

    /*
     * Activity结束,从堆栈中移除
     * Activity ends, removed from stack.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().finishActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    /*
     * onResume中，注册umeng统计页面跳转和应用时长
     * OnResume, register umeng statistics page jump and application duration.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (useUmeng) {

////        统计页面跳转
//            //Statistics page jump
//            MobclickAgent.onPageStart(getClass().getSimpleName());
////        统计应用时长
////            Statistics application duration
//            MobclickAgent.onResume(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (useUmeng) {
//            MobclickAgent.onPageStart(getClass().getSimpleName());
//            MobclickAgent.onPause(this);
        }
    }
}