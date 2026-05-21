package com.meishe.sdkdemo.base;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ${gexinyu} on 2018/5/24.
 */

public abstract class  BaseFragment<T> extends Fragment {
    /**
     * 贴附的activity
     * Attached activity
     */
    protected FragmentActivity mActivity;

    /**
     * 根view
     * Root view
     */
    protected View mRootView;

    /**
     * 是否对用户可见
     * Visible to users
     */
    protected boolean mIsVisible;
    /**
     * 是否加载完成
     * 当执行完oncreatview,View的初始化方法后方法后即为true
     * Whether loading is completed
     *After oncreatview is executed, it will be true after the initialization method of the view
     */
    protected boolean mIsPrepare;
    public T listener;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        listener = (T)mActivity;
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
     *What to do when the user is visible
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
     *Set root layout resource id
     */
    protected abstract int initRootView();


    /**
     * 初始化数据
     *Initialize Data
     */
    protected abstract void initArguments(Bundle arguments);


    /**
     * 初始化View
     *Initialize View
     */
    protected abstract void initView();


    /**
     * 懒加载，仅当用户可见切view初始化结束后才会执行
     *Lazy loading. It will be executed only after the initialization of the user's visible cut view is completed
     */
    protected abstract void onLazyLoad();


    /**
     * 设置监听事件
     *Set listening events
     */
    protected abstract void initListener();

}