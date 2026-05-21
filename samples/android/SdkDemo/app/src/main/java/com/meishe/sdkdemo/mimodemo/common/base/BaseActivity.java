package com.meishe.sdkdemo.mimodemo.common.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.meicam.sdk.NvsStreamingContext;
import com.meishe.sdkdemo.mimodemo.common.utils.AppManager;
import com.meishe.sdkdemo.mimodemo.common.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.TimelineUtil;

import androidx.appcompat.app.AppCompatActivity;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : ccg
 * @CreateDate : 2020/6/12.
 * @Description :mimo 基类。mimo baseActivity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "BaseActivity";
    protected NvsStreamingContext mStreamingContext;
    private NvAssetManager mAssetManager;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        mStreamingContext = getStreamingContext();
        mStreamingContext.stop();
        AppManager.getInstance().addActivity(this);
        setContentView(initRootView());
        initViews();
        initTitle();
        initData();
        initListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
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

    /**
     * 初始化视图
     * Initialize view
     *
     * @return
     */
    protected abstract int initRootView();

    /**
     * 初始化视图组件
     * Initializes the view component
     */
    protected abstract void initViews();

    /**
     * 初始化头布局
     * Initialize the header layout
     */
    protected abstract void initTitle();

    /**
     * 数据处理
     * Data processing
     */
    protected abstract void initData();

    /**
     * 视图监听事件处理
     * The view listens for event processing
     */
    protected abstract void initListener();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 结束Activity从堆栈中移除
        //End Activity removed from stack
        AppManager.getInstance().finishActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}