package com.meishe.mvvm;

import androidx.lifecycle.ViewModel;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/4/23 上午12:35
 * @Description : 让父类控制是否有下一页以及请求下一页的数据 具体的接口请求子类负责
 *                为了复用不能处理具体跟业务相关的逻辑
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public abstract class BaseListMvvmModel<T> extends ViewModel {

    public static final int PAGE_SIZE = 20;

    /**
     * 分页页码
     */
    protected int mPage = 1;
    /**
     * 点击监听
     */
    protected WeakReference<IBaseModelListener> mReferenceIBaseModelListener;
    /**
     * 是否是分页
     */
    private boolean mIsPaging;
    /**
     * 第一页页码
     */
    private final int INIT_PAGE_NUMBER;
    /**
     * 是否在加载中
     */
    private boolean mIsLoading;

    public BaseListMvvmModel(boolean isPaging, int... initPageNumber) {
        this.mIsPaging = isPaging;
        if (isPaging && initPageNumber != null
                && initPageNumber.length > 0) {
            //设置分页的第一页码
            INIT_PAGE_NUMBER = initPageNumber[0];
        } else {
            INIT_PAGE_NUMBER = 0;
        }
    }

    public void register(IBaseModelListener listener) {
        if (listener != null) {
            mReferenceIBaseModelListener = new WeakReference<>(listener);
        }
    }

    public void refresh() {
        if (!mIsLoading) {
            if (mIsPaging) {
                mPage = INIT_PAGE_NUMBER;
            }
            mIsLoading = true;
            load();
        }
    }

    public void loadNextPage() {
        if (!mIsLoading) {
            mIsLoading = true;
            load();
        }
    }

    /**
     * 子类处理加载网络的
     */
    public abstract void load();

    protected void notifyResult( T resultData) {
        IBaseModelListener listener = mReferenceIBaseModelListener.get();
        if (listener != null) {
            if (mIsPaging) {
                listener.onLoadSuccess(this, resultData,
                        new PagingResult(mPage == INIT_PAGE_NUMBER,
                                resultData == null || ((List) resultData).isEmpty(),
                                resultData != null && ((List) resultData).size() > 0));
            } else {
                listener.onLoadSuccess(this, resultData);
            }

            if (mIsPaging) {
                if (resultData != null && ((List) resultData).size() > 0) {
                    mPage++;
                }
            }
        }
        mIsLoading = false;
    }

    protected void loadFail(final String errorMessage) {
        IBaseModelListener listener = mReferenceIBaseModelListener.get();
        if (listener != null) {
            if (mIsPaging) {
                listener.onLoadFail(this, errorMessage, new PagingResult(mPage == INIT_PAGE_NUMBER, true, false));
            } else {
                listener.onLoadFail(this, errorMessage);
            }
        }
        mIsLoading = false;
    }

}
