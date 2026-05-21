package com.meishe.base.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * Common FragmentPagerAdapter
 * @Author : LiHangZhou
 * @CreateDate :2020/04/01 15:12
 * @Description :共用的FragmentStatePagerAdapter
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CommonFragmentStateAdapter extends FragmentStatePagerAdapter {
    private List<? extends Fragment> mFragmentList;
    private List<String> mTabTitles;

    public CommonFragmentStateAdapter(FragmentManager fm, List<? extends Fragment> list) {
        super(fm);
        mFragmentList = list;
    }

    public CommonFragmentStateAdapter(FragmentManager fm, List<? extends Fragment> list, List<String> tabTitles) {
        super(fm);
        mFragmentList = list;
        mTabTitles = tabTitles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mFragmentList == null ? 0 : mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTabTitles == null) {
            return null;
        }
        return mTabTitles.get(position);
    }
}
