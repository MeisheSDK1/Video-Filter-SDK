package com.meishe.sdkdemo.mimodemo.common.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : ccg
 * @CreateDate : 2020/6/12.
 * @Description :mimo 基类。mimo baseActivity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public abstract class BaseFragment<T> extends Fragment {
    /**
     * 依赖的activity
     * Dependent activity
     */
    protected FragmentActivity mActivity;

    /**
     * 根view
     * Root view
     */
    protected View mRootView;

    /**
     * 是否对用户可见
     * Whether to be visible to users
     */
    protected boolean mIsVisible;
    /**
     * 是否加载完成
     * 当执行完oncreatview,View的初始化方法后方法后即为true
     * Whether the loading is complete
     * When the oncreatview is executed, the method after the initialization method of the View is true
     */
    protected boolean mIsPrepare;
    public T listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        listener = (T) mActivity;
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(initRootView(), container, false);

        initArguments(getArguments());

        initView();

        mIsPrepare = true;

        onLazyLoad();

        initListener();

        return mRootView;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        this.mIsVisible = isVisibleToUser;

        if (isVisibleToUser) {
            onVisibleToUser();
        }
    }

    /**
     * 用户可见时执行的操作
     * What to do when the user is visible
     */
    protected void onVisibleToUser() {
        if (mIsPrepare && mIsVisible) {
            onLazyLoad();
        }
    }


    @SuppressWarnings("unchecked")
    protected <T extends View> T findViewById(int id) {
        if (mRootView == null) {
            return null;
        }

        return (T) mRootView.findViewById(id);
    }

    /**
     * 设置根布局资源id
     * Set the id of the root layout resource
     */
    protected abstract int initRootView();


    /**
     * 初始化数据
     * Initialize data
     *
     * @param arguments 接收到的从其他地方传递过来的参数 Received parameters passed from elsewhere
     */
    protected abstract void initArguments(Bundle arguments);


    /**
     * 初始化View
     * Initialize View
     */
    protected abstract void initView();


    /**
     * 懒加载，仅当用户可见切view初始化结束后才会执行
     * Lazy loading is executed only when the user can see that the initialization of the view is complete
     */
    protected abstract void onLazyLoad();


    /**
     * 设置监听事件
     * Set listening event
     */
    protected abstract void initListener();

}