package com.meishe.sdkdemo.base;

import android.graphics.drawable.Drawable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.utils.Constants;

import java.util.List;

/**
 * Created by ${Gxinyu} on 2017/3/23.
 */

public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList;
    private List<String> mTabTitles;

    public BaseFragmentPagerAdapter(FragmentManager fm, List<Fragment> list, List<String> tabTitles) {
        super(fm);
        mFragmentList = list;
        mTabTitles = tabTitles;
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
        String title = mTabTitles.get(position);
        if (Constants.TITLE_SEARCH.equals(title)) {
            Drawable searchDrawable = ContextCompat.getDrawable(MSApplication.getAppContext(), R.mipmap.icon_search);
            searchDrawable.setBounds(0, 0, searchDrawable.getIntrinsicWidth(), searchDrawable.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(searchDrawable, ImageSpan.ALIGN_BOTTOM);
            SpannableString spannableString = new SpannableString(" ");
            spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
        return title;
    }

//    public CharSequence getPageTitle(int position) {
//        Drawable drawable;
//        switch (position) {
//            case 0:
//
//                break;
//            case 1:
//                drawable = ContextCompat.getDrawable(mContext, R.drawable.icon_weibo_timeline_friend);
//                break;
//            case 2:
//                drawable = ContextCompat.getDrawable(mContext, R.drawable.icon_weibo_timeline_mine);
//                break;
//            default:
//                drawable = ContextCompat.getDrawable(mContext, R.drawable.icon_weibo_timeline_public);
//                break;
//        }

//    }
}