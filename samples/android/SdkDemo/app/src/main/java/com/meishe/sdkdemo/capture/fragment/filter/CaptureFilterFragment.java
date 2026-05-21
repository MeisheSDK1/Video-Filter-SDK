package com.meishe.sdkdemo.capture.fragment.filter;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.meishe.base.msbus.MSBus;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseFragmentPagerAdapter;
import com.meishe.sdkdemo.capture.bean.CategoryInfo;
import com.meishe.sdkdemo.capture.bean.KindInfo;
import com.meishe.sdkdemo.capture.bean.TypeAndCategoryInfo;
import com.meishe.sdkdemo.capture.fragment.BaseFragment;
import com.meishe.sdkdemo.capture.fragment.LocalEffectFragment;
import com.meishe.sdkdemo.edit.view.CustomViewPager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.utils.SystemUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ms
 */
public class CaptureFilterFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private TabLayout mTabLayout;
    private CustomViewPager mViewPager;
    private TypeAndCategoryInfo mTypeAndCategoryInfo;
    private View mIvNo;
    private List<Fragment> mFragmentLists;


    public CaptureFilterFragment() {
        // Required empty public constructor
    }

    public static CaptureFilterFragment newInstance(TypeAndCategoryInfo filterTypeInfo) {
        CaptureFilterFragment fragment = new CaptureFilterFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, filterTypeInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTypeAndCategoryInfo = (TypeAndCategoryInfo) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_bottom_view, container, false);
        mTabLayout = view.findViewById(R.id.tab_layout);
        mViewPager = view.findViewById(R.id.view_pager);
        mIvNo = view.findViewById(R.id.iv_no);
        mIvNo.setVisibility(View.VISIBLE);
        initViewPager();
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        mIvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MSBus.getInstance().post(Constants.SubscribeType.SUB_UN_USE_FILTER_TYPE);
            }
        });

        return view;
    }

    @Override
    protected int initRootView() {
        return 0;
    }

    @Override
    protected void initArguments(Bundle arguments) {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void initViewPager() {
        List<String> stringArray = new ArrayList<>();
        List<KindInfo> kinds = null;
        List<KindInfo> allKinds = new ArrayList<>();
        mFragmentLists = new ArrayList<>();
        if (mTypeAndCategoryInfo != null) {
            List<CategoryInfo> categories = mTypeAndCategoryInfo.getCategories();
            if (categories != null) {
                for (CategoryInfo categoryInfo : categories) {
                    String displayNameZhCn = categoryInfo.getDisplayNameZhCn();
                    if ("动画".equals(displayNameZhCn)) {
                        continue;
                    }
                    kinds = categoryInfo.getKinds();
                    allKinds.addAll(allKinds.size(), kinds);
                    boolean isChinese = SystemUtils.isZh(MSApplication.getContext());
                    for (KindInfo kindinfo : kinds) {
                        if (isChinese) {
                            stringArray.add(kindinfo.getDisplayNameZhCn());
                        } else {
                            stringArray.add(kindinfo.getDisplayName());
                        }
                    }
                }
            }
        }

        mTabLayout.addTab(mTabLayout.newTab().setText(Constants.TITLE_SEARCH));
        SearchFragment searchFragment = SearchFragment.newInstance("");
        mFragmentLists.add(searchFragment);
        boolean local = checkLocalEffect();
        if (local) {
            mFragmentLists.add(LocalEffectFragment.newInstance(Constants.FRAGMENT_FILTER_TAG));
        }
        for (int i = 0; i < stringArray.toArray().length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(stringArray.get(i)));
            CaptureFilterTabFragment captureFilterFragment = CaptureFilterTabFragment.
                    newInstance(allKinds.get(i));
            mFragmentLists.add(captureFilterFragment);
        }
        stringArray.add(0, Constants.TITLE_SEARCH);
        if (local) {
            stringArray.add(1, "local");
        }
        mViewPager.setOffscreenPageLimit(1);
        BaseFragmentPagerAdapter fragmentPagerAdapter = new BaseFragmentPagerAdapter(
                getChildFragmentManager(),
                mFragmentLists, stringArray);
        mViewPager.setAdapter(fragmentPagerAdapter);
        mViewPager.setCurrentItem(1);
        mViewPager.setPagingEnabled(false);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    /**
     * 贴纸的
     * /storage/emulated/0/Android/data/com.meishe.ms240sdkdemo/files/NvStreamingSdk/local/filter
     *
     * @return
     */
    private boolean checkLocalEffect() {
        String localPath = null;
        localPath = PathUtils.getLocalFilterDir();
        if (localPath == null) {
            return false;
        }
        File file = new File(localPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String[] list = file.list();
        if (list.length <= 0) {
            return false;
        } else {
            return true;
        }

    }

    public void enableChangedItem(boolean enable) {
        if (mFragmentLists != null) {
            for (Fragment fragment : mFragmentLists) {
                if (fragment instanceof CaptureFilterTabFragment) {
                    CaptureFilterTabFragment captureEffectTabFragment = (CaptureFilterTabFragment) fragment;
                    captureEffectTabFragment.enableChangedItem(enable);
                }
            }
        }
    }
}