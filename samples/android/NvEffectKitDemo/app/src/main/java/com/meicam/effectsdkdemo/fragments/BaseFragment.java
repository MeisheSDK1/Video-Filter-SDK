package com.meicam.effectsdkdemo.fragments;

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

public abstract class BaseFragment<T> extends Fragment {
    /**
     * attached activity
     * 贴附的activity
     */
    protected FragmentActivity mActivity;

    /**
     * root view
     * 根view
     */
    protected View mRootView;

    /**
     * if is visible to user
     * 是否对用户可见
     */
    protected boolean mIsVisible;
    /**
     * if loading completed
     * when do onCreateView，value will be true after the init function of view is called
     * 是否加载完成
     * 当执行完oncreatview中View的初始化方法后即为true
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
     * do operation when the fragment is visible to user
     * 用户可见时执行的操作
     *
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
     * set the root view id
     * 设置根布局资源id
     * @return
     */
    protected abstract int initRootView();


    /**
     * init arguments
     * 初始化数据
     *                  received params from other place
     * @param arguments 接收到的从其他地方传递过来的参数
     */
    protected abstract void initArguments(Bundle arguments);


    /**
     * init view
     * 初始化View
     *
     */
    protected abstract void initView();


    /**
     * for layzedLoad, The switch view is executed only when the user can see that the initialization is complete
     * 懒加载，仅当用户可见切view初始化结束后才会执行
     */
    protected abstract void onLazyLoad();


    /**
     * Set listening event
     * 设置监听事件
     */
    protected abstract void initListener();

}