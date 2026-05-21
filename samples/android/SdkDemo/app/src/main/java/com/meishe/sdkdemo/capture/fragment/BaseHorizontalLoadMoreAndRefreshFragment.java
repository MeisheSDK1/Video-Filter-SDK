package com.meishe.sdkdemo.capture.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meicam.effect.sdk.NvsEffectSdkContext;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.sdkdemo.capture.adapter.CommonLoadMoreAndRefreshAdapter;
import com.meishe.sdkdemo.capture.bean.EffectInfo;
import com.meishe.sdkdemo.utils.PathUtils;

import java.io.File;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/22 下午5:00
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public abstract class BaseHorizontalLoadMoreAndRefreshFragment extends Fragment {
    protected RecyclerView mRecyclerView;
    protected CommonLoadMoreAndRefreshAdapter<EffectInfo> mCommonRecyclerViewAdapter;
    protected Context mContext;
    protected NvsStreamingContext mStreamingContext;
    protected NvsEffectSdkContext mNvsEffectSdkContext;
    protected ViewDataBinding mBinding;

    private View mRootView;


    protected boolean mIsVisible;

    protected boolean mIsPrepare;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,initRootView(),
                container, false);
        mRootView=mBinding.getRoot();
        initArguments(getArguments());
        initView();

        mRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStreamingContext = NvsStreamingContext.getInstance();
        mNvsEffectSdkContext= NvsEffectSdkContext.getInstance();
        initData();
        onLazyLoad();
        mIsPrepare = true;
        initListener();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        this.mIsVisible = isVisibleToUser;

        if (isVisibleToUser) {
            onVisibleToUser();
        }
    }


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

    protected abstract int initRootView();

    protected abstract void initArguments(Bundle arguments);


    protected abstract void initView();

    protected abstract void onLazyLoad();

    protected abstract void initData();

    protected abstract void initListener();



    protected void initRecyclerView(int orientation,int layoutItemId, int variableId) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                orientation, false));
        mCommonRecyclerViewAdapter = new CommonLoadMoreAndRefreshAdapter(layoutItemId, variableId);
        mRecyclerView.setAdapter(mCommonRecyclerViewAdapter);
    }

    protected String getFilterPath(EffectInfo filterInfo, int assetType) {
        String packageUrl = filterInfo.getPackageUrl();
        final String pathDir = PathUtils.getSDCardPathByType(assetType);
        String[] split = packageUrl.split("/");
        String effectPath = pathDir + File.separator + split[split.length - 1];
        return effectPath;
    }
}
