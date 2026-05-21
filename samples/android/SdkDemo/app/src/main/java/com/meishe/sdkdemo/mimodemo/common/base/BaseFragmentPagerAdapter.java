package com.meishe.sdkdemo.mimodemo.common.base;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : ccg
 * @CreateDate : 2020/6/12.
 * @Description :mimo 基类。mimo baseActivity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList;
    private List<String> mTabTitles;
    public BaseFragmentPagerAdapter(FragmentManager fm, List<Fragment> list, List<String> tabTitles) {
        super(fm);
        mFragmentList=list;
        mTabTitles=tabTitles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList == null ? 0 : mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles.get(position);
    }
}