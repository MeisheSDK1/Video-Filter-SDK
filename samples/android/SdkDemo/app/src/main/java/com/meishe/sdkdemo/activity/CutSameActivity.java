package com.meishe.sdkdemo.activity;

import android.view.View;

import com.meishe.cutsame.CutSameEditorActivity;
import com.meishe.cutsame.fragment.TemplateListFragment;
import com.meishe.cutsame.inter.OnTemplateListener;
import com.meishe.net.server.OkDownload;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.edit.interfaces.OnTitleBarClickListener;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.utils.AppManager;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lhz
 * @CreateDate : 2020/11/3
 * @Description :剪同款页面 CutSameActivity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CutSameActivity extends BaseActivity implements OnTemplateListener {
    private CustomTitleBar mTitleBar;
    public String mCutModel = "";

    @Override
    protected int initRootView() {
        return R.layout.activity_cut_same;
    }

    @Override
    protected void initViews() {
        mTitleBar = findViewById(R.id.title_bar);
        initViewFragment();
        OkDownload.initAndroidOs(this);
    }

    private void initViewFragment() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            mCutModel = getIntent().getExtras().getString(CutSameEditorActivity.BUNDLE_KEY, "");
        }
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        TemplateListFragment templateListFragment = TemplateListFragment.create(mCutModel);
        templateListFragment.setOnTemplateListener(this);
        fragmentTransaction.add(R.id.container, templateListFragment).commit();
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.cutSame);
    }

    @Override
    protected void initData() {
        registerFont();
    }

    private void registerFont() {
        mStreamingContext.registerFontByFilePath("assets:/cutsamefont/pp_pixel.ttf");
        mStreamingContext.registerFontByFilePath("assets:/cutsamefont/Muyao-Softbrush.ttf");
    }

    @Override
    protected void initListener() {
        mTitleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {
                finish();
            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBack() {
        AppManager.getInstance().finishActivity();
    }
}
