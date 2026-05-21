package com.meishe.cutsame.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 共用的FragmentPagerAdapter
 * Common FragmentPagerAdapter
 */
public class CommonFragmentCustomTabAdapter extends FragmentPagerAdapter {
    private List<? extends Fragment> mFragmentList;
    private List<String> mTabTitles;
    private Context mContext;
    private int mLayoutId;

    public CommonFragmentCustomTabAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        mFragmentList = list;
    }

    public CommonFragmentCustomTabAdapter(FragmentManager fm, Context context, @LayoutRes
            int layoutId, List<? extends Fragment> list, List<String> tabTitles) {
        super(fm);
        mContext = context;
        mFragmentList = list;
        mTabTitles = tabTitles;
        mLayoutId = layoutId;
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
        return null;
    }

    /**
     * Gets tab view.
     * 获得标签视图
     *
     * @param position the position 位置
     * @return the tab view 标签视图
     */
    public View getTabView(int position) {
        return LayoutInflater.from(mContext).inflate(mLayoutId, null);
    }
}
