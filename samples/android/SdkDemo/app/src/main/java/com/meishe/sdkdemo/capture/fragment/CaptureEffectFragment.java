package com.meishe.sdkdemo.capture.fragment;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.meishe.base.msbus.MSBus;
import com.meishe.base.utils.CommonUtils;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseFragmentPagerAdapter;
import com.meishe.sdkdemo.capture.bean.CategoryInfo;
import com.meishe.sdkdemo.capture.bean.TypeAndCategoryInfo;
import com.meishe.sdkdemo.capture.fragment.sticker.CustomStickerFragment;
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
public class CaptureEffectFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TabLayout mTabLayout;
    private CustomViewPager mViewPager;
    private TypeAndCategoryInfo mTypeAndCategoryInfo;
    private View mIvNo;
    private String mTag;
    private List<Fragment> fragmentLists = new ArrayList<>();


    public CaptureEffectFragment() {
        // Required empty public constructor
    }

    public static CaptureEffectFragment newInstance(TypeAndCategoryInfo filterTypeInfo, String tag) {
        CaptureEffectFragment fragment = new CaptureEffectFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, filterTypeInfo);
        args.putString(ARG_PARAM2, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentLists.clear();
        if (getArguments() != null) {
            mTypeAndCategoryInfo = (TypeAndCategoryInfo) getArguments().getSerializable(ARG_PARAM1);
            mTag = (String) getArguments().getString(ARG_PARAM2);
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
        if (Constants.FRAGMENT_PROP_TAG.equals(mTag)) {
            mIvNo.setVisibility(View.VISIBLE);
        } else {
            mIvNo.setVisibility(View.GONE);
        }
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
                MSBus.getInstance().post(Constants.SubscribeType.SUB_UN_USE_PROP_TYPE);
                MSBus.getInstance().post(Constants.SubscribeType.SUB_UN_SELECT_ITEM_TYPE);
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
        List<CategoryInfo> categories = null;
        if (mTypeAndCategoryInfo != null) {
            categories = mTypeAndCategoryInfo.getCategories();
            if (categories != null) {
                for (CategoryInfo categoryInfo : categories) {
                    String displayNameZhCn = categoryInfo.getDisplayNameZhCn();
                    if ("动画".equals(displayNameZhCn) || "贴纸模板".equals(displayNameZhCn)) {
                        continue;
                    }
                    boolean isChinese = SystemUtils.isZh(MSApplication.getContext());
                    if (isChinese) {
                        stringArray.add(displayNameZhCn);
                    } else {
                        stringArray.add(categoryInfo.getDisplayName());
                    }
                }
            }
        }

        mTabLayout.addTab(mTabLayout.newTab().setText(Constants.TITLE_SEARCH));
        boolean local = checkLocalEffect();
        SearchEffectTabFragment searchFragment = SearchEffectTabFragment.newInstance(mTag);
        fragmentLists.add(searchFragment);
        if (local) {
            fragmentLists.add(LocalEffectFragment.newInstance(mTag));
        }
        if (Constants.FRAGMENT_STICKER_TAG.equals(mTag)) {
            //当是贴纸的时候增加自定义
            //Add customization when it is a sticker
            fragmentLists.add(CustomStickerFragment.newInstance());
        }

        for (int i = 0; i < stringArray.toArray().length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(stringArray.get(i)));
            CaptureEffectTabFragment captureFilterFragment = CaptureEffectTabFragment.
                    newInstance(categories.get(i), mTag, stringArray.get(i));
            fragmentLists.add(captureFilterFragment);
        }

        stringArray.add(0, Constants.TITLE_SEARCH);
        if (local) {
            stringArray.add(1, "local");
        }
        if (Constants.FRAGMENT_STICKER_TAG.equals(mTag)) {
            if (local) {
                stringArray.add(2, getResources().getString(R.string.sticker_custom));
            } else {
                //当是贴纸的时候增加自定义
                //Add customization when it is a sticker
                stringArray.add(1, getResources().getString(R.string.sticker_custom));
            }

        }
        mViewPager.setOffscreenPageLimit(1);
        BaseFragmentPagerAdapter fragmentPagerAdapter = new BaseFragmentPagerAdapter(
                getChildFragmentManager(),
                fragmentLists, stringArray);
        mViewPager.setAdapter(fragmentPagerAdapter);
        if (Constants.FRAGMENT_STICKER_TAG.equals(mTag)) {
            mViewPager.setCurrentItem(2);
        } else {
            mViewPager.setCurrentItem(1);
        }
        mViewPager.setPagingEnabled(false);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    public void selectEffectById(String id) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        if (CommonUtils.isEmpty(fragmentLists)) {
            return;
        }
        for (Fragment fragment : fragmentLists) {
            if (fragment instanceof CaptureEffectTabFragment) {
                CaptureEffectTabFragment tabFragment = (CaptureEffectTabFragment) fragment;
                tabFragment.selectEffectById(id);
            }
        }
    }

    /**
     * 贴纸的
     * sticker local path
     * /storage/emulated/0/Android/data/com.meishe.ms240sdkdemo/files/NvStreamingSdk/local/sticker
     *
     * @return
     */
    private boolean checkLocalEffect() {
        String localPath = null;
        if (Constants.FRAGMENT_STICKER_TAG.equals(mTag)) {
            localPath = PathUtils.getLocalStickerDir();
        } else if (Constants.FRAGMENT_COMPONENT_CAPTION_TAG.equals(mTag)) {
            localPath = PathUtils.getLocalCompDir();
        } else if (Constants.FRAGMENT_PROP_TAG.equals(mTag)) {
            localPath = PathUtils.getLocalPropDir();
        } else if (Constants.FRAGMENT_FILTER_TAG.equals(mTag)) {
            localPath = PathUtils.getLocalFilterDir();
        }
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
}