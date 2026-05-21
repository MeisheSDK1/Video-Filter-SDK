package com.meishe.sdkdemo.capturescene.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseFragmentPagerAdapter;
import com.meishe.sdkdemo.capturescene.CaptureSceneFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import static com.meishe.sdkdemo.capturescene.data.Constants.CAPTURE_SCENE_TYPE_IMAGE;
import static com.meishe.sdkdemo.capturescene.data.Constants.CAPTURE_SCENE_TYPE_VIDEO;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2022/1/18 13:03
 * @Description :
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class CaptureSceneBottomView extends LinearLayout {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Context mContext;
    private NvsStreamingContext mStreamingContext;
    private CaptureSceneFragment mVideoFragment;
    private CaptureSceneFragment mImageFragment;

    public CaptureSceneBottomView(Context context) {
        this(context, null);
    }

    public CaptureSceneBottomView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptureSceneBottomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_capture_scene_bottom_view, this, true);
        mTabLayout = view.findViewById(R.id.tab_layout);
        mViewPager = view.findViewById(R.id.view_pager);
        initViewPager();
    }


    private void initViewPager() {
        List<Fragment> fragmentLists = new ArrayList<>();
        mImageFragment = CaptureSceneFragment.newInstance(CAPTURE_SCENE_TYPE_IMAGE);
        fragmentLists.add(mImageFragment);
        mVideoFragment = CaptureSceneFragment.newInstance(CAPTURE_SCENE_TYPE_VIDEO);
        fragmentLists.add(mVideoFragment);

        String[] stringArray = getResources().getStringArray(R.array.capture_scene_type);
        for (String s : stringArray) {
            mTabLayout.addTab(mTabLayout.newTab().setText(s));
        }

        mViewPager.setOffscreenPageLimit(2);
        BaseFragmentPagerAdapter fragmentPagerAdapter = new BaseFragmentPagerAdapter(
                ((FragmentActivity) mContext).getSupportFragmentManager(),
                fragmentLists, Arrays.asList(stringArray.clone()));
        mViewPager.setAdapter(fragmentPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    public void show(final NvsStreamingContext streamingContext) {
        CaptureSceneBottomView.this.setVisibility(VISIBLE);
        if (mStreamingContext == null) {
            setStreamingContext(streamingContext);
        }
    }

    public void setStreamingContext(NvsStreamingContext streamingContext) {
        mStreamingContext = streamingContext;
    }


    public void clearCaptureScene() {
        mStreamingContext.removeCurrentCaptureScene();
        mStreamingContext.removeAllCaptureVideoFx();
        mImageFragment.clearCaptureScene();
    }

    public void hide() {
        this.setVisibility(GONE);
    }

    public boolean isShow() {
        return getVisibility() == View.VISIBLE;
    }

    public void addLocalResource(String filePath) {
        if (mImageFragment != null) {
            mImageFragment.addLocalResource(filePath);
        }
    }
}
